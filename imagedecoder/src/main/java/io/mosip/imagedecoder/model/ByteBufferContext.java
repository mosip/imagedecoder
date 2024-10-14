package io.mosip.imagedecoder.model;

import java.nio.ByteBuffer;
import java.util.Objects;

import lombok.Data;

@Data
public class ByteBufferContext {
	private ByteBuffer buffer;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ByteBufferContext))
			return false;
		ByteBufferContext that = (ByteBufferContext) o;
		return Objects.equals(buffer, that.buffer);
	}

	@Override
	public int hashCode() {
		return Objects.hash(buffer);
	}

	public boolean canEqual(Object other) {
		return other instanceof ByteBufferContext;
	}
}