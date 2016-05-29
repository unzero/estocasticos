package agentes;

import java.math.BigInteger;
import java.util.concurrent.LinkedBlockingDeque;

import mensajes.Mensaje;

public class Ciudadano implements Agente{

	private BigInteger cedula;
	private LinkedBlockingDeque<Mensaje> bandeja;
	private int pos_x,pos_y;

	public Ciudadano(BigInteger cc,int x,int y){
		bandeja = new LinkedBlockingDeque<>();
		cedula = cc;
		pos_x = x;
		pos_y = y;
	}

	@Override
	public void run(){
		System.out.println(this);
		try{
			while( true ){
				if( !bandeja.isEmpty() ){
					Mensaje nx = bandeja.pollFirst();
					if( nx.obtenerTipo().equals("ROBO") ){
						System.out.println("He sido robado en: "+pos_x+","+pos_y);
					}
				}
				Thread.sleep(1000);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private int siguienteAccion(){
		return 0;
	}

	@Override
	public String obtenerTipo(){
		return "CIUDADANO";
	}

	@Override
	public BigInteger obtenerIdentidad(){
		return cedula;
	}

	@Override
	public void mensajeNuevo(Mensaje msj){
		bandeja.add(msj);
	}
	
	@Override
	public String toString(){
		return "Ciudadano en: "+pos_x+","+pos_y;
	}

}
	