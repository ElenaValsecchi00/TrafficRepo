package trafficProva;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

/**
 * Classe che gestisce il Cross Component
 * @author vava5
 */
public class CrossComponent extends Component{
	/**
	 * Veicoli che stanno impegnando l'incrocio
	 */
	protected HashSet<Entity> macchineCheAttraversano = new HashSet<Entity>();
	/**
	 * Mappa incroci:incroci raggiungibili
	 */
	protected HashMap<Integer,Entity> incrociRaggiungibili = new HashMap<Integer,Entity>();
	/**
	 * Mappa incroci:semafori
	 */
	protected List<List<Entity>> semPerIncrocio; 
	/**
	 * Metodo che mappa incroci e semafori
	 */
	protected void mapFromCrossToLight()
	{
		List<Entity> semaforiRossi = getGameWorld().getEntitiesByType(TrafficType.REDLIGHTS);//Carica i semafori rossi
		List<Entity> semaforiVerdi = getGameWorld().getEntitiesByType(TrafficType.GREENLIGHTS);//Carica i semafori verdi
		//codice per mappare incroci e semafori	
		//semafori verdi
		List<Entity> semaforiVerdiCorr =  semaforiVerdi.stream()
				.filter(m->entity.getX()-250 <= m.getX() && m.getX()<= entity.getX()+250 && entity.getY()-250 <= m.getY() && m.getY() <= entity.getY()+250)
				.collect(Collectors.toList());
		//semafori rossi
		List<Entity> semaforiRossiCorr = semaforiRossi.stream()
				.filter(m->entity.getX()-250 <= m.getX() && m.getX()<= entity.getX()+250 && entity.getY()-250 <= m.getY() && m.getY() <= entity.getY()+250)
				.collect(Collectors.toList());
		//inserimento nella lista dei semafori
		semPerIncrocio = new ArrayList<List<Entity>>(Arrays.asList(semaforiVerdiCorr, semaforiRossiCorr));
		
	}

	/**
	 * Metodo che sceglie l'incrocio più vicino 
	 * @param possibleCrosses
	 * @param angolo
	 * @param originalCross
	 * @return Entity incrocio più vicino
	 */
	protected Entity closestCross(List<Entity> possibleCrosses,int angolo)
	{
		if(possibleCrosses.size()==0)return null;
		Entity target = possibleCrosses.get(0);
		//tra gli incroci raggiungibili nella direzione puntata sceglie quello più vicino
		for(Entity newCross: possibleCrosses)
		{
			boolean condition = switch(angolo)
			{
			case 270 ->(entity.getY()-newCross.getY() <entity.getY()-target.getY());
			case 90 ->(newCross.getY()-entity.getY() < target.getY()-entity.getY());
			case 0 ->(newCross.getX()-entity.getX() < target.getX()-entity.getX());
			case 180 ->(entity.getX()-newCross.getX() < entity.getX()-target.getX());
			default -> throw new IllegalArgumentException();
			};
			if(condition) target = newCross;
		}
        return target;
	}
	/**
	 * Metodo che individua le uscite raggiungibili e le associa a ogni incrocio
	 * @param cross
	 */
	protected void getExits()
	{
		List<Entity> exits = getGameWorld().getEntitiesByType(TrafficType.EXITS);
		//per ogni direzione della circonferenza attorno all'incrocio calcola l'uscita se presente, altrimenti inserisce un valore nullo nel dizionario
		if(incrociRaggiungibili.get(270)==null)incrociRaggiungibili.put(270, exits.stream().filter(m->m.getX() == entity.getX() && m.getY() < entity.getY()).findFirst().orElse(null));
		if(incrociRaggiungibili.get(90)==null)incrociRaggiungibili.put(90, exits.stream().filter(m->m.getX() == entity.getX() && m.getY() > entity.getY()).findFirst().orElse(null));
		if(incrociRaggiungibili.get(0)==null)incrociRaggiungibili.put(0, exits.stream().filter(m->m.getX() > entity.getX() && m.getY() == entity.getY()).findFirst().orElse(null));
		if(incrociRaggiungibili.get(180)==null)incrociRaggiungibili.put(180,exits.stream().filter(m->m.getX() < entity.getX() && m.getY() == entity.getY()).findFirst().orElse(null));
	}
	/**
	 * Metodo che assegna a ogni incrocio gli incroci che sono da lì raggiungibili 
	 * @param cross
	 * @return Mappa degli incroci e degli spostamenti possibili da loro
	 */
	protected void getCrosses()
	{
		List<Entity> crosses = getGameWorld().getEntitiesByType(TrafficType.CROSSES);
		//per ogni direzione della circonferenza attorno all'incrocio calcola l'incrocio più vicino
		incrociRaggiungibili.put(270, closestCross(crosses.stream().filter(m->m.getX() == entity.getX() && m.getY() < entity.getY()).collect(Collectors.toList()),270));
		incrociRaggiungibili.put(90, closestCross(crosses.stream().filter(m->m.getX() == entity.getX() && m.getY() > entity.getY()).collect(Collectors.toList()),90));
		incrociRaggiungibili.put(0, closestCross(crosses.stream().filter(m->m.getX() > entity.getX() && m.getY() == entity.getY()).collect(Collectors.toList()),0));
		incrociRaggiungibili.put(180, closestCross(crosses.stream().filter(m->m.getX() < entity.getX() && m.getY() == entity.getY()).collect(Collectors.toList()),180));
	}
	

}
