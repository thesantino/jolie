package jolie.compat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.teavm.classlib.java.util.concurrent.TScheduledExecutorService;

public final class SingleThreadScheduler implements TScheduledExecutorService {
	private final ThreadFactory factory;
	private final List< Runnable > pending = new ArrayList<>();
	private boolean shutdown;
	private int active;

	public SingleThreadScheduler( ThreadFactory factory ) {
		this.factory = factory;
	}

	@Override
	public ScheduledFuture< ? > schedule( Runnable task, long delay, TimeUnit unit ) {
		TaskFuture< Object > f = new TaskFuture<>();
		long ms = unit.toMillis( delay );
		Runnable r = () -> {
			try {
				Thread.sleep( ms );
			} catch( InterruptedException e ) {
				finish( task, false );
				return;
			}
			boolean run;
			synchronized( this ) {
				run = !shutdown && !f.isDone() && pending.remove( task );
			}
			if( run ) {
				try {
					task.run();
					f.complete( null );
				} catch( Throwable e ) {
					f.fail( e );
				}
			}
			finish( task, true );
		};
		synchronized( this ) {
			if( shutdown ) {
				f.cancel( false );
				return f;
			}
			pending.add( task );
			active++;
		}
		factory.newThread( r ).start();
		return f;
	}

	private synchronized void finish( Runnable task, boolean ignored ) {
		active--;
		notifyAll();
	}

	@Override
	public void execute( Runnable r ) {
		schedule( r, 0, TimeUnit.MILLISECONDS );
	}

	@Override
	public Future< ? > submit( Runnable r ) {
		return schedule( r, 0, TimeUnit.MILLISECONDS );
	}

	@Override
	public < T > Future< T > submit( Callable< T > c ) {
		TaskFuture< T > f = new TaskFuture<>();
		execute( () -> {
			try {
				f.complete( c.call() );
			} catch( Throwable e ) {
				f.fail( e );
			}
		} );
		return f;
	}

	@Override
	public synchronized void shutdown() {
		shutdown = true;
	}

	@Override
	public synchronized List< Runnable > shutdownNow() {
		shutdown = true;
		List< Runnable > out = new ArrayList<>( pending );
		pending.clear();
		return out;
	}

	@Override
	public synchronized boolean isShutdown() {
		return shutdown;
	}

	@Override
	public synchronized boolean isTerminated() {
		return shutdown && active == 0;
	}

	@Override
	public synchronized boolean awaitTermination( long timeout, TimeUnit unit ) throws InterruptedException {
		long deadline = System.currentTimeMillis() + unit.toMillis( timeout );
		while( active > 0 ) {
			long left = deadline - System.currentTimeMillis();
			if( left <= 0 )
				return false;
			wait( left );
		}
		return true;
	}
}
