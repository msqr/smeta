package magoffin.matt.meta.audio;

import java.util.Date;

import magoffin.matt.meta.MetadataImage;

/**
 * Metadata types for audio media.
 */
public enum AudioMetadataType {
	
	/** The album name. */
	ALBUM,
	
	/** The album cover image as a {@link MetadataImage}. */
	ALBUM_COVER(MetadataImage.class),
	
	/** The artist name. */
	ARTIST,
	
	/** The audio format, such as codec, frequency, bits, etc. */
	AUDIO_FORMAT,
	
	/** 
	 * The bit rate (eg bits/sec) as an {@link java.lang.Integer}.
	 * 
	 * <p>For variable bit rate resources, this should be an estimate
	 * value, or average value.</p>
	 */
	BIT_RATE(Integer.class),
	
	/** A comment. */
	COMMENT,
	
	/** The composer. */
	COMPOSER,
	
	/** The disc number (for multi-disc sets) as a {@link java.lang.Integer}. */
	DISC_NUM(Integer.class),
	
	/** The total discs in this set as a {@link java.lang.Integer}. */
	DISC_TOTAL(Integer.class),
	
	/** 
	 * The total length of the autio in milliseconds as 
	 * an {@link java.lang.Long}. 
	 */
	DURATION(Long.class),
	
	/** The music genre. */
	GENRE,
	
	/** The release date as a {@link java.util.Date}. */
	RELEASE_DATE(Date.class),
	
	/** The digital sample frequency (eg Hz) as a {@link java.lang.Double}. */
	SAMPLE_RATE(Double.class),
	
	/** The song name. */
	SONG_NAME,
	
	/** The track number on disc as a {@link java.lang.Integer}. */
	TRACK_NUM(Integer.class),
	
	/** The total number of tracks on disc as a {@link java.lang.Integer}. */
	TRACK_TOTAL(Integer.class),
	
	/** Flag indicating variable bit rate as a {@link java.lang.Boolean}. */
	VBR_FLAG(Boolean.class),
	
	/** The music year as a {@link java.lang.Integer}. */
	YEAR(Integer.class);
	
	private Class<?> objectType;
	
	/**
	 * Default constructor, for String types.
	 */
	private AudioMetadataType() {
		this(String.class);
	}
	
	/**
	 * Construct with a specific type.
	 * 
	 * @param objectType the object type
	 */
	private AudioMetadataType(Class<?> objectType) {
		this.objectType = objectType;
	}
	
	/**
	 * Get the type of object this metadata represents.
	 * @return object type
	 */
	public Class<?> getObjectType() {
		return this.objectType;
	}
	
}