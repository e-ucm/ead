package es.eucm.ead.editor.view.builders.mockup.gallery;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.engine.I18N;

public abstract class BaseGalleryWithNavigation extends BaseGallery {

	private Navigation navigation;
	private HiddenPanel filterPanel;

	@Override
	public Actor build(Controller controller) {
		Skin skin = controller.getEditorAssets().getSkin();
		final Vector2 viewport = controller.getPlatform().getSize();

		navigation = new Navigation(viewport, controller, skin);
		return super.build(controller);
	}

	@Override
	protected Button topLeftButton(Skin skin) {
		return navigation.getButton();
	}

	@Override
	protected WidgetGroup topWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		Table top = (Table) super.topWidget(viewport, i18n, skin, controller);

		Button filterButton = new TextButton(i18n.m("general.gallery.filter"),
				skin);
		filterButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (BaseGalleryWithNavigation.this.filterPanel.isVisible()) {
					BaseGalleryWithNavigation.this.filterPanel.hide();
				} else {
					BaseGalleryWithNavigation.this.filterPanel.show();
				}
				return false;
			}
		});

		top.add(filterButton);

		return top;
	}
	
	@Override
	protected WidgetGroup centerWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		Table centerWidget = (Table) super.centerWidget(viewport, i18n, skin, controller);
		
		this.filterPanel = filterPanel(i18n, skin);
		Container wrapper = new Container(this.filterPanel);
		wrapper.setFillParent(true);
		wrapper.right().top();
		centerWidget.addActor(wrapper);
		
		Container navWrapper = new Container(navigation.getPanel());
		navWrapper.setFillParent(true);
		navWrapper.top().left().fillY();
		centerWidget.addActor(navWrapper);
		return centerWidget;
	}

	@Override
	protected WidgetGroup bottomWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		ToolBar botBar = new ToolBar(viewport, skin, 0.04f);

		Button bottomLeftButton = bottomLeftButton(viewport, i18n, skin,
				controller);
		if (bottomLeftButton != null) {
			botBar.add(bottomLeftButton).left().expandX();
		}
		Button bottomRightButton = bottomRightButton(viewport, i18n, skin,
				controller);
		if (bottomRightButton != null) {
			botBar.add(bottomRightButton).right();
		}

		return botBar;
	}

	protected abstract HiddenPanel filterPanel(I18N i18n, Skin skin);

	protected abstract Button bottomLeftButton(Vector2 viewport, I18N i18n,
			Skin skin, Controller controller);

	protected abstract Button bottomRightButton(Vector2 viewport, I18N i18n,
			Skin skin, Controller controller);
}
