package es.eucm.ead.editor.actions;

import org.junit.Test;

import es.eucm.ead.editor.control.actions.RemoveFromScene;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;

import static org.junit.Assert.assertEquals;

public class RemoveFromSceneTest extends EditorActionTest {
	@Override
	protected Class getEditorAction() {
		return RemoveFromScene.class;
	}

	@Test
	public void testRemove() {
		Scene scene = new Scene();
		SceneElement sceneElement = new SceneElement();
		scene.getChildren().add(sceneElement);
		mockController.action(action, scene, sceneElement);
		assertEquals(scene.getChildren().size(), 0);
	}

}
