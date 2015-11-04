package me.civ5.dds.structures;

public abstract class DdsHeader {
	private static final int SIZE = 124;
	
	protected static final long DDSD_CAPS        = 0x000001;
	protected static final long DDSD_HEIGHT      = 0x000002; 
	protected static final long DDSD_WIDTH       = 0x000004;
	protected static final long DDSD_PITCH       = 0x000008;
	protected static final long DDSD_PIXELFORMAT = 0x001000;
	protected static final long DDSD_MIPMAPCOUNT = 0x020000;
	protected static final long DDSD_LINEARSIZE  = 0x080000;
	protected static final long DDSD_DEPTH       = 0x800000;

	public static final long DDS_HEADER_FLAGS_TEXTURE     = (DDSD_CAPS|DDSD_HEIGHT|DDSD_WIDTH|DDSD_PIXELFORMAT);
	public static final long DDS_HEADER_FLAGS_MIPMAP      = DDSD_MIPMAPCOUNT;
	public static final long DDS_HEADER_FLAGS_VOLUME      = DDSD_DEPTH;
	public static final long DDS_HEADER_FLAGS_PITCH       = DDSD_PITCH;
	public static final long DDS_HEADER_FLAGS_LINEARSIZE  = DDSD_LINEARSIZE;

	protected static final long DDSCAPS_COMPLEX = 0x000008;
	protected static final long DDSCAPS_MIPMAP  = 0x400000;
	protected static final long DDSCAPS_TEXTURE = 0x001000;

	public static final long DDS_SURFACE_FLAGS_MIPMAP  = (DDSCAPS_COMPLEX|DDSCAPS_MIPMAP);
	public static final long DDS_SURFACE_FLAGS_TEXTURE = DDSCAPS_TEXTURE;
	public static final long DDS_SURFACE_FLAGS_CUBEMAP = DDSCAPS_COMPLEX;

	protected static final long DDSCAPS2_CUBEMAP           = 0x000200;
	protected static final long DDSCAPS2_CUBEMAP_POSITIVEX = 0x000400;
	protected static final long DDSCAPS2_CUBEMAP_NEGATIVEX = 0x000800;
	protected static final long DDSCAPS2_CUBEMAP_POSITIVEY = 0x001000;
	protected static final long DDSCAPS2_CUBEMAP_NEGATIVEY = 0x002000;
	protected static final long DDSCAPS2_CUBEMAP_POSITIVEZ = 0x004000;
	protected static final long DDSCAPS2_CUBEMAP_NEGATIVEZ = 0x008000;
	protected static final long DDSCAPS2_VOLUME            = 0x200000;
	 
	public static final long DDS_CUBEMAP_POSITIVEX = (DDSCAPS2_CUBEMAP|DDSCAPS2_CUBEMAP_POSITIVEX);
	public static final long DDS_CUBEMAP_NEGATIVEX = (DDSCAPS2_CUBEMAP|DDSCAPS2_CUBEMAP_NEGATIVEX);
	public static final long DDS_CUBEMAP_POSITIVEY = (DDSCAPS2_CUBEMAP|DDSCAPS2_CUBEMAP_POSITIVEY);
	public static final long DDS_CUBEMAP_NEGATIVEY = (DDSCAPS2_CUBEMAP|DDSCAPS2_CUBEMAP_NEGATIVEY);
	public static final long DDS_CUBEMAP_POSITIVEZ = (DDSCAPS2_CUBEMAP|DDSCAPS2_CUBEMAP_POSITIVEZ);
	public static final long DDS_CUBEMAP_NEGATIVEZ = (DDSCAPS2_CUBEMAP|DDSCAPS2_CUBEMAP_NEGATIVEZ);
	public static final long DDS_CUBEMAP_ALLFACES  = (DDS_CUBEMAP_POSITIVEX|DDS_CUBEMAP_NEGATIVEX|DDS_CUBEMAP_POSITIVEY|DDS_CUBEMAP_NEGATIVEY|DDS_CUBEMAP_POSITIVEZ|DDSCAPS2_CUBEMAP_NEGATIVEZ);
	public static final long DDS_FLAGS_VOLUME      = DDSCAPS2_VOLUME;

	private DdsDword flags;
	private DdsDword height;
	private DdsDword width;
	private DdsDword pitch;
	private DdsDword depth;
	private DdsDword mipmaps;
	
	private DdsPixelFormat pixelFormat;
	
	private DdsDword caps;
	private DdsDword caps2;
	private DdsDword caps3;
	private DdsDword caps4;
	
	protected void setFlags(long flags) {
		this.flags = new DdsDword(flags);
	}
	
	protected void setHeight(long height) {
		this.height = new DdsDword(height);
	}
	
	protected void setWidth(long width) {
		this.width = new DdsDword(width);
	}
	
	protected void setPitch(long pitch) {
		this.pitch = new DdsDword(pitch);
	}
	
	protected void setDepth(long depth) {
		this.depth = new DdsDword(depth);
	}
	
	protected void setMipmaps(long mipmaps) {
		this.mipmaps = new DdsDword(mipmaps);
	}
	
	protected void setCaps(long caps) {
		setCaps(caps, 0, 0, 0);
	}

	protected void setCaps(long caps, long caps2) {
		setCaps(caps, caps2, 0, 0);
	}
	
	protected void setCaps(long caps, long caps2, long caps3, long caps4) {
		this.caps = new DdsDword(caps);
		this.caps2 = new DdsDword(caps2);
		this.caps3 = new DdsDword(caps3);
		this.caps4 = new DdsDword(caps4);
	}
	
	protected void setPixelFormat(DdsPixelFormat pixelFormat) {
		this.pixelFormat = pixelFormat;
	}

	protected int getBytes(byte[] b, int offset) {
		int start = offset;
		
		offset = (new DdsDword(SIZE)).getBytes(b, offset);
		offset = flags.getBytes(b, offset);
		offset = height.getBytes(b, offset);
		offset = width.getBytes(b, offset);
		offset = pitch.getBytes(b, offset);
		offset = depth.getBytes(b, offset);
		offset = mipmaps.getBytes(b, offset);
		
		DdsDword reserved1 = new DdsDword(0);
		for (int i = 0; i < 11; ++i) {
			offset = reserved1.getBytes(b, offset);
		}

		offset = pixelFormat.getBytes(b, offset);

		offset = caps.getBytes(b, offset);
		offset = caps2.getBytes(b, offset);
		offset = caps3.getBytes(b, offset);
		offset = caps4.getBytes(b, offset);

		DdsDword reserved2 = new DdsDword(0);
		offset = reserved2.getBytes(b, offset);

		assert ((offset-start) == SIZE) : "DDS_HEADER is incorrect size";
		return offset;
	}
}
