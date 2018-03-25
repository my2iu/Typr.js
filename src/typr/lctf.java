package typr;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.html.Uint8Array;
import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import elemental.util.Collections;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

//OpenType Layout Common Table Formats

@JsType(name="_lctf", namespace="Typr")
public class lctf
{
  @JsFunction public static interface Subt<T>
  {
    T subt (Uint8Array data, char ltype, int offset);
  }
  
  public static class LayoutCommonTable<T>
  {
    @JsProperty public JavaScriptObject scriptList;
    @JsProperty public JavaScriptObject featureList;
    @JsProperty public ArrayOf<LookupTable<T>> lookupList;
  }
  
  @JsIgnore public static <U> LayoutCommonTable<U> parse (Uint8Array data, int offset, int length, TyprFont font, Subt<U> subt)
  {
//	var bin = Typr._bin;
	LayoutCommonTable<U> obj = new LayoutCommonTable<>();
	int offset0 = offset;
	int tableVersion = bin.readVersion(data, offset);  offset += 4;
	
	char offScriptList  = bin.readUshort(data, offset);  offset += 2;
	char offFeatureList = bin.readUshort(data, offset);  offset += 2;
	char offLookupList  = bin.readUshort(data, offset);  offset += 2;
	
	obj.scriptList  = readScriptList (data, offset0 + offScriptList);
	obj.featureList = readFeatureList(data, offset0 + offFeatureList);
	obj.lookupList  = readLookupList (data, offset0 + offLookupList, subt);
	
	return obj;
}

  @JsMethod public static <U> ArrayOf<LookupTable<U>> readLookupList (Uint8Array data, int offset, Subt<U> subt)
  {
//	var bin = Typr._bin;
	int offset0 = offset;
	ArrayOf<LookupTable<U>> obj = Collections.arrayOf();
	char count = bin.readUshort(data, offset);  offset+=2;
	for(int i=0; i<count; i++) 
	{
		char noff = bin.readUshort(data, offset);  offset+=2;
		LookupTable<U> lut = readLookupTable(data, offset0 + noff, subt);
		obj.push(lut);
	}
	return obj;
  }

  public static class LookupTable<T>
  {
    @JsProperty public ArrayOf<T> tabs = Collections.arrayOf();
    @JsProperty public char ltype;
    @JsProperty public char flag;
  }
  
  @JsMethod public static <U> LookupTable<U> readLookupTable (Uint8Array data, int offset, Subt<U> subt)
  {
	//console.log("Parsing lookup table", offset);
//	var bin = Typr._bin;
	int offset0 = offset;
	LookupTable<U> obj = new LookupTable<>();
	
	obj.ltype = bin.readUshort(data, offset);  offset+=2;
	obj.flag  = bin.readUshort(data, offset);  offset+=2;
	int cnt   = bin.readUshort(data, offset);  offset+=2;
	
	for(int i=0; i<cnt; i++)
	{
		char noff = bin.readUshort(data, offset);  offset+=2;
		U tab = subt.subt(data, obj.ltype, offset0 + noff);
		//console.log(obj.type, tab);
		obj.tabs.push(tab);
	}
	return obj;
  }

  @JsMethod public static int numOfOnes (int n)
  {
	int num = 0;
	for(int i=0; i<32; i++) if(((n>>>i)&1) != 0) num++;
	return num;
  }

  @JsMethod public static ArrayOfInt readClassDef(Uint8Array data, int offset)
  {
//	var bin = Typr._bin;
	ArrayOfInt obj = Collections.arrayOfInt();
	char format = bin.readUshort(data, offset);  offset+=2;
	if(format==1) 
	{
		char startGlyph  = bin.readUshort(data, offset);  offset+=2;
		char glyphCount  = bin.readUshort(data, offset);  offset+=2;
		for(int i=0; i<glyphCount; i++)
		{
			obj.push(startGlyph+i);
			obj.push(startGlyph+i);
			obj.push(bin.readUshort(data, offset));  offset+=2;
		}
	}
	if(format==2)
	{
		char count = bin.readUshort(data, offset);  offset+=2;
		for(int i=0; i<count; i++)
		{
			obj.push(bin.readUshort(data, offset));  offset+=2;
			obj.push(bin.readUshort(data, offset));  offset+=2;
			obj.push(bin.readUshort(data, offset));  offset+=2;
		}
	}
	return obj;
  }
  @JsMethod public static native JavaScriptObject getInterval (JavaScriptObject tab, JavaScriptObject val)
  /*-{
	for(var i=0; i<tab.length; i+=3)
	{
		var start = tab[i], end = tab[i+1], index = tab[i+2];
		if(start<=val && val<=end) return i;
	}
	return -1;
}-*/;

  @JsMethod public static ArrayOfInt readValueRecord (Uint8Array data, int offset, char valFmt)
  {
//	var bin = Typr._bin;
     int iValFmt = (int)valFmt;
	ArrayOfInt arr = Collections.arrayOfInt();
	arr.push( ((iValFmt&1) != 0) ? bin.readShort(data, offset) : 0 );  offset += ((iValFmt&1) != 0) ? 2 : 0;
	arr.push( ((iValFmt&2) != 0) ? bin.readShort(data, offset) : 0 );  offset += ((iValFmt&2) != 0) ? 2 : 0;
	arr.push( ((iValFmt&4) != 0) ? bin.readShort(data, offset) : 0 );  offset += ((iValFmt&4) != 0) ? 2 : 0;
	arr.push( ((iValFmt&8) != 0) ? bin.readShort(data, offset) : 0 );  offset += ((iValFmt&8) != 0) ? 2 : 0;
	return arr;
  };

  @JsMethod public static native JavaScriptObject readCoverage (Uint8Array data, int offset)
  /*-{
	var bin = Typr._bin;
	var cvg = {};
	cvg.fmt   = bin.readUshort(data, offset);  offset+=2;
	var count = bin.readUshort(data, offset);  offset+=2;
	//console.log("parsing coverage", offset-4, format, count);
	if(cvg.fmt==1) cvg.tab = bin.readUshorts(data, offset, count); 
	if(cvg.fmt==2) cvg.tab = bin.readUshorts(data, offset, count*3);
	return cvg;
}-*/;

  @JsMethod public static native JavaScriptObject coverageIndex (JavaScriptObject cvg, JavaScriptObject val)
  /*-{
	var tab = cvg.tab;
	if(cvg.fmt==1) return tab.indexOf(val);
	if(cvg.fmt==2) {
		var ind = Typr._lctf.getInterval(tab, val);
		if(ind!=-1) return tab[ind+2] + (val - tab[ind]);
	}
	return -1;
}-*/;

  @JsMethod public static native JavaScriptObject readFeatureList (Uint8Array data, int offset)
  /*-{
	var bin = Typr._bin;
	var offset0 = offset;
	var obj = [];
	
	var count = bin.readUshort(data, offset);  offset+=2;
	
	for(var i=0; i<count; i++)
	{
		var tag = bin.readASCII(data, offset, 4);  offset+=4;
		var noff = bin.readUshort(data, offset);  offset+=2;
		obj.push({tag: tag.trim(), tab:Typr._lctf.readFeatureTable(data, offset0 + noff)});
	}
	return obj;
}-*/;

  @JsMethod public static native JavaScriptObject readFeatureTable (JavaScriptObject data, JavaScriptObject offset)
  /*-{
	var bin = Typr._bin;
	
	var featureParams = bin.readUshort(data, offset);  offset+=2;	// = 0
	var lookupCount = bin.readUshort(data, offset);  offset+=2;
	
	var indices = [];
	for(var i=0; i<lookupCount; i++) indices.push(bin.readUshort(data, offset+2*i));
	return indices;
}-*/;


  @JsMethod public static native JavaScriptObject readScriptList (Uint8Array data, int offset)
  /*-{
	var bin = Typr._bin;
	var offset0 = offset;
	var obj = {};
	
	var count = bin.readUshort(data, offset);  offset+=2;
	
	for(var i=0; i<count; i++)
	{
		var tag = bin.readASCII(data, offset, 4);  offset+=4;
		var noff = bin.readUshort(data, offset);  offset+=2;
		obj[tag.trim()] = Typr._lctf.readScriptTable(data, offset0 + noff);
	}
	return obj;
}-*/;

  @JsMethod public static native JavaScriptObject readScriptTable (JavaScriptObject data, JavaScriptObject offset)
  /*-{
	var bin = Typr._bin;
	var offset0 = offset;
	var obj = {};
	
	var defLangSysOff = bin.readUshort(data, offset);  offset+=2;
	obj['default'] = Typr._lctf.readLangSysTable(data, offset0 + defLangSysOff);
	
	var langSysCount = bin.readUshort(data, offset);  offset+=2;
	
	for(var i=0; i<langSysCount; i++)
	{
		var tag = bin.readASCII(data, offset, 4);  offset+=4;
		var langSysOff = bin.readUshort(data, offset);  offset+=2;
		obj[tag.trim()] = Typr._lctf.readLangSysTable(data, offset0 + langSysOff);
	}
	return obj;
}-*/;

  @JsMethod public static native JavaScriptObject readLangSysTable (JavaScriptObject data, JavaScriptObject offset)
  /*-{
	var bin = Typr._bin;
	var obj = {};
	
	var lookupOrder = bin.readUshort(data, offset);  offset+=2;
	//if(lookupOrder!=0)  throw "lookupOrder not 0";
	obj.reqFeature = bin.readUshort(data, offset);  offset+=2;
	//if(obj.reqFeature != 0xffff) throw "reqFeatureIndex != 0xffff";
	
	//console.log(lookupOrder, obj.reqFeature);
	
	var featureCount = bin.readUshort(data, offset);  offset+=2;
	obj.features = bin.readUshorts(data, offset, featureCount);
	return obj;
  }-*/;
}