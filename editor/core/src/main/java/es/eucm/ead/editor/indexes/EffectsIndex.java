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
package es.eucm.ead.editor.indexes;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import es.eucm.ead.schema.effects.AnimationEffect;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.TrackEffect;
import es.eucm.ead.schema.effects.controlstructures.ControlStructure;

/**
 * An index relating the short string representation of an effect (translated to
 * the current language) and its class
 */
public class EffectsIndex extends ModelIndex {

	private Array<Term> animationEffects;

	private Array<Term> standardEffects;

	private boolean init;

	public EffectsIndex() {
		super(Effect.class);
		animationEffects = new Array<Term>();
		standardEffects = new Array<Term>();
		init = false;
	}

	private void initialize() {
		Array<Term> terms = getTerms();
		for (Term t : terms) {
			if (!ClassReflection.isAssignableFrom(ControlStructure.class,
					(Class) t.getData())
					&& !(t.getData() == TrackEffect.class)
					&& !(t.getData() == AnimationEffect.class)) {
				if (ClassReflection.isAssignableFrom(AnimationEffect.class,
						(Class) t.getData())) {
					animationEffects.add(t);
				} else {
					standardEffects.add(t);
				}
			}
		}
		init = true;
	}

	public Array<Term> getInstantTypeEffects() {
		if (!init) {
			initialize();
		}
		return standardEffects;
	}

	public Array<Term> getAnimationTypeEffects() {
		if (!init) {
			initialize();
		}
		return animationEffects;
	}

}
