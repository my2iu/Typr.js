package typr;

import java.util.HashMap;
import java.util.Map;

import elemental.html.Uint8Array;
import elemental.util.MapFromIntToString;
import typr.tabs.name;

public class TyprJava
{
  /**
   * Read the offset table at the start of the file and then the
   * table records describing what font tables are in the file.
   */
  public static Map<String, TableRecord> readTableRecords(Uint8Array data)
  {
    int offset = 0;
    
    int sfnt_version = bin.readVersion(data, offset);
    offset += 4;
    int numTables = bin.readUshort(data, offset);
    offset += 2;
    int searchRange = bin.readUshort(data, offset);
    offset += 2;
    int entrySelector = bin.readUshort(data, offset);
    offset += 2;
    int rangeShift = bin.readUshort(data, offset);
    offset += 2;
    
    // OTTO or 1 or true (OTTO is for opentype with postscript outlines and typ1 is postscript font in truetype format) 
    if (sfnt_version != 0x00010000 && sfnt_version != 0x4f54544f
        && sfnt_version != 0x74727565)
      throw new IllegalArgumentException("Not a truetype or opentype font");
    
    
    //console.log(sfnt_version, numTables, searchRange, entrySelector, rangeShift);
    
//  var tabs = {};
    Map<String, TableRecord> tableRecords = new HashMap<>();
    
    for(int i=0; i<numTables; i++)
    {
      TableRecord tableRecord = new TableRecord();
      tableRecord.tag = bin.readUint(data, offset); offset += 4;
      tableRecord.checkSum = bin.readUint(data, offset); offset += 4;
      tableRecord.offset = bin.readUint(data, offset); offset += 4;
      tableRecord.length = bin.readUint(data, offset); offset += 4;
      tableRecords.put(tableRecord.tagAsString(), tableRecord);
    }
    return tableRecords;
  }

  public static class TableRecord
  {
    public int tag;
    public int checkSum;
    public int offset;
    public int length;
    public String tagAsString()
    {
      String s = "";
      int ch = (tag >> 24) & 255;
      s += String.valueOf((char)ch);
      ch = (tag & 0xff0000) >> 16;
      s += String.valueOf((char)ch);
      ch = (tag & 0xff00) >> 8;
      s += String.valueOf((char)ch);
      ch = (tag & 0xff);
      s += String.valueOf((char)ch);
      return s;
    }
  }
  
  public static MapFromIntToString parseHeaderAndNames(Uint8Array data)
  {
    try {
      Map<String, TableRecord> tables = readTableRecords(data);
      if (tables.containsKey("name"))
      {
        name nameTable = name.parse(data, tables.get("name").offset, tables.get("name").length);
        return nameTable.getEnglishNames();
      }
    }
    catch (Throwable t)
    {
      return null;
    }
    return null;
  }
}
