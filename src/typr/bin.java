package typr;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.client.Browser;
import elemental.html.ArrayBuffer;
import elemental.html.Int16Array;
import elemental.html.Int32Array;
import elemental.html.Int8Array;
import elemental.html.Uint16Array;
import elemental.html.Uint32Array;
import elemental.html.Uint8Array;
import elemental.util.SettableInt;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(namespace="Typr",name="_bin")
public class bin
{
  @JsMethod public static int readFixed (Uint8Array data, int o)
  {
		return ((data.intAt(o)<<8) | data.intAt(o+1)) +  (((data.intAt(o+2)<<8)|data.intAt(o+3))/(256*256+4));
	}
	@JsMethod public static native JavaScriptObject readF2dot14 (JavaScriptObject data, JavaScriptObject o)
	/*-{
		var num = Typr._bin.readShort(data, o);
		return num / 16384;
		
		var intg = (num >> 14), frac = ((num & 0x3fff)/(0x3fff+1));
		return (intg>0) ? (intg+frac) : (intg-frac);
	}-*/;
	@JsMethod public static int readInt (Uint8Array buff, int p)
	{
		//if(p>=buff.length) throw "error";
		SettableInt a = (SettableInt)t.uint8;
		a.setAt(0, buff.intAt(p+3));
		a.setAt(1, buff.intAt(p+2));
		a.setAt(2, buff.intAt(p+1));
		a.setAt(3, buff.intAt(p));
		return t.int32.intAt(0);
	}
	
	@JsMethod public static native JavaScriptObject readInt8 (JavaScriptObject buff, JavaScriptObject p)
	/*-{
		//if(p>=buff.length) throw "error";
		var a = Typr._bin.t.uint8;
		a[0] = buff[p];
		return Typr._bin.t.int8[0];
	}-*/;
	@JsMethod public static short readShort (Uint8Array buff, int p)
	{
		//if(p>=buff.length) throw "error";
		SettableInt a = (SettableInt)t.uint8;
		a.setAt(1, buff.intAt(p)); a.setAt(0, buff.intAt(p+1));
		return (short)t.int16.intAt(0);
	}
	@JsMethod public static int readUshort (Uint8Array buff, int p)
	{
		//if(p>=buff.length) throw "error";
		return (buff.intAt(p)<<8) | buff.intAt(p+1);
	}
	@JsMethod public static native JavaScriptObject readUshorts (JavaScriptObject buff, JavaScriptObject p, JavaScriptObject len)
	/*-{
		var arr = [];
		for(var i=0; i<len; i++) arr.push(Typr._bin.readUshort(buff, p+i*2));
		return arr;
	}-*/;
	@JsMethod public static native JavaScriptObject readUint (JavaScriptObject buff, JavaScriptObject p)
	/*-{
		//if(p>=buff.length) throw "error";
		var a = Typr._bin.t.uint8;
		a[3] = buff[p];  a[2] = buff[p+1];  a[1] = buff[p+2];  a[0] = buff[p+3];
		return Typr._bin.t.uint32[0];
	}-*/;
	@JsMethod public static native JavaScriptObject readUint64 (JavaScriptObject buff, JavaScriptObject p)
	/*-{
		//if(p>=buff.length) throw "error";
		return (Typr._bin.readUint(buff, p)*(0xffffffff+1)) + Typr._bin.readUint(buff, p+4);
	}-*/;
	@JsMethod public static native JavaScriptObject readASCII (JavaScriptObject buff, JavaScriptObject p, JavaScriptObject l)	// l : length in Characters (not Bytes)
	/*-{
		//if(p>=buff.length) throw "error";
		var s = "";
		for(var i = 0; i < l; i++) s += String.fromCharCode(buff[p+i]);
		return s;
	}-*/;
	@JsMethod public static native JavaScriptObject readUnicode (JavaScriptObject buff, JavaScriptObject p, JavaScriptObject l)
	/*-{
		//if(p>=buff.length) throw "error";
		var s = "";
		for(var i = 0; i < l; i++)	
		{
			var c = (buff[p++]<<8) | buff[p++];
			s += String.fromCharCode(c);
		}
		return s;
	}-*/;
	@JsProperty static JavaScriptObject _tdec;
	@JsMethod public static native JavaScriptObject readUTF8 (JavaScriptObject buff, JavaScriptObject p, JavaScriptObject l) /*-{
		var tdec = Typr._bin._tdec;
		if(tdec && p==0 && l==buff.length) return tdec["decode"](buff);
		return Typr._bin.readASCII(buff,p,l);
	}-*/;
	@JsMethod public static native JavaScriptObject readBytes (JavaScriptObject buff, JavaScriptObject p, JavaScriptObject l)
	/*-{
		//if(p>=buff.length) throw "error";
		var arr = [];
		for(var i=0; i<l; i++) arr.push(buff[p+i]);
		return arr;
	}-*/;
	@JsMethod public static native JavaScriptObject readASCIIArray (JavaScriptObject buff, JavaScriptObject p, JavaScriptObject l)	// l : length in Characters (not Bytes)
	/*-{
		//if(p>=buff.length) throw "error";
		var s = [];
		for(var i = 0; i < l; i++)	
			s.push(String.fromCharCode(buff[p+i]));
		return s;
	}-*/;
@JsIgnore static native void init() /*-{ 
Typr._bin._tdec = window["TextDecoder"] ? new window["TextDecoder"]() : null;
}-*/;
  @JsType
  static class Union
  {
    @JsProperty ArrayBuffer buff = Browser.getWindow().newUint8Array(8).getBuffer();
    @JsProperty Int8Array int8 = Browser.getWindow().newInt8Array(buff, 0, 8); 
    @JsProperty Uint8Array uint8 = Browser.getWindow().newUint8Array(buff, 0, 8); 
    @JsProperty Int16Array int16 = Browser.getWindow().newInt16Array(buff, 0, 4); 
    @JsProperty Uint16Array uint16 = Browser.getWindow().newUint16Array(buff, 0, 4); 
    @JsProperty Int32Array int32 = Browser.getWindow().newInt32Array(buff, 0, 2); 
    @JsProperty Uint32Array uint32 = Browser.getWindow().newUint32Array(buff, 0, 2); 
  }
  @JsProperty static Union t = new Union();
}