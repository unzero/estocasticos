package agentes;

import sistema.Mensaje;

public class Robo implements Mensaje {

	private int posX;
	private int posY;
	
	public Robo(int x,int y){
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
		return "ROBO";
	}

}
