#!/bin/sh
echo "                     Welcome to the eAdventure platform (v${project.version})!!"
echo "    You can get more info about the project at:"
echo "                                     http://e-adventure.e-ucm.es"
echo "    We hope you will find the game editor useful. Please, do not hesitate"
echo "          to contact us for suggestions and bug reporting via e-mail:"
echo "                              e-adventure@e-ucm.es"
echo "    You can access the whole list of contributors at:"
echo "                                      http://e-adventure.e-ucm.es/contributors/"
echo "                                            (C)2005-2014 e-UCM research group"
PROGRAM="$0"
PROGRAMDIR=`dirname "$PROGRAM"`
[ -z "$EADVENTURE_HOME" ] && EADVENTURE_HOME=`cd "$PROGRAMDIR"; pwd`"/eadventure"
JAVA_OPTS="$JAVA_OPTS -Xms256m -Xmx512m"
_RUNJAVA=`which java`
if [ -z "$JAVA_HOME" -a -z "$_RUNJAVA" ]; then
 echo "JAVA_HOME environment variable is configured properly and java command is not available";
fi;

if [ -z "$_RUNJAVA" ]; then
  _RUNJAVA="$JAVA_HOME/bin/java"
fi;

OLDPWD=`pwd`
cd "$EADVENTURE_HOME"
exec "$_RUNJAVA" $JAVA_OPTS -jar "libs/${eadventure.main.jar}"
cd $OLDPWD
