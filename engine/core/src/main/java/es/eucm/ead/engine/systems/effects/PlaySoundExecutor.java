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
package es.eucm.ead.engine.systems.effects;

import com.badlogic.ashley.core.Entity;

import com.badlogic.gdx.utils.Array;

import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.schema.assets.Sound;
import es.eucm.ead.schema.effects.AddComponent;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.PlaySound;

public class PlaySoundExecutor extends EffectExecutor<PlaySound> {

	private Array<Effect> list;
	private EffectsSystem system;

	public PlaySoundExecutor(EffectsSystem system) {
		list = new Array<Effect>(1);
		this.system = system;
	}

	@Override
	public void execute(Entity owner, PlaySound effect) {

		Sound sound = new Sound();
		sound.setLoop(effect.isLoop());
		sound.setUri(effect.getUri());
		sound.setVolume(effect.getVolume());

		AddComponent addSound = new AddComponent();
		addSound.setTarget(effect.getTarget());
		addSound.setComponent(sound);
		list.clear();
		list.add(addSound);
		system.executeEffectList(list);
	}
}
