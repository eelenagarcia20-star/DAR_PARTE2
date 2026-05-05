import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMI_juego_servidor {

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "192.168.50.10");

            JuegoImplement obj = new JuegoImplement();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("Juego", obj);

            System.out.println("Servidor listo.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}