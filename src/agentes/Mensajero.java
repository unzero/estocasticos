package agentes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sql.rowset.spi.SyncResolver;
import javax.swing.plaf.SliderUI;

import mensajes.Criptoanalisis;
import mensajes.Criptoanalizar;
import mensajes.Mensaje;
import mensajes.MensajeDeRed;
import mensajes.Migracion;
import red.Cliente;
import red.Nodo;
import red.Servidor;
import sistema.Ciudad;
import sistema.NodoConectado;

public class Mensajero implements Agente{

	public static Mensajero mensajero;
	private LinkedBlockingDeque<NodoConectado> nodosConectados;
	private LinkedBlockingDeque<Mensaje> bandeja;
	private Servidor servidor;
	private boolean estado;
	private SecureRandom rand;
	private Nodo miNodo;
	private boolean fronteraAbierta;

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
			debug("No ha sido posible iniciar el agente mensajero");
		}
		fronteraAbierta = true;
	}

	@Override
	public void run(){
		/*
		for(NodoConectado nodoConectado : nodosConectados){
			//test de conexion
			MensajeDeRed msj = new MensajeDeRed((Nodo)miNodo.clone(),nodoConectado.obtenerNodo(),new Postal("Hola!!"));
			Cliente cl = new Cliente(msj, nodoConectado.obtenerNodo() );

			if( !cl.enviar() ){
				debug("ERROR AL ENVIAR EL MENSAJE");
			}
			//debug("Msj enviado");
		}
		 */
		debug("Mensajero iniciado");
		try{
		while( true ){

			//LECTURA DE MENSAJE EXTERNO -> INTERNO
			Mensaje nuevoMensaje = servidor.leerMensaje();
			if( nuevoMensaje != null){
				/*if( nuevoMensaje.obtenerTipo().equals("CRIPTOANALIZAR") ){
					Criptografo.getInstance(null,null).mensajeNuevo(nuevoMensaje);
				}else if( nuevoMensaje.obtenerTipo().equals("CRIPTOANALISIS") ){
					Alcalde.getInstance(null).mensajeNuevo(nuevoMensaje);
				}*/
				if( ((MensajeDeRed)nuevoMensaje).obtenerMensaje().obtenerTipo().startsWith("INMIGRACION")
						&& fronteraAbierta){
					debug("Un nuevo inmigrante desde: "+((MensajeDeRed)nuevoMensaje).obtenerOrigen());
					Ciudad.getInstance(null,null).mensajeNuevo(new Migracion(
							((MensajeDeRed)nuevoMensaje).obtenerMensaje().obtenerTipo(),null,null,null));
				}else if( ((MensajeDeRed)nuevoMensaje).obtenerMensaje().obtenerTipo().startsWith("INMIGRACION")
						&& !fronteraAbierta){
					Nodo dest = ((MensajeDeRed)nuevoMensaje).obtenerOrigen();
					String tipo = "DEPORTADOC";
					if( ((MensajeDeRed)nuevoMensaje).obtenerMensaje().obtenerTipo().equals("INMIGRACIONL") ){
						tipo = "DEPORTADOL";
					}
					new Cliente(new MensajeDeRed((Nodo)miNodo.clone(), dest, 
							new Migracion(tipo, null, null, null)), dest).enviar();
				}else if( ((MensajeDeRed)nuevoMensaje).obtenerMensaje().obtenerTipo().startsWith("DEPORTADO")){
					debug("Nuevo deportado");
					Ciudad.getInstance(null,null).mensajeNuevo(new Migracion(
							((MensajeDeRed)nuevoMensaje).obtenerMensaje().obtenerTipo(),null,null,null));
				}
			}	

			//LECTURA DE MENSAJE INTERNO -> EXTERNO
			if( !bandeja.isEmpty() ){
				Mensaje nx = bandeja.pollFirst();
				if( nx.obtenerTipo().equals("CRIPTOANALIZAR")){
					criptoanalizar(nx);
				}else if( nx.obtenerTipo().equals("CRIPTOANALISIS") ){
					new Cliente(nx, ((Criptoanalisis)nx).obtenerDestino()).enviar();
				}else if( nx.obtenerTipo().startsWith("INMIGRACION") ){
					inmigrar((Migracion)nx);
				}else if( nx.obtenerTipo().equals("CIERREFRONTERA" ) ){
					fronteraAbierta = false;
					debug("............................CIERREN LAS PUERTAS!!!!!!!!!!!!!!");
				}else if( nx.obtenerTipo().equals("ABRANFRONTERA") ){
					fronteraAbierta = true;
					debug("VIENE UNA LOLI............................ABRAN LAS PUERTAS!!!!!!!!!!!!!!");
				}
			}
		}
		}catch (Exception ex) {
			ex.printStackTrace();
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
	
	private void inmigrar(Migracion mig){
		NodoConectado[] tmp = nodosConectados.toArray(new NodoConectado[0]);
		NodoConectado dest = null;
		while( dest == null ){
			dest = tmp[rand.nextInt(tmp.length)];
		}
		Mensaje nx = new MensajeDeRed((Nodo)miNodo.clone(), dest.obtenerNodo(), 
				new Migracion(mig.obtenerTipo(), null, null, null));
		new Cliente(nx,dest.obtenerNodo()).enviar();
		debug("Migracion exitosa, adios buen hombre!!");
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
	
	private void debug(String msj){
		System.out.println("MENSAJERO: "+msj);
	}

}
