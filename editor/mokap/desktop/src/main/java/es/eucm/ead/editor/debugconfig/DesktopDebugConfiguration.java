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
package es.eucm.ead.editor.debugconfig;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import es.eucm.ead.editor.debugconfig.devices.DeviceSpecs;
import es.eucm.ead.editor.debugconfig.devices.MokapVirtualDevice;

/**
 * Simple config extension to run {@link es.eucm.ead.editor.MokapDesktop} with
 * versatile configurations. Useful for debugging, demoing and taking snapshots.
 * 
 * Created by jtorrente on 1/03/15.
 */
public class DesktopDebugConfiguration extends LwjglApplicationConfiguration {

	public static final String ENGLISH = "en_EN";

	public static final String SPANISH = "es_ES";

	/**
	 * Returns default settings for default device (
	 * {@link es.eucm.ead.editor.debugconfig.devices.MokapVirtualDevice}).
	 * Language is not overriden.
	 */
	public static DesktopDebugConfiguration build() {
		return build(MokapVirtualDevice.class, null);
	}

	/**
	 * Equivalent to {@code build(clazz, null)}.
	 */
	public static DesktopDebugConfiguration build(
			Class<? extends DeviceSpecs> clazz) {
		return build(clazz, null);
	}

	/**
	 * Equivalent to {@code build(null, overrideLanguage)}.
	 */
	public static DesktopDebugConfiguration build(String overrideLanguage) {
		return build(MokapVirtualDevice.class, overrideLanguage);
	}

	/**
	 * Returns the specific settings (dpi, width, height and perhaps language)
	 * to debug {@link es.eucm.ead.editor.MokapDesktop} emulating a particular
	 * physical device. For a list of emulated devices available, see package
	 * {@link es.eucm.ead.editor.debugconfig.devices}.
	 * 
	 * @param clazz
	 *            The class representing the physical device to emulate. For
	 *            example, Nexus10.class. It cannot be {@code null}, or a
	 *            {@link NullPointerException} is thrown.
	 * @param language
	 *            If not null, it will override the default language preference
	 *            and run the editor in the language provided (e.g
	 *            {@link #ENGLISH} or {@link #SPANISH}). If {@code null},
	 *            default language will be used. See
	 *            {@link es.eucm.ead.editor.control.Preferences} for more
	 *            details.
	 * @return The configuration to be used. See
	 *         {@link com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration}
	 *         for more details. If the device specs cannot be loaded from
	 *         {@code clazz}, for whatever reason, this may return {@code null};
	 * @throws java.lang.NullPointerException
	 *             if {@code clazz} is {@code null}.
	 */
	public static DesktopDebugConfiguration build(
			Class<? extends DeviceSpecs> clazz, String language) {
		if (clazz == null) {
			throw new NullPointerException("clazz attribute cannot be null");
		}

		DesktopDebugConfiguration config = new DesktopDebugConfiguration();
		DeviceSpecs specs;
		try {
			specs = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			specs = null;
		}

		if (specs != null) {
			config.overrideDensity = specs.pixelDensity();
			config.width = specs.screenWidth();
			config.height = specs.screenHeight();
			config.title = "Mokap simulated in " + specs.name();
			config.editorLanguage = language;
			return config;
		}

		return null;
	}

	/**
	 * Overrides the system's stored preference on editor language.
	 * 
	 * {@code null} is the default value for this property. If null, editor
	 * language is not overriden. Admitted values: {@link #ENGLISH} and
	 * {@link #SPANISH}
	 */
	public String editorLanguage = null;

}
