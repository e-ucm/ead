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
package es.eucm.ead.editor.view.ui.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ShowContextMenu;
import es.eucm.ead.editor.control.actions.model.generic.AddToArray;
import es.eucm.ead.editor.indexes.EffectsIndex;
import es.eucm.ead.editor.indexes.FuzzyIndex.Term;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.view.controllers.SearchResultsWidget;
import es.eucm.ead.editor.view.controllers.SearchResultsWidget.SearchListener;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.EndGame;
import es.eucm.ead.schema.effects.GoScene;

import java.util.HashMap;
import java.util.Map;

public class EffectsWidget extends LinearLayout implements
		ModelListener<ListEvent> {

	public static final String EFFECTS_NAME = "effectswidget";

	private static final Map<Class, Class> EFFECTS_WIDGETS = new HashMap<Class, Class>();

	static {
		EFFECTS_WIDGETS.put(GoScene.class, GoSceneWidget.class);
		EFFECTS_WIDGETS.put(EndGame.class, EndgameWidget.class);
	}

	private Controller controller;

	private Model model;

	private Object parent;

	private Array<Effect> effects;

	private LinearLayout effectsContainer;

	private int idGenerator = 0;

	public EffectsWidget(Controller controller) {
		super(false);
		this.controller = controller;
		this.model = controller.getModel();
		Skin skin = controller.getApplicationAssets().getSkin();
		SearchResultsWidget searchResultsWidget = new SearchResultsWidget(
				controller.getIndex(EffectsIndex.class), skin);
		searchResultsWidget.addListener(new ResultListener());
		effectsContainer = new LinearLayout(false);
		add(effectsContainer).expandX();

		TextButton textButton = new TextButton(controller
				.getApplicationAssets().getI18N().m("effect.add"), skin);

		textButton.addListener(new ActionOnClickListener(controller,
				ShowContextMenu.class, textButton, searchResultsWidget));

		add(textButton).expandX().margin(0, 5.0f, 0, 5.0f);
	}

	public void read(Object parent, Array<Effect> effects) {
		if (this.effects != null) {
			model.removeListener(this.effects, this);
		}

		this.parent = parent;
		this.effects = effects;
		model.addListListener(this.effects, this);

		effectsContainer.clearChildren();
		idGenerator = 0;
		for (Effect effect : effects) {
			addEffect(-1, effect);
		}
	}

	private void addEffect(int index, Effect effect) {
		EffectWidget effectWidget = null;

		Class widgetClass = EFFECTS_WIDGETS.get(effect.getClass());
		if (widgetClass == null) {
			Gdx.app.error("EffectsWidget",
					"No widget for class " + effect.getClass()
							+ ". Using default");
			effectWidget = new UnknownEffectWidget(effect.getClass());
		} else {
			try {
				effectWidget = (EffectWidget) ClassReflection
						.newInstance(widgetClass);
			} catch (ReflectionException e) {
				Gdx.app.error("EffectsWidget",
						"Impossible to create effect widget.", e);
			}
		}

		if (effectWidget != null) {
			effectWidget.setName(EFFECTS_NAME + idGenerator++);
			effectWidget.initialize(controller);
			effectWidget.setEffect(effect);
			effectWidget.setUserObject(effect);
			effectsContainer.add(index, effectWidget).expandX();
		}
	}

	@Override
	public void modelChanged(ListEvent event) {
		switch (event.getType()) {
		case ADDED:
			addEffect(event.getIndex(), (Effect) event.getElement());
			break;
		case REMOVED:
			Actor actor = findUserObject(event.getElement());
			if (actor != null) {
				actor.remove();
			}
			break;
		}
	}

	public class ResultListener extends SearchListener {
		@SuppressWarnings("unchecked")
		@Override
		public void termSelected(Term term) {
			Effect effect = controller.getTemplates().createEffect(
					(Class<Effect>) term.getData());
			controller.action(AddToArray.class, parent, effects, effect);

		}
	}
}
