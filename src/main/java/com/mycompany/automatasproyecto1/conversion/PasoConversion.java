
package com.mycompany.automatasproyecto1.conversion;

import java.util.List;

public final class PasoConversion {

    private final String descripcion;
    private final List<String> estados;
    private final String inicio;
    private final String aceptacion;
    private final List<NormalizadorAutomata.Arista> transiciones;

    public PasoConversion(String descripcion, List<String> estados,
            String inicio, String aceptacion,
            List<NormalizadorAutomata.Arista> transiciones) {

        this.descripcion = descripcion;
        this.estados = List.copyOf(estados);
        this.inicio = inicio;
        this.aceptacion = aceptacion;
        this.transiciones = List.copyOf(transiciones);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public List<String> getEstados() {
        return estados;
    }

    public String getInicio() {
        return inicio;
    }

    public String getAceptacion() {
        return aceptacion;
    }

    public List<NormalizadorAutomata.Arista> getTransiciones() {
        return transiciones;
    }
}
