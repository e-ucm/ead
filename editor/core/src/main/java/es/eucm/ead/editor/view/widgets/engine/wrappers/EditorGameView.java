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
package es.eucm.ead.editor.view.widgets.engine.wrappers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.view.ShapeDrawable;
import es.eucm.ead.engine.GameAssets;
import es.eucm.ead.engine.GameView;

public class EditorGameView extends GameView implements ShapeDrawable {

	private float cameraWidth;

	private float cameraHeight;

	private Model model;

	private static final Color STAGE_BORDER_COLOR = Color.WHITE;

	public EditorGameView(Model model, GameAssets gameAssets) {
		super(gameAssets);
		this.model = model;
		this.model.addLoadListener(new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {
				addProjectListener();
				modelLoaded();
			}
		});

	}

	protected void addProjectListener() {
		model.addFieldListener(model.getGame(), new FieldListener() {
			@Override
			public void modelChanged(FieldEvent event) {
				modelLoaded();
			}

			@Override
			public boolean listenToField(FieldNames fieldName) {
				return FieldNames.EDIT_SCENE == fieldName;
			}
		});
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		super.drawChildren(batch, parentAlpha);
	}

	protected void modelLoaded() {
		setCameraSize(model.getGame().getWidth(), model.getGame().getHeight());
		invalidateHierarchy();
	}

	public void setCameraSize(float width, float height) {
		this.cameraWidth = width;
		this.cameraHeight = height;
	}

	@Override
	public float getPrefWidth() {
		return cameraWidth;
	}

	@Override
	public float getPrefHeight() {
		return cameraHeight;
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		Actor a = super.hit(x, y, touchable);
		if (a == null) {
			if (x > 0 && x < getWidth() && y > 0 && y < getHeight()) {
				return this;
			}
		}
		return a;
	}

	@Override
	public void drawShapes(ShapeRenderer sr) {
		sr.begin(ShapeRenderer.ShapeType.Line);
		sr.setColor(STAGE_BORDER_COLOR);
		sr.rect(0, 0, getWidth(), getHeight());
		sr.end();
	}
}
