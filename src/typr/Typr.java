package typr;

import java.util.HashMap;
import java.util.Map;

import elemental.client.Browser;
import elemental.html.ArrayBuffer;
import elemental.html.Uint8Array;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import typr.tabs.CFF;
import typr.tabs.GPOS;
import typr.tabs.GPOSParser;
import typr.tabs.GSUB;
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
  @JsMethod public static TyprFont parse (ArrayBuffer buff)
  {
//	var bin = Typr._bin;
	Uint8Array data = Browser.getWindow().newUint8Array(buff, 0, buff.getByteLength());
	int offset = 0;
	
	double sfnt_version = bin.readFixed(data, offset);
	offset += 4;
	int numTables = bin.readUshort(data, offset);
	offset += 2;
	int searchRange = bin.readUshort(data, offset);
	offset += 2;
	int entrySelector = bin.readUshort(data, offset);
	offset += 2;
	int rangeShift = bin.readUshort(data, offset);
	offset += 2;
	
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
	obj._data = data;
	//console.log(sfnt_version, numTables, searchRange, entrySelector, rangeShift);
	
//	var tabs = {};
	Map<String, Integer> tabOffset = new HashMap<>();
	Map<String, Integer> tabLength = new HashMap<>();
	
	for(int i=0; i<numTables; i++)
	{
		String tag = bin.readASCII(data, offset, 4);   offset += 4;
		int checkSum = bin.readUint(data, offset);  offset += 4;
		int toffset = bin.readUint(data, offset);   offset += 4;
		int length = bin.readUint(data, offset);    offset += 4;
		tabOffset.put(tag, toffset);
		tabLength.put(tag, length);
//		Browser.getWindow().getConsole().log(tag + ":" + length);
//		tabs[tag] = {offset:toffset, length:length};
		
		//if(tags.indexOf(tag)==-1) console.log("unknown tag", tag, length);
	}
	
	for(String t: tags)
	{
		//console.log(t);
		//if(tabs[t]) console.log(t, tabs[t].offset, tabs[t].length);
		if (tabOffset.containsKey(t)) 
		  parseTab(obj,data,tabOffset.get(t), tabLength.get(t),t);
//		if(tabs[t]) obj[t.trim()] = Typr[t.trim()].parse(data, tabs[t].offset, tabs[t].length, obj);
	}
	
	return obj;
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
      obj.glyf = glyf.parse(data, offset, length, obj);
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

  /**
   * Call this once in order to make sure Typr classes are accessible
   * both from the main window and also the GWT iframe
   */
  @JsIgnore public static void init()
  {
    remapTypr();
    bin.init();
  }
  @JsIgnore private static native void remapTypr() /*-{
    window.Typr = $wnd.Typr;
  }-*/;

  
  @JsMethod public static int _tabOffset (Uint8Array data, String tab)
  {
//	var bin = Typr._bin;
	int numTables = bin.readUshort(data, 4);
	int offset = 12;
	for(int i=0; i<numTables; i++)
	{
		String tag = bin.readASCII(data, offset, 4);   offset += 4;
		int checkSum = bin.readUint(data, offset);  offset += 4;
		int toffset = bin.readUint(data, offset);   offset += 4;
		int length = bin.readUint(data, offset);    offset += 4;
		if(tag.equals(tab)) return toffset;
	}
	return 0;
  }




}