/* ===================================================================
 * ID3v1_1MetadataResource.java
 * 
 * Created Jan 16, 2007 4:09:34 PM
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

/**
 * {@link MetadataResource} implementation for ID3v1.1 resources.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class ID3v1_1MetadataResource extends ID3v1MetadataResource {

	/**
	 * Construct with a RandomAccessFile.
	 * 
	 * @param file the file
	 * @throws IOException if an error occurs
	 */
	public ID3v1_1MetadataResource(RandomAccessFile file) throws IOException {
		super(file);
	}

	@Override
	protected void parseFile(RandomAccessFile file) throws IOException {
		super.parseFile(file);
		
		byte[] buffer = new byte[30];
		
		// redo comments to length 28 string
		file.seek(file.length() - 31);
		file.read(buffer, 0, 30);
		setValue(AudioMetadataType.COMMENT,
				new String(buffer, 0, 28, 
				ID3MetadataResourceFactory.ISO_8859_1_CHARSET).trim());
		
		// set track number
		Integer trackNum = (int)buffer[29];
		if ( trackNum > 0 ) {
			setValue(AudioMetadataType.TRACK_NUM, trackNum);
		}
	}
	
}
