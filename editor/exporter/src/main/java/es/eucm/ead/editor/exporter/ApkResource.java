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
 * Helper class that stores constants needed by Export to produce the Maven
 * project that compiles standalone apks:
 * <ul>
 * <li>AndroidManifest.xml</li>
 * <li>pom.xml</li>
 * <li>res/layout/main.xml</li>
 * <li>res/values/strings.xml</li>
 * </ul>
 * 
 * Created by jtorrente on 3/01/15.
 */
public class ApkResource {

	// ///////////////////////////////
	// PUBLIC SECTION
	// ///////////////////////////////

	/**
	 * Filename of the launcher icon, as it appears under drawable-* folders,
	 * without extension
	 */
	public static final String APP_ICON_NAME = "ic_launcher";

	/**
	 * Name of the APK file produced by maven under target/ folder, without
	 * extension
	 */
	public static final String OUTPUT_FILENAME = "output";

	/**
	 * @return The contents of file pom.xml
	 * @param artifactId
	 *            The artifactId used for the pom (e.g. game-of-thrones). If
	 *            {@code null}, an artifactId containing only lowercase letters,
	 *            dashes and digits is generated automatically from the {}@code
	 *            appName}
	 * @param appName
	 *            The name of the application, in a user-friendly format (e.g.
	 *            Game Of Thrones). Cannot be {@code null}.
	 */
	public static String getPom(String artifactId, String appName) {
		if (artifactId == null) {
			artifactId = appNameToArtifactId(appName);
		}
		return POM.replaceAll(ARTIFACTID_PLACEHOLDER, artifactId);
	}

	/**
	 * @return The contents of file AndroidManifest.xml
	 * @param packageName
	 *            The main package for the application. It is important that
	 *            this package had not been used before for other standalone
	 *            mokap, as Google Play do not allow two apps with the same main
	 *            package. If null, a package name is automatically generated.
	 *            The default package name created will always start with
	 *            {@value #DEFAULT_PACKAGE_PARENT}, followed by a subpackage
	 *            created from the {@code appName}
	 * @param appName
	 *            The name of the application, in a user-friendly format (e.g.
	 *            Game Of Thrones). Cannot be {@code null}.
	 * @param canvasWidth
	 *            Canvas width to be passed to the EngineActivity.
	 * @param canvasHeight
	 *            Canvas height to be passed to the EngineActivity.
	 */
	public static String getAndroidManifest(String packageName, String appName,
			int canvasWidth, int canvasHeight) {
		if (packageName == null) {
			packageName = appNameToPackage(appName);
		}
		return ANDROID_MANIFEST.replaceAll(PACKAGE_PLACEHOLDER, packageName)
				.replaceAll(CANVAS_WIDTH_VALUE, "" + canvasWidth)
				.replaceAll(CANVAS_HEIGHT_VALUE, "" + canvasHeight);
	}

	/**
	 * @return The contents of file res/layout/main.xml
	 */
	public static String getLayoutMain() {
		return LAYOUT_MAIN;
	}

	/**
	 * @return Contents of file res/values/strings.xml. It just contains the
	 *         name of the app
	 * @param appName
	 *            The name of the application, in a user-friendly format (e.g.
	 *            Game Of Thrones). Cannot be {@code null}.
	 */
	public static String getValuesStrings(String appName) {
		return VALUES_STRINGS.replaceAll(APPNAME_PLACEHOLDER, appName);
	}

	// ///////////////////////////////
	// PRIVATE CONSTANTS
	// ///////////////////////////////

	// Fully qualified name of engine activity that launches the game.
	private static final String ENGINE_ACTIVITY = "es.eucm.ead.engine.android.EngineActivity";
	/*
	 * Meta-data keys EngineActivity expects to provide canvas width and height
	 */
	private static final String CANVAS_WIDTH_KEY = "CanvasWidth";
	private static final String CANVAS_HEIGHT_KEY = "CanvasHeight";

	private static final String DEFAULT_PACKAGE_PARENT = "es.eucm.mokaps";

	// Placeholders
	private static final String ARTIFACTID_PLACEHOLDER = "##ARTIFACTID##";
	private static final String PACKAGE_PLACEHOLDER = "##PACKAGE##";
	private static final String APPNAME_PLACEHOLDER = "##APPNAME##";
	private static final String CANVAS_WIDTH_VALUE = "##CANVASWIDTH##";
	private static final String CANVAS_HEIGHT_VALUE = "##CANVASHEIGHT##";

	// ///////////////////////////////
	// PRIVATE METHODS
	// ///////////////////////////////
	private static String appNameToPackage(String appName) {
		return DEFAULT_PACKAGE_PARENT + "." + appNameToId(appName, "");
	}

	private static String appNameToArtifactId(String appName) {
		return appNameToId(appName, "-");
	}

	private static String appNameToId(String appName, String replacementStr) {
		appName = appName.toLowerCase();
		String id = "";
		for (int i = 0; i < appName.length(); i++) {
			char currentChar = appName.charAt(i);
			if (Character.isLetterOrDigit(currentChar)) {
				id += currentChar;
			} else {
				id += replacementStr;
			}
		}
		return id;
	}

	// /////////////////////////////////////////
	// PRIVATE CONSTANTS WITH FILE CONTENTS
	// /////////////////////////////////////////
	private static final String ANDROID_MANIFEST = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
			+ "          package=\""
			+ PACKAGE_PLACEHOLDER
			+ "\"\n"
			+ "          android:screenOrientation=\"landscape\"\n"
			+ "          android:versionCode=\"9\" android:versionName=\"0.9.9\">\n"
			+ "    <uses-sdk android:minSdkVersion=\"10\" android:targetSdkVersion=\"19\"/>\n"
			+ "\n"
			+ "    <!-- Tell the system that you need ES 2.0. -->\n"
			+ "    <uses-feature android:glEsVersion=\"0x00020000\"\n"
			+ "                  android:required=\"true\"/>\n"
			+ "    <uses-feature android:name=\"android.hardware.camera\"\n"
			+ "                  android:required=\"false\"/>\n"
			+ "\n"
			+ "\n"
			+ "    <application android:allowBackup=\"false\" android:debuggable=\"false\" android:label=\"@string/app_name\" android:icon=\"@drawable/"
			+ APP_ICON_NAME
			+ "\">\n"
			+ "\n"
			+ "        <activity android:name=\""
			+ ENGINE_ACTIVITY
			+ "\"\n"
			+ "                  android:label=\"@string/app_name\" android:configChanges=\"keyboard|keyboardHidden|orientation|screenSize\" android:screenOrientation=\"sensorLandscape\" android:theme=\"@android:style/Theme.NoTitleBar.Fullscreen\">\n"
			+ "            <intent-filter>\n"
			+ "                <action android:name=\"android.intent.action.MAIN\"/>\n"
			+ "                <category android:name=\"android.intent.category.LAUNCHER\"/>\n"
			+ "            </intent-filter>\n"
			+ "            <meta-data android:name=\""
			+ CANVAS_WIDTH_KEY
			+ "\" android:value=\""
			+ CANVAS_WIDTH_VALUE
			+ "\" />"
			+ "            <meta-data android:name=\""
			+ CANVAS_HEIGHT_KEY
			+ "\" android:value=\""
			+ CANVAS_HEIGHT_VALUE
			+ "\" />"
			+ "        </activity>\n" + "    </application>\n" + "</manifest>";

	private static final String LAYOUT_MAIN = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
			+ "              android:orientation=\"vertical\"\n"
			+ "              android:layout_width=\"fill_parent\"\n"
			+ "              android:layout_height=\"fill_parent\"\n"
			+ "        >\n"
			+ "    <TextView\n"
			+ "            android:layout_width=\"fill_parent\"\n"
			+ "            android:layout_height=\"wrap_content\"\n"
			+ "            android:text=\"Android - Mokap\"\n"
			+ "            />\n" + "</LinearLayout>\n" + "\n";

	private static final String VALUES_STRINGS = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
			+ "<resources>\n"
			+ "    <string name=\"app_name\">"
			+ APPNAME_PLACEHOLDER + "</string>\n" + "</resources>\n";

	private static final String POM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n"
			+ "    <parent>\n"
			+ "        <groupId>org.sonatype.oss</groupId>\n"
			+ "        <artifactId>oss-parent</artifactId>\n"
			+ "        <version>7</version>\n"
			+ "    </parent>\n"
			+ "    <modelVersion>4.0.0</modelVersion>\n"
			+ "    <groupId>es.e-ucm.ead</groupId>\n"
			+ "    <version>1.0-SNAPSHOT</version>\n" + "    <artifactId>"
			+ ARTIFACTID_PLACEHOLDER
			+ "</artifactId>\n"
			+ "    <packaging>apk</packaging>\n"
			+ "    <properties>\n"
			+ "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n"
			+ "        <gdx.version>1.5.0</gdx.version>\n"
			+ "        <android.version>4.1.1.4</android.version>\n"
			+ "        <android.platform.version>19</android.platform.version>\n"
			+ "        <android.editor.version>4.1.1.4</android.editor.version>\n"
			+ "        <android.editor.platform.version>19</android.editor.platform.version>\n"
			+ "        <android.maven.version>3.9.0-rc.1</android.maven.version>\n"
			+ "        <gwt.version>2.5.0</gwt.version>\n"
			+ "        <gwt.maven.version>2.5.0</gwt.maven.version>\n"
			+ "        <network.version>0.1-SNAPSHOT</network.version>\n"
			+ "        <java.version>1.6</java.version>\n"
			+ "\n"
			+ "        <keystore.path>game.keystore</keystore.path>\n"
			+ "        <keystore.alias>game</keystore.alias>\n"
			+ "        <!-- you can pass these on the command line as -Dkeystore.password=foo\n"
			+ "\t\t\tetc. -->\n"
			+ "        <keystore.password/>\n"
			+ "        <key.password>${keystore.password}</key.password>\n"
			+ "    </properties>\n"
			+ "\n"
			+ "    <!-- ====================================================================== -->\n"
			+ "    <!-- SOURCE CODE MANAGEMENT -->\n"
			+ "    <!-- ====================================================================== -->\n"
			+ "    <scm>\n"
			+ "        <url>http://github.com/e-ucm/ead/tree/master</url>\n"
			+ "        <connection>scm:git:git://git@github.com:e-ucm/ead.git</connection>\n"
			+ "        <developerConnection>scm:git:ssh://git@github.com:e-ucm/ead.git</developerConnection>\n"
			+ "    </scm>\n"
			+ "    <issueManagement>\n"
			+ "        <system>GitHub</system>\n"
			+ "        <url>https://github.com/e-ucm/ead/issues</url>\n"
			+ "    </issueManagement>\n"
			+ "    <ciManagement>\n"
			+ "        <system>Travis</system>\n"
			+ "        <url>https://travis-ci.org/e-ucm/ead</url>\n"
			+ "    </ciManagement>\n"
			+ "    <!-- ====================================================================== -->\n"
			+ "    <!-- DISTRIBUTION MANAGEMENT -->\n"
			+ "    <!-- ====================================================================== -->\n"
			+ "    <distributionManagement>\n"
			+ "        <repository>\n"
			+ "            <id>sonatype-nexus-staging</id>\n"
			+ "            <name>Nexus Release Repository</name>\n"
			+ "            <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>\n"
			+ "        </repository>\n"
			+ "        <snapshotRepository>\n"
			+ "            <id>sonatype-nexus-snapshots</id>\n"
			+ "            <name>Sonatype Nexus Snapshots</name>\n"
			+ "            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>\n"
			+ "        </snapshotRepository>\n"
			+ "    </distributionManagement>\n"
			+ "    <!-- ====================================================================== -->\n"
			+ "    <!-- R E P O S I T O R I E S -->\n"
			+ "    <!-- ====================================================================== -->\n"
			+ "    <repositories>\n"
			+ "        <!-- SNAPSHOTS from libGDX -->\n"
			+ "        <repository>\n"
			+ "            <id>sonatype-nexus-snapshots</id>\n"
			+ "            <name>Sonatype Nexus Snapshots</name>\n"
			+ "            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>\n"
			+ "            <snapshots>\n"
			+ "                <enabled>true</enabled>\n"
			+ "            </snapshots>\n"
			+ "        </repository>\n"
			+ "        <repository>\n"
			+ "            <id>caprica</id>\n"
			+ "            <name>Caprica Software</name>\n"
			+ "            <url>http://www.capricasoftware.co.uk/repo</url>\n"
			+ "        </repository>\n"
			+ "        <repository>\n"
			+ "            <id>Project github maven repo</id>\n"
			+ "            <name>Project github maven repo</name>\n"
			+ "            <url>https://github.com/e-ucm/eadventure/raw/master/etc/repository/</url>\n"
			+ "            <snapshots>\n"
			+ "                <enabled>true</enabled>\n"
			+ "            </snapshots>\n"
			+ "            <releases>\n"
			+ "                <enabled>true</enabled>\n"
			+ "            </releases>\n"
			+ "        </repository>\n"
			+ "    </repositories>\n"
			+ "    <pluginRepositories>\n"
			+ "        <pluginRepository>\n"
			+ "            <id>apache-plugin-snapshot</id>\n"
			+ "            <name>Apache Snapshots Plugins</name>\n"
			+ "            <releases>\n"
			+ "                <enabled>false</enabled>\n"
			+ "            </releases>\n"
			+ "            <snapshots>\n"
			+ "                <enabled>true</enabled>\n"
			+ "            </snapshots>\n"
			+ "            <url>http://repository.apache.org/snapshots/</url>\n"
			+ "        </pluginRepository>\n"
			+ "        <pluginRepository>\n"
			+ "            <id>snapshots-codehaus-maven2</id>\n"
			+ "            <name>Codehaus Snapshots Plugins</name>\n"
			+ "            <url>http://snapshots.repository.codehaus.org</url>\n"
			+ "            <layout>default</layout>\n"
			+ "            <releases>\n"
			+ "                <enabled>false</enabled>\n"
			+ "            </releases>\n"
			+ "            <snapshots>\n"
			+ "                <enabled>true</enabled>\n"
			+ "            </snapshots>\n"
			+ "        </pluginRepository>\n"
			+ "        <pluginRepository>\n"
			+ "            <id>nexus-oss-maven-plugin-snapshots</id>\n"
			+ "            <name>Sonatype Nexus OSS maven plugin snapshots</name>\n"
			+ "            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>\n"
			+ "            <layout>default</layout>\n"
			+ "            <releases>\n"
			+ "                <enabled>false</enabled>\n"
			+ "            </releases>\n"
			+ "            <snapshots>\n"
			+ "                <enabled>true</enabled>\n"
			+ "            </snapshots>\n"
			+ "        </pluginRepository>\n"
			+ "    </pluginRepositories>\n"
			+ "\n"
			+ "\n"
			+ "    <dependencies>\n"
			+ "        <dependency>\n"
			+ "            <groupId>es.e-ucm.ead</groupId>\n"
			+ "            <artifactId>engine-core</artifactId>\n"
			+ "            <version>${project.version}</version>\n"
			+ "        </dependency>\n"
			+ "        <dependency>\n"
			+ "            <groupId>es.e-ucm.ead</groupId>\n"
			+ "            <artifactId>engine-android</artifactId>\n"
			+ "            <version>${project.version}</version>\n"
			+ "        </dependency>\n"
			+ "        <dependency>\n"
			+ "            <groupId>com.badlogicgames.gdx</groupId>\n"
			+ "            <artifactId>gdx-backend-android</artifactId>\n"
			+ "            <version>${gdx.version}</version>\n"
			+ "        </dependency>\n"
			+ "        <dependency>\n"
			+ "            <groupId>com.badlogicgames.gdx</groupId>\n"
			+ "            <artifactId>gdx-platform</artifactId>\n"
			+ "            <version>${gdx.version}</version>\n"
			+ "            <classifier>natives-armeabi</classifier>\n"
			+ "            <scope>provided</scope>\n"
			+ "        </dependency>\n"
			+ "        <dependency>\n"
			+ "            <groupId>com.badlogicgames.gdx</groupId>\n"
			+ "            <artifactId>gdx-platform</artifactId>\n"
			+ "            <version>${gdx.version}</version>\n"
			+ "            <classifier>natives-armeabi-v7a</classifier>\n"
			+ "            <scope>provided</scope>\n"
			+ "        </dependency>\n"
			+ "        <dependency>\n"
			+ "            <groupId>com.google.android</groupId>\n"
			+ "            <artifactId>android</artifactId>\n"
			+ "            <version>${android.editor.version}</version>\n"
			+ "            <scope>provided</scope>\n"
			+ "        </dependency>\n"
			+ "        <dependency>\n"
			+ "            <groupId>com.badlogicgames.gdx</groupId>\n"
			+ "            <artifactId>gdx-freetype-platform</artifactId>\n"
			+ "            <version>${gdx.version}</version>\n"
			+ "            <classifier>natives-armeabi-v7a</classifier>\n"
			+ "            <scope>provided</scope>\n"
			+ "        </dependency>\n"
			+ "    </dependencies>\n"
			+ "    <build>\n"
			+ "        <!-- output APK doesn't contain version number, needed for Intellij Idea -->\n"
			+ "        <finalName>"
			+ OUTPUT_FILENAME
			+ "</finalName>\n"
			+ "        <pluginManagement>\n"
			+ "            <plugins>\n"
			+ "                <plugin>\n"
			+ "                    <groupId>com.jayway.maven.plugins.android.generation2</groupId>\n"
			+ "                    <artifactId>android-maven-plugin</artifactId>\n"
			+ "                    <version>${android.maven.version}</version>\n"
			+ "                    <configuration>\n"
			+ "                        <generateApk>false</generateApk>\n"
			+ "                        <androidManifestFile>${project.basedir}/AndroidManifest.xml</androidManifestFile>\n"
			+ "                        <assetsDirectory>${project.basedir}/assets</assetsDirectory>\n"
			+ "                        <resourceDirectory>${project.basedir}/res</resourceDirectory>\n"
			+ "                        <sdk>\n"
			+ "                            <platform>${android.platform.version}</platform>\n"
			+ "                        </sdk>\n"
			+ "                        <undeployBeforeDeploy>true</undeployBeforeDeploy>\n"
			+ "                        <dex>\n"
			+ "                            <jvmArguments>\n"
			+ "                                <jvmArgument>-Xmx1024m</jvmArgument>\n"
			+ "                            </jvmArguments>\n"
			+ "                        </dex>\n"
			+ "                    </configuration>\n"
			+ "                    <extensions>true</extensions>\n"
			+ "                </plugin>\n"
			+ "                <plugin>\n"
			+ "                    <groupId>com.googlecode.mavennatives</groupId>\n"
			+ "                    <artifactId>maven-nativedependencies-plugin</artifactId>\n"
			+ "                    <version>0.0.7</version>\n"
			+ "                </plugin>\n"
			+ "                <plugin>\n"
			+ "                    <groupId>org.apache.maven.plugins</groupId>\n"
			+ "                    <artifactId>maven-jarsigner-plugin</artifactId>\n"
			+ "                    <version>1.2</version>\n"
			+ "                </plugin>\n"
			+ "            </plugins>\n"
			+ "        </pluginManagement>\n"
			+ "        <plugins>\n"
			+ "            <plugin>\n"
			+ "                <groupId>com.jayway.maven.plugins.android.generation2</groupId>\n"
			+ "                <artifactId>android-maven-plugin</artifactId>\n"
			+ "            </plugin>\n"
			+ "        </plugins>\n"
			+ "    </build>\n"
			+ "    <profiles>\n"
			+ "        <profile>\n"
			+ "            <id>android-build</id>\n"
			+ "            <build>\n"
			+ "                <plugins>\n"
			+ "                    <!-- responsible for unpacking the shared libraries to the libs/ folder -->\n"
			+ "                    <plugin>\n"
			+ "                        <groupId>com.googlecode.mavennatives</groupId>\n"
			+ "                        <artifactId>maven-nativedependencies-plugin</artifactId>\n"
			+ "                        <configuration>\n"
			+ "                            <nativesTargetDir>libs</nativesTargetDir>\n"
			+ "                            <separateDirs>true</separateDirs>\n"
			+ "                        </configuration>\n"
			+ "                        <executions>\n"
			+ "                            <execution>\n"
			+ "                                <phase>prepare-package</phase>\n"
			+ "                                <goals>\n"
			+ "                                    <goal>copy</goal>\n"
			+ "                                </goals>\n"
			+ "                            </execution>\n"
			+ "                        </executions>\n"
			+ "                    </plugin>\n"
			+ "                    <!-- responsible for being able to compile for Android -->\n"
			+ "                    <plugin>\n"
			+ "                        <groupId>com.jayway.maven.plugins.android.generation2</groupId>\n"
			+ "                        <artifactId>android-maven-plugin</artifactId>\n"
			+ "                        <configuration>\n"
			+ "                            <generateApk>true</generateApk>\n"
			+ "                        </configuration>\n"
			+ "                    </plugin>\n"
			+ "                </plugins>\n"
			+ "            </build>\n"
			+ "        </profile>\n"
			+ "        <profile>\n"
			+ "            <id>android-sign</id>\n"
			+ "            <build>\n"
			+ "                <plugins>\n"
			+ "                    <plugin>\n"
			+ "                        <groupId>org.apache.maven.plugins</groupId>\n"
			+ "                        <artifactId>maven-jarsigner-plugin</artifactId>\n"
			+ "                        <configuration>\n"
			+ "                            <archiveDirectory/>\n"
			+ "                            <includes>\n"
			+ "                                <include>target/*.apk</include>\n"
			+ "                            </includes>\n"
			+ "                            <keystore>${keystore.path}</keystore>\n"
			+ "                            <storepass>${keystore.password}</storepass>\n"
			+ "                            <keypass>${key.password}</keypass>\n"
			+ "                            <alias>${keystore.alias}</alias>\n"
			+ "                            <arguments>\n"
			+ "                                <argument>-sigalg</argument>\n"
			+ "                                <argument>MD5withRSA</argument>\n"
			+ "                                <argument>-digestalg</argument>\n"
			+ "                                <argument>SHA1</argument>\n"
			+ "                            </arguments>\n"
			+ "                        </configuration>\n"
			+ "                        <executions>\n"
			+ "                            <execution>\n"
			+ "                                <id>signing</id>\n"
			+ "                                <phase>package</phase>\n"
			+ "                                <goals>\n"
			+ "                                    <goal>sign</goal>\n"
			+ "                                </goals>\n"
			+ "                            </execution>\n"
			+ "                        </executions>\n"
			+ "                    </plugin>\n"
			+ "                    <plugin>\n"
			+ "                        <groupId>com.jayway.maven.plugins.android.generation2</groupId>\n"
			+ "                        <artifactId>android-maven-plugin</artifactId>\n"
			+ "                        <configuration>\n"
			+ "                            <zipalign>\n"
			+ "                                <skip>false</skip>\n"
			+ "                            </zipalign>\n"
			+ "                            <sign>\n"
			+ "                                <debug>false</debug>\n"
			+ "                            </sign>\n"
			+ "                        </configuration>\n"
			+ "                        <executions>\n"
			+ "                            <execution>\n"
			+ "                                <id>alignApk</id>\n"
			+ "                                <phase>package</phase>\n"
			+ "                                <goals>\n"
			+ "                                    <goal>zipalign</goal>\n"
			+ "                                </goals>\n"
			+ "                            </execution>\n"
			+ "                        </executions>\n"
			+ "                    </plugin>\n"
			+ "                </plugins>\n"
			+ "            </build>\n"
			+ "        </profile>\n" + "    </profiles>\n" + "</project>\n";
}
