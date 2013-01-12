/* ===================================================================
 * MetadataConfigurationException.java
 * 
 * Created Jan 16, 2007 8:01:22 AM
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

package magoffin.matt.meta;

import java.io.File;

/**
 * Exception thrown when configuration errors occur.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class MetadataNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = -7309634515113272523L;

	private File metadataFile;
	private MetadataResourceFactory factory;

	/**
	 * Default constructor.
	 */
	public MetadataNotSupportedException() {
		super();
	}
	
	/**
	 * Construct from a file and factory.
	 * 
	 * @param metadataFile the metadata file that caused the exception
	 * @param factory the factory that threw the exception
	 */
	public MetadataNotSupportedException(File metadataFile, MetadataResourceFactory factory) {
		this.metadataFile = metadataFile;
		this.factory = factory;
	}
	
	/**
	 * Construct from a file and factory with nested Throwable.
	 * 
	 * @param metadataFile the metadata file that caused the exception
	 * @param factory the factory that threw the exception
	 * @param cause the cause of the error
	 */
	public MetadataNotSupportedException(File metadataFile, MetadataResourceFactory factory, 
			Throwable cause) {
		super(cause);
		this.metadataFile = metadataFile;
		this.factory = factory;
	}
	
	/**
	 * @return the factory
	 */
	public MetadataResourceFactory getFactory() {
		return factory;
	}
	
	/**
	 * @return the metadataFile
	 */
	public File getMetadataFile() {
		return metadataFile;
	}

}
