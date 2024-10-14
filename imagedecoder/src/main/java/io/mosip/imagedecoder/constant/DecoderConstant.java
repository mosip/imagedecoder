package io.mosip.imagedecoder.constant;

public class DecoderConstant {
	public DecoderConstant() {
		throw new IllegalStateException("DecoderConstant class");
	}

	public static final String LOGGER_SESSIONID = "DECODER";
	public static final String LOGGER_IDTYPE = "DECODER";
	public static final String LOGGER_EMPTY = "";

	public static final int REQUEST_IMAGE_HEADER = 1;
	public static final int REQUEST_IMAGE_HEADER_AND_DATA = 2;

	public static final String IMAGE_WIDTH = "WIDTH";
	public static final String IMAGE_HEIGHT = "HEIGHT";
	public static final String IMAGE_LOSSLESS = "LOSSLESS";
	public static final String IMAGE_DEPTH = "DEPTH";
	public static final String IMAGE_DPI_HORIZONTAL = "DPI_HORIZONTAL";
	public static final String IMAGE_DPI_VERTICAL = "DPI_VERTICAL";
	public static final String IMAGE_BIT_RATE = "BIT_RATE";
	public static final String IMAGE_DATA = "DATA";
	public static final String IMAGE_SIZE = "SIZE";
	public static final String IMAGE_TYPE = "TYPE";
	public static final String IMAGE_COLOR_SPACE = "COLOR_SPACE";
	public static final String IMAGE_ASPECT_RATIO = "ASPECT_RATIO";
	public static final String IMAGE_COMPRESSION_RATIO = "COMPRESSION_RATIO";

	public static final int WHENCE_SEEK_END = 1;
	public static final int WHENCE_SEEK_CUR = 2;
	public static final int WHENCE_SEEK_SET = 3;

	public static final String IMAGE_TYPE_JP2000 = "JP2000";
	public static final String IMAGE_TYPE_WSQ = "WSQ";
}
