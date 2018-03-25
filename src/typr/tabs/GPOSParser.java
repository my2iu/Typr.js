package typr.tabs;

import elemental.html.Uint8Array;
import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import elemental.util.Collections;
import jsinterop.annotations.JsProperty;
import typr.TyprFont;
import typr.bin;
import typr.lctf;
import typr.lctf.Coverage;
import typr.lctf.LayoutCommonTable;

public class GPOSParser
{
  public static LayoutCommonTable<GPOSTab> parse(Uint8Array data, int offset, int length, TyprFont font) {  return lctf.parse(data, offset, length, font, subtGpos);  }


  public static class GPOSTab
  {
    @JsProperty public char format;
    @JsProperty public Coverage coverage;
    @JsProperty public char valFmt1;
    @JsProperty public char valFmt2;
    @JsProperty public ArrayOfInt classDef1;
    @JsProperty public ArrayOfInt classDef2;
    @JsProperty public ArrayOf<ArrayOf<PairSet>> pairsets;
    @JsProperty public ArrayOf<ArrayOf<MatrixEntry>> matrix;
    
  }
  
  static class PairSet
  {
    @JsProperty public char gid;
    @JsProperty public ArrayOfInt val1;
    @JsProperty public ArrayOfInt val2;
  }

  static class MatrixEntry
  {
    @JsProperty public ArrayOfInt val1;
    @JsProperty public ArrayOfInt val2;
  }

  public static lctf.Subt<GPOSTab> subtGpos = (data, ltype, offset) -> { // lookup type
    if(ltype!=2) return null;
    
//  var bin = Typr._bin, 
    int offset0 = offset;
    GPOSTab tab = new GPOSTab();
    
    tab.format  = bin.readUshort(data, offset);  offset+=2;
    char covOff  = bin.readUshort(data, offset);  offset+=2;
    tab.coverage = lctf.readCoverage(data, covOff+offset0);
    tab.valFmt1 = bin.readUshort(data, offset);  offset+=2;
    tab.valFmt2 = bin.readUshort(data, offset);  offset+=2;
    int ones1 = lctf.numOfOnes(tab.valFmt1);
    int ones2 = lctf.numOfOnes(tab.valFmt2);
    if(tab.format==1)
    {
        tab.pairsets = Collections.arrayOf();
        char count = bin.readUshort(data, offset);  offset+=2;
        
        for(int i=0; i<count; i++)
        {
            char psoff = bin.readUshort(data, offset);  offset+=2;
            psoff += offset0;
            char pvcount = bin.readUshort(data, psoff);  psoff+=2;
            ArrayOf<PairSet> arr = Collections.arrayOf();
            for(int j=0; j<pvcount; j++)
            {
                char gid2 = bin.readUshort(data, psoff);  psoff+=2;
                PairSet pair = new PairSet();
                pair.gid = gid2;
                if(tab.valFmt1!=0) {  pair.val1 = lctf.readValueRecord(data, psoff, tab.valFmt1);  psoff+=ones1*2;  }
                if(tab.valFmt2!=0) {  pair.val2 = lctf.readValueRecord(data, psoff, tab.valFmt2);  psoff+=ones2*2;  }
                arr.push(pair);
            }
            tab.pairsets.push(arr);
        }
    }
    if(tab.format==2)
    {
        char classDef1 = bin.readUshort(data, offset);  offset+=2;
        char classDef2 = bin.readUshort(data, offset);  offset+=2;
        char class1Count = bin.readUshort(data, offset);  offset+=2;
        char class2Count = bin.readUshort(data, offset);  offset+=2;
        
        tab.classDef1 = lctf.readClassDef(data, offset0 + classDef1);
        tab.classDef2 = lctf.readClassDef(data, offset0 + classDef2);
        
        tab.matrix = Collections.arrayOf();
        for(int i=0; i<class1Count; i++)
        {
            ArrayOf<MatrixEntry> row = Collections.arrayOf();
            for(int j=0; j<class2Count; j++)
            {
               ArrayOfInt value1 = null, value2 = null;
                if(tab.valFmt1!=0) { value1 = lctf.readValueRecord(data, offset, tab.valFmt1);  offset+=ones1*2; }
                if(tab.valFmt2!=0) { value2 = lctf.readValueRecord(data, offset, tab.valFmt2);  offset+=ones2*2; }
                MatrixEntry m = new MatrixEntry();
                m.val1 = value1;
                m.val2 = value2;
                row.push(m);
            }
            tab.matrix.push(row);
        }
    }
    return tab;
  };

}
