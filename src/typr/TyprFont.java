package typr;

import elemental.html.Uint8Array;
import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import jsinterop.annotations.JsProperty;
import typr.lctf.LayoutCommonTable;
import typr.tabs.GPOSParser.GPOSTab;
import typr.tabs.GSUBParser.GSUBTab;

public class TyprFont
{
//  @JsProperty public Uint8Array _data;
  @JsProperty public Uint8Array _rawGlyfTableData;  // Glyf table only parsed when needed

  
  @JsProperty public typr.tabs.cmap cmap;
  @JsProperty public typr.tabs.head head;
  @JsProperty public typr.tabs.hhea hhea;
  @JsProperty public typr.tabs.maxp maxp;
  @JsProperty public typr.tabs.hmtx hmtx;
  @JsProperty public typr.tabs.name name;
  @JsProperty public typr.tabs.OS2 OS2;
  @JsProperty public typr.tabs.post post;
  @JsProperty public ArrayOfInt loca;
  @JsProperty public ArrayOf<typr.tabs.glyf> glyf;
  @JsProperty public typr.tabs.kern kern;
  @JsProperty public typr.tabs.CFF CFF;
  @JsProperty public LayoutCommonTable<GPOSTab> GPOS;
  @JsProperty public LayoutCommonTable<GSUBTab> GSUB;
  @JsProperty public typr.tabs.SVG SVG;
}