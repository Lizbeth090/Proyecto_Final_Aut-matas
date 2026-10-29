
package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;

public class PruebaSimbolosEspeciales {

    public static void main(String[] args) {

        Automata afd = new Automata();

        Estado q0 = new Estado("q0", 0, 0);
        Estado q1 = new Estado("q1", 100, 0);
        Estado q2 = new Estado("q2", 200, 0);

        afd.agregarEstado(q0);
        afd.agregarEstado(q1);
        afd.agregarEstado(q2);

        afd.setEstadoInicial(q0);
        afd.agregarEstadoAceptacion(q2);

        afd.agregarTransicion(new Transicion(q0, q1, "+"));
        afd.agregarTransicion(new Transicion(q1, q2, "*"));

        ExpresionRegular regex = new ConversorRegex().convertir(afd);
        VerificadorEquivalencia verificador =
                new VerificadorEquivalencia();

        System.out.println("Regex matematica: " + regex);
        System.out.println("Regex Java: " + regex.aRegexJava());

        String[] cadenas = {"+*", "+", "*", "++", "**", "", "a"};
        boolean[] esperados = {true, false, false, false, false, false, false};

        for (int i = 0; i < cadenas.length; i++) {

            boolean aceptaAFD =
                    verificador.aceptaAFD(afd, cadenas[i]);

            boolean aceptaRegex =
                    verificador.aceptaRegex(regex, cadenas[i]);

            if (aceptaAFD != esperados[i]
                    || aceptaRegex != esperados[i]) {
                throw new AssertionError(
                        "Error con la cadena: " + cadenas[i]);
            }

            System.out.println("OK - '" + cadenas[i]
                    + "' | AFD: " + aceptaAFD
                    + " | Regex: " + aceptaRegex);
        }

        if (afd.getEstados().size() != 3
                || afd.getTransiciones().size() != 2) {
            throw new AssertionError("El AFD original fue modificado.");
        }

        System.out.println(
                "PRUEBA CORRECTA: simbolos especiales.");
    }
}
