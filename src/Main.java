import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static final String API_KEY = "7b6cf7b9d76ccf6733175f82"; // Reemplaza con tu API key
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";
    private static HttpClient cliente = HttpClient.newHttpClient();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion;

        do {
            mostrarMenu();
            opcion = leerOpcion();

            switch (opcion) {
                case 1:
                    consultarTasas();
                    break;
                case 2:
                    convertirMoneda();
                    break;
                case 3:
                    System.out.println("\n👋 ¡Gracias por usar el consultor de tasas!");
                    break;
                default:
                    System.out.println("\n❌ Opción no válida. Intenta de nuevo.");
            }

            if (opcion != 3) {
                System.out.println("\nPresiona Enter para continuar...");
                scanner.nextLine();
            }

        } while (opcion != 3);

        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   💱 CONSULTOR DE TASAS DE CAMBIO");
        System.out.println("=".repeat(50));
        System.out.println("1. Consultar tasas para una moneda");
        System.out.println("2. Convertir entre monedas");
        System.out.println("3. Salir");
        System.out.println("=".repeat(50));
        System.out.print("Selecciona una opción: ");
    }

    private static int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void consultarTasas() {
        System.out.print("\nIngresa el código de la moneda base (ej: USD, EUR, COP): ");
        String monedaBase = scanner.nextLine().toUpperCase().trim();

        String url = BASE_URL + API_KEY + "/latest/" + monedaBase;

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            System.out.println("\n⏳ Consultando tasas para " + monedaBase + "...");
            HttpResponse<String> respuesta = cliente.send(solicitud,
                    HttpResponse.BodyHandlers.ofString());

            if (respuesta.statusCode() == 200) {
                System.out.println("\n✅ Tasas obtenidas exitosamente:");
                System.out.println("=".repeat(40));

                String json = respuesta.body();
                // Extraer información básica del JSON
                if (json.contains("conversion_rates")) {
                    System.out.println("Moneda base: " + monedaBase);
                    System.out.println("Tasas disponibles para múltiples monedas");
                    System.out.println("\nVista previa de los datos:");
                    System.out.println(json.substring(0, Math.min(json.length(), 300)) + "...");
                }
            } else {
                System.out.println("\n❌ Error: " + respuesta.statusCode());
                System.out.println("Mensaje: " + respuesta.body());
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("\n❌ Error de conexión: " + e.getMessage());
        }
    }

    private static void convertirMoneda() {
        System.out.print("\nMoneda de origen (ej: USD): ");
        String origen = scanner.nextLine().toUpperCase().trim();

        System.out.print("Moneda de destino (ej: EUR): ");
        String destino = scanner.nextLine().toUpperCase().trim();

        System.out.print("Cantidad a convertir: ");
        double cantidad;
        try {
            cantidad = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Cantidad inválida");
            return;
        }

        String url = BASE_URL + API_KEY + "/pair/" + origen + "/" + destino + "/" + cantidad;

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            System.out.println("\n⏳ Convirtiendo " + cantidad + " " + origen + " a " + destino + "...");
            HttpResponse<String> respuesta = cliente.send(solicitud,
                    HttpResponse.BodyHandlers.ofString());

            if (respuesta.statusCode() == 200) {
                String json = respuesta.body();
                System.out.println("\n✅ Resultado de la conversión:");
                System.out.println("=".repeat(40));
                System.out.println("Respuesta de la API:");
                System.out.println(json);
            } else {
                System.out.println("\n❌ Error: " + respuesta.statusCode());
                System.out.println("Mensaje: " + respuesta.body());
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("\n❌ Error de conexión: " + e.getMessage());
        }
    }
}