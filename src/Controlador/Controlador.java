/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Jugador;
import Modelo.TableroData;
import Recursos.EstadoDelJuego;
import Vista.MenuConfiguracion;
import Vista.MenuInicial;
import Vista.Tablero2;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author lucasbloise
 */
public class Controlador implements ActionListener {

    public Controlador(MenuInicial menuInicial, MenuConfiguracion menuConfiguracion, Tablero2 tableroVista, Jugador jugador1, Jugador jugador2, TableroData tablero) {
        this.menuInicial = menuInicial;
        this.menuConfiguracion = menuConfiguracion;
        this.tableroVista = tableroVista;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.tablero = tablero;
    }

    MenuInicial menuInicial;
    MenuConfiguracion menuConfiguracion;
    Tablero2 tableroVista;
    Jugador jugador1;
    Jugador jugador2;
    TableroData tablero;
    ArrayList<JButton> botones = new ArrayList<>();
    EstadoDelJuego estadoDelJuego = EstadoDelJuego.JUGADOR_1_UBICANDO_BARCOS;
    ImageIcon water = new ImageIcon("/Users/lucasbloise/NetBeansProjects/BatallaNavalLucasBloise/src/Recursos/water.png");
    ImageIcon ship = new ImageIcon("/Users/lucasbloise/NetBeansProjects/BatallaNavalLucasBloise/src/Recursos/ship.png");

    public void init() {
        // Archivos
        initArchivoDeGuardado();
        leerArchivoDeGuardado();

        //MENU INICIAL ACTION LISTENERS
        menuInicial.getConfigButton().addActionListener(this);
        menuInicial.getPlayButton().addActionListener(this);

        //MENU CONFIGURACION ACTION LISTENERS
        menuConfiguracion.getTableRowsTextField().addActionListener(this);
        menuConfiguracion.getColumnsTableTextField().addActionListener(this);
        menuConfiguracion.getPlayer1NameTextField().addActionListener(this);
        menuConfiguracion.getPlayer2TextField().addActionListener(this);
        menuConfiguracion.getSaveButton().addActionListener(this);

        //VISIBILIDAD ACTION LISTENERS
        menuInicial.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == menuInicial.getConfigButton()) {
            menuInicial.setVisible(false);
            menuConfiguracion.setVisible(true);
        } else if (source == menuConfiguracion.getSaveButton()) {
            jugador1.setNombre(menuConfiguracion.getPlayer1NameTextField().getText());
            jugador2.setNombre(menuConfiguracion.getPlayer2TextField().getText());
            tablero.setCantidadFilas(Integer.parseInt(menuConfiguracion.getTableRowsTextField().getText()));
            tablero.setCantidadColumnas(Integer.parseInt(menuConfiguracion.getColumnsTableTextField().getText()));
            guardarArchivoDeGuardado();
            menuInicial.setVisible(true);
            menuConfiguracion.setVisible(false);
        } else if (source == menuInicial.getPlayButton()) {
            prepararTablero();
            JOptionPane.showMessageDialog(null, "Turno de ubicar los barcos del Jugador 1");
            menuInicial.setVisible(false);
            tableroVista.setVisible(true);
           
        } else if (botones.contains(source)) {
            JButton boton = botones.get(botones.indexOf(source));
           
            String[] posicionBoton= boton.getName().split(",");
            int botonX = Integer.parseInt(posicionBoton[0]);
            int botonY = Integer.parseInt(posicionBoton[1]);        
            switch (estadoDelJuego) {
                case JUGADOR_1_UBICANDO_BARCOS:
                    if (jugador1.validarInsertarBarco(botonX, botonY)) {
                        boton.setIcon(ship);
                        jugador1.insertarBarco(botonX, botonY);
                        validarEstadoDelJuego();
                    }else{
                        JOptionPane.showMessageDialog(null, "Posicion Invalida");
                    }
                case JUGADOR_2_UBICANDO_BARCOS:
                    if (jugador2.validarInsertarBarco(botonX, botonY)) {
                        boton.setIcon(ship);
                        jugador2.insertarBarco(botonX, botonY);
                        validarEstadoDelJuego();
                    }else{
                        JOptionPane.showMessageDialog(null, "Posicion Invalida");
                    }
                    break;
              
            }
            
            
        }

    }
    
    public void validarEstadoDelJuego() {
        System.out.println(jugador1.getTablero()[0][0].hashCode());
        System.out.println(jugador2.getTablero()[1][0].hashCode());
        
        // SI EL JUGADOR 1 TERMINO DE INGRESAR LOS BARCOS
        if (jugador1.termineDePosicionarBarcos() && estadoDelJuego.equals(EstadoDelJuego.JUGADOR_1_UBICANDO_BARCOS)) {
            JOptionPane.showMessageDialog(null, "Turno de ubicar los barcos del Jugador 2");
            limpiarMapa();
            estadoDelJuego = EstadoDelJuego.JUGADOR_2_UBICANDO_BARCOS;
            return;
        }else if (jugador2.termineDePosicionarBarcos() && estadoDelJuego.equals(EstadoDelJuego.JUGADOR_2_UBICANDO_BARCOS) ) {
            JOptionPane.showMessageDialog(null, "Empieza el ataque entre jugadores");
            limpiarMapa();
        }
        
    }
    
     public void limpiarMapa() {
        for (JButton boton : botones) {
            boton.setIcon(water);
        }
     }

    public void prepararTablero() {
        botones.clear();
        leerArchivoDeGuardado();
        // PARTE VISUAL
        
        for (int i = 0; i < (tablero.getCantidadFilas()); i++) {
            for (int j = 0; j < tablero.getCantidadColumnas(); j++) {
            JButton botonTemporal = new JButton();
            botonTemporal.setIcon(water);
            botonTemporal.setName(i + "," + j);
            botones.add(botonTemporal);
        }
        }
        

        for (JButton boton : botones) {
            boton.addActionListener(this);
            tableroVista.add(boton);
        }

        tableroVista.setLayout(new GridLayout(tablero.getCantidadFilas(), tablero.getCantidadColumnas()));
        tableroVista.setSize(600, 600);
        
 
        
        // LLENAR DE AGUA EL MAPA
        jugador1.setTablero(tablero.getCantidadFilas(), tablero.getCantidadColumnas());
        jugador2.setTablero(tablero.getCantidadFilas(), tablero.getCantidadColumnas());
        jugador1.llenarTableroconAgua(tablero.getCantidadFilas(), tablero.getCantidadColumnas());
        jugador2.llenarTableroconAgua(tablero.getCantidadFilas(), tablero.getCantidadColumnas());
    }

    public void initArchivoDeGuardado() {
        try {
            File myObj = new File("guardadoJuego.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void guardarArchivoDeGuardado() {
        String auxNombres = "";
        auxNombres = jugador1.getNombre() + "," + jugador2.getNombre() + "," + tablero.getCantidadFilas() + "," + tablero.getCantidadColumnas();

        try {
            FileWriter myWriter = new FileWriter("guardadoJuego.txt");
            myWriter.write(auxNombres);
            myWriter.close();
            System.out.println("Successfully wrote to the file." + auxNombres);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public void leerArchivoDeGuardado() {
        String[] textoGuardado;
        try {
            File myObj = new File("guardadoJuego.txt");
            Scanner myReader = new Scanner(myObj);
            String data = myReader.nextLine();
            textoGuardado = data.split(",");
            myReader.close();
            System.out.println(data);
            for (int i = 0; i < textoGuardado.length; i++) {
                switch (i) {
                    case 0:
                        menuConfiguracion.getPlayer1NameTextField().setText(textoGuardado[0]);
                        break;
                    case 1:
                        menuConfiguracion.getPlayer2TextField().setText(textoGuardado[1]);
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

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
