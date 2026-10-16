package jolie.compat;

import java.net.URL;

public final class ClassLoaderCompat {
	private ClassLoaderCompat() {}

	public static Class< ? > findLoadedClass( ClassLoader l, String name ) {
		return null;
	}

	public static ClassLoader getParent( ClassLoader l ) {
		return null;
	}

	public static URL findResource( ClassLoader l, String name ) {
		return null;
	}

	public static void addURL( ClassLoader l, URL u ) {}

	public static Class< ? > loadClass( ClassLoader l, String name ) throws ClassNotFoundException {
		throw new ClassNotFoundException( name );
	}
}
