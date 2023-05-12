package io.mosip.imagedecoder.constant;

public enum DecoderErrorCodes {	
	SUCCESS("TOOLKIT_SUCCESS_000", "Success"),
	INVALID_DATA_ERROR("DECODER_ERR_001", "Invalid Data Error."),
	UNSUPPORTED_FORMAT_ERROR("DECODER_ERR_002", "Unsupported format."),
	BUFFEREDIMAGE_ALLOCATION_FAILED_ERROR("DECODER_ERR_003", "BufferedImage allocation failed."),

	TECHNICAL_ERROR_EXCEPTION("DECODER_REQ_ERR_500", "Technical Error");

	private final String errorCode;
	private final String errorMessage;

	private DecoderErrorCodes(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public static DecoderErrorCodes fromErrorCode(String errorCode) {
		for (DecoderErrorCodes paramCode : DecoderErrorCodes.values()) {
			if (paramCode.getErrorCode().equalsIgnoreCase(errorCode)) {
				return paramCode;
			}
		}
		return TECHNICAL_ERROR_EXCEPTION;
	}
}