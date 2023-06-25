/* ===================================================================
 * VideoUtils.java
 * 
 * Created 25/06/2023 6:26:41 pm
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

/**
 * Video utilities.
 *
 * @author matt
 * @version 1.0
 */
public final class VideoUtils {

	private VideoUtils() {
		// not available
	}

	/**
	 * Get a time duration value from a duration value.
	 * 
	 * @param ms
	 *        the duration, in milliseconds
	 * @return the duration, as a hh:mm:ss.SSS string
	 */
	public static final String durationTime(long ms) {
		StringBuilder buf = new StringBuilder();
		int hours = (int) Math.floor(ms / 3600000d);
		int mins = (int) Math.floor((ms / 60000d) - (hours * 60));
		int secs = (int) Math.floor((ms / 1000) - (hours * 60 * 60) - (mins * 60));
		int frac = (int) (ms - (hours * 60 * 60 * 1000) - (mins * 60 * 1000) - (secs * 1000));
		if ( hours > 0 ) {
			buf.append(hours).append(":");
		}
		if ( mins < 10 && hours > 0 ) {
			buf.append("0");
		}
		buf.append(mins);
		buf.append(":");
		if ( secs < 10 ) {
			buf.append("0");
		}
		buf.append(secs);
		if ( frac > 0 ) {
			buf.append(".").append(frac);
		}
		return buf.toString();
	}

}
