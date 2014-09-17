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
package es.eucm.ead.editor.view.widgets.editionview.prefabs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel.Position;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithFadePanel;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public abstract class PrefabPanel extends IconWithFadePanel implements
		SelectionListener {

	private static final float SEPARATION = 5, PAD_TITLE = 100, PAD = 20;

	protected Skin skin;
	protected I18N i18n;
	protected Controller controller;

	protected Selection selection;

	private PrefabPanelStyle style;

	private boolean used;

	private static ClickListener trashListener = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			Actor listenerActor = event.getListenerActor();
			PrefabPanel panel = (PrefabPanel) listenerActor.getUserObject();
			panel.trashClicked();
		}
	};

	public PrefabPanel(String icon, float iconPad, float size,
			String panelName, Controller controller, Actor touchable) {
		super(icon, iconPad, SEPARATION, size, controller
				.getApplicationAssets().getSkin(), Position.RIGHT);

		this.controller = controller;
		this.skin = controller.getApplicationAssets().getSkin();
		this.i18n = controller.getApplicationAssets().getI18N();

		this.used = false;
		setStyle(skin.get(PrefabPanelStyle.class));

		selection = controller.getModel().getSelection();

		panel.addTouchableActor(touchable);

		IconButton trash = new IconButton("recycle", 0, skin);
		trash.addListener(trashListener);
		trash.setUserObject(this);

		LinearLayout top = new LinearLayout(true);

		top.add(new Label(i18n.m(panelName), skin)).expand(true, true)
				.margin(PAD, PAD, PAD_TITLE, PAD);
		top.add(trash).margin(PAD);
		panel.add(top);
		panel.row().padBottom(PAD);

		this.setDisabled(true);

		controller.getModel().addSelectionListener(this);
	}

	protected abstract void trashClicked();

	@Override
	public boolean listenToContext(String contextId) {
		return true;
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		setUsed(false);
		if (selection.get(Selection.SCENE_ELEMENT).length == 1) {
			this.setDisabled(false);
			selectionChanged();
		} else {
			hidePanel();
			this.setDisabled(true);
		}
	}

	protected abstract void selectionChanged();

	@Override
	protected void showPanel() {
		actualizePanel();
		super.showPanel();
	}

	protected abstract void actualizePanel();

	public void setStyle(PrefabPanelStyle style) {
		this.style = style;
		super.setStyle(style);
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (used) {
			style.inUse.draw(batch, getX(), getY(), getWidth(), getHeight());
		}
	}

	/**
	 * The style for {@link PrefabPanel} See also {@link IconButtonStyle}
	 */
	public static class PrefabPanelStyle extends IconButtonStyle {

		/**
		 * {@link PrefabPanelStyle#inUse}.
		 */
		public Drawable inUse;

		/**
		 * Default constructor used for reflection
		 */
		public PrefabPanelStyle() {
		}

		public PrefabPanelStyle(Drawable inUse) {
			this.inUse = inUse;
		}

		public PrefabPanelStyle(PrefabPanelStyle prefabPanelStyle) {
			super(prefabPanelStyle);
			this.inUse = prefabPanelStyle.inUse;
		}

	}
}
