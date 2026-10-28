package com.mycompany.automatasproyecto1.conversion;
import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NormalizadorAutomata {
public static final String EPSILON = "ε";
public GrafoNormalizado normalizar(Automata original) {
        if (original == null || original.getEstadoInicial() == null) {
            throw new IllegalArgumentException("El autómata necesita un estado inicial.");
        }
        Set<String> nombres = new LinkedHashSet<>();
        for (Estado estado : original.getEstados()) {
            if (estado == null || estado.getNombre() == null
                    || estado.getNombre().isBlank()
                    || !nombres.add(estado.getNombre())) {
                throw new IllegalArgumentException("Hay estados sin nombre o con nombres repetidos.");
            }
        }

        Estado inicialOriginal = original.getEstadoInicial();
        if (!original.getEstados().contains(inicialOriginal)) {
            throw new IllegalArgumentException("El estado inicial no pertenece al autómata.");
        }

        List<Arista> aristas = new ArrayList<>();

        for (Transicion t : original.getTransiciones()) {
            if (t == null || !original.getEstados().contains(t.getOrigen())
                    || !original.getEstados().contains(t.getDestino())
                    || t.getSimbolo() == null || t.getSimbolo().isBlank()) {
                throw new IllegalArgumentException("Hay una transición inválida.");
            }

            
for (String simbolo : separarSimbolos(t.getSimbolo())) {
    aristas.add(new Arista(t.getOrigen().getNombre(),
            simbolo, t.getDestino().getNombre()));
}

        }

        String nuevoInicio = nombreDisponible("q_start", nombres);
        nombres.add(nuevoInicio);

        String nuevoFinal = nombreDisponible("q_accept", nombres);
        nombres.add(nuevoFinal);

        aristas.add(new Arista(nuevoInicio, EPSILON, inicialOriginal.getNombre()));

        for (Estado estado : original.getEstados()) {
            if (original.getEstadosAceptacion().contains(estado)) {
                aristas.add(new Arista(estado.getNombre(), EPSILON, nuevoFinal));
            }
        }

        return new GrafoNormalizado(new ArrayList<>(nombres),
                nuevoInicio, nuevoFinal, aristas);
    }

public static List<String> separarSimbolos(String etiqueta) {
    if (etiqueta == null || etiqueta.isBlank()) {
        throw new IllegalArgumentException(
                "La etiqueta no puede estar vacia.");
    }

    Set<String> simbolos = new LinkedHashSet<>();

    for (String parte : etiqueta.split(",", -1)) {
        String simbolo = parte.trim();

        if (simbolo.isEmpty()
                || simbolo.codePointCount(0, simbolo.length()) != 1
                || simbolo.equals(EPSILON)
                || simbolo.equals("∅")) {
            throw new IllegalArgumentException(
                    "Etiqueta de AFD invalida: " + etiqueta);
        }

        if (!simbolos.add(simbolo)) {
            throw new IllegalArgumentException(
                    "Simbolo repetido en etiqueta: " + etiqueta);
        }
    }

    return new ArrayList<>(simbolos);
}

    private String nombreDisponible(String base, Set<String> nombres) {
        String nombre = base;
        int numero = 1;

        while (nombres.contains(nombre)) {
            nombre = base + "_" + numero++;
        }
        return nombre;
    }

    public static class Arista {
        private final String origen;
        private final String simbolo;
        private final String destino;

        public Arista(String origen, String simbolo, String destino) {
            this.origen = origen;
            this.simbolo = simbolo;
            this.destino = destino;
        }

        public String getOrigen() { return origen; }
        public String getSimbolo() { return simbolo; }
        public String getDestino() { return destino; }

        @Override
        public String toString() {
            return origen + " --" + simbolo + "--> " + destino;
        }
    }

    public static class GrafoNormalizado {
        private final List<String> estados;
        private final String inicio;
        private final String aceptacion;
        private final List<Arista> aristas;

        public GrafoNormalizado(List<String> estados, String inicio,
                String aceptacion, List<Arista> aristas) {
            this.estados = List.copyOf(estados);
            this.inicio = inicio;
            this.aceptacion = aceptacion;
            this.aristas = List.copyOf(aristas);
        }

        public List<String> getEstados() { return estados; }
        public String getInicio() { return inicio; }
        public String getAceptacion() { return aceptacion; }
        public List<Arista> getAristas() { return aristas; }
    }
}

