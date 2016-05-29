package mensajes;

import agentes.Agente;

public class Migracion implements Mensaje{
	
	private Agente migrante;
	private int[] org,des;
	private String tipo;
	
	public Migracion(String tp,Agente mg,int[] orgn,int[] dest){
		tipo = tp;
		migrante = mg;
		org = orgn;
		des = dest;
	}
	
	public Agente obtenerMigrante(){
		return migrante;
	}
	
	public int[] obtenerOrigen(){
		return org;
	}
	
	public int[] obtenerDestino(){		
		return des;
	}
	
	@Override
	public String obtenerTipo(){
		return tipo;
	}

}
