# Mokap Android App  [![Build Status](https://travis-ci.org/e-ucm/ead.png)](https://travis-ci.org/e-ucm/ead)

[![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=es.eucm.mokap) 

[![Download from Google Play](https://cloud.githubusercontent.com/assets/5658058/6525540/d9b428c0-c406-11e4-9066-019ad2a98ab0.jpg)](https://play.google.com/store/apps/details?id=es.eucm.mokap)

[Official web page](http://mokap.es/)

Please see the [issues](https://github.com/e-ucm/ead/issues) section to
report any bugs or feature requests and to see the list of known issues.

## License

* [GNU LGPL 3.0](https://www.gnu.org/licenses/lgpl.html)

## Building With Maven

The build requires [Maven](http://maven.apache.org/download.html)
v3.0.5+ and the [Android SDK](http://developer.android.com/sdk/index.html) 19.0.0
to be installed in your development environment. In addition you'll need to set
the `ANDROID_HOME` environment variable to the location of your SDK, e.g.:

```bash
export ANDROID_HOME=/opt/tools/android-sdk
```

After satisfying those requirements, the build is pretty simple:

* Run `mvn clean install` from the root directory to run the integration tests, the formatter and to download all the dependencies.

Import the project to [IntelliJ 14](https://www.jetbrains.com/idea/) as a maven project.

To run on desktop launch [MokapDesktop.java](https://github.com/e-ucm/ead/blob/3d79a4677c15ccd3896f7bc6624f6cc70e41f7fa/editor/mokap/desktop/src/main/java/es/eucm/ead/editor/MokapDesktop.java) from the `mokap-desktop` module as a Java Application. 
On Android you need to launch the `mokap-android` module as an Android Application.

To have access to the assets of our repository you will need a key that you can obtain by contacting us at `contact@mokap.es`. Send us an e-mail asking for the key.

## Acknowledgements

This project uses the [libGDX](https://github.com/libgdx/libgdx) framework.

It also uses many other open source libraries such as:

* [android-maven-plugin](https://github.com/jayway/maven-android-plugin)
* [Universal Tween Engine](https://github.com/AurelienRibon/universal-tween-engine)
* [Ashley](https://github.com/libgdx/ashley)
* [JsonSchema2pojo](https://github.com/joelittlejohn/jsonschema2pojo)

These are just a few of the major dependencies, the rest of dependencies can be found in the `pom.xml` file of each module.

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/e-ucm/ead/pulls).

Any contributions, large or small, major features, bug fixes, additional
language translations, unit/integration tests are welcomed and appreciated
but will be thoroughly reviewed and discussed.

## More info.

* [Documentation](https://github.com/e-ucm/ead/wiki)
* [Developers blog](http://www.e-ucm.es/ead2blog/) With weekly news and updates about the eAdventure development
