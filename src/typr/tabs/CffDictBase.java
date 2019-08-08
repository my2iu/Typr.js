package typr.tabs;

import elemental.util.ArrayOf;
import elemental.util.ArrayOfInt;
import elemental.util.ArrayOfNumber;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType
public class CffDictBase
{
  @JsProperty String version;  
  boolean hasVersion = false;
  @JsProperty double rawVersion;   // top (SID)
  boolean hasRawVersion = false;
  @JsProperty String Notice;
  boolean hasNotice = false;
  @JsProperty double RawNotice; // top (SID)
  boolean hasRawNotice = false;
  @JsProperty String FullName; 
  boolean hasFullName = false;
  @JsProperty double RawFullName; // top (SID)
  boolean hasRawFullName = false;
  @JsProperty String FamilyName; 
  boolean hasFamilyName = false;
  @JsProperty double RawFamilyName; // top (SID)
  boolean hasRawFamilyName = false;
  @JsProperty double Weight; // top (SID)
  boolean hasWeight = false;
  @JsProperty ArrayOfNumber FontBBox; // top (array)
  boolean hasFontBBox = false;
  @JsProperty double BlueValues; // private (delta)
  boolean hasBlueValues = false;
  @JsProperty double OtherBlues; // private (delta)
  boolean hasOtherBlues = false;
  @JsProperty double FamilyBlues; // private (delta)
  boolean hasFamilyBlues = false;
  @JsProperty double FamilyOtherBlues; // private (delta)
  boolean hasFamilyOtherBlues = false;
  @JsProperty double StdHW; // private (number)
  boolean hasStdHW = false;
  @JsProperty double StdVW; // private (number)
  boolean hasStdVW = false;
//  @JsProperty JavaScriptObject escape;
//  boolean has = false;
  @JsProperty double UniqueID; // top (number)
  boolean hasUniqueID = false;
  @JsProperty ArrayOfNumber XUID; // top (array)
  boolean hasXUID = false;
  @JsProperty double RawSubrs; // private (number)
  boolean hasRawSubrs = false;
  @JsProperty public ArrayOf<ArrayOfInt> Subrs;
  boolean hasSubrs = false;
  @JsProperty public int Bias;
  @JsProperty double defaultWidthX; // private (number)
  boolean hasdefaultWidthX = false;
  @JsProperty double nominalWidthX; // private (number)
  boolean hasnominalWidthX = false;
  @JsProperty double RawCharStrings; // top (number)
  boolean hasRawCharStrings = false;
  @JsProperty double RawCharset; // top (number)
  boolean hasRawCharset = false;
  @JsProperty double RawEncoding; // top (number)
  boolean hasRawEncoding = false;
  @JsProperty ArrayOfNumber RawPrivate; // top (number,number)
  boolean hasRawPrivate = false;
  
  @JsProperty public CffDictBase Private;
  boolean hasPrivate = false;

  @JsProperty double RawFDArray; // CIDFont (number)
  boolean hasRawFDArray = false;
  @JsProperty double RawFDSelect; // CIDFont (number)
  boolean hasRawFDSelect = false;
  @JsProperty String Copyright; 
  boolean hasCopyright = false;
  @JsProperty double RawCopyright; // top (SID)
  boolean hasRawCopyright = false;
  @JsProperty boolean isFixedPitch; // top (bool)
  boolean hasIsFixedPitch = false;
  @JsProperty double ItalicAngle; // top (number)
  boolean hasItalicAngle = false;
  @JsProperty double UnderlinePosition; // top (number)
  boolean hasUnderlinePosition = false;
  @JsProperty double UnderlineThickness; // top (number)
  boolean hasUnderlineThickness = false;
  @JsProperty double PaintType; // top (number)
  boolean hasPaintType = false;
  @JsProperty double CharstringType; // top (number)
  boolean hasCharstringType = false;
  @JsProperty ArrayOfNumber FontMatrix; // top (array)
  boolean hasFontMatrix = false;
  @JsProperty double StrokeWidth; // top (number)
  boolean hasStrokeWidth = false;
  @JsProperty double BlueScale; // private (number)
  boolean hasBlueScale = false;
  @JsProperty double BlueShift; // private (number)
  boolean hasBlueShift = false;
  @JsProperty double BlueFuzz; // private (number)
  boolean hasBlueFuzz = false;
  @JsProperty double StemSnapH; // private (delta)
  boolean hasStemSnapH = false;
  @JsProperty double StemSnapV; // private (delta)
  boolean hasStemSnapV = false;
  @JsProperty boolean ForceBold; // private (boolean)
  boolean hasForceBold = false;
  @JsProperty double LanguageGroup; // private (number)
  boolean hasLanguageGroup = false;
  @JsProperty double ExpansionFactor; // private (number)
  boolean hasExpansionFactor = false;
  @JsProperty double initialRandomSeed; // private (number)
  boolean hasInitialRandomSeed = false;
  @JsProperty double SyntheticBase; // top (number)
  boolean hasSyntheticBase = false;
  @JsProperty double PostScript; // top (SID)
  boolean hasPostScript = false;
  @JsProperty double BaseFontName; // top (SID)
  boolean hasBaseFontName = false;
  @JsProperty double BaseFontBlend; // top (delta)
  boolean hasBaseFontBlend = false;
  @JsProperty ArrayOfNumber ROS; // CIDFont (SID SID number)
  boolean hasROS = false;
  @JsProperty double CIDFontVersion; // CIDFont (number)
  boolean hasCIDFontVersion = false;
  @JsProperty double CIDFontRevision; // CIDFont (number)
  boolean hasCIDFontRevision = false;
  @JsProperty double CIDFontType; // CIDFont (number)
  boolean hasCIDFontType = false;
  @JsProperty double CIDCount; // CIDFont (number)
  boolean hasCIDCount = false;
  @JsProperty double UIDBase; // CIDFont (number)
  boolean hasUIDBase = false;
  @JsProperty double FontName; // CIDFont (SID)
  boolean hasFontName = false;

  
  @JsIgnore public void setRawVersion(double val) {
    rawVersion = val;
    hasRawVersion = true;
  }
  
  @JsIgnore public void setVersion(String val) {
    version = val;
    hasVersion = true;
  }

  @JsIgnore public void setRawNotice(double val) {
    RawNotice = val;
    hasRawNotice = true;
  }

  @JsIgnore public void setNotice(String val) {
    Notice = val;
    hasNotice = true;
  }

  @JsIgnore public void setRawFullName(double val) {
    RawFullName = val;
    hasRawFullName = true;
  }
  
  @JsIgnore public void setFullName(String val) {
    FullName = val;
    hasFullName = true;
  }

  @JsIgnore public void setRawFamilyName(double val) {
    RawFamilyName = val;
    hasRawFamilyName = true;
  }

  @JsIgnore public void setFamilyName(String val) {
    FamilyName = val;
    hasFamilyName = true;
  }

  @JsIgnore public void setWeight(double val) {
    Weight = val;
    hasWeight = true;
  }

  @JsIgnore public void setFontBBox(ArrayOfNumber val) {
    FontBBox = val;
    hasFontBBox = true;
  }

  @JsIgnore public void setBlueValues(double val) {
    BlueValues = val;
    hasBlueValues = true;
  }

  @JsIgnore public void setOtherBlues(double val) {
    OtherBlues = val;
    hasOtherBlues = true;
  }

  @JsIgnore public void setFamilyBlues(double val) {
    FamilyBlues = val;
    hasFamilyBlues = true;
  }

  @JsIgnore public void setFamilyOtherBlues(double val) {
    FamilyOtherBlues = val;
    hasFamilyOtherBlues = true;
  }

  @JsIgnore public void setStdHW(double val) {
    StdHW = val;
    hasStdHW = true;
  }

  @JsIgnore public void setStdVW(double val) {
    StdVW = val;
    hasStdVW = true;
  }

  @JsIgnore public void setUniqueID(double val) {
    UniqueID = val;
    hasUniqueID = true;
  }

  @JsIgnore public void setXUID(ArrayOfNumber val) {
    XUID = val;
    hasXUID = true;
  }

  @JsIgnore public void setRawCharset(double val) {
    RawCharset = val;
    hasRawCharset = true;
  }

  @JsIgnore public void setEncoding(double val) {
    RawEncoding = val;
    hasRawEncoding = true;
  }

  @JsIgnore public void setCharStrings(double val) {
    RawCharStrings = val;
    hasRawCharStrings = true;
  }

  @JsIgnore public void setRawPrivate(ArrayOfNumber val) {
    RawPrivate = val;
    hasRawPrivate = true;
  }

  @JsIgnore public void setPrivate(CffDictBase val) {
    Private = val;
    hasPrivate = true;
  }

  @JsIgnore public void setRawSubrs(double val) {
    RawSubrs = val;
    hasRawSubrs = true;
  }
  
  @JsIgnore public void setSubrs(ArrayOf<ArrayOfInt> subrs, int Bias)
  {
    this.Subrs = subrs;
    this.Bias = Bias;
    this.hasSubrs = true;
  }

  @JsIgnore public void setdefaultWidthX (double val) {
    defaultWidthX = val;
    hasdefaultWidthX = true;
  }

  @JsIgnore public void setnominalWidthX(double val) {
    nominalWidthX = val;
    hasnominalWidthX = true;
  }

  @JsIgnore public void setRawCopyright(double val) {
    RawCopyright = val;
    hasRawCopyright = true;
  }

  @JsIgnore public void setCopyright(String val) {
    Copyright = val;
    hasCopyright = true;
  }

  @JsIgnore public void setisFixedPitch(boolean val) {
    isFixedPitch = val;
    hasIsFixedPitch = true;
  }

  @JsIgnore public void setItalicAngle(double val) {
    ItalicAngle = val;
    hasItalicAngle = true;
  }

  @JsIgnore public void setUnderlinePosition(double val) {
    UnderlinePosition = val;
    hasUnderlinePosition = true;
  }

  @JsIgnore public void setUnderlineThickness(double val) {
    UnderlineThickness = val;
    hasUnderlineThickness = true;
  }

  @JsIgnore public void setPaintType(double val) {
    PaintType = val;
    hasPaintType = true;
  }

  @JsIgnore public void setCharstringType(double val) {
    CharstringType = val;
    hasCharstringType = true;
  }

  @JsIgnore public void setFontMatrix(ArrayOfNumber val) {
    FontMatrix = val;
    hasFontMatrix = true;
  }

  @JsIgnore public void setStrokeWidth(double val) {
    StrokeWidth = val;
    hasStrokeWidth = true;
  }

  @JsIgnore public void setBlueScale(double val) {
    BlueScale = val;
    hasBlueScale = true;
  }

  @JsIgnore public void setBlueShift(double val) {
    BlueShift = val;
    hasBlueShift = true;
  }

  @JsIgnore public void setBlueFuzz(double val) {
    BlueFuzz = val;
    hasBlueFuzz = true;
  }

  @JsIgnore public void setStemSnapH(double val) {
    StemSnapH = val;
    hasStemSnapH = true;
  }

  @JsIgnore public void setStemSnapV(double val) {
    StemSnapV = val;
    hasStemSnapV = true;
  }

  @JsIgnore public void setForceBold(boolean val) {
    ForceBold = val;
    hasForceBold = true;
  }

  @JsIgnore public void setLanguageGroup(double val) {
    LanguageGroup = val;
    hasLanguageGroup = true;
  }

  @JsIgnore public void setExpansionFactor(double val) {
    ExpansionFactor = val;
    hasExpansionFactor = true;
  }

  @JsIgnore public void setinitialRandomSeed(double val) {
    initialRandomSeed = val;
    hasInitialRandomSeed = true;
  }

  @JsIgnore public void setSyntheticBase(double val) {
    SyntheticBase = val;
    hasSyntheticBase = true;
  }

  @JsIgnore public void setPostScript(double val) {
    PostScript = val;
    hasPostScript = true;
  }

  @JsIgnore public void setBaseFontName(double val) {
    BaseFontName = val;
    hasBaseFontName = true;
  }

  @JsIgnore public void setBaseFontBlend(double val) {
    BaseFontBlend = val;
    hasBaseFontBlend = true;
  }

  @JsIgnore public void setROS(ArrayOfNumber val) {
    ROS = val;
    hasROS = true;
  }

  @JsIgnore public void setCIDFontVersion(double val) {
    CIDFontVersion = val;
    hasCIDFontVersion = true;
  }

  @JsIgnore public void setCIDFontRevision(double val) {
    CIDFontRevision = val;
    hasCIDFontRevision = true;
  }

  @JsIgnore public void setCIDFontType(double val) {
    CIDFontType = val;
    hasCIDFontType = true;
  }

  @JsIgnore public void setCIDCount(double val) {
    CIDCount = val;
    hasCIDCount = true;
  }

  @JsIgnore public void setUIDBase(double val) {
    UIDBase = val;
    hasUIDBase = true;
  }

  @JsIgnore public void setRawFDArray(double val) {
    this.RawFDArray = val;
    hasRawFDArray = true;
  }

  @JsIgnore public void setRawFDSelect(double val) {
    RawFDSelect = val;
    hasRawFDSelect = true;
  }

  @JsIgnore public void setFontName(double val) {
    FontName = val;
    hasFontName = true;
  }

}
