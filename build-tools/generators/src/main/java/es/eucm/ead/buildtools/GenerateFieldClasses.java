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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Creates classes with String constants for the names of the fields of several
 * classes. Useful for use in mokap-backend.
 * 
 * Created by jtorrente on 8/12/14.
 */
public class GenerateFieldClasses {

	// Common properties
	private static final String SCHEMAX_REPO_PACKAGE = "es.eucm.ead.schemax.repo";
	private static final String EDITOR_SCHEMA_DESTFOLDER = "editor/schema/src/main/java/";
	private static final String REPOCOMPONENTS_MAIN_PATH = "build-tools/generators/src/main/resources/schema/editor/components/repo/";

	public static void main(String[] args) {
		System.out.println("Generating Field classes ...");
		System.out.println();
		Files files = new LwjglFiles();
		Json json = new Json();
		FileHandle dir = files.internal(REPOCOMPONENTS_MAIN_PATH);
		// Generate fields classes for all repo classes
		buildCodeForAllClasses(files, json, dir, SCHEMAX_REPO_PACKAGE,
				EDITOR_SCHEMA_DESTFOLDER);
	}

	/*
	 * Generates field classes for all classes defined through json schemas in
	 * folder sourceFolder (recursively)
	 */
	private static final void buildCodeForAllClasses(Files files, Json json,
			FileHandle sourceFolder, String targetPackageName,
			String targetDirectory) {
		for (FileHandle file : sourceFolder.list()) {
			if (file.extension().toLowerCase().equals("json")) {
				buildCode(files, json, file.path(), targetPackageName,
						targetDirectory, true);
			} else if (file.isDirectory()) {
				buildCodeForAllClasses(files, json, file, targetPackageName,
						targetDirectory);
			}
		}
	}

	private static String buildCode(Files files, Json json,
			String originJsonSchemaPath, String targetPackageName,
			String targetDirectory, boolean mainClass) {
		FileHandle fh = files.internal(originJsonSchemaPath);

		JsonValue next = json.fromJson(null, null, fh);
		next = next.child();

		String fieldsCode = "";
		String headerCode = "";
		String targetClassName = "";
		while ((next = next.next()) != null) {
			if (next.name().equals("properties")) {
				JsonValue nextProperty = next.child();
				fieldsCode += buildFields(nextProperty);
				break;
			} else if (next.name().equals("extends")) {
				String relativeParentJsonSchemaPath = next.child().asString();
				// Calculate directory to find parent class
				String parentJsonSchemaPath = originJsonSchemaPath.substring(0,
						originJsonSchemaPath.lastIndexOf("/") + 1)
						+ relativeParentJsonSchemaPath;
				fieldsCode += buildCode(files, json, parentJsonSchemaPath,
						null, null, false);
			} else if (next.name().equals("javaType") && mainClass) {
				String javaType = next.asString();
				String originalClassName = javaType.substring(
						javaType.lastIndexOf(".") + 1, javaType.length());
				targetClassName = originalClassName + "Fields";
				headerCode = getClassHeader(originalClassName,
						targetPackageName, targetClassName);
			}
		}

		if (mainClass) {
			if (fieldsCode.length() > 0) {
				System.out.println("Generating code for class "
						+ targetClassName);
				String classCode = headerCode + fieldsCode + "}";
				writeClass(files, classCode, targetPackageName,
						targetClassName, targetDirectory);
				return classCode;
			} else {
				System.out.println("Skipping class " + targetClassName
						+ " (no properties)");
				return "";
			}
		} else {
			return fieldsCode;
		}
	}

	private static void writeClass(Files files, String classCode,
			String targetPackageName, String targetClassName,
			String targetDirectory) {
		FileHandle destDir = new FileHandle(files.internal(
				targetDirectory + targetPackageName.replaceAll("\\.", "/")
						+ "/").file());
		destDir.mkdirs();
		FileHandle destFile = destDir.child(targetClassName + ".java");
		if (destFile.exists()) {
			destFile.delete();
		}
		destFile.writeString(classCode, false, "UTF-8");
	}

	private static String buildFields(JsonValue nextProperty) {
		String classCode = "";
		while (nextProperty != null) {
			// Look for description
			JsonValue description = nextProperty.child();
			while (description != null
					&& !description.name().equals("description")) {
				description = description.next();
			}
			if (description != null) {
				classCode += "\t/**\n";
				classCode += "\t *" + description.asString() + "\n";
				classCode += "\t*/\n";
			}
			classCode += "\tpublic static final String "
					+ nextProperty.name().toUpperCase() + " = \""
					+ nextProperty.name() + "\";\n\n";
			nextProperty = nextProperty.next();
		}
		return classCode;
	}

	private static String getClassHeader(String originalClassName,
			String targetPackageName, String targetClassName) {
		return "package "
				+ targetPackageName
				+ ";\n"
				+ "\n"
				+ "/**\n"
				+ " * Utility class for referring to fields in class "
				+ originalClassName
				+ "\n"
				+ " * Contains a String static constant with the name of each field in the class (defined through properties in the json schema).\n"
				+ " * Useful to protect backend from changes in api.\n"
				+ " * Autogenerated through Maven.\n" + " */\n"
				+ "public class " + targetClassName + "{\n";
	}
}
