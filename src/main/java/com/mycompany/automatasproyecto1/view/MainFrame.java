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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Ventana principal del editor de autómatas.
 */
public class MainFrame extends JFrame {

    private JPanel panelHerramientas;
    private JPanel panelLienzo;
    private JPanel panelSimulacion;

    private mxGraph graph;
    private mxGraphComponent graphComponent;

    private AutomataController controller;
    private Automata automata;

    private boolean modoAgregarEstado = false;
    private int contadorEstados = 0;

    private Map<Object, Estado> mapaEstados;
    private Map<Object, Transicion> mapaTransiciones;

    private boolean modoCrearTransicion = false;
    private Estado estadoOrigenTransicion = null;
    private Object celdaOrigenTransicion = null;

    private Timer timerSimulacion;
    private String cadenaSimulacion = "";
    private int posicionSimulacion = 0;
    private Estado estadoActualSimulacion = null;

    private JTextField campoCadena;
    private JLabel etiquetaResultado;

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

        
        // PANEL DE HERRAMIENTAS
        
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
                new GridLayout(6, 1, 5, 5)
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

        JButton btnMover =
                new JButton("Mover");

        panelHerramientas.add(btnAgregarEstado);
        panelHerramientas.add(btnTransicion);
        panelHerramientas.add(btnInicial);
        panelHerramientas.add(btnAceptacion);
        panelHerramientas.add(btnEliminar);
        panelHerramientas.add(btnMover);

        
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

        graph = new mxGraph();

        Object parent =
                graph.getDefaultParent();

        graphComponent =
                new mxGraphComponent(graph);

        panelLienzo.add(
                graphComponent,
                BorderLayout.CENTER
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

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona primero el estado origen\n"
                    + "y después el estado destino."
            );
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

        
        // BOTÓN MOVER
        

        btnMover.addActionListener(e -> {

            modoAgregarEstado = false;

            modoCrearTransicion = false;

            estadoOrigenTransicion = null;

            celdaOrigenTransicion = null;

            graphComponent.setCursor(
                    new Cursor(
                            Cursor.DEFAULT_CURSOR
                    )
            );
        });

        
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
                etiquetaResultado
        );

        
        // BOTÓN SIMULAR
        

        btnSimular.addActionListener(e -> {

            iniciarSimulacion();

        });

        
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

    // =============================================================
    // CREAR ESTADO
    // =============================================================

    private void crearEstadoEnPosicion(
            int x,
            int y,
            Object parent) {

        String nombre =
                "q" + contadorEstados;

        Estado estado =
                controller.crearEstado(
                        nombre,
                        x,
                        y
                );

        graph.getModel().beginUpdate();

        try {

            Object celda =
                    graph.insertVertex(
                            parent,
                            null,
                            estado.getNombre(),
                            x - 40,
                            y - 40,
                            80,
                            80
                    );

            mapaEstados.put(
                    celda,
                    estado
            );

        } finally {

            graph.getModel().endUpdate();
        }

        contadorEstados++;

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

            // =====================================================
            // IMPORTANTE:
            //
            // Movemos la etiqueta de la transición ligeramente
            // hacia arriba para que no quede atravesada por la
            // línea de la flecha.
            // =====================================================

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

        } finally {

            graph.getModel().endUpdate();
        }

        // ---------------------------------------------------------
        // FINALIZAR MODO TRANSICIÓN
        // ---------------------------------------------------------

        limpiarModoTransicion();
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

        JOptionPane.showMessageDialog(
                this,
                estado.getNombre()
                + " ahora es el estado inicial."
        );
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

                String texto =
                        estado.getNombre();

                // Estado inicial
                if (estado.isInicial()) {

                    texto =
                            "→ "
                            + texto;
                }

                // Estado de aceptación
                if (estado.isAceptacion()) {

                    texto =
                            texto
                            + " (F)";
                }

                graph.getModel().setValue(
                        celda,
                        texto
                );
            }

        } finally {

            graph.getModel().endUpdate();
        }
    }

    
    // ACTUALIZAR ESTILO DE LOS ESTADOS
    

    private void actualizarEstiloEstados() {

        for (
                Map.Entry<Object, Estado> entrada
                : mapaEstados.entrySet()
        ) {

            Object celda =
                    entrada.getKey();

            Estado estado =
                    entrada.getValue();

            if (estado.isAceptacion()) {

                graph.setCellStyles(
                        mxConstants.STYLE_STROKEWIDTH,
                        "3",
                        new Object[]{
                            celda
                        }
                );

            } else {

                graph.setCellStyles(
                        mxConstants.STYLE_STROKEWIDTH,
                        "1",
                        new Object[]{
                            celda
                        }
                );
            }
        }
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

        Object celda =
                graph.getSelectionCell();

        if (celda == null) {
            return;
        }

        Estado estado =
                mapaEstados.get(celda);

        if (estado == null) {
            return;
        }

        mxGeometry geometria =
                graph.getModel()
                        .getGeometry(celda);

        if (geometria == null) {
            return;
        }

        estado.setX(
                (int) geometria.getX()
        );

        estado.setY(
                (int) geometria.getY()
        );
    }

    
    // INICIAR SIMULACIÓN
   

    private void iniciarSimulacion() {

        if (
                timerSimulacion != null
                && timerSimulacion.isRunning()
        ) {

            timerSimulacion.stop();
        }

       

        if (
                automata.getEstadoInicial()
                == null
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes establecer un estado inicial."
            );

            return;
        }

        cadenaSimulacion =
                campoCadena.getText();

        posicionSimulacion =
                0;

        estadoActualSimulacion =
                automata.getEstadoInicial();

        etiquetaResultado.setText(
                "Resultado: Simulando..."
        );

        seleccionarEstadoVisual(
                estadoActualSimulacion
        );

        // ---------------------------------------------------------
        // CADENA VACÍA
        // ---------------------------------------------------------

        if (cadenaSimulacion.isEmpty()) {

            finalizarSimulacion();

            return;
        }

        

        timerSimulacion =
                new Timer(
                        800,
                        e ->
                                ejecutarPasoSimulacion()
                );

        timerSimulacion.start();
    }

    
    // EJECUTAR PASO DE SIMULACIÓN
    

    private void ejecutarPasoSimulacion() {

        if (
                posicionSimulacion
                >= cadenaSimulacion.length()
        ) {

            finalizarSimulacion();

            return;
        }

        char caracter =
                cadenaSimulacion.charAt(
                        posicionSimulacion
                );

        String simbolo =
                String.valueOf(
                        caracter
                );

        Estado siguiente =
                buscarSiguienteEstado(
                        estadoActualSimulacion,
                        simbolo
                );

       
        // NO EXISTE TRANSICIÓN
        

        if (siguiente == null) {

            timerSimulacion.stop();

            etiquetaResultado.setText(
                    "Resultado: RECHAZADA"
            );

            JOptionPane.showMessageDialog(
                    this,
                    "La cadena fue rechazada.\n\n"
                    + "No existe una transición desde "
                    + estadoActualSimulacion.getNombre()
                    + " con el símbolo '"
                    + simbolo
                    + "'."
            );

            return;
        }

        
        // AVANZAR AL SIGUIENTE ESTADO
       

        estadoActualSimulacion =
                siguiente;

        posicionSimulacion++;

        seleccionarEstadoVisual(
                estadoActualSimulacion
        );

        // ---------------------------------------------------------
        // FINALIZAR SI YA SE PROCESÓ TODA LA CADENA
        // ---------------------------------------------------------

        if (
                posicionSimulacion
                >= cadenaSimulacion.length()
        ) {

            finalizarSimulacion();
        }
    }

    // BUSCAR SIGUIENTE ESTADO

    private Estado buscarSiguienteEstado(
            Estado actual,
            String simbolo) {

        for (
                Transicion transicion
                : automata.getTransiciones()
        ) {

            if (
                    transicion.getOrigen()
                    == actual
                    &&
                    transicion.getSimbolo()
                            .equals(simbolo)
            ) {

                return transicion.getDestino();
            }
        }

        return null;
    }

    // FINALIZAR SIMULACIÓN

    private void finalizarSimulacion() {

        if (
                timerSimulacion != null
                && timerSimulacion.isRunning()
        ) {

            timerSimulacion.stop();
        }

        if (
                automata
                        .getEstadosAceptacion()
                        .contains(
                                estadoActualSimulacion
                        )
        ) {

            etiquetaResultado.setText(
                    "Resultado: ACEPTADA"
            );

        } else {

            etiquetaResultado.setText(
                    "Resultado: RECHAZADA"
            );
        }
    }

    // SELECCIONAR ESTADO VISUALMENTE

    private void seleccionarEstadoVisual(
            Estado estado) {

        for (
                Map.Entry<Object, Estado> entrada
                : mapaEstados.entrySet()
        ) {

            if (
                    entrada.getValue()
                    == estado
            ) {

                graph.setSelectionCell(
                        entrada.getKey()
                );

                return;
            }
        }
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
}