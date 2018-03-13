package typr.tabs;

import elemental.html.Uint8Array;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import typr.bin;

@JsType(namespace="Typr")
public class post
{
  @JsProperty int version;
  @JsProperty int italicAngle;
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