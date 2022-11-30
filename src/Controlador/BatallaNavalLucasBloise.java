/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Controlador;

import Modelo.Jugador;
import Modelo.TableroData;
import Vista.MenuConfiguracion;
import Vista.MenuInicial;
import Vista.Tablero2;

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
    Tablero2 tableroVista = new Tablero2();
    Jugador jugador1 = new Jugador("Lucas", 0);
    Jugador jugador2 = new Jugador("Agus", 0);
    TableroData tablero = new TableroData(0, 0);
 
    Controlador controlador = new Controlador(menuInicial, menuConfiguracion, tableroVista, jugador1, jugador2, tablero);
    controlador.init();
    }
    
}
