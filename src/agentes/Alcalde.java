package agentes;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sound.midi.Synthesizer;

import sistema.Mensaje;

public class Alcalde implements Agente{
	
	public static Alcalde alcalde;
	private LinkedBlockingDeque<Mensaje> bandeja;
	private Set<BigInteger> ciudadanos;
	private ConcurrentHashMap<BigInteger, Boolean> buscados;
	
	private Alcalde(Set<BigInteger> cd){
		bandeja = new LinkedBlockingDeque<>();
		buscados = new ConcurrentHashMap<>();
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
			Thread.sleep(5000);
		}
		}catch(Exception ex){
			ex.printStackTrace();
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
