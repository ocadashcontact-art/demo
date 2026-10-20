
# API Tienda

API REST desarrollada con Java y Spring Boot para gestionar productos, carritos de compra y pedidos, utilizando PostgreSQL como base de datos.

## Tecnologías

- Java 17
- Spring Boot 3.5.6
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok
- Postman / Thunder Client para pruebas

## Requisitos previos

- JDK 17 o compatible.
- PostgreSQL instalado y en ejecución.
- Maven (se puede utilizar el Maven Wrapper incluido).
- Un cliente REST para realizar pruebas.

## Configuración de la base de datos

Crear una base de datos llamada `tiendaya` en PostgreSQL.

Configurar `src/main/resources/application.properties` con las credenciales correspondientes:

```properties
spring.application.name=api-tienda
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/tiendaya
spring.datasource.username=USUARIO_POSTGRES
spring.datasource.password=CONTRASENA_POSTGRES
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Reemplazar `USUARIO_POSTGRES` y `CONTRASENA_POSTGRES` por las credenciales locales. No publicar contraseñas reales en repositorios públicos.

## Ejecutar el proyecto

Desde la raíz del proyecto, ejecutar en Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

La API estará disponible en:

`http://localhost:8080`

## Endpoints de productos

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/products` | Listar productos |
| GET | `/products/{id}` | Consultar producto por ID |
| POST | `/products?name=Teclado&price=50000` | Crear producto |
| PUT | `/products/{id}?name=Mouse&price=30000` | Actualizar producto |
| PATCH | `/products/{id}?price=35000` | Actualizar parcialmente |
| DELETE | `/products/{id}` | Eliminar producto |

## Endpoints de pedidos

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/orders` | Crear carrito |
| POST | `/orders/{orderId}/items` | Agregar producto |
| GET | `/orders` | Listar pedidos |
| GET | `/orders/{orderId}` | Consultar pedido |
| GET | `/orders/{orderId}/detalle` | Consultar detalle |
| PUT | `/orders/{orderId}/confirmar` | Confirmar pedido |
| PUT | `/orders/{orderId}/cancelar` | Cancelar carrito |

## Ejemplo: agregar un producto

Solicitud:

`POST /orders/1/items`

Cuerpo JSON:

```json
{
  "productId": 1,
  "quantity": 2
}
```

## Reglas de negocio

- La cantidad solicitada debe ser mayor que cero.
- El nombre del producto es obligatorio y su precio debe ser mayor que cero.
- El total del pedido se calcula en el servidor.
- Solo se pueden modificar pedidos en estado `CARRITO`.
- No se pueden confirmar pedidos vacíos.
- Se verifica el inventario antes de confirmar un pedido.
- Al confirmar, se descuenta el stock de los productos.
- Solo se pueden cancelar pedidos en estado `CARRITO`.
- No se permite eliminar un producto asociado a pedidos.

## Estados de los pedidos

- `CARRITO`
- `CONFIRMADO`
- `CANCELADO`

## Códigos HTTP utilizados

- `200 OK`: operación exitosa.
- `201 Created`: recurso creado.
- `204 No Content`: eliminación exitosa.
- `400 Bad Request`: solicitud o datos inválidos.
- `404 Not Found`: recurso inexistente.
- `409 Conflict`: conflicto con el estado del pedido o el inventario.

## Pruebas

Las pruebas de los endpoints se realizan mediante Postman o Thunder Client. Se deben registrar evidencias de las operaciones exitosas, validaciones, errores y comportamiento del inventario.

## autor
### heyder Alexander Rodriguez cardona
