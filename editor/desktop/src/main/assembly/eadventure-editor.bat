@ECHO OFF
REM
REM eAdventure is a research project of the
REM    e-UCM research group.
REM
REM    Copyright 2005-2014 e-UCM research group.
REM
REM    You can access a list of all the contributors to eAdventure at:
REM          http://e-adventure.e-ucm.es/contributors
REM
REM    e-UCM is a research group of the Department of Software Engineering
REM          and Artificial Intelligence at the Complutense University of Madrid
REM          (School of Computer Science).
REM
REM          CL Profesor Jose Garcia Santesmases 9,
REM          28040 Madrid (Madrid), Spain.
REM
REM          For more info please visit:  <http://e-adventure.e-ucm.es> or
REM          <http://www.e-ucm.es>
REM
REM ****************************************************************************
REM
REM  This file is part of eAdventure
REM
REM      eAdventure is free software: you can redistribute it and/or modify
REM      it under the terms of the GNU Lesser General Public License as published by
REM      the Free Software Foundation, either version 3 of the License, or
REM      (at your option) any later version.
REM
REM      eAdventure is distributed in the hope that it will be useful,
REM      but WITHOUT ANY WARRANTY; without even the implied warranty of
REM      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
REM      GNU Lesser General Public License for more details.
REM
REM      You should have received a copy of the GNU Lesser General Public License
REM      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
REM

REM ---------------------------------------------------------------------------
REM Environment Variable Prerequisites
REM
REM   Do not set the variables in this script. Instead put them into a script
REM   setenv.bat in CATALINA_BASE/bin to keep your customizations separate.
REM
REM   JAVA_HOME       Must point at your Java Development Kit installation.
REM                   Required to run the with the "debug" argument.
REM
REM   JDK_HOME        Must point at your Java Runtime installation.
REM                   Defaults to JAVA_HOME if empty. If JDK_HOME and JAVA_HOME
REM                   are both set, JDK_HOME is used.
REM
REM   JAVA_OPTS       (Optional) Java runtime options used when any command
REM                   is executed.


SETLOCAL

SET "EADVENTURE_HOME=%CD%"

REM Guess EADVENTURE_HOME if not defined
SET "CURRENT_DIR=%CD%"
IF NOT "%EADVENTURE_HOME%" == "" GOTO gotHome
SET "EADVENTURE_HOME=%CURRENT_DIR%"
:gotHome

IF EXIST "%EADVENTURE_HOME%\eadventure-editor.bat" GOTO okHome
ECHO The EADVENTURE_HOME environment variable is not defined correctly
ECHO This environment variable is needed to run this program
GOTO END
:okHome


REM Check if we have an embedded jdk
IF NOT EXIST "%EADVENTURE_HOME%\jdk" GOTO :checkJava
SET "JDK_HOME=%EADVENTURE_HOME%\jdk"

:checkJava
IF NOT "%JDK_HOME%" == "" GOTO gotjdkHome
IF NOT "%JAVA_HOME%" == "" GOTO gotJavaHome
ECHO Neither the JAVA_HOME nor the JDK_HOME environment variable are defined
ECHO At least one of these environment variable is needed to run this program
GOTO exit

:gotJavaHome
REM No jdk given, use JAVA_HOME as JDK_HOME
SET "JDK_HOME=%JAVA_HOME%"

:gotjdkHome
REM Check if we have a usable jdk
IF NOT EXIST "%JDK_HOME%\bin\java.exe" GOTO nojdkHome
IF NOT EXIST "%JDK_HOME%\bin\javaw.exe" GOTO nojdkHome
GOTO okJava

:nojdkHome
REM Needed at least a jdk
ECHO The JDK_HOME environment variable is not defined correctly
ECHO This environment variable is needed to run this program
GOTO exit

:okJava
REM Set standard command for invoking windowed Java Apps.
REM Note that NT requires a window name argument when using start.
REM Also note the quoting as JAVA_HOME may contain spaces.
SET _RUNJAVA="%JDK_HOME%\bin\javaw.exe"


REM Ensure that any user defined CLASSPATH variables are not used on startup,
SET CLASSPATH=

REM JVM options
SET JAVA_OPTS="%JAVA_OPTS% -Xms256m -Xmx512m -client"

REM Launch eAdventure

REM Get remaining unshifted command line arguments and save them in the
SET CMD_LINE_ARGS=
:setArgs
IF ""%1""=="""" GOTO doneSetArgs
SET CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
SHIFT
GOTO setArgs
:doneSetArgs

ECHO **************************************************************************
ECHO     Welcome to the eAdventure platform (${project.version}) !!
ECHO     You can get more info about this project at:
ECHO         http://e-adventure.e-ucm.es
ECHO     We hope you'll find the game editor useful. Please, do not hesitate
ECHO     to contact us for suggestions and bug reporting via e-mail:
ECHO         e-adventure@e-ucm.es
ECHO     You can access the whole list of contributors at:
ECHO         http://e-adventure.e-ucm.es/contributors/
ECHO                                          (C)2005-2014 e-UCM research group
ECHO  *************************************************************************

SET TITLE="eAdventure-Editor"
START "%TITLE%" "%_RUNJAVA%" %JAVA_OPTS% -jar "%EADVENTURE_HOME%\libs\${eadventure.main.jar}" %CMD_LINE_ARGS%

:exit
