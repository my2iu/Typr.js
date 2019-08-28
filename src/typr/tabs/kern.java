package typr.tabs;

import elemental.html.Uint8Array;
import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import elemental.util.Collections;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import typr.TyprFont;
import typr.bin;

public class kern
{
  public ArrayOfInt glyph1;
  public ArrayOf<KernRval> rval;

  public static class KernRval
  {
    public ArrayOfInt glyph2;
    public ArrayOfInt vals;
  }
  
  @JsIgnore
  public static kern parse(Uint8Array data, int offset, int length,
      TyprFont font)
  {
    // var bin = Typr._bin;

    char version = bin.readUshort(data, offset);
    offset += 2;
    if (version == 1) return parseV1(data, offset - 2, length, font);
    char nTables = bin.readUshort(data, offset);
    offset += 2;

    kern map = new kern();
    map.glyph1 = Collections.arrayOfInt();
    map.rval = Collections.arrayOf();
    for (int i = 0; i < nTables; i++)
    {
      offset += 2; // skip version
      char lengthDiscard = bin.readUshort(data, offset);
      offset += 2;
      char coverage = bin.readUshort(data, offset);
      offset += 2;
      int format = coverage >>> 8;
      // I have seen format 128 once, that's why I do
      format &= 0xf;
      if (format == 0)
        offset = readFormat0(data, offset, map);
      else
        throw new IllegalArgumentException(
            "unknown kern table format: " + (int) format);
    }
    return map;
  }

  @JsIgnore
  public static kern parseV1(Uint8Array data, int offset, int length,
      TyprFont font)
  {
//		var bin = Typr._bin;

		int version = bin.readVersion(data, offset);
		offset += 4;
		int nTables = bin.readUint(data, offset);
		offset += 4;

		kern map = new kern();
	    map.glyph1 = Collections.arrayOfInt();
	    map.rval = Collections.arrayOf();
		for (int i = 0; i < nTables; i++) {
			int lengthDiscard = bin.readUint(data, offset);
			offset += 4;
			char coverage = bin.readUshort(data, offset);
			offset += 2;
			char tupleIndex = bin.readUshort(data, offset);
			offset += 2;
			int format = coverage >>> 8;
			//		 I have seen format 128 once, that's why I do  
			format &= 0xf;
			if (format == 0)
				offset = readFormat0(data, offset, map);
			else
				throw new IllegalArgumentException("unknown kern table format: " + format);
		}
		return map;
  }

  @JsIgnore
  public static int readFormat0(Uint8Array data, int offset, kern map)
  {
//		var bin = Typr._bin;
		int pleft = -1;
		char nPairs = bin.readUshort(data, offset);
		offset += 2;
		char searchRange = bin.readUshort(data, offset);
		offset += 2;
		char entrySelector = bin.readUshort(data, offset);
		offset += 2;
		char rangeShift = bin.readUshort(data, offset);
		offset += 2;
		for (int j = 0; j < nPairs; j++) {
			char left = bin.readUshort(data, offset);
			offset += 2;
			char right = bin.readUshort(data, offset);
			offset += 2;
			short value = bin.readShort(data, offset);
			offset += 2;
			if (left != pleft) {
				map.glyph1.push(left);
				KernRval rval = new KernRval();
				rval.glyph2 = Collections.arrayOfInt();
                rval.vals = Collections.arrayOfInt();
				map.rval.push(rval);
			}
			KernRval rval = map.rval.get(map.rval.length() - 1);
			rval.glyph2.push(right);
			rval.vals.push(value);
			pleft = left;
		}
		return offset;
  }

}