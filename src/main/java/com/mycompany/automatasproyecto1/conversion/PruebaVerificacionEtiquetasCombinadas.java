
package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;

public class PruebaVerificacionEtiquetasCombinadas {

    public static void main(String[] args) {

        Automata afd = new Automata();

        Estado q0 = new Estado("q0", 0, 0);
        Estado q1 = new Estado("q1", 100, 0);

        afd.agregarEstado(q0);
        afd.agregarEstado(q1);
        afd.setEstadoInicial(q0);
        afd.agregarEstadoAceptacion(q1);

        // Una sola flecha con dos simbolos
        afd.agregarTransicion(new Transicion(q0, q1, "0,1"));

        ExpresionRegular regex =
                new ConversorRegex().convertir(afd);

        VerificadorEquivalencia verificador =
                new VerificadorEquivalencia();

        System.out.println("Regex obtenida: " + regex);

        String[] cadenas = {"0", "1", "01", "10", "", "2"};
        boolean[] esperados = {true, true, false, false, false, false};

        for (int i = 0; i < cadenas.length; i++) {

            boolean resultadoAFD =
                    verificador.aceptaAFD(afd, cadenas[i]);

            boolean resultadoRegex =
                    verificador.aceptaRegex(regex, cadenas[i]);

            if (resultadoAFD != esperados[i]
                    || resultadoRegex != esperados[i]) {

                throw new AssertionError(
                        "Error con la cadena: " + cadenas[i]);
            }

            System.out.println("OK - '" + cadenas[i]
                    + "' | AFD: " + resultadoAFD
                    + " | Regex: " + resultadoRegex);
        }

        // Comprobar que la flecha original no cambio
        if (afd.getTransiciones().size() != 1
                || !afd.getTransiciones().get(0)
                        .getSimbolo().equals("0,1")) {

            throw new AssertionError(
                    "Se modifico la transicion original.");
        }

        System.out.println(
                "PRUEBA CORRECTA: verificacion de etiquetas combinadas.");
    }
}
