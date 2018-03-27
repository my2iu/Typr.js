package typr.tabs;

import elemental.html.Uint8Array;
import elemental.util.ArrayOfInt;
import elemental.util.Collections;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import typr.bin;

@JsType(namespace="Typr")
public class OS2
{
  @JsIgnore public static OS2 parse (Uint8Array data, int offset, int length)
  {
//	var bin = Typr._bin;
	char ver = bin.readUshort(data, offset); offset += 2;
	
	OS2 obj = new OS2();
	if     (ver==0) version0(data, offset, obj);
	else if(ver==1) version1(data, offset, obj);
	else if(ver==2 || ver==3 || ver==4) version2(data, offset, obj);
	else if(ver==5) version5(data, offset, obj);
	else throw new IllegalArgumentException("unknown OS/2 table version: "+(int)ver);
	
	return obj;
  }

  // Version 0 properties
  @JsProperty public short xAvgCharWidth;
  @JsProperty public char usWeightClass;
  @JsProperty public char usWidthClass;
  @JsProperty public char fsType;
  @JsProperty public short ySubscriptXSize;
  @JsProperty public short ySubscriptYSize;
  @JsProperty public short ySubscriptXOffset;
  @JsProperty public short ySubscriptYOffset;
  @JsProperty public short ySuperscriptXSize;
  @JsProperty public short ySuperscriptYSize;
  @JsProperty public short ySuperscriptXOffset;
  @JsProperty public short ySuperscriptYOffset;
  @JsProperty public short yStrikeoutSize;
  @JsProperty public short yStrikeoutPosition;
  @JsProperty public short sFamilyClass;
  @JsProperty public ArrayOfInt panose;
  @JsProperty public int ulUnicodeRange1;
  @JsProperty public int ulUnicodeRange2;
  @JsProperty public int offset;
  @JsProperty public int ulUnioffsetnge3;
  @JsProperty public ArrayOfInt achVendID;
  @JsProperty public char fsSelection;
  @JsProperty public char usFirstCharIndex;
  @JsProperty public char usLastCharIndex;
  @JsProperty public short sTypoAscender;
  @JsProperty public short sTypoDescender;
  @JsProperty public short sTypoLineGap;
  @JsProperty public char usWinAscent;
  @JsProperty public char usWinDescent;

  // Version 1 Properties
  @JsProperty public Integer ulCodePageRange1;
  @JsProperty public Integer ulCodePageRange2;

  // Version 2 Properties
  @JsProperty public Short sxHeight;
  @JsProperty public Short sCapHeight;
  @JsProperty public Character usDefault;
  @JsProperty public Character usBreak;
  @JsProperty public Character usMaxContext;
  
  // Version 5 Properties
  @JsProperty public Character usLowerOpticalPointSize;
  @JsProperty public Character usUpperOpticalPointSize;
  
  @JsIgnore private static int version0 (Uint8Array data, int offset, OS2 obj)
  {
//	var bin = Typr._bin;
	obj.xAvgCharWidth = bin.readShort(data, offset); offset += 2;
	obj.usWeightClass = bin.readUshort(data, offset); offset += 2;
	obj.usWidthClass  = bin.readUshort(data, offset); offset += 2;
	obj.fsType = bin.readUshort(data, offset); offset += 2;
	obj.ySubscriptXSize = bin.readShort(data, offset); offset += 2;
	obj.ySubscriptYSize = bin.readShort(data, offset); offset += 2;
	obj.ySubscriptXOffset = bin.readShort(data, offset); offset += 2;
	obj.ySubscriptYOffset = bin.readShort(data, offset); offset += 2; 
	obj.ySuperscriptXSize = bin.readShort(data, offset); offset += 2; 
	obj.ySuperscriptYSize = bin.readShort(data, offset); offset += 2; 
	obj.ySuperscriptXOffset = bin.readShort(data, offset); offset += 2;
	obj.ySuperscriptYOffset = bin.readShort(data, offset); offset += 2;
	obj.yStrikeoutSize = bin.readShort(data, offset); offset += 2;
	obj.yStrikeoutPosition = bin.readShort(data, offset); offset += 2;
	obj.sFamilyClass = bin.readShort(data, offset); offset += 2;
	obj.panose = bin.readBytes(data, offset, 10);  offset += 10;
	obj.ulUnicodeRange1	= bin.readUint(data, offset);  offset += 4;
	obj.ulUnicodeRange2	= bin.readUint(data, offset);  offset += 4;
	obj.ulUnioffsetnge3	= bin.readUint(data, offset);  offset += 4;
	obj.offset	= bin.readUint(data, offset);  offset += 4;
	obj.achVendID = Collections.arrayOfInt();
	obj.achVendID.push(bin.readInt8(data, offset));
	obj.achVendID.push(bin.readInt8(data, offset+1));
	obj.achVendID.push(bin.readInt8(data, offset+2));
	obj.achVendID.push(bin.readInt8(data, offset+3));
	offset += 4;
	obj.fsSelection	 = bin.readUshort(data, offset); offset += 2;
	obj.usFirstCharIndex = bin.readUshort(data, offset); offset += 2;
	obj.usLastCharIndex = bin.readUshort(data, offset); offset += 2;
	obj.sTypoAscender = bin.readShort(data, offset); offset += 2;
	obj.sTypoDescender = bin.readShort(data, offset); offset += 2;
	obj.sTypoLineGap = bin.readShort(data, offset); offset += 2;
	obj.usWinAscent = bin.readUshort(data, offset); offset += 2;
	obj.usWinDescent = bin.readUshort(data, offset); offset += 2;
	return offset;
  }

  @JsIgnore private static int version1 (Uint8Array data, int offset, OS2 obj)
  {
//	var bin = Typr._bin;
	offset = version0(data, offset, obj);
	
	obj.ulCodePageRange1 = bin.readUint(data, offset); offset += 4;
	obj.ulCodePageRange2 = bin.readUint(data, offset); offset += 4;
	return offset;
  }

  @JsIgnore private static int version2 (Uint8Array data, int offset, OS2 obj)
  {
//	var bin = Typr._bin;
	offset = version1(data, offset, obj);
	
	obj.sxHeight = bin.readShort(data, offset); offset += 2;
	obj.sCapHeight = bin.readShort(data, offset); offset += 2;
	obj.usDefault = bin.readUshort(data, offset); offset += 2;
	obj.usBreak = bin.readUshort(data, offset); offset += 2;
	obj.usMaxContext = bin.readUshort(data, offset); offset += 2;
	return offset;
  }

  @JsIgnore private static int version5 (Uint8Array data, int offset, OS2 obj)
  {
//	var bin = Typr._bin;
	offset = version2(data, offset, obj);

	obj.usLowerOpticalPointSize = bin.readUshort(data, offset); offset += 2;
	obj.usUpperOpticalPointSize = bin.readUshort(data, offset); offset += 2;
	return offset;
  }
}