package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jolie.compat.TaskFuture;

public class TCompletableFuture< T > implements Future< T > {
	private final TaskFuture< T > inner = new TaskFuture<>();

	public TCompletableFuture() {}

	public boolean complete( T value ) {
		return inner.complete( value );
	}

	public boolean completeExceptionally( Throwable t ) {
		return inner.fail( t );
	}

	public T join() {
		try {
			return inner.get();
		} catch( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	public T getNow( T valueIfAbsent ) {
		return inner.isDone() ? join() : valueIfAbsent;
	}

	@Override
	public boolean cancel( boolean b ) {
		return inner.cancel( b );
	}

	@Override
	public boolean isCancelled() {
		return inner.isCancelled();
	}

	@Override
	public boolean isDone() {
		return inner.isDone();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return inner.get();
	}

	@Override
	public T get( long t, TimeUnit u )
		throws InterruptedException, ExecutionException, TimeoutException {
		return inner.get( t, u );
	}

	public static < U > java.util.concurrent.CompletableFuture< U > completedFuture( U value ) {
		TCompletableFuture< U > f = new TCompletableFuture<>();
		f.complete( value );
		return (java.util.concurrent.CompletableFuture< U >) (Object) f;
	}
}
