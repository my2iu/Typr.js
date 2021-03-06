package typr.tabs;

import elemental.html.Uint8Array;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import typr.bin;

public class post
{
  @JsProperty double version;
  @JsProperty double italicAngle;
  @JsProperty short underlinePosition;
  @JsProperty short underlineThickness;
  
  @JsIgnore public static post parse (Uint8Array data, int offset, int length)
  {
//	var bin = Typr._bin;
	post obj = new post();
	
	obj.version           = bin.readFixed(data, offset);  offset+=4;
	obj.italicAngle       = bin.readFixed(data, offset);  offset+=4;
	obj.underlinePosition = bin.readShort(data, offset);  offset+=2;
	obj.underlineThickness = bin.readShort(data, offset);  offset+=2;

	return obj;
  }
}