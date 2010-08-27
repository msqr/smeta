/* ===================================================================
 * AbstractMetadataResourceTest.java
 * 
 * Created Jan 17, 2007 3:51:07 PM
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

package magoffin.matt.meta.support;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Unit test for the {@link AbstractMetadataResource} class.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class AbstractMetadataResourceTest extends TestCase {

	/**
	 * Test attempting to get a non-existing value.
	 */
	public void testNonExistingValue() {
		MockMetadataResource mResource = new MockMetadataResource();
		
		Object o = mResource.getValue("key", Locale.US);
		assertNull(o);

		 o = mResource.getValue("not", Locale.US);
		assertNull(o);
	}
	
	/**
	 * Test attempting to get a non-existing value.
	 */
	public void testNonExistingValues() {
		MockMetadataResource mResource = new MockMetadataResource();
		
		Iterable<?> list = mResource.getValues("key", Locale.US);
		assertNotNull(list);
		assertFalse(list.iterator().hasNext());
	}
	
	/**
	 * Test setting a single value.
	 */
	public void testSetValue() {
		MockMetadataResource mResource = new MockMetadataResource();
		mResource.setValue("key", "value");
		
		Object o = mResource.getValue("key", Locale.US);
		assertNotNull(o);
		assertEquals("value", o);
	}
	
	/**
	 * Test replacing a single value.
	 */
	public void testReplaceValue() {
		MockMetadataResource mResource = new MockMetadataResource();
		mResource.setValue("key", "value");
		mResource.setValue("key", "value2");
		
		Object o = mResource.getValue("key", Locale.US);
		assertNotNull(o);
		assertEquals("value2", o);
	}
	
	/**
	 * Test setting a list of values.
	 */
	public void testSetValues() {
		MockMetadataResource mResource = new MockMetadataResource();
		Object[] in = new Object[]{"value1","value2","value3"};
		mResource.setValues("key", Arrays.asList(in));
		
		Iterable<?> list = mResource.getValues("key", Locale.US);
		assertNotNull(list);
		int idx = 0;
		for ( Object o : list ) {
			assertEquals(in[idx++], o);
		}
	}
	
	/**
	 * Test replacing a list of values.
	 */
	public void testReplaceValues() {
		MockMetadataResource mResource = new MockMetadataResource();
		Object[] in = new Object[]{"value1","value2","value3"};
		mResource.setValues("key", Arrays.asList(in));
		in = new Object[]{"value4",new Integer(5),"value6"};
		mResource.setValues("key", Arrays.asList(in));
		
		Iterable<?> list = mResource.getValues("key", Locale.US);
		assertNotNull(list);
		int idx = 0;
		for ( Object o : list ) {
			assertEquals(in[idx++], o);
		}
	}
	
	/**
	 * Test no errors handled properly.
	 */
	public void testNoErrors() {
		MockMetadataResource mResource = new MockMetadataResource();
		Map<String, List<String>> errors = mResource.getParseErrors();
		assertNotNull(errors);
		assertEquals(0, errors.size());
	}
	
	/**
	 * Test add a single error.
	 */
	public void testAddError() {
		MockMetadataResource mResource = new MockMetadataResource();
		String err = "This is an error";
		mResource.addMetadataError("key", err);
		
		Map<String, List<String>> errors = mResource.getParseErrors();
		assertNotNull(errors);
		assertTrue(errors.containsKey("key"));
		List<String> errList = errors.get("key");
		assertNotNull(errList);
		assertEquals(1, errList.size());
		assertEquals(err, errList.get(0));
	}
	
	/**
	 * Test add a multiple errors.
	 */
	public void testAddErrors() {
		MockMetadataResource mResource = new MockMetadataResource();
		String err1 = "This is an error";
		String err2 = "This is another error";
		String err3 = "This is yet another error";
		mResource.addMetadataError("key1", err1);
		mResource.addMetadataError("key2", err2);
		mResource.addMetadataError("key1", err3);
		
		Map<String, List<String>> errors = mResource.getParseErrors();
		assertNotNull(errors);
		assertTrue(errors.containsKey("key1"));
		List<String> errList = errors.get("key1");
		assertNotNull(errList);
		assertEquals(2, errList.size());
		assertEquals(err1, errList.get(0));
		assertEquals(err3, errList.get(1));

		assertTrue(errors.containsKey("key2"));
		errList = errors.get("key2");
		assertNotNull(errList);
		assertEquals(1, errList.size());
		assertEquals(err2, errList.get(0));
	}
	
}
