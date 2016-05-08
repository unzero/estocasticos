package red;

import java.io.Serializable;

public class Nodo implements Serializable{
	
	private String ip;
	private int puerto;
	
	public Nodo(String ip,int pt){
		this.ip = ip;
		this.puerto = pt;
	}
	
	public String obtenerIP(){
		return ip;
	}
	
	public int obtenerPuerto(){
		return puerto;
	}

	@Override
	public Object clone(){
		Nodo nodo = new Nodo(ip,puerto);
		return nodo;
	}
	
	@Override
	public String toString(){
		return "Nodo: "+ip+":"+puerto;
	}
}
