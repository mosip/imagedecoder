package io.mosip.imagedecoder.openjpeg;

import io.mosip.imagedecoder.model.openjpeg.Raw;

public class RawHelper {
	// Static variable reference of singleInstance of type Singleton
    private static RawHelper singleInstance = null;    
    private RawHelper()
	{ 
		super ();
	} 
  
	//synchronized method to control simultaneous access 
	public static synchronized RawHelper getInstance()
	{ 
		if (singleInstance == null)
			singleInstance = new RawHelper();
  
        return singleInstance;
	}
	
	public Raw rawCreate() {
		Raw raw = new Raw();
		return raw;
	}

	public void rawDestroy(Raw raw) {
		if(raw != null) {
			raw = null;
		}
	}

	public int rawNoOfBytes(Raw raw) {
		return raw.getBpIndex() - raw.getStart();
	}

	public void rawInitDecode(Raw raw, byte[] bp, int bpIndex, int len) {
		raw.setBpIndex(bpIndex);
		raw.setStart(bpIndex);
		raw.setLengthMax(len);
		raw.setLength(0);
		raw.setC(0);
		raw.setCt(0);
	}

	public int rawDecode(Raw raw) {
		int d;
		if (raw.getCt() == 0) {
			raw.setCt(8);
			if (raw.getLength() == raw.getLengthMax()) {
				raw.setC((byte)0xff);
			} else {
				if (raw.getC() == (byte)0xff) {
					raw.setCt(7);
				}
				raw.setC(raw.getBp()[(int) raw.getLength()]);
				raw.setLength(raw.getLength () + 1);
			}
		}
		raw.setCt(raw.getCt() - 1);
		d = (raw.getC() >> raw.getCt()) & 0x01;
		
		return d;
	}
}
