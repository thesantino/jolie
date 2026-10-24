package jolie.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import jolie.Interpreter;
import jolie.compat.net.BytePipe;
import jolie.net.protocols.CommProtocol;

/** A streaming channel over two in-memory pipes. No selectors: CommCore polls it. */
public class BrowserCommChannel extends StreamingCommChannel implements PollableCommChannel {
	private final BytePipe inPipe;
	private final BytePipe outPipe;
	private final InputStream istream;
	private final OutputStream ostream;

	public BrowserCommChannel( BytePipe inPipe, BytePipe outPipe, URI location, CommProtocol protocol )
		throws IOException {
		super( location, protocol );
		this.inPipe = inPipe;
		this.outPipe = outPipe;
		this.istream = inPipe.in;
		this.ostream = new BufferedOutputStream( outPipe.out );
		setToBeClosed( false ); // keep connections open, like the socket channel
	}

	private String state() {
		return " [inPipe closed=" + inPipe.isClosed() + " avail=" + inPipe.available()
			+ " drained=" + inPipe.drained() + "]";
	}

	@Override
	protected CommMessage recvImpl() throws IOException {
		try {
			if( inPipe.drained() ) {
				closeImpl();
				throw new ChannelClosingException();
			}
			return protocol().recv( istream, ostream );
		} catch( ChannelClosingException e ) {
			throw e;
		} catch( IOException | RuntimeException e ) {
			jolie.compat.Output
				.write( "[chan recv] " + e.getClass().getName() + ": " + e.getMessage() + state() + "\n" );
			if( inPipe.drained() ) {
				closeImpl();
				throw new ChannelClosingException();
			}
			throw e;
		}
	}

	@Override
	protected void sendImpl( CommMessage message ) throws IOException {
		try {
			protocol().send( ostream, message, istream );
			ostream.flush();
		} catch( IOException | RuntimeException e ) {
			jolie.compat.Output
				.write( "[chan send] " + e.getClass().getName() + ": " + e.getMessage() + state() + "\n" );
			throw e;
		}
	}

	@Override
	public boolean isReady() {
		// data waiting, or EOF waiting (so the handler can observe the close)
		return inPipe.available() > 0 || inPipe.isClosed();
	}

	@Override
	protected void disposeForInputImpl() throws IOException {
		final CommCore core = Interpreter.getInstance().commCore();
		final jolie.net.ports.InputPort port = parentInputPort();
		Runnable schedule = () -> core.scheduleReceive( this, port );
		if( inPipe.whenReady( schedule ) ) {
			schedule.run();
		}
	}

	@Override
	public CommChannel createDuplicate() {
		throw new UnsupportedOperationException( "duplicate of a virtual socket channel" );
	}

	@Override
	protected boolean isOpenImpl() {
		return !inPipe.drained();
	}

	@Override
	protected void closeImpl() {
		inPipe.close();
		outPipe.close();
	}

}
