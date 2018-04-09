package typr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import elemental.util.MapFromIntToString;
import typr.TyprJava.TableRecord;
import typr.tabs.cmap;
import typr.tabs.name;

public class TyprTest
{
  @Test
  public void testHeaderReading() throws IOException
  {
    TyprMisc.init();
    FileUint8Array file = new FileUint8Array(new File("demo/Cabin-Bold.otf"));    
    MapFromIntToString names = TyprJava.parseHeaderAndNames(file);
    assertEquals("Cabin", names.get(name.NameType.FONT_FAMILY.ordinal()));
    assertEquals("Bold", names.get(name.NameType.FONT_SUBFAMILY.ordinal()));
  }
  
  @Test
  public void testCmapReading() throws IOException
  {
    TyprMisc.init();
    FileUint8Array file = new FileUint8Array(new File("demo/Cabin-Bold.otf"));
    Map<String, TableRecord> tables = TyprJava.readTableRecords(file, 0);
    cmap c = cmap.parse(file, tables.get("cmap").offset, tables.get("cmap").length);
    assertEquals(3, c.platformEncodingMap.keys().length());
    assertTrue(c.platformEncodingMap.hasKey("p3e1"));
    assertTrue(c.platformEncodingMap.hasKey("p0e3"));
    assertTrue(c.platformEncodingMap.hasKey("p1e0"));
    cmap.Table table = c.tables.get(c.platformEncodingMap.get("p0e3"));
    assertEquals(32, table.startCount.get(0));
    assertEquals(234, table.glyphIdArray.get(0));
  }
}
