package typr.tabs;

import elemental.client.Browser;
import elemental.html.Uint8Array;
import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import elemental.util.Collections;
import elemental.util.MapFromStringTo;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import typr.TyprFont;
import typr.bin;
import typr.lctf;
import typr.lctf.Coverage;
import typr.lctf.LayoutCommonTable;

@JsType(namespace="Typr")
public class GSUBParser
{
  @JsIgnore public static LayoutCommonTable<GSUBTab> parse (Uint8Array data, int offset, int length, TyprFont font) {  return lctf.parse(data, offset, length, font, GSUBParser.subt);  }

  public static class GSUBTab
  {

    @JsProperty public char fmt;
    @JsProperty public Coverage coverage;
    @JsProperty public short delta;
    @JsProperty public ArrayOfInt newg;
    @JsProperty public ArrayOf<ArrayOf<Ligature>> vals;
    @JsProperty public ArrayOfInt cDef;
    @JsProperty public ArrayOf<ArrayOf<SubClassRule>> scset;
  }
  
  @JsIgnore public static lctf.Subt<GSUBTab> subt = (data, ltype, offset) -> { // lookup type
//    var bin = Typr._bin;
    int offset0 = offset;
    GSUBTab tab = new GSUBTab();
    
    if(ltype!=1 && ltype!=4 && ltype!=5) return null;
    
    tab.fmt  = bin.readUshort(data, offset);  offset+=2;
    char covOff  = bin.readUshort(data, offset);  offset+=2;
    tab.coverage = lctf.readCoverage(data, covOff+offset0);   // not always is coverage here
    
    if(false) {}
    //  Single Substitution Subtable
    else if(ltype==1) { 
        if(tab.fmt==1) {
            tab.delta = bin.readShort(data, offset);  offset+=2;
        }
        else if(tab.fmt==2) {
            char cnt = bin.readUshort(data, offset);  offset+=2;
            tab.newg = bin.readUshorts(data, offset, cnt);  offset+=tab.newg.length()*2;
        }
    }
    //  Ligature Substitution Subtable
    else if(ltype==4) {
        tab.vals = Collections.arrayOf();
        char cnt = bin.readUshort(data, offset);  offset+=2;
        for(int i=0; i<cnt; i++) {
            char loff = bin.readUshort(data, offset);  offset+=2;
            tab.vals.push(readLigatureSet(data, offset0+loff));
        }
        //console.log(tab.coverage);
        //console.log(tab.vals);
    } 
    //  Contextual Substitution Subtable
    else if(ltype==5) {
        if(tab.fmt==2) {
            char cDefOffset = bin.readUshort(data, offset);  offset+=2;
            tab.cDef = lctf.readClassDef(data, offset0 + cDefOffset);
            tab.scset = Collections.arrayOf();
            char subClassSetCount = bin.readUshort(data, offset);  offset+=2;
            for(int i=0; i<subClassSetCount; i++)
            {
                char scsOff = bin.readUshort(data, offset);  offset+=2;
                tab.scset.push(  scsOff==0 ? null : readSubClassSet(data, offset0 + scsOff)  );
            }
        }
        else Browser.getWindow().getConsole().log("unknown table format"+ (int)tab.fmt);
    }
    
//  else if(ltype==6) {
//      if(fmt==2) {
//          var btDef = bin.readUshort(data, offset);  offset+=2;
//          var inDef = bin.readUshort(data, offset);  offset+=2;
//          var laDef = bin.readUshort(data, offset);  offset+=2;
//          
//          tab.btDef = Typr._lctf.readClassDef(data, offset0 + btDef);
//          tab.inDef = Typr._lctf.readClassDef(data, offset0 + inDef);
//          tab.laDef = Typr._lctf.readClassDef(data, offset0 + laDef);
//          
//          tab.scset = [];
//          var cnt = bin.readUshort(data, offset);  offset+=2;
//          for(var i=0; i<cnt; i++) {
//              var loff = bin.readUshort(data, offset);  offset+=2;
//              tab.scset.push(Typr.GSUBParser.readChainSubClassSet(data, offset0+loff));
//          }
//      }
//  } 
    //if(tab.coverage.indexOf(3)!=-1) console.log(ltype, fmt, tab);
    
    return tab;
  };

  @JsIgnore public static ArrayOf<SubClassRule> readSubClassSet (Uint8Array data, int offset)
  {
//    var rUs = Typr._bin.readUshort, ;
    int offset0 = offset;
    ArrayOf<SubClassRule> lset = Collections.arrayOf();
    char cnt = bin.readUshort(data, offset);  offset+=2;
    for(int i=0; i<cnt; i++) {
        char loff = bin.readUshort(data, offset);  offset+=2;
        lset.push(readSubClassRule(data, offset0+loff));
    }
    return lset;
  }
  
  public static class SubClassRule
  {
    @JsProperty public ArrayOfInt input;
    @JsProperty public ArrayOfInt substLookupRecords;
  }
  @JsIgnore public static SubClassRule readSubClassRule(Uint8Array data, int offset)
  {
//    var rUs = Typr._bin.readUshort, ;
    int offset0 = offset;
    SubClassRule rule = new SubClassRule();
    char gcount = bin.readUshort(data, offset);  offset+=2;
    char scount = bin.readUshort(data, offset);  offset+=2;
    rule.input = Collections.arrayOfInt();
    for(int i=0; i<gcount-1; i++) {
        rule.input.push(bin.readUshort(data, offset));  offset+=2;
    }
    rule.substLookupRecords = readSubstLookupRecords(data, offset, scount);
    return rule;
  }
  @JsIgnore public static ArrayOfInt readSubstLookupRecords (Uint8Array data, int offset, int cnt)
  {
//    var rUs = Typr._bin.readUshort;
    ArrayOfInt out = Collections.arrayOfInt();
    for(int i=0; i<cnt; i++) {  out.push(bin.readUshort(data, offset)); out.push(bin.readUshort(data, offset+2));  offset+=4;  }
    return out;
  }

  @JsIgnore public static ArrayOf<MapFromStringTo<ArrayOfInt>> readChainSubClassSet (Uint8Array data, int offset)
  {
//    var bin = Typr._bin, 
    int offset0 = offset;
    ArrayOf<MapFromStringTo<ArrayOfInt>> lset = Collections.arrayOf();
    char cnt = bin.readUshort(data, offset);  offset+=2;
    for(int i=0; i<cnt; i++) {
        char loff = bin.readUshort(data, offset);  offset+=2;
        lset.push(readChainSubClassRule(data, offset0+loff));
    }
    return lset;
  }
  
  @JsIgnore public static MapFromStringTo<ArrayOfInt> readChainSubClassRule(Uint8Array data, int offset)
  {
//    var bin = Typr._bin;
    int offset0 = offset;
    MapFromStringTo<ArrayOfInt> rule = Collections.mapFromStringTo();
    String [] pps = new String[] {"backtrack", "input", "lookahead"};
    for(int pi=0; pi<pps.length; pi++) {
        char cnt = bin.readUshort(data, offset);  offset+=2;  if(pi==1) cnt--;
        rule.put(pps[pi], bin.readUshorts(data, offset, cnt));  offset+= rule.get(pps[pi]).length()*2;
    }
    char cnt = bin.readUshort(data, offset);  offset+=2;
    rule.put("subst", bin.readUshorts(data, offset, cnt*2));  offset += rule.get("subst").length()*2;
    return rule;
  }

  @JsIgnore public static ArrayOf<Ligature> readLigatureSet (Uint8Array data, int offset)
  {
//    var bin = Typr._bin, 
    int offset0 = offset;
    ArrayOf<Ligature> lset = Collections.arrayOf();
    char lcnt = bin.readUshort(data, offset);  offset+=2;
    for(int j=0; j<lcnt; j++) {
        char loff = bin.readUshort(data, offset);  offset+=2;
        lset.push(readLigature(data, offset0+loff));
    }
    return lset;
  }
  
  public static class Ligature
  {
    @JsProperty public ArrayOfInt chain;
    @JsProperty public int nglyph;
  }
  
  @JsIgnore public static Ligature readLigature (Uint8Array data, int offset)
  {
//    var bin = Typr._bin, 
    Ligature lig = new Ligature();
    lig.chain = Collections.arrayOfInt();
    lig.nglyph = bin.readUshort(data, offset);  offset+=2;
    char ccnt = bin.readUshort(data, offset);  offset+=2;
    for(int k=0; k<ccnt-1; k++) {  lig.chain.push(bin.readUshort(data, offset));  offset+=2;  }
    return lig;
  }
}
