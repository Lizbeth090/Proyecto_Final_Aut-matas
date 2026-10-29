
package com.mycompany.automatasproyecto1.conversion;

import javax.swing.JOptionPane;

public class PruebaSimbolosVentana {

    public static void main(String[] args) {

        JOptionPane.showMessageDialog(
            null,
            "Épsilon: ε\n" +
            "Conjunto vacío: ∅\n" +
            "Unión: a ∪ b\n" +
            "Estrella de Kleene: a*",
            "Prueba de símbolos matemáticos",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
