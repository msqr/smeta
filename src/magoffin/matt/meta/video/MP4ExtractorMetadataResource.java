/* ===================================================================
 * MP4ExtractorMetadataResource.java
 * 
 * Created 25/06/2023 2:34:54 pm
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import com.drew.imaging.mp4.Mp4Reader;
import com.drew.imaging.quicktime.QuickTimeReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QuickTimeAtomHandler;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import com.drew.metadata.mp4.Mp4BoxHandler;
import com.drew.metadata.mp4.Mp4Directory;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;
import magoffin.matt.meta.MetadataResource;
import magoffin.matt.meta.support.AbstractExtractorEnumMetadataResource;
import magoffin.matt.meta.support.LocalizablePlaceholder;
import magoffin.matt.meta.support.LocalizablePlaceholder.ValueGenerator;

/**
 * {@link MetadataResource} for video metadata using Metadata Extractor.
 *
 * @author matt
 * @version 1.0
 */
public class MP4ExtractorMetadataResource extends
		AbstractExtractorEnumMetadataResource<VideoMetadataType> implements VideoMetadataResource {

	/**
	 * Construct from a File.
	 * 
	 * @param file
	 *        the input file
	 * @throws IOException
	 *         if an IO error occurs
	 */
	public MP4ExtractorMetadataResource(File file) throws IOException {
		super(new Metadata());
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			Mp4Reader.extract(in, new Mp4BoxHandler(meta));
		} finally {
			if ( in != null ) {
				try {
					in.close();
				} catch ( IOException e ) {
					// ignore
				}
			}
		}
		in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			QuickTimeReader.extract(in, new QuickTimeAtomHandler(meta));
		} finally {
			if ( in != null ) {
				try {
					in.close();
				} catch ( IOException e ) {
					// ignore
				}
			}
		}
		extractMetadata();
	}

	private void extractMetadata() {
		for ( Directory directory : meta.getDirectories() ) {
			for ( com.drew.metadata.Tag tag : directory.getTags() ) {
				System.out.println(tag);
			}
		}
		setValue(VideoMetadataType.DATE_TAKEN, extractCreationDate());

		Long duration = extractDuration();
		if ( duration != null ) {
			setValue(VideoMetadataType.DURATION, duration);
			setValue(VideoMetadataType.DURATION_TIME, VideoUtils.durationTime(duration));
		}

		setValue(VideoMetadataType.VIDEO_FORMAT,
				getMetaString(Mp4VideoDirectory.class, Mp4VideoDirectory.TAG_COMPRESSION_TYPE));
		setValue(VideoMetadataType.WIDTH,
				getMetaString(Mp4VideoDirectory.class, Mp4VideoDirectory.TAG_WIDTH));
		setValue(VideoMetadataType.HEIGHT,
				getMetaString(Mp4VideoDirectory.class, Mp4VideoDirectory.TAG_HEIGHT));
		setValue(VideoMetadataType.FPS,
				getMetaFloat(Mp4VideoDirectory.class, Mp4VideoDirectory.TAG_FRAME_RATE));

		extractAudioMetadata();
	}

	private void extractAudioMetadata() {
		setValue(VideoMetadataType.AUDIO_FORMAT, new LocalizablePlaceholder(new ValueGenerator() {

			public Object getValueForLocale(Locale locale) {
				StringBuilder buf = new StringBuilder();
				buf.append(getMetaString(Mp4SoundDirectory.class, Mp4SoundDirectory.TAG_AUDIO_FORMAT));

				Integer rate = getMetaInteger(Mp4SoundDirectory.class,
						Mp4SoundDirectory.TAG_AUDIO_SAMPLE_RATE);
				if ( rate != null ) {
					buf.append(", ").append(String.format("%.1f", rate.doubleValue() / 1000.0))
							.append(" kHz");
				}
				Integer size = getMetaInteger(Mp4SoundDirectory.class,
						Mp4SoundDirectory.TAG_AUDIO_SAMPLE_RATE);
				if ( size != null ) {
					buf.append(", ")
							.append(getMetaInteger(Mp4SoundDirectory.class,
									Mp4SoundDirectory.TAG_AUDIO_SAMPLE_SIZE))
							.append(' ').append(getGeneralVideoMessage("bit", locale));
				}
				Integer count = getMetaInteger(Mp4SoundDirectory.class,
						Mp4SoundDirectory.TAG_NUMBER_OF_CHANNELS);
				if ( count != null ) {
					buf.append(", ");
					switch (count) {
						case 1:
							buf.append(getGeneralVideoMessage("mono", locale));
							break;
						case 2:
							buf.append(getGeneralVideoMessage("stereo", locale));
							break;
						default:
							buf.append(count).append(' ')
									.append(getGeneralVideoMessage("channels", locale));
					}
				}
				return buf.toString();
			}
		}));
	}

	private String getGeneralVideoMessage(String key, Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle("magoffin/matt/meta/video/general", locale);
		try {
			return bundle.getString(key);
		} catch ( MissingResourceException e ) {
			return null;
		}
	}

	/**
	 * Extract a creation date from the metadata.
	 * 
	 * <p>
	 * This method attempts to find the creation date for the image by taking
	 * the first value available from:
	 * </p>
	 * 
	 * <ol>
	 * <li>QuickTimeMetadataDirectory.TAG_CREATION_DATE</li>
	 * <li>Mp4Directory.TAG_CREATION_TIME</li>
	 * </ol>
	 * 
	 * @return a Date
	 */
	private Date extractCreationDate() {
		Date date = getMetaDate(QuickTimeMetadataDirectory.class,
				QuickTimeMetadataDirectory.TAG_CREATION_DATE);
		if ( date == null ) {
			date = getMetaDate(Mp4Directory.class, Mp4Directory.TAG_CREATION_TIME);
		}
		return date;
	}

	/**
	 * Extract the video duration from the metadata.
	 * 
	 * @return the duration, in milliseconds
	 */
	private Long extractDuration() {
		Long units = getMetaLong(Mp4Directory.class, Mp4Directory.TAG_DURATION);
		Long scale = getMetaLong(Mp4Directory.class, Mp4Directory.TAG_TIME_SCALE);
		if ( units != null && scale != null ) {
			return Math.round((units.doubleValue() / scale.doubleValue()) * 1000);
		}
		return null;
	}

}
