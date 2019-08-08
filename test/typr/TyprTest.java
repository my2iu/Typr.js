package typr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import elemental.util.ArrayOfInt;
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
  
  @Test
  public void testParseWholeFile() throws IOException
  {
    TyprMisc.init();
    FileUint8Array file = new FileUint8Array(new File("demo/LiberationSans-Bold.ttf"));    
    TyprFont font = Typr.parseIndex(file, 0);
    assertNotNull(font);
  }
  
  @Test
  public void testFontWithCff() throws IOException
  {
    TyprMisc.init();
    FileUint8Array file = new FileUint8Array(new File("demo/Cabin-Bold.otf"));    
    TyprFont font = Typr.parseIndex(file, 0);
    TyprMisc.init();
    ArrayOfInt glyphs = TyprU.stringToGlyphs(font, "hi");
    TyprPath path = TyprU.glyphsToPath(font, glyphs);
    Assert.assertEquals("M498.0 0.0L498.0 269.0C498.0 410.0 441.0 508.0 285.0 508.0C248.0 508.0 210.0 493.0 181.0 474.0L181.0 750.0L57.0 750.0L57.0 0.0L181.0 0.0L181.0 363.0C204.0 390.0 233.0 406.0 275.0 406.0C275.0 476.0 304.0 432.0 211.0 432.0L211.0 163.0ZM733.0 576.0L733.0 700.0L609.0 700.0L609.0 576.0ZM733.0 0.0L733.0 500.0L609.0 500.0L609.0 0.0Z", TyprU.pathToSVG(path, null));
    // From JS: M498 0L498 269C498 410 441 508 285 508C248 508 210 493 181 474L181 750L57 750L57 0L181 0L181 363C204 390 233 406 275 406C345 406 374 362 374 269L374 0ZM733 576L733 700L609 700L609 576ZM733 0L733 500L609 500L609 0Z
  }
}
