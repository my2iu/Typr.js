package typr;

import elemental.util.ArrayOfNumber;
import elemental.util.ArrayOfString;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative=true,namespace=JsPackage.GLOBAL) class TyprPath
{
  @JsProperty public ArrayOfString cmds;
  @JsProperty public ArrayOfNumber crds;
}