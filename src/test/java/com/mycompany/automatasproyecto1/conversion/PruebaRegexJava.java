
package com.mycompany.automatasproyecto1.conversion;

import java.util.regex.Pattern;

public class PruebaRegexJava {

    public static void main(String[] args) {

        ExpresionRegular a = ExpresionRegular.simbolo("a");
        ExpresionRegular b = ExpresionRegular.simbolo("b");

        probar(a, "a", true);
        probar(a, "b", false);

        ExpresionRegular union = ExpresionRegular.union(a, b);
        probar(union, "a", true);
        probar(union, "b", true);
        probar(union, "ab", false);

        ExpresionRegular ab = ExpresionRegular.concatenar(a, b);
        probar(ab, "ab", true);
        probar(ab, "ba", false);

        ExpresionRegular estrella = ExpresionRegular.estrella(a);
        probar(estrella, "", true);
        probar(estrella, "aaa", true);
        probar(estrella, "b", false);

        probar(ExpresionRegular.epsilon(), "", true);
        probar(ExpresionRegular.vacio(), "", false);
        probar(ExpresionRegular.simbolo("+"), "+", true);

        System.out.println("PRUEBA CORRECTA: Regex compatible con Java.");
    }

    private static void probar(ExpresionRegular expresion,
            String cadena, boolean esperado) {

        String regex = expresion.aRegexJava();
        boolean resultado = Pattern.matches(regex, cadena);

        if (resultado != esperado) {
            throw new AssertionError("Fallo con cadena: " + cadena);
        }

        System.out.println("OK - Cadena: '" + cadena
                + "' | Aceptada: " + resultado);
    }
}

