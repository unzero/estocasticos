package agentes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.math3.distribution.BetaDistribution;

import mensajes.Mensaje;
import mensajes.Seguridad;
import sistema.Ciudad;

public class Policia implements Agente,Comparable<Policia> {

	private LinkedBlockingDeque<Mensaje> bandeja;
	private int posX,posY;
	private SecureRandom rand;
	private double efectividad;

	public Policia(int x,int y,double ef){
		bandeja = new LinkedBlockingDeque<>();
		rand = new SecureRandom();
		posX = x;
		posY = y;
		efectividad = ef;
	}

	@Override
	public void run() {
		System.out.println("Nuevo policía");
		try{
			while( true ){
				for(int x=0;x<3;++x){
					requisa();
				}
				
				if( !bandeja.isEmpty() ){
					Mensaje nx = bandeja.pollFirst();
					if( nx.obtenerTipo().equals("REDADA") ){
						posX = ((Seguridad)nx).obtenerX();
						posY = ((Seguridad)nx).obtenerY();
						System.out.println("Redada en: "+posX+","+posY);
					}
				}else{
					migracion();
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void migracion() throws Exception{
		int dim = Ciudad.getInstance(null, null).obtenerDimension();
		int nx = -1;
		int ny = -1;
		double mIndice = 2;
		for(int x=0;x<4;++x){
			int tx = rand.nextInt(dim);
			int ty = rand.nextInt(dim);
			if( Ciudad.getInstance(null, null).obtenerIndice(tx, ty) < mIndice ){
				mIndice = Ciudad.getInstance(null, null).obtenerIndice(tx, ty);
				nx = tx;
				ny = ty;
			}
		}
		posX = nx;
		posY = ny;
	}
	
	private void requisa() throws Exception{
		Agente civil = Ciudad.getInstance(null,null).obtenerHabitante(posX, posY);
		BetaDistribution beta = new BetaDistribution(2, 1);
		double exito = beta.density(efectividad);
		exito *= beta.density(Ciudad.getInstance(null, null).obtenerIndice(posX, posY));
		
		if( civil != null && civil.obtenerTipo().equals("LADRON") && exito >= 0.5 ){
			/*civil.mensajeNuevo(new Seguridad("CAPTURADO",posX,posY));
			Ciudad.getInstance(null, null).mensajeNuevo(new Seguridad("CAPTURADO",posX, posY));*/
			efectividad *= 1.005;
		}else if( civil != null && civil.obtenerIdentidad().equals("LADRON") ){
			double castigo = 0.1*(1-Ciudad.getInstance(null, null).obtenerIPG());
			efectividad *= (1-castigo);
		}
		Ciudad.getInstance(null, null).mensajeNuevo(new Seguridad("VIGILANCIA",posX, posY));
		System.out.println("Más seguridad en: "+posX+","+posY);
		Thread.sleep(1000);
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

}
