package typr;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.html.ArrayBuffer;
import elemental.html.Uint8Array;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import typr.tabs.CFF;
import typr.tabs.GPOS;
import typr.tabs.GSUB;
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
  @JsMethod public static native TyprFont parse (ArrayBuffer buff)
  /*-{
	var bin = Typr._bin;
	var data = new Uint8Array(buff);
	var offset = 0;
	
	var sfnt_version = bin.readFixed(data, offset);
	offset += 4;
	var numTables = bin.readUshort(data, offset);
	offset += 2;
	var searchRange = bin.readUshort(data, offset);
	offset += 2;
	var entrySelector = bin.readUshort(data, offset);
	offset += 2;
	var rangeShift = bin.readUshort(data, offset);
	offset += 2;
	
	var tags = [
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
		];
	
	var obj = {_data:data};
	//console.log(sfnt_version, numTables, searchRange, entrySelector, rangeShift);
	
	var tabs = {};
	
	for(var i=0; i<numTables; i++)
	{
		var tag = bin.readASCII(data, offset, 4);   offset += 4;
		var checkSum = bin.readUint(data, offset);  offset += 4;
		var toffset = bin.readUint(data, offset);   offset += 4;
		var length = bin.readUint(data, offset);    offset += 4;
		tabs[tag] = {offset:toffset, length:length};
		
		//if(tags.indexOf(tag)==-1) console.log("unknown tag", tag, length);
	}
	
	for(var i=0; i< tags.length; i++)
	{
		var t = tags[i];
		//console.log(t);
		//if(tabs[t]) console.log(t, tabs[t].offset, tabs[t].length);
		if (tabs[t]) {
		  @typr.Typr::parseTab(Ltypr/TyprFont;Lelemental/html/Uint8Array;IILjava/lang/String;)(obj,data,tabs[t].offset, tabs[t].length,t);
		}
//		if(tabs[t]) obj[t.trim()] = Typr[t.trim()].parse(data, tabs[t].offset, tabs[t].length, obj);
	}
	
	return obj;
}-*/;

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
      obj.GPOS = GPOS.parse(data, offset, length, obj);
      break;
    case "GSUB":
      obj.GSUB = GSUB.parse(data, offset, length, obj);
      break;
    
    case "SVG ":
      obj.SVG = SVG.parse(data, offset, length);
      break;
    //"VORG",
    
    }
    
  }
  
  @JsMethod public static native JavaScriptObject _tabOffset (JavaScriptObject data, JavaScriptObject tab)
/*-{
	var bin = Typr._bin;
	var numTables = bin.readUshort(data, 4);
	var offset = 12;
	for(var i=0; i<numTables; i++)
	{
		var tag = bin.readASCII(data, offset, 4);   offset += 4;
		var checkSum = bin.readUint(data, offset);  offset += 4;
		var toffset = bin.readUint(data, offset);   offset += 4;
		var length = bin.readUint(data, offset);    offset += 4;
		if(tag==tab) return toffset;
	}
	return 0;
}-*/;




}