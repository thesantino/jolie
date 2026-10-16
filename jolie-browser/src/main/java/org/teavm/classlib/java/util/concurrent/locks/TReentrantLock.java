package org.teavm.classlib.java.util.concurrent.locks;

import java.util.concurrent.TimeUnit;

public class TReentrantLock implements TLock {
	private Thread owner;
	private int holds;

	public TReentrantLock() {}

	public TReentrantLock( boolean fair ) {}

	@Override
	public void lock() {
		Thread me = Thread.currentThread();
		synchronized( this ) {
			while( owner != null && owner != me ) {
				try {
					wait();
				} catch( InterruptedException e ) {
					// keep waiting, like lock() in the JDK
				}
			}
			owner = me;
			holds++;
		}
	}

	@Override
	public boolean tryLock() {
		Thread me = Thread.currentThread();
		synchronized( this ) {
			if( owner != null && owner != me ) {
				return false;
			}
			owner = me;
			holds++;
			return true;
		}
	}

	@Override
	public void unlock() {
		synchronized( this ) {
			if( owner != Thread.currentThread() ) {
				throw new IllegalMonitorStateException();
			}
			holds--;
			if( holds == 0 ) {
				owner = null;
				notifyAll();
			}
		}
	}

	@Override
	public boolean tryLock( long time, TimeUnit unit ) throws InterruptedException {
		Thread me = Thread.currentThread();
		long deadline = System.currentTimeMillis() + unit.toMillis( time );
		synchronized( this ) {
			while( owner != null && owner != me ) {
				long left = deadline - System.currentTimeMillis();
				if( left <= 0 ) {
					return false;
				}
				wait( left );
			}
			owner = me;
			holds++;
			return true;
		}
	}

	public boolean isHeldByCurrentThread() {
		synchronized( this ) {
			return owner == Thread.currentThread();
		}
	}

	public boolean isLocked() {
		synchronized( this ) {
			return owner != null;
		}
	}

	// fully releases the lock and returns the hold count, for Condition.await
	private int releaseAll() {
		synchronized( this ) {
			if( owner != Thread.currentThread() ) {
				throw new IllegalMonitorStateException();
			}
			int saved = holds;
			holds = 0;
			owner = null;
			notifyAll();
			return saved;
		}
	}

	private void reacquire( int saved ) {
		lock();
		synchronized( this ) {
			holds = saved;
		}
	}

	@Override
	public TCondition newCondition() {
		return new Cond();
	}

	private class Cond implements TCondition {
		private long generation;

		@Override
		public void await() throws InterruptedException {
			int saved;
			synchronized( this ) {
				long g = generation;
				saved = releaseAll();
				try {
					while( generation == g ) {
						wait();
					}
				} finally {
					// reacquire outside this monitor below
				}
			}
			reacquire( saved );
		}

		@Override
		public boolean await( long time, TimeUnit unit ) throws InterruptedException {
			long ms = Math.max( 1, unit.toMillis( time ) );
			boolean signalled;
			int saved;
			synchronized( this ) {
				long g = generation;
				saved = releaseAll();
				if( generation == g ) {
					wait( ms );
				}
				signalled = generation != g;
			}
			reacquire( saved );
			return signalled;
		}

		@Override
		public void signal() {
			signalAll();
		}

		@Override
		public void signalAll() {
			synchronized( this ) {
				generation++;
				notifyAll();
			}
		}
	}
}
