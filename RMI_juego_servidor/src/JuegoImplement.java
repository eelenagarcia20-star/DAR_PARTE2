import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class JuegoImplement extends UnicastRemoteObject implements JuegoInterfaz {

    private Map<String, Partida> partidas = new HashMap<>();

    public JuegoImplement() throws RemoteException {
        super();
    }

    @Override
    public synchronized String iniciarJuego(String idCliente) throws RemoteException {
        Partida p = new Partida();
        partidas.put(idCliente, p);
        System.out.println("Partida iniciada para cliente: " + idCliente);
        return String.join(",", p.tablero);
    }

    @Override
    public synchronized String realizarMovimiento(String idCliente, int pos) throws RemoteException {
        Partida p = partidas.get(idCliente);

        if (p == null) {
            return "ERROR:No hay partida iniciada";
        }

        if (pos < 0 || pos > 8 || !p.juegoActivo || !p.tablero[pos].equals("_")) {
            return "ERROR:Movimiento no valido";
        }

        p.tablero[pos] = "X";

        String res = p.comprobarEstado();

        if (res == null) {
            p.movimientoIA();
            res = p.comprobarEstado();
        }

        String estadoStr = String.join(",", p.tablero);

        if (res != null) {
            p.juegoActivo = false;
            return "RESULT:" + res + "|" + estadoStr;
        }

        return "STATE:" + estadoStr;
    }

    private static class Partida {
        String[] tablero = new String[9];
        boolean juegoActivo = true;

        Partida() {
            Arrays.fill(tablero, "_");
        }

        void movimientoIA() {
            List<Integer> libres = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                if (tablero[i].equals("_")) {
                    libres.add(i);
                }
            }

            if (!libres.isEmpty()) {
                tablero[libres.get(new Random().nextInt(libres.size()))] = "O";
            }
        }

        String comprobarEstado() {
            int[][] combos = {
                {0,1,2}, {3,4,5}, {6,7,8},
                {0,3,6}, {1,4,7}, {2,5,8},
                {0,4,8}, {2,4,6}
            };

            for (int[] c : combos) {
                if (!tablero[c[0]].equals("_") &&
                    tablero[c[0]].equals(tablero[c[1]]) &&
                    tablero[c[1]].equals(tablero[c[2]])) {
                    return tablero[c[0]];
                }
            }

            if (!Arrays.asList(tablero).contains("_")) {
                return "EMPATE";
            }

            return null;
        }
    }
}