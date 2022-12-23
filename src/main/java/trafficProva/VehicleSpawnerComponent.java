package trafficProva;


import com.almasb.fxgl.entity.component.Component;

import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.*;

import java.util.concurrent.ThreadLocalRandom;
/**
 * Classe che gestisce i comportamenti degli spawner dei veicoli
 * @author vava5
 */
public class VehicleSpawnerComponent extends Component{
	
	/**
	 * Variabile q
	 */
	private int q;
	/**
	 * Variabile booleana che indica se lo spawner ha già spawnato entità durante la partita o no
	 */
	protected boolean firstUsed;
	/**
	 * Metodo che sceglie cosa spawnare
	 */
	protected void chooseSpawn()
	{
		int num = ThreadLocalRandom.current().nextInt(5);
		if(num==1) spawnMotorCycles();
		else if(num==2) spawnCamion();
		else spawnCars();
	}
	/**
	 * Metodo che spawna le macchine
	 */
	private void spawnCars()
	{
		spawn("Macchina", entity.getPosition());
	}
	/**
	 * Metodo che spawna mototrini
	 */
	private void spawnMotorCycles()
	{
		if(entity.getRotation()==270||entity.getRotation()==90) spawn("Moto", new Point2D(entity.getX()+30,entity.getY()));
		else if(entity.getRotation()==0||entity.getRotation()==180) spawn("Moto", new Point2D(entity.getX(),entity.getY()+10));
		else spawn("Moto", entity.getPosition());
	}
	/**
	 * Metodo che spawna camion
	 */
	private void spawnCamion()
	{
		if(entity.getRotation()==90||entity.getRotation()==270) spawn("Camion", new Point2D(entity.getX()-20,entity.getY()));
		else spawn("Camion", entity.getPosition());
	}
	/**
	 * Aumenta q
	 */
	protected void add() {q = q+1;}
	/**
	 * Diminuisce q
	 */
	protected void subtract() {q = q-1;}
	/**
	 * Restituisce q
	 * @return int q
	 */
	protected int getQ() {return q;}

}
