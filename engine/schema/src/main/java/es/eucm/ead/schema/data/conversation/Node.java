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

package es.eucm.ead.schema.data.conversation;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import es.eucm.ead.schema.effects.Effect;

/**
 * A line of conversation, spoken (or produced) by a participant. May include a
 * condition and/or an effect.
 * 
 */
@Generated("org.jsonschema2pojo")
public class Node {

	/**
	 * ID of the line. Used to create a conversation graph. Unique within the
	 * conversation.
	 * 
	 */
	private int id;
	/**
	 * Index of speaker, from the speaker array defined when the conversation
	 * started
	 * 
	 */
	private int speaker = 0;
	/**
	 * The expression that serves as condition (default: null for 'no
	 * condition'). If not null and evaluates to false, this line is not
	 * available
	 * 
	 */
	private String condition = null;
	/**
	 * i18n keys of text said by speaker as part of this node. If audio
	 * renderings exist, the same key can be used to look them up. Same for
	 * possible associated images.
	 * 
	 */
	private List<String> lines = new ArrayList<String>();
	/**
	 * Effects define events that affects/changes the game state.
	 * 
	 */
	private Effect effect;
	/**
	 * IDs of lines that can be used to reply to this line
	 * 
	 */
	private List<Integer> outgoing = new ArrayList<Integer>();

	/**
	 * ID of the line. Used to create a conversation graph. Unique within the
	 * conversation.
	 * 
	 */
	public int getId() {
		return id;
	}

	/**
	 * ID of the line. Used to create a conversation graph. Unique within the
	 * conversation.
	 * 
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Index of speaker, from the speaker array defined when the conversation
	 * started
	 * 
	 */
	public int getSpeaker() {
		return speaker;
	}

	/**
	 * Index of speaker, from the speaker array defined when the conversation
	 * started
	 * 
	 */
	public void setSpeaker(int speaker) {
		this.speaker = speaker;
	}

	/**
	 * The expression that serves as condition (default: null for 'no
	 * condition'). If not null and evaluates to false, this line is not
	 * available
	 * 
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * The expression that serves as condition (default: null for 'no
	 * condition'). If not null and evaluates to false, this line is not
	 * available
	 * 
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

	/**
	 * i18n keys of text said by speaker as part of this node. If audio
	 * renderings exist, the same key can be used to look them up. Same for
	 * possible associated images.
	 * 
	 */
	public List<String> getLines() {
		return lines;
	}

	/**
	 * i18n keys of text said by speaker as part of this node. If audio
	 * renderings exist, the same key can be used to look them up. Same for
	 * possible associated images.
	 * 
	 */
	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	/**
	 * Effects define events that affects/changes the game state.
	 * 
	 */
	public Effect getEffect() {
		return effect;
	}

	/**
	 * Effects define events that affects/changes the game state.
	 * 
	 */
	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	/**
	 * IDs of lines that can be used to reply to this line
	 * 
	 */
	public List<Integer> getOutgoing() {
		return outgoing;
	}

	/**
	 * IDs of lines that can be used to reply to this line
	 * 
	 */
	public void setOutgoing(List<Integer> outgoing) {
		this.outgoing = outgoing;
	}

}
