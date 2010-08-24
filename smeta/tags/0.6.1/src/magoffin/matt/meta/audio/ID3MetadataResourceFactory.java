/* ===================================================================
 * ID3MetadataResourceFactory.java
 * 
 * Created Jan 16, 2007 11:04:09 AM
 * 
 * Copyright (c) 2007 Matt Magoffin (spamsqr@msqr.us)
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA
 * ===================================================================
 * $Id$
 * ===================================================================
 */

package magoffin.matt.meta.audio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import magoffin.matt.meta.MetadataNotSupportedException;
import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.MetadataResourceFactory;

/**
 * {@link MetadataResourceFactory} for ID3 metadata, eg. MP3 audio.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class ID3MetadataResourceFactory implements MetadataResourceFactory {
	
	/** The Java ISO-8859-1 Charset name. */
	public static final String ISO_8859_1_CHARSET = "ISO-8859-1";

	/* (non-Javadoc)
	 * @see magoffin.matt.meta.MetadataResourceFactory#getMetadataResourceInstance(java.io.File)
	 */
	public MetadataResource getMetadataResourceInstance(File file) 
	throws IOException, MetadataNotSupportedException {
		RandomAccessFile rFile = new RandomAccessFile(file, "r");
		
		MetadataResource result = getID3v2(rFile);
		if ( result != null ) {
			return result;
		}

		if ( isID3v1_1(rFile) ) {
			return new ID3v1_1MetadataResource(rFile);
		} else if ( isID3v1(rFile) ) {
			return new ID3v1MetadataResource(rFile);
		}
		
		throw new MetadataNotSupportedException(file, this);
	}
	
	private boolean isID3v1(RandomAccessFile file) throws IOException {
		if ( file.length() < 128 ) {
			return false;
		}
		file.seek(file.length() - 128);
		byte[] buffer = new byte[3];
		file.read(buffer, 0, 3);
		return "TAG".equals(new String(buffer, 0, 3, ISO_8859_1_CHARSET));
	}

	private boolean isID3v1_1(RandomAccessFile file) throws IOException {
		if ( !isID3v1(file) ) {
			return false;
		}
		
		// check for 0 character at byte at position +125
		// followed by non-0 character at byte 126
		file.seek(file.length() - 3);
		return file.readByte() == 0 && file.readByte() != 0;
	}
	
	private MetadataResource getID3v2(RandomAccessFile file) 
	throws IOException {
		byte[] buffer = new byte[3];
		file.seek(0);
		
		// read the tag if it exists
		file.read(buffer, 0, 3);
		String tag = new String(buffer, 0, 3);
		if ( !"ID3".equals(tag) ) {
		    return null;
		}
		
		// read the major version number
		int major = file.read();
		if ( major == 4 ) {
			return new ID3v2_4MetadataResource(file);
		} else if ( major == 3 ) {
			return new ID3v2_3MetadataResource(file);
		} else if ( major == 2 ) {
			return new ID3v2_2MetadataResource(file);
		}
		
		return null;
	}

}
