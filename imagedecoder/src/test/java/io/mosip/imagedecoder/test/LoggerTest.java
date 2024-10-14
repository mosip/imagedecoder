package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.mosip.imagedecoder.logger.ImageDecoderLogger;
import io.mosip.kernel.core.logger.spi.Logger;

class LoggerTest {
	@Test
    void testGetLogger() {
        Class<?> clazz = ImageDecoderLogger.class;
        
        // Fetch the logger
        Logger logger = ImageDecoderLogger.getLogger(clazz);
        
        // Assert the logger is fetched correctly
        assertNotNull(logger);
    }

    @Test
    void testPrivateConstructor() {
        // Use reflection to test that the private constructor throws an exception
        assertThrows(Exception.class, () -> {
            var constructor = ImageDecoderLogger.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }
}