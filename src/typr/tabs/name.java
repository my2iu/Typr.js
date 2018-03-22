package typr.tabs;

import elemental.html.Uint8Array;
import elemental.util.Collections;
import elemental.util.MapFromIntTo;
import elemental.util.MapFromIntToString;
import elemental.util.MapFromStringTo;
import jsinterop.annotations.JsIgnore;
import typr.bin;

public class name
{
  MapFromStringTo<MapFromIntTo<MapFromIntToString>> platformLangNameMap = Collections.mapFromStringTo();
  
  public static enum NameType
  {
    COPYRIGHT, FONT_FAMILY, FONT_SUBFAMILY, UNIQUE_FONT_IDENTIFIER, FULL_FONT_NAME,
    VERSION, POSTSCRIPT_NAME, TRADEMARK, MANUFACTURER, DESIGNER, DESCRIPTION, 
    VENDOR_URL, DESIGNER_URL, LICENSE, LICENSE_URL, RESERVED, 
    TYPOGRAPHIC_FAMILY, TYPOGRAPHIC_SUBFAMILY,  
    COMPATIBLE_FULL_NAME, SAMPLE_TEXT, 
    // OpenType stuff
    POSTSCRIPT_CID_FINDFONT_NAME, WWS_FAMILY_NAME, WWS_SUBFAMILY,
    LIGHT_BACKGROUND_PALETTE, DARK_BACKGROUND_PALETTE, VARIATIONS_POSTSCRIPT_NAME_PREFIX
  }
  
  public MapFromIntToString getEnglishNames()
  {
    if (platformLangNameMap.hasKey("p3"))
    {
      MapFromIntTo<MapFromIntToString> langNameMap = platformLangNameMap.get("p3");
      if (langNameMap.hasKey(0x409))  // US English
        return langNameMap.get(0x409);
      if (langNameMap.hasKey(0x809))  // UK English
        return langNameMap.get(0x809);
    }
    if (platformLangNameMap.hasKey("p1"))
    {
      MapFromIntTo<MapFromIntToString> langNameMap = platformLangNameMap.get("p1");
      if (langNameMap.hasKey(0))  // English
        return langNameMap.get(0);
    }
    
    if (platformLangNameMap.hasKey("p0"))
    {
      MapFromIntTo<MapFromIntToString> langNameMap = platformLangNameMap.get("p1");
      if (langNameMap.hasKey(0))  // Default
        return langNameMap.get(0);
      if (langNameMap.hasKey(0xffff))  // Default
        return langNameMap.get(0xffff);
    }
    
    // Just grab something
    return platformLangNameMap.values().get(0).values().get(0);
//    if (platformLangNameMap.hasKey("p"))
//    if (platformLangNameMap.hasKey("p0"))
//    {
//      // Unicode stuff has generic encoding
//      if ()
//    }
//  for(var p in obj) if(obj[p].postScriptName!=null && obj[p]._lang==0x0409) return obj[p];        // United States
//  for(var p in obj) if(obj[p].postScriptName!=null && obj[p]._lang==0x0c0c) return obj[p];        // Canada
//  for(var p in obj) if(obj[p].postScriptName!=null) return obj[p];
//  
//  var tname;
//  for(var p in obj) { tname=p; break; }
//  console.log("returning name table with languageID "+ obj[tname]._lang);
  }
  
  @JsIgnore public static name parse (Uint8Array data, int offset, int tabLength)
  {
//	var bin = Typr._bin;
    name obj = new name();
    int tableStartOffset = offset;
	char format = bin.readUshort(data, offset);  offset += 2;
	char count  = bin.readUshort(data, offset);  offset += 2;
	char stringOffset = bin.readUshort(data, offset);  offset += 2;
	
	
	//console.log(format, count);
	
	int offset0 = offset;
	
	for(int i=0; i<count; i++)
	{
		int platformID = bin.readUshort(data, offset);  offset += 2;
		int encodingID = bin.readUshort(data, offset);  offset += 2;
		int languageID = bin.readUshort(data, offset);  offset += 2;
		int nameID     = bin.readUshort(data, offset);  offset += 2;
		char length     = bin.readUshort(data, offset);  offset += 2;
		char noffset    = bin.readUshort(data, offset);  offset += 2;
		//console.log(platformID, encodingID, languageID.toString(16), nameID, length, noffset);
		
		String plat = "p"+(int)platformID;//Typr._platforms[platformID];
		if(!obj.platformLangNameMap.hasKey(plat)) obj.platformLangNameMap.put(plat, Collections.<MapFromIntToString>mapFromIntTo());

		if (!obj.platformLangNameMap.get(plat).hasKey((int)languageID)) obj.platformLangNameMap.get(plat).put((int)languageID, Collections.mapFromIntToString());
		
//		String [] names = new String[] {
//			"copyright",
//			"fontFamily",
//			"fontSubfamily",
//			"ID",
//			"fullName",
//			"version",
//			"postScriptName",
//			"trademark",
//			"manufacturer",
//			"designer",
//			"description",
//			"urlVendor",
//			"urlDesigner",
//			"licence",
//			"licenceURL",
//			"---",
//			"typoFamilyName",
//			"typoSubfamilyName",
//			"compatibleFull",
//			"sampleText",
//			"postScriptCID",
//			"wwsFamilyName",
//			"wwsSubfamilyName",
//			"lightPalette",
//			"darkPalette"
//		};
//		String cname = names[nameID];
		int soff = tableStartOffset + stringOffset + noffset;
		String str = null;
		if(platformID == 0)
		{
		  str = bin.readUnicode(data, soff, length/2);
		}
		else if (platformID == 3) // windows
		{
	        if(encodingID == 0 ) str = bin.readUnicode(data, soff, length/2);
	        else if (encodingID == 1) str = bin.readUnicode(data, soff, length/2); 
	        else
	          System.out.println(encodingID);
		}
		else if (platformID == 1) // Mac
		{
		  // Mac has special encodings for each language--just use ASCII for the Roman encoding
	        if(encodingID == 0) str = bin.readASCII  (data, soff, length);
	        else // Just assume ASCII (we'll hopefully find an entry that we can understand elsewhere)
	          { str = bin.readASCII(data, soff, length);  log("reading unknown MAC encoding "+encodingID+" as ASCII"); }
		}
		if (str == null)
		  continue;  // Unknown encoding
//		else if(encodingID == 0) str = bin.readASCII  (data, soff, length);
//		else if(encodingID == 1) str = bin.readUnicode(data, soff, length/2);
//		else if(encodingID == 3) str = bin.readUnicode(data, soff, length/2);
//		
//		else if(platformID == 1) { str = bin.readASCII(data, soff, length);  log("reading unknown MAC encoding "+encodingID+" as ASCII"); }
//		else throw new IllegalArgumentException("unknown encoding "+encodingID + ", platformID: "+platformID);

		obj.platformLangNameMap.get(plat).get((int)languageID).put(nameID, str);
//		obj[plat][cname] = str;
//		obj[plat]._lang = languageID;
	}
	if(format == 1)
	{
		char langTagCount = bin.readUshort(data, offset);  offset += 2;
		for(int i=0; i<langTagCount; i++)
		{
			char length  = bin.readUshort(data, offset);  offset += 2;
			char noffset = bin.readUshort(data, offset);  offset += 2;
		}
	}
	
	//console.log(obj);
	
//	for(var p in obj) if(obj[p].postScriptName!=null && obj[p]._lang==0x0409) return obj[p];		// United States
//	for(var p in obj) if(obj[p].postScriptName!=null && obj[p]._lang==0x0c0c) return obj[p];		// Canada
//	for(var p in obj) if(obj[p].postScriptName!=null) return obj[p];
//	
//	var tname;
//	for(var p in obj) { tname=p; break; }
//	console.log("returning name table with languageID "+ obj[tname]._lang);
//	return obj[tname];
	return obj;
  }
 
  static void log(String str)
  {
    // Ignore the data to log
  }
  
}