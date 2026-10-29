package com.mycompany.automatasproyecto1.conversion;
public class PruebaExpresionRegular {
    public static void main(String[] args) {
        ExpresionRegular a = ExpresionRegular.simbolo("a");
        ExpresionRegular b = ExpresionRegular.simbolo("b");
        ExpresionRegular union = ExpresionRegular.union(a, b);
        ExpresionRegular concatenacion =
                ExpresionRegular.concatenar(a, b);
        ExpresionRegular estrella =
                ExpresionRegular.estrella(union);
        System.out.println("Unión: " + union);
        System.out.println("Concatenación: " + concatenacion);
        System.out.println("Kleene: " + estrella);
        System.out.println("Épsilon + a: "
                + ExpresionRegular.concatenar(
                        ExpresionRegular.epsilon(), a));

        System.out.println("Vacío + a: "
                + ExpresionRegular.concatenar(
                        ExpresionRegular.vacio(), a));
        if (!union.toString().equals("a ∪ b")
                || !concatenacion.toString().equals("ab")
                || !estrella.toString().equals("(a ∪ b)*")
                || !ExpresionRegular.concatenar(
                        ExpresionRegular.epsilon(), a)
                        .toString().equals("a")
                || !ExpresionRegular.concatenar(
                        ExpresionRegular.vacio(), a)
                        .toString().equals("∅")) {
            throw new AssertionError("La prueba falló.");
        }
        System.out.println("PRUEBA CORRECTA");
    }
}
