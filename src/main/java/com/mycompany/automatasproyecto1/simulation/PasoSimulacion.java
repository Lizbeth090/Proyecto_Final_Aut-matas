
package com.mycompany.automatasproyecto1.simulation;

import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;


public class PasoSimulacion {

    private final int numeroPaso;
    private final String simboloLeido;
    private final Estado estadoOrigen;
    private final Estado estadoDestino;
    private final Transicion transicion;
    private final String mensaje;

    public PasoSimulacion(
            int numeroPaso,
            String simboloLeido,
            Estado estadoOrigen,
            Estado estadoDestino,
            Transicion transicion) {

        this(
                numeroPaso,
                simboloLeido,
                estadoOrigen,
                estadoDestino,
                transicion,
                null
        );
    }

    
    public PasoSimulacion(
            int numeroPaso,
            String simboloLeido,
            Estado estadoOrigen,
            Estado estadoDestino,
            Transicion transicion,
            String mensaje) {

        this.numeroPaso = numeroPaso;
        this.simboloLeido = simboloLeido;
        this.estadoOrigen = estadoOrigen;
        this.estadoDestino = estadoDestino;
        this.transicion = transicion;
        this.mensaje = mensaje;
    }

    public int getNumeroPaso() {
        return numeroPaso;
    }

    public String getSimboloLeido() {
        return simboloLeido;
    }

    public Estado getEstadoOrigen() {
        return estadoOrigen;
    }

    public Estado getEstadoDestino() {
        return estadoDestino;
    }

    public Transicion getTransicion() {
        return transicion;
    }

    public String getMensaje() {
        return mensaje;
    }

    /**
     * Indica si este paso termino con un error.
     */
    public boolean tieneError() {
        return mensaje != null && !mensaje.isBlank();
    }

    @Override
    public String toString() {
        String simbolo = simboloLeido == null
                ? "ε"
                : simboloLeido;

        String origen = estadoOrigen == null
                ? "-"
                : estadoOrigen.getNombre();

        String destino = estadoDestino == null
                ? "-"
                : estadoDestino.getNombre();

        if (tieneError()) {
            return "Paso " + numeroPaso
                    + ": simbolo '" + simbolo + "'"
                    + ", desde " + origen
                    + ". Error: " + mensaje;
        }

        return "Paso " + numeroPaso
                + ": simbolo '" + simbolo + "'"
                + ", " + origen + " -> " + destino;
    }
}
