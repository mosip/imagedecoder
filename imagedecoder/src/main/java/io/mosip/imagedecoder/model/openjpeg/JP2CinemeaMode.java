package io.mosip.imagedecoder.model.openjpeg;

/**
 * Digital cinema operation mode
 */
public enum JP2CinemeaMode {
	OFF(0),
	/** Not Digital Cinema */
	CINEMA2K_24(1),
	/** 2K Digital Cinema at 24 fps */
	CINEMA2K_48(2),
	/** 2K Digital Cinema at 48 fps */
	CINEMA4K_24(3);

	/** 4K Digital Cinema at 24 fps */

	private final int value;

	JP2CinemeaMode(int value) {
		this.value = value;
	}

	@SuppressWarnings({ "java:S1144" })
	JP2CinemeaMode(JP2CinemeaMode value) {
		this.value = value.value();
	}

	public int value() {
		return this.value;
	}

	public static JP2CinemeaMode fromValue(int value) {
		for (JP2CinemeaMode c : JP2CinemeaMode.values()) {
			if (c.value == value) {
				return c;
			}
		}
		throw new IllegalArgumentException("No enum constant for value: " + value);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + Integer.toHexString(value) + ")";
	}
}