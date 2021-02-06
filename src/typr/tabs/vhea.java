package typr.tabs;

import elemental.html.Uint8Array;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import typr.bin;

/**
 * Vertical Header
 */
public class vhea
{
//  @JsProperty public int version;
  @JsProperty public int vertTypoAscender;
  @JsProperty public int vertTypoDescender;
  @JsProperty public int vertTypoLineGap;
  @JsProperty public int advanceHeightMax;
  @JsProperty public int minTopSideBearing;
  @JsProperty public int minBottomSideBearing;
  @JsProperty public int yMaxExtent;
  @JsProperty public int caretSlopeRise;
  @JsProperty public int caretSlopeRun;
  @JsProperty public int caretOffset;
  // reserved
  // reserved
  // reserved
  // reserved
  @JsProperty public int metricDataFormat;
  @JsProperty public int numOfLongVerMetrics;
  
  @JsIgnore public static vhea parse (Uint8Array data, int offset, int length)
  {
	vhea obj = new vhea();
	double tableVersion = bin.readFixed(data, offset);  offset += 4;
	obj.vertTypoAscender = bin.readShort(data, offset);  offset += 2; 
    obj.vertTypoDescender = bin.readShort(data, offset);  offset += 2;
    obj.vertTypoLineGap = bin.readShort(data, offset);  offset += 2; 

    obj.advanceHeightMax = bin.readShort(data, offset);  offset += 2; 
    obj.minTopSideBearing = bin.readShort(data, offset);  offset += 2; 
    obj.minBottomSideBearing = bin.readShort(data, offset);  offset += 2; 
    obj.yMaxExtent = bin.readShort(data, offset);  offset += 2; 

	obj.caretSlopeRise = bin.readShort(data, offset);  offset += 2;
	obj.caretSlopeRun  = bin.readShort(data, offset);  offset += 2;
	obj.caretOffset    = bin.readShort(data, offset);  offset += 2;
	
	offset += 4*2;
	
	obj.metricDataFormat = bin.readShort (data, offset);  offset += 2;
	obj.numOfLongVerMetrics = bin.readUshort(data, offset);  offset += 2;
	return obj;
  }
}