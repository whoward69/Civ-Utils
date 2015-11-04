package me.civ5.dds.structures;

public class DdsSignature {
	private static final int SIZE = 4;
	
	/* package */ int getBytes(byte[] b, int offset) {
		int start = offset;
		
		b[offset++] = 'D';
		b[offset++] = 'D';
		b[offset++] = 'S';
		b[offset++] = ' ';

		assert ((offset-start) == SIZE) : "DDS signature is incorrect size";
		return offset;
	}
}
