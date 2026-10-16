package jolie.compat;

public final class RuntimeCompat {
	private RuntimeCompat() {}

	public static int availableProcessors( Runtime r ) {
		return 0;
	}
}
