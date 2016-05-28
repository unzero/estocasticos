package agentes;

import sistema.Mensaje;

public class Migracion implements Mensaje{
	
	private Agente migrante;
	private int[] org,des;
	
	public Migracion(Agente mg,int[] orgn,int[] dest){
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
		return "MIGRACION";
	}

}
