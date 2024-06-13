package io.mosip.imagedecoder.model.openjpeg;


/**
 * Progression order
 */
public enum ProgressionOrder {
	PROG_UNKNOWN(-1),
	/** < place-holder */
	LRCP(0),
	/** < layer-resolution-component-precinct order */
	RLCP(1),
	/** < resolution-layer-component-precinct order */
	RPCL(2),
	/** < resolution-precinct-component-layer order */
	PCRL(3),
	/** < precinct-component-resolution-layer order */
	CPRL(4);

	/** < component-precinct-resolution-layer order */

	private final int value;

	ProgressionOrder(int value) {
		this.value = value;
	}

	@SuppressWarnings({ "java:S1144" })
	ProgressionOrder(ProgressionOrder value) {
		this.value = value.value();
	}

	public int value() {
		return this.value;
	}

	public static ProgressionOrder fromValue(int value) {
		for (ProgressionOrder c : ProgressionOrder.values()) {
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