
package com.mycompany.automatasproyecto1.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Representa un autómata finito.
 */
public class Automata {

    // Conjunto de estados Q
    private List<Estado> estados;

    // Alfabeto Σ
    private Set<String> alfabeto;

    // Conjunto de transiciones δ
    private List<Transicion> transiciones;

    // Estado inicial q0
    private Estado estadoInicial;

    // Conjunto de estados de aceptación F
    private Set<Estado> estadosAceptacion;

    /**
     * Constructor del autómata.
     */
    public Automata() {
        estados = new ArrayList<>();
        alfabeto = new HashSet<>();
        transiciones = new ArrayList<>();
        estadosAceptacion = new HashSet<>();
    }

    /**
     * Agrega un estado al autómata.
     */
    public void agregarEstado(Estado estado) {
        if (!estados.contains(estado)) {
            estados.add(estado);
        }
    }

    /**
     * Elimina un estado del autómata.
     */
    public void eliminarEstado(Estado estado) {
        estados.remove(estado);
        estadosAceptacion.remove(estado);

        if (estadoInicial == estado) {
            estadoInicial = null;
        }

        // Eliminar también las transiciones relacionadas
        transiciones.removeIf(transicion ->
                transicion.getOrigen() == estado
                || transicion.getDestino() == estado);
    }

    /**
     * Agrega un símbolo al alfabeto.
     */
    public void agregarSimbolo(String simbolo) {
        if (simbolo != null && !simbolo.isBlank()) {
            alfabeto.add(simbolo);
        }
    }

    /**
     * Agrega una transición.
     */
    public void agregarTransicion(Transicion transicion) {
        if (transicion != null) {
            transiciones.add(transicion);

            // El símbolo de la transición pasa a formar parte del alfabeto
            agregarSimbolo(transicion.getSimbolo());
        }
    }

    /**
     * Elimina una transición.
     */
    public void eliminarTransicion(Transicion transicion) {
        transiciones.remove(transicion);
    }

    /**
     * Establece el estado inicial.
     */
    public void setEstadoInicial(Estado estado) {
        if (estado != null && estados.contains(estado)) {

            // Si había otro estado inicial, deja de ser inicial
            if (estadoInicial != null) {
                estadoInicial.setInicial(false);
            }

            estadoInicial = estado;
            estadoInicial.setInicial(true);
        }
    }

    /**
     * Agrega un estado de aceptación.
     */
    public void agregarEstadoAceptacion(Estado estado) {
        if (estado != null && estados.contains(estado)) {
            estadosAceptacion.add(estado);
            estado.setAceptacion(true);
        }
    }

    /**
     * Elimina un estado de aceptación.
     */
    public void eliminarEstadoAceptacion(Estado estado) {
        estadosAceptacion.remove(estado);

        if (estado != null) {
            estado.setAceptacion(false);
        }
    }

    // ==========================
    // GETTERS
    // ==========================

    public List<Estado> getEstados() {
        return estados;
    }

    public Set<String> getAlfabeto() {
        return alfabeto;
    }

    public List<Transicion> getTransiciones() {
        return transiciones;
    }

    public Estado getEstadoInicial() {
        return estadoInicial;
    }

    public Set<Estado> getEstadosAceptacion() {
        return estadosAceptacion;
    }
}