package io.mosip.imagedecoder.openjpeg;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.CodeStreamInfo;
import io.mosip.imagedecoder.model.openjpeg.CompressionContextInfo;
import io.mosip.imagedecoder.model.openjpeg.CompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.DecompressionContextInfo;
import io.mosip.imagedecoder.model.openjpeg.DecompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.J2K;
import io.mosip.imagedecoder.model.openjpeg.JP2;
import io.mosip.imagedecoder.model.openjpeg.JP2CinemeaMode;
import io.mosip.imagedecoder.model.openjpeg.JP2CodecFormat;
import io.mosip.imagedecoder.model.openjpeg.LimitDecoding;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.ProgressionOrder;
import io.mosip.imagedecoder.model.openjpeg.RsizCapabilities;
import io.mosip.imagedecoder.model.openjpeg.TileInfo;

public class OpenJpegHelper {
	private Logger logger = ImageDecoderLogger.getLogger(OpenJpegHelper.class);
	private J2KHelper j2k = null;
	private JP2Helper jp2 = null;

	public OpenJpegHelper() {
		super();
		setJ2k(new J2KHelper());
		setJp2(new JP2Helper());
	}

	public J2KHelper getJ2k() {
		return j2k;
	}

	public void setJ2k(J2KHelper j2k) {
		this.j2k = j2k;
	}

	public JP2Helper getJp2() {
		return jp2;
	}

	public void setJp2(JP2Helper jp2) {
		this.jp2 = jp2;
	}

	public String version() {
		return OpenJpegConstant.OPENJPEG_VERSION;
	}

	@SuppressWarnings({ "java:S6208"})
	public DecompressionContextInfo createDecompression(JP2CodecFormat format) {
		DecompressionContextInfo decompressionContextInfo = new DecompressionContextInfo();
		decompressionContextInfo.getContextInfo().setIsDecompressor(1);
		switch (format) {
		case CODEC_J2K:
		case CODEC_JPT:
			/* get a J2K decoder handle */
			decompressionContextInfo.getContextInfo()
					.setJ2kHandle(this.getJ2k().j2kCreateDecompression(decompressionContextInfo));
			break;
		case CODEC_JP2:
			/* get a JP2 decoder handle */
			decompressionContextInfo.getContextInfo()
					.setJp2Handle(this.getJp2().jp2CreateDecompression(decompressionContextInfo));
			break;
		case CODEC_UNKNOWN:
		default:
			return null;
		}

		decompressionContextInfo.getContextInfo().setCodecFormat(format);
		return decompressionContextInfo;
	}

	public void destroyDecompression(DecompressionContextInfo decompressionContextInfo) {
		if (decompressionContextInfo != null) {
			/* destroy the codec */
			switch (decompressionContextInfo.getContextInfo().getCodecFormat()) {
			case CODEC_J2K, CODEC_JPT:
				this.getJ2k().j2kDestroyDecompression((J2K) decompressionContextInfo.getContextInfo().getJ2kHandle());
				break;
			case CODEC_JP2:
				this.getJp2().jp2DestroyDecompression((JP2) decompressionContextInfo.getContextInfo().getJp2Handle());
				break;
			case CODEC_UNKNOWN:
			default:
				break;
			}
		}
	}

	public void setDefaultDecoderParameters(DecompressionParameters parameters, boolean useJPWL) {
		if (parameters != null) {
			/* default decoding parameters */
			parameters.setCpLayer(0);
			parameters.setCpReduce(0);
			parameters.setCpLimitDecoding(LimitDecoding.NO_LIMITATION);

			parameters.setDecodeFormat(-1);
			parameters.setCodecFormat(-1);
			/* UniPG>> */
			if (useJPWL) {
				parameters.setJpwlCorrect(0);
				parameters.setJpwlExpComps(OpenJpegConstant.JPWL_EXPECTED_COMPONENTS);
				parameters.setJpwlMaxTiles(OpenJpegConstant.JPWL_MAXIMUM_TILES);
			}
			/* <<UniPG */
		}
	}

	@SuppressWarnings({ "java:S6208"})
	public void setupDecoder(DecompressionContextInfo decompressionContextInfo, DecompressionParameters parameters,
			boolean useJPWL) {
		if (decompressionContextInfo != null && parameters != null) {
			switch (decompressionContextInfo.getContextInfo().getCodecFormat()) {
			case CODEC_J2K:
			case CODEC_JPT:
				this.getJ2k().j2kSetupDecoder((J2K) decompressionContextInfo.getContextInfo().getJ2kHandle(),
						parameters, useJPWL);
				break;
			case CODEC_JP2:
				this.getJp2().jp2SetupDecoder((JP2) decompressionContextInfo.getContextInfo().getJp2Handle(),
						parameters, useJPWL);
				break;
			case CODEC_UNKNOWN:
			default:
				break;
			}
		}
	}

	public OpenJpegImage decode(DecompressionContextInfo decompressionContextInfo, Cio cio, boolean useJPWL) {
		return decodeWithInfo(decompressionContextInfo, cio, null, useJPWL);
	}

	public OpenJpegImage decodeWithInfo(DecompressionContextInfo decompressionContextInfo, Cio cio,
			CodeStreamInfo codeStreamInfo, boolean useJPWL) {
		if (decompressionContextInfo != null && cio != null) {
			switch (decompressionContextInfo.getContextInfo().getCodecFormat()) {
			case CODEC_J2K:
				return this.getJ2k().j2kDecode((J2K) decompressionContextInfo.getContextInfo().getJ2kHandle(), cio,
						codeStreamInfo, useJPWL);
			case CODEC_JPT:
				return this.getJ2k().j2kDecodeJPTStream((J2K) decompressionContextInfo.getContextInfo().getJ2kHandle(),
						cio, codeStreamInfo, useJPWL);
			case CODEC_JP2:
				return this.getJp2().jp2Decode((JP2) decompressionContextInfo.getContextInfo().getJp2Handle(), cio,
						codeStreamInfo, useJPWL);
			case CODEC_UNKNOWN:
			default:
				break;
			}
		}
		return null;
	}

	@SuppressWarnings({ "java:S6208"})
	public CompressionContextInfo createCompression(JP2CodecFormat format) {
		CompressionContextInfo compressionContextInfo = new CompressionContextInfo();
		compressionContextInfo.getContextInfo().setIsDecompressor(0);
		switch (format) {
		case CODEC_J2K:
			/* get a J2K coder handle */
			compressionContextInfo.getContextInfo()
					.setJ2kHandle(this.getJ2k().j2kCreateCompression(compressionContextInfo));
			if (compressionContextInfo.getContextInfo().getJ2kHandle() == null) {
				compressionContextInfo = null;
				return compressionContextInfo;
			}
			break;
		case CODEC_JP2:
			/* get a JP2 coder handle */
			compressionContextInfo.getContextInfo()
					.setJp2Handle(this.getJp2().jp2CreateCompression(compressionContextInfo));
			if (compressionContextInfo.getContextInfo().getJp2Handle() == null) {
				compressionContextInfo = null;
				return compressionContextInfo;
			}
			break;
		case CODEC_JPT:
		case CODEC_UNKNOWN:
		default:
			compressionContextInfo = null;
			return compressionContextInfo;
		}

		compressionContextInfo.getContextInfo().setCodecFormat(format);

		return compressionContextInfo;
	}

	@SuppressWarnings({ "java:S6208"})
	public void destroyCompression(CompressionContextInfo compressionContextInfo) {
		if (compressionContextInfo != null) {
			/* destroy the codec */
			switch (compressionContextInfo.getContextInfo().getCodecFormat()) {
			case CODEC_J2K:
				this.getJ2k().j2kDestroyCompression((J2K) compressionContextInfo.getContextInfo().getJ2kHandle());
				break;
			case CODEC_JP2:
				this.getJp2().jp2DestroyCompression((JP2) compressionContextInfo.getContextInfo().getJp2Handle());
				break;
			case CODEC_JPT:
			case CODEC_UNKNOWN:
			default:
				break;
			}
			/* destroy the compressor */
		}
	}

	public void setDefaultEncodeParameters(CompressionParameters parameters, boolean useJPWL) {
		if (parameters != null) {
			/* default coding parameters */
			parameters.setCpCinemaMode(JP2CinemeaMode.OFF);
			parameters.setMaxCompSize(0);
			parameters.setNoOfResolution(6);
			parameters.setCpRsizCap(RsizCapabilities.STD_RSIZ);
			parameters.setCodeBlockWidthInit(64);
			parameters.setCodeBlockHeightInit(64);
			parameters.setProgressionOrder(ProgressionOrder.LRCP);
			parameters.setRoiCompNo(-1); /* no ROI */
			parameters.setSubSamplingDX(1);
			parameters.setSubSamplingDY(1);
			parameters.setTpOn(0);
			parameters.setDecodeFormat(-1);
			parameters.setCodecFormat(-1);
			parameters.getTcpRates()[0] = 0;
			parameters.setTcpNoOfLayers(1);
			parameters.setCpDistortionAllocation(1);

			/* UniPG>> */
			if (useJPWL) {
				parameters.setJpwlEpcOn(0);
				parameters.setJpwlHprotMH(-1);/* -1 means unassigned */
				int i;
				for (i = 0; i < OpenJpegConstant.JPWL_MAX_NO_TILESPECS; i++) {
					parameters.getJpwlHprotTPHTileNo()[i] = -1; /* unassigned */
					parameters.getJpwlHprotTPH()[i] = 0; /* absent */
				}
				for (i = 0; i < OpenJpegConstant.JPWL_MAX_NO_PACKSPECS; i++) {
					parameters.getJpwlPprotTileNo()[i] = -1; /* unassigned */
					parameters.getJpwlPprotPacketNo()[i] = -1; /* unassigned */
					parameters.getJpwlPprot()[i] = 0; /* absent */
				}
				parameters.setJpwlSensSize(0); /* 0 means no ESD */
				parameters.setJpwlSensAddr(0); /* 0 means auto */
				parameters.setJpwlSensRange(0); /* 0 means packet */
				parameters.setJpwlSensMH(-1); /* -1 means unassigned */
				for (i = 0; i < OpenJpegConstant.JPWL_MAX_NO_TILESPECS; i++) {
					parameters.getJpwlHprotTPHTileNo()[i] = -1; /* unassigned */
					parameters.getJpwlSensTPH()[i] = -1; /* absent */
				}
			}
			/* <<UniPG */
		}
	}

	public void setupEncoder(CompressionContextInfo compressionContextInfo, CompressionParameters parameters,
			OpenJpegImage image, boolean useJPWL) {
		if (compressionContextInfo != null && parameters != null && image != null) {
			switch (compressionContextInfo.getContextInfo().getCodecFormat()) {
			case CODEC_J2K:
				this.getJ2k().j2kSetupEncoder((J2K) compressionContextInfo.getContextInfo().getJ2kHandle(), parameters,
						image, useJPWL);
				break;
			case CODEC_JP2:
				this.getJp2().jp2SetupEncoder((JP2) compressionContextInfo.getContextInfo().getJp2Handle(), parameters,
						image, useJPWL);
				break;
			case CODEC_JPT, CODEC_UNKNOWN:
			default:
				break;
			}
		}
	}

	public int encode(CompressionContextInfo compressionContextInfo, Cio cio, OpenJpegImage image, char[] index,
			boolean useJPWL) {
		if (index != null)
			logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, 
					"Set index to null when calling the encode function. To extract the index, use the encodeWithInfo() function. No index will be generated during this encoding");
		return encodeWithInfo(compressionContextInfo, cio, image, null, useJPWL);
	}

	public int encodeWithInfo(CompressionContextInfo compressionContextInfo, Cio cio, OpenJpegImage image,
			CodeStreamInfo codeStreamInfo, boolean useJPWL) {
		if (compressionContextInfo != null && cio != null && image != null) {
			switch (compressionContextInfo.getContextInfo().getCodecFormat()) {
			case CODEC_J2K:
				this.getJ2k().j2kEncode((J2K) compressionContextInfo.getContextInfo().getJ2kHandle(), cio, image,
						codeStreamInfo, useJPWL);
				break;
			case CODEC_JP2:
				this.getJp2().jp2Encode((JP2) compressionContextInfo.getContextInfo().getJp2Handle(), cio, image,
						codeStreamInfo, useJPWL);
				break;
			case CODEC_JPT, CODEC_UNKNOWN:
			default:
				break;
			}
		}
		return 0;
	}

	public void destroyCodeStreamInfo(CodeStreamInfo codeStreamInfo) {
		if (codeStreamInfo != null) {
			int tileNo;
			for (tileNo = 0; tileNo < codeStreamInfo.getTileWidth() * codeStreamInfo.getTileHeight(); tileNo++) {
				TileInfo tileInfo = codeStreamInfo.getTileInfo()[tileNo];
				tileInfo.setThresh(null);
				tileInfo.setPacket(null);
				tileInfo.setTp(null);
			}
			codeStreamInfo.setTileInfo(null);
			codeStreamInfo.setMarkers(null);
			codeStreamInfo.setNoOfDecompositionComps(null);
		}
	}
}