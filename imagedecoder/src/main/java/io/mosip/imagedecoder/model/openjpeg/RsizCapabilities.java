package io.mosip.imagedecoder.model.openjpeg;


/**
 * Rsiz Capabilities
 */
public enum RsizCapabilities {
	STD_RSIZ(0),
	/** Standard JPEG2000 profile */
	CINEMA2K(3),
	/** Profile name for a 2K image */
	CINEMA4K(4);

	/** Profile name for a 4K image */

	private final int value;

	RsizCapabilities(int value) {
		this.value = value;
	}

	RsizCapabilities(RsizCapabilities value) {
		this.value = value.value();
	}

	public int value() {
		return this.value;
	}

	public static RsizCapabilities fromValue(int value) {
		for (RsizCapabilities c : RsizCapabilities.values()) {
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
