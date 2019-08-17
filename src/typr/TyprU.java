package typr;

import java.util.function.Consumer;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.NumberFormat;

import elemental.client.Browser;
import elemental.html.CanvasRenderingContext2D;
import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import elemental.util.ArrayOfNumber;
import elemental.util.ArrayOfString;
import elemental.util.Collections;
import elemental.util.MapFromStringTo;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import typr.lctf.FeatureList;
import typr.lctf.LayoutCommonTable;
import typr.lctf.LookupTable;
import typr.tabs.CFF;
import typr.tabs.CFF.CffDict;
import typr.tabs.CFF.GetCharStringOutput;
import typr.tabs.CffDictBase;
import typr.tabs.GPOSParser.GPOSTab;
import typr.tabs.GPOSParser.MatrixEntry;
import typr.tabs.GPOSParser.PairSet;
import typr.tabs.GSUBParser.GSUBTab;
import typr.tabs.GSUBParser.Ligature;
import typr.tabs.GSUBParser.SubClassRule;
import typr.tabs.SVG;
import typr.tabs.glyf;
import typr.tabs.glyf.Part;
import typr.tabs.glyf.PartInternal;

@JsType(namespace="Typr",name="U")
public class TyprU
{
  @JsMethod public static int codeToGlyph (TyprFont font, int code)
  {
	typr.tabs.cmap cmap = font.cmap;
	
    int tind = -1;
    if(cmap.platformEncodingMap.hasKey("p0e4")) tind = cmap.platformEncodingMap.get("p0e4");
    else if(cmap.platformEncodingMap.hasKey("p0e1")) tind = cmap.platformEncodingMap.get("p0e1");
    else if(cmap.platformEncodingMap.hasKey("p0e3")) tind = cmap.platformEncodingMap.get("p0e3");
    else if(cmap.platformEncodingMap.hasKey("p3e1")) tind = cmap.platformEncodingMap.get("p3e1");
    else if(cmap.platformEncodingMap.hasKey("p1e0")) tind = cmap.platformEncodingMap.get("p1e0");
	
	if(tind==-1)
	   throw new IllegalArgumentException("no familiar platform and encoding!");
	
	typr.tabs.cmap.Table tab = cmap.tables.get(tind);
	
	if(tab.format==0)
	{
		if(code>=tab.map.length()) return 0;
		return tab.map.get(code);
	}
	else if(tab.format==4)
	{
		int sind = -1;
		for(int i=0; i<tab.endCount.length(); i++)   if(code<=tab.endCount.get(i)){  sind=i;  break;  } 
		if(sind==-1) return 0;
		if(tab.startCount.get(sind)>code) return 0;
		
		int gli = 0;
		if(tab.idRangeOffset.get(sind)!=0) gli = tab.glyphIdArray.get((code-tab.startCount.get(sind)) + (tab.idRangeOffset.get(sind)>>1) - (tab.idRangeOffset.length()-sind));
		else                           gli = code + tab.idDelta.get(sind);
		return gli & 0xFFFF;
	}
	else if(tab.format==12)
	{
		if(code>tab.groups.get(tab.groups.length()-1).get(1)) return 0;
		for(int i=0; i<tab.groups.length(); i++)
		{
			ArrayOfInt grp = tab.groups.get(i);
			if(grp.get(0)<=code && code<=grp.get(1)) return grp.get(2) + (code-grp.get(0));
		}
		return 0;
	}
	else throw new IllegalArgumentException("unknown cmap table format "+tab.format);
  }


  @JsMethod public static TyprPath glyphToPath (TyprFont font, int gid)
  {
    TyprPath path = new TyprPath();
	if(font.SVG != null && font.SVG.entries.hasKey(gid)) {
	  if (font.SVG.parsedEntries.hasKey(gid)) return font.SVG.parsedEntries.get(gid); 
		String p = font.SVG.entries.get(gid);  if(p==null) return path;
		TyprPath pth = SVG.toPath(p);  
		font.SVG.parsedEntries.put(gid, pth);
		return pth;
	}
	else if(font.CFF != null) {
	    CffDictBase Private = font.CFF.Private;
	    if (font.CFF.isCIDFont)
	    {
	      int fdIdx = font.CFF.FDSelect.getFd(gid);
	      CffDict fd = font.CFF.FDArray.get(fdIdx);
	      Private = fd.Private;
	    }
	    glyphToPathCFF(font, gid, path, Private);
	}
	else if(font.glyf != null) {  _drawGlyf(gid, font, path);  }
	return path;
  }

  @JsType
  public static class CFFPathState
  {
    @JsProperty double x = 0;  // maybe an int?
    @JsProperty double y = 0;  // maybe an int?
    @JsProperty ArrayOfNumber stack = Collections.arrayOfNumber();  // Array of ?
    @JsProperty int nStems = 0;
    @JsProperty boolean haveWidth = false;
    @JsProperty double width;   // maybe an int?
    @JsProperty boolean open = false;
    @JsMethod void init(CffDictBase Private)
    {
      this.width = (Private != null ? Private.defaultWidthX : 0);
    }
  }
  
  @JsIgnore static void glyphToPathCFF(TyprFont font, int gid, TyprPath path, CffDictBase Private)
  {
    CFFPathState state = new CFFPathState();
    state.init(Private);
    _drawCFF(font.CFF.CharStrings.get(gid), state, font.CFF, path, Private, (str) -> {Browser.getWindow().getConsole().log(str);} );
  }
  
  @JsMethod public static ArrayOfInt getGlyphDimensions (TyprFont font, int gid)
  {
    if((font.SVG != null && font.SVG.entries.hasKey(gid)) || font.CFF != null) 
    {
      TyprPath path = glyphToPath(font, gid);
      // Calculate some bounds from the path since no bounding box is available
      if (path != null && path.crds.length() >= 2)
      {
        int xMin, xMax; 
        xMin = xMax = (int)path.crds.get(0);
        int yMin, yMax; 
        yMin = yMax = (int)path.crds.get(1);
        for (int i = 2; i < path.crds.length(); i+=2)
        {
          int x = (int)path.crds.get(i);
          int y = (int)path.crds.get(i + 1);
          if (x < xMin) xMin = x;
          if (x > xMax) xMax = x;
          if (y < yMin) yMin = y;
          if (y > yMax) yMax = y;
        }
        ArrayOfInt toReturn = Collections.arrayOfInt();
        toReturn.push(xMin);
        toReturn.push(yMin);
        toReturn.push(xMax);
        toReturn.push(yMax);
        return toReturn;
      }
    }
    else if(font.glyf != null) 
    {  
      glyf gl = _getGlyf(gid, font);
      if (gl != null)
      {
        ArrayOfInt toReturn = Collections.arrayOfInt();
        toReturn.push(gl.xMin);
        toReturn.push(gl.yMin);
        toReturn.push(gl.xMax);
        toReturn.push(gl.yMax);
        return toReturn;
      }
    }
    
	return null;
  }
  
  @JsIgnore public static glyf _getGlyf (int gid, TyprFont font)
  {
    glyf gl = font.glyf.get(gid);
	if(gl==null) 
	{
	  gl = glyf._parseGlyf(font, gid);
	  font.glyf.set(gid, gl);
	}
	return gl;
  }
  @JsIgnore public static void _drawGlyf (int gid, TyprFont font, TyprPath path)
  {
	glyf gl = _getGlyf(gid, font);
	if(gl!=null){
		if(gl.noc>-1) _simpleGlyph(gl, path);
		else          _compoGlyph (gl, font, path);
	}
  }
  @JsIgnore public static void _simpleGlyph (glyf gl, TyprPath p)
  {
	for(int c=0; c<gl.noc; c++)
	{
		int i0 = (c==0) ? 0 : (gl.endPts.get(c-1) + 1);
		int il = gl.endPts.get(c);
		
		for(int i=i0; i<=il; i++)
		{
		    int pr = (i==i0)?il:(i-1);
		    int nx = (i==il)?i0:(i+1);
			boolean onCurve = ((gl.flags.get(i)&1) != 0);
			boolean prOnCurve = ((gl.flags.get(pr)&1) != 0);
			boolean nxOnCurve = ((gl.flags.get(nx)&1) != 0);
			
			int x = gl.xs.get(i), y = gl.ys.get(i);
			
			if(i==i0) { 
				if(onCurve)  
				{
					if(prOnCurve) TyprUP.moveTo(p, gl.xs.get(pr), gl.ys.get(pr)); 
					else          {  TyprUP.moveTo(p,x,y);  continue;  //  will do curveTo at il    
  }
				}
				else        
				{
					if(prOnCurve) TyprUP.moveTo(p,  gl.xs.get(pr),       gl.ys.get(pr)        );
					else          TyprUP.moveTo(p, (gl.xs.get(pr)+x)/2, (gl.ys.get(pr)+y)/2   ); 
				}
			}
			if(onCurve)
			{
				if(prOnCurve) TyprUP.lineTo(p,x,y);
			}
			else
			{
				if(nxOnCurve) TyprUP.qcurveTo(p, x, y, gl.xs.get(nx), gl.ys.get(nx)); 
				else          TyprUP.qcurveTo(p, x, y, (x+gl.xs.get(nx))/2, (y+gl.ys.get(nx))/2); 
			}
		}
		TyprUP.closePath(p);
	}
  }
  @JsIgnore public static void _compoGlyph (glyf gl, TyprFont font, TyprPath p)
  {
	for(int j=0; j<gl.parts.length(); j++)
	{
		TyprPath path = new TyprPath();
		Part prt = gl.parts.get(j);
		_drawGlyf(prt.glyphIndex, font, path);
		
		PartInternal m = prt.m;
		for(int i=0; i<path.crds.length(); i+=2)
		{
			double x = path.crds.get(i), y = path.crds.get(i+1);
			p.crds.push(x*m.a + y*m.b + m.tx);
			p.crds.push(x*m.c + y*m.d + m.ty);
		}
		for(int i=0; i<path.cmds.length(); i++) p.cmds.push(path.cmds.get(i));
	}
  }


  @JsIgnore public static int _getGlyphClass (int g, ArrayOfInt cd)
  {
	int intr = lctf.getInterval(cd, g);
	return intr==-1 ? 0 : cd.get(intr+2);
	//for(var i=0; i<cd.start.length; i++) 
	//	if(cd.start[i]<=g && cd.end[i]>=g) return cd.class[i];
	//return 0;
  }

  @JsMethod public static int getPairAdjustment (TyprFont font, int g1, int g2)
  {
	if(font.GPOS != null)
	{
		LookupTable<GPOSTab> ltab = null;
		for(int i=0; i<font.GPOS.featureList.length(); i++) 
		{
			lctf.FeatureList fl = font.GPOS.featureList.get(i);
			if(fl.tag=="kern")
				for(int j=0; j<fl.tab.length(); j++) 
					if(font.GPOS.lookupList.get(fl.tab.get(j)).ltype==2) ltab=font.GPOS.lookupList.get(fl.tab.get(j));
		}
		if(ltab != null)
		{
//			var adjv = 0;
			for(int i=0; i<ltab.tabs.length(); i++)
			{
			    GPOSTab tab = ltab.tabs.get(i);
				int ind = lctf.coverageIndex(tab.coverage, g1);
				if(ind==-1) continue;
				MatrixEntry adj = null;
				if(tab.format==1)
				{
					ArrayOf<PairSet> right = tab.pairsets.get(ind);
					for(int j=0; j<right.length(); j++) if(right.get(j).gid2==g2) adj = right.get(j);
					if(adj==null) continue;
				}
				else if(tab.format==2)
				{
					int c1 = _getGlyphClass(g1, tab.classDef1);
					int c2 = _getGlyphClass(g2, tab.classDef2);
					adj = tab.matrix.get(c1).get(c2);
				}
				return adj.val1.get(2);
			}
		}
	}
	return getPairAdjustmentFromKern(font, g1, g2);
  }
  @JsIgnore private static int getPairAdjustmentFromKern (TyprFont font, int g1, int g2)
  {
    if(font.kern != null)
    {
        int ind1 = font.kern.glyph1.indexOf(g1);
        if(ind1!=-1)
        {
            int ind2 = font.kern.rval.get(ind1).glyph2.indexOf(g2);
            if(ind2!=-1) return font.kern.rval.get(ind1).vals.get(ind2);
        }
    }
    
    return 0;
  }

  
  @JsMethod public static ArrayOfInt stringToGlyphs (TyprFont font, String str)
  {
	ArrayOfInt gls = Collections.arrayOfInt();
	for(int i=0; i<str.length(); i++) {
		int cc = str.codePointAt(i);  if(cc>0xffff) i++;
		gls.push(codeToGlyph(font, cc));
	}
	//console.log(gls.slice(0));
	
	//console.log(gls);  return gls;
	
	LayoutCommonTable<GSUBTab> gsub = font.GSUB;  if(gsub==null) return gls;
	ArrayOf<LookupTable<GSUBTab>> llist = gsub.lookupList;
	ArrayOf<FeatureList> flist = gsub.featureList;
	
	String wsep = "\n\t\" ,.:;!?()  ،";
	String R = "آأؤإاةدذرزوٱٲٳٵٶٷڈډڊڋڌڍڎڏڐڑڒړڔڕږڗژڙۀۃۄۅۆۇۈۉۊۋۍۏےۓەۮۯܐܕܖܗܘܙܞܨܪܬܯݍݙݚݛݫݬݱݳݴݸݹࡀࡆࡇࡉࡔࡧࡩࡪࢪࢫࢬࢮࢱࢲࢹૅેૉ૊૎૏ૐ૑૒૝ૡ૤૯஁ஃ஄அஉ஌எஏ஑னப஫஬";
	String L = "ꡲ્૗";
	
	for(int ci=0; ci<gls.length(); ci++) {
		int gl = gls.get(ci);
		
		boolean slft = ci==0              || wsep.indexOf(str.charAt(ci-1))!=-1;
		boolean srgt = ci==gls.length()-1 || wsep.indexOf(str.charAt(ci+1))!=-1;
		
		if(!slft && R.indexOf(str.charAt(ci-1))!=-1) slft=true;
		if(!srgt && R.indexOf(str.charAt(ci  ))!=-1) srgt=true;
		
		if(!srgt && L.indexOf(str.charAt(ci+1))!=-1) srgt=true;
		if(!slft && L.indexOf(str.charAt(ci  ))!=-1) slft=true;
		
		String feat = null;
		if(slft) feat = srgt ? "isol" : "init";
		else     feat = srgt ? "fina" : "medi";
		
		for(int fi=0; fi<flist.length(); fi++)
		{
			if(!feat.equals(flist.get(fi).tag)) continue;
			for(int ti=0; ti<flist.get(fi).tab.length(); ti++)
			{
				LookupTable<GSUBTab> tab = llist.get(flist.get(fi).tab.get(ti));
				if(tab.ltype!=1) continue;
				_applyType1(gls, ci, tab);
			}
		}
	}
	final String[] cligs = new String[] {"rlig", "liga", "mset"};
	
	//console.log(gls);
	
	for(int ci=0; ci<gls.length(); ci++) {
		int gl = gls.get(ci);
		int rlim = Math.min(3, gls.length()-ci-1);
		for(int fi=0; fi<flist.length(); fi++)
		{
			FeatureList fl = flist.get(fi);
			int ligMatch = -1;
			for (int cligsIdx = 0; cligsIdx < cligs.length; cligsIdx++)
			{
			  if (cligs[cligsIdx].equals(fl.tag))
			  {
			    ligMatch = cligsIdx;
			    break;
			  }
			}
			if(ligMatch==-1) continue;
			for(int ti=0; ti<fl.tab.length(); ti++)
			{
				LookupTable<GSUBTab> tab = llist.get(fl.tab.get(ti));
				for(int j=0; j<tab.tabs.length(); j++)
				{
					if(tab.tabs.get(j)==null) continue;
					int ind = lctf.coverageIndex(tab.tabs.get(j).coverage, gl);  if(ind==-1) continue;  
					//*
					if(tab.ltype==4) {
						ArrayOf<Ligature> vals = tab.tabs.get(j).vals.get(ind);
						
						for(int k=0; k<vals.length(); k++) {
							Ligature lig = vals.get(k);
							int rl = lig.chain.length();  if(rl>rlim) continue;
							boolean good = true;
							for(int l=0; l<rl; l++) if(lig.chain.get(l)!=gls.get(ci+(1+l))) good=false;
							if(!good) continue;
							gls.set(ci,lig.nglyph);
							for(int l=0; l<rl; l++) gls.set(ci+l+1,-1);
							//console.log("lig", fl.tag,  gl, lig.chain, lig.nglyph);
						}
					}
					else  if(tab.ltype==5) {
						GSUBTab ltab = tab.tabs.get(j);  if(ltab.fmt!=2) continue;
						int cind = lctf.getInterval(ltab.cDef, gl);
						int cls = ltab.cDef.get(cind+2);
						ArrayOf<SubClassRule> scs = ltab.scset.get(cls); 
						for(int i=0; i<scs.length(); i++) {
							SubClassRule sc = scs.get(i);
							ArrayOfInt inp = sc.input;
							if(inp.length()>rlim) continue;
							boolean good = true;
							for(int l=0; l<inp.length(); l++) {
								int cind2 = lctf.getInterval(ltab.cDef, gls.get(ci+1+l));
								if(cind==-1 && ltab.cDef.get(cind2+2)!=inp.get(l)) {  good=false;  break;  }
							}
							if(!good) continue;
							//console.log(ci, gl);
							ArrayOfInt lrs = sc.substLookupRecords;
							for(int k=0; k<lrs.length(); k+=2)
							{
								int gi = lrs.get(k);
								int tabi = lrs.get(k+1);
								//Typr.U._applyType1(gls, ci+gi, llist[tabi]);
								//console.log(tabi, gls[ci+gi], llist[tabi]);
							}
						}
					}
				}
			}
		}
	}
	
	return gls;
  }
  
  @JsMethod public static void _applyType1 (ArrayOfInt gls, int ci, LookupTable<GSUBTab> tab) {
	int gl = gls.get(ci);
	for(int j=0; j<tab.tabs.length(); j++) {
		GSUBTab ttab = tab.tabs.get(j);
		int ind = lctf.coverageIndex(ttab.coverage,gl);  if(ind==-1) continue;  
		if(ttab.fmt==1) gls.set(ci, gls.get(ci)+ttab.delta);
		else            gls.set(ci, ttab.newg.get(ind));
		//console.log(ci, gl, "subst", flist[fi].tag, i, j, ttab.newg[ind]);
	}
  }

  @JsMethod public static ArrayOfInt glyphsToPositions (TyprFont font, ArrayOfInt gls)
  {	
	ArrayOfInt pos = Collections.arrayOfInt();
	int x = 0;
	pos.push(0);
	
	for(int i=0; i<gls.length(); i++)
	{
		int gid = gls.get(i);  if(gid==-1) continue;
		int gid2 = (i<gls.length()-1 && gls.get(i+1)!=-1)  ? gls.get(i+1) : 0;
		x += font.hmtx.aWidth.get(gid);
		if(i<gls.length()-1) x += getPairAdjustment(font, gid, gid2);
		pos.push(x);
	}
	return pos;
  }

  @JsMethod(name="glyphsToPathOverloaded") public static TyprPath glyphsToPath (TyprFont font, ArrayOfInt gls)
  {
    return glyphsToPath(font, gls, null);
  }

  @JsMethod public static TyprPath glyphsToPath (TyprFont font, ArrayOfInt gls, String clr)
  {	
	//gls = gls.reverse();//gls.slice(0,12).concat(gls.slice(12).reverse());
	
	TyprPath tpath = new TyprPath();
	double x = 0;
	
	for(int i=0; i<gls.length(); i++)
	{
		int gid = gls.get(i);  if(gid==-1) continue;
		int gid2 = (i<gls.length()-1 && gls.get(i+1)!=-1)  ? gls.get(i+1) : 0;
		TyprPath path = glyphToPath(font, gid);
		for(int j=0; j<path.crds.length(); j+=2)
		{
			tpath.crds.push(path.crds.get(j) + x);
			tpath.crds.push(path.crds.get(j+1));
		}
		if(clr != null) tpath.cmds.push(clr);
		for(int j=0; j<path.cmds.length(); j++) tpath.cmds.push(path.cmds.get(j));
		if(clr != null) tpath.cmds.push("X");
		x += font.hmtx.aWidth.get(gid);// - font.hmtx.lsBearing[gid];
		if(i<gls.length()-1) x += getPairAdjustment(font, gid, gid2);
	}
	return tpath;
  }

  @JsIgnore private static int typrPathCoordCount(String cmd)
  {
    switch (cmd)
    {
    case "M": return 2;
    case "L": return 2;
    case "Q": return 4;
    case "C": return 6;
    }
    return 0;
  }
  
  @JsMethod public static String pathToSVG (TyprPath path, Integer prec)
  {
	if(prec==null) prec = 5;
	ArrayOfString out = Collections.arrayOfString();
	int co = 0;
//	var lmap = new {"M":2,"L":2,"Q":4,"C":6};
	for(int i=0; i<path.cmds.length(); i++)
	{
		String cmd = path.cmds.get(i);
		int cn = co+(typrPathCoordCount(cmd) != 0?typrPathCoordCount(cmd):0);  
		out.push(cmd);
		while(co<cn) {  
		  double c = path.crds.get(co++);  
		  out.push(Double.parseDouble(TyprMisc.toFixed(c, prec))+(co==cn?"":" "));  }
	}
	return out.join("");
  }

  @JsMethod public static void pathToContext (TyprPath path, CanvasRenderingContext2D ctx)
  {
	int c = 0;
	ArrayOfNumber crds = path.crds;
	
	for(int j=0; j<path.cmds.length(); j++)
	{
		String cmd = path.cmds.get(j);
		if     (cmd.equals("M")) {
			ctx.moveTo((float)crds.get(c), (float)crds.get(c+1));
			c+=2;
		}
		else if(cmd=="L") {
			ctx.lineTo((float)crds.get(c), (float)crds.get(c+1));
			c+=2;
		}
		else if(cmd=="C") {
			ctx.bezierCurveTo((float)crds.get(c), (float)crds.get(c+1), (float)crds.get(c+2), (float)crds.get(c+3), (float)crds.get(c+4), (float)crds.get(c+5));
			c+=6;
		}
		else if(cmd=="Q") {
			ctx.quadraticCurveTo((float)crds.get(c), (float)crds.get(c+1), (float)crds.get(c+2), (float)crds.get(c+3));
			c+=4;
		}
		else if(cmd.charAt(0)=='#') {
			ctx.beginPath();
			ctx.setFillStyle(cmd);
		}
		else if(cmd=="Z") {
			ctx.closePath();
		}
		else if(cmd=="X") {
			ctx.fill();
		}
	}
  }


  public static class TyprUP
  {
    public static void moveTo (TyprPath p, double x, double y)
    {
      p.cmds.push("M");  p.crds.push(x); p.crds.push(y);
    }
    public static void lineTo (TyprPath p, double x, double y)
    {
	p.cmds.push("L");  p.crds.push(x); p.crds.push(y);
    }
    public static void curveTo (TyprPath p, double a,double b,double c,double d,double e,double f)
    {
	p.cmds.push("C");  p.crds.push(a); p.crds.push(b); p.crds.push(c); p.crds.push(d); p.crds.push(e); p.crds.push(f);
    }
    public static void qcurveTo (TyprPath p, double a,double b,double c,double d)
    {
	p.cmds.push("Q");  p.crds.push(a); p.crds.push(b); p.crds.push(c); p.crds.push(d);
    }
    public static void closePath (TyprPath p) {  p.cmds.push("Z");}
  }


  @JsIgnore public static double getPrivateNominalWidthX(CffDictBase Private)
  {
    return Private.nominalWidthX;
  }

  @JsIgnore public static double getCFFNominalWidthX(CFF font)
  {
    return font.nominalWidthX;
  }

  @JsIgnore public static ArrayOfInt getPrivateSubrs(CffDictBase obj, int ind)
  {
    return obj.Subrs.get(ind + obj.Bias);
  }

  @JsIgnore public static ArrayOfInt getCFFSubrs(CFF obj, int ind)
  {
    return obj.Subrs.get(ind + obj.Bias);
  }

  
  @JsIgnore public static void _drawCFF (ArrayOfInt cmds, CFFPathState state, CFF font, TyprPath p, CffDictBase Private, Consumer<String> consoleLog)
 {
	ArrayOfNumber stack = state.stack;
	int nStems = state.nStems;
	boolean haveWidth=state.haveWidth;
	double width=state.width;
	boolean open=state.open;
	int i=0;
	double x=state.x, y=state.y, c1x=0, c1y=0, c2x=0, c2y=0, c3x=0, c3y=0, c4x=0, c4y=0, jpx=0, jpy=0;
	
	//var o = {val:0,size:0};
	GetCharStringOutput o = new GetCharStringOutput();
	//console.log(cmds);
	while(i<cmds.length())
	{
		CFF.getCharString(cmds, i, o);
		String v = o.val;
		double vnum = o.numVal;
		i += o.size;
			
		if(v == null) {
	        stack.push(vnum);
		}
		else if(v.equals("o1") || v.equals("o18"))  //  hstem || hstemhm
		{
			boolean hasWidthArg;

			// The number of stem operators on the stack is always even.
			// If the value is uneven, that means a width is specified.
			hasWidthArg = (stack.length() % 2 != 0);
			if (hasWidthArg && !haveWidth) {
				width = stack.shift() + getPrivateNominalWidthX(Private);
			}

			nStems += stack.length() >> 1;
			stack.setLength(0);
			haveWidth = true;
		}
		else if(v.equals("o3") || v.equals("o23"))  // vstem || vstemhm
		{
			boolean hasWidthArg;

			// The number of stem operators on the stack is always even.
			// If the value is uneven, that means a width is specified.
			hasWidthArg = (stack.length() % 2 != 0);
			if (hasWidthArg && !haveWidth) {
				width = stack.shift() + getPrivateNominalWidthX(Private);
			}

			nStems += stack.length() >> 1;
			stack.setLength(0);
			haveWidth = true;
		}
		else if(v.equals("o4"))
		{
			if (stack.length() > 1 && !haveWidth) {
                        width = stack.shift() + getPrivateNominalWidthX(Private);
                        haveWidth = true;
                    }
			if(open) TyprUP.closePath(p);

                    y += stack.pop();
					TyprUP.moveTo(p,x,y);   open=true;
		}
		else if(v.equals("o5"))
		{
			while (stack.length() > 0) {
                        x += stack.shift();
                        y += stack.shift();
                        TyprUP.lineTo(p, x, y);
                    }
		}
		else if(v.equals("o6") || v.equals("o7"))  // hlineto || vlineto
		{
			int count = stack.length();
			boolean isX = (v == "o6");
			
			for(int j=0; j<count; j++) {
				double sval = stack.shift();
				
				if(isX) x += sval;  else  y += sval;
				isX = !isX;
				TyprUP.lineTo(p, x, y);
			}
		}
		else if(v.equals("o8") || v.equals("o24"))	// rrcurveto || rcurveline
		{
			int count = stack.length();
			int index = 0;
			while(index+6 <= count) {
				c1x = x + stack.shift();
				c1y = y + stack.shift();
				c2x = c1x + stack.shift();
				c2y = c1y + stack.shift();
				x = c2x + stack.shift();
				y = c2y + stack.shift();
				TyprUP.curveTo(p, c1x, c1y, c2x, c2y, x, y);
				index+=6;
			}
			if(v.equals("o24"))
			{
				x += stack.shift();
				y += stack.shift();
				TyprUP.lineTo(p, x, y);
			}
		}
		else if(v.equals("o11"))  break;
		else if(v.equals("o1234") || v.equals("o1235") || v.equals("o1236") || v.equals("o1237"))//if((v+"").slice(0,3)=="o12")
		{
			if(v.equals("o1234"))
			{
				c1x = x   + stack.shift();    // dx1
                c1y = y;                      // dy1
				c2x = c1x + stack.shift();    // dx2
				c2y = c1y + stack.shift();    // dy2
				jpx = c2x + stack.shift();    // dx3
				jpy = c2y;                    // dy3
				c3x = jpx + stack.shift();    // dx4
				c3y = c2y;                    // dy4
				c4x = c3x + stack.shift();    // dx5
				c4y = y;                      // dy5
				x = c4x + stack.shift();      // dx6
				TyprUP.curveTo(p, c1x, c1y, c2x, c2y, jpx, jpy);
				TyprUP.curveTo(p, c3x, c3y, c4x, c4y, x, y);
				
			}
			if(v.equals("o1235"))
			{
				c1x = x   + stack.shift();    // dx1
				c1y = y   + stack.shift();    // dy1
				c2x = c1x + stack.shift();    // dx2
				c2y = c1y + stack.shift();    // dy2
				jpx = c2x + stack.shift();    // dx3
				jpy = c2y + stack.shift();    // dy3
				c3x = jpx + stack.shift();    // dx4
				c3y = jpy + stack.shift();    // dy4
				c4x = c3x + stack.shift();    // dx5
				c4y = c3y + stack.shift();    // dy5
				x = c4x + stack.shift();      // dx6
				y = c4y + stack.shift();      // dy6
				stack.shift();                // flex depth
				TyprUP.curveTo(p, c1x, c1y, c2x, c2y, jpx, jpy);
				TyprUP.curveTo(p, c3x, c3y, c4x, c4y, x, y);
			}
			if(v.equals("o1236"))
			{
				c1x = x   + stack.shift();    // dx1
				c1y = y   + stack.shift();    // dy1
				c2x = c1x + stack.shift();    // dx2
				c2y = c1y + stack.shift();    // dy2
				jpx = c2x + stack.shift();    // dx3
				jpy = c2y;                    // dy3
				c3x = jpx + stack.shift();    // dx4
				c3y = c2y;                    // dy4
				c4x = c3x + stack.shift();    // dx5
				c4y = c3y + stack.shift();    // dy5
				x = c4x + stack.shift();      // dx6
				TyprUP.curveTo(p, c1x, c1y, c2x, c2y, jpx, jpy);
				TyprUP.curveTo(p, c3x, c3y, c4x, c4y, x, y);
			}
			if(v.equals("o1237"))
			{
				c1x = x   + stack.shift();    // dx1
				c1y = y   + stack.shift();    // dy1
				c2x = c1x + stack.shift();    // dx2
				c2y = c1y + stack.shift();    // dy2
				jpx = c2x + stack.shift();    // dx3
				jpy = c2y + stack.shift();    // dy3
				c3x = jpx + stack.shift();    // dx4
				c3y = jpy + stack.shift();    // dy4
				c4x = c3x + stack.shift();    // dx5
				c4y = c3y + stack.shift();    // dy5
				if (Math.abs(c4x - x) > Math.abs(c4y - y)) {
				    x = c4x + stack.shift();
				} else {
				    y = c4y + stack.shift();
				}
				TyprUP.curveTo(p, c1x, c1y, c2x, c2y, jpx, jpy);
				TyprUP.curveTo(p, c3x, c3y, c4x, c4y, x, y);
			}
		}
		else if(v.equals("o14"))
		{
			if (stack.length() > 0 && !haveWidth) {
                        width = stack.shift() + getCFFNominalWidthX(font);
                        haveWidth = true;
                    }
			if(stack.length()==4) // seac = standard encoding accented character
			{
			
				double asb = 0;
				double adx = stack.shift();
				double ady = stack.shift();
				int bchar = (int)stack.shift();
				int achar = (int)stack.shift();
			
				
				int bind = CFF.glyphBySE(font, bchar);
				int aind = CFF.glyphBySE(font, achar);
				
				//console.log(bchar, bind);
				//console.log(achar, aind);
				//state.x=x; state.y=y; state.nStems=nStems; state.haveWidth=haveWidth; state.width=width;  state.open=open;
				
				_drawCFF(font.CharStrings.get(bind), state,font,p,Private, consoleLog);
				state.x = adx; state.y = ady;
				_drawCFF(font.CharStrings.get(aind), state,font,p,Private, consoleLog);
				
				//x=state.x; y=state.y; nStems=state.nStems; haveWidth=state.haveWidth; width=state.width;  open=state.open;
			}
			if(open) {  TyprUP.closePath(p);  open=false;  }
		}		
		else if(v.equals("o19") || v.equals("o20")) 
		{ 
			boolean hasWidthArg;

			// The number of stem operators on the stack is always even.
			// If the value is uneven, that means a width is specified.
			hasWidthArg = (stack.length() % 2 != 0);
			if (hasWidthArg && !haveWidth) {
				width = stack.shift() + getPrivateNominalWidthX(Private);
			}

			nStems += stack.length() >> 1;
			stack.setLength(0);
			haveWidth = true;
			
			i += (nStems + 7) >> 3;
		}
		
		else if(v.equals("o21")) {
			if (stack.length() > 2 && !haveWidth) {
                        width = stack.shift() + getPrivateNominalWidthX(Private);
                        haveWidth = true;
                    }

                    y += stack.pop();
                    x += stack.pop();
					
					if(open) TyprUP.closePath(p);
                    TyprUP.moveTo(p,x,y);   open=true;
		}
		else if(v.equals("o22"))
		{
			 if (stack.length() > 1 && !haveWidth) {
                        width = stack.shift() + getPrivateNominalWidthX(Private);
                        haveWidth = true;
                    }
					
                    x += stack.pop();
					
					if(open) TyprUP.closePath(p);
					TyprUP.moveTo(p,x,y);   open=true;                    
		}
		else if(v.equals("o25"))
		{
			while (stack.length() > 6) {
                        x += stack.shift();
                        y += stack.shift();
                        TyprUP.lineTo(p, x, y);
                    }

                    c1x = x + stack.shift();
                    c1y = y + stack.shift();
                    c2x = c1x + stack.shift();
                    c2y = c1y + stack.shift();
                    x = c2x + stack.shift();
                    y = c2y + stack.shift();
                    TyprUP.curveTo(p, c1x, c1y, c2x, c2y, x, y);
		}
		else if(v.equals("o26")) 
		{
			if ((stack.length() % 2) != 0) {
                        x += stack.shift();
                    }

                    while (stack.length() > 0) {
                        c1x = x;
                        c1y = y + stack.shift();
                        c2x = c1x + stack.shift();
                        c2y = c1y + stack.shift();
                        x = c2x;
                        y = c2y + stack.shift();
                        TyprUP.curveTo(p, c1x, c1y, c2x, c2y, x, y);
                    }

		}
		else if(v.equals("o27"))
		{
			if ((stack.length() % 2) != 0) {
                        y += stack.shift();
                    }

                    while (stack.length() > 0) {
                        c1x = x + stack.shift();
                        c1y = y;
                        c2x = c1x + stack.shift();
                        c2y = c1y + stack.shift();
                        x = c2x + stack.shift();
                        y = c2y;
                        TyprUP.curveTo(p, c1x, c1y, c2x, c2y, x, y);
                    }
		}
		else if(v.equals("o10") || v.equals("o29"))	// callsubr || callgsubr
		{
			if(stack.length()==0) { consoleLog.accept("error: empty stack");  }
			else {
				int ind = (int)stack.pop();
				ArrayOfInt subr;
				if (v.equals("o10"))
	                subr = getPrivateSubrs(Private, ind);
				else
                  subr = getCFFSubrs(font, ind);
				state.x=x; state.y=y; state.nStems=nStems; state.haveWidth=haveWidth; state.width=width;  state.open=open;
				_drawCFF(subr, state,font,p,Private, consoleLog);
				x=state.x; y=state.y; nStems=state.nStems; haveWidth=state.haveWidth; width=state.width;  open=state.open;
			}
		}
		else if(v.equals("o30") || v.equals("o31"))   // vhcurveto || hvcurveto
		{
			int count, count1 = stack.length();
			int index = 0;
			boolean alternate = (v == "o31");
			
			count  = count1 & ~2;
			index += count1 - count;
			
			while ( index < count ) 
			{
				if(alternate)
				{
					c1x = x + stack.shift();
					c1y = y;
					c2x = c1x + stack.shift();
					c2y = c1y + stack.shift();
					y = c2y + stack.shift();
					if(count-index == 5) {  x = c2x + stack.shift();  index++;  }
					else x = c2x;
					alternate = false;
				}
				else
				{
					c1x = x;
					c1y = y + stack.shift();
					c2x = c1x + stack.shift();
					c2y = c1y + stack.shift();
					x = c2x + stack.shift();
					if(count-index == 5) {  y = c2y + stack.shift();  index++;  }
					else y = c2y;
					alternate = true;
				}
                TyprUP.curveTo(p, c1x, c1y, c2x, c2y, x, y);
				index += 4;
			}
		}
		
		else  {   consoleLog.accept("Unknown operation: "+v + " " + cmds); throw new IllegalArgumentException();  }
	}
	//console.log(cmds);
	state.x=x; state.y=y; state.nStems=nStems; state.haveWidth=haveWidth; state.width=width; state.open=open;
  }
}