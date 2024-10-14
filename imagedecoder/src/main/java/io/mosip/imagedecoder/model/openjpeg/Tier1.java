package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tier-1 coding (coding of code-block coefficients)
 */
@Data
@ToString
public class Tier1 {
	/** codec context */
	private CodecContextInfo codecContextInfo;

	/** MQC component */
	private MQCoder mqc;
	/** RAW component */
	private Raw raw;

	private int[] data;
	private int[] flags;
	private int width;
	private int height;
	private int dataSize;
	private int flagsSize;
	private int flagsStride;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Tier1 tier1 = (Tier1) o;
		return width == tier1.width && height == tier1.height && dataSize == tier1.dataSize
				&& flagsSize == tier1.flagsSize && flagsStride == tier1.flagsStride
				&& Objects.equals(codecContextInfo, tier1.codecContextInfo) && Objects.equals(mqc, tier1.mqc)
				&& Objects.equals(raw, tier1.raw) && Arrays.equals(data, tier1.data)
				&& Arrays.equals(flags, tier1.flags);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(codecContextInfo, mqc, raw, width, height, dataSize, flagsSize, flagsStride);
		result = 31 * result + Arrays.hashCode(data);
		result = 31 * result + Arrays.hashCode(flags);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof Tier1;
	}
}
