package io.mosip.imagedecoder.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.mosip.imagedecoder.exceptions.DecoderException;

class ExceptionTest {
	@Test
	void testConstructorWithErrorCodeAndMessage() {
		String errorCode = "DECODER_ERR_001";
		String errorMessage = "Invalid Data Error.";

		DecoderException exception = new DecoderException(errorCode, errorMessage);

		assertEquals(errorCode, exception.getErrorCode());
		assertTrue(exception.getMessage().contains(errorMessage));
		assertNull(exception.getCause()); // Ensure there is no root cause
	}

	@Test
	void testConstructorWithErrorCodeMessageAndRootCause() {
		String errorCode = "DECODER_ERR_001";
		String errorMessage = "Invalid Data Error.";
		Throwable rootCause = new NullPointerException("Null pointer exception");

		DecoderException exception = new DecoderException(errorCode, errorMessage, rootCause);

		assertEquals(errorCode, exception.getErrorCode());
		assertTrue(exception.getMessage().contains(errorMessage));
		assertEquals(rootCause, exception.getCause()); // Ensure the root cause is set
	}

	@Test
	void testExceptionMessage() {
		String errorCode = "DECODER_ERR_001";
		String errorMessage = "Invalid Data Error.";

		DecoderException exception = new DecoderException(errorCode, errorMessage);

		String expectedMessage = "ERROR_003: A third error occurred";
		assertTrue(exception.getMessage().contains(errorMessage));
	}
}
