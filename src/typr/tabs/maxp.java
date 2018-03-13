package typr.tabs;

import elemental.html.Uint8Array;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import typr.bin;

public class maxp
{
  @JsProperty public char numGlyphs;
  @JsProperty public char maxPoints;
  @JsProperty public char maxContours;
  @JsProperty public char maxCompositePoints;
  @JsProperty public char maxCompositeContours;
  @JsProperty public char maxZones;
  @JsProperty public char maxTwilightPoints;
  @JsProperty public char maxStorage;
  @JsProperty public char maxFunctionDefs;
  @JsProperty public char maxInstructionDefs;
  @JsProperty public char maxStackElements;
  @JsProperty public char maxSizeOfInstructions;
  @JsProperty public char maxComponentElements;
  @JsProperty public char maxComponentDepth;
  
  @JsIgnore  public static maxp parse (Uint8Array data, int offset, int length)
  {
	//console.log(data.length, offset, length);
	
//	var bin = Typr._bin;
	maxp obj = new maxp();
	
	// both versions 0.5 and 1.0
	int ver = bin.readUint(data, offset); offset += 4;
	obj.numGlyphs = bin.readUshort(data, offset);  offset += 2;
	
	// only 1.0
	if(ver == 0x00010000)
	{
		obj.maxPoints             = bin.readUshort(data, offset);  offset += 2;
		obj.maxContours           = bin.readUshort(data, offset);  offset += 2;
		obj.maxCompositePoints    = bin.readUshort(data, offset);  offset += 2;
		obj.maxCompositeContours  = bin.readUshort(data, offset);  offset += 2;
		obj.maxZones              = bin.readUshort(data, offset);  offset += 2;
		obj.maxTwilightPoints     = bin.readUshort(data, offset);  offset += 2;
		obj.maxStorage            = bin.readUshort(data, offset);  offset += 2;
		obj.maxFunctionDefs       = bin.readUshort(data, offset);  offset += 2;
		obj.maxInstructionDefs    = bin.readUshort(data, offset);  offset += 2;
		obj.maxStackElements      = bin.readUshort(data, offset);  offset += 2;
		obj.maxSizeOfInstructions = bin.readUshort(data, offset);  offset += 2;
		obj.maxComponentElements  = bin.readUshort(data, offset);  offset += 2;
		obj.maxComponentDepth     = bin.readUshort(data, offset);  offset += 2;
	}
	
	return obj;
  }
}