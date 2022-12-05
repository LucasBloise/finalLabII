/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Recursos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 *
 * @author lucasbloise
 */
public class Utilidades {
    
    private static final ImageIcon waterIcon = new ImageIcon("/Users/lucasbloise/NetBeansProjects/BatallaNavalLucasBloise/src/Recursos/water.png");
    private static final ImageIcon shipIcon = new ImageIcon("/Users/lucasbloise/NetBeansProjects/BatallaNavalLucasBloise/src/Recursos/ship.png");
    private static final ImageIcon shipDamegeIcon = new ImageIcon("/Users/lucasbloise/NetBeansProjects/BatallaNavalLucasBloise/src/Recursos/shipDamage.png");
    private static final ImageIcon waterSplashIcon = new ImageIcon("/Users/lucasbloise/NetBeansProjects/BatallaNavalLucasBloise/src/Recursos/waterSplash.png");
    private static final LocalDateTime now = LocalDateTime.now();
    private static final Timer tiempo = new Timer(0, null);
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static ImageIcon getWaterIcon() {
        return waterIcon;
    }

    public static ImageIcon getShipIcon() {
        return shipIcon;
    }

    public static ImageIcon getShipDamegeIcon() {
        return shipDamegeIcon;
    }

    public static ImageIcon getWaterSplashIcon() {
        return waterSplashIcon;
    }

    public static LocalDateTime getNow() {
        return now;
    }

    public static Timer getTiempo() {
        return tiempo;
    }

    public static DateTimeFormatter getDtf() {
        return dtf;
    }
}
