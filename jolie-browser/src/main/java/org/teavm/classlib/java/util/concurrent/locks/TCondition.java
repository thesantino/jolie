package org.teavm.classlib.java.util.concurrent.locks;

import java.util.concurrent.TimeUnit;

public interface TCondition {
	void await() throws InterruptedException;

	boolean await( long time, TimeUnit unit ) throws InterruptedException;

	void signal();

	void signalAll();
}
