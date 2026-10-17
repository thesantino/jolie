package jolie.compat;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class TaskFuture< V > implements ScheduledFuture< V > {
	private boolean done;
	private boolean cancelled;
	private V value;
	private Throwable error;

	public synchronized boolean complete( V v ) {
		if( done )
			return false;
		value = v;
		done = true;
		notifyAll();
		return true;
	}

	public synchronized boolean fail( Throwable t ) {
		if( done )
			return false;
		error = t;
		done = true;
		notifyAll();
		return true;
	}

	private Thread thread;

	public synchronized void setThread( Thread t ) {
		this.thread = t;
	}

	@Override
	public synchronized boolean cancel( boolean mayInterrupt ) {
		if( done )
			return false;
		cancelled = true;
		done = true;
		notifyAll();
		if( thread != null ) {
			thread.interrupt();
		}
		return true;
	}

	@Override
	public synchronized boolean isCancelled() {
		return cancelled;
	}

	@Override
	public synchronized boolean isDone() {
		return done;
	}

	private V result() throws ExecutionException {
		if( cancelled )
			throw new CancellationException();
		if( error != null )
			throw new ExecutionException( error );
		return value;
	}

	@Override
	public synchronized V get() throws InterruptedException, ExecutionException {
		while( !done )
			wait();
		return result();
	}

	@Override
	public synchronized V get( long timeout, TimeUnit unit )
		throws InterruptedException, ExecutionException, TimeoutException {
		long deadline = System.currentTimeMillis() + unit.toMillis( timeout );
		while( !done ) {
			long left = deadline - System.currentTimeMillis();
			if( left <= 0 )
				throw new TimeoutException();
			wait( left );
		}
		return result();
	}

	@Override
	public long getDelay( TimeUnit unit ) {
		return 0;
	}

	@Override
	public int compareTo( Delayed o ) {
		return 0;
	}
}
