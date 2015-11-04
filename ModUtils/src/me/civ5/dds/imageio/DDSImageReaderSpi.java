package me.civ5.dds.imageio;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class DDSImageReaderSpi extends ImageReaderSpi {

	static final int MAGIC = 0x20534444;

	public DDSImageReaderSpi() {
		super("Niklas K. Rasmussen", // vendorName
				"0.0.1 ALPHA 1", // version
				new String[] { "DDS" },// names
				new String[] { "dds" },// suffixes
				new String[] { "image/dds" },// MIMETypes
				"me.civ5.dds.reader.DDSImageReader", // readerClassName
				new Class[] { ImageInputStream.class }, // inputTypes
				null, // writerSpiNames
				false, // supportsStandardStreamMetadataFormat
				null, // nativeStreamMetadataFormatName
				null, // nativeStreamMetadataFormatClassName
				null, // extraStreamMetadataFormatNames
				null, // extraStreamMetadataFormatClassNames
				false, // supportsStandardImageMetadataFormat
				null, // nativeImageMetadataFormatName
				null, // nativeImageMetadataFormatClassName
				null, // extraImageMetadataFormatNames
				null); // extraImageMetadataFormatClassNames
	}

	@Override
	public boolean canDecodeInput(Object source) throws IOException {
		if (!(source instanceof ImageInputStream))
			return false;
		ImageInputStream stream = (ImageInputStream) source;
		stream.reset();
		stream.mark();
		final ByteOrder order = stream.getByteOrder();
		try {
			stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
			int magic = stream.readInt();
			if (magic != MAGIC)
				return false;
			int size = stream.readInt();
			if (size != 124)
				return false;
			stream.reset();
			return true;
		} finally {
			stream.setByteOrder(order);
		}
	}

	@Override
	public ImageReader createReaderInstance(Object extension) throws IOException {
		return new DDSImageReader(this);
	}

	@Override
	public String getDescription(Locale locale) {
		return "Java DDS ImageIO Plugin by Niklas K. Rasmussen";
	}

}
