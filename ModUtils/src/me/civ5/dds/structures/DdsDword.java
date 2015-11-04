package me.civ5.dds.structures;

public class DdsDword {
	private long dword;
	
	public DdsDword(long value) {
		this.dword = value;
	}
	
	/* package */ int getBytes(byte[] b, int offset) {
		b[offset+0] = ((byte) ((dword >>  0) & 0xff));
		b[offset+1] = ((byte) ((dword >>  8) & 0xff));
		b[offset+2] = ((byte) ((dword >> 16) & 0xff));
		b[offset+3] = ((byte) ((dword >> 24) & 0xff));
		
		return offset + 4;
	}
}
