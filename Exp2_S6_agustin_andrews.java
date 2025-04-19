/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package s6teatromoro;
import java.util.Scanner;

/**
 *
 * @author aandr
 */
public class S6TeatroMoro {        
    static String reservaUbicacion = "";
    static int reservaAsientoNum = 0;
    static int reservaPrecio = 0;
    static boolean reservaActiva = false;
    
    static boolean vip1 = false, vip2 = false, vip3 = false, vip4 = false, vip5 = false;
    static boolean plateaBaja1 = false, plateaBaja2 = false, plateaBaja3 = false, plateaBaja4 = false, plateaBaja5 = false;
    static boolean plateaAlta1 = false, plateaAlta2 = false, plateaAlta3 = false, plateaAlta4 = false, plateaAlta5 = false;
    static boolean palcos1 = false, palcos2 = false, palcos3 = false, palcos4 = false, palcos5 = false;

    static String boletosVIP = "";
    static String boletosPlateaBaja = "";
    static String boletosPlateaAlta = "";
    static String boletosPalcos = "";
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenido al sistema del Teatro Moro");
        while(true) {
            System.out.println("\n----- MENÚ PRINCIPAL -----");
            System.out.println("1. Reservar entrada");
            System.out.println("2. Comprar entrada");
            System.out.println("3. Modificar venta");
            System.out.println("4. Imprimir boleto");
            System.out.println("5. Salir");
            System.out.print("Seleccione opción: ");
            
            int opcion = validarEntero(sc, 1, 5);
            
            switch(opcion) {
                case 1 -> reservarEntrada(sc);
                case 2 -> comprarEntrada(sc);
                case 3 -> modificarVenta(sc);
                case 4 -> imprimirBoleto();
                case 5 -> {
                    System.out.println("Gracias por usar nuestro sistema. ¡Hasta pronto!");
                    sc.close();
                    return;
                }
            }
        }
    }

    private static void reservarAsiento(String asiento) {
        String[] partes = asiento.split("-");
        String zona = partes[0];
        int numero = Integer.parseInt(partes[1]);
        
        switch(zona) {
            case "VIP" -> {
                switch(numero) {
                    case 1 -> vip1 = true;
                    case 2 -> vip2 = true;
                    case 3 -> vip3 = true;
                    case 4 -> vip4 = true;
                    case 5 -> vip5 = true;
                }
            }
            case "PLATEA BAJA" -> {
                switch(numero) {
                    case 1 -> plateaBaja1 = true;
                    case 2 -> plateaBaja2 = true;
                    case 3 -> plateaBaja3 = true;
                    case 4 -> plateaBaja4 = true;
                    case 5 -> plateaBaja5 = true;
                }
            }
            case "PLATEA ALTA" -> {
                switch(numero) {
                    case 1 -> plateaAlta1 = true;
                    case 2 -> plateaAlta2 = true;
                    case 3 -> plateaAlta3 = true;
                    case 4 -> plateaAlta4 = true;
                    case 5 -> plateaAlta5 = true;
                }
            }
            case "PALCOS" -> {
                switch(numero) {
                    case 1 -> palcos1 = true;
                    case 2 -> palcos2 = true;
                    case 3 -> palcos3 = true;
                    case 4 -> palcos4 = true;
                    case 5 -> palcos5 = true;
                }
            }
        }
        reservaUbicacion = zona;
        reservaAsientoNum = numero;
        reservaPrecio = calcularPrecio(asiento);
    }

    private static int calcularPrecio(String asiento) {
        String zona = asiento.split("-")[0];
        return switch (zona) {
            case "VIP" -> 30000;
            case "PLATEA BAJA" -> 15000;
            case "PLATEA ALTA" -> 18000;
            case "PALCOS" -> 13000;
            default -> 0;
        };
    }
    
    private static void guardarDatosVenta(String asiento, int precio, int precioOriginal) {
        String[] partes = asiento.split("-");
        String zona = partes[0];
        int numero = Integer.parseInt(partes[1]);

        // Calcular el porcentaje de descuento
        int descuento = 100 - (precio * 100 / precioOriginal);
        String descuentoInfo = (descuento > 0) ? " (Descuento: " + descuento + "%, Original: $" + precioOriginal + ")" : "";

        String boleto = "Asiento: " + asiento + ", Precio: $" + precio + descuentoInfo + "\n";

        switch (zona) {
            case "VIP" -> boletosVIP += boleto;
            case "PLATEA BAJA" -> boletosPlateaBaja += boleto;
            case "PLATEA ALTA" -> boletosPlateaAlta += boleto;
            case "PALCOS" -> boletosPalcos += boleto;
        }

        reservaAsientoNum = numero;
    }


    private static void liberarAsiento(String asiento) {
        String[] partes = asiento.split("-");
        String zona = partes[0];
        int numero = Integer.parseInt(partes[1]);
        
        switch(zona) {
            case "VIP" -> {
                switch(numero) {
                    case 1 -> vip1 = false;
                    case 2 -> vip2 = false;
                    case 3 -> vip3 = false;
                    case 4 -> vip4 = false;
                    case 5 -> vip5 = false;
                }
            }
            case "PLATEA BAJA" -> {
                switch(numero) {
                    case 1 -> plateaBaja1 = false;
                    case 2 -> plateaBaja2 = false;
                    case 3 -> plateaBaja3 = false;
                    case 4 -> plateaBaja4 = false;
                    case 5 -> plateaBaja5 = false;
                }
            }
            case "PLATEA ALTA" -> {
                switch(numero) {
                    case 1 -> plateaAlta1 = false;
                    case 2 -> plateaAlta2 = false;
                    case 3 -> plateaAlta3 = false;
                    case 4 -> plateaAlta4 = false;
                    case 5 -> plateaAlta5 = false;
                }
            }
            case "PALCOS" -> {
                switch(numero) {
                    case 1 -> palcos1 = false;
                    case 2 -> palcos2 = false;
                    case 3 -> palcos3 = false;
                    case 4 -> palcos4 = false;
                    case 5 -> palcos5 = false;
                }
            }
        }
    }

    private static void confirmarCompraReserva() {
        reservarAsiento(reservaUbicacion + "-" + reservaAsientoNum);

        int precioOriginal = calcularPrecio(reservaUbicacion + "-" + reservaAsientoNum);

        guardarDatosVenta(reservaUbicacion + "-" + reservaAsientoNum, reservaPrecio, precioOriginal);
        reservaActiva = false;
        System.out.println("Reserva convertida a compra exitosamente!");
    }

    private static void reservarEntrada(Scanner sc) {
        System.out.println("\n--- RESERVA DE ENTRADAS ---");

        // Verificar si ya hay una reserva activa
        if (reservaActiva) {
            System.out.println("Ya tiene una reserva activa:");
            System.out.println("Zona: " + reservaUbicacion + ", Asiento: " + reservaAsientoNum);
            System.out.print("¿Desea sobrescribir esta reserva? (S/N): ");
            String respuesta = sc.nextLine().toUpperCase();

            if (!respuesta.equals("S")) {
                System.out.println("Por favor, transforme la reserva en compra antes de realizar otra reserva.");
                return;
            }
            // Liberar el asiento previamente reservado
            liberarAsiento(reservaUbicacion + "-" + reservaAsientoNum);
            reservaActiva = false; // Marcar la reserva como no activa temporalmente
        }

        mostrarAsientosDisponibles();

        // Elegir zona
        System.out.print("Seleccione zona (1. VIP, 2. PLATEA BAJA, 3. PLATEA ALTA, 4. PALCOS): ");
        int numeroZona = validarEntero(sc, 1, 4);
        String zona = obtenerZonaPorNumero(numeroZona);

        // Elegir asiento
        System.out.print("Seleccione número de asiento (1-5): "); // Revisar zona en punto de depuración
        int numeroAsiento = validarEntero(sc, 1, 5);

        String asiento = zona + "-" + numeroAsiento;

        if (verificarDisponibilidad(asiento)) {
            // Calcular el precio original y aplicar descuentos
            int precioOriginal = calcularPrecio(asiento);
            int precioConDescuento = aplicarDescuentos(sc, precioOriginal);

            reservarAsiento(asiento);
            reservaPrecio = precioConDescuento;
            System.out.println("Asiento reservado exitosamente!");
            reservaActiva = true;
        } else {
            System.out.println("Asiento no disponible!");
        }
    }


    private static void comprarEntrada(Scanner sc) {
        System.out.println("\n--- COMPRA DE ENTRADAS ---");

        if (reservaActiva) {
            System.out.println("Tienes una reserva:");
            System.out.println("Zona: " + reservaUbicacion + ", Asiento: " + reservaAsientoNum);
            System.out.print("Desea convertir reserva en compra? (S/N): ");
            String respuesta = sc.nextLine().toUpperCase();

            if (respuesta.equals("S")) {
                confirmarCompraReserva();
                return;
            }
        }

        mostrarAsientosDisponibles();

        // Elegir zona
        System.out.print("Seleccione zona (1. VIP, 2. PLATEA BAJA, 3. PLATEA ALTA, 4. PALCOS): ");
        int numeroZona = validarEntero(sc, 1, 4);
        String zona = obtenerZonaPorNumero(numeroZona);

        // Elegir asiento
        System.out.print("Seleccione número de asiento (1-5): ");
        int numeroAsiento = validarEntero(sc, 1, 5);

        String asiento = zona + "-" + numeroAsiento;

        if (!verificarDisponibilidad(asiento)) {
            System.out.println("Asiento no disponible. Seleccione otro.");
            return;
        }

        int precioOriginal = calcularPrecio(asiento);
        int precio = aplicarDescuentos(sc, precioOriginal);

        System.out.println("Precio final: $" + precio);
        System.out.print("Confirmar compra? (S/N): ");
        String confirmacion = sc.nextLine().toUpperCase();

        if (confirmacion.equals("S")) {
            reservarAsiento(asiento);
            guardarDatosVenta(asiento, precio, precioOriginal);
            System.out.println("Compra realizada exitosamente!");
        }
    }

    
    private static void modificarVenta(Scanner sc) {
        System.out.println("\n--- MODIFICAR VENTA ---");
        System.out.println("Seleccione la zona del boleto que desea devolver:");
        System.out.println("1. VIP");
        System.out.println("2. Platea Baja");
        System.out.println("3. Platea Alta");
        System.out.println("4. Palcos");
        System.out.print("Ingrese opción: ");
        int zonaSeleccionada = validarEntero(sc, 1, 4);
        String zona = obtenerZonaPorNumero(zonaSeleccionada);

        System.out.print("Ingrese el número del asiento que desea devolver (1-5): ");
        int numeroAsiento = validarEntero(sc, 1, 5);
        String asiento = zona + "-" + numeroAsiento;

        // Verificar si el boleto existe en la zona seleccionada
        switch (zona) {
            case "VIP" -> {
                if (boletosVIP.contains(asiento)) {
                    boletosVIP = boletosVIP.replaceFirst("Asiento: " + asiento + ".*\\n", "");
                    liberarAsiento(asiento);
                    System.out.println("Boleto devuelto exitosamente.");
                } else {
                    System.out.println("El boleto no existe en esta zona.");
                }
            }
            case "PLATEA BAJA" -> {
                if (boletosPlateaBaja.contains(asiento)) {
                    boletosPlateaBaja = boletosPlateaBaja.replaceFirst("Asiento: " + asiento + ".*\\n", "");
                    liberarAsiento(asiento);
                    System.out.println("Boleto devuelto exitosamente.");
                } else {
                    System.out.println("El boleto no existe en esta zona.");
                }
            }
            case "PLATEA ALTA" -> {
                if (boletosPlateaAlta.contains(asiento)) {
                    boletosPlateaAlta = boletosPlateaAlta.replaceFirst("Asiento: " + asiento + ".*\\n", "");
                    liberarAsiento(asiento);
                    System.out.println("Boleto devuelto exitosamente.");
                } else {
                    System.out.println("El boleto no existe en esta zona.");
                }
            }
            case "PALCOS" -> {
                if (boletosPalcos.contains(asiento)) {
                    boletosPalcos = boletosPalcos.replaceFirst("Asiento: " + asiento + ".*\\n", "");
                    liberarAsiento(asiento);
                    System.out.println("Boleto devuelto exitosamente.");
                } else {
                    System.out.println("El boleto no existe en esta zona.");
                }
            }
            default -> System.out.println("Zona no válida.");
        }
    }
    
    private static void imprimirBoleto() {
        System.out.println("\n--- BOLETOS ELECTRÓNICOS ---");
        int totalCompra = 0; // Variable para acumular el total de la compra

        if (!boletosVIP.isEmpty()) {
            System.out.println("Zona VIP:");
            System.out.println(boletosVIP);
            totalCompra += calcularTotalZona(boletosVIP);
        }
        if (!boletosPlateaBaja.isEmpty()) {
            System.out.println("Zona Platea Baja:");
            System.out.println(boletosPlateaBaja);
            totalCompra += calcularTotalZona(boletosPlateaBaja);
        }
        if (!boletosPlateaAlta.isEmpty()) {
            System.out.println("Zona Platea Alta:");
            System.out.println(boletosPlateaAlta);
            totalCompra += calcularTotalZona(boletosPlateaAlta);
        }
        if (!boletosPalcos.isEmpty()) {
            System.out.println("Zona Palcos:");
            System.out.println(boletosPalcos);
            totalCompra += calcularTotalZona(boletosPalcos);
        }
        System.out.println("---------------------------");
        System.out.println("Total de la compra: $" + totalCompra);
    }

    private static boolean verificarOcupado(String zona, int numero) {
        switch(zona) {
            case "VIP":
                switch(numero) {
                    case 1 -> {
                        return vip1;
                }
                    case 2 -> {
                        return vip2;
                }
                    case 3 -> {
                        return vip3;
                }
                    case 4 -> {
                        return vip4;
                }
                    case 5 -> {
                        return vip5;
                }
                }

            case "PLATEA BAJA":
                switch(numero) {
                    case 1 -> {
                        return plateaBaja1;
                }
                    case 2 -> {
                        return plateaBaja2;
                }
                    case 3 -> {
                        return plateaBaja3;
                }
                    case 4 -> {
                        return plateaBaja4;
                }
                    case 5 -> {
                        return plateaBaja5;
                }
                }

            case "PLATEA ALTA":
                switch(numero) {
                    case 1 -> {
                        return plateaAlta1;
                }
                    case 2 -> {
                        return plateaAlta2;
                }
                    case 3 -> {
                        return plateaAlta3;
                }
                    case 4 -> {
                        return plateaAlta4;
                }
                    case 5 -> {
                        return plateaAlta5;
                }
                }

            case "PALCOS":
                switch(numero) {
                    case 1 -> {
                        return palcos1;
                }
                    case 2 -> {
                        return palcos2;
                }
                    case 3 -> {
                        return palcos3;
                }
                    case 4 -> {
                        return palcos4;
                }
                    case 5 -> {
                        return palcos5;
                }
                }

        }
        return true;
    }

    // Métodos auxiliares

    private static String obtenerZonaPorNumero(int numeroZona) {
        return switch (numeroZona) {
            case 1 -> "VIP";
            case 2 -> "PLATEA BAJA";
            case 3 -> "PLATEA ALTA";
            case 4 -> "PALCOS";
            default -> "";
        };
    }

    private static int validarEntero(Scanner sc, int min, int max) {
        // Ciclo infinito, sólo se sale de el cuando usuario ingresa un número válido
        while(true) {
            try {
                int valor = Integer.parseInt(sc.nextLine());
                if(valor >= min && valor <= max) return valor;
                System.out.print("Valor fuera de rango. Reingrese: ");
            } catch(NumberFormatException e) {
                System.out.print("Entrada inválida. Ingrese número: ");
            }
        }
    }
    
    private static void mostrarAsientosDisponibles() {
        System.out.println("\nAsientos disponibles:");
        System.out.println("1. VIP: " + (!vip1 ? "1 " : "") + (!vip2 ? "2 " : "") + (!vip3 ? "3 " : "") + (!vip4 ? "4 " : "") + (!vip5 ? "5 " : ""));
        System.out.println("2. Platea Baja: " + (!plateaBaja1 ? "1 " : "") + (!plateaBaja2 ? "2 " : "") + (!plateaBaja3 ? "3 " : "") + (!plateaBaja4 ? "4 " : "") + (!plateaBaja5 ? "5 " : ""));
        System.out.println("3. Platea Alta: " + (!plateaAlta1 ? "1 " : "") + (!plateaAlta2 ? "2 " : "") + (!plateaAlta3 ? "3 " : "") + (!plateaAlta4 ? "4 " : "") + (!plateaAlta5 ? "5 " : ""));
        System.out.println("4. Palcos: " + (!palcos1 ? "1 " : "") + (!palcos2 ? "2 " : "") + (!palcos3 ? "3 " : "") + (!palcos4 ? "4 " : "") + (!palcos5 ? "5 " : ""));
    }
    private static boolean verificarDisponibilidad(String asiento) {
        String[] partes = asiento.split("-");
        String zona = partes[0];
        int numero = Integer.parseInt(partes[1]);

        return !verificarOcupado(zona, numero);
    }

    private static int aplicarDescuentos(Scanner sc, int precio) {
        System.out.print("Ingrese edad: ");
        int edad = validarEntero(sc, 1, 120);
        
        if(edad >= 60) {
            precio *= 0.85;
            System.out.println("Aplicado descuento tercera edad (15%)");
        } else {
            System.out.print("Es estudiante? (S/N): ");
            String respuesta = sc.nextLine().toUpperCase();
            if(respuesta.equals("S")) {
                precio *= 0.90;
                System.out.println("Aplicado descuento estudiante (10%)");
            }
        }
        return precio;
    }

    private static int calcularTotalZona(String boletos) {
        int total = 0;
        String[] lineas = boletos.split("\n");
        for (String linea : lineas) {
            if (linea.contains("Precio: $")) {
                // Extraer solo el valor numérico del precio
                String precioStr = linea.substring(linea.indexOf("Precio: $") + 9).split(" ")[0].trim();
                total += Integer.parseInt(precioStr);
            }
        }
        return total;
    }
}
