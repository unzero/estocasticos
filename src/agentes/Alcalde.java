package agentes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sound.midi.Synthesizer;

import mensajes.Criptoanalisis;
import mensajes.Mensaje;
import mensajes.Seguridad;
import sistema.Ciudad;

public class Alcalde implements Agente{
	
	public static Alcalde alcalde;
	private LinkedBlockingDeque<Mensaje> bandeja;
	private Set<BigInteger> ciudadanos;
	private ConcurrentHashMap<BigInteger, Boolean> buscados;
	private SecureRandom rand;
	
	private Alcalde(Set<BigInteger> cd){
		bandeja = new LinkedBlockingDeque<>();
		buscados = new ConcurrentHashMap<>();
		rand = new SecureRandom();
		ciudadanos = cd;
	}
	
	@Override
	public void run(){
		System.out.println("Peña-loza ha salido de su cama");
		try{
		while( true ) {
			if( !bandeja.isEmpty() ){
				Mensaje nx = bandeja.pollFirst();
				if( nx.obtenerTipo().equals("CRIPTOANALISIS") ){
					validarSospechosos(((Criptoanalisis)nx).obtenerIdentidades());
				}
			}
			organizarRedada();
			Thread.sleep(5000);
		}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	private void organizarRedada() throws Exception{
		int dim = Ciudad.getInstance(null, null).obtenerDimension();
		int nx = -1;
		int ny = -1;
		double mIndice = 2;
		for(int x=0;x<dim+dim;++x){
			int tx = rand.nextInt(dim);
			int ty = rand.nextInt(dim);
			if( Ciudad.getInstance(null, null).obtenerIndice(tx, ty) < mIndice ){
				mIndice = Ciudad.getInstance(null, null).obtenerIndice(tx, ty);
				nx = tx;
				ny = ty;
			}
		}
		LinkedList<Policia> escuadron = Ciudad.getInstance(null, null).obtenerPolicias(2);
		for( Policia pol : escuadron ){
			pol.mensajeNuevo(new Seguridad("REDADA", nx, ny));
		}
	}
	
	public void validarSospechosos(LinkedList<BigInteger> sospechosos){
		
		for(BigInteger sospechoso : sospechosos ){
			if( ciudadanos.contains(sospechoso) ){
				buscados.put(sospechoso, true);
			}
		}
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
	
	public static Alcalde getInstance(Set<BigInteger> cd){
		if( alcalde == null ) {
			alcalde = new Alcalde(cd);
		}
		return alcalde;
	}
	
	private void debug(String msj){
		System.out.println("ALCALDE: "+msj);
	}

}
