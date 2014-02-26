package es.eucm.ead.editor.view.builders.mockup.gallery;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.engine.I18N;

public class BaseGallery implements ViewBuilder{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Make the Gallery view with three WidgetsGroup that return the 
	 * bottom, center and topWidget functions.
	 * If any WidgetGroup is null, this is not added.
	 * */
	@Override
	public Actor build(Controller controller) {
		I18N i18n = controller.getEditorAssets().getI18N();
		Skin skin = controller.getEditorAssets().getSkin();
		final Vector2 viewport = controller.getPlatform().getSize();

		Table window = new Table();
		window.setFillParent(true);

		Navigation nav = new Navigation(viewport, controller, skin);

		WidgetGroup top = topWidget(viewport, i18n, skin, controller);
		WidgetGroup center = centerWidget(viewport, i18n, skin, controller);
		WidgetGroup bottom = bottomWidget(viewport, i18n, skin, controller);
		
		if(top!=null) {
			window.add(top).expandX().fill();
		}
		if(center!=null) {
			window.row();
			window.add(center).center().fill().expand();
		}
		if(bottom!=null) {
			window.row();
			window.add(bottom).expandX().fill();
		}
		window.addActor(nav);
		window.debug();
		return window;
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
		topBar.add("").fill().expand().center();
		topBar.add(searchTf).right().fill().expand();
		topBar.add(order).right().fill();

		return topBar;
	}
	
	protected WidgetGroup centerWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		return null;
	}

	protected WidgetGroup bottomWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		return null;
	}

	@Override
	public void initialize(Controller controller) {

	}

	@Override
	public void release(Controller controller) {

	}

}
