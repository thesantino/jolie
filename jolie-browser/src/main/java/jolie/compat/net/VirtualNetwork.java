package jolie.compat.net;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public final class VirtualNetwork {
	private VirtualNetwork() {}

	public static final class Connection {
		public final BytePipe toServer = new BytePipe();
		public final BytePipe toClient = new BytePipe();
	}

	public static final class Queue {
		private final ArrayDeque< Connection > pending = new ArrayDeque<>();
		private boolean closed;

		synchronized void offer( Connection c ) {
			pending.add( c );
			notifyAll();
		}

		public synchronized Connection accept() {
			while( pending.isEmpty() ) {
				if( closed )
					return null;
				try {
					wait();
				} catch( InterruptedException e ) {
					return null;
				}
			}
			return pending.removeFirst();
		}

		public synchronized void close() {
			closed = true;
			notifyAll();
		}
	}

	private static final class Pending {
		boolean done;
		Connection conn;
		boolean ok;
	}

	private static final Map< Integer, Queue > LISTENERS = new HashMap<>();
	private static final Map< Integer, BytePipe > PIPES = new HashMap<>(); // handle -> pipe che riceve
	private static final Map< Integer, Pending > PENDING = new HashMap<>();
	private static int nextId = 1;
	private static boolean bridged;

	/** Chiamato da worker.js: da qui le connessioni passano dalla pagina. */
	public static synchronized void enableBridge() {
		bridged = true;
	}

	public static Queue listen( int port ) throws IOException {
		boolean useBridge;
		synchronized( VirtualNetwork.class ) {
			if( LISTENERS.containsKey( port ) )
				throw new IOException( "port already in use: " + port );
			useBridge = bridged;
		}
		if( useBridge ) {
			Pending p = new Pending();
			int id;
			synchronized( VirtualNetwork.class ) {
				id = nextId++;
				PENDING.put( id, p );
			}
			NetJs.listen( port, id );
			synchronized( p ) {
				while( !p.done ) {
					try {
						p.wait();
					} catch( InterruptedException e ) {
						throw new IOException( "interrupted" );
					}
				}
			}
			if( !p.ok )
				throw new IOException( "port already in use: " + port );
		}
		synchronized( VirtualNetwork.class ) {
			Queue q = new Queue();
			LISTENERS.put( port, q );
			return q;
		}
	}

	public static void listenResult( int id, int ok ) {
		Pending p;
		synchronized( VirtualNetwork.class ) {
			p = PENDING.remove( id );
		}
		if( p == null )
			return;
		synchronized( p ) {
			p.ok = ok != 0;
			p.done = true;
			p.notifyAll();
		}
	}

	public static synchronized void unlisten( int port ) {
		Queue q = LISTENERS.remove( port );
		if( q != null ) {
			q.close();
			if( bridged )
				NetJs.unlisten( port );
		}
	}

	public static Connection connect( int port ) throws IOException {
		if( bridged )
			return connectRemote( port );
		Queue q;
		synchronized( VirtualNetwork.class ) {
			q = LISTENERS.get( port );
		}
		if( q == null )
			throw new IOException( "connection refused: port " + port );
		Connection c = new Connection();
		q.offer( c );
		return c;
	}

	// ---- modalità bridged ----

	private static Connection connectRemote( int port ) throws IOException {
		Pending p = new Pending();
		int id;
		synchronized( VirtualNetwork.class ) {
			id = nextId++;
			PENDING.put( id, p );
		}
		NetJs.connect( port, id );
		synchronized( p ) {
			while( !p.done ) {
				try {
					p.wait();
				} catch( InterruptedException e ) {
					throw new IOException( "interrupted" );
				}
			}
		}
		if( p.conn == null )
			throw new IOException( "connection refused (bridged, no listener): port " + port );
		return p.conn;
	}

	/** Collega una connessione al handle JS h. serverSide: true per chi ha accettato. */
	private static synchronized Connection wire( int h, boolean serverSide ) {
		Connection c = new Connection();
		BytePipe in = serverSide ? c.toServer : c.toClient; // dati in arrivo dal peer
		BytePipe out = serverSide ? c.toClient : c.toServer; // dati che scriviamo noi
		PIPES.put( h, in );
		out.setSink( b -> NetJs.send( h, encode( b ) ), () -> NetJs.close( h ) );
		return c;
	}

	// Chiamati da JS (via JolieJS)

	public static void connectResult( int id, int h ) {
		Pending p;
		synchronized( VirtualNetwork.class ) {
			p = PENDING.remove( id );
		}
		if( p == null )
			return;
		if( h != 0 )
			p.conn = wire( h, false );
		synchronized( p ) {
			p.done = true;
			p.notifyAll();
		}
	}

	public static void incoming( int port, int h ) {
		Queue q;
		synchronized( VirtualNetwork.class ) {
			q = LISTENERS.get( port );
		}
		if( q == null ) {
			NetJs.close( h );
			return;
		}
		q.offer( wire( h, true ) );
	}

	public static void data( int h, String s ) {
		BytePipe pipe;
		synchronized( VirtualNetwork.class ) {
			pipe = PIPES.get( h );
		}
		if( pipe != null )
			pipe.feed( decode( s ) );
	}

	public static void closed( int h ) {
		BytePipe pipe;
		synchronized( VirtualNetwork.class ) {
			pipe = PIPES.remove( h );
		}
		if( pipe != null )
			pipe.close();
	}

	private static String encode( byte[] b ) {
		StringBuilder sb = new StringBuilder( b.length );
		for( byte x : b )
			sb.append( (char) (x & 0xff) );
		return sb.toString();
	}

	private static byte[] decode( String s ) {
		byte[] b = new byte[ s.length() ];
		for( int i = 0; i < b.length; i++ )
			b[ i ] = (byte) s.charAt( i );
		return b;
	}
}
