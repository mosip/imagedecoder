package io.mosip.imagedecoder.openjpeg;

import io.mosip.imagedecoder.model.openjpeg.Bio;

public class BioHelper {
	// Static variable reference of singleInstance of type Singleton
    private static BioHelper singleInstance = null;    
    private BioHelper()
	{ 
		super ();
	} 
  
	//synchronized method to control simultaneous access 
	public static synchronized BioHelper getInstance()
	{ 
		if (singleInstance == null)
			singleInstance = new BioHelper();
  
        return singleInstance;
	}
	
	public int bioByteOut(Bio bio) {
		bio.setBuf((bio.getBuf() << 8) & 0xffff);
		bio.setCt(bio.getBuf() == 0xff00 ? 7 : 8);
		if (bio.getBpIndex() >= bio.getEnd()) {
			return 1;
		}
		bio.setBpIndex(bio.getBpIndex() + 1);
		bio.getBp()[bio.getBpIndex()] = (byte) (bio.getBuf() >> 8);
		return 0;
	}

	public int bioByteIn(Bio bio) {
		bio.setBuf((bio.getBuf() << 8) & 0xffff);
		bio.setCt(bio.getBuf() == 0xff00 ? 7 : 8);
		if (bio.getBpIndex() >= bio.getEnd()) {
			return 1;
		}
		bio.setBuf(bio.getBuf() | (bio.getBp()[bio.getBpIndex()] & 0xff));
		bio.setBpIndex(bio.getBpIndex() + 1);
		return 0;
	}

	public void bioPutBit(Bio bio, int b) {
		if (bio.getCt() == 0) {
			bioByteOut(bio);
		}
		bio.setCt(bio.getCt() - 1);
		bio.setBuf(bio.getBuf() | (b << bio.getCt()));
	}

	public int bioGetBit(Bio bio) {
		if (bio.getCt() == 0) {
			bioByteIn(bio);
		}
		bio.setCt(bio.getCt() - 1);
		return (int) ((bio.getBuf() >> bio.getCt()) & 1);
	}

	public Bio bioCreate() {
		Bio bio = new Bio();
		return bio;
	}

	public void bioDestroy(Bio bio) {
		if (bio != null)
			bio = null;
	}

	public int bioNoOfBytes(Bio bio) {
		return (bio.getBpIndex() - bio.getStart());
	}

	public void bioInitEncoder(Bio bio, byte[] bp, int len) {
		bio.setStart(0);
		bio.setBpIndex(0);
		bio.setEnd(0 + len);
		bio.setBp(bp);
		bio.setBuf(0);
		bio.setCt(8);
	}

	public void bioInitDecoder(Bio bio, byte[] bp, int len) {
		bio.setStart(0);
		bio.setBpIndex(0);
		bio.setEnd(0 + len);
		bio.setBp(bp);
		bio.setBuf(0);
		bio.setCt(0);
	}

	public void bioWrite(Bio bio, int v, int n) {
		int i;
		for (i = n - 1; i >= 0; i--) {
			bioPutBit(bio, (v >> i) & 1);
		}
	}

	public int bioRead(Bio bio, int n) {
		int i, v;
		v = 0;
		for (i = n - 1; i >= 0; i--) {
			v += bioGetBit(bio) << i;
		}
		return v;
	}

	public int bioFlush(Bio bio) {
		bio.setCt(0);
		if (bioByteOut(bio) != 0) {
			return 1;
		}
		if (bio.getCt() == 7) {
			bio.setCt(0);
			if (bioByteOut(bio) != 0) {
				return 1;
			}
		}
		return 0;
	}

	public int bioInAlign(Bio bio) {
		bio.setCt(0);
		if ((bio.getBuf() & 0xff) == 0xff) {
			if (bioByteIn(bio) != 0) {
				return 1;
			}
			bio.setCt(0);
		}
		return 0;
	}
}
