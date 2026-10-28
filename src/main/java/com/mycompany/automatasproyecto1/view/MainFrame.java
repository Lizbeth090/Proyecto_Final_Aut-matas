package com.mycompany.automatasproyecto1.view;

import com.mycompany.automatasproyecto1.io.RestauradorModeloAFD;
import com.mycompany.automatasproyecto1.conversion.ConversorRegex;
import com.mycompany.automatasproyecto1.conversion.ResultadoConversion;
import com.mycompany.automatasproyecto1.conversion.VerificadorEquivalencia;
import com.mycompany.automatasproyecto1.simulation.SimuladorAFD;
import com.mycompany.automatasproyecto1.simulation.ResultadoSimulacion;
import com.mycompany.automatasproyecto1.io.AutomataJson;
import com.mycompany.automatasproyecto1.view.components.EstilosUI;
import com.mycompany.automatasproyecto1.view.components.PanelMensajes;
import com.mycompany.automatasproyecto1.view.editor.EditorVisualAFD;
import com.mycompany.automatasproyecto1.view.simulation.AnimadorSimulacion;
import com.mycompany.automatasproyecto1.view.graph.CurvaAFD;
import com.mycompany.automatasproyecto1.view.graph.GeometriaTransicionesAFD;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxPerimeter;

import com.mycompany.automatasproyecto1.controller.AutomataController;
import com.mycompany.automatasproyecto1.model.Automata;
import com.mycompany.automatasproyecto1.model.Estado;
import com.mycompany.automatasproyecto1.model.Transicion;

import javax.swing.BorderFactory;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

/**
 * Ventana principal del editor de autómatas.
 */
public class MainFrame extends JFrame {
    
    // CONFIGURACIÓN VISUAL DE LOS ESTADOS
    private static final int TAMANO_ESTADO = 80;

    private static final String ESTILO_ESTADO =
            "shape=ellipse;"
            + "fillColor=#FFFFFF;"
            + "strokeColor=#000000;"
            + "strokeWidth=2;"
            + "fontSize=16;"
            + "align=center;"
            + "verticalAlign=middle;";

    private JPanel panelHerramientas;
    private JPanel panelLienzo;
    private JPanel panelSimulacion;

    private mxGraph graph;
    private mxGraphComponent graphComponent;

    private AutomataController controller;
    private Automata automata;

    private boolean modoAgregarEstado = false;
    
    
    private Object marcadorEstadoInicial = null;

    private Map<Object, Estado> mapaEstados;
    private Map<Object, Transicion> mapaTransiciones;

    private boolean modoCrearTransicion = false;
    private Estado estadoOrigenTransicion = null;
    private Object celdaOrigenTransicion = null;

    private JTextField campoCadena;
    private JLabel etiquetaResultado;
    
    private final PanelMensajes scrollMensajes =
        new PanelMensajes();
    
    private AnimadorSimulacion animadorSimulacion;
    
    private EditorVisualAFD editorVisualAFD;

    
    /**
    * Inicializa el grafo y configura su comportamiento visual.
    */
    private void configurarGrafo() {

        graph = new mxGraph();
        
        // Registrar curva corregida para el editor AFD.
        mxGraphics2DCanvas.putShape(
                "curvaAFD",
                new CurvaAFD()
        );
        
        // Conectar las transiciones al borde real
        // de los estados circulares.
        graph.getStylesheet()
                .getDefaultVertexStyle()
                .put(
                        mxConstants.STYLE_PERIMETER,
                        mxPerimeter.EllipsePerimeter
                );
        
        // ESTILO GENERAL DE LAS TRANSICIONES
        graph.getStylesheet().getDefaultEdgeStyle().put(
                mxConstants.STYLE_ENDARROW,
                mxConstants.ARROW_BLOCK
        );

        graph.getStylesheet().getDefaultEdgeStyle().put(
                mxConstants.STYLE_ENDSIZE,
                12
        );

        graph.getStylesheet().getDefaultEdgeStyle().put(
                mxConstants.STYLE_STROKEWIDTH,
                2
        );

        // FLECHA INICIAL FUERA DEL CIRCULO
        
        graph.setConstrainChildren(false);
        
        graph.setCellsEditable(false);
        
        // Evitar que las transiciones se desconecten
        
        graph.setCellsDisconnectable(false);
        
        graph.setAllowDanglingEdges(false);
        
        graph.setDisconnectOnMove(false);

        // Evitar deformarlas arrastrando sus puntos
        
        graph.setCellsBendable(false);
        
        // Impedir cambiar el tamaño de los estados
        
        graph.setCellsResizable(false);

        // Permitir mover los estados
        
        graph.setCellsMovable(true);

        graph.addListener(mxEvent.CELLS_MOVED, (sender, evt) -> {
            Object[] movidas = (Object[]) evt.getProperty("cells");
            if (movidas == null) return;

            for (Object celda : movidas) {
                if (mapaEstados.containsKey(celda)) {
                    javax.swing.SwingUtilities.invokeLater(
                            this::actualizarCurvasTransiciones);
                    break;
                }
            }
        });

    }
    
    /**
     * Constructor de la ventana.
     */
    public MainFrame() {

        controller = new AutomataController();
        automata = controller.getAutomata();

        mapaEstados = new HashMap<>();
        mapaTransiciones = new HashMap<>();

        setTitle("Editor de Autómatas y Expresiones Regulares");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel encabezado = new JPanel(new BorderLayout());

        encabezado.setBackground(new Color(62, 43, 112));

        encabezado.setBorder(
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        );

        JLabel titulo = new JLabel(
                "AUTOMATAS"
        );

        titulo.setFont(
                new Font("Segoe UI", Font.BOLD, 19)
        );

        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel(
                "Construccion, simulacion y conversion a Regex"
        );

        subtitulo.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        subtitulo.setForeground(
                new Color(224, 210, 255)
        );

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 4));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        encabezado.add(textos, BorderLayout.WEST);

        add(encabezado, BorderLayout.NORTH);


        
        // PANEL DE HERRAMIENTAS
       
        configurarPanelHerramientas();
        
        // PANEL DEL LIENZO
        

        panelLienzo = new JPanel();

        panelLienzo.setBorder(
                BorderFactory.createTitledBorder(
                        "Lienzo del Autómata"
                )
        );

        panelLienzo.setLayout(
                new BorderLayout()
        );

        configurarGrafo();
        
        editorVisualAFD = new EditorVisualAFD(
                graph,
                mapaEstados
        );

        Object parent =
                graph.getDefaultParent();

        graphComponent =
                new mxGraphComponent(graph);
        
        animadorSimulacion = new AnimadorSimulacion(
                graph,
                graphComponent,
                mapaEstados,
                mapaTransiciones
        );
        
        // Ocultar los botones + y - de los estados
        graphComponent.setFoldingEnabled(false);
        
        graphComponent.getGraphControl().setBackground(
                new Color(252, 250, 255)
        );

        graphComponent.getViewport().setBackground(
                new Color(252, 250, 255)
        );

        graphComponent.setPageVisible(false);
        
        graphComponent.setConnectable(false);
        

        getRootPane().getInputMap(
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        ).put(
                javax.swing.KeyStroke.getKeyStroke("ESCAPE"),
                "cancelarModo"
        );

        getRootPane().getActionMap().put(
                "cancelarModo",
                new javax.swing.AbstractAction() {

                    @Override
                    public void actionPerformed(
                            java.awt.event.ActionEvent e) {

                        if (!modoAgregarEstado && !modoCrearTransicion) {
                            return;
                        }

                        modoAgregarEstado = false;
                        limpiarModoTransicion();
                        mostrarAviso("Operación cancelada.");
                    }
                }
        );


        // LIENZO CON PANEL DE MENSAJES SUPERPUESTO

        JLayeredPane capaLienzo = new JLayeredPane() {

            @Override
            public void doLayout() {

                graphComponent.setBounds(
                        0, 0, getWidth(), getHeight()
                );

                int ancho = Math.min(280, getWidth() - 24);

                scrollMensajes.setBounds(
                        Math.max(8, getWidth() - ancho - 12),
                        12,
                        ancho,
                        140
                );
            }
        };

        capaLienzo.add(
                graphComponent,
                JLayeredPane.DEFAULT_LAYER
        );

        capaLienzo.add(
                scrollMensajes,
                JLayeredPane.PALETTE_LAYER
        );

        panelLienzo.add(
                capaLienzo,
                BorderLayout.CENTER
        );

        
        // EVENTOS DEL LIENZO
        

        graphComponent
                .getGraphControl()
                .addMouseListener(
                        new MouseAdapter() {

                    @Override
                    public void mousePressed(
                            MouseEvent e) {

                        // -------------------------------------------------
                        // MODO AGREGAR ESTADO
                        // -------------------------------------------------

                        if (modoAgregarEstado) {

                            crearEstadoEnPosicion(
                                    e.getX(),
                                    e.getY(),
                                    parent
                            );

                            return;
                        }

                        
                        // MODO CREAR TRANSICIÓN
                        

                        if (modoCrearTransicion) {

                            procesarClickTransicion(e);

                            return;
                        }
                    }
                }
        );

        // =========================================================
        // SINCRONIZAR POSICIÓN DE LOS ESTADOS
        // =========================================================

        graphComponent
                .getGraphControl()
                .addMouseListener(
                        new MouseAdapter() {

                    @Override
                    public void mouseReleased(
                            MouseEvent e) {

                        sincronizarPosicionEstado();
                    }
                }
        );

        
        // PANEL DE SIMULACIÓN
        configurarPanelSimulacion();

        // ESTILO GENERAL DE LA VENTANA

        getContentPane().setBackground(
                new Color(245, 240, 255)
        );

        // PANEL DE HERRAMIENTAS
        panelHerramientas.setBackground(
                new Color(250, 247, 255)
        );

        panelHerramientas.setPreferredSize(
                new Dimension(205, 0)
        );

        panelHerramientas.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(
                                        new Color(196, 173, 255),
                                        2, true
                                ),
                                "Herramientas",
                                0, 0,
                                new Font("Segoe UI", Font.BOLD, 13),
                                new Color(88, 60, 150)
                        ),
                        BorderFactory.createEmptyBorder(
                                10, 10, 10, 10
                        )
                )
        );

        // PANEL DEL LIENZO
        panelLienzo.setBackground(Color.WHITE);

        panelLienzo.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                new Color(196, 173, 255),
                                2, true
                        ),
                        "Lienzo del Autómata",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13),
                        new Color(88, 60, 150)
                )
        );

        // PANEL DE SIMULACIÓN
        panelSimulacion.setBackground(
                new Color(250, 247, 255)
        );

        panelSimulacion.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(
                                        new Color(196, 173, 255),
                                        2, true
                                ),
                                "Simulación",
                                0, 0,
                                new Font("Segoe UI", Font.BOLD, 13),
                                new Color(88, 60, 150)
                        ),
                        BorderFactory.createEmptyBorder(
                                6, 10, 8, 10
                        )
                )
        );



        // AGREGAR PANELES A LA VENTANA
        

        add(
                panelHerramientas,
                BorderLayout.WEST
        );

        add(
                panelLienzo,
                BorderLayout.CENTER
        );

        add(
                panelSimulacion,
                BorderLayout.SOUTH
        );
    }
    
    
    /**
    * Inserta visualmente un estado en el lienzo.
    * Se utiliza tanto al crear como al importar.
    */
   private Object insertarEstadoEnLienzo(
           Object parent,
           Estado estado,
           int x,
           int y) {

       return graph.insertVertex(
               parent,
               null,
               estado.getNombre(),
               x,
               y,
               TAMANO_ESTADO,
               TAMANO_ESTADO,
               ESTILO_ESTADO
       );
   }
    
    
   
   
    private void configurarPanelHerramientas() {

        panelHerramientas = new JPanel();

        panelHerramientas.setPreferredSize(
                new Dimension(180, 0)
        );

        panelHerramientas.setBorder(
                BorderFactory.createTitledBorder(
                        "Herramientas"
                )
        );

        panelHerramientas.setLayout(
                new GridLayout(9, 1, 5, 5)
        );

        JButton btnAgregarEstado =
                new JButton("Agregar Estado");

        JButton btnTransicion =
                new JButton("Crear Transición");

        JButton btnInicial =
                new JButton("Estado Inicial");

        JButton btnAceptacion =
                new JButton("Estado Aceptación");

        JButton btnEliminar =
                new JButton("Eliminar");

       
        JButton btnRenombrar =
                new JButton("Renombrar Estado");
        
        JButton btnEditarTransicion =
                new JButton("Editar Transición");
        
        JButton btnImportar =
                new JButton("Importar Autómata");

        JButton btnExportar =
                new JButton("Exportar Autómata");

        panelHerramientas.add(btnAgregarEstado);
        panelHerramientas.add(btnTransicion);
        panelHerramientas.add(btnInicial);
        panelHerramientas.add(btnAceptacion);
        panelHerramientas.add(btnEliminar);
        panelHerramientas.add(btnRenombrar);
        panelHerramientas.add(btnEditarTransicion);
        panelHerramientas.add(btnImportar);
        panelHerramientas.add(btnExportar);
        
        
        estilizarBotones(panelHerramientas);

        // Destacar Importar y Exportar
        btnImportar.setBackground(new java.awt.Color(197, 215, 255));
        btnExportar.setBackground(new java.awt.Color(255, 205, 230));

        panelHerramientas.setBackground(
                new java.awt.Color(250, 247, 255)
        );
        
        // BOTÓN AGREGAR ESTADO
        

        btnAgregarEstado.addActionListener(e -> {

            modoAgregarEstado = true;

            modoCrearTransicion = false;

            estadoOrigenTransicion = null;

            celdaOrigenTransicion = null;

            graphComponent.setCursor(
                    new Cursor(
                            Cursor.CROSSHAIR_CURSOR
                    )
            );
        });

        
        // BOTÓN CREAR TRANSICIÓN
        

        btnTransicion.addActionListener(e -> {

            modoAgregarEstado = false;

            modoCrearTransicion = true;

            estadoOrigenTransicion = null;

            celdaOrigenTransicion = null;

            graphComponent.setCursor(
                    new Cursor(
                            Cursor.DEFAULT_CURSOR
                    )
            );

            mostrarAviso("Selecciona el estado de origen.");
        });

        
        // BOTÓN ESTADO INICIAL
      

        btnInicial.addActionListener(e -> {

            establecerEstadoInicial();

        });

        
        // BOTÓN ESTADO DE ACEPTACIÓN
        

        btnAceptacion.addActionListener(e -> {

            establecerEstadoAceptacion();

        });

        
        // BOTÓN ELIMINAR
        

        btnEliminar.addActionListener(e -> {

            eliminarElementoSeleccionado();

        });

        
        
        btnRenombrar.addActionListener(e -> {
            renombrarEstadoSeleccionado();
        });

        
        btnEditarTransicion.addActionListener(e -> {
            editarTransicionSeleccionada();
        });
        
        //BOTONES DE IMPORTAR Y EXPORTAR
        
        btnExportar.addActionListener(e ->
                exportarAutomataAArchivo()
        );


        btnImportar.addActionListener(e ->
                importarAutomataDesdeArchivo()
        );
        

    }
    
    /**
    * Configura el panel de simulación y sus botones.
    */
    private void configurarPanelSimulacion() {

        panelSimulacion = new JPanel();

        
        panelSimulacion.setBorder(
                BorderFactory.createTitledBorder(
                        "Simulación"
                )
        );

        panelSimulacion.setLayout(
                new FlowLayout(
                        FlowLayout.LEFT
                )
        );

        JLabel etiquetaCadena =
                new JLabel("Cadena:");

        campoCadena =
                new JTextField(20);

        JButton btnSimular =
                new JButton("Simular");
        
        JButton btnConvertirRegex =
        new JButton("Convertir a Regex");
        
        JButton btnReiniciar =
        new JButton("Reiniciar Simulación");

        etiquetaResultado =
                new JLabel(
                        "Resultado: ---"
                );

        panelSimulacion.add(
                etiquetaCadena
        );

        panelSimulacion.add(
                campoCadena
        );

        panelSimulacion.add(
                btnSimular
        );
        
        panelSimulacion.add(
                btnConvertirRegex
        );

        panelSimulacion.add(
                btnReiniciar
        );

        panelSimulacion.add(
                etiquetaResultado
        );
        
        
        estilizarBotones(panelSimulacion);

        btnSimular.setBackground(
                new java.awt.Color(126, 95, 205)
        );
        btnSimular.setForeground(java.awt.Color.WHITE);

        btnConvertirRegex.setBackground(
                new java.awt.Color(177, 129, 225)
        );
        btnConvertirRegex.setForeground(java.awt.Color.WHITE);

        panelSimulacion.setBackground(
                new java.awt.Color(250, 247, 255)
        );

        
        // BOTÓN SIMULAR

        btnSimular.addActionListener(e -> {
            
            restaurarColoresSimulacion();
            
            iniciarSimulacion();

        });
        
        
        //BOTÓN DE CONVERSION REGEX
        
        
        btnConvertirRegex.addActionListener(e -> {

            VentanaConversionRegex ventana =
                    new VentanaConversionRegex();

            ventana.mostrarCopiaAutomata(graph);

            
            ventana.configurarAccionConversion(() -> {

                try {
                    ResultadoConversion resultado =
                            new ConversorRegex().convertirConPasos(automata);

                    ventana.cargarResultadoConversion(resultado, graph);

                    ventana.configurarVerificador(cadena -> {

                        try {
                            VerificadorEquivalencia verificador =
                                    new VerificadorEquivalencia();

                            boolean aceptadaAFD =
                                    verificador.aceptaAFD(automata, cadena);

                            boolean aceptadaRegex =
                                    verificador.aceptaRegex(
                                            resultado.getExpresion(), cadena
                                    );

                            ventana.mostrarResultadoVerificacion(
                                    aceptadaAFD, aceptadaRegex
                            );

                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(
                                    ventana,
                                    ex.getMessage(),
                                    "Error de verificación",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        }
                    });

                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(
                            ventana,
                            ex.getMessage(),
                            "Error de conversión",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            });


            ventana.setVisible(true);
        });

        
        
        //BOTÓN REINICIAR
        
        btnReiniciar.addActionListener(e -> {
            reiniciarSimulacion();
        });

    }
   
    // =============================================================
    // CREAR ESTADO
    // =============================================================

    private void crearEstadoEnPosicion(
            int x,
            int y,
            Object parent) {

        String nombre = 
                obtenerNombreEstadoDisponible();

        Estado estado =
                controller.crearEstado(
                        nombre,
                        x,
                        y
                );

        graph.getModel().beginUpdate();

        try {

            Object celda = insertarEstadoEnLienzo(
                    parent,
                    estado,
                    x - 40,
                    y - 40
            );

            mapaEstados.put(
                    celda,
                    estado
            );

        } finally {

            graph.getModel().endUpdate();
        }

        

        modoAgregarEstado = false;

        graphComponent.setCursor(
                new Cursor(
                        Cursor.DEFAULT_CURSOR
                )
        );
    }

    
    // CREAR TRANSICIÓN
    

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
                mapaEstados.get(celda);

        if (estado == null) {
            return;
        }

        
        // PRIMER CLICK: ESTADO ORIGEN
        

        if (estadoOrigenTransicion == null) {

            estadoOrigenTransicion =
                    estado;

            celdaOrigenTransicion =
                    celda;

            mostrarAviso(
                    "Origen: " + estado.getNombre()
                    + ". Ahora selecciona el destino."
            );

            return;
        }

        // ---------------------------------------------------------
        // SEGUNDO CLICK: ESTADO DESTINO
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

        // Si se cancela
        if (simbolo == null) {

            limpiarModoTransicion();

            return;
        }

        simbolo =
                simbolo.trim();

        // No permitir símbolo vacío
        if (simbolo.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El símbolo no puede estar vacío."
            );
            return;
        }
        
        // CREAR TRANSICIÓN EN EL MODELO
       

        Transicion transicion =
                controller.crearTransicion(
                        estadoOrigenTransicion,
                        estadoDestino,
                        simbolo
                );

        
        // CREAR TRANSICIÓN VISUAL
        

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
            

            // Configurar los bucles del mismo estado
            if (celdaOrigenTransicion == celdaDestino) {
                configurarEstiloBucle(celdaTransicion);
            }

            // Ajustar la posición de la etiqueta de transición
            mxGeometry geometriaTransicion =
                    graph.getModel()
                            .getGeometry(
                                    celdaTransicion
                            );

            if (geometriaTransicion != null) {

                geometriaTransicion.setOffset(
                        new mxPoint(
                                0,
                                -15
                        )
                );
            }

            

            graph.setCellStyles(
                    mxConstants
                            .STYLE_LABEL_BACKGROUNDCOLOR,
                    "#FFFFFF",
                    new Object[]{
                        celdaTransicion
                    }
            );

            mapaTransiciones.put(
                    celdaTransicion,
                    transicion
            );
            
            graph.setCellStyles(
                    mxConstants.STYLE_MOVABLE,
                    "0",
                    new Object[]{celdaTransicion}
            );

        } finally {

            graph.getModel().endUpdate();
        }

        actualizarCurvasTransiciones();

        // ---------------------------------------------------------
        // FINALIZAR MODO TRANSICIÓN
        // ---------------------------------------------------------

        limpiarModoTransicion();
        
        mostrarAviso("Transición creada correctamente.");
    }

    
    // ESTABLECER ESTADO INICIAL
    

    private void establecerEstadoInicial() {

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
                mapaEstados.get(celda);

        if (estado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "La selección no es un estado."
            );

            return;
        }

        controller.establecerEstadoInicial(
                estado
        );

        actualizarEtiquetasEstados();
        
        actualizarFlechaInicial(celda);

        mostrarAviso(estado.getNombre() + " ahora es el estado inicial.");
    
    }

    
    // ESTABLECER ESTADO DE ACEPTACIÓN
    

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
                mapaEstados.get(celda);

        if (estado == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "La selección no es un estado."
            );

            return;
        }

        // Si ya es de aceptación, lo quitamos.
        if (estado.isAceptacion()) {

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
    // ACTUALIZAR ETIQUETAS DE LOS ESTADOS
    // =============================================================

    private void actualizarEtiquetasEstados() {
        editorVisualAFD.actualizarEtiquetasEstados();
    }

    
//FLECHA DE ESTADO INICIAL
    

private void actualizarFlechaInicial(Object celdaEstado) {

    graph.getModel().beginUpdate();

    try {
        // Eliminar la flecha anterior si existe.
        if (marcadorEstadoInicial != null
                && graph.getModel().getParent(marcadorEstadoInicial) != null) {

            graph.removeCells(new Object[]{marcadorEstadoInicial});
        }

        // Crear una flecha visual afuera del círculo.
        marcadorEstadoInicial = graph.insertVertex(
                celdaEstado,
                null,
                "➜",
                -36, 32,
                48, 36,
                "shape=label;"
                + "fillColor=none;"
                + "strokeColor=none;"
                + "fontColor=#000000;"
                + "fontSize=30;"
                + "fontStyle=1;"
                + "align=center;"
                + "verticalAlign=middle;"
                + "movable=0;"
                + "resizable=0;"
                + "editable=0;"
                + "selectable=0;"
        );

    } finally {
        graph.getModel().endUpdate();
    }

    graphComponent.repaint();
}

    
    // ACTUALIZAR ESTILO DE LOS ESTADOS

    private void actualizarEstiloEstados() {
        editorVisualAFD.actualizarEstiloEstados();
    }


    
    // ELIMINAR ELEMENTO SELECCIONADO
    

    private void eliminarElementoSeleccionado() {

        Object celda =
                graph.getSelectionCell();

        if (celda == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero selecciona un estado o transición."
            );

            return;
        }

        // ---------------------------------------------------------
        // INTENTAR ELIMINAR ESTADO
        // ---------------------------------------------------------

        Estado estado =
                mapaEstados.get(celda);

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

            mapaTransiciones.entrySet().removeIf(entrada -> {
                Transicion t = entrada.getValue();
                return t.getOrigen() == estado || t.getDestino() == estado;
            });

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
            actualizarCurvasTransiciones();

            return;
        }

        // ---------------------------------------------------------
        // INTENTAR ELIMINAR TRANSICIÓN
        // ---------------------------------------------------------

        Transicion transicion =
                mapaTransiciones.get(celda);

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

            actualizarCurvasTransiciones();
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "No se pudo identificar el elemento."
        );
    }

    // =============================================================
    // SINCRONIZAR POSICIÓN DEL ESTADO
    // =============================================================

    private void sincronizarPosicionEstado() {
        editorVisualAFD.sincronizarPosicionEstado();
    }

    
    // INICIAR SIMULACIÓN
   

    
    
    private void iniciarSimulacion() {

        animadorSimulacion.detener();

        restaurarColoresSimulacion();

        if (automata.getEstadoInicial() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes establecer un estado inicial."
            );

            return;
        }

        SimuladorAFD simulador =
                new SimuladorAFD(automata);

        ResultadoSimulacion resultado =
                simulador.simular(campoCadena.getText());

        etiquetaResultado.setText("Resultado: Simulando...");

        animadorSimulacion.reproducir(
                resultado,
                automata.getEstadoInicial(),

                r -> etiquetaResultado.setText(
                        "Resultado: " + r.getEstadoResultado()
                ),

                mensaje -> JOptionPane.showMessageDialog(
                        this,
                        mensaje,
                        "Validación de cadena",
                        JOptionPane.WARNING_MESSAGE
                )
        );
    }

    // LIMPIAR MODO TRANSICIÓN

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
    
    // RENOMBRAR ESTADOS
    
    private void renombrarEstadoSeleccionado() {

        Object celda = graph.getSelectionCell();

        Estado estado = mapaEstados.get(celda);

        if (estado == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero selecciona un estado."
            );
            return;
        }

        String nuevoNombre = JOptionPane.showInputDialog(
                this,
                "Escribe el nuevo nombre del estado:",
                estado.getNombre()
        );

        if (nuevoNombre == null) {
            return;
        }

        nuevoNombre = nuevoNombre.trim();

        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El nombre no puede estar vacío."
            );
            return;
        }

        // Evitar nombres repetidos
        for (Estado otro : automata.getEstados()) {

            if (otro != estado
                    && otro.getNombre().equals(nuevoNombre)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ya existe un estado con ese nombre."
                );
                return;
            }
        }

        // Actualizar el modelo
        estado.setNombre(nuevoNombre);

        // Actualizar el lienzo
        actualizarEtiquetasEstados();

        JOptionPane.showMessageDialog(
                this,
                "Estado renombrado correctamente."
        );
    }
    
    
    //EDITAR TRANSICION
    
    private void editarTransicionSeleccionada() {

        Object celda = graph.getSelectionCell();
        Transicion transicion = mapaTransiciones.get(celda);

        if (transicion == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero selecciona una transición."
            );
            return;
        }

        String nuevoSimbolo = JOptionPane.showInputDialog(
                this,
                "Nuevo símbolo de la transición:",
                transicion.getSimbolo()
        );

        if (nuevoSimbolo == null) {
            return;
        }

        nuevoSimbolo = nuevoSimbolo.trim();

        if (nuevoSimbolo.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El símbolo no puede estar vacío."
            );
            return;
        }

        // Evitar transiciones duplicadas en un AFD.
        for (Transicion otra : automata.getTransiciones()) {
            if (otra != transicion
                    && otra.getOrigen() == transicion.getOrigen()
                    && otra.getSimbolo().equals(nuevoSimbolo)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ya existe una transición con ese símbolo "
                        + "desde el mismo estado."
                );
                return;
            }
        }

        // Actualizar los datos.
        transicion.setSimbolo(nuevoSimbolo);
        automata.agregarSimbolo(nuevoSimbolo);

        // Actualizar la etiqueta visual.
        graph.getModel().beginUpdate();
        try {
            graph.getModel().setValue(celda, nuevoSimbolo);
        } finally {
            graph.getModel().endUpdate();
        }

        JOptionPane.showMessageDialog(
                this,
                "Transición actualizada correctamente."
        );
    }
    
    
    //REINICIAR SIMULACIÓN 
    
   private void reiniciarSimulacion() {

        animadorSimulacion.detener();

        campoCadena.setText("");
        etiquetaResultado.setText("Resultado: ---");

        restaurarColoresSimulacion();

        graph.clearSelection();
        graphComponent.repaint();
    }

    //SEPARAR TRANSICIONES
    
    private void actualizarCurvasTransiciones() {
        GeometriaTransicionesAFD.actualizarCurvasTransiciones(graph);
    }


    // COLOR EN SIMULACION
    private void restaurarColoresSimulacion() {
        animadorSimulacion.restaurarColores();
    }

    
    private String obtenerNombreEstadoDisponible() {

        int numero = 0;

        while (true) {

            String nombre = "q" + numero;
            boolean existe = false;

            for (Estado estado : automata.getEstados()) {

                if (estado.getNombre().equals(nombre)) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                return nombre;
            }

            numero++;
        }
    }
    

    private void exportarAutomataAArchivo() {

        if (automata.getEstados().isEmpty()) {
            mostrarAviso("No hay estados para exportar.");
            return;
        }

        JFileChooser selector = new JFileChooser();

        selector.setFileFilter(
                new FileNameExtensionFilter(
                        "Autómatas JSON (*.json)", "json"
                )
        );

        selector.setSelectedFile(
                new java.io.File("mi_automata.json")
        );

        if (selector.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File archivo = selector.getSelectedFile();

        if (!archivo.getName().toLowerCase()
                .endsWith(".json")) {

            archivo = new java.io.File(
                    archivo.getParentFile(),
                    archivo.getName() + ".json"
            );
        }

        if (archivo.exists()) {
            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "El archivo ya existe. ¿Deseas reemplazarlo?",
                    "Confirmar exportación",
                    JOptionPane.YES_NO_OPTION
            );

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            // Obtener posiciones actuales del dibujo
            Map<Estado, Point> posiciones = new HashMap<>();

            for (Map.Entry<Object, Estado> entrada
                    : mapaEstados.entrySet()) {

                mxGeometry geometria =
                        graph.getModel().getGeometry(
                                entrada.getKey()
                        );

                if (geometria != null) {
                    posiciones.put(
                            entrada.getValue(),
                            new Point(
                                    (int) Math.round(geometria.getX()),
                                    (int) Math.round(geometria.getY())
                            )
                    );
                }
            }

            AutomataJson.guardarEnArchivo(
                    archivo.toPath(),
                    automata,
                    posiciones
            );

            mostrarAviso("Autómata exportado correctamente.");

        } catch (IOException | IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al exportar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    
    //MOSTRAR MENSAJES
    private void mostrarAviso(String mensaje) {
        scrollMensajes.agregarMensaje(mensaje);
    }
    
    
    //Importar 


    private void importarAutomataDesdeArchivo() {

        JFileChooser selector = new JFileChooser();

        selector.setFileFilter(
                new FileNameExtensionFilter(
                        "Autómatas JSON (*.json)", "json"
                )
        );

        if (selector.showOpenDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            java.nio.file.Path archivo =
                    selector.getSelectedFile().toPath();

            // Leer y validar antes de reemplazar el autómata
            AutomataJson.Datos datos =
                    AutomataJson.leerDesdeArchivo(archivo);

            if (!automata.getEstados().isEmpty()) {

                int respuesta = JOptionPane.showConfirmDialog(
                        this,
                        "¿Deseas reemplazar el autómata actual?",
                        "Importar autómata",
                        JOptionPane.YES_NO_OPTION
                );

                if (respuesta != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            cargarAutomataImportado(datos);

            mostrarAviso(
                    "Autómata importado correctamente."
            );

        } catch (IOException | IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al importar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    
    //RECONSTRUIR AUTOMATA
     
    private void cargarAutomataImportado(
            AutomataJson.Datos datos) {

        // Reconstruir el modelo utilizando la clase especializada.
        RestauradorModeloAFD.Resultado resultado =
                RestauradorModeloAFD.reconstruir(datos);

        AutomataController nuevoController =
                resultado.controller();

        Map<String, Estado> nuevosEstados =
                resultado.estados();

        // Detener cualquier simulación anterior
        reiniciarSimulacion();

        modoAgregarEstado = false;
        limpiarModoTransicion();
        marcadorEstadoInicial = null;
        animadorSimulacion.limpiarResaltados();

        graph.getModel().beginUpdate();

        try {
            Object parent = graph.getDefaultParent();

            // Limpiar el dibujo anterior
            graph.removeCells(
                    graph.getChildCells(parent, true, true)
            );

            mapaEstados.clear();
            mapaTransiciones.clear();

            controller = nuevoController;
            automata = nuevoController.getAutomata();

            Map<Estado, Object> celdas =
                    new HashMap<>();

            // Reconstruir estados visuales
            for (AutomataJson.EstadoDato dato : datos.estados) {

                Estado estado = nuevosEstados.get(dato.nombre);

                Object celda = insertarEstadoEnLienzo(
                        parent,
                        estado,
                        dato.x,
                        dato.y
                );

                mapaEstados.put(celda, estado);
                celdas.put(estado, celda);
            }

            // Reconstruir las transiciones
            for (Transicion transicion
                    : automata.getTransiciones()) {

                Object celda = graph.insertEdge(
                        parent,
                        null,
                        transicion.getSimbolo(),
                        celdas.get(transicion.getOrigen()),
                        celdas.get(transicion.getDestino())
                );
                
                // Recuperar el estilo de los bucles
                if (transicion.getOrigen() == transicion.getDestino()) {
                    configurarEstiloBucle(celda);
                }

                mxGeometry geometria =
                        graph.getModel().getGeometry(celda);

                if (geometria != null) {

                    mxGeometry nueva =
                            (mxGeometry) geometria.clone();

                    nueva.setOffset(
                            new mxPoint(0, -15)
                    );

                    graph.getModel().setGeometry(celda, nueva);
                }

                graph.setCellStyles(
                        mxConstants.STYLE_LABEL_BACKGROUNDCOLOR,
                        "#FFFFFF",
                        new Object[]{celda}
                );

                graph.setCellStyles(
                        mxConstants.STYLE_MOVABLE,
                        "0",
                        new Object[]{celda}
                );

                mapaTransiciones.put(celda, transicion);
            }

        } finally {
            graph.getModel().endUpdate();
        }

        // Recuperar estados finales
        actualizarEstiloEstados();

        // Recuperar la flecha del estado inicial
        if (automata.getEstadoInicial() != null) {

            for (Map.Entry<Object, Estado> entrada
                    : mapaEstados.entrySet()) {

                if (entrada.getValue()
                        == automata.getEstadoInicial()) {

                    actualizarFlechaInicial(
                            entrada.getKey()
                    );
                    break;
                }
            }
        }

        // Recuperar las curvas de las transiciones
        actualizarCurvasTransiciones();

        graph.clearSelection();
        graphComponent.refresh();
        graphComponent.repaint();
    }
      
     // Aplica el estilo visual a una transición que regresa al mismo estado.

    private void configurarEstiloBucle(Object celda) {

        Object[] celdas = {celda};

        graph.setCellStyles(
                mxConstants.STYLE_ROUNDED,
                "1",
                celdas
        );

        graph.setCellStyles(
                mxConstants.STYLE_LOOP,
                "loopEdgeStyle",
                celdas
        );

        graph.setCellStyles(
                mxConstants.STYLE_DIRECTION,
                mxConstants.DIRECTION_NORTH,
                celdas
        );

        graph.setCellStyles(
                mxConstants.STYLE_SEGMENT,
                "30",
                celdas
        );

        graph.setCellStyles(
                mxConstants.STYLE_SHAPE,
                "curvaAFD",
                celdas
        );

        graph.setCellStyles(
                mxConstants.STYLE_ENDSIZE,
                "8",
                celdas
        );
    }

    private void estilizarBotones(JPanel panel) {
        EstilosUI.estilizarBotonesPanel(panel);
    }
}