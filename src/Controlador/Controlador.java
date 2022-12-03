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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;

/**
 *
 * @author lucasbloise
 */
public class Controlador implements ActionListener {

    public Controlador(MenuInicial menuInicial, MenuConfiguracion menuConfiguracion, Tablero tableroVista,PuntajeVista puntajeVista,HistorialVista historialVista ,Jugador jugador1, Jugador jugador2, TableroData tablero, Timer timer) {
        this.menuInicial = menuInicial;
        this.menuConfiguracion = menuConfiguracion;
        this.tableroVista = tableroVista;
        this.puntajeVista = puntajeVista;
        this.historialVista = historialVista;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.tablero = tablero;
        this.tiempo = timer;
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
    Timer tiempo;
     int centesimas_segundos = 0;
     int segundos = 0;
     int minutos = 0;
     int horas = 0;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
    LocalDateTime now = LocalDateTime.now();  

    public void init() {
        // Archivos
        initArchivoDeGuardado("preferenciasJuego");
        initArchivoDeGuardado("historial");
        leerArchivoDeGuardadoPreferencias();
        leerArchivoDeGuardadoHistorial("historial");
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
        tiempo.addActionListener(this);
        
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
            guardarArchivoDeGuardadoPreferencias("preferenciasJuego");
            menuInicial.setVisible(true);
            menuConfiguracion.setVisible(false);
        } else if (source == menuInicial.getPlayButton()) {
            prepararTablero();
            JOptionPane.showMessageDialog(null, "Turno de ubicar los barcos de " + jugador1.getNombre());
            menuInicial.setVisible(false);
            tableroVista.setVisible(true);
            

        }else if (source == menuInicial.getHistoryButton()) {
            
            menuInicial.setVisible(false);
            historialVista.setVisible(true);
        } else if (botones.contains(source)) {
            JButton boton = botones.get(botones.indexOf(source));

            String[] posicionBoton = boton.getName().split(",");
            int botonX = Integer.parseInt(posicionBoton[0]);
            int botonY = Integer.parseInt(posicionBoton[1]);
            switch (estadoDelJuego) {
                case JUGADOR_1_UBICANDO_BARCOS:
                    // UBICAR BARCOS JUGADOR 1
                    if (jugador1.validarInsertarBarco(botonX, botonY)) {
                        boton.setIcon(Utilidades.shipIcon);
                        jugador1.insertarBarco(botonX, botonY);
                        validarEstadoDelJuego();
                    } else {
                        JOptionPane.showMessageDialog(null, "Posicion Invalida,ya hay un barco ubicado en esa posicion");
                    }
                    break;
                case JUGADOR_2_UBICANDO_BARCOS:
                    // UBICAR BARCOS JUGADOR 2
                    if (jugador2.validarInsertarBarco(botonX, botonY)) {
                        boton.setIcon(Utilidades.shipIcon);
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

                    } else if (jugador2.validarAtaqueAgua(botonX, botonY)){
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

                    } else if (jugador1.validarAtaqueAgua(botonX, botonY)){
                        JOptionPane.showMessageDialog(null, "Disparo al agua");
                    }
                    if (jugador1.ejecutarAtaque(botonX, botonY)) {
                        JOptionPane.showMessageDialog(null, "Hundiste un Barco!");
                    }
                    validarEstadoDelJuego();
                    break;

            }
             actualizarPuntajeVista();
        }else{
          centesimas_segundos ++;
            if(centesimas_segundos == 100){
                segundos++;
                centesimas_segundos = 0;
            }
            if(segundos == 60){
                minutos ++;
                segundos = 0;
            } 
            if(minutos == 60){
                horas ++;
                minutos = 0;
            }
            if(horas == 24){
                horas = 0;
            }
            
        String texto = (horas<=9?"0":"")+horas+":"+(minutos<=9?"0":"")+minutos+":"+(segundos <= 9?"0":"")+segundos+":"+(centesimas_segundos <=9?"0":"")+centesimas_segundos;
        puntajeVista.getTiempoDePartida().setText("Tiempo de Partida: "+texto);
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
                tiempo.start();
                puntajeVista.setVisible(true);
                JOptionPane.showMessageDialog(null, "Le toca atacar a " + jugador1.getNombre() );
                limpiarMapa();
                estadoDelJuego = EstadoDelJuego.ATAQUE_JUGADOR_1;
            } else if (estadoDelJuego.equals(EstadoDelJuego.ATAQUE_JUGADOR_1)) {
                estadoDelJuego = EstadoDelJuego.ATAQUE_JUGADOR_2;
                JOptionPane.showMessageDialog(null, "Le toca atacar a " + jugador2.getNombre());
                renderizarMapaJugador(jugador1.getTablero());
            } else if (estadoDelJuego.equals(EstadoDelJuego.ATAQUE_JUGADOR_2)) {
                estadoDelJuego = EstadoDelJuego.ATAQUE_JUGADOR_1;
                JOptionPane.showMessageDialog(null, "Le toca atacar a " + jugador1.getNombre() );
                renderizarMapaJugador(jugador2.getTablero());
                 
            }
        }else{
            
            volverAlMenuDeInicio();
        }
    }

    public boolean hayUnGanador() {
        if (estadoDelJuego != EstadoDelJuego.JUGADOR_1_UBICANDO_BARCOS && estadoDelJuego != EstadoDelJuego.JUGADOR_2_UBICANDO_BARCOS) {
            if (!jugador1.meQuedanBarcos()) {
                JOptionPane.showMessageDialog(null, "Gano el jugador 2");
                guardarArchivoDeGuardadoHistorial(jugador1.getNombre() + " vs " + jugador2.getNombre() + " gano " + jugador2.getNombre() +" en " + dtf.format(now));
                return true;
            } else if (!jugador2.meQuedanBarcos()) {
                JOptionPane.showMessageDialog(null, "Gano el jugador 1");
                guardarArchivoDeGuardadoHistorial(jugador1.getNombre() + " vs " + jugador2.getNombre() + " gano " + jugador1.getNombre()+" en " + dtf.format(now));
                return true;
            }
        }
        return false;
    }

    public void limpiarMapa() {
        for (JButton boton : botones) {
            boton.setIcon(Utilidades.waterIcon);
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
                        case AGUA:
                            boton.setIcon(Utilidades.waterIcon);
                            break;
                        case BARCO_HUNDIDO:
                            boton.setIcon(Utilidades.shipDamegeIcon);
                            break;
                        case DISPARO_REPETIDO:
                            boton.setIcon(Utilidades.waterSplashIcon);
                            break;
                    }
                }
            }
        }
            
          
        }
    }
    
     public void volverAlMenuDeInicio() {
        tiempo.stop();
        tableroVista.setVisible(false);
      
        
    }

    public void prepararTablero() {
        botones.clear();
        leerArchivoDeGuardadoPreferencias();
        // PARTE VISUAL

        for (int i = 0; i < (tablero.getCantidadFilas()); i++) {
            for (int j = 0; j < tablero.getCantidadColumnas(); j++) {
                JButton botonTemporal = new JButton();
                botonTemporal.setIcon(Utilidades.waterIcon);
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
    
    
    public void actualizarPuntajeVista(){
           puntajeVista.getPlayer1Disparos().setText("Disparos: " + jugador2.getIntentos());
           puntajeVista.getPlayer1DisparosAgua().setText("Disparos al agua: " + jugador2.getIntentosAgua());
           puntajeVista.getPlayer1DisparosRepetidos().setText("Disparos Repetidos: " + jugador2.getIntentosRepetidos());
           puntajeVista.getPlayer1BarcosHundidos().setText("Barcos Hundidos: " + jugador2.getBarcosHundidos());
         
           puntajeVista.getPlayer2Disparos().setText("Disparos: " + jugador1.getIntentos());
           puntajeVista.getPlayer2DisparosAgua().setText("Disparos al agua: " + jugador1.getIntentosAgua());
           puntajeVista.getPlayer2DisparosRepetidos().setText("Disparos Repetidos: " + jugador1.getIntentosRepetidos());
        puntajeVista.getPlayer2BarcosHundidos().setText("Barcos Hundidos: " + jugador1.getBarcosHundidos());

    }

    public void initArchivoDeGuardado(String fileName) {
        try {
            File myObj = new File("src" + File.separator + fileName + ".txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                leerArchivoDeGuardadoPreferencias();
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
  

    public void guardarArchivoDeGuardadoPreferencias(String fileName) {
        String auxNombres = "";
        auxNombres = jugador1.getNombre() + "," + jugador2.getNombre() + "," + tablero.getCantidadFilas() + "," + tablero.getCantidadColumnas();

        try {
            FileWriter myWriter = new FileWriter("src" + File.separator + fileName + ".txt");
            myWriter.write(auxNombres);
            myWriter.close();
            System.out.println("Successfully wrote to the file." + auxNombres);
        } catch (IOException e) {
            System.out.println("An error occurred.");
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
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("src" + File.separator + "historial" + ".txt");
            myWriter.write(textoGuardado + "," + resultado);
            myWriter.close();
            System.out.println("Successfully wrote to the file." + resultado);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
    
    public void leerArchivoDeGuardadoHistorial(String fileName) {
        String[] textoGuardado;
        historialVista.getHistorialLista().removeAll();
        try {
            File myObj = new File("src" + File.separator + fileName + ".txt");
            Scanner myReader = new Scanner(myObj);

            if (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                textoGuardado = data.split(",");
                myReader.close();
               
                
                historialVista.getHistorialLista().setListData(textoGuardado);
               
            }

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
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
            }

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
