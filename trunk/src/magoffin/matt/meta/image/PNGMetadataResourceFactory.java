/* ===================================================================
 * PNGMetadataResourceFactory.java
 * 
 * Created Feb 5, 2007 4:27:31 PM
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

package magoffin.matt.meta.image;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import magoffin.matt.meta.MetadataNotSupportedException;
import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.MetadataResourceFactory;

/**
 * {@link MetadataResourceFactory} for PNG metadata.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class PNGMetadataResourceFactory implements MetadataResourceFactory {

	public MetadataResource getMetadataResourceInstance(File file) 
	throws IOException, MetadataNotSupportedException {
		try {
			return new PNGMetadataResource(new BufferedInputStream(
					new FileInputStream(file)));
		} catch ( Exception e ) {
			throw new MetadataNotSupportedException(file, this, e);
		}
	}

}
