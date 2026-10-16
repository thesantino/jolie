package jolie.compat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Resources {
	private static final Map< String, String > FILES = new HashMap<>();
	static {
		FILES.put( "libjolie.properties", "jolie.version=1.13.5-git\n" );
		FILES.put( "ascii-logo.txt", "Jolie" );
	}

	private Resources() {}

	// Later: the page can fill this with the virtual file system (include files, etc.)
	public static void put( String name, String content ) {
		FILES.put( name, content );
	}

	public static URL getResource( ClassLoader loader, String name ) {
		return null;
	}

	public static InputStream getResourceAsStream( ClassLoader loader, String name ) {
		String content = FILES.get( name );
		return content == null ? null : new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
	}
}
