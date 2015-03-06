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
package es.eucm.ead.editor.view.builders.scene.templates;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.Parameters;
import es.eucm.ead.schema.editor.components.templates.Attribute;
import es.eucm.ead.schema.editor.components.templates.TemplateAttributes;
import es.eucm.ead.schemax.FieldName;

public class TemplateEditor extends AbstractWidget {

	public static final float AREA_CM = 0.5f;

	private final float PAD = WidgetBuilder.dpToPixels(32);

	private TemplateAttributes template;

	private Group container;

	private Parameters parameters;

	private AttributeMenu attributes;

	private TemplateBar templateBar;

	private Button random;

	public TemplateEditor(Group container, final Controller controller) {

		Skin skin = controller.getApplicationAssets().getSkin();

		this.container = container;

		setFillParent(true);

		attributes = new AttributeMenu(skin);
		attributes.setVisible(false);

		parameters = new Parameters();

		templateBar = new TemplateBar(controller);

		random = WidgetBuilder.circleButton(SkinConstants.IC_RANDOM);
		random.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				super.touchDragged(event, x, y, pointer);
				getStage().cancelTouchFocusExcept(this, TemplateEditor.this);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				for (Attribute attr : template.getAttributes()) {
					float min = attr.getMin();
					float max = attr.getMax();
					controller.action(
							SetField.class,
							getParameterByName(attr.getName()),
							FieldName.VALUE,
							(float) Math.round(min + Math.random()
									* (max - min)));
				}
			}
		});

		addActor(random);
		addActor(attributes);
		addActor(templateBar);

		addListener(new InputListener() {

			private float lastX;

			private float lastY;

			private int direction;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				lastX = x;
				lastY = y;
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				if (direction == 0) {
					if (Math.abs(y - lastY) > AbstractWidget
							.cmToXPixels(AREA_CM)) {
						attributes.show();
						direction = 1;
						lastY = y;
					} else if (Math.abs(x - lastX) > AbstractWidget
							.cmToXPixels(AREA_CM)) {
						direction = 2;
						lastX = x;
					}
				} else if (direction == 1) {
					attributes.verticalMove(y - lastY);
					lastY = y;
				} else if (direction == 2) {
					templateBar.horizontalMove(getWidth(), x - lastX);
					lastX = x;
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				if (direction == 1) {
					String name = attributes.getAttributeSelected();
					templateBar.changeWidget(getAttributeByName(name),
							getParameterByName(name));
					attributes.hide();
				} else if (direction == 2) {
					templateBar.stopMove();
				}
				direction = 0;
			}
		});
	}

	public void show() {
		if (!hasParent()) {
			container.addActor(this);
			String name = attributes.getAttributeSelected();
			templateBar.changeWidget(getAttributeByName(name),
					getParameterByName(name));
		}
	}

	public void hide() {
		remove();
	}

	private Attribute getAttributeByName(String name) {
		for (Attribute attr : template.getAttributes()) {
			if (attr.getName().equals(name)) {
				return attr;
			}
		}
		return null;
	}

	private Parameter getParameterByName(String name) {
		for (Parameter parameter : parameters.getParameters()) {
			if (parameter.getName().equals(name)) {
				return parameter;
			}
		}
		return null;
	}

	@Override
	public void layout() {
		super.layout();
		random.setPosition(getWidth() - PAD - random.getWidth(), PAD);
		attributes.setPosition(getWidth() * 0.5f, getHeight() * 0.5f);
	}

	public void addTemplateAttributes(TemplateAttributes att) {
		attributes.clearAttributeMenu();
		parameters.getParameters().clear();
		template = att;
		for (Attribute attribute : att.getAttributes()) {
			attributes.addAttribute(attribute.getName());
			Parameter parameter = new Parameter();
			parameter.setValue(attribute.getDefaultValue());
			parameter.setName(attribute.getName());
			parameters.getParameters().add(parameter);
		}
	}
}
