package es.eucm.ead.mockup.core.control.handlers;

import com.badlogic.gdx.Gdx;

import es.eucm.ead.mockup.core.facade.IAnswerListener;

public class MenuHandler extends ScreenHandler implements IAnswerListener{

	private boolean close;

	@Override
	public void act(float delta) {
		stage.act(delta);
	}

	@Override
	public void onBackKeyPressed() {
		if(!close){
			close = true;
			mockupController.getResolver().showDecisionBox(IAnswerListener.QUESTION_EXIT, "Salir", "¿Estás seguro?", 
					"Sí", "No", this); //TODO use I18N
		}
	}

	@Override
	public void onReceiveAnswer(int question, int answer) {
		if(question == IAnswerListener.QUESTION_EXIT){
			if (close) {
				if (answer == IAnswerListener.QUESTION_EXIT_ANSWER_YES) {
					Gdx.app.exit();
				} else {
					close = false;
				}
			}
		}		
	}

}
