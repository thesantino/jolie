package jolie.net;

import java.io.IOException;
import java.net.URI;
import jolie.net.ext.CommChannelFactory;
import jolie.net.ports.OutputPort;

public class BrowserChannelFactory extends CommChannelFactory {
	public BrowserChannelFactory( CommCore commCore ) {
		super( commCore );
	}

	@Override
	public CommChannel createChannel( URI location, OutputPort port ) throws IOException {
		throw new IOException( "socket medium is not available in the browser" );
	}
}
