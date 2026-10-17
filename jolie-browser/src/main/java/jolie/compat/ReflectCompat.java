package jolie.compat;

import java.lang.reflect.Method;

public final class ReflectCompat {
	private ReflectCompat() {}

	public static Method getMethod( Class< ? > c, String name, Class< ? >[] params ) {
		try {
			return c.getMethod( name, params );
		} catch( Throwable e ) {
			return null;
		}
	}
}
