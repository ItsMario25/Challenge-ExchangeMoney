# 💱 Conversor de Monedas - API ExchangeRate

Aplicación en Java que consume la API de [ExchangeRate-API](https://www.exchangerate-api.com/) para consultar tasas de cambio en tiempo real y realizar conversiones entre diferentes monedas.

## 📋 Características

- Consulta de tasas de cambio para cualquier moneda base
- Conversión entre diferentes monedas
- Interfaz de línea de comandos intuitiva
- Manejo seguro de API keys mediante archivo de configuración
- Manejo de errores y excepciones

## 🛠️ Tecnologías Utilizadas

- Java 11+
- HttpClient (Java 11+)
- Maven (opcional)
- ExchangeRate-API

## 📁 Estructura del Proyecto
ApiBack/
├── src/
│ └── main/
│ └── java/
│ └── Main.java
├── config.properties # (No incluido en git)
├── README.md
└── .gitignore


## 🔧 Configuración Inicial

### 1. Obtener una API Key

1. Regístrate gratuitamente en [ExchangeRate-API](https://www.exchangerate-api.com/)
2. Confirma tu email para activar la cuenta
3. Obtén tu API key en el dashboard

### 2. Configurar el archivo de propiedades

Crea un archivo `config.properties` en la raíz del proyecto:

```properties
# config.properties
api.key=TU_API_KEY_AQUI
api.url=https://v6.exchangerate-api.com/v6/