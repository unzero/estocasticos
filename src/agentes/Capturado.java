package agentes;

import sistema.Mensaje;

public class Capturado implements Mensaje {
	
	private int posX,posY;
	
	public Capturado(int x,int y){
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
		return "CAPTURADO";
	}

}
