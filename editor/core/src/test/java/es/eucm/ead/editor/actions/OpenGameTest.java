package es.eucm.ead.editor.actions;

import com.badlogic.gdx.utils.GdxRuntimeException;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class OpenGameTest extends EditorActionTest implements
		ModelListener<LoadEvent> {

	private int count;

	private File emptyProject;

	@Override
	protected String getEditorAction() {
		return OpenGame.NAME;
	}

	@Before
	public void setUp() {
		super.setUp();
		controller.getModel().addModelListener(this);
		count = 0;
		URL url = ClassLoader.getSystemResource("projects/empty/project.json");
		try {
			emptyProject = new File(url.toURI()).getParentFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNoArgs() {
		platform.pushPath(emptyProject.getAbsolutePath());
		controller.action(action);
		loadAllPendingAssets();
		assertEquals(emptyProject.getAbsolutePath() + "/",
				controller.getLoadingPath());
		assertEquals(count, 1);
	}

	@Test
	public void testWithPath() {
		controller.action(action, emptyProject.getAbsolutePath());
		loadAllPendingAssets();
		assertEquals(emptyProject.getAbsolutePath() + "/",
				controller.getLoadingPath());
		assertEquals(count, 1);
	}

	@Test
	public void testWithInvalidPath() {
		try {
			controller.action(action, "ñor/ñor");
			fail("An exception should be thrown");
		} catch (EditorActionException e) {

		}
	}

	@Test
	public void testInvalidProject() {
		URL url = ClassLoader
				.getSystemResource("projects/invalid/project.json");
		File project = null;
		try {
			project = new File(url.toURI()).getParentFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		controller.action(action, project.getAbsolutePath());

		try {
			controller.getProjectAssets().finishLoading();
			fail("An exception must be thrown");
		} catch (GdxRuntimeException e) {

		}

	}

	@Override
	public void modelChanged(LoadEvent event) {
		Model model = event.getModel();
		assertNull(model.getProject().getEditScene());
		assertEquals(model.getGame().getTitle(), "");
		count++;
	}
}
