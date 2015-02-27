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
package es.eucm.ead.android;

import android.content.Context;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.google.android.gms.analytics.HitBuilders;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Tracker;

/**
 * Created by angel on 23/09/14.
 */
public class GATracker extends Tracker {

	private com.google.android.gms.analytics.Tracker tracker;

	public static final String CATEGORY_PROJECT = "Project";

	public static final String ACTION_NEW_SCENE = "new_scene";

	public static final String CATEGORY_UI = "UI";

	public static final String CATEGORY_EDIT = "edit";

	public static final String ACTION_PRESS = "press";

	private Context context;

	public GATracker(Context context, Controller controller,
			com.google.android.gms.analytics.Tracker tracker) {
		super(controller);
		this.tracker = tracker;
		this.context = context;
	}

	@Override
	protected void startSessionImpl() {

	}

	public void changeView(String simpleName) {
		tracker.setScreenName(simpleName);
		tracker.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public void newScene() {
		tracker.send(new HitBuilders.EventBuilder()
				.setCategory(CATEGORY_PROJECT).setAction(ACTION_NEW_SCENE)
				.build());
	}

	@Override
	public void buttonPressed(String label) {
		tracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_UI)
				.setAction(ACTION_PRESS).setLabel(label).build());
	}

	public void actionPerformedImpl(Class clazz, boolean performed) {
		String action = ClassReflection.getSimpleName(clazz).toLowerCase();
		tracker.send(new HitBuilders.EventBuilder().setCategory(CATEGORY_EDIT)
				.setAction(action).setValue(performed ? 1 : 0).build());
	}

}
