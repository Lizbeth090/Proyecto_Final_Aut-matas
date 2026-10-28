
package com.mycompany.automatasproyecto1.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.automatasproyecto1.model.*;

import java.awt.Point;
import java.util.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AutomataJson {

    private static final Gson gson =
            new GsonBuilder().setPrettyPrinting().create();

    private AutomataJson() {}

    public static class Datos {
        public int version = 1;
        public List<EstadoDato> estados = new ArrayList<>();
        public String inicial;
        public List<TransicionDato> transiciones =
                new ArrayList<>();
    }

    public static class EstadoDato {
        public String nombre;
        public int x, y;
        public boolean aceptacion;
    }

    public static class TransicionDato {
        public String origen;
        public String destino;
        public String simbolo;
    }

    // EXPORTAR
    public static String exportar(
            Automata automata,
            Map<Estado, Point> posiciones) {

        Datos datos = new Datos();

        if (automata.getEstadoInicial() != null) {
            datos.inicial =
                    automata.getEstadoInicial().getNombre();
        }

        for (Estado estado : automata.getEstados()) {

            EstadoDato dato = new EstadoDato();

            dato.nombre = estado.getNombre();
            dato.aceptacion =
                    automata.esEstadoAceptacion(estado);

            Point posicion = posiciones.get(estado);

            dato.x = posicion == null
                    ? estado.getX() : posicion.x;

            dato.y = posicion == null
                    ? estado.getY() : posicion.y;

            datos.estados.add(dato);
        }

        for (Transicion transicion :
                automata.getTransiciones()) {

            TransicionDato dato = new TransicionDato();

            dato.origen =
                    transicion.getOrigen().getNombre();

            dato.destino =
                    transicion.getDestino().getNombre();

            dato.simbolo = transicion.getSimbolo();

            datos.transiciones.add(dato);
        }

        return gson.toJson(datos);
    }

    // IMPORTAR Y VALIDAR
    public static Datos importar(String json) {

        Datos datos;

        try {
            datos = gson.fromJson(json, Datos.class);
        } catch (com.google.gson.JsonParseException ex) {
            throw new IllegalArgumentException(
                    "El archivo JSON no es válido.", ex);
        }

        if (datos == null || datos.version != 1
                || datos.estados == null
                || datos.transiciones == null) {
            throw new IllegalArgumentException(
                    "Formato de autómata no válido.");
        }

        if (datos.estados.size() > 500
                || datos.transiciones.size() > 5000) {
            throw new IllegalArgumentException(
                    "El archivo contiene demasiados elementos.");
        }

        Set<String> nombres = new HashSet<>();

        for (EstadoDato estado : datos.estados) {

            if (estado == null
                    || estado.nombre == null
                    || estado.nombre.isBlank()) {
                throw new IllegalArgumentException(
                        "Estado sin nombre válido.");
            }

            String nombre = estado.nombre.toLowerCase(
                    Locale.ROOT);

            if (!nombres.add(nombre)) {
                throw new IllegalArgumentException(
                        "Estado repetido: " + estado.nombre);
            }
        }

        if (datos.inicial != null
                && !nombres.contains(
                        datos.inicial.toLowerCase(
                                Locale.ROOT))) {
            throw new IllegalArgumentException(
                    "El estado inicial no existe.");
        }

        Set<String> transiciones = new HashSet<>();

        for (TransicionDato t : datos.transiciones) {

            if (t == null || t.origen == null
                    || t.destino == null
                    || t.simbolo == null
                    || t.simbolo.length() != 1
                    || !nombres.contains(
                            t.origen.toLowerCase(Locale.ROOT))
                    || !nombres.contains(
                            t.destino.toLowerCase(Locale.ROOT))) {

                throw new IllegalArgumentException(
                        "Transición no válida.");
            }

            String clave =
                    t.origen.toLowerCase(Locale.ROOT)
                    + ":" + t.simbolo;

            if (!transiciones.add(clave)) {
                throw new IllegalArgumentException(
                        "Hay transiciones duplicadas.");
            }
        }

        return datos;
    }
    
    
    /**
     * Guarda el automata en un archivo JSON.
     */
    public static void guardarEnArchivo(
            Path archivo,
            Automata automata,
            Map<Estado, Point> posiciones) throws IOException {

        String json = exportar(automata, posiciones);

        Files.writeString(
                archivo,
                json,
                StandardCharsets.UTF_8
        );
    }

    /**
     * Lee y valida un automata desde un archivo JSON.
     */
    public static Datos leerDesdeArchivo(
            Path archivo) throws IOException {

        if (Files.size(archivo) > 2_000_000) {
            throw new IllegalArgumentException(
                    "El archivo supera el limite de 2 MB."
            );
        }

        String json = Files.readString(
                archivo,
                StandardCharsets.UTF_8
        );

        return importar(json);
    }

}
