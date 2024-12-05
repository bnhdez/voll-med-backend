# API REST de Voll Clínica

Este proyecto es una API REST diseñada para gestionar la información de médicos, usuarios y autenticación de usuarios en la plataforma "Voll Clínica". La API está construida con **Spring Boot** y utiliza **MySQL** como base de datos. Para la autenticación y autorización, se emplea **JWT (JSON Web Token)**.

## Tecnologías utilizadas

- **Java 17**: Lenguaje de programación.
- **Spring Boot 3.x**: Framework para crear aplicaciones Java.
- **Spring Security**: Para la autenticación y autorización de usuarios.
- **JWT (JSON Web Token)**: Para la gestión de tokens de acceso.
- **JPA (Java Persistence API)**: Para la interacción con la base de datos.
- **MySQL**: Sistema de gestión de base de datos.
- **Flyway**: Herramienta para la migración de base de datos.
- **Lombok**: Biblioteca para reducir la verbosidad del código (eliminación de getters, setters, constructores).
- **Insomnia**: Herramienta para testear la API y hacer consultas HTTP.

## Requisitos

- **JDK 17** o superior.
- **MySQL** instalado localmente o en un servidor.
- **Maven** para gestionar dependencias.
- **Insomnia** o cualquier otra herramienta para hacer pruebas de API.

## Configuración

### 1. Configuración de la base de datos

En el archivo `application.properties`, debes configurar los datos de acceso a tu base de datos MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vollmed_api
spring.datasource.username=root
spring.datasource.password=tu_contraseña
```

Asegúrate de que la base de datos `vollmed_api` esté creada en MySQL antes de ejecutar el proyecto. Flyway se encargará de gestionar las migraciones.

### 2. Configuración del archivo `application.properties`

Este archivo también incluye la configuración del secreto de JWT, que debe mantenerse privado:

```properties
api.security.secret=${JWT_SECRET:123456} # Secreto utilizado para generar y verificar JWT.
```

### 3. Dependencias de Maven

El archivo `pom.xml` tiene todas las dependencias necesarias, como Spring Boot, MySQL, Flyway, JWT, Lombok y Spring Security.

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    ...
    <dependency>
        <groupId>com.auth0</groupId>
        <artifactId>java-jwt</artifactId>
        <version>4.2.0</version>
    </dependency>
</dependencies>
```

## Endpoints

### Autenticación de Usuario

- **POST /login**: Permite a los usuarios autenticarse. Acepta las credenciales y devuelve un token JWT.

#### Ejemplo de solicitud:

```json
{
  "login": "usuario",
  "clave": "contraseña"
}
```

#### Respuesta:

```json
{
  "jwTtoken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Gestión de Médicos

#### Registrar Médico

- **POST /medicos**: Registra un nuevo médico en la base de datos.

#### Listar Médicos

- **GET /medicos**: Devuelve una lista paginada de médicos activos.

#### Actualizar Médico

- **PUT /medicos**: Actualiza los datos de un médico existente.

#### Eliminar Médico

- **DELETE /medicos/{id}**: Desactiva un médico (eliminación lógica).

#### Detalles del Médico

- **GET /medicos/{id}**: Devuelve los detalles de un médico específico.

## Archivos clave del proyecto

A continuación, se presentan los archivos más importantes para comprender la lógica y el funcionamiento del proyecto:

### 1. **Autenticación y autorización con JWT**

- **`AutenticacionController.java`**: Este controlador maneja la autenticación de los usuarios mediante el uso de **JWT**. El endpoint `POST /login` recibe las credenciales del usuario y devuelve un token JWT.

```java
@PostMapping
public ResponseEntity autenticarUsuario(@RequestBody @Valid DatosAutenticacionUsuario datosAutenticacionUsuario) {
    Authentication authToken = new UsernamePasswordAuthenticationToken(datosAutenticacionUsuario.login(),
            datosAutenticacionUsuario.clave());
    var usuarioAutenticado = authenticationManager.authenticate(authToken);
    var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
    return ResponseEntity.ok(new DatosJWTToken(JWTtoken));
}
```

- **`TokenService.java`**: Este servicio es responsable de generar y validar el token JWT.

```java
public String generarToken(Usuario usuario) {
    try {
        Algorithm algorithm = Algorithm.HMAC256(apiSecret);
        return JWT.create()
                .withIssuer("voll med")
                .withSubject(usuario.getLogin())
                .withClaim("id", usuario.getId())
                .withExpiresAt(generarFechaExpiracion())
                .sign(algorithm);
    } catch (JWTCreationException exception) {
        throw new RuntimeException();
    }
}
```

### 2. **Seguridad y filtro de autenticación**

- **`SecurityConfigurations.java`**: Aquí se configuran las reglas de seguridad, como la autenticación y autorización de los usuarios. Se desactiva la protección CSRF, se establece la política de sesiones y se especifica que las peticiones deben ser autenticadas.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityFilter securityFilter) throws Exception {
    return http.csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(req -> {
                req.requestMatchers(HttpMethod.POST, "/login").permitAll();
                req.anyRequest().authenticated();
            })
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
}
```

### 3. **Operaciones CRUD de médicos**

- **`MedicoController.java`**: Este controlador maneja las operaciones CRUD de los médicos, como registrar, actualizar, eliminar y listar médicos.

```java
@PostMapping
public ResponseEntity<DatosRespuestaMedico> registrarMedico(@RequestBody @Valid DatosRegistroMedico datosRegistroMedico,
                                                            UriComponentsBuilder uriComponentsBuilder) {
    Medico medico = medicoRepository.save(new Medico(datosRegistroMedico));
    URI url = uriComponentsBuilder.path("medicos/{id}").buildAndExpand(medico.getId()).toUri();
    return ResponseEntity.created(url).body(datosRespuestaMedico);
}
```

### 4. **Modelo de datos y entidades**

- **`Medico.java`**: Esta es la entidad principal que representa a un médico. Utiliza **JPA** para mapearse a la tabla `medicos` en la base de datos.

```java
@Entity(name = "Medico")
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String documento;
    @Enumerated(EnumType.STRING)
    private Especialidad especialidad;
    @Embedded
    private Direccion direccion;
    private boolean activo;
}
```

### 5. **Validación y manejo de errores**

- **`TratadorDeErrores.java`**: Este componente maneja los errores globalmente en la API, como errores 404 (no encontrado) y 400 (bad request) por validaciones fallidas.

```java
@ExceptionHandler(EntityNotFoundException.class)
public ResponseEntity tratarError404() {
    return ResponseEntity.notFound().build();
}
```

---

## Cómo probar la API

1. **Insomnia**: Puedes usar Insomnia o cualquier otra herramienta de pruebas de API para realizar solicitudes a los endpoints.
2. **Pruebas comunes**:
    - Realiza un `POST` a `/login` con las credenciales de un usuario para obtener el JWT.
    - Usa este token en las cabeceras `Authorization: Bearer <JWT>` para autenticarte en las demás rutas.

