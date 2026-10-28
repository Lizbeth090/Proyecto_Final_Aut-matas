
package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;

public class PruebaVacioEpsilon {

    public static void main(String[] args) {

        Automata sinFinales = new Automata();
        Estado q0 = new Estado("q0", 0, 0);

        sinFinales.agregarEstado(q0);
        sinFinales.setEstadoInicial(q0);
        sinFinales.agregarTransicion(new Transicion(q0, q0, "a"));

        probar(sinFinales, "CONJUNTO VACIO",
                new String[]{"", "a", "aa", "b"},
                new boolean[]{false, false, false, false});

        Automata soloVacia = new Automata();
        Estado q1 = new Estado("q1", 0, 0);

        soloVacia.agregarEstado(q1);
        soloVacia.setEstadoInicial(q1);
        soloVacia.agregarEstadoAceptacion(q1);

        probar(soloVacia, "CADENA VACIA",
                new String[]{"", "a", "aa", "b"},
                new boolean[]{true, false, false, false});

        System.out.println("PRUEBA CORRECTA: epsilon y conjunto vacio.");
    }

    private static void probar(Automata afd, String nombre,
            String[] cadenas, boolean[] esperados) {

        ExpresionRegular regex = new ConversorRegex().convertir(afd);
        VerificadorEquivalencia verificador =
                new VerificadorEquivalencia();

        System.out.println("\n" + nombre);
        System.out.println("Regex obtenida: " + regex);

        for (int i = 0; i < cadenas.length; i++) {

            boolean aceptaAFD =
                    verificador.aceptaAFD(afd, cadenas[i]);

            boolean aceptaRegex =
                    verificador.aceptaRegex(regex, cadenas[i]);

            if (aceptaAFD != esperados[i]
                    || aceptaRegex != esperados[i]) {
                throw new AssertionError(
                        "Error con cadena: '" + cadenas[i] + "'");
            }

            System.out.println("OK - '" + cadenas[i]
                    + "' | AFD: " + aceptaAFD
                    + " | Regex: " + aceptaRegex);
        }
    }
}
