package mensajes;

import red.Nodo;

public class MensajeDeRed implements Mensaje{
	
	private Nodo origen,destino;
	private Mensaje mensaje;
	
	public MensajeDeRed(Nodo org,Nodo dst,Mensaje msj){
		origen = org;
		destino = dst;
		mensaje = msj;
	}
	
	public Nodo obtenerOrigen(){
		return (Nodo)origen.clone();
	}
	
	public Nodo obtenerDestino(){
		return (Nodo)destino.clone();
	}
	
	public Mensaje obtenerMensaje(){
		return mensaje;
	}
	
	@Override
	public String obtenerTipo(){
		return "RED";
	}
	
	@Override
	public String toString(){
		return "Nuevo mensaje de red\n\tOrigen: "+origen+"\n\tDestino: "+destino+"\n\tMensaje: "+mensaje;
	}

}
