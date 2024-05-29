package io.mosip.imagedecoder.util.openjpeg;

public class MathUtil {
	// Static variable reference of singleInstance of type Singleton
	private static MathUtil singleInstance = null;

	private MathUtil() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized MathUtil getInstance() {
		if (singleInstance == null)
			singleInstance = new MathUtil();

		return singleInstance;
	}

	/*
	 * Get the minimum of two integers
	 * 
	 * @return Returns a if a < b else b
	 */
	public int intMin(int a, int b) {
		return a < b ? a : b;
	}

	/**
	 * Get the maximum of two integers
	 * 
	 * @return Returns a if a > b else b
	 */
	public int intMax(int a, int b) {
		return (a > b) ? a : b;
	}

	/**
	 * Clamp an integer inside an interval
	 * 
	 * @return
	 *         <ul>
	 *         <li>Returns a if (min < a < max)
	 *         <li>Returns max if (a > max)
	 *         <li>Returns min if (a < min)
	 *         </ul>
	 */
	public int intClamp(int a, int min, int max) {
		if (a < min)
			return min;
		if (a > max)
			return max;
		return a;
	}

	/**
	 * @return Get absolute value of integer
	 */
	public int intAbs(int a) {
		return a < 0 ? -a : a;
	}

	/**
	 * Divide an integer and round upwards
	 * 
	 * @return Returns a divided by b
	 */
	public int intCeilDiv(int a, int b) {
		return (a + b - 1) / b;
	}

	/**
	 * Divide an integer by a power of 2 and round upwards
	 * 
	 * @return Returns a divided by 2^b
	 */
	public int intCeilDivPow2(int a, int b) {
		return (a + (1 << b) - 1) >> b;
	}

	/**
	 * Divide an integer by a power of 2 and round downwards
	 * 
	 * @return Returns a divided by 2^b
	 */
	public int intFloorDivPow2(int a, int b) {
		return a >> b;
	}

	/**
	 * Get logarithm of an integer and round downwards
	 * 
	 * @return Returns log2(a)
	 */
	public int intFloorLog2(int a) {
		int l;
		for (l = 0; a > 1; l++) {
			a >>= 1;
		}
		return l;
	}
}
