package es.eucm.ead.editor.view.builders.mockup.edition;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.EditionComponents.EditionComponent;
import es.eucm.ead.editor.view.widgets.mockup.EditionComponents.PaintComponent;
import es.eucm.ead.editor.view.widgets.mockup.EditionComponents.TextComponent;
import es.eucm.ead.engine.I18N;

public class EditionWindow implements ViewBuilder{

	private Navigation navigation;

	private Array<EditionComponent> components;
	private EditionComponent currentVisible;

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Actor build(Controller controller) {
		//I18N i18n = controller.getEditorAssets().getI18N();
		Skin skin = controller.getEditorAssets().getSkin();
		final Vector2 viewport = controller.getPlatform().getSize();

		components = editionComponents(viewport, controller);

		Table window = new Table();
		window.setFillParent(true);

		navigation = new Navigation(viewport, controller, skin);

		ToolBar top = toolbar(viewport, skin);
		
		Container navWrapper = new Container(navigation.getPanel());
		navWrapper.setFillParent(true);
		navWrapper.top().left().fillY();

		Table center = new Table(){
			@Override
			public void layout() {
				super.layout();
				for(Actor children : getChildren()){
					if(children instanceof EditionComponent){
						EditionComponent edit = (EditionComponent) children;
						
						edit.pack();
						final Button button = edit.getButton();
						button.pack();
						float prefX = button.getX() + button.getWidth()/2f
								-edit.getWidth()/2f;
						if(prefX + edit.getWidth() > getStage().getWidth()){
							prefX = getStage().getWidth() - edit.getWidth();
						}
						children.setPosition(prefX, getHeight() - edit.getHeight());
					}
				}
			}
		}.debug();
		for(EditionComponent i : components){
			center.addActor(i);
		}
		center.addActor(navWrapper);
		window.add(top).fillX().expandX();
		window.row();
		window.add(center).fill().expand();	

		return window;
	}

	private ToolBar toolbar(Vector2 viewport, Skin skin){
		ToolBar top = new ToolBar(viewport, skin);
		top.add(navigation.getButton()).left().expandX();
		top.left();

		for(EditionComponent component : components){
			top.add(component.getButton());
		}

		return top;
	}

	//TODO
	protected Array<EditionComponent> editionComponents(Vector2 viewport, Controller controller){
		I18N i18n = controller.getEditorAssets().getI18N();
		Skin skin = controller.getEditorAssets().getSkin();
		Array<EditionComponent> components = new Array<EditionComponent>();

		components.add(new PaintComponent(this, viewport, i18n, skin));
		components.add(new TextComponent(this, viewport, i18n, skin));

		final ButtonGroup buttonGroup = new ButtonGroup();
		for(EditionComponent component : components){
			buttonGroup.add(component.getButton());
		}
		return components;
	}

	public void changeCurrentVisible(EditionComponent component) {
		this.currentVisible = component;
	}

	public EditionComponent getCurrentVisible(){
		return this.currentVisible;
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
