package typr;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.client.Browser;
import elemental.html.CanvasRenderingContext2D;
import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import typr.lctf.LookupTable;
import typr.tabs.GPOSParser.GPOSTab;
import typr.tabs.GPOSParser.MatrixEntry;
import typr.tabs.GPOSParser.PairSet;
import typr.tabs.glyf;

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


  @JsMethod public static native TyprPath glyphToPath (TyprFont font, int gid)
  /*-{
	var path = { cmds:[], crds:[] };
	if(font.SVG && font.SVG.entries[gid]) {
		var p = font.SVG.entries[gid];  if(p==null) return path;
		if(typeof p == "string") {  p = Typr.SVG.toPath(p);  font.SVG.entries[gid]=p;  }
		return p;
	}
	else if(font.CFF) {
	    var Private = font.CFF.Private;
	    if (font.CFF.isCIDFont)
	    {
	      var fdIdx = font.CFF.FDSelect.getFd(gid);
	      var fd = font.CFF.FDArray[fdIdx];
	      Private = fd.Private;
	    }
		var state = {x:0,y:0,stack:[],nStems:0,haveWidth:false,width: Private ? Private.defaultWidthX : 0,open:false};
		Typr.U._drawCFF(font.CFF.CharStrings[gid], state, font.CFF, path, Private);
	}
	else if(font.glyf) {  Typr.U._drawGlyf(gid, font, path);  }
	return path;
  }-*/;

  @JsMethod public static native ArrayOfInt getGlyphDimensions (TyprFont font, int gid)
  /*-{
    if((font.SVG != null && font.SVG.entries[gid]) || font.CFF != null) 
    {
      var path = Typr.U.glyphToPath(font, gid);
      // Calculate some bounds from the path since no bounding box is available
      if (path != null && path.crds.length >= 2)
      {
        var xMin = xMax = path.crds[0]; 
        var yMin = yMax = path.crds[1]; 
        for (var i = 2; i < path.crds.length; i+=2)
        {
          var x = path.crds[i];
          var y = path.crds[i + 1];
          if (x < xMin) xMin = x;
          if (x > xMax) xMax = x;
          if (y < yMin) yMin = y;
          if (y > yMax) yMax = y;
        }
        return [xMin, yMin, xMax, yMax];
      }
    }
    else if(font.glyf != null) 
    {  
      var gl = Typr.U._getGlyf(gid, font);
      if (gl != null)
        return [gl.xMin, gl.yMin, gl.xMax, gl.yMax];
    }
    
	return null;
  }-*/;
  
  @JsMethod public static glyf _getGlyf (int gid, TyprFont font)
  {
    glyf gl = font.glyf.get(gid);
	if(gl==null) 
	{
	  gl = glyf._parseGlyf(font, gid);
	  font.glyf.set(gid, gl);
	}
	return gl;
  }
  @JsMethod public static native JavaScriptObject _drawGlyf (JavaScriptObject gid, JavaScriptObject font, JavaScriptObject path)
  /*-{
	var gl = Typr.U._getGlyf(gid, font);
	if(gl!=null){
		if(gl.noc>-1) Typr.U._simpleGlyph(gl, path);
		else          Typr.U._compoGlyph (gl, font, path);
	}
  }-*/;
  @JsMethod public static native JavaScriptObject _simpleGlyph (JavaScriptObject gl, JavaScriptObject p)
  /*-{
	for(var c=0; c<gl.noc; c++)
	{
		var i0 = (c==0) ? 0 : (gl.endPts[c-1] + 1);
		var il = gl.endPts[c];
		
		for(var i=i0; i<=il; i++)
		{
			var pr = (i==i0)?il:(i-1);
			var nx = (i==il)?i0:(i+1);
			var onCurve = gl.flags[i]&1;
			var prOnCurve = gl.flags[pr]&1;
			var nxOnCurve = gl.flags[nx]&1;
			
			var x = gl.xs[i], y = gl.ys[i];
			
			if(i==i0) { 
				if(onCurve)  
				{
					if(prOnCurve) Typr.U.P.moveTo(p, gl.xs[pr], gl.ys[pr]); 
					else          {  Typr.U.P.moveTo(p,x,y);  continue;  //  will do curveTo at il    
  }
				}
				else        
				{
					if(prOnCurve) Typr.U.P.moveTo(p,  gl.xs[pr],       gl.ys[pr]        );
					else          Typr.U.P.moveTo(p, (gl.xs[pr]+x)/2, (gl.ys[pr]+y)/2   ); 
				}
			}
			if(onCurve)
			{
				if(prOnCurve) Typr.U.P.lineTo(p,x,y);
			}
			else
			{
				if(nxOnCurve) Typr.U.P.qcurveTo(p, x, y, gl.xs[nx], gl.ys[nx]); 
				else          Typr.U.P.qcurveTo(p, x, y, (x+gl.xs[nx])/2, (y+gl.ys[nx])/2); 
			}
		}
		Typr.U.P.closePath(p);
	}
  }-*/;
  @JsMethod public static native JavaScriptObject _compoGlyph (JavaScriptObject gl, JavaScriptObject font, JavaScriptObject p)
  /*-{
	for(var j=0; j<gl.parts.length; j++)
	{
		var path = { cmds:[], crds:[] };
		var prt = gl.parts[j];
		Typr.U._drawGlyf(prt.glyphIndex, font, path);
		
		var m = prt.m;
		for(var i=0; i<path.crds.length; i+=2)
		{
			var x = path.crds[i  ], y = path.crds[i+1];
			p.crds.push(x*m.a + y*m.b + m.tx);
			p.crds.push(x*m.c + y*m.d + m.ty);
		}
		for(var i=0; i<path.cmds.length; i++) p.cmds.push(path.cmds[i]);
	}
  }-*/;


  @JsMethod public static native int _getGlyphClass (int g, ArrayOfInt cd)
  /*-{
	var intr = Typr._lctf.getInterval(cd, g);
	return intr==-1 ? 0 : cd[intr+2];
	//for(var i=0; i<cd.start.length; i++) 
	//	if(cd.start[i]<=g && cd.end[i]>=g) return cd.class[i];
	//return 0;
  }-*/;

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
  @JsIgnore private static native int getPairAdjustmentFromKern (TyprFont font, int g1, int g2)
  /*-{
    if(font.kern != null)
    {
        var ind1 = font.kern.glyph1.indexOf(g1);
        if(ind1!=-1)
        {
            var ind2 = font.kern.rval[ind1].glyph2.indexOf(g2);
            if(ind2!=-1) return font.kern.rval[ind1].vals[ind2];
        }
    }
    
    return 0;
  }-*/;

  
  @JsMethod public static native ArrayOfInt stringToGlyphs (TyprFont font, String str)
  /*-{
	var gls = [];
	for(var i=0; i<str.length; i++) {
		var cc = str.codePointAt(i);  if(cc>0xffff) i++;
		gls.push(Typr.U.codeToGlyph(font, cc));
	}
	//console.log(gls.slice(0));
	
	//console.log(gls);  return gls;
	
	var gsub = font["GSUB"];  if(gsub==null) return gls;
	var llist = gsub.lookupList, flist = gsub.featureList;
	
	var wsep = "\n\t\" ,.:;!?()  ،";
	var R = "آأؤإاةدذرزوٱٲٳٵٶٷڈډڊڋڌڍڎڏڐڑڒړڔڕږڗژڙۀۃۄۅۆۇۈۉۊۋۍۏےۓەۮۯܐܕܖܗܘܙܞܨܪܬܯݍݙݚݛݫݬݱݳݴݸݹࡀࡆࡇࡉࡔࡧࡩࡪࢪࢫࢬࢮࢱࢲࢹૅેૉ૊૎૏ૐ૑૒૝ૡ૤૯஁ஃ஄அஉ஌எஏ஑னப஫஬";
	var L = "ꡲ્૗";
	
	for(var ci=0; ci<gls.length; ci++) {
		var gl = gls[ci];
		
		var slft = ci==0            || wsep.indexOf(str[ci-1])!=-1;
		var srgt = ci==gls.length-1 || wsep.indexOf(str[ci+1])!=-1;
		
		if(!slft && R.indexOf(str[ci-1])!=-1) slft=true;
		if(!srgt && R.indexOf(str[ci  ])!=-1) srgt=true;
		
		if(!srgt && L.indexOf(str[ci+1])!=-1) srgt=true;
		if(!slft && L.indexOf(str[ci  ])!=-1) slft=true;
		
		var feat = null;
		if(slft) feat = srgt ? "isol" : "init";
		else     feat = srgt ? "fina" : "medi";
		
		for(var fi=0; fi<flist.length; fi++)
		{
			if(flist[fi].tag!=feat) continue;
			for(var ti=0; ti<flist[fi].tab.length; ti++)
			{
				var tab = llist[flist[fi].tab[ti]];
				if(tab.ltype!=1) continue;
				Typr.U._applyType1(gls, ci, tab);
			}
		}
	}
	var cligs = ["rlig", "liga", "mset"];
	
	//console.log(gls);
	
	for(var ci=0; ci<gls.length; ci++) {
		var gl = gls[ci];
		var rlim = Math.min(3, gls.length-ci-1);
		for(var fi=0; fi<flist.length; fi++)
		{
			var fl = flist[fi];  if(cligs.indexOf(fl.tag)==-1) continue;
			for(var ti=0; ti<fl.tab.length; ti++)
			{
				var tab = llist[fl.tab[ti]];
				for(var j=0; j<tab.tabs.length; j++)
				{
					if(tab.tabs[j]==null) continue;
					var ind = Typr._lctf.coverageIndex(tab.tabs[j].coverage, gl);  if(ind==-1) continue;  
					//*
					if(tab.ltype==4) {
						var vals = tab.tabs[j].vals[ind];
						
						for(var k=0; k<vals.length; k++) {
							var lig = vals[k], rl = lig.chain.length;  if(rl>rlim) continue;
							var good = true;
							for(var l=0; l<rl; l++) if(lig.chain[l]!=gls[ci+(1+l)]) good=false;
							if(!good) continue;
							gls[ci]=lig.nglyph;
							for(var l=0; l<rl; l++) gls[ci+l+1]=-1;
							//console.log("lig", fl.tag,  gl, lig.chain, lig.nglyph);
						}
					}
					else  if(tab.ltype==5) {
						var ltab = tab.tabs[j];  if(ltab.fmt!=2) continue;
						var cind = Typr._lctf.getInterval(ltab.cDef, gl);
						var cls = ltab.cDef[cind+2], scs = ltab.scset[cls]; 
						for(var i=0; i<scs.length; i++) {
							var sc = scs[i], inp = sc.input;
							if(inp.length>rlim) continue;
							var good = true;
							for(var l=0; l<inp.length; l++) {
								var cind2 = Typr._lctf.getInterval(ltab.cDef, gls[ci+1+l]);
								if(cind==-1 && ltab.cDef[cind2+2]!=inp[l]) {  good=false;  break;  }
							}
							if(!good) continue;
							//console.log(ci, gl);
							var lrs = sc.substLookupRecords;
							for(var k=0; k<lrs.length; k+=2)
							{
								var gi = lrs[k], tabi = lrs[k+1];
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
  }-*/;
  
  @JsMethod public static native JavaScriptObject _applyType1 (JavaScriptObject gls, JavaScriptObject ci, JavaScriptObject tab) /*-{
	var gl = gls[ci];
	for(var j=0; j<tab.tabs.length; j++) {
		var ttab = tab.tabs[j];
		var ind = Typr._lctf.coverageIndex(ttab.coverage,gl);  if(ind==-1) continue;  
		if(ttab.fmt==1) gls[ci] = gls[ci]+ttab.delta;
		else            gls[ci] = ttab.newg[ind];
		//console.log(ci, gl, "subst", flist[fi].tag, i, j, ttab.newg[ind]);
	}
  }-*/;

  @JsMethod public static native ArrayOfInt glyphsToPositions (TyprFont font, ArrayOfInt gls)
  /*-{	
	var pos = [];
	var x = 0;
	pos.push(0);
	
	for(var i=0; i<gls.length; i++)
	{
		var gid = gls[i];  if(gid==-1) continue;
		var gid2 = (i<gls.length-1 && gls[i+1]!=-1)  ? gls[i+1] : 0;
		x += font.hmtx.aWidth[gid];
		if(i<gls.length-1) x += Typr.U.getPairAdjustment(font, gid, gid2);
		pos.push(x);
	}
	return pos;
}-*/;

  @JsMethod(name="glyphsToPathOverloaded") public static TyprPath glyphsToPath (TyprFont font, ArrayOfInt gls)
  {
    return glyphsToPath(font, gls, null);
  }

  @JsMethod public static native TyprPath glyphsToPath (TyprFont font, ArrayOfInt gls, String clr)
  /*-{	
	//gls = gls.reverse();//gls.slice(0,12).concat(gls.slice(12).reverse());
	
	var tpath = {cmds:[], crds:[]};
	var x = 0;
	
	for(var i=0; i<gls.length; i++)
	{
		var gid = gls[i];  if(gid==-1) continue;
		var gid2 = (i<gls.length-1 && gls[i+1]!=-1)  ? gls[i+1] : 0;
		var path = Typr.U.glyphToPath(font, gid);
		for(var j=0; j<path.crds.length; j+=2)
		{
			tpath.crds.push(path.crds[j] + x);
			tpath.crds.push(path.crds[j+1]);
		}
		if(clr) tpath.cmds.push(clr);
		for(var j=0; j<path.cmds.length; j++) tpath.cmds.push(path.cmds[j]);
		if(clr) tpath.cmds.push("X");
		x += font.hmtx.aWidth[gid];// - font.hmtx.lsBearing[gid];
		if(i<gls.length-1) x += Typr.U.getPairAdjustment(font, gid, gid2);
	}
	return tpath;
  }-*/;

  @JsMethod public static native JavaScriptObject pathToSVG (JavaScriptObject path, JavaScriptObject prec)
  /*-{
	if(prec==null) prec = 5;
	var out = [], co = 0, lmap = {"M":2,"L":2,"Q":4,"C":6};
	for(var i=0; i<path.cmds.length; i++)
	{
		var cmd = path.cmds[i], cn = co+(lmap[cmd]?lmap[cmd]:0);  
		out.push(cmd);
		while(co<cn) {  var c = path.crds[co++];  out.push(parseFloat(c.toFixed(prec))+(co==cn?"":" "));  }
	}
	return out.join("");
  }-*/;

  @JsMethod public static native JavaScriptObject pathToContext (TyprPath path, CanvasRenderingContext2D ctx)
  /*-{
	var c = 0, crds = path.crds;
	
	for(var j=0; j<path.cmds.length; j++)
	{
		var cmd = path.cmds[j];
		if     (cmd=="M") {
			ctx.moveTo(crds[c], crds[c+1]);
			c+=2;
		}
		else if(cmd=="L") {
			ctx.lineTo(crds[c], crds[c+1]);
			c+=2;
		}
		else if(cmd=="C") {
			ctx.bezierCurveTo(crds[c], crds[c+1], crds[c+2], crds[c+3], crds[c+4], crds[c+5]);
			c+=6;
		}
		else if(cmd=="Q") {
			ctx.quadraticCurveTo(crds[c], crds[c+1], crds[c+2], crds[c+3]);
			c+=4;
		}
		else if(cmd.charAt(0)=="#") {
			ctx.beginPath();
			ctx.fillStyle = cmd;
		}
		else if(cmd=="Z") {
			ctx.closePath();
		}
		else if(cmd=="X") {
			ctx.fill();
		}
	}
  }-*/;


  @JsType(namespace="Typr.U",name="P")
  public static class TyprUP
  {
    @JsMethod public static void moveTo (TyprPath p, double x, double y)
    {
      p.cmds.push("M");  p.crds.push(x); p.crds.push(y);
    }
    @JsMethod public static void lineTo (TyprPath p, double x, double y)
    {
	p.cmds.push("L");  p.crds.push(x); p.crds.push(y);
    }
    @JsMethod public static void curveTo (TyprPath p, double a,double b,double c,double d,double e,double f)
    {
	p.cmds.push("C");  p.crds.push(a); p.crds.push(b); p.crds.push(c); p.crds.push(d); p.crds.push(e); p.crds.push(f);
    }
    @JsMethod public static void qcurveTo (TyprPath p, double a,double b,double c,double d)
    {
	p.cmds.push("Q");  p.crds.push(a); p.crds.push(b); p.crds.push(c); p.crds.push(d);
    }
    @JsMethod public static void closePath (TyprPath p) {  p.cmds.push("Z");}
  }


  @JsMethod public static native JavaScriptObject _drawCFF (JavaScriptObject cmds, JavaScriptObject state, JavaScriptObject font, JavaScriptObject p, JavaScriptObject Private)
  /*-{
	var stack = state.stack;
	var nStems = state.nStems, haveWidth=state.haveWidth, width=state.width, open=state.open;
	var i=0;
	var x=state.x, y=state.y, c1x=0, c1y=0, c2x=0, c2y=0, c3x=0, c3y=0, c4x=0, c4y=0, jpx=0, jpy=0;
	
	var o = {val:0,size:0};
	//console.log(cmds);
	while(i<cmds.length)
	{
		Typr.CFF.getCharString(cmds, i, o);
		var v = o.val;
		i += o.size;
			
		if(false) {}
		else if(v=="o1" || v=="o18")  //  hstem || hstemhm
		{
			var hasWidthArg;

			// The number of stem operators on the stack is always even.
			// If the value is uneven, that means a width is specified.
			hasWidthArg = stack.length % 2 !== 0;
			if (hasWidthArg && !haveWidth) {
				width = stack.shift() + Private.nominalWidthX;
			}

			nStems += stack.length >> 1;
			stack.length = 0;
			haveWidth = true;
		}
		else if(v=="o3" || v=="o23")  // vstem || vstemhm
		{
			var hasWidthArg;

			// The number of stem operators on the stack is always even.
			// If the value is uneven, that means a width is specified.
			hasWidthArg = stack.length % 2 !== 0;
			if (hasWidthArg && !haveWidth) {
				width = stack.shift() + Private.nominalWidthX;
			}

			nStems += stack.length >> 1;
			stack.length = 0;
			haveWidth = true;
		}
		else if(v=="o4")
		{
			if (stack.length > 1 && !haveWidth) {
                        width = stack.shift() + Private.nominalWidthX;
                        haveWidth = true;
                    }
			if(open) Typr.U.P.closePath(p);

                    y += stack.pop();
					Typr.U.P.moveTo(p,x,y);   open=true;
		}
		else if(v=="o5")
		{
			while (stack.length > 0) {
                        x += stack.shift();
                        y += stack.shift();
                        Typr.U.P.lineTo(p, x, y);
                    }
		}
		else if(v=="o6" || v=="o7")  // hlineto || vlineto
		{
			var count = stack.length;
			var isX = (v == "o6");
			
			for(var j=0; j<count; j++) {
				var sval = stack.shift();
				
				if(isX) x += sval;  else  y += sval;
				isX = !isX;
				Typr.U.P.lineTo(p, x, y);
			}
		}
		else if(v=="o8" || v=="o24")	// rrcurveto || rcurveline
		{
			var count = stack.length;
			var index = 0;
			while(index+6 <= count) {
				c1x = x + stack.shift();
				c1y = y + stack.shift();
				c2x = c1x + stack.shift();
				c2y = c1y + stack.shift();
				x = c2x + stack.shift();
				y = c2y + stack.shift();
				Typr.U.P.curveTo(p, c1x, c1y, c2x, c2y, x, y);
				index+=6;
			}
			if(v=="o24")
			{
				x += stack.shift();
				y += stack.shift();
				Typr.U.P.lineTo(p, x, y);
			}
		}
		else if(v=="o11")  break;
		else if(v=="o1234" || v=="o1235" || v=="o1236" || v=="o1237")//if((v+"").slice(0,3)=="o12")
		{
			if(v=="o1234")
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
				Typr.U.P.curveTo(p, c1x, c1y, c2x, c2y, jpx, jpy);
				Typr.U.P.curveTo(p, c3x, c3y, c4x, c4y, x, y);
				
			}
			if(v=="o1235")
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
				Typr.U.P.curveTo(p, c1x, c1y, c2x, c2y, jpx, jpy);
				Typr.U.P.curveTo(p, c3x, c3y, c4x, c4y, x, y);
			}
			if(v=="o1236")
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
				Typr.U.P.curveTo(p, c1x, c1y, c2x, c2y, jpx, jpy);
				Typr.U.P.curveTo(p, c3x, c3y, c4x, c4y, x, y);
			}
			if(v=="o1237")
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
				Typr.U.P.curveTo(p, c1x, c1y, c2x, c2y, jpx, jpy);
				Typr.U.P.curveTo(p, c3x, c3y, c4x, c4y, x, y);
			}
		}
		else if(v=="o14")
		{
			if (stack.length > 0 && !haveWidth) {
                        width = stack.shift() + font.nominalWidthX;
                        haveWidth = true;
                    }
			if(stack.length==4) // seac = standard encoding accented character
			{
			
				var asb = 0;
				var adx = stack.shift();
				var ady = stack.shift();
				var bchar = stack.shift();
				var achar = stack.shift();
			
				
				var bind = Typr.CFF.glyphBySE(font, bchar);
				var aind = Typr.CFF.glyphBySE(font, achar);
				
				//console.log(bchar, bind);
				//console.log(achar, aind);
				//state.x=x; state.y=y; state.nStems=nStems; state.haveWidth=haveWidth; state.width=width;  state.open=open;
				
				Typr.U._drawCFF(font.CharStrings[bind], state,font,p,Private);
				state.x = adx; state.y = ady;
				Typr.U._drawCFF(font.CharStrings[aind], state,font,p,Private);
				
				//x=state.x; y=state.y; nStems=state.nStems; haveWidth=state.haveWidth; width=state.width;  open=state.open;
			}
			if(open) {  Typr.U.P.closePath(p);  open=false;  }
		}		
		else if(v=="o19" || v=="o20") 
		{ 
			var hasWidthArg;

			// The number of stem operators on the stack is always even.
			// If the value is uneven, that means a width is specified.
			hasWidthArg = stack.length % 2 !== 0;
			if (hasWidthArg && !haveWidth) {
				width = stack.shift() + Private.nominalWidthX;
			}

			nStems += stack.length >> 1;
			stack.length = 0;
			haveWidth = true;
			
			i += (nStems + 7) >> 3;
		}
		
		else if(v=="o21") {
			if (stack.length > 2 && !haveWidth) {
                        width = stack.shift() + Private.nominalWidthX;
                        haveWidth = true;
                    }

                    y += stack.pop();
                    x += stack.pop();
					
					if(open) Typr.U.P.closePath(p);
                    Typr.U.P.moveTo(p,x,y);   open=true;
		}
		else if(v=="o22")
		{
			 if (stack.length > 1 && !haveWidth) {
                        width = stack.shift() + Private.nominalWidthX;
                        haveWidth = true;
                    }
					
                    x += stack.pop();
					
					if(open) Typr.U.P.closePath(p);
					Typr.U.P.moveTo(p,x,y);   open=true;                    
		}
		else if(v=="o25")
		{
			while (stack.length > 6) {
                        x += stack.shift();
                        y += stack.shift();
                        Typr.U.P.lineTo(p, x, y);
                    }

                    c1x = x + stack.shift();
                    c1y = y + stack.shift();
                    c2x = c1x + stack.shift();
                    c2y = c1y + stack.shift();
                    x = c2x + stack.shift();
                    y = c2y + stack.shift();
                    Typr.U.P.curveTo(p, c1x, c1y, c2x, c2y, x, y);
		}
		else if(v=="o26") 
		{
			if (stack.length % 2) {
                        x += stack.shift();
                    }

                    while (stack.length > 0) {
                        c1x = x;
                        c1y = y + stack.shift();
                        c2x = c1x + stack.shift();
                        c2y = c1y + stack.shift();
                        x = c2x;
                        y = c2y + stack.shift();
                        Typr.U.P.curveTo(p, c1x, c1y, c2x, c2y, x, y);
                    }

		}
		else if(v=="o27")
		{
			if (stack.length % 2) {
                        y += stack.shift();
                    }

                    while (stack.length > 0) {
                        c1x = x + stack.shift();
                        c1y = y;
                        c2x = c1x + stack.shift();
                        c2y = c1y + stack.shift();
                        x = c2x + stack.shift();
                        y = c2y;
                        Typr.U.P.curveTo(p, c1x, c1y, c2x, c2y, x, y);
                    }
		}
		else if(v=="o10" || v=="o29")	// callsubr || callgsubr
		{
			var obj = (v=="o10" ? Private : font);
			if(stack.length==0) { console.log("error: empty stack");  }
			else {
				var ind = stack.pop();
				var subr = obj.Subrs[ ind + obj.Bias ];
				state.x=x; state.y=y; state.nStems=nStems; state.haveWidth=haveWidth; state.width=width;  state.open=open;
				Typr.U._drawCFF(subr, state,font,p,Private);
				x=state.x; y=state.y; nStems=state.nStems; haveWidth=state.haveWidth; width=state.width;  open=state.open;
			}
		}
		else if(v=="o30" || v=="o31")   // vhcurveto || hvcurveto
		{
			var count, count1 = stack.length;
			var index = 0;
			var alternate = v == "o31";
			
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
                Typr.U.P.curveTo(p, c1x, c1y, c2x, c2y, x, y);
				index += 4;
			}
		}
		
		else if((v+"").charAt(0)=="o") {   console.log("Unknown operation: "+v, cmds); throw v;  }
		else stack.push(v);
	}
	//console.log(cmds);
	state.x=x; state.y=y; state.nStems=nStems; state.haveWidth=haveWidth; state.width=width; state.open=open;
  }-*/;
}