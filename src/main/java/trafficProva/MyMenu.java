/**
 * 
 */
package trafficProva;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;

import static com.almasb.fxgl.dsl.FXGL.*;

import javafx.beans.binding.StringBinding;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Classe costruttrice del main menu
 * @author vava5
 */
public class MyMenu extends FXGLMenu {

	/**
	 * Costruttore Menu
	 * @param type
	 */
	public MyMenu() {
		super(MenuType.MAIN_MENU);
	
		TrafficButton newGameBtn = new TrafficButton("New Game", ()->fireNewGame());
		TrafficButton quitBtn = new TrafficButton("Quit", ()->fireExit());
		
		var box = new VBox(30, newGameBtn, quitBtn);
		box.setTranslateX(100);
		box.setTranslateY(1500);
		box.setAlignment(Pos.CENTER_LEFT);
		
		getContentRoot().getChildren().addAll(box);
	}
	/**
	 * Override di funzione che crea un bottone
	 * @param String name
	 * @param Runnable action
	 * @return Button 
	 */
	@Override
	protected Button createActionButton(String name, Runnable action) {
		return new Button();
	}
	/**
	 * Override di funzione che crea un bottone 
	 * @param StringBinding name
	 * @param Runnable action
	 * @return Button 
	 */
	@Override
	protected Button createActionButton(StringBinding name, Runnable action) {
		return new Button();
	}
	/**
	 * Override di funzione che crea il background
	 * @param double width
	 * @param double height
	 * @return Node 
	 */
	@Override
	protected Node createBackground(double width, double height) {	
		return texture("background/MenuBackground.png");
	}
	/**
	 * Override di funzione che crea un profileView
	 * @param String arg0
	 * @return Node
	 */
	@Override
	protected Node createProfileView(String arg0) {
		return new Rectangle();
	}
	/**
	 * Override di funzione che crea un titleView
	 * @param String title
	 * @return Node 
	 */
	@Override
	protected Node createTitleView(String title) {
		return new Rectangle();
	}
	/**
	 * Override di funzione che crea un versioneView
	 * @param String arg0
	 * @return Node 
	 */
	@Override
	protected Node createVersionView(String arg0) {
		return new Rectangle();
	}
	/**
	 * Classe che modella i bottoni
	 * @author vava5
	 */
	private static class TrafficButton extends StackPane{
		
		/**
		 * Nome del bottone
		 */
		private String name;
		/**
		 * Azione legata al bottone
		 */
		private Runnable action;
		/**
		 * Testo
		 */
		private Text text;
		/**
		 * Selector 
		 */
		private Rectangle selector;
		/**
		 * Costruttore bottone
		 * @param name
		 * @param action
		 */
		public TrafficButton(String name, Runnable action)
		{
			this.name = name;
			this.action = action;
			
			//Impostazioni di testi e selector
			text = getUIFactoryService().newText(name, Color.BLACK, 128.0);
			selector = new Rectangle(10, 128, Color.BLACK);
			selector.setTranslateX(-25);
			selector.visibleProperty().bind(focusedProperty());
			
			//Impostazioni di visione e focus
			setAlignment(Pos.CENTER_LEFT);
			setFocusTraversable(true);
			
			//Azione al click
			setOnKeyPressed(m-> {
				if(m.getCode() == KeyCode.ENTER)action.run();
			});
			//Aggiungi componenti al bottone
			getChildren().addAll(selector,text);
		}
	}
}
