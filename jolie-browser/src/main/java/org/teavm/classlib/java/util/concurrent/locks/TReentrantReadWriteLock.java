package org.teavm.classlib.java.util.concurrent.locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TReentrantReadWriteLock implements TReadWriteLock {
	private final ReentrantLock shared = new ReentrantLock();
	private final ReadLock read = new ReadLock( shared );
	private final WriteLock write = new WriteLock( shared );

	public TReentrantReadWriteLock() {}

	public TReentrantReadWriteLock( boolean fair ) {}

	@Override
	public Lock readLock() {
		return (Lock) (Object) read;
	}

	@Override
	public Lock writeLock() {
		return (Lock) (Object) write;
	}

	public static class ReadLock implements TLock {
		private final ReentrantLock l;

		ReadLock( ReentrantLock l ) {
			this.l = l;
		}

		@Override
		public void lock() {
			l.lock();
		}

		@Override
		public boolean tryLock() {
			return l.tryLock();
		}

		@Override
		public void unlock() {
			l.unlock();
		}

		@Override
		public TCondition newCondition() {
			return (TCondition) (Object) l.newCondition();
		}

		@Override
		public boolean tryLock( long t, java.util.concurrent.TimeUnit u ) throws InterruptedException {
			return l.tryLock( t, u );
		}
	}

	public static class WriteLock extends ReadLock {
		WriteLock( ReentrantLock l ) {
			super( l );
		}
	}
}
