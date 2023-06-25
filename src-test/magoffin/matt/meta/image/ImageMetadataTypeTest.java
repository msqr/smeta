/* ===================================================================
 * ImageMetadataTypeTest.java
 * 
 * Created Jan 17, 2007 4:39:51 PM
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

import static org.junit.Assert.assertEquals;
import java.util.Date;
import org.junit.Test;

/**
 * Test case for the {@link ImageMetadataType} class.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class ImageMetadataTypeTest {

	/**
	 * Test date object type.
	 */
	@Test
	public void testDateObjectType() {
		assertEquals(Date.class, ImageMetadataType.DATE_TAKEN.getObjectType());
	}

}
