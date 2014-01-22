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
package es.eucm.ead.mockup.core.engine;

import com.badlogic.gdx.scenes.scene2d.EventListener;

import es.eucm.ead.editor.Editor;
import es.eucm.ead.editor.conversors.EditorConversor;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.Factory;
import es.eucm.ead.engine.io.SchemaIO;
import es.eucm.ead.engine.scene.SceneManager;

/**
 * Editor's engine. Used to display previews or actual SceneElementActors while editing.
 * 
 * Work in progress.
 */
public class MockupEngine extends Engine {

	private MockupEventListener mockupEventListener;

	public MockupEngine() {
		super(null, false);
	}

	@Override
	protected SceneManager createSceneManager(Assets assets) {
		return new MockupSceneManager(assets);
	}

	@Override
	public void create() {
		Editor.conversor = new EditorConversor();
		super.create();
	}

	public void setMockupEventListener(MockupEventListener mockupEventListener) {
		this.mockupEventListener = mockupEventListener;
	}

	@Override
	protected EventListener createEventListener() {
		//	Esto lo creará nuestro Controlador para gestionar las iteracciones del usuario...
		// luego se seteará (almacenandose en un atributo) el objeto a esta clase (antes del create), 
		// y se devolverá ese atributo por aquí.
		return mockupEventListener;
	}

	@Override
	protected Factory createFactory() {
		return new MockupFactory();
	}

	@Override
	protected SchemaIO createJsonIO() {
		return new MockupIO();
	}
}
