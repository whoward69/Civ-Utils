package me.civ5.dds.structures;

public class DdsPixelFormat {
	private static final int SIZE = 32;
	
	public static final long DDPF_ALPHAPIXELS = 0x000001; 
	public static final long DDPF_ALPHA       = 0x000002; 
	public static final long DDPF_FOURCC      = 0x000004; 
	public static final long DDPF_RGB         = 0x000040; 
	public static final long DDPF_YUV         = 0x000200;
	public static final long DDPF_LUMINANCE   = 0x020000; 

	private DdsDword flags;
	private DdsDword fourCC;
	private DdsDword rgbBits;
	private DdsDword rMask;
	private DdsDword gMask;
	private DdsDword bMask;
	private DdsDword aMask;
	
	/* package */ DdsPixelFormat(long flags, long fourCC, long rgbBits) {
		this.flags = new DdsDword(flags);
		this.fourCC = new DdsDword(fourCC);
		this.rgbBits = new DdsDword(rgbBits);
	}
	
	/* package */ DdsPixelFormat(long flags, long fourCC, long rgbBits, long rMask, long gMask, long bMask, long aMask) {
		this(flags, fourCC, rgbBits);
		setMasks(rMask, gMask, bMask, aMask);
	}

	/* package */ void setMasks(long rMask, long gMask, long bMask, long aMask) {
		this.rMask = new DdsDword(rMask);
		this.gMask = new DdsDword(gMask);
		this.bMask = new DdsDword(bMask);
		this.aMask = new DdsDword(aMask);
	}

	/* package */ int getBytes(byte[] b, int offset) {
		int start = offset;
		
		offset = (new DdsDword(SIZE)).getBytes(b, offset);
		offset = flags.getBytes(b, offset);
		offset = fourCC.getBytes(b, offset);
		offset = rgbBits.getBytes(b, offset);

		offset = rMask.getBytes(b, offset);
		offset = gMask.getBytes(b, offset);
		offset = bMask.getBytes(b, offset);
		offset = aMask.getBytes(b, offset);

		assert ((offset-start) == SIZE) : "DDS_PIXELFORMAT is incorrect size";
		return offset;
	}
}
