/* ===================================================================
 * MP3FileHelper.java
 * 
 * Created Jan 18, 2007 10:06:37 PM
 * 
 * Copyright (c) 2007 Matt Magoffin (spamsqr@msqr.us)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * ===================================================================
 * $Id$
 * ===================================================================
 */

package org.farng.mp3;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Helper class for MP3 files, to extract metadata directly from MPEG audio
 * itself (not any tags embedded in file).
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class MP3FileHelper implements MP3AudioMetadata {

	private RandomAccessFile file;
	private int numberMP3SyncFrame = 5;
	private boolean variableBitRate = false;
	private boolean copyProtected = false;
	private boolean home = false;
	private boolean padding = false;
	private boolean privacy = false;
	private boolean protection = false;
	private byte emphasis;
	private byte layer;
	private byte mode;
	private byte modeExtension;
	private byte mpegVersion;
	private double frequency;
	private int bitRate;

	/**
	 * Construct from a RandomAccessFile.
	 * 
	 * @param file  the file
	 * @throws IOException if an IO error occurs
	 */
	public MP3FileHelper(RandomAccessFile file) throws IOException {
		this.file = file;
		boolean isMp3 = parseMpegAudio();
		if (isMp3) {
			// ?
		}
	}
	
	/**
	 * Default constructor.
	 *
	 * <p>The {@link #setFile(RandomAccessFile)} method must be called,
	 * followed by {@link #parseMpegAudio()}, to read the MPEG metadata
	 * after using this constructor.</p>
	 */
	protected MP3FileHelper() {
		this.file = null;
	}

	/**
	 * Parse metadata from the MPEG audio stream.
	 * 
	 * <p>Calling this method will position the file pointer at the start of the
	 * audio frame found.</p>
	 * 
	 * @return true if found, false otherwise
	 * @throws IOException if an IO error occurs
	 */
	protected boolean parseMpegAudio() throws IOException {
		boolean syncFound = false;
		byte first;
		byte second;
		long filePointer = 1;
		try {
			file.seek(0);
			do {
				first = file.readByte();
				if (first == (byte) 0xFF) {
					filePointer = file.getFilePointer();
					second = (byte) (file.readByte() & (byte) 0xE0);
					if (second == (byte) 0xE0) {
						file.seek(filePointer - 1);

						// seek the next frames, recursively
						syncFound = seekNextMP3Frame(numberMP3SyncFrame);
					}
					file.seek(filePointer);
				}
			} while (syncFound == false);
			file.seek(filePointer - 1);
		} catch (EOFException ex) {
			syncFound = false;
		} catch (IOException ex) {
			throw ex;
		}
		return syncFound;
	}

	private boolean seekNextMP3Frame(final int iterations) throws IOException {
		final boolean syncFound;
		final byte[] buffer;
		final byte first;
		final byte second;
		final long filePointer;
		if (iterations == 0) {
			syncFound = true;
		} else {
			try {
				readFrameHeader();
			} catch (TagException ex) {
				return false;
			}
			final int size = getFrameSize();
			if ((size <= 0) || (size > file.length())) {
				return false;
			}
			buffer = new byte[size - 4];
			file.read(buffer);
			filePointer = file.getFilePointer();
			first = file.readByte();
			if (first == (byte) 0xFF) {
				second = (byte) (file.readByte() & (byte) 0xE0);
				if (second == (byte) 0xE0) {
					file.seek(filePointer);

					// recursively find the next frames
					syncFound = seekNextMP3Frame(iterations - 1);
				} else {
					syncFound = false;
				}
			} else {
				syncFound = false;
			}
		}
		return syncFound;
	}

	private void readFrameHeader() throws IOException, TagNotFoundException,
			InvalidTagException {
		final byte[] buffer = new byte[4];
		file.read(buffer);

		// sync
		if ((buffer[0] != (byte) 0xFF)
				|| ((buffer[1] & (byte) 0xE0) != (byte) 0xE0)) {
			throw new TagNotFoundException("MP3 Frame sync bits not found");
		}
		mpegVersion = (byte) ((buffer[1] & TagConstant.MASK_MP3_VERSION) >> 3);
		layer = (byte) ((buffer[1] & TagConstant.MASK_MP3_LAYER) >> 1);
		protection = (buffer[1] & TagConstant.MASK_MP3_PROTECTION) != 1;
		final int bitRateValue = (buffer[2] & TagConstant.MASK_MP3_BITRATE)
				| (buffer[1] & TagConstant.MASK_MP3_ID)
				| (buffer[1] & TagConstant.MASK_MP3_LAYER);
		final Long object = (Long) TagConstant.bitrate.get(new Long(
				bitRateValue));
		if (object != null) {
			if (object.longValue() != bitRate) {
				variableBitRate = true;
			}
			bitRate = object.intValue();
		} else {
			throw new InvalidTagException("Invalid bit rate");
		}
		final int frequencyValue = (buffer[2] & TagConstant.MASK_MP3_FREQUENCY) >>> 2;
		if (mpegVersion == 3) { // Version 1.0
			switch (frequencyValue) {
				case 0:
					frequency = 44.1;
					break;
				case 1:
					frequency = 48.0;
					break;
				case 2:
					frequency = 32.0;
					break;
			}
		} else if (mpegVersion == 2) { // Version 2.0
			switch (frequencyValue) {
				case 0:
					frequency = 22.05;
					break;
				case 1:
					frequency = 24.00;
					break;
				case 2:
					frequency = 16.00;
					break;
			}
		} else if (mpegVersion == 00) { // Version 2.5
			switch (frequencyValue) {
				case 0:
					frequency = 11.025;
					break;
				case 1:
					frequency = 12.00;
					break;
				case 2:
					frequency = 8.00;
					break;
			}
		} else {
			throw new InvalidTagException("Invalid MPEG version");
		}
		padding = (buffer[2] & TagConstant.MASK_MP3_PADDING) != 0;
		privacy = (buffer[2] & TagConstant.MASK_MP3_PRIVACY) != 0;
		mode = (byte) ((buffer[3] & TagConstant.MASK_MP3_MODE) >> 6);
		modeExtension = (byte) ((buffer[3] & TagConstant.MASK_MP3_MODE_EXTENSION) >> 4);
		copyProtected = (buffer[3] & TagConstant.MASK_MP3_COPY) != 0;
		home = (buffer[3] & TagConstant.MASK_MP3_HOME) != 0;
		emphasis = (byte) ((buffer[3] & TagConstant.MASK_MP3_EMPHASIS));
	}

	private int getFrameSize() {
		if (frequency == 0) {
			return 0;
		}
		final int size;
		final int paddingByte = padding ? 1 : 0;
		if (layer == 3) { // Layer I
			size = (int) ((((12 * bitRate) / frequency) + paddingByte) * 4);
		} else {
			size = (int) (((144 * bitRate) / frequency) + paddingByte);
		}
		return size;
	}

	/**
	 * Get the MPEG file associated with this instance.
	 * @return the file
	 */
	protected RandomAccessFile getFile() {
		return file;
	}
	
	/**
	 * Set the MPEG file associated with this instance.
	 * @param file the file to set
	 */
	protected void setFile(RandomAccessFile file) {
		this.file = file;
	}

	/**
	 * @return the numberMP3SyncFrame
	 */
	public int getNumberMP3SyncFrame() {
		return numberMP3SyncFrame;
	}

	/**
	 * @param numberMP3SyncFrame
	 *            the numberMP3SyncFrame to set
	 */
	public void setNumberMP3SyncFrame(int numberMP3SyncFrame) {
		this.numberMP3SyncFrame = numberMP3SyncFrame;
	}

	/**
	 * @return the bitRate
	 */
	public int getBitRate() {
		return bitRate;
	}

	/**
	 * @return the copyProtected
	 */
	public boolean isCopyProtected() {
		return copyProtected;
	}

	/**
	 * @return the emphasis
	 */
	public byte getEmphasis() {
		return emphasis;
	}

	/**
	 * @return the frequency
	 */
	public double getFrequency() {
		return frequency;
	}

	/**
	 * @return the home
	 */
	public boolean isHome() {
		return home;
	}

	/**
	 * @return the layer
	 */
	public byte getLayer() {
		return layer;
	}

	/**
	 * @return the mode
	 */
	public byte getMode() {
		return mode;
	}

	/**
	 * @return the modeExtension
	 */
	public byte getModeExtension() {
		return modeExtension;
	}

	/**
	 * @return the mpegVersion
	 */
	public byte getMpegVersion() {
		return mpegVersion;
	}

	/**
	 * @return the padding
	 */
	public boolean isPadding() {
		return padding;
	}

	/**
	 * @return the privacy
	 */
	public boolean isPrivacy() {
		return privacy;
	}

	/**
	 * @return the protection
	 */
	public boolean isProtection() {
		return protection;
	}

	/**
	 * @return the variableBitRate
	 */
	public boolean isVariableBitRate() {
		return variableBitRate;
	}
	
	/**
	 * @param bitRate the bitRate to set
	 */
	protected void setBitRate(int bitRate) {
		this.bitRate = bitRate;
	}
	
	/**
	 * @param copyProtected the copyProtected to set
	 */
	protected void setCopyProtected(boolean copyProtected) {
		this.copyProtected = copyProtected;
	}
	
	/**
	 * @param emphasis the emphasis to set
	 */
	protected void setEmphasis(byte emphasis) {
		this.emphasis = emphasis;
	}
	
	/**
	 * @param frequency the frequency to set
	 */
	protected void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	
	/**
	 * @param home the home to set
	 */
	protected void setHome(boolean home) {
		this.home = home;
	}
	
	/**
	 * @param layer the layer to set
	 */
	protected void setLayer(byte layer) {
		this.layer = layer;
	}
	
	/**
	 * @param mode the mode to set
	 */
	protected void setMode(byte mode) {
		this.mode = mode;
	}
	
	/**
	 * @param modeExtension the modeExtension to set
	 */
	protected void setModeExtension(byte modeExtension) {
		this.modeExtension = modeExtension;
	}
	
	/**
	 * @param mpegVersion the mpegVersion to set
	 */
	protected void setMpegVersion(byte mpegVersion) {
		this.mpegVersion = mpegVersion;
	}
	
	/**
	 * @param padding the padding to set
	 */
	protected void setPadding(boolean padding) {
		this.padding = padding;
	}
	
	/**
	 * @param privacy the privacy to set
	 */
	protected void setPrivacy(boolean privacy) {
		this.privacy = privacy;
	}
	
	/**
	 * @param protection the protection to set
	 */
	protected void setProtection(boolean protection) {
		this.protection = protection;
	}
	
	/**
	 * @param variableBitRate the variableBitRate to set
	 */
	protected void setVariableBitRate(boolean variableBitRate) {
		this.variableBitRate = variableBitRate;
	}

}
