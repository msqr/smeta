/* ===================================================================
 * ID3v2_3MetadataResource.java
 * 
 * Created Jan 18, 2007 9:29:25 PM
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

import java.io.IOException;
import java.io.RandomAccessFile;

import magoffin.matt.meta.MetadataResource;

import org.farng.mp3.MP3Tag;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v2_4;

/**
 * {@link MetadataResource} implementation for ID3v2.3 resources.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class ID3v2_4MetadataResource extends ID3v2_3MetadataResource {
	
	/**
	 * Construct with a RandomAccessFile.
	 * 
	 * @param file the file
	 * @throws IOException if an error occurs
	 */
	public ID3v2_4MetadataResource(RandomAccessFile file) throws IOException {
		super(file);
	}
	
	/**
	 * Get a {@link MP3Tag} implementation for a file.
	 * 
	 * @param file the file
	 * @throws IOException if an IO error occurs
	 * @return the MP3Tag implementation
	 */
	@Override
	protected MP3Tag getMP3Tag(RandomAccessFile file) throws IOException {
		MP3Tag id3 = null;
		try {
			id3 = new ID3v2_4(file);
		} catch ( TagException e ) {
			throw new RuntimeException(e);
		}
		return id3;
	}

}
