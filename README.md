# SimOS

Simulador de consola que representa la administración de recursos de un sistema operativo
(CPU, concurrencia, memoria, disco y seguridad). Proyecto académico de Ingeniería de
Sistemas; toda la interfaz es de consola y solo se usa el JDK estándar.

## Requisitos

- JDK 17 o superior.
- Maven 3.8 o superior.

## Comandos

```bash
mvn -q compile
mvn -q exec:java -Dexec.mainClass=simos.Main
mvn -q package
java -jar target/simos.jar
```

## Estado actual

Fase 1 completada: base del proyecto, login, roles, permisos, bitácora y módulo de
Seguridad. Fase 2 en curso: Módulo 1 (Planificación de CPU) con FCFS y Round Robin,
entrada por teclado o desde `src/main/resources/procesos.txt` (formato
`nombre;llegada;rafaga`). Los módulos de concurrencia, memoria y almacenamiento se
habilitarán en fases posteriores (por ahora muestran un aviso).

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
