
package com.mycompany.automatasproyecto1.view.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PanelMensajes extends JScrollPane {

    private final JTextArea areaMensajes;

    public PanelMensajes() {

        areaMensajes = new JTextArea();

        areaMensajes.setEditable(false);
        areaMensajes.setLineWrap(true);
        areaMensajes.setWrapStyleWord(true);

        areaMensajes.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );

        areaMensajes.setForeground(
                new Color(88, 70, 150)
        );

        areaMensajes.setBackground(
                new Color(252, 249, 255)
        );

        areaMensajes.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 12, 10, 12
                )
        );

        setViewportView(areaMensajes);

        setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                new Color(196, 173, 255),
                                2,
                                true
                        ),
                        "✦ Mensajes del sistema"
                )
        );

        setPreferredSize(new Dimension(280, 140));

        areaMensajes.setText(
                "Editor listo para comenzar.\n"
        );
    }

    public void agregarMensaje(String mensaje) {

        areaMensajes.append(
                "• " + mensaje + "\n"
        );

        areaMensajes.setCaretPosition(
                areaMensajes.getDocument().getLength()
        );
    }
}
