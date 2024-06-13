package io.mosip.imagedecoder.wsq;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;

import java.awt.image.BufferedImage;

import io.mosip.imagedecoder.constant.DecoderConstant;
import io.mosip.imagedecoder.constant.DecoderErrorCodes;
import io.mosip.imagedecoder.constant.wsq.WsqErrorCode;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;
import io.mosip.imagedecoder.model.DecoderRequestInfo;
import io.mosip.imagedecoder.model.DecoderResponseInfo;
import io.mosip.imagedecoder.model.Response;
import io.mosip.imagedecoder.model.wsq.WsqInfo;
import io.mosip.imagedecoder.spi.IImageDecoderApi;
import io.mosip.imagedecoder.util.Base64UrlUtil;
import io.mosip.imagedecoder.util.openjpeg.ImageUtil;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class WSQDecoder does the decoding of WSQ image information
 * 
 * @author Janardhan B S
 * 
 */
public class WsqDecoder implements IImageDecoderApi {
	private Logger logger = ImageDecoderLogger.getLogger(WsqDecoder.class);

	@Override
	public Response<DecoderResponseInfo> decode(DecoderRequestInfo requestInfo) {
		Response<DecoderResponseInfo> response = new Response<>();
		try {
			DecoderResponseInfo responseInfo = new DecoderResponseInfo();
			WsqInfo wsqInfo = WsqDecoderHelper.getInstance().wsqDecode(requestInfo.getImageData(),
					requestInfo.getImageData().length);

			responseInfo.setImageType(DecoderConstant.IMAGE_TYPE_WSQ);
			responseInfo.setImageWidth(wsqInfo.getWidth() + "");
			responseInfo.setImageHeight(wsqInfo.getHeight() + "");
			responseInfo.setImageLossless(wsqInfo.getLossyFlag() == 1 ? "0" : "1");
			responseInfo.setImageDepth(wsqInfo.getDepth() + "");
			responseInfo.setImageDpiHorizontal(wsqInfo.getPpi() + "");
			responseInfo.setImageDpiVertical(wsqInfo.getPpi() + "");
			responseInfo.setImageBitRate(String.format("%.2f", wsqInfo.getBitRate()) + "");
			responseInfo.setImageSize(wsqInfo.getData().length + "");
			responseInfo.setImageData(Base64UrlUtil.getInstance().encodeToURLSafeBase64(wsqInfo.getData()) + "");
			responseInfo.setImageColorSpace(wsqInfo.getColorSpace() + "");
			responseInfo.setImageAspectRatio(
					ImageUtil.getInstance().calculateAspectRatio(wsqInfo.getWidth(), wsqInfo.getHeight()) + "");

			responseInfo.setImageCompressionRatio(getCompressionRatio(requestInfo, wsqInfo));
			if (requestInfo.isBufferedImage()) {
				responseInfo.setBufferedImage(getBufferedImage(wsqInfo));
			}

			response.setResponse(responseInfo);
			response.setStatusCode(0);
			response.setStatusMessage(DecoderErrorCodes.SUCCESS.getErrorMessage());
		} catch (Exception ex) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "decode", ex);
			response.setStatusCode(WsqErrorCode.TECHNICAL_ERROR_EXCEPTION.getErrorId());
			response.setStatusMessage(ex.getLocalizedMessage());
		}
		return response;
	}

	private String getCompressionRatio(DecoderRequestInfo requestInfo, WsqInfo wsqInfo) {
		return (ImageUtil.getInstance().calculateCompressionRatio(wsqInfo.getWidth(), wsqInfo.getHeight(),
				wsqInfo.getDepth(), requestInfo.getImageData().length) + " : 1");
	}

	private BufferedImage getBufferedImage(WsqInfo wsqInfo) {
		return ImageUtil.getInstance().fromByteGray(wsqInfo.getWidth(), wsqInfo.getHeight(), wsqInfo.getData());
	}
}