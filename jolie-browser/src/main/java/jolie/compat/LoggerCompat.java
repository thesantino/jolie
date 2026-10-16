package jolie.compat;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoggerCompat {
	private LoggerCompat() {}

	public static void setLevel( Logger logger, Level level ) {
		// no-op: TeaVM's Logger has no setLevel
	}
}
