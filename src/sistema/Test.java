package sistema;

import java.util.LinkedList;

import javax.crypto.CipherInputStream;

public class Test {
	
	public static void main(String[] args){
		try{
			long[] datos = {5,5,5};
			LinkedList<String> direcciones = new LinkedList<>();
			direcciones.add("192.168.2.9");
			Ciudad ct = Ciudad.getInstance(datos, direcciones);
		}catch(Exception ex){
			System.out.println("NO SE HA PODIDO INCIAR EL SISTEMA");
			ex.printStackTrace();
		}
			
	}

}
