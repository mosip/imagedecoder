package io.mosip.imagedecoder.model;

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
	@SuppressWarnings({ "java:S1700"})
	private T response;
}