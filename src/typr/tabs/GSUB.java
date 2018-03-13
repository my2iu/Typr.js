package typr.tabs;

import com.google.gwt.core.client.JavaScriptObject;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType(namespace="Typr")
public class GSUB
{
  @JsMethod public static native JavaScriptObject parse (JavaScriptObject data, int offset, int length, JavaScriptObject font) /*-{  return Typr._lctf.parse(data, offset, length, font, Typr.GSUB.subt);  }-*/;


  @JsMethod public static native JavaScriptObject subt (JavaScriptObject data, JavaScriptObject ltype, int offset)	// lookup type
  /*-{
	var bin = Typr._bin, offset0 = offset, tab = {};
	
	if(ltype!=1 && ltype!=4 && ltype!=5) return null;
	
	tab.fmt  = bin.readUshort(data, offset);  offset+=2;
	var covOff  = bin.readUshort(data, offset);  offset+=2;
	tab.coverage = Typr._lctf.readCoverage(data, covOff+offset0);	// not always is coverage here
	
	if(false) {}
	//  Single Substitution Subtable
	else if(ltype==1) {	
		if(tab.fmt==1) {
			tab.delta = bin.readShort(data, offset);  offset+=2;
		}
		else if(tab.fmt==2) {
			var cnt = bin.readUshort(data, offset);  offset+=2;
			tab.newg = bin.readUshorts(data, offset, cnt);  offset+=tab.newg.length*2;
		}
	}
	//  Ligature Substitution Subtable
	else if(ltype==4) {
		tab.vals = [];
		var cnt = bin.readUshort(data, offset);  offset+=2;
		for(var i=0; i<cnt; i++) {
			var loff = bin.readUshort(data, offset);  offset+=2;
			tab.vals.push(Typr.GSUB.readLigatureSet(data, offset0+loff));
		}
		//console.log(tab.coverage);
		//console.log(tab.vals);
	} 
	//  Contextual Substitution Subtable
	else if(ltype==5) {
		if(tab.fmt==2) {
			var cDefOffset = bin.readUshort(data, offset);  offset+=2;
			tab.cDef = Typr._lctf.readClassDef(data, offset0 + cDefOffset);
			tab.scset = [];
			var subClassSetCount = bin.readUshort(data, offset);  offset+=2;
			for(var i=0; i<subClassSetCount; i++)
			{
				var scsOff = bin.readUshort(data, offset);  offset+=2;
				tab.scset.push(  scsOff==0 ? null : Typr.GSUB.readSubClassSet(data, offset0 + scsOff)  );
			}
		}
		else console.log("unknown table format", tab.fmt);
	}
	
//	else if(ltype==6) {
//		if(fmt==2) {
//			var btDef = bin.readUshort(data, offset);  offset+=2;
//			var inDef = bin.readUshort(data, offset);  offset+=2;
//			var laDef = bin.readUshort(data, offset);  offset+=2;
//			
//			tab.btDef = Typr._lctf.readClassDef(data, offset0 + btDef);
//			tab.inDef = Typr._lctf.readClassDef(data, offset0 + inDef);
//			tab.laDef = Typr._lctf.readClassDef(data, offset0 + laDef);
//			
//			tab.scset = [];
//			var cnt = bin.readUshort(data, offset);  offset+=2;
//			for(var i=0; i<cnt; i++) {
//				var loff = bin.readUshort(data, offset);  offset+=2;
//				tab.scset.push(Typr.GSUB.readChainSubClassSet(data, offset0+loff));
//			}
//		}
//	} 
	//if(tab.coverage.indexOf(3)!=-1) console.log(ltype, fmt, tab);
	
	return tab;
}-*/;

  @JsMethod public static native JavaScriptObject readSubClassSet (JavaScriptObject data, int offset)
  /*-{
	var rUs = Typr._bin.readUshort, offset0 = offset, lset = [];
	var cnt = rUs(data, offset);  offset+=2;
	for(var i=0; i<cnt; i++) {
		var loff = rUs(data, offset);  offset+=2;
		lset.push(Typr.GSUB.readSubClassRule(data, offset0+loff));
	}
	return lset;
}-*/;
  @JsMethod public static native JavaScriptObject readSubClassRule(JavaScriptObject data, int offset)
  /*-{
	var rUs = Typr._bin.readUshort, offset0 = offset, rule = {};
	var gcount = rUs(data, offset);  offset+=2;
	var scount = rUs(data, offset);  offset+=2;
	rule.input = [];
	for(var i=0; i<gcount-1; i++) {
		rule.input.push(rUs(data, offset));  offset+=2;
	}
	rule.substLookupRecords = Typr.GSUB.readSubstLookupRecords(data, offset, scount);
	return rule;
}-*/;
  @JsMethod public static native JavaScriptObject readSubstLookupRecords (JavaScriptObject data, int offset, int cnt)
  /*-{
	var rUs = Typr._bin.readUshort;
	var out = [];
	for(var i=0; i<cnt; i++) {  out.push(rUs(data, offset), rUs(data, offset+2));  offset+=4;  }
	return out;
}-*/;

  @JsMethod public static native JavaScriptObject readChainSubClassSet (JavaScriptObject data, int offset)
  /*-{
	var bin = Typr._bin, offset0 = offset, lset = [];
	var cnt = bin.readUshort(data, offset);  offset+=2;
	for(var i=0; i<cnt; i++) {
		var loff = bin.readUshort(data, offset);  offset+=2;
		lset.push(Typr.GSUB.readChainSubClassRule(data, offset0+loff));
	}
	return lset;
}-*/;
  @JsMethod public static native JavaScriptObject readChainSubClassRule(JavaScriptObject data, int offset)
  /*-{
	var bin = Typr._bin, offset0 = offset, rule = {};
	var pps = ["backtrack", "input", "lookahead"];
	for(var pi=0; pi<pps.length; pi++) {
		var cnt = bin.readUshort(data, offset);  offset+=2;  if(pi==1) cnt--;
		rule[pps[pi]]=bin.readUshorts(data, offset, cnt);  offset+= rule[pps[pi]].length*2;
	}
	var cnt = bin.readUshort(data, offset);  offset+=2;
	rule.subst = bin.readUshorts(data, offset, cnt*2);  offset += rule.subst.length*2;
	return rule;
}-*/;

  @JsMethod public static native JavaScriptObject readLigatureSet (JavaScriptObject data, int offset)
  /*-{
	var bin = Typr._bin, offset0 = offset, lset = [];
	var lcnt = bin.readUshort(data, offset);  offset+=2;
	for(var j=0; j<lcnt; j++) {
		var loff = bin.readUshort(data, offset);  offset+=2;
		lset.push(Typr.GSUB.readLigature(data, offset0+loff));
	}
	return lset;
}-*/;
  @JsMethod public static native JavaScriptObject readLigature (JavaScriptObject data, int offset)
  /*-{
	var bin = Typr._bin, lig = {chain:[]};
	lig.nglyph = bin.readUshort(data, offset);  offset+=2;
	var ccnt = bin.readUshort(data, offset);  offset+=2;
	for(var k=0; k<ccnt-1; k++) {  lig.chain.push(bin.readUshort(data, offset));  offset+=2;  }
	return lig;
}-*/;
}