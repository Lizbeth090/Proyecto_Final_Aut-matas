package com.mycompany.automatasproyecto1.conversion;
public final class ExpresionRegular {
    private enum Tipo {
        VACIO, EPSILON, SIMBOLO, UNION, CONCATENACION, ESTRELLA
    }
    private final Tipo tipo;
    private final String simbolo;
    private final ExpresionRegular izquierda;
    private final ExpresionRegular derecha;
    private ExpresionRegular(Tipo tipo, String simbolo,
            ExpresionRegular izquierda, ExpresionRegular derecha) {
        this.tipo = tipo;
        this.simbolo = simbolo;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }
    public static ExpresionRegular vacio() {
        return new ExpresionRegular(Tipo.VACIO, null, null, null);
    }
    public static ExpresionRegular epsilon() {
        return new ExpresionRegular(Tipo.EPSILON, null, null, null);
    }
    public static ExpresionRegular simbolo(String valor) {
        if (valor == null || valor.codePointCount(0, valor.length()) != 1
                || valor.equals("ε") || valor.equals("∅")) {
            throw new IllegalArgumentException("Símbolo inválido.");
        }
        return new ExpresionRegular(Tipo.SIMBOLO, valor, null, null);
    }
    public static ExpresionRegular union(
            ExpresionRegular a, ExpresionRegular b) {
        if (a.tipo == Tipo.VACIO) return b;
        if (b.tipo == Tipo.VACIO) return a;
        if (a.toString().equals(b.toString())) return a;
        return new ExpresionRegular(Tipo.UNION, null, a, b);
    }
    public static ExpresionRegular concatenar(
            ExpresionRegular a, ExpresionRegular b) {
        if (a.tipo == Tipo.VACIO || b.tipo == Tipo.VACIO) {
            return vacio();
        }
        if (a.tipo == Tipo.EPSILON) return b;
        if (b.tipo == Tipo.EPSILON) return a;
        return new ExpresionRegular(Tipo.CONCATENACION, null, a, b);
    }

    public static ExpresionRegular estrella(ExpresionRegular a) {
        if (a.tipo == Tipo.VACIO || a.tipo == Tipo.EPSILON) {
            return epsilon();
        }
        if (a.tipo == Tipo.ESTRELLA) return a;

        return new ExpresionRegular(Tipo.ESTRELLA, null, a, null);
    }
    private int prioridad() {
        return switch (tipo) {
            case UNION -> 1;
            case CONCATENACION -> 2;
            case ESTRELLA -> 3;
            default -> 4;
        };
    }
    private String mostrar(int prioridadPadre) {
        String texto = switch (tipo) {
            case VACIO -> "∅";
            case EPSILON -> "ε";
            case SIMBOLO -> simbolo.matches("[a-zA-Z0-9]")
                    ? simbolo : "{" + simbolo + "}";
            case UNION -> izquierda.mostrar(1) + " ∪ "
                    + derecha.mostrar(1);
            case CONCATENACION -> izquierda.mostrar(2)
                    + derecha.mostrar(2);
            case ESTRELLA -> izquierda.mostrar(3) + "*";
        };

        if (prioridad() < prioridadPadre) {
            return "(" + texto + ")";
        }
        return texto;
    }
    @Override
    public String toString() {
        return mostrar(0);
    }
    
public String aRegexJava() {
    return switch (tipo) {
        case VACIO -> "(?!)";
        case EPSILON -> "";
        case SIMBOLO -> simbolo.matches("[a-zA-Z0-9]")
                ? simbolo : java.util.regex.Pattern.quote(simbolo);
        case UNION -> "(?:" + izquierda.aRegexJava() + "|"
                + derecha.aRegexJava() + ")";
        case CONCATENACION -> izquierda.aRegexJava()
                + derecha.aRegexJava();
        case ESTRELLA -> "(?:" + izquierda.aRegexJava() + ")*";
    };
}

}
