package agentes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import javax.sql.rowset.spi.SyncResolver;

import sistema.Ciudad;
import sistema.Mensaje;

public class Criptografo implements Agente {

	
	public static Criptografo criptografo;
	private LinkedBlockingDeque<Mensaje> bandeja;
	private LinkedList<BigInteger> coprimos;
	private BigInteger identidadCiudad;
	private SecureRandom rand;
	
	private Criptografo(BigInteger identidad,LinkedList<BigInteger> cp){
		coprimos = cp;
		bandeja = new LinkedBlockingDeque<>();
		identidadCiudad = identidad.add(BigInteger.ZERO);
		rand = new SecureRandom();
		System.out.println("OK "+coprimos.size());
	}

	@Override
	public void run() {
		System.out.println("Criptografo correcto");
		try{
			while( true ) {
				if( !bandeja.isEmpty() ){
					Mensaje nx = bandeja.pollFirst();
					if( nx.obtenerTipo().equals("CRIPTOANALIZAR") ){
						criptoanalizar((Criptoanalizar)nx);
					}
				}else{
					if( rand.nextDouble() < 0.05 ){
						buscarSospechoso();
					}
				}
				Thread.sleep(100);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void buscarSospechoso() throws Exception{
		for(BigInteger coprimo : coprimos){
			Mensajero.getInstance(null).mensajeNuevo(new Criptoanalizar(identidadCiudad.add(BigInteger.ZERO),coprimo,
					Ciudad.getInstance(null, null).obtenerIdentidadCifrada(),null));
		}
	}
	
	private void criptoanalizar(Criptoanalizar msj) throws Exception{
		System.out.println("Decifrando");
		LinkedList<BigInteger> identidades = new LinkedList<>();
		//todo 
		for(BigInteger x=BigInteger.ZERO;!x.equals(msj.obtenerIdentidad());x=x.add(BigInteger.ONE)){
			BigInteger nuevaId = msj.obtenerIdentidad().subtract(x);
			if( nuevaId.compareTo(BigInteger.ZERO) < 0){
				nuevaId = nuevaId.add(msj.obtenerSospechoso());
			}
			identidades.add(nuevaId.multiply(msj.obtenerCoprimo()).mod(msj.obtenerIdentidad()) );
		}
		Mensajero.getInstance(null).mensajeNuevo(new Criptoanalisis(identidades, msj.obtenerOrigen()));
	}
	

	@Override
	public String obtenerTipo() {
		return "CRIPTOGRAFO";
	}

	@Override
	public void mensajeNuevo(Mensaje msj) {
		bandeja.add(msj);
	}

	@Override
	public BigInteger obtenerIdentidad() {
		return new BigInteger("-3");
	}
	
	public static synchronized Criptografo getInstance(BigInteger identidad,LinkedList<BigInteger> cp){
		if( criptografo == null){
			criptografo = new Criptografo(identidad,cp);
		}
		return criptografo;
	}

}
