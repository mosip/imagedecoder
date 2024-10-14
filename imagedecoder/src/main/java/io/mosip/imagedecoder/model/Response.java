package io.mosip.imagedecoder.model;

import java.util.Objects;

import lombok.Data;

/**
 * The Class Response.
 *
 * @author Janardhan B S
 * @param <T> the generic type
 */
@Data
public class Response<T> {

	/** The status code. */
	private Integer statusCode;

	/** The status message. */
	private String statusMessage;

	/** The response. */
	@SuppressWarnings({ "java:S1700" })
	private T response;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Response<?>))
			return false;
		Response<?> that = (Response<?>) o;
		return Objects.equals(statusCode, that.statusCode) && Objects.equals(statusMessage, that.statusMessage)
				&& Objects.equals(response, that.response);
	}

	@Override
	public int hashCode() {
		return Objects.hash(statusCode, statusMessage, response);
	}

	public boolean canEqual(Object other) {
		return other instanceof Response;
	}
}