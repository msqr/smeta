/* ===================================================================
 * AbstractEnumMetadataResourceTest.java
 * 
 * Created Jan 17, 2007 4:22:02 PM
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
 * Unit test for the {@link AbstractEnumMetadataResource} class.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class AbstractEnumMetadataResourceTest extends TestCase {

	/**
	 * Test attempting to get a non-existing value.
	 */
	public void testNonExistingValue() {
		MockEnumMetadataResource mResource = new MockEnumMetadataResource();
		
		Object o = mResource.getValue(MockMetadataType.KEY1, Locale.US);
		assertNull(o);
		
		mResource.addValue(MockMetadataType.KEY1, "value");

		 o = mResource.getValue("not", Locale.US);
		assertNull(o);
	}
	
	/**
	 * Test attempting to get a non-existing value.
	 */
	public void testNonExistingValues() {
		MockEnumMetadataResource mResource = new MockEnumMetadataResource();
		
		Iterable<?> list = mResource.getValues(MockMetadataType.KEY1, Locale.US);
		assertNotNull(list);
		assertFalse(list.iterator().hasNext());
	}
	
	/**
	 * Test setting a single value.
	 */
	public void testSetValue() {
		MockEnumMetadataResource mResource = new MockEnumMetadataResource();
		mResource.setValue(MockMetadataType.KEY1, "value");
		
		Object o = mResource.getValue(MockMetadataType.KEY1, Locale.US);
		assertNotNull(o);
		assertEquals("value", o);
	}
	
	/**
	 * Test replacing a single value.
	 */
	public void testReplaceValue() {
		MockEnumMetadataResource mResource = new MockEnumMetadataResource();
		mResource.setValue(MockMetadataType.KEY1, "value");
		mResource.setValue(MockMetadataType.KEY1, "value2");
		
		Object o = mResource.getValue(MockMetadataType.KEY1, Locale.US);
		assertNotNull(o);
		assertEquals("value2", o);
	}
	
	/**
	 * Test setting a list of values.
	 */
	public void testSetValues() {
		MockEnumMetadataResource mResource = new MockEnumMetadataResource();
		Object[] in = new Object[]{"value1","value2","value3"};
		mResource.setValues(MockMetadataType.KEY1, Arrays.asList(in));
		
		Iterable<?> list = mResource.getValues(MockMetadataType.KEY1, Locale.US);
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
		MockEnumMetadataResource mResource = new MockEnumMetadataResource();
		Object[] in = new Object[]{"value1","value2","value3"};
		mResource.setValues(MockMetadataType.KEY1, Arrays.asList(in));
		in = new Object[]{"value4",new Integer(5),"value6"};
		mResource.setValues(MockMetadataType.KEY1, Arrays.asList(in));
		
		Iterable<?> list = mResource.getValues(MockMetadataType.KEY1, Locale.US);
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
		MockEnumMetadataResource mResource = new MockEnumMetadataResource();
		Map<String, List<String>> errors = mResource.getParseErrors();
		assertNotNull(errors);
		assertEquals(0, errors.size());
	}
	
	/**
	 * Test add a single error.
	 */
	public void testAddError() {
		MockEnumMetadataResource mResource = new MockEnumMetadataResource();
		String err = "This is an error";
		mResource.addMetadataError(MockMetadataType.KEY1, err);
		
		Map<String, List<String>> errors = mResource.getParseErrors();
		assertNotNull(errors);
		assertTrue(errors.containsKey(MockMetadataType.KEY1.name()));
		List<String> errList = errors.get(MockMetadataType.KEY1.name());
		assertNotNull(errList);
		assertEquals(1, errList.size());
		assertEquals(err, errList.get(0));
	}
	
	/**
	 * Test add a multiple errors.
	 */
	public void testAddErrors() {
		MockEnumMetadataResource mResource = new MockEnumMetadataResource();
		String err1 = "This is an error";
		String err2 = "This is another error";
		String err3 = "This is yet another error";
		mResource.addMetadataError(MockMetadataType.KEY1, err1);
		mResource.addMetadataError(MockMetadataType.KEY2, err2);
		mResource.addMetadataError(MockMetadataType.KEY1, err3);
		
		Map<String, List<String>> errors = mResource.getParseErrors();
		assertNotNull(errors);
		assertTrue(errors.containsKey(MockMetadataType.KEY1.name()));
		List<String> errList = errors.get(MockMetadataType.KEY1.name());
		assertNotNull(errList);
		assertEquals(2, errList.size());
		assertEquals(err1, errList.get(0));
		assertEquals(err3, errList.get(1));

		assertTrue(errors.containsKey(MockMetadataType.KEY2.name()));
		errList = errors.get(MockMetadataType.KEY2.name());
		assertNotNull(errList);
		assertEquals(1, errList.size());
		assertEquals(err2, errList.get(0));
	}
	

}
