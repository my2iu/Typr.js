package typr.tabs;

import elemental.html.Uint8Array;
import elemental.util.ArrayOfInt;
import elemental.util.Collections;
import jsinterop.annotations.JsIgnore;
import typr.TyprFont;
import typr.bin;

public class loca
{
  @JsIgnore public static ArrayOfInt parse (Uint8Array data, int offset, int length, TyprFont font)
  {
//	var bin = Typr._bin;
	ArrayOfInt obj = Collections.arrayOfInt();
	
	short ver = font.head.indexToLocFormat;
	//console.log("loca", ver, length, 4*font.maxp.numGlyphs);
	int len = font.maxp.numGlyphs+1;
	
	if(ver==0) for(int i=0; i<len; i++) obj.push(bin.readUshort(data, offset+(i<<1))<<1);
	if(ver==1) for(int i=0; i<len; i++) obj.push(bin.readUint  (data, offset+(i<<2))   );
	
	return obj;
  }
}