package mensajes;

public class Seguridad implements Mensaje {

	private String tipo;
	private int posX,posY;
	
	public Seguridad(String tp,int x,int y){
		tipo = tp;
		posX = x;
		posY = y;
	}
	
	public int obtenerX(){
		return posX;
	}
	
	public int obtenerY(){
		return posY;
	}
	
	@Override
	public String obtenerTipo() {
		return tipo;
	}

}
