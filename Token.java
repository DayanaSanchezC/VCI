public class Token {
    String lexema;
    String token;
    String pts;
    String linea;

    public Token(String lexema, String token, String pts, String linea) {
        this.lexema = lexema;
        this.token = token;
        this.pts = pts;
        this.linea = linea;
    }

    @Override
    public String toString() {
        return lexema + "," + token + "," + pts + "," + linea;
    }
}
