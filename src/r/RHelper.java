package r;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public abstract class RHelper {
	public static String surroundRTryCatch(String expr) {
		return "tryCatch(expr = {"+expr+"})";
	}
	public static REXP evalWithTryCatch(RConnection rconn,String expr) throws Exception {
		REXP r = rconn.eval(expr);
		String expr2 = surroundRTryCatch(expr);
		if (r.isString()) {
			throw new RserveException(rconn, "Error in evaluating expr :"+expr2 +" = >"+r.asString());
		}
		return r;
	}
}
