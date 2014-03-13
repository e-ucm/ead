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
start javaw -Xms256m -Xmx512m -jar eadventure-editor.jar