package io.mosip.imagedecoder.sample;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.text.MessageFormat;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;

import io.mosip.imagedecoder.model.DecoderRequestInfo;
import io.mosip.imagedecoder.model.DecoderResponseInfo;
import io.mosip.imagedecoder.model.Response;
import io.mosip.imagedecoder.openjpeg.OpenJpegDecoder;
import io.mosip.imagedecoder.openjpeg.Tier2Helper;
import io.mosip.imagedecoder.spi.IImageDecoderApi;
import io.mosip.imagedecoder.wsq.WsqDecoder;

public class SampleImageDecoderApplication {
	private static Logger logger = ImageDecoderLogger.getLogger(SampleImageDecoderApplication.class);

	public static void main(String[] args) {
		if (args != null && args.length >= 2) {
			// Argument 0 should contain
			// io.mosip.imagedecoder.image.type=0(jp2000)/1(wsq)"
			String imageType = args[0];
			logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("main :: imageType :: Argument 0 :: {0}", imageType));
			if (imageType.contains(ApplicationConstant.MOSIP_IMAGE_TYPE))// 0
			{
				imageType = imageType.split("=")[1];
			}

			// Argument 1 should contain
			// "io.mosip.imagedecoder.image.folder.path"
			String biometricFolderPath = args[1];
			logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("main :: biometricFolderPath :: Argument 1 = {0}", biometricFolderPath));
			if (biometricFolderPath.contains(ApplicationConstant.MOSIP_BIOMETRIC_FOLDER_PATH)) {
				biometricFolderPath = biometricFolderPath.split("=")[1];
			}

			switch (Integer.parseInt(imageType)) {
			case ApplicationConstant.IMAGE_TYPE_JP2000:
				decodeJPEG2000(biometricFolderPath, "jp2");
				break;
			case ApplicationConstant.IMAGE_TYPE_WSQ:
				decodeWSQ2000(biometricFolderPath, "wsq");
				break;
			default:
				break;
			}
		}
	}

	public static void decodeJPEG2000(String biometricFolderPath, String fileExtension) {
		logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,
				MessageFormat.format("decodeJPEG2000 :: Started :: inputImageType :: JPEG2000 :: biometricFolderPath ::{0} :: fileExtension :: {1}",
				biometricFolderPath, fileExtension));

		FileOutputStream tmpOutputStream = null;
		try {
			IImageDecoderApi decoder = null;
			String dirName = new File(".").getCanonicalPath();
			dirName = dirName + biometricFolderPath;
			List<String> files = null;
			try {
				files = findFiles(Paths.get(dirName), fileExtension);
			} catch (IOException e) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "decodeJPEG2000 :: Error ", e);
			}

			for (String fileName : files) {
				File initialFile = new File(fileName);
				if (initialFile.exists()) {
					logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("decodeJPEG2000 :: fileName ::{0}", fileName));
					try {
						DecoderRequestInfo requestInfo = new DecoderRequestInfo();
						byte[] imageData = Files.readAllBytes(Paths.get(fileName));
						requestInfo.setImageData(imageData);
						requestInfo.setBufferedImage(true);

						decoder = new OpenJpegDecoder();

						long startTimeOneFile = System.currentTimeMillis();
						Response<DecoderResponseInfo> info = decoder.decode(requestInfo);
						long endTimeOneFile = System.currentTimeMillis();
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("Time Taken for one file : {0} in milliseconds", (endTimeOneFile - startTimeOneFile)));

						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: WIDTH \t= {0}",  info.getResponse().getImageWidth()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: HEIGHT \t= {0}",  info.getResponse().getImageHeight()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: DEPTH \t= {0}",  info.getResponse().getImageDepth()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: LOSSLESS \t= {0}",  info.getResponse().getImageLossless()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: DPI_HORIZONTAL \t= {0}",  info.getResponse().getImageDpiHorizontal()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: DPI_VERTICAL \t= {0}",  info.getResponse().getImageDpiVertical()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: TYPE \t= {0}",  info.getResponse().getImageType()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: SIZE \t= {0}",  info.getResponse().getImageSize()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: COLORSPACE \t= {0}",  info.getResponse().getImageColorSpace()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: ASPECT_RATIO \t= {0}",  info.getResponse().getImageAspectRatio()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: COMPRESSION_RATIO \t= {0}",  info.getResponse().getImageCompressionRatio()));
					} catch (Exception e) {
						logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "decodeJPEG2000 :: Error ", e);
					}
					logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, 
							"===============================================================================");
				}
			}
		} catch (Exception ex) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "decodeJPEG2000 :: Error ", ex);
		} finally {
			try {
				if (tmpOutputStream != null)
					tmpOutputStream.close();
			} catch (Exception ex) {
			}
		}
		logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "decodeJPEG2000 :: Ended :: ");
	}

	public static void decodeWSQ2000(String biometricFolderPath, String fileExtension) {
		logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,
				MessageFormat.format("decodeJPEG2000 :: Started :: inputImageType :: WSQ :: biometricFolderPath ::{0} :: fileExtension :: {1}",
				biometricFolderPath, fileExtension));

		FileOutputStream tmpOutputStream = null;
		try {
			IImageDecoderApi decoder = null;
			String dirName = new File(".").getCanonicalPath();
			dirName = dirName + biometricFolderPath;
			List<String> files = null;
			try {
				files = findFiles(Paths.get(dirName), fileExtension);
			} catch (IOException e) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "decodeWSQ2000 :: Error ", e);
			}

			for (String fileName : files) {
				File initialFile = new File(fileName);
				if (initialFile.exists()) {
					logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("decodeWSQ :: fileName ::{0}", fileName));
					try {
						DecoderRequestInfo requestInfo = new DecoderRequestInfo();
						byte[] imageData = Files.readAllBytes(Paths.get(fileName));
						requestInfo.setImageData(imageData);
						requestInfo.setBufferedImage(true);

						decoder = new WsqDecoder();
						long startTimeOneFile = System.currentTimeMillis();
						Response<DecoderResponseInfo> info = decoder.decode(requestInfo);
						long endTimeOneFile = System.currentTimeMillis();
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("Time Taken for one file : {0} in milliseconds", (endTimeOneFile - startTimeOneFile)));

						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: WIDTH \t= {0}",  info.getResponse().getImageWidth()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: HEIGHT \t= {0}",  info.getResponse().getImageHeight()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: DEPTH \t= {0}",  info.getResponse().getImageDepth()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: LOSSLESS \t= {0}",  info.getResponse().getImageLossless()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: DPI_HORIZONTAL \t= {0}",  info.getResponse().getImageDpiHorizontal()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: DPI_VERTICAL \t= {0}",  info.getResponse().getImageDpiVertical()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: TYPE \t= {0}",  info.getResponse().getImageType()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: SIZE \t= {0}",  info.getResponse().getImageSize()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: COLORSPACE \t= {0}",  info.getResponse().getImageColorSpace()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: ASPECT_RATIO \t= {0}",  info.getResponse().getImageAspectRatio()));
						logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("INFO :: COMPRESSION_RATIO \t= {0}",  info.getResponse().getImageCompressionRatio()));
					} catch (Exception e) {
						logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY,"decodeWSQ2000 :: Error ", e);
					}
					logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, 
							"===============================================================================");
				}
			}
		} catch (Exception ex) {
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "decodeWSQ2000 :: Error ", ex);
		} finally {
			try {
				if (tmpOutputStream != null)
					tmpOutputStream.close();
			} catch (Exception ex) {
			}
		}
		logger.info(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "decodeWSQ2000 :: Ended :: ");
	}

	public static List<String> findFiles(Path path, String fileExtension) throws IOException {
		if (!Files.isDirectory(path)) {
			throw new IllegalArgumentException("Path must be a directory!");
		}

		List<String> result;

		try (Stream<Path> walk = Files.walk(path)) {
			result = walk.filter(p -> !Files.isDirectory(p))
					// this is a path, not string,
					// this only test if path end with a certain path
					// .filter(p -> p.endsWith(fileExtension))
					// convert path to string first
					.map(p -> p.toString().toLowerCase()).filter(f -> f.endsWith(fileExtension))
					.collect(Collectors.toList());
		}

		return result;
	}
}