package lab4;

import lab4.client.GomokuClient;
import lab4.data.GomokuGameState;
import lab4.gui.GomokuGUI;

public class GomokuMain {
    public static void main(String[] args) {
        int port = 13337;
        if(args.length > 0) {
            String p = args[0];
            if (p.matches("[0-9]+")) {
                port = Integer.parseInt(p);
            }
        }

        run(port);
    }

    /**
     * Runs the program based on port
     *
     * @param port which port to listen on for incomming connections
     */
    private static void run(int port) {
        GomokuClient gc = new GomokuClient(port);
        GomokuGameState ggs = new GomokuGameState(gc);
        GomokuGUI gg = new GomokuGUI(ggs, gc);
    }
}
