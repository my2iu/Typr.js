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

public class cmap
{
  @JsProperty public MapFromStringToInt platformEncodingMap;
  
  @JsProperty public ArrayOf<Table> tables;
  public static class Table
  {
    @JsProperty(name="format") public int format;
    
    // parse0
    @JsProperty(name="map") public ArrayOfInt map;
    
    // parse4
    @JsProperty(name="startCount") public ArrayOfInt startCount;
    @JsProperty(name="endCount") public ArrayOfInt endCount;
    @JsProperty(name="idRangeOffset") public ArrayOfInt idRangeOffset;
    @JsProperty(name="glyphIdArray") public ArrayOfInt glyphIdArray;
    @JsProperty(name="idDelta") public ArrayOfInt idDelta;
    @JsProperty(name="searchRange") public char searchRange;
    @JsProperty(name="entrySelector") public char entrySelector;
    @JsProperty(name="rangeShift") public char rangeShift;

    // parse6
    @JsProperty(name="firstCode") public char firstCode;
    
    // parse12
    @JsProperty(name="groups") public ArrayOf<ArrayOfInt> groups;
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
		
		if(obj.platformEncodingMap.hasKey(id)) throw new IllegalArgumentException("multiple tables for one platform+encoding: " + id);
		obj.platformEncodingMap.put(id, tind);
	}
	return obj;
  }

  @JsIgnore public static Table parse0 (Uint8Array data, int offset)
  {
//	var bin = Typr._bin;
	Table obj = new Table();
    obj.format = bin.readUshort(data, offset);  offset += 2;
	char len    = bin.readUshort(data, offset);  offset += 2;
	char lang   = bin.readUshort(data, offset);  offset += 2;
	obj.map = Collections.arrayOfInt();
	for(int i=0; i<len-6; i++) obj.map.push(data.intAt(offset+i));
	return obj;
  }

  @JsIgnore public static Table parse4 (Uint8Array data, int offset)
  {
//	var bin = Typr._bin;
	int offset0 = offset;
    Table obj = new Table();
	
    obj.format = bin.readUshort(data, offset);  offset+=2;
	char length = bin.readUshort(data, offset);  offset+=2;
	char language = bin.readUshort(data, offset);  offset+=2;
	char segCountX2 = bin.readUshort(data, offset);  offset+=2;
	int segCount = segCountX2/2;
    obj.searchRange = bin.readUshort(data, offset);  offset+=2;
    obj.entrySelector = bin.readUshort(data, offset);  offset+=2;
    obj.rangeShift = bin.readUshort(data, offset);  offset+=2;
    obj.endCount   = bin.readUshorts(data, offset, segCount);  offset += segCount*2;
	offset+=2;
    obj.startCount = bin.readUshorts(data, offset, segCount);  offset += segCount*2;
	obj.idDelta = Collections.arrayOfInt();
	for(int i=0; i<segCount; i++) {obj.idDelta.push(bin.readShort(data, offset));  offset+=2;}
    obj.idRangeOffset = bin.readUshorts(data, offset, segCount);  offset += segCount*2;
	obj.glyphIdArray = Collections.arrayOfInt();
	while(offset< offset0+length) {obj.glyphIdArray.push(bin.readUshort(data, offset));  offset+=2;}
	return obj;
  }

  @JsIgnore public static Table parse6 (Uint8Array data, int offset)
  {
//	var bin = Typr._bin;
	int offset0 = offset;
    Table obj = new Table();
	
    obj.format = bin.readUshort(data, offset);  offset+=2;
	char length = bin.readUshort(data, offset);  offset+=2;
	char language = bin.readUshort(data, offset);  offset+=2;
    obj.firstCode = bin.readUshort(data, offset);  offset+=2;
	char entryCount = bin.readUshort(data, offset);  offset+=2;
	obj.glyphIdArray = Collections.arrayOfInt();
	for(int i=0; i<entryCount; i++) {obj.glyphIdArray.push(bin.readUshort(data, offset));  offset+=2;}
	
	return obj;
  }

  @JsIgnore public static Table parse12 (Uint8Array data, int offset)
  {
//	var bin = Typr._bin;
	int offset0 = offset;
    Table obj = new Table();
	
    obj.format = bin.readUshort(data, offset);  offset+=2;
	offset += 2;
	int length = bin.readUint(data, offset);  offset+=4;
	int lang   = bin.readUint(data, offset);  offset+=4;
	int nGroups= bin.readUint(data, offset);  offset+=4;
	obj.groups = Collections.arrayOf();
	
	for(int i=0; i<nGroups; i++)  
	{
		int off = offset + i * 12;
		int startCharCode = bin.readUint(data, off+0);
		int endCharCode   = bin.readUint(data, off+4);
		int startGlyphID  = bin.readUint(data, off+8);
		ArrayOfInt entry = Collections.arrayOfInt();
		entry.push(startCharCode);
		entry.push(endCharCode);
		entry.push(startGlyphID);
		obj.groups.push(entry);
	}
	return obj;
  }
}