package es.eucm.ead.editor.view.builders.mockup.gallery;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ChangeView;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.mockup.camera.Picture;
import es.eucm.ead.editor.view.builders.mockup.camera.Video;
import es.eucm.ead.editor.view.widgets.GridLayout;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.engine.I18N;

public class Gallery implements ViewBuilder {

	public static final String NAME = "mockup_gallery";

	private static final String IC_PHOTOCAMERA = "ic_photocamera",
			IC_VIDEOCAMERA = "ic_videocamera";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .25F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .2F;
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {
		I18N i18n = controller.getEditorAssets().getI18N();
		Skin skin = controller.getEditorAssets().getSkin();
		
		GridLayout galleryTable = new GridLayout();
		galleryTable.pad(2);
		galleryTable.setFillParent(true);
		
		Table tIn = new Table().debug();
		tIn.pad(2);
		tIn.setFillParent(true);
		
		//FIXME (Testing GridLayout)
		for(int i=10; i<40; i++){
			galleryTable.addActor(new TextButton("proyecto"+i, skin));
			tIn.add(new TextButton("proyecto "+i, skin));
			tIn.row();
		}
		//END FIXME
		
		Table window = new Table();
		window.setFillParent(true);

		ScrollPane sp = new ScrollPane(galleryTable);
		sp.setScrollingDisabled(true, false);
		sp.layout();
		
		Navigation nav = new Navigation(controller, skin);
	
		ToolBar topBar = topToolbar(i18n, skin, nav);
		ToolBar botBar = bottomToolbar(i18n, skin, controller);
		
		window.add(topBar).expandX().fill();
		window.row();
		window.add(sp).center().fill().expand();
		window.row();
		window.add(botBar).expandX().fill();
		window.addActor(nav.getPanel());
		window.debug();
		return window;
	}
	
	private ToolBar topToolbar(I18N i18n, Skin skin, Navigation nav){
		
		String search = i18n.m("general.gallery.search");
		TextField searchTf = new TextField("", skin);
		searchTf.setMessageText(search);
		searchTf.setMaxLength(search.length());
		String[] orders = new String[] { i18n.m("general.gallery.sort"), i18n.m("general.gallery.nameAZ"),
				i18n.m("general.gallery.nameZA"), i18n.m("general.gallery.more"), i18n.m("general.gallery.less") };
																
		SelectBox order = new SelectBox(orders, skin);
		
		ToolBar topBar = new ToolBar(skin);
		topBar.add(nav.getButton()).expandY().left();
		topBar.add("").fill().expand().center();
		topBar.add(searchTf).right().fill().expand();
		topBar.add(order).right().fill();
		
		return topBar;
	}
	
	private ToolBar bottomToolbar(I18N i18n, Skin skin, Controller controller){
		ToolBar botBar = new ToolBar(skin);
		
		BottomProjectMenuButton pictureButton = new BottomProjectMenuButton(
				i18n.m("general.mockup.photo"), skin, IC_PHOTOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				controller, ChangeView.NAME, Picture.NAME);
		BottomProjectMenuButton videoButton = new BottomProjectMenuButton(
				i18n.m("general.mockup.video"), skin, IC_VIDEOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				controller, ChangeView.NAME, Video.NAME);
		
		botBar.add(pictureButton).left();
		botBar.add("").expandX();
		botBar.add(videoButton).right();
		
		return botBar;
	}	

	@Override
	public void initialize(Controller controller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void release(Controller controller) {
		// TODO Auto-generated method stub

	}

}
