package io.mosip.imagedecoder.model.openjpeg;

/**
 * Supported image color spaces
 */
public enum Jp2ColorSpace {
	CLRSPC_UNKNOWN(-1),
	/** < place-holder */
	CLRSPC_SRGB(1),
	/** < sRGB */
	CLRSPC_GRAY(2),
	/** < grayscale */
	CLRSPC_SYCC(3);

	/** < YUV */

	private final int value;

	Jp2ColorSpace(int value) {
		this.value = value;
	}

	Jp2ColorSpace(Jp2ColorSpace value) {
		this.value = value.value();
	}

	public int value() {
		return this.value;
	}

	public static Jp2ColorSpace fromValue(int value) {
		for (Jp2ColorSpace c : Jp2ColorSpace.values()) {
			if (c.value == value) {
				return c;
			}
		}
		throw new IllegalArgumentException(value + "");
	}

	@Override
	public String toString() {
		return super.toString() + "(" + Integer.toHexString(value) + ")";
	}
}