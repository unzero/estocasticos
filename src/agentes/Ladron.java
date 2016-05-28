package agentes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingDeque;

import sistema.Ciudad;
import sistema.Mensaje;

public class Ladron implements Agente{

	private static final int MIGRACION_INT = 0;
	private static final int MIGRACION_OUT = 1;
	private static final int ROBO = 2;

	//CAMPOS DE LA CLASE
	private BigInteger cedula;
	private int posX, posY;
	private LinkedBlockingDeque<Mensaje> bandeja;
	private SecureRandom rand;

	public Ladron(BigInteger cc,int x,int y){
		bandeja = new LinkedBlockingDeque<>();
		rand = new SecureRandom();
		cedula = cc;
		posX = x;
		posY = y;
	}

	@Override
	public void run(){
		System.out.println(this);
		try{
			while( true ){
				//LECTURA DE UN MENSAJE, SI LO HAY
				if( ! bandeja.isEmpty() ){
					Mensaje nx = bandeja.pollFirst();
					if( nx.obtenerTipo().equals("MIGRACION") ){
						posX = ((Migracion)nx).obtenerDestino()[0];
						posY = ((Migracion)nx).obtenerDestino()[1];
					}
					//System.out.println("Migracion "+this);
				}
				switch( siguienteAccion() ){
				case MIGRACION_INT:
					migracion_int();
					break;
				case ROBO:
					robo();
					break;
				}
				Thread.sleep(1000);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void migracion_int() throws Exception {
		int i = rand.nextInt(Ciudad.getInstance(null,null).obtenerDimension());
		int j = rand.nextInt(Ciudad.getInstance(null,null).obtenerDimension());
		int[] org = {posX,posY};
		int[] des = {i,j};
		Ciudad.getInstance(null,null).mensajeNuevo(new Migracion(this,org,des));	
	}
	
	private void robo() throws Exception{
		//System.out.println("Intento de robo");
		Agente victima = Ciudad.getInstance(null,null).obtenerHabitante(posX, posY);
		//System.out.println("Victima");
		if( victima.obtenerTipo().equals("CIUDADANO") ){
			victima.mensajeNuevo(new Robo(posX,posY));
			Ciudad.getInstance(null,null).mensajeNuevo(new Robo(posX, posY));
		}
	}

	private int siguienteAccion(){
		double nx = rand.nextGaussian();
		if( nx < 0.2 ){
			return MIGRACION_INT;
		}else if( nx < 0.4){
			return MIGRACION_OUT;
		}
		return ROBO;
	}

	@Override
	public String obtenerTipo(){
		return "LADRON";
	}

	@Override
	public void mensajeNuevo(Mensaje msj){
		bandeja.add(msj);
	}

	@Override
	public BigInteger obtenerIdentidad(){
		return cedula;
	}

	@Override
	public String toString(){
		return "Ladron en: "+posX+" , "+posY;
	}

}
