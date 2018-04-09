package typr;

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
}
