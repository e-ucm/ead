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
package es.eucm.ead.mockup.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import es.eucm.ead.core.EAdEngine;
import es.eucm.ead.core.EngineStage;
import es.eucm.ead.core.Factory;
import es.eucm.ead.core.FileResolver;
import es.eucm.ead.core.io.JsonIO;
import es.eucm.ead.core.scene.SceneManager;
import es.eucm.ead.mockup.core.factories.MockupFactory;
import es.eucm.ead.mockup.core.io.MockupIO;
import es.eucm.ead.mockup.core.listeners.MockupEventListener;
import es.eucm.ead.mockup.core.scene.MockupSceneManager;

public class MockupEngine extends EAdEngine {

	public MockupEngine() {
		super(null);
	}

	public void setLoadingPath(String path) {
		super.setLoadingPath(path);
	}

	@Override
	public void create() {
		super.create();
	}

	@Override
	protected EngineStage createStage() {
		return new MockupStage(Gdx.graphics.getWidth(), Gdx.graphics
				.getHeight(), false);
	}

	@Override
	protected EventListener createEventListener() {
		return new MockupEventListener((MockupStage) EAdEngine.stage);
	}

	@Override
	protected Factory createFactory() {
		return new MockupFactory();
	}

	@Override
	protected JsonIO createJsonIO(FileResolver fileResolver) {
		return new MockupIO();
	}

	@Override
	protected SceneManager createSceneManager(AssetManager assetManager) {
		return new MockupSceneManager(assetManager);
	}
}
