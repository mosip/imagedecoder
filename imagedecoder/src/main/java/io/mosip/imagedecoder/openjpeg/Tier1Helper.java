package io.mosip.imagedecoder.openjpeg;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.model.openjpeg.CodecContextInfo;
import io.mosip.imagedecoder.model.openjpeg.MQCoder;
import io.mosip.imagedecoder.model.openjpeg.Raw;
import io.mosip.imagedecoder.model.openjpeg.TcdBand;
import io.mosip.imagedecoder.model.openjpeg.TcdCodeBlockDecoder;
import io.mosip.imagedecoder.model.openjpeg.TcdCodeBlockEncoder;
import io.mosip.imagedecoder.model.openjpeg.TcdPass;
import io.mosip.imagedecoder.model.openjpeg.TcdPrecinct;
import io.mosip.imagedecoder.model.openjpeg.TcdResolution;
import io.mosip.imagedecoder.model.openjpeg.TcdSegment;
import io.mosip.imagedecoder.model.openjpeg.TcdTile;
import io.mosip.imagedecoder.model.openjpeg.TcdTileComponent;
import io.mosip.imagedecoder.model.openjpeg.Tcp;
import io.mosip.imagedecoder.model.openjpeg.Tier1;
import io.mosip.imagedecoder.model.openjpeg.TileComponentCodingParameters;
import io.mosip.imagedecoder.util.openjpeg.MathUtil;

//T1 - Implementation of the tier-1 coding
public class Tier1Helper {
	// Static variable reference of singleInstance of type Singleton
	private static Tier1Helper singleInstance = null;

	private Tier1Helper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized Tier1Helper getInstance() {
		if (singleInstance == null)
			singleInstance = new Tier1Helper();

		return singleInstance;
	}

	private int tier1GetContextNoZC(int f, int orient) {
		return OpenJpegConstant.LUT_CONTEXTNO_ZC[(orient << 8) | (f & OpenJpegConstant.T1_SIG_OTH)];
	}

	private int tier1GetContextNoSC(int f) {
		return OpenJpegConstant.LUT_CONTEXTNO_SC[(f & (OpenJpegConstant.T1_SIG_PRIM | OpenJpegConstant.T1_SGN)) >> 4];
	}

	private int tier1GetContextNoMAG(int f) {
		int tmp1 = (f & OpenJpegConstant.T1_SIG_OTH) != 0 ? OpenJpegConstant.T1_CTXNO_MAG + 1
				: OpenJpegConstant.T1_CTXNO_MAG;
		int tmp2 = (f & OpenJpegConstant.T1_REFINE) != 0 ? OpenJpegConstant.T1_CTXNO_MAG + 2 : tmp1;
		return (tmp2);
	}

	private int tier1GetSPB(int f) {
		return OpenJpegConstant.LUT_SPB[(f & (OpenJpegConstant.T1_SIG_PRIM | OpenJpegConstant.T1_SGN)) >> 4];
	}

	private int tier1GetNmseDecSig(int x, int bitpos) {
		if (bitpos > OpenJpegConstant.T1_NMSEDEC_FRACBITS) {
			return OpenJpegConstant.LUT_NMSEDEC_SIG[(x >> (bitpos - OpenJpegConstant.T1_NMSEDEC_FRACBITS))
					& ((1 << OpenJpegConstant.T1_NMSEDEC_BITS) - 1)];
		}

		return OpenJpegConstant.LUT_NMSEDEC_SIG_0[x & ((1 << OpenJpegConstant.T1_NMSEDEC_BITS) - 1)];
	}

	private int tier1GetNmseDecRef(int x, int bitpos) {
		if (bitpos > OpenJpegConstant.T1_NMSEDEC_FRACBITS) {
			return OpenJpegConstant.LUT_NMSEDEC_REF[(x >> (bitpos - OpenJpegConstant.T1_NMSEDEC_FRACBITS))
					& ((1 << OpenJpegConstant.T1_NMSEDEC_BITS) - 1)];
		}

		return OpenJpegConstant.LUT_NMSEDEC_REF_0[x & ((1 << OpenJpegConstant.T1_NMSEDEC_BITS) - 1)];
	}

	private void tier1UpdateFlags(int[] flagsp, int flagspIndex, int s, int stride) {
		int npIndex = flagspIndex - stride;
		int spIndex = flagspIndex + stride;

		flagsp[npIndex - 1] |= OpenJpegConstant.T1_SIG_SE;
		flagsp[npIndex + 0] |= OpenJpegConstant.T1_MOD[s];
		flagsp[npIndex + 1] |= OpenJpegConstant.T1_SIG_SW;

		flagsp[flagspIndex - 1] |= OpenJpegConstant.T1_MOD[s + 2];
		flagsp[flagspIndex + 0] |= OpenJpegConstant.T1_SIG;
		flagsp[flagspIndex + 1] |= OpenJpegConstant.T1_MOD[s + 4];

		flagsp[spIndex - 1] |= OpenJpegConstant.T1_SIG_NE;
		flagsp[spIndex + 0] |= OpenJpegConstant.T1_MOD[s + 6];
		flagsp[spIndex + 1] |= OpenJpegConstant.T1_SIG_NW;
	}

	@SuppressWarnings({ "java:S107", "java:S1659", "java:S3776" })
	private void tier1EncodeSigpassStep(Tier1 t1, int[] flagsp, int flagspIndex, int[] data, int dataIndex, int orient,
			int bpno, int one, int[] nmsedec, int nmsedecIndex, int type, int vsc) {
		int v;
		int flag;

		MQCoder mqc = t1.getMqc(); /* MQC component */

		flag = vsc != 0
				? ((flagsp[flagspIndex]) & (~(OpenJpegConstant.T1_SIG_S | OpenJpegConstant.T1_SIG_SE
						| OpenJpegConstant.T1_SIG_SW | OpenJpegConstant.T1_SGN_S)))
				: (flagsp[flagspIndex]);
		if ((flag & OpenJpegConstant.T1_SIG_OTH) != 0
				&& (flag & (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT)) == 0) {
			v = (MathUtil.getInstance().intAbs(data[dataIndex]) & one) != 0 ? 1 : 0;
			MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, tier1GetContextNoZC(flag, orient)); /* ESSAI */
			if (type == OpenJpegConstant.T1_TYPE_RAW) { /* BYPASS/LAZY MODE */
				MQCoderHelper.getInstance().mqcBypassEncode(mqc, v);
			} else {
				MQCoderHelper.getInstance().mqcEncode(mqc, v);
			}
			if (v != 0) {
				v = data[dataIndex] < 0 ? 1 : 0;
				nmsedec[nmsedecIndex] += tier1GetNmseDecSig(MathUtil.getInstance().intAbs(data[dataIndex]),
						bpno + OpenJpegConstant.T1_NMSEDEC_FRACBITS);
				MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, tier1GetContextNoSC(flag)); /* ESSAI */
				if (type == OpenJpegConstant.T1_TYPE_RAW) { /* BYPASS/LAZY MODE */
					MQCoderHelper.getInstance().mqcBypassEncode(mqc, v);
				} else {
					MQCoderHelper.getInstance().mqcEncode(mqc, v ^ tier1GetSPB(flag));
				}
				tier1UpdateFlags(flagsp, flagspIndex, v, t1.getFlagsStride());
			}
			flagsp[flagspIndex] |= OpenJpegConstant.T1_VISIT;
		}
	}

	@SuppressWarnings({ "java:S107", "java:S1659", "java:S3776" })
	private void tier1DecodeSigpassStep(Tier1 t1, int[] flagsp, int flagspIndex, int[] data, int dataIndex, int orient,
			int oneplushalf, int type, int vsc) {
		int v;
		int flag;

		Raw raw = t1.getRaw(); /* RAW component */
		MQCoder mqc = t1.getMqc(); /* MQC component */

		flag = vsc != 0
				? ((flagsp[flagspIndex]) & (~(OpenJpegConstant.T1_SIG_S | OpenJpegConstant.T1_SIG_SE
						| OpenJpegConstant.T1_SIG_SW | OpenJpegConstant.T1_SGN_S)))
				: (flagsp[flagspIndex]);
		if ((flag & OpenJpegConstant.T1_SIG_OTH) != 0
				&& (flag & (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT)) == 0) {
			if (type == OpenJpegConstant.T1_TYPE_RAW) {
				if (RawHelper.getInstance().rawDecode(raw) != 0) {
					v = RawHelper.getInstance().rawDecode(raw); /* ESSAI */
					data[dataIndex] = v != 0 ? -oneplushalf : oneplushalf;
					tier1UpdateFlags(flagsp, flagspIndex, v, t1.getFlagsStride());
				}
			} else {
				MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, tier1GetContextNoZC(flag, orient));
				if (MQCoderHelper.getInstance().mqcDecode(mqc) != 0) {
					MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, tier1GetContextNoSC(flag));
					v = MQCoderHelper.getInstance().mqcDecode(mqc) ^ tier1GetSPB(flag);
					data[dataIndex] = v != 0 ? -oneplushalf : oneplushalf;
					tier1UpdateFlags(flagsp, flagspIndex, v, t1.getFlagsStride());
				}
			}
			flagsp[flagspIndex] |= OpenJpegConstant.T1_VISIT;
		}
	} /* VSC and BYPASS */

	@SuppressWarnings({ "java:S1659" })
	private void tier1EncodeSigpass(Tier1 t1, int bpno, int orient, int[] nmsedec, int nmsedecIndex, int type,
			int cblksty) {
		int i, j, k, one, vsc;
		nmsedec[nmsedecIndex] = 0;
		one = 1 << (bpno + OpenJpegConstant.T1_NMSEDEC_FRACBITS);
		for (k = 0; k < t1.getHeight(); k += 4) {
			for (i = 0; i < t1.getWidth(); ++i) {
				for (j = k; j < k + 4 && j < t1.getHeight(); ++j) {
					vsc = ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_VSC) != 0
							&& (j == k + 3 || j == t1.getHeight() - 1)) ? 1 : 0;
					tier1EncodeSigpassStep(t1, t1.getFlags(), (((j + 1) * t1.getFlagsStride()) + i + 1), t1.getData(),
							((j * t1.getWidth()) + i), orient, bpno, one, nmsedec, nmsedecIndex, type, vsc);
				}
			}
		}
	}

	@SuppressWarnings({ "java:S1659" })
	private void tier1DecodeSigpass(Tier1 t1, int bpno, int orient, int type, int cblksty) {
		int i, j, k, one, half, oneplushalf, vsc;
		one = 1 << bpno;
		half = one >> 1;
		oneplushalf = one | half;
		for (k = 0; k < t1.getHeight(); k += 4) {
			for (i = 0; i < t1.getWidth(); ++i) {
				for (j = k; j < k + 4 && j < t1.getHeight(); ++j) {
					vsc = ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_VSC) != 0
							&& (j == k + 3 || j == t1.getHeight() - 1)) ? 1 : 0;
					tier1DecodeSigpassStep(t1, t1.getFlags(), (((j + 1) * t1.getFlagsStride()) + i + 1), t1.getData(),
							((j * t1.getWidth()) + i), orient, oneplushalf, type, vsc);
				}
			}
		}
	}
	/* VSC and BYPASS */

	@SuppressWarnings({ "java:S107", "java:S1659" })
	private void tier1EncodeRefpassStep(Tier1 t1, int[] flagsp, int flagspIndex, int[] data, int dataIndex, int bpno,
			int one, int[] nmsedec, int nmsedecIndex, int type, int vsc) {
		int v, flag;

		MQCoder mqc = t1.getMqc(); /* MQC component */

		flag = vsc != 0
				? ((flagsp[flagspIndex]) & (~(OpenJpegConstant.T1_SIG_S | OpenJpegConstant.T1_SIG_SE
						| OpenJpegConstant.T1_SIG_SW | OpenJpegConstant.T1_SGN_S)))
				: (flagsp[flagspIndex]);
		if ((flag & (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT)) == OpenJpegConstant.T1_SIG) {
			nmsedec[nmsedecIndex] += tier1GetNmseDecRef(MathUtil.getInstance().intAbs(data[dataIndex]),
					bpno + OpenJpegConstant.T1_NMSEDEC_FRACBITS);
			v = (MathUtil.getInstance().intAbs(data[dataIndex]) & one) != 0 ? 1 : 0;
			MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, tier1GetContextNoMAG(flag)); /* ESSAI */
			if (type == OpenJpegConstant.T1_TYPE_RAW) { /* BYPASS/LAZY MODE */
				MQCoderHelper.getInstance().mqcBypassEncode(mqc, v);
			} else {
				MQCoderHelper.getInstance().mqcEncode(mqc, v);
			}
			flagsp[flagspIndex] |= OpenJpegConstant.T1_REFINE;
		}
	}

	@SuppressWarnings({ "java:S107", "java:S1659" })
	private void tier1DecodeRefpassStep(Tier1 t1, int[] flagsp, int flagspIndex, int[] data, int dataIndex, int poshalf,
			int neghalf, int type, int vsc) {
		int v, t, flag;

		MQCoder mqc = t1.getMqc(); /* MQC component */
		Raw raw = t1.getRaw(); /* RAW component */

		flag = vsc != 0
				? ((flagsp[flagspIndex]) & (~(OpenJpegConstant.T1_SIG_S | OpenJpegConstant.T1_SIG_SE
						| OpenJpegConstant.T1_SIG_SW | OpenJpegConstant.T1_SGN_S)))
				: (flagsp[flagspIndex]);
		if ((flag & (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT)) == OpenJpegConstant.T1_SIG) {
			MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, tier1GetContextNoMAG(flag)); /* ESSAI */
			if (type == OpenJpegConstant.T1_TYPE_RAW) {
				v = RawHelper.getInstance().rawDecode(raw);
			} else {
				v = MQCoderHelper.getInstance().mqcDecode(mqc);
			}
			t = v != 0 ? poshalf : neghalf;
			data[dataIndex] += data[dataIndex] < 0 ? -t : t;
			flagsp[flagspIndex] |= OpenJpegConstant.T1_REFINE;
		}
	} /* VSC and BYPASS */

	@SuppressWarnings({ "java:S1659" })
	private void tier1EncodeRefpass(Tier1 t1, int bpno, int[] nmsedec, int nmsedecIndex, int type, int cblksty) {
		int i, j, k, one, vsc;
		nmsedec[nmsedecIndex] = 0;
		one = 1 << (bpno + OpenJpegConstant.T1_NMSEDEC_FRACBITS);
		for (k = 0; k < t1.getHeight(); k += 4) {
			for (i = 0; i < t1.getWidth(); ++i) {
				for (j = k; j < k + 4 && j < t1.getHeight(); ++j) {
					vsc = ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_VSC) != 0
							&& (j == k + 3 || j == t1.getHeight() - 1)) ? 1 : 0;
					tier1EncodeRefpassStep(t1, t1.getFlags(), (((j + 1) * t1.getFlagsStride()) + i + 1), t1.getData(),
							((j * t1.getWidth()) + i), bpno, one, nmsedec, nmsedecIndex, type, vsc);
				}
			}
		}
	}

	@SuppressWarnings({ "java:S1659" })
	private void tier1DecodeRefpassStep(Tier1 t1, int bpno, int type, int cblksty) {
		int i, j, k, one, poshalf, neghalf;
		int vsc;
		one = 1 << bpno;
		poshalf = one >> 1;
		neghalf = bpno > 0 ? -poshalf : -1;
		for (k = 0; k < t1.getHeight(); k += 4) {
			for (i = 0; i < t1.getWidth(); ++i) {
				for (j = k; j < k + 4 && j < t1.getHeight(); ++j) {
					vsc = ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_VSC) != 0
							&& (j == k + 3 || j == t1.getHeight() - 1)) ? 1 : 0;
					tier1DecodeRefpassStep(t1, t1.getFlags(), (((j + 1) * t1.getFlagsStride()) + i + 1), t1.getData(),
							((j * t1.getWidth()) + i), poshalf, neghalf, type, vsc);
				}
			}
		}
	}
	/* VSC and BYPASS */

	@SuppressWarnings({ "java:S107", "java:S1659", "java:S3776" })
	private void tier1EncodeClnpassStep(Tier1 t1, int[] flagsp, int flagspIndex, int[] data, int dataIndex, int orient,
			int bpno, int one, int[] nmsedec, int nmsedecIndex, int partial, int vsc) {
		int v, flag;
		boolean labelPartial = false;
		MQCoder mqc = t1.getMqc(); /* MQC component */

		flag = vsc != 0
				? ((flagsp[flagspIndex]) & (~(OpenJpegConstant.T1_SIG_S | OpenJpegConstant.T1_SIG_SE
						| OpenJpegConstant.T1_SIG_SW | OpenJpegConstant.T1_SGN_S)))
				: (flagsp[flagspIndex]);
		if (partial != 0) {
			labelPartial = true;
		}
		if (!labelPartial) {
			if ((flagsp[flagspIndex] & (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT)) == 0) {
				MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, tier1GetContextNoZC(flag, orient));
				v = (MathUtil.getInstance().intAbs(data[dataIndex]) & one) != 0 ? 1 : 0;
				MQCoderHelper.getInstance().mqcEncode(mqc, v);
				if (v != 0) {
					nmsedec[nmsedecIndex] += tier1GetNmseDecSig(MathUtil.getInstance().intAbs(data[dataIndex]),
							bpno + OpenJpegConstant.T1_NMSEDEC_FRACBITS);
					MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, tier1GetContextNoSC(flag));
					v = data[dataIndex] < 0 ? 1 : 0;
					MQCoderHelper.getInstance().mqcEncode(mqc, v ^ tier1GetSPB(flag));
					tier1UpdateFlags(flagsp, flagspIndex, v, t1.getFlagsStride());
				}
			}
		} else {
			nmsedec[nmsedecIndex] += tier1GetNmseDecSig(MathUtil.getInstance().intAbs(data[dataIndex]),
					bpno + OpenJpegConstant.T1_NMSEDEC_FRACBITS);
			MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, tier1GetContextNoSC(flag));
			v = data[dataIndex] < 0 ? 1 : 0;
			MQCoderHelper.getInstance().mqcEncode(mqc, v ^ tier1GetSPB(flag));
			tier1UpdateFlags(flagsp, flagspIndex, v, t1.getFlagsStride());
		}
		flagsp[flagspIndex] &= ~OpenJpegConstant.T1_VISIT;
	}

	@SuppressWarnings({ "java:S107", "java:S1659", "java:S3776" })
	private void tier1DecodeClnpassStep(Tier1 t1, int[] flagsp, int flagspIndex, int[] data, int dataIndex, int orient,
			int oneplushalf, int partial, int vsc) {
		int v, flag;
		boolean labelPartial = false;
		MQCoder mqc = t1.getMqc(); /* MQC component */

		flag = vsc != 0
				? ((flagsp[flagspIndex]) & (~(OpenJpegConstant.T1_SIG_S | OpenJpegConstant.T1_SIG_SE
						| OpenJpegConstant.T1_SIG_SW | OpenJpegConstant.T1_SGN_S)))
				: (flagsp[flagspIndex]);

		if (partial != 0) {
			labelPartial = true;
		}
		if (!labelPartial) {
			if ((flag & (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT)) == 0) {
				int zc = tier1GetContextNoZC(flag, orient);
				MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, zc);
				if (MQCoderHelper.getInstance().mqcDecode(mqc) != 0) {
					MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, tier1GetContextNoSC(flag));
					v = MQCoderHelper.getInstance().mqcDecode(mqc) ^ tier1GetSPB(flag);
					data[dataIndex] = v != 0 ? -oneplushalf : oneplushalf;
					tier1UpdateFlags(flagsp, flagspIndex, v, t1.getFlagsStride());
				}
			}
		} else {
			MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, tier1GetContextNoSC(flag));
			v = MQCoderHelper.getInstance().mqcDecode(mqc) ^ tier1GetSPB(flag);
			data[dataIndex] = v != 0 ? -oneplushalf : oneplushalf;
			tier1UpdateFlags(flagsp, flagspIndex, v, t1.getFlagsStride());
		}
		flagsp[flagspIndex] &= ~OpenJpegConstant.T1_VISIT;
	}
	/* VSC and BYPASS */

	private int tier1GetFlags(Tier1 t1, int x, int y) {
		return t1.getFlags()[((x) * (t1.getFlagsStride())) + (y)];
	}

	@SuppressWarnings({ "java:S1659", "java:S3776" })
	private void tier1EncodeClnpass(Tier1 t1, int bpno, int orient, int[] nmsedec, int nmsedecIndex, int cblksty) {
		int i, j, k, one, agg, runLength, vsc;

		MQCoder mqc = t1.getMqc(); /* MQC component */

		nmsedec[nmsedecIndex] = 0;
		one = 1 << (bpno + OpenJpegConstant.T1_NMSEDEC_FRACBITS);
		for (k = 0; k < t1.getHeight(); k += 4) {
			for (i = 0; i < t1.getWidth(); ++i) {
				if (k + 3 < t1.getHeight()) {
					if ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_VSC) != 0) {
						agg = !((tier1GetFlags(t1, 1 + k, 1 + i) & (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT
								| OpenJpegConstant.T1_SIG_OTH)) != 0
								|| (tier1GetFlags(t1, 1 + k + 1, 1 + i) & (OpenJpegConstant.T1_SIG
										| OpenJpegConstant.T1_VISIT | OpenJpegConstant.T1_SIG_OTH)) != 0
								|| (tier1GetFlags(t1, 1 + k + 2, 1 + i) & (OpenJpegConstant.T1_SIG
										| OpenJpegConstant.T1_VISIT | OpenJpegConstant.T1_SIG_OTH)) != 0
								|| ((tier1GetFlags(t1, 1 + k + 3, 1 + i)
										& (~(OpenJpegConstant.T1_SIG_S | OpenJpegConstant.T1_SIG_SE
												| OpenJpegConstant.T1_SIG_SW | OpenJpegConstant.T1_SGN_S)))
										& (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT
												| OpenJpegConstant.T1_SIG_OTH)) != 0) ? 1 : 0;
					} else {
						agg = !((tier1GetFlags(t1, 1 + k, 1 + i) & (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT
								| OpenJpegConstant.T1_SIG_OTH)) != 0
								|| (tier1GetFlags(t1, 1 + k + 1, 1 + i) & (OpenJpegConstant.T1_SIG
										| OpenJpegConstant.T1_VISIT | OpenJpegConstant.T1_SIG_OTH)) != 0
								|| (tier1GetFlags(t1, 1 + k + 2, 1 + i) & (OpenJpegConstant.T1_SIG
										| OpenJpegConstant.T1_VISIT | OpenJpegConstant.T1_SIG_OTH)) != 0
								|| (tier1GetFlags(t1, 1 + k + 3, 1 + i) & (OpenJpegConstant.T1_SIG
										| OpenJpegConstant.T1_VISIT | OpenJpegConstant.T1_SIG_OTH)) != 0) ? 1 : 0;
					}
				} else {
					agg = 0;
				}
				if (agg != 0) {
					for (runLength = 0; runLength < 4; ++runLength) {
						if ((MathUtil.getInstance().intAbs(t1.getData()[((k + runLength) * t1.getWidth()) + i])
								& one) != 0)
							break;
					}
					MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, OpenJpegConstant.T1_CTXNO_AGG);
					MQCoderHelper.getInstance().mqcEncode(mqc, runLength != 4 ? 1 : 0);
					if (runLength == 4) {
						continue;
					}
					MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, OpenJpegConstant.T1_CTXNO_UNI);
					MQCoderHelper.getInstance().mqcEncode(mqc, runLength >> 1);
					MQCoderHelper.getInstance().mqcEncode(mqc, runLength & 1);
				} else {
					runLength = 0;
				}
				for (j = k + runLength; j < k + 4 && j < t1.getHeight(); ++j) {
					vsc = ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_VSC) != 0
							&& (j == k + 3 || j == t1.getHeight() - 1)) ? 1 : 0;
					tier1EncodeClnpassStep(t1, t1.getFlags(), (((j + 1) * t1.getFlagsStride()) + i + 1), t1.getData(),
							((j * t1.getWidth()) + i), orient, bpno, one, nmsedec, nmsedecIndex,
							((agg != 0 && (j == k + runLength)) ? 1 : 0), vsc);
				}
			}
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776", "java:S6541" })
	private void tier1DecodeClnpassStep(Tier1 t1, int bpno, int orient, int cblksty) {
		int i, j, k, one, half, oneplushalf, agg, runLength, vsc;
		int segsym = (cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_SEGSYM) != 0 ? 1 : 0;

		MQCoder mqc = t1.getMqc(); /* MQC component */

		one = 1 << bpno;
		half = one >> 1;
		oneplushalf = one | half;
		for (k = 0; k < t1.getHeight(); k += 4) {
			for (i = 0; i < t1.getWidth(); ++i) {
				if (k + 3 < t1.getHeight()) {
					if ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_VSC) != 0) {
						agg = !((tier1GetFlags(t1, 1 + k, 1 + i) & (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT
								| OpenJpegConstant.T1_SIG_OTH)) != 0
								|| (tier1GetFlags(t1, 1 + k + 1, 1 + i) & (OpenJpegConstant.T1_SIG
										| OpenJpegConstant.T1_VISIT | OpenJpegConstant.T1_SIG_OTH)) != 0
								|| (tier1GetFlags(t1, 1 + k + 2, 1 + i) & (OpenJpegConstant.T1_SIG
										| OpenJpegConstant.T1_VISIT | OpenJpegConstant.T1_SIG_OTH)) != 0
								|| ((tier1GetFlags(t1, 1 + k + 3, 1 + i)
										& (~(OpenJpegConstant.T1_SIG_S | OpenJpegConstant.T1_SIG_SE
												| OpenJpegConstant.T1_SIG_SW | OpenJpegConstant.T1_SGN_S)))
										& (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT
												| OpenJpegConstant.T1_SIG_OTH)) != 0) ? 1 : 0;
					} else {
						agg = !((tier1GetFlags(t1, 1 + k, 1 + i) & (OpenJpegConstant.T1_SIG | OpenJpegConstant.T1_VISIT
								| OpenJpegConstant.T1_SIG_OTH)) != 0
								|| (tier1GetFlags(t1, 1 + k + 1, 1 + i) & (OpenJpegConstant.T1_SIG
										| OpenJpegConstant.T1_VISIT | OpenJpegConstant.T1_SIG_OTH)) != 0
								|| (tier1GetFlags(t1, 1 + k + 2, 1 + i) & (OpenJpegConstant.T1_SIG
										| OpenJpegConstant.T1_VISIT | OpenJpegConstant.T1_SIG_OTH)) != 0
								|| (tier1GetFlags(t1, 1 + k + 3, 1 + i) & (OpenJpegConstant.T1_SIG
										| OpenJpegConstant.T1_VISIT | OpenJpegConstant.T1_SIG_OTH)) != 0) ? 1 : 0;
					}
				} else {
					agg = 0;
				}
				if (agg != 0) {
					MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, OpenJpegConstant.T1_CTXNO_AGG);
					int decodeValue = MQCoderHelper.getInstance().mqcDecode(mqc);
					if (decodeValue == 0) {
						continue;
					}
					MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, OpenJpegConstant.T1_CTXNO_UNI);
					runLength = MQCoderHelper.getInstance().mqcDecode(mqc);
					runLength = (runLength << 1) | MQCoderHelper.getInstance().mqcDecode(mqc);
				} else {
					runLength = 0;
				}
				for (j = k + runLength; j < k + 4 && j < t1.getHeight(); ++j) {
					vsc = ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_VSC) != 0
							&& (j == k + 3 || j == t1.getHeight() - 1)) ? 1 : 0;

					tier1DecodeClnpassStep(t1, t1.getFlags(), (((j + 1) * t1.getFlagsStride()) + i + 1), t1.getData(),
							((j * t1.getWidth()) + i), orient, oneplushalf, (agg != 0 && (j == k + runLength) ? 1 : 0),
							vsc);
				}
			}
		}
		if (segsym != 0) {
			int v = 0;
			MQCoderHelper.getInstance().mqcSetCurrentContext(mqc, OpenJpegConstant.T1_CTXNO_UNI);
			v = MQCoderHelper.getInstance().mqcDecode(mqc);
			v = (v << 1) | MQCoderHelper.getInstance().mqcDecode(mqc);
			v = (v << 1) | MQCoderHelper.getInstance().mqcDecode(mqc);
			v = (v << 1) | MQCoderHelper.getInstance().mqcDecode(mqc);
		}
	}
	/* VSC and BYPASS */

	/** mod fixed_quality */
	@SuppressWarnings({ "java:S107", "java:S1659" })
	private double tier1GetWmseDecode(int nmsedec, int compno, int level, int orient, int bpno, int qmfbid,
			double stepsize, int numcomps) {
		double w1, w2, wmsedec;
		if (qmfbid == 1) {
			w1 = (numcomps > 1) ? MctHelper.getInstance().mctGetNorm(compno) : 1.0;
			w2 = DwtHelper.getInstance().dwtGetNorm(level, orient);
		} else {
			w1 = (numcomps > 1) ? MctHelper.getInstance().mctGetNormReal(compno) : 1.0;
			w2 = DwtHelper.getInstance().dwtGetNormReal(level, orient);
		}
		wmsedec = w1 * w2 * stepsize * (1 << bpno);
		wmsedec *= wmsedec * nmsedec / 8192.0;

		return wmsedec;
	}

	private int allocateBuffers(Tier1 t1, int w, int h) {
		int datasize = w * h;
		int flagssize;

		if (datasize > t1.getDataSize()) {
			t1.setData(new int[datasize]);
			t1.setDataSize(datasize);
		}
		for (int index = 0; index < datasize; index++)
			t1.getData()[index] = 0;

		t1.setFlagsStride(w + 2);
		flagssize = t1.getFlagsStride() * (h + 2);

		if (flagssize > t1.getFlagsSize()) {
			t1.setFlags(new int[flagssize]);
			t1.setFlagsSize(flagssize);
		}
		for (int index = 0; index < flagssize; index++)
			t1.getFlags()[index] = 0;

		t1.setWidth(w);
		t1.setHeight(h);

		return 1;
	}

	/** mod fixed_quality */
	@SuppressWarnings({ "java:S107", "java:S1659", "java:S1854", "java:S3776", "java:S3923", "java:S6541" })
	private void tier1EncodeCodeBlock(Tier1 t1, TcdCodeBlockEncoder cblk, int orient, int compno, int level, int qmfbid,
			double stepsize, int cblksty, int numcomps, TcdTile tile) {
		double cumwmsedec = 0.0;

		MQCoder mqc = t1.getMqc(); /* MQC component */

		int passno, bpno, passtype;
		int nmsedecIndex = 0;
		int[] nmsedec = new int[1];
		int i, max;
		int type = OpenJpegConstant.T1_TYPE_MQ;
		double tempwmsedec;

		max = 0;
		for (i = 0; i < t1.getWidth() * t1.getHeight(); ++i) {
			int tmp = Math.abs(t1.getData()[i]);
			max = MathUtil.getInstance().intMax(max, tmp);
		}

		cblk.setNoOfBps(
				max != 0 ? (MathUtil.getInstance().intFloorLog2(max) + 1) - OpenJpegConstant.T1_NMSEDEC_FRACBITS : 0);

		bpno = cblk.getNoOfBps() - 1;
		passtype = 2;

		MQCoderHelper.getInstance().mqcResetStates(mqc);
		MQCoderHelper.getInstance().mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_UNI, 0, 46);
		MQCoderHelper.getInstance().mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_AGG, 0, 3);
		MQCoderHelper.getInstance().mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_ZC, 0, 4);
		MQCoderHelper.getInstance().mqcInitEncode(mqc, cblk.getData(), cblk.getDataIndex());

		for (passno = 0; bpno >= 0; ++passno) {
			TcdPass pass = cblk.getPasses()[passno];
			int correction = 3;
			type = ((bpno < (cblk.getNoOfBps() - 4)) && (passtype < 2)
					&& (cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_LAZY) != 0) ? OpenJpegConstant.T1_TYPE_RAW
							: OpenJpegConstant.T1_TYPE_MQ;

			switch (passtype) {
			case 0:
				tier1EncodeSigpass(t1, bpno, orient, nmsedec, nmsedecIndex, type, cblksty);
				break;
			case 1:
				tier1EncodeRefpass(t1, bpno, nmsedec, nmsedecIndex, type, cblksty);
				break;
			case 2:
				tier1EncodeClnpass(t1, bpno, orient, nmsedec, nmsedecIndex, cblksty);
				/* code switch SEGMARK (i.e. SEGSYM) */
				if ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_SEGSYM) != 0)
					MQCoderHelper.getInstance().mqcSegMarkEncode(mqc);
				break;
			default:
				break;
			}

			/* fixed_quality */
			tempwmsedec = tier1GetWmseDecode(nmsedec[nmsedecIndex], compno, level, orient, bpno, qmfbid, stepsize,
					numcomps);
			cumwmsedec += tempwmsedec;
			tile.setDistortionTile(tile.getDistortionTile() + tempwmsedec);

			/* Code switch "RESTART" (i.e. TERMALL) */
			if ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_TERMALL) != 0 && !((passtype == 2) && (bpno - 1 < 0))) {
				if (type == OpenJpegConstant.T1_TYPE_RAW) {
					MQCoderHelper.getInstance().mqcFlush(mqc);
					correction = 1;
				} else {
					MQCoderHelper.getInstance().mqcFlush(mqc);
					correction = 1;
				}
				pass.setTerm(1);
			} else {
				if (((bpno < (cblk.getNoOfBps() - 4) && (passtype > 0))
						|| ((bpno == (cblk.getNoOfBps() - 4)) && (passtype == 2)))
						&& (cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_LAZY) != 0) {
					if (type == OpenJpegConstant.T1_TYPE_RAW) {
						MQCoderHelper.getInstance().mqcFlush(mqc);
						correction = 1;
					} else {
						MQCoderHelper.getInstance().mqcFlush(mqc);
						correction = 1;
					}
					pass.setTerm(1);
				} else {
					pass.setTerm(0);
				}
			}

			if (++passtype == 3) {
				passtype = 0;
				bpno--;
			}

			if (pass.getTerm() != 0 && bpno > 0) {
				type = ((bpno < (cblk.getNoOfBps() - 4)) && (passtype < 2)
						&& (cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_LAZY) != 0) ? OpenJpegConstant.T1_TYPE_RAW
								: OpenJpegConstant.T1_TYPE_MQ;
				if (type == OpenJpegConstant.T1_TYPE_RAW)
					MQCoderHelper.getInstance().mqcBypassInitEncode(mqc);
				else
					MQCoderHelper.getInstance().mqcRestartInitEncode(mqc);
			}

			pass.setDistortionDec(cumwmsedec);
			pass.setRate(MQCoderHelper.getInstance().mqcNoOfBytes(mqc) + correction); /* FIX ME */

			/* Code-switch "RESET" */
			if ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_RESET) != 0)
				MQCoderHelper.getInstance().mqcResetEncode(mqc);
		}

		/* Code switch "ERTERM" (i.e. PTERM) */
		if ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_PTERM) != 0)
			MQCoderHelper.getInstance().mqcErTermEncode(mqc);
		else /* Default coding */
		if ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_LAZY) == 0)
			MQCoderHelper.getInstance().mqcFlush(mqc);

		cblk.setTotalPasses(passno);

		for (passno = 0; passno < cblk.getTotalPasses(); passno++) {
			TcdPass pass = cblk.getPasses()[passno];
			if (pass.getRate() > MQCoderHelper.getInstance().mqcNoOfBytes(mqc))
				pass.setRate(MQCoderHelper.getInstance().mqcNoOfBytes(mqc));
			/* Preventing generation of FF as last data byte of a pass */
			if ((pass.getRate() > 1) && (cblk.getData()[pass.getRate() - 1] == (byte) 0xFF)) {
				pass.setRate(pass.getRate() - 1);
			}
			pass.setLength(pass.getRate() - (passno == 0 ? 0 : cblk.getPasses()[passno - 1].getRate()));
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776" })
	private void tier1DecodeCodeBlock(Tier1 t1, TcdCodeBlockDecoder cblk, int orient, int roishift, int cblksty) {
		Raw raw = t1.getRaw(); /* RAW component */
		MQCoder mqc = t1.getMqc(); /* MQC component */

		int bpno, passtype;
		int segno, passno;
		int type = OpenJpegConstant.T1_TYPE_MQ; /* BYPASS mode */

		if (allocateBuffers(t1, cblk.getX1() - cblk.getX0(), cblk.getY1() - cblk.getY0()) == 0) {
			return;
		}

		bpno = roishift + cblk.getNoOfBps() - 1;
		passtype = 2;

		MQCoderHelper.getInstance().mqcResetStates(mqc);
		MQCoderHelper.getInstance().mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_UNI, 0, 46);
		MQCoderHelper.getInstance().mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_AGG, 0, 3);
		MQCoderHelper.getInstance().mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_ZC, 0, 4);

		for (segno = 0; segno < cblk.getNoOfSegs(); ++segno) {
			TcdSegment seg = cblk.getSegs()[segno];

			/* BYPASS mode */
			type = ((bpno <= (cblk.getNoOfBps() - 1) - 4) && (passtype < 2)
					&& (cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_LAZY) != 0) ? OpenJpegConstant.T1_TYPE_RAW
							: OpenJpegConstant.T1_TYPE_MQ;
			/*
			 * FIX ME: check why we get here with a null pointer. Why? Partially downloaded
			 * and/or corrupt textures?
			 */
			if (seg.getData() == null) {
				continue;
			}
			if (type == OpenJpegConstant.T1_TYPE_RAW) {
				RawHelper.getInstance().rawInitDecode(raw, seg.getData(), seg.getDataIndex(), seg.getLength());
			} else {
				MQCoderHelper.getInstance().mqcInitDecode(mqc, seg.getData(), seg.getDataIndex(), seg.getLength());
			}

			for (passno = 0; passno < seg.getNoOfPasses(); ++passno) {
				switch (passtype) {
				case 0:
					tier1DecodeSigpass(t1, bpno + 1, orient, type, cblksty);
					break;
				case 1:
					tier1DecodeRefpassStep(t1, bpno + 1, type, cblksty);
					break;
				case 2:
					tier1DecodeClnpassStep(t1, bpno + 1, orient, cblksty);
					break;
				default:
					break;
				}

				if ((cblksty & OpenJpegConstant.J2K_CCP_CBLKSTY_RESET) != 0 && type == OpenJpegConstant.T1_TYPE_MQ) {
					MQCoderHelper.getInstance().mqcResetStates(mqc);
					MQCoderHelper.getInstance().mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_UNI, 0, 46);
					MQCoderHelper.getInstance().mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_AGG, 0, 3);
					MQCoderHelper.getInstance().mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_ZC, 0, 4);
				}
				if (++passtype == 3) {
					passtype = 0;
					bpno--;
				}
			}
		}
	}

	/* ----------------------------------------------------------------------- */
	public Tier1 tier1Create(CodecContextInfo cinfo) {
		Tier1 t1 = new Tier1();

		t1.setCodecContextInfo(cinfo);
		/* create MQC and RAW handles */
		t1.setMqc(MQCoderHelper.getInstance().mqcCreate());
		t1.setRaw(RawHelper.getInstance().rawCreate());

		t1.setData(null);
		t1.setFlags(null);
		t1.setDataSize(0);
		t1.setFlagsSize(0);

		return t1;
	}

	public void tier1Destroy(Tier1 t1) {
		if (t1 != null) {
			/* destroy MQC and RAW handles */
			MQCoderHelper.getInstance().mqcDestroy(t1.getMqc());
			RawHelper.getInstance().rawDestroy(t1.getRaw());
			t1.setData(null);
			t1.setFlags(null);
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S3776" })
	public void tier1EncodeCodeBlocks(Tier1 t1, TcdTile tile, Tcp tcp) {
		int compno, resNo, bandNo, precNo, codeBlockNo;

		tile.setDistortionTile(0); /* fixed_quality */

		for (compno = 0; compno < tile.getNoOfComps(); ++compno) {
			TcdTileComponent tilec = tile.getComps()[compno];
			TileComponentCodingParameters tccp = tcp.getTccps()[compno];
			int tileWidth = tilec.getX1() - tilec.getX0();

			for (resNo = 0; resNo < tilec.getNoOfResolutions(); ++resNo) {
				TcdResolution res = tilec.getResolutions()[resNo];

				for (bandNo = 0; bandNo < res.getNoOfBands(); ++bandNo) {
					TcdBand band = res.getBands()[bandNo];

					for (precNo = 0; precNo < res.getPWidth() * res.getPHeight(); ++precNo) {
						TcdPrecinct prc = band.getPrecincts()[precNo];

						for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); ++codeBlockNo) {
							TcdCodeBlockEncoder cblk = prc.getTcdCodeBlockEncoder()[codeBlockNo];
							int codeBlockWidth;
							int codeBlockHeight;
							int i, j;

							int x = cblk.getX0() - band.getX0();
							int y = cblk.getY0() - band.getY0();
							if ((band.getBandNo() & 1) != 0) {
								TcdResolution pres = tilec.getResolutions()[resNo - 1];
								x += pres.getX1() - pres.getX0();
							}
							if ((band.getBandNo() & 2) != 0) {
								TcdResolution pres = tilec.getResolutions()[resNo - 1];
								y += pres.getY1() - pres.getY0();
							}

							if (allocateBuffers(t1, cblk.getX1() - cblk.getX0(), cblk.getY1() - cblk.getY0()) == 0) {
								return;
							}

							codeBlockWidth = t1.getWidth();
							codeBlockHeight = t1.getHeight();

							int fromIndex = (y * tileWidth) + x;
							if (tccp.getQmfbid() == 1) {
								for (j = 0; j < codeBlockHeight; ++j) {
									for (i = 0; i < codeBlockWidth; ++i) {
										int tmp = tilec.getIData()[fromIndex + ((j * tileWidth) + i)];
										t1.getData()[(j * codeBlockWidth)
												+ i] = tmp << OpenJpegConstant.T1_NMSEDEC_FRACBITS;
									}
								}
							} else {
								for (j = 0; j < codeBlockHeight; ++j) {
									for (i = 0; i < codeBlockWidth; ++i) {
										int tmp = (int) tilec.getFData()[fromIndex + ((j * tileWidth) + i)];
										t1.getData()[(j * codeBlockWidth) + i] = fixMul(tmp,
												8192 * 8192 / ((int) Math.floor(band.getStepSize() * 8192))) >> (11
														- OpenJpegConstant.T1_NMSEDEC_FRACBITS);
									}
								}
							}

							tier1EncodeCodeBlock(t1, cblk, band.getBandNo(), compno,
									tilec.getNoOfResolutions() - 1 - resNo, tccp.getQmfbid(), band.getStepSize(),
									tccp.getCodeBlockStyle(), tile.getNoOfComps(), tile);

						} /* codeBlockNo */
					} /* precNo */
				} /* bandNo */
			} /* resNo */
		} /* compno */
	}

	@SuppressWarnings({ "java:S1659", "java:S3776", "java:S6541" })
	public void tier1DecodeCodeBlocks(Tier1 t1, TcdTileComponent tilec, TileComponentCodingParameters tccp) {
		int resNo, bandNo, precNo, codeBlockNo;
		int tileWidth = tilec.getX1() - tilec.getX0();

		for (resNo = 0; resNo < tilec.getNoOfResolutions(); ++resNo) {
			TcdResolution res = tilec.getResolutions()[resNo];

			for (bandNo = 0; bandNo < res.getNoOfBands(); ++bandNo) {
				TcdBand band = res.getBands()[bandNo];

				for (precNo = 0; precNo < res.getPWidth() * res.getPHeight(); ++precNo) {
					TcdPrecinct precinct = band.getPrecincts()[precNo];

					for (codeBlockNo = 0; codeBlockNo < precinct.getCWidth() * precinct.getCHeight(); ++codeBlockNo) {
						TcdCodeBlockDecoder cblk = precinct.getTcdCodeBlockDecoder()[codeBlockNo];
						int[] data = null;
						int codeBlockWidth, codeBlockHeight;
						int x, y;
						int i, j;

						tier1DecodeCodeBlock(t1, cblk, band.getBandNo(), tccp.getRoiShift(), tccp.getCodeBlockStyle());

						x = cblk.getX0() - band.getX0();
						y = cblk.getY0() - band.getY0();
						if ((band.getBandNo() & 1) != 0) {
							TcdResolution pres = tilec.getResolutions()[resNo - 1];
							x += pres.getX1() - pres.getX0();
						}
						if ((band.getBandNo() & 2) != 0) {
							TcdResolution pres = tilec.getResolutions()[resNo - 1];
							y += pres.getY1() - pres.getY0();
						}

						data = t1.getData();
						codeBlockWidth = t1.getWidth();
						codeBlockHeight = t1.getHeight();

						if (tccp.getRoiShift() != 0) {
							int thresh = 1 << tccp.getRoiShift();
							for (j = 0; j < codeBlockHeight; ++j) {
								for (i = 0; i < codeBlockWidth; ++i) {
									int val = data[(j * codeBlockWidth) + i];
									int mag = Math.abs(val);
									if (mag >= thresh) {
										mag >>= tccp.getRoiShift();
										data[(j * codeBlockWidth) + i] = val < 0 ? -mag : mag;
									}
								}
							}
						}

						int fromIndex = (y * tileWidth) + x;
						if (tccp.getQmfbid() == 1) {
							tilec.setFData(null);
							for (j = 0; j < codeBlockHeight; ++j) {
								for (i = 0; i < codeBlockWidth; ++i) {
									int tmp = data[(j * codeBlockWidth) + i];
									tilec.getIData()[fromIndex + ((j * tileWidth) + i)] = tmp / 2;
								}
							}
						} else {
							tilec.setIData(null);
							for (j = 0; j < codeBlockHeight; ++j) {
								for (i = 0; i < codeBlockWidth; ++i) {
									double tmp = data[(j * codeBlockWidth) + i] * band.getStepSize();
									tilec.getFData()[fromIndex + ((j * tileWidth) + i)] = tmp;
								}
							}
						}
						cblk.setData(null);
						cblk.setSegs(null);
					} /* codeBlockNo */

					precinct.setTcdCodeBlockDecoder(null);
				} /* precNo */
			} /* bandNo */
		} /* resNo */
	}

	/**
	 * Multiply two fixed-precision rational numbers.
	 * 
	 * @param a
	 * @param b
	 * @return Returns a * b
	 */
	private int fixMul(int a, int b) {
		long temp = (long) a * b;
		temp += temp & 4096;
		return (int) (temp >> 13);
	}
}
