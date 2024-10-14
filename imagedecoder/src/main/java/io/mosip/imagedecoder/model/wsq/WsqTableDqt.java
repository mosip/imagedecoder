package io.mosip.imagedecoder.model.wsq;

import java.util.Arrays;
import java.util.Objects;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WsqTableDqt {
	private float binCenter;
	private float[] qBin = new float[WsqConstant.MAX_SUBBANDS];
	private float[] zBin = new float[WsqConstant.MAX_SUBBANDS];
	private int dqtDef;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WsqTableDqt))
			return false;
		WsqTableDqt that = (WsqTableDqt) obj;
		return Float.compare(that.binCenter, binCenter) == 0 && dqtDef == that.dqtDef && Arrays.equals(qBin, that.qBin)
				&& Arrays.equals(zBin, that.zBin);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(binCenter, dqtDef);
		result = 31 * result + Arrays.hashCode(qBin);
		result = 31 * result + Arrays.hashCode(zBin);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof WsqTableDqt;
	}
}