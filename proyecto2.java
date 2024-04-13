
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class proyecto2 {
    public static void main(String[] args) {
        Stack<Token> PilaOp = new Stack<>();
        Stack<Token> PilaEst = new Stack<>();
        Stack<Integer> PilaDir = new Stack<>();
        ArrayList<Token> VCI = new ArrayList<>(); // ArrayList para almacenar el VCI
        String nombreArchivo = "entrada.txt";
        Token[] tokens = procesarArchivo(nombreArchivo);
        for (int i = 0; i < tokens.length; i++) {
            Token token = tokens[i];
            if (Prioridad(token.lexema) != -1) { // Si el token es un operador
                if (token.lexema.equals(";")) {
                    while (!PilaOp.isEmpty()) {
                        VCI.add(PilaOp.pop());
                    }
                } else if (token.lexema.equals("(")) {
                    PilaOp.push(token);
                } else if (token.lexema.equals(")")) {
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
                    || token.token.equals("-10")
                    || token.lexema.equals(";"))) { // Añadir a VCI si no es "Inicio", "Fin", "Si" o ";"
                VCI.add(token);
            } else if (token.lexema.equals(";")) {
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
                        PilaEst.push(token);
                        int posicion = PilaDir.pop();
                        Token dir = new Token(String.valueOf(VCI.size() + 2), null, null, null);
                        VCI.set(posicion, dir);
                        int direccion = VCI.size();
                        VCI.add(null);
                        PilaDir.push(direccion);
                        VCI.add(tokens[i + 1]);
                    } else  if ((!PilaDir.isEmpty() && i < tokens.length - 1 && !tokens[i + 1].token.equals("-7"))) {
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

                }

            } else if (token.token.equals("-9")) {// repetir
                PilaEst.push(token);
                PilaDir.push(VCI.size());
            } else if (token.token.equals("-10")) {// hasta
                Token temporal = token;
                while (!tokens[i].token.equals("-75")) {
                    while (!PilaOp.isEmpty()) {
                        if (Prioridad(token.lexema) != -1) { // Si el token es un operador
                            if (token.lexema.equals(";")) {
                                while (!PilaOp.isEmpty()) {
                                    VCI.add(PilaOp.pop());
                                }
                            } else if (token.lexema.equals("(")) {
                                PilaOp.push(token);
                            } else if (token.lexema.equals(")")) {
                                while (!PilaOp.peek().lexema.equals("(")) {
                                    VCI.add(PilaOp.pop());
                                }
                                PilaOp.pop();
                            } else {
                                while (!PilaOp.isEmpty()
                                        && Prioridad(token.lexema) <= Prioridad(PilaOp.peek().lexema)) {
                                    VCI.add(PilaOp.pop());
                                }
                                PilaOp.push(token);
                            }
                        } else if (!token.lexema.equals(";")) {
                            VCI.add(token);
                        } else if (token.lexema.equals(";")) {
                            while (!PilaOp.isEmpty()) {
                                VCI.add(PilaOp.pop());
                            }
                        }
                    }
                    i++;
                }
                if (!PilaDir.isEmpty()) {
                    int posicion = PilaDir.pop();
                    Token dir = new Token(String.valueOf(posicion), null, null, null);
                    VCI.add(dir);
                    VCI.add(temporal);
                }
            }
        }
        for (int i = 0; i < VCI.size(); i++) {
            Token token = VCI.get(i);
            System.out.println(token);
        }

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
                return -1; // Si no es un operador o palabra reservada válida, retornar -1
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
}
