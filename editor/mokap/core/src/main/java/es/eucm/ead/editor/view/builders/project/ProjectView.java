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
package es.eucm.ead.editor.view.builders.project;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.CloseProject;
import es.eucm.ead.editor.control.actions.editor.Rename;
import es.eucm.ead.editor.control.actions.editor.ShareProject;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.PlayView;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schemax.FieldName;

/**
 * Project view. A list with the scenes of the project
 */
public class ProjectView implements ViewBuilder, BackListener, FieldListener {

	private Controller controller;

	private LinearLayout view;

	private TextButton title;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;
		Skin skin = controller.getApplicationAssets().getSkin();
		view = new LinearLayout(false);
		view.background(skin.getDrawable(SkinConstants.DRAWABLE_GRAY_100));
		view.add(buildToolbar(skin)).expandX();
		view.add(new ProjectScenesGallery(2.25f, 3, controller))
				.expand(true, true).top();
	}

	@Override
	public Actor getView(Object... args) {
		controller.getCommands().pushStack();
		controller.getModel().addFieldListener(
				Q.getComponent(controller.getModel().getGame(),
						Documentation.class), this);
		readTitle();
		return view;
	}

	@Override
	public void release(Controller controller) {
		controller.getModel().removeListener(
				Q.getComponent(controller.getModel().getGame(),
						Documentation.class), this);
		controller.getCommands().popStack(false);
	}

	private Actor buildToolbar(Skin skin) {
		MultiWidget toolbar = new MultiWidget(skin, SkinConstants.STYLE_TOOLBAR);

		LinearLayout project = new LinearLayout(true);
		project.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_GO, null,
				CloseProject.class));
		project.add(
				new Container<TextButton>(title = WidgetBuilder.textButton("",
						SkinConstants.STYLE_TOOLBAR)).width(0).fillX())
				.expandX().marginLeft(WidgetBuilder.dpToPixels(8));

		title.getLabel().setAlignment(Align.left);
		Cell cell = title.getLabelCell();
		LinearLayout titleCell = new LinearLayout(true);
		titleCell.add(new Image(skin, SkinConstants.IC_EDIT)).marginRight(
				WidgetBuilder.dpToPixels(8));
		titleCell.add(title.getLabel());
		cell.setActor(titleCell);
		title.padLeft(WidgetBuilder.dpToPixels(8));
		WidgetBuilder.actionOnClick(title, Rename.class, Selection.MOKAP);

		project.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_SHARE, null,
				ShareProject.class));
		project.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_PLAY, null,
				ChangeView.class, PlayView.class));

		toolbar.addWidgets(project);
		return toolbar;
	}

	@Override
	public boolean onBackPressed() {
		controller.action(CloseProject.class);
		return true;
	}

	private void readTitle() {
		title.setText(Q.getTitle(controller.getModel().getGame(), controller
				.getApplicationAssets().getI18N().m("untitled")));
	}

	@Override
	public boolean listenToField(String fieldName) {
		return FieldName.NAME.equals(fieldName);
	}

	@Override
	public void modelChanged(FieldEvent event) {
		readTitle();
	}
}
