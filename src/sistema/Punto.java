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
	
}
