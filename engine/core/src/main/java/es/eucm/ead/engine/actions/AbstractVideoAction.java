/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.eucm.ead.engine.actions;

import es.eucm.ead.schema.actions.Video;

/**
 * Abstract implementation of video action
 */
public abstract class AbstractVideoAction extends AbstractAction<Video> {

	/**
	 * If the video is done
	 */
	private boolean done;

	@Override
	protected boolean delegate(float delta) {
		return done;
	}

	@Override
	public void initialize(Video schemaObject) {
		done = false;
		play(schemaObject.getUri(), schemaObject.isSkippable());
	}

	/**
	 * Play the video in the given. To be implemented by each supported platform
	 * 
	 * @param uri
	 *            the uri
	 * @param skippable
	 *            if the video can be skipped if the player wants to
	 */
	protected abstract void play(String uri, boolean skippable);

	/**
	 * The video has ended. To be called by implementing classes
	 */
	public void end() {
		done = true;
	}

    /**
     * This method should be overwriten by implementation subclases, if the subclass makes use of any native resources that need to be released when exit() or dispose() are invoked.
     * For example, VLC-based implementations of VideoAction for desktop should overwrite this. In contrast, Android implementations may not need it.
     */
    public static void release(){

    }
}
