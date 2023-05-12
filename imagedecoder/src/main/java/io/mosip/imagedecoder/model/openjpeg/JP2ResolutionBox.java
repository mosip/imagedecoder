package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
// DWT - Implementation of a discrete wavelet transform
public class JP2ResolutionBox {
	/*
	resc: 	(0)Capture resolution box. This box specifies the resolution at which this image was captured. The format
			of this box is specified in Annex I.7.3.6.1.
	resd: 	(1)Default display resolution box. This box specifies the default resolution at which this image should be
			displayed. 
	*/
	private int resolutionBoxType;

	/*VRcN:Vertical Capture resolution numerator. This parameter specifies the VRcN value in Equation I.4, which
	is used to calculate the vertical capture resolution. This parameter is encoded as a 16-bit big endian
	unsigned integer.
	*/
	private int verticalNumerator;
	/*
	VRcD:Vertical Capture resolution denominator. This parameter specifies the VRcD value in Equation I.4,
	which is used to calculate the vertical capture resolution. This parameter is encoded as a 16-bit big
	endian unsigned integer.
	*/
	private int verticalDenominator;
	/*
	HRcN:Horizontal Capture resolution numerator. This parameter specifies the HRcN value in Equation I.5,
	which is used to calculate the horizontal capture resolution. This parameter is encoded as a 16-bit big
	endian unsigned integer.
	*/
	private int horizontalNumerator;
	/*
	HRcD:Horizontal Capture resolution denominator. This parameter specifies the HRcD value in Equation I.5,
	which is used to calculate the horizontal capture resolution. This parameter is encoded as a 16-bit big
	endian unsigned integer.
	*/
	private int horizontalDenominator;
	/*
	VRcE:Vertical Capture resolution exponent. This parameter specifies the VRcE value in Equation I.4, which is
	used to calculate the vertical capture resolution. This parameter is encoded as a twos-compliment 8-bit
	signed integer.
	*/
	private int verticalExponent;
	/*
	HRcE:Horizontal Capture resolution exponent. This parameter specifies the HRcE value in Equation I.5,
	which is used to calculate the horizontal capture resolution. This parameter is encoded as a twoscompliment 8-bit signed integer.
	*/
	private int horizontalExponent;

	private int verticalResolution;
	private int horizontalResolution;
}
