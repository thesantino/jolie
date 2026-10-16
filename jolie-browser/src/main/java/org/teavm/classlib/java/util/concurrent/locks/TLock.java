package org.teavm.classlib.java.util.concurrent.locks;

public interface TLock {
	void lock();

	boolean tryLock();

	void unlock();

	TCondition newCondition();

	boolean tryLock( long time, java.util.concurrent.TimeUnit unit ) throws InterruptedException;
}
