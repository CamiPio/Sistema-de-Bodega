## Modelo: Datos y Reglas 
 
Usuario: id_usuario, Nombre, Apellido, Correo, Contraseña  

	Regla: 6 caracteres como mínimo, minusculas, mayusculas, números y 	caracteres especiales. No se permiten () - ´”`’   


Catalogo: id_producto, Nombre, Categoría, Descripción, Cantidad, Disponibilidad, Observación  



Préstamo: id_prestamo, Fecha, id_producto, nombre del solicitante, Estado Activo y Observacion de entrega.   

	Regla: por defecto agregar en observación de entega pendiente.


## Vista: pantallas 

Pantalla de Inicio de Sesión (Login) 

Vista Catálogo de Productos 

Vista Detalle de Producto 

Vista de Gestión-Solicitud de Préstamo 

Formulario para Agregar Producto 

Vista para listar los prestamos e incluir botón para marcar devoluciones. 

 

## Controladores 

Registro de usuario: el usuario y contraseña es validado y confirmado, entonces puede acceder al catálogo de bodega. 

Mostrar Catálogo: el usuario elige el producto que desea, se verifica la disponibilidad y se muestra la vista préstamo. 

Busqueda Producto: se filtran los productos por categoría o nombre y se muestran únicamente los disponibles. 

Estado Prestamo: Si el estado del préstamo es activo debe de disminuir la cantidad en el stock del producto en el catálogo, si el estado es inactivo debe aumentar la cantidad en el stock solo si la observación es buen estado. 