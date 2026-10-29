
package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;

public class PruebaVariosFinales {

    public static void main(String[] args) {

        Automata afd = new Automata();

        Estado q0 = new Estado("q0", 0, 0);
        Estado q1 = new Estado("q1", 100, 0);
        Estado q2 = new Estado("q2", 100, 100);

        afd.agregarEstado(q0);
        afd.agregarEstado(q1);
        afd.agregarEstado(q2);

        afd.setEstadoInicial(q0);
        afd.agregarEstadoAceptacion(q1);
        afd.agregarEstadoAceptacion(q2);

        afd.agregarTransicion(new Transicion(q0, q1, "a"));
        afd.agregarTransicion(new Transicion(q0, q2, "b"));

        NormalizadorAutomata.GrafoNormalizado normalizado =
                new NormalizadorAutomata().normalizar(afd);

        long conexionesFinales = normalizado.getAristas()
                .stream()
                .filter(t -> t.getDestino().equals(normalizado.getAceptacion())
                        && t.getSimbolo().equals(
                                NormalizadorAutomata.EPSILON))
                .count();

        if (conexionesFinales != 2) {
            throw new AssertionError(
                    "No se conectaron los dos estados finales.");
        }

        ExpresionRegular regex = new ConversorRegex().convertir(afd);
        VerificadorEquivalencia verificador =
                new VerificadorEquivalencia();

        System.out.println("Regex obtenida: " + regex);

        String[] cadenas = {"a", "b", "", "ab", "aa", "bb"};
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

        if (afd.getEstados().size() != 3
                || afd.getTransiciones().size() != 2
                || afd.getEstadosAceptacion().size() != 2) {
            throw new AssertionError("El AFD original fue modificado.");
        }

        System.out.println(
                "PRUEBA CORRECTA: multiples estados finales.");
    }
}
