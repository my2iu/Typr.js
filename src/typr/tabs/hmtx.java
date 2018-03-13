package typr.tabs;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.html.Uint8Array;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType(namespace="Typr")
public class hmtx
{
  @JsIgnore  public static native JavaScriptObject parse (Uint8Array data, int offset, int length, JavaScriptObject font)
  /*-{
	var bin = Typr._bin;
	var obj = {};
	
	obj.aWidth = [];
	obj.lsBearing = [];
	
	
	var aw = 0, lsb = 0;
	
	for(var i=0; i<font.maxp.numGlyphs; i++)
	{
		if(i<font.hhea.numberOfHMetrics) {  aw=bin.readUshort(data, offset);  offset += 2;  lsb=bin.readShort(data, offset);  offset+=2;  }
		obj.aWidth.push(aw);
		obj.lsBearing.push(lsb);
	}
	
	return obj;
}-*/;
}