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

/**
 * Exception thrown when configuration errors occur.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class MetadataConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 6483450025883561945L;

	/**
	 * Default constructor.
	 */
	public MetadataConfigurationException() {
		super();
	}

	/**
	 * Construct with a message.
	 * @param msg the message
	 */
	public MetadataConfigurationException(String msg) {
		super(msg);
	}

	/**
	 * Construct with a nested Throwable.
	 * @param e the nested Throwable
	 */
	public MetadataConfigurationException(Throwable e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Construct with a message and nested Throwable.
	 * @param msg the message
	 * @param e the nested Throwable
	 */
	public MetadataConfigurationException(String msg, Throwable e) {
		super(msg, e);
	}

}
