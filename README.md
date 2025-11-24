# Proyectobackend - Productos Similares

Este proyecto es un pequeño backend para crear una API intermedia para obtener productos similares a uno dado su id

El proyecto consultará un endpoint disponible en el pueto 3001 (proyecto "simulado" ejecutado en docker)

Devolverá el json de productos similares.

## Características y Consideraciones

1. Código optimizado:
- Procesamiento paralelo con CompletableFuture para obtener los detalles de producto de forma concurrente.
- Se establece un timeout apropiado para las llamadas a la API externas (5 secgundos para conexión y lectura).

2. Resiliencia:
- Se añade control de errores para los fallos en las llamadas a la API.
- URL de API externa configurable.

3. Claridad de código y fácil mantenimiento:
- Separación entre controlador, servicio y modelo.
- Métodos con nombres claros y documentación Javadoc.
- Configured centralizada en un archivo

4. Cumple el contrato de la API:
- Implementado la estructura como se especifica en el contrato .
- Devuelve la respuesta correspondiente al esquema
- Se ejecuta en el puerto 5000 como se especifica en los requisitos
