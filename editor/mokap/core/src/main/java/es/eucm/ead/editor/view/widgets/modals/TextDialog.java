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
package es.eucm.ead.editor.view.widgets.modals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.utils.Actions2;
import es.eucm.ead.editor.view.Modal;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.i18n.I18N;

public class TextDialog extends Table implements Modal {

	private Label textLabel;

	private Views views;

	public TextDialog(Skin skin, I18N i18N) {
		this(skin.get(TextDialogStyle.class), i18N);
	}

	public TextDialog(TextDialogStyle style, I18N i18N) {
		background(style.background);
		float pad24dp = WidgetBuilder.dpToPixels(24);
		float pad16dp = WidgetBuilder.dpToPixels(16);
		pad(pad24dp, pad24dp, pad16dp, pad24dp);

		add(textLabel = new Label("", style.textStyle)).expand().fill();
		textLabel.setWrap(true);

		TextButton ok = WidgetBuilder.dialogButton(i18N.m("ok"),
				style.buttonStyle);
		ok.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				views.hideModal();
			}
		});
		row();
		add(ok).expandX().right().padTop(pad16dp);

	}

	@Override
	public void show(Views views) {
		this.views = views;
		((Layout) getParent()).layout();
		float y = getY();
		setY(Gdx.graphics.getHeight());
		clearActions();
		addAction(Actions2.moveToY(y, 0.33f, Interpolation.exp5Out));
	}

	@Override
	public void hide(Runnable runnable) {
		addAction(Actions.sequence(Actions2.moveToY(Gdx.graphics.getHeight(),
				0.33f, Interpolation.exp5Out), Actions.run(runnable)));
	}

	@Override
	public boolean hideAlways() {
		return false;
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth() * .7f;
	}

	public static class TextDialogStyle {

		public Drawable background;

		public LabelStyle textStyle;

		public TextButtonStyle buttonStyle;

	}

	public void setText(String text) {
		this.textLabel.setText(text);
	}
}
