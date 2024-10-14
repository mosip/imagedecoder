package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * JP2 component
 */
@Data
@ToString
public class JP2 {
	/** codec context */
	private CodecContextInfo codecContextInfo;
	/** handle to the J2K codec */
	private J2K j2k;
	private long width;
	private long height;
	private long noOfComps;
	private long bpc;
	private long c; // COLORSPACE
	private long unknownC; // UNKNOWN COLORSPACE
	private long ipr;
	private long meth;
	private long approx;
	private long enumcs;
	private long precedence;
	private long brand;
	private long minVersion;
	private long noOfCl;
	private long[] cl;
	private JP2Component[] comps;
	private long[] j2kCodestreamOffset = new long[1];
	private long[] j2kCodestreamLength = new long[1];

	private JP2ResolutionBox resolutionBox = new JP2ResolutionBox();

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof JP2))
			return false;
		JP2 that = (JP2) obj;
		return canEqual(that) && width == that.width && height == that.height && noOfComps == that.noOfComps
				&& bpc == that.bpc && c == that.c && unknownC == that.unknownC && ipr == that.ipr && meth == that.meth
				&& approx == that.approx && enumcs == that.enumcs && precedence == that.precedence
				&& brand == that.brand && minVersion == that.minVersion && noOfCl == that.noOfCl
				&& Arrays.equals(cl, that.cl) && Arrays.equals(j2kCodestreamOffset, that.j2kCodestreamOffset)
				&& Arrays.equals(j2kCodestreamLength, that.j2kCodestreamLength)
				&& Objects.equals(codecContextInfo, that.codecContextInfo) && Objects.equals(j2k, that.j2k)
				&& Arrays.equals(comps, that.comps) && Objects.equals(resolutionBox, that.resolutionBox);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(codecContextInfo, j2k, width, height, noOfComps, bpc, c, unknownC, ipr, meth, approx,
				enumcs, precedence, brand, minVersion, noOfCl, resolutionBox);
		result = 31 * result + Arrays.hashCode(cl);
		result = 31 * result + Arrays.hashCode(j2kCodestreamOffset);
		result = 31 * result + Arrays.hashCode(j2kCodestreamLength);
		result = 31 * result + Arrays.hashCode(comps);
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof JP2;
	}
}