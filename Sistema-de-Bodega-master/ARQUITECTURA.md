## Modelo: Datos y Reglas 
 
Usuario: Nombre, Apellido, Teléfono, Correo, Rol, Contraseña  

	Regla: 6 caracteres como mínimo, minusculas, mayusculas, números y 	caracteres especiales. No se permiten () - ´”`’   


Catalogo: Nombre, Categoría, Descripción, Cantidad, Disponibilidad, Estado, Observación, id_categoría  


Categoría: Nombre, Descripción,   


Préstamo: Fecha, id_producto, id_usuario, Referencia, Tiempo   


Regla: Si la disponibilidad es mayor a 0 se realiza el prestamo  

  
Devolución: Fecha, id_prestamo, Estado, Observación 


## Vista: pantallas 

Pantalla de Inicio de Sesión (Login) 

Formulario de Registro de Usuario 

Vista Catálogo de Productos 

Vista Detalle de Producto 

Vista de Gestión-Solicitud de Préstamo 

Formulario para Agregar Producto 

 

## Controladores 

Registro de usuario: el usuario y contraseña es validado y confirmado, entonces puede acceder al catálogo de bodega. 


Se muestra el catálogo: el usuario elige el producto que desea, se verifica la disponibilidad y se muestra la vista préstamo. 

 
Préstamo: los datos del usuario y producto son validados y se muestra un mensaje “Préstamo aprobado” o “Préstamo rechazado”. 


Devolución: se verifican los datos del préstamo y se agrega nuevamente el producto a la disponibilidad en catálogo.  