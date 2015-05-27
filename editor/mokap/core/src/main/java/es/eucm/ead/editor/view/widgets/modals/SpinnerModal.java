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
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.utils.Actions2;
import es.eucm.ead.editor.view.Modal;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.i18n.I18N;

public class SpinnerModal extends LinearLayout implements Modal {

	private Label prefixLabel;

	private Label suffixLabel;

	private Label numberLabel;

	private int value;

	private int minValue;

	private int maxValue;

	private ClickRepeatTask repeatTask = new ClickRepeatTask();

	private SpinnerModalListener modalListener;

	private Views views;

	public SpinnerModal(Skin skin, I18N i18N) {
		this(skin.get(SpinnerModalStyle.class), i18N);
	}

	public void setModalListener(SpinnerModalListener modalListener) {
		this.modalListener = modalListener;
	}

	public SpinnerModal(SpinnerModalStyle style, I18N i18N) {
		super(false);
		background(style.background);
		pad(WidgetBuilder.dpToPixels(16));

		LinearLayout statement = new LinearLayout(true);
		statement.add(prefixLabel = new Label("", style.textStyle)).centerY();

		LinearLayout numberButton = new LinearLayout(false);

		ImageButton up = new ImageButton(style.upStyle);
		up.addListener(new IncListener(1));
		numberButton.add(up).centerX();
		numberButton.add(numberLabel = new Label("", style.numberStyle))
				.centerX();

		ImageButton down = new ImageButton(style.downStyle);
		down.addListener(new IncListener(-1));
		numberButton.add(down).centerX();

		statement.add(numberButton).centerY()
				.marginLeft(WidgetBuilder.dpToPixels(16))
				.marginRight(WidgetBuilder.dpToPixels(16));
		statement.add(suffixLabel = new Label("", style.textStyle)).centerY();

		add(statement).expandY().marginLeft(WidgetBuilder.dpToPixels(16))
				.marginRight(WidgetBuilder.dpToPixels(16));

		LinearLayout closeButtons = new LinearLayout(true);
		closeButtons.addSpace();
		TextButton cancel = WidgetBuilder.dialogButton(i18N.m("cancel"),
				style.buttonsStyle);
		cancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (modalListener != null) {
					modalListener.cancelled();
				}
				views.hideModal();
			}
		});

		closeButtons.add(cancel);

		TextButton ok = WidgetBuilder.dialogButton(i18N.m("ok"),
				style.buttonsStyle);
		ok.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (modalListener != null) {
					modalListener.value(value);
				}
				views.hideModal();
			}
		});
		closeButtons.add(ok).marginLeft(WidgetBuilder.dpToPixels(8));
		add(closeButtons).expandX();
	}

	private void setValue(int value) {
		this.value = Math.min(maxValue, Math.max(minValue, value));
		numberLabel.setText(this.value + "");
	}

	private void inc(int increment) {
		int newValue = value + increment;
		if (newValue < minValue) {
			newValue = maxValue;
		} else if (newValue > maxValue) {
			newValue = minValue;
		}
		setValue(newValue);
	}

	public void set(String prefix, String suffix, int initialValue,
			int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		prefixLabel.setText(prefix);
		setValue(initialValue);
		suffixLabel.setText(suffix);
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

	class ClickRepeatTask extends Task {
		int inc;

		public void run() {
			inc(this.inc);
		}
	}

	public interface SpinnerModalListener {

		void value(int value);

		void cancelled();

	}

	public class IncListener extends InputListener {

		private int inc;

		public IncListener(int inc) {
			this.inc = inc;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			repeatTask.inc = inc;
			if (!repeatTask.isScheduled()) {
				Timer.schedule(repeatTask, 0.4f, 0.05f);
			}
			return true;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			inc(inc);
			repeatTask.cancel();
		}

	}

	public static class SpinnerModalStyle {

		public Drawable background;

		public LabelStyle textStyle;

		public LabelStyle numberStyle;

		public ImageButtonStyle upStyle;

		public ImageButtonStyle downStyle;

		public TextButtonStyle buttonsStyle;

	}
}
