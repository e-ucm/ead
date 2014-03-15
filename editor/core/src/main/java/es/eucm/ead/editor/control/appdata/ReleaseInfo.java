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
 * Simple object for storing information about the current release of the ead2 editor. This release.json file should be generated automatically on each release.
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
     * The release flavour: dev=development (does not ever ask to update). For explanations on canary, beta and stable, see the wiki
     * 
     */
    private ReleaseInfo.ReleaseType releaseType = ReleaseInfo.ReleaseType.fromValue("dev");
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
     * The release flavour: dev=development (does not ever ask to update). For explanations on canary, beta and stable, see the wiki
     * 
     */
    public ReleaseInfo.ReleaseType getReleaseType() {
        return releaseType;
    }

    /**
     * The release flavour: dev=development (does not ever ask to update). For explanations on canary, beta and stable, see the wiki
     * 
     */
    public void setReleaseType(ReleaseInfo.ReleaseType releaseType) {
        this.releaseType = releaseType;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Generated("org.jsonschema2pojo")
    public static enum ReleaseType {

        DEV("dev"),
        CANARY("canary"),
        BETA("beta"),
        STABLE("stable");
        private final String value;
        private static Map<String, ReleaseInfo.ReleaseType> constants = new HashMap<String, ReleaseInfo.ReleaseType>();

        static {
            for (ReleaseInfo.ReleaseType c: ReleaseInfo.ReleaseType.values()) {
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
