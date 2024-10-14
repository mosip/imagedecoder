package io.mosip.imagedecoder.logger;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.factory.Logfactory;

public final class ImageDecoderLogger {
	/**
	 * Instantiates a new ImageDecoder logger.
	 */
	private ImageDecoderLogger() {
		throw new IllegalStateException("ImageDecoderLogger class");
	}

	/**
	 * Method to get the rolling file logger for the class provided.
	 *
	 * @param clazz the clazz
	 * @return the logger
	 */
	public static Logger getLogger(Class<?> clazz) {
		return Logfactory.getSlf4jLogger(clazz);
	}
}