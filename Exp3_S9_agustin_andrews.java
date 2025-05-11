/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package s9teatromoro;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Sistema de gestión para el Teatro Moro
 * @author aandr
 */
public class S9TeatroMoro {
    // ===== CONSTANTES Y VARIABLES DE INSTANCIA =====
    private static final int MAX_CAPACIDAD = 1000; // Capacidad máxima para los arreglos
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Estructuras de datos principales
    private final Cliente[] clientes;
    private final Asiento[] asientos;
    private final Venta[] ventas;
    private final List<Descuento> descuentos;
    private final List<Reserva> reservas;

    // Contadores para los arreglos
    private int numClientes;
    private int numVentas;
    private int nextVentaId;
    private int nextReservaId;
    private int nextClienteId;
    
    // Configuración
    private int tiempoExpiracionReserva;
    
    // ===== CONSTRUCTOR =====
    public S9TeatroMoro(int capacidadAsientos) {
        // Inicialización de arreglos
        clientes = new Cliente[MAX_CAPACIDAD];
        asientos = new Asiento[capacidadAsientos];
        ventas = new Venta[MAX_CAPACIDAD];
        
        // Inicialización de listas
        descuentos = new ArrayList<>();
        reservas = new ArrayList<>();

        // Inicialización de contadores
        numClientes = 0;
        numVentas = 0;
        nextVentaId = 1;
        nextReservaId = 1;
        nextClienteId = 1;
        
        // Configuración inicial
        tiempoExpiracionReserva = 15; // 15 minutos por defecto

        // Inicializar asientos con categorías
        inicializarAsientos(capacidadAsientos);
        
        // Inicializar descuentos base
        inicializarDescuentos();
    }
    
    // ===== MÉTODOS DE INICIALIZACIÓN =====
    private void inicializarAsientos(int capacidadAsientos) {
        String[] categorias = {"VIP", "Palco", "Platea Baja", "Platea Alta", "Galería"};
        double[] preciosBase = {10000.0, 8000.0, 6000.0, 5000.0, 4000.0};

        for (int i = 0; i < capacidadAsientos; i++) {
            // Determinar categoría basada en posición
            int categoriaIndex = i / (capacidadAsientos / 5);
            if (categoriaIndex >= 5) categoriaIndex = 4; // Por si acaso
            
            String categoria = categorias[categoriaIndex];
            double precioBase = preciosBase[categoriaIndex];
            
            asientos[i] = new Asiento(
                i + 1, 
                "Fila " + ((i / 10) + 1) + ", Asiento " + ((i % 10) + 1),
                categoria,
                precioBase
            );
        }
    }
    
    private void inicializarDescuentos() {
        descuentos.add(new Descuento("Niño", 0.10, cliente -> cliente.getEdad() < 12));
        descuentos.add(new Descuento("Mujer", 0.20, cliente -> "Femenino".equalsIgnoreCase(cliente.getGenero())));
        descuentos.add(new Descuento("Estudiante", 0.15, Cliente::isEstudiante));
        descuentos.add(new Descuento("Tercera Edad", 0.25, cliente -> cliente.getEdad() >= 65));
    }
    
    // ===== GESTIÓN DE CLIENTES =====
    public boolean agregarCliente(Cliente cliente) {
        if (cliente == null || cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            System.err.println("Error: Datos del cliente inválidos.");
            return false;
        }
        if (numClientes >= MAX_CAPACIDAD) {
            System.err.println("Error: Capacidad máxima de clientes alcanzada.");
            return false;
        }

        // Validar si el cliente ya existe (por ID)
        if (buscarClientePorId(cliente.getIdCliente()) != null) {
            System.err.println("Error: Cliente con ID " + cliente.getIdCliente() + " ya existe.");
            return false;
        }

        // Asignamos el siguiente ID disponible automáticamente
        cliente.setIdCliente(nextClienteId++);

        clientes[numClientes++] = cliente;
        System.out.println("Cliente agregado: " + cliente);
        return true;
    }

    public Cliente buscarClientePorId(int idCliente) {
        for (int i = 0; i < numClientes; i++) {
            if (clientes[i] != null && clientes[i].getIdCliente() == idCliente) {
                return clientes[i];
            }
        }
        return null; // No encontrado
    }
    
    public void mostrarClientes() {
        System.out.println("\n--- Lista de Clientes ---");
        boolean hayClientes = false;
        for (int i = 0; i < numClientes; i++) {
            if (clientes[i] != null) {
                System.out.println(clientes[i]);
                hayClientes = true;
            }
        }
        if (!hayClientes) {
            System.out.println("No hay clientes registrados.");
        }
    }
    
    // ===== GESTIÓN DE ASIENTOS =====
    public Asiento buscarAsientoPorId(int idAsiento) {
        // Los IDs de asiento van de 1 a capacidadAsientos
        if (idAsiento < 1 || idAsiento > asientos.length) {
            return null; // ID fuera de rango
        }
        // El índice del arreglo es idAsiento - 1
        if (asientos[idAsiento - 1] != null) {
            return asientos[idAsiento - 1];
        }
        return null; // No debería pasar si se inicializó correctamente
    }
    
    public void mostrarAsientosDisponibles() {
        // Verificar reservas expiradas antes de mostrar asientos disponibles
        verificarReservasExpiradas();
        
        // Verificar si hay asientos disponibles
        boolean hayDisponibles = false;
        for (Asiento asiento : asientos) {
            if (asiento != null && asiento.isDisponible()) {
                hayDisponibles = true;
                mostrarAsientosDisponiblesVisual();
                break;
            }
        }
        if (!hayDisponibles) {
            System.out.println("No hay asientos disponibles.");
        }
    }
    
    public void mostrarAsientosDisponiblesVisual() {
        System.out.println("\n========== MAPA DE ASIENTOS TEATRO MORO ==========");
        
        // Determinar número de filas y asientos por fila
        int totalAsientos = asientos.length;
        int asientosPorFila = 10; // Asumimos 10 asientos por fila según la inicialización
        int numFilas = (int) Math.ceil((double) totalAsientos / asientosPorFila);
        
        // Mostrar leyenda
        System.out.println("\nLEYENDA:");
        System.out.println("  [##] = Asiento disponible (## es el número de asiento)");
        System.out.println("  [XX] = Asiento ocupado");
        System.out.println("  Categorías: \u001B[31mVIP\u001B[0m, \u001B[32mPalco\u001B[0m, \u001B[33mPlatea Baja\u001B[0m, \u001B[34mPlatea Alta\u001B[0m, \u001B[35mGalería\u001B[0m");
        
        System.out.println("\n                    ESCENARIO");
        System.out.println("   --------------------------------------------");
        
        // Iterar por filas
        for (int fila = 0; fila < numFilas; fila++) {
            // Imprimir número de fila
            System.out.printf("F%-2d ", fila + 1);
            
            // Imprimir asientos de esta fila
            for (int col = 0; col < asientosPorFila; col++) {
                int indiceAsiento = fila * asientosPorFila + col;
                
                // Verificar que no excedamos el número total de asientos
                if (indiceAsiento < totalAsientos && asientos[indiceAsiento] != null) {
                    Asiento asiento = asientos[indiceAsiento];
                    String colorCode = obtenerColorPorCategoria(asiento.getCategoria());
                    
                    if (asiento.isDisponible()) {
                        // Formato para asientos disponibles: [##] donde ## es el número de asiento
                        System.out.printf("%s[%2d]%s ", colorCode, asiento.getIdAsiento(), "\u001B[0m");
                    } else {
                        // Formato para asientos ocupados: [XX]
                        System.out.printf("%s[XX]%s ", colorCode, "\u001B[0m");
                    }
                } else {
                    // Espacio en blanco si no hay asiento
                    System.out.print("     ");
                }
            }
            
            // Nueva línea después de cada fila
            System.out.println();
        }
        
        System.out.println("   --------------------------------------------");
        System.out.println("                    ENTRADA");
        System.out.println("\nTotal asientos disponibles: " + contarAsientosDisponibles() + " de " + totalAsientos);
    }
    
    private String obtenerColorPorCategoria(String categoria) {
        return switch (categoria) {
            case "VIP" -> "\u001B[31m"; // Rojo
            case "Palco" -> "\u001B[32m"; // Verde
            case "Platea Baja" -> "\u001B[33m"; // Amarillo
            case "Platea Alta" -> "\u001B[34m"; // Azul
            case "Galería" -> "\u001B[35m"; // Magenta
            default -> "\u001B[0m"; // Reset/Normal
        };
    }
    
    private int contarAsientosDisponibles() {
        int contador = 0;
        for (Asiento asiento : asientos) {
            if (asiento != null && asiento.isDisponible()) {
                contador++;
            }
        }
        return contador;
    }
    
    // ===== GESTIÓN DE DESCUENTOS =====
    public double obtenerPorcentajeDescuento(Cliente cliente) {
        double totalDescuento = 0.0;
        StringBuilder descuentosAplicados = new StringBuilder();
        
        for (Descuento desc : descuentos) {
            if (desc.aplicaA(cliente)) {
                totalDescuento += desc.getPorcentaje();
                if (descuentosAplicados.length() > 0) {
                    descuentosAplicados.append(", ");
                }
                descuentosAplicados.append(desc.getNombre()).append(" (")
                    .append(String.format("%.0f%%", desc.getPorcentaje() * 100)).append(")");
            }
        }
        
        // Opcional: limitar el descuento máximo a 50% para evitar descuentos excesivos
        if (totalDescuento > 0.5) {
            System.out.println("Aviso: Descuento máximo limitado a 50%.");
            totalDescuento = 0.5;
        }
        
        if (totalDescuento > 0) {
            System.out.println("Descuentos aplicados: " + descuentosAplicados +
                            " - Total: " + String.format("%.0f%%", totalDescuento * 100));
        }
        
        return totalDescuento;
    }
    
    // ===== GESTIÓN DE RESERVAS =====
    public Reserva agregarReserva(int idCliente, int idAsiento) {
        // Verificar reservas expiradas antes de procesar una nueva
        verificarReservasExpiradas();
         
        // Validación de entrada y referencias cruzadas
        Cliente cliente = buscarClientePorId(idCliente);
        if (cliente == null) {
            System.err.println("Error al reservar: Cliente con ID " + idCliente + " no encontrado.");
            return null;
        }
        Asiento asiento = buscarAsientoPorId(idAsiento);
        if (asiento == null) {
            System.err.println("Error al reservar: Asiento con ID " + idAsiento + " no existe.");
            return null;
        }
        if (!asiento.isDisponible()) {
            System.err.println("Error al reservar: Asiento con ID " + idAsiento + " no está disponible.");
            return null;
        }

        // Crear y agregar la reserva
        Reserva nuevaReserva = new Reserva(nextReservaId++, idCliente, idAsiento);
        reservas.add(nuevaReserva);
        asiento.setDisponible(false); // Marcar asiento como no disponible (reservado)
        System.out.println("Reserva agregada: " + nuevaReserva);
        System.out.println("Esta reserva expirará en " + tiempoExpiracionReserva + " minutos si no se completa la compra.");
        return nuevaReserva;
    }

    public Reserva buscarReservaPorId(int idReserva) {
        for (Reserva r : reservas) {
            if (r.getIdReserva() == idReserva) {
                return r;
            }
        }
        return null;
    }

    public boolean cancelarReserva(int idReserva) {
        Reserva reserva = buscarReservaPorId(idReserva);
        if (reserva != null && "Activa".equals(reserva.getEstado())) {
            Asiento asiento = buscarAsientoPorId(reserva.getIdAsiento());
            if (asiento != null) {
                asiento.setDisponible(true); // Liberar el asiento
                System.out.println("Asiento ID " + asiento.getIdAsiento() + 
                    " (" + asiento.getUbicacion() + ") devuelto a disponibles.");
            }
            reserva.setEstado("Cancelada");
            System.out.println("Reserva cancelada: " + reserva);
            return true;
        }
        System.err.println("Error: No se pudo cancelar la reserva ID " + idReserva + " (no encontrada o no activa).");
        return false;
    }
    
    public void mostrarReservasActivas() {
        // Verificar reservas expiradas antes de mostrar las activas
        verificarReservasExpiradas();
        
        System.out.println("\n--- Reservas Activas ---");
        boolean hayActivas = false;
        for (Reserva reserva : reservas) {
            if (reserva != null && "Activa".equals(reserva.getEstado())) {
                LocalDateTime ahora = LocalDateTime.now();
                long minutosRestantes = tiempoExpiracionReserva - ChronoUnit.MINUTES.between(reserva.getFechaReserva(), ahora);
                System.out.println(reserva + " - Expira en: " + (minutosRestantes <= 0 ? "procesando expiración..." : minutosRestantes + " minutos"));
                hayActivas = true;
            }
        }
        if (!hayActivas) {
            System.out.println("No hay reservas activas.");
        }
    }
    
    public void verificarReservasExpiradas() {
        LocalDateTime ahora = LocalDateTime.now();
        boolean hayExpiradas = false;
        
        List<Reserva> reservasExpiradas = new ArrayList<>();
        
        // Primero identificamos las reservas expiradas
        for (Reserva reserva : reservas) {
            if ("Activa".equals(reserva.getEstado())) {
                long minutosTranscurridos = ChronoUnit.MINUTES.between(reserva.getFechaReserva(), ahora);
                
                if (minutosTranscurridos >= tiempoExpiracionReserva) {
                    reservasExpiradas.add(reserva);
                    hayExpiradas = true;
                }
            }
        }
        
        // Luego procesamos cada una
        for (Reserva reserva : reservasExpiradas) {
            Asiento asiento = buscarAsientoPorId(reserva.getIdAsiento());
            if (asiento != null) {
                asiento.setDisponible(true); // Liberar el asiento
                System.out.println("Asiento ID " + asiento.getIdAsiento() + 
                    " (" + asiento.getUbicacion() + ") liberado por expiración de reserva ID " + 
                    reserva.getIdReserva() + ".");
            }
            reserva.setEstado("Expirada");
            System.out.println("Reserva ID " + reserva.getIdReserva() + " ha expirado después de " + 
                    tiempoExpiracionReserva + " minutos.");
        }
        
        if (hayExpiradas) {
            System.out.println("Se liberaron " + reservasExpiradas.size() + " reservas expiradas.");
        }
    }
    
    // ===== GESTIÓN DE VENTAS =====
    public Venta venderEntrada(int idCliente, int idAsiento) {
        // Verificar reservas expiradas antes de procesar una venta
        verificarReservasExpiradas();
        
        // 1. Validaciones de entrada y referencias cruzadas
        Cliente cliente = buscarClientePorId(idCliente);
        Asiento asiento = buscarAsientoPorId(idAsiento);

        if (cliente == null) {
            System.err.println("Error Venta: Cliente con ID " + idCliente + " no encontrado.");
            return null;
        }
        if (asiento == null) {
            System.err.println("Error Venta: Asiento con ID " + idAsiento + " no existe.");
            return null;
        }
        if (asiento.getIdAsiento() <= 0 || asiento.getIdAsiento() > asientos.length) {
            System.err.println("Error Venta: ID de asiento inválido: " + idAsiento);
            return null;
        }

        Reserva reservaExistente = null;
        // Revisa si el asiento está disponible o está reservado por el cliente
        if (!asiento.isDisponible()) {
            for(Reserva r : reservas) {
                // Busca una reserva activa para este asiento y este cliente
                if(r.getIdAsiento() == idAsiento && r.getIdCliente() == idCliente && "Activa".equals(r.getEstado())) {
                    reservaExistente = r;
                    break;
                }
            }
            // Si el asiento está ocupado pero no por una reserva activa del cliente, mostramos un error al usuario
            if(reservaExistente == null) {
                System.err.println("Error Venta: Asiento con ID " + idAsiento + " no está disponible (ocupado o reservado por otro cliente).");
                return null;
            }
            // Si se encuentra, marcamos la reserva como completada
            reservaExistente.setEstado("Completada");
            System.out.println("Completando reserva existente ID: " + reservaExistente.getIdReserva());
        } 

        // 2. Calcular precio con descuento
        double porcentajeDescuento = obtenerPorcentajeDescuento(cliente);
        double precioBase = asiento.getPrecioBase(); // Usar el precio base del asiento
        double precioFinal = precioBase * (1 - porcentajeDescuento);

        // 3. Validar capacidad del arreglo de ventas
        if (numVentas >= MAX_CAPACIDAD) {
            System.err.println("Error Venta: Capacidad máxima de ventas alcanzada.");
            // Si la venta falla por capacidad, revierte el estado de la reserva
            if(reservaExistente != null) {
                reservaExistente.setEstado("Activa"); // Revertir estado de reserva
                System.out.println("Venta fallida por capacidad, reserva ID " + reservaExistente.getIdReserva() + " revertida a Activa.");
            }
            return null;
        }

        // 4. Crear y registrar la venta en el arreglo
        Venta nuevaVenta = new Venta(nextVentaId++, idCliente, idAsiento, precioBase, porcentajeDescuento);
        ventas[numVentas++] = nuevaVenta;

        // 5. Marcar el asiento como no disponible
        asiento.setDisponible(false);

        System.out.println("Venta realizada con éxito:");
        nuevaVenta.imprimirBoleta(cliente, asiento);

        return nuevaVenta;
    }
    
    public boolean cancelarCompra(int idVenta) {
        // Busca la venta a cancelar
        Venta ventaACancelar = null;
        int indiceVenta = -1;
        
        for (int i = 0; i < numVentas; i++) {
            if (ventas[i] != null && ventas[i].getIdVenta() == idVenta) {
                ventaACancelar = ventas[i];
                indiceVenta = i;
                break;
            }
        }
        
        if (ventaACancelar == null) {
            System.err.println("Error: No se encontró la venta con ID " + idVenta);
            return false;
        }

        // Busca el asiento para liberarlo
        Asiento asiento = buscarAsientoPorId(ventaACancelar.getIdAsiento());
        if (asiento == null) {
            System.err.println("Error: No se encontró el asiento con ID " + ventaACancelar.getIdAsiento());
            return false;
        }
        
        // Libera el asiento
        asiento.setDisponible(true);
        
        double montoDevuelto = ventaACancelar.getPrecioFinal();
        
        // Información de la cancelación
        System.out.println("\n=== CANCELACIÓN DE VENTA ===");
        System.out.println("Venta ID: " + ventaACancelar.getIdVenta() + " cancelada.");
        System.out.println("Asiento ID: " + asiento.getIdAsiento() + " liberado.");
        System.out.println("Monto devuelto: $" + String.format("%.2f", montoDevuelto));
        System.out.println("===============================");
        
        // Eliminamos la venta del registro
        ventas[indiceVenta] = null;
        
        // Reacomodar el arreglo para no dejar huecos
        compactarArregloVentas();
        
        return true;
    }
    
    private void compactarArregloVentas() {
        for (int i = 0; i < numVentas - 1; i++) {
            if (ventas[i] == null) {
                // Mueve todas las ventas una posición hacia atrás
                for (int j = i; j < numVentas - 1; j++) {
                    ventas[j] = ventas[j + 1];
                }
                ventas[numVentas - 1] = null;
                numVentas--;
                break;
            }
        }
    }
    
    public void mostrarVentas() {
        System.out.println("\n--- Historial de Ventas ---");
        boolean hayVentas = false;
        for (int i = 0; i < numVentas; i++) {
            if (ventas[i] != null) {
                System.out.println(ventas[i]);
                hayVentas = true;
            }
        }
        if (!hayVentas) {
            System.out.println("No hay ventas registradas.");
        }
    }
    
    // ===== CONFIGURACIÓN DEL SISTEMA =====
    public int getTiempoExpiracionReserva() {
        return tiempoExpiracionReserva;
    }
    
    public void setTiempoExpiracionReserva(int minutos) {
        if (minutos > 0) {
            this.tiempoExpiracionReserva = minutos;
            System.out.println("Tiempo de expiración de reservas actualizado a " + minutos + " minutos.");
        } else {
            System.err.println("Error: El tiempo de expiración debe ser mayor que 0.");
        }
    }
    
    public void verificarIntegridadSistema() {
        System.out.println("\n--- Verificación de Integridad del Sistema ---");
        
        // 1. Verificar que ningún asiento esté duplicado
        System.out.println("Verificando asientos...");
        boolean asientosOK = true;
        for (int i = 0; i < asientos.length; i++) {
            if (asientos[i] == null) {
                System.err.println("Error: Asiento en índice " + i + " es nulo");
                asientosOK = false;
            }
            if (asientos[i].getIdAsiento() != i + 1) {
                System.err.println("Error: ID de asiento inconsistente en índice " + i);
                asientosOK = false;
            }
        }
        
        // 2. Verificar consistencia entre reservas y disponibilidad de asientos
        System.out.println("Verificando reservas y disponibilidad de asientos...");
        boolean reservasOK = true;
        for (Reserva reserva : reservas) {
            if ("Activa".equals(reserva.getEstado())) {
                Asiento asiento = buscarAsientoPorId(reserva.getIdAsiento());
                if (asiento != null && asiento.isDisponible()) {
                    System.err.println("Error: Asiento " + asiento.getIdAsiento() + 
                            " está marcado como disponible pero tiene una reserva activa");
                    reservasOK = false;
                }
            }
        }
        
        // 3. Verificar que cada venta corresponde a un cliente y asiento existente
        System.out.println("Verificando ventas...");
        boolean ventasOK = true;
        for (int i = 0; i < numVentas; i++) {
            if (ventas[i] != null) {
                if (buscarClientePorId(ventas[i].getIdCliente()) == null) {
                    System.err.println("Error: Venta " + ventas[i].getIdVenta() + 
                            " hace referencia a un cliente inexistente");
                    ventasOK = false;
                }
                if (buscarAsientoPorId(ventas[i].getIdAsiento()) == null) {
                    System.err.println("Error: Venta " + ventas[i].getIdVenta() + 
                            " hace referencia a un asiento inexistente");
                    ventasOK = false;
                }
            }
        }
        
        // Resumen
        if (asientosOK && reservasOK && ventasOK) {
            System.out.println("Verificación completada: El sistema mantiene integridad de datos.");
        } else {
            System.err.println("Verificación completada: Se encontraron problemas de integridad.");
        }
    }
    
    // ===== MÉTODOS DE UTILIDAD PARA VALIDACIÓN DE ENTRADAS =====
    private static int validarEntero(Scanner sc, int min, int max) {
        int valor = -1;
        boolean valido = false;
        while (!valido) {
            System.out.print("> ");
            if (sc.hasNextInt()) {
                valor = sc.nextInt();
                if (valor >= min && valor <= max) {
                    valido = true;
                } else {
                    System.err.println("Error: Opción fuera de rango [" + min + "-" + max + "]. Intente de nuevo.");
                }
            } else {
                System.err.println("Error: Entrada inválida. Ingrese un número entero.");
                sc.next();
            }
        }
        sc.nextLine();
        return valor;
    }

    private static int validarEnteroPositivo(Scanner sc, String prompt) {
        int valor = -1;
        while (valor <= 0) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                valor = sc.nextInt();
                if (valor <= 0) {
                    System.err.println("Error: Ingrese un número positivo.");
                }
            } else {
                System.err.println("Error: Entrada inválida. Ingrese un número entero.");
                sc.next();
            }
        }
        sc.nextLine();
        return valor;
    }

    private static String validarStringNoVacio(Scanner sc, String prompt) {
        String input = "";
        while (input.trim().isEmpty()) {
            System.out.print(prompt);
            input = sc.nextLine();
            if (input.trim().isEmpty()) {
                System.err.println("Error: La entrada no puede estar vacía.");
            }
        }
        return input.trim();
    }
    
    // ===== MÉTODOS DE INTERFAZ DE USUARIO =====
    private static void gestionarAgregarCliente(Scanner sc, S9TeatroMoro teatro) {
        System.out.println("\n--- Agregar Nuevo Cliente ---");
        String nombre = validarStringNoVacio(sc, "Ingrese nombre del cliente: ");
        
        int edad = validarEnteroPositivo(sc, "Ingrese edad del cliente: ");
        
        System.out.println("Seleccione el género del cliente:");
        System.out.println("1. Masculino");
        System.out.println("2. Femenino");
        int generoOpcion = validarEntero(sc, 1, 2);
        String genero = generoOpcion == 1 ? "Masculino" : "Femenino";
        
        System.out.println("¿El cliente es estudiante? (S/N): ");
        boolean esEstudiante = sc.nextLine().trim().equalsIgnoreCase("S");
        
        Cliente nuevoCliente = new Cliente(0, nombre, edad, genero, esEstudiante);
        teatro.agregarCliente(nuevoCliente);
    }

    private static void gestionarReserva(Scanner sc, S9TeatroMoro teatro) {
        System.out.println("\n--- Reservar Entrada ---");
        teatro.mostrarClientes();
        int idCliente = validarEnteroPositivo(sc, "Ingrese ID del cliente para la reserva: ");
        if (teatro.buscarClientePorId(idCliente) == null) {
            System.err.println("Error: Cliente con ID " + idCliente + " no encontrado.");
            return;
        }

        teatro.mostrarAsientosDisponibles();
        int idAsiento = validarEnteroPositivo(sc, "Ingrese ID del asiento a reservar: ");

        teatro.agregarReserva(idCliente, idAsiento);
    }

    private static void gestionarCompra(Scanner sc, S9TeatroMoro teatro) {
        System.out.println("\n--- Comprar Entrada ---");
        teatro.mostrarClientes();
        int idCliente = validarEnteroPositivo(sc, "Ingrese ID del cliente para la compra: ");
        if (teatro.buscarClientePorId(idCliente) == null) {
            System.err.println("Error: Cliente con ID " + idCliente + " no encontrado.");
            return;
        }

        teatro.mostrarAsientosDisponibles(); 
        System.out.println("(También puede comprar un asiento previamente reservado por usted)");
        int idAsiento = validarEnteroPositivo(sc, "Ingrese ID del asiento a comprar: ");

        teatro.venderEntrada(idCliente, idAsiento);
    }

    private static void gestionarCancelacionReserva(Scanner sc, S9TeatroMoro teatro) {
        System.out.println("\n--- Cancelar Reserva ---");
        teatro.mostrarReservasActivas();
        if (teatro.reservas.isEmpty() || teatro.reservas.stream().noneMatch(r -> "Activa".equals(r.getEstado()))) {
            System.out.println("No hay reservas activas para cancelar.");
            return;
        }

        int idReserva = validarEnteroPositivo(sc, "Ingrese ID de la reserva a cancelar: ");
        teatro.cancelarReserva(idReserva);
    }

    private static void gestionarCancelacionCompra(Scanner sc, S9TeatroMoro teatro) {
        System.out.println("\n--- Cancelar Compra ---");
        teatro.mostrarVentas();
        
        if (teatro.numVentas == 0) {
            System.out.println("No hay ventas registradas para cancelar.");
            return;
        }
        
        int idVenta = validarEnteroPositivo(sc, "Ingrese ID de la venta a cancelar: ");
        teatro.cancelarCompra(idVenta);
    }

    private static void gestionarImpresion(Scanner sc, S9TeatroMoro teatro) {
        while (true) {
            System.out.println("\n--- Mostrar Información ---");
            System.out.println("1. Mostrar Ventas Realizadas");
            System.out.println("2. Mostrar Reservas Activas");
            System.out.println("3. Mostrar Asientos Disponibles");
            System.out.println("4. Mostrar Clientes Registrados");
            System.out.println("5. Volver al Menú Principal");
            System.out.print("Seleccione opción: ");

            int opcion = validarEntero(sc, 1, 5);

            switch (opcion) {
                case 1:
                    teatro.mostrarVentas();
                    break;
                case 2:
                    teatro.mostrarReservasActivas();
                    break;
                case 3:
                    teatro.mostrarAsientosDisponibles();
                    break;
                case 4:
                    teatro.mostrarClientes();
                    break;
                case 5:
                    return; // Volver al menú principal
            }
            System.out.println("\nPresione Enter para continuar...");
            sc.nextLine();
        }
    }

    // ===== MÉTODO PRINCIPAL (MAIN) =====
    public static void main(String[] args) {
        System.out.println("Iniciando sistema Teatro Moro...");
        // Crea una instancia con 50 asientos 
        S9TeatroMoro teatro = new S9TeatroMoro(50);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Sistema listo. Capacidad de asientos: " + teatro.asientos.length);

        // Pre-cargar algunos clientes para facilitar pruebas
        teatro.agregarCliente(new Cliente(0, "Juan Perez", 35, "Masculino", false)); // Cliente general
        teatro.agregarCliente(new Cliente(0, "Ana Lopez", 20, "Femenino", true));    // Estudiante mujer
        teatro.agregarCliente(new Cliente(0, "Carlos Soto", 70, "Masculino", false)); // Tercera edad
        teatro.agregarCliente(new Cliente(0, "María González", 8, "Femenino", false)); // Niña
        System.out.println("Clientes iniciales cargados.");

        // Ciclo While Principal del Menú
        while (true) {
            System.out.println("\n----- MENÚ PRINCIPAL -----");
            System.out.println("1. Reservar entrada");
            System.out.println("2. Comprar entrada");
            System.out.println("3. Cancelar reserva");
            System.out.println("4. Cancelar compra");
            System.out.println("5. Mostrar Información");
            System.out.println("6. Agregar Cliente");
            System.out.println("7. Configurar Tiempo de Expiración de Reservas");
            System.out.println("8. Verificar integridad de datos del sistema");
            System.out.println("9. Salir");
            System.out.print("Seleccione opción: ");
            int opcion = validarEntero(scanner, 1, 9);

            switch (opcion) {
                case 1:
                    gestionarReserva(scanner, teatro);
                    break;
                case 2:
                    gestionarCompra(scanner, teatro);
                    break;
                case 3:
                    gestionarCancelacionReserva(scanner, teatro);
                    break;
                case 4:
                    gestionarCancelacionCompra(scanner, teatro);
                    break;
                case 5:
                    gestionarImpresion(scanner, teatro);
                    break;
                case 6:
                    gestionarAgregarCliente(scanner, teatro);
                    break;
                case 7:
                    int minutos = validarEnteroPositivo(scanner, "Ingrese el nuevo tiempo de expiración de reservas (en minutos): ");
                    teatro.setTiempoExpiracionReserva(minutos);
                    break;
                case 8:
                    teatro.verificarIntegridadSistema();
                    break;
                case 9:
                    System.out.println("Gracias por usar nuestro sistema. ¡Hasta pronto!");
                    scanner.close();
                    return; // Termina la aplicación
            }
            System.out.println("\nPresione Enter para volver al menú...");
            scanner.nextLine(); // Pausa antes de mostrar el menú de nuevo
        }
    }
}

// ===== DEFINICIÓN DE CLASES DE ENTIDADES =====
/**
 * Clase que representa a un cliente del teatro
 */
class Cliente {
    private int idCliente;
    private String nombre;
    private int edad;
    private String genero;
    private boolean esEstudiante;

    public Cliente(int idCliente, String nombre, int edad, String genero, boolean esEstudiante) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
        this.esEstudiante = esEstudiante;
    }

    // Getters y Setters
    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
    
    public boolean isEstudiante() {
        return esEstudiante;
    }
    
    public void setEstudiante(boolean esEstudiante) {
        this.esEstudiante = esEstudiante;
    }

    @Override
    public String toString() {
        return "Cliente{" +
               "idCliente=" + idCliente +
               ", nombre='" + nombre + '\'' +
               ", edad=" + edad +
               ", genero='" + genero + '\'' +
               ", esEstudiante=" + esEstudiante +
               '}';
    }
}

/**
 * Clase que representa un asiento en el teatro
 */
class Asiento {
    private int idAsiento;
    private String ubicacion;
    private boolean disponible;
    private String categoria;
    private double precioBase;

    public Asiento(int idAsiento, String ubicacion, String categoria, double precioBase) {
        this.idAsiento = idAsiento;
        this.ubicacion = ubicacion;
        this.disponible = true;
        this.categoria = categoria;
        this.precioBase = precioBase;
    }

    // Getters y Setters
    public int getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(int idAsiento) {
        this.idAsiento = idAsiento;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    @Override
    public String toString() {
        return "Asiento{" +
               "idAsiento=" + idAsiento +
               ", ubicacion='" + ubicacion + '\'' +
               ", categoria='" + categoria + '\'' +
               ", precioBase=" + String.format("%.2f", precioBase) +
               ", disponible=" + disponible +
               '}';
    }
}

/**
 * Clase que representa una reserva de asiento
 */
class Reserva {
    private final int idReserva;
    private final int idCliente;
    private final int idAsiento;
    private final LocalDateTime fechaReserva;
    private String estado; // "Activa", "Cancelada", "Completada", "Expirada"

    public Reserva(int idReserva, int idCliente, int idAsiento) {
        this.idReserva = idReserva;
        this.idCliente = idCliente;
        this.idAsiento = idAsiento;
        this.fechaReserva = LocalDateTime.now();
        this.estado = "Activa"; // Por defecto
    }

    // Getters y Setters
    public int getIdReserva() {
        return idReserva;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdAsiento() {
        return idAsiento;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        // Validación simple del estado
        if ("Activa".equals(estado) || "Cancelada".equals(estado) || "Completada".equals(estado) || "Expirada".equals(estado)) {
            this.estado = estado;
        } else {
            System.err.println("Error: Estado de reserva no válido: " + estado);
        }
    }

    @Override
    public String toString() {
        // Formatea la fecha para mayor legibilidad
        String formattedDate = fechaReserva != null ? fechaReserva.format(S9TeatroMoro.DATE_TIME_FORMATTER) : "N/A";
        return "Reserva{" +
               "idReserva=" + idReserva +
               ", idCliente=" + idCliente +
               ", idAsiento=" + idAsiento +
               ", fechaReserva=" + formattedDate +
               ", estado='" + estado + '\'' +
               '}';
    }
}

/**
 * Interfaz funcional para evaluar si un cliente cumple la condición para un descuento
 */
interface CondicionDescuento {
    boolean cumpleCriterio(Cliente cliente);
}

/**
 * Clase que representa un descuento aplicable
 */
class Descuento {
    private String nombre;
    private double porcentaje;
    private CondicionDescuento condicion;

    public Descuento(String nombre, double porcentaje, CondicionDescuento condicion) {
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.condicion = condicion;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPorcentaje() {
        return porcentaje;
    }
    
    public boolean aplicaA(Cliente cliente) {
        return condicion.cumpleCriterio(cliente);
    }

    @Override
    public String toString() {
        return "Descuento{" +
               "nombre='" + nombre + '\'' +
               ", porcentaje=" + String.format("%.0f%%", porcentaje * 100) +
               '}';
    }
}

/**
 * Clase que representa una venta de entrada
 */
class Venta {
    private int idVenta;
    private int idCliente;
    private int idAsiento;
    private double precioBase;
    private double descuento;
    private double precioFinal;
    private LocalDateTime fechaVenta;

    public Venta(int idVenta, int idCliente, int idAsiento, double precioBase, double descuento) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.idAsiento = idAsiento;
        this.precioBase = precioBase;
        this.descuento = descuento;
        this.precioFinal = precioBase * (1 - descuento);
        this.fechaVenta = LocalDateTime.now();
    }

    // Getters
    public int getIdVenta() {
        return idVenta;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdAsiento() {
        return idAsiento;
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public double getPrecioBase() {
        return precioBase;
    }
    
    public double getDescuento() {
        return descuento;
    }

    public void imprimirBoleta(Cliente cliente, Asiento asiento) {
        System.out.println("\n====== BOLETA DE VENTA ======");
        System.out.println("ID Venta: " + idVenta);
        System.out.println("Fecha: " + fechaVenta.format(S9TeatroMoro.DATE_TIME_FORMATTER));
        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("Asiento: " + asiento.getUbicacion());
        System.out.println("Categoría: " + asiento.getCategoria());
        System.out.println("Precio Base: $" + String.format("%.2f", precioBase));
        System.out.println("Descuento: $" + String.format("%.2f", precioBase * descuento) + 
                          " (" + String.format("%.0f%%", descuento * 100) + ")");
        System.out.println("Precio Final: $" + String.format("%.2f", precioFinal));
        System.out.println("===========================");
    }

    @Override
    public String toString() {
        String formattedDate = fechaVenta != null ? fechaVenta.format(S9TeatroMoro.DATE_TIME_FORMATTER) : "N/A";
        return "Venta{" +
               "idVenta=" + idVenta +
               ", idCliente=" + idCliente +
               ", idAsiento=" + idAsiento +
               ", precioBase=" + String.format("%.2f", precioBase) +
               ", descuento=" + String.format("%.0f%%", descuento * 100) +
               ", precioFinal=" + String.format("%.2f", precioFinal) +
               ", fechaVenta=" + formattedDate +
               '}';
    }
}