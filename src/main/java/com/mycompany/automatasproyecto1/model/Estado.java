
package com.mycompany.automatasproyecto1.model;

/**
 * Representa un estado dentro de un autómata finito.
 */
public class Estado {

    private String nombre;
    private int x;
    private int y;
    private boolean inicial;
    private boolean aceptacion;

    /**
     * Constructor del estado.
     */
    public Estado(String nombre, int x, int y) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.inicial = false;
        this.aceptacion = false;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isInicial() {
        return inicial;
    }

    public void setInicial(boolean inicial) {
        this.inicial = inicial;
    }

    public boolean isAceptacion() {
        return aceptacion;
    }

    public void setAceptacion(boolean aceptacion) {
        this.aceptacion = aceptacion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}