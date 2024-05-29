package io.mosip.imagedecoder.openjpeg;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;
import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.CodeStreamInfo;
import io.mosip.imagedecoder.model.openjpeg.CodecContextInfo;
import io.mosip.imagedecoder.model.openjpeg.CompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.DecompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.J2K;
import io.mosip.imagedecoder.model.openjpeg.JP2;
import io.mosip.imagedecoder.model.openjpeg.JP2Box;
import io.mosip.imagedecoder.model.openjpeg.JP2Component;
import io.mosip.imagedecoder.model.openjpeg.Jp2ColorSpace;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.util.ByteStreamUtil;
import io.mosip.kernel.core.logger.spi.Logger;

public class JP2Helper {
	private Logger logger = ImageDecoderLogger.getLogger(JP2Helper.class);

	private J2KHelper j2k = new J2KHelper();

	public JP2Helper() {
		super();
	}

	public J2KHelper getJ2k() {
		return j2k;
	}

	public void setJ2k(J2KHelper j2k) {
		this.j2k = j2k;
	}

	@SuppressWarnings({ "java:S1172"})
	private int jp2ReadBoxHeader(CodecContextInfo codecContextInfo, Cio cio, JP2Box box) {
		box.setInitPosition(CioHelper.getInstance().cioTell(cio));
		box.setLength((int) CioHelper.getInstance().cioRead(cio, 4));
		box.setType((int) CioHelper.getInstance().cioRead(cio, 4));
		if (box.getLength() == 1) {
			if (CioHelper.getInstance().cioRead(cio, 4) != 0) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Cannot handle box sizes higher than 2^32");
				return 0;
			}
			box.setLength((int) CioHelper.getInstance().cioRead(cio, 4));
			if (box.getLength() == 0)
				box.setLength(CioHelper.getInstance().cioNoOfBytesLeft(cio) + 12);
		} else if (box.getLength() == 0) {
			box.setLength(CioHelper.getInstance().cioNoOfBytesLeft(cio) + 8);
		}

		return 1;
	}

	@SuppressWarnings({ "java:S135", "unused"})
	private void jp2WriteUrl(Cio cio, char[] url) {
		int i;
		JP2Box box = new JP2Box();

		box.setInitPosition(CioHelper.getInstance().cioTell(cio));
		CioHelper.getInstance().cioSkip(cio, 4);
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.JP2_URL, 4); /* DBTL */
		CioHelper.getInstance().cioWrite(cio, 0, 1); /* VERS */
		CioHelper.getInstance().cioWrite(cio, 0, 3); /* FLAG */

		if (url != null) {
			for (i = 0; i < url.length; i++) {
				CioHelper.getInstance().cioWrite(cio, url[i], 1);
			}
		}

		box.setLength(CioHelper.getInstance().cioTell(cio) - box.getInitPosition());
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition());
		CioHelper.getInstance().cioWrite(cio, box.getLength(), 4); /* L */
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition() + box.getLength());
	}

	private int jp2ReadIHeader(JP2 jp2, Cio cio) {
		JP2Box box = new JP2Box();

		CodecContextInfo codecContextInfo = jp2.getCodecContextInfo();

		jp2ReadBoxHeader(codecContextInfo, cio, box);
		if (OpenJpegConstant.JP2_IHDR != box.getType()) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Expected IHDR Marker");
			return 0;
		}

		jp2.setHeight(CioHelper.getInstance().cioRead(cio, 4)); /* HEIGHT */
		jp2.setWidth(CioHelper.getInstance().cioRead(cio, 4)); /* WIDTH */
		jp2.setNoOfComps(CioHelper.getInstance().cioRead(cio, 2)); /* NC */
		jp2.setComps(new JP2Component[(int) jp2.getNoOfComps()]);

		jp2.setBpc(CioHelper.getInstance().cioRead(cio, 1)); /* BPC */

		jp2.setC(CioHelper.getInstance().cioRead(cio, 1)); /* C */
		jp2.setUnknownC(CioHelper.getInstance().cioRead(cio, 1)); /* UnkC */
		jp2.setIpr(CioHelper.getInstance().cioRead(cio, 1)); /* IPR */

		if (CioHelper.getInstance().cioTell(cio) - box.getInitPosition() != box.getLength()) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Error with IHDR Box");
			return 0;
		}

		return 1;
	}

	public void jp2WriteIHeader(JP2 jp2, Cio cio) {
		JP2Box box = new JP2Box();

		box.setInitPosition(CioHelper.getInstance().cioTell(cio));
		CioHelper.getInstance().cioSkip(cio, 4);
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.JP2_IHDR, 4); /* IHDR */

		CioHelper.getInstance().cioWrite(cio, jp2.getHeight(), 4); /* HEIGHT */
		CioHelper.getInstance().cioWrite(cio, jp2.getWidth(), 4); /* WIDTH */
		CioHelper.getInstance().cioWrite(cio, jp2.getNoOfComps(), 2); /* NC */

		CioHelper.getInstance().cioWrite(cio, jp2.getBpc(), 1); /* BPC */

		CioHelper.getInstance().cioWrite(cio, jp2.getC(), 1); /* C : Always 7 */
		CioHelper.getInstance().cioWrite(cio, jp2.getUnknownC(), 1); /* UnkC, colorspace unknown */
		CioHelper.getInstance().cioWrite(cio, jp2.getIpr(), 1); /* IPR, no intellectual property */

		box.setLength(CioHelper.getInstance().cioTell(cio) - box.getInitPosition());
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition());
		CioHelper.getInstance().cioWrite(cio, box.getLength(), 4); /* L */
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition() + box.getLength());
	}

	public void jp2WriteBpcc(JP2 jp2, Cio cio) {
		int i;
		JP2Box box = new JP2Box();

		box.setInitPosition(CioHelper.getInstance().cioTell(cio));
		CioHelper.getInstance().cioSkip(cio, 4);
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.JP2_BPCC, 4); /* BPCC */

		for (i = 0; i < jp2.getNoOfComps(); i++) {
			CioHelper.getInstance().cioWrite(cio, jp2.getComps()[i].getBpcc(), 1);
		}

		box.setLength(CioHelper.getInstance().cioTell(cio) - box.getInitPosition());
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition());
		CioHelper.getInstance().cioWrite(cio, box.getLength(), 4); /* L */
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition() + box.getLength());
	}

	private int jp2ReadBpcc(JP2 jp2, Cio cio) {
		int i;
		JP2Box box = new JP2Box();

		CodecContextInfo codecContextInfo = jp2.getCodecContextInfo();

		jp2ReadBoxHeader(codecContextInfo, cio, box);
		if (OpenJpegConstant.JP2_BPCC != box.getType()) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Expected BPCC Marker");
			return 0;
		}

		for (i = 0; i < jp2.getNoOfComps(); i++) {
			jp2.getComps()[i].setBpcc((int) CioHelper.getInstance().cioRead(cio, 1));
		}

		if (CioHelper.getInstance().cioTell(cio) - box.getInitPosition() != box.getLength()) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Error with BPCC Box");
			return 0;
		}

		return 1;
	}

	public void jp2WriteColr(JP2 jp2, Cio cio) {
		JP2Box box = new JP2Box();

		box.setInitPosition(CioHelper.getInstance().cioTell(cio));
		CioHelper.getInstance().cioSkip(cio, 4);
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.JP2_COLR, 4); /* COLR */

		CioHelper.getInstance().cioWrite(cio, jp2.getMeth(), 1); /* METH */
		CioHelper.getInstance().cioWrite(cio, jp2.getPrecedence(), 1); /* PRECEDENCE */
		CioHelper.getInstance().cioWrite(cio, jp2.getApprox(), 1); /* APPROX */

		if (jp2.getMeth() == 1) {
			CioHelper.getInstance().cioWrite(cio, jp2.getEnumcs(), 4); /* EnumCS */
		} else {
			CioHelper.getInstance().cioWrite(cio, 0, 1); /* PROFILE (??) */
		}

		box.setLength(CioHelper.getInstance().cioTell(cio) - box.getInitPosition());
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition());
		CioHelper.getInstance().cioWrite(cio, box.getLength(), 4); /* L */
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition() + box.getLength());
	}

	private int jp2ReadColr(JP2 jp2, Cio cio) {
		JP2Box box = new JP2Box();
		int skipLength;

		CodecContextInfo codecContextInfo = jp2.getCodecContextInfo();

		jp2ReadBoxHeader(codecContextInfo, cio, box);
		do {
			if (OpenJpegConstant.JP2_COLR != box.getType()) {
				CioHelper.getInstance().cioSkip(cio, box.getLength() - 8);
				jp2ReadBoxHeader(codecContextInfo, cio, box);
			}
		} while (OpenJpegConstant.JP2_COLR != box.getType());

		jp2.setMeth(CioHelper.getInstance().cioRead(cio, 1)); /* METH */
		jp2.setPrecedence(CioHelper.getInstance().cioRead(cio, 1)); /* PRECEDENCE */
		jp2.setApprox(CioHelper.getInstance().cioRead(cio, 1)); /* APPROX */

		if (jp2.getMeth() == 1) {
			jp2.setEnumcs(CioHelper.getInstance().cioRead(cio, 4)); /* EnumCS */
		} else {
			/* skip PROFILE */
			skipLength = box.getInitPosition() + box.getLength() - CioHelper.getInstance().cioTell(cio);
			if (skipLength < 0) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Error with Colr box size");
				return 0;
			}
			CioHelper.getInstance().cioSkip(cio,
					box.getInitPosition() + box.getLength() - CioHelper.getInstance().cioTell(cio));
		}

		if (CioHelper.getInstance().cioTell(cio) - box.getInitPosition() != box.getLength()) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Error with Colr Box");
			return 0;
		}
		return 1;
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3358", "unused"})
	private int jp2ReadRes(JP2 jp2, Cio cio) {
		JP2Box box = new JP2Box();
		int skipLength;

		CodecContextInfo codecContextInfo = jp2.getCodecContextInfo();

		jp2ReadBoxHeader(codecContextInfo, cio, box);
		if (OpenJpegConstant.JP2_RES == box.getType() && box.getLength() == 26) {
			CioHelper.getInstance().cioSkip(cio, 4);
			long resx = CioHelper.getInstance().cioRead(cio, 4);
			long resc1 = ByteStreamUtil.getInstance().makeBETag('r', 'e', 's', ' ');
			long resc = ByteStreamUtil.getInstance().makeBETag('r', 'e', 's', 'c');
			long resd = ByteStreamUtil.getInstance().makeBETag('r', 'e', 's', 'd');
			if (resx != resc && resx != resd)
				return 1;

			int resolutionBoxType = resx == resc ? 0 : resx == resd ? 1 : -1;

			jp2.getResolutionBox().setResolutionBoxType(resolutionBoxType); /* resolutionBoxType */
			jp2.getResolutionBox().setResolutionBoxType(resolutionBoxType);
			jp2.getResolutionBox().setVerticalNumerator((int) CioHelper.getInstance().cioRead(cio, 2));
			jp2.getResolutionBox().setVerticalDenominator((int) CioHelper.getInstance().cioRead(cio, 2));
			jp2.getResolutionBox().setHorizontalNumerator((int) CioHelper.getInstance().cioRead(cio, 2));
			jp2.getResolutionBox().setHorizontalDenominator((int) CioHelper.getInstance().cioRead(cio, 2));
			jp2.getResolutionBox().setVerticalExponent((int) CioHelper.getInstance().cioRead(cio, 1));
			jp2.getResolutionBox().setHorizontalExponent((int) CioHelper.getInstance().cioRead(cio, 1));

			double inchPerMeter = OpenJpegConstant.INCH_PER_METER;
			long vnum, vden, hnum, hden, vexp, hexp;
			vnum = jp2.getResolutionBox().getVerticalNumerator();
			vden = jp2.getResolutionBox().getVerticalDenominator();
			hnum = jp2.getResolutionBox().getHorizontalNumerator();
			hden = jp2.getResolutionBox().getHorizontalDenominator();
			vexp = jp2.getResolutionBox().getVerticalExponent();
			hexp = jp2.getResolutionBox().getHorizontalExponent();

			int verticalResolution = (int) Math
					.ceil(((Math.round(((double) vnum / vden) * Math.pow(10, vexp)) / inchPerMeter) / 100) * 100);
			int horizontalResolution = (int) Math
					.ceil(((Math.round(((double) hnum / hden) * Math.pow(10, hexp)) / inchPerMeter) / 100) * 100);
			jp2.getResolutionBox().setHorizontalResolution(horizontalResolution);
			jp2.getResolutionBox().setVerticalResolution(verticalResolution);

			if (CioHelper.getInstance().cioTell(cio) - box.getInitPosition() != box.getLength()) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Error with RES Box");
				return 0;
			}
		} else {
			CioHelper.getInstance().cioPosition(cio, box.getInitPosition() - 1);
		}
		return 1;
	}

	public void jp2WriteJP2h(JP2 jp2, Cio cio) {
		JP2Box box = new JP2Box();

		box.setInitPosition(CioHelper.getInstance().cioTell(cio));
		CioHelper.getInstance().cioSkip(cio, 4);
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.JP2_JP2H, 4); /* JP2H */

		jp2WriteIHeader(jp2, cio);

		if (jp2.getBpc() == 255) {
			jp2WriteBpcc(jp2, cio);
		}
		jp2WriteColr(jp2, cio);

		box.setLength(CioHelper.getInstance().cioTell(cio) - box.getInitPosition());
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition());
		CioHelper.getInstance().cioWrite(cio, box.getLength(), 4); /* L */
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition() + box.getLength());
	}

	private int jp2ReadJP2h(JP2 jp2, Cio cio) {
		JP2Box box = new JP2Box();
		int skipLength;

		CodecContextInfo codecContextInfo = jp2.getCodecContextInfo();

		jp2ReadBoxHeader(codecContextInfo, cio, box);
		do {
			if (OpenJpegConstant.JP2_JP2H != box.getType()) {
				if (box.getType() == OpenJpegConstant.JP2_JP2C) {
					logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Expected JP2H Marker");
					return 0;
				}
				CioHelper.getInstance().cioSkip(cio, box.getLength() - 8);
				jp2ReadBoxHeader(codecContextInfo, cio, box);
			}
		} while (OpenJpegConstant.JP2_JP2H != box.getType());

		if (jp2ReadIHeader(jp2, cio) == 0)
			return 0;

		if (jp2.getBpc() == 255 && jp2ReadBpcc(jp2, cio) == 0) {
			return 0;
		}
		if (jp2ReadColr(jp2, cio) == 0)
			return 0;

		if (jp2ReadRes(jp2, cio) == 0)
			return 0;

		skipLength = box.getInitPosition() + box.getLength() - CioHelper.getInstance().cioTell(cio);
		if (skipLength < 0) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Error with JP2H Box");
			return 0;
		}
		CioHelper.getInstance().cioSkip(cio,
				box.getInitPosition() + box.getLength() - CioHelper.getInstance().cioTell(cio));

		return 1;
	}

	public void jp2WriteFtyp(JP2 jp2, Cio cio) {
		int i;
		JP2Box box = new JP2Box();

		box.setInitPosition(CioHelper.getInstance().cioTell(cio));
		CioHelper.getInstance().cioSkip(cio, 4);
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.JP2_FTYP, 4); /* FTYP */

		CioHelper.getInstance().cioWrite(cio, jp2.getBrand(), 4); /* BR */
		CioHelper.getInstance().cioWrite(cio, jp2.getMinVersion(), 4); /* MinV */

		for (i = 0; i < jp2.getNoOfCl(); i++) {
			CioHelper.getInstance().cioWrite(cio, jp2.getCl()[i], 4); /* CL */
		}

		box.setLength(CioHelper.getInstance().cioTell(cio) - box.getInitPosition());
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition());
		CioHelper.getInstance().cioWrite(cio, box.getLength(), 4); /* L */
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition() + box.getLength());
	}

	private int jp2ReadFtyp(JP2 jp2, Cio cio) {
		int i;
		JP2Box box = new JP2Box();

		CodecContextInfo codecContextInfo = jp2.getCodecContextInfo();

		jp2ReadBoxHeader(codecContextInfo, cio, box);

		if (OpenJpegConstant.JP2_FTYP != box.getType()) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Expected FTYP Marker");
			return 0;
		}

		jp2.setBrand(CioHelper.getInstance().cioRead(cio, 4)); /* BR */
		jp2.setMinVersion(CioHelper.getInstance().cioRead(cio, 4)); /* MinV */
		jp2.setNoOfCl((box.getLength() - 16) / 4);
		jp2.setCl(new long[(int) jp2.getNoOfCl()]);

		for (i = 0; i < (int) jp2.getNoOfCl(); i++) {
			jp2.getCl()[i] = CioHelper.getInstance().cioRead(cio, 4); /* CLi */
		}

		if (CioHelper.getInstance().cioTell(cio) - box.getInitPosition() != box.getLength()) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Error with FTYP Box");
			return 0;
		}

		return 1;
	}

	public int jp2WriteJP2c(JP2 jp2, Cio cio, OpenJpegImage image, CodeStreamInfo codeStreamInfo, boolean useJPWL) {
		long[] j2kCodestreamOffset = new long[1];
		long[] j2kCodestreamLength = new long[1];
		JP2Box box = new JP2Box();

		J2K j2kInfo = jp2.getJ2k();

		box.setInitPosition(CioHelper.getInstance().cioTell(cio));
		CioHelper.getInstance().cioSkip(cio, 4);
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.JP2_JP2C, 4); /* JP2C */

		/* J2K encoding */
		j2kCodestreamOffset[0] = CioHelper.getInstance().cioTell(cio);
		if (this.getJ2k().j2kEncode(j2kInfo, cio, image, codeStreamInfo, useJPWL) == 0) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Failed to encode image");
			return 0;
		}
		j2kCodestreamLength[0] = CioHelper.getInstance().cioTell(cio) - j2kCodestreamOffset[0];

		jp2.getJ2kCodestreamOffset()[0] = j2kCodestreamOffset[0];
		jp2.getJ2kCodestreamLength()[0] = j2kCodestreamLength[0];

		box.setLength((int) (8 + jp2.getJ2kCodestreamLength()[0]));
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition());
		CioHelper.getInstance().cioWrite(cio, box.getLength(), 4); /* L */
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition() + box.getLength());

		return box.getLength();
	}

	private int jp2ReadJP2c(JP2 jp2, Cio cio, long[] j2kCodestreamLength, long[] j2kCodestreamOffset) {
		JP2Box box = new JP2Box();

		CodecContextInfo codecContextInfo = jp2.getCodecContextInfo();

		jp2ReadBoxHeader(codecContextInfo, cio, box);
		do {
			if (OpenJpegConstant.JP2_JP2C != box.getType()) {
				CioHelper.getInstance().cioSkip(cio, box.getLength() - 8);
				jp2ReadBoxHeader(codecContextInfo, cio, box);
			}
		} while (OpenJpegConstant.JP2_JP2C != box.getType());

		j2kCodestreamOffset[0] = CioHelper.getInstance().cioTell(cio);
		j2kCodestreamLength[0] = (long) box.getLength() - 8;

		return 1;
	}

	public void jp2WriteJP(Cio cio) {
		JP2Box box = new JP2Box();

		box.setInitPosition(CioHelper.getInstance().cioTell(cio));
		CioHelper.getInstance().cioSkip(cio, 4);
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.JP2_JP, 4); /* JP2 signature */
		CioHelper.getInstance().cioWrite(cio, 0x0d0a870a, 4);

		box.setLength(CioHelper.getInstance().cioTell(cio) - box.getInitPosition());
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition());
		CioHelper.getInstance().cioWrite(cio, box.getLength(), 4); /* L */
		CioHelper.getInstance().cioSeek(cio, box.getInitPosition() + box.getLength());
	}

	private int jp2ReadJP(JP2 jp2, Cio cio) {
		JP2Box box = new JP2Box();

		CodecContextInfo codecContextInfo = jp2.getCodecContextInfo();

		jp2ReadBoxHeader(codecContextInfo, cio, box);
		if (OpenJpegConstant.JP2_JP != box.getType()) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Expected JP Marker");
			return 0;
		}
		if (0x0d0a870a != CioHelper.getInstance().cioRead(cio, 4)) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Error with JP Marker");
			return 0;
		}
		if (CioHelper.getInstance().cioTell(cio) - box.getInitPosition() != box.getLength()) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Error with JP Box size");
			return 0;
		}

		return 1;
	}

	private int jp2ReadStruct(JP2 jp2, Cio cio) {
		if (jp2ReadJP(jp2, cio) == 0)
			return 0;
		if (jp2ReadFtyp(jp2, cio) == 0)
			return 0;
		if (jp2ReadJP2h(jp2, cio) == 0)
			return 0;
		if (jp2ReadJP2c(jp2, cio, jp2.getJ2kCodestreamLength(), jp2.getJ2kCodestreamOffset()) == 0)
			return 0;

		return 1;
	}

	/* ----------------------------------------------------------------------- */
	/* JP2 decoder interface */
	/* ----------------------------------------------------------------------- */
	public JP2 jp2CreateDecompression(CodecContextInfo codecContextInfo) {
		JP2 jp2 = new JP2();
		jp2.setCodecContextInfo(codecContextInfo);
		/* create the J2K codec */
		jp2.setJ2k(this.getJ2k().j2kCreateDecompression(codecContextInfo));
		if (jp2.getJ2k() == null) {
			jp2DestroyDecompression(jp2);
			return null;
		}
		return jp2;
	}

	public void jp2DestroyDecompression(JP2 jp2) {
		if (jp2 != null) {
			/* destroy the J2K codec */
			this.getJ2k().j2kDestroyDecompression(jp2.getJ2k());

			if (jp2.getComps() != null) {
				jp2.setComps(null);
			}
			if (jp2.getCl() != null) {
				jp2.setCl(null);
			}
		}
	}

	public void jp2SetupDecoder(JP2 jp2, DecompressionParameters parameters, boolean useJPWL) {
		/* setup the J2K codec */
		this.getJ2k().j2kSetupDecoder(jp2.getJ2k(), parameters, useJPWL);
		/* further JP2 initializations go here */
	}

	public OpenJpegImage jp2Decode(JP2 jp2, Cio cio, CodeStreamInfo codeStreamInfo, boolean useJPWL) {
		OpenJpegImage image = null;

		if (jp2 == null || cio == null) {
			return null;
		}

		/* JP2 header decoding */
		if (jp2ReadStruct(jp2, cio) == 0) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Failed to decode jp2 structure");
			return null;
		}

		/* J2K data decoding */
		image = this.getJ2k().j2kDecode(jp2.getJ2k(), cio, codeStreamInfo, useJPWL);
		if (image == null) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Failed to decode jp2 structure");
			return null;
		}

		/* set resolution box */
		image.setResolutionBox(jp2.getResolutionBox());

		/* Set Image Color Space */
		if (jp2.getEnumcs() == 16)
			image.setColorSpace(Jp2ColorSpace.CLRSPC_SRGB);
		else if (jp2.getEnumcs() == 17)
			image.setColorSpace(Jp2ColorSpace.CLRSPC_GRAY);
		else if (jp2.getEnumcs() == 18)
			image.setColorSpace(Jp2ColorSpace.CLRSPC_SYCC);
		else
			image.setColorSpace(Jp2ColorSpace.CLRSPC_UNKNOWN);

		return image;
	}

	/* ----------------------------------------------------------------------- */
	/* JP2 encoder interface */
	/* ----------------------------------------------------------------------- */
	public JP2 jp2CreateCompression(CodecContextInfo codecContextInfo) {
		JP2 jp2 = new JP2();
		jp2.setCodecContextInfo(codecContextInfo);
		/* create the J2K codec */
		jp2.setJ2k(this.getJ2k().j2kCreateCompression(codecContextInfo));
		if (jp2.getJ2k() == null) {
			jp2DestroyCompression(jp2);
			return null;
		}
		return jp2;
	}

	public void jp2DestroyCompression(JP2 jp2) {
		if (jp2 != null) {
			/* destroy the J2K codec */
			this.getJ2k().j2kDestroyCompression(jp2.getJ2k());

			if (jp2.getComps() != null) {
				jp2.setComps(null);
			}
			if (jp2.getCl() != null) {
				jp2.setCl(null);
			}
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776"})
	public void jp2SetupEncoder(JP2 jp2, CompressionParameters parameters, OpenJpegImage image, boolean useJPWL) {
		int i;
		int depth0, sign;

		if (jp2 == null || parameters == null || image == null)
			return;

		/* setup the J2K codec */
		/* ------------------- */

		/* Check if number of components respects standard */
		if (image.getNoOfComps() < 1 || image.getNoOfComps() > 16384) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, 
					"Invalid number of components specified while setting up JP2 encoder");
			return;
		}

		this.getJ2k().j2kSetupEncoder(jp2.getJ2k(), parameters, image, useJPWL);

		/* setup the JP2 codec */
		/* ------------------- */

		/* Profile box */

		jp2.setBrand(OpenJpegConstant.JP2_JP2); /* BR */
		jp2.setMinVersion(0); /* MinV */
		jp2.setNoOfCl(1);
		jp2.setCl(new long[(int) jp2.getNoOfCl()]);
		jp2.getCl()[0] = OpenJpegConstant.JP2_JP2; /* CL0 : JP2 */

		/* Image Header box */

		jp2.setNoOfComps(image.getNoOfComps()); /* NC */
		jp2.setComps(new JP2Component[(int) jp2.getNoOfComps()]);
		jp2.setHeight((long) image.getY1() - image.getY0()); /* HEIGHT */
		jp2.setWidth((long) image.getX1() - image.getX0()); /* WIDTH */
		/* BPC */
		depth0 = image.getComps()[0].getPrec() - 1;
		sign = image.getComps()[0].getSgnd();
		jp2.setBpc(depth0 + (long) (sign << 7));
		for (i = 1; i < image.getNoOfComps(); i++) {
			int depth = image.getComps()[i].getPrec() - 1;
			sign = image.getComps()[i].getSgnd();
			if (depth0 != depth)
				jp2.setBpc(255);
		}
		jp2.setC(7); /* C : Always 7 */
		jp2.setUnknownC(0); /* UnkC, colorspace specified in colr box */
		jp2.setIpr(0); /* IPR, no intellectual property */

		/* BitsPerComponent box */

		for (i = 0; i < image.getNoOfComps(); i++) {
			jp2.getComps()[i].setBpcc(image.getComps()[i].getPrec() - 1 + (image.getComps()[i].getSgnd() << 7));
		}

		/* Colour Specification box */

		if ((image.getNoOfComps() == 1 || image.getNoOfComps() == 3) && (jp2.getBpc() != 255)) {
			jp2.setMeth(1); /* METH: Enumerated colourspace */
		} else {
			jp2.setMeth(2); /* METH: Restricted ICC profile */
		}
		if (jp2.getMeth() == 1) {
			if (image.getColorSpace() == Jp2ColorSpace.CLRSPC_SRGB)
				jp2.setEnumcs(16); /* sRGB as defined by IEC 61966–2–1 */
			else if (image.getColorSpace() == Jp2ColorSpace.CLRSPC_GRAY)
				jp2.setEnumcs(17); /* greyscale */
			else if (image.getColorSpace() == Jp2ColorSpace.CLRSPC_SYCC)
				jp2.setEnumcs(18); /* YUV */
		} else {
			jp2.setEnumcs(0); /* PROFILE (??) */
		}
		jp2.setPrecedence(0); /* PRECEDENCE */
		jp2.setApprox(0); /* APPROX */
	}

	public int jp2Encode(JP2 jp2, Cio cio, OpenJpegImage image, CodeStreamInfo codeStreamInfo, boolean useJPWL) {
		/* JP2 encoding */

		/* JPEG 2000 Signature box */
		jp2WriteJP(cio);
		/* File Type box */
		jp2WriteFtyp(jp2, cio);
		/* JP2 Header box */
		jp2WriteJP2h(jp2, cio);

		/* J2K encoding */

		if (jp2WriteJP2c(jp2, cio, image, codeStreamInfo, useJPWL) == 0) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Failed to encode image");
			return 0;
		}

		return 1;
	}
}