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
package es.eucm.ead.editor.view.widgets.mockup.panels;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.view.widgets.mockup.buttons.TweenButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TweenDragButton.TweenType;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Tween;

/**
 * HiddenPanel to represent and modify the Tween properties. The
 * TweenEditionPanel shows some characteristics depending on the type of Tween.
 * 
 */
public class TweenEditionPanel extends HiddenPanel {

	private TextField xText;
	private TextField yText;
	private TextField angle;
	private TextField scaleX;
	private TextField scaleY;
	private TextField alpha;
	private TextField duration;

	private Tween tween;

	private TweenButton tweenButton;

	private Label lft;
	private Label rgt;
	private Label bot;

	private Cell<TextField> left;
	private Cell<TextField> right;

	private TextButton accept;
	private TextButton cancel;

	private Dialog dialog;

	private I18N i18n;

	public TweenEditionPanel(Skin skin, final I18N i18n) {
		super(skin);
		this.setVisible(false);

		this.i18n = i18n;

		this.dialog = new Dialog("", skin);
		this.dialog.button(i18n.m("general.ok"));

		accept = new TextButton(i18n.m("general.accept"), skin);
		cancel = new TextButton(i18n.m("general.cancel"), skin);

		this.lft = new Label("", skin);
		this.rgt = new Label("", skin);
		this.bot = new Label(i18n.m("general.edition.tween.duration") + ":",
				skin);

		this.add(lft);
		this.left = this.add();
		this.add(rgt);
		this.right = this.add();

		this.row();

		this.add(bot);
		this.duration = new TextField("", skin);
		this.add(this.duration);

		this.row();

		this.add(accept);
		this.add(cancel);

		cancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TweenEditionPanel.this.hide();
			}
		});

		accept.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				int errorCode = saveText();
				if (errorCode == 0) {
					TweenEditionPanel.this.hide();
					tweenButton.setDuration(duration.getText());
					tweenButton.layout();
					tweenButton.getParentTrack().layout();
				} else if (errorCode == -1) {
					TweenEditionPanel.this.dialog.setTitle(i18n
							.m("general.edition.tween.numbers"));
					TweenEditionPanel.this.dialog.show(getStage());
				} else if (errorCode == -2) {
					TweenEditionPanel.this.dialog.setTitle(i18n
							.m("general.edition.tween.alpha-0-1"));
					TweenEditionPanel.this.dialog.show(getStage());
				}
			}
		});

		this.xText = new TextField("", skin);
		this.yText = new TextField("", skin);
		this.angle = new TextField("", skin);
		this.scaleX = new TextField("", skin);
		this.scaleY = new TextField("", skin);
		this.alpha = new TextField("", skin);

	}

	public void show(TweenType type, TweenButton tweenButton) {
		this.tweenButton = tweenButton;
		this.tween = tweenButton.getTween();
		if (type == TweenType.MOVE) {
			showMove();
		} else if (type == TweenType.SCALE) {
			showScale();
		} else if (type == TweenType.ROTATE) {
			showRotate();
		} else if (type == TweenType.ALPHA) {
			showAlpha();
		}
	}

	private void showScale() {
		this.lft.setText(i18n.m("general.edition.tween.scale") + " X:");
		this.rgt.setText(i18n.m("general.edition.tween.scale") + " Y:");

		ScaleTween aux = (ScaleTween) tween;
		scaleX.setText("" + aux.getScaleX());
		scaleY.setText("" + aux.getScaleY());
		duration.setText("" + tween.getDuration());

		this.setWidget(scaleX, scaleY);
		super.show();
	}

	private void showMove() {
		this.lft.setText(i18n.m("general.edition.tween.goal") + " X:");
		this.rgt.setText(i18n.m("general.edition.tween.goal") + " Y:");

		MoveTween aux = (MoveTween) tween;
		scaleX.setText("" + aux.getX());
		scaleY.setText("" + aux.getY());
		duration.setText("" + tween.getDuration());

		this.setWidget(xText, yText);
		super.show();
	}

	private void showRotate() {
		this.lft.setText(i18n.m("general.edition.tween.angle") + ":");
		this.rgt.setText("");

		RotateTween aux = (RotateTween) tween;
		angle.setText("" + aux.getRotation());
		duration.setText("" + tween.getDuration());

		this.setWidget(angle, null);
		super.show();
	}

	private void showAlpha() {
		this.lft.setText(i18n.m("general.edition.alpha") + ":");
		this.rgt.setText("");

		AlphaTween aux = (AlphaTween) tween;
		alpha.setText("" + aux.getAlpha());
		duration.setText("" + tween.getDuration());

		this.setWidget(alpha, null);
		super.show();
	}

	public void setTween(Tween tween) {
		this.tween = tween;
	}

	/**
	 * Saves the values of fields.
	 * 
	 * @return <b>0</b> if all correct.<br>
	 *         <b>-1</b> if any field has non-numeric character the function.<br>
	 *         <b>-2</b> if the TweenEditionPanel show a AlphaTween and the
	 *         alpha field no has a value between 0 and 1.
	 */
	private int saveText() {
		try {
			if (tween instanceof MoveTween) {
				((MoveTween) tween).setX(Float.valueOf(xText.getText()));
				((MoveTween) tween).setY(Float.valueOf(yText.getText()));
			} else if (tween instanceof ScaleTween) {
				((ScaleTween) tween).setScaleX(Float.valueOf(scaleX.getText()));
				((ScaleTween) tween).setScaleY(Float.valueOf(scaleY.getText()));
			} else if (tween instanceof RotateTween) {
				((RotateTween) tween)
						.setRotation(Float.valueOf(angle.getText()));
			} else if (tween instanceof AlphaTween) {
				float f = Float.valueOf(alpha.getText());
				if (f >= 0f && f <= 1f) {
					((AlphaTween) tween).setAlpha(f);
				} else {
					return -2;
				}
			}
			tween.setDuration(Float.valueOf(duration.getText()));
		} catch (NumberFormatException e) {
			return -1;
		}

		return 0;
	}

	private void setWidget(TextField left, TextField right) {
		this.left.setWidget(null);
		this.right.setWidget(null);
		this.left.setWidget(left);
		if (right != null) {
			this.right.setWidget(right);
		}
	}
}
