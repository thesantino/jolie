package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import jolie.compat.SingleThreadScheduler;

public final class TExecutors {
	private TExecutors() {}

	public static ScheduledExecutorService newSingleThreadScheduledExecutor( ThreadFactory factory ) {
		// the cast goes through Object, because ScheduledExecutorService maps to TScheduledExecutorService
		return (ScheduledExecutorService) (Object) new SingleThreadScheduler( factory );
	}
}
