package mensajes;

public class Postal implements Mensaje{
	
	private String mensaje;
	
	public Postal(String msj){
		mensaje = msj;
	}
	
	@Override
	public String obtenerTipo(){
		return "POSTAL";
	}
	
	@Override
	public String toString(){
		return "Postal: "+mensaje;
	}

}
