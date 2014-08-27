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
package es.eucm.ead.editor.view.widgets.editionview.draw;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.editionview.MockupSceneEditor;
import es.eucm.ead.editor.view.widgets.editionview.draw.BrushStrokes.Mode;
import es.eucm.ead.engine.I18N;

public class PaintToolbar extends Toolbar {

	private static final Vector2 TEMP = new Vector2();

	private static final float NORMAL_PAD = 40;

	private MockupSceneEditor parent;

	private BrushStrokes brushStrokes;

	public PaintToolbar(MockupSceneEditor parent, Controller controller) {
		super(controller.getApplicationAssets().getSkin(), "white_bottom");
		this.parent = parent;
		brushStrokes = new BrushStrokes(parent, controller);
		ApplicationAssets assets = controller.getApplicationAssets();
		Skin skin = assets.getSkin();
		I18N i18n = assets.getI18N();

		final Image erase = new Image(skin, "rectangle");
		erase.setColor(Color.PINK);

		// Colors
		Image color1 = new Image(skin, "rectangle");
		color1.setColor(Color.YELLOW);

		Image color2 = new Image(skin, "rectangle");
		color2.setColor(Color.ORANGE);

		Image color3 = new Image(skin, "rectangle");
		color3.setColor(Color.RED);

		Image color4 = new Image(skin, "rectangle");
		color4.setColor(Color.GREEN);

		Image color5 = new Image(skin, "rectangle");
		color5.setColor(Color.BLUE);

		Image color6 = new Image(skin, "rectangle");
		color6.setColor(Color.BLACK);

		final Slider slider = new Slider(10, 40, 1, false, skin,
				"white-horizontal");
		brushStrokes.setMaxDrawRadius(80f);
		brushStrokes.setRadius(slider.getValue());

		final TextButton save = new TextButton(i18n.m("save"), skin, "white");

		final TextButton cancel = new TextButton(i18n.m("cancel"), skin,
				"white");
		cancel.setDisabled(true);

		add(erase).padLeft(NORMAL_PAD).padRight(NORMAL_PAD);
		add(color1);
		add(color2);
		add(color3);
		add(color4);
		add(color5);
		add(color6);
		add(slider).expandX().fill().padLeft(NORMAL_PAD).padRight(NORMAL_PAD);
		add(save).padLeft(NORMAL_PAD);
		add(cancel).padLeft(NORMAL_PAD).padRight(NORMAL_PAD);

		ChangeListener listener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listener = event.getListenerActor();

				if (listener == cancel) {
					hide();
				} else if (listener == save) {
					if (brushStrokes.save()) {
						brushStrokes.createSceneElement();
					}
					hide();
				} else if (listener == slider) {
					brushStrokes.setRadius(slider.getValue());
				}
			}
		};

		ClickListener clickListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor listener = event.getListenerActor();
				Mode mode;
				if (listener == erase) {
					mode = Mode.ERASE;
				} else {
					mode = Mode.DRAW;
					brushStrokes.setColor(event.getListenerActor().getColor());
				}
				brushStrokes.setMode(mode);
			}
		};
		erase.addListener(clickListener);
		color1.addListener(clickListener);
		color2.addListener(clickListener);
		color3.addListener(clickListener);
		color4.addListener(clickListener);
		color5.addListener(clickListener);
		color6.addListener(clickListener);
		slider.addListener(listener);
		save.addListener(listener);
		cancel.addListener(listener);
	}

	public void show() {
		setTouchable(Touchable.enabled);
		parent.getStage().addActor(this);
		parent.localToStageCoordinates(TEMP.set(0f, 0f));
		parent.addActor(brushStrokes);
		parent.getGroupEditor().setTouchable(Touchable.disabled);
		brushStrokes.setBounds(0f, 0f, parent.getWidth(), parent.getHeight());
		brushStrokes.show();

		float prefW = MathUtils.round(getPrefWidth());
		float prefH = MathUtils.round(getPrefHeight());
		float x = MathUtils.round(TEMP.x + (parent.getWidth() - prefW) * .5f);
		float y = -prefH;

		setBounds(x, y, prefW, prefH);
		addAction(Actions.moveTo(x, 0f, .3f, Interpolation.sineOut));
	}

	public void hide() {
		if (!isShowing())
			return;
		clearActions();
		setTouchable(Touchable.disabled);
		brushStrokes.remove();
		brushStrokes.hide();
		parent.getGroupEditor().setTouchable(Touchable.enabled);

		addAction(sequence(
				Actions.moveTo(getX(), -getHeight(), .2f, Interpolation.fade),
				Actions.removeActor()));
	}

	public boolean isShowing() {
		return hasParent() && isTouchable();
	}

}
