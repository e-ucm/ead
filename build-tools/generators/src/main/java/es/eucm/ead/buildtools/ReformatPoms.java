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
package es.eucm.ead.buildtools;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reformats POMs to avoid spurious diffs
 */
public class ReformatPoms {

	static final String POM_NAME = "pom.xml";
	static final int MAX_LENGTH = 80;
	static final int SPACES_PER_INDENT = 4;
	static String expandedTab;

	static Pattern openingTagWithAttrsRegex;
	static Pattern openTagTextAndCloseRegex;

	static {
		// calculate size of an expanded tab
		expandedTab = " ";
		for (int i = 0; i < SPACES_PER_INDENT; i++) {
			expandedTab += " ";
		}

		// initialize openingTagWithAttrsRegex
		openingTagWithAttrsRegex = Pattern
				.compile("<[^\\s=>/]+(\\s+[^\\s=>/]+=\"[^\"]+\")+>");

		// initialize openTagTextAndCloseRegex
		openTagTextAndCloseRegex = Pattern.compile("<([^>]+)>([^<]+)</\\1>");
	}

	public static void main(String[] args) {
		System.out.println("Reformatting poms...");
		Files files = new LwjglFiles();
		reformat(new FileHandle(files.internal(".").file()));
	}

	public static void reformat(FileHandle folder) {
		for (FileHandle child : folder.list()) {
			if (child.isDirectory()) {
				reformat(child);
			} else if (child.name().equals(POM_NAME)) {
				System.err.println("Reformatting POM: " + child.path());
				String xml = postProcess(prettify(child.readString()));
				child.writeString(xml, false);
			}
		}
	}

	public static int countIndents(String line) {
		int indents = 0;
		for (char c : line.toCharArray()) {
			if (Character.isWhitespace(c)) {
				indents++;
			} else {
				return indents;
			}
		}
		return indents;
	}

	public static String indentLine(String line, int indentCount) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indentCount; i++) {
			sb.append('\t');
		}
		return sb.append(line).toString();
	}

	public static String prettify(String xml) {
		xml = xml.replaceAll("[\t]", expandedTab).replaceAll(expandedTab, "\t");
		StringWriter sw;
		try {
			OutputFormat format = new OutputFormat("\t");
			format.setNewlines(true);
			format.setPadText(false);
			format.setTrimText(true);
			org.dom4j.Document document = DocumentHelper.parseText(xml);
			sw = new StringWriter();
			XMLWriter writer = new XMLWriter(sw, format);
			writer.write(document);
		} catch (Exception e) {
			throw new RuntimeException("Error pretty printing xml:\n" + xml, e);
		}
		return sw.toString();
	}

	public static void fixLongText(String text, int indents, StringBuilder out) {
		String[] parts = text.split(" ");
		if (parts.length > 1) {
			int pos = indents * SPACES_PER_INDENT + parts[0].length();
			out.append(indentLine(parts[0], indents));
			for (int p = 1; p < parts.length - 1; p++) {
				int partLength = parts[p].length();
				if (pos + 1 + partLength >= MAX_LENGTH) {
					pos = indents * SPACES_PER_INDENT + parts[p].length();
					out.append('\n').append(indentLine(parts[p], indents));
				} else {
					pos += 1 + partLength;
					out.append(' ').append(parts[p]);
				}
			}
			out.append(' ').append(parts[parts.length - 1]).append('\n');
		} else {
			out.append(indentLine(parts[parts.length - 1], indents)).append(
					'\n');
		}
	}

	public static String postProcess(String xml) {
		// now, attempt to do magic line-wrapping (heuristic-driven)
		StringBuilder pretty = new StringBuilder();
		for (String line : xml.split("[\n]+")) {
			boolean fixed = false;
			if (line.length() > MAX_LENGTH) {
				int indents = countIndents(line);
				String trimmed = line.trim();
				if (trimmed.startsWith("<!--") || !trimmed.startsWith("<")) {
					// split by word-boundary
					fixLongText(trimmed, indents, pretty);
					fixed = true;
				} else if (openingTagWithAttrsRegex.matcher(trimmed).matches()) {
					// this looks like a big XML start-tag; lets split by
					// attribute-boundary
					String[] parts = trimmed.split("[\"] ");
					pretty.append(indentLine(parts[0], indents)).append('\"')
							.append('\n');
					for (int i = 1; i < parts.length - 1; i++) {
						pretty.append(indentLine(parts[i], indents + 1))
								.append('\"').append('\n');
					}
					pretty.append(
							indentLine(parts[parts.length - 1], indents + 1))
							.append('\n');
					fixed = true;
				} else if (openTagTextAndCloseRegex.matcher(trimmed).matches()) {
					Matcher m = openTagTextAndCloseRegex.matcher(trimmed);
					m.find();
					pretty.append(indentLine("<", indents)).append(m.group(1))
							.append(">\n");
					fixLongText(m.group(2), indents + 1, pretty);
					pretty.append(indentLine("</", indents)).append(m.group(1))
							.append(">\n");
					fixed = true;
				}
			}

			if (!fixed) {
				pretty.append(line).append('\n');
			}
		}
		return pretty.toString();
	}
}
