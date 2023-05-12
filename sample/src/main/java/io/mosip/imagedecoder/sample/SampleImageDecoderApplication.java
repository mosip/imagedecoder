package io.mosip.imagedecoder.sample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.mosip.imagedecoder.model.DecoderRequestInfo;
import io.mosip.imagedecoder.model.DecoderResponseInfo;
import io.mosip.imagedecoder.model.Response;
import io.mosip.imagedecoder.openjpeg.OpenJpegDecoder;
import io.mosip.imagedecoder.spi.IImageDecoderApi;
import io.mosip.imagedecoder.wsq.WsqDecoder;

public class SampleImageDecoderApplication {
	static Logger LOGGER = LoggerFactory.getLogger(SampleImageDecoderApplication.class);

	public static void main(String[] args) {
		if (args != null && args.length >= 2) {
			// Argument 0 should contain
			// io.mosip.imagedecoder.image.type=0(jp2000)/1(wsq)"
			String imageType = args[0];
			LOGGER.info("main :: imageType :: Argument [0] " + imageType);
			if (imageType.contains(ApplicationConstant.MOSIP_IMAGE_TYPE))// 0
			{
				imageType = imageType.split("=")[1];
			}

			// Argument 1 should contain
			// "io.mosip.imagedecoder.image.folder.path"
			String biometricFolderPath = args[1];
			LOGGER.info("main :: biometricFolderPath :: Argument [1] " + biometricFolderPath);
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
		LOGGER.info("decodeJPEG2000 :: Started :: inputImageType :: JPEG2000 :: biometricFolderPath :: "
				+ biometricFolderPath + " :: fileExtension :: " + fileExtension);
		FileOutputStream tmpOutputStream = null;
		try {
			IImageDecoderApi decoder = null;
			String dirName = new File(".").getCanonicalPath();
			dirName = dirName + biometricFolderPath;
			List<String> files = null;
			try {
				files = findFiles(Paths.get(dirName), fileExtension);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (String fileName : files) {
				File initialFile = new File(fileName);
				if (initialFile.exists()) {
					LOGGER.info("decodeJPEG2000 :: fileName ::" + fileName);

					DecoderRequestInfo requestInfo = new DecoderRequestInfo();
					byte[] imageData = Files.readAllBytes(Paths.get(fileName));
					requestInfo.setImageData(imageData);
					requestInfo.setBufferedImage(true);

					decoder = new OpenJpegDecoder();

					long startTimeOneFile = System.currentTimeMillis();
					Response<DecoderResponseInfo> info = decoder.decode(requestInfo);
					long endTimeOneFile = System.currentTimeMillis();
					System.out.println(
							"Time Taken for one file : " + (endTimeOneFile - startTimeOneFile) + " milliseconds");

					LOGGER.info("INFO :: WIDTH \t= " + info.getResponse().getImageWidth());
					LOGGER.info("INFO :: HEIGHT\t=" + info.getResponse().getImageHeight());
					LOGGER.info("INFO :: DEPTH \t=" + info.getResponse().getImageDepth());
					LOGGER.info("INFO :: LOSSLESS \t=" + info.getResponse().getImageLossless());
					LOGGER.info("INFO :: DPI_HORIZONTAL \t=" + info.getResponse().getImageDpiHorizontal());
					LOGGER.info("INFO :: DPI_VERTICAL \t=" + info.getResponse().getImageDpiVertical());
					LOGGER.info("INFO :: TYPE \t=" + info.getResponse().getImageType());
					LOGGER.info("INFO :: SIZE \t=" + info.getResponse().getImageSize());
					LOGGER.info("INFO :: COLORSPACE\t=" + info.getResponse().getImageColorSpace());
					LOGGER.info("INFO :: ASPECT_RATIO\t=" + info.getResponse().getImageAspectRatio());
					LOGGER.info("INFO :: COMPRESSION_RATIO\t=" + info.getResponse().getImageCompressionRatio());

					LOGGER.info("===============================================================================");					
				}
			}
		} catch (Exception ex) {
			LOGGER.info("decodeJPEG2000 :: Error ", ex);
		} finally {
			try {
				if (tmpOutputStream != null)
					tmpOutputStream.close();
			} catch (Exception ex) {
			}
		}
		LOGGER.info("decodeJPEG2000 :: Ended :: ");
	}

	public static void decodeWSQ2000(String biometricFolderPath, String fileExtension) {
		LOGGER.info("decodeWSQ2000 :: Started :: inputImageType :: WSQ :: biometricFolderPath :: "
				+ biometricFolderPath + " :: fileExtension :: " + fileExtension);
		FileOutputStream tmpOutputStream = null;
		try {
			IImageDecoderApi decoder = null;
			String dirName = new File(".").getCanonicalPath();
			dirName = dirName + biometricFolderPath;
			List<String> files = null;
			try {
				files = findFiles(Paths.get(dirName), fileExtension);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (String fileName : files) {
				File initialFile = new File(fileName);
				if (initialFile.exists()) {
					LOGGER.info("decodeWSQ2000 :: fileName ::" + fileName);

					DecoderRequestInfo requestInfo = new DecoderRequestInfo();
					byte[] imageData = Files.readAllBytes(Paths.get(fileName));
					requestInfo.setImageData(imageData);
					requestInfo.setBufferedImage(true);

					decoder = new WsqDecoder();
					long startTimeOneFile = System.currentTimeMillis();
					Response<DecoderResponseInfo> info = decoder.decode(requestInfo);
					long endTimeOneFile = System.currentTimeMillis();
					System.out.println(
							"Time Taken for one file : " + (endTimeOneFile - startTimeOneFile) + " milliseconds");

					LOGGER.info("INFO :: WIDTH \t= " + info.getResponse().getImageWidth());
					LOGGER.info("INFO :: HEIGHT\t=" + info.getResponse().getImageHeight());
					LOGGER.info("INFO :: DEPTH \t=" + info.getResponse().getImageDepth());
					LOGGER.info("INFO :: LOSSLESS \t=" + info.getResponse().getImageLossless());
					LOGGER.info("INFO :: DPI_HORIZONTAL \t=" + info.getResponse().getImageDpiHorizontal());
					LOGGER.info("INFO :: DPI_VERTICAL \t=" + info.getResponse().getImageDpiVertical());
					LOGGER.info("INFO :: BIT_RATE \t=" + info.getResponse().getImageBitRate());
					LOGGER.info("INFO :: TYPE \t=" + info.getResponse().getImageType());
					LOGGER.info("INFO :: SIZE \t=" + info.getResponse().getImageSize());
					LOGGER.info("INFO :: COLORSPACE\t=" + info.getResponse().getImageColorSpace());
					LOGGER.info("INFO :: ASPECT_RATIO\t=" + info.getResponse().getImageAspectRatio());
					LOGGER.info("INFO :: COMPRESSION_RATIO\t=" + info.getResponse().getImageCompressionRatio());

					// For testing
					// BufferedImage image = null;
					// try
					// {
					// image = ImageUtil.fromByteGray(width, height, cdata);
					// File outputfile = new
					// File("D:\\Project\\Mosip\\imagedecoder\\imagedecoder\\BiometricInfo\\info_left_thumb_auth.jpg");
					// ImageIO.write(image, "jpg", outputfile);
					// }
					// catch(Exception ex)
					// {
					// ex.printStackTrace();
					// }

				}
			}
		} catch (Exception ex) {
			LOGGER.info("decodeWSQ2000 :: Error ", ex);
		} finally {
			try {
				if (tmpOutputStream != null)
					tmpOutputStream.close();
			} catch (Exception ex) {
			}
		}
		LOGGER.info("decodeWSQ2000 :: Ended :: ");
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
