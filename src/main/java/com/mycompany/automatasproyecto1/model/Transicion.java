
package com.mycompany.automatasproyecto1.model;

/**
 * Representa una transición entre dos estados de un autómata.
 */
public class Transicion {

    private Estado origen;
    private Estado destino;
    private String simbolo;

    /**
     * Constructor de una transición.
     */
    public Transicion(Estado origen, Estado destino, String simbolo) {
        this.origen = origen;
        this.destino = destino;
        this.simbolo = simbolo;
    }

    public Estado getOrigen() {
        return origen;
    }

    public void setOrigen(Estado origen) {
        this.origen = origen;
    }

    public Estado getDestino() {
        return destino;
    }

    public void setDestino(Estado destino) {
        this.destino = destino;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    @Override
    public String toString() {
        return origen.getNombre() + " --" + simbolo + "--> "
                + destino.getNombre();
    }
}
