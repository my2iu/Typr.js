package typr;

import java.util.function.Consumer;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.ArrayBuffer;
import elemental.html.CanvasElement;
import elemental.html.CanvasRenderingContext2D;
import elemental.util.ArrayOfInt;
import elemental.util.ArrayOfNumber;
import elemental.util.ArrayOfString;
import elemental.xml.XMLHttpRequest;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

public class GwtTestEntryPoint implements com.google.gwt.core.client.EntryPoint
{
  @JsType(isNative=true,namespace=JsPackage.GLOBAL)
  public static class TyprFont
  {
    @JsType(isNative=true,namespace=JsPackage.GLOBAL)
    public static class TyprFontHead
    {
      @JsProperty public int unitsPerEm;
      @JsProperty public int xMin;
      @JsProperty public int xMax;
      @JsProperty public int yMin;
      @JsProperty public int yMax;
    }
    @JsType(isNative=true,namespace=JsPackage.GLOBAL)
    public static class TyprFontHorizontalHeader
    {
      @JsProperty public int advanceWidthMax;
      @JsProperty public int ascender;
      @JsProperty public int descender;
      @JsProperty public int lineGap;
    }
    @JsProperty public TyprFontHead head;
    @JsProperty public TyprFontHorizontalHeader hhea;
    
  }

  @JsType(isNative=true,namespace=JsPackage.GLOBAL)
  static class TyprPath
  {
    @JsProperty public ArrayOfString cmds;
    @JsProperty public ArrayOfNumber crds;
  }

  public static void loadXmlHttpArrayBuffer(String url, final Consumer<ArrayBuffer> dataCallback, final Runnable failCallback)
  {
    final XMLHttpRequest xmlhttp = Browser.getWindow().newXMLHttpRequest();
    xmlhttp.setResponseType("arraybuffer");
    xmlhttp.setOnreadystatechange(new EventListener() {
      @Override public void handleEvent(Event evt)
      {
        if (xmlhttp.getReadyState() == 4) {
          if (xmlhttp.getStatus() == 200 || (xmlhttp.getStatus() == 0 && xmlhttp.getResponse() != null)) 
          {
            if (dataCallback != null)
              dataCallback.accept((ArrayBuffer)xmlhttp.getResponse());
          } else {
            // error
            if (failCallback != null)
              failCallback.run();
          }
        }
      }});
    xmlhttp.open("GET", url, true);
    xmlhttp.send();
  }

  @JsMethod public static native void remapTypr()
  /*-{
    window.Typr = $wnd.Typr;
    Typr["OS/2"] = Typr.OS2;
}-*/;
  
  
  /**
   * This is the entry point method.
   */
  public void onModuleLoad()
  {
    remapTypr();
    bin.init();
    CanvasElement canvas = (CanvasElement)Browser.getDocument().querySelector("canvas");
    loadXmlHttpArrayBuffer("../demo/LiberationSans-Bold.ttf", (arrbuf) -> {
      // TODO: handle what happens if loading fails
      TyprFont font = Typr.parse(arrbuf);
      ArrayOfInt glyphs = TyprU.stringToGlyphs(font, "hi");
      TyprPath path = TyprU.glyphsToPath(font, glyphs);
      CanvasRenderingContext2D ctx = (CanvasRenderingContext2D)canvas.getContext("2d");
      ctx.save();
      ctx.translate(0, 200);
      ctx.scale(0.1f, -0.1f);
      TyprU.pathToContext(path, ctx);
      ctx.fill();
      ctx.restore();
    }, null);
  }

}
