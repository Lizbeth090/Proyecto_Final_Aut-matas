
package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class VerificadorEquivalencia {

    public boolean aceptaAFD(Automata afd, String cadena) {
        if (afd == null || cadena == null || afd.getEstadoInicial() == null) {
            throw new IllegalArgumentException("AFD o cadena invalida.");
        }

        validarDeterminismo(afd);
        Estado actual = afd.getEstadoInicial();

        for (int i = 0; i < cadena.length();) {
            int codigo = cadena.codePointAt(i);
            String simbolo = new String(Character.toChars(codigo));
            i += Character.charCount(codigo);

            Estado siguiente = null;

            for (Transicion t : afd.getTransiciones()) {
                
if (t.getOrigen() == actual
        && NormalizadorAutomata.separarSimbolos(
                t.getSimbolo()).contains(simbolo)) {

                    siguiente = t.getDestino();
                    break;
                }
            }

            if (siguiente == null) return false;
            actual = siguiente;
        }

        return afd.getEstadosAceptacion().contains(actual);
    }

    public boolean aceptaRegex(ExpresionRegular expresion, String cadena) {
        if (expresion == null || cadena == null) {
            throw new IllegalArgumentException("Regex o cadena invalida.");
        }
        return Pattern.matches(expresion.aRegexJava(), cadena);
    }

    public boolean verificar(Automata afd, ExpresionRegular expresion,
            String cadena) {
        return aceptaAFD(afd, cadena) == aceptaRegex(expresion, cadena);
    }

    private void validarDeterminismo(Automata afd) {
        Map<Estado, Set<String>> vistos = new HashMap<>();

        for (Transicion t : afd.getTransiciones()) {
            if (t == null || t.getOrigen() == null || t.getDestino() == null
                    || !afd.getEstados().contains(t.getOrigen())
                    || !afd.getEstados().contains(t.getDestino())
                    || t.getSimbolo() == null) {
                throw new IllegalArgumentException("Transicion invalida.");
            }

         
for (String simbolo :
        NormalizadorAutomata.separarSimbolos(t.getSimbolo())) {

    if (!vistos.computeIfAbsent(
            t.getOrigen(), k -> new HashSet<>()).add(simbolo)) {

        throw new IllegalArgumentException(
                "El AFD tiene transiciones repetidas para: "
                + simbolo);
    }
}

        }
    }
}

