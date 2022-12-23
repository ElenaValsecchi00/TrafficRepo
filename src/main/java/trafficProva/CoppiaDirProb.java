/**
 * 
 */
package trafficProva;

/**
 * Classe che costruisce una coppia Stringa, Intero che permette di assegnare a ogni direzione che pu� prendere la macchina una probabilit� diversa
 * @author vava5
 */
public class CoppiaDirProb {
	/**
	 * Direzione
	 */
	protected String direzione;
	/**
	 * Probabilit�
	 */
	protected int probabilit�;
	/**
	 * Costruttore
	 * @param direzione
	 * @param probabilit�
	 */
	protected CoppiaDirProb(String direzione, int probabilit�) 
	{
		this.direzione = direzione;
		this.probabilit� = probabilit�;
	}
	/**
	 * Metodo che ritorna la probabilit�
	 * @return int
	 */
	protected int getProb() {return probabilit�;}
	/**
	 * Metodo che ritorna la direzione
	 * @return string
	 */
	protected String getDir() {return direzione;}
	/**
	 * Metodo che imposta la probabilit�
	 * @param prob
	 */
	protected void setProb(int prob) {probabilit� = prob;}
	/**
	 * Metodo che imposta la direzione
	 * @param dir
	 */
	protected void setDir(String dir) {direzione = dir;}
	/**
	 * Metodo toString()
	 */
	@Override
	public String toString() {return "("+direzione+","+probabilit�+")";}

}
