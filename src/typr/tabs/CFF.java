package typr.tabs;

import elemental.client.Browser;
import elemental.html.Uint8Array;
import elemental.util.ArrayOf;
import elemental.util.ArrayOfBoolean;
import elemental.util.ArrayOfInt;
import elemental.util.ArrayOfNumber;
import elemental.util.ArrayOfString;
import elemental.util.Collections;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import typr.bin;

@JsType(namespace="Typr")
public class CFF extends CffDictBase
{
  @JsIgnore public static CFF parse (Uint8Array data, int offset, int length)
  {
//        var bin = Typr._bin;
        
        data = Browser.getWindow().newUint8Array(data.getBuffer(), offset, length);
        offset = 0;
        
        // Header
        int major = data.intAt(offset);  offset++;
        int minor = data.intAt(offset);  offset++;
        int hdrSize = data.intAt(offset);  offset++;
        int offsize = data.intAt(offset);  offset++;
        //console.log(major, minor, hdrSize, offsize);
        
        // Name INDEX
        ArrayOfInt ninds = Collections.arrayOfInt();
        offset = readIndex(data, offset, ninds);
        ArrayOfString names = Collections.arrayOfString();
        
        for(int i=0; i<ninds.length()-1; i++) names.push(bin.readASCII(data, offset+ninds.get(i), ninds.get(i+1)-ninds.get(i)));
        //console.log(names);
        offset += ninds.get(ninds.length()-1);
        
        
        // Top DICT INDEX
        ArrayOfInt tdinds = Collections.arrayOfInt();
        offset = readIndex(data, offset, tdinds);
        // Top DICT Data
        ArrayOf<CffDict> topDicts = Collections.arrayOf();
        boolean isCIDFont = false;
        for(int i=0; i<tdinds.length()-1; i++)
        {
          ArrayOfBoolean firstIsROS = Collections.arrayOfBoolean();
          firstIsROS.push(false);
          topDicts.push( readDict(data, offset+tdinds.get(i), offset+tdinds.get(i+1), firstIsROS) );
          if (i == 0 && firstIsROS.get(0))
            isCIDFont = true;
        }
        offset += tdinds.get(tdinds.length()-1);
        CffDict topdict = topDicts.get(0);
        //console.log(topdict);
        
        // String INDEX
        ArrayOfInt sinds = Collections.arrayOfInt();
        offset = readIndex(data, offset, sinds);
        // String Data
        ArrayOfString strings = Collections.arrayOfString();
        for(int i=0; i<sinds.length()-1; i++) strings.push(bin.readASCII(data, offset+sinds.get(i), sinds.get(i+1)-sinds.get(i)));
        offset += sinds.get(sinds.length()-1);
        
        // Global Subr INDEX  (subroutines)     
        readSubrs(data, offset, topdict);
        
        return parseMore(topdict, data, offset, strings, isCIDFont);
  }
  @JsProperty public ArrayOf<ArrayOfInt> CharStrings;
  @JsProperty public ArrayOfInt Encoding;
  @JsProperty public ArrayOfInt charset;
//  @JsProperty public CffDict Private;
  @JsProperty public ArrayOf<CffDict> FDArray;
  @JsProperty public FDSelect FDSelect;
  @JsProperty public boolean isCIDFont = false;
  
  @JsIgnore static CFF parseMore(CffDict topdict, Uint8Array data, int offset, ArrayOfString strings, boolean isCIDFont)
  {
        CFF obj = new CFF();

        // charstrings
        if(topdict.hasRawCharStrings)
        {
            offset = (int)topdict.RawCharStrings;
            ArrayOfInt sinds = Collections.arrayOfInt();
            offset = readIndex(data, offset, sinds);
            
            ArrayOf<ArrayOfInt> cstr = Collections.arrayOf();
            for(int i=0; i<sinds.length()-1; i++) cstr.push(bin.readBytes(data, offset+sinds.get(i), sinds.get(i+1)-sinds.get(i)));
            //offset += sinds[sinds.length-1];
            obj.CharStrings = cstr;
            //console.log(topdict.CharStrings);
        }
        
        // Encoding
        if(topdict.hasRawEncoding) 
        {
          obj.Encoding = readEncoding(data, (int)topdict.RawEncoding, obj.CharStrings.length());
        }
        
        // charset
        if(topdict.hasRawCharset)
        {
          obj.charset = readCharset (data, (int)topdict.RawCharset , obj.CharStrings.length());
        }
        
        if(topdict.hasRawPrivate)
        {
            offset = (int)topdict.RawPrivate.get(1);
            ArrayOfBoolean firstIsROS = Collections.arrayOfBoolean();
            firstIsROS.push(false);
            obj.Private = readDict(data, offset, offset+(int)topdict.RawPrivate.get(0), firstIsROS);
            if(obj.Private.hasRawSubrs)  readSubrs(data, offset+(int)obj.Private.RawSubrs, obj.Private);
        }
        
        if (isCIDFont)
        {
          obj.isCIDFont = true;
          int fdArrayOffset = (int)topdict.RawFDArray;
          int fdSelectOffset = (int)topdict.RawFDSelect;
          // Read indices of the various dictionaries
          ArrayOfInt fdArrIndices = Collections.arrayOfInt();
          offset = readIndex(data, fdArrayOffset, fdArrIndices);
          // Read in the font dictionaries now
          ArrayOf<CffDict> cidFontDicts = Collections.arrayOf();
          for(int i=0; i<fdArrIndices.length()-1; i++)
          {
            ArrayOfBoolean dummyFirstIsROS = Collections.arrayOfBoolean();
            dummyFirstIsROS.push(false);
            CffDict fontDict = readDict(data, offset+fdArrIndices.get(i), offset+fdArrIndices.get(i+1), dummyFirstIsROS);
            // Might as well parse the Private data for each font dictionary too
            if (fontDict.hasRawPrivate)
            {
              int privateOffset = (int)fontDict.RawPrivate.get(1);
              CffDict parsedPrivate = readDict(data, privateOffset, privateOffset+(int)fontDict.RawPrivate.get(0), dummyFirstIsROS);
              fontDict.setPrivate(parsedPrivate);
              if(parsedPrivate.hasRawSubrs)  
                readSubrs(data, privateOffset+(int)parsedPrivate.RawSubrs, parsedPrivate);
            }
            cidFontDicts.push( fontDict );
          }
          obj.FDArray = cidFontDicts;
          offset += fdArrIndices.get(fdArrIndices.length()-1);
          // Read in the data for selecting which FontDict to use for each glyph
          obj.FDSelect = readFDSelect(data, fdSelectOffset, obj.CharStrings.length());
        }
//        ArrayOfString topdictKeys = topdict.keys();
//        for (int i = 0; i < topdict.keys().length(); i++)
//        {
//          String p = topdictKeys.get(i);
//          switch(p)
//          {
//          case "FamilyName":
//          case "FullName":
//          case "Notice":
//          case "version":
//          case "Copyright":
//            setCFFString(obj, p, strings.get(getDictInt(topdict, p) -426 + 35 ));
//            break;
//          case "CharStrings":
//          case "Encoding":
//          case "charset":
//          case "Private":
//            // Already handled elsewhere
//            break;
//          case "FDArray":
//          case "FDSelect":
//            if (isCIDFont) break;
//            // Fall through if not a CID font (should not be possible)
//          default:
//            copyCFFJSObj(obj, p, topdict);
//          }
//        }
        
        if (topdict.hasRawVersion) 
          obj.setVersion(lookupCffString(strings, (int)topdict.rawVersion));
  
        if (topdict.hasRawNotice) 
          obj.setNotice(lookupCffString(strings, (int)topdict.RawNotice));
  
        if (topdict.hasRawFullName) 
          obj.setFullName(lookupCffString(strings, (int)topdict.RawFullName));
  
        if (topdict.hasRawFamilyName) 
          obj.setFamilyName(lookupCffString(strings, (int)topdict.RawFamilyName));
  
        if (topdict.hasWeight) 
          obj.setWeight(topdict.Weight);
  
        if (topdict.hasFontBBox)
          obj.setFontBBox(topdict.FontBBox);
    
        if (topdict.hasBlueValues)
          obj.setBlueValues(topdict.BlueValues);
    
        if (topdict.hasOtherBlues)
          obj.setOtherBlues(topdict.OtherBlues);
    
        if (topdict.hasFamilyBlues)
          obj.setFamilyBlues(topdict.FamilyBlues);
    
        if (topdict.hasFamilyOtherBlues)
          obj.setFamilyOtherBlues(topdict.FamilyOtherBlues);
    
        if (topdict.hasStdHW)
          obj.setStdHW(topdict.StdHW);
    
        if (topdict.hasStdVW)
          obj.setStdVW(topdict.StdVW);
  
        if (topdict.hasUniqueID)
          obj.setUniqueID(topdict.UniqueID);
    
        if (topdict.hasXUID)
          obj.setXUID(topdict.XUID);
    
        if (false)
        {
          // Already handled elsewhere
          if (topdict.hasRawCharset)
            obj.setRawCharset(topdict.RawCharset);

          if (topdict.hasRawEncoding)
            obj.setEncoding(topdict.RawEncoding);

          if (topdict.hasRawCharStrings)
            obj.setCharStrings(topdict.RawCharStrings);

          if (topdict.hasRawPrivate)
            obj.setRawPrivate(topdict.RawPrivate);
        }
    
        if (topdict.hasRawSubrs)
          obj.setRawSubrs(topdict.RawSubrs);

        if (topdict.hasSubrs)
          obj.setSubrs(topdict.Subrs, topdict.Bias);

        if (topdict.hasdefaultWidthX)
          obj.setdefaultWidthX(topdict.defaultWidthX);
    
        if (topdict.hasnominalWidthX)
          obj.setnominalWidthX(topdict.nominalWidthX);
    
        if (topdict.hasRawCopyright)
          obj.setCopyright(lookupCffString(strings, (int)topdict.RawCopyright));
    
        if (topdict.hasIsFixedPitch)
          obj.setisFixedPitch(topdict.isFixedPitch);
    
        if (topdict.hasItalicAngle)
          obj.setItalicAngle(topdict.ItalicAngle);
    
        if (topdict.hasUnderlinePosition)
          obj.setUnderlinePosition(topdict.UnderlinePosition);
    
        if (topdict.hasUnderlineThickness)
          obj.setUnderlineThickness(topdict.UnderlineThickness);
    
        if (topdict.hasPaintType)
          obj.setPaintType(topdict.PaintType);
    
        if (topdict.hasCharstringType)
          obj.setCharstringType(topdict.CharstringType);
    
        if (topdict.hasFontMatrix)
          obj.setFontMatrix(topdict.FontMatrix);
    
        if (topdict.hasStrokeWidth)
          obj.setStrokeWidth(topdict.StrokeWidth);
    
        if (topdict.hasBlueScale)
          obj.setBlueScale(topdict.BlueScale);
    
        if (topdict.hasBlueShift)
          obj.setBlueShift(topdict.BlueShift);
    
        if (topdict.hasBlueFuzz)
          obj.setBlueFuzz(topdict.BlueFuzz);
    
        if (topdict.hasStemSnapH)
          obj.setStemSnapH(topdict.StemSnapH);
    
        if (topdict.hasStemSnapV)
          obj.setStemSnapV(topdict.StemSnapV);
    
        if (topdict.hasForceBold)
          obj.setForceBold(topdict.ForceBold);
    
        if (topdict.hasLanguageGroup)
          obj.setLanguageGroup(topdict.LanguageGroup);
    
        if (topdict.hasExpansionFactor)
          obj.setExpansionFactor(topdict.ExpansionFactor);
    
        if (topdict.hasInitialRandomSeed)
          obj.setinitialRandomSeed(topdict.initialRandomSeed);
    
        if (topdict.hasSyntheticBase)
          obj.setSyntheticBase(topdict.SyntheticBase);
    
        if (topdict.hasPostScript)
          obj.setPostScript(topdict.PostScript);
    
        if (topdict.hasBaseFontName)
          obj.setBaseFontName(topdict.BaseFontName);
    
        if (topdict.hasBaseFontBlend)
          obj.setBaseFontBlend(topdict.BaseFontBlend);
    
        if (topdict.hasROS)
          obj.setROS(topdict.ROS);
    
        if (topdict.hasCIDFontVersion)
          obj.setCIDFontVersion(topdict.CIDFontVersion);
    
        if (topdict.hasCIDFontRevision)
          obj.setCIDFontRevision(topdict.CIDFontRevision);
    
        if (topdict.hasCIDFontType)
          obj.setCIDFontType(topdict.CIDFontType);
    
        if (topdict.hasCIDCount)
          obj.setCIDCount(topdict.CIDCount);
    
        if (topdict.hasUIDBase)
          obj.setUIDBase(topdict.UIDBase);

        if (!isCIDFont)
        {
          // Should be impossible to have these fields set on a CID font, but whatever
          if (topdict.hasRawFDArray)
            obj.setRawFDArray(topdict.RawFDArray);
      
          if (topdict.hasRawFDSelect)
            obj.setRawFDSelect(topdict.RawFDSelect);
        }

    
        if (topdict.hasFontName)
          obj.setFontName(topdict.FontName);
    

        //console.log(obj);
        return obj;
    }
  
  @JsIgnore private static String lookupCffString(ArrayOfString strings, int idx)
  {
    return strings.get(idx -426 + 35);
  }

  static public abstract class FDSelect
  {
    public int format;
    @JsMethod public abstract int getFd(int glyphId);
  }
  static public class FDSelect0 extends FDSelect
  {
    ArrayOfInt fds;
    @Override @JsMethod public int getFd(int glyphId)
    {
      return fds.get(glyphId);
    }
  }
  static public class FDSelect3 extends FDSelect
  {
    ArrayOf<FDSelectRange3> range3;
    @Override @JsMethod public int getFd(int glyphId)
    {
      if (glyphId >= range3.get(range3.length() - 1).first)
        throw new IllegalArgumentException("Glyph outside of FD range");
      if (glyphId < range3.get(0).first)
        throw new IllegalArgumentException("Glyph outside of FD range");
      return getFd(glyphId, 0, range3.length() - 1);
    }
    private int getFd(int glyphId, int min, int max)
    {
      if (max - min < 2)
        return range3.get(min).fd;
      int mid = (min + max) / 2;
      if (range3.get(mid).first > glyphId)
        return getFd(glyphId, min, mid);
      else
        return getFd(glyphId, mid, max);
    }
  }
  static public class FDSelectRange3
  {
    char first;
    int fd;
  }

  private static FDSelect readFDSelect(Uint8Array data,
      int offset, int numGlyphs)
  {
    int format = data.intAt(offset);
    offset++;

    if (format == 0)
    {
      // Untested branch
      FDSelect0 select = new FDSelect0();
      select.fds = Collections.arrayOfInt();
      for (int n = 0; n < numGlyphs; n++)
      {
        select.fds.push(data.intAt(offset));
        offset++;
      }
      return select;
    }
    else if (format == 3)
    {
      int numRanges = bin.readUshort(data, offset);
      offset += 2;
      FDSelect3 select = new FDSelect3();
      select.range3 = Collections.arrayOf();
      for (int i = 0; i < numRanges; i++)
      {
        FDSelectRange3 r = new FDSelectRange3();
        r.first = bin.readUshort(data, offset);
        offset += 2;
        r.fd = data.intAt(offset);
        offset++;
        select.range3.push(r);
      }
      FDSelectRange3 sentinel = new FDSelectRange3();
      sentinel.first = bin.readUshort(data, offset);
      select.range3.push(sentinel);
      return select;
    }
    else
      throw new IllegalArgumentException("Unknown FDSelect type " + format);
  }

  @JsIgnore private static void readSubrs(Uint8Array data, int offset, CffDictBase obj)
  {
//        var bin = Typr._bin;
        ArrayOfInt gsubinds = Collections.arrayOfInt();
        offset = readIndex(data, offset, gsubinds);
        
        int bias, nSubrs = gsubinds.length();
        if (false) bias = 0;
        else if (nSubrs <  1240) bias = 107;
        else if (nSubrs < 33900) bias = 1131;
        else bias = 32768;
        
        ArrayOf<ArrayOfInt> subrs = Collections.arrayOf();
        for(int i=0; i<gsubinds.length()-1; i++) subrs.push(bin.readBytes(data, offset+gsubinds.get(i), gsubinds.get(i+1)-gsubinds.get(i)));
        //offset += gsubinds[gsubinds.length-1];
        putBiasSubrs(obj, bias, subrs);
  }
  @JsIgnore static void putBiasSubrs (CffDictBase obj, int bias, ArrayOf<ArrayOfInt> subrs)
  {
    obj.setSubrs(subrs, bias);
  }
    
    private static int[] tableSE = new int[] {
      0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,
      1,   2,   3,   4,   5,   6,   7,   8,
      9,  10,  11,  12,  13,  14,  15,  16,
     17,  18,  19,  20,  21,  22,  23,  24,
     25,  26,  27,  28,  29,  30,  31,  32,
     33,  34,  35,  36,  37,  38,  39,  40,
     41,  42,  43,  44,  45,  46,  47,  48,
     49,  50,  51,  52,  53,  54,  55,  56,
     57,  58,  59,  60,  61,  62,  63,  64,
     65,  66,  67,  68,  69,  70,  71,  72,
     73,  74,  75,  76,  77,  78,  79,  80,
     81,  82,  83,  84,  85,  86,  87,  88,
     89,  90,  91,  92,  93,  94,  95,   0,
      0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,
      0,  96,  97,  98,  99, 100, 101, 102,
    103, 104, 105, 106, 107, 108, 109, 110,
      0, 111, 112, 113, 114,   0, 115, 116,
    117, 118, 119, 120, 121, 122,   0, 123,
      0, 124, 125, 126, 127, 128, 129, 130,
    131,   0, 132, 133,   0, 134, 135, 136,
    137,   0,   0,   0,   0,   0,   0,   0,
      0,   0,   0,   0,   0,   0,   0,   0,
      0, 138,   0, 139,   0,   0,   0,   0,
    140, 141, 142, 143,   0,   0,   0,   0,
      0, 144,   0,   0,   0, 145,   0,   0,
    146, 147, 148, 149,   0,   0,   0,   0
    };
  
    @JsMethod public static int glyphByUnicode (CFF cff, int code)
    {
        for(int i=0; i<cff.charset.length(); i++) if(cff.charset.get(i)==code) return i;
        return -1;
    }
    
    @JsMethod public static int glyphBySE(CFF cff, int charcode)  // glyph by standard encoding
    {
        if ( charcode < 0 || charcode > 255 ) return -1;
        return CFF.glyphByUnicode(cff, CFF.tableSE[charcode]);        
    }
    
    @JsIgnore private static ArrayOfInt readEncoding (Uint8Array data, int offset, int num)
        {
//        var bin = Typr._bin;
        
        ArrayOfInt array = Collections.arrayOfInt(); //['.notdef'];
        array.push(0xdeadbeef);
        
        int format = data.intAt(offset);  offset++;
        //console.log("Encoding");
        //console.log(format);
        
        if(format==0)
        {
            int nCodes = data.intAt(offset);  offset++;
            for(int i=0; i<nCodes; i++)  array.push(data.intAt(offset+i));
        }
//        else if(format==1 || format==2)
//        {
//            while(charset.length<num)
//            {
//                var first = bin.readUshort(data, offset);  offset+=2;
//                var nLeft=0;
//                if(format==1) {  nLeft = data[offset];  offset++;  }
//                else          {  nLeft = bin.readUshort(data, offset);  offset+=2;  }
//                for(var i=0; i<=nLeft; i++)  {  charset.push(first);  first++;  }
//            }
//        }
        else throw new IllegalArgumentException("error: unknown encoding format: " + format);
        
        return array;
    }

    @JsIgnore private static ArrayOfInt readCharset (Uint8Array data, int offset, int num)
        {
//        var bin = Typr._bin;
        
        ArrayOfInt charset = Collections.arrayOfInt(); //['.notdef'];
        charset.push(0xdeadbeef);
        int format = data.intAt(offset);  offset++;
        
        if(format==0)
        {
            for(int i=0; i<num; i++) 
            {
                int first = bin.readUshort(data, offset);  offset+=2;
                charset.push(first);
            }
        }
        else if(format==1 || format==2)
        {
            while(charset.length()<num)
            {
                int first = bin.readUshort(data, offset);  offset+=2;
                int nLeft=0;
                if(format==1) {  nLeft = data.intAt(offset);  offset++;  }
                else          {  nLeft = bin.readUshort(data, offset);  offset+=2;  }
                for(int i=0; i<=nLeft; i++)  {  charset.push(first);  first++;  }
            }
        }
        else throw new IllegalArgumentException("error: format: " + format);
        
        return charset;
    }

    @JsIgnore private static int readIndex (Uint8Array data, int offset, ArrayOfInt inds)
    {
//        var bin = Typr._bin;
        
        char count = bin.readUshort(data, offset);  offset+=2;
        int offsize = data.intAt(offset);  offset++;
        
        if     (offsize==1) for(int i=0; i<count+1; i++) inds.push( data.intAt(offset+i) );
        else if(offsize==2) for(int i=0; i<count+1; i++) inds.push( bin.readUshort(data, offset+i*2) );
        else if(offsize==3) for(int i=0; i<count+1; i++) inds.push( bin.readUint  (data, offset+i*3 - 1) & 0x00ffffff );
        else if(count!=0) throw new IllegalArgumentException("unsupported offset size: " + offsize + ", count: " + (int)count);
        
        offset += (count+1)*offsize;
        return offset-1;
    }
    

    public static class GetCharStringOutput
    {
      public String val = null;
      public double numVal;
      public int size;
    }
    
    @JsIgnore public static void getCharString (ArrayOfInt data, int offset, GetCharStringOutput o)
    {
        int b0 = data.get(offset), b1 = data.get(offset+1), b2 = data.get(offset+2), b3 = data.get(offset+3), b4=data.get(offset+4);
        int vs = 1;
        boolean valSet = false;
        double numVal = 0;
        int op = 0;
        // operand
        if(b0<=20) { op = b0;  vs=1;  }
        if(b0==12) { op = b0*100+b1;  vs=2;  }
        //if(b0==19 || b0==20) { op = b0/ *+" "+b1* /;  vs=2; }
        if(21 <=b0 && b0<= 27) { op = b0;  vs=1; }
        if(b0==28) { valSet = true; numVal = bin.readShort(data,offset+1);  vs=3; }
        if(29 <=b0 && b0<= 31) { op = b0;  vs=1; }
        if(32 <=b0 && b0<=246) { valSet = true; numVal = b0-139;  vs=1; }
        if(247<=b0 && b0<=250) { valSet = true; numVal = (b0-247)*256+b1+108;  vs=2; }
        if(251<=b0 && b0<=254) { valSet = true; numVal =-(b0-251)*256-b1-108;  vs=2; }
        if(b0==255) { valSet = true; numVal = (double)bin.readInt(data, offset+1)/0xffff;  vs=5;   }
        
        if (valSet)
        {
          o.val = null;
          o.numVal = numVal;
        }
        else
        {
          o.val = "o"+op;
          o.numVal = 0;
        }
        o.size = vs;
    }
    
//    @JsMethod public static native JavaScriptObject readCharString (JavaScriptObject data, int offset, int length)
//        /*-{
//        var end = offset + length;
//        var bin = Typr._bin;
//        var arr = [];
//        
//        while(offset<end)
//        {
//            var b0 = data[offset], b1 = data[offset+1], b2 = data[offset+2], b3 = data[offset+3], b4=data[offset+4];
//            var vs = 1;
//            var op=null, val=null;
//            // operand
//            if(b0<=20) { op = b0;  vs=1;  }
//            if(b0==12) { op = b0*100+b1;  vs=2;  }
//            if(b0==19 || b0==20) { op = b0;  vs=2; } // if(b0==19 || b0==20) { op = b0/ *+" "+b1* /;  vs=2; }
//            
//            if(21 <=b0 && b0<= 27) { op = b0;  vs=1; }
//            if(b0==28) { val = bin.readShort(data,offset+1);  vs=3; }
//            if(29 <=b0 && b0<= 31) { op = b0;  vs=1; }
//            if(32 <=b0 && b0<=246) { val = b0-139;  vs=1; }
//            if(247<=b0 && b0<=250) { val = (b0-247)*256+b1+108;  vs=2; }
//            if(251<=b0 && b0<=254) { val =-(b0-251)*256-b1-108;  vs=2; }
//            if(b0==255) {  val = bin.readInt(data, offset+1)/0xffff;  vs=5;   }
//            
//            arr.push(val!=null ? val : "o"+op);
//            offset += vs;   
//
//            //var cv = arr[arr.length-1];
//            //if(cv==undefined) throw "error";
//            //console.log()
//        }   
//        return arr;
//    }-*/;

    @JsType
    public static class CffDict extends CffDictBase
    {

      @JsIgnore public void loadDictValue(int key, ArrayOfNumber val)
      {
        switch(key)
        {
        case 0:
          setRawVersion(val.get(0)); break;
        case 1:
          setRawNotice(val.get(0)); break;
        case 2:
          setRawFullName(val.get(0)); break;
        case 3:
          setRawFamilyName(val.get(0)); break;
        case 4:
          setWeight(val.get(0)); break;
        case 5:
          setFontBBox(val);
          break;
        case 6:
          setBlueValues(val.get(0));
          break;
        case 7:
          setOtherBlues(val.get(0));
          break;
        case 8:
          setFamilyBlues(val.get(0));
          break;
        case 9:
          setFamilyOtherBlues(val.get(0));
          break;
        case 10:
          setStdHW(val.get(0));
          break;
        case 11:
          setStdVW(val.get(0));
          break;
        case 12:
          break;
        case 13:
          setUniqueID(val.get(0));
          break;
        case 14:
          setXUID(val);
          break;
        case 15:
          setRawCharset(val.get(0));
          break;
        case 16:
          setEncoding(val.get(0));
          break;
        case 17:
          setCharStrings(val.get(0));
          break;
        case 18:
          setRawPrivate(val);
          break;
        case 19:
          setRawSubrs(val.get(0));
          break;
        case 20:
          setdefaultWidthX(val.get(0));
          break;
        case 21:
          setnominalWidthX(val.get(0));
          break;
        }
      }
      
      @JsIgnore public void loadEscapedDictValue(int key, ArrayOfNumber val)
      {
        switch(key)
        {
        case 0:
          setRawCopyright(val.get(0));
          break;
        case 1:
          setisFixedPitch(val.get(0) != 0);
          break;
        case 2:
          setItalicAngle(val.get(0));
          break;
        case 3:
          setUnderlinePosition(val.get(0));
          break;
        case 4:
          setUnderlineThickness(val.get(0));
          break;
        case 5:
          setPaintType(val.get(0));
          break;
        case 6:
          setCharstringType(val.get(0));
          break;
        case 7:
          setFontMatrix(val);
          break;
        case 8:
          setStrokeWidth(val.get(0));
          break;
        case 9:
          setBlueScale(val.get(0));
          break;
        case 10:
          setBlueShift(val.get(0));
          break;
        case 11:
          setBlueFuzz(val.get(0));
          break;
        case 12:
          setStemSnapH(val.get(0));
          break;
        case 13:
          setStemSnapV(val.get(0));
          break;
        case 14:
          setForceBold(val.get(0) != 0);
          break;
        case 17:
          setLanguageGroup(val.get(0));
          break;
        case 18:
          setExpansionFactor(val.get(0));
          break;
        case 19:
          setinitialRandomSeed(val.get(0));
          break;
        case 20:
          setSyntheticBase(val.get(0));
          break;
        case 21:
          setPostScript(val.get(0));
          break;
        case 22:
          setBaseFontName(val.get(0));
          break;
        case 23:
          setBaseFontBlend(val.get(0));
          break;
        case 30:
          setROS(val);
          break;
        case 31:
          setCIDFontVersion(val.get(0));
          break;
        case 32:
          setCIDFontRevision(val.get(0));
          break;
        case 33:
          setCIDFontType(val.get(0));
          break;
        case 34:
          setCIDCount(val.get(0));
          break;
        case 35:
          setUIDBase(val.get(0));
          break;
        case 36:
          setRawFDArray(val.get(0));
          break;
        case 37:
          setRawFDSelect(val.get(0));
          break;
        case 38:
          setFontName(val.get(0));
          break;
        }
      }
      
    }

    @JsIgnore private static CffDict readDict (Uint8Array data, int offset, int end, ArrayOfBoolean firstIsROS)
    {
        //var dict = [];
      CffDict dict = new CffDict();
        ArrayOfNumber carr = Collections.arrayOfNumber();
        boolean firstKey = true;
        
        while(offset<end)
        {
            int b0 = data.intAt(offset), b1 = data.intAt(offset+1), b2 = data.intAt(offset+2), b3 = data.intAt(offset+3), b4=data.intAt(offset+4);
            int vs = 1;
            double val = 0xdeadbeef;
            // operand
            if(b0==28) { val = bin.readShort(data,offset+1);  vs=3; }
            if(b0==29) { val = bin.readInt  (data,offset+1);  vs=5; }
            if(32 <=b0 && b0<=246) { val = b0-139;  vs=1; }
            if(247<=b0 && b0<=250) { val = (b0-247)*256+b1+108;  vs=2; }
            if(251<=b0 && b0<=254) { val =-(b0-251)*256-b1-108;  vs=2; }
            if(b0==255) {  val = (double)bin.readInt(data, offset+1)/0xffff;  vs=5;  throw new IllegalArgumentException("unknown number");  }
            
            if(b0==30) 
            {  
                ArrayOfInt nibs = Collections.arrayOfInt();
                vs = 1;
                while(true)
                {
                    int b = data.intAt(offset+vs);  vs++;
                    int nib0 = b>>4, nib1 = b&0xf;
                    if(nib0 != 0xf) nibs.push(nib0);  if(nib1!=0xf) nibs.push(nib1);
                    if(nib1==0xf) break;
                }
                String s = "";
                final String [] chars = new String[] {"0","1","2","3","4","5","6","7","8","9",".","e","e-","reserved","-","endOfNumber"};
                for(int i=0; i<nibs.length(); i++) s += chars[nibs.get(i)];
                //console.log(nibs);
                val = Double.parseDouble(s); //parseFloat(s);
            }

            if(b0<=21)  // operator
            {
              if (b0 != 12)
              {
                dict.loadDictValue(b0, carr);
              }
              vs=1;
              if(b0==12) {
                dict.loadEscapedDictValue(b1, carr);
                vs=2;
                if (firstKey && b1 == 30)
                  firstIsROS.set(0, true);
              }
              firstKey = false;
//              dict.put(key, carr.length()==1 ? carr.get(0) : carr);
              carr=Collections.arrayOfNumber();
            }
            else  carr.push(val);  
            
            offset += vs;       
        }   
        return dict;
    }
}