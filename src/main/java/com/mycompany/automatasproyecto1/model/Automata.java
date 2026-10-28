
package com.mycompany.automatasproyecto1.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Automata {

    private final List<Estado> estados;
    private final Set<String> alfabeto;
    private final List<Transicion> transiciones;
    private Estado estadoInicial;
    private final Set<Estado> estadosAceptacion;

    public Automata() {
        estados = new ArrayList<>();
        alfabeto = new HashSet<>();
        transiciones = new ArrayList<>();
        estadosAceptacion = new HashSet<>();
        estadoInicial = null;
    }

    // ---------------- ESTADOS ----------------

    public void agregarEstado(Estado estado) {
        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado no puede ser nulo.");
        }

        if (!estados.contains(estado)) {
            estados.add(estado);
        }
    }

    public void eliminarEstado(Estado estado) {
        if (estado == null || !estados.contains(estado)) {
            return;
        }

        // Eliminar todas las transiciones relacionadas con el estado.
        transiciones.removeIf(t ->
                t.getOrigen() == estado
                || t.getDestino() == estado);

        estadosAceptacion.remove(estado);
        estado.setAceptacion(false);

        if (estadoInicial == estado) {
            estado.setInicial(false);
            estadoInicial = null;
        }

        estados.remove(estado);

        // Recalcular el alfabeto después de eliminar transiciones.
        actualizarAlfabeto();
    }

    // ---------------- ALFABETO ----------------

    public void agregarSimbolo(String simbolo) {
        if (simbolo != null && !simbolo.isBlank()) {
            alfabeto.add(simbolo.trim());
        }
    }

    private void actualizarAlfabeto() {
        alfabeto.clear();

        for (Transicion transicion : transiciones) {
            String simbolo = transicion.getSimbolo();

            if (simbolo != null && !simbolo.isBlank()) {
                alfabeto.add(simbolo);
            }
        }
    }

    // ---------------- TRANSICIONES ----------------

    public void agregarTransicion(Transicion transicion) {
        if (transicion == null) {
            throw new IllegalArgumentException(
                    "La transicion no puede ser nula.");
        }

        Estado origen = transicion.getOrigen();
        Estado destino = transicion.getDestino();
        String simbolo = transicion.getSimbolo();

        if (origen == null || destino == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el origen y destino.");
        }

        if (!estados.contains(origen)
                || !estados.contains(destino)) {
            throw new IllegalArgumentException(
                    "Los estados deben pertenecer al automata.");
        }

        if (simbolo == null || simbolo.isBlank()) {
            throw new IllegalArgumentException(
                    "El simbolo no puede estar vacio.");
        }

        simbolo = simbolo.trim();
        transicion.setSimbolo(simbolo);

        for (Transicion existente : transiciones) {
            boolean mismoOrigen =
                    existente.getOrigen() == origen;
            boolean mismoSimbolo =
                    existente.getSimbolo().equals(simbolo);

            if (mismoOrigen && mismoSimbolo) {
                throw new IllegalArgumentException(
                        "El estado " + origen.getNombre()
                        + " ya tiene una transicion con el simbolo "
                        + simbolo + ".");
            }
        }

        transiciones.add(transicion);
        actualizarAlfabeto();
    }

    public void eliminarTransicion(Transicion transicion) {
        if (transicion != null && transiciones.remove(transicion)) {
            actualizarAlfabeto();
        }
    }

   
    public Transicion buscarTransicion(
            Estado origen, String simbolo) {

        if (origen == null || simbolo == null) {
            return null;
        }

        for (Transicion transicion : transiciones) {
            if (transicion.getOrigen() == origen
                    && transicion.getSimbolo().equals(simbolo)) {
                return transicion;
            }
        }

        return null;
    }

    // ---------------- ESTADO INICIAL ----------------

    public void setEstadoInicial(Estado estado) {
        if (estado != null && !estados.contains(estado)) {
            throw new IllegalArgumentException(
                    "El estado inicial debe pertenecer al automata.");
        }

        if (estadoInicial != null) {
            estadoInicial.setInicial(false);
        }

        estadoInicial = estado;

        if (estadoInicial != null) {
            estadoInicial.setInicial(true);
        }
    }

    // ---------------- ESTADOS DE ACEPTACION ----------------

    public void agregarEstadoAceptacion(Estado estado) {
        if (estado == null || !estados.contains(estado)) {
            throw new IllegalArgumentException(
                    "El estado final debe pertenecer al automata.");
        }

        estadosAceptacion.add(estado);
        estado.setAceptacion(true);
    }

    public void eliminarEstadoAceptacion(Estado estado) {
        if (estado != null) {
            estadosAceptacion.remove(estado);
            estado.setAceptacion(false);
        }
    }

    public boolean esEstadoAceptacion(Estado estado) {
        return estadosAceptacion.contains(estado);
    }

    // ---------------- CONSULTAS ----------------

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
