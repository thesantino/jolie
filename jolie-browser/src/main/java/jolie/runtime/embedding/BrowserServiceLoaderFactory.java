package jolie.runtime.embedding;

import jolie.Interpreter;
import jolie.compat.JavaServices;
import jolie.runtime.JavaService;
import jolie.runtime.expression.Expression;

public class BrowserServiceLoaderFactory implements EmbeddedServiceLoaderFactory {
	@Override
	public EmbeddedServiceLoader createLoader( Interpreter interpreter,
		EmbeddedServiceLoader.EmbeddedServiceConfiguration configuration, Expression channelDest )
		throws EmbeddedServiceLoaderCreationException {
		String path = ((EmbeddedServiceLoader.ExternalEmbeddedServiceConfiguration) configuration).servicePath();
		return new Loader( channelDest, interpreter, path );
	}

	private static class Loader extends EmbeddedServiceLoader {
		private final Interpreter interpreter;
		private final String className;

		Loader( Expression channelDest, Interpreter interpreter, String className ) {
			super( channelDest );
			this.interpreter = interpreter;
			this.className = className;
		}

		@Override
		public void load() throws EmbeddedServiceLoadingException {
			JavaService s = JavaServices.create( null, className );
			s.setInterpreter( interpreter );
			setChannel( new JavaCommChannel( s ) );
		}
	}
}
