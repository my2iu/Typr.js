package typr;

import com.google.gwt.core.client.JavaScriptObject;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(namespace="Typr",name="_bin")
public class bin
{
  @JsMethod public static native JavaScriptObject readFixed (JavaScriptObject data, JavaScriptObject o)
  /*-{
		return ((data[o]<<8) | data[o+1]) +  (((data[o+2]<<8)|data[o+3])/(256*256+4));
	}-*/;
	@JsMethod public static native JavaScriptObject readF2dot14 (JavaScriptObject data, JavaScriptObject o)
	/*-{
		var num = Typr._bin.readShort(data, o);
		return num / 16384;
		
		var intg = (num >> 14), frac = ((num & 0x3fff)/(0x3fff+1));
		return (intg>0) ? (intg+frac) : (intg-frac);
	}-*/;
	@JsMethod public static native JavaScriptObject readInt (JavaScriptObject buff, JavaScriptObject p)
	/*-{
		//if(p>=buff.length) throw "error";
		var a = Typr._bin.t.uint8;
		a[0] = buff[p+3];
		a[1] = buff[p+2];
		a[2] = buff[p+1];
		a[3] = buff[p];
		return Typr._bin.t.int32[0];
	}-*/;
	
	@JsMethod public static native JavaScriptObject readInt8 (JavaScriptObject buff, JavaScriptObject p)
	/*-{
		//if(p>=buff.length) throw "error";
		var a = Typr._bin.t.uint8;
		a[0] = buff[p];
		return Typr._bin.t.int8[0];
	}-*/;
	@JsMethod public static native JavaScriptObject readShort (JavaScriptObject buff, JavaScriptObject p)
	/*-{
		//if(p>=buff.length) throw "error";
		var a = Typr._bin.t.uint8;
		a[1] = buff[p]; a[0] = buff[p+1];
		return Typr._bin.t.int16[0];
	}-*/;
	@JsMethod public static native JavaScriptObject readUshort (JavaScriptObject buff, JavaScriptObject p)
	/*-{
		//if(p>=buff.length) throw "error";
		return (buff[p]<<8) | buff[p+1];
	}-*/;
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
static native void init() /*-{ 
Typr._bin._tdec = window["TextDecoder"] ? new window["TextDecoder"]() : null;
Typr._bin.t = {
	buff: new ArrayBuffer(8),
};
Typr._bin.t.int8   = new Int8Array  (Typr._bin.t.buff);
Typr._bin.t.uint8  = new Uint8Array (Typr._bin.t.buff);
Typr._bin.t.int16  = new Int16Array (Typr._bin.t.buff);
Typr._bin.t.uint16 = new Uint16Array(Typr._bin.t.buff);
Typr._bin.t.int32  = new Int32Array (Typr._bin.t.buff);
Typr._bin.t.uint32 = new Uint32Array(Typr._bin.t.buff);
}-*/;


}