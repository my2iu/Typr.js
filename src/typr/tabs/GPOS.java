package typr.tabs;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.html.Uint8Array;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType(namespace="Typr")
public class GPOS
{
  @JsIgnore public static native JavaScriptObject parse (Uint8Array data, int offset, int length, JavaScriptObject font) /*-{  return Typr._lctf.parse(data, offset, length, font, Typr.GPOS.subt);  }-*/;



  @JsMethod public static native JavaScriptObject subt (JavaScriptObject data, JavaScriptObject ltype, int offset)	// lookup type
  /*-{
	if(ltype!=2) return null;
	
	var bin = Typr._bin, offset0 = offset, tab = {};
	
	tab.format  = bin.readUshort(data, offset);  offset+=2;
	var covOff  = bin.readUshort(data, offset);  offset+=2;
	tab.coverage = Typr._lctf.readCoverage(data, covOff+offset0);
	tab.valFmt1 = bin.readUshort(data, offset);  offset+=2;
	tab.valFmt2 = bin.readUshort(data, offset);  offset+=2;
	var ones1 = Typr._lctf.numOfOnes(tab.valFmt1);
	var ones2 = Typr._lctf.numOfOnes(tab.valFmt2);
	if(tab.format==1)
	{
		tab.pairsets = [];
		var count = bin.readUshort(data, offset);  offset+=2;
		
		for(var i=0; i<count; i++)
		{
			var psoff = bin.readUshort(data, offset);  offset+=2;
			psoff += offset0;
			var pvcount = bin.readUshort(data, psoff);  psoff+=2;
			var arr = [];
			for(var j=0; j<pvcount; j++)
			{
				var gid2 = bin.readUshort(data, psoff);  psoff+=2;
				var value1, value2;
				if(tab.valFmt1!=0) {  value1 = Typr._lctf.readValueRecord(data, psoff, tab.valFmt1);  psoff+=ones1*2;  }
				if(tab.valFmt2!=0) {  value2 = Typr._lctf.readValueRecord(data, psoff, tab.valFmt2);  psoff+=ones2*2;  }
				arr.push({gid2:gid2, val1:value1, val2:value2});
			}
			tab.pairsets.push(arr);
		}
	}
	if(tab.format==2)
	{
		var classDef1 = bin.readUshort(data, offset);  offset+=2;
		var classDef2 = bin.readUshort(data, offset);  offset+=2;
		var class1Count = bin.readUshort(data, offset);  offset+=2;
		var class2Count = bin.readUshort(data, offset);  offset+=2;
		
		tab.classDef1 = Typr._lctf.readClassDef(data, offset0 + classDef1);
		tab.classDef2 = Typr._lctf.readClassDef(data, offset0 + classDef2);
		
		tab.matrix = [];
		for(var i=0; i<class1Count; i++)
		{
			var row = [];
			for(var j=0; j<class2Count; j++)
			{
				var value1 = null, value2 = null;
				if(tab.valFmt1!=0) { value1 = Typr._lctf.readValueRecord(data, offset, tab.valFmt1);  offset+=ones1*2; }
				if(tab.valFmt2!=0) { value2 = Typr._lctf.readValueRecord(data, offset, tab.valFmt2);  offset+=ones2*2; }
				row.push({val1:value1, val2:value2});
			}
			tab.matrix.push(row);
		}
	}
	return tab;
}-*/;
}