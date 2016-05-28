package agentes;

import java.math.BigInteger;
import java.util.concurrent.LinkedBlockingDeque;

import sistema.Mensaje;

public class Alcalde implements Agente{
	
	public static Alcalde alcalde;
	private LinkedBlockingDeque<Mensaje> bandeja;
	
	private Alcalde(){
	}
	
	@Override
	public void run(){
	}
	
	@Override
	public String obtenerTipo(){
		return "ALCALDE";
	}
	
	@Override
	public void mensajeNuevo(Mensaje msj){
		bandeja.add(msj);
	}
	
	@Override
	public BigInteger obtenerIdentidad(){
		return new BigInteger("-2");
	}
	
	public static Alcalde getInstance(){
		if( alcalde == null ) {
			alcalde = new Alcalde();
		}
		return alcalde;
	}

}
