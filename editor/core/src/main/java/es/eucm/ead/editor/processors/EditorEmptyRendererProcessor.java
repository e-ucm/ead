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

import ashley.core.Component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.components.EditorEmptyRendererComponent;
import es.eucm.ead.editor.control.engine.Engine;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.processors.renderers.EmptyRendererProcessor;
import es.eucm.ead.schema.renderers.EmptyRenderer;

public class EditorEmptyRendererProcessor extends EmptyRendererProcessor {

	public static final Color INTERACTIVE_ZONE_COLOR = new Color(0.0f, 1.0f,
			0.0f, 0.25f);

	private SpriteDrawable drawable;

	private Engine engine;

	public EditorEmptyRendererProcessor(Engine engine, GameLoop gameLoop,
			ApplicationAssets applicationAssets) {
		super(gameLoop);
		this.engine = engine;
		TextureRegionDrawable blank = (TextureRegionDrawable) applicationAssets
				.getSkin().getDrawable("blank");
		Sprite sprite = new Sprite(blank.getRegion());
		sprite.setColor(INTERACTIVE_ZONE_COLOR);
		drawable = new SpriteDrawable(sprite);
	}

	@Override
	public Component getComponent(EmptyRenderer component) {
		EditorEmptyRendererComponent emptyRendererComponent = gameLoop
				.createComponent(EditorEmptyRendererComponent.class);
		emptyRendererComponent.setDrawable(drawable);
		emptyRendererComponent.setEngine(engine);
		read(emptyRendererComponent, component);
		return emptyRendererComponent;
	}
}
