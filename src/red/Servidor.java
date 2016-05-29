package red;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import mensajes.Mensaje;

public class Servidor implements Runnable {
	
	
	public static Servidor servidor;
	public static final int PUERTO = 5001;
	private boolean estado;
	private LinkedBlockingDeque<Mensaje> colaDeEntrada;
	
	private Servidor(){
		estado = true;
		colaDeEntrada = new LinkedBlockingDeque<>();
	}
		
	@Override
	public void run(){
		try{
			Mensaje mensajeRecibido;
			ServerSocket socketEscucha = new ServerSocket(PUERTO);
			//System.out.println("Servidor en escucha...");
			while(true){
				//System.out.println("OK....");
				Socket conexion = socketEscucha.accept();
				//System.out.println("Nuevo mensaje!!");
				ObjectInputStream nuevoMensaje  = new ObjectInputStream(conexion.getInputStream());
				mensajeRecibido = (Mensaje)nuevoMensaje.readObject();
				colaDeEntrada.push(mensajeRecibido);
				//System.out.println(mensajeRecibido.toString());
			}
		}catch(Exception ex){
			estado = false;
			//throw new ErrorDeRed("No ha sido posible iniciar el servidor");
			System.out.println("Error al iniciar un servidor");
		}
	}
	
	public Mensaje leerMensaje(){
		return colaDeEntrada.poll();
	}
		
	public boolean obtenerEstado(){
		return estado;
	}

	
	public static Servidor getInstance(){
		if( servidor == null ){
			servidor = new Servidor();
		}
		return servidor;
	}
	
}
