/* ===================================================================
 * MetadataResourceFactoryTest.java
 * 
 * Created Jan 16, 2007 8:07:54 AM
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

import junit.framework.TestCase;

/**
 * Test case for the {@link MetadataResourceFactoryManager} class.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class MetadataResourceFactoryManagerTest extends TestCase {

	/**
	 * Test able to construct class.
	 */
	public void testConstruct() {
		MetadataResourceFactoryManager manager = new MetadataResourceFactoryManager();
		assertNull(manager.getManagerProperties());
	}
	
	/**
	 * Test able to instantiate factory for a File.
	 */
	public void testInstantiateFactoryForFile() {
		MetadataResourceFactoryManager manager = new MetadataResourceFactoryManager();
		manager.setManagerProperties("magoffin/matt/meta/smeta.properties");
		MetadataResourceFactory factory = manager.getMetadataResourceFactory(
				new File("mock.test"));
		assertNotNull(factory);
		
		// now get again, to test retrieving from cache
		MetadataResourceFactory factory2 = manager.getMetadataResourceFactory(
				new File("foo.test"));
		assertNotNull(factory2);
		assertSame(factory.getClass(), factory2.getClass());

		// now get again, to test retrieving with different case
		factory2 = manager.getMetadataResourceFactory(
				new File("FOO.TEST"));
		assertNotNull(factory2);
		assertSame(factory.getClass(), factory2.getClass());
	}
	
	/**
	 * Test able to instantiate factory for a MIME type.
	 */
	public void testInstantiateFactoryForMIME() {
		MetadataResourceFactoryManager manager = new MetadataResourceFactoryManager();
		manager.setManagerProperties("magoffin/matt/meta/smeta.properties");
		MetadataResourceFactory factory = manager.getMetadataResourceFactory("foo/mime");
		assertNotNull(factory);
		
		// now get again, to test retrieving from cache
		MetadataResourceFactory factory2 = manager.getMetadataResourceFactory("foo/mime");
		assertNotNull(factory2);
		assertSame(factory.getClass(), factory2.getClass());

		// now get again, to test retrieving with different case
		factory2 = manager.getMetadataResourceFactory("FOO/MIME");
		assertNotNull(factory2);
		assertSame(factory.getClass(), factory2.getClass());
	}
	
	/**
	 * Test able to instantiate factory for a MIME type and file.
	 */
	public void testInstantiateFactoryForMIMEAndFile() {
		MetadataResourceFactoryManager manager = new MetadataResourceFactoryManager();
		manager.setManagerProperties("magoffin/matt/meta/smeta.properties");
		MetadataResourceFactory factory = manager.getMetadataResourceFactory("foo/mime");
		assertNotNull(factory);
		
		// now get again, to test retrieving via file
		MetadataResourceFactory factory2 = manager.getMetadataResourceFactory(
				new File("foo.test"));
		assertNotNull(factory2);
		assertSame(factory.getClass(), factory2.getClass());
	}
	
	/**
	 * Test exception thrown for missing configuration.
	 */
	public void testMissingConfiguration() {
		MetadataResourceFactoryManager manager = new MetadataResourceFactoryManager();
		manager.setManagerProperties("does/not/exist/smeta.properties");
		manager.setDisableSearchPath(true);
		try {
			manager.getMetadataResourceFactory(new File("mock.test"));
			fail("Should have thrown exception for missing configuration");
		} catch ( MetadataConfigurationException e ) {
			assertNotNull(e.getMessage());
		}
	}
	
	/**
	 * Test null factory returned for missing factory configuration.
	 */
	public void testMissingFactoryConfiguration() {
		MetadataResourceFactoryManager manager = new MetadataResourceFactoryManager();
		manager.setManagerProperties("magoffin/matt/meta/smeta.properties");
		manager.setDisableSearchPath(true);
		MetadataResourceFactory factory = manager.getMetadataResourceFactory(
				new File("mock.foo"));
		assertNull(factory);
	}
	
	/**
	 * Test exception thrown for bad factory configuration.
	 */
	public void testBadFactoryConfiguration() {
		MetadataResourceFactoryManager manager = new MetadataResourceFactoryManager();
		manager.setManagerProperties("magoffin/matt/meta/smeta.properties");
		manager.setDisableSearchPath(true);
		try {
			manager.getMetadataResourceFactory(new File("mock.bad"));
			fail("Should have thrown exception for bad configuration");
		} catch ( MetadataConfigurationException e ) {
			assertNotNull(e.getMessage());
		}
	}
	
	/**
	 * Test able to construct default manager instance.
	 */
	public void testDefaultInstance() {
		MetadataResourceFactoryManager manager 
			= MetadataResourceFactoryManager.getDefaultManagerInstance();
		assertNotNull(manager);
		MetadataResourceFactoryManager manager2
			= MetadataResourceFactoryManager.getDefaultManagerInstance();
		assertSame(manager, manager2);
	}
	
}
