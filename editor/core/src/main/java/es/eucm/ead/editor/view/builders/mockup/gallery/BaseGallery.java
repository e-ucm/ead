package es.eucm.ead.editor.view.builders.mockup.gallery;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.panels.GalleryGrid;
import es.eucm.ead.engine.I18N;

public abstract class BaseGallery implements ViewBuilder {

	private WidgetGroup top;
	private WidgetGroup center;
	private WidgetGroup bottom;
	private Table rootWindow;
	private GalleryGrid<Actor> galleryTable;

	@Override
	public String getName() { return null; }

	/**
	 * Make the Gallery view with three WidgetsGroup that return the bottom,
	 * center and topWidget functions. If any WidgetGroup is null, this is not
	 * added.
	 * */
	@Override
	public Actor build(Controller controller) {
		I18N i18n = controller.getEditorAssets().getI18N();
		Skin skin = controller.getEditorAssets().getSkin();
		final Vector2 viewport = controller.getPlatform().getSize();

		rootWindow = new Table().debug();
		rootWindow.setFillParent(true);

		top = topWidget(viewport, i18n, skin, controller);
		center = centerWidget(viewport, i18n, skin, controller);
		bottom = bottomWidget(viewport, i18n, skin, controller);

		if (top != null) {
			rootWindow.add(top).expandX().fill();
		}
		if (center != null) {
			rootWindow.row();
			rootWindow.add(center).center().fill().expand();
		}
		if (bottom != null) {
			rootWindow.row();
			rootWindow.add(bottom).expandX().fill();
		}
		galleryTable.setActorsToHide(top, bottom);
		return rootWindow;
	}

	protected WidgetGroup topWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {

		String search = i18n.m("general.gallery.search");
		TextField searchTf = new TextField("", skin);
		searchTf.setMessageText(search);
		searchTf.setMaxLength(search.length());
		String[] orders = new String[] { i18n.m("general.gallery.sort"),
				i18n.m("general.gallery.nameAZ"),
				i18n.m("general.gallery.nameZA"),
				i18n.m("general.gallery.more"), i18n.m("general.gallery.less") };

		SelectBox<String> order = new SelectBox<String>(skin);
		order.setItems(orders);

		ToolBar topBar = new ToolBar(viewport, skin);
		topBar.debug();
		topBar.add(topLeftButton(skin)).left().expandX();
		topBar.right();
		topBar.add(searchTf, order);

		return topBar;
	}
	
	protected abstract Button topLeftButton(Skin skin);

	protected WidgetGroup centerWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {

		Table centerWidget = new Table().debug();
		
		galleryTable = new GalleryGrid<Actor>(skin, 8, 4, viewport, rootWindow);

		addElementsToTheGallery(galleryTable, viewport, i18n, skin);

		ScrollPane sp = new ScrollPane(galleryTable);
		sp.setScrollingDisabled(true, false);
		
		centerWidget.add(sp).expand().fill();

		return centerWidget;
	}

	protected abstract void addElementsToTheGallery(
			GalleryGrid<Actor> galleryTable, Vector2 viewport, I18N i18n, Skin skin);

	protected WidgetGroup bottomWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		return null;
	}

	@Override
	public void initialize(Controller controller) { }

	@Override
	public void release(Controller controller) { }

}
