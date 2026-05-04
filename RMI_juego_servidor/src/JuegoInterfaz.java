import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JuegoInterfaz extends Remote {
    String iniciarJuego(String idCliente) throws RemoteException;
    String realizarMovimiento(String idCliente,int posicion) throws RemoteException;
    String obtenerEstado(String idCliente) throws RemoteException;
}
