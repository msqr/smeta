/* ===================================================================
 * MP3AudioMetadata.java
 * 
 * Created Jan 19, 2007 8:36:54 AM
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

/**
 * API for metadata about an MPEG audio stream
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public interface MP3AudioMetadata {

	/**
	 * @return the bitRate
	 */
	public int getBitRate();

	/**
	 * @return the copyProtected
	 */
	public boolean isCopyProtected();

	/**
	 * @return the emphasis
	 */
	public byte getEmphasis();

	/**
	 * @return the frequency
	 */
	public double getFrequency();

	/**
	 * @return the home
	 */
	public boolean isHome();

	/**
	 * @return the layer
	 */
	public byte getLayer();

	/**
	 * @return the mode
	 */
	public byte getMode();

	/**
	 * @return the modeExtension
	 */
	public byte getModeExtension();

	/**
	 * @return the mpegVersion
	 */
	public byte getMpegVersion();

	/**
	 * @return the padding
	 */
	public boolean isPadding();

	/**
	 * @return the privacy
	 */
	public boolean isPrivacy();

	/**
	 * @return the protection
	 */
	public boolean isProtection();

	/**
	 * @return the variableBitRate
	 */
	public boolean isVariableBitRate();
	

}
