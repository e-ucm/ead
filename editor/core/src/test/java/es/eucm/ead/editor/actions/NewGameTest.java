package es.eucm.ead.editor.actions;

import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.control.actions.NewGame;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NewGameTest extends EditorActionTest implements
		ModelListener<LoadEvent> {

	private int count;

	@Override
	protected String getEditorAction() {
		return NewGame.NAME;
	}

	@Before
	public void setUp() {
		super.setUp();
		controller.getModel().addModelListener(this);
		count = 0;
	}

	@Test
	public void testNoArgs() {
		controller.action(action);
		loadAllPendingAssets();
		File file = platform.lastTempFile();
		assertTrue(controller.getLoadingPath().startsWith(
				file.getAbsolutePath()));
		assertEquals(count, 1);
	}

	@Test
	public void testWithPath() {
		File file = platform.createTempFile(true);
		controller.action(action, file.getAbsolutePath());
		loadAllPendingAssets();
		assertTrue(controller.getLoadingPath().startsWith(
				file.getAbsolutePath()));
		assertEquals(count, 1);
	}

	@Test
	public void testInvalidName() {
		try {
			// The \0 : < > are an invalid characters for files in different OS.
			// With this, we ensure the file doesn't exist
			controller.action(action, ":<>ñor\0");
			fail("An exception should be thrown");
		} catch (EditorActionException e) {

		}
	}

	@Override
	public void modelChanged(LoadEvent event) {
		Model model = event.getModel();
		assertEquals(model.getProject().getEditScene(), "scene0");
		assertEquals(model.getGame().getInitialScene(), "scene0");
		count++;
	}
}
