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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.view.widgets.MultiWidget.MultiWidgetStyle;
import es.eucm.ead.editor.view.widgets.Switch.SwitchStyle;

public class SwitchDropDownPane extends DropDownPane {

	protected Switch switchWidget;

	private Label title;

	private MultiWidget openWidget;

	private boolean enable;

	public SwitchDropDownPane(Skin skin) {
		this(skin.get(SwitchDropDownPaneStyle.class));
	}

	public SwitchDropDownPane(SwitchDropDownPaneStyle style) {
		Table header = new Table();
		switchWidget = new Switch(style.switchStyle);
		switchWidget.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setEnable(switchWidget.isChecked());
				openWidget.setVisible(enable);
				if (enable) {
					open();
				} else {
					close();
				}
			}
		});
		header.add(switchWidget).padRight(WidgetBuilder.dpToPixels(16));
		header.add(title = new Label("", style.titleStyle)).expandX().width(0)
				.fillX();
		openWidget = new MultiWidget(new MultiWidgetStyle());
		Image openIcon = new Image(style.openIcon);
		openIcon.setColor(style.iconColor);
		Image closeIcon = new Image(style.closeIcon);
		closeIcon.setColor(style.iconColor);

		openWidget.addWidgets(openIcon, closeIcon);
		openWidget.setVisible(false);

		header.add(openWidget);

		addHeaderRow(header);
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
		switchWidget.setChecked(enable);
		openWidget.setVisible(enable);
	}

	public boolean isEnable() {
		return this.enable;
	}

	@Override
	public void open(boolean animate) {
		if (enable) {
			super.open(animate);
			openWidget.setSelectedWidget(1);
		}
	}

	@Override
	public void close(boolean animate) {
		super.close(animate);
		openWidget.setSelectedWidget(0);
	}

	public void setTitle(String text) {
		this.title.setText(text);
	}

	public static class SwitchDropDownPaneStyle {

		public SwitchStyle switchStyle;

		public LabelStyle titleStyle;

		public Drawable openIcon;

		public Drawable closeIcon;

		public Color iconColor;

	}
}
