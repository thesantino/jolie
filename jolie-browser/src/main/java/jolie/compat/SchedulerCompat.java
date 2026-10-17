package jolie.compat;

import java.util.List;
import java.util.concurrent.TimeUnit;

public final class SchedulerCompat {
	private SchedulerCompat() {}

	public static List< Runnable > shutdownNow( Object s ) {
		return ((SingleThreadScheduler) s).shutdownNow();
	}

	public static void shutdown( Object s ) {
		((SingleThreadScheduler) s).shutdown();
	}

	public static boolean awaitTermination( Object s, long t, TimeUnit u ) throws InterruptedException {
		return ((SingleThreadScheduler) s).awaitTermination( t, u );
	}
}
