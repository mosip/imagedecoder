package io.mosip.imagedecoder.openjpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.CodecContextInfo;
import io.mosip.imagedecoder.model.openjpeg.CodingParameters;
import io.mosip.imagedecoder.model.openjpeg.J2K;
import io.mosip.imagedecoder.model.openjpeg.JP2;

public class CioHelper {
	private static Logger LOGGER = LoggerFactory.getLogger(CioHelper.class);
	// Static variable reference of singleInstance of type Singleton
    private static CioHelper singleInstance = null;    
    private CioHelper()
	{ 
		super ();
	} 
  
	//synchronized method to control simultaneous access 
	public static synchronized CioHelper getInstance()
	{ 
		if (singleInstance == null)
			singleInstance = new CioHelper();
  
        return singleInstance;
	}
	
	public Cio cioOpen(CodecContextInfo codecContextInfo, byte[] buffer, int length) {
		CodingParameters codingParameters = null;
		Cio cio = new Cio();
		cio.setCodecContextInfo(codecContextInfo);
		if (buffer != null && length != 0) {
			/* wrap a user buffer containing the encoded image */
			cio.setOpenMode(OpenJpegConstant.STREAM_READ);
			cio.setBuffer(buffer);
			cio.setLength(length);
		} else if (buffer == null && length == 0 && codecContextInfo != null) {
			/* allocate a buffer for the encoded image */
			cio.setOpenMode(OpenJpegConstant.STREAM_WRITE);
			switch (codecContextInfo.getContextInfo().getCodecFormat()) {
			case CODEC_J2K:
				codingParameters = ((J2K) codecContextInfo.getContextInfo().getJ2kHandle()).getCodingParameters();
				break;
			case CODEC_JP2:
				codingParameters = ((JP2) codecContextInfo.getContextInfo().getJp2Handle()).getJ2k().getCodingParameters();
				break;
			default:
				cio = null;
				return null;
			}
			/* 0.1625 = 1.3/8 and 2000 bytes as a minimum for headers */
			cio.setLength((int) (0.1625 * codingParameters.getImageSize() + 2000));
			cio.setBuffer(new byte[cio.getLength()]);
		} else {
			cio = null;
			return null;
		}

		/* Initialize byte IO */
		cio.setBpIndex(-1);
		cio.setStart(-1);
		cio.setEnd(0 + cio.getLength());
		cio.setBuffer(cio.getBuffer());

		return cio;
	}

	public void cioClose(Cio cio) {
		if (cio != null) {
			if (cio.getOpenMode() == OpenJpegConstant.STREAM_WRITE) {
				/* destroy the allocated buffer */
				cio.setBuffer(null);
			}
			/* destroy the cio */
			cio = null;
		}
	}

	/*
	 * Get position in byte stream.
	 */
	public int cioTell(Cio cio) {
		return cio.getBpIndex() - cio.getStart();
	}

	/*
	 * Set position in byte stream. pos : position, in number of bytes, from the
	 * beginning of the stream
	 */
	public void cioSeek(Cio cio, int pos) {
		cio.setBpIndex(cio.getStart() + pos);
	}

	/*
	 * Number of bytes left before the end of the stream.
	 */
	public int cioNoOfBytesLeft(Cio cio) {
		return cio.getEnd() - cio.getBpIndex();
	}

	/*
	 * Get Byte array to the current position in the stream.
	 */
	public byte[] cioGetBuffer(Cio cio) {
		byte[] data = new byte[cio.getEnd() - cio.getBpIndex()];
		System.arraycopy(cio.getBuffer(), cio.getBpIndex(), data, 0, data.length);
		cio.setBpIndex(data.length);
		return data;
	}

	/*
	 * get buffer index position.
	 */
	public int cioGetBufferIndex(Cio cio) {
		return cio.getBpIndex();
	}

	/*
	 * Write a byte.
	 */
	public int cioByteOut(Cio cio, byte value) {
		if (cio.getBpIndex() >= cio.getEnd()) {
			LOGGER.error(String.format("write error" + cio.getCodecContextInfo()));
			return 0;
		}
		cio.setBpIndex(cio.getBpIndex() + 1);
		cio.getBuffer()[cio.getBpIndex()] = value;
		return 1;
	}

	/*
	 * Read a byte.
	 */
	public byte cioByteIn(Cio cio) {
		if (cio.getBpIndex() >= cio.getEnd()) {
			LOGGER.error(String.format("read error: passed the end of the codestream (current = %d, end = %d)",
					cio.getBpIndex(), cio.getEnd()));
			return 0;
		}
		cio.setBpIndex(cio.getBpIndex() + 1);
		return cio.getBuffer()[cio.getBpIndex()];
	}

	/*
	 * Write some bytes. v : value to write noOfBytes : number of bytes to write
	 */
	public long cioWrite(Cio cio, long v, int noOfBytes) {
		int i;
		for (i = noOfBytes - 1; i >= 0; i--) {
			if (cioByteOut(cio, (byte) ((v >> (i << 3)) & 0xff)) == 0)
				return 0;
		}
		return noOfBytes;
	}

	/*
	 * Read some bytes. noOfBytes : number of bytes to read return : value of the n bytes
	 * read
	 */
	public long cioRead(Cio cio, int noOfBytes) {
		int i;
		long v;
		v = 0;
		for (i = noOfBytes - 1; i >= 0; i--) {
			v += (cioByteIn(cio) & 0xff) << (i << 3);
		}
		return v;
	}

	/*
	 * Skip some bytes. noOfBytes : number of bytes to skip
	 */
	public void cioSkip(Cio cio, int noOfBytes) {
		cio.setBpIndex(cio.getBpIndex() + noOfBytes);
	}

	/*
	 * set position . pos : position of buffer
	 */
	public void cioPosition(Cio cio, int pos) {
		cio.setBpIndex(pos);
	}
}
