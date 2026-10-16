package jolie.compat;

import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;

public final class PathsCompat {
	private PathsCompat() {}

	public static Path get( URI uri ) {
		if( "file".equals( uri.getScheme() ) && uri.getPath() != null ) {
			return java.nio.file.Paths.get( uri.getPath() );
		}
		throw new FileSystemNotFoundException( String.valueOf( uri ) );
	}

	public static URI toUri( Path p ) {
		String s = p.toString();
		if( s.isEmpty() ) {
			s = "/";
		} else if( !s.startsWith( "/" ) ) {
			s = "/" + s;
		}
		try {
			return new URI( "file", null, s, null );
		} catch( java.net.URISyntaxException e ) {
			throw new IllegalArgumentException( String.valueOf( e.getReason() ) );
		}
	}
}
