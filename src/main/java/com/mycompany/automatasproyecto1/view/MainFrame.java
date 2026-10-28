package com.mycompany.automatasproyecto1.view;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

import com.mycompany.automatasproyecto1.controller.AutomataController;
import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;
import com.mycompany.automatasproyecto1.simulation.PasoSimulacion;
import com.mycompany.automatasproyecto1.simulation.ResultadoSimulacion;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.HashMap;
import java.util.Map;


public class MainFrame extends JFrame {

    // COLORES DEL DISEÑO


    private static final Color COLOR_FONDO =
            new Color(18, 27, 40);

    private static final Color COLOR_PANEL =
            new Color(28, 40, 56);

    private static final Color COLOR_PANEL_SECUNDARIO =
            new Color(35, 50, 68);

    private static final Color COLOR_LIENZO =
            new Color(23, 34, 50);

    private static final Color COLOR_BORDE =
            new Color(70, 92, 116);

    private static final Color COLOR_TEXTO =
            new Color(235, 242, 250);

    private static final Color COLOR_TEXTO_SECUNDARIO =
            new Color(170, 187, 204);

    private static final Color COLOR_VERDE =
            new Color(48, 210, 137);

    private static final Color COLOR_AZUL =
            new Color(48, 145, 220);

    private static final Color COLOR_ROJO =
            new Color(190, 76, 86);

    private static final Color COLOR_AMARILLO =
            new Color(218, 166, 76);

    private static final Color COLOR_ESTADO =
            new Color(48, 67, 88);

    private static final Color COLOR_ESTADO_SELECCION =
            new Color(57, 82, 107);

    // =============================================================
    // COMPONENTES
    // =============================================================

    private JPanel panelHerramientas;
    private JPanel panelLienzo;
    private JPanel panelSimulacion;

    private mxGraph graph;
    private mxGraphComponent graphComponent;
    // Última celda pulsada en el lienzo; ayuda a eliminar flechas visuales.
    private Object ultimaCeldaPulsada = null;

    private AutomataController controller;
    private Automata automata;

    private boolean modoAgregarEstado = false;
    private int contadorEstados = 0;

    private Map<Object, Estado> mapaEstados;
    private Map<Object, Transicion> mapaTransiciones;

    // Marcadores visuales para las flechas de estado inicial.
    private Map<Estado, Object> mapaMarcadoresIniciales;
    private Map<Estado, Object> mapaFlechasIniciales;

    private boolean modoCrearTransicion = false;

    private Estado estadoOrigenTransicion = null;

    private Object celdaOrigenTransicion = null;

    private Timer timerSimulacion;

    private String cadenaSimulacion = "";

    private int posicionSimulacion = 0;

    private Estado estadoActualSimulacion = null;

    private ResultadoSimulacion resultadoSimulacion;

    private List<PasoSimulacion> pasosSimulacion;

    private int indicePasoSimulacion = 0;

    private JTextField campoCadena;

    private JLabel etiquetaResultado;

    // =============================================================
    // CONSTRUCTOR
    // =============================================================

    public MainFrame() {

        controller = new AutomataController();

        automata = controller.getAutomata();

        mapaEstados = new HashMap<>();

        mapaTransiciones = new HashMap<>();

        mapaMarcadoresIniciales = new HashMap<>();
        mapaFlechasIniciales = new HashMap<>();

        configurarVentana();

        configurarPanelHerramientas();

        configurarLienzo();

        configurarPanelSimulacion();

        configurarEventos();
    }

    // =============================================================
    // CONFIGURAR VENTANA
    // =============================================================

    private void configurarVentana() {

        setTitle(
                "Editor de Autómatas y Expresiones Regulares"
        );

        setSize(
                1150,
                720
        );

        setMinimumSize(
                new Dimension(
                        950,
                        600
                )
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        getContentPane().setBackground(
                COLOR_FONDO
        );

        setLayout(
                new BorderLayout(
                        8,
                        8
                )
        );
    }

    // =============================================================
    // CONFIGURAR PANEL DE HERRAMIENTAS
    // =============================================================

    private void configurarPanelHerramientas() {

        panelHerramientas =
                new JPanel();

        panelHerramientas.setPreferredSize(
                new Dimension(
                        225,
                        0
                )
        );

        panelHerramientas.setBackground(
                COLOR_PANEL
        );

        panelHerramientas.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                10,
                                10,
                                10
                        )
                )
        );

        panelHerramientas.setLayout(
                new GridLayout(
                        7,
                        1,
                        8,
                        8
                )
        );

        JButton btnAgregarEstado =
                crearBoton(
                        "+   Agregar Estado",
                        COLOR_VERDE
                );

        JButton btnTransicion =
                crearBoton(
                        "→   Crear Transición",
                        COLOR_AZUL
                );

        JButton btnInicial =
                crearBoton(
                        "│   Estado Inicial",
                        COLOR_AZUL
                );

        JButton btnAceptacion =
                crearBoton(
                        "◎   Estado Aceptación",
                        COLOR_VERDE
                );

        JButton btnEliminar =
                crearBoton(
                        "×   Eliminar",
                        COLOR_ROJO
                );

        JButton btnMover =
                crearBoton(
                        "✋   Mover",
                        COLOR_AMARILLO
                );

        JButton btnEditarTransicion =
                crearBoton(
                        "✎   Editar Transición",
                        COLOR_AZUL
                );

        panelHerramientas.add(
                btnAgregarEstado
        );

        panelHerramientas.add(
                btnTransicion
        );

        panelHerramientas.add(
                btnInicial
        );

        panelHerramientas.add(
                btnAceptacion
        );

        panelHerramientas.add(
                btnEliminar
        );

        panelHerramientas.add(
                btnMover
        );

        panelHerramientas.add(
                btnEditarTransicion
        );

        add(
                panelHerramientas,
                BorderLayout.WEST
        );

        // Guardamos los botones mediante sus listeners.
        btnAgregarEstado.addActionListener(
                e -> activarModoAgregarEstado()
        );

        btnTransicion.addActionListener(
                e -> activarModoTransicion()
        );

        btnInicial.addActionListener(
                e -> establecerEstadoInicial()
        );

        btnAceptacion.addActionListener(
                e -> establecerEstadoAceptacion()
        );

        btnEliminar.addActionListener(
                e -> eliminarElementoSeleccionado()
        );

        btnMover.addActionListener(
                e -> activarModoMover()
        );

        btnEditarTransicion.addActionListener(
                e -> editarTransicionSeleccionada()
        );
    }

    // =============================================================
    // CREAR BOTÓN
    // =============================================================

    private JButton crearBoton(
            String texto,
            Color color) {

        JButton boton =
                new JButton(texto);

        boton.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        boton.setForeground(
                COLOR_TEXTO
        );

        boton.setBackground(
                new Color(
                        39,
                        53,
                        70
                )
        );

        boton.setFocusPainted(
                false
        );

        boton.setOpaque(
                true
        );

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                color,
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                10,
                                8,
                                10
                        )
                )
        );

        boton.setMargin(
                new Insets(
                        5,
                        5,
                        5,
                        5
                )
        );

        return boton;
    }

    // =============================================================
    // CONFIGURAR LIENZO
    // =============================================================

    private void configurarLienzo() {

        panelLienzo =
                new JPanel();

        panelLienzo.setBackground(
                COLOR_LIENZO
        );

        panelLienzo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                6,
                                6,
                                6,
                                6
                        )
                )
        );

        panelLienzo.setLayout(
                new BorderLayout()
        );

        graph =
                new mxGraph();

        graph.setGridEnabled(
                true
        );

        graph.setGridSize(
                20
        );

        graph.setCellsResizable(
                true
        );

        graph.setCellsMovable(
                true
        );

        graph.setCellsEditable(
                false
        );

        graph.setAllowDanglingEdges(
                false
        );

        graph.setEdgeLabelsMovable(
                false
        );

        graphComponent =
                new mxGraphComponent(
                        graph
                );

        // Cuadrícula visible, similar al diseño de referencia.
        graphComponent.setGridVisible(true);
        graphComponent.setGridStyle(mxGraphComponent.GRID_STYLE_LINE);
        graphComponent.setGridColor(new Color(48, 63, 80));

        graphComponent.setConnectable(
                false
        );

        graphComponent.setToolTips(
                true
        );

        graphComponent
                .getViewport()
                .setBackground(
                        COLOR_LIENZO
                );

        panelLienzo.add(
                graphComponent,
                BorderLayout.CENTER
        );

        add(
                panelLienzo,
                BorderLayout.CENTER
        );
    }

    // =============================================================
    // CONFIGURAR PANEL DE SIMULACIÓN
    // =============================================================

    private void configurarPanelSimulacion() {

        panelSimulacion =
                new JPanel();

        panelSimulacion.setBackground(
                COLOR_PANEL
        );

        panelSimulacion.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                12,
                                8,
                                12
                        )
                )
        );

        panelSimulacion.setLayout(
                new BorderLayout(
                        10,
                        5
                )
        );

        // ---------------------------------------------------------
        // PARTE IZQUIERDA
        // ---------------------------------------------------------

        JPanel panelEntrada =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                4
                        )
                );

        panelEntrada.setOpaque(
                false
        );

        JLabel etiquetaCadena =
                new JLabel(
                        "Cadena de Prueba:"
                );

        etiquetaCadena.setForeground(
                COLOR_TEXTO
        );

        etiquetaCadena.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        campoCadena =
                new JTextField(
                        22
                );

        campoCadena.setPreferredSize(
                new Dimension(
                        240,
                        35
                )
        );

        campoCadena.setBackground(
                new Color(
                        17,
                        27,
                        41
                )
        );

        campoCadena.setForeground(
                COLOR_TEXTO
        );

        campoCadena.setCaretColor(
                COLOR_TEXTO
        );

        campoCadena.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                5,
                                8,
                                5,
                                8
                        )
                )
        );

        JButton btnSimular =
                crearBoton(
                        "▶   Simular",
                        COLOR_AZUL
                );

        panelEntrada.add(
                etiquetaCadena
        );

        panelEntrada.add(
                campoCadena
        );

        panelEntrada.add(
                btnSimular
        );

        // ---------------------------------------------------------
        // RESULTADO
        // ---------------------------------------------------------

        JPanel panelResultado =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                4
                        )
                );

        panelResultado.setOpaque(
                false
        );

        JLabel etiquetaTituloResultado =
                new JLabel(
                        "Resultado:"
                );

        etiquetaTituloResultado.setForeground(
                COLOR_TEXTO
        );

        etiquetaTituloResultado.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        etiquetaResultado =
                new JLabel(
                        "[ Pendiente ]"
                );

        etiquetaResultado.setForeground(
                COLOR_TEXTO_SECUNDARIO
        );

        etiquetaResultado.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        panelResultado.add(
                etiquetaTituloResultado
        );

        panelResultado.add(
                etiquetaResultado
        );

        panelSimulacion.add(
                panelEntrada,
                BorderLayout.WEST
        );

        panelSimulacion.add(
                panelResultado,
                BorderLayout.EAST
        );

        add(
                panelSimulacion,
                BorderLayout.SOUTH
        );

        btnSimular.addActionListener(
                e -> iniciarSimulacion()
        );
    }

    // =============================================================
    // CONFIGURAR EVENTOS DEL LIENZO
    // =============================================================

    private void configurarEventos() {

        graphComponent
                .getGraphControl()
                .addMouseListener(
                        new MouseAdapter() {

                    @Override
                    public void mousePressed(
                            MouseEvent e) {

                        // Guardar la celda bajo el cursor antes de que otros
                        // eventos refresquen el grafo o cambien la selección.
                        ultimaCeldaPulsada = graphComponent.getCellAt(e.getX(), e.getY());

                        if (
                                modoAgregarEstado
                        ) {

                            crearEstadoEnPosicion(
                                    e.getX(),
                                    e.getY()
                            );

                            return;
                        }

                        if (
                                modoCrearTransicion
                        ) {

                            procesarClickTransicion(
                                    e
                            );
                        }
                    }

                    @Override
                    public void mouseReleased(
                            MouseEvent e) {

                        sincronizarPosicionEstado();
                    }
                }
        );
    }

    // =============================================================
    // MODO AGREGAR ESTADO
    // =============================================================

    private void activarModoAgregarEstado() {

        modoAgregarEstado =
                true;

        modoCrearTransicion =
                false;

        estadoOrigenTransicion =
                null;

        celdaOrigenTransicion =
                null;

        graphComponent.setCursor(
                new Cursor(
                        Cursor.CROSSHAIR_CURSOR
                )
        );
    }

    // =============================================================
    // MODO TRANSICIÓN
    // =============================================================

    private void activarModoTransicion() {

        modoAgregarEstado =
                false;

        modoCrearTransicion =
                true;

        estadoOrigenTransicion =
                null;

        celdaOrigenTransicion =
                null;

        graphComponent.setCursor(
                new Cursor(
                        Cursor.DEFAULT_CURSOR
                )
        );

        JOptionPane.showMessageDialog(
                this,
                "Selecciona primero el estado origen\n"
                + "y después el estado destino."
        );
    }

    // =============================================================
    // MODO MOVER
    // =============================================================

    private void activarModoMover() {

        modoAgregarEstado =
                false;

        modoCrearTransicion =
                false;

        estadoOrigenTransicion =
                null;

        celdaOrigenTransicion =
                null;

        graphComponent.setCursor(
                new Cursor(
                        Cursor.DEFAULT_CURSOR
                )
        );
    }

    // =============================================================
    // CREAR ESTADO
    // =============================================================

    private void crearEstadoEnPosicion(
            int x,
            int y) {

        String nombre =
                "q" + contadorEstados;

        Estado estado =
                controller.crearEstado(
                        nombre,
                        x,
                        y
                );

        Object parent =
                graph.getDefaultParent();

        graph.getModel().beginUpdate();

        try {

            Object celda =
                    graph.insertVertex(
                            parent,
                            null,
                            estado.getNombre(),
                            x - 35,
                            y - 35,
                            70,
                            70
                    );

            // -----------------------------------------------------
            // ESTILO DEL ESTADO
            // -----------------------------------------------------

            graph.setCellStyles(
                    mxConstants.STYLE_SHAPE,
                    mxConstants.SHAPE_ELLIPSE,
                    new Object[]{
                        celda
                    }
            );

            graph.setCellStyles(
                    mxConstants.STYLE_FILLCOLOR,
                    "#304358",
                    new Object[]{
                        celda
                    }
            );

            graph.setCellStyles(
                    mxConstants.STYLE_STROKECOLOR,
                    "#7895B2",
                    new Object[]{
                        celda
                    }
            );

            graph.setCellStyles(
                    mxConstants.STYLE_STROKEWIDTH,
                    "2",
                    new Object[]{
                        celda
                    }
            );

            graph.setCellStyles(
                    mxConstants.STYLE_FONTCOLOR,
                    "#FFFFFF",
                    new Object[]{
                        celda
                    }
            );

            graph.setCellStyles(
                    mxConstants.STYLE_FONTSIZE,
                    "15",
                    new Object[]{
                        celda
                    }
            );

            graph.setCellStyles(
                    mxConstants.STYLE_FONTSTYLE,
                    "0",
                    new Object[]{
                        celda
                    }
            );

            mapaEstados.put(
                    celda,
                    estado
            );

        } finally {

            graph.getModel().endUpdate();
        }

        contadorEstados++;

        modoAgregarEstado =
                false;

        graphComponent.setCursor(
                new Cursor(
                        Cursor.DEFAULT_CURSOR
                )
        );
    }

    // =============================================================
    // PROCESAR TRANSICIÓN
    // =============================================================

    private void procesarClickTransicion(
            MouseEvent e) {

        Object celda =
                graphComponent.getCellAt(
                        e.getX(),
                        e.getY()
                );

        if (celda == null) {
            return;
        }

        Estado estado =
                mapaEstados.get(
                        celda
                );

        if (estado == null) {
            return;
        }

        // ---------------------------------------------------------
        // PRIMER CLICK: ORIGEN
        // ---------------------------------------------------------

        if (
                estadoOrigenTransicion
                == null
        ) {

            estadoOrigenTransicion =
                    estado;

            celdaOrigenTransicion =
                    celda;

            JOptionPane.showMessageDialog(
                    this,
                    "Origen seleccionado: "
                    + estado.getNombre()
                    + "\n\n"
                    + "Selecciona ahora el destino."
            );

            return;
        }

        // ---------------------------------------------------------
        // SEGUNDO CLICK: DESTINO
        // ---------------------------------------------------------

        Estado estadoDestino =
                estado;

        Object celdaDestino =
                celda;

        String simbolo =
                JOptionPane.showInputDialog(
                        this,
                        "Escribe el símbolo de la transición:",
                        "Crear Transición",
                        JOptionPane.PLAIN_MESSAGE
                );

        if (simbolo == null) {

            limpiarModoTransicion();

            return;
        }

        simbolo =
                simbolo.trim();

        if (simbolo.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "El símbolo no puede estar vacío."
            );

            return;
        }

        // ---------------------------------------------------------
        // CREAR EN EL MODELO
        // ---------------------------------------------------------

        Transicion transicion;
        try {
            transicion = controller.crearTransicion(
                    estadoOrigenTransicion,
                    estadoDestino,
                    simbolo
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "No se pudo crear la transición",
                    JOptionPane.WARNING_MESSAGE
            );
            limpiarModoTransicion();
            return;
        }

        // ---------------------------------------------------------
        // CREAR VISUALMENTE
        // ---------------------------------------------------------

        graph.getModel().beginUpdate();

        try {

            Object celdaTransicion =
                    graph.insertEdge(
                            graph.getDefaultParent(),
                            null,
                            simbolo,
                            celdaOrigenTransicion,
                            celdaDestino
                    );

            boolean esBucle =
                    celdaOrigenTransicion == celdaDestino;

            // -----------------------------------------------------
            // GEOMETRÍA DE LA TRANSICIÓN
            // -----------------------------------------------------

            if (esBucle) {

                // JGraphX usa mxEdgeStyle.Loop y STYLE_DIRECTION
                // para decidir en qué lado aparece el bucle.
                // NORTH lo coloca arriba del estado.
                graph.setCellStyles(
                        mxConstants.STYLE_EDGE,
                        mxConstants.EDGESTYLE_LOOP,
                        new Object[]{celdaTransicion}
                );

                graph.setCellStyles(
                        mxConstants.STYLE_DIRECTION,
                        mxConstants.DIRECTION_NORTH,
                        new Object[]{celdaTransicion}
                );

                graph.setCellStyles(
                        mxConstants.STYLE_SEGMENT,
                        "28",
                        new Object[]{celdaTransicion}
                );

                graph.setCellStyles(
                        mxConstants.STYLE_ROUNDED,
                        "1",
                        new Object[]{celdaTransicion}
                );

            } else {

                graph.setCellStyles(
                        mxConstants.STYLE_EDGE,
                        mxConstants.EDGESTYLE_ORTHOGONAL,
                        new Object[]{celdaTransicion}
                );
            }

            // -----------------------------------------------------
            // ESTILO DE LA FLECHA
            // -----------------------------------------------------

            graph.setCellStyles(
                    mxConstants.STYLE_STROKECOLOR,
                    "#8BA5BF",
                    new Object[]{
                        celdaTransicion
                    }
            );

            graph.setCellStyles(
                    mxConstants.STYLE_FONTCOLOR,
                    "#FFFFFF",
                    new Object[]{
                        celdaTransicion
                    }
            );

            graph.setCellStyles(
                    mxConstants.STYLE_FONTSIZE,
                    "14",
                    new Object[]{
                        celdaTransicion
                    }
            );

            graph.setCellStyles(
                    mxConstants.STYLE_STROKEWIDTH,
                    "2",
                    new Object[]{
                        celdaTransicion
                    }
            );

            // -----------------------------------------------------
            // FONDO BLANCO DE LA ETIQUETA
            // -----------------------------------------------------

            graph.setCellStyles(
                    mxConstants
                            .STYLE_LABEL_BACKGROUNDCOLOR,
                    "#172231",
                    new Object[]{
                        celdaTransicion
                    }
            );

            // -----------------------------------------------------
            // MOVER LA ETIQUETA HACIA ARRIBA
            // -----------------------------------------------------

            mxGeometry geometriaTransicion =
                    graph.getModel()
                            .getGeometry(
                                    celdaTransicion
                            );

            if (
                    geometriaTransicion
                    != null
            ) {

                geometriaTransicion.setOffset(
                        new mxPoint(
                                0,
                                esBucle ? -8 : -15
                        )
                );
            }

            mapaTransiciones.put(
                    celdaTransicion,
                    transicion
            );

        } finally {

            graph.getModel().endUpdate();
        }

        limpiarModoTransicion();
    }

    // =============================================================
    // ESTADO INICIAL

private void establecerEstadoInicial() {

    Object celda = graph.getSelectionCell();

    if (celda == null) {
        JOptionPane.showMessageDialog(
                this,
                "Primero selecciona un estado."
        );
        return;
    }

    Estado estado = mapaEstados.get(celda);

    if (estado == null) {
        JOptionPane.showMessageDialog(
                this,
                "La selección no es un estado."
        );
        return;
    }

    // Si ya es el inicial, quitamos esa designación.
    if (automata.getEstadoInicial() == estado) {
        controller.establecerEstadoInicial(null);
    } else {
        // Si no lo es, lo convertimos en inicial.
        controller.establecerEstadoInicial(estado);
    }

    // Actualizar la representación visual.
    actualizarEtiquetasEstados();
    actualizarEstiloEstados();
    graph.refresh();
}



    // =============================================================
    // ESTADO DE ACEPTACIÓN
    // =============================================================

    private void establecerEstadoAceptacion() {

        Object celda =
                graph.getSelectionCell();

        if (celda == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero selecciona un estado."
            );

            return;
        }

        Estado estado =
                mapaEstados.get(
                        celda
                );

        if (estado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "La selección no es un estado."
            );

            return;
        }

        if (
                estado.isAceptacion()
        ) {

            automata.eliminarEstadoAceptacion(
                    estado
            );

        } else {

            controller.establecerEstadoAceptacion(
                    estado
            );
        }

        actualizarEtiquetasEstados();

        actualizarEstiloEstados();
    }

    // =============================================================
    // ACTUALIZAR ETIQUETAS DE ESTADOS
    // =============================================================

    private void actualizarEtiquetasEstados() {

        graph.getModel().beginUpdate();

        try {

            for (
                    Map.Entry<Object, Estado> entrada
                    : mapaEstados.entrySet()
            ) {

                Object celda =
                        entrada.getKey();

                Estado estado =
                        entrada.getValue();

                // El nombre del estado se mantiene limpio.
                // La flecha inicial y el doble círculo son visuales.
                graph.getModel().setValue(
                        celda,
                        estado.getNombre()
                );
            }

        } finally {

            graph.getModel().endUpdate();
        }
    }

    // =============================================================
    // ACTUALIZAR ESTILO DE ESTADOS
    // =============================================================

    private void actualizarEstiloEstados() {

        for (
                Map.Entry<Object, Estado> entrada
                : mapaEstados.entrySet()
        ) {

            Object celda = entrada.getKey();
            Estado estado = entrada.getValue();

            // -----------------------------------------------------
            // FORMA DEL ESTADO
            // -----------------------------------------------------
            // Los estados de aceptación usan el doble círculo real
            // de JGraphX, en lugar de simularlo con un borde grueso.
            graph.setCellStyles(
                    mxConstants.STYLE_SHAPE,
                    estado.isAceptacion()
                            ? mxConstants.SHAPE_DOUBLE_ELLIPSE
                            : mxConstants.SHAPE_ELLIPSE,
                    new Object[]{celda}
            );

            graph.setCellStyles(
                    mxConstants.STYLE_FILLCOLOR,
                    estado.isAceptacion() ? "#284B4B" : "#304358",
                    new Object[]{celda}
            );

            graph.setCellStyles(
                    mxConstants.STYLE_STROKECOLOR,
                    estado.isAceptacion() ? "#48D99A" : "#7895B2",
                    new Object[]{celda}
            );

            graph.setCellStyles(
                    mxConstants.STYLE_STROKEWIDTH,
                    estado.isAceptacion() ? "2" : "2",
                    new Object[]{celda}
            );

            graph.setCellStyles(
                    mxConstants.STYLE_FONTCOLOR,
                    "#FFFFFF",
                    new Object[]{celda}
            );

            graph.setCellStyles(
                    mxConstants.STYLE_FONTSIZE,
                    "15",
                    new Object[]{celda}
            );

            graph.setCellStyles(
                    mxConstants.STYLE_ALIGN,
                    mxConstants.ALIGN_CENTER,
                    new Object[]{celda}
            );

            graph.setCellStyles(
                    mxConstants.STYLE_VERTICAL_ALIGN,
                    mxConstants.ALIGN_MIDDLE,
                    new Object[]{celda}
            );
        }

        actualizarMarcadorInicial();
    }

    // =============================================================
    // MARCADOR VISUAL DEL ESTADO INICIAL
    // =============================================================

    private void actualizarMarcadorInicial() {

        graph.getModel().beginUpdate();
        try {
            // Eliminar las flechas y los vértices auxiliares anteriores.
            java.util.List<Object> eliminar = new java.util.ArrayList<>();
            eliminar.addAll(mapaFlechasIniciales.values());
            eliminar.addAll(mapaMarcadoresIniciales.values());

            if (!eliminar.isEmpty()) {
                graph.removeCells(eliminar.toArray());
            }

            mapaFlechasIniciales.clear();
            mapaMarcadoresIniciales.clear();

            Estado inicial = automata.getEstadoInicial();
            if (inicial == null) {
                return;
            }

            Object celdaEstado = obtenerCeldaEstado(inicial);
            if (celdaEstado == null) {
                return;
            }

            mxGeometry geo = graph.getModel().getGeometry(celdaEstado);
            if (geo == null) {
                return;
            }

            Object parent = graph.getDefaultParent();
            Object marcador = graph.insertVertex(
                    parent, null, "",
                    geo.getX() - 42, geo.getY() + 34, 2, 2
            );

            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR,
                    mxConstants.NONE, new Object[]{marcador});
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR,
                    mxConstants.NONE, new Object[]{marcador});
            graph.setCellStyles(mxConstants.STYLE_OPACITY,
                    "0", new Object[]{marcador});
            graph.setCellStyles(mxConstants.STYLE_MOVABLE,
                    "0", new Object[]{marcador});
            graph.setCellStyles(mxConstants.STYLE_RESIZABLE,
                    "0", new Object[]{marcador});
            graph.setCellStyles(mxConstants.STYLE_DELETABLE,
                    "0", new Object[]{marcador});

            Object flecha = graph.insertEdge(
                    parent, null, "", marcador, celdaEstado
            );
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR,
                    "#9BB4CC", new Object[]{flecha});
            // Línea más gruesa para facilitar que el usuario pueda pulsarla.
            graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH,
                    "4", new Object[]{flecha});
            graph.setCellStyles(mxConstants.STYLE_ENDARROW,
                    mxConstants.ARROW_CLASSIC, new Object[]{flecha});
            graph.setCellStyles(mxConstants.STYLE_STARTARROW,
                    mxConstants.NONE, new Object[]{flecha});
            // La flecha se elimina mediante nuestro botón Eliminar.
            // Se deja habilitada como celda seleccionable/eliminable en JGraphX.
            graph.setCellStyles(mxConstants.STYLE_DELETABLE,
                    "1", new Object[]{flecha});
            graph.setCellStyles(mxConstants.STYLE_MOVABLE,
                    "0", new Object[]{flecha});

            mapaMarcadoresIniciales.put(inicial, marcador);
            mapaFlechasIniciales.put(inicial, flecha);
        } finally {
            graph.getModel().endUpdate();
        }

        graph.refresh();
    }

    private Object obtenerCeldaEstado(Estado estado) {

        for (Map.Entry<Object, Estado> entrada : mapaEstados.entrySet()) {
            if (entrada.getValue() == estado) {
                return entrada.getKey();
            }
        }

        return null;
    }

    // =============================================================
    // EDITAR TRANSICIÓN SELECCIONADA
    // =============================================================

    private void editarTransicionSeleccionada() {

        Object celda = graph.getSelectionCell();

        if (celda == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero selecciona una transición para editarla."
            );
            return;
        }

        Transicion transicion = mapaTransiciones.get(celda);

        if (transicion == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "La selección no es una transición del autómata."
            );
            return;
        }

        String nuevoSimbolo = JOptionPane.showInputDialog(
                this,
                "Escribe el nuevo símbolo de la transición:",
                transicion.getSimbolo()
        );

        if (nuevoSimbolo == null) {
            return;
        }

        try {
            controller.editarTransicion(transicion, nuevoSimbolo);

            graph.getModel().beginUpdate();
            try {
                graph.getModel().setValue(celda, transicion.getSimbolo());
            } finally {
                graph.getModel().endUpdate();
            }

            graph.refresh();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "No se pudo editar la transición",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // =============================================================
    // ELIMINAR ELEMENTO
    // =============================================================

    private void eliminarElementoSeleccionado() {

        Object celda = graph.getSelectionCell();

        // Si el refresco visual dejó vacía la selección, usamos la última
        // celda pulsada en el lienzo.
        if (celda == null) {
            celda = ultimaCeldaPulsada;
        }

        if (celda == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero haz clic en un estado, una transición o la flecha inicial y después pulsa Eliminar."
            );
            return;
        }

        // ---------------------------------------------------------
        // ELIMINAR LA FLECHA DEL ESTADO INICIAL SI SE SELECCIONÓ
        // DIRECTAMENTE LA FLECHA. No se elimina el estado.
        // ---------------------------------------------------------
        Estado estadoDeFlecha = null;
        for (Map.Entry<Estado, Object> entrada : mapaFlechasIniciales.entrySet()) {
            if (entrada.getValue() == celda) {
                estadoDeFlecha = entrada.getKey();
                break;
            }
        }

        if (estadoDeFlecha == null) {
            for (Map.Entry<Estado, Object> entrada : mapaMarcadoresIniciales.entrySet()) {
                if (entrada.getValue() == celda) {
                    estadoDeFlecha = entrada.getKey();
                    break;
                }
            }
        }

        if (estadoDeFlecha != null) {
            controller.establecerEstadoInicial(null);
            actualizarMarcadorInicial();
            graph.clearSelection();
            ultimaCeldaPulsada = null;
            graph.refresh();
            JOptionPane.showMessageDialog(
                    this,
                    "Se eliminó la flecha del estado inicial. El estado se conservó."
            );
            return;
        }

        // ---------------------------------------------------------
        // ELIMINAR ESTADO
        // ---------------------------------------------------------

        Estado estado = mapaEstados.get(celda);

        if (estado != null) {

            int respuesta =
                    JOptionPane.showConfirmDialog(
                            this,
                            "¿Eliminar el estado "
                            + estado.getNombre()
                            + "?",
                            "Eliminar estado",
                            JOptionPane.YES_NO_OPTION
                    );

            if (
                    respuesta
                    != JOptionPane.YES_OPTION
            ) {

                return;
            }

            // -----------------------------------------------------
            // Eliminar visualmente las transiciones relacionadas
            // -----------------------------------------------------

            for (
                    Map.Entry<Object, Transicion> entrada
                    : new HashMap<>(
                            mapaTransiciones
                    ).entrySet()
            ) {

                Transicion transicion =
                        entrada.getValue();

                if (
                        transicion.getOrigen()
                                == estado
                        ||
                        transicion.getDestino()
                                == estado
                ) {

                    Object celdaTransicion =
                            entrada.getKey();

                    mapaTransiciones.remove(
                            celdaTransicion
                    );

                    graph.removeCells(
                            new Object[]{
                                celdaTransicion
                            }
                    );
                }
            }

            // -----------------------------------------------------
            // Eliminar marcador visual de estado inicial
            // -----------------------------------------------------

            Object marcadorInicial = mapaMarcadoresIniciales.remove(estado);
            Object flechaInicial = mapaFlechasIniciales.remove(estado);

            if (flechaInicial != null) {
                graph.removeCells(new Object[]{flechaInicial});
            }

            if (marcadorInicial != null) {
                graph.removeCells(new Object[]{marcadorInicial});
            }

            // -----------------------------------------------------
            // Eliminar del modelo
            // -----------------------------------------------------

            controller.eliminarEstado(
                    estado
            );

            mapaEstados.remove(
                    celda
            );

            graph.removeCells(
                    new Object[]{
                        celda
                    }
            );

            actualizarEtiquetasEstados();

            actualizarEstiloEstados();
            ultimaCeldaPulsada = null;
            graph.clearSelection();

            return;
        }

        // ---------------------------------------------------------
        // ELIMINAR TRANSICIÓN
        // ---------------------------------------------------------

        Transicion transicion =
                mapaTransiciones.get(
                        celda
                );

        if (transicion != null) {

            controller.eliminarTransicion(
                    transicion
            );

            mapaTransiciones.remove(
                    celda
            );

            graph.removeCells(
                    new Object[]{
                        celda
                    }
            );
            ultimaCeldaPulsada = null;
            graph.clearSelection();

            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el elemento."
        );
    }

    // =============================================================
    // SINCRONIZAR POSICIÓN
    // =============================================================

    private void sincronizarPosicionEstado() {

        Object celda =
                graph.getSelectionCell();

        if (celda == null) {
            return;
        }

        Estado estado =
                mapaEstados.get(
                        celda
                );

        if (estado == null) {
            return;
        }

        mxGeometry geometria =
                graph.getModel()
                        .getGeometry(
                                celda
                        );

        if (geometria == null) {
            return;
        }

        estado.setX(
                (int) geometria.getX()
        );

        estado.setY(
                (int) geometria.getY()
        );

        actualizarMarcadorInicial();
    }

    // =============================================================
    // INICIAR SIMULACIÓN
    // =============================================================

    private void iniciarSimulacion() {

        if (timerSimulacion != null && timerSimulacion.isRunning()) {
            timerSimulacion.stop();
        }

        String cadena = campoCadena.getText();
        if (cadena == null) {
            cadena = "";
        }

        // El controlador y SimuladorAFD calculan el resultado real.
        // MainFrame solamente presenta y anima los pasos.
        try {
            resultadoSimulacion = controller.simularCadena(cadena);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo ejecutar la simulación: " + ex.getMessage(),
                    "Error de simulación",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        pasosSimulacion = resultadoSimulacion.getPasos();
        indicePasoSimulacion = 0;
        cadenaSimulacion = cadena;
        posicionSimulacion = 0;
        estadoActualSimulacion = automata.getEstadoInicial();

        if (automata.getEstadoInicial() == null) {
            etiquetaResultado.setText("[ SIN ESTADO INICIAL ]");
            etiquetaResultado.setForeground(COLOR_ROJO);
            JOptionPane.showMessageDialog(
                    this,
                    resultadoSimulacion.getMensaje(),
                    "No se puede simular",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        etiquetaResultado.setText("[ SIMULANDO... ]");
        etiquetaResultado.setForeground(COLOR_AZUL);
        actualizarEstiloSimulacion();

        // El paso cero representa el estado inicial; lo mostramos primero.
        if (pasosSimulacion == null || pasosSimulacion.isEmpty()) {
            finalizarSimulacionDesdeResultado();
            return;
        }

        indicePasoSimulacion = 1;

        if (pasosSimulacion.size() == 1) {
            finalizarSimulacionDesdeResultado();
            return;
        }

        timerSimulacion = new Timer(800, e -> ejecutarPasoSimulacion());
        timerSimulacion.start();
    }

    // =============================================================
    // EJECUTAR PASO DE SIMULACIÓN
    // =============================================================

    private void ejecutarPasoSimulacion() {

        if (pasosSimulacion == null || indicePasoSimulacion >= pasosSimulacion.size()) {
            finalizarSimulacionDesdeResultado();
            return;
        }

        PasoSimulacion paso = pasosSimulacion.get(indicePasoSimulacion);
        indicePasoSimulacion++;

        if (paso.getEstadoDestino() != null) {
            estadoActualSimulacion = paso.getEstadoDestino();
            posicionSimulacion++;
        }

        actualizarEstiloSimulacion();

        if (paso.tieneError()) {
            if (timerSimulacion != null) {
                timerSimulacion.stop();
            }
            etiquetaResultado.setText("[ RECHAZADA ]");
            etiquetaResultado.setForeground(COLOR_ROJO);
            etiquetaResultado.setToolTipText(paso.getMensaje());
            JOptionPane.showMessageDialog(
                    this,
                    paso.getMensaje(),
                    "Simulación detenida",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        if (indicePasoSimulacion >= pasosSimulacion.size()) {
            finalizarSimulacionDesdeResultado();
        }
    }

    // =============================================================
    // FINALIZAR SEGÚN EL RESULTADO DEL SIMULADOR
    // =============================================================

    private void finalizarSimulacionDesdeResultado() {

        if (timerSimulacion != null && timerSimulacion.isRunning()) {
            timerSimulacion.stop();
        }

        if (resultadoSimulacion == null) {
            return;
        }

        estadoActualSimulacion = resultadoSimulacion.getEstadoFinal();

        if (resultadoSimulacion.isAceptada()) {
            etiquetaResultado.setText("[ ACEPTADA ]");
            etiquetaResultado.setForeground(COLOR_VERDE);
        } else {
            etiquetaResultado.setText("[ RECHAZADA ]");
            etiquetaResultado.setForeground(COLOR_ROJO);
        }

        etiquetaResultado.setToolTipText(resultadoSimulacion.getMensaje());
        actualizarEstiloSimulacion();

        // Mostrar el recorrido y el motivo final para facilitar las pruebas.
        StringBuilder detalle = new StringBuilder();
        detalle.append(resultadoSimulacion.getMensaje()).append("\n\nRecorrido:");
        for (PasoSimulacion paso : resultadoSimulacion.getPasos()) {
            detalle.append("\n").append(paso.toString());
        }

        JOptionPane.showMessageDialog(
                this,
                detalle.toString(),
                "Resultado de la simulación",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =============================================================
    // RESALTAR ESTADO ACTUAL
    // =============================================================

    private void actualizarEstiloSimulacion() {

        for (
                Map.Entry<Object, Estado> entrada
                : mapaEstados.entrySet()
        ) {

            Object celda =
                    entrada.getKey();

            Estado estado =
                    entrada.getValue();

            graph.setCellStyles(
                    mxConstants.STYLE_SHAPE,
                    estado.isAceptacion()
                            ? mxConstants.SHAPE_DOUBLE_ELLIPSE
                            : mxConstants.SHAPE_ELLIPSE,
                    new Object[]{celda}
            );

            // -----------------------------------------------------
            // ESTADO ACTUAL
            // -----------------------------------------------------

            if (
                    estado
                    == estadoActualSimulacion
            ) {

                graph.setCellStyles(
                        mxConstants.STYLE_FILLCOLOR,
                        "#356A70",
                        new Object[]{
                            celda
                        }
                );

                graph.setCellStyles(
                        mxConstants.STYLE_STROKECOLOR,
                        "#48D99A",
                        new Object[]{
                            celda
                        }
                );

                graph.setCellStyles(
                        mxConstants.STYLE_STROKEWIDTH,
                        "3",
                        new Object[]{
                            celda
                        }
                );

            } else if (
                    estado.isAceptacion()
            ) {

                // -------------------------------------------------
                // ESTADO DE ACEPTACIÓN
                // -------------------------------------------------

                graph.setCellStyles(
                        mxConstants.STYLE_FILLCOLOR,
                        "#284B4B",
                        new Object[]{
                            celda
                        }
                );

                graph.setCellStyles(
                        mxConstants.STYLE_STROKECOLOR,
                        "#48D99A",
                        new Object[]{
                            celda
                        }
                );

                graph.setCellStyles(
                        mxConstants.STYLE_STROKEWIDTH,
                        "2",
                        new Object[]{
                            celda
                        }
                );

            } else {

                // -------------------------------------------------
                // ESTADO NORMAL
                // -------------------------------------------------

                graph.setCellStyles(
                        mxConstants.STYLE_FILLCOLOR,
                        "#304358",
                        new Object[]{
                            celda
                        }
                );

                graph.setCellStyles(
                        mxConstants.STYLE_STROKECOLOR,
                        "#7895B2",
                        new Object[]{
                            celda
                        }
                );

                graph.setCellStyles(
                        mxConstants.STYLE_STROKEWIDTH,
                        "2",
                        new Object[]{
                            celda
                        }
                );
            }
        }
    }

    // =============================================================
    // LIMPIAR MODO TRANSICIÓN
    // =============================================================

    private void limpiarModoTransicion() {

        modoCrearTransicion =
                false;

        estadoOrigenTransicion =
                null;

        celdaOrigenTransicion =
                null;

        graphComponent.setCursor(
                new Cursor(
                        Cursor.DEFAULT_CURSOR
                )
        );
    }
}