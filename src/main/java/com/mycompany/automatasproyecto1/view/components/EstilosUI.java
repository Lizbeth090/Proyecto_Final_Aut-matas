
package com.mycompany.automatasproyecto1.view.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

public final class EstilosUI {

    private static final Color BORDE_ACTIVO =
            new Color(189, 165, 235);

    private static final Color FONDO_INACTIVO =
            new Color(246, 242, 251);

    private static final Color TEXTO_INACTIVO =
            new Color(145, 130, 180);

    private static final Color BORDE_INACTIVO =
            new Color(220, 210, 240);

    private EstilosUI() {
        // Clase de utilidades visuales
    }

    private static Border crearBorde(
            Color color, int vertical, int horizontal) {

        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        color, 1, true
                ),
                BorderFactory.createEmptyBorder(
                        vertical, horizontal,
                        vertical, horizontal
                )
        );
    }

    private static void aplicarEstilo(
            JButton boton,
            Color fondo,
            Color texto,
            int vertical,
            int horizontal) {

        boton.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );

        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(true);
        boton.setOpaque(true);

        boton.setBorder(
                crearBorde(
                        BORDE_ACTIVO,
                        vertical,
                        horizontal
                )
        );

        boton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
    }

    // ESTILOS DE LA VENTANA PRINCIPAL
    public static void estilizarBotonesPanel(JPanel panel) {

        for (Component componente : panel.getComponents()) {

            if (componente instanceof JButton boton) {

                aplicarEstilo(
                        boton,
                        new Color(224, 210, 255),
                        new Color(65, 42, 115),
                        8, 8
                );
            }
        }
    }

    // ESTILOS DE LA VENTANA REGEX
    public static void estilizarBotonRegex(
            JButton boton,
            Color fondo,
            Color texto) {

        aplicarEstilo(boton, fondo, texto, 9, 14);

        boton.addPropertyChangeListener("enabled", evt -> {

            if (boton.isEnabled()) {

                boton.setBackground(fondo);
                boton.setForeground(texto);

                boton.setBorder(
                        crearBorde(BORDE_ACTIVO, 9, 14)
                );

            } else {

                boton.setBackground(FONDO_INACTIVO);
                boton.setForeground(TEXTO_INACTIVO);

                boton.setBorder(
                        crearBorde(BORDE_INACTIVO, 9, 14)
                );
            }

            boton.repaint();
        });

        if (!boton.isEnabled()) {

            boton.setBackground(FONDO_INACTIVO);
            boton.setForeground(TEXTO_INACTIVO);

            boton.setBorder(
                    crearBorde(BORDE_INACTIVO, 9, 14)
            );
        }
    }
}
