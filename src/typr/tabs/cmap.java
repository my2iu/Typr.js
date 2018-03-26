package typr.tabs;

import elemental.client.Browser;
import elemental.html.Uint8Array;
import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import elemental.util.Collections;
import elemental.util.MapFromStringToInt;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import typr.bin;

@JsType(namespace="Typr")
public class cmap
{
  @JsProperty public MapFromStringToInt platformEncodingMap;
  
  @JsProperty public ArrayOf<Table> tables;
  @JsType(isNative=true)
  public static interface Table
  {
    @JsProperty(name="format") public int format();
    
    // parse0
    @JsProperty(name="map") public ArrayOfInt map();
    
    // parse4
    @JsProperty(name="startCount") public ArrayOfInt startCount();
    @JsProperty(name="endCount") public ArrayOfInt endCount();
    @JsProperty(name="idRangeOffset") public ArrayOfInt idRangeOffset();
    @JsProperty(name="glyphIdArray") public ArrayOfInt glyphIdArray();
    @JsProperty(name="idDelta") public ArrayOfInt idDelta();
    
    // parse12
    @JsProperty(name="groups") public ArrayOf<ArrayOfInt> groups();
  }
  
  @JsIgnore public static cmap parse(Uint8Array data, int offset, int length)
  {
	data = Browser.getWindow().newUint8Array(data.getBuffer(), offset, length);
	offset = 0;

	int offset0 = offset;
//	var bin = Typr._bin;
	cmap obj = new cmap();
	char version   = bin.readUshort(data, offset);  offset += 2;
	char numTables = bin.readUshort(data, offset);  offset += 2;
	
	//console.log(version, numTables);
	
	ArrayOfInt offs = Collections.arrayOfInt();
	obj.tables = Collections.arrayOf();
	obj.platformEncodingMap = Collections.mapFromStringToInt();
	
	
	for(int i=0; i<numTables; i++)
	{
		int platformID = bin.readUshort(data, offset);  offset += 2;
		int encodingID = bin.readUshort(data, offset);  offset += 2;
		int noffset = bin.readUint(data, offset);       offset += 4;
		
		String id = "p"+platformID+"e"+encodingID;
		
		//console.log("cmap subtable", platformID, encodingID, noffset);
		
		
		int tind = offs.indexOf(noffset);
		
		if(tind==-1)
		{
			tind = obj.tables.length();
			Table subt = null;
			offs.push(noffset);
			int format = bin.readUshort(data, noffset);
			if     (format== 0) subt = parse0(data, noffset);
			else if(format== 4) subt = parse4(data, noffset);
			else if(format== 6) subt = parse6(data, noffset);
			else if(format==12) subt = parse12(data,noffset);
			else Browser.getWindow().getConsole().log("unknown format: "+format + " "+ platformID+ " "+ encodingID + " " + noffset);
			obj.tables.push(subt);
		}
		
		if(obj.platformEncodingMap.hasKey(id)) throw new IllegalArgumentException("multiple tables for one platform+encoding");
		obj.platformEncodingMap.put(id, tind);
	}
	return obj;
  }

  @JsIgnore public static native Table parse0 (Uint8Array data, int offset)
  /*-{
	var bin = Typr._bin;
	var obj = {};
	obj.format = bin.readUshort(data, offset);  offset += 2;
	var len    = bin.readUshort(data, offset);  offset += 2;
	var lang   = bin.readUshort(data, offset);  offset += 2;
	obj.map = [];
	for(var i=0; i<len-6; i++) obj.map.push(data[offset+i]);
	return obj;
  }-*/;

  @JsIgnore public static native Table parse4 (Uint8Array data, int offset)
  /*-{
	var bin = Typr._bin;
	var offset0 = offset;
	var obj = {};
	
	obj.format = bin.readUshort(data, offset);  offset+=2;
	var length = bin.readUshort(data, offset);  offset+=2;
	var language = bin.readUshort(data, offset);  offset+=2;
	var segCountX2 = bin.readUshort(data, offset);  offset+=2;
	var segCount = segCountX2/2;
	obj.searchRange = bin.readUshort(data, offset);  offset+=2;
	obj.entrySelector = bin.readUshort(data, offset);  offset+=2;
	obj.rangeShift = bin.readUshort(data, offset);  offset+=2;
	obj.endCount   = bin.readUshorts(data, offset, segCount);  offset += segCount*2;
	offset+=2;
	obj.startCount = bin.readUshorts(data, offset, segCount);  offset += segCount*2;
	obj.idDelta = [];
	for(var i=0; i<segCount; i++) {obj.idDelta.push(bin.readShort(data, offset));  offset+=2;}
	obj.idRangeOffset = bin.readUshorts(data, offset, segCount);  offset += segCount*2;
	obj.glyphIdArray = [];
	while(offset< offset0+length) {obj.glyphIdArray.push(bin.readUshort(data, offset));  offset+=2;}
	return obj;
  }-*/;

  @JsIgnore public static native Table parse6 (Uint8Array data, int offset)
  /*-{
	var bin = Typr._bin;
	var offset0 = offset;
	var obj = {};
	
	obj.format = bin.readUshort(data, offset);  offset+=2;
	var length = bin.readUshort(data, offset);  offset+=2;
	var language = bin.readUshort(data, offset);  offset+=2;
	obj.firstCode = bin.readUshort(data, offset);  offset+=2;
	var entryCount = bin.readUshort(data, offset);  offset+=2;
	obj.glyphIdArray = [];
	for(var i=0; i<entryCount; i++) {obj.glyphIdArray.push(bin.readUshort(data, offset));  offset+=2;}
	
	return obj;
  }-*/;

  @JsIgnore public static native Table parse12 (Uint8Array data, int offset)
  /*-{
	var bin = Typr._bin;
	var offset0 = offset;
	var obj = {};
	
	obj.format = bin.readUshort(data, offset);  offset+=2;
	offset += 2;
	var length = bin.readUint(data, offset);  offset+=4;
	var lang   = bin.readUint(data, offset);  offset+=4;
	var nGroups= bin.readUint(data, offset);  offset+=4;
	obj.groups = [];
	
	for(var i=0; i<nGroups; i++)  
	{
		var off = offset + i * 12;
		var startCharCode = bin.readUint(data, off+0);
		var endCharCode   = bin.readUint(data, off+4);
		var startGlyphID  = bin.readUint(data, off+8);
		obj.groups.push([  startCharCode, endCharCode, startGlyphID  ]);
	}
	return obj;
  }-*/;
}