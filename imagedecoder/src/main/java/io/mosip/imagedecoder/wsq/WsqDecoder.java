package io.mosip.imagedecoder.wsq;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.imagedecoder.constant.DecoderConstant;
import io.mosip.imagedecoder.constant.wsq.WsqErrorCode;
import io.mosip.imagedecoder.model.DecoderRequestInfo;
import io.mosip.imagedecoder.model.DecoderResponseInfo;
import io.mosip.imagedecoder.model.Response;
import io.mosip.imagedecoder.model.wsq.WsqInfo;
import io.mosip.imagedecoder.spi.IImageDecoderApi;
import io.mosip.imagedecoder.util.Base64UrlUtil;
import io.mosip.imagedecoder.util.openjpeg.ImageUtil;

/**
 * The Class WSQDecoder does the decoding of WSQ image information
 * 
 * @author Janardhan B S
 * 
 */
public class WsqDecoder implements IImageDecoderApi {
	Logger LOGGER = LoggerFactory.getLogger(WsqDecoder.class);

	@Override
	public Response<DecoderResponseInfo> decode(DecoderRequestInfo requestInfo) {
		Response<DecoderResponseInfo> response = new Response<DecoderResponseInfo>();
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
			responseInfo.setImageData(Base64UrlUtil.encodeToURLSafeBase64(wsqInfo.getData()) + "");
			responseInfo.setImageColorSpace(wsqInfo.getColorSpace() + "");
			responseInfo
					.setImageAspectRatio(ImageUtil.calculateAspectRatio(wsqInfo.getWidth(), wsqInfo.getHeight()) + "");
			String compressionRatio = "";
			try {
				compressionRatio = ((int) ImageUtil.calculateCompressionRatio(wsqInfo.getWidth(), wsqInfo.getHeight(),
						wsqInfo.getDepth(), requestInfo.getImageData().length) + " : 1");
			} catch (Exception ex) {
			}

			responseInfo.setImageCompressionRatio(compressionRatio);
			if (requestInfo.isBufferedImage())
			{
				BufferedImage image = null;
				try
				{
					image = ImageUtil.fromByteGray(wsqInfo.getWidth(), wsqInfo.getHeight(), wsqInfo.getData());
					responseInfo.setBufferedImage(image);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					responseInfo.setBufferedImage(null);
				}
			}
			
			response.setResponse(responseInfo);
			response.setStatusCode(0);
			response.setStatusMessage("SUCCESS");
		} catch (Exception ex) {
			ex.printStackTrace();
			response.setStatusCode(WsqErrorCode.TECHNICAL_ERROR_EXCEPTION.getErrorId());
			response.setStatusMessage(ex.getLocalizedMessage());
		}
		return response;
	}
}
