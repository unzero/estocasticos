package sistema;

import red.Nodo;

public class NodoConectado {
	
	private Nodo nodo;
	private String estado;
	
	public NodoConectado(String ip,int puerto){
		nodo = new Nodo(ip, puerto);
		estado = "INDETERMINADO";
	}
	
	public void cambiarEstado(String nuevoEstado){
		estado = nuevoEstado;
	}
	
	public Nodo obtenerNodo(){
		return (Nodo)nodo.clone();
	}
	
	@Override
	public Object clone(){
		NodoConectado nuevoNodo = new NodoConectado(nodo.obtenerIP(),nodo.obtenerPuerto());
		nuevoNodo.estado = estado;
		return nuevoNodo;
	}
	
	@Override
	public String toString(){
		return "Nueva comunidad\n\t"+nodo+"\n\tEstado: "+estado;
	}

}
