package com.mycompany.automatasproyecto1.view.graph;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

public final class DistribuidorFlechasRegex {

    private DistribuidorFlechasRegex() {
        // Clase auxiliar para organizar transiciones
    }

    
    public static void separarFlecha(
            mxGraph grafo,
            Object flecha,
            Object origen,
            Object destino,
            int[] carriles) {

        // Los bucles se dejan con su forma original
        if (origen == destino) {
            return;
        }

        mxGeometry gOrigen =
                grafo.getModel().getGeometry(origen);

        mxGeometry gDestino =
                grafo.getModel().getGeometry(destino);

        if (gOrigen == null || gDestino == null) {
            return;
        }

        double x1 = gOrigen.getX()
                + gOrigen.getWidth() / 2;

        double y1 = gOrigen.getY()
                + gOrigen.getHeight() / 2;

        double x2 = gDestino.getX()
                + gDestino.getWidth() / 2;

        double y2 = gDestino.getY()
                + gDestino.getHeight() / 2;

        // Distribuir las transiciones entre estados de la misma fila
        if (Math.abs(y1 - y2) > 10) {
            return;
        }

        boolean retrocede = x2 < x1;
        boolean saltaEstados = Math.abs(x2 - x1) > 250;

        // Las flechas cortas hacia adelante quedan rectas
        if (!retrocede && !saltaEstados) {
            return;
        }

        // Flechas largas por arriba;
        // flechas que retroceden por abajo
        // Asignar una altura distinta a cada flecha
        int carril;

        if (retrocede) {
            carril = carriles[1]++;
        } else {
            carril = carriles[0]++;
        }

        double distancia = 115 + (carril * 55);

        double altura = retrocede
                ? Math.max(y1, y2) + distancia
                : Math.min(y1, y2) - distancia;

        // Separar los puntos donde salen y llegan
        // las distintas flechas
        double[] posiciones = {
            0.28, 0.72, 0.42, 0.58, 0.50
        };

        double ancla = posiciones[
                carril % posiciones.length
        ];

        double xSalida =
                gOrigen.getX()
                + gOrigen.getWidth() * ancla;

        double xLlegada =
                gDestino.getX()
                + gDestino.getWidth() * ancla;

        java.util.List<mxPoint> puntos =
                new java.util.ArrayList<>();

        puntos.add(new mxPoint(xSalida, altura));
        puntos.add(new mxPoint(xLlegada, altura));

        mxGeometry geometria =
                (mxGeometry) grafo.getModel()
                        .getGeometry(flecha).clone();

        geometria.setPoints(puntos);

        grafo.getModel().setGeometry(
                flecha, geometria
        );

        grafo.setCellStyles(
                mxConstants.STYLE_EDGE,
                "segmentEdgeStyle",
                new Object[]{flecha}
        );
        
        // Conectar las rutas curvas desde arriba o abajo
        String posicionY = retrocede ? "1" : "0";

        grafo.setCellStyles(
                mxConstants.STYLE_EXIT_X,
                String.valueOf(ancla),
                new Object[]{flecha}
        );

        grafo.setCellStyles(
                mxConstants.STYLE_EXIT_Y,
                posicionY,
                new Object[]{flecha}
        );

        grafo.setCellStyles(
                mxConstants.STYLE_ENTRY_X,
                String.valueOf(ancla),
                new Object[]{flecha}
        );

        grafo.setCellStyles(
                mxConstants.STYLE_ENTRY_Y,
                posicionY,
                new Object[]{flecha}
        );
    }
    
}