
package com.mycompany.automatasproyecto1.controller;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;
import com.mycompany.automatasproyecto1.simulation.ResultadoSimulacion;
import com.mycompany.automatasproyecto1.simulation.SimuladorAFD;

public class AutomataController {

    private final Automata automata;
    private final SimuladorAFD simulador;

    public AutomataController() {
        automata = new Automata();
        simulador = new SimuladorAFD(automata);
    }

    // ---------------- MODELO ----------------

    public Automata getAutomata() {
        return automata;
    }

    // ---------------- ESTADOS ----------------

    public Estado crearEstado(String nombre, int x, int y) {
        validarNombreEstado(nombre, null);

        Estado estado = new Estado(nombre.trim(), x, y);
        automata.agregarEstado(estado);
        return estado;
    }

    
    public void editarEstado(Estado estado, String nuevoNombre) {
        if (estado == null || !automata.getEstados().contains(estado)) {
            throw new IllegalArgumentException(
                    "El estado seleccionado no pertenece al automata.");
        }

        validarNombreEstado(nuevoNombre, estado);
        estado.setNombre(nuevoNombre.trim());
    }

    private void validarNombreEstado(
            String nombre, Estado estadoExcluido) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del estado no puede estar vacio.");
        }

        String nombreNormalizado = nombre.trim();

        for (Estado existente : automata.getEstados()) {
            if (existente != estadoExcluido
                    && existente.getNombre().equalsIgnoreCase(
                            nombreNormalizado)) {
                throw new IllegalArgumentException(
                        "Ya existe un estado llamado "
                        + nombreNormalizado + ".");
            }
        }
    }

    public void establecerEstadoInicial(Estado estado) {
    automata.setEstadoInicial(estado);
}

    public void establecerEstadoAceptacion(Estado estado) {
        automata.agregarEstadoAceptacion(estado);
    }

    public void quitarEstadoAceptacion(Estado estado) {
        automata.eliminarEstadoAceptacion(estado);
    }

    public void eliminarEstado(Estado estado) {
        automata.eliminarEstado(estado);
    }

    // ---------------- TRANSICIONES ----------------

    public Transicion crearTransicion(
            Estado origen,
            Estado destino,
            String simbolo) {

        validarSimboloTransicion(origen, simbolo, null);

        Transicion transicion = new Transicion(
                origen,
                destino,
                simbolo.trim()
        );

        automata.agregarTransicion(transicion);
        return transicion;
    }

    
    public void editarTransicion(
            Transicion transicion,
            String nuevoSimbolo) {

        if (transicion == null
                || !automata.getTransiciones().contains(transicion)) {
            throw new IllegalArgumentException(
                    "La transicion seleccionada no pertenece al automata.");
        }

        validarSimboloTransicion(
                transicion.getOrigen(),
                nuevoSimbolo,
                transicion
        );

        transicion.setSimbolo(nuevoSimbolo.trim());

        // El alfabeto se recalcula a partir de las transiciones actuales.
        actualizarAlfabeto();
    }

    private void validarSimboloTransicion(
            Estado origen,
            String simbolo,
            Transicion transicionExcluida) {

        if (simbolo == null || simbolo.isBlank()) {
            throw new IllegalArgumentException(
                    "Debes ingresar un simbolo para la transicion.");
        }

        String normalizado = simbolo.trim();

        if (normalizado.length() != 1) {
            throw new IllegalArgumentException(
                    "Cada transicion del AFD debe usar un solo caracter.");
        }

        if (origen == null || !automata.getEstados().contains(origen)) {
            throw new IllegalArgumentException(
                    "El estado de origen no pertenece al automata.");
        }

        for (Transicion existente : automata.getTransiciones()) {
            if (existente != transicionExcluida
                    && existente.getOrigen() == origen
                    && existente.getSimbolo().equals(normalizado)) {
                throw new IllegalArgumentException(
                        "El estado " + origen.getNombre()
                        + " ya tiene una transicion con el simbolo "
                        + normalizado + ".");
            }
        }
    }

    private void actualizarAlfabeto() {
        automata.getAlfabeto().clear();

        for (Transicion transicion : automata.getTransiciones()) {
            String simbolo = transicion.getSimbolo();

            if (simbolo != null && !simbolo.isBlank()) {
                automata.agregarSimbolo(simbolo);
            }
        }
    }

    public void eliminarTransicion(Transicion transicion) {
        automata.eliminarTransicion(transicion);
    }

    // ---------------- SIMULACION ----------------

    public ResultadoSimulacion simularCadena(String cadena) {
        return simulador.simular(cadena);
    }

    public boolean esCadenaAceptada(String cadena) {
        return simularCadena(cadena).isAceptada();
    }

    public ResultadoSimulacion validarCadena(String cadena) {
        return simularCadena(cadena);
    }
}
