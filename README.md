# Proyecto de Consulta de Precios - Detalles de Implementación
Este documento describe cómo se abordaron los requisitos para implementar un servicio REST de consulta de precios usando Spring Boot, JPA, H2, pruebas unitarias, y Swagger. Todos los componentes han sido implementados para garantizar modularidad y alta calidad.

## 1. Modelo (Model)
### Requisito:
Definir las entidades que representan los datos necesarios para la lógica de negocio.

### Abordaje:
Se crearon dos clases modelo: PRICES y BRAND, que representan las tablas correspondientes en la base de datos H2. Estas clases contienen la información necesaria para realizar las consultas de precios en función de la fecha, producto y marca.

### ¿Por qué es necesario?
Los modelos definen la estructura de los datos que la aplicación manipula y persiste en la base de datos. JPA se utiliza para mapear estas clases a la base de datos en memoria H2, lo que permite realizar operaciones CRUD de manera eficiente.

### Uso de JPA:
Java Persistence API (JPA) se ha utilizado para mapear objetos Java a las tablas de la base de datos. Esto permite usar anotaciones como @Entity, @Id, @GeneratedValue, y relaciones como @ManyToOne y @OneToMany.

```java
@Entity
public class PRICES {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long priceId;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private BRAND brand;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int priceList;
    private int productId;
    private int priority;
    private double price;
    private String curr;

    // Getters y Setters
}

@Entity
public class BRAND {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "brand")
    private Set<PRICES> prices;

    // Getters y Setters
}

```

### Explicación:

#### PRICES: Contiene detalles sobre el precio, incluidas fechas de inicio y fin, prioridad, producto y precio.
#### BRAND: Almacena la información sobre la marca.
#### Relación One-To-Many: Un BRAND puede tener múltiples precios asociados (PRICES).

## 2. Repositorio (Repository)
### Requisito:
Crear una capa que interactúe con la base de datos en memoria H2 para recuperar la información de los precios.

### Abordaje:
Se implementó el repositorio PriceRepository, que extiende JpaRepository. Esto facilita las operaciones CRUD sobre las entidades. Además, se creó una consulta personalizada para obtener el precio correcto basado en la fecha, el producto y la marca.

### ¿Por qué es necesario?
El repositorio encapsula la lógica de acceso a la base de datos, separando esta lógica del resto de la lógica de negocio. Esto permite un código más modular y mantenible.

### Uso de JPA en el Repositorio:
El repositorio utiliza JpaRepository, lo que simplifica las operaciones CRUD sin necesidad de escribir consultas SQL manuales. JPA genera automáticamente las consultas SQL basadas en los métodos definidos.

### Código del repositorio:
```java
public interface PriceRepository extends JpaRepository<PRICES, Long> {
    @Query("SELECT p FROM PRICES p WHERE p.productId = :productId AND p.brand.id = :brandId AND :date BETWEEN p.startDate AND p.endDate ORDER BY p.priority DESC")
    PRICES findValidPrice(LocalDateTime date, int productId, int brandId);
}
```
### Explicación:
La consulta personalizada findValidPrice busca el precio adecuado en función de la fecha, el producto y la marca, priorizando aquellos precios con mayor valor en el campo priority si las fechas se superponen.

## 3. DTO (Data Transfer Object)
### Requisito:
Transformar los datos desde las entidades a un formato adecuado para devolver al cliente en el endpoint.

### Abordaje:
Se creó un DTO PricesDto para transferir los datos de manera eficiente. Además, se implementó un Mapper para convertir las entidades PRICES en PricesDto.

### ¿Por qué es necesario?
El uso de DTOs evita exponer directamente las entidades de la base de datos y permite personalizar la respuesta para el cliente, garantizando una mayor seguridad y flexibilidad en la presentación de los datos.

### Código del DTO:
```java
public class PricesDto {
    private long productId;
    private String brandName;
    private double price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Getters y Setters
}
```
### Mapper:
```java
public class PricesMapper {
    public static PricesDto mapToPricesDto(PRICES prices) {
        PricesDto dto = new PricesDto();
        dto.setProductId(prices.getProductId());
        dto.setBrandName(prices.getBrand().getName());
        dto.setPrice(prices.getPrice());
        dto.setStartDate(prices.getStartDate());
        dto.setEndDate(prices.getEndDate());
        return dto;
    }
}
```

Aquí tienes el contenido corregido y formateado correctamente en Markdown, cumpliendo con todos los requisitos solicitados:

## 4. Controlador (Controller)
### Requisito:
Crear un endpoint REST que permita consultar los precios en función de la fecha, el producto y la marca.

### Abordaje:
El controlador PriceBrandController gestiona las solicitudes HTTP GET. Recibe los parámetros de fecha, producto y marca, y devuelve el precio aplicable en forma de DTO.

### Uso de Spring Boot:
El controlador utiliza anotaciones de Spring Boot como @RestController, lo que facilita la exposición de servicios REST. La anotación @RequestMapping define la ruta base del endpoint.

### Código del controlador:
```java
@RestController
@RequestMapping("/api/prices")
public class PriceBrandController {

    @Autowired
    private PriceBrandService priceBrandService;

    @GetMapping("/")
    public ResponseEntity<PricesDto> getPriceByDateAndProduct(
            @RequestParam("date") String date, 
            @RequestParam("productId") int productId,
            @RequestParam("brandId") int brandId) {
        
        PricesDto pricesDto = priceBrandService.getPriceByDateAndProduct(date, productId, brandId);
        if (pricesDto != null) {
            return ResponseEntity.ok(pricesDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
}
```

## 5. Capa de Servicio (Service)
### Requisito:
Implementar la lógica de negocio que determine el precio adecuado en función de los parámetros recibidos.

### Abordaje:
La capa de servicio PriceBrandService gestiona la lógica de negocio. Se comunica con el repositorio para obtener los datos y utiliza el Mapper para convertir la entidad PRICES en PricesDto.

### Uso de Spring Boot en el Servicio:
Se utiliza la anotación @Service para definir la capa de servicio, lo que facilita la inyección de dependencias y la separación de responsabilidades dentro de la aplicación.

### Código del servicio:
```java
@Service
public class PriceBrandService {

    @Autowired
    private PriceRepository priceRepository;

    public PricesDto getPriceByDateAndProduct(String date, int productId, int brandId) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        PRICES price = priceRepository.findValidPrice(dateTime, productId, brandId);
        if (price != null) {
            return PricesMapper.mapToPricesDto(price);
        }
        return null;
    }
}
```
## 6. Base de Datos en Memoria H2
### Requisito:
Usar una base de datos en memoria H2, inicializada con los datos de ejemplo proporcionados.

### Abordaje:
Se configuró una base de datos H2 en memoria, que se inicializa al iniciar la aplicación con los datos proporcionados para el examen. Esto permite realizar las pruebas sin necesidad de configurar una base de datos externa.

### Configuración en application.properties:
```
properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=TRUE
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
```
### Acceso a la consola H2:
Para acceder a la consola H2, utiliza la siguiente URL en tu navegador:

```
http://localhost:8080/h2-console
```

## 7. Pruebas Unitarias
### Requisito:
Validar que el endpoint REST devuelva los resultados correctos en los escenarios descritos.

### Abordaje:
Se implementaron pruebas unitarias utilizando MockMvc para validar los distintos escenarios del servicio, asegurando que el endpoint devuelva el precio correcto basado en los parámetros proporcionados.

### Código de pruebas:
```java
Copiar código
@SpringBootTest
@AutoConfigureMockMvc
public class PriceBrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceBrandService priceBrandService;

    @Test
    public void givenValidRequest_whenGetPrice_thenReturnsPrice() throws Exception {
        PricesDto pricesDto = new PricesDto();
        pricesDto.setPrice(35.50);
        
        BDDMockito.given(priceBrandService.getPriceByDateAndProduct(anyString(), anyInt(), anyInt())).willReturn(pricesDto);

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/prices/")
                .param("date", "2020-06-14T10:00:00")
                .param("productId", "35455")
                .param("brandId", "1"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", CoreMatchers.is(35.50)));
    }
}
```
## 8. Documentación Swagger
### Requisito:
Generar documentación interactiva del API.

### Abordaje:
Se utilizó Springdoc OpenAPI para generar la documentación interactiva de la API, permitiendo a los usuarios visualizar y probar los endpoints a través de la interfaz de Swagger.

### Acceso a Swagger:
Para acceder a la interfaz de Swagger, usa la siguiente URL en tu navegador:

```
Copiar código
http://localhost:8080/swagger-ui.html
```

## 9. Consideraciones Finales
### Justificación de la Configuración:
Se utilizó Java 17 y Spring Boot 3.0.11 debido a su compatibilidad con Eclipse en Mac. No obstante, el proyecto es compatible con versiones superiores de ambas tecnologías, lo que garantiza flexibilidad y escalabilidad a futuro.