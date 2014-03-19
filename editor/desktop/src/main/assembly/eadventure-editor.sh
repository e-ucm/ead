#!/bin/sh
#
# eAdventure is a research project of the
#    e-UCM research group.
#
#    Copyright 2005-2014 e-UCM research group.
#
#    You can access a list of all the contributors to eAdventure at:
#          http://e-adventure.e-ucm.es/contributors
#
#    e-UCM is a research group of the Department of Software Engineering
#          and Artificial Intelligence at the Complutense University of Madrid
#          (School of Computer Science).
#
#          CL Profesor Jose Garcia Santesmases 9,
#          28040 Madrid (Madrid), Spain.
#
#          For more info please visit:  <http://e-adventure.e-ucm.es> or
#          <http://www.e-ucm.es>
#
# ****************************************************************************
#
#  This file is part of eAdventure
#
#      eAdventure is free software: you can redistribute it and/or modify
#      it under the terms of the GNU Lesser General Public License as published by
#      the Free Software Foundation, either version 3 of the License, or
#      (at your option) any later version.
#
#      eAdventure is distributed in the hope that it will be useful,
#      but WITHOUT ANY WARRANTY; without even the implied warranty of
#      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#      GNU Lesser General Public License for more details.
#
#      You should have received a copy of the GNU Lesser General Public License
#      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
#

echo "                     Welcome to the eAdventure platform (v${project-version})!!"
echo "    You can get more info about the project at:"
echo "                                     http://e-adventure.e-ucm.es"
echo "    We hope you will find the game editor useful. Please, do not hesitate"
echo "          to contact us for suggestions and bug reporting via e-mail:"
echo "                              e-adventure@e-ucm.es"
echo "    You can access the whole list of contributors at:"
echo "                                      http://e-adventure.e-ucm.es/contributors/"
echo "                                            (C)2005-2012 e-UCM research group"
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
