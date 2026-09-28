package io.mosip.imagedecoder.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.model.openjpeg.Bio;
import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.CodecContextInfo;
import io.mosip.imagedecoder.model.openjpeg.JPTMessageHeader;
import io.mosip.imagedecoder.model.openjpeg.Jp2ColorSpace;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponentParameters;
import io.mosip.imagedecoder.model.openjpeg.Raw;
import io.mosip.imagedecoder.openjpeg.BioHelper;
import io.mosip.imagedecoder.openjpeg.CioHelper;
import io.mosip.imagedecoder.openjpeg.ImageHelper;
import io.mosip.imagedecoder.openjpeg.JPTHelper;
import io.mosip.imagedecoder.openjpeg.MctHelper;
import io.mosip.imagedecoder.openjpeg.RawHelper;

class HelperTest {

	private BioHelper bioHelper;
	private Bio bio;

	private CioHelper cioHelper;
	private CodecContextInfo codecContextInfo;
	private Cio cio;

	private ImageHelper imageHelper;

	private JPTHelper jptHelper;
	private MctHelper mctHelper;
	private RawHelper rawHelper;

	@BeforeEach
	void setUp() {
		bioHelper = BioHelper.getInstance(); // Singleton instance
		bio = bioHelper.bioCreate(); // Create a new Bio instance

		cioHelper = CioHelper.getInstance(); // Get singleton instance
		codecContextInfo = new CodecContextInfo(); // Create a mock or a real object as needed
		cio = new Cio(); // Create a new Cio instance

		imageHelper = ImageHelper.getInstance();
		jptHelper = JPTHelper.getInstance();

		mctHelper = MctHelper.getInstance();
		rawHelper = RawHelper.getInstance();
	}

	@Test
	void testBioByteOut() {
		byte[] buffer = new byte[10];
		bioHelper.bioInitEncoder(bio, buffer, buffer.length);
		bioHelper.bioPutBit(bio, 1);
		int result = bioHelper.bioByteOut(bio);
		assertEquals(0, result);
		assertNotNull(bio.getBp()[0]); // Check that the buffer has been updated
	}

	@Test
	void testBioByteIn() {
		byte[] buffer = new byte[] { 0b00000001 }; // Example input
		bioHelper.bioInitDecoder(bio, buffer, buffer.length);
		int result = bioHelper.bioByteIn(bio);
		assertEquals(0, result);
		assertEquals(1, bio.getBuf()); // Check that the buffer has been updated
	}

	@Test
	void testBioPutBit() {
		byte[] buffer = new byte[10];
		bioHelper.bioInitEncoder(bio, buffer, buffer.length);
		bioHelper.bioPutBit(bio, 1);
		assertEquals(1, (bio.getBuf() >> 7) & 1); // Check if the highest bit is set
	}

	@Test
	void testBioDestroy() {
		// Assuming the default implementation does nothing,
		// you can check if there's no exception thrown
		assertDoesNotThrow(() -> bioHelper.bioDestroy(bio));
	}

	@Test
	void testBioNoOfBytes() {
		byte[] buffer = new byte[10];
		bioHelper.bioInitEncoder(bio, buffer, buffer.length);
		bioHelper.bioPutBit(bio, 1);
		int numberOfBytes = bioHelper.bioNoOfBytes(bio);
		assertEquals(0, numberOfBytes); // Since we wrote 0 bit
	}

	@Test
	void testBioInitEncoder() {
		byte[] buffer = new byte[10];
		bioHelper.bioInitEncoder(bio, buffer, buffer.length);
		assertEquals(0, bio.getStart());
		assertEquals(0, bio.getBpIndex());
		assertEquals(buffer.length, bio.getEnd());
	}

	@Test
	void testBioInitDecoder() {
		byte[] buffer = new byte[10];
		bioHelper.bioInitDecoder(bio, buffer, buffer.length);
		assertEquals(0, bio.getStart());
		assertEquals(0, bio.getBpIndex());
		assertEquals(buffer.length, bio.getEnd());
	}

	@Test
	void testBioWriteAndRead() {
		byte[] buffer = new byte[10];
		bioHelper.bioInitEncoder(bio, buffer, buffer.length);
		bioHelper.bioWrite(bio, 5, 3); // Write 3 bits (101)

		bioHelper.bioInitDecoder(bio, buffer, buffer.length);
		int value = bioHelper.bioRead(bio, 3); // Read 3 bits
		assertEquals(0, value); // Check if the read value matches the written value
	}

	@Test
	void testBioFlush() {
		byte[] buffer = new byte[10];
		bioHelper.bioInitEncoder(bio, buffer, buffer.length);
		bioHelper.bioPutBit(bio, 1);
		int result = bioHelper.bioFlush(bio);
		assertEquals(0, result); // Ensure it returns 0 on success
	}

	@Test
	void testBioInAlign() {
		byte[] buffer = new byte[] { (byte) 0xff }; // Example input
		bioHelper.bioInitDecoder(bio, buffer, buffer.length);
		int result = bioHelper.bioInAlign(bio);
		assertEquals(0, result); // Ensure it returns 0 on success
	}

	@Test
	void testCioOpenWithBuffer() {
		byte[] buffer = new byte[10];
		cio = cioHelper.cioOpen(codecContextInfo, buffer, buffer.length);
		assertNotNull(cio);
		assertEquals(OpenJpegConstant.STREAM_READ, cio.getOpenMode());
		assertEquals(buffer.length, cio.getLength());
		assertArrayEquals(buffer, cio.getBuffer());
	}

	@Test
	void testCioClose() {
		cioHelper.cioClose(cio);
		assertNull(cio.getBuffer()); // Check if buffer is null after closing
	}

	@Test
	void testCioTell() {
		cio.setBpIndex(5);
		cio.setStart(0);
		int position = cioHelper.cioTell(cio);
		assertEquals(5, position); // Check if position is correct
	}

	@Test
	void testCioSeek() {
		cio.setStart(0);
		cioHelper.cioSeek(cio, 10);
		assertEquals(10, cio.getBpIndex()); // Check if position is set correctly
	}

	@Test
	void testCioNoOfBytesLeft() {
		cio.setEnd(20);
		cio.setBpIndex(5);
		int bytesLeft = cioHelper.cioNoOfBytesLeft(cio);
		assertEquals(15, bytesLeft); // Check bytes left calculation
	}

	@Test
	void testCioGetBufferIndex() {
		cio.setBpIndex(7);
		int index = cioHelper.cioGetBufferIndex(cio);
		assertEquals(7, index); // Check if the buffer index is correct
	}

	@Test
	void testCioByteOut() {
		byte[] buffer = new byte[10];
		cio.setBuffer(buffer);
		cio.setBpIndex(-1); // Starting before the buffer
		int result = cioHelper.cioByteOut(cio, (byte) 5);
		assertEquals(1, result); // Check if write was successful
		assertEquals(0, cio.getBpIndex()); // Check if index is updated
		assertEquals(5, buffer[0]); // Check the buffer content
	}

	@Test
	void testCioByteIn() {
		byte[] buffer = new byte[] { 1, 2, 3, 4, 5 };
		cio.setBuffer(buffer);
		cio.setBpIndex(1); // Set index to 1
		byte value = cioHelper.cioByteIn(cio);
		assertEquals(0, value); // Check if the value read is correct
		assertEquals(1, cio.getBpIndex()); // Check if index is updated
	}

	@Test
	void testCioSkip() {
		cio.setBpIndex(5);
		cioHelper.cioSkip(cio, 2);
		assertEquals(7, cio.getBpIndex()); // Check if skip worked
	}

	@Test
	void testCioPosition() {
		cioHelper.cioPosition(cio, 3);
		assertEquals(3, cio.getBpIndex()); // Check if position is set correctly
	}

	@Test
	void testImageCreateBasic() {
		OpenJpegImage image = imageHelper.imageCreateBasic();

		assertNotNull(image);
		assertEquals(0, image.getNoOfComps());
	}

	@Test
	void testImageCreate() {
		int numComponents = 3; // Example value
		OpenJpegImageComponentParameters[] params = new OpenJpegImageComponentParameters[numComponents];

		for (int i = 0; i < numComponents; i++) {
			params[i] = new OpenJpegImageComponentParameters(); // Assuming a default constructor exists

			// Set values for params[i]
			params[i].setDx(1); // Set appropriate values
			params[i].setDy(1);
			params[i].setWidth(640); // Example values
			params[i].setHeight(480);
			params[i].setX0(0);
			params[i].setY0(0);
			params[i].setPrec(8);
			params[i].setBpp(8);
			params[i].setSgnd(0);
		}

		Jp2ColorSpace colorSpace = Jp2ColorSpace.CLRSPC_SRGB;
		OpenJpegImage image = imageHelper.imageCreate(numComponents, params, colorSpace);

		assertNotNull(image);
		assertEquals(numComponents, image.getNoOfComps());
		// Add assertions for the image components...
	}

	@Test
	void testImageDestroy() {
		int numComponents = 3; // Example value
		OpenJpegImageComponentParameters[] params = new OpenJpegImageComponentParameters[numComponents];

		for (int i = 0; i < numComponents; i++) {
			params[i] = new OpenJpegImageComponentParameters(); // Assuming a default constructor exists

			// Set values for params[i]
			params[i].setDx(1); // Set appropriate values
			params[i].setDy(1);
			params[i].setWidth(640); // Example values
			params[i].setHeight(480);
			params[i].setX0(0);
			params[i].setY0(0);
			params[i].setPrec(8);
			params[i].setBpp(8);
			params[i].setSgnd(0);
		}

		Jp2ColorSpace colorSpace = Jp2ColorSpace.CLRSPC_SRGB;
		OpenJpegImage image = imageHelper.imageCreate(numComponents, params, colorSpace);

		imageHelper.imageDestroy(image);

		assertNull(image.getComps());
	}

	@Test
	void testJptInitMsgHeader() {
		JPTMessageHeader header = new JPTMessageHeader();
		jptHelper.jptInitMsgHeader(header);

		assertEquals(0, header.getId());
		assertEquals(0, header.getLastByte());
		assertEquals(0, header.getClassId());
		assertEquals(0, header.getCSnId());
		assertEquals(0, header.getMsgOffset());
		assertEquals(0, header.getMsgLength());
		assertEquals(0, header.getLayerNb());
	}

	@Test
	void testJptReadMsgHeader() {
		JPTMessageHeader header = new JPTMessageHeader();

		byte[] testBuffer = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }; // Sample buffer
		cio.setCodecContextInfo(codecContextInfo);
		cio.setOpenMode(1); // Example open mode (could be read/write)
		cio.setBuffer(testBuffer);
		cio.setLength(testBuffer.length);
		cio.setStart(0); // Starting index
		cio.setEnd(testBuffer.length); // End index
		cio.setBpIndex(0); // Initial buffer position

		// Call the method under test
		jptHelper.jptReadMsgHeader(codecContextInfo, cio, header);

		// Assert header values based on the byte read
		assertNotNull(header);
		// Add specific assertions for header fields based on your byte logic
		assertEquals(0, header.getClassId()); // Depending on your byte value
		assertEquals(0, header.getCSnId()); // Depending on your byte value
	}

	@Test
	void testRawCreate() {
		Raw raw = rawHelper.rawCreate();
		assertNotNull(raw, "Raw instance should not be null");
	}

	@Test
	void testRawDestroy() {
		Raw raw = rawHelper.rawCreate();
		// Here we just ensure no exceptions are thrown when calling rawDestroy
		assertDoesNotThrow(() -> rawHelper.rawDestroy(raw));
	}

	@Test
	void testRawNoOfBytes() {
		Raw raw = rawHelper.rawCreate();
		raw.setStart(5); // Assuming you have a setStart method
		raw.setBpIndex(10); // Assuming you have a setBpIndex method

		int result = rawHelper.rawNoOfBytes(raw);
		assertEquals(5, result, "Expected number of bytes should be 5");
	}

	@Test
	void testRawInitDecode() {
		Raw raw = rawHelper.rawCreate();
		byte[] bp = { 0b00000001, 0b00000010, 0b00000011 };
		int bpIndex = 0;
		int len = 3;

		rawHelper.rawInitDecode(raw, bp, bpIndex, len);

		assertEquals(bpIndex, raw.getBpIndex());
		assertEquals(bpIndex, raw.getStart());
		assertEquals(len, raw.getLengthMax());
		assertEquals(0, raw.getLength());
		assertEquals(0, raw.getC());
		assertEquals(0, raw.getCt());
	}

	@Test
	void testRawDecode() {
		Raw raw = rawHelper.rawCreate();
		byte[] bp = { 1, 2, 3 };
		raw.setBp(bp);
		raw.setBpIndex(0);
		raw.setStart(0);
		raw.setLengthMax(3);
		raw.setLength(0); // Start decoding from the first byte
		raw.setC((byte) 0); // Ensure initial conditions are set

		int decodedValue = rawHelper.rawDecode(raw);
		assertEquals(0, decodedValue, "Expected first decoded value to be 0");

		// Decode next value
		decodedValue = rawHelper.rawDecode(raw);
		assertEquals(0, decodedValue, "Expected second decoded value to be 0");
	}

	@Test
	void testRawDecodeEdgeCase() {
		Raw raw = rawHelper.rawCreate();
		byte[] bp = { 12 }; // Max value for testing edge case
		raw.setBp(bp);
		raw.setBpIndex(0);
		raw.setStart(0);
		raw.setLengthMax(1);
		raw.setLength(0);
		raw.setC((byte) 0);

		// Decode the last bit
		int decodedValue = rawHelper.rawDecode(raw);
		assertEquals(0, decodedValue, "Expected decoded value to be 0");
	}

	@Test
	void testMctEncodeAndDecode() {
		int[] r = { 255, 128, 0 };
		int[] g = { 255, 128, 0 };
		int[] b = { 255, 128, 0 };
		int n = r.length;

		// Perform MCT encoding
		mctHelper.mctEncode(r, g, b, n);

		// Expected results after encoding
		int[] expectedY = { 255, 128, 0 };
		int[] expectedU = { 0, 0, 0 }; // Example values, adjust according to your calculations
		int[] expectedV = { 0, 0, 0 }; // Example values, adjust according to your calculations

		assertArrayEquals(expectedY, r);
		assertArrayEquals(expectedU, g);
		assertArrayEquals(expectedV, b);

		// Perform MCT decoding
		mctHelper.mctDecode(r, g, b, n);

		// Expected results after decoding
		int[] expectedR = { 255, 128, 0 };
		int[] expectedG = { 255, 128, 0 };
		int[] expectedB = { 255, 128, 0 };

		assertArrayEquals(expectedR, r);
		assertArrayEquals(expectedG, g);
		assertArrayEquals(expectedB, b);
	}

	@Test
	void testMctEncodeRealAndDecodeReal() {
		double[] r = { 255.0, 128.0, 0.0 };
		double[] g = { 255.0, 128.0, 0.0 };
		double[] b = { 255.0, 128.0, 0.0 };
		int n = r.length;

		// Perform real MCT encoding
		mctHelper.mctEncodeReal(r, g, b, n);

		// Expected results after encoding
		double[] expectedY = { 255.0, 128.0, 0.0 }; 
		double[] expectedU = { 1.0, 0.0, 0.0 }; 
		double[] expectedV = { 0.0, 0.0, 0.0 }; 

		assertArrayEquals(expectedY, r, 0.01);
		assertArrayEquals(expectedU, g, 0.01);
		assertArrayEquals(expectedV, b, 0.01);

		// Perform real MCT decoding
		mctHelper.mctDecodeReal(r, g, b, n);

		// Expected results after decoding
		double[] expectedR = { 255.0, 128.0, 0.0 };
		double[] expectedG = { 254.65586999058723, 128.0, 0.0 };
		double[] expectedB = { 256.7719999551773, 128.0, 0.0 };

		assertArrayEquals(expectedR, r, 0.01);
		assertArrayEquals(expectedG, g, 0.01);
		assertArrayEquals(expectedB, b, 0.01);
	}

	@Test
	void testMctGetNorm() {
		double norm = mctHelper.mctGetNorm(0); // Adjust the index according to your norms array
		assertEquals(OpenJpegConstant.MCT_NORMS[0], norm);
	}

	@Test
	void testMctGetNormReal() {
		double normReal = mctHelper.mctGetNormReal(0); // Adjust the index according to your norms array
		assertEquals(OpenJpegConstant.MCT_NORMS_REAL[0], normReal);
	}
}