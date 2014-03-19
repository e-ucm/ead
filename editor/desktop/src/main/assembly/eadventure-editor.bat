@REM
@REM eAdventure is a research project of the
@REM    e-UCM research group.
@REM
@REM    Copyright 2005-2014 e-UCM research group.
@REM
@REM    You can access a list of all the contributors to eAdventure at:
@REM          http://e-adventure.e-ucm.es/contributors
@REM
@REM    e-UCM is a research group of the Department of Software Engineering
@REM          and Artificial Intelligence at the Complutense University of Madrid
@REM          (School of Computer Science).
@REM
@REM          CL Profesor Jose Garcia Santesmases 9,
@REM          28040 Madrid (Madrid), Spain.
@REM
@REM          For more info please visit:  <http://e-adventure.e-ucm.es> or
@REM          <http://www.e-ucm.es>
@REM
@REM ****************************************************************************
@REM
@REM  This file is part of eAdventure
@REM
@REM      eAdventure is free software: you can redistribute it and/or modify
@REM      it under the terms of the GNU Lesser General Public License as published by
@REM      the Free Software Foundation, either version 3 of the License, or
@REM      (at your option) any later version.
@REM
@REM      eAdventure is distributed in the hope that it will be useful,
@REM      but WITHOUT ANY WARRANTY; without even the implied warranty of
@REM      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
@REM      GNU Lesser General Public License for more details.
@REM
@REM      You should have received a copy of the GNU Lesser General Public License
@REM      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
@REM

ECHO OFF
ECHO ********************************************************************************
ECHO                      Welcome to the eAdventure platform (v${project.version})!!  
ECHO     You can get more info about this project at: 
ECHO                                      http://e-adventure.e-ucm.es
ECHO
ECHO     We hope you'll find the game editor useful. Please, do not hesitate  
ECHO           to contact us for suggestions and bug reporting via e-mail:  
ECHO                               e-adventure@e-ucm.es 
ECHO     
ECHO     You can access the whole list of contributors at:
ECHO                                        http://e-adventure.e-ucm.es/contributors/                                 
ECHO
ECHO                                             (C)2005-2014 e-UCM research group
ECHO  *******************************************************************************
cd eadventure
start javaw -Xms256m -Xmx512m -jar libs\${eadventure.main.jar}
