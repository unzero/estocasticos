package sistema;

public class Punto {
	
	public int x,y;
	
	public Punto(int i,int j){
		x = i;
		y = j;
	}
	
	@Override
	public String toString(){
		return "Punto: "+x+","+y;
	}
	
	public int obtenerX(){
		return x;
	}
	
	public int obtenerY(){
		return y;
	}
	
}
