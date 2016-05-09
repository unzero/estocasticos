package sistema;

import java.net.InetAddress;
import java.util.LinkedList;

import agentes.Mensajero;
import red.Servidor;

public class Comunidad {
	
	private Mensajero mensajero;
	
	public Comunidad() throws Exception{
		
		String ip = "192.168.2.10";//InetAddress.getLocalHost().getHostAddress();
		NodoConectado autoNodo = new NodoConectado(ip,Servidor.PUERTO);
		mensajero = Mensajero.getInstance(autoNodo);
		Thread mensajeroT = new Thread(mensajero);
		mensajeroT.start();
		mensajero.agregarNodo(new NodoConectado("192.168.2.9", 5001));
	}

}
