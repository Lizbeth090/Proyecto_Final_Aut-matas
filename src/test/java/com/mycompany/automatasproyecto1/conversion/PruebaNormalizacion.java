package com.mycompany.automatasproyecto1.conversion;
import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;
public class PruebaNormalizacion {
public static void main(String[] args) {
        Automata original = new Automata();

        Estado q0 = new Estado("q0", 0, 0);
        Estado q1 = new Estado("q1", 100, 0);

        original.agregarEstado(q0);
        original.agregarEstado(q1);
        original.setEstadoInicial(q0);
        original.agregarEstadoAceptacion(q1);

        original.agregarTransicion(new Transicion(q0, q1, "a"));

        NormalizadorAutomata normalizador = new NormalizadorAutomata();
        NormalizadorAutomata.GrafoNormalizado resultado =
                normalizador.normalizar(original);
        System.out.println("Estados: " + resultado.getEstados());
        System.out.println("Inicio: " + resultado.getInicio());
        System.out.println("Aceptación: " + resultado.getAceptacion());
        System.out.println("Transiciones:");
        for (NormalizadorAutomata.Arista arista : resultado.getAristas()) {
            System.out.println(arista);
        }
if (resultado.getEstados().size() != 4
                || resultado.getAristas().size() != 3
                || original.getEstados().size() != 2
                || original.getTransiciones().size() != 1
                || original.getEstadoInicial() != q0) {
            throw new AssertionError("La prueba falló.");
        }
        System.out.println("PRUEBA CORRECTA: el original sigue intacto.");
    }
}
