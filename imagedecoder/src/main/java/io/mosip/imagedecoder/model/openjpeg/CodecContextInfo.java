package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/*
 * Routines that are to be used by both halves of the library are declared to
 * receive a pointer to this structure. There are no actual instances of
 * CommonStructure, only of CompressionContextInfo and DecompressionContextInfo.
 */
public class CodecContextInfo {
	private ContextInfo contextInfo = new ContextInfo(); /* Fields common to both master struct types */
	/*
	 * Additional fields follow in an actual CompressionContextInfo or DecompressionContextInfo. All three
	 * structs must agree on these initial fields!
	 */
}