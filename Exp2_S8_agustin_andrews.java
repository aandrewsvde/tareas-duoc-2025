/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package s8teatromoro;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author aandr
 */
public class S8TeatroMoro {
    // --- Estructuras de Datos ---
    private static final int MAX_CAPACIDAD = 1000; // Capacidad máxima para los arreglos
    private Cliente[] clientes;
    private Asiento[] asientos;
    private Venta[] ventas;
    private List<Descuento> descuentos;
    private List<Reserva> reservas;

    // Contadores para los arreglos
    private int numClientes;
    private int numVentas;
    private int nextVentaId;
    private int nextReservaId;
    private int nextClienteId;

    private static final double PRECIO_BASE_ENTRADA = 5000.0; // Precio base de una entrada
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Formatea las fechas para impresión

    // --- Constructor del Teatro ---
    public S8TeatroMoro(int capacidadAsientos) {
        // Inicialización de arreglos con tamaño fijo
        clientes = new Cliente[MAX_CAPACIDAD];
        asientos = new Asiento[capacidadAsientos]; // Tamaño específico para asientos
        ventas = new Venta[MAX_CAPACIDAD];

        // Inicialización de listas
        descuentos = new ArrayList<>();
        reservas = new ArrayList<>();

        // Inicialización de contadores y IDs
        numClientes = 0;
        numVentas = 0;
        nextVentaId = 1;
        nextReservaId = 1;
        nextClienteId = 1;

        // Inicializar asientos (ejemplo básico)
        for (int i = 0; i < capacidadAsientos; i++) {
            asientos[i] = new Asiento(i + 1, "Fila " + ((i / 10) + 1) + ", Asiento " + ((i % 10) + 1));
        }

        // Inicializar descuentos base
        descuentos.add(new Descuento("Estudiante", 0.10)); // 10%
        descuentos.add(new Descuento("Tercera Edad", 0.15)); // 15%
    }
    
    public static void main(String[] args) {
        System.out.println("Iniciando sistema Teatro Moro...");
        // Crea una instancia con 50 asientos 
        S8TeatroMoro teatro = new S8TeatroMoro(50);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Sistema listo. Capacidad de asientos: " + teatro.asientos.length);

        // --- Pre-cargar algunos clientes para facilitar pruebas ---
        teatro.agregarCliente(new Cliente(0, "Juan Perez", "General"));
        teatro.agregarCliente(new Cliente(0, "Ana Lopez", "Estudiante"));
        teatro.agregarCliente(new Cliente(0, "Carlos Soto", "Tercera Edad"));
        System.out.println("Clientes iniciales cargados.");
        // -------------------------------------------------------

        // ---  Ciclo While Principal del Menú ---
        while (true) {
            mostrarMenuPrincipal();
            int opcion = validarEntero(scanner, 1, 6);

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
                    gestionarImpresion(scanner, teatro);
                    break;
                case 5:
                    gestionarAgregarCliente(scanner, teatro);
                    break;
                case 6:
                    System.out.println("Gracias por usar nuestro sistema. ¡Hasta pronto!");
                    scanner.close();
                    return; // Termina la aplicación
            }
             System.out.println("\nPresione Enter para volver al menú...");
             scanner.nextLine(); // Pausa antes de mostrar el menú de nuevo
        }
    }

    // --- Gestión de Clientes (Arreglo) ---
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
        // Observación: Esta validación sería innecesaria ya que generamos los IDs secuencialmente
        // se agrega de todas formas para cumplir con los objetivos de la actividad.
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

    // --- Gestión de Asientos (Arreglo) ---
    public Asiento buscarAsientoPorId(int idAsiento) {
        // Los IDs de asiento van de 1 a capacidadAsientos
        if (idAsiento < 1 || idAsiento > asientos.length) {
            return null; // ID fuera de rango
        }
        // El índice del arreglo es idAsiento - 1
        // Revisa si el índice del asiento existe en el arreglo (debería si se inicializó correctamente el sistema)
        if (asientos[idAsiento - 1] != null) {
             return asientos[idAsiento - 1];
        }
        return null; // No debería pasar si se inicializó correctamente
    }

    // --- Gestión de Descuentos (Lista) ---
    public double obtenerPorcentajeDescuento(String tipoCliente) {
        for (Descuento desc : descuentos) {
            if (desc.getTipoCliente().equalsIgnoreCase(tipoCliente)) {
                return desc.getPorcentaje();
            }
        }
        return 0.0; // Sin descuento
    }

    // --- Gestión de Reservas (Lista) ---
     public Reserva agregarReserva(int idCliente, int idAsiento) {
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
             }
             reserva.setEstado("Cancelada");
             System.out.println("Reserva cancelada: " + reserva);
             return true;
         }
         System.err.println("Error: No se pudo cancelar la reserva ID " + idReserva + " (no encontrada o no activa).");
         return false;
     }

    // --- Funcionalidad Principal: Venta de Entradas ---
    public Venta venderEntrada(int idCliente, int idAsiento) {
        // 1. Validaciones de entrada y referencias cruzadas
        Cliente cliente = buscarClientePorId(idCliente);
        if (cliente == null) {
            System.err.println("Error Venta: Cliente con ID " + idCliente + " no encontrado.");
            return null;
        }
        Asiento asiento = buscarAsientoPorId(idAsiento);
        if (asiento == null) {
            System.err.println("Error Venta: Asiento con ID " + idAsiento + " no existe.");
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
        } // Si el asiento estaba disponible, asiento.isDisponible() devolvió true, y reservaExistente permanece null

        // 2. Calcular precio con descuento
        double porcentajeDescuento = obtenerPorcentajeDescuento(cliente.getTipo());
        double precioFinal = PRECIO_BASE_ENTRADA * (1 - porcentajeDescuento);

        // 3. Validar capacidad del arreglo de ventas
        if (numVentas >= MAX_CAPACIDAD) {
            System.err.println("Error Venta: Capacidad máxima de ventas alcanzada.");
            // Si la venta falla por capacidad, revierte el estado de la reserve si esta se cambió a completada
             if(reservaExistente != null) {
                 reservaExistente.setEstado("Activa"); // Revertir estado de reserva
                 System.out.println("Venta fallida por capacidad, reserva ID " + reservaExistente.getIdReserva() + " revertida a Activa.");
             }
            return null;
        }

        // 4. Crear y registrar la venta en el arreglo
        Venta nuevaVenta = new Venta(nextVentaId++, idCliente, idAsiento, precioFinal);
        ventas[numVentas++] = nuevaVenta;

        // 5. Marcar el asiento como no disponible (si no estaba ya ocupado por la reserva)
        // Esto es redundante si reservaExistente no era null, pero es más seguro llamarlo de nuevo
        asiento.setDisponible(false);

        System.out.println("Venta realizada con éxito: " + nuevaVenta);
        System.out.println("Cliente: " + cliente.getNombre() + ", Asiento: " + asiento.getUbicacion() + ", Precio: $" + precioFinal);

        return nuevaVenta;
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

    public void mostrarAsientosDisponibles() {
        System.out.println("\n--- Asientos Disponibles ---");
        boolean hayDisponibles = false;
        for (Asiento asiento : asientos) {
            // Revisa si el objeto de asiento no es nulo antes de acceder a sus métodos
            if (asiento != null && asiento.isDisponible()) {
                System.out.println(asiento);
                hayDisponibles = true;
            }
        }
         if (!hayDisponibles) {
            System.out.println("No hay asientos disponibles.");
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

     public void mostrarReservasActivas() {
        System.out.println("\n--- Reservas Activas ---");
        boolean hayActivas = false;
        for (Reserva reserva : reservas) {
            if (reserva != null && "Activa".equals(reserva.getEstado())) {
                System.out.println(reserva);
                hayActivas = true;
            }
        }
         if (!hayActivas) {
            System.out.println("No hay reservas activas.");
        }
    }


    /**
     * Muestra el menú principal de opciones.
     */
    private static void mostrarMenuPrincipal() {
        System.out.println("\n----- MENÚ PRINCIPAL -----");
        System.out.println("1. Reservar entrada");
        System.out.println("2. Comprar entrada");
        System.out.println("3. Cancelar Reserva"); 
        System.out.println("4. Mostrar Información");
        System.out.println("5. Agregar Cliente");
        System.out.println("6. Salir");
        System.out.print("Seleccione opción: ");
    }

    /**
     * Valida que la entrada del usuario sea un entero dentro de un rango.
     * @param sc Scanner para leer la entrada.
     * @param min Valor mínimo permitido.
     * @param max Valor máximo permitido.
     * @return El entero validado.
     */
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

     /**
     * Valida que la entrada del usuario sea un entero positivo.
     * @param sc Scanner para leer la entrada.
     * @param prompt Mensaje a mostrar al usuario.
     * @return El entero positivo validado.
     */
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

    /**
     * Valida que la entrada del usuario no sea una cadena vacía.
     * @param sc Scanner para leer la entrada.
     * @param prompt Mensaje a mostrar al usuario.
     * @return La cadena no vacía validada.
     */
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


    /**
     * Gestiona el proceso de agregar un nuevo cliente.
     * @param sc Scanner para leer la entrada.
     * @param teatro Instancia de S8TeatroMoro.
     */
    private static void gestionarAgregarCliente(Scanner sc, S8TeatroMoro teatro) {
        System.out.println("\n--- Agregar Nuevo Cliente ---");
        String nombre = validarStringNoVacio(sc, "Ingrese nombre del cliente: ");

        // Menu para seleccionar tipo de cliente
        System.out.println("Seleccione el tipo de cliente:");
        System.out.println("1. General");
        System.out.println("2. Estudiante");
        System.out.println("3. Tercera Edad");

        int tipoOpcion = validarEntero(sc, 1, 3);

        String tipo;

        switch (tipoOpcion) {
            case 1:
                tipo = "General";
                break;
            case 2:
                tipo = "Estudiante";
                break;
            case 3:
                tipo = "Tercera Edad";
                break;
            default:
                // No debería llegar aquí por el método validarEntero
                System.err.println("Error: Opción de tipo inválida.");
                return;
        }
        Cliente nuevoCliente = new Cliente(0, nombre, tipo);
        teatro.agregarCliente(nuevoCliente);
    }


    /**
     * Gestiona el proceso de reserva de entradas.
     * @param sc Scanner para leer la entrada.
     * @param teatro Instancia de S8TeatroMoro.
     */
    private static void gestionarReserva(Scanner sc, S8TeatroMoro teatro) {
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

    /**
     * Gestiona el proceso de compra de entradas.
     * @param sc Scanner para leer la entrada.
     * @param teatro Instancia de S8TeatroMoro.
     */
    private static void gestionarCompra(Scanner sc, S8TeatroMoro teatro) {
        System.out.println("\n--- Comprar Entrada ---");
        teatro.mostrarClientes();
        int idCliente = validarEnteroPositivo(sc, "Ingrese ID del cliente para la compra: ");
         if (teatro.buscarClientePorId(idCliente) == null) {
            System.err.println("Error: Cliente con ID " + idCliente + " no encontrado.");
            return;
        }

        teatro.mostrarAsientosDisponibles(); // Mostrar asientos disponibles antes de pedir ID
        System.out.println("(También puede comprar un asiento previamente reservado por usted)");
        int idAsiento = validarEnteroPositivo(sc, "Ingrese ID del asiento a comprar: ");

        teatro.venderEntrada(idCliente, idAsiento);
    }

    /**
     * Gestiona la cancelación de una reserva activa.
     * @param sc Scanner para leer la entrada.
     * @param teatro Instancia de S8TeatroMoro.
     */
    private static void gestionarCancelacionReserva(Scanner sc, S8TeatroMoro teatro) {
        System.out.println("\n--- Cancelar Reserva ---");
        teatro.mostrarReservasActivas();
        if (teatro.reservas.isEmpty() || teatro.reservas.stream().noneMatch(r -> "Activa".equals(r.getEstado()))) {
             System.out.println("No hay reservas activas para cancelar.");
             return;
        }

        int idReserva = validarEnteroPositivo(sc, "Ingrese ID de la reserva a cancelar: ");
        teatro.cancelarReserva(idReserva);
    }

     /**
     * Muestra un submenú para elegir qué información mostrar.
     * @param sc Scanner para leer la entrada.
     * @param teatro Instancia de S8TeatroMoro.
     */
    private static void gestionarImpresion(Scanner sc, S8TeatroMoro teatro) {
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
}

// --- Clases de diferentes entidades del sistema ---

class Cliente {
    private int idCliente;
    private String nombre;
    private String tipo; // "General", "Estudiante", "Tercera Edad"

    public Cliente(int idCliente, String nombre, String tipo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.tipo = tipo;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Cliente{" +
               "idCliente=" + idCliente +
               ", nombre='" + nombre + '\'' +
               ", tipo='" + tipo + '\'' +
               '}';
    }
}

class Reserva {
    private int idReserva;
    private int idCliente;
    private int idAsiento;
    private LocalDateTime fechaReserva;
    private String estado; // "Activa", "Cancelada", "Completada"

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
        if ("Activa".equals(estado) || "Cancelada".equals(estado) || "Completada".equals(estado)) {
            this.estado = estado;
        } else {
            System.err.println("Error: Estado de reserva no válido: " + estado);
        }
    }

     @Override
    public String toString() {
        // Formatea la fecha para mayor legibilidad
        String formattedDate = fechaReserva != null ? fechaReserva.format(S8TeatroMoro.DATE_TIME_FORMATTER) : "N/A";
        return "Reserva{" +
               "idReserva=" + idReserva +
               ", idCliente=" + idCliente +
               ", idAsiento=" + idAsiento +
               ", fechaReserva=" + formattedDate +
               ", estado='" + estado + '\'' +
               '}';
    }
}

class Descuento {
    private String tipoCliente; // "Estudiante", "Tercera Edad"
    private double porcentaje; // 0.10 para 10%, 0.15 para 15%

    public Descuento(String tipoCliente, double porcentaje) {
        this.tipoCliente = tipoCliente;
        this.porcentaje = porcentaje;
    }

    // Getters
    public String getTipoCliente() {
        return tipoCliente;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    @Override
    public String toString() {
        return "Descuento{" +
               "tipoCliente='" + tipoCliente + '\'' +
               ", porcentaje=" + String.format("%.0f%%", porcentaje * 100) + // Muestra el porcentaje sin decimales
               '}';
    }
}

class Venta {
    private int idVenta;
    private int idCliente;
    private int idAsiento;
    private double precioFinal;
    private LocalDateTime fechaVenta;

    public Venta(int idVenta, int idCliente, int idAsiento, double precioFinal) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.idAsiento = idAsiento;
        this.precioFinal = precioFinal;
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

     @Override
    public String toString() {
        // Formatea la fecha para mayor legibilidad
        String formattedDate = fechaVenta != null ? fechaVenta.format(S8TeatroMoro.DATE_TIME_FORMATTER) : "N/A";
        return "Venta{" +
               "idVenta=" + idVenta +
               ", idCliente=" + idCliente +
               ", idAsiento=" + idAsiento +
               ", precioFinal=" + String.format("%.2f", precioFinal) + // Formatea el precio
               ", fechaVenta=" + formattedDate +
               '}';
    }
}

class Asiento {
    private int idAsiento;
    private String ubicacion; // Ejemplo: "Fila 5, Asiento 12"
    private boolean disponible;

    public Asiento(int idAsiento, String ubicacion) {
        this.idAsiento = idAsiento;
        this.ubicacion = ubicacion;
        this.disponible = true; // Por defecto, el asiento está disponible
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

    @Override
    public String toString() {
        return "Asiento{" +
               "idAsiento=" + idAsiento +
               ", ubicacion='" + ubicacion + '\'' +
               ", disponible=" + disponible +
               '}';
    }
}
