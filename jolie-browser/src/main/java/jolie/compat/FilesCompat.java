package jolie.compat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public final class FilesCompat {
	private FilesCompat() {}

	public static InputStream newInputStream( Path path, OpenOption[] options ) throws IOException {
		String name = path.toString();
		while( name.startsWith( "/" ) ) {
			name = name.substring( 1 );
		}
		InputStream is = Resources.getResourceAsStream( null, name );
		if( is == null ) {
			// fall back to the bare file name, in case Jolie resolved it against a directory
			is = Resources.getResourceAsStream( null, name.substring( name.lastIndexOf( '/' ) + 1 ) );
		}
		if( is == null ) {
			throw new NoSuchFileException( name );
		}
		return is;
	}
}
