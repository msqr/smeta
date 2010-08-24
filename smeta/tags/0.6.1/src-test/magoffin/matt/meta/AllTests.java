/* ===================================================================
 * AllTests.java
 * 
 * Created Jan 17, 2007 3:05:30 PM
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

import junit.framework.Test;
import junit.framework.TestSuite;
import magoffin.matt.meta.audio.AudioMetadataTypeTest;
import magoffin.matt.meta.audio.ID3MetadataResourceFactoryTest;
import magoffin.matt.meta.image.EXIFMetadataResourceFactoryTest;
import magoffin.matt.meta.image.ImageMetadataTypeTest;
import magoffin.matt.meta.support.AbstractEnumMetadataResourceTest;
import magoffin.matt.meta.support.AbstractMetadataResourceTest;

/**
 * Test suite for all unit tests.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class AllTests {

	/**
	 * Get a test suite.
	 * @return suite
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for magoffin.matt.meta");
		// $JUnit-BEGIN$
		suite.addTestSuite(MetadataResourceFactoryManagerTest.class);
		suite.addTestSuite(AbstractMetadataResourceTest.class);
		suite.addTestSuite(AbstractEnumMetadataResourceTest.class);
		suite.addTestSuite(AudioMetadataTypeTest.class);
		suite.addTestSuite(ID3MetadataResourceFactoryTest.class);
		suite.addTestSuite(ImageMetadataTypeTest.class);
		suite.addTestSuite(EXIFMetadataResourceFactoryTest.class);
		suite.addTestSuite(EnvironmentResourcesTest.class);
		// $JUnit-END$
		return suite;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

}
