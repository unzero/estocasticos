package agentes;

import java.math.BigInteger;

import red.Nodo;
import sistema.Mensaje;

public class Criptoanalizar implements Mensaje {

	private BigInteger identidad;
	private BigInteger sospechoso;
	private BigInteger coprimo;
	private Nodo origen;
	
	public Criptoanalizar(BigInteger id,BigInteger cop,BigInteger sos,Nodo org){
		identidad = id;
		coprimo = cop;
		sospechoso = sos;
		origen = org;
	}
	
	public BigInteger obtenerSospechoso(){
		return sospechoso.add(BigInteger.ZERO);
	}
	
	public BigInteger obtenerIdentidad(){
		return identidad.add(BigInteger.ZERO);
	}
	
	public BigInteger obtenerCoprimo(){
		return coprimo.add(BigInteger.ZERO);
	}
	
	public Nodo obtenerOrigen(){
		return (Nodo)origen.clone();
	}
	
	@Override
	public String obtenerTipo() {
		return "CRIPTOANALIZAR";
	}

}
