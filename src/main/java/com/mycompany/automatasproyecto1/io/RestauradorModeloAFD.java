
package com.mycompany.automatasproyecto1.io;

import com.mycompany.automatasproyecto1.controller.AutomataController;
import com.mycompany.automatasproyecto1.model.Estado;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RestauradorModeloAFD {

    private RestauradorModeloAFD() {
        // Clase de utilidad.
    }

    public record Resultado(
            AutomataController controller,
            Map<String, Estado> estados) {
    }

    public static Resultado reconstruir(
            AutomataJson.Datos datos) {

        AutomataController nuevoController =
                new AutomataController();

        Map<String, Estado> nuevosEstados =
                new LinkedHashMap<>();

        // Reconstruir estados.
        for (AutomataJson.EstadoDato dato : datos.estados) {

            Estado estado = nuevoController.crearEstado(
                    dato.nombre, dato.x, dato.y
            );

            nuevosEstados.put(dato.nombre, estado);

            if (dato.aceptacion) {
                nuevoController.establecerEstadoAceptacion(
                        estado
                );
            }
        }

        // Restaurar estado inicial.
        if (datos.inicial != null) {

            Estado inicial = nuevosEstados.get(datos.inicial);

            if (inicial == null) {
                throw new IllegalArgumentException(
                        "No se encontró el estado inicial."
                );
            }

            nuevoController.establecerEstadoInicial(inicial);
        }

        // Reconstruir transiciones.
        for (AutomataJson.TransicionDato dato
                : datos.transiciones) {

            Estado origen = nuevosEstados.get(dato.origen);
            Estado destino = nuevosEstados.get(dato.destino);

            if (origen == null || destino == null) {
                throw new IllegalArgumentException(
                        "Una transición contiene estados inexistentes."
                );
            }

            nuevoController.crearTransicion(
                    origen, destino, dato.simbolo
            );
        }

        return new Resultado(nuevoController, nuevosEstados);
    }
}
