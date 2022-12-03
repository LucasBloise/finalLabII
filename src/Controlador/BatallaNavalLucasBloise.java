/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Controlador;

import Modelo.Jugador;
import Modelo.TableroData;
import Vista.HistorialVista;
import Vista.MenuConfiguracion;
import Vista.MenuInicial;
import Vista.PuntajeVista;
import Vista.Tablero;
import javax.swing.Timer;

/**
 *
 * @author lucasbloise
 */
public class BatallaNavalLucasBloise {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    MenuInicial menuInicial = new MenuInicial();
    MenuConfiguracion menuConfiguracion= new MenuConfiguracion();
    Tablero tableroVista = new Tablero();
    PuntajeVista puntajeVista = new PuntajeVista();
     HistorialVista historialVista = new HistorialVista();
    Jugador jugador1 = new Jugador("Lucas", 0);
    Jugador jugador2 = new Jugador("Agus", 0);
    TableroData tablero = new TableroData(0, 0);
 
    Controlador controlador = new Controlador(menuInicial, menuConfiguracion, tableroVista, puntajeVista,historialVista, jugador1, jugador2, tablero,new Timer(0, null));
    controlador.init();
    }
    
}
