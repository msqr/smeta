/* ===================================================================
 * AbstractVideoMetadataResource.java
 * 
 * Created Jan 30, 2007 10:43:33 AM
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
 */

package magoffin.matt.meta.video;

import magoffin.matt.meta.support.AbstractEnumMetadataResource;

/**
 * Abstract implementation of {@link VideoMetadataResource}.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version 1.1
 */
public abstract class AbstractVideoMetadataResource
		extends AbstractEnumMetadataResource<VideoMetadataType> implements VideoMetadataResource {

	/**
	 * Get a time duration value from a duration value.
	 * 
	 * @param ms
	 *        the duration, in milliseconds
	 * @return the duration, as a hh:mm:ss.SSS string
	 */
	protected String getDurationTime(long ms) {
		return VideoUtils.durationTime(ms);
	}

}
