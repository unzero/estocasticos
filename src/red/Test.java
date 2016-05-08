package red;

public class Test {

	public static void main(String[] args){
		Thread servidor = new Thread(new Servidor());
		servidor.start();
		Cliente nuevoCliente = new Cliente(new MensajeDeRed("Esto es una prueba"),"192.168.2.10",5001);
		nuevoCliente.enviar();
		nuevoCliente.enviar();
	}
}
