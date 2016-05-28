package sistema;

import java.awt.Point;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;

import agentes.*;
import red.Servidor;

public class Ciudad extends Observable implements Agente{


	public static Ciudad ciudad;
	//Datos de la ciudad 
	private BigInteger identidad;
	private Mensajero mensajero;
	private boolean estado;
	private int dimension;
	private double[][] indiceSeguridad;
	private double IPG = 0.0;
	private volatile HashMap<BigInteger,Boolean>[][] posicion;
	private HashMap<BigInteger, Agente> habitantes;
	private HashMap<BigInteger, Agente> policia;
	private HashMap<BigInteger, Boolean> cedulas;
	private LinkedBlockingDeque<Mensaje> bandeja;
	private SecureRandom rand;

	private Ciudad(int[] datos,LinkedList<String> direcciones){
		estado = true;
		try{
			//DATOS BASICOS DE LA CIUDAD, COMO SU INDICE DE SEGUIRDAD INICIAL
			dimension = datos[0];
			rand = new SecureRandom();
			identidad = BigInteger.probablePrime(50, rand);
			indiceSeguridad = new double[dimension][dimension];
			cedulas = new HashMap<>();
			policia = new HashMap<>();
			habitantes = new HashMap<>();
			posicion = new HashMap[dimension][dimension];
			bandeja = new LinkedBlockingDeque<>();
			for(int i=0;i<dimension;++i){
				for(int j=0;j<dimension;++j){
					indiceSeguridad[i][j] = rand.nextGaussian();
					posicion[i][j] = new HashMap<BigInteger,Boolean>();
				}
			}
			calcularIPG();

			//INICIALIZACION DE AGENTES LADRONES
			for(int x=0;x<datos[1];++x){
				BigInteger cc = new BigInteger(100, new SecureRandom()).mod(identidad);
				while( cedulas.containsKey(cc) ){
					cc = new BigInteger(100, new SecureRandom()).mod(identidad);
				}
				cedulas.put(cc, true);
				int i = new Random().nextInt(dimension), j = new Random().nextInt(dimension);
				Ladron ld = new Ladron(cc,i,j);
				habitantes.put(cc, ld);
				Thread ld_th = new Thread(ld);
				ld_th.start();
				posicion[i][j].put(cc, true);
				//System.out.println("OK");
			}
			
			//INCIALIZACION DE AGENTES CIUDADANOS
			for(int x=0;x<datos[2];++x){
				BigInteger cc = new BigInteger(100, new SecureRandom()).mod(identidad);
				while( cedulas.containsKey(cc) ){
					cc = new BigInteger(100, new SecureRandom()).mod(identidad);
				}
				cedulas.put(cc, true);
				int i = new Random().nextInt(dimension), j = new Random().nextInt(dimension);
				Ciudadano ld = new Ciudadano(cc,i,j);
				habitantes.put(cc, ld);
				Thread cd_th = new Thread(ld);
				cd_th.start();
				posicion[i][j].put(cc, true);
				//System.out.println("OK");
			}


			//CONEXION CON LOS OTROS NODOS
			LinkedList<NodoConectado> nodos = new LinkedList<>();
			for(String ip : direcciones){
				nodos.add(new NodoConectado(ip,Servidor.PUERTO));
			}
			mensajero = Mensajero.getInstance(nodos);
			Thread mens_th = new Thread(mensajero);
			//mens_th.start();
		}catch(Exception ex){
			System.out.println(ex.getMessage());
			estado = false;
		}
	}

	@Override
	public void run(){
		while( true ){
			if( !bandeja.isEmpty() ){
				//System.out.println("msj nuevop");
				Mensaje nx = bandeja.pollFirst();
				if( nx.obtenerTipo().equals("MIGRACION")){
					Migracion msj  = (Migracion)nx;
					if( msj.obtenerDestino() == null){
						//MIGRACION HACIA OTRA CIUDAD
					}else{
						posicion[msj.obtenerOrigen()[0]][msj.obtenerOrigen()[1]].remove(msj.obtenerMigrante().obtenerIdentidad());
						posicion[msj.obtenerDestino()[0]][msj.obtenerDestino()[1]].put(msj.obtenerMigrante().obtenerIdentidad(), true);
						msj.obtenerMigrante().mensajeNuevo(new Migracion(null,msj.obtenerOrigen(),msj.obtenerDestino()));	
					}
				}else if( nx.obtenerTipo().equals("ROBO") ){
					recalcularIndice(((Robo)nx).obtenerX(), ((Robo)nx).obtenerY(), -1);
				}
			}

		}

	}

	public double obtenerIndice(int i,int j){
		return indiceSeguridad[i][j];
	}

	private void recalcularIndice(int i,int j,int influencia){
		indiceSeguridad[i][j] = indiceSeguridad[i][j]*(1.0 + 0.1*influencia);
		if( indiceSeguridad[i][j] < 0 ){
			indiceSeguridad[i][j] = 0;
		}else if( indiceSeguridad[i][j] > 1 ){
			indiceSeguridad[i][j] = 1;
		}
		setChanged();
		notifyObservers(new Punto(i,j));
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

	public int obtenerDimension(){
		return dimension;
	}
	
	public Agente obtenerHabitante(int x,int y){
		BigInteger[] cc = posicion[x][y].keySet().toArray(new BigInteger[0]);
		return habitantes.get(cc[rand.nextInt(cc.length)]);
	}

	@Override
	public String obtenerTipo(){
		return "CIUDAD";
	}

	@Override
	public void mensajeNuevo(Mensaje msj){
		bandeja.add(msj);
	}

	@Override
	public BigInteger obtenerIdentidad(){
		return identidad;
	}

	/**
	 * 
	 * @param datos 0 -> dimension ; 1 -> ladrones ; 2 -> ciudadanos ; 3 -> policias 
	 * @param direcciones
	 * @return
	 * @throws Exception
	 */
	public static synchronized Ciudad getInstance(int[] datos,LinkedList<String> direcciones) throws Exception{
		if( ciudad == null){
			ciudad = new Ciudad(datos,direcciones);
			if( !ciudad.estado ){
				throw new Exception("No ha sido posible iniciar la ciudad");
			}
		}
		return ciudad;
	}

}
