package jolie.compat.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Arrays;

/** One-directional in-memory byte stream with blocking reads (green-thread friendly). */
public final class BytePipe {
	private final ArrayDeque< byte[] > chunks = new ArrayDeque<>();
	private int offset;
	private int available;
	private boolean closed;

	public final InputStream in = new InputStream() {
		@Override
		public int read() throws IOException {
			byte[] one = new byte[ 1 ];
			int n = take( one, 0, 1 );
			return n < 0 ? -1 : one[ 0 ] & 0xff;
		}

		@Override
		public int read( byte[] b, int off, int len ) throws IOException {
			return len == 0 ? 0 : take( b, off, len );
		}

		@Override
		public int available() {
			return BytePipe.this.available();
		}

		@Override
		public void close() {
			BytePipe.this.close();
		}
	};

	public final OutputStream out = new OutputStream() {
		@Override
		public void write( int b ) throws IOException {
			put( new byte[] { (byte) b }, 0, 1 );
		}

		@Override
		public void write( byte[] b, int off, int len ) throws IOException {
			put( b, off, len );
		}

		@Override
		public void close() {
			BytePipe.this.close();
		}
	};

	private synchronized void put( byte[] b, int off, int len ) throws IOException {
		if( closed )
			throw new IOException( "pipe closed" );
		if( len == 0 )
			return;
		if( sink != null ) {
			sink.accept( Arrays.copyOfRange( b, off, off + len ) );
			return;
		}
		chunks.add( Arrays.copyOfRange( b, off, off + len ) );
		available += len;
		notifyAll();
		Runnable l = takeListener();
		if( l != null )
			l.run();
	}

	private synchronized int take( byte[] b, int off, int len ) throws IOException {
		while( available == 0 ) {
			if( closed )
				return -1;
			try {
				wait();
			} catch( InterruptedException e ) {
				throw new IOException( "interrupted" );
			}
		}
		int copied = 0;
		while( copied < len && !chunks.isEmpty() ) {
			byte[] c = chunks.peekFirst();
			int n = Math.min( len - copied, c.length - offset );
			System.arraycopy( c, offset, b, off + copied, n );
			copied += n;
			offset += n;
			if( offset == c.length ) {
				chunks.removeFirst();
				offset = 0;
			}
		}
		available -= copied;
		return copied;
	}

	public synchronized int available() {
		return available;
	}

	public synchronized boolean isClosed() {
		return closed;
	}

	public synchronized boolean drained() {
		return closed && available == 0;
	}

	public synchronized void close() {
		boolean first = !closed;
		closed = true;
		notifyAll();
		Runnable l = takeListener();
		if( l != null )
			l.run();
		if( first && onClose != null ) {
			Runnable r = onClose;
			onClose = null;
			r.run();
		}
	}

	private Runnable readyListener;

	/**
	 * Returns true if the pipe is already readable (data or EOF); otherwise stores r to run once when
	 * it becomes so.
	 */
	public synchronized boolean whenReady( Runnable r ) {
		if( available > 0 || closed ) {
			return true;
		}
		readyListener = r;
		return false;
	}

	private Runnable takeListener() {
		Runnable l = readyListener;
		readyListener = null;
		return l;
	}

	private java.util.function.Consumer< byte[] > sink;
	private Runnable onClose;

	/** Se impostato, le scritture vanno al sink invece di restare nel pipe. */
	public synchronized void setSink( java.util.function.Consumer< byte[] > sink, Runnable onClose ) {
		this.sink = sink;
		this.onClose = onClose;
	}

	/** Inserisce byte arrivati dall'esterno. */
	public void feed( byte[] b ) {
		try {
			put( b, 0, b.length );
		} catch( IOException e ) {
			// pipe chiuso: i dati vengono scartati
		}
	}
}
