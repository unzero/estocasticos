package agentes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingDeque;

import sistema.Ciudad;
import sistema.Mensaje;

public class Policia implements Agente{

	private LinkedBlockingDeque<Mensaje> bandeja;
	private int posX,posY;
	private SecureRandom rand;

	public Policia(int x,int y){
		bandeja = new LinkedBlockingDeque<>();
		rand = new SecureRandom();
		posX = x;
		posY = y;
	}

	@Override
	public void run() {
		System.out.println("Nuevo policía");
		try{
			while( true ){
				for(int x=0;x<5;++x){
					Agente civil = Ciudad.getInstance(null,null).obtenerHabitante(posX, posY);
					if( civil != null && civil.obtenerTipo().equals("LADRON") ){
						civil.mensajeNuevo(new Capturado(posX,posY));
						Ciudad.getInstance(null, null).mensajeNuevo(new Capturado(posX, posY));
					}
					Ciudad.getInstance(null, null).mensajeNuevo(new Capturado(posX, posY));
					System.out.println("Más seguridad en: "+posX+","+posY);
					Thread.sleep(1000);
				}
				
				double nx = rand.nextDouble();
				if( nx < 0.1 ){
					int dim = Ciudad.getInstance(null,null).obtenerDimension();
					posX = rand.nextInt(dim);
					posY = rand.nextInt(dim);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public String obtenerTipo() {
		return "POLICIA";
	}

	@Override
	public void mensajeNuevo(Mensaje msj) {
		bandeja.add(msj);
	}

	@Override
	public BigInteger obtenerIdentidad() {
		return new BigInteger("-4");
	}

}
