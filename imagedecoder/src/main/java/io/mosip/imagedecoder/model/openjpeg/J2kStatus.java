package io.mosip.imagedecoder.model.openjpeg;

/**
Values that specify the status of the decoding process when decoding the main header. 
These values may be combined with a | operator. 
*/
public enum J2kStatus {
	J2K_STATE_MHSOC  (0x0001), /**< a SOC marker is expected */
	J2K_STATE_MHSIZ  (0x0002), /**< a SIZ marker is expected */
	J2K_STATE_MH     (0x0004), /**< the decoding process is in the main header */
	J2K_STATE_TPHSOT (0x0008), /**< the decoding process is in a tile part header and expects a SOT marker */
	J2K_STATE_TPH    (0x0010), /**< the decoding process is in a tile part header */
	J2K_STATE_MT     (0x0020), /**< the EOC marker has just been read */
	J2K_STATE_NEOC   (0x0040), /**< the decoding process must not expect a EOC marker because the codestream is truncated */
	J2K_STATE_ERR    (0x0080);  /**< the decoding process has encountered an error */
			
	private final int value;

	J2kStatus(int value) {
		this.value = value;
	}

	J2kStatus(J2kStatus value) {
		this.value = value.value();
	}

	public int value() {
		return this.value;
	}

	public static J2kStatus fromValue(int value) {
		for (J2kStatus c : J2kStatus.values()) {
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