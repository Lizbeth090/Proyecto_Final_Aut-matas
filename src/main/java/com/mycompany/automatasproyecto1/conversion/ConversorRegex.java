
package com.mycompany.automatasproyecto1.conversion;

import com.mycompany.automatasproyecto1.model.Automata;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConversorRegex {

    // Mantiene funcionando las pruebas anteriores.
    public ExpresionRegular convertir(Automata original) {
        return convertirConPasos(original).getExpresion();
    }

    public ResultadoConversion convertirConPasos(Automata original) {
        NormalizadorAutomata.GrafoNormalizado grafo =
                new NormalizadorAutomata().normalizar(original);

        List<String> activos = new ArrayList<>(grafo.getEstados());
        Map<String, Map<String, ExpresionRegular>> rutas =
                new LinkedHashMap<>();

        List<PasoConversion> pasos = new ArrayList<>();

        for (String estado : activos) {
            rutas.put(estado, new LinkedHashMap<>());
        }

        for (NormalizadorAutomata.Arista arista : grafo.getAristas()) {
            ExpresionRegular etiqueta;

            if (arista.getSimbolo().equals(NormalizadorAutomata.EPSILON)) {
                etiqueta = ExpresionRegular.epsilon();
            } else if (arista.getSimbolo().equals("∅")) {
                etiqueta = ExpresionRegular.vacio();
            } else {
                etiqueta = ExpresionRegular.simbolo(arista.getSimbolo());
            }

            poner(rutas, arista.getOrigen(), arista.getDestino(),
                    ExpresionRegular.union(
                            obtener(rutas, arista.getOrigen(), arista.getDestino()),
                            etiqueta));
        }

        pasos.add(capturarPaso("Autómata normalizado", activos,
                grafo, rutas));

        for (String eliminado : new ArrayList<>(activos)) {
            if (eliminado.equals(grafo.getInicio())
                    || eliminado.equals(grafo.getAceptacion())) {
                continue;
            }

            for (String desde : activos) {
                if (desde.equals(eliminado)) continue;

                for (String hasta : activos) {
                    if (hasta.equals(eliminado)) continue;

                    ExpresionRegular alternativa =
                            ExpresionRegular.concatenar(
                                    obtener(rutas, desde, eliminado),
                                    ExpresionRegular.concatenar(
                                            ExpresionRegular.estrella(
                                                    obtener(rutas, eliminado, eliminado)),
                                            obtener(rutas, eliminado, hasta)));

                    poner(rutas, desde, hasta,
                            ExpresionRegular.union(
                                    obtener(rutas, desde, hasta),
                                    alternativa));
                }
            }

            activos.remove(eliminado);
            rutas.remove(eliminado);

            for (Map<String, ExpresionRegular> salidas : rutas.values()) {
                salidas.remove(eliminado);
            }

            pasos.add(capturarPaso("Eliminar " + eliminado,
                    activos, grafo, rutas));
        }

        return new ResultadoConversion(
                obtener(rutas, grafo.getInicio(), grafo.getAceptacion()),
                pasos);
    }

    private PasoConversion capturarPaso(
            String descripcion,
            List<String> activos,
            NormalizadorAutomata.GrafoNormalizado grafo,
            Map<String, Map<String, ExpresionRegular>> rutas) {

        List<NormalizadorAutomata.Arista> transiciones =
                new ArrayList<>();

        for (String desde : activos) {
            for (String hasta : activos) {
                ExpresionRegular expresion =
                        obtener(rutas, desde, hasta);

                if (!expresion.toString().equals("∅")) {
                    transiciones.add(
                            new NormalizadorAutomata.Arista(
                                    desde,
                                    expresion.toString(),
                                    hasta));
                }
            }
        }

        return new PasoConversion(
                descripcion,
                activos,
                grafo.getInicio(),
                grafo.getAceptacion(),
                transiciones);
    }

    private ExpresionRegular obtener(
            Map<String, Map<String, ExpresionRegular>> rutas,
            String desde, String hasta) {

        return rutas.get(desde).getOrDefault(
                hasta, ExpresionRegular.vacio());
    }

    private void poner(
            Map<String, Map<String, ExpresionRegular>> rutas,
            String desde, String hasta,
            ExpresionRegular expresion) {

        rutas.get(desde).put(hasta, expresion);
    }
}
