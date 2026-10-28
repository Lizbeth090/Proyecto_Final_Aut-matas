
package com.mycompany.automatasproyecto1.view.simulation;

import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;
import com.mycompany.automatasproyecto1.simulation.ResultadoSimulacion;
import com.mycompany.automatasproyecto1.simulation.PasoSimulacion;
import javax.swing.Timer;
import java.util.function.Consumer;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import java.util.HashMap;
import java.util.Map;

public final class AnimadorSimulacion {
    
    private Timer timer;
    private ResultadoSimulacion resultadoActual;
    private int indicePaso;

    private final mxGraph graph;
    private final mxGraphComponent graphComponent;
    private final Map<Object, Estado> mapaEstados;
    private final Map<Object, Transicion> mapaTransiciones;

    private final Map<Object, String> estilosResaltados =
            new HashMap<>();

    public AnimadorSimulacion(
            mxGraph graph,
            mxGraphComponent graphComponent,
            Map<Object, Estado> mapaEstados,
            Map<Object, Transicion> mapaTransiciones) {

        this.graph = graph;
        this.graphComponent = graphComponent;
        this.mapaEstados = mapaEstados;
        this.mapaTransiciones = mapaTransiciones;
    }

    // Seleccionar visualmente un estado.
    public void seleccionarEstado(Estado estado) {

        for (Map.Entry<Object, Estado> entrada
                : mapaEstados.entrySet()) {

            if (entrada.getValue() == estado) {
                graph.setSelectionCell(entrada.getKey());
                return;
            }
        }
    }

    // Resaltar un paso de simulación.
    public void resaltarPaso(
            Estado origen,
            Estado destino,
            String simbolo) {

        restaurarColores();

        Object celdaDestino = null;
        Object celdaTransicion = null;

        for (Map.Entry<Object, Estado> entrada
                : mapaEstados.entrySet()) {

            if (entrada.getValue() == destino) {
                celdaDestino = entrada.getKey();
                break;
            }
        }

        for (Map.Entry<Object, Transicion> entrada
                : mapaTransiciones.entrySet()) {

            Transicion t = entrada.getValue();

            if (t.getOrigen() == origen
                    && t.getDestino() == destino
                    && t.getSimbolo().equals(simbolo)) {

                celdaTransicion = entrada.getKey();
                break;
            }
        }

        graph.getModel().beginUpdate();

        try {
            if (celdaDestino != null) {

                estilosResaltados.put(
                        celdaDestino,
                        graph.getModel().getStyle(celdaDestino)
                );

                graph.setCellStyles(
                        mxConstants.STYLE_FILLCOLOR,
                        "#BFDBFE",
                        new Object[]{celdaDestino}
                );

                graph.setCellStyles(
                        mxConstants.STYLE_STROKECOLOR,
                        "#2563EB",
                        new Object[]{celdaDestino}
                );

                graph.setCellStyles(
                        mxConstants.STYLE_STROKEWIDTH,
                        "4",
                        new Object[]{celdaDestino}
                );
            }

            if (celdaTransicion != null) {

                estilosResaltados.put(
                        celdaTransicion,
                        graph.getModel().getStyle(celdaTransicion)
                );

                graph.setCellStyles(
                        mxConstants.STYLE_STROKECOLOR,
                        "#8B5CF6",
                        new Object[]{celdaTransicion}
                );

                graph.setCellStyles(
                        mxConstants.STYLE_STROKEWIDTH,
                        "4",
                        new Object[]{celdaTransicion}
                );
            }

        } finally {
            graph.getModel().endUpdate();
        }

        graph.clearSelection();
        graphComponent.repaint();
    }

    // Recuperar los estilos originales.
    public void restaurarColores() {

        graph.getModel().beginUpdate();

        try {
            for (Map.Entry<Object, String> entrada
                    : estilosResaltados.entrySet()) {

                Object celda = entrada.getKey();

                if (graph.getModel().getParent(celda) != null) {
                    graph.getModel().setStyle(
                            celda,
                            entrada.getValue()
                    );
                }
            }

            estilosResaltados.clear();

        } finally {
            graph.getModel().endUpdate();
        }
    }

    public void limpiarResaltados() {
        estilosResaltados.clear();
    }
    
    
    /**
     * Reproduce los pasos de una simulación.
     */
    public void reproducir(
            ResultadoSimulacion resultado,
            Estado estadoInicial,
            Consumer<ResultadoSimulacion> alTerminar,
            Consumer<String> mostrarError) {

        detener();

        resultadoActual = resultado;
        indicePaso = 1;

        seleccionarEstado(estadoInicial);

        if (resultadoActual.getPasos().size() <= 1) {
            finalizar(alTerminar);
            return;
        }

        timer = new Timer(800, e -> {

            if (resultadoActual == null) {
                return;
            }

            if (indicePaso >= resultadoActual.getPasos().size()) {
                finalizar(alTerminar);
                return;
            }

            PasoSimulacion paso =
                    resultadoActual.getPasos().get(indicePaso);

            indicePaso++;

            if (paso.tieneError()) {
                finalizar(alTerminar);
                mostrarError.accept(paso.getMensaje());
                return;
            }

            resaltarPaso(
                    paso.getEstadoOrigen(),
                    paso.getEstadoDestino(),
                    paso.getSimboloLeido()
            );

            if (indicePaso >= resultadoActual.getPasos().size()) {
                finalizar(alTerminar);
            }
        });

        timer.start();
    }

    /**
     * Finaliza la reproducción.
     */
    private void finalizar(
            Consumer<ResultadoSimulacion> alTerminar) {

        if (timer != null) {
            timer.stop();
            timer = null;
        }

        if (resultadoActual != null) {
            alTerminar.accept(resultadoActual);
        }
    }

    /**
     * Detiene una simulación y reinicia su progreso.
     */
    public void detener() {

        if (timer != null) {
            timer.stop();
            timer = null;
        }

        resultadoActual = null;
        indicePaso = 0;
    }

}
