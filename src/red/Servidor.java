package red;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import agentes.Mensaje;

public class Servidor implements Runnable {
	
	public Servidor(){
		
		
	}
	
	@Override
	public void run(){
		try{
			Mensaje mensajeRecibido;
			ServerSocket socketEscucha = new ServerSocket(5001);
			System.out.println("Servidor en escucha...");
			while(true){
				Socket conexion = socketEscucha.accept();
				System.out.println("Nuevo mensaje!!");
				ObjectInputStream nuevoMensaje  = new ObjectInputStream(conexion.getInputStream());
				mensajeRecibido = (Mensaje)nuevoMensaje.readObject();
				System.out.println(mensajeRecibido.toString());
			}
			
		}catch(Exception ex){
			System.out.println("Error al iniciar un servidor");
		}
		
	}

}
