package red;

import java.io.ObjectOutputStream;
import java.net.Socket;

import agentes.Mensaje;

public class Cliente {
	
	private Mensaje mensaje;
	private String hostIP;
	private int hostPort;
	
	public Cliente(Mensaje msj,String servidor,int puerto){
		mensaje = msj;
		hostIP = servidor;
		hostPort = puerto;
	}
	
	public boolean enviar(){
		try{
			Socket conexion = new Socket(hostIP,hostPort);
			ObjectOutputStream nuevoMensaje = new ObjectOutputStream(conexion.getOutputStream());
			nuevoMensaje.writeObject(mensaje);
			
			
			conexion.close();
		}catch(Exception ex){
			System.out.println("Error al enviar el mensaje");
			return false;
		}
		
		return true;
	}
	

}
