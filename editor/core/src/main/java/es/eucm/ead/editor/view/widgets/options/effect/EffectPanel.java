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
package es.eucm.ead.editor.view.widgets.options.effect;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.options.DefaultOptionsPanel;
import es.eucm.ead.engine.I18N;

public class EffectPanel extends AbstractWidget {

	private Controller controller;

	private SelectBox triggerSelect;

	private SelectBox effectSelect;

	private DefaultOptionsPanel triggerOptions;

	private DefaultOptionsPanel effectOptions;

	private Class<?>[] triggerClasses;

	private Class<?>[] effectClasses;

	public EffectPanel(Controller controller, Class<?>[] triggerClasses,
			Class<?>[] effectClasses) {
		this.controller = controller;
		I18N i18N = controller.getEditorAssets().getI18N();
		this.triggerClasses = triggerClasses;
		this.effectClasses = effectClasses;

		String[] triggersLabels = new String[triggerClasses.length];
		for (int i = 0; i < triggerClasses.length; i++) {
			triggersLabels[i] = i18N.m("trigger."
					+ ClassReflection.getSimpleName(triggerClasses[i]));
		}
		String[] effectsLabels = new String[effectClasses.length];
		for (int i = 0; i < effectClasses.length; i++) {
			effectsLabels[i] = i18N.m("effect."
					+ ClassReflection.getSimpleName(effectClasses[i]));
		}

		Skin skin = controller.getEditorAssets().getSkin();
		triggerSelect = new SelectBox(triggersLabels, skin);
		effectSelect = new SelectBox(effectsLabels, skin);
		triggerSelect.setSelection(0);
		triggerSelect.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				updateTriggerPanel();
			}
		});
		effectSelect.setSelection(0);
		effectSelect.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				updateEffectPanel();
			}
		});
		addActor(triggerSelect);
		addActor(effectSelect);
		updateEffectPanel();
		updateTriggerPanel();
	}

	private void updateEffectPanel() {
		Class<?> effectClass = effectClasses[effectSelect.getSelectionIndex()];
		if (effectOptions != null) {
			effectOptions.remove();
		}
		effectOptions = new DefaultOptionsPanel(controller, effectClass);
		addActor(effectOptions);
		invalidate();
	}

	private void updateTriggerPanel() {
		Class<?> triggerClass = triggerClasses[triggerSelect
				.getSelectionIndex()];
		if (triggerOptions != null) {
			triggerOptions.remove();
		}
		triggerOptions = new DefaultOptionsPanel(controller, triggerClass);
		addActor(triggerOptions);
		invalidate();
	}

	@Override
	public float getPrefWidth() {
		return Math.max(triggerSelect.getPrefWidth(),
				triggerOptions.getPrefWidth())
				+ Math.max(effectSelect.getPrefWidth(),
						effectOptions.getPrefWidth());
	}

	@Override
	public float getPrefHeight() {
		return Math.max(
				triggerSelect.getPrefHeight() + triggerOptions.getPrefHeight(),
				effectSelect.getPrefHeight() + effectOptions.getPrefHeight());
	}

	@Override
	public void layout() {
		float x = 0;
		float height = triggerSelect.getPrefHeight();
		float y = getHeight() - height;
		float width = getWidth() / 2.0f;
		triggerSelect.setBounds(x, y, width, height);
		x = getWidth() / 2.0f;
		effectSelect.setBounds(x, y, width, height);
		x = 0;
		width = triggerOptions.getPrefWidth();
		height = triggerOptions.getPrefHeight();
		y = getHeight() - height - triggerSelect.getHeight();
		triggerOptions.setBounds(x, y, width, height);
		x = getWidth() / 2.0f;
		width = effectOptions.getPrefWidth();
		height = effectOptions.getPrefHeight();
		y = getHeight() - height - effectSelect.getHeight();
		effectOptions.setBounds(x, y, width, height);
	}
}
