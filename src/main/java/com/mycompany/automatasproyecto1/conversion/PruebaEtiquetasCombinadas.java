package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;
import java.util.regex.Pattern;

public class PruebaEtiquetasCombinadas {

    public static void main(String[] args) {
        Automata afd = new Automata();
        Estado q0 = new Estado("q0", 0, 0);
        Estado q1 = new Estado("q1", 100, 0);

        afd.agregarEstado(q0);
        afd.agregarEstado(q1);
        afd.setEstadoInicial(q0);
        afd.agregarEstadoAceptacion(q1);
        afd.agregarTransicion(new Transicion(q0, q1, "0,1"));

        NormalizadorAutomata.GrafoNormalizado grafo =
                new NormalizadorAutomata().normalizar(afd);

        long cantidad = grafo.getAristas().stream()
                .filter(t -> t.getOrigen().equals("q0")
                        && t.getDestino().equals("q1")
                        && (t.getSimbolo().equals("0")
                            || t.getSimbolo().equals("1")))
                .count();

        ExpresionRegular regex = new ConversorRegex().convertir(afd);
        String javaRegex = regex.aRegexJava();

        if (cantidad != 2
                || !Pattern.matches(javaRegex, "0")
                || !Pattern.matches(javaRegex, "1")
                || Pattern.matches(javaRegex, "01")
                || Pattern.matches(javaRegex, "")
                || afd.getTransiciones().size() != 1
                || !afd.getTransiciones().get(0).getSimbolo().equals("0,1")) {
            throw new AssertionError("Fallo con etiquetas combinadas.");
        }

        System.out.println("Aristas originales: " + afd.getTransiciones().size());
        System.out.println("Aristas normalizadas q0 -> q1: " + cantidad);
        System.out.println("Regex generada: " + regex);
        System.out.println("Acepta 0: " + Pattern.matches(javaRegex, "0"));
        System.out.println("Acepta 1: " + Pattern.matches(javaRegex, "1"));
        System.out.println("Rechaza 01: " + !Pattern.matches(javaRegex, "01"));
        System.out.println("PRUEBA CORRECTA: etiqueta 0,1.");
    }
}
