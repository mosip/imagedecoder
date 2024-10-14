package io.mosip.imagedecoder.model.wsq;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WsqTableDtt {
	private float[] lowFilter;
	private float[] highFilter;
	private int lowSize;
	private int highSize;
	private int lowDef;
	private int highDef;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WsqTableDtt))
			return false;
		WsqTableDtt that = (WsqTableDtt) obj;
		return lowSize == that.lowSize && highSize == that.highSize && lowDef == that.lowDef && highDef == that.highDef
				&& Arrays.equals(lowFilter, that.lowFilter) && Arrays.equals(highFilter, that.highFilter);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(lowSize, highSize, lowDef, highDef);
		result = 31 * result + Arrays.hashCode(lowFilter);
		result = 31 * result + Arrays.hashCode(highFilter);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof WsqTableDtt;
	}
}