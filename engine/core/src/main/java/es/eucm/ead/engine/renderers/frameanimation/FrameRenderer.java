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
package es.eucm.ead.engine.renderers.frameanimation;

import com.badlogic.gdx.graphics.g2d.Batch;
import es.eucm.ead.engine.renderers.AbstractRenderer;
import es.eucm.ead.schema.renderers.frameanimation.Frame;

/**
 * Created by Javier Torrente on 2/02/14.
 */
public class FrameRenderer extends TimedRenderer<Frame> {

	private AbstractRenderer delegateRenderer;

	@Override
	public void draw(Batch batch) {
		// Just delegate to delegateRenderer
		delegateRenderer.draw(batch);
	}

	@Override
	public float getHeight() {
		return delegateRenderer.getHeight();
	}

	@Override
	public float getWidth() {
		return delegateRenderer.getWidth();
	}

	@Override
	public void initialize(Frame schemaObject) {
		super.initialize(schemaObject);
		delegateRenderer = gameLoop.getFactory().getEngineObject(
				schemaObject.getDelegateRenderer());
	}

	@Override
	public void dispose() {
		delegateRenderer.dispose();
		super.dispose();
	}

    @Override
    // Must override act to ensure this call is propagated to delegateRenderer
    public void act(float delta){
        super.act(delta);
        delegateRenderer.act(delta);
    }
}
