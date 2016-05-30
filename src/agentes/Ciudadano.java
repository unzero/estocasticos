package agentes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.LinkedBlockingDeque;

import mensajes.Mensaje;
import mensajes.Migracion;
import mensajes.Seguridad;
import sistema.Ciudad;

public class Ciudadano implements Agente{

	private BigInteger cedula;
	private LinkedBlockingDeque<Mensaje> bandeja;
	private int pos_x,pos_y, last;
	private SecureRandom rand;
	private static final int MIGRACION_INT = 0;
	private static final int MIGRACION_OUT = 1;
	private static final int AVISO = 2;
	private double[] preferencia;
	private boolean robado;
	

	public Ciudadano(BigInteger cc,int x,int y){
		bandeja = new LinkedBlockingDeque<>();
		cedula = cc;
		pos_x = x;
		pos_y = y;
		last =-1;
		robado=false;
		rand = new SecureRandom();
		preferencia = new double[4];
		preferencia[0]= rand.nextDouble();
		preferencia[1]= rand.nextDouble();
		preferencia[2]= rand.nextDouble();
		preferencia[3]= rand.nextDouble();
	}

	@Override
	public void run(){
		System.out.println(this);
		try{
			while( true ){
				if( !bandeja.isEmpty() ){
					Mensaje nx = bandeja.pollFirst();
					if( nx.obtenerTipo().equals("MIGRACION") ){
						pos_x = ((Migracion)nx).obtenerDestino()[0];
						pos_y = ((Migracion)nx).obtenerDestino()[1];
					}else if( nx.obtenerTipo().equals("ROBO") ){
						System.out.println("He sido robado en: "+pos_x+","+pos_y);
						castigar();
						switch( siguienteAccion() ){
						case MIGRACION_INT:
							migracion_int();
							last=0;
							break;
						case MIGRACION_OUT:
							migracion_out();
							last=1;
							break;
						case AVISO:
							aviso();
							last=2;
							break;
						}
					}
				}else{
					if(preferencia[3]>=Ciudad.getInstance(null, null).obtenerIPG()){
						migracion_int();
						premiar();
					}
				}
				
				Thread.sleep(1000);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void premiar() {
		if(robado){
			preferencia[last] += rand.nextDouble()/4;
		}
	}

	private void castigar() {
		if(robado){
			preferencia[last] -= rand.nextDouble();
		}
	}

	
	
	private void aviso() throws Exception {
		Alcalde.getInstance(null).mensajeNuevo(new Seguridad ("ASALTO", pos_x, pos_y) );
		migracion_int();
		
	}

	private void migracion_out() throws Exception {
		int[] pos = {pos_x,pos_y};
		Ciudad.getInstance(null, null).mensajeNuevo(new Migracion("INMIGRACION", this, pos, null));
		
	}

	private void migracion_int() throws Exception {
		int i = rand.nextInt(Ciudad.getInstance(null,null).obtenerDimension());
		int j = rand.nextInt(Ciudad.getInstance(null,null).obtenerDimension());
		int[] org = {pos_x,pos_y};
		int[] des = {i,j};
		Ciudad.getInstance(null,null).mensajeNuevo(new Migracion("MIGRACION",this,org,des));	
	}
	
	private int siguienteAccion(){
		double nx = rand.nextGaussian();
		if( preferencia[0]>=preferencia[1] && preferencia[0]>=preferencia[2]){
			return MIGRACION_INT;
		}else if( preferencia[1]>=preferencia[2]){
			return MIGRACION_OUT;
		}
		return AVISO;
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