package io.mosip.imagedecoder.model.openjpeg;

/**
 * T2 encoding mode
 */
public enum J2KT2Mode {
	THRESH_CALC(0),
	/** Function called in Rate allocation process */
	FINAL_PASS(1);

	/** Function called in Tier 2 process */

	private final int value;

	J2KT2Mode(int value) {
		this.value = value;
	}

	@SuppressWarnings({ "java:S1144" })
	J2KT2Mode(J2KT2Mode value) {
		this.value = value.value();
	}

	public int value() {
		return this.value;
	}

	public static J2KT2Mode fromValue(int value) {
		for (J2KT2Mode c : J2KT2Mode.values()) {
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