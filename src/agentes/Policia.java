package agentes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.math3.distribution.BetaDistribution;

import mensajes.Mensaje;
import mensajes.Patrullar;
import mensajes.Seguridad;
import sistema.Ciudad;

public class Policia implements Agente,Comparable<Policia> {

	
	private static final int MIGRACION = 0;
	private static final int PATRULLAR = 1;
	private LinkedBlockingDeque<Mensaje> bandeja;
	private int posX,posY;
	private SecureRandom rand;
	private double efectividad;
	private LinkedBlockingDeque<Mensaje> radio;
	private double preferencia;
	private double ultima = -1;

	public Policia(int x,int y,double ef){
		bandeja = new LinkedBlockingDeque<>();
		radio = new LinkedBlockingDeque<>();
		rand = new SecureRandom();
		posX = x;
		posY = y;
		efectividad = rand.nextDouble();
		preferencia = rand.nextDouble();
		ultima = PATRULLAR;
	}

	@Override
	public void run() {
		System.out.println("Nuevo policía");
		try{
			while( true ){
				double indiceInicial = Ciudad.getInstance(null, null).obtenerIndice(posX, posY);
				for(int x=0;x<3;++x){
					requisa();
				}
				double indiceFinal = Ciudad.getInstance(null,null).obtenerIndice(posX, posY);
				if( (indiceInicial -indiceFinal > 0 && ultima == PATRULLAR) || (indiceInicial-indiceFinal == 0 && ultima == MIGRACION) ){
					premiar();
				}else{
					castigar();
				}
				
				if( !radio.isEmpty() && radio.peek().obtenerTipo().equals("PATRULLAR") ){
					posX = ((Patrullar)radio.peek()).obtenerX();
					posX = ((Patrullar)radio.peek()).obtenerY();
					((Patrullar)radio.peek()).obtenerPolicia().enviarSenal(new Patrullar("RPATRULLAR", null, -1, -1));
					radio.pollFirst();
					continue;
				}
				while( !radio.isEmpty() ){
					radio.pollFirst();
				}
				if( !bandeja.isEmpty() ){
					Mensaje nx = bandeja.pollFirst();
					if( nx.obtenerTipo().equals("REDADA") ){
						posX = ((Seguridad)nx).obtenerX();
						posY = ((Seguridad)nx).obtenerY();
						//System.out.println("Redada en: "+posX+","+posY);
					}
				}else{
					switch (siguienteAccion()) {
					case MIGRACION:
						migracion();
						break;
					case PATRULLAR:
						patrullar();
						break;
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void premiar(){
		preferencia += rand.nextDouble()/4.0;
		if( preferencia > 1){
			preferencia = 1;
		}
	}
	
	private void castigar(){
		preferencia -= rand.nextDouble()/4.0;
		if( preferencia < 0){
			preferencia = 0;
		}
	}
	
	public void enviarSenal(Mensaje msj){
		radio.add(msj);
	}
	
	private int siguienteAccion(){
		double nx = rand.nextDouble();
		if( nx < preferencia ){
			ultima = PATRULLAR;
			return PATRULLAR;
		}
		ultima = MIGRACION;
		return MIGRACION;
	}
	
	private void patrullar() throws Exception{
		while( !radio.isEmpty() ){
			radio.pollFirst();
		}
		//LOGICA DE COOPERACION
		Policia companero = Ciudad.getInstance(null, null).obtenerPolicias(1).removeFirst();
		int[] pos = nuevaPosicion(rand.nextDouble() < 0.3);
		companero.enviarSenal(new Patrullar("PATRULLAR", this, pos[0],pos[1]));
		Thread.sleep(100);
		if( ! radio.isEmpty() && radio.peek().obtenerTipo().equals("RPATRULLAR") ){
			System.out.println("trabajo en equipo!!!!!!!!!!!");
			radio.peek();
		}
		posX = pos[0];
		posY = pos[1];
	}
	
	private int[] nuevaPosicion(boolean tipo) throws Exception{
		int dim = Ciudad.getInstance(null, null).obtenerDimension();
		int nx = -1;
		int ny = -1;
		double mIndice = 2;
		if( tipo )mIndice = 0;
		for(int x=0;x<4;++x){
			int tx = rand.nextInt(dim);
			int ty = rand.nextInt(dim);
			if( !tipo && ( Ciudad.getInstance(null, null).obtenerIndice(tx, ty) < mIndice || (Ciudad.getInstance(null, null).obtenerIndice(tx, ty) == mIndice && 
					Ciudad.getInstance(null,null).cantidadHabitantes(tx, ty) > Ciudad.getInstance(null, null).cantidadHabitantes(nx, ny)
					))){
				mIndice = Ciudad.getInstance(null, null).obtenerIndice(tx, ty);
				nx = tx;
				ny = ty;
			}
			/*
					
					(Ciudad.getInstance(null,null).obtenerIndice(tx, ty) == mIndice && 
					Ciudad.getInstance(null, null).cantidadHabitantes(tx, ty) > Ciudad.getInstance(null, null).cantidadHabitantes(nx, ny)
					))
					
					*
					*/
			
			if( tipo && ( Ciudad.getInstance(null,null).obtenerIndice(tx,ty) > mIndice ) ){
				mIndice = Ciudad.getInstance(null, null).obtenerIndice(tx, ty);
				nx = tx;
				ny = ty;
			}
		}
		return new int[]{nx,ny};
	}
	
	private void migracion() throws Exception{
		int[] pos =  nuevaPosicion(rand.nextDouble() < 0.3);
		System.out.println("Policia moviendose a "+pos[0]+","+pos[1]);
		posX = pos[0];
		posY = pos[1];
	}
	
	private boolean requisa() throws Exception{
		Agente civil = Ciudad.getInstance(null,null).obtenerHabitante(posX, posY);
		BetaDistribution beta = new BetaDistribution(2, 1);
		double exitoCaptura = beta.density(efectividad);
		exitoCaptura *= beta.density(Ciudad.getInstance(null, null).obtenerIndice(posX, posY));
		if( civil != null && civil.obtenerTipo().equals("LADRON") && exitoCaptura >= 0.5 ){
			civil.mensajeNuevo(new Seguridad("CAPTURADO",posX,posY));
			Ciudad.getInstance(null, null).mensajeNuevo(new Seguridad("CAPTURADO",posX, posY));
			efectividad *= 1.005;
			return true;
		}else if( civil != null && civil.obtenerIdentidad().equals("LADRON") ){
			double castigo = 0.1*(1-Ciudad.getInstance(null, null).obtenerIPG());
			efectividad *= (1-castigo);
		}
		if( efectividad < 0.01){
			efectividad = 0.01;
		}
		if( efectividad > 0.97 ){
			efectividad = 0.97;
		}
		Ciudad.getInstance(null, null).mensajeNuevo(new Seguridad("VIGILANCIA",posX, posY));
		//System.out.println("Más seguridad en: "+posX+","+posY);
		Thread.sleep(1000);
		return false;
	}

	public double obtenerEfectividad(){
		return efectividad;
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

	@Override
	public int compareTo(Policia o) {
		if( o.efectividad > efectividad ){
			return 1;
		}
		return 0;
	}
	
	/*
	 * 	tmp = rand()
	 * 
	 * 
	 */

}
