package jolie.compat;

public final class SystemCompat {
	private SystemCompat() {}

	public static String getProperty( String key ) {
		if( key == null ) {
			return null;
		}
		switch( key ) {
		case "user.dir":
			return "/";
		case "file.separator":
			return "/";
		case "path.separator":
			return ":";
		case "line.separator":
			return "\n";
		default:
			return null;
		}
	}
}
