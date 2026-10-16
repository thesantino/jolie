package jolie.net;

import java.io.IOException;
import jolie.Interpreter;
import jolie.net.ext.CommListenerFactory;
import jolie.net.ext.CommProtocolFactory;
import jolie.net.ports.InputPort;

public class BrowserListenerFactory extends CommListenerFactory {
	public BrowserListenerFactory( CommCore commCore ) {
		super( commCore );
	}

	@Override
	public CommListener createListener( Interpreter interpreter, CommProtocolFactory protocolFactory,
		InputPort inputPort ) throws IOException {
		throw new IOException( "socket medium is not available in the browser" );
	}
}
