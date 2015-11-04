package me.civ5.dds.imageio;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class DDSImageReader extends ImageReader {

	private static final int MAGIC = 0x20534444;
	private static final int BANDS_COUNT = 4;
	private DDSHeader ddsHeader = null;
	private ImageInputStream stream;

	public DDSImageReader(ImageReaderSpi originatingProvider) {
		super(originatingProvider);
		this.setInput(input);
	}

	@Override
	public void setInput(Object input, boolean seekForwardOnly, boolean ignoreMetadata) {
		super.setInput(input, seekForwardOnly, ignoreMetadata);
		if (input == null) {
			this.stream = null;
			return;
		}
		// The input source must be an ImageInputStream because the originating
		// provider -- the DDSImageReaderSpi class -- passes STANDARD_INPUT_TYPE
		// -- an array consisting only of ImageInputStream -- to its superclass
		// in its constructor call.

		if (input instanceof ImageInputStream) {
			this.stream = (ImageInputStream) input;
		} else {
			throw new IllegalArgumentException("ImageInputStream expected!");
		}
	}

	@Override
	public int getNumImages(boolean allowSearch) throws IIOException {
		readHeader();
		return (int) ddsHeader.getMipMapCount();
	}

	@Override
	public int getWidth(int imageIndex) throws IIOException {
		readHeader();
		checkIndex(imageIndex);
		return (int) ddsHeader.getWidth(imageIndex);
	}

	@Override
	public int getHeight(int imageIndex) throws IIOException {
		readHeader();
		checkIndex(imageIndex);
		return (int) ddsHeader.getHeight(imageIndex);
	}

	@Override
	public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) throws IIOException {
		readHeader();
		checkIndex(imageIndex);
		ImageTypeSpecifier imageType = null;
		java.util.List<ImageTypeSpecifier> l = new ArrayList<ImageTypeSpecifier>();

		imageType = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_4BYTE_ABGR);

		l.add(imageType);
		return l.iterator();
	}

	// XXX - create meta data
	@Override
	public IIOMetadata getStreamMetadata() {
		return null;
	}

	// XXX - create meta data
	@Override
	public IIOMetadata getImageMetadata(int imageIndex) {
		return null;
	}

	@Override
	public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
		readHeader();
		checkIndex(imageIndex);
		DDSLineReader ddsLineReader = new DDSLineReader();
		stream.reset();
		stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);

		// Skips bytes to the selected imageIndex (MipMap)
		long skipsBytes = 0;
		for (int i = 0; i < imageIndex; i++) {
			if (ddsHeader.getPixelFormat().isCompressed()) {
				if (ddsHeader.getPixelFormat().isDXT1() || ddsHeader.getPixelFormat().isATI1()) { // DXT1 & ATI (8 bytes)
					long bytes = (8 * (Math.max(1, (ddsHeader.getHeight(i) / 4) * Math.max(1, ddsHeader.getWidth(i) / 4))));
					bytes = Math.max(bytes, 8);
					skipsBytes = skipsBytes + bytes;
				} else { // DXT3 & DXT5 & ATI2 (16 bytes)
					long bytes = (16 * (Math.max(1, (ddsHeader.getHeight(i) / 4) * Math.max(1, ddsHeader.getWidth(i) / 4))));
					bytes = Math.max(bytes, 16);
					skipsBytes = skipsBytes + bytes;
				}
			} else { // Uncompressed
				long bytes = (ddsHeader.getPixelFormat().getRgbBitCount() / 8) * ddsHeader.getHeight(i) * ddsHeader.getWidth(i);
				skipsBytes = skipsBytes + bytes;
			}
		}
		if (skipsBytes > 0)
			stream.skipBytes(skipsBytes);
		int width = (int) ddsHeader.getWidth(imageIndex);
		int height = (int) ddsHeader.getHeight(imageIndex);
		// Calculate and return a Rectangle that identifies the region of the
		// source image that should be read:
		//
		// 1. If param is null, the upper-left corner of the region is (0, 0),
		// and the width and height are specified by the width and height
		// arguments. In other words, the entire image is read.
		//
		// 2. If param is not null
		//
		// 2.1 If param.getSourceRegion() returns a non-null Rectangle, the
		// region is calculated as the intersection of param's Rectangle
		// and the earlier (0, 0, width, height Rectangle).
		//
		// 2.2 param.getSubsamplingXOffset() is added to the region's x
		// coordinate and subtracted from its width.
		//
		// 2.3 param.getSubsamplingYOffset() is added to the region's y
		// coordinate and subtracted from its height.

		Rectangle sourceRegion = getSourceRegion(param, width, height);

		// Source subsampling is used to return a scaled-down source image.
		// Default 1 values for X and Y subsampling indicate that a non-scaled
		// source image will be returned.

		int sourceXSubsampling = 1;
		int sourceYSubsampling = 1;

		// The final step in reading an image from a source to a destination is
		// to map the source samples in various source bands to destination
		// samples in various destination bands. This lets you return only the
		// red component of an image, for example. Default null values indicate
		// that all source and destination bands are used.

		int[] sourceBands = null;
		int[] destinationBands = null;

		// The destination offset determines the starting location in the
		// destination where decoded pixels are placed. Default (0, 0) values
		// indicate the upper-left corner.

		Point destinationOffset = new Point(0, 0);

		// If param is not null, override the source subsampling, source bands,
		// destination bands, and destination offset defaults.

		if (param != null) {
			sourceXSubsampling = param.getSourceXSubsampling();
			sourceYSubsampling = param.getSourceYSubsampling();
			sourceBands = param.getSourceBands();
			destinationBands = param.getDestinationBands();
			destinationOffset = param.getDestinationOffset();
		}

		// Obtain a BufferedImage into which decoded pixels will be placed. This
		// destination will be returned to the application.
		//
		// 1. If param is not null
		//
		// 1.1 If param.getDestination() returns a BufferedImage
		//
		// 1.1.1 Return this BufferedImage
		//
		// Else
		//
		// 1.1.2 Invoke param.getDestinationType ().
		//
		// 1.1.3 If the returned ImageTypeSpecifier equals
		// getImageTypes (0) (see below), return its BufferedImage.
		//
		// 2. If param is null or a BufferedImage has not been obtained
		//
		// 2.1 Return getImageTypes (0)'s BufferedImage.

		BufferedImage dst = getDestination(param, getImageTypes(0), width, height);

		// dst.
		// Verify that the number of source bands and destination bands, as
		// specified by param, are the same. If param is null, 3 is compared
		// with dst.getSampleModel ().getNumBands (), thewhich must also equal 3.
		// An IllegalArgumentException is thrown if the number of source bands
		// differs from the number of destination bands.

		checkReadParamBandSettings(param, BANDS_COUNT, dst.getSampleModel().getNumBands());

		// Create a WritableRaster for the source.

		WritableRaster wrSrc = Raster.createBandedRaster(DataBuffer.TYPE_BYTE, width, 1, BANDS_COUNT, new Point(0, 0));

		byte[][] banks = ((DataBufferByte) wrSrc.getDataBuffer()).getBankData();

		// Create a WritableRaster for the destination.

		WritableRaster wrDst = dst.getRaster();

		// Identify destination rectangle for clipping purposes. Only source
		// pixels within this rectangle are copied to the destination.

		int dstMinX = wrDst.getMinX();
		int dstMaxX = dstMinX + wrDst.getWidth() - 1;
		int dstMinY = wrDst.getMinY();
		int dstMaxY = dstMinY + wrDst.getHeight() - 1;

		// Create a child raster that exposes only the desired source bands.

		if (sourceBands != null) {
			wrSrc = wrSrc.createWritableChild(0, 0, width, 1, 0, 0, sourceBands);
		}

		// Create a child raster that exposes only the desired destination
		// bands.

		if (destinationBands != null) {
			wrDst = wrDst.createWritableChild(0, 0, wrDst.getWidth(), wrDst.getHeight(), 0, 0, destinationBands);
		}

		// Read all bytes for the selected imageIndex (More Memory, Less Time)
		byte[] bytes = ddsLineReader.readAll(stream, ddsHeader.getPixelFormat().getFormat(), (int) ddsHeader.getPixelFormat().getRgbBitCount(), width, height);

		int srcY = 0;
		try {
			int[] pixel = wrSrc.getPixel(0, 0, (int[]) null);

			for (srcY = 0; srcY < height; srcY++) {
				// Decode the next row from the DDS file.
				// XXX - Can be moved down past the continue/breaks...
				ddsLineReader.decodeLine(bytes, ddsHeader, banks, width, srcY);

				// Reject rows that lie outside the source region, or which are
				// not part of the subsampling.

				if ((srcY < sourceRegion.y) || (srcY >= sourceRegion.y + sourceRegion.height) || (((srcY - sourceRegion.y) % sourceYSubsampling) != 0)) {
					continue;
				}

				// Determine the row's location in the destination.

				int dstY = destinationOffset.y + (srcY - sourceRegion.y) / sourceYSubsampling;
				if (dstY < dstMinY) {
					continue; // The row is above the top of the destination rectangle.
				}

				if (dstY > dstMaxY) {
					break; // The row is below the bottom of the destination rectangle.
				}

				// Copy each subsampled source pixel that fits into the
				// destination rectangle into the destination.

				for (int srcX = sourceRegion.x; srcX < sourceRegion.x + sourceRegion.width; srcX++) {
					if (((srcX - sourceRegion.x) % sourceXSubsampling) != 0) {
						continue;
					}

					int dstX = destinationOffset.x + (srcX - sourceRegion.x) / sourceXSubsampling;
					if (dstX < dstMinX) {
						continue; // The pixel is to the destination rectangle's left.
					}

					if (dstX > dstMaxX) {
						break; // The pixel is to the destination rectangle's right.
					}

					// Copy the pixel. Sub-banding is automatically handled.

					wrSrc.getPixel(srcX, 0, pixel);
					wrDst.setPixel(dstX, dstY, pixel);
				}
			}
		} catch (IOException e) {
			throw new IIOException("Error reading line " + srcY + ": " + e.getMessage(), e);
		}
		// magic
		return dst;
	}

	@Override
	public String getFormatName() throws IOException {
		readHeader();
		return super.getFormatName() + " (" + ddsHeader.getPixelFormat().getFormat().getName() + ")";
	}

	private void checkIndex(int imageIndex) throws IllegalArgumentException {
		if (imageIndex > ddsHeader.getMipMapCount()) {
			throw new IllegalArgumentException("MipMap index not found");
		}
	}

	private void readHeader() throws IIOException {
		if (ddsHeader != null) {
			return;
		}

		if (stream == null) {
			throw new IIOException("Failed to load header: Stream is null");
		}

		try {
			stream.reset();
			stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);

			int magic = stream.readInt();
			if (magic != MAGIC) {
				throw new IIOException("Failed To Load Header: magic (" + magic + ") is not MAGIC (" + MAGIC + ")");
			}
			int size = stream.readInt();
			if (size != 124) {
				throw new IIOException("Failed To Load Header: size (" + size + ") value is not 124");
			}
			long flags = stream.readInt() & 0xFFFFFFFFL;
			long height = stream.readInt() & 0xFFFFFFFFL;
			long width = stream.readInt() & 0xFFFFFFFFL;
			long linearSize = stream.readInt() & 0xFFFFFFFFL;
			long depth = stream.readInt() & 0xFFFFFFFFL;
			long mipMapCount = stream.readInt() & 0xFFFFFFFFL;

			stream.skipBytes(11 * 4);

			DDSPixelFormat ddsPixelFormat = readPixelFormat();
			long caps = stream.readInt() & 0xFFFFFFFFL;
			long caps2 = stream.readInt() & 0xFFFFFFFFL;
			long caps3 = stream.readInt() & 0xFFFFFFFFL;
			long caps4 = stream.readInt() & 0xFFFFFFFFL;
			ddsHeader = new DDSHeader(size, flags, height, width, linearSize, depth, mipMapCount, ddsPixelFormat, caps, caps2, caps3, caps4);
		} catch (IOException ex) {
			throw new IIOException("Failed To Load Header: " + ex.getMessage());
		}
	}

	private DDSPixelFormat readPixelFormat() throws IOException {
		int size = stream.readInt();
		if (size != 32) {
			throw new IOException("Failed load PixelFormat: File ill formed"); // should throw something...
		}
		long flags = stream.readInt() & 0xFFFFFFFFL;
		long fourCC = stream.readInt() & 0xFFFFFFFFL;
		long rgbBitCount = stream.readInt() & 0xFFFFFFFFL;
		long rBitMask = stream.readInt() & 0xFFFFFFFFL;
		long gBitMask = stream.readInt() & 0xFFFFFFFFL;
		long bBitMask = stream.readInt() & 0xFFFFFFFFL;
		long aBitMask = stream.readInt() & 0xFFFFFFFFL;
		DDSPixelFormat ddsPixelFormat = new DDSPixelFormat(size, flags, fourCC, rgbBitCount, rBitMask, gBitMask, bBitMask, aBitMask);
		stream.readInt();
		return ddsPixelFormat;
	}
}
