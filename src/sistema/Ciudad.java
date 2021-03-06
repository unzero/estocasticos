package sistema;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import agentes.Agente;
import agentes.Alcalde;
import agentes.Ciudadano;
import agentes.Ladron;
import agentes.Mensajero;
import agentes.Policia;
import mensajes.Mensaje;
import mensajes.Migracion;
import mensajes.Seguridad;
import red.Servidor;

public class Ciudad extends Observable implements Agente{

	private static final int INTRODUCIRPOLICIA = 0;
	private static final int INTRODUCIRLADRON = 1;
	private static final int INTRODUCIRCIUDADANO = 2;
	private static final int NONE = -1;
	public static Ciudad ciudad;
	//Datos de la ciudad 
	private BigInteger identidad;
	private Mensajero mensajero;
	private boolean estado;
	private int dimension;
	private volatile double[][] indiceSeguridad;
	private double IPG = 0.0;
	private ConcurrentHashMap<BigInteger,Boolean>[][] posicion;
	private ConcurrentHashMap<BigInteger, Agente> habitantes;
	private ConcurrentHashMap<BigInteger, BigInteger> ladrones;
	private ConcurrentHashMap<BigInteger, Agente> policia;
	private ConcurrentHashMap<BigInteger, Boolean> cedulas;
	private LinkedBlockingDeque<Mensaje> bandeja;
	private LinkedList<BigInteger> coprimos;
	private SecureRandom rand;

	private Ciudad(int[] datos,LinkedList<String> direcciones){
		estado = true;
		try{

			//CONEXION CON LOS OTROS NODOS
			LinkedList<NodoConectado> nodos = new LinkedList<>();
			for(String ip : direcciones){
				nodos.add(new NodoConectado(ip,Servidor.PUERTO));
			}
			mensajero = Mensajero.getInstance(nodos);
			Thread mens_th = new Thread(mensajero);
			mens_th.start();
			Thread.sleep(5000);

			//DATOS BASICOS DE LA CIUDAD, COMO SU INDICE DE SEGUIRDAD INICIAL
			dimension = datos[0];
			rand = new SecureRandom();
			//
			identidad = BigInteger.ONE;
			for(int x=0;x<0x6;++x){
				identidad = identidad.multiply(BigInteger.probablePrime(rand.nextInt(3)+2, new SecureRandom()));
			}
			debug(""+identidad);

			//coprimos = buscarCoprimos();

			indiceSeguridad = new double[dimension][dimension];
			cedulas = new ConcurrentHashMap<>();
			policia = new ConcurrentHashMap<>();
			habitantes = new ConcurrentHashMap<>();
			posicion = new ConcurrentHashMap[dimension][dimension];
			bandeja = new LinkedBlockingDeque<>();
			ladrones = new ConcurrentHashMap<>();
			for(int i=0;i<dimension;++i){
				for(int j=0;j<dimension;++j){
					indiceSeguridad[i][j] = rand.nextDouble();
					posicion[i][j] = new ConcurrentHashMap<BigInteger,Boolean>();
				}
			}
			calcularIPG();

			//INICIALIZACION DE AGENTES LADRONES
			for(int x=0;x<datos[1];++x){
				BigInteger cc = new BigInteger(30, new SecureRandom()).mod(identidad);
				while( cedulas.containsKey(cc) ){
					cc = new BigInteger(100, new SecureRandom()).mod(identidad);
				}
				cedulas.put(cc, true);
				int i = new Random().nextInt(dimension), j = new Random().nextInt(dimension);
				Ladron ld = new Ladron(cc,i,j);
				ladrones.put(cc,cifrar(cc));
				habitantes.put(cc, ld);
				Thread ld_th = new Thread(ld);
				ld_th.start();
				posicion[i][j].put(cc, true);
				//debug("OK");
			}

			//INCIALIZACION DE AGENTES CIUDADANOS
			for(int x=0;x<datos[2];++x){
				BigInteger cc = new BigInteger(30, new SecureRandom()).mod(identidad);
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
				//debug("OK");
			}			

			//INICIALIZACION DE LOS AGENTES DE POLICIA
			for(int x=0;x<datos[3];++x){
				int i = rand.nextInt(dimension), j = rand.nextInt(dimension);
				Policia pol = new Policia(i,j,rand.nextDouble());
				new Thread(pol).start();
				policia.put(new BigInteger(""+x), pol);
			}

			//CREACION AGENTES CRIPTOGRAFO Y ALCALDE
			//new Thread(Criptografo.getInstance(identidad,coprimos)).start();
			new Thread(Alcalde.getInstance(cedulas.keySet())).start();


		}catch(Exception ex){
			debug(ex.getMessage());
			estado = false;
		}
	}

	@Override
	public void run(){
		try{
			while( true ){
				if( !bandeja.isEmpty() ){
					//debug("msj nuevop");
					Mensaje nx = bandeja.pollFirst();
					if( nx.obtenerTipo().equals("MIGRACION")){
						Migracion msj  = (Migracion)nx;
						posicion[msj.obtenerOrigen()[0]][msj.obtenerOrigen()[1]].remove(msj.obtenerMigrante().obtenerIdentidad());
						posicion[msj.obtenerDestino()[0]][msj.obtenerDestino()[1]].put(msj.obtenerMigrante().obtenerIdentidad(), true);
						msj.obtenerMigrante().mensajeNuevo(new Migracion("MIGRACION",null,msj.obtenerOrigen(),msj.obtenerDestino()));	
					}else if( nx.obtenerTipo().equals("INMIGRACION") ){
						inmigrar((Migracion)nx);
					}else if( nx.obtenerTipo().startsWith("INMIGRACION") || nx.obtenerTipo().startsWith("DEPORTADO")){
						nuevaInmigracion((Migracion)nx);
					}else if( nx.obtenerTipo().equals("ROBO") ){
						recalcularIndice(((Seguridad)nx).obtenerX(), ((Seguridad)nx).obtenerY(), -1);
					}else if( nx.obtenerTipo().equals("CAPTURADO") ){
						recalcularIndice(((Seguridad)nx).obtenerX(), ((Seguridad)nx).obtenerY(), 1);
					}else if( nx.obtenerTipo().equals("VIGILANCIA") ){
						recalcularIndice(((Seguridad)nx).obtenerX(), ((Seguridad)nx).obtenerY(), 0.2);
					}
				}

				switch (siguienteAccion()) {
				case INTRODUCIRPOLICIA:
					nuevoPolicia();
					break;
				case INTRODUCIRLADRON:
					nuevoHabitante("Ladron");
					break;
				case INTRODUCIRCIUDADANO:
					nuevoHabitante("Ciudadano");
					break;
				}
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private int siguienteAccion(){
		double nx = rand.nextDouble();
		if( nx < 0.00000001 ){
			return INTRODUCIRCIUDADANO;
		}else if( nx < 0.00000002){
			return INTRODUCIRLADRON;
		}else if( nx < 0.00000003){
			return INTRODUCIRPOLICIA;
		}
		return NONE;
	}

	private void nuevoPolicia(){
		int i = rand.nextInt(dimension), j = rand.nextInt(dimension);
		Policia pol = new Policia(i,j,rand.nextDouble());
		new Thread(pol).start();
		policia.put(new BigInteger(""+policia.size()), pol);
		debug("Nuevo policia para el sistema------------------");
	}

	private void nuevoHabitante(String tipo){
		BigInteger cc = new BigInteger(30, new SecureRandom()).mod(identidad);
		while( cedulas.containsKey(cc) ){
			cc = new BigInteger(100, new SecureRandom()).mod(identidad);
		}
		cedulas.put(cc, true);
		int i = new Random().nextInt(dimension), j = new Random().nextInt(dimension);
		Agente ag = null;
		if( tipo.equals("Ladron") ){
			ag = new Ladron(cc, i, j);
		}else{
			ag = new Ciudadano(cc, i, j);
		}
		habitantes.put(cc, ag);
		Thread cd_th = new Thread(ag);
		cd_th.start();
		posicion[i][j].put(cc, true);
		debug("Nuevo "+tipo+" para el sistema------------------");
	}

	public double obtenerIndice(int i,int j){
		return indiceSeguridad[i][j];
	}

	private void recalcularIndice(int i,int j,double influencia){
		indiceSeguridad[i][j] = indiceSeguridad[i][j]*(1.0 + 0.1*influencia);
		if( indiceSeguridad[i][j] < 0 ){
			indiceSeguridad[i][j] = 0;
		}else if( indiceSeguridad[i][j] > 1 ){
			indiceSeguridad[i][j] = 1;
		}
		setChanged();
		//debug(indiceSeguridad[i][j]);
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
		calcularIPG();
		return IPG;
	}

	public int obtenerDimension(){
		return dimension;
	}

	public Agente obtenerHabitante(int x,int y){
		BigInteger[] cc = posicion[x][y].keySet().toArray(new BigInteger[0]);
		if( cc.length == 0){
			return null;
		}
		return habitantes.get(cc[rand.nextInt(cc.length)]);
	}

	public LinkedList<Policia> obtenerPolicias(int cantidad){
		LinkedList<Policia> ret = new LinkedList<>();
		/*
		Arrays.sort(ag);

		for(int x=0;x<cantidad;++x){
			ret.add(ag[x]);
		}*/
		Policia[] ag = policia.values().toArray(new Policia[0]);
		ret.add( ag[rand.nextInt(ag.length)] );
		ret.add( ag[rand.nextInt(ag.length)] );
		return ret;
	}

	public BigInteger cifrar(BigInteger identidadOriginal){
		return identidadOriginal;
	}

	public BigInteger obtenerIdentidadCifrada(){
		BigInteger[] identidades = ladrones.keySet().toArray(new BigInteger[0]);
		return ladrones.get(identidades[rand.nextInt(identidades.length)]);
	}

	private LinkedList<BigInteger> buscarCoprimos(){
		LinkedList<BigInteger> ret = new LinkedList<>();
		BigInteger x = new BigInteger("1");
		for(;!x.equals(identidad);){
			if( x.gcd(identidad).equals(BigInteger.ONE) ){
				ret.add(x.add(BigInteger.ZERO));
			}
			x = x.add(BigInteger.ONE);
		}
		return ret;
	}

	private void inmigrar(Migracion nx) throws Exception{
		debug("Nueva inmigracion: "+nx.obtenerMigrante().obtenerIdentidad());
		cedulas.remove(nx.obtenerMigrante().obtenerIdentidad());
		ladrones.remove(nx.obtenerMigrante().obtenerIdentidad());
		habitantes.remove(nx.obtenerMigrante().obtenerIdentidad());
		posicion[nx.obtenerOrigen()[0]][nx.obtenerOrigen()[1]].remove(nx.obtenerMigrante().obtenerIdentidad());
		String tipo = "INMIGRACION";
		if( nx.obtenerMigrante().obtenerTipo().equals("LADRON") ){
			tipo += "L";
		}else{
			tipo += "C";
		}	
		Mensajero.getInstance(null).mensajeNuevo(new Migracion(tipo, null, null, null));
	}

	public ArrayList<Agente> obtenerAHabitantes(int x, int y){
		ArrayList <Agente> personas = new ArrayList<Agente>();
		for (BigInteger key : posicion[x][y].keySet()){
			personas.add(habitantes.get(key));
		}
		return personas;
	}

	public int cantidadHabitantes(int i,int j){
		return posicion[i][j].size();
	}

	private void nuevaInmigracion(Migracion nx){
		String tipo = "Ciudadano";
		if( nx.obtenerTipo().equals("INMIGRACIONL") || nx.obtenerTipo().equals("DEPORTADOL") ){
			tipo = "Ladron";
		}
		nuevoHabitante(tipo);
		debug("Nueva entrada de inmigrante: "+nx.obtenerTipo());
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
	 * @param datos:  0 -> dimension ; 1 -> ladrones ; 2 -> ciudadanos ; 3 -> policias 
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

	private void debug(String msj){
		System.out.println("CIUDAD: "+msj);
	}

}
