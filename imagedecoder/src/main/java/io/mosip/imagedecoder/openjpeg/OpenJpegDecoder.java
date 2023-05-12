package io.mosip.imagedecoder.openjpeg;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.imagedecoder.constant.DecoderConstant;
import io.mosip.imagedecoder.constant.DecoderErrorCodes;
import io.mosip.imagedecoder.model.DecoderRequestInfo;
import io.mosip.imagedecoder.model.DecoderResponseInfo;
import io.mosip.imagedecoder.model.Response;
import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.CodecContextInfo;
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
	private Logger LOGGER = LoggerFactory.getLogger(OpenJpegDecoder.class);
	private OpenJpegHelper decoder = new OpenJpegHelper();

	@Override
	public Response<DecoderResponseInfo> decode(DecoderRequestInfo requestInfo) {
		Response<DecoderResponseInfo> response = new Response<DecoderResponseInfo>();
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
				boolean USE_JPWL = false;
				DecompressionParameters parameters = new DecompressionParameters();
				decoder.setDefaultDecoderParameters(parameters, USE_JPWL);
				decoder.setupDecoder(dInfo, parameters, USE_JPWL);
				cio = CioHelper.getInstance().cioOpen((CodecContextInfo) dInfo, requestInfo.getImageData(), requestInfo.getImageData().length);
				if (cio != null) {
					image = decoder.decode(dInfo, cio, USE_JPWL);
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
						responseInfo.setImageType(DecoderConstant.IMAGE_TYPE_JP2000);
						responseInfo.setImageWidth(width + "");
						responseInfo.setImageHeight(height + "");
						
						if (components > 0 && components < 4) {
							responseInfo.setImageLossless(transform == 1 ? "1" : "0");
							if (resolutionBox != null) 
							{
								responseInfo.setImageDpiHorizontal(resolutionBox.getHorizontalResolution() + "");
								responseInfo.setImageDpiVertical(resolutionBox.getVerticalResolution() + "");
							} 
							else {
								responseInfo.setImageDpiHorizontal(-1 + "");
								responseInfo.setImageDpiVertical(-1 + "");
							} 
							if (image.getColorSpace() == Jp2ColorSpace.CLRSPC_GRAY) 
							{ 
								responseInfo.setImageColorSpace("GRAY" + "");			
								responseInfo.setImageDepth((8 * components) + "");
							}
							else if (image.getColorSpace() == Jp2ColorSpace.CLRSPC_SRGB) 
							{ 
								responseInfo.setImageColorSpace("RGB" + "");			
								responseInfo.setImageDepth((8 * components) + "");
							}
							responseInfo.setImageSize(totalSizeWithComp + "");
							responseInfo.setImageData(Base64UrlUtil.encodeToURLSafeBase64(ImageUtil.integersToBytes (outImage)) + "");
							responseInfo.setImageAspectRatio(ImageUtil.calculateAspectRatio(width, height) + "");
							responseInfo.setImageCompressionRatio(ImageUtil.calculateCompressionRatio(width, height, components, requestInfo.getImageData().length) + " : 1");
						}
			  
						if (requestInfo.isBufferedImage())
						{
							BufferedImage bufferedImage = null;
							try
							{
								 BufferedImage bi = null; 
								 try 
								 {
									 if (image.getColorSpace() == Jp2ColorSpace.CLRSPC_GRAY) 
									 { 
										 bufferedImage = ImageUtil.fromByteGray(width, height, ImageUtil.integersToBytes (outImage)); 
									 }
								 	else if (image.getColorSpace() == Jp2ColorSpace.CLRSPC_SRGB) 
								 	{ 
								 		bufferedImage = ImageUtil.fromJ2kImage(width, height, image); 
								 	}
								 }
						 		catch(Exception ex) { 
						 			ex.printStackTrace(); 
					 			} 
								responseInfo.setBufferedImage(bufferedImage);
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
								responseInfo.setBufferedImage(null);
							}
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
			LOGGER.error("decode ", ex);
			response.setStatusCode(-1);
			response.setStatusMessage(ex.getLocalizedMessage());
		}
		finally
		{
			ImageHelper.getInstance().imageDestroy(image);
			CioHelper.getInstance().cioClose(cio);
			decoder.destroyDecompression(dInfo);
		}
		return response;
	}
}
