/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.engine.tests.renderers;

import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.actors.SceneElementActor;
import es.eucm.ead.engine.mock.MockGame;
import es.eucm.ead.engine.renderers.frameanimation.FrameAnimationRenderer;
import es.eucm.ead.schema.actions.ChangeRenderer;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.components.Bounds;
import es.eucm.ead.schema.renderers.Circle;
import es.eucm.ead.schema.renderers.Polygon;
import es.eucm.ead.schema.renderers.Rectangle;
import es.eucm.ead.schema.renderers.Renderer;
import es.eucm.ead.schema.renderers.frameanimation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Javier Torrente on 2/02/14 to test
 * {@link es.eucm.ead.schema.renderers.frameanimation.FrameAnimation}.
 * */

public class FrameAnimationTest {
	private MockGame mockGame;

	private GameLoop gameLoop;

	@Before
	public void setUp() {
		mockGame = new MockGame();
		gameLoop = mockGame.getGameLoop();
	}

    @Test
    public void testSimpleLinearNoLoopAnimation(){
        testSimpleLinearAnimation(false);
    }

    @Test
    public void testSimpleLinearLoopAnimation(){
        testSimpleLinearAnimation(true);
    }

    @Test
    public void testSimpleRandomAnimation(){
        int nSquares = 100;

        // Load the scene
        mockGame.act();

        //Build two identical sequences of 100 red squares of increasing size and duration
        List<Timed> frames = new ArrayList<Timed>();
        for (int i=0; i<nSquares; i++){
            Frame frame =buildFrame(buildSquare(i + 10, Color.RED), 1);
            frames.add(frame);
        }
        NextFrame nextFrame = buildNextFrame(NextFunction_Type.RANDOM, false);
        FrameAnimation frameAnimation1 = buildFrameAnimation(nextFrame, frames);
        FrameAnimation frameAnimation2 = buildFrameAnimation(nextFrame, frames);

        // Creates a scene element.
        SceneElement sceneElement = new SceneElement();
        sceneElement.setRenderer(frameAnimation1);
        // Adds sceneElement to the game and retrieves the reference to
        // SceneElementActor
        gameLoop.loadSceneElement(sceneElement);
        mockGame.act();

        SceneElementActor sceneElementActor = ((SceneElementActor) (gameLoop
                .getSceneElement(sceneElement)));
        FrameAnimationRenderer frameAnimationRenderer = (FrameAnimationRenderer)sceneElementActor.getRenderer();

        // Iterate through the 100 frames to record the sequence of frameSizes
        List<Float> sequence1=new ArrayList<Float>();
        for (int i=0; i<nSquares; i++){
            sequence1.add(frameAnimationRenderer.getHeight());
            sceneElementActor.act(1.0F);
        }

        // Now add a change renderer action to the scene element so the second frame animation is set
        ChangeRenderer action = new ChangeRenderer();
        action.setSetInitialRenderer(false);
        action.setNewRenderer(frameAnimation2);
        sceneElementActor.addAction(action);
        mockGame.act();

        // Iterate through the 100 frames again to compare if this second sequence is different from the previous one
        boolean different = false;
        for (int i=0; i<nSquares; i++){
            if (!sequence1.get(i).equals(frameAnimationRenderer.getHeight())){
                different = true; break;
            }
            sceneElementActor.act(1.0F);
        }

        assertTrue("Two sequences of random frames should be always different", different);
    }


    private void testSimpleLinearAnimation(boolean loop){
        int nSquares=100;

        // Load the scene
        mockGame.act();

        //Build the animation: a sequence of 100 red squares of increasing size and duration
        List<Timed> frames = new ArrayList<Timed>();
        for (int i=0; i<nSquares; i++){
            frames.add(buildFrame(buildSquare(i+10, Color.RED), i+1));
        }
        FrameAnimation frameAnimation = buildFrameAnimation(buildNextFrame(NextFunction_Type.LINEAR, loop), frames);
        // Creates a scene element.
        SceneElement sceneElement = new SceneElement();
        sceneElement.setRenderer(frameAnimation);
        // Adds sceneElement to the game and retrieves the reference to
        // SceneElementActor
        gameLoop.loadSceneElement(sceneElement);
        mockGame.act();

        SceneElementActor sceneElementActor = ((SceneElementActor) (gameLoop
                .getSceneElement(sceneElement)));
        FrameAnimationRenderer frameAnimationRenderer = (FrameAnimationRenderer)sceneElementActor.getRenderer();

        // Check frames change with appropriate timing
        for (int i=0; i<nSquares; i++){
            assertTrue("The size of the square must be: "+(i+10)+"; It is :"+frameAnimationRenderer.getHeight(), frameAnimationRenderer.getHeight()==i+10);
            sceneElementActor.act((i+1)/2.0F);
            assertTrue("The size of the square must still be: " + (i + 10), frameAnimationRenderer.getHeight() == i + 10);
            sceneElementActor.act((i + 1) / 2.0F);
        }

        for (int i=0; i<nSquares; i++){
            if (loop){
                // Check count restarts
                assertTrue("The size of the square must be: "+(i+10)+"; It is :"+frameAnimationRenderer.getHeight(), frameAnimationRenderer.getHeight()==i+10);
            } else {
                // Check last frame stays still - forever and ever
                assertTrue("The size of the last square must be"+nSquares+9+ "; It is :"+frameAnimationRenderer.getHeight(), frameAnimationRenderer.getHeight()==nSquares+9);
            }

            sceneElementActor.act(i+1);
        }
    }

    /**
     * Creates a square with the given size and Color using the Rectangle renderer
     * @param size  The size (bottom=0, left=0, top=size, right=size)
     * @param color The color {@link es.eucm.ead.engine.tests.renderers.FrameAnimationTest.Color}
     * @return  The rectangle
     */
    private Rectangle buildSquare (int size, Color color){

		Rectangle square = new Rectangle();
		Bounds bounds = new Bounds();
		bounds.setBottom(0);
		bounds.setTop(size);
		bounds.setLeft(0);
		bounds.setRight(size);
		square.setBounds(bounds);
		square.setPaint(buildPaint(color));

	    return square;
	}

    /**
     * Builds a circle with the given radius and color
     */
    private Circle buildCircle (int radius, Color color){

        Circle circle = new Circle();
        circle.setRadius(radius);
        circle.setPaint(buildPaint(color));
        return circle;
    }

    /**
     * Builds an equilateral triangle with the given side size and color
     */
    private Polygon buildTriangle(int size, Color color){
        Polygon polygon = new Polygon();
        polygon.setPaint(buildPaint(color));
        List<Float> points = new ArrayList<Float>();
        points.add(0.0F);points.add(0.0F);
        points.add((float)size/2);points.add((float)size);
        points.add((float)size);points.add(0.0F);
        polygon.setPoints(points);
        return polygon;
    }

    /**
     * Builds a frame with the given params.
     */
    private Frame buildFrame(Renderer delegate, float duration){
        Frame frame = new Frame();
        frame.setDelegateRenderer(delegate);
        frame.setDuration(duration);
        return frame;
    }

    /**
     * Builds an animation with the given timedFrames and just a null nextFrame function.
     * @param timedFrames   The frames to be added to the animation
     * @return  The frame animation
     */
    private FrameAnimation buildFrameAnimation(Timed... timedFrames){
        return buildFrameAnimation(null, timedFrames);
    }

    /**
     * Builds a frame animation with the given frames and nextFrame function
     */
    private FrameAnimation buildFrameAnimation(NextFrame function, Timed... timedFrames){

        List<Timed> frameList = new ArrayList<Timed>();
        for (Timed f:timedFrames){
            frameList.add(f);
        }

        return buildFrameAnimation(function, frameList);
    }

    private FrameAnimation buildFrameAnimation (NextFrame function, List<Timed> frameList){
        FrameAnimation animation = new FrameAnimation();
        animation.setFrames(frameList);
        animation.setNextframe(function);
        return animation;
    }

    /**
     * Builds a function for frame sequencing of the given type.
     * @param type  The type of the function(random or linear)
     * @param loop  If true, the animation will loop, if false it will end with the last frame. Only used if type==Linear
     * @return
     */
    private NextFrame buildNextFrame(NextFunction_Type type, boolean loop){
        NextFrame function = null;
        if (type==NextFunction_Type.LINEAR){
            function = new Linear();
            ((Linear)function).setLoop(loop);
        } else {
            function = new Random();
        }
        return function;
    }

    /**
     * Builds a basic paint for testing, using the given color as primary color for the content. The border is always set to black
     */
    private String buildPaint (Color color){
        switch (color){
            case RED: return "FF0000;000000";
            case GREEN: return "00FF00;000000";
            case BLUE: return "0000FF;000000";
            case WHITE: return "FFFFFF;000000";
            case BLACK:
            default: return "000000;000000";
        }
    }


    private enum Color {RED, GREEN, BLUE, WHITE, BLACK}

    private enum NextFunction_Type {RANDOM, LINEAR}
}
