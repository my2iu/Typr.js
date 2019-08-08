package typr;

import java.text.DecimalFormat;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.core.shared.GwtIncompatible;
import com.google.gwt.i18n.client.NumberFormat;

import elemental.client.Browser;
import elemental.html.ArrayBuffer;
import elemental.html.Uint8Array;
import jsinterop.annotations.JsIgnore;

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

  public static Uint8Array viewOfUint8Array(Uint8Array data, int offset, int length)
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
        return length;      
      }

      @Override
      public int getByteOffset()
      {
        throw new IllegalArgumentException("Operation not supported");      
      }

      @Override
      public int intAt(int index)
      {
        return data.intAt(index+offset);      
      }

      @Override
      public int length()
      {
        return data.length();
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
  
  final static DecimalFormat[] ToFixedList = new DecimalFormat[] {
      new DecimalFormat("#"),
      new DecimalFormat("#.#"),
      new DecimalFormat("#.##"),
      new DecimalFormat("#.###"),
      new DecimalFormat("#.####"),
      new DecimalFormat("#.#####"),
      new DecimalFormat("#.######"),
      new DecimalFormat("#.#######"),
      new DecimalFormat("#.########")
  };
  public static String toFixed(double d, int prec) {
    return ToFixedList[prec].format(d);
  }
  
  public static void consoleLog(Object s)
  {
    System.out.println(s);
  }
}
