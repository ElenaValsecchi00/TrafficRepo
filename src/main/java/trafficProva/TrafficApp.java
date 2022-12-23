/**
 * 
 */
package trafficProva;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.profile.SaveLoadHandler;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Main App
 * @author vava5
 */
public class TrafficApp extends GameApplication {
	/**
	 * Booleano che indica se la partita è stata persa
	 */
	private SimpleBooleanProperty gameIsEnding = new SimpleBooleanProperty(false);
	/**
	 * Metodo che identifica il giocatore 
	 * @return
	 */
	private Entity getPlayer() {return getGameWorld().getSingleton(TrafficType.PLAYER);}
	/**
	 * Metodo che controlla se è possibile passare all'incrocio
	 * @param semaforo
	 * @param car
	 * @param cross
	 * @return Boolean
	 */
	private boolean checkIfPossibleToPass(Entity semaforo, Entity car, Entity cross)
	{
		//Segna che questo incrocio è l'ultimo con cui la macchina è venuta in contatto
    	car.getComponent(CarComponent.class).lastCrossEncountered = cross;
    	//Controlla che il semaforo sia verde
    	List<Entity> semafori = cross.getComponent(CrossComponent.class).semPerIncrocio.get(0);
    	semaforo = semafori.get(0);
    	for(Entity e:semafori)
    	{
    		if(car.getCenter().distance(e.getCenter())<car.getCenter().distance(semaforo.getCenter()))semaforo = e;
    	}
    	//Se lo è e si ha la precedenza decidi il movimento
    	int rot = (int)car.getRotation();
    	rot = (rot>=-1&&rot<=1||rot>=359&&rot<=361)?0:(rot>=179&&rot<=181)?180:(rot>=269&&rot<=271||rot>=-91&&rot<=-89)?270:90;
    	HashSet<Entity> veicoliConcorrenti = car.getComponent(CarComponent.class).getConcurrentVehicles(cross,rot);
    	boolean isThereAnotherVehicle = false;
    	for(Entity veic:veicoliConcorrenti)
		{
    		double goalRot = veic.getComponent(CarComponent.class).goalRotation;
			if(goalRot==rot+90||goalRot==rot+90-360||goalRot==rot||goalRot==rot-360) {isThereAnotherVehicle = true;break;}
		}
    	return semaforo.isVisible()&&!isThereAnotherVehicle;
	}
	/**
	 * Metodo che gestisce la fisica
	 */
	@Override
	protected synchronized void initPhysics() {
	    getPhysicsWorld().addCollisionHandler(new CollisionHandler(TrafficType.CARS, TrafficType.CROSSES) {
	    	Entity semaforo;
	    	/**
	    	 * Metodo che gestisce le collisioni tra macchine e incroci
	    	 */
	        @Override
	        protected void onCollisionBegin(Entity car, Entity cross) 
	        {
	            if(checkIfPossibleToPass(semaforo,car,cross))
	            {   
	            	cross.getComponent(CrossComponent.class).macchineCheAttraversano.add(car);
	            	car.getComponent(CarComponent.class).checkPossibleMovement(cross);
	            }
	            //altrimenti fermati
	            else car.getComponent(CarComponent.class).isMovingForward=false;
	        }
	        @Override 
	        protected void onCollision(Entity car, Entity cross)
	        {
	        	//Controlla che il semaforo sia verde
	        	if(!car.getComponent(CarComponent.class).isMovingForward)
	        	{
		        	//Caso in cui il semaforo è verde e la macchina in questione ha la precedenza
	        		if(checkIfPossibleToPass(semaforo,car,cross))
	        		{
	        			cross.getComponent(CrossComponent.class).macchineCheAttraversano.add(car);
	        			car.getComponent(CarComponent.class).isMovingForward=true;
	        			car.getComponent(CarComponent.class).checkPossibleMovement(cross);
	        		}
	        	}
	        }
	        @Override
	        protected void onCollisionEnd(Entity car, Entity cross)
	        {
	        	cross.getComponent(CrossComponent.class).macchineCheAttraversano.remove(car);	        		        	
	        }
	       
	    });
	    /**
    	 * Metodo che gestisce le collisioni tra macchine e macchine
    	 */
	        getPhysicsWorld().addCollisionHandler(new CollisionHandler(TrafficType.CARS, TrafficType.CARS) {
		        @Override
		        protected void onCollisionBegin(Entity car1, Entity car2) 
		        {
		        	Entity carThatCollides = car1.getComponent(CarComponent.class).isMovingForward==false?car2:car1;
		        	Entity car = car1.getComponent(CarComponent.class).isMovingForward==false?car1:car2;
		        	if(carThatCollides.getRotation()==car.getRotation()-180||carThatCollides.getRotation()==car.getRotation()+180)getGameWorld().removeEntity(carThatCollides)
		        	;
		        	carThatCollides.getComponent(CarComponent.class).isMovingForward=false;
		        }
		        @Override 
		        protected synchronized void onCollisionEnd(Entity car1, Entity car2)
		        {
		        	Entity carThatCollides = car1.getComponent(CarComponent.class).isMovingForward==false?car1:car2;
		        	//dopo un secondo dalla partenza della macchina davanti parte anche quella dietro
		        	getGameTimer().runOnceAfter(() -> {
		        		try
		        		{
		        			carThatCollides.getComponent(CarComponent.class).isMovingForward=true;
		        		}
		        		catch(Exception e) {}
		        	}, Duration.seconds(1));        	
		        }
	        });
	        /**
	    	 * Metodo che gestisce le collisioni tra macchine e uscite
	    	 */
		        getPhysicsWorld().addCollisionHandler(new CollisionHandler(TrafficType.CARS, TrafficType.EXITS) {

			        @Override
			        protected void onCollisionBegin(Entity car, Entity exit) 
			        {	
			        	getGameWorld().getEntitiesByComponent(CarComponent.class).stream().forEach(m->m.getComponents().forEach(f->f.pause()));
			        	car.removeFromWorld();//despawna il veicolo
			        	getGameWorld().getEntitiesByComponent(CarComponent.class).stream().forEach(m->m.getComponents().forEach(f->f.resume()));
			        	getip("Score").set(getip("Score").get()+1);//aumenta il punteggio
			        }
			        
		        });
		    /**
		     * Metodo che gestisce le collisioni tra macchine e fuoristrada
	    	 */
		        getPhysicsWorld().addCollisionHandler(new CollisionHandler(TrafficType.CARS, TrafficType.OUTOFRAILS) {

			        @Override
			        protected void onCollisionBegin(Entity car, Entity fuoristrada) 
			        {	  
			        	getGameWorld().getEntitiesByComponent(CarComponent.class).stream().forEach(m->m.getComponents().forEach(f->f.pause()));
			        	car.removeFromWorld();//despawna il veicolo
			        	getGameWorld().getEntitiesByComponent(CarComponent.class).stream().forEach(m->m.getComponents().forEach(f->f.resume()));
			        }
			        
		        });
	   
	}
	/**
	 * Metodo che gestisce lo spawn delle macchine
	 */
	private void spawnHandler()
	{
		run(() -> getGameWorld().getEntitiesByComponent(VehicleSpawnerComponent.class).forEach(m->m.getComponent(VehicleSpawnerComponent.class).chooseSpawn()),Duration.seconds(6));
	}
	
	/**
	 * Metodo che gestisce lle impostazioni della finestra di gioco
	 */
	@Override
	protected void initSettings(GameSettings settings) 
	{
		settings.setTitle("Traffic");
		settings.setWidth(4250);
		settings.setHeight(2500);
		settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
        settings.setDeveloperMenuEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new MyMenu();
            }
        });
        settings.setMainMenuEnabled(true);
        settings.setFullScreenAllowed(true);
	}

	/**
	 * Metodo che gestisce il flow del gioco
	 */
	@Override
	protected void initGame()
	{
		getGameWorld().addEntityFactory(new TrafficFactory());//Carica la factory
		setLevelFromMap("Liv1.tmx");//Carica il livello
		getSaveLoadService().readAndLoadTask("save1.sav").run();//salva il punteggio
		getGameWorld().getEntitiesByType(TrafficType.GREENLIGHTS).stream().filter(m->m.getPropertyOptional("startsVisible").toString().contentEquals("Optional[false]")).forEach(m->m.setVisible(false));
		getGameWorld().getEntitiesByType(TrafficType.REDLIGHTS).stream().filter(m->m.getPropertyOptional("startsVisible").toString().contentEquals("Optional[false]")).forEach(m->m.setVisible(false));//Mette visibili i semafori che lo devono essere e viceversa
		for(Entity cross:getGameWorld().getEntitiesByType(TrafficType.CROSSES)) 
		{
			cross.getComponent(CrossComponent.class).mapFromCrossToLight();
			cross.getComponent(CrossComponent.class).getCrosses();
			cross.getComponent(CrossComponent.class).getExits();
		}//inizializza i semafori correlati agli incroci, gli incroci e i movimenti possibili
		spawnHandler();
		getGameWorld().getEntitiesByType(TrafficType.CARS).stream().forEach(m->m.getComponent(CarComponent.class).getEntity().setRotation(Math.round(m.getComponent(CarComponent.class).getEntity().getRotation()*10)/10));
	}
	/**
	 * Gestisce le variabili in gioco
	 */
    @Override
    protected void initGameVars(Map<String, Object> vars)
    {
        vars.put("Score", 0);
        vars.put("HighScore", 0);
        vars.put("q", 0);
    }
    /**
     * Metodo che quello che deve essere caricato prima del InitGame
     */
    @Override
    protected void onPreInit() {
        getSaveLoadService().addHandler(new SaveLoadHandler() {
            @Override
            public void onSave(DataFile data) {
                // create a new bundle to store your data
                var bundle = new Bundle("gameData");

                // store some data
                int highScore = geti("HighScore");
                bundle.put("HighScore", highScore);

                // give the bundle to data file
                data.putBundle(bundle);
            }

            @Override
            public void onLoad(DataFile data) {
                // get your previously saved bundle
                var bundle = data.getBundle("gameData");

                // retrieve some data
                int highScore = bundle.get("HighScore");

                // update your game with saved data
                set("HighScore", highScore);
            }
        });
    }
    /**
     * Metodo che gestisce la UI
     */
    @Override
    protected void initUI() {
    	//HighScore
    	var highScoreText = getUIFactoryService().newText("HighScore: ", Color.BLACK, 72.00);
        var highScorePoints = getUIFactoryService().newText("", Color.BLACK, 72.00);
        highScorePoints.textProperty().bind(getip("HighScore").asString());
        //Score
        var scoreText = getUIFactoryService().newText("Score: ", Color.BLACK, 72.00);
        var scorePoints = getUIFactoryService().newText("", Color.BLACK, 72.00);
        scorePoints.textProperty().bind(getip("Score").asString());
        //GameOver
        var gameOverText = getUIFactoryService().newText("GAME OVER", Color.BLACK, 300);
        //VarQ
        var qText = getUIFactoryService().newText("Q: ", Color.BLACK, 72.00);
        var qPoints = getUIFactoryService().newText("", Color.BLACK, 72.00);
        qPoints.textProperty().bind(getip("q").asString());
        //Gestione della visibilità
        gameOverText.visibleProperty().bind(
        		Bindings.when(gameIsEnding).then(true).otherwise(false));
        highScoreText.visibleProperty().bind(
        		Bindings.when(gameIsEnding).then(false).otherwise(true));
        scoreText.visibleProperty().bind(
        		Bindings.when(gameIsEnding).then(false).otherwise(true));
        highScorePoints.visibleProperty().bind(
        		Bindings.when(gameIsEnding).then(false).otherwise(true));
        scorePoints.visibleProperty().bind(
        		Bindings.when(gameIsEnding).then(false).otherwise(true));
        qText.visibleProperty().bind(
        		Bindings.when(gameIsEnding).then(false).otherwise(true));
        qPoints.visibleProperty().bind(
        		Bindings.when(gameIsEnding).then(false).otherwise(true));
        //Posizionamento del testo
        addUINode(highScorePoints, 1650, 950);
        addUINode(qText, 1600, 1050);
        addUINode(qPoints, 1700, 1050);
        addUINode(highScoreText, 1250, 950);
        addUINode(gameOverText, 1225, 1300);
        addUINode(scorePoints, 2250, 950);
        addUINode(scoreText, 2000, 950);
    }
	/**
	 * Metodo che gestisce gli input del giocatore
	 */
	@Override
	protected void initInput()
	{
		/**
		 * Puntatore va in alto
		 */
		getInput().addAction(new UserAction("Up") {
            @Override
            protected void onActionBegin() 
            {
            	Entity player = getPlayer();
            	if(player.getY()>0)
            	{
            		Entity target = getGameWorld().getClosestEntity(player, m->m.isType(TrafficType.CROSSES)).get().getComponent(CrossComponent.class).incrociRaggiungibili.get(270);
            		if(target!=null && !target.isType(TrafficType.EXITS))
            		{
            			player.setX(target.getX());
                		player.setY(target.getY());
            		}
            		
            	}
            }
        }, KeyCode.W);
		/**
		 * Puntatore va in basso
		 */
		getInput().addAction(new UserAction("Down") {
            @Override
            protected void onActionBegin() 
            {
            	Entity player = getPlayer();
            	if(player.getY() < 2250)
            	{
            		Entity target = getGameWorld().getClosestEntity(player, m->m.isType(TrafficType.CROSSES)).get().getComponent(CrossComponent.class).incrociRaggiungibili.get(90);;
	        		if(target!=null && !target.isType(TrafficType.EXITS))
	        		{
	        			player.setX(target.getX());
	            		player.setY(target.getY());
	        		}
        		
            	}	
            }
        }, KeyCode.S);
		/**
		 * Puntatore va a destra
		 */
		getInput().addAction(new UserAction("Right") {
            @Override
            protected void onActionBegin() 
            {
            	Entity player = getPlayer();
            	if(player.getX() < 4000)
            	{
            		Entity target = getGameWorld().getClosestEntity(player, m->m.isType(TrafficType.CROSSES)).get().getComponent(CrossComponent.class).incrociRaggiungibili.get(0);
            		if(target!=null && !target.isType(TrafficType.EXITS))
            		{
            			player.setX(target.getX());
                		player.setY(target.getY());
            		}
            		
            	}
            }
        }, KeyCode.D);
		
		/**
		 * Puntatore va a sinistra
		 */
		getInput().addAction(new UserAction("Left") {
            @Override
            protected void onActionBegin() 
            {
            	Entity player = getPlayer();
            	if(player.getX() > 0)
            	{
            		Entity target = getGameWorld().getClosestEntity(player, m->m.isType(TrafficType.CROSSES)).get().getComponent(CrossComponent.class).incrociRaggiungibili.get(180);
            		if(target!=null && !target.isType(TrafficType.EXITS))
            		{
            			player.setX(target.getX());
                		player.setY(target.getY());
            		}
            		
            	}        	
            }
        }, KeyCode.A);
        /**
         * Cambio colore dei semafori
         */
		getInput().addAction(new UserAction("Switchlights")
		{
			@Override
			protected void onActionBegin()
			{
				Entity player = getPlayer();
				Entity incrocio = getGameWorld().getClosestEntity(player,m->m.isType(TrafficType.CROSSES)).get();
				incrocio.getComponent(CrossComponent.class).semPerIncrocio.get(0).stream().forEach(m->m.setVisible(m.isVisible()?false:true));
				incrocio.getComponent(CrossComponent.class).semPerIncrocio.get(1).stream().forEach(m->m.setVisible(m.isVisible()?false:true));
				
			}
		}, KeyCode.SPACE);
		
	}
	/**
	 * Metodo che controlla se è il caso di finire il gioco
	 */
	protected void EndGame()
	{
		int q = 0;
		for(Entity entity:getGameWorld().getEntitiesByComponent(VehicleSpawnerComponent.class))
		{
			if(entity.getComponent(VehicleSpawnerComponent.class).getQ() > q)q = entity.getComponent(VehicleSpawnerComponent.class).getQ();
			if(entity.getComponent(VehicleSpawnerComponent.class).getQ() >= 3)
			{
				if(getip("Score").get()>getip("HighScore").get())getip("HighScore").set(getip("Score").get());
				getSaveLoadService().saveAndWriteTask("save1.sav").run();
				gameIsEnding.set(true);
				getGameTimer().runOnceAfter(() -> {
	        		try
	        		{
	        			getGameController().gotoMainMenu();
	        		}
	        		catch(Exception e) {}
	        	}, Duration.seconds(5));      
			} 
		}
		getip("q").set(q);//la viariabile q viene aggiornata con quella maggiore tra tutti i punti di spawn
		
	}
	/**
	 * Metodo onUpdate
	 */
	@Override
	public synchronized void onUpdate(double tpf)
	{
		EndGame();
	}
	/**
	 * Metodo che lancia il gioco
	 * @param args
	 */
	public static void main(String[] args)
	{
		launch(args);
	}

}
