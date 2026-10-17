package jolie.compat;

import org.teavm.jso.JSBody;

public final class Output {
	private Output() {}

	@JSBody( params = "s", script = "if (self.jolieOut) self.jolieOut(s);" )
	public static native void write( String s );
}
