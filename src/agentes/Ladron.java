package agentes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.math3.distribution.BetaDistribution;

import mensajes.Mensaje;
import mensajes.Migracion;
import mensajes.Seguridad;
import sistema.Ciudad;

public class Ladron implements Agente{

	private static final int MIGRACION_INT = 0;
	private static final int MIGRACION_OUT = 1;
	private static final int ROBO = 2;

	//CAMPOS DE LA CLASE
	private BigInteger cedula;
	private int posX, posY;
	private LinkedBlockingDeque<Mensaje> bandeja;
	private SecureRandom rand;
	private double habilidad;
	private int mult = 10;

	public Ladron(BigInteger cc,int x,int y){
		bandeja = new LinkedBlockingDeque<>();
		rand = new SecureRandom();
		cedula = cc;
		posX = x;
		posY = y;
		habilidad = rand.nextDouble();
	}

	@Override
	public void run(){
		debug(""+this);
		try{
			while( true ){
				//LECTURA DE UN MENSAJE, SI LO HAY
				if( ! bandeja.isEmpty() ){
					Mensaje nx = bandeja.pollFirst();
					if( nx.obtenerTipo().equals("MIGRACION") ){
						posX = ((Migracion)nx).obtenerDestino()[0];
						posY = ((Migracion)nx).obtenerDestino()[1];
					}else if( nx.obtenerTipo().equals("CAPTURADO") ){
						debug("He sido capturado: "+cedula);
						habilidad = 0.5*habilidad;
						Thread.sleep((mult/10)*10000);
						mult++;
						//return;
					}
					//debug("Migracion "+this);
				}
				switch( siguienteAccion() ){
				case MIGRACION_INT:
					migracion_int();
					break;
				case MIGRACION_OUT:
					migracion_out();
					return;
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
		int dim = Ciudad.getInstance(null, null).obtenerDimension();
		int[] i = new int[dim];
		int[] j = new int[dim];
		double[] indice = new double[dim];
		int[] personas = new int[dim];
		for(int x=0;x<dim;++x){
			i[x] = rand.nextInt(dim);
			j[x] = rand.nextInt(dim);
			indice[x] = Ciudad.getInstance(null, null).obtenerIndice(i[x], j[x]);
			personas[x] = Ciudad.getInstance(null, null).cantidadHabitantes(i[x], j[x]);
		}
		for(int x=0;x<dim;++x){
			for(int y=x+1;y<dim;++y){
				if( personas[y] > personas[x] || (personas[y] == personas[x] && indice[y] >= indice[x]) ){
					int tmp0 = i[x];
					i[x] = i[y];
					i[y] = tmp0;
					
					tmp0 = j[x];
					j[x] = j[y];
					j[y] = tmp0;
					
					tmp0 = personas[x];
					personas[x] = personas[y];
					personas[y] = tmp0;
					
					double tmp = indice[x];
					indice[x] = indice[y];
					indice[y] = tmp;
				}
			}
		}
		
		int[] org = {posX,posY};
		int[] des = {};
		if( rand.nextDouble() < 0.5){
			des = new int[]{i[0],j[0]};
		}else{
			des = new int[]{i[1],j[1]};
		}
			
		Ciudad.getInstance(null,null).mensajeNuevo(new Migracion("MIGRACION",this,org,des));	
	}
	
	private void robo() throws Exception{
		//debug("Intento de robo");
		Agente victima = Ciudad.getInstance(null,null).obtenerHabitante(posX, posY);
		//debug("Victima");
		double indice = Ciudad.getInstance(null, null).obtenerIndice(posX, posY);
		if( victima != null  && victima.obtenerTipo().equals("CIUDADANO") ){
			BetaDistribution beta = new BetaDistribution(2, 1);
			double exito = beta.density(habilidad);
			exito *= beta.density(1.0-Ciudad.getInstance(null, null).obtenerIndice(posX, posY)+0.01);
			//exito *= beta.density(rand.nextDouble());
			if( exito >= 0.5 ){
				victima.mensajeNuevo(new Seguridad("ROBO",posX,posY));
				Ciudad.getInstance(null,null).mensajeNuevo(new Seguridad("ROBO",posX, posY));
				habilidad = habilidad*1.1;
			}else{
				habilidad = habilidad*1.05;
			}
			if( habilidad < 0.01 ){
				habilidad = 0.01;
			}
			if( habilidad > 0.97 ){
				habilidad = 0.97;
			}
			//debug("Ladron: "+cedula+" : "+habilidad);
		}
	}
	
	private void migracion_out() throws Exception{
		int[] pos = {posX,posY};
		Ciudad.getInstance(null, null).mensajeNuevo(new Migracion("INMIGRACION", this, pos, null));
	}

	private int siguienteAccion(){
		double nx = rand.nextDouble();
		if( nx < 0.0005*(mult/5) ){
			return MIGRACION_OUT;
		}else if( nx < 0.4){
			return MIGRACION_INT;
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
	
	private void debug(String msj){
		System.out.println("LADRON: "+msj);
	}

}
