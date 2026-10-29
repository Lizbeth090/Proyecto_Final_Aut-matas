
package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;

public class PruebaVerificadorEquivalencia {

    public static void main(String[] args) {
        Automata afd = new Automata();

        Estado q0 = new Estado("q0", 0, 0);
        Estado q1 = new Estado("q1", 100, 0);

        afd.agregarEstado(q0);
        afd.agregarEstado(q1);
        afd.setEstadoInicial(q0);
        afd.agregarEstadoAceptacion(q1);
        afd.agregarTransicion(new Transicion(q0, q1, "a"));

        ExpresionRegular expresion =
                new ConversorRegex().convertir(afd);

        VerificadorEquivalencia verificador =
                new VerificadorEquivalencia();

        for (String cadena : new String[] {"a", "b", "", "aa", "ab"}) {

            boolean respuestaAFD = verificador.aceptaAFD(afd, cadena);
            boolean respuestaRegex =
                    verificador.aceptaRegex(expresion, cadena);

            System.out.println("Cadena: '" + cadena + "' | AFD: "
                    + respuestaAFD + " | Regex: " + respuestaRegex);

            if (!verificador.verificar(afd, expresion, cadena)) {
                throw new AssertionError(
                        "No coinciden para: '" + cadena + "'");
            }
        }

        if (verificador.verificar(afd,
                ExpresionRegular.simbolo("b"), "a")) {
            throw new AssertionError("No detecto una regex incorrecta.");
        }

        System.out.println("Detecto una regex incorrecta: OK");
        System.out.println(
                "PRUEBA CORRECTA: coinciden en todas las cadenas probadas.");
    }
}

