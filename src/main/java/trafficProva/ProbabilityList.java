package trafficProva;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
/**
 * Classe che costruisce un insieme che restituisce un elemento basato sulle sue probabilità di uscita
 * @author vava5
 */
public class ProbabilityList {
	/**
	 * Array di direzioni
	 */
	protected List<CoppiaDirProb> direzioni;
	/**
	 * Cotruttore
	 */
	protected ProbabilityList() 
	{
		direzioni = new ArrayList<CoppiaDirProb>();
	}
	/**
	 * Metodo che aggiunge una coppia all'array
	 * @param coppia
	 */
	protected void add(CoppiaDirProb coppia) {direzioni.add(coppia);}
	/**
	 * Metodo che restituisce unadirezione casuale seguendo le probabilità
	 * @return
	 */
	protected String getDir()
	{
		int sommaProb = direzioni.stream().mapToInt(m->m.getProb()).reduce(0,Integer::sum);//somma delle probabilità delle direzioni ottenibili
		int indDirScelta = ThreadLocalRandom.current().nextInt(sommaProb);//numero scelto che indicherà la direzione
		int sum = 0; //somma temporanea
        int i=0; //indice temporaneo
        while(sum < indDirScelta ) { //scegli l'indice della direzione
             sum = sum + direzioni.get(i++).getProb();
        }
        return direzioni.get(Math.max(0,i-1)).getDir(); //ritorna la direzione
     
	}
	/**
	 * Metodo che verifica se una direzione è già presente
	 * @param coppiaDaCercare
	 * @return
	 */
	protected boolean isDirIn(CoppiaDirProb coppiaDaCercare)
	{
		for(CoppiaDirProb coppia:direzioni)
		{
			if(coppia.getDir()==coppiaDaCercare.getDir())return true;
		}
		return false;
	}
	/**
	 * Metodo che svuota la ProbabilityList
	 */
	protected void clear() {direzioni.clear();}
	/**
	 * Metodo toString()
	 * @return string
	 */
	@Override
	public String toString() {return "{"+direzioni.stream().map(m->m.toString()).collect(Collectors.joining(","))+"}";}

}
