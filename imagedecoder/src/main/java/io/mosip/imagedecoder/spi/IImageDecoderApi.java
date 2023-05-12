package io.mosip.imagedecoder.spi;

import io.mosip.imagedecoder.model.DecoderRequestInfo;
import io.mosip.imagedecoder.model.DecoderResponseInfo;
import io.mosip.imagedecoder.model.Response;

/**
 * The Interface IImageDecoderApi.
 * 
 * @author Janardhan B S
 * 
 */
public interface IImageDecoderApi {
	public Response<DecoderResponseInfo> decode(DecoderRequestInfo requestInfo);
}
