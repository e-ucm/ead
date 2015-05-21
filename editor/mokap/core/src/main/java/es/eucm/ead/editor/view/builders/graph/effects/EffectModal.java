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
package es.eucm.ead.editor.view.builders.graph.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.commander.Commander;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.graph.LogicView.ReadGraph;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.gdx.Modal;
import es.eucm.gdx.WidgetUtils;
import es.eucm.gdx.actions.ExtraActions;
import es.eucm.gdx.widgets.layouts.LinearLayout;
import es.eucm.graph.core.NodeBuilder;
import es.eucm.graph.core.NodeBuilder.EditionResult;

public abstract class EffectModal<T extends Effect> extends Table implements
		Modal, BackListener {

	private EditionResult editionResult;

	protected Commander commander;

	protected LinearLayout buttons;

	protected T effect;

	protected NodeBuilder<T> nodeBuilder;

	public EffectModal(NodeBuilder<T> nodeBuilder, final Commander commander,
			Skin skin, I18N i18N) {
		this.nodeBuilder = nodeBuilder;
		this.commander = commander;
		setTouchable(Touchable.enabled);
		background(skin.getDrawable(SkinConstants.DRAWABLE_PAGE));
		buttons = new LinearLayout(true);
		buttons.pad(WidgetBuilder.dpToPixels(8));
		buttons.addSpace();
		TextButton ok = new TextButton(i18N.m("ok"), skin,
				SkinConstants.STYLE_DIALOG);
		ok.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				commander.perform(ReadGraph.class);
				WidgetUtils.hideModal();
				if (editionResult != null) {
					editionResult.ok();
				}
			}
		});

		final TextButton cancel = new TextButton(i18N.m("cancel"), skin,
				SkinConstants.STYLE_DIALOG);

		cancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				EffectModal.this.cancel();
				if (editionResult != null) {
					editionResult.canceled();
				}
			}
		});

		buttons.add(ok).marginRight(WidgetBuilder.dpToPixels(16));
		buttons.add(cancel);
		add(buildEditor(skin, i18N)).expand().fill();
		row();
		add(buttons).pad(WidgetBuilder.dpToPixels(16)).expandX().fillX();
	}

	public void setEditionResult(EditionResult editionResult) {
		this.editionResult = editionResult;
	}

	public void read(T effect) {
		this.effect = effect;
		updateEditor(effect);
	}

	/**
	 * @return the editor for the type of effect this modal edits
	 */
	protected abstract Actor buildEditor(Skin skin, I18N i18N);

	/**
	 * Updates the effect editor with the data of the given effect
	 */
	protected abstract void updateEditor(T effect);

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth() * 0.7f;
	}

	@Override
	public float getPrefHeight() {
		return Gdx.graphics.getHeight() * 0.6f;
	}

	@Override
	public void show(Stage stage) {
		float y = getY();
		setY(0);
		addAction(Actions.sequence(Actions.alpha(0), Actions.parallel(
				ExtraActions.moveToY(y, 0.2f, Interpolation.exp5Out),
				Actions.alpha(1.0f, 0.23f, Interpolation.exp5Out))));

	}

	@Override
	public void hide(Runnable runnable) {
		commander.getCommands().popStack(false);
		addAction(Actions.sequence(Actions.parallel(
				ExtraActions.moveToY(0, 0.2f, Interpolation.exp5Out),
				Actions.alpha(.0f, 0.23f, Interpolation.exp5Out)), Actions
				.run(runnable)));
	}

	private void cancel() {
		while (commander.getCommands().undo())
			;
		WidgetUtils.hideModal();
	}

	@Override
	public boolean onBackPressed() {
		cancel();
		return true;
	}

	@Override
	public HidePolicy getHidePolicy() {
		return HidePolicy.NO_HIDE;
	}
}
