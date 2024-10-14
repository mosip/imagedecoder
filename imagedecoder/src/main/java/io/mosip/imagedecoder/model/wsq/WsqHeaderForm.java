package io.mosip.imagedecoder.model.wsq;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WsqHeaderForm {
	private int black;
	private int white;
	private int width;
	private int height;
	private float[] mShift = new float[1];
	private float[] rScale = new float[1];
	private int wsqEncoder;
	private long software;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WsqHeaderForm))
			return false;
		WsqHeaderForm that = (WsqHeaderForm) obj;
		return black == that.black && white == that.white && width == that.width && height == that.height
				&& wsqEncoder == that.wsqEncoder && software == that.software && arrayEquals(mShift, that.mShift)
				&& arrayEquals(rScale, that.rScale);
	}

	private boolean arrayEquals(float[] a1, float[] a2) {
		if (a1 == a2)
			return true;
		if (a1 == null || a2 == null)
			return false;
		if (a1.length != a2.length)
			return false;
		for (int i = 0; i < a1.length; i++) {
			if (Float.compare(a1[i], a2[i]) != 0)
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = Integer.hashCode(black);
		result = 31 * result + Integer.hashCode(white);
		result = 31 * result + Integer.hashCode(width);
		result = 31 * result + Integer.hashCode(height);
		result = 31 * result + arrayHashCode(mShift);
		result = 31 * result + arrayHashCode(rScale);
		result = 31 * result + Integer.hashCode(wsqEncoder);
		result = 31 * result + Long.hashCode(software);
		return result;
	}

	private int arrayHashCode(float[] array) {
		if (array == null)
			return 0;
		int hash = 1;
		for (float f : array) {
			hash = 31 * hash + Float.hashCode(f);
		}
		return hash;
	}

	public boolean canEqual(Object other) {
		return other instanceof WsqHeaderForm;
	}
}
