
package org.farng.mp3.id3;

import org.farng.mp3.AbstractMP3FragmentBody;
import org.farng.mp3.InvalidTagException;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Each ID3v2 frame contains a header and then the payload. This is the super
 * class for the payload
 * 
 * @author Eric Farng
 * @version $Revision$
 */
public abstract class AbstractID3v2FrameBody extends AbstractMP3FragmentBody {

	/**
	 * Creates a new AbstractID3v2FrameBody object.
	 */
	protected AbstractID3v2FrameBody() {
		super();
	}

	/**
	 * Creates a new AbstractID3v2FrameBody object.
	 * 
	 * @param copyObject
	 *            the object to copy
	 */
	protected AbstractID3v2FrameBody(final AbstractID3v2FrameBody copyObject) {
		super(copyObject);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof AbstractID3v2FrameBody && super.equals(obj);
	}

	@Override
	protected int readHeader(final RandomAccessFile file) throws IOException,
			InvalidTagException {
		final int size;
		final byte[] buffer = new byte[3];
		if (has6ByteHeader()) {
			// read the 3 byte size
			file.read(buffer, 0, 3);
			size = (((0xff & buffer[0]) << 16) + ((0xff & buffer[1]) << 8) + (0xff & buffer[2]));
		} else {
			// read the 4 byte size
			size = file.readInt();

			// we need to skip the flag bytes;
			file.skipBytes(2);
		}
		if (size == 0) {
			throw new InvalidTagException("Found empty frame");
		}
		if (size <= 0 || size > file.length()) {
			throw new InvalidTagException("Invalid size for Frame Body");
		}
		return size;
	}

	@Override
	protected void writeHeader(final RandomAccessFile file, final int size)
			throws IOException {
		final byte[] buffer = new byte[3];
		if (has6ByteHeader()) {
			// write the 3 byte size;
			buffer[0] = (byte) ((size & 0x00FF0000) >> 16);
			buffer[1] = (byte) ((size & 0x0000FF00) >> 8);
			buffer[2] = (byte) (size & 0x000000FF);
			file.write(buffer);
		} else {
			// write the 4 byte size;
			file.writeInt(size);

			// need to skip 2 flag bytes
			file.skipBytes(2);
		}
	}
}