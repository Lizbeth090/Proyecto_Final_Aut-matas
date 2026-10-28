
package com.mycompany.automatasproyecto1.view.graph;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxCurveShape;
import com.mxgraph.view.mxCellState;

public class CurvaAFD extends mxCurveShape {

    @Override
    public void paintShape(
            mxGraphics2DCanvas canvas,
            mxCellState state) {

        // Actualizar la curva antes de calcular
        // la dirección de las puntas.
        if (state.getAbsolutePointCount() > 1) {

            validateCurve(
                    state.getAbsolutePoints(),
                    canvas.getScale(),
                    state.getStyle()
            );
        }

        // Dibujar normalmente con JGraphX.
        super.paintShape(canvas, state);
    }
}
