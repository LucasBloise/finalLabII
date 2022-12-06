/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Jugador;
import Modelo.TableroData;
import Recursos.EstadoDelJuego;
import Recursos.PosicionTablero;
import Recursos.Utilidades;
import Vista.HistorialVista;
import Vista.MenuConfiguracion;
import Vista.MenuInicial;
import Vista.PuntajeVista;
import Vista.Tablero;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JOptionPane;


/**
 *
 * @author lucasbloise
 */
public class Controlador implements ActionListener {

    public Controlador(MenuInicial menuInicial, MenuConfiguracion menuConfiguracion, Tablero tableroVista, PuntajeVista puntajeVista, HistorialVista historialVista, Jugador jugador1, Jugador jugador2, TableroData tablero) {
        this.menuInicial = menuInicial;
        this.menuConfiguracion = menuConfiguracion;
        this.tableroVista = tableroVista;
        this.puntajeVista = puntajeVista;
        this.historialVista = historialVista;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.tablero = tablero;
    }

    MenuInicial menuInicial;
    MenuConfiguracion menuConfiguracion;
    Tablero tableroVista;
    PuntajeVista puntajeVista;
    HistorialVista historialVista;
    Jugador jugador1;
    Jugador jugador2;
    TableroData tablero;
    ArrayList<JButton> botones = new ArrayList<>();
    EstadoDelJuego estadoDelJuego = EstadoDelJuego.JUGADOR_1_UBICANDO_BARCOS;
    int centesimas_segundos = 0;
    int segundos = 0;
    int minutos = 0;
    int horas = 0;
  
    

    public void init() {
        // Archivos
        initArchivoDeGuardado("preferenciasJuego");
        initArchivoDeGuardado("historial");
        leerArchivoDeGuardadoPreferencias();
        leerArchivoDeGuardadoHistorial();
        //MENU INICIAL ACTION LISTENERS
        menuInicial.getConfigButton().addActionListener(this);
        menuInicial.getPlayButton().addActionListener(this);
        menuInicial.getHistoryButton().addActionListener(this);

        //MENU CONFIGURACION ACTION LISTENERS
        menuConfiguracion.getTableRowsTextField().addActionListener(this);
        menuConfiguracion.getColumnsTableTextField().addActionListener(this);
        menuConfiguracion.getPlayer1NameTextField().addActionListener(this);
        menuConfiguracion.getPlayer2TextField().addActionListener(this);
        menuConfiguracion.getSaveButton().addActionListener(this);

        // PUNTAJE VISTA INIT
        puntajeVista.getPlayer1Name().setText(jugador1.getNombre());
        puntajeVista.getPlayer2Name().setText(jugador2.getNombre());

        //VISIBILIDAD ACTION LISTENERS
        menuInicial.setVisible(true);
        Utilidades.getTiempo().addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == menuInicial.getConfigButton()) {
            // Manejamos el evento si se apreto el boton de configuracion
            menuInicial.setVisible(false);
            menuConfiguracion.setVisible(true);
        } else if (source == menuConfiguracion.getSaveButton()) {
            guardarValidarDatos();
            guardarArchivoDeGuardadoPreferencias();
            menuInicial.setVisible(true);
            menuConfiguracion.setVisible(false);
        } else if (source == menuInicial.getPlayButton()) {
            // Manejamos el evento si se apreto el boton de iniciar juego
            prepararTablero();
            JOptionPane.showMessageDialog(null, "Turno de ubicar los barcos de " + jugador1.getNombre());
            menuInicial.setVisible(false);
            tableroVista.setVisible(true);

        } else if (source == menuInicial.getHistoryButton()) {
            // Manejamos el evento si se apreto el boton del historial
            leerArchivoDeGuardadoHistorial();
            menuInicial.setVisible(false);
            historialVista.setVisible(true);
        } else if (botones.contains(source)) {
            // Manejamos el evento si se apreto cualquier boton del tablero
            JButton boton = botones.get(botones.indexOf(source));
            String[] posicionBoton = boton.getName().split(",");
            int botonX = Integer.parseInt(posicionBoton[0]);
            int botonY = Integer.parseInt(posicionBoton[1]);
            manejarAtaqueDefensa(botonX, botonY, boton);
            actualizarPuntajeVista();
        } else if (source == Utilidades.getTiempo()) {
            actualizarVistaTiempo();
        }

    }

    

    public void manejarAtaqueDefensa(int botonX, int botonY, JButton boton) throws HeadlessException {
        // Manejamos si es ataque o defensa
        switch (estadoDelJuego) {
            case JUGADOR_1_UBICANDO_BARCOS:
                // UBICAR BARCOS JUGADOR 1
                if (jugador1.validarInsertarBarco(botonX, botonY)) {
                    boton.setIcon(Utilidades.getShipIcon());
                    jugador1.insertarBarco(botonX, botonY);
                    validarEstadoDelJuego();
                } else {
                    JOptionPane.showMessageDialog(null, "Posicion Invalida,ya hay un barco ubicado en esa posicion");
                }
                break;
            case JUGADOR_2_UBICANDO_BARCOS:
                // UBICAR BARCOS JUGADOR 2
                if (jugador2.validarInsertarBarco(botonX, botonY)) {
                    boton.setIcon(Utilidades.getShipIcon());
                    jugador2.insertarBarco(botonX, botonY);
                    validarEstadoDelJuego();
                } else {
                    JOptionPane.showMessageDialog(null, "Posicion Invalida,ya hay un barco ubicado en esa posicion");
                }
                break;
            case ATAQUE_JUGADOR_1:
                // ATAQUE BARCO JUGADOR 1
                if (jugador2.validarAtaqueRepetido(botonX, botonY)) {
                    JOptionPane.showMessageDialog(null, "Disparo Repetido");
                    
                } else if (jugador2.validarAtaqueAgua(botonX, botonY)) {
                    JOptionPane.showMessageDialog(null, "Disparo al agua");
                }
                if (jugador2.ejecutarAtaque(botonX, botonY)) {
                    JOptionPane.showMessageDialog(null, "Hundiste un Barco!");
                }
                
                validarEstadoDelJuego();
                break;
            case ATAQUE_JUGADOR_2:
                // ATAQUE BARCO JUGADOR 2
                if (jugador1.validarAtaqueRepetido(botonX, botonY)) {
                    JOptionPane.showMessageDialog(null, "Disparo Repetido");
                    
                } else if (jugador1.validarAtaqueAgua(botonX, botonY)) {
                    JOptionPane.showMessageDialog(null, "Disparo al agua");
                }
                if (jugador1.ejecutarAtaque(botonX, botonY)) {
                    JOptionPane.showMessageDialog(null, "Hundiste un Barco!");
                }
                validarEstadoDelJuego();
                break;
                
        }
    }

    public void validarEstadoDelJuego() {

        if (!hayUnGanador()) {

            // SI EL JUGADOR 1 TERMINO DE INGRESAR LOS BARCOS
            if (jugador1.termineDePosicionarBarcos() && estadoDelJuego.equals(EstadoDelJuego.JUGADOR_1_UBICANDO_BARCOS)) {
                JOptionPane.showMessageDialog(null, "Turno de ubicar los barcos de " + jugador2.getNombre());
                limpiarMapa();
                estadoDelJuego = EstadoDelJuego.JUGADOR_2_UBICANDO_BARCOS;
                return;
            } else if (jugador2.termineDePosicionarBarcos() && estadoDelJuego.equals(EstadoDelJuego.JUGADOR_2_UBICANDO_BARCOS)) {
                JOptionPane.showMessageDialog(null, "Empieza el ataque entre jugadores");
                Utilidades.getTiempo().start();
                puntajeVista.setVisible(true);
                JOptionPane.showMessageDialog(null, "Le toca atacar a " + jugador1.getNombre());
                limpiarMapa();
                estadoDelJuego = EstadoDelJuego.ATAQUE_JUGADOR_1;
            } else if (estadoDelJuego.equals(EstadoDelJuego.ATAQUE_JUGADOR_1)) {
                estadoDelJuego = EstadoDelJuego.ATAQUE_JUGADOR_2;
                JOptionPane.showMessageDialog(null, "Le toca atacar a " + jugador2.getNombre());
                renderizarMapaJugador(jugador1.getTablero());
            } else if (estadoDelJuego.equals(EstadoDelJuego.ATAQUE_JUGADOR_2)) {
                estadoDelJuego = EstadoDelJuego.ATAQUE_JUGADOR_1;
                JOptionPane.showMessageDialog(null, "Le toca atacar a " + jugador1.getNombre());
                renderizarMapaJugador(jugador2.getTablero());

            }
        } else {

            volverAlMenuDeInicio();
        }
    }

    public boolean hayUnGanador() {
        if (estadoDelJuego != EstadoDelJuego.JUGADOR_1_UBICANDO_BARCOS && estadoDelJuego != EstadoDelJuego.JUGADOR_2_UBICANDO_BARCOS) {
            if (!jugador1.meQuedanBarcos()) {
                JOptionPane.showMessageDialog(null, "Gano " + jugador2.getNombre());
                guardarArchivoDeGuardadoHistorial(jugador1.getNombre() + " vs " + jugador2.getNombre() + " gano " + jugador2.getNombre() + " en " + Utilidades.getDtf().format(Utilidades.getNow()));
                return true;
            } else if (!jugador2.meQuedanBarcos()) {
                JOptionPane.showMessageDialog(null, "Gano " + jugador1.getNombre());
                guardarArchivoDeGuardadoHistorial(jugador1.getNombre() + " vs " + jugador2.getNombre() + " gano " + jugador1.getNombre() + " en " + Utilidades.getDtf().format(Utilidades.getNow()));
                return true;
            }
        }
        return false;
    }
    
    public void guardarValidarDatos() throws HeadlessException, NumberFormatException {
        // Manejamos el evento si se apreto el boton de guardado de preferencias
        if (Integer.parseInt(menuConfiguracion.getTableRowsTextField().getText()) < 2 || Integer.parseInt(menuConfiguracion.getColumnsTableTextField().getText()) < 2) {
            JOptionPane.showMessageDialog(null, "Filas o Columnas no pueden ser menor a 2" );
            tablero.setCantidadFilas(2);
            tablero.setCantidadColumnas(2);
        } else {
            tablero.setCantidadFilas(Integer.parseInt(menuConfiguracion.getTableRowsTextField().getText()));
            tablero.setCantidadColumnas(Integer.parseInt(menuConfiguracion.getColumnsTableTextField().getText()));
        }
        
        jugador1.setNombre(menuConfiguracion.getPlayer1NameTextField().getText());
        jugador2.setNombre(menuConfiguracion.getPlayer2TextField().getText());
    }

    public void limpiarMapa() {
        for (JButton boton : botones) {
            boton.setIcon(Utilidades.getWaterIcon());
        }
    }

    public void renderizarMapaJugador(PosicionTablero[][] tablero) {

        for (JButton boton : botones) {
            String[] posicionBoton = boton.getName().split(",");
            int botonX = Integer.parseInt(posicionBoton[0]);
            int botonY = Integer.parseInt(posicionBoton[1]);

            for (int i = 0; i < TableroData.getCantidadFilas(); i++) {
                for (int j = 0; j < TableroData.getCantidadColumnas(); j++) {
                    if (i == botonX && j == botonY) {

                        switch (tablero[i][j]) {
                            case BARCO:
                                boton.setIcon(Utilidades.getWaterIcon());
                                break;
                            case AGUA:
                                boton.setIcon(Utilidades.getWaterIcon());
                                break;
                            case BARCO_HUNDIDO:
                                boton.setIcon(Utilidades.getShipDamegeIcon());
                                break;
                            case DISPARO_REPETIDO:
                                boton.setIcon(Utilidades.getWaterSplashIcon());
                                break;
                        }
                    }
                }
            }

        }
    }

    public void volverAlMenuDeInicio() {
        Utilidades.getTiempo().stop();
        tableroVista.setVisible(false);

    }

    public void prepararTablero() {
        botones.clear();
        leerArchivoDeGuardadoPreferencias();
        // PARTE VISUAL

        for (int i = 0; i < (tablero.getCantidadFilas()); i++) {
            for (int j = 0; j < tablero.getCantidadColumnas(); j++) {
                JButton botonTemporal = new JButton();
                botonTemporal.setIcon(Utilidades.getWaterIcon());
                botonTemporal.setName(i + "," + j);
                botones.add(botonTemporal);
            }
        }

        for (JButton boton : botones) {
            boton.addActionListener(this);
            tableroVista.add(boton);
        }

        tableroVista.setLayout(new GridLayout(tablero.getCantidadFilas(), tablero.getCantidadColumnas()));
        tableroVista.setSize(700, 700);

        // LLENAR DE AGUA EL MAPA
        jugador1.setTablero(tablero.getCantidadFilas(), tablero.getCantidadColumnas());
        jugador2.setTablero(tablero.getCantidadFilas(), tablero.getCantidadColumnas());
        jugador1.llenarTableroconAgua(tablero.getCantidadFilas(), tablero.getCantidadColumnas());
        jugador2.llenarTableroconAgua(tablero.getCantidadFilas(), tablero.getCantidadColumnas());
    }

    public void actualizarPuntajeVista() {
        // se encarga de actualizar puntaje de la vista
        puntajeVista.getPlayer1Disparos().setText("Disparos: " + jugador2.getIntentos());
        puntajeVista.getPlayer1DisparosAgua().setText("Disparos al agua: " + jugador2.getIntentosAgua());
        puntajeVista.getPlayer1DisparosRepetidos().setText("Disparos Repetidos: " + jugador2.getIntentosRepetidos());
        puntajeVista.getPlayer1BarcosHundidos().setText("Barcos Hundidos: " + jugador2.getBarcosHundidos());

        puntajeVista.getPlayer2Disparos().setText("Disparos: " + jugador1.getIntentos());
        puntajeVista.getPlayer2DisparosAgua().setText("Disparos al agua: " + jugador1.getIntentosAgua());
        puntajeVista.getPlayer2DisparosRepetidos().setText("Disparos Repetidos: " + jugador1.getIntentosRepetidos());
        puntajeVista.getPlayer2BarcosHundidos().setText("Barcos Hundidos: " + jugador1.getBarcosHundidos());

    }

    public void actualizarVistaTiempo() {
    // Se encarga de actualizar el tiempo y su respectiva vista
        centesimas_segundos++;
        if (centesimas_segundos == 100) {
            segundos++;
            centesimas_segundos = 0;
        }
        if (segundos == 60) {
            minutos++;
            segundos = 0;
        }
        if (minutos == 60) {
            horas++;
            minutos = 0;
        }
        if (horas == 24) {
            horas = 0;
        }

        String texto = (horas <= 9 ? "0" : "") + horas + ":" + (minutos <= 9 ? "0" : "") + minutos + ":" + (segundos <= 9 ? "0" : "") + segundos + ":" + (centesimas_segundos <= 9 ? "0" : "") + centesimas_segundos;
        puntajeVista.getTiempoDePartida().setText("Tiempo de Partida: " + texto);
    }

    // MANEJO DE ARCHIVOS
    public void initArchivoDeGuardado(String fileName) {
        try {
            File myObj = new File("src" + File.separator + fileName + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("Archivo creado: " + myObj.getName());
            } else {
                leerArchivoDeGuardadoPreferencias();
                System.out.println("El archivo ya existe.");
            }
        } catch (IOException e) {
            System.out.println("Sucedioo un error al iniciar el archivo.");
            e.printStackTrace();
        }
    }

    public void guardarArchivoDeGuardadoPreferencias() {
        String auxNombres = "";
        auxNombres = jugador1.getNombre() + "," + jugador2.getNombre() + "," + tablero.getCantidadFilas() + "," + tablero.getCantidadColumnas();

        try {
            FileWriter myWriter = new FileWriter("src" + File.separator + "preferenciasJuego" + ".txt");
            myWriter.write(auxNombres);
            myWriter.close();
            System.out.println("Guardado existosamente:." + auxNombres);
        } catch (IOException e) {
            System.out.println("Sucedio un error al guardar las preferencias.");
            e.printStackTrace();
        }

    }

    public void guardarArchivoDeGuardadoHistorial(String resultado) {
        String textoGuardado = "";
        try {
            File myObj = new File("src" + File.separator + "historial" + ".txt");
            Scanner myReader = new Scanner(myObj);

            if (myReader.hasNextLine()) {
                textoGuardado = myReader.nextLine();

                myReader.close();

            }

        } catch (FileNotFoundException e) {
            System.out.println("Sucedio un error al guardar el historial.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("src" + File.separator + "historial" + ".txt");
            myWriter.write(textoGuardado + "," + resultado);
            myWriter.close();
            System.out.println("Guardado existosamente:" + resultado);
        } catch (IOException e) {
            System.out.println("Sucedio un error al guardar el historial.");
            e.printStackTrace();
        }

    }

    public void leerArchivoDeGuardadoHistorial() {
        String[] textoGuardado;
        historialVista.getHistorialLista().removeAll();
        try {
            File myObj = new File("src" + File.separator + "historial" + ".txt");
            Scanner myReader = new Scanner(myObj);

            if (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                textoGuardado = data.split(",");
                myReader.close();

                historialVista.getHistorialLista().setListData(textoGuardado);

            }

        } catch (FileNotFoundException e) {
            System.out.println("Sucedio un error al leer el historial.");
            e.printStackTrace();
        }
    }

    public void leerArchivoDeGuardadoPreferencias() {
        String[] textoGuardado;
        try {
            File myObj = new File("src" + File.separator + "preferenciasJuego" + ".txt");
            Scanner myReader = new Scanner(myObj);

            if (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                textoGuardado = data.split(",");
                myReader.close();
                System.out.println(data);
                for (int i = 0; i < textoGuardado.length; i++) {
                    switch (i) {
                        case 0:
                            jugador1.setNombre(textoGuardado[0]);
                            menuConfiguracion.getPlayer1NameTextField().setText(textoGuardado[0]);
                            puntajeVista.getPlayer1Name().setText(textoGuardado[0]);
                            break;
                        case 1:
                            jugador2.setNombre(textoGuardado[1]);
                            menuConfiguracion.getPlayer2TextField().setText(textoGuardado[1]);
                            puntajeVista.getPlayer2Name().setText(textoGuardado[1]);
                            break;
                        case 2:
                            menuConfiguracion.getTableRowsTextField().setText(textoGuardado[2]);
                            tablero.setCantidadFilas(Integer.parseInt(textoGuardado[2]));
                            break;
                        case 3:
                            menuConfiguracion.getColumnsTableTextField().setText(textoGuardado[3]);
                            tablero.setCantidadColumnas(Integer.parseInt(textoGuardado[3]));
                            break;

                    }
                }
            }else{
                // Si no hay nada guardado , seteamos valores por defecto
                // Jugador 1
                jugador1.setNombre("Jugador 1");
                menuConfiguracion.getPlayer1NameTextField().setText("Jugador 1");
                puntajeVista.getPlayer1Name().setText("Jugador 1");
                 // Jugador 2
                jugador2.setNombre("Jugador 2");
                menuConfiguracion.getPlayer2TextField().setText("Jugador 2");
                puntajeVista.getPlayer2Name().setText("Jugador 2");
                // Tablero
                menuConfiguracion.getTableRowsTextField().setText("4");
                tablero.setCantidadFilas(4);
                menuConfiguracion.getColumnsTableTextField().setText("4");
                tablero.setCantidadColumnas(4);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Sucedio un error al leer las preferencias.");
            e.printStackTrace();
        }
    }

}
