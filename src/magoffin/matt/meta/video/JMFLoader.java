/* ===================================================================
 * JMFLoader.java
 * 
 * Created Jan 10, 2007 6:07:07 PM
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

import java.io.File;

import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;

import org.apache.log4j.Logger;

/**
 * Helper class for working with JMF media.
 * 
 * @author Matt Magoffin (spamsqr@msqr.us)
 * @version $Revision$ $Date$
 */
public class JMFLoader implements ControllerListener {

	private Processor processor = null;

	private Object waitSync = new Object();

	private boolean stateTransitionOK = true;

	private final Logger log = Logger.getLogger(getClass());

	/**
	 * Construct with a specific Processor.
	 * 
	 * @param processor  the processor
	 */
	public JMFLoader(Processor processor) {
		this.processor = processor;
		this.processor.addControllerListener(this);
		
		// Put the Processor into configured state.
		processor.configure();
		if (!waitForState(Processor.Configured)) {
			throw new RuntimeException(
					"Failed to configure the processor for video file");
		}
	}

	/* (non-Javadoc)
	 * @see javax.media.ControllerListener#controllerUpdate(javax.media.ControllerEvent)
	 */
	public void controllerUpdate(ControllerEvent event) {
		if (event instanceof ConfigureCompleteEvent
				|| event instanceof RealizeCompleteEvent
				|| event instanceof PrefetchCompleteEvent) {
			synchronized (waitSync) {
				stateTransitionOK = true;
				waitSync.notifyAll();
			}
		} else if (event instanceof ResourceUnavailableEvent) {
			log.warn("ResourceUnavailableEvent: " + event + ", source = "
					+ event.getSource() + ", controller = "
					+ event.getSourceController());
			synchronized (waitSync) {
				stateTransitionOK = false;
				waitSync.notifyAll();
			}
		}
	}

	/**
	 * Block until the processor has transitioned to the given state. Return
	 * false if the transition failed.
	 * 
	 * @param state the state to wait for
	 * @return true if transitioned to that state without error
	 */
	public boolean waitForState(int state) {
		// stateTransitionOK = true;
		synchronized (waitSync) {
			try {
				while (processor.getState() != state && stateTransitionOK) {
					waitSync.wait();
				}
			} catch (Exception e) {
				log.warn("Exception waiting for state " + state, e);
			}
		}
		return stateTransitionOK;
	}

	/**
	 * Get a TrackControl for an video track.
	 * 
	 * @param mediaResource the media resource
	 * @return the TrackControl
	 */
	public TrackControl getVideoTrackControl(File mediaResource) {

		// So I can use it as a player.
		processor.setContentDescriptor(null);

		// Obtain the track controls.
		TrackControl tc[] = processor.getTrackControls();

		if (tc == null) {
		    log.error("Failed to obtain track controls from the processor for video file "
		    		+mediaResource.getAbsolutePath());
			throw new RuntimeException(
				"Failed to obtain track controls from the processor for video file");
		}

		// Search for the track control for the video track.
		TrackControl videoTrack = null;

		for (int i = 0; i < tc.length; i++) {
		    if (tc[i].getFormat() instanceof VideoFormat) {
		    	videoTrack = tc[i];
		    	break;
		    }
		}

		if (videoTrack == null) {
		    log.error("Media does not contain a video track for video file "
		    		+mediaResource.getAbsolutePath());
			throw new RuntimeException(
				"Media does not contain a video track");
		}
		
		if ( log.isDebugEnabled() ) {
			log.debug("Got video format " +videoTrack.getFormat() 
					+" for file " +mediaResource.getAbsolutePath());
		}
		
		return videoTrack;
	}

	/**
	 * Get a TrackControl for an audio track.
	 * 
	 * @param mediaResource the media resource
	 * @return the TrackControl
	 */
	public TrackControl getAudioTrack(File mediaResource) {
		// Obtain the track controls.
		TrackControl tc[] = processor.getTrackControls();

		if (tc == null) {
		    log.error("Failed to obtain track controls from the processor for video file "
		    		+mediaResource.getAbsolutePath());
			throw new RuntimeException(
				"Failed to obtain track controls from the processor for video file");
		}

		// Search for the track control for the audio track.
		TrackControl audioTrack = null;

		for (int i = 0; i < tc.length; i++) {
		    if (tc[i].getFormat() instanceof AudioFormat) {
		    	audioTrack = tc[i];
		    	break;
		    }
		}

		if (audioTrack == null) {
			if ( log.isDebugEnabled() ) {
				log.debug("Media does not contain an audio track for video file "
		    		+mediaResource.getAbsolutePath());
			}
			return null;
		}
		
		if ( log.isDebugEnabled() ) {
			log.debug("Got audio format " +audioTrack.getFormat() 
					+" for file " +mediaResource.getAbsolutePath());
		}
		
		return audioTrack;
	}

}
