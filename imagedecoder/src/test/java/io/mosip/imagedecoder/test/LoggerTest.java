package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.mosip.imagedecoder.logger.ImageDecoderLogger;
import io.mosip.kernel.core.logger.spi.Logger;

class LoggerTest {
	@Test
	void testGetLogger() {
		Logger logger = ImageDecoderLogger.getLogger(ImageDecoderLogger.class);
		assertNotNull(logger);
	}

	@Test
	void testPrivateConstructor() {
		assertDoesNotThrow(() -> {
			var constructor = ImageDecoderLogger.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			constructor.newInstance();
		});
	}
}
