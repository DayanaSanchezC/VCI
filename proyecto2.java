
import java.io.*;
import java.util.*;
import javax.swing.*;

public class proyecto2 {
    public static void main(String[] args) {
        Stack<Token> PilaOp = new Stack<>();
        Stack<Token> PilaEst = new Stack<>();
        Stack<Integer> PilaDir = new Stack<>();
        ArrayList<Token> VCI = new ArrayList<>(); 
        String nombreArchivo = seleccionarArchivo();
        Token[] tokens = procesarArchivo(nombreArchivo);
        boolean inicioEncontrado = false;

        for (int i = 0; i < tokens.length; i++) {
            Token token = tokens[i];
            if (token.token.equals("-2")) {
                inicioEncontrado = true;
                continue; 
            }
            if (!inicioEncontrado) {
                continue;
            }
            if (Prioridad(token.lexema) != -1) { // Si el token es un operador
                if (token.token.equals("-75")) {//;
                    while (!PilaOp.isEmpty()) {
                        VCI.add(PilaOp.pop());
                    }
                } else if (token.token.equals("-73")) {//(
                    PilaOp.push(token);
                } else if (token.token.equals("-74")) {//)
                    while (!PilaOp.peek().lexema.equals("(")) {
                        VCI.add(PilaOp.pop());
                    }
                    PilaOp.pop();
                } else {
                    while (!PilaOp.isEmpty() && Prioridad(token.lexema) <= Prioridad(PilaOp.peek().lexema)) {
                        VCI.add(PilaOp.pop());
                    }
                    PilaOp.push(token);
                }
            } else if (!(token.token.equals("-2") || token.token.equals("-7") || token.token.equals("-16")
                    || token.token.equals("-3") || token.token.equals("-6") || token.token.equals("-9")
                    || token.token.equals("-10") || token.token.equals("-8") || token.token.equals("-17")
                    || token.token.equals("-75"))) { 
                VCI.add(token);
            } else if (token.token.equals("-75")) {
                while (!PilaOp.isEmpty()) {
                    VCI.add(PilaOp.pop());
                }
            }
            if (token.token.equals("-6")) {// si
                PilaEst.push(token);
            } else if (token.token.equals("-16")) {// entonces
                while (!PilaOp.isEmpty()) {
                    VCI.add(PilaOp.pop());
                }
                int direccion = VCI.size();
                VCI.add(null);
                PilaDir.push(direccion);
                VCI.add(token);
            } else if (token.token.equals("-3")) {// fin
                if (!PilaEst.isEmpty()) {
                    Token ultimo = PilaEst.peek();
                    if (ultimo.token.equals("-6") || ultimo.token.equals("-7")) {
                        if (!PilaEst.isEmpty())
                            PilaEst.pop();
                        if ((!PilaDir.isEmpty() && i < tokens.length - 1 && tokens[i + 1].token.equals("-7"))) {// sino
                            PilaEst.push(tokens[i + 1]);
                            int posicion = PilaDir.pop();
                            Token dir = new Token(String.valueOf(VCI.size() + 2), null, null, null);
                            VCI.set(posicion, dir);
                            int direccion = VCI.size();
                            VCI.add(null);
                            PilaDir.push(direccion);
                            VCI.add(tokens[i + 1]);
                        } else {// no esta entrando
                            if (!PilaDir.isEmpty()) {
                                int posicion = PilaDir.pop();
                                Token dir = new Token(String.valueOf(VCI.size()), null, null, null);
                                VCI.set(posicion, dir);
                            }
                        }
                    }
                    if (ultimo.token.equals("-9")) {
                        if (!PilaEst.isEmpty())
                            PilaEst.pop();
                    }
                    if (ultimo.token.equals("-8")) {
                        PilaEst.pop();
                        VCI.set(PilaDir.pop(), new Token(String.valueOf(VCI.size() + 2), null, null, null));
                        VCI.add(new Token(String.valueOf(PilaDir.pop()), null, null, null));
                        VCI.add(new Token("FIN-MIENTRAS", null, null, null));

                    }
                }
            } else if (token.token.equals("-9")) {// repetir
                PilaEst.push(token);
                PilaDir.push(VCI.size());
            } else if (token.token.equals("-10")) { // hasta
                Token temporal = token;
                List<Token> condicionUntil = new ArrayList<>(); 
                i++;
                while (!tokens[i].token.equals("-75")) {
                    condicionUntil.add(tokens[i]);
                    i++;
                }
                for (Token condToken : condicionUntil) {
                    if (Prioridad(condToken.lexema) != -1) { 
                        if (condToken.token.equals("-75")) {
                            while (!PilaOp.isEmpty()) {
                                VCI.add(PilaOp.pop());
                            }
                        } else if (condToken.token.equals("-73")) {
                            PilaOp.push(condToken);
                        } else if (condToken.token.equals("-74")) {
                            while (!PilaOp.peek().token.equals("-73")) {
                                VCI.add(PilaOp.pop());
                            }
                            PilaOp.pop();
                        } else {
                            while (!PilaOp.isEmpty()
                                    && Prioridad(condToken.lexema) <= Prioridad(PilaOp.peek().lexema)) {
                                VCI.add(PilaOp.pop());
                            }
                            PilaOp.push(condToken);
                        }
                    } else if (!condToken.token.equals("-75") || !condToken.token.equals("-10")) {
                        VCI.add(condToken);
                    }
                }
                if (!PilaDir.isEmpty()) {
                    int posicion = PilaDir.pop();
                    Token dir = new Token(String.valueOf(posicion), null, null, null);
                    VCI.add(dir);
                    VCI.add(temporal);
                }
            } else if (token.token.equals("-8")) {// mientras
                PilaEst.push(token);
                PilaDir.push(VCI.size());
            } else if (token.token.equals("-17")) {// hacer
                while (!PilaOp.isEmpty()) {
                    VCI.add(PilaOp.pop());
                }
                PilaDir.push(VCI.size());
                VCI.add(null);
                VCI.add(token);
            }
    
        }
        escribirArchivo(VCI, "VCI.txt");
       
    }

    private static int Prioridad(String lexema) {
        switch (lexema) {
            case "*":
            case "/":
            case "%":
                return 60;
            case "+":
            case "-":
                return 50;
            case "<":
            case ">":
            case "==":
            case ">=":
            case "<=":
            case "!=":
                return 40;
            case "!":
                return 30;
            case "&&":
                return 20;
            case "||":
                return 10;
            case "=":
                return 0;
            case "(":
                return 0;
            case ")":
                return 0;
            default:
                return -1; 
        }
    }

    public static String seleccionarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int seleccion = fileChooser.showOpenDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            // El usuario canceló la selección, podrías manejarlo de alguna manera
            return null;
        }
    }

    public static Token[] procesarArchivo(String nombreArchivo) {
        Token[] tokens = null;
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            int numLineas = contarLineas(nombreArchivo);
            tokens = new Token[numLineas];
            int i = 0;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",", 4);
                if (partes.length != 4) {
                    System.err.println("Error, se espera que este en el siguiente formato\n"+
                    "Lexema,Token,Posicion,Linea");                    
                    continue; 
                }
                String lexema = partes[0];
                String token = partes[1];
                String pts = partes[2];
                String lineaNum = partes[3];
                tokens[i] = new Token(lexema, token, pts, lineaNum);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    public static int contarLineas(String nombreArchivo) throws IOException {
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            while (reader.readLine() != null)
                lineCount++;
        }
        return lineCount;
    }


    public static void escribirArchivo(List<Token> VCI, String nombreArchivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Token token : VCI) {
                writer.write(token.toString());
                writer.newLine(); 
            }
            System.out.println("Archivo VCI generado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
