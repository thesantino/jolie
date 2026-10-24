package jolie.compat.net;

import org.teavm.jso.JSBody;

public final class NetJs {
	private NetJs() {}

	@JSBody( params = { "p", "id" }, script = "self.jolieNet.listen(p, id);" )
	public static native void listen( int p, int id );

	@JSBody( params = { "p" }, script = "self.jolieNet.unlisten(p);" )
	public static native void unlisten( int p );

	@JSBody( params = { "p", "id" }, script = "self.jolieNet.connect(p, id);" )
	public static native void connect( int p, int id );

	@JSBody( params = { "h", "s" }, script = "self.jolieNet.send(h, s);" )
	public static native void send( int h, String s );

	@JSBody( params = { "h" }, script = "self.jolieNet.close(h);" )
	public static native void close( int h );
}
