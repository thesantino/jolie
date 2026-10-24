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
		try {
			jolie.compat.net.VirtualNetwork.Connection c =
				jolie.compat.net.VirtualNetwork.connect( location.getPort() );
			return new BrowserCommChannel( c.toClient, c.toServer, location, port.getProtocol() );
		} catch( java.net.URISyntaxException e ) {
			throw new IOException( e );
		}
	}
}
