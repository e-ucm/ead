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
package es.eucm.ead.editor.processors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import es.eucm.ead.editor.components.EditorEmptyActor;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.engine.Engine;
import es.eucm.ead.engine.components.renderers.EmptyActor;
import es.eucm.ead.engine.processors.renderers.EmptyRendererProcessor;

public class EditorEmptyRendererProcessor extends EmptyRendererProcessor {

	public static final Color INTERACTIVE_ZONE_COLOR = new Color(0.0f, 1.0f,
			0.0f, 0.35f);

	private SpriteDrawable drawable;

	private SpriteDrawable extendedDrawable;

	private Engine engine;

	public EditorEmptyRendererProcessor(Controller controller) {
		super(controller.getEngine().getGameLoop(), controller
				.getEditorGameAssets());
		this.engine = controller.getEngine();
		TextureRegionDrawable blank = (TextureRegionDrawable) controller
				.getApplicationAssets().getSkin().getDrawable("blank");
		Sprite sprite = new Sprite(blank.getRegion());
		sprite.setColor(INTERACTIVE_ZONE_COLOR);
		drawable = new SpriteDrawable(sprite);

		sprite = new Sprite(blank.getRegion());
		sprite.setColor(INTERACTIVE_ZONE_COLOR);
		sprite.setAlpha(0.25f);
		extendedDrawable = new SpriteDrawable(sprite);
	}

	@Override
	protected EmptyActor createActor() {
		EditorEmptyActor emptyRenderer = new EditorEmptyActor();
		emptyRenderer.setDrawables(drawable, extendedDrawable);
		emptyRenderer.setEngine(engine);
		return emptyRenderer;
	}
}
