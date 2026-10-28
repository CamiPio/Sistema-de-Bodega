# Nombre del proyecto
PROYECTO DEL CURSO DE PROGRAMACIÓN I
Enfoque: Instituciones públicas

## Cómo ejecutar el proyecto

### Requisitos
- JDK 25 instalado y disponible en `PATH`.
- Windows PowerShell (se incluye `mvnw.cmd`, por lo que no es necesario instalar Maven por separado).

### Iniciar la aplicación
Desde la carpeta `sistema-control-inventario-bodega-automotriz`, ejecutar en la terminal:

.\mvnw.cmd spring-boot:run


La primera ejecución puede tardar mientras Maven descarga las dependencias. Cuando la aplicación esté lista, abre [http://localhost:8081](http://localhost:8081) en el navegador. El puerto predeterminado es `8081`; también se puede cambiar definiendo la variable de entorno `PORT`. La base de datos H2 se crea y persiste en `data\bodegadb`.

Para detener la aplicación, presiona `Ctrl+C` en la terminal donde está ejecutándose.

### Crear y ejecutar el JAR
Desde la carpeta `sistema-control-inventario-bodega-automotriz`, crea el JAR ejecutable con:

.\mvnw.cmd package

El archivo se genera en `target\sistema-control-inventario-bodega-automotriz-0.0.1-SNAPSHOT.jar`. Para ejecutarlo:

java -jar .\target\sistema-control-inventario-bodega-automotriz-0.0.1-SNAPSHOT.jar

En Render, define `SPRING_PROFILES_ACTIVE=prod` en las variables de entorno (Render proporciona `PORT` automáticamente) y usa como comando de inicio:

java -jar target/sistema-control-inventario-bodega-automotriz-0.0.1-SNAPSHOT.jar

### Desplegar en Render con Docker
El repositorio incluye un `Dockerfile` en la raíz; Render puede usarlo automáticamente como entorno de ejecución Docker. En Render crea un **Web Service**, conecta este repositorio y deja **Root Directory** vacío para que el contexto sea la raíz del repositorio. Usa el Dockerfile predeterminado (`./Dockerfile`) y deja vacío el comando de inicio: la imagen ya inicia el JAR.

La imagen compila y ejecuta las pruebas con Java 25, y activa el perfil `prod`. Agrega en **Environment** las variables `DB_URL`, `DB_USER` y `DB_PASSWORD` con los datos de Neon. Render proporciona `PORT`; la aplicación lo utiliza automáticamente. No agregues credenciales al Dockerfile ni al repositorio.

### Configurar Neon
El perfil predeterminado es `dev` y conserva H2. Para usar PostgreSQL en Neon desde la PC o en Render, activa el perfil `prod` mediante `SPRING_PROFILES_ACTIVE=prod` y configura estas variables de entorno:

- `DB_URL`: URL JDBC de Neon, con formato `jdbc:postgresql://<host>/<database>?sslmode=require&channelBinding=require`.
- `DB_USER`: usuario de la base de datos.
- `DB_PASSWORD`: contraseña de la base de datos.

En ambos perfiles, el puerto predeterminado es `8081` (o el valor de `PORT` si está definido). En `prod`, la consola H2 se desactiva y Hikari muestra en el log la conexión establecida. Para probar desde la PC, define las tres variables `DB_URL`, `DB_USER` y `DB_PASSWORD` en el entorno del sistema, abre una terminal nueva desde ese entorno, define `SPRING_PROFILES_ACTIVE=prod` y ejecuta:

java -jar .\target\sistema-control-inventario-bodega-automotriz-0.0.1-SNAPSHOT.jar

Confirma en el log `The following 1 profile is active: "prod"`, `HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection@...`, `Tomcat started on port 8081 (http)` y `Started SistemaControlInventarioBodegaAutomotrizApplication`. Si `PORT` tiene otro valor, Tomcat mostrará ese puerto en lugar de `8081`. Mantén las credenciales fuera del repositorio y no las incluyas en comandos que queden guardados en el historial de la terminal.

Las contraseñas nuevas deben tener entre 6 y 72 caracteres, incluir al menos una minúscula, una mayúscula, un número y un carácter especial. No se permiten `()`, `-`, `´`, `”`, `` ` `` ni `’`.

### Ejecutar las pruebas
Desde la carpeta `sistema-control-inventario-bodega-automotriz`, ejecutar:

.\mvnw.cmd test

La aplicación crea una cuenta de administrador inicial al arrancar por primera vez. 

## Funcionalidades
- Registro de beneficiarios/usuarios
- Gestión de datos (CRUD)
- Autenticación por perfiles y roles
- Catálogo de herramientas: ADMIN puede registrar, editar y eliminar; USUARIO puede consultar.
- Desde la barra de menú, cada usuario puede editar sus datos personales y cerrar sesión; los cambios de correo o contraseña mantienen actualizada la autenticación.
- La búsqueda de la barra permite filtrar herramientas por nombre, categoría, descripción u observación.

## Rutas del catálogo
- `GET /catalogo`: consulta del inventario para usuarios autenticados y formulario de registro para ADMIN.
- `POST /catalogo/guardar`: crea o actualiza una herramienta (ADMIN).
- `GET /catalogo/editar/{id}`: carga una herramienta para editar (ADMIN).
- `POST /catalogo/eliminar/{id}`: elimina una herramienta (ADMIN).
- Las cuentas que no sean ADMIN reciben la vista `acceso-denegado` si intentan abrir el formulario de herramientas o cualquier ruta de usuarios.

Los formularios validan campos obligatorios, longitudes y rangos numéricos. Las fechas de préstamo deben ser válidas y no futuras; la disponibilidad se calcula automáticamente como cantidad total menos unidades prestadas, no se puede editar y siempre queda entre cero y la cantidad total. El sistema rechaza préstamos sin existencias y devoluciones duplicadas.

## Tecnologías
- **Lenguaje y ejecución:** Java 25.
- **Framework y servidor web:** Spring Boot 4.1.1, Spring MVC y Spring Boot DevTools.
- **Vistas y presentación:** Thymeleaf, HTML, CSS y JavaScript.
- **Persistencia:** Spring Data JPA, Hibernate y H2 como base de datos predeterminada; PostgreSQL está incluido como dependencia para configuraciones que lo requieran.
- **Seguridad:** Spring Security para autenticación, sesiones, autorización por roles y protección CSRF; BCrypt para almacenar contraseñas como hashes no reversibles.
- **Validación:** Jakarta Bean Validation.

Spring Security protege las rutas y aplica autorización por roles: ADMIN gestiona usuarios y el catálogo; USUARIO consulta el catálogo y gestiona préstamos. Las contraseñas heredadas en texto plano se migran a BCrypt al iniciar el sistema.

## Descripción
Este proyecto resuelve la falta de control en la asignación de herramientas y equipos, problema causado por la gestión manual que actualmente se lleva a cabo en la bodega del taller de Mecánica Automotriz del Instituto Técnico Industrial. Al digitalizar la administración del inventario, el sistema beneficia directamente a estudiantes, instructores y encargados de bodega, pues agiliza la entrega del material, previene extravíos y garantiza que los recursos necesarios estén siempre disponibles y ubicados para cada práctica. 

Para lograrlo, durante el curso se desarrollará una plataforma que incluirá un sistema de autenticación para el acceso seguro mediante roles (administrador y solicitantes: estudiantes y docentes), así como un módulo CRUD para registrar, editar, listar y dar de baja el equipo o señalar herramientas averiadas. Además, la funcionalidad principal será el módulo de préstamos y devoluciones, el cual registrará en tiempo real el ingreso y egreso de cada equipo, guardando un historial detallado con fecha, hora, estado del material, responsable que lo solicitó y el tipo de práctica en la que se utilizó. 

## Lugar a ejecutarse
Instituto Técnico Industrial Chimaltenango, Chimaltenango 

## Autor
- Bilda Quelex - 2396125
- Camila Pío - 2143225
- Victor Sitamul - 2028719 