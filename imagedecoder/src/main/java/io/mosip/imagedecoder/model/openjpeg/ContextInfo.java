package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.ToString;

/**
 * Common fields between JPEG-2000 compression and decompression master structs.
 */
@Data
@ToString
public class ContextInfo {
	private Object clientData;
	/** < Available for use by application */
	private int isDecompressor;
	/** < So common code can tell which is which */
	private JP2CodecFormat codecFormat;
	/** < selected codec */
	private Object j2kHandle;
	/** < pointer to the J2K codec */
	private Object jp2Handle;
	/** < pointer to the JP2 codec */
	private Object mj2Handle;

	/** < pointer to the MJ2 codec */

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ContextInfo))
			return false;
		ContextInfo that = (ContextInfo) obj;
		return isDecompressor == that.isDecompressor && canEqual(that)
				&& (clientData != null ? clientData.equals(that.clientData) : that.clientData == null)
				&& (codecFormat != null ? codecFormat.equals(that.codecFormat) : that.codecFormat == null)
				&& (j2kHandle != null ? j2kHandle.equals(that.j2kHandle) : that.j2kHandle == null)
				&& (jp2Handle != null ? jp2Handle.equals(that.jp2Handle) : that.jp2Handle == null)
				&& (mj2Handle != null ? mj2Handle.equals(that.mj2Handle) : that.mj2Handle == null);
	}

	@Override
	public int hashCode() {
		int result = clientData != null ? clientData.hashCode() : 0;
		result = 31 * result + isDecompressor;
		result = 31 * result + (codecFormat != null ? codecFormat.hashCode() : 0);
		result = 31 * result + (j2kHandle != null ? j2kHandle.hashCode() : 0);
		result = 31 * result + (jp2Handle != null ? jp2Handle.hashCode() : 0);
		result = 31 * result + (mj2Handle != null ? mj2Handle.hashCode() : 0);
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof ContextInfo;
	}
}