package agentes;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.plaf.SliderUI;

import red.Cliente;
import red.MensajeDeRed;
import red.Nodo;
import red.Servidor;
import sistema.Mensaje;
import sistema.NodoConectado;

public class Mensajero implements Agente{
	
	public static Mensajero mensajero;
	private LinkedBlockingDeque<NodoConectado> nodosConectados;
	private Servidor servidor;
	private boolean estado;
	private Nodo miNodo;
	
	private Mensajero(NodoConectado autoNodo){
		estado = true;
		nodosConectados = new LinkedBlockingDeque<>();
		nodosConectados.add(autoNodo);
		miNodo = autoNodo.obtenerNodo();
		servidor = new Servidor();
		Thread hServidor = new Thread(servidor);
		hServidor.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		if(!servidor.obtenerEstado()){
			estado = false;
			System.out.println("No ha sido posible iniciar el agente mensajero");
		}
	}
	
	@Override
	public void run(){
		
		//ABRIENDO LA TIENDA POSTAL
		
		for(int x=0;x<10;++x){
			for(NodoConectado nodoConectado : nodosConectados){
				//test de conexion
				MensajeDeRed msj = new MensajeDeRed((Nodo)miNodo.clone(),nodoConectado.obtenerNodo(),new Postal("Hola!!"));
				Cliente cl = new Cliente(msj, nodoConectado.obtenerNodo() );
				if( !cl.enviar() ){
					System.out.println("ERROR AL ENVIAR EL MENSAJE");
				}
			}
			
			Mensaje nuevoMensaje = servidor.leerMensaje();
			if( nuevoMensaje != null){
				//nuevoMensaje = ((MensajeDeRed)nuevoMensaje).obtenerMensaje();
				System.out.println("Mensaje nuevo: "+nuevoMensaje);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
	}
	
	public void agregarNodo(NodoConectado nuevoNodo){
		nodosConectados.add(nuevoNodo);
	}
	
	public static Mensajero getInstance(NodoConectado autoNodo) throws Exception{
		if( mensajero  == null){
			mensajero = new Mensajero(autoNodo);
			if( !mensajero.estado ){
				throw new Exception("ERROR CON EL MENSAJERO");
			}
		}
		return mensajero;
	}

}
