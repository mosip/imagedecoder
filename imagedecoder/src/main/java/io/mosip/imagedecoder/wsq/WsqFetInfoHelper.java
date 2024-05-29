package io.mosip.imagedecoder.wsq;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import io.mosip.imagedecoder.model.ByteBufferContext;
import io.mosip.imagedecoder.model.wsq.WsqFet;
import io.mosip.imagedecoder.model.wsq.WsqHeaderForm;
import io.mosip.imagedecoder.util.ByteStreamUtil;
import io.mosip.imagedecoder.util.StringUtil;

public class WsqFetInfoHelper {
	// Static variable reference of singleInstance of type Singleton
	private static WsqFetInfoHelper singleInstance = null;

	private WsqFetInfoHelper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized WsqFetInfoHelper getInstance() {
		if (singleInstance == null)
			singleInstance = new WsqFetInfoHelper();

		return singleInstance;
	}

	/************************************************************************/
	public int getNistComments(WsqFet nistcom, byte[] idata, int ilen) {
		int ret;

		/* Get ppi from NISTCOM, if one exists ... */
		if ((ret = WsqTableIOHelper.getInstance().getWsqNistCom(nistcom, idata, ilen)) != 0)
			return ret;

		return 0;
	}

	/************************************************************************/
	public int getWsqPPI(WsqFet nistcom, int[] ppi) {
		int ret;
		StringBuilder value = new StringBuilder();

		if (nistcom != null) {
			if ((ret = WsqFetHelper.getInstance().extractFet(value, WsqConstant.NCM_PPI.toCharArray(), nistcom)) != 0) {
				return ret;
			}
			if (value.toString().length() != 0) {
				ppi[0] = StringUtil.getInstance().atoi(value.toString().toCharArray());
			}
			/* Otherwise, PPI not in NISTCOM, so ppi = -1. */
			else
				ppi[0] = -1;
		}
		/* Otherwise, NISTCOM does NOT exist, so ppi = -1. */
		else
			ppi[0] = -1;

		return 0;
	}

	/************************************************************************/
	public int getWsqLossyFlag(WsqFet nistcom, int[] lossy) {
		int ret;
		StringBuilder value = new StringBuilder();

		if (nistcom != null) {
			if ((ret = WsqFetHelper.getInstance().extractFet(value, WsqConstant.NCM_LOSSY.toCharArray(),
					nistcom)) != 0) {
				return ret;
			}
			if (value.toString().length() != 0) {
				lossy[0] = StringUtil.getInstance().atoi(value.toString().toCharArray());
			}
			/* Otherwise, lossy not in NISTCOM, so lossy = 1. */
			else
				lossy[0] = 1;
		}
		/* Otherwise, NISTCOM does NOT exist, so lossy = 1. */
		else
			lossy[0] = 1;

		return 0;
	}

	/************************************************************************/
	public int getWsqBitRate(WsqFet nistcom, double[] bitRate) {
		int ret;
		StringBuilder value = new StringBuilder();

		if (nistcom != null) {
			if ((ret = WsqFetHelper.getInstance().extractFet(value, WsqConstant.NCM_WSQ_RATE.toCharArray(),
					nistcom)) != 0) {
				return ret;
			}
			if (value.toString().length() != 0) {
				bitRate[0] = StringUtil.getInstance().atof(value.toString());
			}
			/* Otherwise, BitRate not in NISTCOM, so BitRate = 0.0. */
			else
				bitRate[0] = 0.0;
		}
		/* Otherwise, NISTCOM does NOT exist, so BitRate = 0.0. */
		else
			bitRate[0] = 0.0;

		return 0;
	}

	/************************************************************************/
	public int getWsqColorSpace(WsqFet nistcom, StringBuilder colorSpace) {
		int ret;
		StringBuilder value = new StringBuilder();

		if (nistcom != null) {
			if ((ret = WsqFetHelper.getInstance().extractFet(value, WsqConstant.NCM_COLORSPACE.toCharArray(),
					nistcom)) != 0) {
				return ret;
			}
			if (value.toString().length() != 0) {
				colorSpace.append(value.toString());
			}
			/* Otherwise, colorSpace not in NISTCOM, so colorSpace = empty */
			else
				colorSpace.append("");
		}
		/* Otherwise, NISTCOM does NOT exist, so colorSpace = empty */
		else
			colorSpace.append("");

		return 0;
	}

	/******************************************************************/
	/* Routine to read in frame header parameters from memory buffer. */
	/******************************************************************/
	@SuppressWarnings({ "java:S1659", "unused" })
	public int getWsqFrameHeader(WsqHeaderForm headerForm, /* frame header structure */
			ByteBufferContext cbufptr /* current byte in input buffer */
	) {
		int hdrSize, shortData; /* header size and data pointer */
		int scale; /* exponent scaling parameter */

		hdrSize = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);
		headerForm.setBlack((byte) ByteStreamUtil.getInstance().getUByte(cbufptr));
		headerForm.setWhite((byte) ByteStreamUtil.getInstance().getUByte(cbufptr));
		headerForm.setHeight((int) ByteStreamUtil.getInstance().getUShort(cbufptr));
		headerForm.setWidth((int) ByteStreamUtil.getInstance().getUShort(cbufptr));

		scale = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
		shortData = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);

		headerForm.getMShift()[0] = shortData;
		while (scale > 0) {
			headerForm.getMShift()[0] = (headerForm.getMShift()[0] / 10.0f);
			scale--;
		}

		scale = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
		shortData = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);

		headerForm.getRScale()[0] = shortData;
		while (scale > 0) {
			headerForm.getRScale()[0] = (headerForm.getRScale()[0] / 10.0f);
			scale--;
		}

		headerForm.setWsqEncoder((int) ByteStreamUtil.getInstance().getUByte(cbufptr));
		headerForm.setSoftware(ByteStreamUtil.getInstance().getUShort(cbufptr));

		return 0;
	}
}
