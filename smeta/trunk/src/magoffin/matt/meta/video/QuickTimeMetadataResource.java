/* ===================================================================
 * QuickTimeMetadataResource.java
 * 
 * Created Jan 29, 2007 1:12:18 PM
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

package magoffin.matt.meta.video;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;

import magoffin.matt.meta.MetadataConfigurationException;
import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.support.BasicMetadataImage;
import quicktime.QTException;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.qd.Pict;
import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.comp.ComponentDescription;
import quicktime.std.comp.ComponentIdentifier;
import quicktime.std.image.ImageDescription;
import quicktime.std.movies.Movie;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.Media;
import quicktime.std.movies.media.SoundDescription;
import quicktime.std.movies.media.SoundMedia;
import quicktime.std.movies.media.VideoMedia;
import quicktime.util.QTUtils;
import quicktime.util.RawEncodedImage;

/**
 * {@link MetadataResource} for video metadata using QuickTime.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class QuickTimeMetadataResource extends AbstractVideoMetadataResource {
	
	/**
	 * Construct with a QTFile.
	 * @param qtFile the QTFile
	 */
	public QuickTimeMetadataResource(QTFile qtFile) {
		try {
			extractMetadata(qtFile);
		} catch ( QTException e ) {
			log.error(e.errorCodeToString());
			throw new MetadataConfigurationException(e);
		}
	}
	
	/**
	 * Extract the video metadata from the QTFile.
	 * 
	 * @param qtFile the QTFile
	 * @throws QTException if an QuickTime error occurs
	 */
	protected void extractMetadata(QTFile qtFile) throws QTException {
		OpenMovieFile openMovieFile = OpenMovieFile.asRead(qtFile);
		Movie movie = Movie.fromFile(openMovieFile);
		QDRect box = movie.getBox();
		setValue(VideoMetadataType.WIDTH, Integer.valueOf(box.getWidth()));
		setValue(VideoMetadataType.HEIGHT, Integer.valueOf(box.getHeight()));
		Pict poster = movie.getPosterPict();
		if ( poster == null ) {
			poster = movie.getPict(0);
		}
		if ( poster != null ) {
			setupPoster(movie, poster);
		}
		
		double qtDuration = movie.getDuration(); // units
		double qtTimeScale = movie.getTimeScale(); // units/sec
		long ms = Math.round((qtDuration / qtTimeScale) * 1000);
		setValue(VideoMetadataType.DURATION, Long.valueOf(ms));
		setValue(VideoMetadataType.DURATION_TIME, getDurationTime(ms));
		
		Track soundTrack = movie.getIndTrackType(1, 
				StdQTConstants.soundMediaType, 
				StdQTConstants.movieTrackMediaType);
		if ( soundTrack != null ) {
			SoundMedia media = (SoundMedia)soundTrack.getMedia();
			if ( media.getSampleDescriptionCount() > 0 ) {
				if ( log.isDebugEnabled() ) {
					log.debug("Got sound track [" 
							+media.getSampleDescriptionCount()
							+", "
							+media.getSampleDescription(1)
							+"]");
				}
				SoundDescription desc = (SoundDescription)media.getSampleDescription(1);
				String compressionType = getComponentName( //StdQTConstants.compressorComponentType, 
						desc.getDataFormat(), 
						desc.getVendor());
				if ( compressionType == null ) {
					compressionType = "Unknown format";
				}
				
				StringBuilder buf = new StringBuilder();
				buf.append(compressionType);
				if (desc.getSampleRate() > 0f ) {
					buf.append(", ").append(desc.getSampleRate()).append(" Hz, ");
				}
				if (desc.getSampleSize() > 0) {
					buf.append(desc.getSampleSize()).append(
							" bit, ");
				}
				switch (desc.getNumberOfChannels()) {
					case 1:
						buf.append("mono");
						break;
					case 2:
						buf.append("stereo");
						break;
					default:
						buf.append(desc.getNumberOfChannels()).append(
								" channels");
				}
				setValue(VideoMetadataType.AUDIO_FORMAT, buf.toString());
			}
		}
		
		Track videoTrack = movie.getIndTrackType(1, 
				StdQTConstants.videoMediaType, 
				StdQTConstants.movieTrackMediaType);
		if ( videoTrack != null ) {
			if ( log.isDebugEnabled() ) {
				log.debug("Got video track [" +videoTrack +"]");
			}
			VideoMedia media = (VideoMedia)videoTrack.getMedia();
			float rate = getFPS(media);
			if ( rate > 0 ) {
				setValue(VideoMetadataType.FPS, Float.valueOf(rate));
			}
			if ( media.getSampleDescriptionCount() > 0 ) {
				if ( log.isDebugEnabled() ) {
					log.debug("Got video track [" 
							+media.getSampleDescriptionCount()
							+", "
							+media.getSampleDescription(1)
							+"]");
				}
				ImageDescription desc = (ImageDescription)media.getSampleDescription(1);
				
				StringBuilder buf = new StringBuilder();
				String name = desc.getName();
				if ( name == null || name.length() < 1 ) {
					name = QTUtils.fromOSType(desc.getCType()).toUpperCase();
				}
				buf.append(name);
				buf.append(", ").append(desc.getDepth()).append(" bit");
				
				setValue(VideoMetadataType.VIDEO_FORMAT, buf.toString());
			}
			if ( rate <= 0 ) {
				if ( log.isDebugEnabled() ) {
					log.debug("ts = " +media.getTimeScale() +", dur = " +media.getDuration());
				}
				rate = (float)media.getDuration() / (float)media.getTimeScale();
				setValue(VideoMetadataType.FPS, rate);
			}
		}
	}
	
	private float getFPS(Media media) throws QTException {
		long sampleCount = media.getSampleCount();
		if ( sampleCount < 1 ) return 0;
		double duration = media.getDuration();
		double timeScale = media.getTimeScale();
		return (float)((sampleCount * timeScale) / duration);
	}

	private String getComponentName(int dataFormat, int vendor) throws QTException {
		ComponentDescription cd = new ComponentDescription();
		//cd.setType(type);
		cd.setSubType(dataFormat);
		cd.setManufacturer(vendor);
		
		ComponentIdentifier ident = ComponentIdentifier.find(cd);
		if ( ident == null ) {
			return null;
		}
		if ( log.isDebugEnabled() ) {
			log.debug("ComponentIdentifier: " +ident.getInfo().getName());
		}
		return ident.getInfo().getName();
	}
	
	private void setupPoster(Movie movie, Pict pict) throws QTException {
		QDRect box = movie.getBox();
		QDGraphics g = new QDGraphics(box);
		pict.draw(g, box);
		// get data from the QDGraphics
		PixMap pixMap = g.getPixMap();
		RawEncodedImage rei = pixMap.getPixelData();
		// copy bytes to an array
		int intsPerRow = pixMap.getRowBytes() / 4;
		int[] pixels = new int[intsPerRow * box.getHeight()];
		rei.copyToArray(0, pixels, 0, pixels.length);
		// now coax into image, ignoring alpha for speed
		DirectColorModel mod = new DirectColorModel(32, // bits/sample
				0x00ff0000, // R
				0x0000ff00, // G
				0x000000ff, // B
				0x00000000); // ignore alpha
		/* FIXME go directly to BufferedImage here?
		DataBuffer buf = new DataBufferInt(pixels, pixels.length);
		WritableRaster raster = WritableRaster.createPackedRaster(buf, box
				.getWidth(), box.getHeight(), 32, new Point(0, 0));
		BufferedImage image = new BufferedImage(mod, raster, true, null);
		BasicMetadataImage metaImage = new BasicMetadataImage(image);
		setValue(VideoMetadataType.POSTER, metaImage);
		*/
		Image image = Toolkit.getDefaultToolkit().createImage (
				 new MemoryImageSource(box.getWidth(),  // width
					 box.getHeight(), // height
					 mod, // color model
					 pixels, // data
					 0, // offset
					 intsPerRow));
		BasicMetadataImage metaImage = new BasicMetadataImage(image);
		setValue(VideoMetadataType.POSTER, metaImage);
	}
	
}
