package typr.tabs;

import elemental.html.Uint8Array;
import elemental.util.ArrayOfInt;
import elemental.util.Collections;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import typr.TyprFont;
import typr.bin;

public class hmtx
{
  @JsProperty public ArrayOfInt aWidth;
  @JsProperty public ArrayOfInt lsBearing;
  
  @JsIgnore  public static hmtx parse (Uint8Array data, int offset, int length, TyprFont font)
  {
//	var bin = Typr._bin;
	hmtx obj = new hmtx();
	
	obj.aWidth = Collections.arrayOfInt();
	obj.lsBearing = Collections.arrayOfInt();
	
	
	int aw = 0, lsb = 0;
	
	for(int i=0; i<font.maxp.numGlyphs; i++)
	{
		if(i<font.hhea.numberOfHMetrics) {  aw=bin.readUshort(data, offset);  offset += 2;  lsb=bin.readShort(data, offset);  offset+=2;  }
		obj.aWidth.push(aw);
		obj.lsBearing.push(lsb);
	}
	
	return obj;
  }
}