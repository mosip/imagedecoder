package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/** 
JP2 component
*/
public class JP2 {
	/** codec context */
	private CodecContextInfo codecContextInfo;
	/** handle to the J2K codec  */
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
}