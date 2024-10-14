package io.mosip.imagedecoder.model.wsq;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WsqWavletTree {
	private int x;
	private int y;
	private int lenX;
	private int lenY;
	private int invRow;
	private int invCol;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WsqWavletTree))
			return false;
		WsqWavletTree that = (WsqWavletTree) obj;
		return x == that.x && y == that.y && lenX == that.lenX && lenY == that.lenY && invRow == that.invRow
				&& invCol == that.invCol;
	}

	@Override
	public int hashCode() {
		return 31 * x + 31 * y + 31 * lenX + 31 * lenY + 31 * invRow + 31 * invCol;
	}

	public boolean canEqual(Object other) {
		return other instanceof WsqWavletTree;
	}
}