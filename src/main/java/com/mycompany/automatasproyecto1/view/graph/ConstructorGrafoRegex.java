package com.mycompany.automatasproyecto1.view.graph;

import com.mycompany.automatasproyecto1.conversion.PasoConversion;
import com.mycompany.automatasproyecto1.conversion.NormalizadorAutomata;

import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ConstructorGrafoRegex {

    public static final int LIMITE_ETIQUETA_REGEX = 7;

    private ConstructorGrafoRegex() {
        // Evita crear objetos de esta clase auxiliar.
    }

    public static mxGraph construirGrafoPaso(PasoConversion paso) {

        mxGraph grafo = new mxGraph();
        grafo.setConstrainChildren(false);
        grafo.setCellsMovable(false);
        grafo.setCellsResizable(false);
        grafo.setCellsEditable(false);
        grafo.setCellsBendable(false);
        grafo.setAllowLoops(true);

        Map<String, Object> vertices = new HashMap<>();
        Object parent = grafo.getDefaultParent();

        java.util.List<String> nombres = new ArrayList<>();

        nombres.add(paso.getInicio());

        for (String nombre : paso.getEstados()) {
            if (!nombre.equals(paso.getInicio())
                    && !nombre.equals(paso.getAceptacion())) {
                nombres.add(nombre);
            }
        }

        if (!nombres.contains(paso.getAceptacion())) {
            nombres.add(paso.getAceptacion());
        }

        grafo.getModel().beginUpdate();

        try {
            for (int i = 0; i < nombres.size(); i++) {

                String nombre = nombres.get(i);

                int columna = i % 4;
                int fila = i / 4;

                int x = 90 + columna * 190;
                int y = 220 + fila * 280;

                boolean esFinal =
                        nombre.equals(paso.getAceptacion());

                String estilo =
                        "shape=" + (esFinal
                        ? mxConstants.SHAPE_DOUBLE_ELLIPSE
                        : mxConstants.SHAPE_ELLIPSE)
                        + ";fillColor=#FFFFFF;"
                        + "strokeColor=#333333;"
                        + "strokeWidth=2;"
                        + "fontSize=15;"
                        + "align=center;"
                        + "verticalAlign=middle;";

                Object vertice = grafo.insertVertex(
                        parent, null, nombre,
                        x, y, 85, 85, estilo
                );

                vertices.put(nombre, vertice);
            }

            // Indicador exterior del estado inicial
            Object inicio = vertices.get(paso.getInicio());

            if (inicio != null) {
                grafo.insertVertex(
                        inicio, null, "➜",
                        -36, 32, 48, 36,
                        "shape=label;"
                        + "fillColor=none;"
                        + "strokeColor=none;"
                        + "fontSize=28;"
                        + "movable=0;"
                        + "editable=0;"
                        + "selectable=0;"
                );
            }

            // Transiciones calculadas por el algoritmo
           
            int numeroReferencia = 0;
            
            int[] carriles = {0, 0};
            
            // Guardar un único bucle visual por estado
            Map<Object, Object> buclesPorEstado = new HashMap<>();

            for (NormalizadorAutomata.Arista arista
                    : paso.getTransiciones()) {

                Object origen = vertices.get(arista.getOrigen());
                Object destino = vertices.get(arista.getDestino());

                if (origen == null || destino == null) {
                    continue;
                }

                String etiqueta = arista.getSimbolo();

                if (etiqueta != null
                        && etiqueta.length() > LIMITE_ETIQUETA_REGEX) {

                    numeroReferencia++;
                    etiqueta = "R" + numeroReferencia;
                }
                
                
                // Si el estado ya tiene un bucle,
                // unir las etiquetas en una sola flecha
                if (origen == destino) {

                    Object bucleExistente = buclesPorEstado.get(origen);

                    if (bucleExistente != null) {

                        String etiquetaAnterior = String.valueOf(
                                grafo.getModel().getValue(bucleExistente)
                        );

                        grafo.getModel().setValue(
                                bucleExistente,
                                etiquetaAnterior + " | " + etiqueta
                        );

                        continue;
                    }
                }
                
                
                Object flecha = grafo.insertEdge(
                        parent,
                        null,
                        etiqueta,
                        origen,
                        destino,
                        "strokeColor=#8B5CF6;"
                        + "strokeWidth=2;"
                        + "endArrow=classic;"
                        + "fontSize=13;"
                        + "fontColor=#4C1D95;"
                        + "labelBackgroundColor=#FAF7FF;"
                        + "rounded=1;"
                    );
                
                if (origen == destino) {
                    
                    buclesPorEstado.put(origen, flecha);
                    
                    grafo.setCellStyles(
                            mxConstants.STYLE_LOOP,
                            "loopEdgeStyle",
                            new Object[]{flecha}
                    );

                    grafo.setCellStyles(
                            mxConstants.STYLE_DIRECTION,
                            mxConstants.DIRECTION_NORTH,
                            new Object[]{flecha}
                    );

                    grafo.setCellStyles(
                            mxConstants.STYLE_SEGMENT,
                            "30",
                            new Object[]{flecha}
                    );

                    grafo.setCellStyles(
                            mxConstants.STYLE_ROUNDED,
                            "1",
                            new Object[]{flecha}
                    );

                    grafo.setCellStyles(
                            mxConstants.STYLE_SHAPE,
                            mxConstants.SHAPE_CURVE,
                            new Object[]{flecha}
                    );

                } else {

                    DistribuidorFlechasRegex.separarFlecha(
                            grafo, flecha, origen, destino, carriles
                    );
                }
                
                }
            }finally {
            grafo.getModel().endUpdate();
        }

        return grafo;
    }
}