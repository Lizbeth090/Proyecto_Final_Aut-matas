
package com.mycompany.automatasproyecto1.controller;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;


 //* Controlador principal del autómata.

public class AutomataController {

    private Automata automata;

    public AutomataController() {

        automata = new Automata();
    }

    public Automata getAutomata() {

        return automata;
    }

    // ==========================================
    // ESTADOS
    // ==========================================

    public Estado crearEstado(
            String nombre,
            int x,
            int y) {

        Estado estado =
                new Estado(
                        nombre,
                        x,
                        y
                );

        automata.agregarEstado(
                estado
        );

        return estado;
    }

    public void establecerEstadoInicial(
            Estado estado) {

        automata.setEstadoInicial(
                estado
        );
    }

    public void establecerEstadoAceptacion(
            Estado estado) {

        automata.agregarEstadoAceptacion(
                estado
        );
    }

    public void eliminarEstado(
            Estado estado) {

        automata.eliminarEstado(
                estado
        );
    }

    // ==========================================
    // TRANSICIONES
    // ==========================================

    public Transicion crearTransicion(
            Estado origen,
            Estado destino,
            String simbolo) {

        Transicion transicion =
                new Transicion(
                        origen,
                        destino,
                        simbolo
                );

        automata.agregarTransicion(
                transicion
        );

        return transicion;
    }

    public void eliminarTransicion(
            Transicion transicion) {

        automata.eliminarTransicion(
                transicion
        );
    }
}
