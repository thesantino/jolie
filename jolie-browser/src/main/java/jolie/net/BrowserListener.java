package jolie.net;

import java.io.IOException;
import jolie.Interpreter;
import jolie.compat.net.VirtualNetwork;
import jolie.net.ext.CommProtocolFactory;
import jolie.net.ports.InputPort;

public class BrowserListener extends CommListener {
	private final int port;
	private final VirtualNetwork.Queue queue;

	public BrowserListener( Interpreter interpreter, CommProtocolFactory protocolFactory, InputPort inputPort )
		throws IOException {
		super( interpreter, protocolFactory, inputPort );
		this.port = inputPort.location().getPort();
		this.queue = VirtualNetwork.listen( port );
	}

	@Override
	public void onShutdown() {
		VirtualNetwork.unlisten( port );
	}

	@Override
	public void run() {
		try {
			VirtualNetwork.Connection c;
			while( (c = queue.accept()) != null ) {
				CommChannel channel = new BrowserCommChannel(
					c.toServer, c.toClient, inputPort().location(), createProtocol() );
				channel.setParentInputPort( inputPort() );
				interpreter().commCore().scheduleReceive( channel, inputPort() );
			}
		} catch( IOException e ) {
			interpreter().logWarning( e );
		}
	}
}
