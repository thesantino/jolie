package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface TScheduledExecutorService extends TExecutorService {
	ScheduledFuture< ? > schedule( Runnable task, long delay, TimeUnit unit );
}
