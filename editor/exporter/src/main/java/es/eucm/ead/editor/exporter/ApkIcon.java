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
package es.eucm.ead.editor.exporter;

/**
 * Helper class used by Exporter when producing standalone Android mokaps
 * (apks). It provides information (resolution and path under res/) about the
 * different versions of the launcher icon it must produce. Since it is an enum,
 * it also provides order to iterate through icons according to size
 * (resolution).
 * 
 * Created by jtorrente on 3/01/15.
 */
public enum ApkIcon {
	LDPI("ldpi", 36), MDPI("mdpi", 48), HDPI("hdpi", 72), XHDPI("xhdpi", 96), XXHDPI(
			"xxhdpi", 144), XXXHDPI("xxxhdpi", 192);

	private String suffix;
	private int resolution;

	private ApkIcon(String suffix, int resolution) {
		this.suffix = suffix;
		this.resolution = resolution;
	}

	/**
	 * @return The name of the resolution. That is, the suffix that is appended
	 *         after "drawable-" (e.g. drawable-xhdpi). For more details, see <a
	 *         href
	 *         ="http://developer.android.com/design/style/iconography.html">
	 *         Android official docs</a>
	 */
	public String getName() {
		return suffix;
	}

	/**
	 * @return The width and height, in pixels, the icon of this DPI must have
	 *         (icons are square so width=height).
	 */
	public int getResolution() {
		return resolution;
	}

	/**
	 * @return The name of the directory under res/ this icon must be placed
	 *         into (e.g. drawable-xhdpi)
	 */
	public String getPath() {
		return "drawable-" + suffix;
	}

	/**
	 * Given a specific resolution (icon width or height) in pixels, the
	 * {@link ApkIcon} with closest but not greater resolution is found and
	 * returned. The ApkIcon returned is always equals or smaller to the
	 * resolution argument.
	 * 
	 * @param resolution
	 *            The icon width or height being searched for, in pixels
	 * @return An ApkIcon where apkIcon.resolution <= resolution
	 */
	public static ApkIcon fromResolution(int resolution) {
		for (int i = values().length - 1; i >= 0; i--) {
			ApkIcon icon = values()[i];
			ApkIcon prevIcon = i < values().length - 1 ? values()[i + 1] : null;
			if (icon.getResolution() == resolution
					|| resolution > icon.getResolution()
					&& (prevIcon == null || resolution < prevIcon
							.getResolution())) {
				return icon;
			}
		}
		return null;
	}
}
