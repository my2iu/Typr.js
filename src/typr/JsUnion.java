package typr;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.client.Browser;
import elemental.html.ArrayBuffer;
import elemental.html.Int16Array;
import elemental.html.Int32Array;
import elemental.html.Int8Array;
import elemental.html.Uint32Array;
import elemental.html.Uint8Array;
import elemental.util.SettableInt;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import typr.bin.Union;

class JsUnion extends Union
  {
    ArrayBuffer buff;
    Uint8Array uint8; 
    Int8Array int8; 
    Int16Array int16; 
//    @JsProperty Uint16Array uint16; 
    Int32Array int32; 
    Uint32Array uint32; 
    JavaScriptObject _tdec;

    JsUnion()
    {
      buff = Browser.getWindow().newUint8Array(8).getBuffer();
      int8 = Browser.getWindow().newInt8Array(buff, 0, 8); 
      uint8 = Browser.getWindow().newUint8Array(buff, 0, 8); 
      int16 = Browser.getWindow().newInt16Array(buff, 0, 4); 
//      uint16 = Browser.getWindow().newUint16Array(buff, 0, 4); 
      int32 = Browser.getWindow().newInt32Array(buff, 0, 2); 
      uint32 = Browser.getWindow().newUint32Array(buff, 0, 2);
      _tdec = init(); 
    }
    static native JavaScriptObject init() /*-{
      if (window["TextDecoder"]) return new window["TextDecoder"]();
      return null;
    }-*/;
    static native String textDecode(JavaScriptObject tdec, Uint8Array buff) /*-{
      return tdec.decode(buff);
    }-*/;
    

    @Override void writeUint8(int offset, byte b)
    {
      SettableInt a = (SettableInt)uint8;
      a.setAt(offset, b);
    }
    @Override int readInt32()
    {
      return int32.intAt(0);      
    }
    @Override int readUint32()
    {
      return uint32.intAt(0);      
    }
    @Override int readInt8()
    {
      return int8.intAt(0);
    }
    @Override int readInt16()
    {
      return int16.intAt(0);
    }
    @Override String readUTF8(Uint8Array buff, int p, int l) 
    {
      if (_tdec == null)
      {
        return bin.readASCII(buff, p, l);
      }
      Uint8Array copy = Browser.getWindow().newUint8Array(l);
      SettableInt copySetter = (SettableInt)copy;
      for (int n = 0; n < l; n++)
        copySetter.setAt(n, copy.intAt(p + n));
      return textDecode(_tdec, copy);
    }
  }