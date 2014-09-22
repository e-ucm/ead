#!/bin/bash
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

PROGRAM="$0"
PROGRAMDIR=$(dirname "$PROGRAM")

# Only set EADVENTURE_HOME if not already set
if [ -z "$EADVENTURE_HOME" ]; then
	EADVENTURE_HOME=$(cd "$PROGRAMDIR" >/dev/null; pwd)
fi

# Check if we have an embedded jdk
if [ -d "$EADVENTURE_HOME/jdk" ]; then
	JDK_HOME="$EADVENTURE_HOME/jdk"
fi

if [ -z "$JDK_HOME" ]; then

	if [ ! -z "$JAVA_HOME" ]; then
		# No jdk given, use JAVA_HOME as JDK_HOME
		JDK_HOME=$JAVA_HOME
	else
		echo Neither the JAVA_HOME nor the JDK_HOME environment variable are defined
		echo At least one of these environment variable is needed to run this program
		exit 1
	fi
fi

# Check if we have a usable jdk
if [ ! -f "$JDK_HOME/bin/java" ]; then
	echo The JDK_HOME environment variable is not defined correctly
	echo This environment variable is needed to run this program
	exit 1
fi

# Set standard command for invoking windowed Java Apps.
# Also note the quoting as JAVA_HOME may contain spaces.
_RUNJAVA="$JDK_HOME/bin/java"

# Ensure that any user defined CLASSPATH variables are not used on startup,
# but allow them to be specified in setenv.sh, in rare case when it is needed.
CLASSPATH=

# JVM options
JAVA_OPTS="$JAVA_OPTS -Xms256m -Xmx512m -client"

echo "**************************************************************************"
echo "    Welcome to the eAdventure platform (${project.version})!!             "
echo "    You can get more info about this project at:                          "
echo "        http://e-adventure.e-ucm.es                                       "
echo "    We hope you'll find the game editor useful. Please, do not hesitate   "
echo "    to contact us for suggestions and bug reporting via e-mail:           "
echo "        e-adventure@e-ucm.es                                              "
echo "    You can access the whole list of contributors at:                     "
echo "        http://e-adventure.e-ucm.es/contributors/                         "
echo "                                         (C)2005-2014 e-UCM research group"
echo "**************************************************************************"

shift
exec "$_RUNJAVA" $JAVA_OPTS -jar "$EADVENTURE_HOME/libs/${eadventure.main.jar}" "$@"