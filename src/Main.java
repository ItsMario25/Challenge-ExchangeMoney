import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    private static String API_KEY;
    private static String BASE_URL;
    private static HttpClient cliente;
    private static Scanner scanner;

    public static void main(String[] args) {

        try {
            // Cargar configuración desde archivo properties
            if (!cargarConfiguracion()) {
                System.out.println("❌ No se pudo cargar la configuración.");
                System.out.println("Asegúrate de crear el archivo 'config.properties' con tu API key.");
                return;
            }
        } finally {

        }

        cliente = HttpClient.newHttpClient();
        scanner = new Scanner(System.in);
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
    private static boolean cargarConfiguracion() {
        Properties properties = new Properties();

        // Intentar cargar desde el archivo en la raíz del proyecto
        try (InputStream input = Files.newInputStream(Paths.get("config.properties"))) {
            properties.load(input);
            API_KEY = properties.getProperty("api.key");
            BASE_URL = properties.getProperty("api.url", "https://v6.exchangerate-api.com/v6/");

            if (API_KEY == null || API_KEY.isEmpty() || API_KEY.equals("TU_API_KEY_AQUI")) {
                System.out.println("❌ API key no configurada correctamente en config.properties");
                return false;
            }

            return true;

        } catch (IOException e) {
            System.out.println("❌ No se encontró el archivo config.properties");
            System.out.println("📝 Crea el archivo en la raíz del proyecto con el siguiente contenido:");
            System.out.println("api.key=TU_API_KEY_AQUI");
            System.out.println("api.url=https://v6.exchangerate-api.com/v6/");
            return false;
        }
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