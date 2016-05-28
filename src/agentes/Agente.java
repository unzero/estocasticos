package agentes;

import java.io.Serializable;
import java.math.BigInteger;

import sistema.Mensaje;

public interface Agente extends Runnable, Serializable{
	
	public String obtenerTipo();
	public void mensajeNuevo(Mensaje msj);
	public BigInteger obtenerIdentidad();
	
}
