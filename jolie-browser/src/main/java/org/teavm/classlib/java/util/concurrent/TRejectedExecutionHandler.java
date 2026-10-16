package org.teavm.classlib.java.util.concurrent;

import java.util.concurrent.ThreadPoolExecutor;

public interface TRejectedExecutionHandler {
	void rejectedExecution( Runnable r, ThreadPoolExecutor executor );
}
