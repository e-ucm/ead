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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;


/**
 * Simple object for storing a serialized version of an action.
 * 
 */
@Generated("org.jsonschema2pojo")
public class SerializedEditorAction {

    /**
     * The canonical name of the editor action's class serialized. (e.g. es.eucm.ead.editor.control.actions.AddScene)
     * 
     */
    private String actionClass;
    /**
     * The list of arguments passed when this action was performed.
     * 
     */
    private List<Object> arguments = new ArrayList<Object>();

    /**
     * The canonical name of the editor action's class serialized. (e.g. es.eucm.ead.editor.control.actions.AddScene)
     * 
     */
    public String getActionClass() {
        return actionClass;
    }

    /**
     * The canonical name of the editor action's class serialized. (e.g. es.eucm.ead.editor.control.actions.AddScene)
     * 
     */
    public void setActionClass(String actionClass) {
        this.actionClass = actionClass;
    }

    /**
     * The list of arguments passed when this action was performed.
     * 
     */
    public List<Object> getArguments() {
        return arguments;
    }

    /**
     * The list of arguments passed when this action was performed.
     * 
     */
    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }

}
