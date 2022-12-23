/**
 * 
 */
package trafficProva;

/**
 * Classe che costruisce una coppia Stringa, Intero che permette di assegnare a ogni direzione che può prendere la macchina una probabilità diversa
 * @author vava5
 */
public class CoppiaDirProb {
	/**
	 * Direzione
	 */
	protected String direzione;
	/**
	 * Probabilità
	 */
	protected int probabilità;
	/**
	 * Costruttore
	 * @param direzione
	 * @param probabilità
	 */
	protected CoppiaDirProb(String direzione, int probabilità) 
	{
		this.direzione = direzione;
		this.probabilità = probabilità;
	}
	/**
	 * Metodo che ritorna la probabilità
	 * @return int
	 */
	protected int getProb() {return probabilità;}
	/**
	 * Metodo che ritorna la direzione
	 * @return string
	 */
	protected String getDir() {return direzione;}
	/**
	 * Metodo che imposta la probabilità
	 * @param prob
	 */
	protected void setProb(int prob) {probabilità = prob;}
	/**
	 * Metodo che imposta la direzione
	 * @param dir
	 */
	protected void setDir(String dir) {direzione = dir;}
	/**
	 * Metodo toString()
	 */
	@Override
	public String toString() {return "("+direzione+","+probabilità+")";}

}
