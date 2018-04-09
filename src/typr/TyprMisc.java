package typr;

import com.google.gwt.core.shared.GwtIncompatible;

import elemental.client.Browser;
import elemental.html.ArrayBuffer;
import elemental.html.Uint8Array;

@GwtIncompatible public class TyprMisc
{
  static boolean isInit = false;
  public static void init()
  {
    if (isInit) return;
    isInit = true;
    bin.t = new JreUnion();
  }
  
  public static Uint8Array slicedUint8Array(Uint8Array data, int offset, int length)
  {
    // Not a real slice (just for testing purposes)
    return new Uint8Array() {
      @Override
      public ArrayBuffer getBuffer()
      {
        throw new IllegalArgumentException("Operation not supported");      
      }

      @Override
      public int getByteLength()
      {
        throw new IllegalArgumentException("Operation not supported");      
      }

      @Override
      public int getByteOffset()
      {
        throw new IllegalArgumentException("Operation not supported");      
      }

      @Override
      public int intAt(int index)
      {
        throw new IllegalArgumentException("Operation not supported");      
      }

      @Override
      public int length()
      {
        throw new IllegalArgumentException("Operation not supported");      
      }

      @Override
      public double numberAt(int index)
      {
        throw new IllegalArgumentException("Operation not supported");      
      }

      @Override
      public int getLength()
      {
        throw new IllegalArgumentException("Operation not supported");      
      }

      @Override
      public void setElements(Object array)
      {
        throw new IllegalArgumentException("Operation not supported");      
      }

      @Override
      public void setElements(Object array, int offset)
      {
        throw new IllegalArgumentException("Operation not supported");      
      }

      @Override
      public Uint8Array subarray(int start)
      {
        throw new IllegalArgumentException("Operation not supported");      
      }

      @Override
      public Uint8Array subarray(int start, int end)
      {
        throw new IllegalArgumentException("Operation not supported");      
      }
    };
  }
  
  public static void consoleLog(Object s)
  {
    System.out.println(s);
  }
}
