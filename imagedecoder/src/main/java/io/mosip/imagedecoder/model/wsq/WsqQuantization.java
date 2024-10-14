package io.mosip.imagedecoder.model.wsq;

import java.util.Arrays;
import java.util.Objects;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WsqQuantization {
	private float quantizationLevel; /* quantization level */
	private float compressionRatio; /* compression ratio */
	private float compressionBitRate; /* compression bitrate */
	private float[] qbssT = new float[WsqConstant.MAX_SUBBANDS];
	private float[] qbss = new float[WsqConstant.MAX_SUBBANDS];
	private float[] qzbs = new float[WsqConstant.MAX_SUBBANDS];
	@SuppressWarnings({ "java:S6213" })
	private float[] var = new float[WsqConstant.MAX_SUBBANDS];

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WsqQuantization))
			return false;
		WsqQuantization that = (WsqQuantization) obj;
		return Float.compare(that.quantizationLevel, quantizationLevel) == 0
				&& Float.compare(that.compressionRatio, compressionRatio) == 0
				&& Float.compare(that.compressionBitRate, compressionBitRate) == 0 && Arrays.equals(qbssT, that.qbssT)
				&& Arrays.equals(qbss, that.qbss) && Arrays.equals(qzbs, that.qzbs) && Arrays.equals(var, that.var);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(quantizationLevel, compressionRatio, compressionBitRate);
		result = 31 * result + Arrays.hashCode(qbssT);
		result = 31 * result + Arrays.hashCode(qbss);
		result = 31 * result + Arrays.hashCode(qzbs);
		result = 31 * result + Arrays.hashCode(var);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof WsqQuantization;
	}
}