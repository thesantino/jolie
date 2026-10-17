package jolie.compat;

import jolie.Interpreter;
import jolie.runtime.embedding.BrowserServiceLoaderFactory;
import jolie.runtime.embedding.EmbeddedServiceLoaderFactory;

public final class Embedding {
	private Embedding() {}

	public static EmbeddedServiceLoaderFactory factory( Interpreter i, String name ) {
		return "Jolie".equals( name ) ? new BrowserServiceLoaderFactory() : null;
	}
}
