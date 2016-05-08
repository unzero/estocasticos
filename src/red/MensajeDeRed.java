package red;

import agentes.Mensaje;

public class MensajeDeRed implements Mensaje{
	
	private String contenido;
	
	public MensajeDeRed(String cnt){
		this.contenido = cnt;
	}
	
	@Override
	public String toString(){
		return "Nuevo mensaje: "+contenido;
	}

}
