package io.mosip.imagedecoder.constant.openjpeg;

import io.mosip.imagedecoder.constant.DecoderConstant;
import io.mosip.imagedecoder.model.openjpeg.J2KProgressionOrder;
import io.mosip.imagedecoder.model.openjpeg.ProgressionOrder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class OpenJpegConstant extends DecoderConstant {
	public static final String OPENJPEG_VERSION = "1.3.0";
	
	public static final int MQC_NUMCTXS = 19;
	public static final double INCH_PER_METER = 39.37007874016; // to calculate ppi
	
	public static final int MAX_PATH_LEN = 4096;
	/** < Maximum allowed size for filenames */

	public static final int LAST_DATA_BYTE = 255;
	/** < Maximum allowed size for filenames */

	public static final int J2K_MAXRLVLS = 33;
	/** < Number of maximum resolution level authorized */
	public static final int J2K_MAXBANDS = (3 * J2K_MAXRLVLS - 2);
	/** < Number of maximum sub-band linked to number of resolution level */

	/* UniPG>> */
	public static final int JPWL_MAX_NO_TILESPECS = 16;
	/** < Maximum number of tile parts expected by JPWL: increase at your will */
	public static final int JPWL_MAX_NO_PACKSPECS = 16;
	/** < Maximum number of packet parts expected by JPWL: increase at your will */
	public static final int JPWL_MAX_NO_MARKERS = 512;
	/** < Maximum number of JPWL markers: increase at your will */
	public static final String JPWL_PRIVATEINDEX_NAME = "jpwl_index_privatefilename";
	/** < index file name used when JPWL is on */
	public static final int JPWL_EXPECTED_COMPONENTS = 3;
	/** < Expect this number of components, so you'll find better the first EPB */
	public static final int JPWL_MAXIMUM_TILES = 8192;
	/** < Expect this maximum number of tiles, to avoid some crashes */
	public static final int JPWL_MAXIMUM_HAMMING = 2;
	/** < Expect this maximum number of bit errors in marker id's */
	public static final int JPWL_MAXIMUM_EPB_ROOM = 65450; /**
															 * < Expect this maximum number of bytes for composition of
															 * EPBs
															 */

	public static final int T1_NMSEDEC_BITS = 7;

	public static final int T1_SIG_NE = 0x0001;	/**< Context orientation : North-East direction */
	public static final int T1_SIG_SE = 0x0002;	/**< Context orientation : South-East direction */
	public static final int T1_SIG_SW = 0x0004;	/**< Context orientation : South-West direction */
	public static final int T1_SIG_NW = 0x0008;	/**< Context orientation : North-West direction */
	public static final int T1_SIG_N = 0x0010;		/**< Context orientation : North direction */
	public static final int T1_SIG_E = 0x0020;		/**< Context orientation : East direction */
	public static final int T1_SIG_S = 0x0040;		/**< Context orientation : South direction */
	public static final int T1_SIG_W = 0x0080;		/**< Context orientation : West direction */
	public static final int T1_SIG_OTH = (T1_SIG_N|T1_SIG_NE|T1_SIG_E|T1_SIG_SE|T1_SIG_S|T1_SIG_SW|T1_SIG_W|T1_SIG_NW);
	public static final int T1_SIG_PRIM = (T1_SIG_N|T1_SIG_E|T1_SIG_S|T1_SIG_W);

	public static final int T1_SGN_N = 0x0100;
	public static final int T1_SGN_E = 0x0200;
	public static final int T1_SGN_S = 0x0400;
	public static final int T1_SGN_W = 0x0800;
	public static final int T1_SGN = (T1_SGN_N|T1_SGN_E|T1_SGN_S|T1_SGN_W);

	public static final int T1_SIG = 0x1000;
	public static final int T1_REFINE = 0x2000;
	public static final int T1_VISIT = 0x4000;

	public static final int T1_NUMCTXS_ZC = 9;
	public static final int T1_NUMCTXS_SC = 5;
	public static final int T1_NUMCTXS_MAG = 3;
	public static final int T1_NUMCTXS_AGG = 1;
	public static final int T1_NUMCTXS_UNI = 1;

	public static final int T1_CTXNO_ZC = 0;
	public static final int T1_CTXNO_SC = (T1_CTXNO_ZC+T1_NUMCTXS_ZC);
	public static final int T1_CTXNO_MAG = (T1_CTXNO_SC+T1_NUMCTXS_SC);
	public static final int T1_CTXNO_AGG = (T1_CTXNO_MAG+T1_NUMCTXS_MAG);
	public static final int T1_CTXNO_UNI = (T1_CTXNO_AGG+T1_NUMCTXS_AGG);
	public static final int T1_NUMCTXS = (T1_CTXNO_UNI+T1_NUMCTXS_UNI);

	public static final int T1_NMSEDEC_FRACBITS = (T1_NMSEDEC_BITS-1);

	public static final int T1_TYPE_MQ = 0;	/**< Normal coding using entropy coder */
	public static final int T1_TYPE_RAW = 1;	/**< No encoding the information is store under raw format in codestream (mode switch RAW)*/
		/*
	 * Stream open flags.
	 */
	/** The stream was opened for reading. */
	public static final int STREAM_READ = 0x0001;
	/** The stream was opened for writing. */
	public static final int STREAM_WRITE = 0x0002;
	
	public static final int J2K_CP_CSTY_PRT = 0x01;
	public static final int J2K_CP_CSTY_SOP = 0x02;
	public static final int J2K_CP_CSTY_EPH = 0x04;
	public static final int J2K_CCP_CSTY_PRT = 0x01;
	public static final int J2K_CCP_CBLKSTY_LAZY = 0x01;
	public static final int J2K_CCP_CBLKSTY_RESET = 0x02;
	public static final int J2K_CCP_CBLKSTY_TERMALL = 0x04;
	public static final int J2K_CCP_CBLKSTY_VSC = 0x08;
	public static final int J2K_CCP_CBLKSTY_PTERM = 0x10;
	public static final int J2K_CCP_CBLKSTY_SEGSYM = 0x20;
	public static final int J2K_CCP_QNTSTY_NOQNT = 0;
	public static final int J2K_CCP_QNTSTY_SIQNT = 1;
	public static final int J2K_CCP_QNTSTY_SEQNT = 2;
	
	public static final int J2K_MS_SOC = 0xff4f;	/**< SOC marker value */
	public static final int J2K_MS_SOT = 0xff90;	/**< SOT marker value */
	public static final int J2K_MS_SOD = 0xff93;	/**< SOD marker value */
	public static final int J2K_MS_EOC = 0xffd9;	/**< EOC marker value */
	public static final int J2K_MS_SIZ = 0xff51;	/**< SIZ marker value */
	public static final int J2K_MS_COD = 0xff52;	/**< COD marker value */
	public static final int J2K_MS_COC = 0xff53;	/**< COC marker value */
	public static final int J2K_MS_RGN = 0xff5e;	/**< RGN marker value */
	public static final int J2K_MS_QCD = 0xff5c;	/**< QCD marker value */
	public static final int J2K_MS_QCC = 0xff5d;	/**< QCC marker value */
	public static final int J2K_MS_POC = 0xff5f;	/**< POC marker value */
	public static final int J2K_MS_TLM = 0xff55;	/**< TLM marker value */
	public static final int J2K_MS_PLM = 0xff57;	/**< PLM marker value */
	public static final int J2K_MS_PLT = 0xff58;	/**< PLT marker value */
	public static final int J2K_MS_PPM = 0xff60;	/**< PPM marker value */
	public static final int J2K_MS_PPT = 0xff61;	/**< PPT marker value */
	public static final int J2K_MS_SOP = 0xff91;	/**< SOP marker value */
	public static final int J2K_MS_EPH = 0xff92;	/**< EPH marker value */
	public static final int J2K_MS_CRG = 0xff63;	/**< CRG marker value */
	public static final int J2K_MS_COM = 0xff64;	/**< COM marker value */
	
	/* USE_JPWL */
	public static final int J2K_MS_EPC = 0xff68;	/**< EPC marker value (Part 11: JPEG 2000 for Wireless) */
	public static final int J2K_MS_EPB = 0xff66;	/**< EPB marker value (Part 11: JPEG 2000 for Wireless) */ 
	public static final int J2K_MS_ESD = 0xff67;	/**< ESD marker value (Part 11: JPEG 2000 for Wireless) */ 
	public static final int J2K_MS_RED = 0xff69;	/**< RED marker value (Part 11: JPEG 2000 for Wireless) */

	/* USE_JPSEC */
	public static final int J2K_MS_SEC = 0xff65;    /**< SEC marker value (Part 8: Secure JPEG 2000) */
	public static final int J2K_MS_INSEC = 0xff94;  /**< INSEC marker value (Part 8: Secure JPEG 2000) */

	//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	//
	// Constants
	//
	//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	public static final int DBL_DECIMAL_DIG  = 17;                      // # of decimal digits of rounding precision
	public static final int DBL_DIG          = 15;                      // # of decimal digits of precision
	public static final double DBL_EPSILON   = 2.2204460492503131e-016; // smallest such that 1.0+DBL_EPSILON != 1.0
	public static final int DBL_HAS_SUBNORM  = 1;                       // type does support subnormal numbers
	public static final int DBL_MANT_DIG     = 53;                      // # of bits in mantissa
	public static final double DBL_MAX          = 1.7976931348623158e+308; // max value
	public static final int DBL_MAX_10_EXP   = 308;                     // max decimal exponent
	public static final int DBL_MAX_EXP      = 1024;                    // max binary exponent
	public static final double DBL_MIN          = 2.2250738585072014e-308; // min positive value
	public static final int DBL_MIN_10_EXP   = (-307);                  // min decimal exponent
	public static final int DBL_MIN_EXP      = (-1021);                 // min binary exponent
	public static final int _DBL_RADIX       = 2;                       // exponent radix
	public static final double DBL_TRUE_MIN  = 4.9406564584124654e-324; // min positive value

	
	public static final int[] LUT_CONTEXTNO_ZC = { 0, 1, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
			3, 3, 3, 3, 3, 3, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 5, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
			8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
			8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 0, 1, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 5, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
			8, 8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 3, 3,
			3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 4, 4, 4, 4, 4, 4,
			4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 0, 1, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 3, 3,
			3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
			4, 4, 4, 4, 4, 4, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
			7, 7, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8,
			8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
			8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 0, 3, 3, 6, 3, 6, 6, 8, 3, 6, 6, 8, 6, 8,
			8, 8, 1, 4, 4, 7, 4, 7, 7, 8, 4, 7, 7, 8, 7, 8, 8, 8, 1, 4, 4, 7, 4, 7, 7, 8, 4, 7, 7, 8, 7, 8, 8, 8, 2, 5,
			5, 7, 5, 7, 7, 8, 5, 7, 7, 8, 7, 8, 8, 8, 1, 4, 4, 7, 4, 7, 7, 8, 4, 7, 7, 8, 7, 8, 8, 8, 2, 5, 5, 7, 5, 7,
			7, 8, 5, 7, 7, 8, 7, 8, 8, 8, 2, 5, 5, 7, 5, 7, 7, 8, 5, 7, 7, 8, 7, 8, 8, 8, 2, 5, 5, 7, 5, 7, 7, 8, 5, 7,
			7, 8, 7, 8, 8, 8, 1, 4, 4, 7, 4, 7, 7, 8, 4, 7, 7, 8, 7, 8, 8, 8, 2, 5, 5, 7, 5, 7, 7, 8, 5, 7, 7, 8, 7, 8,
			8, 8, 2, 5, 5, 7, 5, 7, 7, 8, 5, 7, 7, 8, 7, 8, 8, 8, 2, 5, 5, 7, 5, 7, 7, 8, 5, 7, 7, 8, 7, 8, 8, 8, 2, 5,
			5, 7, 5, 7, 7, 8, 5, 7, 7, 8, 7, 8, 8, 8, 2, 5, 5, 7, 5, 7, 7, 8, 5, 7, 7, 8, 7, 8, 8, 8, 2, 5, 5, 7, 5, 7,
			7, 8, 5, 7, 7, 8, 7, 8, 8, 8, 2, 5, 5, 7, 5, 7, 7, 8, 5, 7, 7, 8, 7, 8, 8, 8 };

	public static final int[] LUT_CONTEXTNO_SC = { 0x9, 0xa, 0xc, 0xd, 0xa, 0xa, 0xd, 0xd, 0xc, 0xd, 0xc, 0xd, 0xd, 0xd, 0xd, 0xd,
			0x9, 0xa, 0xc, 0xb, 0xa, 0x9, 0xd, 0xc, 0xc, 0xb, 0xc, 0xb, 0xd, 0xc, 0xd, 0xc, 0x9, 0xa, 0xc, 0xb, 0xa,
			0xa, 0xb, 0xb, 0xc, 0xd, 0x9, 0xa, 0xd, 0xd, 0xa, 0xa, 0x9, 0xa, 0xc, 0xd, 0xa, 0x9, 0xb, 0xc, 0xc, 0xb,
			0x9, 0xa, 0xd, 0xc, 0xa, 0x9, 0x9, 0xa, 0xc, 0xd, 0xa, 0x9, 0xb, 0xc, 0xc, 0xd, 0xc, 0xd, 0xb, 0xc, 0xb,
			0xc, 0x9, 0xa, 0xc, 0xb, 0xa, 0xa, 0xb, 0xb, 0xc, 0xb, 0xc, 0xb, 0xb, 0xb, 0xb, 0xb, 0x9, 0xa, 0xc, 0xb,
			0xa, 0x9, 0xd, 0xc, 0xc, 0xd, 0x9, 0xa, 0xb, 0xc, 0xa, 0x9, 0x9, 0xa, 0xc, 0xd, 0xa, 0xa, 0xd, 0xd, 0xc,
			0xb, 0x9, 0xa, 0xb, 0xb, 0xa, 0xa, 0x9, 0xa, 0xc, 0xd, 0xa, 0xa, 0xd, 0xd, 0xc, 0xb, 0x9, 0xa, 0xb, 0xb,
			0xa, 0xa, 0x9, 0xa, 0xc, 0xb, 0xa, 0x9, 0xd, 0xc, 0xc, 0xd, 0x9, 0xa, 0xb, 0xc, 0xa, 0x9, 0x9, 0xa, 0xc,
			0xb, 0xa, 0xa, 0xb, 0xb, 0xc, 0xb, 0xc, 0xb, 0xb, 0xb, 0xb, 0xb, 0x9, 0xa, 0xc, 0xd, 0xa, 0x9, 0xb, 0xc,
			0xc, 0xd, 0xc, 0xd, 0xb, 0xc, 0xb, 0xc, 0x9, 0xa, 0xc, 0xd, 0xa, 0x9, 0xb, 0xc, 0xc, 0xb, 0x9, 0xa, 0xd,
			0xc, 0xa, 0x9, 0x9, 0xa, 0xc, 0xb, 0xa, 0xa, 0xb, 0xb, 0xc, 0xd, 0x9, 0xa, 0xd, 0xd, 0xa, 0xa, 0x9, 0xa,
			0xc, 0xb, 0xa, 0x9, 0xd, 0xc, 0xc, 0xb, 0xc, 0xb, 0xd, 0xc, 0xd, 0xc, 0x9, 0xa, 0xc, 0xd, 0xa, 0xa, 0xd,
			0xd, 0xc, 0xd, 0xc, 0xd, 0xd, 0xd, 0xd, 0xd };

	public static final int[] LUT_SPB = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0,
			0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
			1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0,
			1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0,
			0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

	public static final int[] LUT_NMSEDEC_SIG = { 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
			0x0000, 0x0180, 0x0300, 0x0480, 0x0600, 0x0780, 0x0900, 0x0a80, 0x0c00, 0x0d80, 0x0f00, 0x1080, 0x1200,
			0x1380, 0x1500, 0x1680, 0x1800, 0x1980, 0x1b00, 0x1c80, 0x1e00, 0x1f80, 0x2100, 0x2280, 0x2400, 0x2580,
			0x2700, 0x2880, 0x2a00, 0x2b80, 0x2d00, 0x2e80, 0x3000, 0x3180, 0x3300, 0x3480, 0x3600, 0x3780, 0x3900,
			0x3a80, 0x3c00, 0x3d80, 0x3f00, 0x4080, 0x4200, 0x4380, 0x4500, 0x4680, 0x4800, 0x4980, 0x4b00, 0x4c80,
			0x4e00, 0x4f80, 0x5100, 0x5280, 0x5400, 0x5580, 0x5700, 0x5880, 0x5a00, 0x5b80, 0x5d00, 0x5e80, 0x6000,
			0x6180, 0x6300, 0x6480, 0x6600, 0x6780, 0x6900, 0x6a80, 0x6c00, 0x6d80, 0x6f00, 0x7080, 0x7200, 0x7380,
			0x7500, 0x7680 };

	public static final int[] LUT_NMSEDEC_SIG_0 = { 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0080, 0x0080, 0x0080,
			0x0080, 0x0100, 0x0100, 0x0100, 0x0180, 0x0180, 0x0200, 0x0200, 0x0280, 0x0280, 0x0300, 0x0300, 0x0380,
			0x0400, 0x0400, 0x0480, 0x0500, 0x0580, 0x0580, 0x0600, 0x0680, 0x0700, 0x0780, 0x0800, 0x0880, 0x0900,
			0x0980, 0x0a00, 0x0a80, 0x0b80, 0x0c00, 0x0c80, 0x0d00, 0x0e00, 0x0e80, 0x0f00, 0x1000, 0x1080, 0x1180,
			0x1200, 0x1300, 0x1380, 0x1480, 0x1500, 0x1600, 0x1700, 0x1780, 0x1880, 0x1980, 0x1a80, 0x1b00, 0x1c00,
			0x1d00, 0x1e00, 0x1f00, 0x2000, 0x2100, 0x2200, 0x2300, 0x2400, 0x2500, 0x2680, 0x2780, 0x2880, 0x2980,
			0x2b00, 0x2c00, 0x2d00, 0x2e80, 0x2f80, 0x3100, 0x3200, 0x3380, 0x3480, 0x3600, 0x3700, 0x3880, 0x3a00,
			0x3b00, 0x3c80, 0x3e00, 0x3f80, 0x4080, 0x4200, 0x4380, 0x4500, 0x4680, 0x4800, 0x4980, 0x4b00, 0x4c80,
			0x4e00, 0x4f80, 0x5180, 0x5300, 0x5480, 0x5600, 0x5800, 0x5980, 0x5b00, 0x5d00, 0x5e80, 0x6080, 0x6200,
			0x6400, 0x6580, 0x6780, 0x6900, 0x6b00, 0x6d00, 0x6e80, 0x7080, 0x7280, 0x7480, 0x7600, 0x7800, 0x7a00,
			0x7c00, 0x7e00 };

	public static final  int[] LUT_NMSEDEC_REF = { 0x1800, 0x1780, 0x1700, 0x1680, 0x1600, 0x1580, 0x1500, 0x1480, 0x1400,
			0x1380, 0x1300, 0x1280, 0x1200, 0x1180, 0x1100, 0x1080, 0x1000, 0x0f80, 0x0f00, 0x0e80, 0x0e00, 0x0d80,
			0x0d00, 0x0c80, 0x0c00, 0x0b80, 0x0b00, 0x0a80, 0x0a00, 0x0980, 0x0900, 0x0880, 0x0800, 0x0780, 0x0700,
			0x0680, 0x0600, 0x0580, 0x0500, 0x0480, 0x0400, 0x0380, 0x0300, 0x0280, 0x0200, 0x0180, 0x0100, 0x0080,
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0080, 0x0100, 0x0180, 0x0200, 0x0280, 0x0300,
			0x0380, 0x0400, 0x0480, 0x0500, 0x0580, 0x0600, 0x0680, 0x0700, 0x0780, 0x0800, 0x0880, 0x0900, 0x0980,
			0x0a00, 0x0a80, 0x0b00, 0x0b80, 0x0c00, 0x0c80, 0x0d00, 0x0d80, 0x0e00, 0x0e80, 0x0f00, 0x0f80, 0x1000,
			0x1080, 0x1100, 0x1180, 0x1200, 0x1280, 0x1300, 0x1380, 0x1400, 0x1480, 0x1500, 0x1580, 0x1600, 0x1680,
			0x1700, 0x1780 };

	public static final int[] LUT_NMSEDEC_REF_0 = { 0x2000, 0x1f00, 0x1e00, 0x1d00, 0x1c00, 0x1b00, 0x1a80, 0x1980, 0x1880,
			0x1780, 0x1700, 0x1600, 0x1500, 0x1480, 0x1380, 0x1300, 0x1200, 0x1180, 0x1080, 0x1000, 0x0f00, 0x0e80,
			0x0e00, 0x0d00, 0x0c80, 0x0c00, 0x0b80, 0x0a80, 0x0a00, 0x0980, 0x0900, 0x0880, 0x0800, 0x0780, 0x0700,
			0x0680, 0x0600, 0x0580, 0x0580, 0x0500, 0x0480, 0x0400, 0x0400, 0x0380, 0x0300, 0x0300, 0x0280, 0x0280,
			0x0200, 0x0200, 0x0180, 0x0180, 0x0100, 0x0100, 0x0100, 0x0080, 0x0080, 0x0080, 0x0080, 0x0000, 0x0000,
			0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0080, 0x0080, 0x0080, 0x0080,
			0x0100, 0x0100, 0x0100, 0x0180, 0x0180, 0x0200, 0x0200, 0x0280, 0x0280, 0x0300, 0x0300, 0x0380, 0x0400,
			0x0400, 0x0480, 0x0500, 0x0580, 0x0580, 0x0600, 0x0680, 0x0700, 0x0780, 0x0800, 0x0880, 0x0900, 0x0980,
			0x0a00, 0x0a80, 0x0b80, 0x0c00, 0x0c80, 0x0d00, 0x0e00, 0x0e80, 0x0f00, 0x1000, 0x1080, 0x1180, 0x1200,
			0x1300, 0x1380, 0x1480, 0x1500, 0x1600, 0x1700, 0x1780, 0x1880, 0x1980, 0x1a80, 0x1b00, 0x1c00, 0x1d00,
			0x1e00, 0x1f00 };
	
	/* <summary>                                                              */
	/* This table contains the norms of the 5-3 wavelets for different bands. */
	/* </summary>                                                             */
	public static final double DWT_NORMS [][] = {
		{1.000, 1.500, 2.750, 5.375, 10.68, 21.34, 42.67, 85.33, 170.7, 341.3},
		{1.038, 1.592, 2.919, 5.703, 11.33, 22.64, 45.25, 90.48, 180.9},
		{1.038, 1.592, 2.919, 5.703, 11.33, 22.64, 45.25, 90.48, 180.9},
		{.7186, .9218, 1.586, 3.043, 6.019, 12.01, 24.00, 47.97, 95.93}
	};

	/* <summary>                                                              */
	/* This table contains the norms of the 9-7 wavelets for different bands. */
	/* </summary>                                                             */
	public static final double DWT_NORMS_REAL[][] = {
		{1.000, 1.965, 4.177, 8.403, 16.90, 33.84, 67.69, 135.3, 270.6, 540.9},
		{2.022, 3.989, 8.355, 17.04, 34.27, 68.63, 137.3, 274.6, 549.0},
		{2.022, 3.989, 8.355, 17.04, 34.27, 68.63, 137.3, 274.6, 549.0},
		{2.080, 3.865, 8.307, 17.18, 34.71, 69.59, 139.3, 278.6, 557.2}
	};

	public static final float DWT_ALPHA =  1.586134342f; //  12994
	public static final float DWT_BETA =  0.052980118f; //    434
	public static final float DWT_GAMMA = -0.882911075f; //  -7233
	public static final float DWT_DELTA = -0.443506852f; //  -3633

	public static final float K      = 1.230174105f; //  10078
	/* FIXME: What is this constant? */
	public static final float C13318 = 1.625732422f;

	/* <summary> */
	/* This table contains the norms of the basis function of the reversible MCT. */
	/* </summary> */
	public static final double[] MCT_NORMS = { 1.732, 0.8292, 0.8292 };

	/* <summary> */
	/*
	 * This table contains the norms of the basis function of the irreversible MCT.
	 */
	/* </summary> */
	public static final double[] MCT_NORMS_REAL = { 1.732, 1.805, 1.573 };
	
	/** @defgroup JP2 JP2 - JPEG-2000 file format reader/writer */
	/*@{*/

	public static final int JPIP_JPIP = 0x6a706970;

	public static final int JP2_JP   = 0x6A502020;		/**< JPEG 2000 signature box */
	public static final int JP2_FTYP = 0x66747970;		/**< File type box */
	public static final int JP2_JP2H = 0x6A703268;		/**< JP2 header box */
	public static final int JP2_IHDR = 0x69686472;		/**< Image header box */
	public static final int JP2_COLR = 0x636F6C72;		/**< Colour specification box */
	public static final int JP2_RES  = 0x72657320;		/**< Resolution specification box */
	public static final int JP2_RESC  = 0x72657363;		/**< Resolution C specification box */
	public static final int JP2_RESD  = 0x72657364;		/**< Resolution D specification box */
	public static final int JP2_JP2C = 0x6A703263;		/**< Contiguous codestream box */
	public static final int JP2_URL  = 0x75726C20;		/**< URL box */
	public static final int JP2_DBTL = 0x6474626C;		/**< ??? */
	public static final int JP2_BPCC = 0x62706363;		/**< Bits per component box */
	public static final int JP2_JP2  = 0x6A703220;		/**< File type fields */
	
	public static final boolean JPWL_ASSUME = false;
	
	public static final J2KProgressionOrder[] J2KPROGRESSION_ORDER_INFO = { new J2KProgressionOrder(ProgressionOrder.CPRL, "CPRL".toCharArray()),
			new J2KProgressionOrder(ProgressionOrder.LRCP, "LRCP".toCharArray()),
			new J2KProgressionOrder(ProgressionOrder.PCRL, "PCRL".toCharArray()),
			new J2KProgressionOrder(ProgressionOrder.RLCP, "RLCP".toCharArray()),
			new J2KProgressionOrder(ProgressionOrder.RPCL, "RPCL".toCharArray()),
			new J2KProgressionOrder(ProgressionOrder.PROG_UNKNOWN, "".toCharArray()) };

	public static final int T1_MOD[] = { OpenJpegConstant.T1_SIG_S, OpenJpegConstant.T1_SIG_S | OpenJpegConstant.T1_SGN_S, OpenJpegConstant.T1_SIG_E,
			OpenJpegConstant.T1_SIG_E | OpenJpegConstant.T1_SGN_E, OpenJpegConstant.T1_SIG_W, OpenJpegConstant.T1_SIG_W | OpenJpegConstant.T1_SGN_W,
			OpenJpegConstant.T1_SIG_N, OpenJpegConstant.T1_SIG_N | OpenJpegConstant.T1_SGN_N };

}
