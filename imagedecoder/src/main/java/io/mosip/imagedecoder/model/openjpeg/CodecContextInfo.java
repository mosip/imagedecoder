package io.mosip.imagedecoder.model.openjpeg;

import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/*
 * Routines that are to be used by both halves of the library are declared to
 * receive a pointer to this structure. There are no actual instances of
 * CommonStructure, only of CompressionContextInfo and DecompressionContextInfo.
 */
@Data
@ToString
public class CodecContextInfo {
	private ContextInfo contextInfo = new ContextInfo(); /* Fields common to both master struct types */
	/*
	 * Additional fields follow in an actual CompressionContextInfo or
	 * DecompressionContextInfo. All three structs must agree on these initial
	 * fields!
	 */

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CodecContextInfo))
			return false;
		CodecContextInfo that = (CodecContextInfo) obj;
		return Objects.equals(contextInfo, that.contextInfo);
	}

	@Override
	public int hashCode() {
		return Objects.hash(contextInfo);
	}

	public boolean canEqual(Object other) {
		return other instanceof CodecContextInfo;
	}
}