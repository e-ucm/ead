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
package es.eucm.ead.repobuilder.libs;

import es.eucm.ead.repobuilder.RepoLibraryBuilder;
import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;
import es.eucm.ead.schema.editor.components.repo.licenses.RepoLicense;

/**
 * Created by Javier Torrente on 25/09/14.
 */
public class MockupIconsLib extends RepoLibraryBuilder {
	public MockupIconsLib() {
		super("mockup_icons");
	}

	private static String[] es = {
			"Activo;Botón,seleccionado,destacar,resaltar,efecto,naranja",
			"Checkbutton desactivado;pequeño rectángulo,negro,selección",
			"Checkbutton con foco desactivado;rectángulo pequeño,negro,selección",
			"Check verde;selección,seleccionado,checkbox",
			"Check verde con foco;selección,checkbox,seleccionado",
			"Radio button sin seleccionar;Círculo pequeño,negro",
			"Radio button con foco sin seleccionar;Círculo pequeño,negro",
			"Radio button seleccionado;verde,círculo", "Campo de texto 1",
			"Campo de texto 2", "Campo de texto 3",
			"Galería del sistema;Android,negro",
			"Info;Acerca de,sobre,información;negro", "Atrás;flecha,negro",
			"Parpadear;gradiente,efecto,negro", "Cámara;fotografía,negro",
			"Copiar;duplicar,clonar,negro",
			"Selector de color;paleta,pintar;blanco",
			"Conversación;hablar,cómic,negro",
			"Incrementar;redimensionar,aumentar,negro",
			"Efectos;imagen,brillo", "Primero;blanco,ganador",
			"Entrando en puerta;negro", "Saliendo de puerta;negro",
			"Casa;principal,negro", "Mover horizontal;flecha,negro",
			"Incrementar;redimensionar,aumentar,negro",
			"Interactivo;táctil,tocar,touch,área,negro",
			"Cargando;cargar,círculos,naranja", "Candado;bloquear,negro",
			"Menú;negro", "Otros;tres puntos,negro",
			"Brocha;dibujar,lápiz,pintar,crear,negro",
			"Pegar;negro,clipboard,portapapeles,negro",
			"Lápiz;dibujar,escribir,crear,negro",
			"Probar escena;naranja,play,reproducir",
			"Probar;naranja,play,reproducir",
			"Papelera;borrar,eliminar,quitar,negro", "Rehacer;negro,flecha",
			"Eliminar;rojo,x,borrar,quitar",
			"Reordenar A-Z;filtrar,buscar,ascendente,flecha,negro",
			"Reordenar Z-A;filtrar,buscar,descendente,flecha,negro",
			"Repositorio;descargar,nube,cloud,negro", "Rotar;flecha,360,negro",
			"Borrador;goma,borrar,negro", "Buscar;lupa,filtrar",
			"Opciones;configuración,propiedades,engranaje,negro",
			"Compartir;naranja", "Sonido;altavoz,escuchar,música,negro",
			"Texto;negro", "Al fondo;delante,negro", "Al frente;detrás,negro",
			"Animar;estado,transición,negro", "Deshacer;flecha,negro",
			"Variable;programación,programar,código,negro",
			"Mover verticalmente;flecha,negro", "Visibilidad;ojo,negro,ver",
			"Cámara;foto,color", "Info;naranja,ayuda,información",
			"Nuevo proyecto;añadir,crear,negro", "Nueva escena;añadir,gris",
			"Brocha coloreada;dibujar,pincel,lápiz,crear,pintar",
			"Repositorio;nube,naranja,descargar,cloud",
			"Galería del sistema coloreada;Android,naranja" };

	private static String[] en = {
			"Active;Button,selected,highlight,effect,orange",
			"Checkbutton off;little rectangle,black",
			"Checkbutton focused off;little rectangle,black",
			"Green check;selected,checkbox",
			"Focused green check;selected,checkbox",
			"Unchecked radio button;Little circle,black",
			"Unchecked focused radio button;Little circle,black",
			"Checked radio button;Little circle,green", "Text field 1",
			"Text field 2", "Text field 3", "Android gallery;black",
			"Info;about,information;black", "Back arrow;black",
			"Blink;gradient,effect,black", "Camera;photo,black",
			"Copy;duplicate,clone,black", "Color picker;palette,paint;white",
			"Conversation;speak,talk,bubble,black",
			"Increase;resize,augment,black", "Effects;image effects,shinning",
			"First;white,winner", "Entering Gateway;door,black",
			"Exiting Gateway;door,black", "Home;house,black",
			"Move horizontally;arrow,black", "Increase;resize,black",
			"Interactive;touch,area,black", "Loading;circles,orange",
			"Lock;black", "Menu;black", "Others;three points,black",
			"Brush;draw,pencil,paint,create,black",
			"Paste;black,clipboard,black", "Pencil;draw,write,create,black",
			"Play scene;orange", "Play;orange",
			"Paper bin;recycle,delete,remove,black", "Redo;black,arrow",
			"Remove;red,x,delete", "Reorder A-Z;filter,search,arrow,black",
			"Reorder Z-A;filter,search,arrow,black",
			"Repository;download,cloud,fetch,black", "Rotate;arrow,360,black",
			"Rubber;erase,black", "Search;find,magnifying glass,look up",
			"Settings;engine,black", "Share;orange",
			"Sound;speaker,hear,music,black", "Text;black", "To back;black",
			"To front;black", "Animate;state,transition,black",
			"Undo;arrow,black", "Variable;programming,code,black",
			"Move vertically;arrow,black", "Visibility;eye,black,see",
			"Camera;photo,color", "Info;orange,help,information",
			"New project;add,create,black", "New scene;add,grey",
			"Colored Brush;draw,pencil,create,paint",
			"Repository;cloud,orange,download,fetch",
			"Colored Android Gallery;orange" };

	private static String[] files = { "button_active.9.png",
			"button_check_off.png", "button_check_off_focused.png",
			"button_check_on.png", "button_check_on_focused.png",
			"button_radio_off.png", "button_radio_off_focused.png",
			"button_radio_on.png", "textfield_default.9.png",
			"textfield_disabled.9.png", "textfield_focused.9.png",
			"_0000_Android-Gallery.png", "_0001_About.png", "_0002_Back.png",
			"_0003_Blink.png", "_0004_Camera.png", "_0005_Clone.png",
			"_0006_Color-Picker.png", "_0007_Conversation.png",
			"_0008_Increase.png", "_0009_Effects.png", "_0010_First.png",
			"_0011_Gateway.png", "_0012_Gateway-reverse.png", "_0013_Home.png",
			"_0014_Horizontal-Move.png", "_0015_Increase.png",
			"_0016_Interactive.png", "_0017_Loading.png", "_0018_Lock.png",
			"_0019_Menu.png", "_0020_Others.png", "_0021_Pencil.png",
			"_0022_Paste.png", "_0023_Pencil.png", "_0026_Play-scene.png",
			"_0027_Play.png", "_0028_Paper-bin.png", "_0029_Redo.png",
			"_0030_Remove.png", "_0031_Reorder-AZ.png", "_0032_Reorder-ZA.png",
			"_0033_Repository.png", "_0034_Rotate.png", "_0035_Rubber.png",
			"_0036_Search.png", "_0037_Settings.png", "_0038_Share.png",
			"_0039_Sound.png", "_0040_Text.png", "_0041_toBack.png",
			"_0042_toFront.png", "_0043_Tween.png", "_0044_Undo.png",
			"_0045_Variable.png", "_0046_Vertical-Move.png",
			"_0047_Visibility.png", "_0048_Camera-Color.png", "_0049_Info.png",
			"_0050_New-project.png", "_0051_New-scene.png",
			"_0052_Pencil-color.png", "_0053_Repository-color.png",
			"_0054_Android-Gallery-color.png" };

	@Override
	protected void doBuild() {
		setCommonProperty(AUTO_IDS, "true");
		setCommonProperty(LIB_NAME, "old-mockup-icons");

		setCommonProperty(THUMBNAILS, "");
		setCommonProperty(RESOURCES, "");

		setCommonProperty(MAX_WIDTH, "120");
		setCommonProperty(MAX_HEIGHT, "120");
		setCommonProperty(TAGS,
				"eAdventure,eUCM,mockup,icon,mochap;icono,mochap");
		setCommonProperty(AUTHOR_NAME, "Antonio Calvo & Cristian Rotaru");
		setCommonProperty(LICENSE, DefaultLicenses.License.CC_BY.toString());
		setCommonProperty(PUBLISHER, "mokap");
		setCommonProperty(CATEGORIES, "elements-objects");

		for (int i = 0; i < files.length; i++) {
			String nameEn = en[i].split(";")[0];
			String tagsEn[] = en[i].split(";").length > 1 ? en[i].split(";")[1]
					.split(",") : new String[0];
			String nameEs = es[i].split(";")[0];
			String tagsEs[] = es[i].split(";").length > 1 ? es[i].split(";")[1]
					.split(",") : new String[0];

			repoEntity(nameEn, nameEs, "", "", files[i], files[i]);
			for (String tagEn : tagsEn) {
				tag(tagEn, "");
			}
			for (String tagEs : tagsEs) {
				tag("", tagEs);
			}
		}

		repoLib("Icons from Mochap app", "Iconos de la aplicación Mochap", "",
				"", null);
	}
}
