package io.mosip.imagedecoder.constant.wsq;

import io.mosip.imagedecoder.constant.DecoderConstant;
import io.mosip.imagedecoder.util.ByteStreamUtil;

public class WsqConstant extends DecoderConstant {
	/* WSQ Marker Definitions */
	public static final int SOI_WSQ = 0xffa0;
	public static final int EOI_WSQ = 0xffa1;
	public static final int SOF_WSQ = 0xffa2;
	public static final int SOB_WSQ = 0xffa3;
	public static final int DTT_WSQ = 0xffa4;
	public static final int DQT_WSQ = 0xffa5;
	public static final int DHT_WSQ = 0xffa6;
	public static final int DRT_WSQ = 0xffa7;
	public static final int COM_WSQ = 0xffa8;
	/* Case for getting ANY marker. */
	public static final int ANY_WSQ = 0xffff;
	/* Cases for getting a table from a set of possible ones. */
	public static final int TBLS_N_SOF = 2;
	public static final int TBLS_N_SOS = (TBLS_N_SOF + 1);
	public static final int TBLS_N_SOB = (TBLS_N_SOF + 2);

	/* Subband Definitions */
	public static final int STRT_SUBBAND_2 = 19;
	public static final int STRT_SUBBAND_3 = 52;
	public static final int MAX_SUBBANDS = 64;
	public static final int NUM_SUBBANDS = 60;
	public static final int STRT_SUBBAND_DEL = NUM_SUBBANDS;
	public static final int STRT_SIZE_REGION_2 = 4;
	public static final int STRT_SIZE_REGION_3 = 51;

	public static final int MIN_IMG_DIM = 256;

	public static final int WHITE = 255;
	public static final int BLACK = 0;

	public static final int COEFF_CODE = 0;
	public static final int RUN_CODE = 1;

	public static final int RAW_IMAGE = 1;
	public static final int IHEAD_IMAGE = 0;

	public static final float VARIANCE_THRESH = 1.01f;

	public static final int W_TREELEN = 20;
	public static final int Q_TREELEN = 64;

	public static final int MAX_DHT_TABLES = 8;

	public static final int MAX_HUFFBITS = 16; // DO NOT CHANGE THIS CONSTANT!! */
	public static final int MAX_HUFFCOUNTS_WSQ = 256; /* Length of code table: change as needed */
	/* but DO NOT EXCEED 256 */
	public static final int MAX_HUFFCOEFF = 74; /* -73 .. +74 */
	public static final int MAX_HUFFZRUN = 100;

	public static final int READ_TABLE_LEN = 1;
	public static final int NO_READ_TABLE_LEN = 0;

	public static final int MAXFETS = 100;
	public static final int MAXFETLENGTH = 512;
	public static final String NCM_EXT = "ncm";
	public static final String NCM_HEADER = "NIST_COM"; /* manditory */
	public static final String NCM_PIX_WIDTH = "PIX_WIDTH"; /* manditory */
	public static final String NCM_PIX_HEIGHT = "PIX_HEIGHT"; /* manditory */
	public static final String NCM_PIX_DEPTH = "PIX_DEPTH"; /* 1,8,24 (manditory) */
	public static final String NCM_PPI = "PPI"; /* -1 if unknown (manditory) */

	public static final String NCM_COLORSPACE = "COLORSPACE"; /* RGB,YCbCr,GRAY */
	public static final String NCM_N_CMPNTS = "NUM_COMPONENTS"; /* [1..4] (manditory w/hv_factors) */
	public static final String NCM_HV_FCTRS = "HV_FACTORS"; /* H0,V0:H1,V1:... */
	public static final String NCM_INTRLV = "INTERLEAVE"; /* 0,1 (manditory w/depth=24) */
	public static final String NCM_COMPRESSION = "COMPRESSION"; /* NONE,JPEGB,JPEGL,WSQ */
	public static final String NCM_JPEGB_QUAL = "JPEGB_QUALITY"; /* [20..95] */
	public static final String NCM_JPEGL_PREDICT = "JPEGL_PREDICT"; /* [1..7] */
	public static final String NCM_WSQ_RATE = "WSQ_BITRATE"; /* ex. .75,2.25 (-1.0 if unknown) */
	public static final String NCM_LOSSY = "LOSSY"; /* 0,1 */

	public static final String NCM_HISTORY = "HISTORY"; /* ex. SD historical data */
	public static final String NCM_FING_CLASS = "FING_CLASS"; /* ex. A,L,R,S,T,W */
	public static final String NCM_SEX = "SEX"; /* m,f */
	public static final String NCM_SCAN_TYPE = "SCAN_TYPE"; /* l,i */
	public static final String NCM_FACE_POS = "FACE_POS"; /* f,p */
	public static final String NCM_AGE = "AGE";
	public static final String NCM_SD_ID = "SD_ID"; /* 4,9,10,14,18 */

	/// < Invalid data found when processing input
	public static final int AVERROR_INVALIDDATA = ByteStreamUtil.getInstance().ffErrorTag('I', 'N', 'D', 'A'); 
}
