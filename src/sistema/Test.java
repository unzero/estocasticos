package sistema;

import java.security.SecureRandom;
import java.util.LinkedList;

import javax.crypto.CipherInputStream;

import org.apache.commons.math3.distribution.BetaDistribution;

public class Test {
	
	public static void main(String[] args){
		/*try{
			int[] datos = {10,5,5};
			LinkedList<String> direcciones = new LinkedList<>();
			direcciones.add("192.168.2.10");
			Ciudad ct = Ciudad.getInstance(datos, direcciones);
			Thread th_ct = new Thread(ct);
			th_ct.start();
		}catch(Exception ex){
			System.out.println("NO SE HA PODIDO INCIAR EL SISTEMA");
			ex.printStackTrace();
		}*/
		BetaDistribution bb = new BetaDistribution(2, 0.9);
		for(int x=0;x<100;++x){
			double w = new SecureRandom().nextDouble();
			double k = new SecureRandom().nextDouble();
			//System.out.println(w+" -- "+bb.density(w));k
			double t0 = bb.density(w);
			double t1 = bb.density(k);
			System.out.println(w+" -- "+k+" == "+t0*t1*bb.density(new SecureRandom().nextDouble()));
			//System.out.println(bb.cumulativeProbability(w));
		}
			
	}

}
