package io.mosip.imagedecoder.model;

import java.nio.ByteBuffer;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ByteBufferContext {
	private ByteBuffer buffer;
}