package mensajes;

import agentes.Policia;

public class Patrullar implements Mensaje {

	private String tipo;
	private int posX,posY;
	private Policia policia;
	
	public Patrullar(String tp,Policia ag,int x,int y){
		policia = ag;
		tipo = tp;
		posX = x;
		posY = y;
	}
	
	public Policia obtenerPolicia(){
		return policia;
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
