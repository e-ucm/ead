/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import es.eucm.ead.core.factories.Conversor;
import es.eucm.ead.core.factories.ConversorFactory;
import es.eucm.ead.core.io.EditorIO;
import es.eucm.ead.core.io.JsonIO;
import es.eucm.ead.core.io.Platform;
import es.eucm.ead.core.listeners.EditorEventListener;
import es.eucm.ead.core.scene.EditorSceneManager;
import es.eucm.ead.core.scene.SceneManager;

public class EditorEngine extends EAdEngine {

	public static Conversor conversor;
	public static Platform platform;

	public EditorEngine(String path, Platform platform) {
		super(path);
		this.platform = platform;
	}

	@Override
	public void create() {
		conversor = new Conversor();
		super.create();
	}

	@Override
	protected EngineStage createStage() {
		return new EditorStage(Gdx.graphics.getWidth(), Gdx.graphics
				.getHeight(), false);
	}

	@Override
	protected EventListener createEventListener() {
		return new EditorEventListener((EditorStage) EditorEngine.stage);
	}

	@Override
	protected Factory createFactory() {
		return new ConversorFactory();
	}

	@Override
	protected JsonIO createJsonIO(FileResolver fileResolver) {
		return new EditorIO(fileResolver);
	}

	@Override
	protected SceneManager createSceneManager(AssetManager assetManager) {
		return new EditorSceneManager(assetManager);
	}
}
