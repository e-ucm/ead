package es.eucm.ead.mockup.core.mockupengine;

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
		super(null);
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
