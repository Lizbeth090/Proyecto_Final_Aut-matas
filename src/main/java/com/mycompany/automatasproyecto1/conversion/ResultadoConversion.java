
package com.mycompany.automatasproyecto1.conversion;

import java.util.List;

public final class ResultadoConversion {

    private final ExpresionRegular expresion;
    private final List<PasoConversion> pasos;

    public ResultadoConversion(ExpresionRegular expresion,
            List<PasoConversion> pasos) {

        this.expresion = expresion;
        this.pasos = List.copyOf(pasos);
    }

    public ExpresionRegular getExpresion() {
        return expresion;
    }

    public List<PasoConversion> getPasos() {
        return pasos;
    }
}

