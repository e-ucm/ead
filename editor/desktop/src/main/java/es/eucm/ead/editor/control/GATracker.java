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

import com.badlogic.gdx.Gdx;
import es.eucm.network.Method;
import es.eucm.network.requests.Request;
import es.eucm.network.requests.RequestCallback;
import es.eucm.network.requests.Response;

/**
 * Google Analytics tracker
 * 
 * Created by angel on 21/03/14.
 */
public class GATracker extends Tracker {

	public static final String ANALYTICS_URL = "http://www.google-analytics.com/collect";

	private String tracker;

	private String version;

	public GATracker(Controller controller) {
		super(controller);
		tracker = controller.getReleaseInfo().getTracking();
		version = controller.getReleaseInfo().getAppVersion();
		// If no tracking id, disable tracker
		if (tracker == null) {
			setEnabled(false);
		}

	}

	@Override
	protected void startSessionImpl() {
		ga("t=event&sc=start&ec=Session&ea=Start");
	}

	@Override
	protected void endSessionImpl() {
		ga("t=event&sc=end&ec=Session&ea=End");
	}

	/**
	 * Sends the given payload to backend
	 */
	private void ga(String payload) {
		Request request = new Request();
		request.setEntity("v=1&tid=" + tracker + "&an=eAdventureDesktop&av="
				+ version + "&cid=" + cid + "&" + payload);
		request.setMethod(Method.POST);
		requestHelper.send(request, ANALYTICS_URL, new RequestCallback() {
			@Override
			public void error(Request request, Throwable throwable) {
				Gdx.app.error("GATracker", "Error sending trace", throwable);
			}

			@Override
			public void success(Request request, Response response) {
				Gdx.app.debug("", "");
				// Do nothing
			}
		});
	}

	@Override
	public void preferenceChanged(String preferenceName, Object newValue) {
		if (tracker == null) {
			setEnabled(false);
		} else {
			super.preferenceChanged(preferenceName, newValue);
		}
	}
}
