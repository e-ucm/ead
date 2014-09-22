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
package es.eucm.ead.editor.control.background;

import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * 
 * Class to test {@link BackgroundExecutor}
 * 
 * Created by angel on 25/03/14.
 */
public class BackgroundExecutorTest {

	private BackgroundExecutor executor;

	private boolean done;

	@Before
	public void setUp() {
		executor = new BackgroundExecutor();
	}

	@Test
	public void testNormalTask() {

		BackgroundTask<Boolean> task = new BackgroundTask<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				int sleeps = 10;
				for (int i = 0; i < sleeps; i++) {
					float percentage = (float) i / (float) sleeps;
					setCompletionPercentage(percentage);
					Thread.sleep(100);
				}
				return true;
			}
		};

		done = false;
		executor.submit(task, new BackgroundTaskListener<Boolean>() {

			private float lastPercentage = 0.0f;

			@Override
			public void completionPercentage(float percentage) {
				assertTrue(lastPercentage <= percentage);
			}

			@Override
			public void done(BackgroundExecutor backgroundExecutor,
					Boolean result) {
				assertTrue(result);
				done = true;
			}

			@Override
			public void error(Throwable e) {

			}
		});
		long loops = 0;
		while (!done) {
			executor.act();
			loops++;
		}

		assertTrue(loops > 2);
	}

	@Test
	public void testErronousTask() {
		BackgroundTask<Boolean> task = new BackgroundTask<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				throw new Exception("ñor");
			}
		};

		done = false;
		executor.submit(task, new BackgroundTaskListener<Boolean>() {

			@Override
			public void completionPercentage(float percentage) {
			}

			@Override
			public void done(BackgroundExecutor backgroundExecutor,
					Boolean result) {
				fail("This shouldn't be called");
			}

			@Override
			public void error(Throwable e) {
				assertEquals(e.getCause().getMessage(), "ñor");
				done = true;
			}
		});
		while (!done) {
			executor.act();
		}
	}
}
