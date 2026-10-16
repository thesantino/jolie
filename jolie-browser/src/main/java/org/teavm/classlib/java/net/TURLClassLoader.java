package org.teavm.classlib.java.net;

import java.net.URL;

public class TURLClassLoader extends ClassLoader {
	public TURLClassLoader( URL[] urls, ClassLoader parent ) {
		super();
	}

	protected Class< ? > findClass( String name ) throws ClassNotFoundException {
		throw new ClassNotFoundException( name );
	}
}
