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
//	var bin = Typr._bin;
    try {
      
      Uint8Array data = Browser.getWindow().newUint8Array(buff, 0, buff.getByteLength());
      boolean isTtcf = TyprJava.checkIsTtcf(data);
      TyprFont obj = new TyprFont();
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
       Browser.getWindow().getConsole().log(t);
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
      obj._rawGlyfTableData = Browser.getWindow().newUint8Array(data.getBuffer().slice(offset, offset + length), 0, length);
      obj.glyf = glyf.parse(obj._rawGlyfTableData, 0, length, obj);
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