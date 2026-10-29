
package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;

public class PruebaConversorRegex {

    public static void main(String[] args) {
        Automata original = new Automata();

        Estado q0 = new Estado("q0", 0, 0);
        Estado q1 = new Estado("q1", 100, 0);

        original.agregarEstado(q0);
        original.agregarEstado(q1);
        original.setEstadoInicial(q0);
        original.agregarEstadoAceptacion(q1);

        original.agregarTransicion(new Transicion(q0, q1, "a"));

        String resultado = new ConversorRegex().convertir(original).toString();
        System.out.println("Resultado de conversion: " + resultado);

        if (!resultado.equals("a")) {
            throw new AssertionError("Se esperaba la expresion a");
        }

        if (original.getEstados().size() != 2
                || original.getTransiciones().size() != 1
                || original.getEstadoInicial() != q0) {
            throw new AssertionError("El AFD original fue modificado");
        }

        System.out.println("PRUEBA CORRECTA: conversion y AFD original intacto.");
    }
}
