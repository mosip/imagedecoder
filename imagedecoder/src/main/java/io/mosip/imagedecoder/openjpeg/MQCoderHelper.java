package io.mosip.imagedecoder.openjpeg;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.model.openjpeg.MQCoder;
import io.mosip.imagedecoder.model.openjpeg.MQCoderState;

@SuppressWarnings({ "java:S3008"})
public class MQCoderHelper {
	/* <summary> */
	/* This array defines all the possible states for a context. */
	/* </summary> */
	private static MQCoderState[] MQC_STATES = new MQCoderState[] { new MQCoderState(0x5601, 0, 2, 3),
			new MQCoderState(0x5601, 1, 3, 2), new MQCoderState(0x3401, 0, 4, 12),
			new MQCoderState(0x3401, 1, 5, 13), new MQCoderState(0x1801, 0, 6, 18),
			new MQCoderState(0x1801, 1, 7, 19), new MQCoderState(0x0ac1, 0, 8, 24),
			new MQCoderState(0x0ac1, 1, 9, 25), new MQCoderState(0x0521, 0, 10, 58),
			new MQCoderState(0x0521, 1, 11, 59), new MQCoderState(0x0221, 0, 76, 66),
			new MQCoderState(0x0221, 1, 77, 67), new MQCoderState(0x5601, 0, 14, 13),
			new MQCoderState(0x5601, 1, 15, 12), new MQCoderState(0x5401, 0, 16, 28),
			new MQCoderState(0x5401, 1, 17, 29), new MQCoderState(0x4801, 0, 18, 28),
			new MQCoderState(0x4801, 1, 19, 29), new MQCoderState(0x3801, 0, 20, 28),
			new MQCoderState(0x3801, 1, 21, 29), new MQCoderState(0x3001, 0, 22, 34),
			new MQCoderState(0x3001, 1, 23, 35), new MQCoderState(0x2401, 0, 24, 36),
			new MQCoderState(0x2401, 1, 25, 37), new MQCoderState(0x1c01, 0, 26, 40),
			new MQCoderState(0x1c01, 1, 27, 41), new MQCoderState(0x1601, 0, 58, 42),
			new MQCoderState(0x1601, 1, 59, 43), new MQCoderState(0x5601, 0, 30, 29),
			new MQCoderState(0x5601, 1, 31, 28), new MQCoderState(0x5401, 0, 32, 28),
			new MQCoderState(0x5401, 1, 33, 29), new MQCoderState(0x5101, 0, 34, 30),
			new MQCoderState(0x5101, 1, 35, 31), new MQCoderState(0x4801, 0, 36, 32),
			new MQCoderState(0x4801, 1, 37, 33), new MQCoderState(0x3801, 0, 38, 34),
			new MQCoderState(0x3801, 1, 39, 35), new MQCoderState(0x3401, 0, 40, 36),
			new MQCoderState(0x3401, 1, 41, 37), new MQCoderState(0x3001, 0, 42, 38),
			new MQCoderState(0x3001, 1, 43, 39), new MQCoderState(0x2801, 0, 44, 38),
			new MQCoderState(0x2801, 1, 45, 39), new MQCoderState(0x2401, 0, 46, 40),
			new MQCoderState(0x2401, 1, 47, 41), new MQCoderState(0x2201, 0, 48, 42),
			new MQCoderState(0x2201, 1, 49, 43), new MQCoderState(0x1c01, 0, 50, 44),
			new MQCoderState(0x1c01, 1, 51, 45), new MQCoderState(0x1801, 0, 52, 46),
			new MQCoderState(0x1801, 1, 53, 47), new MQCoderState(0x1601, 0, 54, 48),
			new MQCoderState(0x1601, 1, 55, 49), new MQCoderState(0x1401, 0, 56, 50),
			new MQCoderState(0x1401, 1, 57, 51), new MQCoderState(0x1201, 0, 58, 52),
			new MQCoderState(0x1201, 1, 59, 53), new MQCoderState(0x1101, 0, 60, 54),
			new MQCoderState(0x1101, 1, 61, 55), new MQCoderState(0x0ac1, 0, 62, 56),
			new MQCoderState(0x0ac1, 1, 63, 57), new MQCoderState(0x09c1, 0, 64, 58),
			new MQCoderState(0x09c1, 1, 65, 59), new MQCoderState(0x08a1, 0, 66, 60),
			new MQCoderState(0x08a1, 1, 67, 61), new MQCoderState(0x0521, 0, 68, 62),
			new MQCoderState(0x0521, 1, 69, 63), new MQCoderState(0x0441, 0, 70, 64),
			new MQCoderState(0x0441, 1, 71, 65), new MQCoderState(0x02a1, 0, 72, 66),
			new MQCoderState(0x02a1, 1, 73, 67), new MQCoderState(0x0221, 0, 74, 68),
			new MQCoderState(0x0221, 1, 75, 69), new MQCoderState(0x0141, 0, 76, 70),
			new MQCoderState(0x0141, 1, 77, 71), new MQCoderState(0x0111, 0, 78, 72),
			new MQCoderState(0x0111, 1, 79, 73), new MQCoderState(0x0085, 0, 80, 74),
			new MQCoderState(0x0085, 1, 81, 75), new MQCoderState(0x0049, 0, 82, 76),
			new MQCoderState(0x0049, 1, 83, 77), new MQCoderState(0x0025, 0, 84, 78),
			new MQCoderState(0x0025, 1, 85, 79), new MQCoderState(0x0015, 0, 86, 80),
			new MQCoderState(0x0015, 1, 87, 81), new MQCoderState(0x0009, 0, 88, 82),
			new MQCoderState(0x0009, 1, 89, 83), new MQCoderState(0x0005, 0, 90, 84),
			new MQCoderState(0x0005, 1, 91, 85), new MQCoderState(0x0001, 0, 90, 86),
			new MQCoderState(0x0001, 1, 91, 87), new MQCoderState(0x5601, 0, 92, 92),
			new MQCoderState(0x5601, 1, 93, 93) };

	static {
		for (int index = 0; index < MQC_STATES.length; index++) {
			MQCoderState state = MQC_STATES[index];
			state.setNmps(mqcGetNmpsState(state.getNmpsIndex()));
			state.setNlps(mqcGetNlpsState(state.getNlpsIndex()));
		}
	}

	// Static variable reference of singleInstance of type Singleton
    private static MQCoderHelper singleInstance = null;    
    private MQCoderHelper()
	{ 
		super ();
	} 
  
	//synchronized method to control simultaneous access 
	public static synchronized MQCoderHelper getInstance()
	{ 
		if (singleInstance == null)
			singleInstance = new MQCoderHelper();
  
        return singleInstance;
	}
	/*
	 * ========================================================== local functions
	 * ==========================================================
	 */

	private void mqcByteOut(MQCoder mqc) {
		if ((mqc.getBp()[mqc.getBpIndex()] & 0xff) == 0xff) {
			mqc.setBpIndex(mqc.getBpIndex() + 1);
			mqc.getBp()[mqc.getBpIndex()] = (byte) (mqc.getC() >> 20);
			mqc.setC(mqc.getC() & 0xfffff);
			mqc.setCt(7);
		} else {
			if ((mqc.getC() & 0x8000000) == 0) { /* ((mqc->c&0x8000000)==0) CHANGE */
				mqc.setBpIndex(mqc.getBpIndex() + 1);
				mqc.getBp()[mqc.getBpIndex()] = (byte) (mqc.getC() >> 19);
				mqc.setC(mqc.getC() & 0x7ffff);
				mqc.setCt(8);
			} else {
				mqc.setBpIndex(mqc.getBpIndex() + 1);
				if ((mqc.getBp()[mqc.getBpIndex()] & 0xff) == 0xff) {
					mqc.setC(mqc.getC() & 0x7ffffff);
					mqc.setBpIndex(mqc.getBpIndex() + 1);
					mqc.getBp()[mqc.getBpIndex()] = (byte) (mqc.getC() >> 20);
					mqc.setC(mqc.getC() & 0xfffff);
					mqc.setCt(7);
				} else {
					mqc.setBpIndex(mqc.getBpIndex() + 1);
					mqc.getBp()[mqc.getBpIndex()] = (byte) (mqc.getC() >> 19);
					mqc.setC(mqc.getC() & 0x7ffff);
					mqc.setCt(8);
				}
			}
		}
	}

	private void mqcRenormalization(MQCoder mqc) {
		do {
			mqc.setA(mqc.getA() << 1);
			mqc.setC(mqc.getC() << 1);
			mqc.setCt(mqc.getCt() - 1);
			if (mqc.getCt() == 0) {
				mqcByteOut(mqc);
			}
		} while ((mqc.getA() & 0x8000) == 0);
	}

	private void mqcCodeMps(MQCoder mqc) {
		mqc.setA(mqc.getA() - mqc.getCurrentContext().getQeval());
		if ((mqc.getA() & 0x8000) == 0) {
			if (mqc.getA() < mqc.getCurrentContext().getQeval()) {
				mqc.setA(mqc.getCurrentContext().getQeval());
			} else {
				mqc.setC(mqc.getC() + mqc.getCurrentContext().getQeval());
			}
			mqc.setCurrentContext(mqc.getCurrentContext().getNmps());
			mqcRenormalization(mqc);
		} else {
			mqc.setC(mqc.getC() + mqc.getCurrentContext().getQeval());
		}
	}

	private void mqcCodeLps(MQCoder mqc) {
		mqc.setA(mqc.getA() - mqc.getCurrentContext().getQeval());
		if (mqc.getA() < mqc.getCurrentContext().getQeval()) {
			mqc.setC(mqc.getC() + mqc.getCurrentContext().getQeval());
		} else {
			mqc.setA(mqc.getCurrentContext().getQeval());
		}
		mqc.setCurrentContext(mqc.getCurrentContext().getNlps());
		mqcRenormalization(mqc);
	}

	private void mqcSetBits(MQCoder mqc) {
		long tempc = mqc.getC() + mqc.getA();
		mqc.setC(mqc.getC() | 0xffff);
		if (mqc.getC() >= tempc) {
			mqc.setC(mqc.getC() - 0x8000);
		}
	}

	private void mqcByteIn(MQCoder mqc) {
		if (mqc.getBpIndex() < (mqc.getEnd())) {
			long c;
			if ((mqc.getBpIndex() + 1) < (mqc.getEnd())) {
				c = (mqc.getBp()[mqc.getBpIndex() + 1] & 0xff);
			} else {
				c = 0xff;
			}
			if ((mqc.getBp()[mqc.getBpIndex()] & 0xff) == 0xff) {
				if (c > 0x8f) {
					mqc.setC(mqc.getC() + 0xff00);
					mqc.setCt(8);
				} else {
					mqc.setBpIndex(mqc.getBpIndex() + 1);
					mqc.setC(mqc.getC() + (c << 9));
					mqc.setCt(7);
				}
			} else {
				mqc.setBpIndex(mqc.getBpIndex() + 1);
				mqc.setC(mqc.getC() + (c << 8));
				mqc.setCt(8);
			}
		} else {
			mqc.setC(mqc.getC() + 0xff00);
			mqc.setCt(8);
		}
	}

	private void mqcRenormalizationD(MQCoder mqc) {
		do {
			if (mqc.getCt() == 0) {
				mqcByteIn(mqc);
			}
			mqc.setA(mqc.getA() << 1);
			mqc.setC(mqc.getC() << 1);
			mqc.setCt(mqc.getCt() - 1);
		} while (mqc.getA() < 0x8000);
	}

	public MQCoder mqcCreate() {
		return new MQCoder();
	}

	@SuppressWarnings({ "java:S1186"})
	public void mqcDestroy(MQCoder mqc) {
	}

	public int mqcNoOfBytes(MQCoder mqc) {
		return mqc.getBpIndex() - mqc.getStart();
	}

	@SuppressWarnings("unused")
	public void mqcInitEncode(MQCoder mqc, byte[] bp, int bpIndex) 
	{
		mqcSetCurrentContext(mqc, 0);
		mqc.setA(0x8000);
		mqc.setC(0);
		mqc.setBpIndex(bpIndex);
		mqc.setContextIndex(0);
		mqc.setCt(12);
		if ((mqc.getBp()[mqc.getBpIndex()] & 0xff) == 0xff) {
			mqc.setCt(13);
		}
		mqc.setStart(bpIndex);
	}

	public void mqcEncode(MQCoder mqc, int d) {
		if (mqc.getCurrentContext().getMps() == d) {
			mqcCodeMps(mqc);
		} else {
			mqcCodeLps(mqc);
		}
	}

	public void mqcFlush(MQCoder mqc) {
		mqcSetBits(mqc);
		mqc.setC(mqc.getC() << mqc.getCt());
		mqcByteOut(mqc);
		mqc.setC(mqc.getC() << mqc.getCt());
		mqcByteOut(mqc);

		if ((mqc.getBp()[mqc.getBpIndex()] & 0xff) != 0xff) {
			mqc.setBpIndex(mqc.getBpIndex() + 1);
		}
	}

	public void mqcBypassInitEncode(MQCoder mqc) {
		mqc.setC(0);
		mqc.setCt(8);
	}

	public void mqcBypassEncode(MQCoder mqc, int d) {
		mqc.setCt(mqc.getCt() - 1);
		mqc.setC(mqc.getC() + (d << mqc.getCt()));
		if (mqc.getCt() == 0) {
			mqc.setBpIndex(mqc.getBpIndex() + 1);
			mqc.getBp()[mqc.getBpIndex()] = (byte) mqc.getC();
			mqc.setCt(8);
			if ((mqc.getBp()[mqc.getBpIndex()] & 0xff) == 0xff) {
				mqc.setCt(7);
			}
			mqc.setC(0);
		}
	}

	public int mqcBypassFlushEncode(MQCoder mqc) {
		int bitPadding;

		bitPadding = 0;

		if (mqc.getCt() != 0) {
			while (mqc.getCt() > 0) {
				mqc.setCt(mqc.getCt() - 1);
				mqc.setC(mqc.getC() + bitPadding << mqc.getCt());
				bitPadding = (bitPadding + 1) & 0x01;
			}
			mqc.setBpIndex(mqc.getBpIndex() + 1);
			mqc.getBp()[mqc.getBpIndex()] = (byte) mqc.getC();
			mqc.setCt(8);
			mqc.setC(0);
		}

		return 1;
	}

	public void mqcResetEncode(MQCoder mqc) {
		mqcResetStates(mqc);
		mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_UNI, 0, 46);
		mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_AGG, 0, 3);
		mqcSetState(mqc, OpenJpegConstant.T1_CTXNO_ZC, 0, 4);
	}

	public int mqcRestartEncode(MQCoder mqc) {
		int correction = 1;

		/* <flush part> */
		int n = (int) (27 - 15 - mqc.getCt());
		mqc.setC(mqc.getC() << mqc.getCt());
		while (n > 0) {
			mqcByteOut(mqc);
			n -= mqc.getCt();
			mqc.setC(mqc.getC() << mqc.getCt());
		}
		mqcByteOut(mqc);

		return correction;
	}

	public void mqcRestartInitEncode(MQCoder mqc) {
		/* <Re-init part> */
		mqcSetCurrentContext(mqc, 0);
		mqc.setA(0x8000);
		mqc.setC(0);
		mqc.setCt(12);
		mqc.setBpIndex(mqc.getBpIndex() - 1);
		if ((mqc.getBp()[mqc.getBpIndex()] & 0xff) == 0xff) {
			mqc.setCt(13);
		}
	}

	public void mqcErTermEncode(MQCoder mqc) {
		int k = (int) (11 - mqc.getCt() + 1);

		while (k > 0) {
			mqc.setC(mqc.getC() << mqc.getCt());
			mqc.setCt(0);
			mqcByteOut(mqc);
			k -= mqc.getCt();
		}

		if ((mqc.getBp()[mqc.getBpIndex()] & 0xff) != 0xff) {
			mqcByteOut(mqc);
		}
	}

	public void mqcSegMarkEncode(MQCoder mqc) {
		int i;
		mqcSetCurrentContext(mqc, 18);

		for (i = 1; i < 5; i++) {
			mqcEncode(mqc, i % 2);
		}
	}

	public void mqcInitDecode(MQCoder mqc, byte[] bp, int bpIndex, int len) {
		mqcSetCurrentContext(mqc, 0);
		mqc.setStart(bpIndex);
		mqc.setBpIndex(bpIndex);
		mqc.setEnd(bpIndex + len);
		mqc.setBp(bp);
		if (len == 0)
			mqc.setC(0xff << 16);
		else
			mqc.setC((mqc.getBp()[mqc.getBpIndex()] & 0xff) << 16);

		mqcByteIn(mqc);
		mqc.setC(mqc.getC() << 7);
		mqc.setCt(mqc.getCt() - 7);
		mqc.setA(0x8000);
	}

	private int mqcLpsExchange(MQCoder mqc) {
		int d;
		if (mqc.getA() < mqc.getCurrentContext().getQeval()) {
			mqc.setA(mqc.getCurrentContext().getQeval());
			d = mqc.getCurrentContext().getMps();

			MQCoderState state = mqc.getCurrentContext().getNmps();
			mqc.setCurrentContext(state);
			mqc.getContexts()[mqc.getContextIndex()] = state;
		} else {
			mqc.setA(mqc.getCurrentContext().getQeval());
			d = 1 - mqc.getCurrentContext().getMps();

			MQCoderState state = mqc.getCurrentContext().getNlps();
			mqc.setCurrentContext(state);
			mqc.getContexts()[mqc.getContextIndex()] = state;
		}

		return d;
	}

	private int mqcMpsExchange(MQCoder mqc) {
		int d;
		if (mqc.getA() < mqc.getCurrentContext().getQeval()) {
			d = 1 - mqc.getCurrentContext().getMps();

			MQCoderState state = mqc.getCurrentContext().getNlps();
			mqc.setCurrentContext(state);
			mqc.getContexts()[mqc.getContextIndex()] = state;
		} else {
			d = mqc.getCurrentContext().getMps();

			MQCoderState state = mqc.getCurrentContext().getNmps();
			mqc.setCurrentContext(state);
			mqc.getContexts()[mqc.getContextIndex()] = state;
		}

		return d;
	}

	public int mqcDecode(MQCoder mqc) {
		int d;

		mqc.setA(mqc.getA() - mqc.getCurrentContext().getQeval());
		if ((mqc.getC() >> 16) < mqc.getCurrentContext().getQeval()) {
			d = mqcLpsExchange(mqc);
			mqcRenormalizationD(mqc);
		} else {
			mqc.setC(mqc.getC() - (mqc.getCurrentContext().getQeval() << 16));
			if ((mqc.getA() & 0x8000) == 0) {
				d = mqcMpsExchange(mqc);
				mqcRenormalizationD(mqc);
			} else {
				d = mqc.getCurrentContext().getMps();
			}
		}

		return d;
	}

	public void mqcResetStates(MQCoder mqc) {
		int i;
		for (i = 0; i < OpenJpegConstant.MQC_NUMCTXS; i++) {
			mqc.getContexts()[i] = MQC_STATES[0];
		}
		mqc.setContextIndex(0);
	}

	public void mqcSetState(MQCoder mqc, int ctxno, int msb, int prob) {
		mqc.getContexts()[ctxno] = MQC_STATES[msb + (prob << 1)];
	}

	private static MQCoderState mqcGetNlpsState(int nlpsIndex) {
		return MQC_STATES[nlpsIndex];
	}

	private static MQCoderState mqcGetNmpsState(int nmpsIndex) {
		return MQC_STATES[nmpsIndex];
	}

	public void mqcSetCurrentContext(MQCoder mqc, int ctxno) {
		mqc.setContextIndex(ctxno);
		mqc.setCurrentContext(mqc.getContexts()[ctxno]);
	}
}
