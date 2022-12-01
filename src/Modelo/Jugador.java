/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Recursos.PosicionTablero;

/**
 *
 * @author lucasbloise
 */
public class Jugador {

    public Jugador(String nombre, int puntaje) {
        this.nombre = nombre;
        this.puntaje = puntaje;
    }

    private String nombre;
    private int puntaje;
    private PosicionTablero[][] tablero;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }
    
    public PosicionTablero[][] getTablero() {
        return tablero;
    }

    public void setTablero(int cantidadFilas,int CantidadColumnas) {
        this.tablero = new PosicionTablero[cantidadFilas][CantidadColumnas];
    }
    
    public void llenarTableroconAgua(int cantidadFilas,int CantidadColumnas) {
        for (int i = 0; i < cantidadFilas; i++) {
            for (int j = 0; j < CantidadColumnas; j++) {
                this.tablero[i][j] = PosicionTablero.AGUA;
            }
        }
    }

    public void insertarBarco(int posicionX, int posicionY) {
        this.tablero[posicionX][posicionY] = PosicionTablero.BARCO;
    }
    
     public boolean validarInsertarBarco(int posicionX, int posicionY) {  
         return tablero[posicionX][posicionY] != PosicionTablero.BARCO;
    }
     
    public boolean termineDePosicionarBarcos() {  
      int contadorBarcos = 0;
      boolean haTerminado = false;
         for (int i = 0; i < TableroData.getCantidadFilas(); i++) {
            for (int j = 0; j < TableroData.getCantidadColumnas(); j++) {
                if (tablero[i][j] == PosicionTablero.BARCO) {
                    contadorBarcos += 1;
                }
            }
        }
        if (contadorBarcos == 2) {
            contadorBarcos = 0;
            return true;
                 
        }else{
            return false;
        }
 
    }
    
     public boolean validarAtaque(int posicionX, int posicionY) {  
         // True es que le pego a un barco, false le pego al Agua
         if (tablero[posicionX][posicionY] == PosicionTablero.BARCO) {
             tablero[posicionX][posicionY] = PosicionTablero.BARCO_HUNDIDO;
             return true;
         }else {
             tablero[posicionX][posicionY] = PosicionTablero.DISPARO_REPETIDO;
             return false;
         }     
    }
     
    public boolean meQuedanBarcos() {  
         // True es que le quedan barcos, false no quedan barcos
       for (int i = 0; i < TableroData.getCantidadFilas(); i++) {
            for (int j = 0; j < TableroData.getCantidadColumnas(); j++) {
                if (tablero[i][j] == PosicionTablero.BARCO) {
                    return true;
                }
            }
        }    
       return false;
    }
}
