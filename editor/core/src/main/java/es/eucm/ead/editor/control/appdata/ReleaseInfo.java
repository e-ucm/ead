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

package es.eucm.ead.editor.control.appdata;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 * Simple object for storing information about the current release of the ead2
 * editor. This release.json file should be generated automatically on each
 * release.
 * 
 */
@Generated("org.jsonschema2pojo")
public class ReleaseInfo {

	/**
	 * The release version given as three numbers separated by dots (e.g. 2.0.0)
	 * 
	 */
	private String appVersion;
	/**
	 * The release flavour. For explanations on canary, beta and stable, see the
	 * wiki
	 * 
	 */
	private ReleaseInfo.ReleaseType releaseType;
	/**
	 * true if this is a dev working copy, not an actual release. If true, the
	 * update system is disabled.
	 * 
	 */
	private boolean dev;
	/**
	 * The API Key used for tracking
	 * 
	 */
	private String tracking;
	/**
	 * URL that stores the update.json file with information about the latest
	 * release available.
	 * 
	 */
	private String updateURL;
	/**
	 * URL for bug reporting.
	 * 
	 */
	private String bugReportURL;
	/**
	 * Simple enum that stores platform-os types. To be used by UpdateInfo and
	 * ReleaseInfo. More info:
	 * https://github.com/e-ucm/ead/wiki/Platform-version-names
	 * 
	 */
	private OS os = OS.fromValue("multiplatform");
	/**
	 * The relative or absolute path of the engine.jar library that is used for
	 * exporting the games from the editor. This path changes depending on
	 * whether you are on an actual release or on a development environment. In
	 * an actual release, this is expected to be in lib/engine.jar while in a
	 * development environment this will point to a local Maven repo
	 * 
	 */
	private String engineLibPath;

	/**
	 * The release version given as three numbers separated by dots (e.g. 2.0.0)
	 * 
	 */
	public String getAppVersion() {
		return appVersion;
	}

	/**
	 * The release version given as three numbers separated by dots (e.g. 2.0.0)
	 * 
	 */
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	/**
	 * The release flavour. For explanations on canary, beta and stable, see the
	 * wiki
	 * 
	 */
	public ReleaseInfo.ReleaseType getReleaseType() {
		return releaseType;
	}

	/**
	 * The release flavour. For explanations on canary, beta and stable, see the
	 * wiki
	 * 
	 */
	public void setReleaseType(ReleaseInfo.ReleaseType releaseType) {
		this.releaseType = releaseType;
	}

	/**
	 * true if this is a dev working copy, not an actual release. If true, the
	 * update system is disabled.
	 * 
	 */
	public boolean isDev() {
		return dev;
	}

	/**
	 * true if this is a dev working copy, not an actual release. If true, the
	 * update system is disabled.
	 * 
	 */
	public void setDev(boolean dev) {
		this.dev = dev;
	}

	/**
	 * The API Key used for tracking
	 * 
	 */
	public String getTracking() {
		return tracking;
	}

	/**
	 * The API Key used for tracking
	 * 
	 */
	public void setTracking(String tracking) {
		this.tracking = tracking;
	}

	/**
	 * URL that stores the update.json file with information about the latest
	 * release available.
	 * 
	 */
	public String getUpdateURL() {
		return updateURL;
	}

	/**
	 * URL that stores the update.json file with information about the latest
	 * release available.
	 * 
	 */
	public void setUpdateURL(String updateURL) {
		this.updateURL = updateURL;
	}

	/**
	 * URL for bug reporting.
	 * 
	 */
	public String getBugReportURL() {
		return bugReportURL;
	}

	/**
	 * URL for bug reporting.
	 * 
	 */
	public void setBugReportURL(String bugReportURL) {
		this.bugReportURL = bugReportURL;
	}

	/**
	 * Simple enum that stores platform-os types. To be used by UpdateInfo and
	 * ReleaseInfo. More info:
	 * https://github.com/e-ucm/ead/wiki/Platform-version-names
	 * 
	 */
	public OS getOs() {
		return os;
	}

	/**
	 * Simple enum that stores platform-os types. To be used by UpdateInfo and
	 * ReleaseInfo. More info:
	 * https://github.com/e-ucm/ead/wiki/Platform-version-names
	 * 
	 */
	public void setOs(OS os) {
		this.os = os;
	}

	/**
	 * The relative or absolute path of the engine.jar library that is used for
	 * exporting the games from the editor. This path changes depending on
	 * whether you are on an actual release or on a development environment. In
	 * an actual release, this is expected to be in lib/engine.jar while in a
	 * development environment this will point to a local Maven repo
	 * 
	 */
	public String getEngineLibPath() {
		return engineLibPath;
	}

	/**
	 * The relative or absolute path of the engine.jar library that is used for
	 * exporting the games from the editor. This path changes depending on
	 * whether you are on an actual release or on a development environment. In
	 * an actual release, this is expected to be in lib/engine.jar while in a
	 * development environment this will point to a local Maven repo
	 * 
	 */
	public void setEngineLibPath(String engineLibPath) {
		this.engineLibPath = engineLibPath;
	}

	@Generated("org.jsonschema2pojo")
	public static enum ReleaseType {

		NIGHTLY("nightly"), BETA("beta"), STABLE("stable");
		private final String value;
		private static Map<String, ReleaseInfo.ReleaseType> constants = new HashMap<String, ReleaseInfo.ReleaseType>();

		static {
			for (ReleaseInfo.ReleaseType c : values()) {
				constants.put(c.value, c);
			}
		}

		private ReleaseType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static ReleaseInfo.ReleaseType fromValue(String value) {
			ReleaseInfo.ReleaseType constant = constants.get(value);
			if (constant == null) {
				throw new IllegalArgumentException(value);
			} else {
				return constant;
			}
		}

	}

}
