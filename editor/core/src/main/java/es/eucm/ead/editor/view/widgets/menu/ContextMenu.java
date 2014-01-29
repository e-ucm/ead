package es.eucm.ead.editor.view.widgets.menu;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;

public class ContextMenu extends WidgetGroup {

	private Controller controller;

	private Skin skin;

	private Array<ContextMenuItem> items;

	public ContextMenu(Controller controller, Skin skin) {
		this.controller = controller;
		this.skin = skin;
		items = new Array<ContextMenuItem>();
	}

	public ContextMenuItem item(String label) {
		ContextMenuItem contextMenuItem = new ContextMenuItem(label, skin);
		addItem(contextMenuItem);
		return contextMenuItem;
	}

	public ContextMenuItem item(String label, String actionName, Object... args) {
		ContextMenuItem item = item(label);
		item.addListener(new ActionOnClickListener(controller, actionName, args));
		return item;
	}

	private void addItem(ContextMenuItem item) {
		addActor(item);
		items.add(item);
	}

	@Override
	public float getPrefWidth() {
		float prefWidth = 0;
		for (ContextMenuItem item : items) {
			prefWidth = Math.max(item.getPrefWidth(), prefWidth);
		}
		return prefWidth;
	}

	@Override
	public float getPrefHeight() {
		float prefHeight = 0;
		for (ContextMenuItem item : items) {
			prefHeight += item.getPrefHeight();
		}
		return prefHeight;
	}

	@Override
	public void layout() {
		float y = getHeight();
		for (ContextMenuItem item : items) {
			float height = item.getPrefHeight();
			item.setBounds(0, y, getWidth(), height);
			y -= height;
		}
	}

}
