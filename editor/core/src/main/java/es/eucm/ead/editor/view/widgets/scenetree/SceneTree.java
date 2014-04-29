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
package es.eucm.ead.editor.view.widgets.scenetree;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import es.eucm.ead.editor.view.widgets.EditableLabel;
import es.eucm.ead.editor.view.widgets.layouts.HorizontalLayout;
import es.eucm.ead.editor.view.widgets.layouts.HorizontalLayout.Constrains;
import es.eucm.ead.editor.view.widgets.layouts.TopBottomLayout;
import es.eucm.ead.editor.view.widgets.scenetree.SceneTreeListener.SceneTreeEvent;
import es.eucm.ead.editor.view.widgets.scenetree.SceneTreeListener.SceneTreeEvent.Type;

/**
 * Widget to represent a hierarchy of objects. Each object is contained in a
 * {@link Node}. All nodes are layout vertically. When a node is selected, it
 * becomes in the selected node (it receives the keyboard focus), and can be
 * deleted pressing DEL. Also, the selected node can be changed using UP and
 * DOWN keys.
 */
public class SceneTree extends TopBottomLayout {

	private SceneTreeStyle style;

	public SceneTree(Skin skin) {
		this.style = skin.get(SceneTreeStyle.class);
		expandChildrenWidth().align(Align.left);
		this.setBackground(style.background);
	}

	/**
	 * Adds a node to the hierarchy
	 * 
	 * @param name
	 *            a name for the node. It will be displayed in an editable label
	 * @param icons
	 *            some options icons for the node. They'll be placed in the
	 *            right side
	 * @return the created node
	 */
	public Node addNode(String name, Drawable... icons) {
		return addNode(second.size, new Node(this, name, style, icons));
	}

	/**
	 * Adds a node to the hierarchy
	 * 
	 * @param index
	 *            the index to add in the list
	 * @param node
	 *            the node to add
	 * @return the node passed argument
	 */
	public Node addNode(int index, Node node) {
		addTop(index, node);
		node.setLeftMargin(0);
		notifySceneTreeEvent(node, Type.ADDED);
		return node;
	}

	/**
	 * Moves the focus
	 * 
	 * @param move
	 *            movement of the focus (1: go to the next node, -1: go to
	 *            previous)
	 */
	private void moveFocus(int move) {
		Actor focus = getStage().getKeyboardFocus();
		int index = getChildren().indexOf(focus, true);
		if (index != -1) {
			index += move;
			if (index >= 0 && index < getChildren().size) {
				getStage().setKeyboardFocus(getChildren().get(index));
			}
		}
	}

	/**
	 * Notifies an scene tree event event to listeners
	 */
	private void notifySceneTreeEvent(Node node, Type type) {
		SceneTreeEvent event = Pools.obtain(SceneTreeEvent.class);
		event.setNode(node);
		event.setType(type);
		this.fire(event);
		Pools.free(event);
	}

	/**
	 * Style for the scene tree widget
	 */
	public static class SceneTreeStyle {

		/**
		 * Style for the editable label
		 */
		public TextFieldStyle textFieldStyle;

		/**
		 * Style for the button "show children" button
		 */
		public ButtonStyle childrenButtonStyle;

		/**
		 * Tab margin for child node respect its parent
		 */
		public float nodeTabMargin = 15;

		/**
		 * Background for the scene tree
		 */
		public Drawable background;

		/**
		 * Background for the node with focus
		 */
		public Drawable focusNodeBackground;
	}

	/**
	 * A node inside the scene tree
	 */
	public static class Node extends TopBottomLayout {

		private SceneTree parentTree;

		private SceneTreeStyle style;

		private HorizontalLayout nodeContent;

		private TopBottomLayout childrenContent;

		private EditableLabel nameField;

		private Button showGroup;

		private float leftMargin;

		private boolean childrenVisible;

		private Constrains labelConstrain;

		public Node(final SceneTree parentTree, String name,
				SceneTreeStyle sceneTreeStyle, Drawable... icons) {
			this.parentTree = parentTree;
			this.style = sceneTreeStyle;
			expandChildrenWidth().align(Align.left).computeInvisibles(false);
			setRequestKeyboardFocus(true);

			nodeContent = new HorizontalLayout();
			nodeContent.add(new Button(sceneTreeStyle.childrenButtonStyle));
			nodeContent.add(new Button(sceneTreeStyle.childrenButtonStyle));
			addTop(nodeContent);
			addTop(childrenContent = new TopBottomLayout());
			childrenContent.expandChildrenWidth().align(Align.left);

			// Button to show/hide children in the tree
			showGroup = new Button(sceneTreeStyle.childrenButtonStyle);
			showGroup.setVisible(false);
			showGroup.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					toggleChildrenVisible();
				}
			});

			// Editable label with the name of the node
			nameField = new EditableLabel(name, sceneTreeStyle.textFieldStyle);
			nameField.addListener(new InputListener() {
				@Override
				public boolean keyTyped(InputEvent event, char character) {
					parentTree.notifySceneTreeEvent(Node.this, Type.UPDATED);
					return true;
				}
			});
			nodeContent.add(showGroup);
			labelConstrain = nodeContent.add(nameField).expand();

			// Add icons
			for (Drawable icon : icons) {
				nodeContent.add(new Image(icon)).right();
			}

			// Keyboard input
			addListener(new InputListener() {
				@Override
				public boolean keyDown(InputEvent event, int keycode) {
					if (!event.isHandled()) {
						switch (keycode) {
						case Keys.SPACE:
							toggleChildrenVisible();
							return true;
						case Keys.DEL:
						case Keys.FORWARD_DEL:
							moveFocus(1, true);
							remove();
							return true;
						case Keys.UP:
							moveFocus(-1, false);
							return true;
						case Keys.DOWN:
							moveFocus(1, false);
							return true;
						}
					}
					return false;
				}
			});

			// Focus handling
			addListener(new FocusListener() {
				@Override
				public void keyboardFocusChanged(FocusEvent event, Actor actor,
						boolean focused) {
					nodeContent
							.setBackground(focused && actor == Node.this ? style.focusNodeBackground
									: null);
				}
			});
		}

		/**
		 * Sets the tab margin for the node
		 */
		private void setLeftMargin(float leftMargin) {
			this.leftMargin = leftMargin;
			labelConstrain.setLeftMargin(leftMargin);
		}

		/**
		 * @return the content of the editable label of the node
		 */
		public String getNodeName() {
			return nameField.getText();
		}

		/**
		 * Adds a node to the hierarchy
		 * 
		 * @param name
		 *            a name for the node. It will be displayed in an editable
		 *            label
		 * @param icons
		 *            some options icons for the node. They'll be placed in the
		 *            right side
		 * @return the created node
		 */
		public Node addNode(String name, Drawable... icons) {
			return addNode(0, new Node(parentTree, name, style, icons));
		}

		/**
		 * Adds a node to the hierarchy
		 * 
		 * @param index
		 *            the index to add in the list
		 * @param node
		 *            the node to add
		 * @return the node passed argument
		 */
		public Node addNode(int index, Node node) {
			childrenContent.addTop(index, node);
			node.setLeftMargin(this.leftMargin + style.nodeTabMargin);
			setChildrenVisible(true);
			parentTree.notifySceneTreeEvent(node, Type.ADDED);
			return node;
		}

		/**
		 * Toggles children visiblity
		 */
		public void toggleChildrenVisible() {
			setChildrenVisible(!childrenVisible);
		}

		/**
		 * Shows/hides node children
		 * 
		 * @param childrenVisible
		 *            if children must be shown
		 */
		public void setChildrenVisible(boolean childrenVisible) {
			this.childrenVisible = childrenVisible;
			showGroup.setChecked(childrenVisible);
			childrenContent.setVisible(childrenVisible);
			invalidateHierarchy();
		}

		private void moveFocus(int move, boolean ignoreChildren) {
			// Go to children
			if (!ignoreChildren && childrenVisible && move > 0) {
				getStage().setKeyboardFocus(
						childrenContent.getChildren().get(0));
			} else {
				Group container = getParent();
				Group node = container.getParent();
				Array<Actor> children = container.getChildren();
				int index = children.indexOf(this, true);
				if (index != -1) {
					index += move;
					if (index >= 0 && index < children.size) {
						Node nextNode = (Node) children.get(index);
						nextNode.requestFocus(move < 0);
					} else if (node instanceof Node) {
						if (index < 0) {
							((Node) node).requestFocus(false);
						} else {
							((Node) node).moveFocus(move, true);
						}
					} else if (node instanceof SceneTree) {
						((SceneTree) node).moveFocus(move);
					}
				}
			}
		}

		/**
		 * Request the focus for the node
		 * 
		 * @param goToDeepestChild
		 *            if the focus must be passed to deepest visible child in
		 *            the node
		 */
		private void requestFocus(boolean goToDeepestChild) {
			if (childrenVisible && goToDeepestChild) {
				Node node = (Node) childrenContent.getChildren().get(
						childrenContent.getChildren().size - 1);
				node.requestFocus(true);
			} else {
				getStage().setKeyboardFocus(this);
			}
		}

		@Override
		public boolean remove() {
			parentTree.notifySceneTreeEvent(this, Type.REMOVED);
			return super.remove();
		}

		@Override
		public void layout() {
			boolean hasChildren = childrenContent.getChildren().size > 0;
			showGroup.setVisible(hasChildren);
			setChildrenVisible(childrenVisible && hasChildren);
			super.layout();
		}

		@Override
		public float getPrefWidth() {
			return isVisible() ? super.getPrefWidth() : 0;
		}

		@Override
		public float getPrefHeight() {
			return isVisible() ? super.getPrefHeight() : 0;
		}

	}

}
