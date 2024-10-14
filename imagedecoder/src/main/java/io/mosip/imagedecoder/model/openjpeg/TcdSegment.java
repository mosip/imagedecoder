package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Segment
 */
@Data
@ToString
public class TcdSegment {
	private byte[] data;
	private int dataIndex;
	private int noOfPasses;
	private int length;
	private int maxPasses;
	private int noOfNewPasses;
	private int newLength;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TcdSegment that = (TcdSegment) o;
		return dataIndex == that.dataIndex && noOfPasses == that.noOfPasses && length == that.length
				&& maxPasses == that.maxPasses && noOfNewPasses == that.noOfNewPasses && newLength == that.newLength
				&& Arrays.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(dataIndex, noOfPasses, length, maxPasses, noOfNewPasses, newLength);
		result = 31 * result + Arrays.hashCode(data);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TcdSegment;
	}
}