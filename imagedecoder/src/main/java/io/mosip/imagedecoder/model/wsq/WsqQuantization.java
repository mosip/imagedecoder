package io.mosip.imagedecoder.model.wsq;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class WsqQuantization {
	private float quantizationlevel;  /* quantization level */
	private float compressionratio; /* compression ratio */
	private float compressionBitRate;  /* compression bitrate */
	private float[] qbssT = new float[WsqConstant.MAX_SUBBANDS];
	private float[] qbss = new float[WsqConstant.MAX_SUBBANDS];
	private float[] qzbs = new float[WsqConstant.MAX_SUBBANDS];
	@SuppressWarnings({ "java:S6213" })
	private float[] var = new float[WsqConstant.MAX_SUBBANDS];
}
