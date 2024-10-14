package io.mosip.imagedecoder.openjpeg;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;

import java.awt.image.BufferedImage;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;

import io.mosip.imagedecoder.constant.DecoderConstant;
import io.mosip.imagedecoder.constant.DecoderErrorCodes;
import io.mosip.imagedecoder.model.DecoderRequestInfo;
import io.mosip.imagedecoder.model.DecoderResponseInfo;
import io.mosip.imagedecoder.model.Response;
import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.DecompressionContextInfo;
import io.mosip.imagedecoder.model.openjpeg.DecompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.JP2CodecFormat;
import io.mosip.imagedecoder.model.openjpeg.JP2ResolutionBox;
import io.mosip.imagedecoder.model.openjpeg.Jp2ColorSpace;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.spi.IImageDecoderApi;
import io.mosip.imagedecoder.util.Base64UrlUtil;
import io.mosip.imagedecoder.util.openjpeg.ImageUtil;

/**
 * The Class JPEG2000Decoder does the decoding of JPEG2000 image information
 * 
 * @author Janardhan B S
 * 
 */
public class OpenJpegDecoder implements IImageDecoderApi {
	private Logger logger = ImageDecoderLogger.getLogger(OpenJpegDecoder.class);
	private OpenJpegHelper decoder = new OpenJpegHelper();

	@Override
	@SuppressWarnings({ "java:S1659", "java:S3776", "java:S6541" })
	public Response<DecoderResponseInfo> decode(DecoderRequestInfo requestInfo) {
		Response<DecoderResponseInfo> response = new Response<>();
		OpenJpegImage image = null;
		Cio cio = null;
		DecompressionContextInfo dInfo = null;
		DecoderResponseInfo responseInfo = new DecoderResponseInfo();
		try {
			int width = 0;
			int height = 0;
			int components = 0;
			int totalSize = 0;
			int transform = 0;
			int totalSizeWithComp = 0;
			int[] outImage = null;
			JP2ResolutionBox resolutionBox = null;
			dInfo = decoder.createDecompression(JP2CodecFormat.CODEC_JP2);
			if (dInfo != null) {
				boolean useJPWL = false;
				DecompressionParameters parameters = new DecompressionParameters();
				decoder.setDefaultDecoderParameters(parameters, useJPWL);
				decoder.setupDecoder(dInfo, parameters, useJPWL);
				cio = CioHelper.getInstance().cioOpen(dInfo, requestInfo.getImageData(),
						requestInfo.getImageData().length);
				if (cio != null) {
					image = decoder.decode(dInfo, cio, useJPWL);
					if (image != null) {
						width = image.getX1() - image.getX0();
						height = image.getY1() - image.getY0();
						components = image.getNoOfComps();
						totalSize = width * height;
						outImage = new int[totalSize * components];
						totalSizeWithComp = components * totalSize;
						transform = image.getQmfbid();
						resolutionBox = image.getResolutionBox();

						int nIndex = 0;
						for (int i = 0; i < components; i++) {
							if (image.getComps()[i] != null && image.getComps()[i].getData() != null)
								System.arraycopy(image.getComps()[i].getData(), 0, outImage, nIndex, totalSize);
							nIndex += totalSize;
						}
						responseInfo.setAllInfo(requestInfo.isAllInfo());
						responseInfo.setImageType(DecoderConstant.IMAGE_TYPE_JP2000);
						responseInfo.setImageWidth(width + "");
						responseInfo.setImageHeight(height + "");

						if (requestInfo.isAllInfo()) {
							if (components > 0 && components < 4) {
								responseInfo.setImageLossless(transform == 1 ? "1" : "0");
								if (resolutionBox != null) {
									responseInfo.setImageDpiHorizontal(resolutionBox.getHorizontalResolution() + "");
									responseInfo.setImageDpiVertical(resolutionBox.getVerticalResolution() + "");
								} else {
									responseInfo.setImageDpiHorizontal(-1 + "");
									responseInfo.setImageDpiVertical(-1 + "");
								}
								if (image.getColorSpace() == Jp2ColorSpace.CLRSPC_GRAY) {
									responseInfo.setImageColorSpace("GRAY" + "");
									responseInfo.setImageDepth((8 * components) + "");
								} else if (image.getColorSpace() == Jp2ColorSpace.CLRSPC_SRGB) {
									responseInfo.setImageColorSpace("RGB" + "");
									responseInfo.setImageDepth((8 * components) + "");
								}
								responseInfo.setImageSize(totalSizeWithComp + "");
								responseInfo.setImageData(Base64UrlUtil.getInstance()
										.encodeToURLSafeBase64(ImageUtil.getInstance().integersToBytes(outImage)) + "");
								responseInfo.setImageAspectRatio(
										ImageUtil.getInstance().calculateAspectRatio(width, height) + "");
								responseInfo.setImageCompressionRatio(ImageUtil.getInstance().calculateCompressionRatio(
										width, height, components, requestInfo.getImageData().length) + " : 1");
							}
						}

						if (requestInfo.isBufferedImage()) {
							responseInfo.setBufferedImage(getBufferedImage(image, outImage, width, height));
						}
						response.setResponse(responseInfo);
						response.setStatusCode(0);
						response.setStatusMessage(DecoderErrorCodes.SUCCESS.getErrorMessage());
						return response;
					}
				}
			}

			response.setResponse(null);
			response.setStatusCode(-500);
			response.setStatusMessage(DecoderErrorCodes.TECHNICAL_ERROR_EXCEPTION.getErrorMessage());
		} catch (Exception ex) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "decode", ex);
			response.setStatusCode(-1);
			response.setStatusMessage(ex.getLocalizedMessage());
		} finally {
			ImageHelper.getInstance().imageDestroy(image);
			CioHelper.getInstance().cioClose(cio);
			decoder.destroyDecompression(dInfo);
		}
		return response;
	}

	private BufferedImage getBufferedImage(OpenJpegImage image, int[] outImage, int width, int height) {
		BufferedImage bufferedImage = null;
		try {
			if (image.getColorSpace() == Jp2ColorSpace.CLRSPC_GRAY) {
				bufferedImage = ImageUtil.getInstance().fromByteGray(width, height,
						ImageUtil.getInstance().integersToBytes(outImage));
			} else if (image.getColorSpace() == Jp2ColorSpace.CLRSPC_SRGB) {
				bufferedImage = ImageUtil.getInstance().fromJ2kImage(width, height, image);
			}
		} catch (Exception ex) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "decode", ex);
		}
		return bufferedImage;
	}
}