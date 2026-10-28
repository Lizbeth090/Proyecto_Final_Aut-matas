
package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;

public class PruebaPasosConversion {

    public static void main(String[] args) {
        Automata original = new Automata();

        Estado q0 = new Estado("q0", 0, 0);
        Estado q1 = new Estado("q1", 100, 0);

        original.agregarEstado(q0);
        original.agregarEstado(q1);
        original.setEstadoInicial(q0);
        original.agregarEstadoAceptacion(q1);
        original.agregarTransicion(new Transicion(q0, q1, "a"));

        ResultadoConversion resultado =
                new ConversorRegex().convertirConPasos(original);

        for (int i = 0; i < resultado.getPasos().size(); i++) {
            PasoConversion paso = resultado.getPasos().get(i);

            System.out.println("PASO " + i + ": "
                    + paso.getDescripcion());
            System.out.println("Estados: " + paso.getEstados());

            for (NormalizadorAutomata.Arista t
                    : paso.getTransiciones()) {
                System.out.println("  " + t);
            }
        }

        System.out.println("Resultado final: "
                + resultado.getExpresion());

        if (resultado.getPasos().size() != 3
                || resultado.getPasos().get(0).getEstados().size() != 4
                || resultado.getPasos().get(1).getEstados().size() != 3
                || resultado.getPasos().get(2).getEstados().size() != 2
                || resultado.getPasos().get(2).getTransiciones().size() != 1
                || !resultado.getExpresion().toString().equals("a")
                || original.getEstados().size() != 2
                || original.getTransiciones().size() != 1) {
            throw new AssertionError("La prueba falló");
        }

        System.out.println(
                "PRUEBA CORRECTA: se guardaron los pasos sin modificar el AFD.");
    }
}
