/**
 * Entrada do programa:
 * <ul>
 *   <li>Sem {@code --cli} → interface gráfica <strong>Swing</strong> (só JDK, sem JavaFX).</li>
 *   <li>{@code --cli} → modo console: {@code --cli [entrada.png] [x] [y]}</li>
 * </ul>
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && "--cli".equals(args[0])) {
            String[] rest = new String[args.length - 1];
            System.arraycopy(args, 1, rest, 0, rest.length);
            MainConsole.main(rest);
        } else {
            FloodFillSwingApp.iniciar();
        }
    }
}
