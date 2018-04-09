package typr;

import elemental.html.Uint8Array;
import elemental.util.ArrayOfInt;
import elemental.util.ArrayOfString;
import elemental.util.Collections;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType(namespace="Typr",name="_bin")
public class bin
{
  @JsMethod public static double readFixed (Uint8Array data, int o)
  {
		return ((data.intAt(o)<<8) | data.intAt(o+1)) +  (((data.intAt(o+2)<<8)|data.intAt(o+3))/(256.0*256+4));
	}
  @JsMethod public static int readVersion (Uint8Array data, int o)
  {
    return readInt(data, o);
  }
	@JsMethod public static double readF2dot14 (Uint8Array data, int o)
	{
		short num = bin.readShort(data, o);
		return num / 16384.0;
//		
//		var intg = (num >> 14), frac = ((num & 0x3fff)/(0x3fff+1));
//		return (intg>0) ? (intg+frac) : (intg-frac);
	}
	@JsMethod public static int readInt (Uint8Array buff, int p)
	{
		//if(p>=buff.length) throw "error";
	  t.writeUint8(0, (byte)buff.intAt(p+3));
      t.writeUint8(1, (byte)buff.intAt(p+2));
      t.writeUint8(2, (byte)buff.intAt(p+1));
      t.writeUint8(3, (byte)buff.intAt(p));
      return t.readInt32();
	}
	
	@JsMethod public static byte readInt8 (Uint8Array buff, int p)
	{
		//if(p>=buff.length) throw "error";
		t.writeUint8(0, (byte)buff.intAt(p));
		return (byte)t.readInt8();
	}
	@JsMethod public static short readShort (Uint8Array buff, int p)
	{
		//if(p>=buff.length) throw "error";
		t.writeUint8(1, (byte)buff.intAt(p));
		t.writeUint8(0, (byte)buff.intAt(p+1));
		return (short)t.readInt16();
	}
	@JsMethod public static char readUshort (Uint8Array buff, int p)
	{
		//if(p>=buff.length) throw "error";
		return (char)((buff.intAt(p)<<8) | buff.intAt(p+1));
	}
	@JsMethod public static ArrayOfInt readUshorts (Uint8Array buff, int p, int len)
	{
		ArrayOfInt arr = Collections.arrayOfInt();
		for(int i=0; i<len; i++) arr.push(bin.readUshort(buff, p+i*2));
		return arr;
	};
	@JsMethod public static int readUint (Uint8Array buff, int p)
	{
		//if(p>=buff.length) throw "error";
	  t.writeUint8(3, (byte)buff.intAt(p));  
	  t.writeUint8(2, (byte)buff.intAt(p+1));  
	  t.writeUint8(1, (byte)buff.intAt(p+2));  
	  t.writeUint8(0, (byte)buff.intAt(p+3));
	  return t.readUint32();
	}
	@JsMethod public static double readUint64 (Uint8Array buff, int p)
	{
		//if(p>=buff.length) throw "error";
		return (((double)bin.readUint(buff, p))*(0xffffffff+1)) + bin.readUint(buff, p+4);
	}
	@JsMethod public static String readASCII (Uint8Array buff, int p, int l)	// l : length in Characters (not Bytes)
	{
		//if(p>=buff.length) throw "error";
		String s = "";
		for(int i = 0; i < l; i++) s += String.valueOf((char)buff.intAt(p+i));
		return s;
	}
	@JsMethod public static String readUnicode (Uint8Array buff, int p, int l)
	{
		//if(p>=buff.length) throw "error";
		String s = "";
		for(int i = 0; i < l; i++)	
		{
			char c = (char)((buff.intAt(p++)<<8) | buff.intAt(p++));
			s += String.valueOf(c);
		}
		return s;
	}
	@JsMethod public static String readUTF8 (Uint8Array buff, int p, int l)
	{
	  return t.readUTF8(buff, p, l);
	}
	@JsMethod public static ArrayOfInt readBytes (Uint8Array buff, int p, int l)
	{
		//if(p>=buff.length) throw "error";
		ArrayOfInt arr = Collections.arrayOfInt();
		for(int i=0; i<l; i++) arr.push(buff.intAt(p+i));
		return arr;
	}
	@JsMethod public static ArrayOfString readASCIIArray (Uint8Array buff, int p, int l)	// l : length in Characters (not Bytes)
	{
		//if(p>=buff.length) throw "error";
		ArrayOfString s = Collections.arrayOfString();
		for(int i = 0; i < l; i++)	
			s.push(String.valueOf((char)buff.intAt(p+i)));
		return s;
	}
  static abstract class Union
  {
    abstract void writeUint8(int offset, byte b);
    abstract int readInt32(); 
    abstract int readUint32(); 
    abstract int readInt8();
    abstract int readInt16();
    abstract String readUTF8 (Uint8Array buff, int p, int l);
  }
  public static Union t;
}