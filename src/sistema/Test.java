package sistema;

public class Test {
	
	public static void main(String[] args){
		try{
			Comunidad cm = new Comunidad();
		}catch(Exception ex){
			System.out.println("NO SE HA PODIDO INCIAR EL SISTEMA");
			ex.printStackTrace();
		}
			
	}

}
