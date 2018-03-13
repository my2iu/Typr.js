package typr.tabs;

import com.google.gwt.core.client.JavaScriptObject;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType(namespace="Typr")
public class loca
{
  @JsMethod public static native JavaScriptObject parse (JavaScriptObject data, int offset, int length, JavaScriptObject font)
  /*-{
	var bin = Typr._bin;
	var obj = [];
	
	var ver = font.head.indexToLocFormat;
	//console.log("loca", ver, length, 4*font.maxp.numGlyphs);
	var len = font.maxp.numGlyphs+1;
	
	if(ver==0) for(var i=0; i<len; i++) obj.push(bin.readUshort(data, offset+(i<<1))<<1);
	if(ver==1) for(var i=0; i<len; i++) obj.push(bin.readUint  (data, offset+(i<<2))   );
	
	return obj;
}-*/;
}