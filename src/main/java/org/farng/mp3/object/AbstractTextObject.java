/* ===================================================================
 * AbstractTextObject.java
 * 
 * Created Jan 21, 2007 8:36:36 PM
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

package org.farng.mp3.object;

/**
 * Base implementation for text-based text objects, to handle text
 * encoding.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public abstract class AbstractTextObject extends AbstractMP3Object {
	
	public AbstractTextObject() {
		super();
	}
	
	public AbstractTextObject(AbstractTextObject copy) {
		super(copy);
	}
	
	/**
	 * Read a String from a byte array.
	 * 
	 * <p>The byte at <code>offset - 1</code> will be used as the 
	 * encoding flag bit.</p>
	 * 
	 * @param data the input data
	 * @param offset the input offset
	 */
    public void readByteArray(final byte[] data, final int offset) {
    	// byte 0 is the encoding scheme
    	int encoding = offset > 0 ? data[offset-1] : 0;
    	String javaEncoding = "ISO-8859-1";
    	switch ( encoding ) {
    		case 0:
    			javaEncoding = "ISO-8859-1";
    			break;
    			
    		case 1:
    		case 2:
    			javaEncoding = "UTF-16";
    			break;
    			
    		case 3:
    			javaEncoding = "UTF-8";
    			break;
    	}
    	
    	readString(data, offset, javaEncoding);
    }
    
    /**
     * Read a String from offset to the length of the data array.
     * 
     * <p>This method will look for a null character, and return the 
     * substring up to that character if found.</p>
     * 
     * @param data the input data
     * @param offset the starting offset
     * @param javaEncoding the text encoding
     */
	protected void readString(byte[] data, int offset, String javaEncoding) {
    	String str;
    	try {
    		str = new String(data, offset, data.length-offset, javaEncoding);
    	} catch ( Exception e ) {
    		throw new RuntimeException(e);
    	}
    	// look for null character
    	int nullIdx = str.indexOf(0);
    	if ( nullIdx != -1 ) {
    		str = str.substring(0, nullIdx);
    	}
    	setValue(str);
	}

}
