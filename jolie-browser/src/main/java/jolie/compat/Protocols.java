package jolie.compat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import jolie.net.CommCore;
import jolie.net.ext.CommProtocolFactory;

public final class Protocols {
	private static final Map< String, Function< CommCore, CommProtocolFactory > > MAP = new HashMap<>();

	private Protocols() {}

	public static void register( String name, Function< CommCore, CommProtocolFactory > f ) {
		MAP.put( name, f );
	}

	// first parameter is the JolieClassLoader instance (static-ified call)
	public static CommProtocolFactory create( Object loader, String name, CommCore core ) {
		Function< CommCore, CommProtocolFactory > f = MAP.get( name );
		return f == null ? null : f.apply( core );
	}
}
