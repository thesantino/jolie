package jolie.compat;

import java.util.Locale;

public final class LocaleCompat {
	private LocaleCompat() {}

	public static Locale forLanguageTag( String tag ) {
		if( tag == null || tag.isEmpty() ) {
			return Locale.ENGLISH;
		}
		String[] p = tag.split( "-" );
		return p.length > 1 ? new Locale( p[ 0 ], p[ 1 ] ) : new Locale( p[ 0 ] );
	}
}
