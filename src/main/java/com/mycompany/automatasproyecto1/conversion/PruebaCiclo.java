
package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;

public class PruebaCiclo {
    public static void main(String[] args) {
        Automata afd = new Automata();
        Estado q0 = new Estado("q0", 0, 0);

        afd.agregarEstado(q0);
        afd.setEstadoInicial(q0);
        afd.agregarEstadoAceptacion(q0);
        afd.agregarTransicion(new Transicion(q0, q0, "a"));

        ExpresionRegular regex = new ConversorRegex().convertir(afd);
        VerificadorEquivalencia verificador = new VerificadorEquivalencia();

        System.out.println("Regex obtenida: " + regex);

        String[] cadenas = {"", "a", "aa", "aaa", "b", "ab"};
        boolean[] esperados = {true, true, true, true, false, false};

        for (int i = 0; i < cadenas.length; i++) {
            boolean aceptaAFD = verificador.aceptaAFD(afd, cadenas[i]);
            boolean aceptaRegex = verificador.aceptaRegex(regex, cadenas[i]);

            if (aceptaAFD != esperados[i] || aceptaRegex != esperados[i]) {
                throw new AssertionError(
                        "Resultado incorrecto con: '" + cadenas[i] + "'");
            }

            System.out.println("OK - '" + cadenas[i] + "' | AFD: "
                    + aceptaAFD + " | Regex: " + aceptaRegex);
        }

        if (afd.getEstados().size() != 1
                || afd.getTransiciones().size() != 1) {
            throw new AssertionError("El AFD original fue modificado.");
        }

        System.out.println(
                "PRUEBA CORRECTA: ciclo y estrella de Kleene.");
    }
}
