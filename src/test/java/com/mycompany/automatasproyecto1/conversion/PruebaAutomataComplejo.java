
package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;

public class PruebaAutomataComplejo {

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

        afd.agregarTransicion(new Transicion(q0, q1, "a"));
        afd.agregarTransicion(new Transicion(q0, q0, "b"));
        afd.agregarTransicion(new Transicion(q1, q1, "a"));
        afd.agregarTransicion(new Transicion(q1, q2, "b"));
        afd.agregarTransicion(new Transicion(q2, q1, "a"));
        afd.agregarTransicion(new Transicion(q2, q0, "b"));

        ResultadoConversion resultado =
                new ConversorRegex().convertirConPasos(afd);
        ExpresionRegular regex = resultado.getExpresion();
        VerificadorEquivalencia verificador = new VerificadorEquivalencia();

        System.out.println("AFD: acepta cadenas que terminan en 'ab'");
        System.out.println("Regex generada: " + regex);
        System.out.println("Etapas guardadas: " + resultado.getPasos().size());

        String[] muestras = {"", "a", "b", "ab", "aab", "bab",
                             "abab", "aba", "abb", "bbb", "bbab"};

        for (String cadena : muestras) {
            comprobar(afd, regex, verificador, cadena);
            System.out.println("OK - '" + cadena + "' | Aceptada: "
                    + cadena.endsWith("ab"));
        }

        int total = 0;
        for (int longitud = 0; longitud <= 6; longitud++) {
            int cantidad = 1 << longitud;

            for (int numero = 0; numero < cantidad; numero++) {
                StringBuilder cadena = new StringBuilder();

                for (int posicion = longitud - 1; posicion >= 0; posicion--) {
                    cadena.append(((numero >> posicion) & 1) == 0 ? 'a' : 'b');
                }

                comprobar(afd, regex, verificador, cadena.toString());
                total++;
            }
        }

        if (total != 127 || resultado.getPasos().size() != 4
                || resultado.getPasos().get(0).getEstados().size() != 5
                || resultado.getPasos().get(3).getEstados().size() != 2
                || afd.getEstados().size() != 3
                || afd.getTransiciones().size() != 6
                || afd.getEstadoInicial() != q0
                || !afd.getEstadosAceptacion().contains(q2)) {
            throw new AssertionError("Fallo en pasos o se modifico el AFD.");
        }

        System.out.println("Cadenas comprobadas: " + total);
        System.out.println("PRUEBA CORRECTA: caminos, ciclos y conversion.");
    }

    private static void comprobar(Automata afd, ExpresionRegular regex,
            VerificadorEquivalencia verificador, String cadena) {

        boolean esperado = cadena.endsWith("ab");
        boolean aceptaAFD = verificador.aceptaAFD(afd, cadena);
        boolean aceptaRegex = verificador.aceptaRegex(regex, cadena);

        if (aceptaAFD != esperado || aceptaRegex != esperado) {
            throw new AssertionError("Error con la cadena: '" + cadena
                    + "' | AFD=" + aceptaAFD + " | Regex=" + aceptaRegex);
        }
    }
}
