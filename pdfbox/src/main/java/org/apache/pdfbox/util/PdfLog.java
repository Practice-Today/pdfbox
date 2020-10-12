package org.apache.pdfbox.util;

import java.util.Map.Entry;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.fdf.FDFField;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

public class PdfLog {


	public static boolean verboseBtn=false;
	public static boolean verboseTx=true;

	private static String log(COSBase cosBase) {
		return cosBase.toString().replace("COSString{", "")
				.replace("COSObject{","")
				.replace("COSInt{", "")
				.replace("COSDictionary{", "")
				.replace("COSName{", "")
				.replace("}","");
	}

	public static void v(FDFField fdfField) {
		int count = fdfField.getCOSObject().entrySet().size();
		PdfLog.v("fdfField", fdfField.getPartialFieldName()+"["+String.valueOf(count)+"]");
		for (Entry<COSName, COSBase> item : fdfField.getCOSObject().entrySet() ) {
			switch (item.getKey().getName()) {
			case "T":
				PdfLog.v("    Tag", log(item.getValue()));
				break;
			case "V":
				PdfLog.v("    Val", log(item.getValue()));
				break;
			default:
				PdfLog.v("    Entry "+item.getKey().getName(), log(item.getValue()));
			}
		}	
	}
	
	
	
	public static void v(PDField docField) {
		int count = docField.getCOSObject().entrySet().size();
		String name = docField.getPartialName();
		String type = docField.getFieldType();
		String value = docField.getValueAsString();
		PdfLog.v("pdField", name+":"+type+" = "+value);
		
		if (!verboseBtn && type.equals("Btn")) return;
		if (!verboseTx && type.equals("Tx")) return;
		
		for (Entry<COSName, COSBase> item : docField.getCOSObject().entrySet() ) {
			String nam = item.getKey().getName();
			switch (nam) {
			// There are 100's of these! https://pdfbox.apache.org/docs/2.0.1/javadocs/org/apache/pdfbox/cos/COSName.html
			case "FT":
			case "Tag":
			case "V":
			case "T":
				break;
			case "DA":
				PdfLog.v("    "+nam, log(item.getValue()));
				break;
			case "Kids":
				COSArray ary = (COSArray) item.getValue();
				count = ary.size();
				PdfLog.v("    "+nam, "["+String.valueOf(count)+"]");
				for (COSBase kid : (COSArray)item.getValue().getCOSObject()) {
					COSObject kidd = (COSObject)kid;
					PdfLog.v("    ","\""+kidd.getClass().toString());//.getCOSObject().toString()+"\"");					
				}
				break;
			case "Rect":
				COSArray rect = (COSArray) item.getValue();
				String x = String.valueOf(rect.getInt(0));
				String y = String.valueOf(rect.getInt(1));
				String w = String.valueOf(rect.getInt(2));
				String h = String.valueOf(rect.getInt(3));
				v("    Rect:","("+x+","+y+","+w+","+h+")");
				break;
			case "AA":
				PdfLog.v("    \""+item.getKey().getName()+"\"", log(item.getValue()));
				for (Entry<COSName, COSBase> itm : ((COSDictionary) item.getValue().getCOSObject()).entrySet()) {
					String key = itm.getKey().getName();
					switch (key) {
					case "F":
					case "K":
						v("    AA."+key, itm.getKey().toString()+"="+itm.getValue().toString());
						break;
					default:
						v("    AA."+key, itm.getKey().toString()+"="+itm.getValue().toString());
						break;
					}
				}
				break;
			default:
				PdfLog.v("    \""+item.getKey().getName()+"\"", log(item.getValue()));
			}
		}		
	}


	
	
    private static String codelink(StackTraceElement frame) {
        String funcLine = frame.getFileName()+":"+frame.getLineNumber();
        return "("+funcLine+")";
    }
    private static String codeLink() {
        return codelink(Thread.currentThread().getStackTrace()[3]);
    }
    private static int m(String tag, String msg) {
    	System.err.println(tag+": "+msg);
    	return 0;
    }

    public static int v(String tag, String msg) { return PdfLog.m(tag,msg); }
    public static int v(String msg) {
        return PdfLog.v(codeLink(), msg);
    }

    public static int d(String tag, String msg) {
        return PdfLog.m(tag,msg);
    }
    public static int d(String msg) {
        return PdfLog.d(codeLink(), msg);
    }

    public static int i(String tag, String msg) { return PdfLog.m(tag,msg); }
    public static int i(String msg) {
        return PdfLog.i(codeLink(), msg);
    }

    public static int w(String tag, String msg) { return PdfLog.m(tag,msg); }
    public static int w(String msg) {
        return PdfLog.w(codeLink(), msg);
    }

    public static int e(String tag, String msg) { return PdfLog.m(tag,msg); }
    public static int e(String msg) {
        return PdfLog.e(codeLink(), msg);
    }

    private PdfLog() { }

}
