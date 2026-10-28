package com.mycompany.automatasproyecto1.view.editor;

import com.mycompany.automatasproyecto1.model.Estado;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import java.util.Map;

public final class EditorVisualAFD {

    private final mxGraph graph;
    private final Map<Object, Estado> mapaEstados;

    public EditorVisualAFD(
            mxGraph graph,
            Map<Object, Estado> mapaEstados) {

        this.graph = graph;
        this.mapaEstados = mapaEstados;
    }

    public void actualizarEtiquetasEstados() {

        graph.getModel().beginUpdate();

        try {

            for (
                    Map.Entry<Object, Estado> entrada
                    : mapaEstados.entrySet()
            ) {

                Object celda =
                        entrada.getKey();

                Estado estado =
                        entrada.getValue();

                String texto =
                        estado.getNombre();

                

                // Estado de aceptación
                

                graph.getModel().setValue(
                        celda,
                        texto
                );
            }

        } finally {

            graph.getModel().endUpdate();
        }
    }
    
    public void actualizarEstiloEstados() {

    graph.getModel().beginUpdate();

    try {
        for (Map.Entry<Object, Estado> entrada
                : mapaEstados.entrySet()) {

            Object celda = entrada.getKey();
            Estado estado = entrada.getValue();

            if (estado.isAceptacion()) {

                // Estado final: doble círculo
                graph.setCellStyles(
                        mxConstants.STYLE_SHAPE,
                        mxConstants.SHAPE_DOUBLE_ELLIPSE,
                        new Object[]{celda}
                );

            } else {

                // Estado normal: un círculo
                graph.setCellStyles(
                        mxConstants.STYLE_SHAPE,
                        mxConstants.SHAPE_ELLIPSE,
                        new Object[]{celda}
                );
            }

            graph.setCellStyles(
                    mxConstants.STYLE_STROKEWIDTH,
                    "2",
                    new Object[]{celda}
            );
        }

        } finally {
            graph.getModel().endUpdate();
        }
    }
    
    public void sincronizarPosicionEstado() {

        Object celda =
                graph.getSelectionCell();

        if (celda == null) {
            return;
        }

        Estado estado =
                mapaEstados.get(celda);

        if (estado == null) {
            return;
        }

        mxGeometry geometria =
                graph.getModel()
                        .getGeometry(celda);

        if (geometria == null) {
            return;
        }

        estado.setX(
                (int) geometria.getX()
        );

        estado.setY(
                (int) geometria.getY()
        );
    }
}