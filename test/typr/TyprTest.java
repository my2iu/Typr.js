package typr;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import elemental.util.MapFromIntToString;
import typr.tabs.name;

public class TyprTest
{
  @Test
  public void testHeaderReading() throws IOException
  {
    bin.t = new JreUnion();
    FileUint8Array file = new FileUint8Array(new File("demo/Cabin-Bold.otf"));    
    MapFromIntToString names = TyprJava.parseHeaderAndNames(file);
    assertEquals("Cabin", names.get(name.NameType.FONT_FAMILY.ordinal()));
    assertEquals("Bold", names.get(name.NameType.FONT_SUBFAMILY.ordinal()));
  }
}
