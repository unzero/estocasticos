package sistema;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedList;

import agentes.*;
import red.Servidor;

public class Ciudad implements Agente{
	
	
	public static Ciudad ciudad;
	//Datos de la ciudad 
	private BigInteger identidad;
	private Mensajero mensajero;
	private boolean estado;
	private long dimension;
	private double[][] indiceSeguridad;
	private double IPG = 0.0;
	
	private Ciudad(long[] datos,LinkedList<String> direcciones){
		estado = true;
		try{
			//datos basicos de la ciudad
			dimension = datos[0];
			identidad = BigInteger.probablePrime(50, new SecureRandom());
			LinkedList<NodoConectado> nodos = new LinkedList<>();
			for(String ip : direcciones){
				nodos.add(new NodoConectado(ip,Servidor.PUERTO));
			}
			mensajero = Mensajero.getInstance(nodos);
			Thread mens_th = new Thread(mensajero);
		}catch(Exception ex){
			System.out.println(ex.getMessage());
			estado = false;
		}
	}
	
	@Override
	public void run(){
		//startup 
		
	}
	
	private void recalcularIndice(int i,int j,int influencia){
		
	}
	
	private void calcularIPG(){
		double acum = 0.0;
		for(int i=0;i<dimension;++i){
			for(int j=0;j<dimension;++j){
				acum += indiceSeguridad[i][j];
			}
		}
		IPG = acum/(dimension*dimension);
	}
	
	public double obtenerIPG(){
		return IPG;
	}
	
	public long obtenerDimension(){
		return dimension;
	}
	
	public static Ciudad getInstance(long[] datos,LinkedList<String> direcciones) throws Exception{
		if( ciudad == null){
			ciudad = new Ciudad(datos,direcciones);
			if( !ciudad.estado ){
				throw new Exception("No ha sido posible iniciar la ciudad");
			}
		}
		return ciudad;
	}

}
