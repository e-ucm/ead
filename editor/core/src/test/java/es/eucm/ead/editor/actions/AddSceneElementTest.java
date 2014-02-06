package es.eucm.ead.editor.actions;

import com.badlogic.gdx.graphics.Texture;
import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.control.actions.AddSceneElement;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.renderers.Image;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddSceneElementTest extends EditorActionTest {
	@Override
	protected String getEditorAction() {
		return AddSceneElement.NAME;
	}

	@Test
	public void testAddSceneElement() throws URISyntaxException {
		openEmpty();
		URL url = ClassLoader.getSystemResource("blank.png");
		File image = new File(url.toURI());
		platform.pushPath(image.getAbsolutePath());

		Scene scene = controller.getModel().getEditScene();
		int size = scene.getChildren().size();
		controller.action(action);
		assertEquals(scene.getChildren().size(), size + 1);
		SceneElement sceneElement = scene.getChildren().get(0);
		assertEquals(sceneElement.getRenderer().getClass(), Image.class);
		String newPath = ((Image) sceneElement.getRenderer()).getUri();
		assertTrue(newPath.startsWith(ProjectAssets.IMAGES_FOLDER + "blank"));

		controller.getProjectAssets().finishLoading();
		assertTrue(controller.getProjectAssets().isLoaded(newPath,
				Texture.class));
	}
}
