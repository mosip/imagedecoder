package io.mosip.imagedecoder.wsq;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import io.mosip.imagedecoder.constant.wsq.WsqErrorCode;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;
import io.mosip.imagedecoder.model.ByteBufferContext;
import io.mosip.imagedecoder.model.wsq.WsqFet;
import io.mosip.imagedecoder.model.wsq.WsqHeaderForm;
import io.mosip.imagedecoder.model.wsq.WsqTableDht;
import io.mosip.imagedecoder.model.wsq.WsqTableDqt;
import io.mosip.imagedecoder.model.wsq.WsqTableDtt;
import io.mosip.imagedecoder.util.ByteStreamUtil;
import io.mosip.imagedecoder.util.StringUtil;
import io.mosip.imagedecoder.util.wsq.WsqUtil;
import io.mosip.kernel.core.logger.spi.Logger;

public class WsqTableIOHelper {
	private Logger logger = ImageDecoderLogger.getLogger(WsqTableIOHelper.class);
	// Static variable reference of singleInstance of type Singleton
	private static WsqTableIOHelper singleInstance = null;

	private WsqTableIOHelper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized WsqTableIOHelper getInstance() {
		if (singleInstance == null)
			singleInstance = new WsqTableIOHelper();

		return singleInstance;
	}

	/******************************************************/
	/* Routine to read in WSQ markers from memory buffer. */
	/******************************************************/
	public int getWsqMarker(int[] marker, /* marker read */
			int type, /* type of markers that could be found */
			ByteBufferContext cbufptr /* current byte in input buffer */
	) {
		if (ByteStreamUtil.getInstance().getBytesLeft(cbufptr) < 2) {
			return WsqConstant.AVERROR_INVALIDDATA;
		}

		marker[0] = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);

		switch (type) {
		case WsqConstant.SOI_WSQ:
			if (marker[0] != WsqConstant.SOI_WSQ) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, MessageFormat.format("getWsqMarker : No SOI marker. {0}", marker[0]));
				return (WsqErrorCode.NO_SOI_MARKER.getErrorId());
			}
			return 0;
		case WsqConstant.TBLS_N_SOF:
			if (marker[0] != WsqConstant.DTT_WSQ && marker[0] != WsqConstant.DQT_WSQ && marker[0] != WsqConstant.DHT_WSQ
					&& marker[0] != WsqConstant.SOF_WSQ && marker[0] != WsqConstant.COM_WSQ) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, "getWsqMarker : No SOF, Table, or comment markers.");
				return (WsqErrorCode.NO_SOF_TABLE_COMMENT_MARKER.getErrorId());
			}
			return 0;
		case WsqConstant.TBLS_N_SOB:
			if (marker[0] != WsqConstant.DTT_WSQ && marker[0] != WsqConstant.DQT_WSQ && marker[0] != WsqConstant.DHT_WSQ
					&& marker[0] != WsqConstant.SOB_WSQ && marker[0] != WsqConstant.COM_WSQ) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format(
						"getWsqMarker : No SOB, Table, or comment markers. {0}", marker[0]));
				return (WsqErrorCode.NO_SOB_TABLE_COMMENT_MARKER.getErrorId());
			}
			return 0;
		case WsqConstant.ANY_WSQ:
			if ((marker[0] & 0xff00) != 0xff00) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, MessageFormat.format("getWsqMarker : no marker found. {0}", marker[0]));
				return (WsqErrorCode.NO_MARKER_FOUND.getErrorId());
			}
			if ((marker[0] < WsqConstant.SOI_WSQ) || (marker[0] > WsqConstant.COM_WSQ)) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, MessageFormat.format("getWsqMarker : {0} not a valid marker", marker[0]));
				return (WsqErrorCode.NOT_VALID_MARKER_FOUND.getErrorId());
			}
			return 0;
		default:
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, MessageFormat.format("getWsqMarker : Invalid marker -> {0}", marker[0]));
			return (WsqErrorCode.INVALID_MARKER_FOUND.getErrorId());
		}
	}

	/*******************************************************/
	/* Routine to read specified table from memory buffer. */
	/*******************************************************/
	@SuppressWarnings({ "java:S2583" })
	public int getWsqTable(int[] marker, /* WSQ marker */
			WsqTableDtt dttTable, /* transform table structure */
			WsqTableDqt dqtTable, /* quantization table structure */
			WsqTableDht[] dhtTable, /* huffman table structure */
			ByteBufferContext cbufptr /* current byte in input buffer */
	) {
		int ret;

		switch (marker[0]) {
		case WsqConstant.DTT_WSQ:
			if ((ret = getTransformTable(dttTable, cbufptr)) != 0)
				return ret;
			return 0;
		case WsqConstant.DQT_WSQ:
			ret = getQuantizationTable(dqtTable, cbufptr);
			if (ret != 0)
				return ret;
			return 0;
		case WsqConstant.DHT_WSQ:
			if ((ret = getWsqHuffmanTable(dhtTable, cbufptr)) != 0)
				return ret;
			return 0;
		case WsqConstant.COM_WSQ:
			// header size
			int hdrSize = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);
			/* cs = hdrSize - sizeof(length value) */
			int cs = hdrSize - 2;
			/* Allocate including a possible NULL terminator. */
			byte[] comment = new byte[cs + 1];
			ret = getComment(comment, cbufptr);
			if (ret != 0)
				return ret;
			return 0;
		default:
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, MessageFormat.format("getWsqTable : Invalid table defined: {0}", marker[0]));
			return (WsqErrorCode.INVALID_TABLE_DEFINED.getErrorId());
		}
	}

	/************************************************************************/
	/* Routine to read in quantization table parameters from memory buffer. */
	/************************************************************************/
	@SuppressWarnings({ "java:S1659", "unused" })
	private int getQuantizationTable(WsqTableDqt dqtTable, /* quatization table structure */
			ByteBufferContext cbufptr /* current byte in input buffer */
	) {
		int ret;
		int hdrSize; /* header size */
		int cnt, shortData; /* counter and temp short data */
		int[] scale = new int[1]; /* scaling parameter */

		hdrSize = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);
		scale[0] = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
		shortData = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);

		dqtTable.setBinCenter(shortData);
		while (scale[0] > 0) {
			dqtTable.setBinCenter(dqtTable.getBinCenter() / 10.0f);
			scale[0]--;
		}

		for (cnt = 0; cnt < 64; cnt++) {
			scale[0] = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
			shortData = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);

			dqtTable.getQBin()[cnt] = shortData;
			while (scale[0] > 0) {
				dqtTable.getQBin()[cnt] /= 10.0;
				scale[0]--;
			}

			scale[0] = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
			shortData = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);

			dqtTable.getZBin()[cnt] = shortData;
			while (scale[0] > 0) {
				dqtTable.getZBin()[cnt] /= 10.0f;
				scale[0]--;
			}
		}
		dqtTable.setDqtDef(1);

		return 0;
	}

	/*******************************************************************/
	/* Routine to read in huffman table parameters from memory buffer. */
	/*******************************************************************/
	private int getWsqHuffmanTable(WsqTableDht[] dhtTable, /* huffman table structure */
			ByteBufferContext cbufptr /* current byte in input buffer */
	) {
		int ret;
		int[] tableId = new int[1]; /* huffman table indicator */
		int[] huffbits = new int[WsqConstant.MAX_HUFFBITS];
		/* Could allocate only the amount needed ... then we wouldn't */
		/* need to pass MAX_HUFFCOUNTS. */
		int[] huffvalues = new int[WsqConstant.MAX_HUFFCOUNTS_WSQ + 1];
		int[] bytesLeft = new int[1];

		/* First time, read table len. */
		if ((ret = getHuffmanTable(tableId, huffbits, huffvalues, WsqConstant.MAX_HUFFCOUNTS_WSQ, cbufptr,
				WsqConstant.READ_TABLE_LEN, bytesLeft)) != 0)
			return ret;

		/* Store table into global structure list. */
		System.arraycopy(huffbits, 0, dhtTable[tableId[0]].getHuffBits(), 0, WsqConstant.MAX_HUFFBITS);
		System.arraycopy(huffvalues, 0, dhtTable[tableId[0]].getHuffValues(), 0, WsqConstant.MAX_HUFFCOUNTS_WSQ + 1);
		dhtTable[tableId[0]].setTableDef((byte) 1);

		while (bytesLeft[0] != 0) {
			huffbits = new int[WsqConstant.MAX_HUFFBITS];
			huffvalues = new int[WsqConstant.MAX_HUFFCOUNTS_WSQ + 1];
			/* Read next table without reading table len. */
			if ((ret = getHuffmanTable(tableId, huffbits, huffvalues, WsqConstant.MAX_HUFFCOUNTS_WSQ, cbufptr,
					WsqConstant.NO_READ_TABLE_LEN, bytesLeft)) != 0)
				return ret;

			/* If table is already defined ... */
			if (dhtTable[tableId[0]].getTableDef() != 0) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format(
						"getWsqHuffmanTable : huffman table ID = {0} already defined ", tableId[0]));
				return (WsqErrorCode.TABLE_ID_ALREADY_DEFINED.getErrorId());
			}

			/* Store table into global structure list. */
			System.arraycopy(huffbits, 0, dhtTable[tableId[0]].getHuffBits(), 0, WsqConstant.MAX_HUFFBITS);
			System.arraycopy(huffvalues, 0, dhtTable[tableId[0]].getHuffValues(), 0,
					WsqConstant.MAX_HUFFCOUNTS_WSQ + 1);
			dhtTable[tableId[0]].setTableDef(1);
		}

		return 0;
	}

	/*********************************************************************/
	/* Routine to read in transform table parameters from memory buffer. */
	/*********************************************************************/
	@SuppressWarnings({ "java:S1659", "java:S6541", "java:S3776", "unused" })
	private int getTransformTable(WsqTableDtt dttTable, /* transform table structure */
			ByteBufferContext cbufptr /* current byte in input buffer */
	) {
		int ret;
		int hdrSize; /* header size */
		float[] aLowfilter, aHighFilter; /* unexpanded filter coefficients */
		int aSize; /* size of unexpanded coefficients */
		int cnt = 0; /* counter */
		long shortData; /* temp short data */
		int scale, sign; /* scaling and sign parameters */

		hdrSize = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);
		dttTable.setHighSize((int) ByteStreamUtil.getInstance().getUByte(cbufptr));
		dttTable.setLowSize((int) ByteStreamUtil.getInstance().getUByte(cbufptr));

		/* If lofilt member previously allocated ... */
		if (dttTable.getLowFilter() != null) {
			/* Deallocate the member prior to new allocation */
			dttTable.setLowFilter(null);
		}
		dttTable.setLowFilter(new float[dttTable.getLowSize()]);

		/* If hifilt member previously allocated ... */
		if (dttTable.getHighFilter() != null) {
			/* Deallocate the member prior to new allocation */
			dttTable.setHighFilter(null);
		}

		dttTable.setHighFilter(new float[dttTable.getHighSize()]);

		if (dttTable.getHighSize() % 2 != 0)
			aSize = (dttTable.getHighSize() + 1) / 2;
		else
			aSize = dttTable.getHighSize() / 2;

		aLowfilter = new float[aSize];

		aSize--;
		for (cnt = 0; cnt <= aSize; cnt++) {
			sign = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
			scale = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
			shortData = ByteStreamUtil.getInstance().getUInt(cbufptr);

			aLowfilter[cnt] = shortData;
			while (scale > 0) {
				aLowfilter[cnt] /= 10.0;
				scale--;
			}
			if (sign != 0)
				aLowfilter[cnt] *= -1.0;

			if (dttTable.getHighSize() % 2 != 0) {
				dttTable.getHighFilter()[cnt + aSize] = (WsqUtil.getInstance().intSign(cnt) * aLowfilter[cnt]);
				if (cnt > 0)
					dttTable.getHighFilter()[aSize - cnt] = dttTable.getHighFilter()[cnt + aSize];
			} else {
				dttTable.getHighFilter()[cnt + aSize + 1] = (WsqUtil.getInstance().intSign(cnt) * aLowfilter[cnt]);
				dttTable.getHighFilter()[aSize - cnt] = (float) (-1.0 * dttTable.getHighFilter()[cnt + aSize + 1]);
			}
		}
		aLowfilter = null;

		if (dttTable.getLowSize() % 2 != 0)
			aSize = (dttTable.getLowSize() + 1) / 2;
		else
			aSize = dttTable.getLowSize() / 2;

		aHighFilter = new float[aSize];

		aSize--;
		for (cnt = 0; cnt <= aSize; cnt++) {
			sign = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
			scale = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
			shortData = ByteStreamUtil.getInstance().getUInt(cbufptr);

			aHighFilter[cnt] = shortData;
			while (scale > 0) {
				aHighFilter[cnt] /= 10.0;
				scale--;
			}
			if (sign != 0)
				aHighFilter[cnt] *= -1.0;

			if (dttTable.getLowSize() % 2 != 0) {
				dttTable.getLowFilter()[cnt + aSize] = (WsqUtil.getInstance().intSign(cnt) * aHighFilter[cnt]);
				if (cnt > 0)
					dttTable.getLowFilter()[aSize - cnt] = dttTable.getLowFilter()[cnt + aSize];
			} else {
				dttTable.getLowFilter()[cnt + aSize + 1] = (WsqUtil.getInstance().intSign(cnt + 1) * aHighFilter[cnt]);
				dttTable.getLowFilter()[aSize - cnt] = dttTable.getLowFilter()[cnt + aSize + 1];
			}
		}

		dttTable.setLowDef(1);
		dttTable.setHighDef(1);
		return 0;
	}

	/*****************************************************/
	/* Reads huffman table from compressed memory buffer */
	/*****************************************************/
	@SuppressWarnings({ "java:S1659", "unused" })
	private int getHuffmanTable(int[] tableId, int[] huffbits, int[] huffvalues, int maxHuffCounts,
			ByteBufferContext cbufptr, int readTableLength, int[] bytesLeft) {
		int ret, i;
		long tableLength = -1;
		int numOfHuffmanValues;

		/* tableLength */
		if (readTableLength != 0) {
			tableLength = ByteStreamUtil.getInstance().getUShort(cbufptr);
			bytesLeft[0] = (int) (tableLength - 2);
		}

		/* If no bytes left ... */
		if (bytesLeft[0] <= 0) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, "getHuffmanTable : no huffman table bytes remaining");
			return (WsqErrorCode.NO_DATA_TO_READ.getErrorId());
		}

		/* Table ID */
		tableId[0] = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
		bytesLeft[0]--;

		numOfHuffmanValues = 0;
		/* L1 ... L16 */
		for (i = 0; i < WsqConstant.MAX_HUFFBITS; i++) {
			huffbits[i] = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
			numOfHuffmanValues += huffbits[i];
		}
		bytesLeft[0] -= WsqConstant.MAX_HUFFBITS;

		if (numOfHuffmanValues > maxHuffCounts + 1) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format(
					"getHuffmanTable : numOfHuffmanValues {0} is larger than maxHuffCounts {1}", numOfHuffmanValues,
					maxHuffCounts + 1));
			return (WsqErrorCode.VALUE_GREATER_THAN_MAX_COUNT.getErrorId());
		}

		/* V1,1 ... V16,16 */
		for (i = 0; i < numOfHuffmanValues; i++) {
			huffvalues[i] = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);
		}
		bytesLeft[0] -= numOfHuffmanValues;

		return 0;
	}

	/************************************************************************/
	/* Routine to read comment block from a memory buffer. */
	/* NOTE: forces return of NULL termnated comment string. */
	/************************************************************************/
	@SuppressWarnings({ "java:S1854", "java:S1858", "unused" })
	private int getComment(byte[] comment, ByteBufferContext cbufptr /* current byte in input buffer */
	) {
		int ret = 0;

		try {
			/* Read only the number of bytes as specified in the header length. */
			int size = ByteStreamUtil.getInstance().getBufferU(cbufptr, comment, comment.length - 1);

			/* If comment did not explicitly contain a NULL terminator, it will */
			/* have one here by default due to the calloc of one extra byte at */
			/* the end. */
		} catch (Exception ex) {
			logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, "getComment : comment empty " + ex.getLocalizedMessage());
			Arrays.fill(comment, (byte) '\0');
		}
		// Add a null terminator at the end
		comment[comment.length-1] = '\0'; // Null terminator (ASCII 0)
		return ret;
	}

	/******************************************************************/
	/* Routine to read in frame header parameters from memory buffer. */
	/******************************************************************/
	@SuppressWarnings({ "java:S1854", "unused" })
	public int getWsqHeaderForm(WsqHeaderForm headerForm, /* frame header structure */
			ByteBufferContext cbufptr /* current byte in input buffer */
	) {
		int ret = 0;
		int hdrSize;/* header size and data pointer */
		int shortData;
		int scale; /* exponent scaling parameter */

		hdrSize = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);
		headerForm.setBlack((int) ByteStreamUtil.getInstance().getUByte(cbufptr));
		headerForm.setWhite((int) ByteStreamUtil.getInstance().getUByte(cbufptr));
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
		return ret;
	}

	/*****************************************************************/
	/* Get and return first NISTCOM from encoded data stream. */
	/*****************************************************************/
	@SuppressWarnings({ "java:S2583", "java:S3776" })
	public int getWsqNistCom(WsqFet nistcom, byte[] imageData, int imagelength) {
		int ret;
		int[] marker = new int[1];
		StringBuilder commentText = new StringBuilder("");

		ByteBufferContext cbufptr = new ByteBufferContext();
		ByteStreamUtil.getInstance().init(cbufptr, imageData, imageData.length);

		/* Get SOI */
		if ((ret = getWsqMarker(marker, WsqConstant.SOI_WSQ, cbufptr)) != 0) {
			return ret;
		}

		/* Get next marker. */
		if ((ret = getWsqMarker(marker, WsqConstant.ANY_WSQ, cbufptr)) != 0) {
			return ret;
		}

		/* While not at Start of Block (SOB) - */
		/* the start of encoded image data ... */
		while (marker[0] != WsqConstant.SOB_WSQ) {
			if (marker[0] == WsqConstant.COM_WSQ) {
				/* skip Length */
				ByteStreamUtil.getInstance().skipBytesForSize(cbufptr, 2);
				byte[] info = new byte[WsqConstant.NCM_HEADER.length()];
				ByteStreamUtil.getInstance().getBufferU(cbufptr, info, 8);
				if (StringUtil.getInstance().stringCompare(new String(info, StandardCharsets.UTF_8), WsqConstant.NCM_HEADER) == 0) {
					// header size
					int hdrSize = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);
					/* cs = hdrSize - sizeof(length value) */
					int cs = hdrSize - 2;
					/* Allocate including a possible NULL terminator. */
					byte[] comment = new byte[cs + 1];
					if ((ret = getComment(comment, cbufptr)) != 0)
						return ret;
					
					commentText.append(new String(comment, StandardCharsets.UTF_8));
					if ((ret = WsqFetHelper.getInstance().string2fet(nistcom,
							commentText.toString().toCharArray())) != 0) {
						commentText.setLength(0);
						return ret;
					}
					commentText.setLength(0);
					return 0;
				}
			}
			/* Skip marker segment. */
			if ((ret = getSkipMarkerSegment(marker, cbufptr)) != 0)
				return ret;
			/* Get next marker. */
			if ((ret = getWsqMarker(marker, WsqConstant.ANY_WSQ, cbufptr)) != 0)
				return ret;
		}

		/* NISTCOM not found ... */
		return 0;
	}

	/*****************************************************************/
	/* Skips the segment data following a JPEGB, JPEGL, or WSQ */
	/* marker in the given memory buffer. */
	/*****************************************************************/
	@SuppressWarnings({ "java:S1172" })
	private int getSkipMarkerSegment(int[] marker, ByteBufferContext cbufptr) {
		int length;

		/* Get ushort Length. */
		length = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);
		length -= 2;

		/* Check for EOB ... */
		ByteStreamUtil.getInstance().skipBytesForSize(cbufptr, length);

		return 0;
	}

	/******************************************************************/
	/* Routine to read in block header parameters from memory buffer. */
	/******************************************************************/
	@SuppressWarnings({ "java:S1854", "unused" })
	public int getBlockHeader(int[] hufftableId, /* huffman table indicator */
			ByteBufferContext cbufptr /* current byte in input buffer */
	) {
		int hdrSize = 0; /* block header size */

		hdrSize = (int) ByteStreamUtil.getInstance().getUShort(cbufptr);
		hufftableId[0] = (int) ByteStreamUtil.getInstance().getUByte(cbufptr);

		return 0;
	}
}