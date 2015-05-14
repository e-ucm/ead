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

import es.eucm.ead.editor.importer.EAdventure1XGame;
import es.eucm.ead.editor.importer.EAdventure1XLoader;
import es.eucm.ead.engine.components.renderers.StatesActor;
import es.eucm.ead.repobuilder.RepoLibraryBuilder;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.State;
import es.eucm.ead.schema.renderers.States;
import es.eucm.eadventure.common.data.animation.Animation;
import es.eucm.eadventure.common.data.animation.Frame;
import es.eucm.eadventure.common.data.chapter.Chapter;
import es.eucm.eadventure.common.data.chapter.elements.*;
import es.eucm.eadventure.common.data.chapter.resources.Resources;
import es.eucm.eadventure.common.data.chapter.scenes.Scene;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class to produce resource libraries from ead 1.x games. Only resources
 * are imported - no recursiveness. This way, items and npcs references in
 * scenes are not imported into the scene - just the backgrounds.
 * 
 * Names, tags and descriptions are obtained from ead1.x documentation fields.
 * See
 * {@link #processEAd1XElementWithResources(es.eucm.ead.editor.importer.EAdventure1XGame, es.eucm.ead.repobuilder.libs.EAd1XLibBuilder.EAd1XElementWithResources)}
 * for more details on how each model piece is processed and imported.
 * 
 * Created by Javier Torrente on 29/09/14.
 */
public abstract class EAd1XLibBuilder extends RepoLibraryBuilder {

	/*
	 * String constants used to identify ead 1.x resources that do not have to
	 * be imported
	 */
	public static final String EMPTY_ANIMATION = "EmptyAnimation";
	public static final String EMPTY_IMAGE = "EmptyImage";
	public static final String EMPTY_ICON = "EmptyIcon";
	public static final String EMPTY_BACKGROUND = "EmptyBackground";

	/**
	 * Creates the object but does not actually build the game. Just creates the
	 * temp folder and unzips the the contents of the file specified by the
	 * relative path {@code root}
	 * 
	 * @param root
	 */
	public EAd1XLibBuilder(String root) {
		super(root);
		setCommonProperty(RESOURCES, "");
		setCommonProperty(THUMBNAILS, "");
		setCommonProperty(MAX_HEIGHT, "600");
		setCommonProperty(AUTO_IDS, "true");
		setCommonProperty(PUBLISHER, "eAdventure");
		setCommonProperty(LICENSE, DefaultLicenses.License.CC_BY.toString());

	}

	// ////////////////////////////////////////////////////////////
	// Abstract methods - subclasses have to implement these
	// ////////////////////////////////////////////////////////////
	/**
	 * @return Name of the repo library, in a user-friendly format, in English
	 *         (E.g. "Finding a Job")
	 */
	protected abstract String getLibraryNameEn();

	/**
	 * @return Name of the repo library, in a user-friendly format, in Spanish
	 *         (E.g. "Recursos del Juego 1492")
	 */
	protected abstract String getLibraryNameEs();

	@Override
	protected void doBuild() {
		// Load game
		EAdventure1XLoader loader = new EAdventure1XLoader();
		EAdventure1XGame game = loader.load(rootFolder.path());
		// Process chapters
		for (Chapter chapter : game.getAdventureData().getChapters()) {
			// Process scenes
			for (Scene scene : chapter.getScenes()) {
				try {
					processEAd1XElementWithResources(game,
							EAd1XElementWithResources.build(scene));
				} catch (Throwable t) {
					System.err.println("ERROR processing scene: "
							+ scene.getId());
					t.printStackTrace();
				}
			}

			// Process atrezzo
			for (Atrezzo atrezzo : chapter.getAtrezzo()) {
				try {
					processEAd1XElementWithResources(game,
							EAd1XElementWithResources.build(atrezzo));
				} catch (Throwable t) {
					System.err.println("ERROR processing atrezzo: "
							+ atrezzo.getId());
					t.printStackTrace();
				}
			}

			// Process items
			for (Item item : chapter.getItems()) {
				try {
					processEAd1XElementWithResources(game,
							EAd1XElementWithResources.build(item));
				} catch (Throwable t) {
					System.err
							.println("ERROR processing item: " + item.getId());
					t.printStackTrace();
				}
			}

			// Process player
			try {
				processEAd1XElementWithResources(game,
						EAd1XElementWithResources.build(chapter.getPlayer()));
			} catch (Throwable t) {
				System.err.println("ERROR processing player");
				t.printStackTrace();
			}

			// Process NPCs
			for (NPC npc : chapter.getCharacters()) {
				try {
					processEAd1XElementWithResources(game,
							EAd1XElementWithResources.build(npc));
				} catch (Throwable t) {
					System.err.println("ERROR processing npc: " + npc.getId());
					t.printStackTrace();
				}
			}
		}

		repoLib("Resources from the eAdventure 1.X game '" + getLibraryNameEn()
				+ "'", "Recursos obtenidos del juego hecho con eAdventure '"
				+ getLibraryNameEs() + "'", "", "", null);
	}

	/**
	 * Generates name, description and tags from the eAdventure1.X game. It can
	 * either be defined in the Documentation section, using format:
	 * 
	 * <pre>
	 * NameEN;NameES#tag1;tag2;tag3...;tagN#DescriptionEN;DescriptionES
	 * </pre>
	 * 
	 * Or in the Descriptions group (Only first description group considered):
	 * 
	 * <pre>
	 *     eAd1xName -> NameEn;NameEs
	 *     eAd1xDescription -> tag1;tag2;...;tagN
	 *     eAd1xDetailedDescription -> DescriptionEn;DescriptionEs
	 * </pre>
	 * 
	 * Then, all existing ead1.x resources in eAd1XElementWithResources are
	 * converted to states. Priority of resources is taken into account to
	 * determine the default state. If ead1.x appearance blocks have names, then
	 * it is expected to be a comma-separated tag list for the state:
	 * 
	 * <pre>
	 *     statetag1;statetag2;...statetagN
	 * </pre>
	 * 
	 * So for example, if an eAd1X character has two different appearances, one
	 * standing the other sitting, this can be noted in the name of each
	 * appearance so in the ead2.0 game different appearences can be referred
	 * to. For each eAd1x resource, default state tags are also added, depending
	 * on the type of the resource. So, for example, walk animations are added a
	 * "walk" and "andar" tags. For more details, see
	 * {@link #stateTag(java.util.List, es.eucm.ead.schema.renderers.State, String)}
	 * .
	 * 
	 * Thumbnails are also produced automatically for each eAd1.x resource. If
	 * the resource is a static image, then it is used to generate the
	 * thumbnail, if it is an animation, the first frame is used.
	 * 
	 * @param game
	 *            The eAd1X game model being processed
	 * @param eAd1XElementWithResources
	 *            Current piece of model being processed (a character, an item,
	 *            etc.)
	 * @return This object, for chaining calls
	 */
	protected RepoLibraryBuilder processEAd1XElementWithResources(
			EAdventure1XGame game,
			EAd1XElementWithResources eAd1XElementWithResources) {
		String nameEn = "", nameEs = "", descriptionEs = "", descriptionEn = "", thumbnail = null;
		List<String> parsedTags = new ArrayList<String>();
		States states = new States();

		// Generate names, descriptions, tags
		String name = null, tags = null, description = null;
		// First, check documentation. If not present, try to read from
		// name,description and full description.
		if (eAd1XElementWithResources.getDocumentation() != null
				&& eAd1XElementWithResources.getDocumentation().length() > 0) {
			String documentation = eAd1XElementWithResources.getDocumentation();
			if (documentation != null) {
				String[] data = documentation.split("#");
				if (data.length > 0) {
					name = data[0];
				}
				if (data.length > 1) {
					tags = data[1];
				}
				if (data.length > 2) {
					description = data[2];
				}
			}
		} else if (eAd1XElementWithResources.getDescriptions().size() > 0) {
			name = eAd1XElementWithResources.getDescriptions().get(0).getName();
			tags = eAd1XElementWithResources.getDescriptions().get(0)
					.getDescription();
			description = eAd1XElementWithResources.getDescriptions().get(0)
					.getDetailedDescription();
		}

		if (name == null || name.length() == 0 || !name.contains(";")) {
			logError(eAd1XElementWithResources, "Has no valid name: " + name);
		} else {
			nameEn = name.split(";")[0];
			nameEs = name.split(";")[1];
		}

		if (description == null || description.length() == 0
				|| !description.contains(";")) {
			logError(eAd1XElementWithResources, "Has no valid description: "
					+ description);
		} else {
			descriptionEn = description.split(";")[0];
			descriptionEs = description.split(";")[1];
		}

		if (tags == null || tags.length() == 0 || !tags.contains(";")) {
			logError(eAd1XElementWithResources, "Has no valid tags: " + tags);
		} else {
			for (String tag : tags.split(";")) {
				parsedTags.add(tag);
			}
		}

		// Convert resources into states
		String[] priorizedResources = eAd1XElementWithResources
				.getPriorizedResources();

		boolean defaultSelected = false;
		for (int resourceIndex = 0; resourceIndex < eAd1XElementWithResources
				.getResources().size(); resourceIndex++) {
			Resources resources = eAd1XElementWithResources.getResources().get(
					resourceIndex);
			List<String> stateCustomTags = new ArrayList<String>();
			if (resources.getName() != null && resources.getName().length() > 0
					&& resources.getName().length() > 0) {
				for (String customTag : resources.getName().split(";")) {
					stateCustomTags.add(customTag);
					parsedTags.add(customTag);
				}
			}
			for (int i = 0; i < priorizedResources.length; i++) {
				String assetType = priorizedResources[i];
				if (resources.existAsset(assetType)) {
					String assetValue = resources.getAssetPath(assetType);
					if (isValidResource(resources, assetType)) {
						State state = makeStateFromResource(game, assetValue);
						for (String customTag : stateCustomTags) {
							state.getStates().add(customTag);
						}
						states.getStates().add(state);
						stateTag(parsedTags, state, assetType);
						if (!defaultSelected) {
							state.getStates()
									.add(StatesActor.DEFAULT_STATE_TAG);
							defaultSelected = true;
							if (assetValue.toLowerCase().endsWith("png")
									|| assetValue.toLowerCase().endsWith("jpg")
									|| assetValue.toLowerCase()
											.endsWith("jpeg")) {
								thumbnail = assetValue;
							} else if (assetValue.endsWith("_01")) {
								thumbnail = assetValue + ".png";
							} else if (assetValue.toLowerCase()
									.endsWith(".eaa")) {
								thumbnail = game.getAnimations()
										.get(assetValue).getFrame(0).getUri();
							}
						}
					}
				}
			}
		}

		repoEntity(nameEn, nameEs, descriptionEn, descriptionEs, thumbnail,
				null);
		for (RepoCategories rc : eAd1XElementWithResources.getCategories()) {
			category(rc);
		}
		getLastEntity().getComponents().add(states);
		for (int i = 0; i < parsedTags.size(); i += 2) {
			if (i < parsedTags.size() - 1) {
				tag(parsedTags.get(i), parsedTags.get(i + 1));
			} else {
				tag(parsedTags.get(i), "");
			}
		}

		return this;
	}

	/**
	 * Adds default tags to a list of state tags, depending on the type of
	 * ead1.x resource being processed
	 */
	private void stateTag(List<String> parsedTags, State state, String assetType) {
		List<String> tags = new ArrayList<String>();
		if (assetType.contains(Scene.RESOURCE_TYPE_BACKGROUND)) {
			a(tags, "background", "scene", "fondo", "escena");
		}

		if (assetType.toLowerCase().equals(Item.RESOURCE_TYPE_IMAGE)) {
			a(tags, "image", "static", "imagen", "estático", "idle", "reposo");
		} else if (assetType.toLowerCase().equals(Item.RESOURCE_TYPE_IMAGEOVER)) {
			a(tags, "image", "static", "imagen", "estático", "over", "encima",
					"sobre");
		}

		if (assetType.contains("stand")) {
			a(tags, "standing", "stand", "idle", "quieto", "parado");
		} else if (assetType.contains("speak")) {
			a(tags, "speaking", "hablando", "speak", "talk", "hablar",
					"conversar", "talking", "conversando", "dialogando",
					"conversation", "conversación");
		} else if (assetType.contains("walk")) {
			a(tags, "walk", "walking", "andar", "andando", "caminar",
					"caminando");
		} else if (assetType.contains("use")) {
			a(tags, "use", "grab", "using", "grabbing", "use with",
					"using with", "give", "giving", "take", "taking", "usar",
					"usando", "agarrar", "agarrando", "manipular",
					"manipulando");
		}

		if (assetType.contains("left")) {
			a(tags, "left", "hacia la izquierda");
		} else if (assetType.contains("right")) {
			a(tags, "right", "hacia la derecha");
		} else if (assetType.contains("up")) {
			a(tags, "up", "hacia arriba", "back", "de espalda");
		} else if (assetType.contains("down")) {
			a(tags, "down", "hacia abajo", "de frente");
		}

		for (String tag : tags) {
			state.getStates().add(tag);
			parsedTags.add(tag);
		}
	}

	private void a(List<String> list, String... tags) {
		for (String tag : tags) {
			if (!list.contains(tag)) {
				list.add(tag);
			}
		}
	}

	/**
	 * @return True if
	 * @param assetType
	 *            is a valid, non-dafault ead1.x resource, false otherwise.
	 */
	protected boolean isValidResource(Resources resources, String assetType) {
		if (!resources.existAsset(assetType)) {
			return false;
		}

		String assetPath = resources.getAssetPath(assetType);
		if (assetPath == null
				|| assetPath.length() == 0
				|| assetPath.toLowerCase().contains(
						EMPTY_ANIMATION.toLowerCase())
				|| assetPath.toLowerCase().contains(
						EMPTY_BACKGROUND.toLowerCase())
				|| assetPath.toLowerCase().contains(EMPTY_IMAGE.toLowerCase())
				|| assetPath.toLowerCase().contains(EMPTY_ICON.toLowerCase())) {
			return false;
		}
		return true;
	}

	private void logError(Object element, String message) {
		try {
			String id = null;
			if (element instanceof Player) {
				id = "Player";
			} else {
				Method method = element.getClass().getDeclaredMethod("getId");
				id = (String) method.invoke(element);
			}
			System.err.println("[Error in element " + id + "]. " + message);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts the given ead1.x resource
	 * 
	 * @param resourcePath
	 *            (defined through its relative path in the ead1.x game
	 * @param eAdventure1XGame
	 *            ) into a {@link State}. The resulting State will contain a
	 *            {@link Frames} animation. If it is a still image, then only a
	 *            frame is added.
	 */
	protected State makeStateFromResource(EAdventure1XGame eAdventure1XGame,
			String resourcePath) {

		State state = new State();

		Frames frames = new Frames();
		frames.setSequence(Frames.Sequence.LINEAR);
		state.setRenderer(frames);

		// Create frames
		Animation animation = eAdventure1XGame.getAnimations()
				.get(resourcePath);
		if (animation != null) {
			for (Frame frame : animation.getFrames()) {
				es.eucm.ead.schema.renderers.Frame newFrame = new es.eucm.ead.schema.renderers.Frame();
				newFrame.setTime(frame.getTime() / 1000F);
				Image image = createImage(frame.getUri());
				newFrame.setRenderer(image);
				frames.getFrames().add(newFrame);
			}
		} else {
			es.eucm.ead.schema.renderers.Frame newFrame = new es.eucm.ead.schema.renderers.Frame();
			newFrame.setTime(1000F);
			Image image = createImage(resourcePath);
			newFrame.setRenderer(image);
			frames.getFrames().add(newFrame);
		}
		return state;
	}

	/**
	 * Wrapper for any piece of the ead1.x model
	 */
	private abstract static class EAd1XElementWithResources {

		// To generate names, descriptions, tags
		public abstract List<Description> getDescriptions();

		public abstract String getDocumentation();

		// To get its resources, for processing
		public abstract List<Resources> getResources();

		/**
		 * @return A list with the types of assets of this type of resource
		 *         (e.g. Scene, Item, NPC, etc.). The order each type of
		 *         resource has in the list determines its priority in order to
		 *         become the default state. So, if for example, this method
		 *         returns ["walkup", "standright"], when processing the element
		 *         any resource in the first appearance block of type "walkup"
		 *         will be setup as default. If no "walkup" resource is present,
		 *         then "standright" will be used (if present).
		 */
		public abstract String[] getPriorizedResources();

		/**
		 * @return List of categories this model piece must be catalogued into
		 */
		public abstract RepoCategories[] getCategories();

		public static EAd1XElementWithResources build(Object obj) {
			if (obj instanceof Element) {
				return new EAd1XElement((Element) obj);
			} else if (obj instanceof Scene) {
				return new EAd1XScene((Scene) obj);
			}
			throw new IllegalArgumentException("Class not supported: "
					+ obj.getClass().getName());
		}
	}

	private static class EAd1XScene extends EAd1XElementWithResources {

		private Scene scene;

		public EAd1XScene(Scene scene) {
			this.scene = scene;
		}

		@Override
		public List<Description> getDescriptions() {
			return new ArrayList<Description>();
		}

		@Override
		public String getDocumentation() {
			return scene.getDocumentation();
		}

		@Override
		public List<Resources> getResources() {
			return scene.getResources();
		}

		@Override
		public String[] getPriorizedResources() {
			return new String[] { Scene.RESOURCE_TYPE_BACKGROUND };
		}

		@Override
		public RepoCategories[] getCategories() {
			return new RepoCategories[] { RepoCategories.SCENES_BACKGROUNDS };
		}

	}

	private static class EAd1XElement extends EAd1XElementWithResources {

		private Element element;

		public EAd1XElement(Element element) {
			this.element = element;
		}

		@Override
		public List<Description> getDescriptions() {
			return element.getDescriptions();
		}

		@Override
		public String getDocumentation() {
			return element.getDocumentation();
		}

		@Override
		public List<Resources> getResources() {
			return element.getResources();
		}

		@Override
		public String[] getPriorizedResources() {
			if (element instanceof NPC) {
				return new String[] { NPC.RESOURCE_TYPE_STAND_RIGHT,
						NPC.RESOURCE_TYPE_STAND_LEFT,
						NPC.RESOURCE_TYPE_STAND_DOWN,
						NPC.RESOURCE_TYPE_SPEAK_RIGHT,
						NPC.RESOURCE_TYPE_SPEAK_LEFT,
						NPC.RESOURCE_TYPE_SPEAK_DOWN,
						NPC.RESOURCE_TYPE_WALK_RIGHT,
						NPC.RESOURCE_TYPE_WALK_LEFT,
						NPC.RESOURCE_TYPE_WALK_DOWN,
						NPC.RESOURCE_TYPE_USE_RIGHT,
						NPC.RESOURCE_TYPE_USE_LEFT, NPC.RESOURCE_TYPE_STAND_UP,
						NPC.RESOURCE_TYPE_SPEAK_UP, NPC.RESOURCE_TYPE_WALK_UP };
			} else if (element instanceof Item) {
				return new String[] { Item.RESOURCE_TYPE_IMAGE,
						Item.RESOURCE_TYPE_IMAGEOVER };
			} else if (element instanceof Atrezzo) {
				return new String[] { Atrezzo.RESOURCE_TYPE_IMAGE };
			}
			return new String[0];
		}

		@Override
		public RepoCategories[] getCategories() {
			if (element instanceof NPC) {
				return new RepoCategories[] { RepoCategories.ELEMENTS,
						RepoCategories.ELEMENTS_CHARACTERS };
			} else {
				return new RepoCategories[] { RepoCategories.ELEMENTS,
						RepoCategories.ELEMENTS_OBJECTS };
			}
		}
	}

}
