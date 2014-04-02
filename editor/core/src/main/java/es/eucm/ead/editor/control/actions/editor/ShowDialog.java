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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import es.eucm.ead.editor.control.actions.EditorAction;

/**
 * <p>
 * Shows an specified dialog.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> Name of the dialog. This allow to create the appropriate dialog from available at
 *          {@link es.eucm.ead.editor.view.builders.classic.dialogs}</dd>
 * <dd><strong>args[1..n]</strong> <em>??</em> Arguments needed for creating each kind of dialog (used in method {@link }</dd>
 * </dl>
 *
 *
 */
public class ShowDialog extends EditorAction {

    /**
     * This constructor
     */
    public ShowDialog() {
        // Different Arguments Lists accepted (the first arguments includes always the name of the class
        // that builds the dialog):
        // 1) es.eucm.ead.editor.view.builders.classic.dialogs.InfoDialogBuilder
        //     [String(builderName), String(message) ]
        // 2) es.eucm.ead.editor.view.builders.classic.dialogs.ConfirmationDialogBuilder
        //     [String(builderName), String(title), String(message),  ]
        // 3) es.eucm.ead.editor.view.builders.classic.dialogs.NewProjectDialog
        //     [String(builderName)]
		super(true, false, String.class);
	}

	@Override
	public void perform(Object... args) {

       // The first argument (args[0]) is the name
       // that showDialog() waits for name parameter
        String name = (String)args[0];

        // extract the first argument from args
        // due to this argument is not included
        // in the list of arguments that showDialog
        // waits for
        Object[] args1N = new Object[args.length-1];

        for (int i=0; i<args.length-1;i++){
            args1N[i] = args[i+1];
        }

		controller.getViews().showDialog((String) args[0], args1N);
	}

    @Override
    public boolean validate(Object... args) {


        return super.validate(args);
    }
}
