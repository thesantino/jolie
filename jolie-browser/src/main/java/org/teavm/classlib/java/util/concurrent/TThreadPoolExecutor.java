package org.teavm.classlib.java.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import jolie.compat.TaskFuture;

public class TThreadPoolExecutor implements TExecutorService {
	private final ThreadFactory factory;
	private final RejectedExecutionHandler handler;
	private int active = 0;
	private boolean shutdown = false;

	public TThreadPoolExecutor( int core, int max, long keepAlive, TimeUnit unit,
		BlockingQueue< Runnable > queue, ThreadFactory factory, RejectedExecutionHandler handler ) {
		this.factory = factory;
		this.handler = handler;
	}

	public static class AbortPolicy implements RejectedExecutionHandler {
		public AbortPolicy() {}

		@Override
		public void rejectedExecution( Runnable r, ThreadPoolExecutor executor ) {
			throw new IllegalStateException( "task rejected" );
		}
	}

	@Override
	public void execute( Runnable r ) {
		synchronized( this ) {
			if( shutdown ) {
				handler.rejectedExecution( r, null );
				return;
			}
			active++;
		}
		Runnable wrapped = () -> {
			try {
				r.run();
			} finally {
				synchronized( TThreadPoolExecutor.this ) {
					active--;
					TThreadPoolExecutor.this.notifyAll();
				}
			}
		};
		factory.newThread( wrapped ).start();
	}

	@Override
	public Future< ? > submit( Runnable task ) {
		TaskFuture< Object > f = new TaskFuture<>();
		execute( () -> {
			try {
				task.run();
				f.complete( null );
			} catch( Throwable e ) {
				f.fail( e );
			}
		} );
		return f;
	}

	@Override
	public < T > Future< T > submit( Callable< T > task ) {
		TaskFuture< T > f = new TaskFuture<>();
		execute( () -> {
			try {
				f.complete( task.call() );
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
		return new ArrayList<>();
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
			if( left <= 0 ) {
				return false;
			}
			wait( left );
		}
		return true;
	}
}
