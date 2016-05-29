package agentes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sql.rowset.spi.SyncResolver;
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
	private SecureRandom rand;
	private Nodo miNodo;

	private Mensajero(LinkedList<NodoConectado> nodos){
		estado = true;
		nodosConectados = new LinkedBlockingDeque<>();
		nodosConectados.addAll(nodos);
		miNodo = nodos.getFirst().obtenerNodo();
		bandeja = new LinkedBlockingDeque<>();
		servidor = Servidor.getInstance();
		rand = new SecureRandom();
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
		/*
		for(NodoConectado nodoConectado : nodosConectados){
			//test de conexion
			MensajeDeRed msj = new MensajeDeRed((Nodo)miNodo.clone(),nodoConectado.obtenerNodo(),new Postal("Hola!!"));
			Cliente cl = new Cliente(msj, nodoConectado.obtenerNodo() );

			if( !cl.enviar() ){
				System.out.println("ERROR AL ENVIAR EL MENSAJE");
			}
			//System.out.println("Msj enviado");
		}
		 */
		System.out.println("Mensajero iniciado");
		while( true ){

			//LECTURA DE MENSAJE EXTERNO -> INTERNO
			Mensaje nuevoMensaje = servidor.leerMensaje();
			if( nuevoMensaje != null){
				if( nuevoMensaje.obtenerTipo().equals("CRIPTOANALIZAR") ){
					Criptografo.getInstance(null,null).mensajeNuevo(nuevoMensaje);
				}else if( nuevoMensaje.obtenerTipo().equals("CRIPTOANALISIS") ){
					Alcalde.getInstance(null).mensajeNuevo(nuevoMensaje);
				}
			}	

			//LECTURA DE MENSAJE INTERNO -> EXTERNO
			if( !bandeja.isEmpty() ){
				Mensaje nx = bandeja.pollFirst();
				if( nx.obtenerTipo().equals("CRIPTOANALIZAR")){
					criptoanalizar(nx);
				}else if( nx.obtenerTipo().equals("CRIPTOANALISIS") ){
					new Cliente(nx, ((Criptoanalisis)nx).obtenerDestino()).enviar();
				}
			}
		}
	}

	private void criptoanalizar(Mensaje nx){
		NodoConectado[] tmp = nodosConectados.toArray(new NodoConectado[0]);
		NodoConectado dest = null;
		while( dest == null ){
			dest = tmp[rand.nextInt(tmp.length)];
		}
		nx = new Criptoanalizar(((Criptoanalizar)nx).obtenerIdentidad(),((Criptoanalizar)nx).obtenerCoprimo(),((Criptoanalizar)nx).obtenerSospechoso(),(Nodo)miNodo.clone());
		new Cliente(nx,dest.obtenerNodo()).enviar();
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

	public static synchronized Mensajero getInstance(LinkedList<NodoConectado> nodos) throws Exception{
		if( mensajero  == null){
			mensajero = new Mensajero(nodos);
			if( !mensajero.estado ){
				throw new Exception("ERROR CON EL MENSAJERO");
			}
		}
		return mensajero;
	}

}
