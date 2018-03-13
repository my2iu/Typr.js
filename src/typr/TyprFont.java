package typr;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative=true,namespace=JsPackage.GLOBAL)
public class TyprFont
{
  @JsProperty public typr.tabs.head head;
  @JsProperty public typr.tabs.hhea hhea;
}