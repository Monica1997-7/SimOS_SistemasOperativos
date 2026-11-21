# SimOS

Simulador de consola que representa la administración de recursos de un sistema operativo
(CPU, concurrencia, memoria, disco y seguridad). Proyecto académico de Ingeniería de
Sistemas; toda la interfaz es de consola y solo se usa el JDK estándar.

## Requisitos

- JDK 17 o superior.
- Maven 3.8 o superior.



## Módulos implementados

El simulador cuenta actualmente con los siguientes módulos funcionales:

 **Módulo 5: Seguridad y Protección (Base)**
  - Inicio de sesión con contraseñas encriptadas mediante hash SHA-256.
  - Control de acceso por roles (Administrador y Usuario).
  - Bitácora de auditoría que registra fecha, hora, usuario, acción y si fue permitida o denegada.

**Módulo 1: Planificación de CPU**
  - Simulación de algoritmos FCFS y Round Robin (con quantum configurable).
  - Cálculo de tiempos de retorno y espera, promedios y generación del diagrama de Gantt en consola.
  - Comparativa automática para indicar cuál algoritmo fue más eficiente.

**Módulo 2: Concurrencia y Sincronización**
  - Simulación de condición de carrera (10 hilos incrementando un contador) y su solución usando mutex (`synchronized`).
  - Implementación del problema Productor-Consumidor con un buffer de tamaño fijo (5 posiciones), utilizando semáforos para controlar los espacios vacíos y llenos.

## Usuarios iniciales

| Usuario | Clave | Rol |
|---------|-------|-----|
| `admin` | `admin123` | Administrador |
| `usuario` | `usuario123` | Usuario |

Las claves están en `src/main/resources/usuarios.txt` como hashes SHA-256. El rol Usuario
no puede entrar al módulo de Seguridad; el Administrador sí.

## Bitácora

Cada login, intento denegado y apertura de módulo se registra en `bitacora.txt` (raíz del
proyecto, ignorado por Git) con el formato:

```
fecha/hora;usuario;acción;resultado
```

Los casos de prueba manuales están en [`docs/casos_prueba.md`](docs/casos_prueba.md).
