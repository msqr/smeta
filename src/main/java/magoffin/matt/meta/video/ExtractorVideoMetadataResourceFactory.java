/* ===================================================================
 * ExtractorVideoMetadataResourceFactory.java
 * 
 * Created 25/06/2023 2:32:45 pm
 * 
 * Copyright (c) 2023 Matt Magoffin.
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
 */

package magoffin.matt.meta.video;

import java.io.File;
import java.io.IOException;
import magoffin.matt.meta.MetadataNotSupportedException;
import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.MetadataResourceFactory;

/**
 * {@link MetadataResourceFactory} for video metadata using the MP4 format using
 * Metadata Extractor.
 *
 * @author matt
 * @version 1.0
 */
public class ExtractorVideoMetadataResourceFactory implements MetadataResourceFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * magoffin.matt.meta.MetadataResourceFactory#getMetadataResourceInstance(
	 * java.io.File)
	 */
	public MetadataResource getMetadataResourceInstance(File file)
			throws IOException, MetadataNotSupportedException {
		try {
			return new MP4ExtractorMetadataResource(file);
		} catch ( IOException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new MetadataNotSupportedException(file, this);
		}
	}

}
