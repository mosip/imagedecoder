package io.mosip.imagedecoder.model.wsq;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WsqHuffCode {
	private int size;
	private long code;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WsqHuffCode))
			return false;
		WsqHuffCode that = (WsqHuffCode) obj;
		return size == that.size && code == that.code;
	}

	@Override
	public int hashCode() {
		int result = Integer.hashCode(size);
		result = 31 * result + Long.hashCode(code);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof WsqHuffCode;
	}
}