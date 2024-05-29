package io.mosip.imagedecoder.openjpeg;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;

/*
 * Multi-Curve Translator (MCT) is a plug-in module that enables some photorealistic 
 * image-to-image translation methods to process high-resolution images in real-time.
 */
public class MctHelper {
	// Static variable reference of singleInstance of type Singleton
	private static MctHelper singleInstance = null;

	private MctHelper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized MctHelper getInstance() {
		if (singleInstance == null)
			singleInstance = new MctHelper();

		return singleInstance;
	}

	/* <summary> */
	/* Foward reversible MCT. */
	/* </summary> */
	public void mctEncode(int[] c0, int[] c1, int[] c2, int n) {
		int i;
		for (i = 0; i < n; ++i) {
			int r = c0[i];
			int g = c1[i];
			int b = c2[i];
			int y = (r + (g * 2) + b) >> 2;
			int u = b - g;
			int v = r - g;
			c0[i] = y;
			c1[i] = u;
			c2[i] = v;
		}
	}

	/* <summary> */
	/* Inverse reversible MCT. */
	/* </summary> */
	public void mctDecode(int[] c0, int[] c1, int[] c2, int n) {
		int i;
		for (i = 0; i < n; ++i) {
			int y = c0[i];
			int u = c1[i];
			int v = c2[i];
			int g = y - ((u + v) >> 2);
			int r = v + g;
			int b = u + g;
			c0[i] = r;
			c1[i] = g;
			c2[i] = b;
		}
	}

	/* <summary> */
	/* Get norm of basis function of reversible MCT. */
	/* </summary> */
	public double mctGetNorm(int compno) {
		return OpenJpegConstant.MCT_NORMS[compno];
	}

	/* <summary> */
	/* Foward irreversible MCT. */
	/* </summary> */
	public void mctEncodeReal(double[] c0, double[] c1, double[] c2, int n) {
		int i;
		for (i = 0; i < n; ++i) {
			double r = c0[i];
			double g = c1[i];
			double b = c2[i];
			int y = fixMul(r, 2449) + fixMul(g, 4809) + fixMul(b, 934);
			int u = -fixMul(r, 1382) - fixMul(g, 2714) + fixMul(b, 4096);
			int v = fixMul(r, 4096) - fixMul(g, 3430) - fixMul(b, 666);
			c0[i] = y;
			c1[i] = u;
			c2[i] = v;
		}
	}

	/* <summary> */
	/* Inverse irreversible MCT. */
	/* </summary> */
	public void mctDecodeReal(double[] c0, double[] c1, double[] c2, int n) {
		int i;
		for (i = 0; i < n; ++i) {
			double y = c0[i];
			double u = c1[i];
			double v = c2[i];
			double r = y + (v * 1.402f);
			double g = y - (u * 0.34413f) - (v * (0.71414f));
			double b = y + (u * 1.772f);
			c0[i] = r;
			c1[i] = g;
			c2[i] = b;
		}
	}

	/* <summary> */
	/* Get norm of basis function of irreversible MCT. */
	/* </summary> */
	public double mctGetNormReal(int compno) {
		return OpenJpegConstant.MCT_NORMS_REAL[compno];
	}

	@SuppressWarnings({ "unused"})
	private int fixMul(int a, int b) 
	{
		long temp = (long)a * b;
		temp += temp & 4096;
		return (int) (temp >> 13);
	}

	private int fixMul(double a, double b) {
		long temp = (long) (a * b);
		temp += temp & 4096;
		return (int) (temp >> 13);
	}
}
