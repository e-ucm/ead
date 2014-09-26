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
package es.eucm.ead.editor.view.widgets.groupeditor;

/**
 * Configuration parameters for {@link GroupEditor}, {@link Handles},
 * {@link Modifier}...
 * 
 */
public class GroupEditorConfiguration {

	/* Default values */
	private static final int HANDLE_SQUARE_SIZE = 6;

	private static final int HANDLE_CIRCLE_SIZE = 4;

	private static final int ROTATION_HANDLE_OFFSET = 20;

	private static final boolean MULTIPLE_SELECTION = true;

	private static final boolean NESTED_GROUP_EDITION = true;

	public int handleSquareSize = HANDLE_SQUARE_SIZE;
	public int handleCircleSize = HANDLE_CIRCLE_SIZE;
	public int rotationHandleOffset = ROTATION_HANDLE_OFFSET;
	public boolean multipleSelection = MULTIPLE_SELECTION;
	public boolean nestedGroupEdition = NESTED_GROUP_EDITION;
	public boolean drawHandles = true;

	/**
	 * Changes the size of the square handles around the grouper. Default value
	 * is {@value #HANDLE_SQUARE_SIZE}.
	 * 
	 * @param handleSquareSize
	 */
	public void setHandleSquareSize(int handleSquareSize) {
		this.handleSquareSize = handleSquareSize;
	}

	/**
	 * Changes the size of the circle handle used to rotate. Default value is
	 * {@value #HANDLE_CIRCLE_SIZE}.
	 * 
	 * @param handleCircleSize
	 */
	public void setHandleCircleSize(int handleCircleSize) {
		this.handleCircleSize = handleCircleSize;
	}

	/**
	 * Changes the offset of the circle rotation handle. Default value is
	 * {@value #ROTATION_HANDLE_OFFSET}.
	 * 
	 * @param rotationHandleOffset
	 */
	public void setRotationHandleOffset(int rotationHandleOffset) {
		this.rotationHandleOffset = rotationHandleOffset;
	}

	/**
	 * Whether the {@link GroupEditorDragListener} should allow multiple
	 * selection or not.
	 * 
	 * @param multipleSelection
	 */
	public void setMultipleSelection(boolean multipleSelection) {
		this.multipleSelection = multipleSelection;
	}

	/**
	 * Whether the {@link GroupEditorDragListener} should allow nested group
	 * edition by double clicking or not.
	 * 
	 * @param nestedGroupEdition
	 */
	public void setNestedGroupEdition(boolean nestedGroupEdition) {
		this.nestedGroupEdition = nestedGroupEdition;
	}
}
