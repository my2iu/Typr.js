package typr.tabs;

import com.google.gwt.core.client.JavaScriptObject;

import elemental.client.Browser;
import elemental.html.Uint8Array;
import elemental.util.Collections;
import elemental.util.MapFromIntTo;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import typr.bin;

@JsType(namespace="Typr")
public class SVG
{
  @JsProperty public MapFromIntTo<String> entries;
  
  @JsIgnore public static SVG parse (Uint8Array data, int offset, int length)
  {
//	var bin = Typr._bin;
	SVG obj = new SVG();
	obj.entries = Collections.mapFromIntTo();

	int offset0 = offset;

	char tableVersion = bin.readUshort(data, offset);	offset += 2;
	int svgDocIndexOffset = bin.readUint(data, offset);	offset += 4;
	int reserved = bin.readUint(data, offset); offset += 4;

	offset = svgDocIndexOffset + offset0;

	char numEntries = bin.readUshort(data, offset);	offset += 2;

	for(int i=0; i<numEntries; i++)
	{
		char startGlyphID = bin.readUshort(data, offset);  offset += 2;
		char endGlyphID   = bin.readUshort(data, offset);  offset += 2;
		int svgDocOffset = bin.readUint  (data, offset);  offset += 4;
		int svgDocLength = bin.readUint  (data, offset);  offset += 4;

		Uint8Array sbuf = Browser.getWindow().newUint8Array(data.getBuffer(), offset0 + svgDocOffset + svgDocIndexOffset, svgDocLength);
		String svg = bin.readUTF8(sbuf, 0, sbuf.length());
		
		for(int f=startGlyphID; f<=endGlyphID; f++) {
			obj.entries.put(f, svg);
		}
	}
	return obj;
  }

  @JsMethod public static native JavaScriptObject toPath (String str)
  /*-{
	var pth = {cmds:[], crds:[]};
	if(str==null) return pth;
	
	var prsr = new DOMParser();
	var doc = prsr["parseFromString"](str,"image/svg+xml");
	
	var svg = doc.firstChild;  while(svg.tagName!="svg") svg = svg.nextSibling;
	var vb = svg.getAttribute("viewBox");
	if(vb) vb = vb.trim().split(" ").map(parseFloat);  else   vb = [0,0,1000,1000];
	Typr.SVG._toPath(svg.children, pth);
	for(var i=0; i<pth.crds.length; i+=2) {
		var x = pth.crds[i], y = pth.crds[i+1];
		x -= vb[0];
		y -= vb[1];
		y = -y;
		pth.crds[i] = x;
		pth.crds[i+1] = y;
	}
	return pth;
}-*/;

  @JsMethod public static native JavaScriptObject _toPath (JavaScriptObject nds, JavaScriptObject pth, JavaScriptObject fill) /*-{
	for(var ni=0; ni<nds.length; ni++) {
		var nd = nds[ni], tn = nd.tagName;
		var cfl = nd.getAttribute("fill");  if(cfl==null) cfl = fill;
		if(tn=="g") Typr.SVG._toPath(nd.children, pth, cfl);
		else if(tn=="path") {
			pth.cmds.push(cfl?cfl:"#000000");
			var d = nd.getAttribute("d");  //console.log(d);
			var toks = Typr.SVG._tokens(d);  //console.log(toks);
			Typr.SVG._toksToPath(toks, pth);  pth.cmds.push("X");
		}
		else if(tn=="defs") {}
		else console.log(tn, nd);
	}
}-*/;

  @JsMethod public static native JavaScriptObject _tokens (JavaScriptObject d) /*-{
	var ts = [], off = 0, rn=false, cn="";  // reading number, current number
	while(off<d.length){
		var cc=d.charCodeAt(off), ch = d.charAt(off);  off++;
		var isNum = (48<=cc && cc<=57) || ch=="." || ch=="-";
		
		if(rn) {
			if(ch=="-") {  ts.push(parseFloat(cn));  cn=ch;  }
			else if(isNum) cn+=ch;
			else {  ts.push(parseFloat(cn));  if(ch!="," && ch!=" ") ts.push(ch);  rn=false;  }
		}
		else {
			if(isNum) {  cn=ch;  rn=true;  }
			else if(ch!="," && ch!=" ") ts.push(ch);
		}
	}
	if(rn) ts.push(parseFloat(cn));
	return ts;
}-*/;

  @JsMethod public static native JavaScriptObject _toksToPath (JavaScriptObject ts, JavaScriptObject pth) /*-{	
	var i = 0, x = 0, y = 0, ox = 0, oy = 0;
	var pc = {"M":2,"L":2,"H":1,"V":1,   "S":4,   "C":6};
	var cmds = pth.cmds, crds = pth.crds;
	
	while(i<ts.length) {
		var cmd = ts[i];  i++;
		
		if(cmd=="z") {  cmds.push("Z");  x=ox;  y=oy;  }
		else {
			var cmu = cmd.toUpperCase();
			var ps = pc[cmu], reps = Typr.SVG._reps(ts, i, ps);
		
			for(var j=0; j<reps; j++) {
				var xi = 0, yi = 0;   if(cmd!=cmu) {  xi=x;  yi=y;  }
				
				if(false) {}
				else if(cmu=="M") {  x = xi+ts[i++];  y = yi+ts[i++];  cmds.push("M");  crds.push(x,y);  ox=x;  oy=y; }
				else if(cmu=="L") {  x = xi+ts[i++];  y = yi+ts[i++];  cmds.push("L");  crds.push(x,y);  }
				else if(cmu=="H") {  x = xi+ts[i++];                   cmds.push("L");  crds.push(x,y);  }
				else if(cmu=="V") {  y = yi+ts[i++];                   cmds.push("L");  crds.push(x,y);  }
				else if(cmu=="C") {
					var x1=xi+ts[i++], y1=yi+ts[i++], x2=xi+ts[i++], y2=yi+ts[i++], x3=xi+ts[i++], y3=yi+ts[i++];
					cmds.push("C");  crds.push(x1,y1,x2,y2,x3,y3);  x=x3;  y=y3;
				}
				else if(cmu=="S") {
					var co = Math.max(crds.length-4, 0);
					var x1 = x+x-crds[co], y1 = y+y-crds[co+1];
					var x2=xi+ts[i++], y2=yi+ts[i++], x3=xi+ts[i++], y3=yi+ts[i++];  
					cmds.push("C");  crds.push(x1,y1,x2,y2,x3,y3);  x=x3;  y=y3;
				}
				else console.log("Unknown SVG command "+cmd);
			}
		}
	}
}-*/;
  @JsMethod public static native JavaScriptObject _reps (JavaScriptObject ts, JavaScriptObject off, JavaScriptObject ps) /*-{
	var i = off;
	while(i<ts.length) {  if((typeof ts[i]) == "string") break;  i+=ps;  }
	return (i-off)/ps;
}-*/;
}