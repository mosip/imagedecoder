package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import lombok.Data;
import lombok.ToString;

/**
 * MQ coder
 */
@Data
@ToString
public class MQCoder {
	private long c;
	private long a;
	private long ct;
	private byte[] bp;
	private int bpIndex;
	private int start;
	private int end;
	private int contextIndex;
	private MQCoderState[] contexts = new MQCoderState[OpenJpegConstant.MQC_NUMCTXS];
	private MQCoderState currentContext;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MQCoder))
			return false;
		MQCoder that = (MQCoder) obj;
		return canEqual(that) && c == that.c && a == that.a && ct == that.ct && bpIndex == that.bpIndex
				&& start == that.start && end == that.end && contextIndex == that.contextIndex
				&& Arrays.equals(bp, that.bp) && Arrays.equals(contexts, that.contexts)
				&& Objects.equals(currentContext, that.currentContext);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(c, a, ct, bpIndex, start, end, contextIndex, currentContext);
		result = 31 * result + Arrays.hashCode(bp);
		result = 31 * result + Arrays.hashCode(contexts);
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof MQCoder;
	}
}