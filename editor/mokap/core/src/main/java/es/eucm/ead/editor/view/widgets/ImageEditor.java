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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Views;
import es.eucm.ead.editor.control.actions.editor.LaunchImageEditor;
import es.eucm.ead.editor.control.actions.model.ChangeSelectionColor;
import es.eucm.ead.editor.control.actions.model.ChangeSelectionText;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.SceneEditor;
import es.eucm.ead.editor.view.widgets.draw.BrushStrokesPicker.BrushStrokesPickerStyle;
import es.eucm.ead.editor.view.widgets.draw.ColorPickerPanel;
import es.eucm.ead.editor.view.widgets.draw.ColorPickerPanel.ColorPickerPanelStyle;
import es.eucm.ead.editor.view.widgets.draw.SlideColorPicker;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.entities.ModelEntity;

public class ImageEditor extends ContextMenu {

	private Controller controller;

	private ColorPickerPanel colorPicker;

	private ModelEntity modelEntity;

	private Actor image;

	public ImageEditor(Controller control) {
		this(control, control.getApplicationAssets().getSkin()
				.get(ImageEditorStyle.class));
	}

	public ImageEditor(Controller control, ImageEditorStyle style) {
		this.controller = control;
		float pad = WidgetBuilder.dpToPixels(8);
		pad(pad);

		setBackground(style.background);

		Skin skin = controller.getApplicationAssets().getSkin();
		colorPicker = new ColorPickerPanel(skin, style.colorPickerStyle,
				controller.getPreferences());
		colorPicker.addListener(new SlideColorPicker.ColorListener() {
			@Override
			public void colorChanged(SlideColorPicker.ColorEvent event) {
				if (!event.getColor().equals(
						Q.toLibgdxColor(modelEntity.getColor()))) {
					image.setColor(event.getColor());
					if (!event.isDragging()) {
						controller.action(ChangeSelectionColor.class,
								event.getColor());
					}
				}
			}
		});

		Button pixlr = WidgetBuilder.button(SkinConstants.IC_EDIT, controller
				.getApplicationAssets().getI18N().m("edit.Pixlr"),
				SkinConstants.STYLE_CONTEXT, LaunchImageEditor.class);
		add(pixlr).expandX().fill();
		row();
		add(colorPicker).padBottom(pad);
		colorPicker.completeRowsIfPossible(this);
	}

	public void prepare(SceneEditor sceneEditor) {
		modelEntity = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE_ELEMENT);
		image = sceneEditor.getGroupEditor().findActor(modelEntity);
		Color color = Q.toLibgdxColor(modelEntity.getColor());
		colorPicker.setPickedColor(color);
		image.setColor(color);
	}

	@Override
	public void show(Views views) {
		super.show(views);
		colorPicker.initResources();
	}

	@Override
	public void hide(Runnable runnable) {
		colorPicker.setUpPickedColor();
		SequenceAction hideAction = getHideAction(runnable);
		hideAction.addAction(Actions.run(colorPicker.getReleaseResources()));
		addAction(hideAction);
	}

	@Override
	public boolean hideAlways() {
		return false;
	}

	static public class ImageEditorStyle {

		public ColorPickerPanelStyle colorPickerStyle;

		/** Optional */
		public Drawable background;

		public ImageEditorStyle() {
		}

		public ImageEditorStyle(BrushStrokesPickerStyle style) {
			this.colorPickerStyle = style.colorPickerStyle;
			this.background = style.background;
		}

		public ImageEditorStyle(ColorPickerPanelStyle colorPickerStyle,
				Drawable background) {
			this.colorPickerStyle = colorPickerStyle;
			this.background = background;
		}
	}
}
