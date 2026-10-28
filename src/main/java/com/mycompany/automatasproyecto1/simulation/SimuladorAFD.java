
package com.mycompany.automatasproyecto1.simulation;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;

import java.util.ArrayList;
import java.util.List;


public class SimuladorAFD {

    private final Automata automata;

    public SimuladorAFD(Automata automata) {
        if (automata == null) {
            throw new IllegalArgumentException(
                    "El automata no puede ser nulo.");
        }

        this.automata = automata;
    }

    
    public ResultadoSimulacion simular(String cadena) {

        if (cadena == null) {
            cadena = "";
        }

        List<PasoSimulacion> pasos = new ArrayList<>();

        // Validar que exista un estado inicial.
        Estado actual = automata.getEstadoInicial();

        if (actual == null) {
            return new ResultadoSimulacion(
                    cadena,
                    false,
                    null,
                    pasos,
                    "El automata no tiene un estado inicial."
            );
        }

        // Registrar el estado inicial como paso cero.
        pasos.add(new PasoSimulacion(
                0,
                null,
                actual,
                actual,
                null
        ));

        // Procesar la cadena caracter por caracter.
        for (int i = 0; i < cadena.length(); i++) {

            String simbolo = String.valueOf(cadena.charAt(i));

            // Validar que el simbolo pertenezca al alfabeto.
            if (!automata.getAlfabeto().contains(simbolo)) {

                pasos.add(new PasoSimulacion(
                        i + 1,
                        simbolo,
                        actual,
                        null,
                        null,
                        "El simbolo '" + simbolo
                        + "' no pertenece al alfabeto."
                ));

                return new ResultadoSimulacion(
                        cadena,
                        false,
                        actual,
                        pasos,
                        "Cadena rechazada: el simbolo '"
                        + simbolo + "' no pertenece al alfabeto."
                );
            }

            // Buscar la transicion correspondiente.
            Transicion transicion =
                    automata.buscarTransicion(actual, simbolo);

            // Si no existe, la cadena se rechaza.
            if (transicion == null) {

                pasos.add(new PasoSimulacion(
                        i + 1,
                        simbolo,
                        actual,
                        null,
                        null,
                        "No existe una transicion desde "
                        + actual.getNombre()
                        + " con el simbolo '" + simbolo + "'."
                ));

                return new ResultadoSimulacion(
                        cadena,
                        false,
                        actual,
                        pasos,
                        "Cadena rechazada: no existe una transicion "
                        + "para el simbolo '" + simbolo
                        + "' desde " + actual.getNombre() + "."
                );
            }

            // Avanzar al siguiente estado.
            Estado origen = actual;
            actual = transicion.getDestino();

            // Registrar el movimiento realizado.
            pasos.add(new PasoSimulacion(
                    i + 1,
                    simbolo,
                    origen,
                    actual,
                    transicion
            ));
        }

        boolean aceptada =
                automata.getEstadosAceptacion().contains(actual);

        String mensaje;

        if (aceptada) {
            mensaje = "Cadena aceptada: se termino en el estado final "
                    + actual.getNombre() + ".";
        } else {
            mensaje = "Cadena rechazada: se termino en el estado "
                    + actual.getNombre()
                    + ", que no es de aceptacion.";
        }

        return new ResultadoSimulacion(
                cadena,
                aceptada,
                actual,
                pasos,
                mensaje
        );
    }

    public Automata getAutomata() {
        return automata;
    }
}
