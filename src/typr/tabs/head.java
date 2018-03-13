package typr.tabs;

import elemental.html.Uint8Array;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import typr.bin;

public class head
{
  @JsProperty public int unitsPerEm;
  @JsProperty public int xMin;
  @JsProperty public int xMax;
  @JsProperty public int yMin;
  @JsProperty public int yMax;
  @JsProperty public int fontRevision;
  @JsProperty public char flags;
  @JsProperty public double created;
  @JsProperty public double modified;
  @JsProperty public char macStyle;
  @JsProperty public char lowestRecPPEM;
  @JsProperty public short fontDirectionHint;
  @JsProperty public short indexToLocFormat;
  @JsProperty public short glyphDataFormat;
  
  @JsIgnore 
  public static head parse(Uint8Array data, int offset, int length) {
//	var bin = Typr._bin;
	head obj = new head();
	int tableVersion = bin.readFixed(data, offset);  offset += 4;
	obj.fontRevision = bin.readFixed(data, offset);  offset += 4;
	int checkSumAdjustment = bin.readUint(data, offset);  offset += 4;
	int magicNumber = bin.readUint(data, offset);  offset += 4;
	obj.flags = bin.readUshort(data, offset);  offset += 2;
	obj.unitsPerEm = bin.readUshort(data, offset);  offset += 2;
	obj.created  = bin.readUint64(data, offset);  offset += 8;
	obj.modified = bin.readUint64(data, offset);  offset += 8;
	obj.xMin = bin.readShort(data, offset);  offset += 2;
	obj.yMin = bin.readShort(data, offset);  offset += 2;
	obj.xMax = bin.readShort(data, offset);  offset += 2;
	obj.yMax = bin.readShort(data, offset);  offset += 2;
	obj.macStyle = bin.readUshort(data, offset);  offset += 2;
	obj.lowestRecPPEM = bin.readUshort(data, offset);  offset += 2;
	obj.fontDirectionHint = bin.readShort(data, offset);  offset += 2;
	obj.indexToLocFormat  = bin.readShort(data, offset);  offset += 2;
	obj.glyphDataFormat   = bin.readShort(data, offset);  offset += 2;
	return obj;
  }
}