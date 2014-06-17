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
package es.eucm.ead.editor.ui.scenes.ribbon;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.scene.ReorderSelection;
import es.eucm.ead.editor.control.actions.model.scene.transform.MirrorSelection;
import es.eucm.ead.editor.control.actions.model.scene.transform.RotateSelection;
import es.eucm.ead.editor.control.actions.model.scene.transform.RotateSelection.Type;
import es.eucm.ead.editor.ui.WidgetsUtils;
import es.eucm.ead.editor.view.widgets.Separator;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public class FormatTab extends LinearLayout {

	public static final float IMAGE_PADDING = 5;

	public static final float DEFAULT_MARGIN = 2.5f;

	private Controller controller;

	private Skin skin;

	private I18N i18N;

	public FormatTab(Controller controller) {
		super(true);
		this.controller = controller;
		skin = controller.getApplicationAssets().getSkin();
		i18N = controller.getApplicationAssets().getI18N();

		defaultWidgetsMargin(DEFAULT_MARGIN);
		add(textButtons());
		add(new Separator(false, skin));
		add(alignButtons());
		add(new Separator(false, skin));
		add(orderButtons());
		add(new Separator(false, skin));
		add(groupButtons());
		add(new Separator(false, skin));
		add(transformButtons());

	}

	private LinearLayout orderButtons() {
		LinearLayout row1 = new LinearLayout(true);
		row1.add(WidgetsUtils.createEnabledIcon(controller, "tofront24x24",
				IMAGE_PADDING, skin, i18N.m("format.tofront"),
				ReorderSelection.class, ReorderSelection.Type.TO_FRONT));
		row1.add(WidgetsUtils.createEnabledIcon(controller, "toback24x24",
				IMAGE_PADDING, skin, i18N.m("format.toback"),
				ReorderSelection.class, ReorderSelection.Type.TO_BACK));

		LinearLayout row2 = new LinearLayout(true);
		row2.add(WidgetsUtils.createEnabledIcon(controller,
				"bringtofront24x24", IMAGE_PADDING, skin,
				i18N.m("format.bringtofront"), ReorderSelection.class,
				ReorderSelection.Type.BRING_TO_FRONT));
		row2.add(WidgetsUtils.createEnabledIcon(controller, "sendtoback24x24",
				IMAGE_PADDING, skin, i18N.m("format.sendtoback"),
				ReorderSelection.class, ReorderSelection.Type.SEND_TO_BACK));

		LinearLayout table = new LinearLayout(false);
		table.add(row1);
		table.add(row2);

		return table;
	}

	private LinearLayout groupButtons() {
		LinearLayout column = new LinearLayout(false);

		column.add(WidgetsUtils.createEnabledIcon(controller, "group24x24",
				IMAGE_PADDING, skin, i18N.m("format.group"), null));
		column.add(WidgetsUtils.createEnabledIcon(controller, "ungroup24x24",
				IMAGE_PADDING, skin, i18N.m("format.ungroup"), null));
		return column;
	}

	private LinearLayout alignButtons() {
		LinearLayout row1 = new LinearLayout(true);

		row1.add(WidgetsUtils.createEnabledIcon(controller, "alignleft24x24",
				IMAGE_PADDING, skin, i18N.m("format.alignleft"), null));
		row1.add(WidgetsUtils.createEnabledIcon(controller, "aligntop24x24",
				IMAGE_PADDING, skin, i18N.m("format.aligntop"), null));
		row1.add(WidgetsUtils.createEnabledIcon(controller, "aligncenter24x24",
				IMAGE_PADDING, skin, i18N.m("format.aligncenter"), null));

		LinearLayout row2 = new LinearLayout(true);

		row2.add(WidgetsUtils.createEnabledIcon(controller, "alignright24x24",
				IMAGE_PADDING, skin, i18N.m("format.alignright"), null));
		row2.add(WidgetsUtils.createEnabledIcon(controller, "alignbottom24x24",
				IMAGE_PADDING, skin, i18N.m("format.alignbottom"), null));
		row2.add(WidgetsUtils.createEnabledIcon(controller, "alignmiddle24x24",
				IMAGE_PADDING, skin, i18N.m("format.alignmiddle"), null));

		LinearLayout table = new LinearLayout(false);
		table.add(row1);
		table.add(row2);

		return table;
	}

	private LinearLayout transformButtons() {
		LinearLayout row1 = new LinearLayout(true);
		row1.add(WidgetsUtils.createEnabledIcon(controller, "mirrorx24x24",
				IMAGE_PADDING, skin, i18N.m("format.mirrorx"),
				MirrorSelection.class, MirrorSelection.Type.VERTICAL));
		row1.add(WidgetsUtils.createEnabledIcon(controller, "rotatecc24x24",
				IMAGE_PADDING, skin, i18N.m("format.rotatecc"),
				RotateSelection.class, Type.COUNTER_CLOCKWISE));

		LinearLayout row2 = new LinearLayout(true);
		row2.add(WidgetsUtils.createEnabledIcon(controller, "mirrory24x24",
				IMAGE_PADDING, skin, i18N.m("format.mirrory"),
				MirrorSelection.class, MirrorSelection.Type.HORIZONTAL));
		row2.add(WidgetsUtils.createEnabledIcon(controller, "rotatecw24x24",
				IMAGE_PADDING, skin, i18N.m("format.rotatecw"),
				RotateSelection.class, Type.CLOCKWISE));

		LinearLayout table = new LinearLayout(false);
		table.add(row1);
		table.add(row2);

		return table;
	}

	private LinearLayout textButtons() {
		LinearLayout row1 = new LinearLayout(true);
		LinearLayout row2 = new LinearLayout(true);
		row2.add(WidgetsUtils.createEnabledIcon(controller, "bold24x24",
				IMAGE_PADDING, skin, i18N.m("format.bold"), null));
		row2.add(WidgetsUtils.createEnabledIcon(controller, "italic24x24",
				IMAGE_PADDING, skin, i18N.m("format.italic"), null));
		row2.add(WidgetsUtils.createEnabledIcon(controller, "lefttext24x24",
				IMAGE_PADDING, skin, i18N.m("format.lefttext"), null));
		row2.add(WidgetsUtils.createEnabledIcon(controller, "centertext24x24",
				IMAGE_PADDING, skin, i18N.m("format.centertext"), null));
		row2.add(WidgetsUtils.createEnabledIcon(controller, "centertext24x24",
				IMAGE_PADDING, skin, i18N.m("format.centertext"), null));
		row2.add(WidgetsUtils.createEnabledIcon(controller, "justifytext24x24",
				IMAGE_PADDING, skin, i18N.m("format.justifytext"), null));

		LinearLayout table = new LinearLayout(false);
		table.add(row1);
		table.add(row2);

		return table;
	}
}
