package typr.tabs;

import elemental.html.Uint8Array;
import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import elemental.util.Collections;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import typr.TyprFont;
import typr.bin;

public class glyf
{
  @JsIgnore public static ArrayOf<glyf> parse (Uint8Array data, int offset, int length, TyprFont font)
  {
	ArrayOf<glyf> obj = Collections.arrayOf(); 
	for(int g=0; g<(int)font.maxp.numGlyphs; g++) obj.push(null);
	return obj;
  }

  @JsProperty public short noc;
  @JsProperty public short xMin;
  @JsProperty public short yMin;
  @JsProperty public short xMax;
  @JsProperty public short yMax;
  @JsProperty public ArrayOfInt endPts;
  @JsProperty public ArrayOfInt instructions;
  @JsProperty public ArrayOfInt flags;
  @JsProperty public ArrayOfInt ys;
  @JsProperty public ArrayOfInt xs;
  @JsProperty public ArrayOfInt instr;
  @JsProperty public ArrayOf<Part> parts;

  
  static class PartInternal
  {
    @JsProperty public double a=1;
    @JsProperty public double b=0;
    @JsProperty public double c=0;
    @JsProperty public double d=1;
    @JsProperty public int tx=0;
    @JsProperty public int ty=0;
  }
  
  static class Part
  {
    @JsProperty public PartInternal m = new PartInternal();
    @JsProperty public int p1 = -1;
    @JsProperty public int p2 = -1;
    @JsProperty public char glyphIndex;
  }

  
  @JsIgnore public static glyf _parseGlyf (TyprFont font, int g)
  {
//	var bin = Typr._bin;
	Uint8Array data = font._rawGlyfTableData;
	
	int offset = font.loca.get(g);
		
	if(font.loca.get(g)==font.loca.get(g+1)) return null;
		
	glyf gl = new glyf();
		
	gl.noc  = bin.readShort(data, offset);  offset+=2;		// number of contours
	gl.xMin = bin.readShort(data, offset);  offset+=2;
	gl.yMin = bin.readShort(data, offset);  offset+=2;
	gl.xMax = bin.readShort(data, offset);  offset+=2;
	gl.yMax = bin.readShort(data, offset);  offset+=2;
	
	if(gl.xMin>=gl.xMax || gl.yMin>=gl.yMax) return null;
		
	if(gl.noc>0)
	{
		gl.endPts = Collections.arrayOfInt();
		for(int i=0; i<gl.noc; i++) { gl.endPts.push(bin.readUshort(data,offset)); offset+=2; }
		
		char instructionLength = bin.readUshort(data,offset); offset+=2;
		if((data.getByteLength()-offset)<instructionLength) return null;
		gl.instructions = bin.readBytes(data, offset, instructionLength);   offset+=instructionLength;
		
		int crdnum = gl.endPts.get(gl.noc-1)+1;
		gl.flags = Collections.arrayOfInt();
		for(int i=0; i<crdnum; i++ ) 
		{ 
			int flag = data.intAt(offset);  offset++; 
			gl.flags.push(flag); 
			if((flag&8)!=0)
			{
				int rep = data.intAt(offset);  offset++;
				for(int j=0; j<rep; j++) { gl.flags.push(flag); i++; }
			}
		}
		gl.xs = Collections.arrayOfInt();
		for(int i=0; i<crdnum; i++) {
			boolean i8=((gl.flags.get(i)&2)!=0), same=((gl.flags.get(i)&16)!=0);  
			if(i8) { gl.xs.push(same ? data.intAt(offset) : -data.intAt(offset));  offset++; }
			else
			{
				if(same) gl.xs.push(0);
				else { gl.xs.push(bin.readShort(data, offset));  offset+=2; }
			}
		}
		gl.ys = Collections.arrayOfInt();
		for(int i=0; i<crdnum; i++) {
			boolean i8=((gl.flags.get(i)&4)!=0), same=((gl.flags.get(i)&32)!=0);  
			if(i8) { gl.ys.push(same ? data.intAt(offset) : -data.intAt(offset));  offset++; }
			else
			{
				if(same) gl.ys.push(0);
				else { gl.ys.push(bin.readShort(data, offset));  offset+=2; }
			}
		}
		int x = 0, y = 0;
		for(int i=0; i<crdnum; i++) { x += gl.xs.get(i); y += gl.ys.get(i);  gl.xs.set(i, x);  gl.ys.set(i, y); }
		//console.log(endPtsOfContours, instructionLength, instructions, flags, xCoordinates, yCoordinates);
	}
	else
	{
	    char ARG_1_AND_2_ARE_WORDS	= 1<<0;
	    char ARGS_ARE_XY_VALUES		= 1<<1;
	    char ROUND_XY_TO_GRID		= 1<<2;
	    char WE_HAVE_A_SCALE			= 1<<3;
	    char RESERVED				= 1<<4;
	    char MORE_COMPONENTS			= 1<<5;
	    char WE_HAVE_AN_X_AND_Y_SCALE= 1<<6;
	    char WE_HAVE_A_TWO_BY_TWO	= 1<<7;
	    char WE_HAVE_INSTRUCTIONS	= 1<<8;
	    char USE_MY_METRICS			= 1<<9;
	    char OVERLAP_COMPOUND		= 1<<10;
	    char SCALED_COMPONENT_OFFSET	= 1<<11;
	    char UNSCALED_COMPONENT_OFFSET	= 1<<12;
		
		gl.parts = Collections.arrayOf();
		char flags;
		do {
			flags = bin.readUshort(data, offset);  offset += 2;
			Part part = new Part();  gl.parts.push(part);
			part.glyphIndex = bin.readUshort(data, offset);  offset += 2;
			short arg1, arg2;
			if ( (flags & ARG_1_AND_2_ARE_WORDS) != 0) {
				arg1 = bin.readShort(data, offset);  offset += 2;
				arg2 = bin.readShort(data, offset);  offset += 2;
			} else {
				arg1 = bin.readInt8(data, offset);  offset ++;
				arg2 = bin.readInt8(data, offset);  offset ++;
			}
			
			if((flags & ARGS_ARE_XY_VALUES) != 0) { part.m.tx = arg1;  part.m.ty = arg2; }
			else  {  part.p1=arg1;  part.p2=arg2;  }
			//part.m.tx = arg1;  part.m.ty = arg2;
			//else { throw "params are not XY values"; }
			
			if ( (flags & WE_HAVE_A_SCALE) != 0 ) {
				part.m.a = part.m.d = bin.readF2dot14(data, offset);  offset += 2;    
			} else if ( (flags & WE_HAVE_AN_X_AND_Y_SCALE) != 0 ) {
				part.m.a = bin.readF2dot14(data, offset);  offset += 2; 
				part.m.d = bin.readF2dot14(data, offset);  offset += 2; 
			} else if ( (flags & WE_HAVE_A_TWO_BY_TWO) != 0 ) {
				part.m.a = bin.readF2dot14(data, offset);  offset += 2; 
				part.m.b = bin.readF2dot14(data, offset);  offset += 2; 
				part.m.c = bin.readF2dot14(data, offset);  offset += 2; 
				part.m.d = bin.readF2dot14(data, offset);  offset += 2; 
			}
		} while ( (flags & MORE_COMPONENTS) != 0 ); 
		if ( (flags & WE_HAVE_INSTRUCTIONS) != 0){
			char numInstr = bin.readUshort(data, offset);  offset += 2;
			gl.instr = Collections.arrayOfInt();
			for(int i=0; i<numInstr; i++) { gl.instr.push(data.intAt(offset));  offset++; }
		}
	}
	return gl;
  }
}