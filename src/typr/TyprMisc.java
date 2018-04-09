package typr;

public class TyprMisc
{
  static boolean isInit = false;
  public static void init()
  {
    if (isInit) return;
    isInit = true;
    bin.t = new JreUnion();
  }
}
