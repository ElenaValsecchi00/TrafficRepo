package trafficProva;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

/**
 * Classe che gestisce i comportamenti dei veicoli
 * @author vava5
 */
public class CarComponent extends Component {
	/**
	 * Velocità della macchina
	 */
	private double speed;
    /**
     * Sta girando a sinistra
     */
    protected boolean isTurningLeft;
    /**
     * Sta girando a destra
     */
    protected boolean isTurningRight;
    /**
     * Sta andando avanti
     */
    protected boolean isMovingForward = true;
    /**
     * Variabile che incrementa fino ad arrivare a 90 gradi(svolta completata)
     */
    private double reach  = 0;
    /**
     * Rotazione da raggiungere
     */
    protected double goalRotation;
    /**
     * Ultimo incrocio che la macchina ha incontrato
     */
    protected Entity lastCrossEncountered;
    /**
     * Spawner associato
     */
    protected Entity associatedSpawner;
    /**
     * Boolean che indica se la macchina si è mossa per la prima volta
     */
    protected boolean isInMap = false;

    /**
     * Metodo onAdded che viene attivato Appena viene aggiunto il component all'entità
     */
    @Override
    public void onAdded()
    {
    	associatedSpawner = getGameWorld().getClosestEntity(entity, m->m.isType(TrafficType.VEHICLESPAWNERS)).get();//assegna spawner
    	entity.setRotation(associatedSpawner.getRotation());//assegna alla macchina la rotazione dello spawner
    	associatedSpawner.getComponent(VehicleSpawnerComponent.class).add();//aggiungi uno alla variabile q
    	goalRotation = entity.getRotation();//la rotazione da raggiungere è al momento quella della macchina
    }
    /**
     * Metodo onUpdate che aggiorna la velocità usando il time per frame
     */
    @Override
    public void onUpdate(double tpf)
    {
    	if(isInMap == false)
    	{
    		if(entity.getX()<FXGL.getAppWidth() && entity.getX()>0 && entity.getY()<FXGL.getAppHeight() && entity.getY()>0) {associatedSpawner.getComponent(VehicleSpawnerComponent.class).subtract();isInMap= true;}
    	}
    	int costSpeed = 240;
        speed = tpf * costSpeed;
        if(isMovingForward)forward();
        if(isTurningLeft)left(tpf,costSpeed);
        if(isTurningRight)right(tpf,costSpeed);  
    }
    /**
     * Metodo che seleziona i veicoli che stanno cercando di attraversare l'incrocio contemporaneamente
     * @param cross
     * @param rot
     * @return
     */
    protected HashSet<Entity> getConcurrentVehicles(Entity cross,int rot)
    {
    	return cross.getComponent(CrossComponent.class).macchineCheAttraversano.stream().filter(m->(int)m.getRotation()!=rot).collect(Collectors.toCollection(HashSet::new));
    }
    /**
     * Metodo che controlla quali sono i movimenti possibili all'incrocio
     * @param dirPoss
     */
    protected void checkPossibleMovement(Entity cross)
    {
    	HashMap<Integer,Entity> dirPoss = cross.getComponent(CrossComponent.class).incrociRaggiungibili;
    	CoppiaDirProb destra = new CoppiaDirProb("Destra",3);
		CoppiaDirProb avanti = new CoppiaDirProb("Avanti",4);
		CoppiaDirProb sinistra = new CoppiaDirProb("Sinistra",3);
    	int rot = (int)entity.getRotation();
    	rot = (rot>=-1&&rot<=1||rot>=359&&rot<=361)?0:(rot>=179&&rot<=181)?180:(rot>=269&&rot<=271||rot>=-91&&rot<=-89)?270:90;
    	HashSet<Entity> veicoliConcorrenti = getConcurrentVehicles(cross,rot);
    	ProbabilityList dirAccettate = new ProbabilityList();
    	if(!associatedSpawner.getComponent(VehicleSpawnerComponent.class).firstUsed) 
    	{
    		dirAccettate.add(avanti);
    		associatedSpawner.getComponent(VehicleSpawnerComponent.class).firstUsed=true;
    		movementDecision(dirAccettate);
    	}
    	else
    	{
    		//a seconda della rotazione della macchina vengono controllate le direzioni in cui è possibile andare
        	if(rot==0||rot==360)
        	{
        		if(dirPoss.get(90)!=null)dirAccettate.add(destra);
        		if(dirPoss.get(0)!=null)dirAccettate.add(avanti);
        		if(dirPoss.get(270)!=null)dirAccettate.add(sinistra);
        	}
        	if(rot==180||rot==-180)
        	{
        		if(dirPoss.get(90)!=null)dirAccettate.add(sinistra);
        		if(dirPoss.get(180)!=null)dirAccettate.add(avanti);
        		if(dirPoss.get(270)!=null)dirAccettate.add(destra);
        	}
        	if(rot==90||rot==-270)
        	{
        		if(dirPoss.get(0)!=null)dirAccettate.add(destra);
        		if(dirPoss.get(90)!=null)dirAccettate.add(avanti);
        		if(dirPoss.get(180)!=null)dirAccettate.add(sinistra);
        	}
        	
        	if(rot==270||rot==-90)
        	{
        		if(dirPoss.get(0)!=null)dirAccettate.add(destra);
        		if(dirPoss.get(270)!=null)dirAccettate.add(avanti);
        		if(dirPoss.get(180)!=null)dirAccettate.add(sinistra);
        	}
        	if(veicoliConcorrenti.size()!=0)
        	{
        		if(dirAccettate.isDirIn(destra)) {dirAccettate.clear();dirAccettate.add(destra);}
        		else if(dirAccettate.isDirIn(avanti)){dirAccettate.clear();dirAccettate.add(avanti);}
        	}
        	lastCrossEncountered = cross;
        	movementDecision(dirAccettate);
    	}
    	
    }
    /**
     * Metodo che sceglie se svoltare e in che direzione
     * @param dirPoss
     */
   private void movementDecision(ProbabilityList dirPoss)
    {
		String dir = dirPoss.getDir();
    	switch(dir)
    	{
    	case "Destra"-> {isTurningRight = true;goalRotation = entity.getRotation()+90;if(goalRotation>360)goalRotation -=360;}
    	case "Sinistra"-> {isTurningLeft = true;goalRotation = entity.getRotation()-90;if(goalRotation>360)goalRotation -=360;}
    	case "Avanti" -> {goalRotation = entity.getRotation();}
    	}
    }
    /**
	 * Vai avanti
	 */
	protected void forward()
	{
		double rotation = entity.getRotation();
		Vec2 dir = Vec2.fromAngle(rotation).mulLocal(speed);
		entity.translate(dir);	
	}
    
    /**
     * Metodo che gestisce la svolta a sinistra
     * @param timePerFrame
     * @param costSpeed
     */
    private void left(double timePerFrame, int costSpeed) 
    {	
		if(reach < 90) 
    	{
    		double costToReach = (costSpeed*0.5)/120;
    		double rotationToAdd = costToReach/timePerFrame;
    		reach+=rotationToAdd*timePerFrame;
    		entity.rotateBy(-rotationToAdd*timePerFrame);
    	}
    	else
    	{
    		reach = 0;   		
    		entity.setRotation(goalRotation);
    		isTurningLeft = false;
    	}

    }
    /**
     * Metodo che gestisce la svolta a destra
     * @param timePerFrame
     * @param costSpeed
     */
   private void right(double timePerFrame, int costSpeed) 
    {  
    	if(reach < 90) 
    	{
    		double costToReach = costSpeed/120;
    		double rotationToAdd = costToReach/timePerFrame;
    		reach+=rotationToAdd*timePerFrame;
    		entity.rotateBy(rotationToAdd*timePerFrame);
    	}
    	else
    	{
    		reach = 0;
    		entity.setRotation(goalRotation);
    		isTurningRight = false;
    	}  	
    }

}
