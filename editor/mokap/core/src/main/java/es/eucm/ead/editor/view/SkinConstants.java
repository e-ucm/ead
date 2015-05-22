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
package es.eucm.ead.editor.view;

import com.badlogic.gdx.graphics.Color;

/**
 * Class with constant for icons
 */
public interface SkinConstants {

	String ENGINE_DEFAULT_FONT = "roboto-small";

	String COLOR_SEMI_BLACK = "black_semi";
	String COLOR_GRAY = "gray";

	String DRAWABLE_LOGO = "logo";
	String DRAWABLE_BLANK = "blank";
	String DRAWABLE_BUTTON = "button";
	String DRAWABLE_GRAY_100 = "gray_100_bg";
	String DRAWABLE_SEMI_BLANK = "blank75";
	String DRAWABLE_PAGE = "page";
	String DRAWABLE_PAGE_LEFT = "page_left";
	String DRAWABLE_PAGE_RIGHT = "page_right";
	String DRAWABLE_BLACK_BG = "black_bg";
	String DRAWABLE_LIGHT_GRAY_BG = "light_gray_bg";
	String DRAWABLE_TRANSPARENT_48 = "transparent_48_bg";
	String DRAWABLE_TEXT_FIELD = "text_field";
	String DRAWABLE_TOOLBAR = "toolbar";
	String DRAWABLE_BROWN_TOOLBAR = "brown_toolbar";
	String DRAWABLE_CUP = "cup";
	String DRAWABLE_PENCIL = "pencil";
	String DRAWABLE_SEMI_TRANSPARENT = "semi_transparent_bg";

	String STYLE_DEFAULT = "default";
	String STYLE_CONTEXT = "context";
	String STYLE_EDITION = "edition";
	String STYLE_COMPONENT = "component";
	String STYLE_TOOLBAR = "toolbar";
	String STYLE_DROP_DOWN = "drop_down";
	String STYLE_ADD = "add";
	String STYLE_SCENE = "scene";
	String STYLE_CIRCLE = "circle";
	String STYLE_SECONDARY_CIRCLE = "secondary_circle";
	String STYLE_NAVIGATION_SCENE = "navigation_scene";
	String STYLE_NAVIGATION = "navigation";
	String STYLE_SLIDER_PAGES = "pages";
	String STYLE_CHECK = "check";
	String STYLE_GRAY = "gray";
	String STYLE_MARKER = "marker";
	String STYLE_TOAST_ACTION = "toast_action";
	String STYLE_RADIO_CHECKBOX = "default_radio";
	String STYLE_CONTEXT_RADIO = "context_radio";
	String STYLE_PERFORMANCE = "performance";
	String STYLE_SELECTION = "selection";
	String STYLE_DIALOG = "dialog";
	String STYLE_ORANGE = "orange";
	String STYLE_TEMPLATE = "template";
	String STYLE_CATEGORY = "category";
	String STYLE_BIG = "big";

	String TABLET_GAME = "tablet_game";
	String TABLET_CARD = "tablet_card";
	String TABLET_PRESENTATION = "tablet_presentation";
	String TABLET_ACCURATE_SELECT = "tablet_accurate_selection";
	String TABLET_INSERT = "tablet_insert";
	String TABLET_ZONE = "tablet_zone";
	String TABLET_MULTIPLE_SELECT = "tablet_multiple_selection";
	String TABLET_PLAY = "tablet_play";

	String MOKAP_LOGO = "mokap_logo";
	String MOKAP_ACCURATE_SELECT = "mokap_accurate_selection";
	String MOKAP_COMPOSE = "mokap_compose";
	String MOKAP_ZONE = "mokap_zone";
	String MOKAP_MULTIPLE_SELECT = "mokap_multiple";
	String MOKAP_CUP = "mokap_cup";

	String IC_MOKAP = "ic_mokap";
	String IC_ADD_MOKAP = "ic_add_mokap";

	String IC_MENU = "ic_menu";
	String IC_ADD = "ic_add";
	String IC_MORE = "ic_more";

	String IC_PASTE = "ic_paste";
	String IC_BRUSH = "ic_brush";
	String IC_RUBBER = "ic_rubber";
	String IC_CIRCLE = "ic_circle";
	String IC_CAMERA = "ic_camera";
	String IC_CLOUD = "ic_cloud";
	String IC_TEXT = "ic_text";
	String IC_ZONE = "ic_zone";
	String IC_CLOUD_DONE = "ic_cloud_done";
	String IC_ERROR = "ic_error";
	String IC_PHOTO = "ic_photo";
	String IC_SOUND = "ic_sound";
	String IC_MUSIC = "ic_music";

	String IC_SINGLE_SELECTION = "ic_single_selection";
	String IC_MULTIPLE_SELECTION = "ic_multiple_selection";

	String IC_GROUP = "ic_group";
	String IC_UNGROUP = "ic_ungroup";

	String IC_EDIT = "ic_edit";

	String IC_COPY = "ic_copy";
	String IC_TO_BACK = "ic_to_back";
	String IC_TO_FRONT = "ic_to_front";
	String IC_SEND_TO_BACK = "ic_send_to_back";
	String IC_BRING_TO_FRONT = "ic_bring_to_front";
	String IC_MIRROR_VERTICAL = "ic_mirror_vertical";
	String IC_MIRROR_HORIZONTAL = "ic_mirror_horizontal";
	String IC_DELETE = "ic_delete";

	String IC_COMPOSE = "ic_compose";
	String IC_FX = "ic_fx";
	String IC_SHARE = "ic_share";
	String IC_PLAY = "ic_play";

	String IC_HOME = "ic_home";

	String IC_CHECK = "ic_check";
	String IC_CLOSE = "ic_close";
	String IC_CLONE = "ic_clone";
	String IC_UNDO = "ic_undo";
	String IC_REDO = "ic_redo";
	String IC_GO = "ic_go";
	String IC_TOUCH = "ic_touch";
	String IC_LINK = "ic_link";
	String IC_SEARCH = "ic_search";
	String IC_BLUR_LINEAR = "ic_blur_linear";
	String IC_BLUR = "ic_blur";

	String IC_ONE = "ic_one";
	String IC_FIT = "ic_fit";

	String IC_ARROW_UP = "ic_arrow_up";
	String IC_ARROW_DOWN = "ic_arrow_down";

	String COLOR_BROWN_MOKA = "brown_moka";
	String COLOR_GRAY_100 = "gray_100";

	String IC_EASE_LINEAR = "ic_ease_linear";
	String IC_EASE_IN_CUBIC = "ic_ease_in_cubic";
	String IC_EASE_OUT_CUBIC = "ic_ease_out_cubic";
	String IC_EASE_IN_OUT_CUBIC = "ic_ease_in_out_cubic";
	String IC_EASE_IN_BOUNCE = "ic_ease_in_bounce";
	String IC_EASE_OUT_BOUNCE = "ic_ease_out_bounce";

	String IC_YOYO = "ic_yoyo";
	String IC_NO_YOYO = "ic_no_yoyo";

	String IC_REPEAT = "ic_repeat";
	String IC_REPLAY = "ic_replay";
	String IC_SPEED = "ic_speed";
	String IC_SPEED_POINTER = "ic_speed_pointer";

	String IC_ALPHA = "ic_alpha";
	String IC_VOLUME = "ic_volume";

	String IC_SEGMENT_END = "ic_segment_end";
	String IC_SEGMENT_MIDDLE = "ic_segment_middle";
	String IC_ARROW = "ic_arrow";
	String IC_ADD_SCENE = "ic_add_scene";
	String IC_ADD_SOUND = "ic_add_sound";

	String IC_RANDOM = "ic_random";

	String IC_WHITE_MOKAP = "ic_white_mokap";
	String IC_IMAGE = "ic_image";

	String IC_REFRESH = "ic_refresh";

	String IC_VISIBILITY = "ic_visibility";
	String IC_TAG = "ic_tag";

	// Categories color
	Color COLOR_MOKAPS = Color.DARK_GRAY;
	Color COLOR_IMAGES = Color.MAROON;
	Color COLOR_SOUNDS = Color.TEAL;

	String IMG_EMPTY_BG = "emptybg.png";

}
