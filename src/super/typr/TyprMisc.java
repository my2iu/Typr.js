package typr;

import elemental.client.Browser;
import elemental.html.Uint8Array;

public class TyprMisc
{
  static boolean isInit = false;
  public static void init()
  {
    if (isInit) return;
    isInit = true;
    remapTypr();
    bin.t = new JsUnion();
  }
  private static native void remapTypr() /*-{
    window.Typr = $wnd.Typr;
  }-*/;
  
  public static void consoleLog(Object s)
  {
    Browser.getWindow().getConsole().log(s);
  }

  public static Uint8Array slicedUint8Array(Uint8Array data, int offset, int length)
  {
    return Browser.getWindow().newUint8Array(data.getBuffer().slice(offset, offset + length), 0, length);
  }

}
