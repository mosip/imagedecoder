package io.mosip.imagedecoder.model.openjpeg;

/**
 * Supported codec
 */
public enum JP2CodecFormat {
	CODEC_UNKNOWN(-1),
	/** < place-holder */
	CODEC_J2K(0),
	/** < JPEG-2000 codestream : read/write */
	CODEC_JPT(1),
	/** < JPT-stream (JPEG 2000, JPIP) : read only */
	CODEC_JP2(2);

	/** < JPEG-2000 file format : read/write */

	private final int value;

	JP2CodecFormat(int value) {
		this.value = value;
	}

	JP2CodecFormat(JP2CodecFormat value) {
		this.value = value.value();
	}

	public int value() {
		return this.value;
	}

	public static JP2CodecFormat fromValue(int value) {
		for (JP2CodecFormat c : JP2CodecFormat.values()) {
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