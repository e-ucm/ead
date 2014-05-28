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
package es.eucm.ead.editor.control;

import es.eucm.ead.editor.control.ViewsHistory.ViewUpdate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by angel on 26/05/14.
 */
public class ViewsHistoryTest {

	@Test
	public void testViewsHistory() {
		ViewsHistory history = new ViewsHistory();

		history.viewUpdated(View1.class, "arg1");
		history.viewUpdated(View2.class, "arg1");
		history.viewUpdated(View3.class, "arg1");
		history.viewUpdated(View1.class, "arg2");
		history.viewUpdated(View1.class, "arg3");
		history.viewUpdated(View1.class, "arg3");
		history.viewUpdated(View3.class, "arg4");

		assertNull(history.next());

		ViewUpdate viewUpdate = history.back();
		assertEquals(viewUpdate.getViewClass(), View1.class);
		assertEquals(viewUpdate.getArgs()[0], "arg3");

		viewUpdate = history.back();
		assertEquals(viewUpdate.getViewClass(), View1.class);
		assertEquals(viewUpdate.getArgs()[0], "arg2");

		viewUpdate = history.back();
		assertEquals(viewUpdate.getViewClass(), View3.class);
		assertEquals(viewUpdate.getArgs()[0], "arg1");

		viewUpdate = history.back();
		assertEquals(viewUpdate.getViewClass(), View2.class);
		assertEquals(viewUpdate.getArgs()[0], "arg1");

		viewUpdate = history.back();
		assertEquals(viewUpdate.getViewClass(), View1.class);
		assertEquals(viewUpdate.getArgs()[0], "arg1");

		assertNull(history.back());

		viewUpdate = history.next();
		assertEquals(viewUpdate.getViewClass(), View2.class);
		assertEquals(viewUpdate.getArgs()[0], "arg1");

		viewUpdate = history.next();
		assertEquals(viewUpdate.getViewClass(), View3.class);
		assertEquals(viewUpdate.getArgs()[0], "arg1");

		viewUpdate = history.next();
		assertEquals(viewUpdate.getViewClass(), View1.class);
		assertEquals(viewUpdate.getArgs()[0], "arg2");

		viewUpdate = history.next();
		assertEquals(viewUpdate.getViewClass(), View1.class);
		assertEquals(viewUpdate.getArgs()[0], "arg3");

		viewUpdate = history.next();
		assertEquals(viewUpdate.getViewClass(), View3.class);
		assertEquals(viewUpdate.getArgs()[0], "arg4");

		assertNull(history.next());
	}

	private static class View1 {
	}

	private static class View2 {
	}

	private static class View3 {
	}

}
