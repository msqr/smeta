package org.farng.mp3.id3;

import org.farng.mp3.InvalidTagException;
import org.farng.mp3.object.ObjectByteArraySizeTerminated;
import org.farng.mp3.object.ObjectNumberHashMap;
import org.farng.mp3.object.ObjectStringNullTerminated;
import org.farng.mp3.object.AbstractMP3Object;

import java.io.IOException;

/**
 * Base implementation for v2.2 PIC and v2.3+ APIC frames.
 *
 * @author Eric Farng
 * @version $Revision$
 */
public abstract class AbstractPictureFrameBody extends AbstractID3v2FrameBody {
    
	/**
     * Creates a new AbstractPictureFrameBody object.
     */
    public AbstractPictureFrameBody() {
        //        this.setObject("Text Encoding", new Byte((byte) 0));
        //        this.setObject("MIME Type", "");
        //        this.setObject("Picture Type", new Byte((byte) 0));
        //        this.setObject("Description", "");
        //        this.setObject("Picture Data", new byte[0]);
    }

    /**
     * Construct from another body (copy constructor).
     * @param body the body to copy
     */
    public AbstractPictureFrameBody(AbstractPictureFrameBody body) {
        super(body);
    }

    /**
     * Creates a new FrameBodyAPIC object.
     *
     * @param textEncoding DOCUMENT ME!
     * @param mimeType DOCUMENT ME!
     * @param pictureType DOCUMENT ME!
     * @param description DOCUMENT ME!
     * @param data DOCUMENT ME!
     */
    public AbstractPictureFrameBody(byte textEncoding, String mimeType, byte pictureType, String description, byte[] data) {
        this.setObject("Text Encoding", new Byte(textEncoding));
        this.setObject("MIME Type", mimeType);
        this.setObject("Picture Type", new Byte(pictureType));
        this.setObject("Description", description);
        this.setObject("Picture Data", data);
    }

    /**
     * Creates a new FrameBodyAPIC object.
     *
     * @param file DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws InvalidTagException DOCUMENT ME!
     */
    public AbstractPictureFrameBody(java.io.RandomAccessFile file)
                  throws IOException, InvalidTagException {
        this.read(file);
    }

    /**
     * Set the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        setObject("Description", description);
    }

    @Override
    public String getDescription() {
        return (String) getObject("Description");
    }

    @Override
    protected void setupObjectList() {
    	appendToObjectList(new ObjectNumberHashMap("Text Encoding", 1));
    	appendToObjectList(createMimeTypeObject());
    	appendToObjectList(new ObjectNumberHashMap("Picture Type", 1));
    	appendToObjectList(new ObjectStringNullTerminated("Description"));
    	appendToObjectList(new ObjectByteArraySizeTerminated("Picture Data"));
    }

    /**
     * Create the appropriate MIME object.
     * 
     * <p>This varies between PIC and APIC.</p>
     * 
     * @return the appropriate AbstractMP3Object
     */
    protected abstract AbstractMP3Object createMimeTypeObject();
}