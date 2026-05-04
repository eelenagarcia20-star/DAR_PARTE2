import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;
import javax.swing.*;

public class RMI_juego_cliente {

    private JuegoInterfaz stub;
    private String idCliente = UUID.randomUUID().toString();

    private JFrame frame;
    private JButton[] botones = new JButton[9];
    private JLabel lblEstado;

    public RMI_juego_cliente() {
        crearGUI();
        conectarServidor();
    }

    private void crearGUI() {
        frame = new JFrame("Cliente RMI - Tres en Raya");
        frame.setSize(350, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        lblEstado = new JLabel("Conectando con el servidor...", SwingConstants.CENTER);
        frame.add(lblEstado, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 3));

        for (int i = 0; i < 9; i++) {
            int pos = i;
            botones[i] = new JButton("");
            botones[i].setFont(new Font("Arial", Font.BOLD, 40));
            botones[i].setEnabled(false);

            botones[i].addActionListener(e -> jugar(pos));

            panel.add(botones[i]);
        }

        frame.add(panel, BorderLayout.CENTER);

        JButton btnStart = new JButton("START");
        btnStart.addActionListener(e -> iniciarPartida());
        frame.add(btnStart, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void conectarServidor() {
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 55555);
            stub = (JuegoInterfaz) reg.lookup("Juego");

            lblEstado.setText("Conectado. Pulsa START.");
        } catch (Exception e) {
            lblEstado.setText("Error de conexión con el servidor.");
            e.printStackTrace();
        }
    }

    private void iniciarPartida() {
        try {
            stub.iniciarJuego(idCliente);

            String estado = stub.obtenerEstado(idCliente);
            procesarEstado(estado);

            lblEstado.setText("Tu turno: juega con X");
        } catch (Exception e) {
            lblEstado.setText("Error al iniciar partida.");
            e.printStackTrace();
        }
    }

    private void jugar(int pos) {
        try {
            String respuesta = stub.realizarMovimiento(idCliente, pos);

            if (respuesta.startsWith("ERROR")) {
                lblEstado.setText(respuesta);
                return;
            }

            String estado = stub.obtenerEstado(idCliente);
            procesarEstado(estado);

        } catch (Exception e) {
            lblEstado.setText("Error de comunicación.");
            e.printStackTrace();
        }
    }

    private void dibujarTablero(String datos) {
        String[] celdas = datos.split(",");

        for (int i = 0; i < 9; i++) {
            botones[i].setText(celdas[i].equals("_") ? "" : celdas[i]);
            botones[i].setEnabled(celdas[i].equals("_"));

            if (celdas[i].equals("X")) {
                botones[i].setBackground(Color.CYAN);
            } else if (celdas[i].equals("O")) {
                botones[i].setBackground(Color.ORANGE);
            } else {
                botones[i].setBackground(null);
            }
        }
    }

    private void bloquearBotones() {
        for (JButton boton : botones) {
            boton.setEnabled(false);
        }
    }
    private void procesarEstado(String respuesta) {
        if (respuesta.startsWith("ERROR")) {
            lblEstado.setText(respuesta);
            return;
        }

        if (respuesta.startsWith("RESULT:")) {
            String[] partes = respuesta.split("\\|");
            String resultado = partes[0].split(":")[1];
            String tablero = partes[1];

            dibujarTablero(tablero);

            if (resultado.equals("X")) {
                lblEstado.setText("Has ganado.");
                JOptionPane.showMessageDialog(frame, "Has ganado.");
            } else if (resultado.equals("O")) {
                lblEstado.setText("Has perdido.");
                JOptionPane.showMessageDialog(frame, "Has perdido.");
            } else {
                lblEstado.setText("Empate.");
                JOptionPane.showMessageDialog(frame, "Empate.");
            }

            bloquearBotones();

        } else if (respuesta.startsWith("STATE:")) {
            String tablero = respuesta.split(":")[1];
            dibujarTablero(tablero);
            lblEstado.setText("Tu turno: juega con X");
        }
    }
    public static void main(String[] args) {
        new RMI_juego_cliente();
    }
}