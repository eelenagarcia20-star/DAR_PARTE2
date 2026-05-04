import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMI_juego_servidor {

    public static void main(String[] args) {
        try {
            JuegoImplement obj = new JuegoImplement();

            Registry registry = LocateRegistry.createRegistry(55555);

            registry.rebind("Juego", obj);

            System.out.println("Servidor listo.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}