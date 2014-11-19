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
package es.eucm.ead.editor.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Simple tool with functionality for zipping and unzipping compressed files.
 * Useful for importing and exporting, among other applications.
 * 
 * Created by Javier Torrente on 8/07/14.
 */
public class ZipUtils {

	/**
	 * Zips the given {@code inputFolder} into the the {@code outputZip} folder.
	 * If {@code inputFolder} is not actually a folder, an
	 * {@link IllegalArgumentException} is thrown. No checks regarding
	 * {@code outputZip} are performed.
	 */
	public static void zip(FileHandle inputFolder, FileHandle outputZip) {
		if (!inputFolder.isDirectory()) {
			throw new IllegalArgumentException("Input folder "
					+ inputFolder.file().getAbsolutePath()
					+ " is not a directory");
		}
		ZipOutputStream zipOutputStream = null;
		try {
			zipOutputStream = new ZipOutputStream(outputZip.write(false));
			writeDirectoryToZip(zipOutputStream, inputFolder, null);
			zipOutputStream.flush();
		} catch (IOException e) {
		} finally {
			if (zipOutputStream != null) {
				try {
					zipOutputStream.close();
				} catch (IOException e) {
					// Ignore exception, any important stuff should have been
					// cached in the IOException
				} finally {
					zipOutputStream = null;
				}
			}
		}
	}

	/**
	 * Unzips the given {@code sourceZip} to the given {@code outputFolder}
	 */
	public static void unzip(FileHandle sourceZip, FileHandle outputFolder) {

		byte[] buffer = new byte[1024];

		ZipInputStream zis = null;
		OutputStream fos = null;
		try {
			// Create output folder structure if does not exist
			if (!outputFolder.exists()) {
				outputFolder.mkdirs();
			}

			// Get the zip file content
			zis = new ZipInputStream(sourceZip.read());
			// Get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {
				// Write entry
				String fileName = ze.getName();
				FileHandle newFile = outputFolder.child(fileName);
				if (ze.isDirectory()) {
					newFile.mkdirs();
				} else {

					fos = newFile.write(false);

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
				}

				zis.closeEntry();
				if (fos != null) {
					fos.flush();
					fos.close();
				}
				fos = null;
				ze = zis.getNextEntry();
			}

		} catch (IOException ex) {
			Gdx.app.error("Unzipping", "IOException while unzipping "
					+ sourceZip.file().getAbsolutePath() + " to "
					+ outputFolder.file().getAbsolutePath(), ex);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (zis != null) {
					zis.close();
				}
			} catch (IOException ignored) {
				// Ignore exception, anything important should have been catched
				// already
			}
		}
	}

	/**
	 * Copies the contents of the {@code sources} file handles provided as
	 * arguments to a single compressed file (zip/jar) located at
	 * {@code destinyFile}.
	 * 
	 * Sources can be either directories, zip files or jar files. Sources that
	 * do not comply with these restrictions are just skipped.
	 * 
	 * @param destinyFile
	 *            The output file where to write contents
	 * @param sources
	 *            Jars, Zips or directories to copy contents from
	 */
	public static void mergeZipsAndDirsToFile(FileHandle destinyFile,
			FileHandle... sources) {
		ZipOutputStream zos = new ZipOutputStream(destinyFile.write(false));
		mergeZipsAndDirsToZip(zos, sources);
		try {
			zos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Copies the contents of the {@code sources} file handles provided as
	 * arguments to the given output stream {@code destiny}.
	 * 
	 * Sources can be either directories, zip files or jar files. Sources that
	 * do not comply with these restrictions are just skipped.
	 * 
	 * @param destiny
	 *            The output stream where to write the contents
	 * @param sources
	 *            Jars, Zips or directories to copy contents from
	 */
	public static void mergeZipsAndDirsToZip(ZipOutputStream destiny,
			FileHandle... sources) {
		try {
			byte[] readBuffer = new byte[1024];
			for (FileHandle source : sources) {
				// If it is a jar or zip file
				if (hasCompressedFileExtension(source)) {
					InputStream fis = source.read();
					CheckedInputStream checksum = new CheckedInputStream(fis,
							new Adler32());
					ZipInputStream zis = new ZipInputStream(
							new BufferedInputStream(checksum));
					ZipEntry entry = null;

					// Write the contents of the origin zip file to the destiny
					// output
					while ((entry = zis.getNextEntry()) != null) {
						// write the files to the disk
						JarEntry newEntry = new JarEntry(entry.getName());

						destiny.putNextEntry(newEntry);
						int bytesIn = 0;
						while ((bytesIn = zis.read(readBuffer)) != -1) {
							destiny.write(readBuffer, 0, bytesIn);
						}
						// close the Stream
						destiny.closeEntry();
					}
					zis.close();
				}
				// IF it is a dir
				else if (source.isDirectory()) {
					writeDirectoryToZip(destiny, source, "");
				}
			}
		} catch (Exception e) {
			Gdx.app.debug("Merging zips and dirs...",
					"An error occurred while writing " + sources.length
							+ " sources to output stream", e);
		}
	}

	/**
	 * @return true if the given {@code fileHandle} has the extension of a
	 *         compressed file this class can handle (zip or jar).
	 */
	public static boolean hasCompressedFileExtension(FileHandle fileHandle) {
		return hasExtension("zip", fileHandle)
				|| hasExtension("jar", fileHandle);
	}

	private static boolean hasExtension(String extension, FileHandle fileHandle) {
		return extension.equals(fileHandle == null ? null : fileHandle
				.extension().toLowerCase());
	}

	/**
	 * Writes the given directory {@code source} to the given zip output stream
	 * {@code destiny}. Since this method works recursively, it needs as an
	 * argument the relative path of source's parent inside the zip file to
	 * create the new {@link java.util.zip.ZipEntry}. The first call to this
	 * method can pass a null or blank {@code relPath}.
	 * 
	 * This method does not check if {@code source} is actually a directory.
	 * 
	 * @param destiny
	 *            The output stream where to write the contents
	 * @param source
	 *            The source directory to copy from
	 * @param relPath
	 *            The relative path of the source file's parent in the zip file.
	 *            (e.g. "/root", "/" or "")
	 * 
	 */
	public static void writeDirectoryToZip(ZipOutputStream destiny,
			FileHandle source, String relPath) {
		try {
			byte[] readBuffer = new byte[1024];
			FileHandle[] children = source.list();
			for (int i = 0; i < children.length; i++) {
				FileHandle child = children[i];
				String childName = child.name();

				// If it's a directory, make recursive call
				if (child.isDirectory()) {
					String childRelativePath = canonicalizeChild(relPath,
							childName);
					writeDirectoryToZip(destiny, child, childRelativePath);
				}
				// If not a directory, create Zip Entry and write it to the
				// output stream
				else {

					InputStream fis = child.read();

					// Take the path of the file relative to the source
					String entryName = canonicalizeChild(relPath, childName);
					ZipEntry anEntry = new ZipEntry(entryName);

					// Write the file into the ZIP. It is surrounded by a
					// try-catch block to allow the loop to continue if the file
					// cannot be written (Otherwise the external try-catch will
					// capture the exception and no more files in the directory
					// would be put into the ZIP
					try {
						destiny.putNextEntry(anEntry);
						int bytesIn = 0;
						while ((bytesIn = fis.read(readBuffer)) != -1) {
							destiny.write(readBuffer, 0, bytesIn);
						}
					} catch (ZipException zipException) {
						Gdx.app.error(
								"Writing directory to zip",
								"Error writing source "
										+ source.file().getAbsolutePath()
										+ " to zip output stream when processing entry "
										+ entryName, zipException);
					}

					// close the Stream
					fis.close();
					destiny.closeEntry();
				}
			}
		} catch (Exception e) {
			// handle exception
			Gdx.app.error("Writing directory to zip",
					"Error writing source " + source.file().getAbsolutePath()
							+ " to zip output stream", e);
		}
	}

	/**
	 * Returns a canonical relative path by appending {@code relPath} and
	 * {@code childName}. Example: canonicalizeChild("parent\dir\relpath",
	 * "a_child") returns: "parent/dir/relpath/a_child"
	 * 
	 * @param relPath
	 *            The relative path of the parent folder. If it contains back
	 *            slashes (\), these are replaced by /
	 * @param childName
	 *            The file name of the file child to be appended to relPath
	 * @return a canonical version of relPath+"/"childName
	 */
	public static String canonicalizeChild(String relPath, String childName) {
		String canonical;

		if (relPath != null && !relPath.equals("")) {
			relPath = relPath.replaceAll("\\\\", "/");
			canonical = relPath.endsWith("/") ? relPath + childName : relPath
					+ "/" + childName;
		} else {
			canonical = childName;
		}
		return canonical;
	}
}
