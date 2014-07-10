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
package es.eucm.ead.editor.widgets;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import es.eucm.ead.editor.editorui.EditorUITest;
import es.eucm.ead.editor.view.widgets.EditTweensBar;
import es.eucm.ead.editor.view.widgets.IconTextButton;
import es.eucm.ead.editor.view.widgets.IconTextButton.Position;
import es.eucm.ead.editor.view.widgets.Separator;
import es.eucm.ead.editor.view.widgets.layouts.TrackLayout;

public class EditTweensBarTest extends EditorUITest {

	public static void main(String args[]) {
		new LwjglApplication(new EditTweensBarTest(), "Test for EditTweensBar",
				800, 400);
	}

	@Override
	protected void builUI(Group root) {
		controller.getApplicationAssets().loadSkin("skins/light/skin");
		Skin skin = controller.getApplicationAssets().getSkin();

		IconTextButton button1 = new IconTextButton("1", skin,
				skin.getDrawable("actor48x48"), Position.BOTTOM);
		IconTextButton button2 = new IconTextButton("2", skin,
				skin.getDrawable("controls48x48"), Position.BOTTOM);
		IconTextButton button3 = new IconTextButton("3", skin,
				skin.getDrawable("file48x48"), Position.BOTTOM);
		IconTextButton button4 = new IconTextButton("4", skin,
				skin.getDrawable("folder48x48"), Position.BOTTOM);
		IconTextButton button5 = new IconTextButton("5", skin,
				skin.getDrawable("image48x48"), Position.BOTTOM);
		IconTextButton button6 = new IconTextButton("6", skin,
				skin.getDrawable("keyboard48x48"), Position.BOTTOM);
		IconTextButton button7 = new IconTextButton("7", skin,
				skin.getDrawable("newscene48x48"), Position.BOTTOM);
		IconTextButton button8 = new IconTextButton("8", skin,
				skin.getDrawable("actor48x48"), Position.BOTTOM);
		IconTextButton button9 = new IconTextButton("9", skin,
				skin.getDrawable("controls48x48"), Position.BOTTOM);
		IconTextButton button10 = new IconTextButton("10", skin,
				skin.getDrawable("file48x48"), Position.BOTTOM);
		IconTextButton button11 = new IconTextButton("11", skin,
				skin.getDrawable("folder48x48"), Position.BOTTOM);
		IconTextButton button12 = new IconTextButton("12", skin,
				skin.getDrawable("image48x48"), Position.BOTTOM);
		IconTextButton button13 = new IconTextButton("13", skin,
				skin.getDrawable("keyboard48x48"), Position.BOTTOM);
		IconTextButton button14 = new IconTextButton("14", skin,
				skin.getDrawable("newscene48x48"), Position.BOTTOM);

		Table t = new Table(skin);

		Table tracks = new Table(skin);
		ScrollPane sp = new ScrollPane(tracks);

		DragAndDrop tracksDragNDrop = new DragAndDrop();

		EditTweensBar bar = new EditTweensBar(skin.getDrawable("blank"),
				skin.getDrawable("bg-dark"), tracksDragNDrop, controller);

		bar.addInstant(button1);
		bar.addInstant(button2);
		bar.addInstant(button3);
		bar.addGradual(button4);
		bar.addGradual(button5);
		bar.addGradual(button6);
		bar.addGradual(button7);
		bar.addGradual(button8);
		bar.addGradual(button9);
		bar.addGradual(button10);
		bar.addGradual(button11);
		bar.addGradual(button12);
		bar.addGradual(button13);
		bar.addGradual(button14);

		TrackLayout t1 = new TrackLayout(skin.getDrawable("blank"),
				tracksDragNDrop);
		TrackLayout t2 = new TrackLayout(skin.getDrawable("blank"),
				tracksDragNDrop);
		TrackLayout t3 = new TrackLayout(skin.getDrawable("blank"),
				tracksDragNDrop);

		t1.setInScroll(sp);
		tracks.add(new Separator(true, skin));
		tracks.row();
		t2.setInScroll(sp);
		tracks.add(new Separator(true, skin));
		tracks.row();
		t3.setInScroll(sp);

		t.add(bar).fill().expandX().top();
		t.row();
		t.add(new Separator(true, skin));
		t.row();
		t.add(sp).expand().fill();

		t.setFillParent(true);

		root.addActor(t);
	}

}
