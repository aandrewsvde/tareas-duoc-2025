/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package s4teatromoro;

/**
 *
 * @author Agustín Andrews
 */
import java.util.Scanner;
public class S4TeatroMoro {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // Bienvenida al usuario
        System.out.println("Bienvenido al sistema de venta de entradas del teatro moro");
        
        boolean continuar = true;
        // Variables para acumular compras
        int totalAcumulado = 0;
        StringBuilder detalleCompras = new StringBuilder();
        int cantidadEntradas = 0;
        
        while(continuar) {
            // Menú principal
            System.out.println("\n----- MENÚ PRINCIPAL -----");
            System.out.println("1. Comprar entrada");
            System.out.println("2. Finalizar compra");
            System.out.print("Ingrese su opción: ");
            
            // Validar entrada del usuario para el menú
            int opcion = 0;
            boolean opcionIngresadaValida = false;
            while(!opcionIngresadaValida) {
                try {
                    opcion = Integer.parseInt(sc.nextLine());
                    if(opcion == 1 || opcion == 2) {
                        opcionIngresadaValida = true;
                    } else {
                        System.out.print("Opción no válida. Ingrese 1 o 2: ");
                    }
                } catch (NumberFormatException e) {
                    System.out.print("Entrada inválida. Ingrese un número: ");
                }
            }
            
            // Usuario no quiere comprar una entrada
            if(opcion == 2) {
                continuar = false;
                if (cantidadEntradas > 0) {
                    // Mostrar resumen final de todas las compras
                    System.out.println("\n===== RESUMEN FINAL DE COMPRA =====");
                    System.out.println("Cantidad de entradas: " + cantidadEntradas);
                    System.out.println("\nDETALLE DE ENTRADAS:");
                    System.out.println(detalleCompras.toString()); // !!! TODO: REVISAR ESTO
                    System.out.println("\nTOTAL A PAGAR: $" + totalAcumulado);
                    System.out.println("¡Gracias por su compra, disfrute la función!");
                } else {
                    System.out.println("Gracias por utilizar nuestro sistema.");
                }
            // Usuario quiere comprar una entrada
            } else {
                // Mostrar plano del teatro
                System.out.println("\n----- PLANO DEL TEATRO -----");
                System.out.println("1. VIP (Precio base: $30000)");
                System.out.println("2. Platea baja (Precio base: $15000)");
                System.out.println("3. Platea alta (Precio base: $18000)");
                System.out.println("4. Palcos (Precio base: $13000)");
                
                // Solicitar ubicación
                System.out.print("\nSeleccione la zona (1-4): ");
                int opcionZona = 0;
                int precioBase = 0;
                boolean zonaIngresadaValida = false;
                
                while(!zonaIngresadaValida) {
                    try {
                        opcionZona = Integer.parseInt(sc.nextLine());
                        if(opcionZona >= 1 && opcionZona <= 4) {
                            zonaIngresadaValida = true;
                            switch(opcionZona) {
                                case 1: // VIP
                                    precioBase = 30000;
                                    break;
                                case 2: // Platea baja
                                    precioBase = 15000;
                                    break;
                                case 3: // Platea alta
                                    precioBase = 18000;
                                    break;
                                case 4: // Palcos
                                    precioBase = 13000;
                                    break;
                            }
                        } else {
                            System.out.print("Zona no válida. Ingrese un número entre 1 y 4: ");
                        }
                    } catch (NumberFormatException e) {
                        System.out.print("Entrada inválida. Ingrese un número: ");
                    }
                }
                
                // Solicitar edad para descuentos
                System.out.print("\nIngrese su edad: ");
                int edad = 0;
                boolean edadValida = false;
                
                while(!edadValida) {
                    try {
                        edad = Integer.parseInt(sc.nextLine());
                        if(edad > 0 && edad < 120) {
                            edadValida = true;
                        } else {
                            System.out.print("Edad no válida. Ingrese un valor entre 1 y 120: ");
                        }
                    } catch (NumberFormatException e) {
                        System.out.print("Entrada inválida. Ingrese un número: ");
                    }
                }
                
                // Calcular descuento
                double descuento = 0;
                String tipoDescuento = "Sin descuento";
                
                if(edad >= 60) {
                    descuento = 0.15;  // 15% para tercera edad
                    tipoDescuento = "Descuento para adulto mayor (15%)";
                } else {
                    System.out.print("¿Es usted estudiante? (S/N): ");
                    String respuestaEsEstudiante;
                    boolean respuestaValida = false;
                    
                    while(!respuestaValida) {
                        respuestaEsEstudiante = sc.nextLine().toUpperCase();
                        if(respuestaEsEstudiante.equals("S") || respuestaEsEstudiante.equals("N")) {
                            respuestaValida = true;
                            if(respuestaEsEstudiante.equals("S")) {
                                descuento = 0.10;  // 10% para estudiantes
                                tipoDescuento = "Descuento para estudiantes (10%)";
                            }
                        } else {
                            System.out.print("Respuesta no válida. Ingrese S o N: ");
                        }
                    }
                }
                
                int montoDescuento = (int)(precioBase * descuento);
                int precioFinal = precioBase - montoDescuento;
                
                // Actualizar totales acumulados
                totalAcumulado += precioFinal;
                cantidadEntradas++;
                
                // Determinar el nombre de la zona
                String nombreZona;
                switch(opcionZona) {
                    case 1: 
                        nombreZona = "VIP";
                        break;
                    case 2:
                        nombreZona = "Platea baja";
                        break;
                    case 3:
                        nombreZona = "Platea alta";
                        break;
                    case 4:
                        nombreZona = "Palcos";
                        break;
                    default: 
                        nombreZona = "Desconocida";
                }
                
                // Guardar detalles de esta entrada
                detalleCompras.append("Entrada #").append(cantidadEntradas).append(": ")
                              .append(nombreZona).append(" - Precio: $").append(precioFinal)
                              .append(" (").append(tipoDescuento).append(")\n");
                
                // Mostrar resumen de la entrada actual
                System.out.println("\n----- ENTRADA AGREGADA -----");
                System.out.println("Ubicación: " + nombreZona);
                System.out.println("Precio base: $" + precioBase);
                System.out.println(tipoDescuento + ": $" + montoDescuento);
                System.out.println("Precio de esta entrada: $" + precioFinal);
                
                // Mostrar resumen acumulado
                System.out.println("\n----- RESUMEN ACTUAL -----");
                System.out.println("Entradas compradas: " + cantidadEntradas);
                System.out.println("Total a pagar: $" + totalAcumulado);
                
                // Preguntar si desea agregar más entradas
                System.out.print("\n¿Deseas agregar más entradas a tu compra? (S/N): ");
                String respuestaMasEntradas;
                boolean respuestaValida = false;
                
                while(!respuestaValida) {
                    respuestaMasEntradas = sc.nextLine().toUpperCase();
                    if(respuestaMasEntradas.equals("S") || respuestaMasEntradas.equals("N")) {
                        respuestaValida = true;
                        continuar = respuestaMasEntradas.equals("S");
                        if(respuestaMasEntradas.equals("N")) {
                            System.out.print("\nMuchas gracias por su compra, que disfrutes la función");
                        }
                    } else {
                        System.out.print("Respuesta no válida. Ingrese S o N: ");
                    }
                }
            }
        }

        sc.close();
    }
}
