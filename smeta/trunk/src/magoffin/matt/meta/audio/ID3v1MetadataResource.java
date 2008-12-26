/* ===================================================================
 * ID3v1MetadataResource.java
 * 
 * Created Jan 16, 2007 11:05:13 AM
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
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.support.AbstractEnumMetadataResource;

/**
 * {@link MetadataResource} implementation for ID3v1 resources.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class ID3v1MetadataResource 
extends AbstractEnumMetadataResource<AudioMetadataType>
implements AudioMetadataResource {
	
	/**
	 * Construct with a RandomAccessFile.
	 * 
	 * @param file the file
	 * @throws IOException if an error occurs
	 */
	public ID3v1MetadataResource(RandomAccessFile file) throws IOException {
		parseFile(file);
	}
	
	/**
	 * Parse ID3v1 metadata from a file.
	 * 
	 * @param file the file
	 * @throws IOException if an IO error occurs
	 */
	protected void parseFile(RandomAccessFile file) throws IOException {
		file.seek(file.length() - 125);
		byte[] buffer = new byte[30];
		
		file.read(buffer, 0, 30);
		setValue(AudioMetadataType.SONG_NAME,
				new String(buffer, 0, 30, 
				ID3MetadataResourceFactory.ISO_8859_1_CHARSET).trim());
		
		file.read(buffer, 0, 30);
		setValue(AudioMetadataType.ARTIST,
				new String(buffer, 0, 30, 
				ID3MetadataResourceFactory.ISO_8859_1_CHARSET).trim());
		
		file.read(buffer, 0, 30);
		setValue(AudioMetadataType.ALBUM,
				new String(buffer, 0, 30, 
				ID3MetadataResourceFactory.ISO_8859_1_CHARSET).trim());
		
		file.read(buffer, 0, 4);
		String yearStr = new String(buffer, 0, 4, 
				ID3MetadataResourceFactory.ISO_8859_1_CHARSET).trim();
		if ( yearStr.length() > 0 ) {
			try {
				Integer year = Integer.valueOf(yearStr);
				setValue(AudioMetadataType.YEAR, year);
			} catch ( Exception e ) {
				addError(AudioMetadataType.YEAR, "Unable to parse year from [" 
						+yearStr +"]");
			}
		}
		
		file.read(buffer, 0, 30);
		setValue(AudioMetadataType.COMMENT,
				new String(buffer, 0, 30, 
				ID3MetadataResourceFactory.ISO_8859_1_CHARSET).trim());
		
		Byte genre = file.readByte();
		setValue(AudioMetadataType.GENRE, genre);
	}

	@Override
	public Object getValue(String key, Locale locale) {
		Object result = super.getValue(key, locale);
		if ( AudioMetadataType.GENRE.name().equals(key) ) {
			ResourceBundle bundle = null;
			try {
				bundle = ResourceBundle.getBundle("id3v1", locale);
			} catch ( MissingResourceException e ) {
				// fall back to bundle provided in package
				bundle = ResourceBundle.getBundle(
						ID3v1MetadataResource.class.getName(), locale);
			}
			try {
				String genreName = bundle.getString("genre."+result);
				if ( genreName != null ) {
					return genreName;
				}
			} catch ( MissingResourceException e ) {
				if ( log.isDebugEnabled() ) {
					log.debug("Unable to get genre [genre." +result 
							+"] from bundle [" +bundle +"]");
				}
			}
		}
		return super.getValue(key, locale);
	}

}
