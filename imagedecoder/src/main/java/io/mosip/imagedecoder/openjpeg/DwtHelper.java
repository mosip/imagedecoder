package io.mosip.imagedecoder.openjpeg;

import io.mosip.imagedecoder.constant.DecoderErrorCodes;
import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.exceptions.DecoderException;
import io.mosip.imagedecoder.model.openjpeg.Dwt;
import io.mosip.imagedecoder.model.openjpeg.DwtV4;
import io.mosip.imagedecoder.model.openjpeg.StepSize;
import io.mosip.imagedecoder.model.openjpeg.TcdResolution;
import io.mosip.imagedecoder.model.openjpeg.TcdTileComponent;
import io.mosip.imagedecoder.model.openjpeg.TileComponentCodingParameters;
import io.mosip.imagedecoder.model.openjpeg.V4;
import io.mosip.imagedecoder.util.openjpeg.MathUtil;

// DWT - Helper Implementation of a discrete wavelet transform
public class DwtHelper {
	// Static variable reference of singleInstance of type Singleton
	private static DwtHelper singleInstance = null;

	private DwtHelper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized DwtHelper getInstance() {
		if (singleInstance == null)
			singleInstance = new DwtHelper();

		return singleInstance;
	}

	@SuppressWarnings("unused")
	private int[] dwtWS(int i) {
		return new int[i * 2];
	}

	@SuppressWarnings("unused")
	private int[] dwtWD(int i) {
		return new int[1 + (i) * 2];
	}

	/* <summary> */
	/* Forward lazy transform (horizontal). */
	/* </summary> */
	private void dwtInterLeaveHorizontal(int[] a, int[] b, int bIndex, int dn, int sn, int cas) {
		int i;
		for (i = 0; i < sn; i++)
			b[bIndex + i] = a[2 * i + cas];
		for (i = 0; i < dn; i++)
			b[bIndex + (sn + i)] = a[(2 * i + 1 - cas)];
	}

	private void dwtInterLeaveRealHorizontal(double[] a, double[] b, int bIndex, int dn, int sn, int cas) {
		int i;
		for (i = 0; i < sn; i++)
			b[bIndex + i] = a[2 * i + cas];
		for (i = 0; i < dn; i++)
			b[bIndex + (sn + i)] = a[(2 * i + 1 - cas)];
	}

	/* <summary> */
	/* Forward lazy transform (vertical). */
	/* </summary> */
	private void dwtDeInterLeaveVertical(int[] a, int[] b, int bIndex, int dn, int sn, int x, int cas) {
		int i;
		for (i = 0; i < sn; i++)
			b[bIndex + i * x] = a[2 * i + cas];
		for (i = 0; i < dn; i++)
			b[bIndex + (sn + i) * x] = a[(2 * i + 1 - cas)];
	}

	private void dwtDeInterLeaveRealVertical(double[] a, double[] b, int bIndex, int dn, int sn, int x, int cas) {
		int i;
		for (i = 0; i < sn; i++)
			b[bIndex + i * x] = a[2 * i + cas];
		for (i = 0; i < dn; i++)
			b[bIndex + (sn + i) * x] = a[(2 * i + 1 - cas)];
	}

	/* <summary> */
	/* Inverse lazy transform (horizontal). */
	/* </summary> */
	private void dwtInterLeaveHorizontal(Dwt h, int[] a, int aIndex) {
		int aiIndex = aIndex;
		int biIndex = h.getMemIndex() + h.getCas();
		int i = h.getSn();

		while (i-- != 0) {
			h.getMem()[biIndex] = a[aiIndex++];
			biIndex += 2;
		}
		aiIndex = aIndex + h.getSn();
		biIndex = h.getMemIndex() + 1 - h.getCas();
		i = h.getDn();
		while (i-- != 0) {
			h.getMem()[biIndex] = a[aiIndex++];
			biIndex += 2;
		}
	}

	/* <summary> */
	/* Inverse lazy transform (vertical). */
	/* </summary> */
	private void dwtInterLeaveVertical(Dwt v, int[] a, int aIndex, int x) {
		int aiIndex = aIndex;
		int biIndex = v.getMemIndex() + v.getCas();
		int i = v.getSn();
		while (i-- != 0) {
			v.getMem()[biIndex] = a[aiIndex];
			biIndex += 2;
			aiIndex += x;
		}
		aiIndex = aIndex + (v.getSn() * x);
		biIndex = v.getMemIndex() + 1 - v.getCas();
		i = v.getDn();
		while (i-- != 0) {
			v.getMem()[biIndex] = a[aiIndex];
			biIndex += 2;
			aiIndex += x;
		}
	}

	private void dwtSSet(int[] a, int index, int value) {
		a[index * 2] = value;
	}

	private void dwtSSetReal(double[] a, int index, double value) {
		a[index * 2] = value;
	}

	private void dwtDSet(int[] a, int index, int value) {
		a[(1 + (index) * 2)] = value;
	}

	private void dwtDSetReal(double[] a, int index, double value) {
		a[(1 + (index) * 2)] = value;
	}

	private int dwtS(int[] a, int index) {
		return a[index * 2];
	}

	private double dwtSReal(double[] a, int index) {
		return a[index * 2];
	}

	private int dwtD(int[] a, int index) {
		return a[(1 + (index) * 2)];
	}

	private double dwtDReal(double[] a, int index) {
		return a[(1 + (index) * 2)];
	}

	@SuppressWarnings({ "unused" })
	private int dwtS1(int[] a, int index, int dn, int sn) {
		if (index < 0)
			return dwtS(a, 0);
		else if (index >= sn)
			return dwtS(a, sn - 1);
		else
			return dwtS(a, index);
	}

	@SuppressWarnings({ "unused" })
	private double dwtSReal(double[] a, int index, int dn, int sn) {
		if (index < 0)
			return dwtSReal(a, 0);
		else if (index >= sn)
			return dwtSReal(a, sn - 1);
		else
			return dwtSReal(a, index);
	}

	@SuppressWarnings({ "unused" })
	private int dwtD1(int[] a, int index, int dn, int sn) {
		if (index < 0)
			return dwtD(a, 0);
		else if (index >= dn)
			return dwtD(a, dn - 1);
		else
			return dwtD(a, index);
	}

	@SuppressWarnings({ "unused" })
	private double dwtDReal(double[] a, int index, int dn, int sn) {
		if (index < 0)
			return dwtDReal(a, 0);
		else if (index >= dn)
			return dwtDReal(a, dn - 1);
		else
			return dwtDReal(a, index);
	}

	@SuppressWarnings({ "unused" })
	private int dwtSS1(int[] a, int index, int dn, int sn) {
		if (index < 0)
			return dwtS(a, 0);
		else if (index >= dn)
			return dwtS(a, dn - 1);
		else
			return dwtS(a, index);
	}

	@SuppressWarnings({ "unused" })
	private double dwtSS1Real(double[] a, int index, int dn, int sn) {
		if (index < 0)
			return dwtSReal(a, 0);
		else if (index >= dn)
			return dwtSReal(a, dn - 1);
		else
			return dwtSReal(a, index);
	}

	@SuppressWarnings({ "unused" })
	private int dwtDD1(int[] a, int index, int dn, int sn) {
		if (index < 0)
			return dwtD(a, 0);
		else if (index >= sn)
			return dwtD(a, sn - 1);
		else
			return dwtD(a, index);
	}

	@SuppressWarnings({ "unused" })
	private double dwtDD1Real(double[] a, int index, int dn, int sn) {
		if (index < 0)
			return dwtDReal(a, 0);
		else if (index >= sn)
			return dwtDReal(a, sn - 1);
		else
			return dwtDReal(a, index);
	}

	/* <summary> */
	/* Forward 5-3 wavelet transform in 1-Dimension. */
	/* </summary> */
	@SuppressWarnings({ "java:S3776" })
	private void dwtEncode1(int[] a, int dn, int sn, int cas) {
		int i;

		if (cas == 0) {
			if ((dn > 0) || (sn > 1)) { /* NEW : CASE ONE ELEMENT */
				for (i = 0; i < dn; i++)
					dwtDSet(a, i, dwtD(a, i) - ((dwtS1(a, i, dn, sn) + dwtS1(a, i + 1, dn, sn)) >> 1));
				for (i = 0; i < sn; i++)
					dwtSSet(a, i, dwtS(a, i) + ((dwtD1(a, i - 1, dn, sn) + dwtD1(a, i, dn, sn) + 2) >> 2));
			}
		} else {
			if (sn == 0 && dn == 1) /* NEW : CASE ONE ELEMENT */
				dwtSSet(a, 0, dwtS(a, 0) * 2);
			else {
				for (i = 0; i < dn; i++)
					dwtSSet(a, i, dwtS(a, i) - ((dwtDD1(a, i, dn, sn) + dwtDD1(a, i - 1, dn, sn)) >> 1));
				for (i = 0; i < sn; i++)
					dwtDSet(a, i, dwtD(a, i) + ((dwtSS1(a, i, dn, sn) + dwtSS1(a, i + 1, dn, sn) + 2) >> 2));
			}
		}
	}

	/* <summary> */
	/* Inverse 5-3 wavelet transform in 1-Dimension. */
	/* </summary> */
	@SuppressWarnings({ "java:S1659", "java:S3776" })
	private void dwtDecode11(int[] a, int dn, int sn, int cas) {
		int i;

		if (cas == 0) {
			if ((dn > 0) || (sn > 1)) { /* NEW : CASE ONE ELEMENT */
				for (i = 0; i < sn; i++)
					dwtSSet(a, i, dwtS(a, i) - ((dwtD1(a, i - 1, dn, sn) + dwtD1(a, i, dn, sn) + 2) >> 2));
				for (i = 0; i < dn; i++)
					dwtDSet(a, i, dwtD(a, i) + ((dwtS1(a, i, dn, sn) + dwtS1(a, i + 1, dn, sn)) >> 1));
			}
		} else {
			if (sn == 0 && dn == 1) /* NEW : CASE ONE ELEMENT */
				dwtSSet(a, 0, dwtS(a, 0) / 2);
			else {
				for (i = 0; i < sn; i++)
					dwtDSet(a, i, dwtD(a, i) - ((dwtSS1(a, i, dn, sn) + dwtSS1(a, i + 1, dn, sn) + 2) >> 2));
				for (i = 0; i < dn; i++)
					dwtSSet(a, i, dwtS(a, i) + ((dwtDD1(a, i, dn, sn) + dwtDD1(a, i - 1, dn, sn)) >> 1));
			}
		}
	}

	/* <summary> */
	/* Inverse 5-3 wavelet transform in 1-dimension. */
	/* </summary> */
	private void dwtDecode1(Dwt v) {
		dwtDecode11(v.getMem(), v.getDn(), v.getSn(), v.getCas());
	}

	/* <summary> */
	/* Forward 9-7 wavelet transform in 1-dimension. */
	/* </summary> */
	@SuppressWarnings({ "java:S3776" })
	private void dwtEncode1Real(double[] a, int dn, int sn, int cas) {
		int i;
		if (cas == 0) {
			if ((dn > 0) || (sn > 1)) { /* NEW : CASE ONE ELEMENT */
				for (i = 0; i < dn; i++)
					dwtDSetReal(a, i,
							dwtDReal(a, i) - fixMul(dwtSReal(a, i, dn, sn) + dwtSReal(a, i + 1, dn, sn), 12993));
				for (i = 0; i < sn; i++)
					dwtSSetReal(a, i,
							dwtSReal(a, i) - fixMul(dwtDReal(a, i - 1, dn, sn) + dwtDReal(a, i, dn, sn), 434));
				for (i = 0; i < dn; i++)
					dwtDSetReal(a, i,
							dwtDReal(a, i) + fixMul(dwtSReal(a, i, dn, sn) + dwtSReal(a, i + 1, dn, sn), 7233));
				for (i = 0; i < sn; i++)
					dwtSSetReal(a, i,
							dwtSReal(a, i) + fixMul(dwtDReal(a, i - 1, dn, sn) + dwtDReal(a, i, dn, sn), 3633));
				for (i = 0; i < dn; i++)
					dwtDSetReal(a, i, fixMul(dwtDReal(a, i), 5038)); /* 5038 */
				for (i = 0; i < sn; i++)
					dwtSSetReal(a, i, fixMul(dwtSReal(a, i), 6659)); /* 6660 */
			}
		} else {
			if ((sn > 0) || (dn > 1)) { /* NEW : CASE ONE ELEMENT */
				for (i = 0; i < dn; i++)
					dwtSSetReal(a, i,
							dwtSReal(a, i) - fixMul(dwtDD1Real(a, i, dn, sn) + dwtDD1Real(a, i - 1, dn, sn), 12993));
				for (i = 0; i < sn; i++)
					dwtDSetReal(a, i,
							dwtDReal(a, i) - fixMul(dwtSS1Real(a, i, dn, sn) + dwtSS1Real(a, i + 1, dn, sn), 434));
				for (i = 0; i < dn; i++)
					dwtSSetReal(a, i,
							dwtSReal(a, i) + fixMul(dwtDD1Real(a, i, dn, sn) + dwtDD1Real(a, i - 1, dn, sn), 7233));
				for (i = 0; i < sn; i++)
					dwtDSetReal(a, i,
							dwtDReal(a, i) + fixMul(dwtSS1Real(a, i, dn, sn) + dwtSS1Real(a, i + 1, dn, sn), 3633));
				for (i = 0; i < dn; i++)
					dwtSSetReal(a, i, fixMul(dwtSReal(a, i), 5038)); /* 5038 */
				for (i = 0; i < sn; i++)
					dwtDSetReal(a, i, fixMul(dwtDReal(a, i), 6659)); /* 6660 */
			}
		}
	}

	@SuppressWarnings({ "java:S1659" })
	private void dwtEncodeStepSize(int stepSize, int noOfBps, StepSize bandNoStepSize) {
		int p, n;
		p = MathUtil.getInstance().intFloorLog2(stepSize) - 13;
		n = 11 - MathUtil.getInstance().intFloorLog2(stepSize);
		bandNoStepSize.setMant((n < 0 ? stepSize >> -n : stepSize << n) & 0x7ff);
		bandNoStepSize.setExpn(noOfBps - p);
	}

	/* <summary> */
	/* Forward 5-3 wavelet transform in 2-dimension. */
	/* </summary> */
	@SuppressWarnings({ "java:S1659" })
	public void dwtEncode(TcdTileComponent tilec) {
		int i, j, k;
		int aIndex = 0;
		int ajIndex = 0;
		int[] bj = null;
		int w, l;

		w = tilec.getX1() - tilec.getX0();
		l = tilec.getNoOfResolutions() - 1;
		aIndex = 0;

		for (i = 0; i < l; i++) {
			int rw; /* width of the resolution level computed */
			int rh; /* height of the resolution level computed */
			int rw1; /* width of the resolution level once lower than computed one */
			int rh1; /* height of the resolution level once lower than computed one */
			int casCol; /*
						 * 0 = non inversion on horizontal filtering 1 = inversion between low-pass and
						 * high-pass filtering
						 */
			int casRow; /*
						 * 0 = non inversion on vertical filtering 1 = inversion between low-pass and
						 * high-pass filtering
						 */
			int dn, sn;

			rw = tilec.getResolutions()[l - i].getX1() - tilec.getResolutions()[l - i].getX0();
			rh = tilec.getResolutions()[l - i].getY1() - tilec.getResolutions()[l - i].getY0();
			rw1 = tilec.getResolutions()[l - i - 1].getX1() - tilec.getResolutions()[l - i - 1].getX0();
			rh1 = tilec.getResolutions()[l - i - 1].getY1() - tilec.getResolutions()[l - i - 1].getY0();

			casRow = tilec.getResolutions()[l - i].getX0() % 2;
			casCol = tilec.getResolutions()[l - i].getY0() % 2;

			sn = rh1;
			dn = rh - rh1;
			bj = new int[rh];
			for (j = 0; j < rw; j++) {
				ajIndex = aIndex + j;
				for (k = 0; k < rh; k++)
					bj[k] = tilec.getIData()[ajIndex + k * w];
				dwtEncode1(bj, dn, sn, casCol);
				dwtDeInterLeaveVertical(bj, tilec.getIData(), ajIndex, dn, sn, w, casCol);
			}

			sn = rw1;
			dn = rw - rw1;
			bj = new int[rw];
			for (j = 0; j < rh; j++) {
				ajIndex = aIndex + j * w;
				for (k = 0; k < rw; k++)
					bj[k] = tilec.getIData()[ajIndex + k];
				dwtEncode1(bj, dn, sn, casRow);
				dwtInterLeaveHorizontal(bj, tilec.getIData(), ajIndex, dn, sn, casRow);
			}
		}
	}

	/* <summary> */
	/* Inverse 5-3 wavelet transform in 2-dimension. */
	/* </summary> */
	@SuppressWarnings({ "java:S1192" })
	public void dwtDecode(TcdTileComponent tilec, int numres) {
		dwtDecodeTile(tilec, numres, "dwtDecode1");
	}

	/* <summary> */
	/* Get gain of 5-3 wavelet transform. */
	/* </summary> */
	public int dwtGetGain(int orient) {
		if (orient == 0)
			return 0;
		if (orient == 1 || orient == 2)
			return 1;
		return 2;
	}

	/* <summary> */
	/* Get norm of 5-3 wavelet. */
	/* </summary> */
	public double dwtGetNorm(int level, int orient) {
		return OpenJpegConstant.DWT_NORMS[orient][level];
	}

	/* <summary> */
	/* Forward 9-7 wavelet transform in 2-dimension. */
	/* </summary> */

	@SuppressWarnings({ "java:S1659", "java:S3776" })
	public void dwtEncodeReal(TcdTileComponent tilec) {
		int i, j, k;
		int aIndex = 0;
		int ajIndex = 0;
		double[] bj = null;
		int w, l;

		w = tilec.getX1() - tilec.getX0();
		l = tilec.getNoOfResolutions() - 1;
		aIndex = 0;

		for (i = 0; i < l; i++) {
			int rw; /* width of the resolution level computed */
			int rh; /* height of the resolution level computed */
			int rw1; /* width of the resolution level once lower than computed one */
			int rh1; /* height of the resolution level once lower than computed one */
			int casCol; /*
						 * 0 = non inversion on horizontal filtering 1 = inversion between low-pass and
						 * high-pass filtering
						 */
			int casRow; /*
						 * 0 = non inversion on vertical filtering 1 = inversion between low-pass and
						 * high-pass filtering
						 */
			int dn, sn;

			rw = tilec.getResolutions()[l - i].getX1() - tilec.getResolutions()[l - i].getX0();
			rh = tilec.getResolutions()[l - i].getY1() - tilec.getResolutions()[l - i].getY0();
			rw1 = tilec.getResolutions()[l - i - 1].getX1() - tilec.getResolutions()[l - i - 1].getX0();
			rh1 = tilec.getResolutions()[l - i - 1].getY1() - tilec.getResolutions()[l - i - 1].getY0();

			casRow = tilec.getResolutions()[l - i].getX0() % 2;
			casCol = tilec.getResolutions()[l - i].getY0() % 2;

			sn = rh1;
			dn = rh - rh1;
			bj = new double[rh];
			for (j = 0; j < rw; j++) {
				ajIndex = aIndex + j;
				for (k = 0; k < rh; k++)
					bj[k] = tilec.getFData()[ajIndex + k * w];
				dwtEncode1Real(bj, dn, sn, casCol);
				dwtDeInterLeaveRealVertical(bj, tilec.getFData(), ajIndex, dn, sn, w, casCol);
			}

			sn = rw1;
			dn = rw - rw1;
			bj = new double[rw];
			for (j = 0; j < rh; j++) {
				ajIndex = aIndex + j * w;
				for (k = 0; k < rw; k++)
					bj[k] = tilec.getFData()[ajIndex + k];
				dwtEncode1Real(bj, dn, sn, casRow);
				dwtInterLeaveRealHorizontal(bj, tilec.getFData(), ajIndex, dn, sn, casRow);
			}
		}
	}

	/* <summary> */
	/* Get gain of 9-7 wavelet transform. */
	/* </summary> */
	@SuppressWarnings({ "java:S1172" })
	public int dwtGetGainReal(int orient) {
		return 0;
	}

	/* <summary> */
	/* Get norm of 9-7 wavelet. */
	/* </summary> */
	public double dwtGetNormReal(int level, int orient) {
		return OpenJpegConstant.DWT_NORMS_REAL[orient][level];
	}

	@SuppressWarnings({ "java:S1659", "java:S3358", "java:S3776" })
	public void dwtCalcExplicitStepSizes(TileComponentCodingParameters tccp, int prec) {
		int noOfBands;
		int bandNo;
		noOfBands = 3 * tccp.getNoOfResolutions() - 2;
		for (bandNo = 0; bandNo < noOfBands; bandNo++) {
			double stepsize;
			int resNo, level, orient, gain;

			resNo = (bandNo == 0) ? 0 : ((bandNo - 1) / 3 + 1);
			orient = (bandNo == 0) ? 0 : ((bandNo - 1) % 3 + 1);
			level = tccp.getNoOfResolutions() - 1 - resNo;
			gain = (tccp.getQmfbid() == 0) ? 0 : ((orient == 0) ? 0 : (((orient == 1) || (orient == 2)) ? 1 : 2));
			if (tccp.getQuantisationStyle() == OpenJpegConstant.J2K_CCP_QNTSTY_NOQNT) {
				stepsize = 1.0;
			} else {
				double norm = OpenJpegConstant.DWT_NORMS_REAL[orient][level];
				stepsize = (1 << (gain)) / norm;
			}
			dwtEncodeStepSize((int) Math.floor(stepsize * 8192.0), prec + gain, tccp.getStepsizes()[bandNo]);
		}
	}

	/* <summary> */
	/* Determine maximum computed resolution level for inverse wavelet transform */
	/* </summary> */
	private int dwtDecodeMaxResolution(TcdResolution[] arrResolution, int i) {
		int mr = 1;
		int w;
		int rIndex = 0;
		while (--i != 0) {
			rIndex++;
			if (mr < (w = arrResolution[rIndex].getX1() - arrResolution[rIndex].getX0()))
				mr = w;
			if (mr < (w = arrResolution[rIndex].getY1() - arrResolution[rIndex].getY0()))
				mr = w;
		}
		return mr;
	}

	/* <summary> */
	/* Inverse wavelet transform in 2-dimension. */
	/* </summary> */
	private void dwtDecodeTile(TcdTileComponent tilec, int numres, String dwtDecode1DName) {
		Dwt h = new Dwt(); /* Dwt horizontal */
		Dwt v = new Dwt(); /* Dwt vertical */

		int trIndex = 0;
		TcdResolution[] tr = tilec.getResolutions();

		int rw = tr[trIndex].getX1() - tr[trIndex].getX0(); /* width of the resolution level computed */
		int rh = tr[trIndex].getY1() - tr[trIndex].getY0(); /* height of the resolution level computed */

		int w = tilec.getX1() - tilec.getX0();

		h.setMem(new int[dwtDecodeMaxResolution(tr, numres)]);
		v.setMem(h.getMem());

		while (--numres != 0) {
			int j;

			++trIndex;
			h.setSn(rw);
			v.setSn(rh);

			rw = tr[trIndex].getX1() - tr[trIndex].getX0();
			rh = tr[trIndex].getY1() - tr[trIndex].getY0();

			h.setDn(rw - h.getSn());
			h.setCas(tr[trIndex].getX0() % 2);

			for (j = 0; j < rh; ++j) {
				dwtInterLeaveHorizontal(h, tilec.getIData(), j * w);
				if (dwtDecode1DName.equals("dwtDecode1"))
					dwtDecode1(h);

				System.arraycopy(h.getMem(), 0, tilec.getIData(), j * w, rw);
			}

			v.setDn(rh - v.getSn());
			v.setCas(tr[trIndex].getY0() % 2);

			for (j = 0; j < rw; ++j) {
				int k;
				dwtInterLeaveVertical(v, tilec.getIData(), j, w);
				if (dwtDecode1DName.equals("dwtDecode1"))
					dwtDecode1(v);
				for (k = 0; k < rh; ++k) {
					tilec.getIData()[k * w + j] = v.getMem()[k];
				}
			}
		}
		h.setMem(null);
	}

	@SuppressWarnings({ "java:S135", "java:S1659", "java:S3776" })
	private void dwtV4InterLeaveHorizontal(DwtV4 w, double[] a, int aIndex, int x, int size) {
		int waveIndex = w.getCas();
		int count = w.getSn();
		int i, k;
		for (k = 0; k < 2; ++k) {
			for (i = 0; i < count; ++i) {
				int j = i;
				w.getWavelet()[waveIndex + i * 2].getF()[0] = a[aIndex + j];
				j += x;
				if (j > size)
					continue;
				w.getWavelet()[waveIndex + i * 2].getF()[1] = a[aIndex + j];
				j += x;
				if (j > size)
					continue;
				w.getWavelet()[waveIndex + i * 2].getF()[2] = a[aIndex + j];
				j += x;
				if (j > size)
					continue;
				w.getWavelet()[waveIndex + i * 2].getF()[3] = a[aIndex + j];
			}
			waveIndex = 1 - w.getCas();
			aIndex += w.getSn();
			size -= w.getSn();
			count = w.getDn();
		}
	}

	private void dwtV4InterLeaveVertical(DwtV4 w, double[] a, int aIndex, int x) {
		int biIndex = w.getCas();
		int i;
		for (i = 0; i < w.getSn(); ++i) {
			w.getWavelet()[biIndex + i * 2].getF()[0] = a[aIndex + (i * x) + 0];
			w.getWavelet()[biIndex + i * 2].getF()[1] = a[aIndex + (i * x) + 1];
			w.getWavelet()[biIndex + i * 2].getF()[2] = a[aIndex + (i * x) + 2];
			w.getWavelet()[biIndex + i * 2].getF()[3] = a[aIndex + (i * x) + 3];
		}
		aIndex += w.getSn() * x;
		biIndex = 1 - w.getCas();
		for (i = 0; i < w.getDn(); ++i) {
			w.getWavelet()[biIndex + i * 2].getF()[0] = a[aIndex + (i * x) + 0];
			w.getWavelet()[biIndex + i * 2].getF()[1] = a[aIndex + (i * x) + 1];
			w.getWavelet()[biIndex + i * 2].getF()[2] = a[aIndex + (i * x) + 2];
			w.getWavelet()[biIndex + i * 2].getF()[3] = a[aIndex + (i * x) + 3];
		}
	}

	private void dwtV4DecodeStep1(V4[] w, int wIndex, int count, final double c) {
		int fwIndex = wIndex;
		int i;
		for (i = 0; i < count; ++i) {
			double tmp1 = w[fwIndex + i * 2].getF()[0];
			double tmp2 = w[fwIndex + i * 2].getF()[1];
			double tmp3 = w[fwIndex + i * 2].getF()[2];
			double tmp4 = w[fwIndex + i * 2].getF()[3];
			w[fwIndex + i * 2].getF()[0] = tmp1 * c;
			w[fwIndex + i * 2].getF()[1] = tmp2 * c;
			w[fwIndex + i * 2].getF()[2] = tmp3 * c;
			w[fwIndex + i * 2].getF()[3] = tmp4 * c;
		}
	}

	private void dwtV4DecodeStep2(V4[] l, int flIndex, V4[] w, int fwIndex, int k, int m, double c) {
		int i;
		for (i = 0; i < m; ++i) {
			double tmp1One = l[flIndex].getF()[0];
			double tmp1Two = l[flIndex].getF()[1];
			double tmp1Three = l[flIndex].getF()[2];
			double tmp1Four = l[flIndex].getF()[3];
			double tmp2One = w[fwIndex - 1].getF()[0];
			double tmp2Two = w[fwIndex - 1].getF()[1];
			double tmp2Three = w[fwIndex - 1].getF()[2];
			double tmp2Four = w[fwIndex - 1].getF()[3];
			double tmp3One = w[fwIndex].getF()[0];
			double tmp3Two = w[fwIndex].getF()[1];
			double tmp3Three = w[fwIndex].getF()[2];
			double tmp3Four = w[fwIndex].getF()[3];
			w[fwIndex - 1].getF()[0] = tmp2One + ((tmp1One + tmp3One) * c);
			w[fwIndex - 1].getF()[1] = tmp2Two + ((tmp1Two + tmp3Two) * c);
			w[fwIndex - 1].getF()[2] = tmp2Three + ((tmp1Three + tmp3Three) * c);
			w[fwIndex - 1].getF()[3] = tmp2Four + ((tmp1Four + tmp3Four) * c);
			flIndex = fwIndex;
			fwIndex += 2;
		}
		if (m < k) {
			c += c;
			double c1 = l[flIndex].getF()[0] * c;
			double c2 = l[flIndex].getF()[1] * c;
			double c3 = l[flIndex].getF()[2] * c;
			double c4 = l[flIndex].getF()[3] * c;
			for (; m < k; ++m) {
				double tmp1 = w[fwIndex - 1].getF()[0];
				double tmp2 = w[fwIndex - 1].getF()[1];
				double tmp3 = w[fwIndex - 1].getF()[2];
				double tmp4 = w[fwIndex - 1].getF()[3];
				w[fwIndex - 1].getF()[0] = tmp1 + c1;
				w[fwIndex - 1].getF()[1] = tmp2 + c2;
				w[fwIndex - 1].getF()[2] = tmp3 + c3;
				w[fwIndex - 1].getF()[3] = tmp4 + c4;
				fwIndex += 2;
			}
		}
	}

	/* <summary> */
	/* Inverse 9-7 wavelet transform in 1-dimension. */
	/* </summary> */
	private void dwtV4Decode(DwtV4 dwt) {
		int a;
		int b;
		if (dwt.getCas() == 0) {
			if (!((dwt.getDn() > 0) || (dwt.getSn() > 1))) {
				return;
			}
			a = 0;
			b = 1;
		} else {
			if (!((dwt.getSn() > 0) || (dwt.getDn() > 1))) {
				return;
			}
			a = 1;
			b = 0;
		}
		dwtV4DecodeStep1(dwt.getWavelet(), a, dwt.getSn(), OpenJpegConstant.K);
		dwtV4DecodeStep1(dwt.getWavelet(), b, dwt.getDn(), OpenJpegConstant.C13318);
		dwtV4DecodeStep2(dwt.getWavelet(), b, dwt.getWavelet(), a + 1, dwt.getSn(),
				MathUtil.getInstance().intMin(dwt.getSn(), dwt.getDn() - a), OpenJpegConstant.DWT_DELTA);
		dwtV4DecodeStep2(dwt.getWavelet(), a, dwt.getWavelet(), b + 1, dwt.getDn(),
				MathUtil.getInstance().intMin(dwt.getDn(), dwt.getSn() - b), OpenJpegConstant.DWT_GAMMA);
		dwtV4DecodeStep2(dwt.getWavelet(), b, dwt.getWavelet(), a + 1, dwt.getSn(),
				MathUtil.getInstance().intMin(dwt.getSn(), dwt.getDn() - a), OpenJpegConstant.DWT_BETA);
		dwtV4DecodeStep2(dwt.getWavelet(), a, dwt.getWavelet(), b + 1, dwt.getDn(),
				MathUtil.getInstance().intMin(dwt.getDn(), dwt.getSn() - b), OpenJpegConstant.DWT_ALPHA);
	}

	/* <summary> */
	/* Inverse 9-7 wavelet transform in 2-dimension. */
	/* </summary> */
	@SuppressWarnings({ "java:S3776", "java:S3923" })
	public void dwtDecodeReal(TcdTileComponent tilec, int numres) {
		DwtV4 h = new DwtV4(); /* DwtV4 horizontal */
		DwtV4 v = new DwtV4(); /* DwtV4 vertical */

		int resIndex = 0;
		TcdResolution[] arrResolution = tilec.getResolutions();

		int rw = arrResolution[resIndex].getX1()
				- arrResolution[resIndex].getX0(); /* width of the resolution level computed */
		int rh = arrResolution[resIndex].getY1()
				- arrResolution[resIndex].getY0(); /* height of the resolution level computed */

		int w = tilec.getX1() - tilec.getX0(); // width

		h.setWavelet(new V4[dwtDecodeMaxResolution(arrResolution, numres) + 5]);
		for (int index = 0; index < h.getWavelet().length; index++)
			h.getWavelet()[index] = new V4();

		v.setWavelet(h.getWavelet());

		while (--numres != 0) {
			int ajIndex = 0;
			int bufsize = (tilec.getX1() - tilec.getX0()) * (tilec.getY1() - tilec.getY0());
			int j;

			++resIndex;

			h.setSn(rw);
			v.setSn(rh);

			rw = arrResolution[resIndex].getX1()
					- arrResolution[resIndex].getX0(); /* width of the resolution level computed */
			rh = arrResolution[resIndex].getY1()
					- arrResolution[resIndex].getY0(); /* height of the resolution level computed */

			h.setDn(rw - h.getSn());
			h.setCas(arrResolution[resIndex].getX0() % 2);

			for (j = rh; j > 0; j -= 4) {
				dwtV4InterLeaveHorizontal(h, tilec.getFData(), ajIndex, w, bufsize);
				dwtV4Decode(h);
				if (j >= 4) {
					int k;
					for (k = rw; --k >= 0;) {
						tilec.getFData()[ajIndex + k + w * 0] = h.getWavelet()[k].getF()[0];
						tilec.getFData()[ajIndex + k + w * 1] = h.getWavelet()[k].getF()[1];
						tilec.getFData()[ajIndex + k + w * 2] = h.getWavelet()[k].getF()[2];
						tilec.getFData()[ajIndex + k + w * 3] = h.getWavelet()[k].getF()[3];
					}
				} else {
					int k;
					for (k = rw; --k >= 0;) {
						switch (j) {
						case 3:
							tilec.getFData()[ajIndex + k + w * 2] = h.getWavelet()[k].getF()[2];
							tilec.getFData()[ajIndex + k + w * 1] = h.getWavelet()[k].getF()[1];
							tilec.getFData()[ajIndex + k + w * 0] = h.getWavelet()[k].getF()[0];
							break;
						case 2:
							tilec.getFData()[ajIndex + k + w * 1] = h.getWavelet()[k].getF()[1];
							tilec.getFData()[ajIndex + k + w * 0] = h.getWavelet()[k].getF()[0];
							break;
						case 1:
							tilec.getFData()[ajIndex + k + w * 0] = h.getWavelet()[k].getF()[0];
							break;
						default:
							throw new DecoderException(DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorCode(),
									DecoderErrorCodes.UNSUPPORTED_FORMAT_ERROR.getErrorMessage());
						}
					}
				}
				ajIndex += w * 4;
				bufsize -= w * 4;
			}

			v.setDn(rh - v.getSn());
			v.setCas(arrResolution[resIndex].getY0() % 2);

			ajIndex = 0;
			for (j = rw; j > 0; j -= 4) {
				dwtV4InterLeaveVertical(v, tilec.getFData(), ajIndex, w);
				dwtV4Decode(v);
				if (j >= 4) {
					int k;
					for (k = 0; k < rh; ++k) {
						tilec.getFData()[ajIndex + (k * w) + 0] = v.getWavelet()[k].getF()[0];
						tilec.getFData()[ajIndex + (k * w) + 1] = v.getWavelet()[k].getF()[1];
						tilec.getFData()[ajIndex + (k * w) + 2] = v.getWavelet()[k].getF()[2];
						tilec.getFData()[ajIndex + (k * w) + 3] = v.getWavelet()[k].getF()[3];
					}
				} else {
					int k;
					for (k = 0; k < rh; ++k) {
						tilec.getFData()[ajIndex + (k * w) + 0] = v.getWavelet()[k].getF()[0];
						tilec.getFData()[ajIndex + (k * w) + 1] = v.getWavelet()[k].getF()[1];
						tilec.getFData()[ajIndex + (k * w) + 2] = v.getWavelet()[k].getF()[2];
						tilec.getFData()[ajIndex + (k * w) + 3] = v.getWavelet()[k].getF()[3];
					}
				}
				ajIndex += 4;
			}
		}

		h.setWavelet(null);
	}

	/**
	 * Multiply two fixed-precision rational numbers.
	 * 
	 * @param a
	 * @param b
	 * @return Returns a * b
	 */
	@SuppressWarnings("unused")
	private int fixMul(int a, int b) {
		long temp = (a * b);
		temp += temp & 4096;
		return (int) (temp >> 13);
	}

	private int fixMul(double a, double b) {
		long temp = (long) (a * b);
		temp += temp & 4096;
		return (int) (temp >> 13);
	}
}