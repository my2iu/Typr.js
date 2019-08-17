package typr;

import elemental.html.Uint8Array;
import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import elemental.util.Collections;
import elemental.util.MapFromStringTo;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

//OpenType Layout Common Table Formats

public class lctf
{
  @JsFunction public static interface Subt<T>
  {
    T subt (Uint8Array data, char ltype, int offset);
  }
  
  public static class LayoutCommonTable<T>
  {
    @JsProperty public MapFromStringTo<MapFromStringTo<LangSysTable>> scriptList;
    @JsProperty public ArrayOf<FeatureList> featureList;
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

  @JsIgnore public static <U> ArrayOf<LookupTable<U>> readLookupList (Uint8Array data, int offset, Subt<U> subt)
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
  
  @JsIgnore public static <U> LookupTable<U> readLookupTable (Uint8Array data, int offset, Subt<U> subt)
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

  @JsIgnore public static int numOfOnes (int n)
  {
	int num = 0;
	for(int i=0; i<32; i++) if(((n>>>i)&1) != 0) num++;
	return num;
  }

  @JsIgnore public static ArrayOfInt readClassDef(Uint8Array data, int offset)
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
  @JsIgnore public static int getInterval (ArrayOfInt tab, int val)
  {
	for(int i=0; i<tab.length(); i+=3)
	{
		int start = tab.get(i), end = tab.get(i+1), index = tab.get(i+2);
		if(start<=val && val<=end) return i;
	}
	return -1;
  }

  @JsIgnore public static ArrayOfInt readValueRecord (Uint8Array data, int offset, char valFmt)
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

  public static class Coverage
  {
    @JsProperty public char fmt;
    @JsProperty public ArrayOfInt tab;
  }
  
  @JsIgnore public static Coverage readCoverage (Uint8Array data, int offset)
  {
//	var bin = Typr._bin;
	Coverage cvg = new Coverage();
	cvg.fmt   = bin.readUshort(data, offset);  offset+=2;
	int count = bin.readUshort(data, offset);  offset+=2;
	//console.log("parsing coverage", offset-4, format, count);
	if(cvg.fmt==1) cvg.tab = bin.readUshorts(data, offset, count); 
	if(cvg.fmt==2) cvg.tab = bin.readUshorts(data, offset, count*3);
	return cvg;
  }

  @JsIgnore public static int coverageIndex (Coverage cvg, int val)
  {
	ArrayOfInt tab = cvg.tab;
	if(cvg.fmt==1) return tab.indexOf(val);
	if(cvg.fmt==2) {
		int ind = getInterval(tab, val);
		if(ind!=-1) return tab.get(ind+2) + (val - tab.get(ind));
	}
	return -1;
  }

  public static class FeatureTable
  {
    
  }
  
  public static class FeatureList
  {
    @JsIgnore FeatureList(String tag, ArrayOfInt table)
    {
      
    }
    @JsProperty public ArrayOfInt tab;
    @JsProperty public String tag;
  }
  
  @JsIgnore public static ArrayOf<FeatureList> readFeatureList (Uint8Array data, int offset)
  {
//	var bin = Typr._bin;
	int offset0 = offset;
	ArrayOf<FeatureList> obj = Collections.arrayOf();
	
	char count = bin.readUshort(data, offset);  offset+=2;
	
	for(int i=0; i<count; i++)
	{
		String tag = bin.readASCII(data, offset, 4);  offset+=4;
		char noff = bin.readUshort(data, offset);  offset+=2;
		obj.push(new FeatureList(tag.trim(), readFeatureTable(data, offset0 + noff)));
	}
	return obj;
  }

  @JsIgnore public static ArrayOfInt readFeatureTable (Uint8Array data, int offset)
  {
//	var bin = Typr._bin;
	
	char featureParams = bin.readUshort(data, offset);  offset+=2;	// = 0
	char lookupCount = bin.readUshort(data, offset);  offset+=2;
	
	ArrayOfInt indices = Collections.arrayOfInt();
	for(int i=0; i<lookupCount; i++) indices.push(bin.readUshort(data, offset+2*i));
	return indices;
  }


  @JsIgnore public static MapFromStringTo<MapFromStringTo<LangSysTable>> readScriptList (Uint8Array data, int offset)
  {
//	var bin = Typr._bin;
	int offset0 = offset;
	MapFromStringTo<MapFromStringTo<LangSysTable>> obj = Collections.mapFromStringTo();
	
	char count = bin.readUshort(data, offset);  offset+=2;
	
	for(int i=0; i<count; i++)
	{
		String tag = bin.readASCII(data, offset, 4);  offset+=4;
		char noff = bin.readUshort(data, offset);  offset+=2;
		obj.put(tag.trim(), readScriptTable(data, offset0 + noff));
	}
	return obj;
  }

  @JsIgnore public static MapFromStringTo<LangSysTable> readScriptTable (Uint8Array data, int offset)
  {
//	var bin = Typr._bin;
	int offset0 = offset;
	MapFromStringTo<LangSysTable> obj = Collections.mapFromStringTo();
	
	char defLangSysOff = bin.readUshort(data, offset);  offset+=2;
	obj.put("default", readLangSysTable(data, offset0 + defLangSysOff));
	
	char langSysCount = bin.readUshort(data, offset);  offset+=2;
	
	for(int i=0; i<langSysCount; i++)
	{
		String tag = bin.readASCII(data, offset, 4);  offset+=4;
		char langSysOff = bin.readUshort(data, offset);  offset+=2;
		obj.put(tag.trim(), readLangSysTable(data, offset0 + langSysOff));
	}
	return obj;
  }
  
  static class LangSysTable
  {
    @JsProperty public char reqFeature;
    @JsProperty public ArrayOfInt features;
  }

  @JsIgnore public static LangSysTable readLangSysTable (Uint8Array data, int offset)
  {
//	var bin = Typr._bin;
    LangSysTable obj = new LangSysTable();
	
	char lookupOrder = bin.readUshort(data, offset);  offset+=2;
	//if(lookupOrder!=0)  throw "lookupOrder not 0";
	obj.reqFeature = bin.readUshort(data, offset);  offset+=2;
	//if(obj.reqFeature != 0xffff) throw "reqFeatureIndex != 0xffff";
	
	//console.log(lookupOrder, obj.reqFeature);
	
	char featureCount = bin.readUshort(data, offset);  offset+=2;
	obj.features = bin.readUshorts(data, offset, featureCount);
	return obj;
  }
}