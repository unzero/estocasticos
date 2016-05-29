package mensajes;

import java.math.BigInteger;
import java.util.LinkedList;

import red.Nodo;

public class Criptoanalisis implements Mensaje {
	
	private LinkedList<BigInteger> identidades;
	private Nodo destino;
	
	public Criptoanalisis(LinkedList<BigInteger> id,Nodo dt){
		identidades = id;
		destino = dt;
	}
	
	public LinkedList<BigInteger> obtenerIdentidades(){
		return identidades;
	}
	
	public Nodo obtenerDestino(){
		return (Nodo)destino.clone();
	}

	@Override
	public String obtenerTipo() {
		return "CRIPTOANALISIS";
	}

}
