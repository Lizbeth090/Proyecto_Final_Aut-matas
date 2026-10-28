
package com.mycompany.automatasproyecto1.simulation;

import com.mycompany.automatasproyecto1.model.Estado;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ResultadoSimulacion {

    private final String cadena;
    private final boolean aceptada;
    private final Estado estadoFinal;
    private final List<PasoSimulacion> pasos;
    private final String mensaje;

    public ResultadoSimulacion(
            String cadena,
            boolean aceptada,
            Estado estadoFinal,
            List<PasoSimulacion> pasos,
            String mensaje) {

        this.cadena = cadena == null ? "" : cadena;
        this.aceptada = aceptada;
        this.estadoFinal = estadoFinal;

        this.pasos = pasos == null
                ? new ArrayList<>()
                : new ArrayList<>(pasos);

        this.mensaje = mensaje;
    }

    public String getCadena() {
        return cadena;
    }

    public boolean isAceptada() {
        return aceptada;
    }

    public boolean isRechazada() {
        return !aceptada;
    }

    public Estado getEstadoFinal() {
        return estadoFinal;
    }

    /**
     * Devuelve los pasos en una lista que no se puede modificar.
     */
    public List<PasoSimulacion> getPasos() {
        return Collections.unmodifiableList(pasos);
    }

    public String getMensaje() {
        return mensaje;
    }

    /**
     * Devuelve una descripcion general del resultado.
     */
    public String getEstadoResultado() {
        return aceptada ? "ACEPTADA" : "RECHAZADA";
    }

    /**
     * Indica si el simulador encontro algun paso con error.
     */
    public boolean tieneErrores() {
        for (PasoSimulacion paso : pasos) {
            if (paso.tieneError()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        String nombreEstado = estadoFinal == null
                ? "ninguno"
                : estadoFinal.getNombre();

        return "Resultado de simulacion"
                + "\nCadena: " + cadena
                + "\nResultado: " + getEstadoResultado()
                + "\nEstado final: " + nombreEstado
                + "\nMensaje: "
                + (mensaje == null ? "Sin mensajes adicionales" : mensaje)
                + "\nCantidad de pasos: " + pasos.size();
    }
}
