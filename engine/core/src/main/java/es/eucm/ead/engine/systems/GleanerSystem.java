/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
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
package es.eucm.ead.engine.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import es.eucm.ead.engine.gleaner.components.GleanerSettingsComponent;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.gleaner.components.GleanerLocalStorage;
import es.eucm.ead.schema.gleaner.components.GleanerNetStorage;
import es.eucm.ead.schema.gleaner.components.GleanerSettings;
import es.eucm.gleaner.tracker.Tracker;
import es.eucm.gleaner.tracker.storage.LocalStorage;
import es.eucm.gleaner.tracker.storage.NetStorage;
import es.eucm.gleaner.tracker.storage.Storage;

import java.util.Calendar;

/**
 * This system serves as a wrapper for Gleaner's {@link Tracker}. Has the next
 * responsibilities: 1) Reads and processes {@link GleanerSettings} and
 * initializes tracker accordingly. 2) Invokes {@link Tracker#update(float)}
 * each update() cycle. 3) Exposes an API for logging events that are meaningful
 * for Mokap, including Tracker's screen, zone, etc. but also additional ones
 * like effect() and press()
 * 
 * Created by jtorrente on 29/10/2015.
 */
public class GleanerSystem extends EntitySystem {

	public static final String LOCAL_TRACES_FOLDER = "gleaner_traces";

	protected GleanerSettings settings = null;
	private ImmutableArray<Entity> entities;
	protected Tracker tracker;
	protected Storage storage;

	private Json json = new Json(); // To serialize effects

	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(Family.all(
				GleanerSettingsComponent.class).get());
	}

	@Override
	public void removedFromEngine(Engine engine) {
		settings = null;
		entities = null;
		if (tracker != null) {
			tracker.close();
		}
		tracker = null;
		storage = null;
	}

	@Override
	public void update(final float deltaTime) {
		if (settings == null) {
			init();
		}
		if (settings != null) {
			tracker.update(deltaTime);
		}
	}

	protected void init() {
		if (entities.size() > 0) {
			settings = entities.get(0)
					.getComponent(GleanerSettingsComponent.class).getSettings();
			storage = buildGleanerStorage();
			tracker = new Tracker(storage, settings.getFlushInterval());
			tracker.start();
		}
	}

	protected Storage buildGleanerStorage() {
		Storage storage = null;
		if (settings.getStorage() instanceof GleanerLocalStorage) {
			GleanerLocalStorage gleanerLocalStorage = (GleanerLocalStorage) settings
					.getStorage();
			storage = new LocalStorage(
					fileHandleForLocalStorage(gleanerLocalStorage
							.getFilePrefix()));
		} else if (settings.getStorage() instanceof GleanerNetStorage) {
			GleanerNetStorage gleanerNetStorage = (GleanerNetStorage) settings
					.getStorage();
			storage = new NetStorage(Gdx.net, gleanerNetStorage.getHost(),
					gleanerNetStorage.getTrackingCode());
		}
		return storage;
	}

	/**
	 * When local storage is selected, trace files will be stored in a common
	 * folder ({@link #LOCAL_TRACES_FOLDER}), rooted in the first directory that
	 * is available from the following list 1) external storage, 2) local
	 * storage 3) absolute path
	 * 
	 * @return The main folder for the files
	 */
	protected FileHandle fileHandleForFolder() {
		FileHandle folder = null;
		if (Gdx.files.isExternalStorageAvailable()) {
			folder = Gdx.files.external(LOCAL_TRACES_FOLDER);
		} else if (Gdx.files.isLocalStorageAvailable()) {
			folder = Gdx.files.local(LOCAL_TRACES_FOLDER);
		} else {
			folder = Gdx.files.absolute(LOCAL_TRACES_FOLDER);
		}
		folder.mkdirs();
		return folder;
	}

	/**
	 * Trace files for local storage are stored in a subdirectory (see
	 * {@link #fileHandleForFolder()}) and are given a name following the next
	 * pattern: prefix_YYMMDD_HHMMSS.gln where prefix is provided by the user
	 * through the GleanerSettings component, and the rest is the date and time
	 */
	protected FileHandle fileHandleForLocalStorage(String prefix) {
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int minute = Calendar.getInstance().get(Calendar.MINUTE);
		int second = Calendar.getInstance().get(Calendar.SECOND);

		StringBuffer fileName = new StringBuffer();
		fileName.append(prefix);
		fileName.append("_");
		fileName.append(twoDigits(year));
		fileName.append(twoDigits(month));
		fileName.append(twoDigits(day));
		fileName.append("_");
		fileName.append(twoDigits(hour));
		fileName.append(twoDigits(minute));
		fileName.append(twoDigits(second));
		fileName.append(".gln");

		FileHandle folder = fileHandleForFolder();
		FileHandle tracesFile = folder.child(fileName.toString());
		System.out.println(tracesFile.file().getAbsolutePath());
		return tracesFile;
	}

	private String twoDigits(int n) {
		return (n < 10 ? "0" : "") + n;
	}

	/**
	 * Logs that a new scene (screen) has been loaded.
	 * 
	 * Screen events are considered high priority, and therefore a flush is
	 * requested.
	 * 
	 * @param screenId
	 *            The id of the scene (e.g. scenes/s0.json)
	 */
	public void screen(String screenId) {
		if (settings == null) {
			Gdx.app.debug(
					"GLEANER",
					"Event screen("
							+ screenId
							+ ") could not be logged because the system has not been initialized");
		}
		if (settings == null || !settings.isScreen()) {
			return;
		}
		tracker.screen(screenId);
		tracker.requestFlush();
	}

	/**
	 * Equivalent to {@link #screen(String)}, but disabled by default.
	 * 
	 * Zone events are also considered high priority, and therefore a flush is
	 * requested.
	 * 
	 * @param zoneId
	 *            The id of the zone/scene (e.g. scenes/s0.json)
	 */
	public void zone(String zoneId) {
		if (settings == null) {
			Gdx.app.debug(
					"GLEANER",
					"Event zone("
							+ zoneId
							+ ") could not be logged because the system has not been initialized");
		}
		if (settings == null || !settings.isZone()) {
			return;
		}
		tracker.zone(zoneId);
		tracker.requestFlush();
	}

	/**
	 * Logs a change in a particular variable
	 * 
	 * Var events are also considered high priority, and therefore a flush is
	 * requested.
	 * 
	 * @param variable
	 *            The ID of the variable
	 * @param value
	 *            The new value of the variable
	 */
	public void var(String variable, Object value) {
		if (settings == null) {
			Gdx.app.debug(
					"GLEANER",
					"Event var("
							+ variable
							+ ","
							+ value.toString()
							+ ") could not be logged because the system has not been initialized");
		}
		if (settings == null || !settings.isVar()) {
			return;
		}
		tracker.var(variable, value);
		tracker.requestFlush();
	}

	/**
	 * Logs that the user (or the game's internal logic) chose an option from a
	 * certain set
	 * 
	 * Choice events are also considered high priority, and therefore a flush is
	 * requested. Choice events are always manual (they are not logged
	 * automatically)
	 * 
	 * @param choiceId
	 *            The ID of the choice
	 * @param optionId
	 *            The ID of the option
	 */

	public void choice(String choiceId, String optionId) {
		if (settings == null) {
			Gdx.app.debug(
					"GLEANER",
					"Event choice("
							+ choiceId
							+ ","
							+ optionId
							+ ") could not be logged because the system has not been initialized");
			return;
		}
		tracker.choice(choiceId, optionId);
		tracker.requestFlush();
	}

	/**
	 * Logs a click (touchUp) on the screen.
	 * 
	 * @param x
	 *            X-coordinate of the click, in game world coordinates
	 * @param y
	 *            Y-coordinate of the click, in game world coordinates
	 * @param target
	 *            The name of the entity that was hit (if available). Can be
	 *            null (not logged in that case)
	 */

	public void click(float x, float y, String target) {
		if (settings == null) {
			Gdx.app.debug(
					"GLEANER",
					"Event click(On target: "
							+ target
							+ " at coordinates "
							+ x
							+ ","
							+ y
							+ ") could not be logged because the system has not been initialized");
		}
		if (settings == null || !settings.isClick()) {
			return;
		}

		if (target != null) {
			tracker.click(x, y, target);
		} else {
			tracker.click(x, y);
		}
	}

	/**
	 * Similar to {@link #click(float, float, String)}, but for press
	 * (touchDown) events.
	 */
	public void press(float x, float y, String target) {
		if (settings == null) {
			Gdx.app.debug(
					"GLEANER",
					"Event press(On target: "
							+ target
							+ " at coordinates "
							+ x
							+ ","
							+ y
							+ ") could not be logged because the system has not been initialized");
		}
		if (settings == null || !settings.isPress()) {
			return;
		}

		if (target != null) {
			tracker.trace("press", Float.toString(x), Float.toString(y), target);
		} else {
			tracker.trace("press", Float.toString(x), Float.toString(y));
		}
	}

	/**
	 * Logs an arbitrary trace.
	 * 
	 * @param label
	 *            A compulsory label to identify the type of trace
	 * @param params
	 *            An arbitrary number of additional parameters.
	 */
	public void trace(String label, Object... params) {
		if (settings == null) {
			Gdx.app.debug(
					"GLEANER",
					"Event with label "
							+ label
							+ " could not be logged because the system has not been initialized");
			return;
		}
		String[] allParams = new String[params.length + 1];
		allParams[0] = label;
		for (int i = 1; i <= params.length; i++) {
			allParams[i] = params[i - 1].toString();
		}
		tracker.trace(allParams);
	}

	/**
	 * Logs a serialized version of an effect that was just launched.
	 * 
	 * @param effect
	 *            The effect to log.
	 */
	public void effect(Effect effect) {
		if (settings == null) {
			Gdx.app.debug("GLEANER",
					"Effect could not be logged because the system has not been initialized");
			return;
		}
		if (settings.isEffect()) {
			trace("effect", json.toJson(effect, (Class) null));
		}
	}
}
