package io.mosip.imagedecoder.wsq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import io.mosip.imagedecoder.model.ByteBufferContext;
import io.mosip.imagedecoder.model.wsq.WsqFet;
import io.mosip.imagedecoder.model.wsq.WsqHeaderForm;
import io.mosip.imagedecoder.util.ByteStreamUtil;
import io.mosip.imagedecoder.util.StringUtil;

public class WsqFetInfoHelper {
	private Logger LOGGER = LoggerFactory.getLogger(WsqFetInfoHelper.class);
	// Static variable reference of singleInstance of type Singleton
    private static WsqFetInfoHelper singleInstance = null;    
    private WsqFetInfoHelper()
	{ 
		super ();
	} 
  
	//synchronized method to control simultaneous access 
	public static synchronized WsqFetInfoHelper getInstance()
	{ 
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
				nistcom = null;
				return ret;
			}
			if (value != null && value.toString().length() != 0) {
				ppi[0] = StringUtil.atoi(value.toString().toCharArray());
				value = null;
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
			if ((ret = WsqFetHelper.getInstance().extractFet(value, WsqConstant.NCM_LOSSY.toCharArray(), nistcom)) != 0) {
				nistcom = null;
				return ret;
			}
			if (value != null && value.toString().length() != 0) {
				lossy[0] = StringUtil.atoi(value.toString().toCharArray());
				value = null;
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
			if ((ret = WsqFetHelper.getInstance().extractFet(value, WsqConstant.NCM_WSQ_RATE.toCharArray(), nistcom)) != 0) {
				nistcom = null;
				return ret;
			}
			if (value != null && value.toString().length() != 0) {
				bitRate[0] = StringUtil.atof(value.toString());
				value = null;
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
			if ((ret = WsqFetHelper.getInstance().extractFet(value, WsqConstant.NCM_COLORSPACE.toCharArray(), nistcom)) != 0) {
				nistcom = null;
				return ret;
			}
			if (value != null && value.toString().length() != 0) {
				colorSpace.append(value.toString());
				value = null;
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
	public int getWsqFrameHeader(WsqHeaderForm headerForm, /* frame header structure */
			ByteBufferContext cbufptr /* current byte in input buffer */
			) {
		int hdrSize, shortData; /* header size and data pointer */
		int scale; /* exponent scaling parameter */

		//LOGGER.debug(String.format("Reading frame header."));

		hdrSize = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);
		headerForm.setBlack((byte) ByteStreamUtil.getInstance().getUByte(cbufptr));
		headerForm.setWhite((byte) ByteStreamUtil.getInstance().getUByte(cbufptr));
		headerForm.setHeight((int) ByteStreamUtil.getInstance().getUShort(cbufptr));
		headerForm.setWidth((int) ByteStreamUtil.getInstance().getUShort(cbufptr));

		scale = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
		shortData = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);

		headerForm.getMShift()[0] = (float) shortData;
		while (scale > 0) {
			headerForm.getMShift()[0] = (float) (headerForm.getMShift()[0] / 10.0f);
			scale--;
		}

		scale = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
		shortData = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);

		headerForm.getRScale()[0] = (float) shortData;
		while (scale > 0) {
			headerForm.getRScale()[0] = (float) (headerForm.getRScale()[0] / 10.0f);
			scale--;
		}

		headerForm.setWsqEncoder((int) ByteStreamUtil.getInstance().getUByte(cbufptr));
		headerForm.setSoftware(ByteStreamUtil.getInstance().getUShort(cbufptr));

		/*
		LOGGER.debug(String.format(
				"black = %d :: white = %u :: w = %d :: h = %d :: m_shift = %f :: r_scale = %f :: WSQ_encoder = %d :: Software = %d",
				headerForm.getBlack(), headerForm.getWhite(), headerForm.getWidth(), headerForm.getHeight(),
				headerForm.getMShift()[0], headerForm.getRScale()[0], headerForm.getWsqEncoder(),
				headerForm.getSoftware()));
		LOGGER.debug(String.format("Finished reading frame header."));
		*/
		return 0;
	}
}
