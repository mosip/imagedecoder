package io.mosip.imagedecoder.wsq;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;

import java.text.MessageFormat;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import io.mosip.imagedecoder.constant.wsq.WsqErrorCode;
import io.mosip.imagedecoder.exceptions.DecoderException;
import io.mosip.imagedecoder.model.ByteBufferContext;
import io.mosip.imagedecoder.model.wsq.WsqFet;
import io.mosip.imagedecoder.model.wsq.WsqHeaderForm;
import io.mosip.imagedecoder.model.wsq.WsqHuffCode;
import io.mosip.imagedecoder.model.wsq.WsqInfo;
import io.mosip.imagedecoder.model.wsq.WsqQuantizationTree;
import io.mosip.imagedecoder.model.wsq.WsqTableDht;
import io.mosip.imagedecoder.model.wsq.WsqTableDqt;
import io.mosip.imagedecoder.model.wsq.WsqTableDtt;
import io.mosip.imagedecoder.model.wsq.WsqWavletTree;
import io.mosip.imagedecoder.util.ByteStreamUtil;
import io.mosip.imagedecoder.util.wsq.WsqUtil;

/**
 * The Class WSQDecoder Helper does the decoding of WSQ image information
 * 
 * @author Janardhan B S
 * 
 */
public class WsqDecoderHelper {
	private Logger logger = ImageDecoderLogger.getLogger(WsqDecoderHelper.class);
	// Static variable reference of singleInstance of type Singleton
	private static WsqDecoderHelper singleInstance = null;

	private WsqDecoderHelper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized WsqDecoderHelper getInstance() {
		if (singleInstance == null)
			singleInstance = new WsqDecoderHelper();

		return singleInstance;
	}

	/* next byte of data */
	private int code = 0;
	/* stuffed byte of data */
	private int code2 = 0;

	/***************************************************************************/
	/* WSQ Decoder routine. Takes an WSQ compressed memory buffer and decodes */
	/* it, returning the reconstructed pixmap. */
	/***************************************************************************/
	@SuppressWarnings({ "java:S3776" })
	public WsqInfo wsqDecode(byte[] iData, int iLen) {
		code = 0;
		code2 = 0;

		WsqErrorCode errorCode;
		int ret;
		int i;
		int[] marker = new int[1]; /* WSQ marker */
		int numOfPixels; /* image size and counter */
		int width;/* image parameters */
		int height; /* image parameters */
		int[] ppi = new int[1]; /* ppi */
		int[] lossy = new int[1]; /* lossy */
		StringBuilder colorSpace = new StringBuilder();/* ColorSpace */
		double[] bitRate = new double[1]; /* lossy */
		byte[] cData; /* image pointer */
		float[] fdata; /* image pointers */
		long[] qData; /* image pointers */

		WsqTableDht[] dhtTable = new WsqTableDht[WsqConstant.MAX_DHT_TABLES];
		WsqTableDtt dttTable = new WsqTableDtt();
		WsqTableDqt dqtTable = new WsqTableDqt();
		WsqHeaderForm wsqHeaderForm = new WsqHeaderForm();
		WsqWavletTree[] wavletTree = new WsqWavletTree[WsqConstant.W_TREELEN];
		WsqQuantizationTree[] quantizationTree = new WsqQuantizationTree[WsqConstant.Q_TREELEN];

		WsqUtil.getInstance().initWsqDecoderResources(dttTable);

		/* Set memory buffer pointers. */
		ByteBufferContext currentBuffer = new ByteBufferContext();
		ByteStreamUtil.getInstance().init(currentBuffer, iData, iData.length);

		/* Read the SOI marker. */
		if ((ret = WsqTableIOHelper.getInstance().getWsqMarker(marker, WsqConstant.SOI_WSQ, currentBuffer)) != 0) {
			errorCode = WsqErrorCode.fromErrorCode(ret + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		/* Read in supporting tables up to the SOF marker. */
		if ((ret = WsqTableIOHelper.getInstance().getWsqMarker(marker, WsqConstant.TBLS_N_SOF, currentBuffer)) != 0) {
			errorCode = WsqErrorCode.fromErrorCode(ret + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		/* Init DHT Tables to 0. */
		for (i = 0; i < WsqConstant.MAX_DHT_TABLES; i++) {
			dhtTable[i] = new WsqTableDht();
			dhtTable[i].setTableDef(0);
		}

		while (marker[0] != WsqConstant.SOF_WSQ) {
			if ((ret = WsqTableIOHelper.getInstance().getWsqTable(marker, dttTable, dqtTable, dhtTable,
					currentBuffer)) != 0) {
				WsqUtil.getInstance().freeWsqDecoderResources(dttTable);
				errorCode = WsqErrorCode.fromErrorCode(ret + "");
				throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
			}
			if ((ret = WsqTableIOHelper.getInstance().getWsqMarker(marker, WsqConstant.TBLS_N_SOF,
					currentBuffer)) != 0) {
				WsqUtil.getInstance().freeWsqDecoderResources(dttTable);
				errorCode = WsqErrorCode.fromErrorCode(ret + "");
				throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
			}
		}

		/* Read in the Frame Header. */
		if ((ret = WsqTableIOHelper.getInstance().getWsqHeaderForm(wsqHeaderForm, currentBuffer)) != 0) {
			WsqUtil.getInstance().freeWsqDecoderResources(dttTable);
			errorCode = WsqErrorCode.fromErrorCode(ret + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		width = wsqHeaderForm.getWidth();
		height = wsqHeaderForm.getHeight();
		numOfPixels = width * height;

		WsqFet nistcom = WsqFetHelper.getInstance().allocFet(WsqConstant.MAXFETS);
		if ((ret = WsqFetInfoHelper.getInstance().getNistComments(nistcom, iData, iLen)) != 0) {
			WsqUtil.getInstance().freeWsqDecoderResources(dttTable);
			errorCode = WsqErrorCode.fromErrorCode(ret + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		if ((ret = WsqFetInfoHelper.getInstance().getWsqPPI(nistcom, ppi)) != 0) {
			WsqUtil.getInstance().freeWsqDecoderResources(dttTable);
			errorCode = WsqErrorCode.fromErrorCode(ret + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		if ((ret = WsqFetInfoHelper.getInstance().getWsqLossyFlag(nistcom, lossy)) != 0) {
			WsqUtil.getInstance().freeWsqDecoderResources(dttTable);
			errorCode = WsqErrorCode.fromErrorCode(ret + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		if ((ret = WsqFetInfoHelper.getInstance().getWsqBitRate(nistcom, bitRate)) != 0) {
			WsqUtil.getInstance().freeWsqDecoderResources(dttTable);
			errorCode = WsqErrorCode.fromErrorCode(ret + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		if ((ret = WsqFetInfoHelper.getInstance().getWsqColorSpace(nistcom, colorSpace)) != 0) {
			WsqUtil.getInstance().freeWsqDecoderResources(dttTable);
			errorCode = WsqErrorCode.fromErrorCode(ret + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		/* Build WSQ decomposition trees. */
		WsqTreeHelper.getInstance().buildWsqTrees(wavletTree, WsqConstant.W_TREELEN, quantizationTree,
				WsqConstant.Q_TREELEN, width, height);

		/* Allocate working memory. */
		qData = new long[numOfPixels];
		int[] qdataIndex = new int[1];
		/* Decode the Huffman encoded data blocks. */
		if ((ret = decodeHuffmanData(qData, qdataIndex, dttTable, dqtTable, dhtTable, currentBuffer, wsqHeaderForm,
				quantizationTree)) != 0) {
			WsqUtil.getInstance().freeWsqDecoderResources(dttTable);
			errorCode = WsqErrorCode.fromErrorCode(ret + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		fdata = new float[width * height];
		if (dqtTable.getDqtDef() != 1) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,
					"unquantize : quantization table parameters not defined");
			errorCode = WsqErrorCode
					.fromErrorCode(WsqErrorCode.QUANTIZATION_TABLE_PARAMS_NOT_DEFINED.getErrorId() + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		/* Decode the quantize wavelet subband data. */
		if ((ret = WsqUtil.getInstance().unquantize(fdata, dqtTable, quantizationTree, WsqConstant.Q_TREELEN, qData,
				width, height)) != 0) {
			WsqUtil.getInstance().freeWsqDecoderResources(dttTable);
			errorCode = WsqErrorCode.fromErrorCode(ret + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		/* Done with quantized wavelet subband data. */
		if ((ret = WsqUtil.getInstance().wsqReconstruct(fdata, width, height, wavletTree, WsqConstant.W_TREELEN,
				dttTable)) != 0) {
			WsqUtil.getInstance().freeWsqDecoderResources(dttTable);
			errorCode = WsqErrorCode.fromErrorCode(ret + "");
			throw new DecoderException(errorCode.getErrorCode(), errorCode.getErrorMessage());
		}

		cData = new byte[numOfPixels];

		/* Convert floating point pixels to unsigned char pixels. */
		WsqUtil.getInstance().convertImage2Bytes(cData, fdata, width, height, wsqHeaderForm.getMShift()[0],
				wsqHeaderForm.getRScale()[0]);

		/* Done with floating point pixels. */

		WsqUtil.getInstance().freeWsqDecoderResources(dttTable);

		/* Assign reconstructed pixmap and attributes to output pointers. */
		WsqInfo info = new WsqInfo();
		info.setData(cData);
		info.setWidth(width);
		info.setHeight(height);
		info.setDepth(8);
		info.setPpi(ppi[0]);
		info.setLossyFlag(lossy[0]);
		info.setBitRate(bitRate[0]);
		info.setColorSpace(colorSpace.toString());

		/* Return normally. */
		return info;
	}

	/***************************************************************************/
	/* Routine to decode an entire "block" of encoded data from memory buffer. */
	/***************************************************************************/
	@SuppressWarnings({ "java:S107", "java:S654", "java:S1659", "java:S1854", "java:S3776", "java:S6541" })
	private int decodeHuffmanData(long[] ip, /* image pointer */
			int[] qdataIndex, /* image pointer index */
			WsqTableDtt dttTable, /* transform table pointer */
			WsqTableDqt dqtTable, /* quantization table */
			WsqTableDht[] dhtTable, /* huffman table */
			ByteBufferContext currentBuffer, /* points to current byte in input buffer */
			WsqHeaderForm wsqHeaderForm, WsqQuantizationTree[] quantizationTree) {
		int ret;
		int blkNo = 0; /* block number */
		int[] marker = new int[1]; /* WSQ markers */
		int[] bitCount = new int[1]; /* bit count for getWsqNextBits routine */
		int n; /* zero run count */
		int[] nodeptr = new int[1]; /* pointers for decoding */
		int[] lastSize = new int[1]; /* last huffvalue */
		int[] huffTableId = new int[1]; /* huffman table number */
		WsqHuffCode[] hufftable = null; /* huffman code structure */
		long[] maxCode = new long[WsqConstant.MAX_HUFFBITS + 1]; /* used in decoding data */
		long[] minCode = new long[WsqConstant.MAX_HUFFBITS + 1]; /* used in decoding data */
		int[] value = new int[WsqConstant.MAX_HUFFBITS + 1]; /* used in decoding data */
		long[] tBits = new long[1];
		int ipc, ipcMx, ipcQ; /* image byte count adjustment parameters */

		if ((ret = WsqTableIOHelper.getInstance().getWsqMarker(marker, WsqConstant.TBLS_N_SOB, currentBuffer)) != 0)
			return ret;

		bitCount[0] = 0;
		ipc = 0;
		ipcQ = 0;
		ipcMx = wsqHeaderForm.getWidth() * wsqHeaderForm.getHeight();

		while (marker[0] != WsqConstant.EOI_WSQ) {
			if (marker[0] != 0) {
				blkNo++;
				while (marker[0] != WsqConstant.SOB_WSQ) {
					if ((ret = WsqTableIOHelper.getInstance().getWsqTable(marker, dttTable, dqtTable, dhtTable,
							currentBuffer)) != 0)
						return ret;
					if ((ret = WsqTableIOHelper.getInstance().getWsqMarker(marker, WsqConstant.TBLS_N_SOB,
							currentBuffer)) != 0)
						return ret;
				}

				if (dqtTable.getDqtDef() != 0 && ipcQ == 0) {
					for (n = 0; n < 64; n++)
						if (dqtTable.getQBin()[n] == 0.0f)
							ipcMx -= quantizationTree[n].getLenX() * quantizationTree[n].getLenY();

					ipcQ = 1;
				}
				if ((ret = WsqTableIOHelper.getInstance().getBlockHeader(huffTableId, currentBuffer)) != 0)
					return ret;

				if (dhtTable[huffTableId[0]].getTableDef() != 1) {
					logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,
							MessageFormat.format("decodeHuffmanData : huffman table {0} undefined.", huffTableId[0]));
					return (WsqErrorCode.ENCODED_DATA_WRONG.getErrorId());
				}

				/* the next two routines reconstruct the huffman tables */
				hufftable = new WsqHuffCode[WsqConstant.MAX_HUFFCOUNTS_WSQ + 1];
				if ((ret = WsqHuffHelper.getInstance().buildHuffSizes(hufftable, lastSize,
						dhtTable[huffTableId[0]].getHuffBits(), WsqConstant.MAX_HUFFCOUNTS_WSQ)) != 0)
					return ret;

				WsqHuffHelper.getInstance().buildHuffCodes(hufftable);
				if ((ret = WsqHuffHelper.getInstance().checkWsqHuffCodes(hufftable, lastSize[0])) != 0)
					logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,
							MessageFormat.format("huffTableId = {0}", huffTableId[0]));

				/* this routine builds a set of three tables used in decoding */
				/* the compressed data */
				WsqHuffHelper.getInstance().generateDecodeTable(hufftable, maxCode, minCode, value,
						dhtTable[huffTableId[0]].getHuffBits());
				bitCount[0] = 0;
				marker[0] = 0;
			}

			/* get next huffman category code from compressed input data stream */
			if ((ret = decodeData(nodeptr, minCode, maxCode, value, dhtTable[huffTableId[0]].getHuffValues(),
					currentBuffer, bitCount, marker)) != 0)
				return ret;

			if (nodeptr[0] == -1) {
				while (marker[0] == WsqConstant.COM_WSQ && blkNo == 3) {
					if ((ret = WsqTableIOHelper.getInstance().getWsqTable(marker, dttTable, dqtTable, dhtTable,
							currentBuffer)) != 0)
						return ret;
					if ((ret = WsqTableIOHelper.getInstance().getWsqMarker(marker, WsqConstant.ANY_WSQ,
							currentBuffer)) != 0)
						return ret;
				}
				continue;
			}

			if (ipc > ipcMx) {
				logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,
						"decodeHuffmanData [1]: Decoded data extends past image buffer. Encoded data appears corrupt or non-standard.");
				return (WsqErrorCode.ENCODED_DATA_WRONG.getErrorId());
			}

			if (nodeptr[0] > 0 && nodeptr[0] <= 100) {
				ipc += nodeptr[0];
				if (ipc > ipcMx) {
					logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,
							"decodeHuffmanData [2]: Decoded data extends past image buffer. Encoded data appears corrupt or non-standard.");
					return (WsqErrorCode.ENCODED_DATA_WRONG.getErrorId());
				}
				for (n = 0; n < nodeptr[0]; n++)
					ip[qdataIndex[0]++] = 0; /* z run */
			} else if (nodeptr[0] > 106 && nodeptr[0] < 0xff) {
				ip[qdataIndex[0]++] = (long) nodeptr[0] - 180;
				ipc++;
			} else if (nodeptr[0] == 101) {
				if ((ret = getWsqNextBits(tBits, marker, currentBuffer, bitCount, 8)) != 0)
					return ret;
				ip[qdataIndex[0]++] = tBits[0];
				ipc++;
			} else if (nodeptr[0] == 102) {
				if ((ret = getWsqNextBits(tBits, marker, currentBuffer, bitCount, 8)) != 0)
					return ret;
				ip[qdataIndex[0]++] = -tBits[0];
				ipc++;
			} else if (nodeptr[0] == 103) {
				if ((ret = getWsqNextBits(tBits, marker, currentBuffer, bitCount, 16)) != 0)
					return ret;
				ip[qdataIndex[0]++] = tBits[0];
				ipc++;
			} else if (nodeptr[0] == 104) {
				if ((ret = getWsqNextBits(tBits, marker, currentBuffer, bitCount, 16)) != 0)
					return ret;
				ip[qdataIndex[0]++] = -tBits[0];
				ipc++;
			} else if (nodeptr[0] == 105) {
				if ((ret = getWsqNextBits(tBits, marker, currentBuffer, bitCount, 8)) != 0)
					return ret;
				ipc += tBits[0];
				if (ipc > ipcMx) {
					logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,
							"decodeHuffmanData [3]: Decoded data extends past image buffer. Encoded data appears corrupt or non-standard.");
					return (WsqErrorCode.ENCODED_DATA_WRONG.getErrorId());
				}
				n = (int) tBits[0];
				while (n-- != 0)
					ip[qdataIndex[0]++] = 0;
			} else if (nodeptr[0] == 106) {
				if ((ret = getWsqNextBits(tBits, marker, currentBuffer, bitCount, 16)) != 0)
					return ret;
				ipc += tBits[0];
				if (ipc > ipcMx) {
					logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,
							"decodeHuffmanData [4]: Decoded data extends past image buffer. Encoded data appears corrupt or non-standard.");
					return (WsqErrorCode.ENCODED_DATA_WRONG.getErrorId());
				}
				n = (int) tBits[0];
				while (n-- != 0)
					ip[qdataIndex[0]++] = 0;
			} else {
				logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,
						MessageFormat.format("decodeHuffmanData : Invalid code {0} ", nodeptr[0]));
				return (WsqErrorCode.INVALID_CODE_INHUFFMAN_DATA.getErrorId());
			}
		}

		return 0;
	}

	/**********************************************************/
	/* Routine to decode the encoded data from memory buffer. */
	/**********************************************************/
	@SuppressWarnings({ "java:S107", "java:S654", "java:S1659", "java:S1854", "java:S3776", "java:S6541" })
	private int decodeData(int[] huffmanNode, /* returned huffman code category */
			long[] minCode, /* points to minimum code value for */
			/* a given code length */
			long[] maxCode, /* points to maximum code value for */
			/* a given code length */
			int[] value, /* points to first code in the huffman */
			/* code table for a given code length */
			int[] huffvalues, /* defines order of huffman code */
			/* lengths in relation to code sizes */
			ByteBufferContext currentBuffer, /* points to current byte in input buffer */
			int[] bitCount, /* marks the bit to receive from the input byte */
			int[] marker) {
		int ret = 0;
		/* increment variables */
		int inx, inx2;
		/*
		 * becomes a huffman code word (one bit at a time)
		 */
		long[] huffmanCode = new long[1], tBits = new long[1];
		if ((ret = getWsqNextBits(huffmanCode, marker, currentBuffer, bitCount, 1)) != 0)
			return ret;

		if (marker[0] != 0) {
			huffmanNode[0] = -1;
			return ret;
		}

		for (inx = 1; huffmanCode[0] > maxCode[inx]; inx++) {
			if ((ret = getWsqNextBits(tBits, marker, currentBuffer, bitCount, 1)) != 0)
				return ret;

			huffmanCode[0] = (huffmanCode[0] << 1) + tBits[0];
			if (marker[0] != 0) {
				huffmanNode[0] = -1;
				return (0);
			}
		}
		inx2 = value[inx];
		inx2 = (int) (inx2 + (huffmanCode[0] - minCode[inx]));

		huffmanNode[0] = huffvalues[inx2];
		return ret;
	}

	/****************************************************************/
	/* Routine to get nextbit(s) of data stream from memory buffer. */
	/****************************************************************/

	@SuppressWarnings({ "java:S2696" })
	private int getWsqNextBits(long[] bits, /* returned bits */
			int[] marker, /* returned marker */
			ByteBufferContext currentBuffer, /* points to current byte in input buffer */
			int[] bitCount, /* marks the bit to receive from the input byte */
			int bitsRequested) /* number of bits requested */
	{
		int ret;
		long[] tBits = new long[1]; /* bits of current data byte requested */
		int bitsNeeded; /* additional bits required to finish request */

		/*
		 * used to "mask out" n number of bits from data stream
		 */
		int[] bitMask = { 0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff };
		if (bitCount[0] == 0) {
			code = (int) ByteStreamUtil.getInstance().getUByte(currentBuffer);
			bitCount[0] = 8;
			if (code == 0xFF) {
				code2 = (int) ByteStreamUtil.getInstance().getUByte(currentBuffer);
				if (code2 != 0x00 && bitsRequested == 1) {
					marker[0] = (code << 8) | code2;
					bits[0] = 1;
					return (0);
				}
				if (code2 != 0x00) {
					logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "getWsqNextBits : No stuffed zeros");
					return (WsqErrorCode.NO_STUFFED_ZEROS.getErrorId());
				}
			}
		}
		if (bitsRequested <= bitCount[0]) {
			bits[0] = (code >> (bitCount[0] - bitsRequested)) & (bitMask[bitsRequested]);
			bitCount[0] -= bitsRequested;
			code &= bitMask[bitCount[0]];
		} else {
			bitsNeeded = bitsRequested - bitCount[0];
			bits[0] = code << bitsNeeded;
			bitCount[0] = 0;
			marker[0] = 0;
			if ((ret = getWsqNextBits(tBits, marker, currentBuffer, bitCount, bitsNeeded)) != 0)
				return ret;
			bits[0] |= tBits[0];
		}

		return (0);
	}
}