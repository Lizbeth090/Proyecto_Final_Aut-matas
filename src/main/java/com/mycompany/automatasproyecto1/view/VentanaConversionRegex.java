package com.mycompany.automatasproyecto1.view;

import com.mycompany.automatasproyecto1.conversion.PasoConversion;
import com.mycompany.automatasproyecto1.conversion.ResultadoConversion;
import com.mycompany.automatasproyecto1.conversion.NormalizadorAutomata;
import com.mycompany.automatasproyecto1.view.components.EstilosUI;
import com.mycompany.automatasproyecto1.view.graph.ConstructorGrafoRegex;

import com.mxgraph.view.mxGraph;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxConstants;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

public class VentanaConversionRegex extends JFrame {

    private final JPanel panelGrafo =
            new JPanel(new BorderLayout());
    
    private final JSlider barraZoom =
             new JSlider(50, 400, 100);

    // Un único lienzo para toda la conversión
    private final mxGraphComponent componenteVisual =
            new mxGraphComponent(new mxGraph());

    private final JLabel etiquetaPaso =
            new JLabel("Conversión no iniciada");

    private final JTextArea areaDescripcion =
            new JTextArea();

    private final JTextField campoRegex =
            new JTextField();

    private final JButton botonAnterior =
            new JButton("Anterior");

    private final JButton botonSiguiente =
            new JButton("Siguiente");

    private final JButton botonReproducir =
            new JButton("Reproducir");

    private final JTextField campoCadenaVerificacion =
            new JTextField();

    private final JButton botonVerificar =
            new JButton("Verificar cadena");

    private final JButton botonIniciar =
            new JButton("Iniciar conversión");

    private Runnable accionConversion;

    private final JLabel etiquetaAFD =
            new JLabel("AFD: ---");

    private final JLabel etiquetaRegex =
            new JLabel("Regex: ---");

    private final JLabel etiquetaComparacion =
            new JLabel("Resultado: ---");

    private java.util.function.Consumer<String>
            accionVerificacion;

    private final javax.swing.Timer temporizador =
            new javax.swing.Timer(
                    1200,
                    e -> avanzarAutomaticamente()
            );

    private final List<PasoVisual> pasos =
            new ArrayList<>();

    private int indicePaso = 0;

    // Evita programar múltiples reajustes mientras se redimensiona la ventana.
    private boolean ajustePendiente = false;
    

    // INFORMACIÓN DE CADA PASO
    private static class PasoVisual {

        mxGraph grafo;
        String titulo;
        String descripcion;
        String regex;

        PasoVisual(mxGraph grafo, String titulo,
                   String descripcion, String regex) {

            this.grafo = grafo;
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.regex = regex;
        }
    }

    // CONSTRUCTOR
    public VentanaConversionRegex() {

        setTitle("Conversión de autómata a Regex");
        setSize(950, 650);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ENCABEZADO
        JPanel encabezado =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        encabezado.add(new JLabel("Proceso:"));
        encabezado.add(etiquetaPaso);

        add(encabezado, BorderLayout.NORTH);

        // CONFIGURACIÓN DEL LIENZO
        panelGrafo.setBorder(
                BorderFactory.createTitledBorder(
                        "Grafo de conversión"
                )
        );

        componenteVisual.setConnectable(false);
        
        // Ocultar los botones + y - de los estados
        componenteVisual.setFoldingEnabled(false);

        componenteVisual.setPageVisible(false);

        componenteVisual.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        componenteVisual.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        panelGrafo.add(
                componenteVisual,
                BorderLayout.CENTER
        );
        
        
        
    // CONTROLES DE ZOOM
    JPanel panelZoom = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 6, 3)
    );

    panelZoom.setBackground(
            new Color(245, 240, 255)
    );

    JButton botonMenos = new JButton("−");
    JButton botonMas = new JButton("+");
    JButton botonAjustar = new JButton("Ajustar");

    JLabel etiquetaZoom = new JLabel("100%");

    barraZoom.setPreferredSize(new Dimension(160, 25));
    barraZoom.setMajorTickSpacing(50);
    barraZoom.setPaintTicks(true);
    barraZoom.setOpaque(false);

    JLabel tituloZoom = new JLabel("Zoom:");

    tituloZoom.setFont(
            new Font("Segoe UI", Font.BOLD, 13)
    );

    tituloZoom.setForeground(
            new Color(88, 60, 150)
    );

    panelZoom.add(tituloZoom);
    panelZoom.add(botonMenos);
    panelZoom.add(barraZoom);
    panelZoom.add(botonMas);
    panelZoom.add(etiquetaZoom);
    panelZoom.add(botonAjustar);
    
    // ESTILO DE LOS BOTONES DEL ZOOM

    // Botón para reducir (-)
    estilizarBotonRegex(
            botonMenos,
            new Color(224, 210, 255),
            new Color(88, 60, 150)
    );

    // Botón para aumentar (+)
    estilizarBotonRegex(
            botonMas,
            new Color(224, 210, 255),
            new Color(88, 60, 150)
    );

    // Botón Ajustar
    estilizarBotonRegex(
            botonAjustar,
            new Color(177, 129, 225),
            Color.WHITE
    );

    // Estilo de la barra deslizante
    barraZoom.setForeground(
            new Color(126, 95, 205)
    );

    barraZoom.setBackground(
            new Color(245, 240, 255)
    );

    barraZoom.setPaintTicks(false);
    barraZoom.setFocusable(false);

    // Porcentaje de zoom
    etiquetaZoom.setFont(
            new Font("Segoe UI", Font.BOLD, 13)
    );

    etiquetaZoom.setForeground(
            new Color(88, 60, 150)
    );

    panelZoom.setBorder(
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
    );
    panelGrafo.add(panelZoom, BorderLayout.NORTH);

    // Controlar el zoom con la barra
    barraZoom.addChangeListener(e -> {
        etiquetaZoom.setText(barraZoom.getValue() + "%");
        solicitarAjusteGrafo();
    });

    // Reducir
    botonMenos.addActionListener(e ->
            barraZoom.setValue(
                    Math.max(50, barraZoom.getValue() - 25)
            )
    );

    // Aumentar
    botonMas.addActionListener(e ->
            barraZoom.setValue(
                    Math.min(400, barraZoom.getValue() + 25)
            )
    );

    // Recuperar el ajuste automático
    botonAjustar.addActionListener(e ->
            barraZoom.setValue(100)
    );


        // REAJUSTAR CUANDO CAMBIA EL ÁREA REALMENTE VISIBLE DEL LIENZO
        componenteVisual.getViewport().addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        solicitarAjusteGrafo();
                    }
                }
        );

        // DESCRIPCIÓN
        areaDescripcion.setEditable(false);
        areaDescripcion.setLineWrap(true);
        areaDescripcion.setWrapStyleWord(true);

        JScrollPane scroll =
                new JScrollPane(areaDescripcion);

        scroll.setPreferredSize(
                new Dimension(100, 120)
        );

        scroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Descripción del paso"
                )
        );

        JPanel centro =
                new JPanel(new BorderLayout(8, 8));

        centro.add(panelGrafo, BorderLayout.CENTER);
        centro.add(scroll, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);

        // RESULTADO
        JPanel inferior =
                new JPanel(new BorderLayout(8, 8));

        JPanel resultado =
                new JPanel(new BorderLayout(8, 8));

        resultado.add(
                new JLabel("Regex final:"),
                BorderLayout.WEST
        );

        campoRegex.setEditable(false);

        resultado.add(
                campoRegex,
                BorderLayout.CENTER
        );

        inferior.add(resultado, BorderLayout.NORTH);

        // Panel de verificación de equivalencia
        JPanel panelVerificacion = new JPanel(
                new BorderLayout(8, 8)
        );

        panelVerificacion.setBorder(
                BorderFactory.createTitledBorder(
                        "Verificación de equivalencia"
                )
        );

        JPanel filaEntrada = new JPanel(
                new BorderLayout(8, 0)
        );

        filaEntrada.add(
                new JLabel("Cadena:"),
                BorderLayout.WEST
        );

        filaEntrada.add(
                campoCadenaVerificacion,
                BorderLayout.CENTER
        );

        botonVerificar.setEnabled(false);

        filaEntrada.add(
                botonVerificar,
                BorderLayout.EAST
        );

        JPanel filaResultados = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 15, 0)
        );

        filaResultados.add(etiquetaAFD);
        filaResultados.add(etiquetaRegex);
        filaResultados.add(etiquetaComparacion);

        panelVerificacion.add(
                filaEntrada,
                BorderLayout.NORTH
        );

        panelVerificacion.add(
                filaResultados,
                BorderLayout.CENTER
        );

        inferior.add(
                panelVerificacion,
                BorderLayout.CENTER
        );

        // BOTONES
        JPanel controles =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton botonCerrar =
                new JButton("Cerrar");

        botonAnterior.setEnabled(false);
        botonSiguiente.setEnabled(false);

        botonIniciar.setEnabled(false);

        botonIniciar.addActionListener(e -> {
            if (accionConversion != null) {
                accionConversion.run();
            }
        });

        botonAnterior.addActionListener(e ->
                mostrarPaso(indicePaso - 1)
        );

        botonSiguiente.addActionListener(e ->
                mostrarPaso(indicePaso + 1)
        );

        botonReproducir.addActionListener(e -> {
            alternarReproduccion();
        });

        botonCerrar.addActionListener(e -> dispose());

        botonVerificar.addActionListener(e -> {

            if (accionVerificacion != null) {
                accionVerificacion.accept(
                        campoCadenaVerificacion.getText()
                );
            }
        });

        controles.add(botonAnterior);
        controles.add(botonSiguiente);
        controles.add(botonReproducir);
        controles.add(botonIniciar);
        controles.add(botonCerrar);

        inferior.add(controles, BorderLayout.SOUTH);

        add(inferior, BorderLayout.SOUTH);
        
        
        // Texto más visible en botones deshabilitados
        UIManager.put(
                "Button.disabledText",
                new Color(145, 130, 180)
        );

        UIManager.put(
                "Button.disabledForeground",
                new Color(145, 130, 180)
        );
        
        // ESTÉTICA DE LA VENTANA REGEX

        Color fondo = new Color(245, 240, 255);
        Color panel = new Color(252, 249, 255);
        Color morado = new Color(88, 60, 150);
        Color borde = new Color(196, 173, 255);

        getContentPane().setBackground(fondo);

        encabezado.setBackground(fondo);
        centro.setBackground(fondo);
        inferior.setBackground(fondo);
        resultado.setBackground(fondo);
        controles.setBackground(fondo);

        panelGrafo.setBackground(panel);
        panelVerificacion.setBackground(panel);
        filaEntrada.setBackground(panel);
        filaResultados.setBackground(panel);

        // TÍTULOS DE SECCIONES
        panelGrafo.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(borde, 2, true),
                        "Grafo de conversión",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13),
                        morado
                )
        );

        scroll.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(borde, 2, true),
                        "Descripción del paso",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13),
                        morado
                )
        );

        panelVerificacion.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(borde, 2, true),
                        "Verificación de equivalencia",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13),
                        morado
                )
        );

        // CAMPOS DE TEXTO
        areaDescripcion.setBackground(Color.WHITE);
        areaDescripcion.setForeground(morado);
        areaDescripcion.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );

        campoRegex.setBackground(Color.WHITE);
        campoRegex.setForeground(morado);
        campoRegex.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        campoCadenaVerificacion.setBackground(Color.WHITE);
        campoCadenaVerificacion.setForeground(morado);

        // FONDO DEL LIENZO
        componenteVisual.getGraphControl().setBackground(
                new Color(252, 250, 255)
        );

        componenteVisual.getViewport().setBackground(
                new Color(252, 250, 255)
        );

        // BOTONES
        estilizarBotonRegex(
                botonAnterior,
                new Color(224, 210, 255), morado
        );

        estilizarBotonRegex(
                botonSiguiente,
                new Color(224, 210, 255), morado
        );

        estilizarBotonRegex(
                botonReproducir,
                new Color(177, 129, 225), Color.WHITE
        );

        estilizarBotonRegex(
                botonIniciar,
                new Color(126, 95, 205), Color.WHITE
        );

        estilizarBotonRegex(
                botonVerificar,
                new Color(177, 129, 225), Color.WHITE
        );

        estilizarBotonRegex(
                botonCerrar,
                new Color(255, 205, 230), morado
        );
        
        // TÍTULO DEL PROCESO
        etiquetaPaso.setFont(
                new Font("Segoe UI", Font.BOLD, 15)
        );
        etiquetaPaso.setForeground(
                new Color(88, 60, 150)
        );

        // RESULTADOS DE VERIFICACIÓN
        JLabel[] etiquetas = {
            etiquetaAFD,
            etiquetaRegex,
            etiquetaComparacion
        };

        for (JLabel etiqueta : etiquetas) {
            etiqueta.setFont(
                    new Font("Segoe UI", Font.BOLD, 13)
            );
        }

        // SEPARACIÓN ENTRE BOTONES
        ((FlowLayout) controles.getLayout()).setHgap(8);


        // Ajustar al abrir la ventana, después de calcular su tamaño.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                solicitarAjusteGrafo();
            }
        });
    }

    // MOSTRAR COPIA DEL AUTOMATA ORIGINAL
    public void mostrarCopiaAutomata(mxGraph original) {

        pasos.clear();
        indicePaso = 0;

        agregarPasoVisual(
                original,
                "Autómata original",
                "Esta es una copia del autómata creado "
                + "en el editor principal.",
                ""
        );
    }

    // AGREGAR PASO VISUAL
    public void agregarPasoVisual(
            mxGraph original,
            String titulo,
            String descripcion,
            String regex) {

        mxGraph copia = new mxGraph();

        // Bloquear modificaciones
        copia.setCellsEditable(false);
        copia.setCellsMovable(false);
        copia.setCellsResizable(false);
        copia.setCellsBendable(false);
        copia.setCellsDisconnectable(false);
        copia.setAllowDanglingEdges(false);

        Object[] celdas = original.getChildCells(
                original.getDefaultParent(),
                true,
                true
        );

        Object[] clones =
                original.cloneCells(celdas, true);

        copia.getModel().beginUpdate();

        try {
            copia.addCells(clones);
        } finally {
            copia.getModel().endUpdate();
        }

        pasos.add(
                new PasoVisual(
                        copia,
                        titulo,
                        descripcion,
                        regex
                )
        );

        mostrarPaso(indicePaso);
    }

    // CAMBIAR ENTRE PASOS
    private void mostrarPaso(int indice) {

        if (indice < 0 || indice >= pasos.size()) {
            return;
        }

        indicePaso = indice;

        PasoVisual paso = pasos.get(indicePaso);

        // Primero instalar el grafo, después solicitar el ajuste.
        // No se destruye ni reemplaza el componente visual.
        componenteVisual.setGraph(paso.grafo);
        
        // Ajustar inmediatamente antes de repintar
        ajustarGrafoAlLienzo(
                componenteVisual,
                paso.grafo
        );

        componenteVisual.getViewport().setViewPosition(
                new Point(0, 0)
        );

        // Actualizar información
        etiquetaPaso.setText(
                "Paso " + (indicePaso + 1)
                + " de " + pasos.size()
                + " - " + paso.titulo
        );

        areaDescripcion.setText(
                paso.descripcion.replace("∪", "|")
        );

        areaDescripcion.setCaretPosition(0);

        campoRegex.setText(
                paso.regex.replace("∪", "|")
        );

        actualizarBotones();
        componenteVisual.repaint();

    }

    // PROGRAMAR EL AJUSTE UNA SOLA VEZ POR CICLO DE EVENTOS
    private void solicitarAjusteGrafo() {

        if (ajustePendiente) {
            return;
        }

        ajustePendiente = true;

        SwingUtilities.invokeLater(() -> {
            ajustePendiente = false;

            if (pasos.isEmpty() || indicePaso < 0
                    || indicePaso >= pasos.size()) {
                return;
            }

            mxGraph grafo = pasos.get(indicePaso).grafo;
            if (componenteVisual.getGraph() == grafo) {
                ajustarGrafoAlLienzo(componenteVisual, grafo);
            }
        });
    }

    // CENTRAR Y AJUSTAR EL GRAFO
    private void ajustarGrafoAlLienzo(
            mxGraphComponent componente,
            mxGraph grafo) {

        if (componente == null || grafo == null) {
            return;
        }

        Dimension area =
                componente.getViewport().getExtentSize();

        if (area.width <= 0 || area.height <= 0) {
            return;
        }

        // Calcular los límites sin escala ni traslación anteriores.
        grafo.getView().scaleAndTranslate(1.0, 0, 0);
        grafo.getView().validate();

        mxRectangle limites =
                grafo.getView().getGraphBounds();

        if (limites == null
                || limites.getWidth() <= 0
                || limites.getHeight() <= 0) {
            return;
        }

        double margen = 35;

        double escalaX =
                Math.max(1.0, area.getWidth() - margen * 2)
                / limites.getWidth();

        double escalaY =
                Math.max(1.0, area.getHeight() - margen * 2)
                / limites.getHeight();

        // Escala automática original
        double escalaBase = Math.min(
                1.0,
                Math.min(escalaX, escalaY)
        );

        // Aplicar el porcentaje elegido por el usuario
        double escala =
                escalaBase * barraZoom.getValue() / 100.0;

        if (!Double.isFinite(escala) || escala <= 0) {
            return;
        }

        double anchoDibujo = limites.getWidth() * escala;
        double altoDibujo = limites.getHeight() * escala;

        // Centrar si cabe; dejar margen si supera la ventana
        double posicionX = anchoDibujo < area.getWidth() - 70
                ? (area.getWidth() - anchoDibujo) / 2
                : 35;

        double posicionY = altoDibujo < area.getHeight() - 70
                ? (area.getHeight() - altoDibujo) / 2
                : 35;

        double moverX =
                posicionX / escala - limites.getX();

        double moverY =
                posicionY / escala - limites.getY();


        grafo.getView().scaleAndTranslate(
                escala, moverX, moverY
        );

        componente.getViewport().setViewPosition(
                new Point(0, 0)
        );

        componente.refresh();
        componente.repaint();
    }

    // Ejecución independiente de la ventana para pruebas
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            VentanaConversionRegex ventana =
                    new VentanaConversionRegex();
            ventana.setVisible(true);
        });
    }

    // REPRODUCCIÓN DEL AUTOMATA
    private void alternarReproduccion() {

        if (temporizador.isRunning()) {
            temporizador.stop();
        } else {
            if (pasos.size() < 2) {
                return;
            }

            if (indicePaso >= pasos.size() - 1) {
                mostrarPaso(0);
            }

            temporizador.start();
        }

        actualizarBotones();
    }

    private void avanzarAutomaticamente() {

        if (indicePaso < pasos.size() - 1) {
            mostrarPaso(indicePaso + 1);
        }

        if (indicePaso >= pasos.size() - 1) {
            temporizador.stop();
        }

        actualizarBotones();
    }

    private void actualizarBotones() {

        boolean reproduciendo = temporizador.isRunning();

        botonAnterior.setEnabled(
                !reproduciendo && indicePaso > 0
        );

        botonSiguiente.setEnabled(
                !reproduciendo
                && indicePaso < pasos.size() - 1
        );

        botonReproducir.setEnabled(pasos.size() > 1);

        botonReproducir.setText(
                reproduciendo ? "Pausar" : "Reproducir"
        );
    }

    // DETENER EL TEMPORIZADOR AL CERRAR LA VENTANA
    @Override
    public void dispose() {
        temporizador.stop();
        super.dispose();
    }

    // Configurar verificación de cadenas
    public void configurarVerificador(
            java.util.function.Consumer<String> accion) {

        this.accionVerificacion = accion;
        botonVerificar.setEnabled(accion != null);
    }

    // Mostrar resultados de equivalencia
    public void mostrarResultadoVerificacion(
            boolean aceptadaAFD,
            boolean aceptadaRegex) {

        etiquetaAFD.setText(
                "AFD: " + (aceptadaAFD
                        ? "Aceptada" : "Rechazada")
        );

        etiquetaRegex.setText(
                "Regex: " + (aceptadaRegex
                        ? "Aceptada" : "Rechazada")
        );

        if (aceptadaAFD == aceptadaRegex) {
            etiquetaComparacion.setText(
                    "Resultado: Coinciden"
            );
            etiquetaComparacion.setForeground(
                    new Color(30, 135, 65)
            );
        } else {
            etiquetaComparacion.setText(
                    "Resultado: No coinciden"
            );
            etiquetaComparacion.setForeground(
                    new Color(190, 45, 45)
            );
        }
    }

    public void resaltarEstadoEnPaso(
            int numeroPaso, String nombreEstado) {

        if (numeroPaso < 0 || numeroPaso >= pasos.size()) {
            return;
        }

        mxGraph grafo = pasos.get(numeroPaso).grafo;

        Object[] estados = grafo.getChildVertices(
                grafo.getDefaultParent()
        );

        for (Object celda : estados) {

            Object valor = grafo.getModel().getValue(celda);

            if (valor != null
                    && valor.toString().trim().equals(nombreEstado)) {

                grafo.getModel().beginUpdate();

                try {
                    grafo.setCellStyles(
                            mxConstants.STYLE_FILLCOLOR,
                            "#FFF3B0",
                            new Object[]{celda}
                    );

                    grafo.setCellStyles(
                            mxConstants.STYLE_STROKECOLOR,
                            "#D97706",
                            new Object[]{celda}
                    );

                    grafo.setCellStyles(
                            mxConstants.STYLE_STROKEWIDTH,
                            "4",
                            new Object[]{celda}
                    );

                } finally {
                    grafo.getModel().endUpdate();
                }

                if (indicePaso == numeroPaso) {
                    componenteVisual.repaint();
                }

                break;
            }
        }
    }

    public void agregarPruebaEliminacionVisual(
            int pasoOrigen, String nombreEstado) {

        if (pasoOrigen < 0 || pasoOrigen >= pasos.size()) {
            return;
        }

        mxGraph original = pasos.get(pasoOrigen).grafo;

        agregarPasoVisual(
                original,
                "Eliminación visual de " + nombreEstado,
                "Demostración visual de la eliminación. "
                + "Las nuevas transiciones serán calculadas "
                + "por el algoritmo de conversión.",
                ""
        );

        mxGraph copia = pasos.get(pasos.size() - 1).grafo;

        Object[] estados = copia.getChildVertices(
                copia.getDefaultParent()
        );

        for (Object celda : estados) {

            Object valor = copia.getModel().getValue(celda);

            if (valor != null
                    && valor.toString().trim().equals(nombreEstado)) {

                copia.getModel().beginUpdate();

                try {
                    copia.removeCells(
                            new Object[]{celda}, true
                    );
                } finally {
                    copia.getModel().endUpdate();
                }

                break;
            }
        }
    }

    // Resaltar una transición en un paso
    public void resaltarTransicionEnPaso(
            int numeroPaso,
            String nombreOrigen,
            String nombreDestino) {

        if (numeroPaso < 0 || numeroPaso >= pasos.size()) {
            return;
        }

        mxGraph grafo = pasos.get(numeroPaso).grafo;

        Object[] aristas = grafo.getChildEdges(
                grafo.getDefaultParent()
        );

        grafo.getModel().beginUpdate();

        try {
            for (Object arista : aristas) {

                Object origen = grafo.getModel()
                        .getTerminal(arista, true);

                Object destino = grafo.getModel()
                        .getTerminal(arista, false);

                if (origen == null || destino == null) {
                    continue;
                }

                String desde = String.valueOf(
                        grafo.getModel().getValue(origen)
                ).trim();

                String hasta = String.valueOf(
                        grafo.getModel().getValue(destino)
                ).trim();

                if (desde.equals(nombreOrigen)
                        && hasta.equals(nombreDestino)) {

                    grafo.setCellStyles(
                            mxConstants.STYLE_STROKECOLOR,
                            "#2563EB",
                            new Object[]{arista}
                    );

                    grafo.setCellStyles(
                            mxConstants.STYLE_STROKEWIDTH,
                            "4",
                            new Object[]{arista}
                    );

                    grafo.setCellStyles(
                            mxConstants.STYLE_FONTCOLOR,
                            "#1D4ED8",
                            new Object[]{arista}
                    );
                }
            }

        } finally {
            grafo.getModel().endUpdate();
        }

        if (indicePaso == numeroPaso) {
            componenteVisual.repaint();
        }
    }

    public void configurarAccionConversion(Runnable accion) {
        this.accionConversion = accion;
        botonIniciar.setEnabled(accion != null);
    }

    // Mostrar la expresión regular calculada
    public void mostrarRegexFinal(String regex) {
        campoRegex.setText(
                regex.replace("∪", "|")
        );
    }

    public void cargarResultadoConversion(
            ResultadoConversion resultado, mxGraph original) {

        mostrarCopiaAutomata(original);

        for (PasoConversion paso : resultado.getPasos()) {

            mxGraph grafoPaso =
                    ConstructorGrafoRegex.construirGrafoPaso(paso);

            agregarPasoVisual(
                    grafoPaso,
                    paso.getDescripcion(),
                    descripcionLegiblePaso(paso),
                    ""
            );
        }

        // Guardar la Regex en el último paso
        if (!pasos.isEmpty()) {
            pasos.get(pasos.size() - 1).regex =
                    resultado.getExpresion().toString();
        }

        mostrarPaso(0);
    }
    
    
    
    private static final int LIMITE_ETIQUETA_REGEX = 7;

    private String descripcionLegiblePaso(
            PasoConversion paso) {

        StringBuilder descripcion = new StringBuilder(
                paso.getDescripcion() == null
                        ? ""
                        : paso.getDescripcion()
        );

        int numero = 0;

        for (NormalizadorAutomata.Arista arista
                : paso.getTransiciones()) {

            String expresion = arista.getSimbolo();

            if (expresion != null
                    && expresion.length() > LIMITE_ETIQUETA_REGEX) {

                numero++;

                descripcion.append("\n\nR")
                        .append(numero)
                        .append(" (")
                        .append(arista.getOrigen())
                        .append(" → ")
                        .append(arista.getDestino())
                        .append(") = ")
                        .append(expresion);
            }
        }

        return descripcion.toString();
    }
 
    
    
    private void estilizarBotonRegex(
            JButton boton, Color fondo, Color texto) {

        EstilosUI.estilizarBotonRegex(
                boton, fondo, texto
        );
    }

}
