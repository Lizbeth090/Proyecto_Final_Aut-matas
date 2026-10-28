package com.mycompany.automatasproyecto1.view.graph;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

public final class GeometriaTransicionesAFD {

    private GeometriaTransicionesAFD() {
        // Clase auxiliar para organizar las transiciones.
    }

    public static void actualizarCurvasTransiciones(mxGraph graph) {
        Object[] aristas = graph.getChildEdges(graph.getDefaultParent());

        graph.getModel().beginUpdate();
        try {
            for (Object arista : aristas) {
                Object origen = graph.getModel().getTerminal(arista, true);
                Object destino = graph.getModel().getTerminal(arista, false);

                if (origen == null || destino == null || origen == destino) {
                    continue;
                }

                boolean tieneOpuesta = false;
                for (Object otra : aristas) {
                    if (otra != arista
                            && graph.getModel().getTerminal(otra, true) == destino
                            && graph.getModel().getTerminal(otra, false) == origen) {
                        tieneOpuesta = true;
                        break;
                    }
                }

                mxGeometry anterior = graph.getModel().getGeometry(arista);
                if (anterior == null) continue;
                mxGeometry nueva = (mxGeometry) anterior.clone();

                if (tieneOpuesta) {
                    mxGeometry g1 = graph.getModel().getGeometry(origen);
                    mxGeometry g2 = graph.getModel().getGeometry(destino);
                    if (g1 == null || g2 == null) continue;

                    double x1 = g1.getX() + g1.getWidth() / 2;
                    double y1 = g1.getY() + g1.getHeight() / 2;
                    double x2 = g2.getX() + g2.getWidth() / 2;
                    double y2 = g2.getY() + g2.getHeight() / 2;
                    double dx = x2 - x1;
                    double dy = y2 - y1;
                    double distancia = Math.hypot(dx, dy);
                    if (distancia < 1) continue;

                    double separacion = 28;
                    double puntoX = (x1 + x2) / 2
                            - dy / distancia * separacion;
                    double puntoY = (y1 + y2) / 2
                            + dx / distancia * separacion;

                    java.util.List<mxPoint> puntos = new java.util.ArrayList<>();
                    puntos.add(new mxPoint(puntoX, puntoY));
                    nueva.setPoints(puntos);
                } else {
                    // Al eliminar la flecha opuesta, volver a una línea normal.
                    nueva.setPoints(null);
                }

                graph.getModel().setGeometry(arista, nueva);
                
                
                graph.setCellStyles(
                        mxConstants.STYLE_SHAPE,
                        tieneOpuesta ? "curvaAFD"
                                : mxConstants.SHAPE_CONNECTOR,
                        new Object[]{arista}
                );
                
                
                graph.setCellStyles(
                        mxConstants.STYLE_MOVABLE,
                        "0",
                        new Object[]{arista}
                );
            }
        } finally {
            graph.getModel().endUpdate();
        }
    }
}