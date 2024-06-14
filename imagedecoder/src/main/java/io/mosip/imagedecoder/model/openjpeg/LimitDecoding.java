package io.mosip.imagedecoder.model.openjpeg;

/**
 * Limit decoding to certain portions of the codestream.
 */
public enum LimitDecoding {
	NO_LIMITATION(0),
	/** < No limitation for the decoding. The entire codestream will de decoded */
	LIMIT_TO_MAIN_HEADER(1),
	/** < The decoding is limited to the Main Header */
	DECODE_ALL_BUT_PACKETS(2);

	/** < Decode everything except the JPEG 2000 packets */

	private final int value;

	LimitDecoding(int value) {
		this.value = value;
	}

	@SuppressWarnings({ "java:S1144" })
	LimitDecoding(LimitDecoding value) {
		this.value = value.value();
	}

	public int value() {
		return this.value;
	}

	public static LimitDecoding fromValue(int value) {
		for (LimitDecoding c : LimitDecoding.values()) {
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
