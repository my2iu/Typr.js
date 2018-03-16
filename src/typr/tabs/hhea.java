package typr.tabs;

import elemental.html.Uint8Array;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import typr.bin;

/**
 * Horizontal Header
 */
public class hhea
{
  @JsProperty public int advanceWidthMax;
  @JsProperty public int ascender;
  @JsProperty public int descender;
  @JsProperty public int lineGap;
  @JsProperty public short minLeftSideBearing;
  @JsProperty public short minRightSideBearing;
  @JsProperty public short xMaxExtent;
  @JsProperty public short caretSlopeRise;
  @JsProperty public short caretSlopeRun;
  @JsProperty public short caretOffset;
  @JsProperty public short metricDataFormat;
  @JsProperty public char numberOfHMetrics;
  
  @JsIgnore public static hhea parse (Uint8Array data, int offset, int length)
  {
//	var bin = Typr._bin;
	hhea obj = new hhea();
	double tableVersion = bin.readFixed(data, offset);  offset += 4;
	obj.ascender  = bin.readShort(data, offset);  offset += 2;
	obj.descender = bin.readShort(data, offset);  offset += 2;
	obj.lineGap = bin.readShort(data, offset);  offset += 2;
	
	obj.advanceWidthMax = bin.readUshort(data, offset);  offset += 2;
	obj.minLeftSideBearing  = bin.readShort(data, offset);  offset += 2;
	obj.minRightSideBearing = bin.readShort(data, offset);  offset += 2;
	obj.xMaxExtent = bin.readShort(data, offset);  offset += 2;
	
	obj.caretSlopeRise = bin.readShort(data, offset);  offset += 2;
	obj.caretSlopeRun  = bin.readShort(data, offset);  offset += 2;
	obj.caretOffset    = bin.readShort(data, offset);  offset += 2;
	
	offset += 4*2;
	
	obj.metricDataFormat = bin.readShort (data, offset);  offset += 2;
	obj.numberOfHMetrics = bin.readUshort(data, offset);  offset += 2;
	return obj;
  }
}