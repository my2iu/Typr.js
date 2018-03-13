package typr;

import elemental.html.Uint8Array;
import elemental.util.ArrayOfInt;
import jsinterop.annotations.JsProperty;

public class TyprFont
{
  @JsProperty Uint8Array _data;
  
  @JsProperty public typr.tabs.cmap cmap;
  @JsProperty public typr.tabs.head head;
  @JsProperty public typr.tabs.hhea hhea;
  @JsProperty public typr.tabs.maxp maxp;
  @JsProperty public typr.tabs.hmtx hmtx;
  @JsProperty public typr.tabs.name name;
  @JsProperty public typr.tabs.OS2 OS2;
  @JsProperty public typr.tabs.post post;
  @JsProperty public ArrayOfInt loca;
  @JsProperty public typr.tabs.glyf glyf;
  @JsProperty public typr.tabs.kern kern;
  @JsProperty public typr.tabs.CFF CFF;
  @JsProperty public typr.tabs.GPOS GPOS;
  @JsProperty public typr.tabs.GSUB GSUB;
  @JsProperty public typr.tabs.SVG SVG;
}