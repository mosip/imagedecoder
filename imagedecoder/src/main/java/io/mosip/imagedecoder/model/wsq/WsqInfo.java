package io.mosip.imagedecoder.model.wsq;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WsqInfo {
	private byte[] data;
	private int width;
	private int height;
	private int depth;
	private int ppi;
	private int lossyFlag;
	private double bitRate; // bits per pixels
	private String colorSpace;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WsqInfo))
			return false;
		WsqInfo that = (WsqInfo) obj;
		return width == that.width && height == that.height && depth == that.depth && ppi == that.ppi
				&& lossyFlag == that.lossyFlag && Double.compare(that.bitRate, bitRate) == 0
				&& Arrays.equals(data, that.data) && Objects.equals(colorSpace, that.colorSpace);
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(data);
		result = 31 * result + width;
		result = 31 * result + height;
		result = 31 * result + depth;
		result = 31 * result + ppi;
		result = 31 * result + lossyFlag;
		result = 31 * result + Double.hashCode(bitRate);
		result = 31 * result + (colorSpace != null ? colorSpace.hashCode() : 0);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof WsqInfo;
	}
}