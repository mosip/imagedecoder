package io.mosip.imagedecoder.openjpeg;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;

import java.text.MessageFormat;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.CodeStreamInfo;
import io.mosip.imagedecoder.model.openjpeg.CodecContextInfo;
import io.mosip.imagedecoder.model.openjpeg.CodingParameters;
import io.mosip.imagedecoder.model.openjpeg.CompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.DecoderFunctionInfo;
import io.mosip.imagedecoder.model.openjpeg.DecompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.J2K;
import io.mosip.imagedecoder.model.openjpeg.J2KProgressionOrder;
import io.mosip.imagedecoder.model.openjpeg.J2KT2Mode;
import io.mosip.imagedecoder.model.openjpeg.J2kStatus;
import io.mosip.imagedecoder.model.openjpeg.JP2CinemeaMode;
import io.mosip.imagedecoder.model.openjpeg.JPTMessageHeader;
import io.mosip.imagedecoder.model.openjpeg.LimitDecoding;
import io.mosip.imagedecoder.model.openjpeg.MarkerInfo;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponent;
import io.mosip.imagedecoder.model.openjpeg.PiIterator;
import io.mosip.imagedecoder.model.openjpeg.Poc;
import io.mosip.imagedecoder.model.openjpeg.ProgressionOrder;
import io.mosip.imagedecoder.model.openjpeg.StepSize;
import io.mosip.imagedecoder.model.openjpeg.Tcd;
import io.mosip.imagedecoder.model.openjpeg.Tcp;
import io.mosip.imagedecoder.model.openjpeg.TileComponentCodingParameters;
import io.mosip.imagedecoder.model.openjpeg.TileInfo;
import io.mosip.imagedecoder.model.openjpeg.TpInfo;
import io.mosip.imagedecoder.util.openjpeg.MathUtil;

public class J2KHelper {
	private Logger logger = ImageDecoderLogger.getLogger(J2KHelper.class);
	private static final String TAG_JPWL_GIVING_UP = "JPWL: giving up";

	private static final DecoderFunctionInfo[] J2K_DECODER_FUNCTION_INFO = {
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_SOC, J2kStatus.J2K_STATE_MHSOC.value(), "j2kReadSoc"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_SOT,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPHSOT.value(), "j2kReadSot"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_SOD, J2kStatus.J2K_STATE_TPH.value(), "j2kReadSod"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_EOC, J2kStatus.J2K_STATE_TPHSOT.value(), "j2kReadEoc"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_SIZ, J2kStatus.J2K_STATE_MHSIZ.value(), "j2kReadSize"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_COD,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(), "j2kReadCod"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_COC,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(), "j2kReadCoc"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_RGN,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(), "j2kReadRgn"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_QCD,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(), "j2kReadQcd"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_QCC,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(), "j2kReadQcc"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_POC,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(), "j2kReadPoc"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_TLM, J2kStatus.J2K_STATE_MH.value(), "j2kReadTlm"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_PLM, J2kStatus.J2K_STATE_MH.value(), "j2kReadPlm"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_PLT, J2kStatus.J2K_STATE_TPH.value(), "j2kReadPlt"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_PPM, J2kStatus.J2K_STATE_MH.value(), "j2kReadPpm"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_PPT, J2kStatus.J2K_STATE_TPH.value(), "j2kReadPpt"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_SOP, 0, "0"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_CRG, J2kStatus.J2K_STATE_MH.value(), "j2kReadCrg"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_COM,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(), "j2kReadCom"),

			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_EPC,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(), "j2kReadEpc"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_EPB,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(), "j2kReadEpb"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_ESD,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(), "j2kReadEsd"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_RED,
					J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(), "j2kReadRed"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_SEC, J2kStatus.J2K_STATE_MH.value(), "j2kReadSec"),
			new DecoderFunctionInfo(OpenJpegConstant.J2K_MS_INSEC, 0, "j2kReadInSec"),

			new DecoderFunctionInfo(0, J2kStatus.J2K_STATE_MH.value() | J2kStatus.J2K_STATE_TPH.value(),
					"j2kReadUnknown") };

	public J2KHelper() {
		super();
	}

	private char[] j2kGetProgressionOrderCode(ProgressionOrder progressionOrder) {
		J2KProgressionOrder order = null;
		for (int po = 0; po < OpenJpegConstant.J2KPROGRESSION_ORDER_INFO.length; po++) {
			order = OpenJpegConstant.J2KPROGRESSION_ORDER_INFO[po];
			if (order.getProgressionOrder() == progressionOrder) {
				break;
			}
		}
		if (order != null)
			return order.getProgressionName();
		return new char[] {};
	}

	private int j2kGetNoOfTilePart(CodingParameters codingParameters, int piNo, int tileNo) {
		char[] prog;
		int i;
		int tilePartNo = 1;
		int tilePartEnd = 0;
		Tcp tcp = codingParameters.getTcps()[tileNo];
		prog = j2kGetProgressionOrderCode(tcp.getProgressionOrder());

		if (codingParameters.getTilePartOn() == 1) {
			for (i = 0; i < 4; i++) {
				if (tilePartEnd != 1) {
					if (codingParameters.getTilePartFlag() == prog[i]) {
						tilePartEnd = 1;
						codingParameters.setTilePartPosition(i);
					}
					switch (prog[i]) {
					case 'C':
						tilePartNo = tilePartNo * tcp.getPocs()[piNo].getCompE();
						break;
					case 'R':
						tilePartNo = tilePartNo * tcp.getPocs()[piNo].getResE();
						break;
					case 'P':
						tilePartNo = tilePartNo * tcp.getPocs()[piNo].getPrcE();
						break;
					case 'L':
						tilePartNo = tilePartNo * tcp.getPocs()[piNo].getLayE();
						break;
					default:
						break;
					}
				}
			}
		} else {
			tilePartNo = 1;
		}
		return tilePartNo;
	}

	/** mem allocation for TLM marker */
	@SuppressWarnings({ "java:S1659", "unused" })
	private int j2kCalculateTilePart(CodingParameters codingParameters, int imageNoOfComp, OpenJpegImage image,
			J2K j2kInfo) {
		int piNo, tileNo, totalNoOfTilePart = 0;

		j2kInfo.setCurTotalNoOfTilePart(new int[codingParameters.getTileWidth() * codingParameters.getTileHeight()]);
		for (tileNo = 0; tileNo < codingParameters.getTileWidth() * codingParameters.getTileHeight(); tileNo++) {
			int curTotalNoOfTilePart = 0;
			Tcp tcp = codingParameters.getTcps()[tileNo];
			for (piNo = 0; piNo <= tcp.getNoOfPocs(); piNo++) {
				int tilePartNo = 0;
				PiIterator[] arrPI = PiHelper.getInstance().piInitEncode(image, codingParameters, tileNo,
						J2KT2Mode.FINAL_PASS);
				if (arrPI == null) {
					return -1;
				}
				tilePartNo = j2kGetNoOfTilePart(codingParameters, piNo, tileNo);
				totalNoOfTilePart = totalNoOfTilePart + tilePartNo;
				curTotalNoOfTilePart = curTotalNoOfTilePart + tilePartNo;
				PiHelper.getInstance().piDestroy(arrPI, codingParameters, tileNo);
			}
			j2kInfo.getCurTotalNoOfTilePart()[tileNo] = curTotalNoOfTilePart;
			/* INDEX >> */
			if (j2kInfo.getCodeStreamInfo() != null) {
				j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].setNoOfTileParts(curTotalNoOfTilePart);
				j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].setTp(new TpInfo[curTotalNoOfTilePart]);
			}
			/* << INDEX */
		}
		return totalNoOfTilePart;
	}

	public void j2kWriteSoc(J2K j2kInfo, boolean useJPWL) {
		Cio cio = j2kInfo.getCio();
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_SOC, 2);

		/* UniPG>> */
		if (useJPWL) {
			/* update markers struct */
			j2kAddMarker(j2kInfo.getCodeStreamInfo(), OpenJpegConstant.J2K_MS_SOC,
					CioHelper.getInstance().cioTell(cio) - 2, 2);
		}
		/* <<UniPG */
	}

	@SuppressWarnings({ "java:S1186", "java:S1659" })
	private void j2kAddMarker(CodeStreamInfo codeStreamInfo, int j2kMsSoc, int i, int j) {
	}

	private void j2kReadSoc(J2K j2kInfo) {
		j2kInfo.setState(J2kStatus.J2K_STATE_MHSIZ.value());
		/* Index */
		if (j2kInfo.getCodeStreamInfo() != null) {
			j2kInfo.getCodeStreamInfo().setMainHeadStart(CioHelper.getInstance().cioTell(j2kInfo.getCio()) - 2);
			j2kInfo.getCodeStreamInfo().setCodeStreamSize(CioHelper.getInstance().cioNoOfBytesLeft(j2kInfo.getCio()) + 2
					- j2kInfo.getCodeStreamInfo().getMainHeadStart());
		}
	}

	@SuppressWarnings({ "java:S1659" })
	private void j2kWriteSize(J2K j2kInfo) {
		int i;
		int lengthPosition, length;

		Cio cio = j2kInfo.getCio();
		OpenJpegImage image = j2kInfo.getImage();
		CodingParameters codingParameters = j2kInfo.getCodingParameters();

		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_SIZ, 2); /* SIZ */
		lengthPosition = CioHelper.getInstance().cioTell(cio);
		CioHelper.getInstance().cioSkip(cio, 2);
		CioHelper.getInstance().cioWrite(cio, codingParameters.getRsizCap().value(), 2); /* Rsiz (capabilities) */
		CioHelper.getInstance().cioWrite(cio, image.getX1(), 4); /* Xsiz */
		CioHelper.getInstance().cioWrite(cio, image.getY1(), 4); /* Ysiz */
		CioHelper.getInstance().cioWrite(cio, image.getX0(), 4); /* X0siz */
		CioHelper.getInstance().cioWrite(cio, image.getY0(), 4); /* Y0siz */
		CioHelper.getInstance().cioWrite(cio, codingParameters.getTileDX(), 4); /* XTsiz */
		CioHelper.getInstance().cioWrite(cio, codingParameters.getTileDY(), 4); /* YTsiz */
		CioHelper.getInstance().cioWrite(cio, codingParameters.getTileX0(), 4); /* XT0siz */
		CioHelper.getInstance().cioWrite(cio, codingParameters.getTileY0(), 4); /* YT0siz */
		CioHelper.getInstance().cioWrite(cio, image.getNoOfComps(), 2); /* Csiz */
		for (i = 0; i < image.getNoOfComps(); i++) {
			CioHelper.getInstance().cioWrite(cio,
					image.getComps()[i].getPrec() - 1 + (long) (image.getComps()[i].getSgnd() << 7), 1); /* Ssiz_i */
			CioHelper.getInstance().cioWrite(cio, image.getComps()[i].getDX(), 1); /* XRsiz_i */
			CioHelper.getInstance().cioWrite(cio, image.getComps()[i].getDY(), 1); /* YRsiz_i */
		}
		length = CioHelper.getInstance().cioTell(cio) - lengthPosition;
		CioHelper.getInstance().cioSeek(cio, lengthPosition);
		CioHelper.getInstance().cioWrite(cio, length, 2); /* Lsiz */
		CioHelper.getInstance().cioSeek(cio, lengthPosition + length);
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776", "java:S6541", "unused" })
	private void j2kReadSize(J2K j2kInfo, boolean useJPWL) {
		int length, i;

		Cio cio = j2kInfo.getCio();
		OpenJpegImage image = j2kInfo.getImage();
		CodingParameters codingParameters = j2kInfo.getCodingParameters();

		length = (int) CioHelper.getInstance().cioRead(cio, 2); /* Lsiz */
		CioHelper.getInstance().cioRead(cio, 2); /* Rsiz (capabilities) */
		image.setX1((int) CioHelper.getInstance().cioRead(cio, 4)); /* Xsiz */
		image.setY1((int) CioHelper.getInstance().cioRead(cio, 4)); /* Ysiz */
		image.setX0((int) CioHelper.getInstance().cioRead(cio, 4)); /* X0siz */
		image.setY0((int) CioHelper.getInstance().cioRead(cio, 4)); /* Y0siz */
		codingParameters.setTileDX((int) CioHelper.getInstance().cioRead(cio, 4)); /* XTsiz */
		codingParameters.setTileDY((int) CioHelper.getInstance().cioRead(cio, 4)); /* YTsiz */
		codingParameters.setTileX0((int) CioHelper.getInstance().cioRead(cio, 4)); /* XT0siz */
		codingParameters.setTileY0((int) CioHelper.getInstance().cioRead(cio, 4)); /* YT0siz */

		if ((image.getX0() < 0) || (image.getX1() < 0) || (image.getY0() < 0) || (image.getY1() < 0)) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("invalid image size x0:{0}, x1:{1}, y0:{2}, y1:{3}",
					image.getX0(), image.getX1(), image.getY0(), image.getY1()));
			return;
		}

		image.setNoOfComps((int) CioHelper.getInstance().cioRead(cio, 2)); /* Csiz */

		if (useJPWL && j2kInfo.getCodingParameters().getCorrect() != 0) {
			/*
			 * if JPWL is on, we check whether TX errors have damaged too much the SIZ
			 * parameters
			 */
			if ((image.getX1() * image.getY1()) == 0) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("JPWL: bad image size {0} x {1}", image.getX1(),
						image.getY1()));
				if (!OpenJpegConstant.JPWL_ASSUME || OpenJpegConstant.JPWL_ASSUME) {
					logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, TAG_JPWL_GIVING_UP);
					return;
				}
			}
			if (image.getNoOfComps() != ((length - 38) / 3)) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format("JPWL: Csiz is {0} => space in SIZ only for {1} comps.!!!", image.getNoOfComps(),
						((length - 38) / 3)));
				if (!OpenJpegConstant.JPWL_ASSUME) {
					logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, TAG_JPWL_GIVING_UP);
					return;
				}
				/* we try to correct */
				logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "trying to adjust this");
				if (image.getNoOfComps() < ((length - 38) / 3)) {
					length = 38 + 3 * image.getNoOfComps();
					logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("setting Lsiz to {0} => HYPOTHESIS!!!", length));
				} else {
					image.setNoOfComps(((length - 38) / 3));
					logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("setting Csiz to {0} => HYPOTHESIS!!!",
							image.getNoOfComps()));
				}
			}

			/* update components number in the jpwlExpComps filed */
			codingParameters.setExpComps(image.getNoOfComps());
		}

		image.setComps(new OpenJpegImageComponent[image.getNoOfComps()]);
		for (i = 0; i < image.getNoOfComps(); i++) {
			image.getComps()[i] = new OpenJpegImageComponent();
			int tmpSize, width, height;
			tmpSize = (int) CioHelper.getInstance().cioRead(cio, 1); /* Ssiz_i */
			image.getComps()[i].setPrec((tmpSize & 0x7f) + 1);
			image.getComps()[i].setSgnd(tmpSize >> 7);
			image.getComps()[i].setDX((int) CioHelper.getInstance().cioRead(cio, 1)); /* XRsiz_i */
			image.getComps()[i].setDY((int) CioHelper.getInstance().cioRead(cio, 1)); /* YRsiz_i */

			/*
			 * if JPWL is on, we check whether TX errors have damaged too much the SIZ
			 * parameters, again
			 */
			if (useJPWL && j2kInfo.getCodingParameters().getCorrect() != 0
					&& ((image.getComps()[i].getDX() * image.getComps()[i].getDY()) == 0)) {
				logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("JPWL: bad XRsiz_{0}/YRsiz_{1} {2} x {3}", i, i,
						image.getComps()[i].getDX(), image.getComps()[i].getDY()));

				if (!OpenJpegConstant.JPWL_ASSUME) {
					logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, TAG_JPWL_GIVING_UP);
					return;
				}
				/* we try to correct */
				logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "trying to adjust them");
				if (image.getComps()[i].getDX() == 0) {
					image.getComps()[i].setDX(1);
					logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format(
							"trying to adjust them - setting XRsiz_{0} to {1} => HYPOTHESIS!!!", i,
							image.getComps()[i].getDX()));
				}
				if (image.getComps()[i].getDY() == 0) {
					image.getComps()[i].setDY(1);
					logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
							"trying to adjust them - setting YRsiz_{0} to {1} => HYPOTHESIS!!!", i,
							image.getComps()[i].getDY()));
				}
			}

			width = MathUtil.getInstance().intCeilDiv(image.getX1() - image.getX0(), image.getComps()[i].getDX());
			height = MathUtil.getInstance().intCeilDiv(image.getY1() - image.getY0(), image.getComps()[i].getDY());

			image.getComps()[i].setResNoDecoded(0); /* number of resolution decoded */
			image.getComps()[i].setFactor(codingParameters.getReduce()); /* reducing factor per component */
		}

		codingParameters.setTileWidth(MathUtil.getInstance().intCeilDiv(image.getX1() - codingParameters.getTileX0(),
				codingParameters.getTileDX()));
		codingParameters.setTileHeight(MathUtil.getInstance().intCeilDiv(image.getY1() - codingParameters.getTileY0(),
				codingParameters.getTileDY()));

		/*
		 * if JPWL is on, we check whether TX errors have damaged too much the SIZ
		 * parameters
		 */
		if (useJPWL && j2kInfo.getCodingParameters().getCorrect() != 0
				&& ((codingParameters.getTileWidth() < 1) || (codingParameters.getTileHeight() < 1)
						|| (codingParameters.getTileWidth() > codingParameters.getMaxTiles())
						|| (codingParameters.getTileHeight() > codingParameters.getMaxTiles()))) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("JPWL: bad number of tiles {0} x {1}",
					codingParameters.getTileWidth(), codingParameters.getTileHeight()));
			if (!OpenJpegConstant.JPWL_ASSUME) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, TAG_JPWL_GIVING_UP);
				return;
			}
			/* we try to correct */
			if (codingParameters.getTileWidth() < 1) {
				codingParameters.setTileWidth(1);
				logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
						"trying to adjust them - setting {0} tiles in x => HYPOTHESIS!!!",
						codingParameters.getTileWidth()));
			}
			if (codingParameters.getTileWidth() > codingParameters.getMaxTiles()) {
				codingParameters.setTileWidth(1);
				logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
						"trying to adjust them - too large x, increase expectance of {0} - setting {1} tiles in x => HYPOTHESIS!!!",
						codingParameters.getMaxTiles(), codingParameters.getTileWidth()));
			}
			if (codingParameters.getTileHeight() < 1) {
				codingParameters.setTileHeight(1);
				logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
						"trying to adjust them - setting {0} tiles in y => HYPOTHESIS!!!",
						codingParameters.getTileHeight()));
			}
			if (codingParameters.getTileHeight() > codingParameters.getMaxTiles()) {
				codingParameters.setTileHeight(1);
				logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
						"trying to adjust them - too large y, increase expectance of {0} to continue - setting {1} tiles in y => HYPOTHESIS!!!",
						codingParameters.getMaxTiles(), codingParameters.getTileHeight()));
			}
		}

		codingParameters.setTcps(new Tcp[codingParameters.getTileWidth() * codingParameters.getTileHeight()]);
		codingParameters.setTileNo(new int[codingParameters.getTileWidth() * codingParameters.getTileHeight()]);
		codingParameters.setTileNoSize(0);

		if (useJPWL && (j2kInfo.getCodingParameters().getCorrect() != 0
				&& (codingParameters.getTcps() == null || codingParameters.getTcps().length == 0))) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "JPWL: could not alloc tcps field of codingParameters");
			if (!OpenJpegConstant.JPWL_ASSUME || OpenJpegConstant.JPWL_ASSUME) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, TAG_JPWL_GIVING_UP);
				return;
			}
		}

		for (i = 0; i < codingParameters.getTileWidth() * codingParameters.getTileHeight(); i++) {
			codingParameters.getTcps()[i] = new Tcp();
			codingParameters.getTcps()[i].setIsPoc(0);
			codingParameters.getTcps()[i].setNoOfPocs(0);
			codingParameters.getTcps()[i].setFirst(1);
		}

		/* Initialization for PPM marker */
		codingParameters.setPpm(0);
		codingParameters.setPpmData(null);
		codingParameters.setPpmDataFirst(null);
		codingParameters.setPpmPrevious(0);
		codingParameters.setPpmStore(0);

		j2kInfo.getDefaultTcp().setTccps(new TileComponentCodingParameters[image.getNoOfComps()]);
		for (i = 0; i < image.getNoOfComps(); i++) {
			j2kInfo.getDefaultTcp().getTccps()[i] = new TileComponentCodingParameters();
		}

		for (i = 0; i < codingParameters.getTileWidth() * codingParameters.getTileHeight(); i++) {
			codingParameters.getTcps()[i].setTccps(new TileComponentCodingParameters[image.getNoOfComps()]);
			for (int j = 0; j < image.getNoOfComps(); j++) {
				codingParameters.getTcps()[i].getTccps()[j] = new TileComponentCodingParameters();
			}
		}
		j2kInfo.setTileData(new byte[codingParameters.getTileWidth() * codingParameters.getTileHeight()][]);
		j2kInfo.setTileLength(new int[codingParameters.getTileWidth() * codingParameters.getTileHeight()]);
		j2kInfo.setState(J2kStatus.J2K_STATE_MH.value());

		/* Index */
		if (j2kInfo.getCodeStreamInfo() != null) {
			CodeStreamInfo codeStreamInfo = j2kInfo.getCodeStreamInfo();
			codeStreamInfo.setImageWidth(image.getX1() - image.getX0());
			codeStreamInfo.setImageHeight(image.getY1() - image.getY0());
			codeStreamInfo.setNoOfComps(image.getNoOfComps());
			codeStreamInfo.setTileWidth(codingParameters.getTileWidth());
			codeStreamInfo.setTileHeight(codingParameters.getTileHeight());
			codeStreamInfo.setTileX(codingParameters.getTileDX());
			codeStreamInfo.setTileY(codingParameters.getTileDY());
			codeStreamInfo.setTileOX(codingParameters.getTileX0());
			codeStreamInfo.setTileOY(codingParameters.getTileY0());
			codeStreamInfo
					.setTileInfo(new TileInfo[codingParameters.getTileWidth() * codingParameters.getTileHeight()]);
		}
	}

	private void j2kWriteCom(J2K j2kInfo) {
		int i;
		int lengthPosition;
		int length;

		if (j2kInfo.getCodingParameters().getComment() != null) {
			Cio cio = j2kInfo.getCio();
			char[] comment = j2kInfo.getCodingParameters().getComment();

			CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_COM, 2);
			lengthPosition = CioHelper.getInstance().cioTell(cio);
			CioHelper.getInstance().cioSkip(cio, 2);
			CioHelper.getInstance().cioWrite(cio, 1, 2); /* General use (IS 8859-15:1999 (Latin) values) */
			for (i = 0; i < comment.length; i++) {
				CioHelper.getInstance().cioWrite(cio, comment[i], 1);
			}
			length = CioHelper.getInstance().cioTell(cio) - lengthPosition;
			CioHelper.getInstance().cioSeek(cio, lengthPosition);
			CioHelper.getInstance().cioWrite(cio, length, 2);
			CioHelper.getInstance().cioSeek(cio, lengthPosition + length);
		}
	}

	private void j2kReadCom(J2K j2kInfo) {
		int length;

		Cio cio = j2kInfo.getCio();

		length = (int) CioHelper.getInstance().cioRead(cio, 2);
		CioHelper.getInstance().cioSkip(cio, length - 2);
	}

	public void j2kWriteCox(J2K j2kInfo, int compNo) {
		int i;

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Tcp tcp = codingParameters.getTcps()[j2kInfo.getCurTileNo()];
		TileComponentCodingParameters tccp = tcp.getTccps()[compNo];
		Cio cio = j2kInfo.getCio();

		CioHelper.getInstance().cioWrite(cio, (long) tccp.getNoOfResolutions() - 1, 1); /* SPcox (D) */
		CioHelper.getInstance().cioWrite(cio, (long) tccp.getCodeBlockWidth() - 2, 1); /* SPcox (E) */
		CioHelper.getInstance().cioWrite(cio, (long) tccp.getCodeBlockHeight() - 2, 1); /* SPcox (F) */
		CioHelper.getInstance().cioWrite(cio, tccp.getCodeBlockStyle(), 1); /* SPcox (G) */
		CioHelper.getInstance().cioWrite(cio, tccp.getQmfbid(), 1); /* SPcox (H) */

		if ((tccp.getCodingStyle() & OpenJpegConstant.J2K_CCP_CSTY_PRT) != 0) {
			for (i = 0; i < tccp.getNoOfResolutions(); i++) {
				CioHelper.getInstance().cioWrite(cio,
						tccp.getPrecinctWidth()[i] + (long) (tccp.getPrecinctHeight()[i] << 4), 1); /* SPcox (I_i) */
			}
		}
	}

	private void j2kReadCox(J2K j2kInfo, int compNo) {
		int i;

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Tcp tcp = j2kInfo.getState() == J2kStatus.J2K_STATE_TPH.value()
				? codingParameters.getTcps()[j2kInfo.getCurTileNo()]
				: j2kInfo.getDefaultTcp();
		TileComponentCodingParameters tccp = tcp.getTccps()[compNo];
		Cio cio = j2kInfo.getCio();

		tccp.setNoOfResolutions((int) (CioHelper.getInstance().cioRead(cio, 1) + 1)); /* SPcox (D) */

		// If user wants to remove more resolutions than the codestream contains, return
		// error
		if (codingParameters.getReduce() >= tccp.getNoOfResolutions()) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
					"Error decoding component {0}. The number of resolutions to remove is higher than the number  of resolutions of this component Modify the cp_reduce parameter",
					compNo));
			j2kInfo.setState(j2kInfo.getState() | J2kStatus.J2K_STATE_ERR.value());
		}

		tccp.setCodeBlockWidth((int) (CioHelper.getInstance().cioRead(cio, 1) + 2)); /* SPcox (E) */
		tccp.setCodeBlockHeight((int) (CioHelper.getInstance().cioRead(cio, 1) + 2)); /* SPcox (F) */
		tccp.setCodeBlockStyle((int) CioHelper.getInstance().cioRead(cio, 1)); /* SPcox (G) */
		tccp.setQmfbid((int) CioHelper.getInstance().cioRead(cio, 1)); /* SPcox (H) */
		if ((tccp.getCodingStyle() & OpenJpegConstant.J2K_CP_CSTY_PRT) != 0) {
			for (i = 0; i < tccp.getNoOfResolutions(); i++) {
				int spCox = (int) CioHelper.getInstance().cioRead(cio, 1); /* SPcox (I_i) */
				tccp.getPrecinctWidth()[i] = spCox & 0xf;
				tccp.getPrecinctHeight()[i] = spCox >> 4;
			}
		}

		/* INDEX >> */
		if (j2kInfo.getCodeStreamInfo() != null && compNo == 0) {
			for (i = 0; i < tccp.getNoOfResolutions(); i++) {
				if ((tccp.getCodingStyle() & OpenJpegConstant.J2K_CP_CSTY_PRT) != 0) {
					j2kInfo.getCodeStreamInfo().getTileInfo()[j2kInfo.getCurTileNo()].getPDX()[i] = tccp
							.getPrecinctWidth()[i];
					j2kInfo.getCodeStreamInfo().getTileInfo()[j2kInfo.getCurTileNo()].getPDY()[i] = tccp
							.getPrecinctHeight()[i];
				} else {
					j2kInfo.getCodeStreamInfo().getTileInfo()[j2kInfo.getCurTileNo()].getPDX()[i] = 15;
					j2kInfo.getCodeStreamInfo().getTileInfo()[j2kInfo.getCurTileNo()].getPDY()[i] = 15;
				}
			}
		}
		/* << INDEX */
	}

	@SuppressWarnings({ "java:S1659", "java:S1854" })
	private void j2kWriteCod(J2K j2kInfo) {
		CodingParameters codingParameters = null;
		Tcp tcp = null;
		int lengthPosition, length;

		Cio cio = j2kInfo.getCio();

		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_COD, 2); /* COD */

		lengthPosition = CioHelper.getInstance().cioTell(cio);
		CioHelper.getInstance().cioSkip(cio, 2);

		codingParameters = j2kInfo.getCodingParameters();
		tcp = codingParameters.getTcps()[j2kInfo.getCurTileNo()];

		CioHelper.getInstance().cioWrite(cio, tcp.getCodingStyle(), 1); /* Scod */
		CioHelper.getInstance().cioWrite(cio, tcp.getProgressionOrder().value(), 1); /* SGcod (A) */
		CioHelper.getInstance().cioWrite(cio, tcp.getNoOfLayers(), 2); /* SGcod (B) */
		CioHelper.getInstance().cioWrite(cio, tcp.getMct(), 1); /* SGcod (C) */

		j2kWriteCox(j2kInfo, 0);
		length = CioHelper.getInstance().cioTell(cio) - lengthPosition;
		CioHelper.getInstance().cioSeek(cio, lengthPosition);
		CioHelper.getInstance().cioWrite(cio, length, 2); /* Lcod */
		CioHelper.getInstance().cioSeek(cio, lengthPosition + length);
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776", "unused"})
	private void j2kReadCod(J2K j2kInfo) {
		int length, i, pos;

		Cio cio = j2kInfo.getCio();
		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Tcp tcp = j2kInfo.getState() == J2kStatus.J2K_STATE_TPH.value()
				? codingParameters.getTcps()[j2kInfo.getCurTileNo()]
				: j2kInfo.getDefaultTcp();
		OpenJpegImage image = j2kInfo.getImage();

		length = (int) CioHelper.getInstance().cioRead(cio, 2); /* Lcod */
		tcp.setCodingStyle((int) CioHelper.getInstance().cioRead(cio, 1)); /* Scod */
		tcp.setProgressionOrder(
				ProgressionOrder.fromValue((int) CioHelper.getInstance().cioRead(cio, 1))); /* SGcod (A) */
		tcp.setNoOfLayers((int) CioHelper.getInstance().cioRead(cio, 2)); /* SGcod (B) */
		tcp.setMct((int) CioHelper.getInstance().cioRead(cio, 1)); /* SGcod (C) */

		if (tcp.getMct() != 0 && j2kInfo.getImage().getNoOfComps() < 3) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("MCT {0} with too few components {1}", tcp.getMct(),
					j2kInfo.getImage().getNoOfComps()));
			return;
		}

		pos = CioHelper.getInstance().cioTell(cio);
		for (i = 0; i < image.getNoOfComps(); i++) {
			tcp.getTccps()[i].setCodingStyle(tcp.getCodingStyle() & OpenJpegConstant.J2K_CP_CSTY_PRT);

			CioHelper.getInstance().cioSeek(cio, pos);
			j2kReadCox(j2kInfo, i);
		}

		/* Index */
		if (j2kInfo.getCodeStreamInfo() != null) {
			CodeStreamInfo codeStreamInfo = j2kInfo.getCodeStreamInfo();
			codeStreamInfo.setProgOrder(tcp.getProgressionOrder());
			codeStreamInfo.setNoOfLayers(tcp.getNoOfLayers());
			codeStreamInfo.setNoOfDecompositionComps(new int[image.getNoOfComps()]);
			for (i = 0; i < image.getNoOfComps(); i++) {
				codeStreamInfo.getNoOfDecompositionComps()[i] = tcp.getTccps()[i].getNoOfResolutions() - 1;
			}
		}
	}

	private void j2kWriteCoc(J2K j2kInfo, int compNo) {
		int lengthPosition;
		int length;

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Tcp tcp = codingParameters.getTcps()[j2kInfo.getCurTileNo()];
		OpenJpegImage image = j2kInfo.getImage();
		Cio cio = j2kInfo.getCio();

		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_COC, 2); /* COC */
		lengthPosition = CioHelper.getInstance().cioTell(cio);
		CioHelper.getInstance().cioSkip(cio, 2);
		CioHelper.getInstance().cioWrite(cio, compNo, image.getNoOfComps() <= 256 ? 1 : 2); /* Ccoc */
		CioHelper.getInstance().cioWrite(cio, tcp.getTccps()[compNo].getCodingStyle(), 1); /* Scoc */
		j2kWriteCox(j2kInfo, compNo);
		length = CioHelper.getInstance().cioTell(cio) - lengthPosition;
		CioHelper.getInstance().cioSeek(cio, lengthPosition);
		CioHelper.getInstance().cioWrite(cio, length, 2); /* Lcoc */
		CioHelper.getInstance().cioSeek(cio, lengthPosition + length);
	}

	@SuppressWarnings({ "java:S1659", "unused" })
	private void j2kReadCoc(J2K j2kInfo) {
		int length, compNo;

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Tcp tcp = j2kInfo.getState() == J2kStatus.J2K_STATE_TPH.value()
				? codingParameters.getTcps()[j2kInfo.getCurTileNo()]
				: j2kInfo.getDefaultTcp();
		OpenJpegImage image = j2kInfo.getImage();
		Cio cio = j2kInfo.getCio();

		length = (int) CioHelper.getInstance().cioRead(cio, 2); /* Lcoc */
		compNo = (int) CioHelper.getInstance().cioRead(cio, image.getNoOfComps() <= 256 ? 1 : 2); /* Ccoc */
		tcp.getTccps()[compNo].setCodingStyle((int) CioHelper.getInstance().cioRead(cio, 1)); /* Scoc */
		j2kReadCox(j2kInfo, compNo);
	}

	@SuppressWarnings({ "java:S1659", "unused" })
	private void j2kWriteQcx(J2K j2kInfo, int compNo) {
		int bandNo, noOfBands;
		int expn, mant;

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Tcp tcp = codingParameters.getTcps()[j2kInfo.getCurTileNo()];
		TileComponentCodingParameters tccp = tcp.getTccps()[compNo];
		Cio cio = j2kInfo.getCio();

		CioHelper.getInstance().cioWrite(cio, tccp.getQuantisationStyle() + (long) (tccp.getNoOfGaurdBits() << 5),
				1); /* Sqcx */
		noOfBands = tccp.getQuantisationStyle() == OpenJpegConstant.J2K_CCP_QNTSTY_SIQNT ? 1
				: tccp.getNoOfResolutions() * 3 - 2;

		for (bandNo = 0; bandNo < noOfBands; bandNo++) {
			expn = tccp.getStepsizes()[bandNo].getExpn();
			mant = tccp.getStepsizes()[bandNo].getMant();

			if (tccp.getQuantisationStyle() == OpenJpegConstant.J2K_CCP_QNTSTY_NOQNT) {
				CioHelper.getInstance().cioWrite(cio, expn << 3, 1); /* SPqcx_i */
			} else {
				CioHelper.getInstance().cioWrite(cio, (long) (expn << 11) + mant, 2); /* SPqcx_i */
			}
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S3776", "unused" })
	private void j2kReadQcx(J2K j2kInfo, int compNo, int length, boolean useJPWL) {
		int tmp;
		int bandNo, noOfBands;

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		boolean isCurtilTcp = j2kInfo.getState() == J2kStatus.J2K_STATE_TPH.value();
		Tcp tcp = isCurtilTcp ? codingParameters.getTcps()[j2kInfo.getCurTileNo()] : j2kInfo.getDefaultTcp();
		TileComponentCodingParameters tccp = tcp.getTccps()[compNo];
		Cio cio = j2kInfo.getCio();

		tmp = (int) CioHelper.getInstance().cioRead(cio, 1); /* Sqcx */
		tccp.setQuantisationStyle(tmp & 0x1f);
		tccp.setNoOfGaurdBits(tmp >> 5);

		if (tccp.getQuantisationStyle() == OpenJpegConstant.J2K_CCP_QNTSTY_SIQNT)
			noOfBands = 1;
		else if (tccp.getQuantisationStyle() == OpenJpegConstant.J2K_CCP_QNTSTY_NOQNT)
			noOfBands = length - 1;
		else
			noOfBands = (length - 1) / 2;

		/* if JPWL is on, we check whether there are too many subbands */
		if (useJPWL && (j2kInfo.getCodingParameters().getCorrect() != 0 && (noOfBands < 0)
				|| (noOfBands >= OpenJpegConstant.J2K_MAXBANDS))) {
			logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("JPWL: bad number of subbands in Sqcx {0}", noOfBands));
			if (!OpenJpegConstant.JPWL_ASSUME) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("JPWL: giving up  {0}", noOfBands));
				return;
			}
			/* we try to correct */
			noOfBands = 1;
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
					"trying to adjust them- setting number of bands to {0} => HYPOTHESIS!!!", noOfBands));
		}

		for (bandNo = 0; bandNo < noOfBands; bandNo++) {
			tccp.getStepsizes()[bandNo] = new StepSize();
			int expn, mant;
			if (tccp.getQuantisationStyle() == OpenJpegConstant.J2K_CCP_QNTSTY_NOQNT) {
				expn = (int) (CioHelper.getInstance().cioRead(cio, 1) >> 3); /* SPqcx_i */
				mant = 0;
			} else {
				tmp = (int) CioHelper.getInstance().cioRead(cio, 2); /* SPqcx_i */
				expn = tmp >> 11;
				mant = tmp & 0x7ff;
			}
			tccp.getStepsizes()[bandNo].setExpn(expn);
			tccp.getStepsizes()[bandNo].setMant(mant);
		}
		for (bandNo = noOfBands; bandNo < OpenJpegConstant.J2K_MAXBANDS; bandNo++)
			tccp.getStepsizes()[bandNo] = new StepSize();

		/* Add Antonin : if scalar_derived -> compute other stepsizes */
		if (tccp.getQuantisationStyle() == OpenJpegConstant.J2K_CCP_QNTSTY_SIQNT) {
			for (bandNo = 1; bandNo < OpenJpegConstant.J2K_MAXBANDS; bandNo++) {
				tccp.getStepsizes()[bandNo].setExpn(((tccp.getStepsizes()[0].getExpn()) - ((bandNo - 1) / 3) > 0)
						? (tccp.getStepsizes()[0].getExpn()) - ((bandNo - 1) / 3)
						: 0);
				tccp.getStepsizes()[bandNo].setMant(tccp.getStepsizes()[0].getMant());
			}
		}
	}

	@SuppressWarnings({ "java:S1659", "unused" })
	private void j2kWriteQcd(J2K j2kInfo) {
		int lengthPosition, length;

		Cio cio = j2kInfo.getCio();

		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_QCD, 2); /* QCD */
		lengthPosition = CioHelper.getInstance().cioTell(cio);
		CioHelper.getInstance().cioSkip(cio, 2);
		j2kWriteQcx(j2kInfo, 0);
		length = CioHelper.getInstance().cioTell(cio) - lengthPosition;
		CioHelper.getInstance().cioSeek(cio, lengthPosition);
		CioHelper.getInstance().cioWrite(cio, length, 2); /* Lqcd */
		CioHelper.getInstance().cioSeek(cio, lengthPosition + length);
	}

	@SuppressWarnings({ "java:S1659", "unused" })
	private void j2kReadQcd(J2K j2kInfo, boolean useJPWL) {
		int length, i, pos;

		Cio cio = j2kInfo.getCio();
		OpenJpegImage image = j2kInfo.getImage();

		length = (int) CioHelper.getInstance().cioRead(cio, 2); /* Lqcd */
		pos = CioHelper.getInstance().cioTell(cio);
		for (i = 0; i < image.getNoOfComps(); i++) {
			CioHelper.getInstance().cioSeek(cio, pos);
			j2kReadQcx(j2kInfo, i, length - 2, useJPWL);
		}
	}

	@SuppressWarnings({ "java:S1659", "unused" })
	private void j2kWriteQcc(J2K j2kInfo, int compNo) {
		int lengthPosition, length;

		Cio cio = j2kInfo.getCio();

		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_QCC, 2); /* QCC */
		lengthPosition = CioHelper.getInstance().cioTell(cio);
		CioHelper.getInstance().cioSkip(cio, 2);
		CioHelper.getInstance().cioWrite(cio, compNo, j2kInfo.getImage().getNoOfComps() <= 256 ? 1 : 2); /* Cqcc */
		j2kWriteQcx(j2kInfo, compNo);
		length = CioHelper.getInstance().cioTell(cio) - lengthPosition;
		CioHelper.getInstance().cioSeek(cio, lengthPosition);
		CioHelper.getInstance().cioWrite(cio, length, 2); /* Lqcc */
		CioHelper.getInstance().cioSeek(cio, lengthPosition + length);
	}

	@SuppressWarnings({ "java:S1659", "unused" })
	private void j2kReadQcc(J2K j2kInfo, boolean useJPWL) {
		int length, compNo;
		int noOfComp = j2kInfo.getImage().getNoOfComps();
		Cio cio = j2kInfo.getCio();

		length = (int) CioHelper.getInstance().cioRead(cio, 2); /* Lqcc */
		compNo = (int) CioHelper.getInstance().cioRead(cio, noOfComp <= 256 ? 1 : 2); /* Cqcc */

		if (useJPWL && j2kInfo.getCodingParameters().getCorrect() != 0) {
			int backupCompNo = 0;

			/* compNo is negative or larger than the number of components!!! */
			if ((compNo < 0) || (compNo >= noOfComp)) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
						"JPWL: bad component number in QCC {0} out of a maximum of {1}", compNo, noOfComp));
				if (!OpenJpegConstant.JPWL_ASSUME) {
					logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, TAG_JPWL_GIVING_UP);
					return;
				}
				/* we try to correct */
				compNo = backupCompNo % noOfComp;
				logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("trying to adjust this - setting component number to {0}",
						compNo));
			}

			/* keep your private count of tiles */
			backupCompNo++;
		}

		j2kReadQcx(j2kInfo, compNo, length - 2 - (noOfComp <= 256 ? 1 : 2), useJPWL);
	}

	@SuppressWarnings({ "java:S1659", "unused" })
	private void j2kWritePoc(J2K j2kInfo) {
		int length, noOfPocChanges, i;

		int noOfComps = j2kInfo.getImage().getNoOfComps();

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Tcp tcp = codingParameters.getTcps()[j2kInfo.getCurTileNo()];
		TileComponentCodingParameters tccp = tcp.getTccps()[0];
		Cio cio = j2kInfo.getCio();

		noOfPocChanges = 1 + tcp.getNoOfPocs();
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_POC, 2); /* POC */
		length = 2 + (5 + 2 * (noOfComps <= 256 ? 1 : 2)) * noOfPocChanges;
		CioHelper.getInstance().cioWrite(cio, length, 2); /* Lpoc */
		for (i = 0; i < noOfPocChanges; i++) {
			Poc poc = tcp.getPocs()[i];
			CioHelper.getInstance().cioWrite(cio, poc.getResNo0(), 1); /* RSpoc_i */
			CioHelper.getInstance().cioWrite(cio, poc.getCompNo0(), (noOfComps <= 256 ? 1 : 2)); /* CSpoc_i */
			CioHelper.getInstance().cioWrite(cio, poc.getLayNo1(), 2); /* LYEpoc_i */
			poc.setLayNo1(MathUtil.getInstance().intMin(poc.getLayNo1(), tcp.getNoOfLayers()));
			CioHelper.getInstance().cioWrite(cio, poc.getResNo1(), 1); /* REpoc_i */
			poc.setResNo1(MathUtil.getInstance().intMin(poc.getResNo1(), tccp.getNoOfResolutions()));
			CioHelper.getInstance().cioWrite(cio, poc.getCompNo1(), (noOfComps <= 256 ? 1 : 2)); /* CEpoc_i */
			poc.setCompNo1(MathUtil.getInstance().intMin(poc.getCompNo1(), noOfComps));
			CioHelper.getInstance().cioWrite(cio, poc.getProgressionOrder().value(), 1); /* Ppoc_i */
		}
	}

	@SuppressWarnings({ "java:S1659", "unused" })
	private void j2kReadPoc(J2K j2kInfo) {
		int length, noOfPocChanges, i, oldPoc;

		int noOfComps = j2kInfo.getImage().getNoOfComps();

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Tcp tcp = j2kInfo.getState() == J2kStatus.J2K_STATE_TPH.value()
				? codingParameters.getTcps()[j2kInfo.getCurTileNo()]
				: j2kInfo.getDefaultTcp();
		Cio cio = j2kInfo.getCio();

		oldPoc = tcp.getIsPoc() != 0 ? tcp.getNoOfPocs() + 1 : 0;
		tcp.setIsPoc(1);
		length = (int) CioHelper.getInstance().cioRead(cio, 2); /* Lpoc */
		noOfPocChanges = (length - 2) / (5 + 2 * (noOfComps <= 256 ? 1 : 2));

		for (i = oldPoc; i < noOfPocChanges + oldPoc; i++) {
			Poc poc = tcp.getPocs()[i];
			poc.setResNo0((int) CioHelper.getInstance().cioRead(cio, 1)); /* RSpoc_i */
			poc.setCompNo0((int) CioHelper.getInstance().cioRead(cio, noOfComps <= 256 ? 1 : 2)); /* CSpoc_i */
			poc.setLayNo1((int) CioHelper.getInstance().cioRead(cio, 2)); /* LYEpoc_i */
			poc.setResNo1((int) CioHelper.getInstance().cioRead(cio, 1)); /* REpoc_i */
			poc.setCompNo1(MathUtil.getInstance().intMin(
					(int) CioHelper.getInstance().cioRead(cio, noOfComps <= 256 ? 1 : 2), noOfComps)); /* CEpoc_i */
			poc.setProgressionOrder(
					ProgressionOrder.fromValue((int) CioHelper.getInstance().cioRead(cio, 1))); /* Ppoc_i */
		}

		tcp.setNoOfPocs(noOfPocChanges + oldPoc - 1);
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776", "unused" })
	private void j2kReadCrg(J2K j2kInfo) {
		int length, i, xCrgi, yCrgi;

		Cio cio = j2kInfo.getCio();
		int noOfComps = j2kInfo.getImage().getNoOfComps();

		length = (int) CioHelper.getInstance().cioRead(cio, 2); /* Lcrg */
		for (i = 0; i < noOfComps; i++) {
			xCrgi = (int) CioHelper.getInstance().cioRead(cio, 2); /* Xcrg_i */
			yCrgi = (int) CioHelper.getInstance().cioRead(cio, 2); /* Ycrg_i */
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776", "unused" })
	private void j2kReadTlm(J2K j2kInfo) {
		int length, zTlm, sTlm, st, sp, tileTlm, i;
		long tTlmi, pTlmi;

		Cio cio = j2kInfo.getCio();

		length = (int) CioHelper.getInstance().cioRead(cio, 2); /* Ltlm */
		zTlm = (int) CioHelper.getInstance().cioRead(cio, 1); /* zTlm */
		sTlm = (int) CioHelper.getInstance().cioRead(cio, 1); /* sTlm */
		st = ((sTlm >> 4) & 0x01) + ((sTlm >> 4) & 0x02);
		sp = (sTlm >> 6) & 0x01;
		tileTlm = (length - 4) / ((sp + 1) * 2 + st);
		for (i = 0; i < tileTlm; i++) {
			tTlmi = CioHelper.getInstance().cioRead(cio, st); /* Ttlm_i */
			pTlmi = CioHelper.getInstance().cioRead(cio, sp != 0 ? 4 : 2); /* Ptlm_i */
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776", "unused" })
	private void j2kReadPlm(J2K j2kInfo) {
		int length, i, zPlm, nPlm, add, packetLength = 0;

		Cio cio = j2kInfo.getCio();

		length = (int) CioHelper.getInstance().cioRead(cio, 2); /* Lplm */
		zPlm = (int) CioHelper.getInstance().cioRead(cio, 1); /* Zplm */
		length -= 3;
		while (length > 0) {
			nPlm = (int) CioHelper.getInstance().cioRead(cio, 4); /* Nplm */
			length -= 4;
			for (i = nPlm; i > 0; i--) {
				add = (int) CioHelper.getInstance().cioRead(cio, 1);
				length--;
				packetLength = (packetLength << 7) + add; /* Iplm_ij */
				if ((add & 0x80) == 0) {
					/* New packet */
					packetLength = 0;
				}
				if (length <= 0)
					break;
			}
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776", "unused" })
	private void j2kReadPlt(J2K j2kInfo) {
		int length, i, zPlt, packetLength = 0, add;

		Cio cio = j2kInfo.getCio();

		length = (int) CioHelper.getInstance().cioRead(cio, 2); /* Lplt */
		zPlt = (int) CioHelper.getInstance().cioRead(cio, 1); /* Zplt */
		for (i = length - 3; i > 0; i--) {
			add = (int) CioHelper.getInstance().cioRead(cio, 1);
			packetLength = (packetLength << 7) + add; /* Iplt_i */
			if ((add & 0x80) == 0) {
				/* New packet */
				packetLength = 0;
			}
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S3776", "unused" })
	private void j2kReadPpm(J2K j2kInfo, boolean useJPWL) {
		int length, zPpm, i, j;
		int nPpm;

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Cio cio = j2kInfo.getCio();

		length = (int) CioHelper.getInstance().cioRead(cio, 2);
		codingParameters.setPpm(1);

		zPpm = (int) CioHelper.getInstance().cioRead(cio, 1); /* Z_ppm */
		length -= 3;
		while (length > 0) {
			if (codingParameters.getPpmPrevious() == 0) {
				nPpm = (int) CioHelper.getInstance().cioRead(cio, 4); /* N_ppm */
				length -= 4;
			} else {
				nPpm = codingParameters.getPpmPrevious();
			}
			j = codingParameters.getPpmStore();
			if (zPpm == 0) { /* First PPM marker */
				codingParameters.setPpmData(new byte[nPpm]);
				codingParameters.setPpmDataFirst(codingParameters.getPpmData());
				codingParameters.setPpmLength(nPpm);
			} else { /* NON-first PPM marker */
				int newSize = nPpm + codingParameters.getPpmStore();
				int oldSize = codingParameters.getPpmData() == null ? 0 : codingParameters.getPpmData().length;
				byte[] data = new byte[oldSize + newSize];
				if (oldSize != 0)
					System.arraycopy(codingParameters.getPpmData(), 0, data, 0, codingParameters.getPpmData().length);

				codingParameters.setPpmData(data);

				/* this memory allocation check could be done even in non-JPWL cases */
				if (useJPWL && (codingParameters.getCorrect() != 0 && codingParameters.getPpmData() == null)) {
					logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
							"JPWL: failed memory allocation during PPM marker parsing (pos. {0})",
							CioHelper.getInstance().cioTell(cio)));

					if (!OpenJpegConstant.JPWL_ASSUME || OpenJpegConstant.JPWL_ASSUME) {
						codingParameters.setPpmData(null);
						logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, TAG_JPWL_GIVING_UP);
						return;
					}
				}

				codingParameters.setPpmDataFirst(codingParameters.getPpmData());
				codingParameters.setPpmLength(nPpm + codingParameters.getPpmStore());
			}
			for (i = nPpm; i > 0; i--) { /* Read packet header */
				codingParameters.getPpmData()[j] = (byte) CioHelper.getInstance().cioRead(cio, 1);
				j++;
				length--;
				if (length == 0)
					break; /*
							 * Case of non-finished packet header in present marker but finished in next one
							 */
			}
			codingParameters.setPpmPrevious(i - 1);
			codingParameters.setPpmStore(j);
		}
	}

	@SuppressWarnings({ "java:S1659"})
	private void j2kReadPpt(J2K j2kInfo) {
		int length, zPpt, i, j = 0;

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Tcp tcp = codingParameters.getTcps()[j2kInfo.getCurTileNo()];
		Cio cio = j2kInfo.getCio();

		length = (int) CioHelper.getInstance().cioRead(cio, 2);
		zPpt = (int) CioHelper.getInstance().cioRead(cio, 1);
		tcp.setPpt(1);
		if (zPpt == 0) { /* First PPT marker */
			tcp.setPptData(new byte[(length - 3)]);
			tcp.setPptDataFirst(tcp.getPptData());
			tcp.setPptStore(0);
			tcp.setPptLength(length - 3);
		} else { /* NON-first PPT marker */
			int newSize = (length - 3 + tcp.getPptStore());
			int oldSize = tcp.getPptData() == null ? 0 : tcp.getPptData().length;
			byte[] data = new byte[oldSize + newSize];
			if (oldSize != 0)
				System.arraycopy(tcp.getPptData(), 0, data, 0, tcp.getPptData().length);

			tcp.setPptData(data);
			tcp.setPptDataFirst(tcp.getPptData());
			tcp.setPptLength(length - 3 + tcp.getPptStore());
		}
		j = tcp.getPptStore();
		for (i = length - 3; i > 0; i--) {
			tcp.getPptData()[j] = (byte) CioHelper.getInstance().cioRead(cio, 1);
			j++;
		}
		tcp.setPptStore(j);
	}

	private void j2kWriteTlm(J2K j2kInfo) {
		int lengthPosition;
		Cio cio = j2kInfo.getCio();
		j2kInfo.setTlmStart(CioHelper.getInstance().cioTell(cio));
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_TLM, 2);/* TLM */
		lengthPosition = 4 + (5 * j2kInfo.getTotalNoOfTilePart());
		CioHelper.getInstance().cioWrite(cio, lengthPosition, 2); /* Length tlm */
		CioHelper.getInstance().cioWrite(cio, 0, 1); /* zTlm=0 */
		CioHelper.getInstance().cioWrite(cio, 80, 1); /* sTlm st=1(8bits-255 tiles max),sp=1(pTlm=32bits) */
		CioHelper.getInstance().cioSkip(cio, 5 * j2kInfo.getTotalNoOfTilePart());
	}

	private void j2kWriteSot(J2K j2kInfo, boolean useJPWL) {
		int lengthPosition;
		int length;

		Cio cio = j2kInfo.getCio();

		j2kInfo.setSotStart(CioHelper.getInstance().cioTell(cio));
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_SOT, 2); /* SOT */
		lengthPosition = CioHelper.getInstance().cioTell(cio);
		CioHelper.getInstance().cioSkip(cio, 2); /* Lsot (further) */
		CioHelper.getInstance().cioWrite(cio, j2kInfo.getCurTileNo(), 2); /* Isot */
		CioHelper.getInstance().cioSkip(cio, 4); /* Psot (further in j2kWriteSod) */
		CioHelper.getInstance().cioWrite(cio, j2kInfo.getCurTilePartNo(), 1); /* TPsot */
		CioHelper.getInstance().cioWrite(cio, j2kInfo.getCurTotalNoOfTilePart()[j2kInfo.getCurTileNo()], 1); /* TNsot */
		length = CioHelper.getInstance().cioTell(cio) - lengthPosition;
		CioHelper.getInstance().cioSeek(cio, lengthPosition);
		CioHelper.getInstance().cioWrite(cio, length, 2); /* Lsot */
		CioHelper.getInstance().cioSeek(cio, lengthPosition + length);

		if (useJPWL) {
			/* update markers struct */
			j2kAddMarker(j2kInfo.getCodeStreamInfo(), OpenJpegConstant.J2K_MS_SOT, j2kInfo.getSotStart(), length + 2);
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3012", "java:S3776", "java:S6541", "unused" })
	private void j2kReadSot(J2K j2kInfo, boolean useJPWL) {
		int length, tileNo, totalLength, partNo, noOfParts, i;
		Tcp tcp = null;
		int status = 0;

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Cio cio = j2kInfo.getCio();

		length = (int) CioHelper.getInstance().cioRead(cio, 2);
		tileNo = (int) CioHelper.getInstance().cioRead(cio, 2);

		if (useJPWL && j2kInfo.getCodingParameters().getCorrect() != 0) {

			int backupTileNo = 0;

			/* tileNo is negative or larger than the number of tiles!!! */
			if ((tileNo < 0) || (tileNo > (codingParameters.getTileWidth() * codingParameters.getTileHeight()))) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("JPWL: bad tile number ({0} out of a maximum of {1})",
						tileNo, (codingParameters.getTileWidth() * codingParameters.getTileHeight())));
				if (!OpenJpegConstant.JPWL_ASSUME) {
					logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, TAG_JPWL_GIVING_UP);
					return;
				}
				/* we try to correct */
				tileNo = backupTileNo;
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
						"JPWL: trying to adjust this - setting tile number to {0}", tileNo));
			}

			/* keep your private count of tiles */
			backupTileNo++;
		}

		if (codingParameters.getTileNoSize() == 0) {
			codingParameters.getTileNo()[codingParameters.getTileNoSize()] = tileNo;
			codingParameters.setTileNoSize(codingParameters.getTileNoSize() + 1);
		} else {
			i = 0;
			while (i < codingParameters.getTileNoSize() && status == 0) {
				status = codingParameters.getTileNo()[i] == tileNo ? 1 : 0;
				i++;
			}
			if (status == 0) {
				codingParameters.getTileNo()[codingParameters.getTileNoSize()] = tileNo;
				codingParameters.setTileNoSize(codingParameters.getTileNoSize() + 1);
			}
		}

		totalLength = (int) CioHelper.getInstance().cioRead(cio, 4);

		/* totalLength is negative or larger than the bytes left!!! */
		if (useJPWL && j2kInfo.getCodingParameters().getCorrect() != 0
				&& ((totalLength < 0) || (totalLength > (CioHelper.getInstance().cioNoOfBytesLeft(cio) + 8)))) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
					"JPWL: bad tile byte size ({0} bytes against {1} bytes left)", totalLength,
					CioHelper.getInstance().cioNoOfBytesLeft(cio) + 8));
			if (!OpenJpegConstant.JPWL_ASSUME) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, TAG_JPWL_GIVING_UP);
				return;
			}
			/* we try to correct */
			totalLength = 0;
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format(
					"trying to adjust this - setting Psot to {0} => assuming it is the last tile)", totalLength));
		}

		if (totalLength == 0)
			totalLength = CioHelper.getInstance().cioNoOfBytesLeft(cio) + 8;

		partNo = (int) CioHelper.getInstance().cioRead(cio, 1);
		noOfParts = (int) CioHelper.getInstance().cioRead(cio, 1);

		j2kInfo.setCurTileNo(tileNo);
		j2kInfo.setCurTilePartNo(partNo);
		j2kInfo.setEot((CioHelper.getInstance().cioGetBufferIndex(cio) - 12 + totalLength));
		j2kInfo.setState(J2kStatus.J2K_STATE_TPH.value());
		tcp = codingParameters.getTcps()[j2kInfo.getCurTileNo()];

		/* Index */
		if (j2kInfo.getCodeStreamInfo() != null) {
			if (tcp.getFirst() != 0) {
				if (tileNo == 0)
					j2kInfo.getCodeStreamInfo().setMainHeadEnd(CioHelper.getInstance().cioTell(cio) - 13);

				j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].setTileNo(tileNo);
				j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo]
						.setStartPosition(CioHelper.getInstance().cioTell(cio) - 12);
				j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].setEndPosition(
						j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].getStartPosition() + totalLength - 1);
				j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].setNoOfTileParts(noOfParts);
				if (noOfParts != 0)
					j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].setTp(new TpInfo[noOfParts]);
				else
					j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].setTp(new TpInfo[10]); // Fix me (10)
			} else {
				j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].setEndPosition(
						j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].getEndPosition() + totalLength);
			}
			j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].getTp()[partNo]
					.setTpStartPosition(CioHelper.getInstance().cioTell(cio) - 12);
			j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].getTp()[partNo].setTpEndPosition(
					j2kInfo.getCodeStreamInfo().getTileInfo()[tileNo].getTp()[partNo].getTpStartPosition() + totalLength
							- 1);
		}

		if (tcp.getFirst() == 1) {
			/* Initialization PPT */
			TileComponentCodingParameters[] tmp = tcp.getTccps();
			tcp.setCodingStyle(j2kInfo.getDefaultTcp().getCodingStyle());
			tcp.setDistortionRatio(j2kInfo.getDefaultTcp().getDistortionRatio());
			tcp.setMct(j2kInfo.getDefaultTcp().getMct());
			tcp.setNoOfLayers(j2kInfo.getDefaultTcp().getNoOfLayers());
			tcp.setNoOfPocs(j2kInfo.getDefaultTcp().getNoOfPocs());
			tcp.setIsPoc(j2kInfo.getDefaultTcp().getIsPoc());
			tcp.setPocs(j2kInfo.getDefaultTcp().getPocs());
			tcp.setPpt(0);
			tcp.setPptData(null);
			tcp.setPptDataIndex(j2kInfo.getDefaultTcp().getPptDataIndex());
			tcp.setPptLength(j2kInfo.getDefaultTcp().getPptLength());
			tcp.setPptStore(j2kInfo.getDefaultTcp().getPptStore());
			tcp.setProgressionOrder(j2kInfo.getDefaultTcp().getProgressionOrder());
			tcp.setRates(j2kInfo.getDefaultTcp().getRates());
			tcp.setTccps(j2kInfo.getDefaultTcp().getTccps());

			for (i = 0; i < j2kInfo.getImage().getNoOfComps(); i++) {
				tcp.getTccps()[i] = j2kInfo.getDefaultTcp().getTccps()[i];
			}
			codingParameters.getTcps()[j2kInfo.getCurTileNo()] = tcp;
			codingParameters.getTcps()[j2kInfo.getCurTileNo()].setFirst(0);
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S3776" })
	private void j2kWriteSod(J2K j2kInfo, Object tileCoder, boolean useJPWL) {
		int length, layerNo;
		int totalLength;
		Tcp tcp = null;
		CodeStreamInfo codeStreamInfo = null;

		Tcd tcd = (Tcd) tileCoder; /* cast is needed because of conflicts in header inclusions */
		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Cio cio = j2kInfo.getCio();

		tcd.setTilePartNo(j2kInfo.getTilePartNo());
		tcd.setCurTilePartNo(j2kInfo.getCurTilePartNo());

		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_SOD, 2);
		if (j2kInfo.getCurTileNo() == 0) {
			j2kInfo.setSodStart(CioHelper.getInstance().cioTell(cio) + j2kInfo.getPosCorrection());
		}

		/* INDEX >> */
		codeStreamInfo = j2kInfo.getCodeStreamInfo();
		if (codeStreamInfo != null) {
			if (j2kInfo.getCurTilePartNo() == 0) {
				codeStreamInfo.getTileInfo()[j2kInfo.getCurTileNo()]
						.setEndHeader(CioHelper.getInstance().cioTell(cio) + j2kInfo.getPosCorrection() - 1);
				j2kInfo.getCodeStreamInfo().getTileInfo()[j2kInfo.getCurTileNo()].setTileNo(j2kInfo.getCurTileNo());
			} else {
				if (codeStreamInfo.getTileInfo()[j2kInfo.getCurTileNo()].getPacket()[codeStreamInfo.getPacketNo() - 1]
						.getEndPosition() < CioHelper.getInstance().cioTell(cio))
					codeStreamInfo.getTileInfo()[j2kInfo.getCurTileNo()].getPacket()[codeStreamInfo.getPacketNo()]
							.setStartPosition(CioHelper.getInstance().cioTell(cio));
			}
			/* UniPG>> */
			if (useJPWL) {
				/* update markers struct */
				j2kAddMarker(j2kInfo.getCodeStreamInfo(), OpenJpegConstant.J2K_MS_SOD, j2kInfo.getSodStart(), 2);
			}
			/* <<UniPG */
		}
		/* << INDEX */

		tcp = codingParameters.getTcps()[j2kInfo.getCurTileNo()];
		for (layerNo = 0; layerNo < tcp.getNoOfLayers(); layerNo++) {
			if (tcp.getRates()[layerNo] > (j2kInfo.getSodStart()
					/ (codingParameters.getTileHeight() * codingParameters.getTileWidth()))) {
				tcp.getRates()[layerNo] = tcp.getRates()[layerNo] - (float) (j2kInfo.getSodStart()
						/ (double) (codingParameters.getTileHeight() * codingParameters.getTileWidth()));
			} else if (tcp.getRates()[layerNo] != 0) {
				tcp.getRates()[layerNo] = 1;
			}
		}
		if (j2kInfo.getCurTilePartNo() == 0) {
			tcd.getTcdImage().getTiles()[0].setPacketNo(0);
			if (codeStreamInfo != null)
				codeStreamInfo.setPacketNo(0);
		}

		length = TcdHelper.getInstance().tcdEncodeTile(tcd, j2kInfo.getCurTileNo(),
				CioHelper.getInstance().cioGetBuffer(cio), CioHelper.getInstance().cioNoOfBytesLeft(cio) - 2,
				codeStreamInfo);

		/* Writing Psot in SOT marker */
		totalLength = CioHelper.getInstance().cioTell(cio) + length - j2kInfo.getSotStart();
		CioHelper.getInstance().cioSeek(cio, j2kInfo.getSotStart() + 6);
		CioHelper.getInstance().cioWrite(cio, totalLength, 4);
		CioHelper.getInstance().cioSeek(cio, j2kInfo.getSotStart() + totalLength);
		/* Writing Ttlm and Ptlm in TLM marker */
		if (codingParameters.getCinemaMode().value() != 0) {
			CioHelper.getInstance().cioSeek(cio, j2kInfo.getTlmStart() + 6 + (5 * j2kInfo.getCurTilePartNo()));
			CioHelper.getInstance().cioWrite(cio, j2kInfo.getCurTileNo(), 1);
			CioHelper.getInstance().cioWrite(cio, totalLength, 4);
		}
		CioHelper.getInstance().cioSeek(cio, j2kInfo.getSotStart() + totalLength);
	}

	@SuppressWarnings({ "java:S1659", "java:S3776" })
	private void j2kReadSod(J2K j2kInfo) {
		int length, truncate = 0, i;
		byte[] data = null;
		int dataIndex = 0;

		Cio cio = j2kInfo.getCio();
		int curtileno = j2kInfo.getCurTileNo();

		/* Index */
		if (j2kInfo.getCodeStreamInfo() != null) {
			j2kInfo.getCodeStreamInfo().getTileInfo()[j2kInfo.getCurTileNo()].getTp()[j2kInfo.getCurTilePartNo()]
					.setTpEndHeader(CioHelper.getInstance().cioTell(cio) + j2kInfo.getPosCorrection() - 1);
			if (j2kInfo.getCurTilePartNo() == 0)
				j2kInfo.getCodeStreamInfo().getTileInfo()[j2kInfo.getCurTileNo()]
						.setEndHeader(CioHelper.getInstance().cioTell(cio) + j2kInfo.getPosCorrection() - 1);
			j2kInfo.getCodeStreamInfo().setPacketNo(0);
		}

		length = MathUtil.getInstance().intMin(j2kInfo.getEot() - CioHelper.getInstance().cioGetBufferIndex(cio),
				CioHelper.getInstance().cioNoOfBytesLeft(cio) + 1);

		if (length == CioHelper.getInstance().cioNoOfBytesLeft(cio) + 1) {
			truncate = 1; /* Case of a truncate codestream */
		}

		data = j2kInfo.getTileData()[curtileno];

		int newSize = (j2kInfo.getTileLength()[curtileno] + length);
		int oldSize = data == null ? 0 : data.length;
		byte[] data1 = new byte[oldSize + newSize + 1];
		if (oldSize != 0)
			System.arraycopy(data, 0, data1, 0, data.length);

		dataIndex = 0 + j2kInfo.getTileLength()[curtileno];
		for (i = 0; i < length; i++) {
			data1[dataIndex + i] = (byte) CioHelper.getInstance().cioRead(cio, 1);
		}
		data1[length] = (byte) OpenJpegConstant.LAST_DATA_BYTE;

		j2kInfo.getTileLength()[curtileno] = j2kInfo.getTileLength()[curtileno] + length + 1;
		j2kInfo.getTileData()[curtileno] = data1;
		j2kInfo.setEndCode((byte) OpenJpegConstant.LAST_DATA_BYTE);

		if (truncate == 0) {
			j2kInfo.setState(J2kStatus.J2K_STATE_TPHSOT.value());
		} else {
			j2kInfo.setState(J2kStatus.J2K_STATE_NEOC.value());
		}
		j2kInfo.setCurTilePartNo(j2kInfo.getCurTilePartNo() + 1);
	}

	private void j2kWriteRgn(J2K j2kInfo, int compNo, int tileNo) {
		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Tcp tcp = codingParameters.getTcps()[tileNo];
		Cio cio = j2kInfo.getCio();
		int noOfComps = j2kInfo.getImage().getNoOfComps();

		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_RGN, 2); /* RGN */
		CioHelper.getInstance().cioWrite(cio, noOfComps <= 256 ? 5 : 6, 2); /* Lrgn */
		CioHelper.getInstance().cioWrite(cio, compNo, noOfComps <= 256 ? 1 : 2); /* Crgn */
		CioHelper.getInstance().cioWrite(cio, 0, 1); /* Srgn */
		CioHelper.getInstance().cioWrite(cio, tcp.getTccps()[compNo].getRoiShift(), 1); /* SPrgn */
	}

	@SuppressWarnings({ "java:S1659", "java:S2589", "unused" })
	private void j2kReadRgn(J2K j2kInfo, boolean useJPWL) {
		int length, compNo, roiStyle;

		CodingParameters codingParameters = j2kInfo.getCodingParameters();
		Tcp tcp = j2kInfo.getState() == J2kStatus.J2K_STATE_TPH.value()
				? codingParameters.getTcps()[j2kInfo.getCurTileNo()]
				: j2kInfo.getDefaultTcp();
		Cio cio = j2kInfo.getCio();
		int noOfComps = j2kInfo.getImage().getNoOfComps();

		length = (int) CioHelper.getInstance().cioRead(cio, 2); /* Lrgn */
		compNo = (int) CioHelper.getInstance().cioRead(cio, noOfComps <= 256 ? 1 : 2); /* Crgn */
		roiStyle = (int) CioHelper.getInstance().cioRead(cio, 1); /* Srgn */

		/* totalLength is negative or larger than the bytes left!!! */
		if (useJPWL && j2kInfo.getCodingParameters().getCorrect() != 0 && compNo >= noOfComps) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format(
					"JPWL: bad component number in RGN ({0} when there are only {1})", compNo, noOfComps));
			if (!OpenJpegConstant.JPWL_ASSUME || OpenJpegConstant.JPWL_ASSUME) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, TAG_JPWL_GIVING_UP);
				return;
			}
		}

		tcp.getTccps()[compNo].setRoiShift((int) CioHelper.getInstance().cioRead(cio, 1)); /* SPrgn */
	}

	private void j2kWriteEoc(J2K j2kInfo, boolean useJPWL) {
		Cio cio = j2kInfo.getCio();
		CioHelper.getInstance().cioWrite(cio, OpenJpegConstant.J2K_MS_EOC, 2);

		/* UniPG>> */
		if (useJPWL) {
			/* update markers struct */
			j2kAddMarker(j2kInfo.getCodeStreamInfo(), OpenJpegConstant.J2K_MS_EOC,
					CioHelper.getInstance().cioTell(cio) - 2, 2);
		}
		/* <<UniPG */
	}

	@SuppressWarnings({ "java:S1659" })
	private void j2kReadEoc(J2K j2kInfo, boolean useJPWL) {
		int i, tileNo;
		int success;

		/* if packets should be decoded */
		if (j2kInfo.getCodingParameters().getLimitDecoding() != LimitDecoding.DECODE_ALL_BUT_PACKETS) {
			Tcd tcd = TcdHelper.getInstance().tcdCreate(j2kInfo.getCodecContextInfo());
			TcdHelper.getInstance().tcdMallocDecode(tcd, j2kInfo.getImage(), j2kInfo.getCodingParameters());
			for (i = 0; i < j2kInfo.getCodingParameters().getTileNoSize(); i++) {
				TcdHelper.getInstance().tcdMallocDecodeTile(tcd, j2kInfo.getImage(), j2kInfo.getCodingParameters(), i,
						j2kInfo.getCodeStreamInfo());
				tileNo = j2kInfo.getCodingParameters().getTileNo()[i];
				success = TcdHelper.getInstance().tcdDecodeTile(tcd, j2kInfo.getTileData()[tileNo],
						j2kInfo.getTileLength()[tileNo], tileNo, j2kInfo.getCodeStreamInfo(), useJPWL);
				j2kInfo.getTileData()[tileNo] = null;
				TcdHelper.getInstance().tcdFreeDecodeTile(tcd, i);
				if (success == 0) {
					j2kInfo.setState(j2kInfo.getState() | J2kStatus.J2K_STATE_ERR.value());
					break;
				}
			}
			TcdHelper.getInstance().tcdFreeDecode(tcd);
			TcdHelper.getInstance().tcdDestroy(tcd);
		}
		/* if packets should not be decoded */
		else {
			for (i = 0; i < j2kInfo.getCodingParameters().getTileNoSize(); i++) {
				tileNo = j2kInfo.getCodingParameters().getTileNo()[i];
				j2kInfo.getTileData()[tileNo] = null;
			}
		}
		if ((j2kInfo.getState() & J2kStatus.J2K_STATE_ERR.value()) != 0)
			j2kInfo.setState(J2kStatus.J2K_STATE_MT.value() + J2kStatus.J2K_STATE_ERR.value());
		else
			j2kInfo.setState(J2kStatus.J2K_STATE_MT.value());
	}

	@SuppressWarnings({ "java:S1659", "java:S3776" })
	private void j2kReadUnknown(J2K j2kInfo, boolean useJPWL) {
		logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Unknown marker");

		if (useJPWL && j2kInfo.getCodingParameters().getCorrect() != 0) {
			int m = 0, id, i;
			int minId = 0, minDist = 17, curDist = 0, tmpId;
			CioHelper.getInstance().cioSeek(j2kInfo.getCio(), CioHelper.getInstance().cioTell(j2kInfo.getCio()) - 2);
			id = (int) CioHelper.getInstance().cioRead(j2kInfo.getCio(), 2);
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("JPWL: really do not know this marker {0}", id));
			if (!OpenJpegConstant.JPWL_ASSUME) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, 
						"- possible synch loss due to uncorrectable codestream errors => giving up");
				return;
			}
			/* OK, activate this at your own risk!!! */
			/* we look for the marker at the minimum hamming distance from this */
			while (J2K_DECODER_FUNCTION_INFO[m].getId() != 0) {
				/* 1's where they differ */
				tmpId = J2K_DECODER_FUNCTION_INFO[m].getId() ^ id;

				/* compute the hamming distance between our id and the current */
				curDist = 0;
				for (i = 0; i < 16; i++) {
					if (((tmpId >> i) & 0x0001) != 0) {
						curDist++;
					}
				}

				/* if current distance is smaller, set the minimum */
				if (curDist < minDist) {
					minDist = curDist;
					minId = J2K_DECODER_FUNCTION_INFO[m].getId();
				}

				/* jump to the next marker */
				m++;
			}

			/* do we substitute the marker? */
			if (minDist < OpenJpegConstant.JPWL_MAXIMUM_HAMMING) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format(
						"marker {0} is at distance {1} from the read {2} - trying to substitute in place and crossing fingers!",
						minId, minDist, id));

				CioHelper.getInstance().cioSeek(j2kInfo.getCio(),
						CioHelper.getInstance().cioTell(j2kInfo.getCio()) - 2);
				CioHelper.getInstance().cioWrite(j2kInfo.getCio(), minId, 2);

				/* rewind */
				CioHelper.getInstance().cioSeek(j2kInfo.getCio(),
						CioHelper.getInstance().cioTell(j2kInfo.getCio()) - 2);
			}
		}
	}

	/**
	 * Read the lookup table containing all the marker, status and action
	 * 
	 * @param id Marker value
	 */
	private DecoderFunctionInfo j2kDecoderFunctionLookup(int id) {
		DecoderFunctionInfo e = null;
		for (int eIndex = 0; eIndex < J2K_DECODER_FUNCTION_INFO.length; eIndex++) {
			e = J2K_DECODER_FUNCTION_INFO[eIndex];
			if (e.getId() == id) {
				break;
			}
		}
		return e;
	}

	/* ----------------------------------------------------------------------- */
	/* J2K / JPT decoder interface */
	/* ----------------------------------------------------------------------- */

	public J2K j2kCreateDecompression(CodecContextInfo codecContextInfo) {
		J2K j2kInfo = new J2K();
		j2kInfo.setDefaultTcp(new Tcp());
		j2kInfo.setCodecContextInfo(codecContextInfo);
		j2kInfo.setTileData(null);

		return j2kInfo;
	}

	@SuppressWarnings({ "java:S3776" })
	public void j2kDestroyDecompression(J2K j2kInfo) {
		int i = 0;

		if (j2kInfo.getTileLength() != null) {
			j2kInfo.setTileLength(null);
		}
		if (j2kInfo.getTileData() != null) {
			j2kInfo.setTileData(null);
		}
		if (j2kInfo.getDefaultTcp() != null) {
			Tcp defaultTcp = j2kInfo.getDefaultTcp();
			if (defaultTcp.getPptDataFirst() != null) {
				defaultTcp.setPptDataFirst(null);
			}
			if (j2kInfo.getDefaultTcp().getTccps() != null) {
				j2kInfo.getDefaultTcp().setTccps(null);
			}
			j2kInfo.setDefaultTcp(null);
		}
		if (j2kInfo.getCodingParameters() != null) {
			CodingParameters codingParameters = j2kInfo.getCodingParameters();
			if (codingParameters.getTcps() != null) {
				for (i = 0; i < codingParameters.getTileWidth() * codingParameters.getTileHeight(); i++) {
					if (codingParameters.getTcps()[i].getPptDataFirst() != null) {
						codingParameters.getTcps()[i].setPptDataFirst(null);
					}
					if (codingParameters.getTcps()[i].getTccps() != null) {
						codingParameters.getTcps()[i].setTccps(null);
					}
				}
				codingParameters.setTcps(null);
			}
			if (codingParameters.getPpmDataFirst() != null) {
				codingParameters.setPpmDataFirst(null);
			}
			if (codingParameters.getTileNo() != null) {
				codingParameters.setTileNo(null);
			}
			if (codingParameters.getComment() != null) {
				codingParameters.setComment(null);
			}
		}
	}

	public void j2kSetupDecoder(J2K j2kInfo, DecompressionParameters parameters, boolean useJPWL) {
		if (j2kInfo != null && parameters != null) {
			/* create and initialize the coding parameters structure */
			CodingParameters codingParameters = new CodingParameters();
			codingParameters.setReduce(parameters.getCpReduce());
			codingParameters.setLayer(parameters.getCpLayer());
			codingParameters.setLimitDecoding(parameters.getCpLimitDecoding());

			if (useJPWL) {
				codingParameters.setCorrect(parameters.getJpwlCorrect());
				codingParameters.setExpComps(parameters.getJpwlExpComps());
				codingParameters.setMaxTiles(parameters.getJpwlMaxTiles());
			}

			/*
			 * keep a link to codingParameters so that we can destroy it later in
			 * j2kDestroyDeCompression
			 */
			j2kInfo.setCodingParameters(codingParameters);
		}
	}

	@SuppressWarnings({ "java:S135", "java:S1659", "java:S3776", "java:S6541", "unused" })
	public OpenJpegImage j2kDecode(J2K j2kInfo, Cio cio, CodeStreamInfo codeStreamInfo, boolean useJPWL) {
		OpenJpegImage image = null;

		j2kInfo.setCio(cio);
		j2kInfo.setCodeStreamInfo(codeStreamInfo);

		/* create an empty image */
		image = ImageHelper.getInstance().imageCreateBasic();
		j2kInfo.setImage(image);

		j2kInfo.setState(J2kStatus.J2K_STATE_MHSOC.value());

		for (;;) {
			DecoderFunctionInfo e;
			int id = (int) CioHelper.getInstance().cioRead(cio, 2);

			/* we try to honor JPWL correction power */
			if (useJPWL && j2kInfo.getCodingParameters().getCorrect() != 0) {

				int originalPosition = CioHelper.getInstance().cioTell(cio);
				int status;

				/* call the corrector */
				status = jpwlCorrect(j2kInfo);

				/* go back to where you were */
				CioHelper.getInstance().cioSeek(cio, originalPosition - 2);

				/* re-read the marker */
				id = (int) CioHelper.getInstance().cioRead(cio, 2);

				/* check whether it begins with ff */
				if (id >> 8 != 0xff) {
					logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("JPWL: possible bad marker {0} at {1}", id,
							CioHelper.getInstance().cioTell(cio) - 2));
					if (!OpenJpegConstant.JPWL_ASSUME) {
						ImageHelper.getInstance().imageDestroy(image);
						logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, TAG_JPWL_GIVING_UP);
						return null;
					}
					/* we try to correct */
					id = id | 0xff00;
					CioHelper.getInstance().cioSeek(cio, CioHelper.getInstance().cioTell(cio) - 2);
					CioHelper.getInstance().cioWrite(cio, id, 2);
					logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("trying to adjust this setting marker to  {0}", id));
				}
			}

			if (id >> 8 != 0xff) {
				ImageHelper.getInstance().imageDestroy(image);
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("{0}: expected a marker instead of {1}",
						CioHelper.getInstance().cioTell(cio) - 2, id));
				return null;
			}
			e = j2kDecoderFunctionLookup(id);
			// Check if the marker is known
			if (e != null && (j2kInfo.getState() & e.getStates()) == 0) {
				ImageHelper.getInstance().imageDestroy(image);
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("{0}: unexpected marker {1}",
						CioHelper.getInstance().cioTell(cio) - 2, id));
				return null;
			}
			// Check if the decoding is limited to the main header
			if (e != null && e.getId() == OpenJpegConstant.J2K_MS_SOT
					&& j2kInfo.getCodingParameters().getLimitDecoding() == LimitDecoding.LIMIT_TO_MAIN_HEADER) {
				logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Main Header decoded");
				return image;
			}

			if (e != null && e.getJ2kFunctionName() != null) {
				callHandler(e.getJ2kFunctionName(), new Object[] { j2kInfo, useJPWL });
			}

			if ((j2kInfo.getState() & J2kStatus.J2K_STATE_ERR.value()) != 0)
				return null;

			if (j2kInfo.getState() == J2kStatus.J2K_STATE_MT.value()) {
				break;
			}
			if (j2kInfo.getState() == J2kStatus.J2K_STATE_NEOC.value()) {
				break;
			}
		}
		if (j2kInfo.getState() == J2kStatus.J2K_STATE_NEOC.value()) {
			j2kReadEoc(j2kInfo, useJPWL);
		}

		if (j2kInfo.getState() != J2kStatus.J2K_STATE_MT.value()) {
			logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Incomplete bitstream");
		}

		return image;
	}

	/*
	 * Read a JPT-stream and decode file
	 *
	 */
	@SuppressWarnings({ "java:S1172" })
	private int jpwlCorrect(J2K j2kInfo) {
		return 0;
	}

	@SuppressWarnings({ "java:S135", "java:S1172", "java:S3776", "java:S6541" })
	public OpenJpegImage j2kDecodeJPTStream(J2K j2kInfo, Cio cio, CodeStreamInfo codeStreamInfo, boolean useJPWL) {
		OpenJpegImage image = null;
		JPTMessageHeader header = new JPTMessageHeader();
		int position;

		CodecContextInfo codecContextInfo = j2kInfo.getCodecContextInfo();

		j2kInfo.setCio(cio);

		/* create an empty image */
		image = ImageHelper.getInstance().imageCreateBasic();
		j2kInfo.setImage(image);

		j2kInfo.setState(J2kStatus.J2K_STATE_MHSOC.value());

		/* Initialize the header */
		JPTHelper.getInstance().jptInitMsgHeader(header);
		/* Read the first header of the message */
		JPTHelper.getInstance().jptReadMsgHeader(codecContextInfo, cio, header);

		position = CioHelper.getInstance().cioTell(cio);
		if (header.getClassId() != 6) { /* 6 : Main header data-bin message */
			ImageHelper.getInstance().imageDestroy(image);
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, "[JPT-stream] : Expecting Main header first [classId {%d}] !",
					header.getClassId());
			return null;
		}

		for (;;) {
			DecoderFunctionInfo e = null;
			int id;

			if (CioHelper.getInstance().cioNoOfBytesLeft(cio) == 0) {
				j2kReadEoc(j2kInfo, useJPWL);
				return image;
			}
			/* data-bin read -> need to read a new header */
			if ((CioHelper.getInstance().cioTell(cio) - position) == header.getMsgLength()) {
				JPTHelper.getInstance().jptReadMsgHeader(codecContextInfo, cio, header);
				position = CioHelper.getInstance().cioTell(cio);
				if (header.getClassId() != 4) { /* 4 : Tile data-bin message */
					ImageHelper.getInstance().imageDestroy(image);
					logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "[JPT-stream] : Expecting Tile info !");
					return null;
				}
			}

			id = (int) CioHelper.getInstance().cioRead(cio, 2);
			if (id >> 8 != 0xff) {
				ImageHelper.getInstance().imageDestroy(image);
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("{0}: expected a marker instead of {1}",
						CioHelper.getInstance().cioTell(cio) - 2, id));
				return null;
			}
			e = j2kDecoderFunctionLookup(id);
			if (e != null && (j2kInfo.getState() & e.getStates()) == 0) {
				ImageHelper.getInstance().imageDestroy(image);
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("{0}: unexpected marker  of {1}",
						CioHelper.getInstance().cioTell(cio) - 2, id));
				return null;
			}
			if (e != null && e.getJ2kFunctionName() != null) {
				callHandler(e.getJ2kFunctionName(), new Object[] { j2kInfo, OpenJpegConstant.JPWL_ASSUME });
			}
			if (j2kInfo.getState() == J2kStatus.J2K_STATE_MT.value()) {
				break;
			}
			if (j2kInfo.getState() == J2kStatus.J2K_STATE_NEOC.value()) {
				break;
			}
		}
		if (j2kInfo.getState() == J2kStatus.J2K_STATE_NEOC.value()) {
			j2kReadEoc(j2kInfo, useJPWL);
		}

		if (j2kInfo.getState() != J2kStatus.J2K_STATE_MT.value()) {
			logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Incomplete bitstream");
		}

		return image;
	}

	@SuppressWarnings({ "java:S125" })
	private void callHandler(String methodName, Object[] args) {
		switch (methodName) { // Using strings in `switch` requires a recent version of Java
		case "j2kReadSoc":
			this.j2kReadSoc((J2K) args[0]);
			break;
		case "j2kReadSot":
			this.j2kReadSot((J2K) args[0], (Boolean) args[1]);
			break;
		case "j2kReadSod":
			this.j2kReadSod((J2K) args[0]);
			break;
		case "j2kReadEoc":
			this.j2kReadEoc((J2K) args[0], (Boolean) args[1]);
			break;
		case "j2kReadSize":
			this.j2kReadSize((J2K) args[0], (Boolean) args[1]);
			break;
		case "j2kReadCod":
			this.j2kReadCod((J2K) args[0]);
			break;
		case "j2kReadCoc":
			this.j2kReadCoc((J2K) args[0]);
			break;
		case "j2kReadRgn":
			this.j2kReadRgn((J2K) args[0], (Boolean) args[1]);
			break;
		case "j2kReadQcd":
			this.j2kReadQcd((J2K) args[0], (Boolean) args[1]);
			break;
		case "j2kReadQcc":
			this.j2kReadQcc((J2K) args[0], (Boolean) args[1]);
			break;
		case "j2kReadPoc":
			this.j2kReadPoc((J2K) args[0]);
			break;
		case "j2kReadTlm":
			this.j2kReadTlm((J2K) args[0]);
			break;
		case "j2kReadPlm":
			this.j2kReadPlm((J2K) args[0]);
			break;
		case "j2kReadPlt":
			this.j2kReadPlt((J2K) args[0]);
			break;
		case "j2kReadPpm":
			this.j2kReadPpm((J2K) args[0], (Boolean) args[1]);
			break;
		case "j2kReadPpt":
			this.j2kReadPpt((J2K) args[0]);
			break;
		case "j2kReadCrg":
			this.j2kReadCrg((J2K) args[0]);
			break;
		case "j2kReadCom":
			this.j2kReadCom((J2K) args[0]);
			break;
		case "j2kReadEpc":
			// return this.j2kReadEpc((J2K)args[0]);
			break;
		case "j2kReadEpb":
			// return this.j2kReadEpb((J2K)args[0]);
			break;
		case "j2kReadEsd":
			// return this.j2kReadEsd((J2K)args[0]);
			break;
		case "j2kReadRed":
			// return this.j2kReadRed((J2K)args[0]);
			break;
		case "j2kReadSec":
			// return this.j2kReadSec((J2K)args[0]);
			break;
		case "j2kReadInSec":
			// return this.j2kReadInSec((J2K)args[0]);
			break;

		case "j2kReadUnknown":
			this.j2kReadUnknown((J2K) args[0], (Boolean) args[1]);
			break;
		default:
			logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("wrong handler {0}", methodName));
		}
	}
	/* ----------------------------------------------------------------------- */
	/* J2K encoder interface */
	/* ----------------------------------------------------------------------- */

	public J2K j2kCreateCompression(CodecContextInfo codecContextInfo) {
		J2K j2kInfo = new J2K();
		j2kInfo.setCodecContextInfo(codecContextInfo);
		return j2kInfo;
	}

	public void j2kDestroyCompression(J2K j2kInfo) {
		int tileNo;

		if (j2kInfo == null)
			return;
		if (j2kInfo.getCodingParameters() != null) {
			CodingParameters codingParameters = j2kInfo.getCodingParameters();

			if (codingParameters.getComment() != null) {
				codingParameters.setComment(null);
			}
			if (codingParameters.getMatrice() != null) {
				codingParameters.setMatrice(null);
			}
			for (tileNo = 0; tileNo < codingParameters.getTileWidth() * codingParameters.getTileHeight(); tileNo++) {
				codingParameters.getTcps()[tileNo].setTccps(null);
			}
			codingParameters.setTcps(null);
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S3776", "java:S6541" })
	public void j2kSetupEncoder(J2K j2kInfo, CompressionParameters parameters, OpenJpegImage image, boolean useJPWL) {
		int i, j, tileNo, noOfPocsInTile;
		CodingParameters codingParameters = null;

		if (j2kInfo == null || parameters == null || image == null) {
			return;
		}

		/* create and initialize the coding parameters structure */
		codingParameters = new CodingParameters();

		/*
		 * keep a link to codingParameters so that we can destroy it later in
		 * j2kDestroyCompression
		 */
		j2kInfo.setCodingParameters(codingParameters);

		/* set default values for codingParameters */
		codingParameters.setTileWidth(1);
		codingParameters.setTileHeight(1);

		/*
		 * copy user encoding parameters
		 */
		codingParameters.setCinemaMode(parameters.getCpCinemaMode());
		codingParameters.setMaxCompSize(parameters.getMaxCompSize());
		codingParameters.setRsizCap(parameters.getCpRsizCap());
		codingParameters.setDistortionAllocation(parameters.getCpDistortionAllocation());
		codingParameters.setFixedAllocation(parameters.getCpFixedAllocation());
		codingParameters.setFixedQuality(parameters.getCpFixedQuality());

		/* mod fixed_quality */
		if (parameters.getCpMatrice() != null) {
			int arraySize = parameters.getTcpNoOfLayers() * parameters.getNoOfResolution() * 3;
			codingParameters.setMatrice(new int[arraySize]);
			System.arraycopy(parameters.getCpMatrice(), 0, codingParameters.getMatrice(), 0, arraySize);
		}

		/* tiles */
		codingParameters.setTileDX(parameters.getCpTileDX());
		codingParameters.setTileDY(parameters.getCpTileDY());

		/* tile offset */
		codingParameters.setTileX0(parameters.getCpTileX0());
		codingParameters.setTileY0(parameters.getCpTileY0());

		/* comment string */
		if (parameters.getCpComment() != null) {
			codingParameters.setComment(new char[parameters.getCpComment().length + 1]);
			if (codingParameters.getComment() != null) {
				System.arraycopy(parameters.getCpComment(), 0, codingParameters.getComment(), 0,
						parameters.getCpComment().length);
			}
		}

		/*
		 * calculate other encoding parameters
		 */
		if (parameters.getTileSizeOn() != 0) {
			codingParameters.setTileWidth(MathUtil.getInstance()
					.intCeilDiv(image.getX1() - codingParameters.getTileX0(), codingParameters.getTileDX()));
			codingParameters.setTileHeight(MathUtil.getInstance()
					.intCeilDiv(image.getY1() - codingParameters.getTileY0(), codingParameters.getTileDY()));
		} else {
			codingParameters.setTileDX(image.getX1() - codingParameters.getTileX0());
			codingParameters.setTileDY(image.getY1() - codingParameters.getTileY0());
		}

		if (parameters.getTpOn() != 0) {
			codingParameters.setTilePartFlag(parameters.getTpFlag());
			codingParameters.setTilePartOn(1);
		}

		codingParameters.setImageSize(0);
		for (i = 0; i < image.getNoOfComps(); i++) {
			codingParameters.setImageSize(codingParameters.getImageSize() + (image.getComps()[i].getWidth()
					* image.getComps()[i].getHeight() * image.getComps()[i].getPrec()));
		}

		if (useJPWL) {
			/*
			 * calculate JPWL encoding parameters
			 */
			if (parameters.getJpwlEpcOn() != 0) {
				/* set JPWL on */
				codingParameters.setEpcOn(1);
				codingParameters.setInfoOn(0); /* no informative technique */

				/* set EPB on */
				if ((parameters.getJpwlHprotMH() > 0) || (parameters.getJpwlHprotTPH()[0] > 0)) {
					codingParameters.setEpbOn(1);

					codingParameters.setHprotMH(parameters.getJpwlHprotMH());
					for (i = 0; i < OpenJpegConstant.JPWL_MAX_NO_TILESPECS; i++) {
						codingParameters.getHprotTPHTileNo()[i] = parameters.getJpwlHprotTPHTileNo()[i];
						codingParameters.getHprotTPH()[i] = parameters.getJpwlHprotTPH()[i];
					}
					/* if tile specs are not specified, copy MH specs */
					if (codingParameters.getHprotTPH()[0] == -1) {
						codingParameters.getHprotTPHTileNo()[0] = 0;
						codingParameters.getHprotTPH()[0] = parameters.getJpwlHprotMH();
					}
					for (i = 0; i < OpenJpegConstant.JPWL_MAX_NO_PACKSPECS; i++) {
						codingParameters.getPprotTileNo()[i] = parameters.getJpwlPprotTileNo()[i];
						codingParameters.getPprotPacketNo()[i] = parameters.getJpwlPprotPacketNo()[i];
						codingParameters.getPprot()[i] = parameters.getJpwlPprot()[i];
					}
				}

				/* set ESD writing */
				if ((parameters.getJpwlSensSize() == 1) || (parameters.getJpwlSensSize() == 2)) {
					codingParameters.setEsdOn(1);

					codingParameters.setSensSize(parameters.getJpwlSensSize());
					codingParameters.setSensAddr(parameters.getJpwlSensAddr());
					codingParameters.setSensRange(parameters.getJpwlSensRange());

					codingParameters.setSensMH(parameters.getJpwlSensMH());
					for (i = 0; i < OpenJpegConstant.JPWL_MAX_NO_TILESPECS; i++) {
						codingParameters.getSensTPHTileNo()[i] = parameters.getJpwlSensTPHTileNo()[i];
						codingParameters.getSensTPH()[i] = parameters.getJpwlSensTPH()[i];
					}
				}

				/* always set RED writing to false: we are at the encoder */
				codingParameters.setRedOn(0);

			} else {
				codingParameters.setEpcOn(0);
			}
		}

		/* initialize the mutiple tiles */
		/* ---------------------------- */
		codingParameters.setTcps(new Tcp[codingParameters.getTileWidth() * codingParameters.getTileHeight()]);

		for (tileNo = 0; tileNo < codingParameters.getTileWidth() * codingParameters.getTileHeight(); tileNo++) {
			Tcp tcp = codingParameters.getTcps()[tileNo];
			tcp.setNoOfLayers(parameters.getTcpNoOfLayers());
			for (j = 0; j < tcp.getNoOfLayers(); j++) {
				if (codingParameters.getCinemaMode().value() != 0) {
					if (codingParameters.getFixedQuality() != 0) {
						tcp.getDistortionRatio()[j] = parameters.getTcpDistortionRatio()[j];
					}
					tcp.getRates()[j] = parameters.getTcpRates()[j];
				} else {
					if (codingParameters.getFixedQuality() != 0) { /* add fixed_quality */
						tcp.getDistortionRatio()[j] = parameters.getTcpDistortionRatio()[j];
					} else {
						tcp.getRates()[j] = parameters.getTcpRates()[j];
					}
				}
			}
			tcp.setCodingStyle(parameters.getCodingStyle());
			tcp.setProgressionOrder(parameters.getProgressionOrder());
			tcp.setMct(parameters.getTcpMct());

			noOfPocsInTile = 0;
			tcp.setIsPoc(0);
			if (parameters.getNoOfPocs() != 0) {
				/* initialisation of POC */
				tcp.setIsPoc(1);
				for (i = 0; i < parameters.getNoOfPocs(); i++) {
					if ((tileNo == parameters.getPocs()[i].getTile() - 1)
							|| (parameters.getPocs()[i].getTile() == -1)) {
						Poc tcpPoc = tcp.getPocs()[noOfPocsInTile];
						tcpPoc.setResNo0(parameters.getPocs()[noOfPocsInTile].getResNo0());
						tcpPoc.setCompNo0(parameters.getPocs()[noOfPocsInTile].getCompNo0());
						tcpPoc.setLayNo1(parameters.getPocs()[noOfPocsInTile].getLayNo1());
						tcpPoc.setResNo1(parameters.getPocs()[noOfPocsInTile].getResNo1());
						tcpPoc.setCompNo1(parameters.getPocs()[noOfPocsInTile].getCompNo1());
						tcpPoc.setProgressionOrder1(parameters.getPocs()[noOfPocsInTile].getProgressionOrder1());
						tcpPoc.setTile(parameters.getPocs()[noOfPocsInTile].getTile());
						noOfPocsInTile++;
					}
				}
				tcp.setNoOfPocs(noOfPocsInTile - 1);
			} else {
				tcp.setNoOfPocs(0);
			}

			tcp.setTccps(new TileComponentCodingParameters[image.getNoOfComps()]);

			for (i = 0; i < image.getNoOfComps(); i++) {
				tcp.getTccps()[i] = new TileComponentCodingParameters();
				TileComponentCodingParameters tccp = tcp.getTccps()[i];
				tccp.setCodingStyle(parameters.getCodingStyle() & 0x01); /* 0 => one precinct || 1 => custom precinct */
				tccp.setNoOfResolutions(parameters.getNoOfResolution());
				tccp.setCodeBlockWidth(MathUtil.getInstance().intFloorLog2(parameters.getCodeBlockWidthInit()));
				tccp.setCodeBlockHeight(MathUtil.getInstance().intFloorLog2(parameters.getCodeBlockHeightInit()));
				tccp.setCodeBlockStyle(parameters.getMode());
				tccp.setQmfbid(parameters.getIrreversible() != 0 ? 0 : 1);
				tccp.setQuantisationStyle(parameters.getIrreversible() != 0 ? OpenJpegConstant.J2K_CCP_QNTSTY_SEQNT
						: OpenJpegConstant.J2K_CCP_QNTSTY_NOQNT);
				tccp.setNoOfGaurdBits(2);
				if (i == parameters.getRoiCompNo()) {
					tccp.setRoiShift(parameters.getRoiShift());
				} else {
					tccp.setRoiShift(0);
				}

				if (parameters.getCpCinemaMode().value() != 0) {
					// Precinct size for lowest frequency subband=128
					tccp.getPrecinctWidth()[0] = 7;
					tccp.getPrecinctHeight()[0] = 7;
					// Precinct size at all other resolutions = 256
					for (j = 1; j < tccp.getNoOfResolutions(); j++) {
						tccp.getPrecinctWidth()[j] = 8;
						tccp.getPrecinctHeight()[j] = 8;
					}
				} else {
					if ((parameters.getCodingStyle() & OpenJpegConstant.J2K_CCP_CSTY_PRT) != 0) {
						int p = 0;
						for (j = tccp.getNoOfResolutions() - 1; j >= 0; j--) {
							if (p < parameters.getResSpec()) {

								if (parameters.getPrecinctWidthInit()[p] < 1) {
									tccp.getPrecinctWidth()[j] = 1;
								} else {
									tccp.getPrecinctWidth()[j] = MathUtil.getInstance()
											.intFloorLog2(parameters.getPrecinctWidthInit()[p]);
								}

								if (parameters.getPrecinctWidthInit()[p] < 1) {
									tccp.getPrecinctHeight()[j] = 1;
								} else {
									tccp.getPrecinctHeight()[j] = MathUtil.getInstance()
											.intFloorLog2(parameters.getPrecinctWidthInit()[p]);
								}

							} else {
								int resSpec = parameters.getResSpec();
								int sizePrcWidth = parameters.getPrecinctWidthInit()[resSpec - 1] >> (p
										- (resSpec - 1));
								int sizePrcHeight = parameters.getPrecinctWidthInit()[resSpec - 1] >> (p
										- (resSpec - 1));

								if (sizePrcWidth < 1) {
									tccp.getPrecinctWidth()[j] = 1;
								} else {
									tccp.getPrecinctWidth()[j] = MathUtil.getInstance().intFloorLog2(sizePrcWidth);
								}

								if (sizePrcHeight < 1) {
									tccp.getPrecinctHeight()[j] = 1;
								} else {
									tccp.getPrecinctHeight()[j] = MathUtil.getInstance().intFloorLog2(sizePrcHeight);
								}
							}
							p++;
						} // end for
					} else {
						for (j = 0; j < tccp.getNoOfResolutions(); j++) {
							tccp.getPrecinctWidth()[j] = 15;
							tccp.getPrecinctHeight()[j] = 15;
						}
					}
				}

				DwtHelper.getInstance().dwtCalcExplicitStepSizes(tccp, image.getComps()[i].getPrec());
			}
		}
	}

	@SuppressWarnings({ "java:S125", "java:S1659", "java:S3776", "java:S6541" })
	public int j2kEncode(J2K j2kInfo, Cio cio, OpenJpegImage image, CodeStreamInfo codeStreamInfo, boolean useJPWL) {
		int tileNo, compNo;
		CodingParameters codingParameters = null;

		Tcd tcd = null; /* TCD component */

		j2kInfo.setCio(cio);
		j2kInfo.setImage(image);

		codingParameters = j2kInfo.getCodingParameters();

		/* INDEX >> */
		j2kInfo.setCodeStreamInfo(codeStreamInfo);
		if (codeStreamInfo != null) {
			codeStreamInfo
					.setTileInfo(new TileInfo[codingParameters.getTileWidth() * codingParameters.getTileHeight()]);
			codeStreamInfo.setImageWidth(image.getX1() - image.getX0());
			codeStreamInfo.setImageHeight(image.getY1() - image.getY0());
			codeStreamInfo.setProgOrder(codingParameters.getTcps()[0].getProgressionOrder());
			codeStreamInfo.setTileWidth(codingParameters.getTileWidth());
			codeStreamInfo.setTileHeight(codingParameters.getTileHeight());
			codeStreamInfo.setTileX(codingParameters.getTileDX());
			codeStreamInfo.setTileY(codingParameters.getTileDY());
			codeStreamInfo.setTileOX(codingParameters.getTileX0());
			codeStreamInfo.setTileOY(codingParameters.getTileY0());
			codeStreamInfo.setNoOfComps(image.getNoOfComps());
			codeStreamInfo.setNoOfLayers(codingParameters.getTcps()[0].getNoOfLayers());
			codeStreamInfo.setNoOfDecompositionComps(new int[image.getNoOfComps()]);
			for (compNo = 0; compNo < image.getNoOfComps(); compNo++) {
				codeStreamInfo.getNoOfDecompositionComps()[compNo] = codingParameters.getTcps()[0].getTccps()[0]
						.getNoOfResolutions() - 1;
			}
			codeStreamInfo.setDistortionMax(0.0);
			codeStreamInfo.setMainHeadStart(CioHelper.getInstance().cioTell(cio)); /* position of SOC */
			codeStreamInfo.setMaxMarkerNo(100);
			codeStreamInfo.setMarkers(new MarkerInfo[codeStreamInfo.getMaxMarkerNo()]);
			codeStreamInfo.setMarkerNo(0);
		}
		/* << INDEX */

		j2kWriteSoc(j2kInfo, useJPWL);
		j2kWriteSize(j2kInfo);
		j2kWriteCod(j2kInfo);
		j2kWriteQcd(j2kInfo);

		if (codingParameters.getCinemaMode().value() != 0) {
			for (compNo = 1; compNo < image.getNoOfComps(); compNo++) {
				j2kWriteCoc(j2kInfo, compNo);
				j2kWriteQcc(j2kInfo, compNo);
			}
		}

		for (compNo = 0; compNo < image.getNoOfComps(); compNo++) {
			Tcp tcp = codingParameters.getTcps()[0];
			if (tcp.getTccps()[compNo].getRoiShift() != 0)
				j2kWriteRgn(j2kInfo, compNo, 0);
		}
		if (codingParameters.getComment() != null) {
			j2kWriteCom(j2kInfo);
		}

		j2kInfo.setTotalNoOfTilePart(j2kCalculateTilePart(codingParameters, image.getNoOfComps(), image, j2kInfo));
		/* TLM Marker */
		if (codingParameters.getCinemaMode().value() != 0) {
			j2kWriteTlm(j2kInfo);
			if (codingParameters.getCinemaMode() == JP2CinemeaMode.CINEMA4K_24) {
				j2kWritePoc(j2kInfo);
			}
		}

		/* uncomment only for testing JPSEC marker writing */
		/* j2kWriteSec(j2kInfo); */

		/* INDEX >> */
		if (codeStreamInfo != null) {
			codeStreamInfo.setMainHeadEnd(CioHelper.getInstance().cioTell(cio) - 1);
		}
		/* << INDEX */
		/**** Main Header ENDS here ***/

		/* create the tile encoder */
		tcd = TcdHelper.getInstance().tcdCreate(j2kInfo.getCodecContextInfo());

		/* encode each tile */
		for (tileNo = 0; tileNo < codingParameters.getTileWidth() * codingParameters.getTileHeight(); tileNo++) {
			int piNo;
			int tilePartNo = 0;
			/* UniPG>> */
			int accPackNo = 0;
			/* <<UniPG */

			Tcp tcp = codingParameters.getTcps()[tileNo];

			j2kInfo.setCurTileNo(tileNo);
			j2kInfo.setCurTilePartNo(0);
			tcd.setCurTotalNoOfTileParts(j2kInfo.getCurTotalNoOfTilePart()[j2kInfo.getCurTileNo()]);
			/* initialisation before tile encoding */
			if (tileNo == 0) {
				TcdHelper.getInstance().tcdMallocEncode(tcd, image, codingParameters, j2kInfo.getCurTileNo());
			} else {
				TcdHelper.getInstance().tcdInitEncode(tcd, image, codingParameters, j2kInfo.getCurTileNo());
			}

			/* INDEX >> */
			if (codeStreamInfo != null) {
				codeStreamInfo.getTileInfo()[j2kInfo.getCurTileNo()]
						.setStartPosition(CioHelper.getInstance().cioTell(cio) + j2kInfo.getPosCorrection());
			}
			/* << INDEX */

			for (piNo = 0; piNo <= tcp.getNoOfPocs(); piNo++) {
				int totalNoOfTilePart;
				tcd.setCurPiNo(piNo);

				/* Get number of tile parts */
				totalNoOfTilePart = j2kGetNoOfTilePart(codingParameters, piNo, tileNo);
				tcd.setTilePartPosition(codingParameters.getTilePartPosition());

				for (tilePartNo = 0; tilePartNo < totalNoOfTilePart; tilePartNo++) {
					j2kInfo.setTilePartNo(tilePartNo);
					/* INDEX >> */
					if (codeStreamInfo != null)
						codeStreamInfo.getTileInfo()[j2kInfo.getCurTileNo()].getTp()[j2kInfo.getCurTilePartNo()]
								.setTpStartPosition(CioHelper.getInstance().cioTell(cio) + j2kInfo.getPosCorrection());
					/* << INDEX */
					j2kWriteSot(j2kInfo, useJPWL);

					if (j2kInfo.getCurTilePartNo() == 0 && codingParameters.getCinemaMode().value() == 0) {
						for (compNo = 1; compNo < image.getNoOfComps(); compNo++) {
							j2kWriteCoc(j2kInfo, compNo);
							j2kWriteQcc(j2kInfo, compNo);
						}
						if (codingParameters.getTcps()[tileNo].getNoOfPocs() != 0) {
							j2kWritePoc(j2kInfo);
						}
					}

					/* INDEX >> */
					if (codeStreamInfo != null)
						codeStreamInfo.getTileInfo()[j2kInfo.getCurTileNo()].getTp()[j2kInfo.getCurTilePartNo()]
								.setTpEndHeader(CioHelper.getInstance().cioTell(cio) + j2kInfo.getPosCorrection() + 1);
					/* << INDEX */

					j2kWriteSod(j2kInfo, tcd, useJPWL);

					/* INDEX >> */
					if (codeStreamInfo != null) {
						codeStreamInfo.getTileInfo()[j2kInfo.getCurTileNo()].getTp()[j2kInfo.getCurTilePartNo()]
								.setTpEndPosition(
										CioHelper.getInstance().cioTell(cio) + j2kInfo.getPosCorrection() - 1);
						codeStreamInfo.getTileInfo()[j2kInfo.getCurTileNo()].getTp()[j2kInfo.getCurTilePartNo()]
								.setTpStartPacket(accPackNo);
						codeStreamInfo.getTileInfo()[j2kInfo.getCurTileNo()].getTp()[j2kInfo.getCurTilePartNo()]
								.setTpNoOfPackets(codeStreamInfo.getPacketNo() - accPackNo);
						accPackNo = codeStreamInfo.getPacketNo();
					}
					/* << INDEX */

					j2kInfo.setCurTilePartNo(j2kInfo.getCurTilePartNo() + 1);
				}
			}
			if (codeStreamInfo != null) {
				codeStreamInfo.getTileInfo()[j2kInfo.getCurTileNo()]
						.setEndPosition(CioHelper.getInstance().cioTell(cio) + j2kInfo.getPosCorrection() - 1);
			}
		}

		/* destroy the tile encoder */
		TcdHelper.getInstance().tcdFreeEncode(tcd);
		TcdHelper.getInstance().tcdDestroy(tcd);

		j2kInfo.setCurTotalNoOfTilePart(null);

		j2kWriteEoc(j2kInfo, useJPWL);

		if (codeStreamInfo != null) {
			codeStreamInfo.setCodeStreamSize(CioHelper.getInstance().cioTell(cio) + j2kInfo.getPosCorrection());
			/* UniPG>> */
			/* The following adjustment is done to adjust the codestream size */
			/* if SOD is not at 0 in the buffer. Useful in case of JP2, where */
			/* the first bunch of bytes is not in the codestream */
			codeStreamInfo.setCodeStreamSize(codeStreamInfo.getCodeStreamSize() - codeStreamInfo.getMainHeadStart());
			/* <<UniPG */
		}

		/*
		 * preparation of JPWL marker segments
		 */
		if (useJPWL && codingParameters.getEpcOn() != 0) {
			/* encode according to JPWL */
			jpwlEncode(j2kInfo, cio, image);
		}

		return 1;
	}

	@SuppressWarnings({ "java:S1186" })
	private void jpwlEncode(J2K j2kInfo, Cio cio2, OpenJpegImage image2) {
	}
}