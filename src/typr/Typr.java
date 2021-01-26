package typr;

import java.util.List;
import java.util.Map;

import elemental.client.Browser;
import elemental.html.ArrayBuffer;
import elemental.html.Uint8Array;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import typr.TyprJava.TableRecord;
import typr.tabs.CFF;
import typr.tabs.GPOSParser;
import typr.tabs.GSUBParser;
import typr.tabs.OS2;
import typr.tabs.SVG;
import typr.tabs.cmap;
import typr.tabs.glyf;
import typr.tabs.head;
import typr.tabs.hhea;
import typr.tabs.hmtx;
import typr.tabs.kern;
import typr.tabs.loca;
import typr.tabs.maxp;
import typr.tabs.name;
import typr.tabs.post;

@JsType(namespace=JsPackage.GLOBAL)
public class Typr
{
  @JsMethod public static TyprFont parse(ArrayBuffer buff)
  {
    return parseIndex(buff, 0);
  }
  
  @JsMethod public static TyprFont parseIndex (ArrayBuffer buff, int fontIndex)
  {
    Uint8Array data = Browser.getWindow().newUint8Array(buff, 0, buff.getByteLength());
    return parseIndex(data, fontIndex);
  }

  @JsIgnore public static TyprFont parseIndex (ArrayBuffer buff, int fontIndex, boolean copyTables)
  {
    Uint8Array data = Browser.getWindow().newUint8Array(buff, 0, buff.getByteLength());
    return parseIndex(data, fontIndex, true, copyTables);
  }

  @JsIgnore public static TyprFont parseIndex (Uint8Array data, Integer fontIndex)
  {
    return parseIndex(data, fontIndex, false, false);
  }
  
  @JsIgnore public static TyprFont parseIndex (Uint8Array data, int fontIndex, boolean withRawFontTables, boolean copyTables)
  {
//	var bin = Typr._bin;
    try {
      
      
      boolean isTtcf = TyprJava.checkIsTtcf(data);
//      obj._data = data;
  	
      int fontOffset = 0;
      if (isTtcf)
      {
        List<Integer> fontOffsetList = TyprJava.readTtcfFontOffsets(data);
        fontOffset = fontOffsetList.get(fontIndex);
      }
      
      Map<String, TableRecord> tableRecords = TyprJava.readTableRecords(data, fontOffset);
  
  	   String []tags = new String[] {
  	        "cmap",
  	        "head",
  	        "hhea",
  	        "maxp",
  	        "hmtx",
  	        "name",
  	        "OS/2",
  	        "post",
  	        
  	        //"cvt",
  	        //"fpgm",
  	        "loca",
  	        "glyf",
  	        "kern",
  	        
  	        //"prep"
  	        //"gasp"
  	        
  	        "CFF ",
  	        
  	        
  	        "GPOS",
  	        "GSUB",
  	        
  	        "SVG "
  	        //"VORG",
  	    };

	   TyprFont obj = new TyprFont();
	   if (withRawFontTables)
	   {
	     obj.hasRawFontTables = true;
	     for (TableRecord table: tableRecords.values())
	     {
	       if (copyTables)
	         obj.rawFontTables.put(table.tag, TyprMisc.slicedUint8Array(data, table.offset, table.length));
	       else
             obj.rawFontTables.put(table.tag, TyprMisc.viewOfUint8Array(data, table.offset, table.length));
	     }
	   }
	   else if (tableRecords.containsKey("glyf"))
	   {
	     TableRecord table = tableRecords.get("glyf");
	     if (copyTables)
	       obj.rawFontTables.put(table.tag, TyprMisc.slicedUint8Array(data, table.offset, table.length));
	     else
           obj.rawFontTables.put(table.tag, TyprMisc.viewOfUint8Array(data, table.offset, table.length));
	   }

  	for(String t: tags)
  	{
  		//console.log(t);
  		//if(tabs[t]) console.log(t, tabs[t].offset, tabs[t].length);
  		if (tableRecords.containsKey(t)) 
  		  parseTab(obj,data,tableRecords.get(t).offset, tableRecords.get(t).length,t);
  //		if(tabs[t]) obj[t.trim()] = Typr[t.trim()].parse(data, tabs[t].offset, tabs[t].length, obj);
  	}
  	
  	return obj;
    }
    catch (Throwable t)
    {
      TyprMisc.consoleLog(t);
      return null;
    }
  }

  @JsIgnore public static TyprFont parseFromSeparateTables (Map<Integer, Uint8Array> data, boolean withRawFontTables)
  {
    try {
      TyprFont obj = new TyprFont();

      if (withRawFontTables)
      {
        obj.hasRawFontTables = true;
        obj.rawFontTables.putAll(data);
      }
      else if (data.containsKey(TableRecord.stringTagToInt("glyf")))
      {
        obj.rawFontTables.put(TableRecord.stringTagToInt("glyf"), data.get(TableRecord.stringTagToInt("glyf")));
      }
      
       String []tags = new String[] {
            "cmap",
            "head",
            "hhea",
            "maxp",
            "hmtx",
            "name",
            "OS/2",
            "post",
            
            //"cvt",
            //"fpgm",
            "loca",
            "glyf",
            "kern",
            
            //"prep"
            //"gasp"
            
            "CFF ",
            
            
            "GPOS",
            "GSUB",
            
            "SVG "
            //"VORG",
        };
  
    for(String t: tags)
    {
       int tagInt = TableRecord.stringTagToInt(t);
       if (data.containsKey(tagInt))
       {
          Uint8Array tableData = data.get(tagInt);
          parseTab(obj, tableData, 0, tableData.getByteLength(), t);
       }
    }
    
    return obj;
    }
    catch (Throwable t)
    {
      TyprMisc.consoleLog(t);
      return null;
    }
  }

  static void parseTab(TyprFont obj, Uint8Array data, int offset, int length, String tag)
  {
    switch(tag)
    {
    case "cmap":
      obj.cmap = cmap.parse(data, offset, length);
      break;
    case "head":
      obj.head = head.parse(data, offset, length);
      break;
    case "hhea":
      obj.hhea = hhea.parse(data, offset, length);
      break;
    case "maxp":
      obj.maxp = maxp.parse(data, offset, length);
      break;
    case "hmtx":
      obj.hmtx = hmtx.parse(data, offset, length, obj);
      break;
    case "name":
      obj.name = name.parse(data, offset, length);
      break;
    case "OS/2":
      obj.OS2 = OS2.parse(data, offset, length);
      break;
    case "post":
      obj.post = post.parse(data, offset, length);
      break;
    
    //"cvt",
    //"fpgm",
    case "loca":
      obj.loca = loca.parse(data, offset, length, obj);
      break;
    case "glyf":
      obj.glyf = glyf.parse(obj.rawFontTables.get((103<<24)|(108<<16)|(121<<8)|102), 0, length, obj);
      break;
    case "kern":
      obj.kern = kern.parse(data, offset, length, obj);
      break;
    
    //"prep"
    //"gasp"
    
    case "CFF ":
      obj.CFF = CFF.parse(data, offset, length);
      break;
    
    
    case "GPOS":
      obj.GPOS = GPOSParser.parse(data, offset, length, obj);
      break;
    case "GSUB":
      obj.GSUB = GSUBParser.parse(data, offset, length, obj);
      break;
    
    case "SVG ":
      obj.SVG = SVG.parse(data, offset, length);
      break;
    //"VORG",
    
    }
    
  }

 
//  @JsMethod public static int _tabOffset (Uint8Array data, String tab)
//  {
////	var bin = Typr._bin;
//	int numTables = bin.readUshort(data, 4);
//	int offset = 12;
//	for(int i=0; i<numTables; i++)
//	{
//		String tag = bin.readASCII(data, offset, 4);   offset += 4;
//		int checkSum = bin.readUint(data, offset);  offset += 4;
//		int toffset = bin.readUint(data, offset);   offset += 4;
//		int length = bin.readUint(data, offset);    offset += 4;
//		if(tag.equals(tab)) return toffset;
//	}
//	return 0;
//  }





}