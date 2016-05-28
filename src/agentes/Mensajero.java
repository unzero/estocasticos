package agentes;

import java.math.BigInteger;
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
	private LinkedBlockingDeque<Mensaje> bandeja;
	private Servidor servidor;
	private boolean estado;
	private Nodo miNodo;
	
	private Mensajero(LinkedList<NodoConectado> nodos){
		estado = true;
		nodosConectados = new LinkedBlockingDeque<>();
		nodosConectados.addAll(nodos);
		miNodo = nodos.getFirst().obtenerNodo();
		servidor = Servidor.getInstance();
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
				
				//System.out.println("Msj enviado");
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		
		while( true ){
			Mensaje nuevoMensaje = servidor.leerMensaje();
			if( nuevoMensaje != null){
				//nuevoMensaje = ((MensajeDeRed)nuevoMensaje).obtenerMensaje();
				System.out.println("Mensaje nuevo: "+nuevoMensaje);
			}	
		}
		
	}
	
	public void agregarNodo(NodoConectado nuevoNodo){
		nodosConectados.add(nuevoNodo);
	}
	
	
	@Override
	public String obtenerTipo(){
		return "MENSAJERO";
	}
	
	@Override 
	public void mensajeNuevo(Mensaje msj){
		bandeja.add(msj);
	}
		
	@Override
	public BigInteger obtenerIdentidad(){
		return new BigInteger("-1");
	}
	
	public static Mensajero getInstance(LinkedList<NodoConectado> nodos) throws Exception{
		if( mensajero  == null){
			mensajero = new Mensajero(nodos);
			if( !mensajero.estado ){
				throw new Exception("ERROR CON EL MENSAJERO");
			}
		}
		return mensajero;
	}

}
