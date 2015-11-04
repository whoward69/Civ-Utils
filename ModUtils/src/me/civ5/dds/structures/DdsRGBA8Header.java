package me.civ5.dds.structures;

public class DdsRGBA8Header extends DdsHeader {
	public static final int SIZE = 128;
	
	public static final long RGBA8 = (DdsPixelFormat.DDPF_RGB|DdsPixelFormat.DDPF_ALPHAPIXELS);
	public static final long RGBA8_BITS = 32;
	
	public static final long A_MASK = 0xff000000;
	public static final long R_MASK = 0x00ff0000;
	public static final long G_MASK = 0x0000ff00;
	public static final long B_MASK = 0x000000ff;
	
	private DdsSignature signature = new DdsSignature();
	
	public DdsRGBA8Header() {
		setFlags(DDS_HEADER_FLAGS_TEXTURE|DDS_HEADER_FLAGS_PITCH);
		setDepth(0);
		setMipmaps(1);
		
		setPixelFormat(new DdsPixelFormat(RGBA8, 0, RGBA8_BITS, R_MASK, G_MASK, B_MASK, A_MASK));
		
		setCaps(DDS_SURFACE_FLAGS_TEXTURE);
	}
	
	public DdsRGBA8Header(int width, int height) {
		this();
		setSize(width, height);
	}
	
	public void setSize(int width, int height) {
		setWidth(width);
		setHeight(height);
		setPitch(4 * width);
	}
	
	@Override
	public int getBytes(byte[] b, int offset) {
		offset = signature.getBytes(b, offset);
		offset = super.getBytes(b, offset);
		
		assert (offset == SIZE) : "DDS file header is incorrect size";
		return offset;
	}
}
