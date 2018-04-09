package typr;

import elemental.util.ArrayOfNumber;
import elemental.util.ArrayOfString;
import elemental.util.Collections;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(namespace=JsPackage.GLOBAL)
public class TyprPath
{
  public TyprPath() { cmds = Collections.arrayOfString(); crds = Collections.arrayOfNumber(); }
  @JsProperty public ArrayOfString cmds;
  @JsProperty public ArrayOfNumber crds;
}