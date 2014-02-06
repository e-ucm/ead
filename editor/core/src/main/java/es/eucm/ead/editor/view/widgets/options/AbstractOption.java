/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.view.widgets.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.model.events.ModelEvent;
import es.eucm.ead.editor.view.widgets.options.constraints.Constraint;

/**
 * Abstract implementation for {@link Option}s
 * 
 * @param <S>
 *            type of the option element
 */
public abstract class AbstractOption<S> implements Option<S> {

	/**
	 * Label on the component
	 */
	private String label;
	/**
	 * Tool tip text explanation
	 */
	private String toolTipText;

	private static enum UpdateType {
		/**
		 * Indirect update from model
		 */
		Event,
		/**
		 * Update from user input
		 */
		Control,
		/**
		 * Direct update from code
		 */
		Synthetic
	}

	/**
	 * Last valid status
	 */
	protected boolean valid = false;
	/**
	 * Validity-checking class
	 */
	protected Constraint constraint;
	/**
	 * While updating, external updates will be ignored
	 */
	protected boolean isUpdating = false;
	/**
	 * A copy of the old value. Used when creating change events / commands, and
	 * generally updated by the option itself.
	 */
	protected S oldValue;

	/**
	 * The returned component
	 */
	protected Actor widget;

	protected Skin skin;

	/**
	 * Creates an AbstractAction.
	 * 
	 * @param label
	 *            for the option
	 * @param toolTipText
	 *            for the option (can be null) dependency nodes to be considered
	 *            "changed" when this changes
	 */
	public AbstractOption(String label, String toolTipText) {
		this.label = label;
		this.toolTipText = toolTipText;
		if (toolTipText == null || toolTipText.isEmpty()) {
			throw new RuntimeException(
					"ToolTipTexts MUST be provided for all interface elements!");
		}
	}

	/**
	 * Sets the constraint for this option
	 * 
	 * @param constraint
	 *            the constrain definition
	 */
	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}

	/**
	 * Will be called when the model changes. Uses changeConsideredRelevant to
	 * avoid acting on non-changes.
	 * 
	 * @param event
	 */
	@Override
	public void modelChanged(ModelEvent event) {
		if (isUpdating) {
			Gdx.app.debug("AbstractOption", "option " + hashCode()
					+ " isUpdating -- ignores change");
			return;
		}

		Gdx.app.debug("AbstractOption", "option " + hashCode()
				+ " notified of change: " + event);
		/*
		 * if (event.changes(changed)) { uncontestedUpdate(accessor.read(),
		 * UpdateType.Event); } else { Gdx.app.debug("AbstractOption",
		 * "not interested in change " + event); }
		 */
	}

	/**
	 * Retrieves title (used for label).
	 * 
	 * @see Option#getTitle()
	 */
	@Override
	public String getTitle() {
		return label;
	}

	/**
	 * Retrieves tooltip-text (used for tooltips)
	 * 
	 * @see Option#getTooltipText()
	 */
	@Override
	public String getTooltipText() {
		return toolTipText;
	}

	/**
	 * Creates the control, setting the initial value. Subclasses should
	 * register as listeners to any changes in the control, and call update()
	 * when such changes occur.
	 */
	protected abstract Actor createControl();

	/**
	 * Utility method to draw a border around the component
	 * 
	 * @param valid
	 *            if the value of the option is valid
	 */
	protected void decorate(boolean valid) {
		// Do nothing by default
	}

	/**
	 * Creates and initializes the component. Also sets oldValue for the first
	 * time.
	 * 
	 */
	public Actor getControl(Skin skin) {
		this.skin = skin;
		widget = createControl();
		oldValue = getControlValue();
		return widget;
	}

	/**
	 * Reads the value of the control.
	 * 
	 * @return whatever was read from the control
	 */
	public abstract S getControlValue();

	/**
	 * Writes the value of the control.
	 * 
	 * @param newValue
	 *            to write to control
	 */
	protected abstract void setControlValue(S newValue);

	/**
	 * Queried within modelChanged before considering a change to have occurred.
	 * 
	 * @return
	 */
	protected boolean changeConsideredRelevant(S oldValue, S newValue) {
		return false; // ChangeFieldCommand.defaultIsChange(oldValue, newValue);
	}

	/**
	 * Creates a Command that describes a change to the manager. No change
	 * should be described if no change exists.
	 * 
	 * @return
	 */
	protected Command createUpdateCommand() {
		return null; // new ChangeFieldCommand<S>(getControlValue(), accessor,
						// changed);
	}

	/**
	 * Should return whether a value is valid or not. Invalid values will not
	 * generate updates, and will therefore not affect either model or other
	 * views.
	 * 
	 * @return whether it is valid or not; default is "always-true"
	 */
	protected boolean isValid() {
		return constraint.isValid();
	}

	/**
	 * Set validity. Should be called only from within the
	 */
	public void refreshValid() {
		valid = constraint.isValid();
		decorate(valid);
	}

	/**
	 * Should be called when changes to the control are detected. Updates
	 * oldValue after informing all interested parties. Does nothing if new
	 * value is not valid, same as previous, or if an update is already under
	 * way.
	 */
	protected void update() {
		if (isUpdating) {
			return;
		}
		uncontestedUpdate(getControlValue(), UpdateType.Control);
	}

	/**
	 * Called after the control value is updated. Intended to be used by
	 * subclasses; default implementation is to do nothing. Use to chain updates
	 * for complex models - for example, say that field X, Y and Z are related,
	 * so that X+Y+Z must =10. If X changes, all of them will be invalid. When
	 * all become valid again, valueUpdated would read all related fields and
	 * call updateValue on each of them.
	 * 
	 * Only called if the update is valid.
	 * 
	 * @param oldValue
	 */
	public void valueUpdated(S oldValue, S newValue) {
		// by default, do nothing
	}

	/**
	 * Triggers a manual update. This should be indistinguishable from the user
	 * typing in stuff directly (if this were a typing-enabled control)
	 * 
	 * @param nextValue
	 *            value to set the control to, prior to firing an update
	 */
	public void updateValue(S nextValue) {
		if (isUpdating) {
			return;
		}
		uncontestedUpdate(nextValue, UpdateType.Synthetic);
	}

	/**
	 * Synchronizes model values with control values. Called after the control
	 * has changed due to user (type is Control), or due to programmatic
	 * set-to-this (type is Synthetic), or due to changed validity constraints
	 * (type is Event).
	 */
	private void uncontestedUpdate(S nextValue, UpdateType type) {
		if (!isValid()) {
			if (valid) {
				// add an undoable operation to reset to the previous, valid
				// values
				Gdx.app.debug("AbstractOption", "Notifying of empty command");
				isUpdating = true;
				// manager.performCommand(new EmptyCommand(changed));
				isUpdating = false;
			}
			valid = false;
			constraint.validityChanged();
			Gdx.app.debug("AbstractOption", "Update invalid " + nextValue);
			// ignore - non-valid values are not written to the model
		} else if (!changeConsideredRelevant(oldValue, nextValue)) {
			if (!valid) {
				constraint.validityChanged();
			}
			valid = true;
			Gdx.app.debug("AbstractOption", "Update is nop");
			// ignore - not a real update
		} else {
			// process update
			Gdx.app.debug("AbstractOption", "Update to " + nextValue);

			isUpdating = true;
			if (type.equals(UpdateType.Synthetic)
					|| type.equals(UpdateType.Event)) {
				// the user did not set the control -- it needs to be set here
				setControlValue(nextValue);
			}
			if (!type.equals(UpdateType.Event)) {
				// if incoming event, then the model has already been changed
			}
			valueUpdated(oldValue, nextValue);
			oldValue = nextValue;
			isUpdating = false;
			if (!valid) {
				constraint.validityChanged();
			}
			valid = true;
		}
		decorate(valid);
	}
}
