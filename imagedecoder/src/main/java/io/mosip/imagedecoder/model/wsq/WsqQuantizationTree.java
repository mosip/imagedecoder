package io.mosip.imagedecoder.model.wsq;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WsqQuantizationTree {
	private short x; /* UL corner of block */
	private short y;
	private short lenX; /* block size */
	private short lenY; /* block size */

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WsqQuantizationTree))
			return false;
		WsqQuantizationTree that = (WsqQuantizationTree) obj;
		return x == that.x && y == that.y && lenX == that.lenX && lenY == that.lenY;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, lenX, lenY);
	}

	public boolean canEqual(Object other) {
		return other instanceof WsqQuantizationTree;
	}
}