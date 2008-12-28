/* ===================================================================
 * PNGMetadataResource.java
 * 
 * Created Feb 5, 2007 3:54:11 PM
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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.support.AbstractEnumMetadataResource;

import org.w3c.dom.Node;

/**
 * {@link MetadataResource} for PNG images.
 * 
 * <p>The following text chunk keywords are recognized (in each case
 * only the first occurance of the chunk with the specified keyword
 * is used):</p>
 * 
 * <dl>
 *   <dt>Title</dt>
 *   <dd>The {@link ImageMetadataType#TITLE}.</dd>
 *   
 *   <dt>Description</dt>
 *   <dd>The {@link ImageMetadataType#COMMENT}.</dd>
 *   
 *   <dt>Creation Time</dt>
 *   <dd>Parsed as {@link ImageMetadataType#DATE_TAKEN} using the 
 *   {@link #getDateFormat()} date format, which defaults to 
 *   {@link #DEFAULT_PNG_DATE_FORMAT}.</dd>
 * </dl>
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class PNGMetadataResource 
extends AbstractEnumMetadataResource<ImageMetadataType>
implements MetadataResource {
	
	/** The PNG MIME type. */
	public static final String PNG_MIME = "image/png";
	
	/** The default date format for PNG dates. */
	public static final String DEFAULT_PNG_DATE_FORMAT = "d MMM yyyy H:mm:ss";

	/** the JAXP XPathFactory to use for getting XPath evaulators */
	private XPathFactory xpathFactory;
	private ImageReader reader;
	private String dateFormat = DEFAULT_PNG_DATE_FORMAT;
	
	/**
	 * Constructor.
	 * @param in the input stream to the PNG data
	 */
	public PNGMetadataResource(InputStream in) {
		reader = getReaderForMIME(PNG_MIME, in);
		xpathFactory = XPathFactory.newInstance();
		try {
			parseChunks();
		} catch ( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	

	private void parseChunks() throws IOException {
		// try to get date from PNG tEXT chunk
		IIOMetadata meta = reader.getImageMetadata(0);
		if ( meta != null ) {
			String formatName = meta.getNativeMetadataFormatName();
			Node dom = meta.getAsTree(formatName);
			if ( log.isDebugEnabled() ) {
				debugXml("ImageIO meta DOM: ", new DOMSource(dom));
			}
			
			// look for creation time, with tEXtEntry[@keyword='Creation Time']
			try {
				String dateStr = (String)evaluateXPath(dom, 
						"((//tEXtEntry[@keyword='Creation Time'])[1]/@value|(//zTXtEntry[@keyword='Creation Time'])[1]/@text)[1]",
						XPathConstants.STRING);
				if ( dateStr != null && dateStr.length() > 0 ) {
					SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
					Date d = sdf.parse(dateStr);
					addValue(ImageMetadataType.DATE_TAKEN, d);
				} else {
					if ( log.isDebugEnabled() ) {
						log.debug("No creation time node found in PNG metadata");
					}
				}
			} catch ( ParseException e ) {
				if ( log.isDebugEnabled() ) {
					log.debug("Exception parsing PNG date with pattern [" 
							+dateFormat +"]: " +e.toString());
				}
			}
			
			// look for title
			String title = (String)evaluateXPath(dom,
					"((//tEXtEntry[@keyword='Title'])[1]/@value|(//zTXtEntry[@keyword='Title'])[1]/@text)[1]", 
					XPathConstants.STRING);
			if ( title != null && title.length() > 0 ) {
				addValue(ImageMetadataType.TITLE, title);
			}
			
			String desc = (String)evaluateXPath(dom,
					"((//tEXtEntry[@keyword='Description'])[1]/@value|(//zTXtEntry[@keyword='Description'])[1]/@text)[1]", 
					XPathConstants.STRING);
			if ( desc != null && desc.length() > 0 ) {
				addValue(ImageMetadataType.COMMENT, desc);
			}
		}

	}
	
	private ImageReader getReaderForMIME(String mime, InputStream in) {
		Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(mime);
		if ( !readers.hasNext() ) {
			throw new RuntimeException("MIME type [" +mime 
					+"] not registered for reading in ImageIO");
		}
		ImageReader imageReader = readers.next();
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(in);
			imageReader.setInput(iis);
			return imageReader;
		} catch ( IOException e ) {
			throw new RuntimeException("IOException reading image input stream for [" 
					+mime +"]",e);
		}
	}

	private Object evaluateXPath(Object object, String xpath, QName returnType) {
		try {
			XPath xpathx  = xpathFactory.newXPath();
			return xpathx.evaluate(xpath, object, returnType);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("Error evaluating XPath [" 
					+xpath+ "]", e);
		}
	}

	private void debugXml(String notice, Source xml) {
		if ( !log.isDebugEnabled() ) return;
		try {
			StringWriter writer = new StringWriter();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			if ( transformer.getURIResolver() == null
					&& transformerFactory.getURIResolver() != null ) {
				// See http://issues.apache.org/jira/browse/XALANJ-1131
				transformer.setURIResolver(transformerFactory.getURIResolver());
			}
			transformer.transform(xml, new StreamResult(writer));
			log.debug(notice +writer.toString());
		} catch ( TransformerException e ) {
			log.warn("TransformerException debugging XML", e);
		}
	}
	
	/**
	 * @return the xpathFactory
	 */
	public XPathFactory getXpathFactory() {
		return xpathFactory;
	}
	
	/**
	 * @param xpathFactory the xpathFactory to set
	 */
	public void setXpathFactory(XPathFactory xpathFactory) {
		this.xpathFactory = xpathFactory;
	}
	
	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

}
