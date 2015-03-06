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
package es.eucm.ead.editor.view.builders.scene.groupeditor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor.GroupEditorStyle;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.ContextMenu;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

public class LayerSelector extends ContextMenu {

	private GroupEditor groupEditor;

	private GroupEditorStyle style;

	private ScrollPane scrollPane;

	private LinearLayout layers;

	public LayerSelector(GroupEditor groupeditor, GroupEditorStyle style) {
		super();
		this.style = style;
		this.groupEditor = groupeditor;
		scrollPane = new ScrollPane(layers = new LinearLayout(true));
		scrollPane.setScrollingDisabled(false, true);
		scrollPane.setOverscroll(false, false);
		add(scrollPane).fillX();
		setBackground(style.layersBackground);
		this.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor target = event.getTarget();
				if (target instanceof Layer) {
					if (!groupEditor.isMultipleSelection()) {
						for (Actor layer : layers.getChildren()) {
							if (layer != target) {
								((Layer) layer).setChecked(false);
							}
						}
					}
					groupEditor.addToSelection(((Layer) target).actor);
					groupEditor.fireSelection();
				}
			}
		});
	}

	@Override
	public float getPrefWidth() {
		return Math.min(layers.getPrefWidth(), getParent().getWidth());
	}

	public void prepare(Array<Actor> layersTouched) {
		scrollPane.setScrollPercentX(0.0f);
		layers.clearChildren();
		layersTouched.reverse();
		for (Actor layer : layersTouched) {
			Layer layerWidget = new Layer(style.layerButtonStyle);
			layerWidget.setChecked(groupEditor.getSelection().contains(layer,
					true));
			layerWidget.setActor(layer);
			layers.add(layerWidget);
		}
		layers.pack();
	}

	public static class Layer extends Button {

		private static final float CM = 2.0f;

		private static final float PADDING = 0.1f;

		private Container<Actor> container = new Container<Actor>();

		private Actor actor;

		public Layer(ButtonStyle buttonStyle) {
			super(buttonStyle);
			add(container);
			container.size(
					AbstractWidget.cmToXPixels(CM)
							+ AbstractWidget.cmToXPixels(PADDING) * 2,
					AbstractWidget.cmToYPixels(CM)
							+ AbstractWidget.cmToYPixels(PADDING) * 2);
		}

		public void setActor(Actor actor) {
			this.actor = actor;
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			float x = actor.getX();
			float y = actor.getY();
			float rotation = actor.getRotation();
			float oldScaleX = actor.getScaleX();
			float oldScaleY = actor.getScaleY();
			float originX = actor.getOriginX();
			float originY = actor.getOriginY();
			Group parent = actor.getParent();
			int zIndex = actor.getZIndex();

			float scaleX = (getWidth() - AbstractWidget.cmToXPixels(PADDING) * 2)
					/ actor.getWidth();
			float scaleY = (getHeight() - AbstractWidget.cmToYPixels(PADDING) * 2)
					/ actor.getHeight();
			float scale = Math.min(scaleX, scaleY);
			float deltaX = (actor.getWidth() * scaleX - actor.getWidth()
					* scale) / 2.0f;
			float deltaY = (actor.getHeight() * scaleY - actor.getHeight()
					* scale) / 2.0f;

			actor.setOrigin(0, 0);
			actor.setPosition(getX() + AbstractWidget.cmToXPixels(PADDING)
					+ deltaX, AbstractWidget.cmToYPixels(PADDING) + deltaY
					+ getY());
			actor.setRotation(0);
			actor.setScale(scale, scale);

			container.setActor(actor);

			actor.draw(batch, parentAlpha);
			parent.addActorAt(zIndex, actor);

			actor.setPosition(x, y);
			actor.setRotation(rotation);
			actor.setScale(oldScaleX, oldScaleY);
			actor.setOrigin(originX, originY);
		}
	}

}
