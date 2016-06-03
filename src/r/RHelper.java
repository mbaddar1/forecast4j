package r;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public abstract class RHelper {
	public static String surroundRTryCatch(String expr) {
		return "tryCatch(expr = {"+expr+"})";
	}
	public static REXP evalWithTryCatch(RConnection rconn,String expr) throws Exception {
		
		String expr2 = surroundRTryCatch(expr);
		REXP r = rconn.eval(expr2);
		if (r.isString()) {
			throw new RserveException(rconn, "Error in evaluating expr :"+expr2 +" = >"+r.asString());
		}
		return r;
	}
	public static void printRVar(RConnection rconn,String varName) {
		try {
			REXP r = rconn.eval("capture.output("+varName+")");
			String[] out = r.asStrings();
			for(String s :out) {
				System.out.println(s);
			}
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
