package red;

import java.io.ObjectOutputStream;
import java.net.Socket;

import sistema.Mensaje;

public class Cliente {
	
	private Mensaje mensaje;
	private String IPDestino;
	private int puertoDestino;
	
	public Cliente(Mensaje msj,String servidor,int puerto){
		mensaje = msj;
		IPDestino = servidor;
		puertoDestino = puerto;
	}
	
	public Cliente(Mensaje msj,Nodo destino){
		mensaje = msj;
		IPDestino = destino.obtenerIP();
		puertoDestino = destino.obtenerPuerto();
	}
	
	public boolean enviar(){
		try{
			//System.out.println("OK: "+mensaje);
			Socket conexion = new Socket(IPDestino,puertoDestino);
			//System.out.println("OK");
			ObjectOutputStream nuevoMensaje = new ObjectOutputStream(conexion.getOutputStream());
			
			nuevoMensaje.writeObject(mensaje);
			conexion.close();
		}catch(Exception ex){
			//System.out.println("Error al enviar el mensaje");
			ex.printStackTrace();
			System.err.println(mensaje);
			return false;
		}
		return true;
	}
	

}
