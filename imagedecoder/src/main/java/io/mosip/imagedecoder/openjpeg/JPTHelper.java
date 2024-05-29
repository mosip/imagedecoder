package io.mosip.imagedecoder.openjpeg;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;

import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.CodecContextInfo;
import io.mosip.imagedecoder.model.openjpeg.JPTMessageHeader;

public class JPTHelper {
	private Logger logger = ImageDecoderLogger.getLogger(JPTHelper.class);
	/*
	 * Read the information contains in VBAS [JPP/JPT stream message header] Store
	 * information (7 bits) in value
	 *
	 */

	// Static variable reference of singleInstance of type Singleton
	private static JPTHelper singleInstance = null;

	private JPTHelper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized JPTHelper getInstance() {
		if (singleInstance == null)
			singleInstance = new JPTHelper();

		return singleInstance;
	}

	private long jptReadVBASInfo(Cio cio, long value) {
		byte element;

		element = (byte) CioHelper.getInstance().cioRead(cio, 1);
		while ((element >> 7) == 1) {
			value = (value << 7);
			value |= (element & 0x7f);
			element = (byte) CioHelper.getInstance().cioRead(cio, 1);
		}
		value = (value << 7);
		value |= (element & 0x7f);

		return value;
	}

	/*
	 * Initialize the value of the message header structure
	 *
	 */
	public void jptInitMsgHeader(JPTMessageHeader header) {
		header.setId(0); /* In-class Identifier */
		header.setLastByte(0); /* Last byte information */
		header.setClassId(0); /* Class Identifier */
		header.setCSnId(0); /* CSn : index identifier */
		header.setMsgOffset(0); /* Message offset */
		header.setMsgLength(0); /* Message length */
		header.setLayerNb(0); /* Auxiliary for JPP case */
	}

	/*
	 * Re-initialize the value of the message header structure
	 *
	 * Only parameters always present in message header
	 *
	 */
	private void jptReInitMsgHeader(JPTMessageHeader header) {
		header.setId(0); /* In-class Identifier */
		header.setLastByte(0); /* Last byte information */
		header.setMsgOffset(0); /* Message offset */
		header.setMsgLength(0); /* Message length */
	}

	/*
	 * Read the message header for a JPP/JPT - stream
	 *
	 */
	@SuppressWarnings({ "java:S1172" })
	public void jptReadMsgHeader(CodecContextInfo cinfo, Cio cio, JPTMessageHeader header) {
		byte element, classId = 0, cSn = 0; // NOSONAR
		jptReInitMsgHeader(header);

		/* ------------- */
		/* VBAS : Bin-ID */
		/* ------------- */
		element = (byte) CioHelper.getInstance().cioRead(cio, 1);

		/* See for Class and CSn */
		switch ((element >> 5) & 0x03) {
		case 0:
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Forbidden value encounter in message header !!");
			break;
		case 1:
			classId = 0;
			cSn = 0;
			break;
		case 2:
			classId = 1;
			cSn = 0;
			break;
		case 3:
			classId = 1;
			cSn = 1;
			break;
		default:
			break;
		}

		/* see information on bits 'c' [p 10 : A.2.1 general, ISO/IEC FCD 15444-9] */
		if (((element >> 4) & 0x01) == 1)
			header.setLastByte(1);

		/* In-class identifier */
		header.setId(header.getId() | (element & 0x0f));
		if ((element >> 7) == 1)
			header.setId(jptReadVBASInfo(cio, header.getId()));

		/* ------------ */
		/* VBAS : class */
		/* ------------ */
		if (classId == 1) {
			header.setClassId(0);
			header.setClassId(jptReadVBASInfo(cio, header.getClassId()));
		}

		/* ---------- */
		/* VBAS : cSn */
		/* ---------- */
		if (cSn == 1) {
			header.setCSnId(0);
			header.setCSnId(jptReadVBASInfo(cio, header.getCSnId()));
		}

		/* ----------------- */
		/* VBAS : msgOffset */
		/* ----------------- */
		header.setMsgOffset(jptReadVBASInfo(cio, header.getMsgOffset()));

		/* ----------------- */
		/* VBAS : msgLength */
		/* ----------------- */
		header.setMsgLength(jptReadVBASInfo(cio, header.getMsgLength()));

		/* ---------- */
		/* VBAS : Aux */
		/* ---------- */
		if ((header.getClassId() & 0x01) == 1) {
			header.setLayerNb(0);
			header.setLayerNb(jptReadVBASInfo(cio, header.getLayerNb()));
		}
	}
}
