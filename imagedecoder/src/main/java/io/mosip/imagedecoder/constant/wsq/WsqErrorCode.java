package io.mosip.imagedecoder.constant.wsq;

/**
 * WsqErrorCode Enum for the services errors.
 * 
 * @author Janardhan B S
 * @since 1.0.0
 */
public enum WsqErrorCode {
	NON_COMPLIANT_WITH_WSQ_SPECS(-1, "MOS-EXT-1",
			"A code in the hufftable contains an : all 1's code. This image may still be  decodable. It is not compliant with the WSQ specification."),
	EMPTY_STRING_FOUND(-2, "MOS-EXT-2", "Empty name string found."),
	NO_DATA_TO_READ(-3, "MOS-EXT-3", "No huffman table bytes remaining."),
	TABLE_ID_ALREADY_DEFINED(-4, "MOS-EXT-4", "Huffman table Id already defined."),
	VALUE_GREATER_THAN_MAX_COUNT(-5, "MOS-EXT-5", "Num Of Huffman Values is larger than Max Huff Counts."),

	NO_SOI_MARKER(-10, "MOS-EXT-10", "No SOI marker."),
	NO_SOF_TABLE_COMMENT_MARKER(-11, "MOS-EXT-11", "No SOF, Table, or comment markers."),
	NO_SOB_TABLE_COMMENT_MARKER(-12, "MOS-EXT-12", "No SOB, Table, or comment markers."),
	NO_MARKER_FOUND(-13, "MOS-EXT-13", "No marker found."),
	NOT_VALID_MARKER_FOUND(-14, "MOS-EXT-14", "Not a valid marker found."),
	INVALID_MARKER_FOUND(-15, "MOS-EXT-15", "Invalid marker found."),

	ENCODED_DATA_WRONG(-31, "MOS-EXT-31",
			"Decoded data extends past image buffer. Encoded data appears corrupt or non-standard."),
	INVALID_CODE_INHUFFMAN_DATA(-32, "MOS-EXT-32", "Invalid code in HuffmanData."),
	NO_STUFFED_ZEROS(-33, "MOS-DEC-33", "getWsqNextBits : No stuffed zeros."),
	QUANTIZATION_TABLE_PARAMS_NOT_DEFINED(-34, "MOS-DEC-34", "Quantization table parameters not defined."),
	LOW_PASS_FILTER_COEFF_NOT_DEFINED(-35, "MOS-DEC-35", "Low pass filter coefficients not defined."),
	HIGH_PASS_FILTER_COEFF_NOT_DEFINED(-36, "MOS-DEC-36", "High pass filter coefficients not defined."),
	INVALID_QUANTIZATION_PIXEL_VALUE(-37, "MOS-DEC-37", "Invalid quantization pixel value."),
	IMAGE_DATA_OVERFLOW_WILL_READING(-38, "MOS-DEC-38", "Image Data overflow while reading."),
	INVALID_TABLE_DEFINED(-39, "MOS-DEC-39", "Invalid Table defined."),

	TECHNICAL_ERROR_EXCEPTION(-500, "MOS-EXT-500", "Technical Error");

	private final int errorId;
	private final String errorCode;
	private final String errorMessage;

	private WsqErrorCode(final int errorId, final String errorCode, final String errorMessage) {
		this.errorId = errorId;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public int getErrorId() {
		return errorId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public static WsqErrorCode fromErrorCode(String errorCode) {
		for (WsqErrorCode paramCode : WsqErrorCode.values()) {
			if (paramCode.getErrorCode().equalsIgnoreCase(errorCode)) {
				return paramCode;
			}
		}
		return TECHNICAL_ERROR_EXCEPTION;
	}
}