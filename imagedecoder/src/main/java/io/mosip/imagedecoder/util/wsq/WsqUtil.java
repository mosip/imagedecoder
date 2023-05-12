package io.mosip.imagedecoder.util.wsq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import io.mosip.imagedecoder.constant.wsq.WsqErrorCode;
import io.mosip.imagedecoder.model.wsq.WsqQuantization;
import io.mosip.imagedecoder.model.wsq.WsqQuantizationTree;
import io.mosip.imagedecoder.model.wsq.WsqTableDqt;
import io.mosip.imagedecoder.model.wsq.WsqTableDtt;
import io.mosip.imagedecoder.model.wsq.WsqWavletTree;

public class WsqUtil {
	private static Logger LOGGER = LoggerFactory.getLogger(WsqUtil.class);

	/******************************************************************/
	/* This routine converts the unsigned char data to float. In the */
	/* process it shifts and scales the data so the values range from */
	/* +/- 128.0 This function returns on error. */
	/******************************************************************/
	public static int convertImage2Floats(float[] fImageData, /* output float image data */
			float[] mShift, /* shifting parameter */
			float[] rScale, /* scaling parameter */
			byte[] data, /* input signed byte data */
			int numOfPixels) /* num pixels in image */
	{
		int cnt; /* pixel cnt */
		long sum, overflow; /* sum of pixel values */
		float mean; /* mean pixel value */
		int low, high; /* low/high pixel values */
		float lowDiff, highDiff; /* new low/high pixels values shifting */

		sum = 0;
		overflow = 0;
		low = 255;
		high = 0;
		for (cnt = 0; cnt < numOfPixels; cnt++) {
			if ((data[cnt] & 0xFF) > high) // Convert to unsigned byte
				high = data[cnt] & 0xFF;
			if ((data[cnt] & 0xFF) < low)
				low = data[cnt] & 0xFF;
			sum += data[cnt] & 0xFF;
			if (sum < overflow) {
				LOGGER.error(String.format("convertImage2Float: overflow at %d", cnt));
				return (WsqErrorCode.IMAGE_DATA_OVERFLOW_WILL_READING.getErrorId());
			}
			overflow = sum;
		}

		mean = (float) sum / (float) numOfPixels;
		mShift[0] = mean;

		lowDiff = mShift[0] - low;
		highDiff = high - mShift[0];

		if (lowDiff >= highDiff)
			rScale[0] = lowDiff;
		else
			rScale[0] = highDiff;

		rScale[0] /= (float) 128.0;

		for (cnt = 0; cnt < numOfPixels; cnt++) {
			fImageData[cnt] = ((float) (data[cnt] & 0xFF) - mShift[0]) / rScale[0];
		}
		return (0);
	}

	/*********************************************************/
	/* Routine to convert image from float to signed byte array. */
	/*********************************************************/
	public static void convertImage2Bytes(byte[] data, /* uchar image pointer */
			float[] img, /* image pointer */
			int width, /* image width */
			int height, /* image height */
			float mShift, /* shifting parameter */
			float rScale) /* scaling parameter */
	{
		int r, c; /* row/column counters */
		float imgTmp; /* temp image data store */
		int imageIndex = 0, dataIndex = 0;
		for (r = 0; r < height; r++) {
			for (c = 0; c < width; c++) {
				imgTmp = (img[imageIndex] * rScale) + mShift;
				imgTmp += 0.5;
				if (imgTmp < 0.0f)
					data[dataIndex] = 0; /* neg pix poss after quantization */
				else if (imgTmp > 255.0)
					data[dataIndex] = (byte) 255;
				else
					data[dataIndex] = (byte) imgTmp;

				++imageIndex;
				++dataIndex;
			}
		}
	}

	/**********************************************************/
	/* This routine calculates the variances of the subbands. */
	/**********************************************************/
	public static void variance(WsqQuantization quantValues, /* quantization parameters */
			WsqQuantizationTree quantizationTree[], /* quantization "tree" */
			int qTreeLen, /* length of quantizationTree */
			float[] fImageData, /* image pointer */
			int width, /* image width */
			int height) /* image height */
	{
		int fpIndex; /* temp image pointer Index */
		int cvr; /* subband counter */
		int lenX = 0, lenY = 0; /* dimensions of area to calculate variance */
		int skipX, skipY; /*
							 * pixels to skip to get to area for variance calculation
							 */
		int row, col; /* dimension counters */
		float ssq; /* sum of squares */
		float sum2; /* variance calculation parameter */
		float sumOfPixels; /* sum of pixels */
		float vsum; /* variance sum for subbands 0-3 */

		vsum = 0.0f;
		for (cvr = 0; cvr < 4; cvr++) {
			fpIndex = (quantizationTree[cvr].getY() * width) + quantizationTree[cvr].getX();
			ssq = 0.0f;
			sumOfPixels = 0.0f;

			skipX = quantizationTree[cvr].getLenX() / 8;
			skipY = (9 * quantizationTree[cvr].getLenY()) / 32;

			lenX = (3 * quantizationTree[cvr].getLenX()) / 4;
			lenY = (7 * quantizationTree[cvr].getLenY()) / 16;

			fImageData[fpIndex] += (skipY * width) + skipX;
			for (row = 0; row < lenY; row++, fpIndex += (width - lenX)) {
				for (col = 0; col < lenX; col++) {
					sumOfPixels += fImageData[fpIndex];
					ssq += fImageData[fpIndex] * fImageData[fpIndex];
					fpIndex++;
				}
			}

			sum2 = (sumOfPixels * sumOfPixels) / (lenX * lenY);
			quantValues.getVar()[cvr] = (float) ((ssq - sum2) / ((lenX * lenY) - 1.0));
			vsum += quantValues.getVar()[cvr];
		}

		if (vsum < 20000.0) {
			for (cvr = 0; cvr < WsqConstant.NUM_SUBBANDS; cvr++) {
				fpIndex = (quantizationTree[cvr].getY() * width) + quantizationTree[cvr].getX();
				ssq = 0.0f;
				sumOfPixels = 0.0f;

				lenX = quantizationTree[cvr].getLenX();
				lenY = quantizationTree[cvr].getLenY();

				for (row = 0; row < lenY; row++, fpIndex += (width - lenX)) {
					for (col = 0; col < lenX; col++) {
						sumOfPixels += fImageData[fpIndex];
						ssq += fImageData[fpIndex] * fImageData[fpIndex];
						fpIndex++;
					}
				}
				sum2 = (sumOfPixels * sumOfPixels) / (lenX * lenY);
				quantValues.getVar()[cvr] = (float) ((ssq - sum2) / ((lenX * lenY) - 1.0));
			}
		} else {
			for (cvr = 4; cvr < WsqConstant.NUM_SUBBANDS; cvr++) {
				fpIndex = (quantizationTree[cvr].getY() * width) + quantizationTree[cvr].getX();
				ssq = 0.0f;
				sumOfPixels = 0.0f;

				skipX = quantizationTree[cvr].getLenX() / 8;
				skipY = (9 * quantizationTree[cvr].getLenY()) / 32;

				lenX = (3 * quantizationTree[cvr].getLenX()) / 4;
				lenY = (7 * quantizationTree[cvr].getLenY()) / 16;

				fImageData[fpIndex] += (skipY * width) + skipX;
				for (row = 0; row < lenY; row++, fpIndex += (width - lenX)) {
					for (col = 0; col < lenX; col++) {
						sumOfPixels += fImageData[fpIndex];
						ssq += fImageData[fpIndex] * fImageData[fpIndex];
						fpIndex++;
					}
				}
				sum2 = (sumOfPixels * sumOfPixels) / (lenX * lenY);
				quantValues.getVar()[cvr] = (float) ((ssq - sum2) / ((lenX * lenY) - 1.0));
			}
		}
	}

	/************************************************/
	/* This routine quantizes the wavelet subbands. */
	/************************************************/
	public static int quantize(short[] sip, /* quantized output init in calling function with width * height */
			int[] cmpSize, /* size of quantized output */
			WsqQuantization quantValues, /* quantization parameters */
			WsqQuantizationTree quantizationTree[], /* quantization "tree" */
			int qTreeLen, /* size of quantizationTree */
			float[] fImageData, /* floating point image pointer */
			int width, /* image width */
			int height) /* image height */
	{
		int i; /* temp counter */
		int j; /* interation index */
		int sipIndex = 0; /* sip index */
		int row, col; /* temp image characteristic parameters */
		int cnt; /* subband counter */
		float zbin; /* zero bin size */
		float[] A = new float[WsqConstant.NUM_SUBBANDS]; /* subband "weights" for quantization */
		float[] m = new float[WsqConstant.NUM_SUBBANDS]; /* subband size to image size ratios */
		/* (reciprocal of FBI spec for 'm') */
		float m1, m2, m3; /* reciprocal constants for 'm' */
		float[] sigma = new float[WsqConstant.NUM_SUBBANDS]; /* square root of subband variances */
		int[] K0 = new int[WsqConstant.NUM_SUBBANDS]; /* initial list of subbands w/variance >= thresh */
		int[] K1 = new int[WsqConstant.NUM_SUBBANDS]; /* working list of subbands */
		int[] K, nK; /* pointers to sets of subbands */
		int[] NP = new int[WsqConstant.NUM_SUBBANDS]; /* current subbounds with nonpositive bit rates. */
		int K0len; /* number of subbands in K0 */
		int Klen, nKlen; /* number of subbands in other subband lists */
		int NPlen; /* number of subbands flagged in NP */
		float S; /* current frac of subbands w/positive bit rate */
		float q; /* current proportionality constant */
		float P; /* product of 'q/Q' ratios */

		/* Set up 'A' table. */
		for (cnt = 0; cnt < WsqConstant.STRT_SUBBAND_3; cnt++)
			A[cnt] = 1.0f;
		A[cnt++ /* 52 */] = 1.32f;
		A[cnt++ /* 53 */] = 1.08f;
		A[cnt++ /* 54 */] = 1.42f;
		A[cnt++ /* 55 */] = 1.08f;
		A[cnt++ /* 56 */] = 1.32f;
		A[cnt++ /* 57 */] = 1.42f;
		A[cnt++ /* 58 */] = 1.08f;
		A[cnt++ /* 59 */] = 1.08f;

		for (cnt = 0; cnt < WsqConstant.MAX_SUBBANDS; cnt++) {
			quantValues.getQbss()[cnt] = 0.0f;
			quantValues.getQzbs()[cnt] = 0.0f;
		}

		/* Set up 'Q1' (prime) table. */
		for (cnt = 0; cnt < WsqConstant.NUM_SUBBANDS; cnt++) {
			if (quantValues.getVar()[cnt] < WsqConstant.VARIANCE_THRESH) {
				quantValues.getQbss()[cnt] = 0.0f;
			} else {
				/* NOTE: q has been taken out of the denominator in the next */
				/* 2 formulas from the original code. */
				if (cnt < WsqConstant.STRT_SIZE_REGION_2 /* 4 */)
					quantValues.getQbss()[cnt] = 1.0f;
				else
					quantValues.getQbss()[cnt] = 10.0f / (A[cnt] * (float) Math.log(quantValues.getVar()[cnt]));
			}
		}

		/* Set up output buffer. */
		// should be defined in calling function
		// sip = new short[width * height];
		sipIndex = 0;

		/* Set up 'm' table (these values are the reciprocal of 'm' in */
		/* the FBI spec). */
		m1 = (float) (1.0 / 1024.0);
		m2 = (float) (1.0 / 256.0);
		m3 = (float) (1.0 / 16.0);
		for (cnt = 0; cnt < WsqConstant.STRT_SIZE_REGION_2; cnt++)
			m[cnt] = m1;
		for (cnt = WsqConstant.STRT_SIZE_REGION_2; cnt < WsqConstant.STRT_SIZE_REGION_3; cnt++)
			m[cnt] = m2;
		for (cnt = WsqConstant.STRT_SIZE_REGION_3; cnt < WsqConstant.NUM_SUBBANDS; cnt++)
			m[cnt] = m3;

		j = 0;
		/* Initialize 'K0' and 'K1' lists. */
		K0len = 0;
		for (cnt = 0; cnt < WsqConstant.NUM_SUBBANDS; cnt++) {
			if (quantValues.getVar()[cnt] >= WsqConstant.VARIANCE_THRESH) {
				K0[K0len] = cnt;
				K1[K0len++] = cnt;
				/* Compute square root of subband variance. */
				sigma[cnt] = (float) Math.sqrt(quantValues.getVar()[cnt]);
			}
		}
		K = K1;
		Klen = K0len;

		while (true) {
			/* Compute new 'S' */
			S = 0.0f;
			for (i = 0; i < Klen; i++) {
				/* Remeber 'm' is the reciprocal of spec. */
				S += m[K[i]];
			}

			/* Compute product 'P' */
			P = 1.0f;
			for (i = 0; i < Klen; i++) {
				/* Remeber 'm' is the reciprocal of spec. */
				P *= Math.pow((sigma[K[i]] / quantValues.getQbss()[K[i]]), m[K[i]]);
			}

			/* Compute new 'q' */
			q = (float) ((Math.pow(2, ((quantValues.getCompressionBitRate() / S) - 1.0)) / 2.5)
					/ Math.pow(P, (1.0 / S)));

			/* Flag subbands with non-positive bitrate. */
			NP = new int[WsqConstant.NUM_SUBBANDS];
			NPlen = 0;
			for (i = 0; i < Klen; i++) {
				if ((quantValues.getQbss()[K[i]] / q) >= (5.0 * sigma[K[i]])) {
					NP[K[i]] = 1;// TRUE
					NPlen++;
				}
			}

			/* If list of subbands with non-positive bitrate is empty ... */
			if (NPlen == 0) {
				/* Then we are done, so break from while loop. */
				break;
			}

			/* Assign new subband set to previous set K minus subbands in set NP. */
			nK = K1;
			nKlen = 0;
			for (i = 0; i < Klen; i++) {
				if (NP[K[i]] == 0)
					nK[nKlen++] = K[i];
			}

			/* Assign new set as K. */
			K = nK;
			Klen = nKlen;

			/* Bump iteration counter. */
			j++;
		}

		/* Flag subbands that are in set 'K0' (the very first set). */
		nK = K1;
		nK = new int[WsqConstant.NUM_SUBBANDS];
		for (i = 0; i < K0len; i++) {
			nK[K0[i]] = 1;// TRUE;
		}
		/* Set 'Q' values. */
		for (cnt = 0; cnt < WsqConstant.NUM_SUBBANDS; cnt++) {
			if (nK[cnt] != 0)
				quantValues.getQbss()[cnt] /= q;
			else
				quantValues.getQbss()[cnt] = 0.0f;
			quantValues.getQzbs()[cnt] = (float) (1.2 * quantValues.getQbss()[cnt]);
		}

		/* Now ready to compute and store bin widths for subbands. */
		int fpIndex = 0;
		for (cnt = 0; cnt < WsqConstant.NUM_SUBBANDS; cnt++) {
			fpIndex = (quantizationTree[cnt].getY() * width) + quantizationTree[cnt].getX();

			if (quantValues.getQbss()[cnt] != 0.0) {
				zbin = (float) (quantValues.getQzbs()[cnt] / 2.0);

				for (row = 0; row < quantizationTree[cnt].getLenY(); row++, fpIndex += width
						- quantizationTree[cnt].getLenX()) {
					for (col = 0; col < quantizationTree[cnt].getLenX(); col++) {
						if (-zbin <= fImageData[fpIndex] && fImageData[fpIndex] <= zbin)
							sip[sipIndex] = 0;
						else if (fImageData[fpIndex] > 0.0)
							sip[sipIndex] = (short) (((fImageData[fpIndex] - zbin) / quantValues.getQbss()[cnt]) + 1.0);
						else
							sip[sipIndex] = (short) (((fImageData[fpIndex] + zbin) / quantValues.getQbss()[cnt]) - 1.0);
						sipIndex++;
						fpIndex++;
					}
				}
			}
		}

		cmpSize[0] = (int) (sipIndex - sip.length);
		return (0);
	}

	/************************************************************************/
	/* Compute quantized WSQ subband block sizes. */
	/************************************************************************/
	public static void quantizedBlockSizes(int[] qSize1, int[] qSize2, int[] qSize3, WsqQuantization quantValues,
			WsqWavletTree wavletTree[], int waveletTreeLen, WsqQuantizationTree quantizationTree[], int qTreeLen) {
		int nodeIndex;

		/* Compute temporary sizes of 3 WSQ subband blocks. */
		qSize1[0] = wavletTree[14].getLenX() * wavletTree[14].getLenY();
		qSize2[0] = (wavletTree[1].getLenX() * wavletTree[5].getLenY())
				+ (wavletTree[4].getLenX() * wavletTree[4].getLenY());
		qSize3[0] = (wavletTree[2].getLenX() * wavletTree[2].getLenY())
				+ (wavletTree[3].getLenX() * wavletTree[3].getLenY());

		/* Adjust size of quantized WSQ subband blocks. */
		for (nodeIndex = 0; nodeIndex < WsqConstant.STRT_SUBBAND_2; nodeIndex++)
			if (quantValues.getQbss()[nodeIndex] == 0.0)
				qSize1[0] -= (quantizationTree[nodeIndex].getLenX() * quantizationTree[nodeIndex].getLenY());

		for (nodeIndex = WsqConstant.STRT_SUBBAND_2; nodeIndex < WsqConstant.STRT_SUBBAND_3; nodeIndex++)
			if (quantValues.getQbss()[nodeIndex] == 0.0)
				qSize2[0] -= (quantizationTree[nodeIndex].getLenX() * quantizationTree[nodeIndex].getLenY());

		for (nodeIndex = WsqConstant.STRT_SUBBAND_3; nodeIndex < WsqConstant.STRT_SUBBAND_DEL; nodeIndex++)
			if (quantValues.getQbss()[nodeIndex] == 0.0)
				qSize3[0] -= (quantizationTree[nodeIndex].getLenX() * quantizationTree[nodeIndex].getLenY());
	}

	/*************************************/
	/* Routine to unquantize image data. */
	/*************************************/
	public static int unquantize(float[] fImageData, /* floating point image pointer */
			WsqTableDqt dqtTable, /* quantization table structure */
			WsqQuantizationTree quantizationTree[], /* quantization table structure */
			int qTreeLen, /* size of quantizationTree */
			long[] sip, /* quantized image pointer */
			int width, /* image width */
			int height) /* image height */
	{
		int row, col; /* cover counter and row/column counters */
		float qBinCenter; /* quantizer bin center */
		int fpIndex = 0; /* image index */
		int spIndex = 0;
		int cnt; /* subband counter */

		if (dqtTable.getDqtDef() != 1) {
			LOGGER.error(String.format("unquantize : quantization table parameters not defined"));
			return (WsqErrorCode.QUANTIZATION_TABLE_PARAMS_NOT_DEFINED.getErrorId());
		}

		qBinCenter = dqtTable.getBinCenter();
		for (cnt = 0; cnt < WsqConstant.NUM_SUBBANDS; cnt++) {
			if (dqtTable.getQBin()[cnt] == 0.0)
				continue;
			fpIndex = (quantizationTree[cnt].getY() * width) + quantizationTree[cnt].getX();

			for (row = 0; row < quantizationTree[cnt].getLenY(); row++, fpIndex += width
					- quantizationTree[cnt].getLenX()) {
				for (col = 0; col < quantizationTree[cnt].getLenX(); col++) {
					if (sip[spIndex] == 0)
						fImageData[fpIndex] = 0.0f;
					else if (sip[spIndex] > 0)
						fImageData[fpIndex] = (float) ((dqtTable.getQBin()[cnt] * ((float) sip[spIndex] - qBinCenter))
								+ (dqtTable.getZBin()[cnt] / 2.0));
					else if (sip[spIndex] < 0)
						fImageData[fpIndex] = (float) ((dqtTable.getQBin()[cnt] * ((float) sip[spIndex] + qBinCenter))
								- (dqtTable.getZBin()[cnt] / 2.0));
					else {
						LOGGER.error(String.format("unquantize : invalid quantization pixel value"));
						return (WsqErrorCode.INVALID_QUANTIZATION_PIXEL_VALUE.getErrorId());
					}
					fpIndex++;
					spIndex++;
				}
			}
		}

		return (0);
	}

	/************************************************************************/
	/* WSQ decompose the image. NOTE: this routine modifies and returns */
	/* the results in "fImageData". */
	/************************************************************************/
	public static int wsqDecompose(float[] fImageData, int width, int height, WsqWavletTree wavletTree[],
			int waveletTreeLen, float[] highFilter, int highSize, float[] lowFilter, int lowSize) {
		int numOfPixels, nodeIndex;
		float[] fImageDataTmp;
		int fDataIndex;

		numOfPixels = width * height;
		/* Allocate temporary floating point pixmap. */
		fImageDataTmp = new float[numOfPixels];

		/* Compute the Wavelet image decomposition. */
		for (nodeIndex = 0; nodeIndex < waveletTreeLen; nodeIndex++) {
			fDataIndex = (wavletTree[nodeIndex].getY() * width) + wavletTree[nodeIndex].getX();
			getLets(fImageDataTmp, 0, fImageData, fDataIndex, wavletTree[nodeIndex].getLenY(),
					wavletTree[nodeIndex].getLenX(), width, 1, highFilter, highSize, lowFilter, lowSize,
					wavletTree[nodeIndex].getInvRow());
			getLets(fImageData, fDataIndex, fImageDataTmp, 0, wavletTree[nodeIndex].getLenX(),
					wavletTree[nodeIndex].getLenY(), 1, width, highFilter, highSize, lowFilter, lowSize,
					wavletTree[nodeIndex].getInvCol());
		}

		return (0);
	}

	/************************************************************/
	/************************************************************/
	public static void getLets(float[] newData, int newDataIndex, /* image pointers for creating subband splits */
			float[] oldData, int oldDataIndex, int len1, /* temporary length parameters */
			int len2, int pitch, /* pitch gives next row_col to filter */
			int stride, /* stride gives next pixel to filter */
			float[] high, int highSize, /* NEW */
			float[] low, /* filter coefficients */
			int lowSize, /* NEW */
			int inv) /* spectral inversion? */
	{
		int lopassIndex = 0, hipassIndex = 0;
		int p0Index = 0, p1Index = 0;
		int pix, rw_cl; /* pixel counter and row/column counter */
		int i, daEven; /* even or odd row/column of pixels */
		int fiEven;
		int loc, hoc, nstr, pstr;
		int llen, hlen;
		int lpxstr, lspxstr;
		int lpxIndex = 0, lspxIndex = 0;
		int hpxstr, hspxstr;
		int hpxIndex = 0, hspxIndex = 0;
		int olle, ohle;
		int olre, ohre;
		int lle, lle2;
		int lre, lre2;
		int hle, hle2;
		int hre, hre2;

		daEven = len2 % 2;
		fiEven = lowSize % 2;

		if (fiEven != 0) {
			loc = (lowSize - 1) / 2;
			hoc = (highSize - 1) / 2 - 1;
			olle = 0;
			ohle = 0;
			olre = 0;
			ohre = 0;
		} else {
			loc = lowSize / 2 - 2;
			hoc = highSize / 2 - 2;
			olle = 1;
			ohle = 1;
			olre = 1;
			ohre = 1;

			if (loc == -1) {
				loc = 0;
				olle = 0;
			}
			if (hoc == -1) {
				hoc = 0;
				ohle = 0;
			}

			for (i = 0; i < highSize; i++)
				high[i] *= -1.0;
		}

		pstr = stride;
		nstr = -pstr;

		if (daEven != 0) {
			llen = (len2 + 1) / 2;
			hlen = llen - 1;
		} else {
			llen = len2 / 2;
			hlen = llen;
		}

		for (rw_cl = 0; rw_cl < len1; rw_cl++) {
			// use new data index
			if (inv != 0) {
				hipassIndex = newDataIndex + rw_cl * pitch;
				lopassIndex = hipassIndex;
			} else {
				lopassIndex = newDataIndex + rw_cl * pitch;
				hipassIndex = lopassIndex;
			}

			// use old data index
			p0Index = oldDataIndex + rw_cl * pitch;
			p1Index = p0Index + (len2 - 1) * stride;

			lspxIndex = p0Index + (loc * stride);
			lspxstr = nstr;
			lle2 = olle;
			lre2 = olre;

			hspxIndex = p0Index + (hoc * stride);
			hspxstr = nstr;
			hle2 = ohle;
			hre2 = ohre;

			for (pix = 0; pix < hlen; pix++) {
				lpxstr = lspxstr;
				lpxIndex = lspxIndex;
				lle = lle2;
				lre = lre2;
				newData[lopassIndex] = oldData[lpxIndex] * low[0];
				for (i = 1; i < lowSize; i++) {
					if (lpxIndex == p0Index) {
						if (lle != 0) {
							lpxstr = 0;
							lle = 0;
						} else
							lpxstr = pstr;
					}

					if (lpxIndex == p1Index) {
						if (lre != 0) {
							lpxstr = 0;
							lre = 0;
						} else
							lpxstr = nstr;
					}
					lpxIndex += lpxstr;
					newData[lopassIndex] += oldData[lpxIndex] * low[i];
				}
				lopassIndex += stride;

				hpxstr = hspxstr;
				hpxIndex = hspxIndex;
				hle = hle2;
				hre = hre2;
				newData[hipassIndex] = oldData[hpxIndex] * high[0];
				for (i = 1; i < highSize; i++) {
					if (hpxIndex == p0Index) {
						if (hle != 0) {
							hpxstr = 0;
							hle = 0;
						} else
							hpxstr = pstr;
					}
					if (hpxIndex == p1Index) {
						if (hre != 0) {
							hpxstr = 0;
							hre = 0;
						} else
							hpxstr = nstr;
					}
					hpxIndex += hpxstr;
					newData[hipassIndex] += oldData[hpxIndex] * high[i];
				}
				hipassIndex += stride;

				for (i = 0; i < 2; i++) {
					if (lspxIndex == p0Index) {
						if (lle2 != 0) {
							lspxstr = 0;
							lle2 = 0;
						} else
							lspxstr = pstr;
					}
					lspxIndex += lspxstr;

					if (hspxIndex == p0Index) {
						if (hle2 != 0) {
							hspxstr = 0;
							hle2 = 0;
						} else
							hspxstr = pstr;
					}
					hspxIndex += hspxstr;
				}
			}
			if (daEven != 0) {
				lpxstr = lspxstr;
				lpxIndex = lspxIndex;
				lle = lle2;
				lre = lre2;
				newData[lopassIndex] = oldData[lpxIndex] * low[0];
				for (i = 1; i < lowSize; i++) {
					if (lpxIndex == p0Index) {
						if (lle != 0) {
							lpxstr = 0;
							lle = 0;
						} else
							lpxstr = pstr;
					}

					if (lpxIndex == p1Index) {
						if (lre != 0) {
							lpxstr = 0;
							lre = 0;
						} else
							lpxstr = nstr;
					}
					lpxIndex += lpxstr;
					newData[lopassIndex] += oldData[lpxIndex] * low[i];
				}
				lopassIndex += stride;
			}
		}
		if (fiEven == 0) {
			for (i = 0; i < highSize; i++)
				high[i] *= -1.0;
		}
	}

	/************************************************************************/
	/* WSQ reconstructs the image. NOTE: this routine modifies and returns */
	/* the results in "fImageData". */
	/************************************************************************/
	public static int wsqReconstruct(float[] fImageData, int width, int height, WsqWavletTree wavletTree[],
			int waveletTreeLen, WsqTableDtt dttTable) {
		int numOfPixels, nodeIndex;
		float[] fImageDataTmp;
		int fdataIndex = 0;

		if (dttTable.getLowDef() != 1) {
			LOGGER.error(String.format("wsq_reconstruct : Lopass filter coefficients not defined"));
			return (WsqErrorCode.LOW_PASS_FILTER_COEFF_NOT_DEFINED.getErrorId());
		}
		if (dttTable.getHighDef() != 1) {
			LOGGER.error(String.format("wsq_reconstruct : Hipass filter coefficients not defined"));
			return (WsqErrorCode.HIGH_PASS_FILTER_COEFF_NOT_DEFINED.getErrorId());
		}

		numOfPixels = width * height;
		/* Allocate temporary floating point pixmap. */
		fImageDataTmp = new float[numOfPixels];

		/* Reconstruct floating point pixmap from wavelet subband data. */
		for (nodeIndex = waveletTreeLen - 1; nodeIndex >= 0; nodeIndex--) {
			fdataIndex = (wavletTree[nodeIndex].getY() * width) + wavletTree[nodeIndex].getX();
			joinLets(fImageDataTmp, 0, fImageData, fdataIndex, wavletTree[nodeIndex].getLenX(),
					wavletTree[nodeIndex].getLenY(), 1, width, dttTable.getHighFilter(), dttTable.getHighSize(),
					dttTable.getLowFilter(), dttTable.getLowSize(), wavletTree[nodeIndex].getInvCol());
			joinLets(fImageData, fdataIndex, fImageDataTmp, 0, wavletTree[nodeIndex].getLenY(),
					wavletTree[nodeIndex].getLenX(), width, 1, dttTable.getHighFilter(), dttTable.getHighSize(),
					dttTable.getLowFilter(), dttTable.getLowSize(), wavletTree[nodeIndex].getInvRow());
		}
		fImageDataTmp = null;
		return (0);
	}

	/****************************************************************/
	public static void joinLets(float[] newData, int newDataIndex, /* image pointers for creating subband splits */
			float[] oldData, int oldDataIndex, int len1, /* temporary length parameters */
			int len2, int pitch, /* pitch gives next row_col to filter */
			int stride, /* stride gives next pixel to filter */
			float[] high, int highSize, /* High filter coefficients */
			float[] low, int lowSize, /* Low filter coefficients */
			int inv) /* spectral inversion? */
	{
		int lp0Index = 0, lp1Index = 0;
		int hp0Index = 0, hp1Index = 0;
		int lopassIndex = 0, hipassIndex = 0;
		int limgIndex = 0, himgIndex = 0;
		int pix, cl_rw; /* pixel counter and column/row counter */
		int i, daEven; /* if "scanline" is even or odd and */
		int loc, hoc;
		int hlen, llen;
		int nstr, pstr;
		int tap;
		int fiEven;
		int olle, ohle, olre, ohre;
		int lle, lle2, lre, lre2;
		int hle, hle2, hre, hre2;
		int lpxIndex = 0, lspxIndex = 0;
		int lpxstr, lspxstr;
		int lstap, lotap;
		int hpxIndex = 0, hspxIndex = 0;
		int hpxstr, hspxstr;
		int hstap, hotap;
		int asym, fhre = 0, ofhre;
		float ssfac, osfac, sfac;

		daEven = len2 % 2;
		fiEven = lowSize % 2;
		pstr = stride;
		nstr = -pstr;
		if (daEven != 0) {
			llen = (len2 + 1) / 2;
			hlen = llen - 1;
		} else {
			llen = len2 / 2;
			hlen = llen;
		}

		if (fiEven != 0) {
			asym = 0;
			ssfac = 1.0f;
			ofhre = 0;
			loc = (lowSize - 1) / 4;
			hoc = (highSize + 1) / 4 - 1;
			lotap = ((lowSize - 1) / 2) % 2;
			hotap = ((highSize + 1) / 2) % 2;
			if (daEven != 0) {
				olle = 0;
				olre = 0;
				ohle = 1;
				ohre = 1;
			} else {
				olle = 0;
				olre = 1;
				ohle = 1;
				ohre = 0;
			}
		} else {
			asym = 1;
			ssfac = -1.0f;
			ofhre = 2;
			loc = lowSize / 4 - 1;
			hoc = highSize / 4 - 1;
			lotap = (lowSize / 2) % 2;
			hotap = (highSize / 2) % 2;
			if (daEven != 0) {
				olle = 1;
				olre = 0;
				ohle = 1;
				ohre = 1;
			} else {
				olle = 1;
				olre = 1;
				ohle = 1;
				ohre = 1;
			}

			if (loc == -1) {
				loc = 0;
				olle = 0;
			}

			if (hoc == -1) {
				hoc = 0;
				ohle = 0;
			}

			for (i = 0; i < highSize; i++)
				high[i] *= -1.0;
		}

		for (cl_rw = 0; cl_rw < len1; cl_rw++) {
			// use new data index
			limgIndex = newDataIndex + cl_rw * pitch;
			himgIndex = limgIndex;
			newData[himgIndex] = 0.0f;
			newData[himgIndex + stride] = 0.0f;
			// Using old data index
			if (inv != 0) {
				hipassIndex = oldDataIndex + cl_rw * pitch;
				lopassIndex = hipassIndex + stride * hlen;
			} else {
				lopassIndex = oldDataIndex + cl_rw * pitch;
				hipassIndex = lopassIndex + stride * llen;
			}

			lp0Index = lopassIndex;
			lp1Index = lp0Index + (llen - 1) * stride;
			lspxIndex = lp0Index + (loc * stride);
			lspxstr = nstr;
			lstap = lotap;
			lle2 = olle;
			lre2 = olre;

			hp0Index = hipassIndex;
			hp1Index = hp0Index + (hlen - 1) * stride;
			hspxIndex = hp0Index + (hoc * stride);
			hspxstr = nstr;
			hstap = hotap;
			hle2 = ohle;
			hre2 = ohre;
			osfac = ssfac;

			for (pix = 0; pix < hlen; pix++) {
				for (tap = lstap; tap >= 0; tap--) {
					lle = lle2;
					lre = lre2;
					lpxIndex = lspxIndex;
					lpxstr = lspxstr;

					newData[limgIndex] = oldData[lpxIndex] * low[tap];
					for (i = tap + 2; i < lowSize; i += 2) {
						if (lpxIndex == lp0Index) {
							if (lle != 0) {
								lpxstr = 0;
								lle = 0;
							} else
								lpxstr = pstr;
						}

						if (lpxIndex == lp1Index) {
							if (lre != 0) {
								lpxstr = 0;
								lre = 0;
							} else
								lpxstr = nstr;
						}
						lpxIndex += lpxstr;
						newData[limgIndex] += oldData[lpxIndex] * low[i];
					}
					limgIndex += stride;
				}

				if (lspxIndex == lp0Index) {
					if (lle2 != 0) {
						lspxstr = 0;
						lle2 = 0;
					} else
						lspxstr = pstr;
				}

				lspxIndex += lspxstr;
				lstap = 1;

				for (tap = hstap; tap >= 0; tap--) {
					hle = hle2;
					hre = hre2;
					hpxIndex = hspxIndex;
					hpxstr = hspxstr;
					fhre = ofhre;
					sfac = osfac;

					for (i = tap; i < highSize; i += 2) {
						if (hpxIndex == hp0Index) {
							if (hle != 0) {
								hpxstr = 0;
								hle = 0;
							} else {
								hpxstr = pstr;
								sfac = 1.0f;
							}
						}
						if (hpxIndex == hp1Index) {
							if (hre != 0) {
								hpxstr = 0;
								hre = 0;
								if (asym != 0 && daEven != 0) {
									hre = 1;
									fhre--;
									sfac = (float) fhre;
									if (sfac == 0.0f)
										hre = 0;
								}
							} else {
								hpxstr = nstr;
								if (asym != 0)
									sfac = -1.0f;
							}
						}
						newData[himgIndex] += oldData[hpxIndex] * high[i] * sfac;
						hpxIndex += hpxstr;
					}
					himgIndex += stride;
				}
				if (hspxIndex == hp0Index) {
					if (hle2 != 0) {
						hspxstr = 0;
						hle2 = 0;
					} else {
						hspxstr = pstr;
						osfac = 1.0f;
					}
				}
				hspxIndex += hspxstr;
				hstap = 1;
			}

			if (daEven != 0) {
				if (lotap != 0)
					lstap = 1;
				else
					lstap = 0;
			} else {
				if (lotap != 0)
					lstap = 2;
				else
					lstap = 1;
			}

			for (tap = 1; tap >= lstap; tap--) {
				lle = lle2;
				lre = lre2;
				lpxIndex = lspxIndex;
				lpxstr = lspxstr;

				newData[limgIndex] = oldData[lpxIndex] * low[tap];
				for (i = tap + 2; i < lowSize; i += 2) {
					if (lpxIndex == lp0Index) {
						if (lle != 0) {
							lpxstr = 0;
							lle = 0;
						} else
							lpxstr = pstr;
					}
					if (lpxIndex == lp1Index) {
						if (lre != 0) {
							lpxstr = 0;
							lre = 0;
						} else
							lpxstr = nstr;
					}
					lpxIndex += lpxstr;
					newData[limgIndex] += oldData[lpxIndex] * low[i];
				}
				limgIndex += stride;
			}

			if (daEven != 0) {
				if (hotap != 0)
					hstap = 1;
				else
					hstap = 0;

				if (highSize == 2) {
					hspxIndex -= hspxstr;
					fhre = 1;
				}
			} else {
				if (hotap != 0)
					hstap = 2;
				else
					hstap = 1;
			}

			for (tap = 1; tap >= hstap; tap--) {
				hle = hle2;
				hre = hre2;
				hpxIndex = hspxIndex;
				hpxstr = hspxstr;
				sfac = osfac;
				if (highSize != 2)
					fhre = ofhre;

				for (i = tap; i < highSize; i += 2) {
					if (hpxIndex == hp0Index) {
						if (hle != 0) {
							hpxstr = 0;
							hle = 0;
						} else {
							hpxstr = pstr;
							sfac = 1.0f;
						}
					}
					if (hpxIndex == hp1Index) {
						if (hre != 0) {
							hpxstr = 0;
							hre = 0;
							if (asym != 0 && daEven != 0) {
								hre = 1;
								fhre--;
								sfac = (float) fhre;
								if (sfac == 0.0)
									hre = 0;
							}
						} else {
							hpxstr = nstr;
							if (asym != 0)
								sfac = -1.0f;
						}
					}
					newData[himgIndex] += oldData[hpxIndex] * high[i] * sfac;
					hpxIndex += hpxstr;
				}
				himgIndex += stride;
			}
		}

		if (fiEven == 0) {
			for (i = 0; i < highSize; i++)
				high[i] *= -1.0;
		}
	}

	/*****************************************************/
	/* Routine to execute an integer sign determination */
	/*****************************************************/
	public static int intSign(int power) /* "sign" power */ {
		int cnt, num = -1; /* counter and sign return value */

		if (power == 0)
			return 1;

		for (cnt = 1; cnt < power; cnt++)
			num *= -1;

		return num;
	}

	/*************************************************************/
	/* Computes size of compressed image file including headers, */
	/* tables, and parameters. */
	/*************************************************************/
	public static int imageSize(int blockLen, /* length of the compressed blocks */
			short[] huffBits1, /* huffman table parameters */
			short[] huffBits2) {
		int totalSize, cnt;

		totalSize = blockLen; /* size of three compressed blocks */

		totalSize += 58; /* size of transform table */
		totalSize += 389; /* size of quantization table */
		totalSize += 17; /* size of frame header */
		totalSize += 3; /* size of block 1 */
		totalSize += 3; /* size of block 2 */
		totalSize += 3; /* size of block 3 */

		totalSize += 3; /* size hufftable variable and hufftable number */
		totalSize += 16; /* size of huffBits1 */
		for (cnt = 1; cnt < 16; cnt++)
			totalSize += huffBits1[cnt]; /* size of huffvalues1 */

		totalSize += 3; /* size hufftable variable and hufftable number */
		totalSize += 16; /* size of huffBits1 */
		for (cnt = 1; cnt < 16; cnt++)
			totalSize += huffBits2[cnt]; /* size of huffvalues2 */

		totalSize += 20; /* SOI,SOF,SOB(3),DTT,DQT,DHT(2),EOI */
		return totalSize;
	}

	/*************************************************************/
	/* Initializes memory used by the WSQ decoder. */
	/*************************************************************/
	public static void initWsqDecoderResources(WsqTableDtt dttTable) {
		/* Init dymanically allocated members to NULL */
		/* for proper memory management in: */
		/* read_transform_table() */
		/* getc_transform_table() */
		/* free_wsq_resources() */
		dttTable.setLowFilter(null);
		dttTable.setHighFilter(null);
	}

	/*************************************************************/
	/* Deallocates memory used by the WSQ decoder. */
	/*************************************************************/
	public static void freeWsqDecoderResources(WsqTableDtt dttTable) {
		if (dttTable.getLowFilter() != null) {
			dttTable.setLowFilter(null);
		}

		if (dttTable.getHighFilter() != null) {
			dttTable.setHighFilter(null);
		}
	}
}
