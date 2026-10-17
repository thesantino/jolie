package jolie.compat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import jolie.runtime.JavaService;

public final class JavaServices {
	private static final Map< String, Supplier< JavaService > > MAP = new HashMap<>();

	private JavaServices() {}

	public static void register( String name, Supplier< JavaService > s ) {
		MAP.put( name, s );
	}

	// first parameter is the JavaServiceNodeLoader instance (static-ified call)
	public static JavaService create( Object loader, String name ) {
		Supplier< JavaService > s = MAP.get( name );
		if( s == null ) {
			throw new IllegalStateException( "Java service not registered in the browser: " + name );
		}
		return s.get();
	}
}
