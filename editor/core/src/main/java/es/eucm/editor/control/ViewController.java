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
package es.eucm.editor.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.esotericsoftware.tablelayout.Cell;
import es.eucm.editor.view.ViewFactory;
import es.eucm.editor.view.dialogs.DialogListener;
import es.eucm.editor.view.dialogs.MessageDialog;
import es.eucm.editor.view.dialogs.OptionsDialog;
import es.eucm.editor.view.options.OptionsPanel;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.I18N;

import java.io.IOException;

/**
 * Class controlling all the views in the editor
 */
public class ViewController {

	private MessageDialog messageDialog;

	private OptionsDialog optionsDialog;

	private XmlReader xmlReader;

	private Assets assets;

	private Group root;

	private I18N i18n;

	private String currentView;

	private String mode;

	private ViewFactory viewFactory;

	public ViewController(Assets assets, Group root, I18N i18n) {
		this.assets = assets;
		this.root = root;
		this.i18n = i18n;
		// messageDialog = new MessageDialog(Editor.assets.getSkin());
		// optionsDialog = new OptionsDialog(Editor.assets.getSkin());
		xmlReader = new XmlReader();
		mode = "";
		viewFactory = new ViewFactory();
	}

	/**
	 * Shows an info dialog. If there's already one showing, the message is
	 * added as a new line in the dialog
	 * 
	 * @param message
	 *            the i18n key of the message
	 * @param dialogListener
	 *            will listen to the result. Can be {@code null}
	 */
	public void showInfo(String message, DialogListener dialogListener) {
		messageDialog.showMessage(MessageDialog.info, message, dialogListener);
	}

	/**
	 * Shows a warning dialog. If there's already one showing, the message is
	 * added as a new line in the dialog
	 * 
	 * @param message
	 *            the i18n key of the message
	 * @param dialogListener
	 *            will listen to the result. Can be {@code null}
	 */
	public void showWarning(String message, DialogListener dialogListener) {
		messageDialog.showMessage(MessageDialog.warning, message,
				dialogListener);
	}

	/**
	 * Shows an error dialog. If there's already one showing, the message is
	 * added as a new line in the dialog
	 * 
	 * @param message
	 *            the i18n key of the message
	 * @param dialogListener
	 *            will listen to the result. Can be {@code null}
	 */
	public void showError(String message, DialogListener dialogListener) {
		messageDialog.showMessage(MessageDialog.error, message, dialogListener);
	}

	/**
	 * Shows in a modal dialog the given options panel
	 * 
	 * @param optionsPanel
	 *            the options panel to show
	 * @param dialogListener
	 *            a listener that will transmit the button pressed (represented
	 *            by one of the keys passed in buttonsKey) in the dialog
	 * @param buttonsKey
	 *            the i18n keys for the buttons of the dialog. Each key creates
	 *            a button that closes the dialog
	 */
	public void showOptionsDialog(OptionsPanel optionsPanel,
			DialogListener dialogListener, String... buttonsKey) {
		optionsDialog.show(optionsPanel, dialogListener, buttonsKey);
	}

	public void setViewMode(String viewMode) {
		mode = viewMode + "/";
		assets.setSkin(viewMode);
		showView(currentView);
	}

	public void showView(String viewName) {
		try {
			FileHandle viewFile = assets.resolve("views/" + mode + viewName
					+ ".xml");
			if (!viewFile.exists()) {
				viewFile = assets.resolve("views/" + viewName + ".xml");
			}
			if (viewFile.exists()) {
				currentView = viewName;
				Element xml = xmlReader.parse(viewFile);
				Actor view = buildView(xml);
				root.clearChildren();
				root.addActor(view);
			} else {
				Gdx.app.error("ViewManager", "View " + viewName
						+ " doesn't exist.");
			}
		} catch (IOException e) {
			Gdx.app.error("ViewManager", "Error reading view " + viewName, e);
		}
	}

	private Actor buildView(Element element) {
		Skin skin = assets.getSkin();
		return viewFactory.build(element, skin);
	}

	private void buildTable(Table table, Element root) {
		for (int i = 0; i < root.getChildCount(); i++) {
			Element element = root.getChild(i);
			if (element.getName().equals("row")) {
				Cell cell = table.row().expandX().colspan(root.getChildCount());
				try {
					if ("expandy".equals(element.getAttribute("class"))) {
						cell.expandY();
					}
				} catch (GdxRuntimeException e) {

				}
				for (int j = 0; j < element.getChildCount(); j++) {
					table.add(buildView(element.getChild(j)));
				}
			} else {
				table.add(buildView(element));
			}
		}
	}
}
