package org.teavm.classlib.java.io;

import java.io.IOException;
import java.io.InputStream;

public class TObjectInputStream extends InputStream {
	private final InputStream in;

	public TObjectInputStream( InputStream in ) throws IOException {
		this.in = in;
	}

	public Object readObject() throws IOException, ClassNotFoundException {
		throw new IOException( "serialized programs are not supported in the browser" );
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}
}
