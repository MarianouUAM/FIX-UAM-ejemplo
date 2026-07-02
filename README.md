<div align="center">

# FixUAM

### Sistema móvil para reportar incidencias tecnológicas en aulas universitarias

FixUAM es una aplicación móvil desarrollada en **Kotlin** con **Jetpack Compose**, pensada para que docentes puedan reportar fallas tecnológicas dentro del aula y el personal de soporte pueda atenderlas de forma rápida, organizada y eficiente.

<br>

![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-IDE-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-Design-32A0A6?style=for-the-badge)
![Estado](https://img.shields.io/badge/Estado-En%20desarrollo-FFA726?style=for-the-badge)

<br>

**Color principal del proyecto:** `#32A0A6`

</div>

---

## Vista general del proyecto

**FixUAM** Nace como una solución para mejorar la comunicación entre los docentes de la UAM y la Dirección Tecnológica.

En muchas ocasiones, cuando ocurre una falla tecnológica en el aula, el docente debe llamar, escribir a alguien de soporte o buscar ayuda de manera informal. Esto puede provocar retrasos, desorden en la atención y pérdida de tiempo durante la clase.

Con FixUAM, el docente puede reportar el problema directamente desde la aplicación, indicando el aula, el tipo de falla, una descripción y una foto como evidencia. El reporte aparece en el panel del colaborador técnico, quien puede tomarlo, atenderlo y marcarlo como solucionado.

---

## Problema que resuelve

Actualmente, los reportes tecnológicos dentro del aula pueden depender de llamadas personales, mensajes informales o avisos verbales. Esto puede generar:

| Problema | Consecuencia |
|---|---|
| Falta de un canal único de comunicación | Los reportes pueden perderse o duplicarse |
| Dependencia de llamadas personales | El proceso se vuelve poco organizado |
| Poco seguimiento del estado | El docente no sabe si el problema fue atendido |
| Falta de evidencia visual | El técnico puede llegar sin conocer bien la falla |
| Ausencia de historial | No se lleva control claro de incidencias |

FixUAM busca centralizar este proceso en una aplicación sencilla, visual y funcional.

---

## Objetivo general

Desarrollar una aplicación móvil que permita reportar, visualizar y gestionar incidencias tecnológicas dentro de las aulas de la UAM, facilitando la comunicación entre docentes y personal de soporte técnico.

---

## Objetivos específicos

- Permitir que los docentes reporten fallas tecnológicas desde una aplicación móvil.
- Registrar el tipo de problema, aula, descripción y evidencia fotográfica.
- Permitir que el colaborador técnico visualice reportes pendientes.
- Facilitar que el colaborador tome y resuelva reportes.
- Actualizar el estado del reporte para que el docente pueda consultarlo.
- Implementar una interfaz moderna con modo claro y modo oscuro.
- Aplicar conceptos de Programación Orientada a Objetos en la estructura del proyecto.
- Organizar el código en paquetes según responsabilidad.

---

## Roles del sistema

La aplicación contempla tres roles dentro de su propuesta general. En la versión actual se encuentran implementados los flujos de **Docente** y **Colaborador**. El rol de **Administrador** se mantiene como una funcionalidad planificada para futuras versiones.

<table>
<tr>
<th>Rol</th>
<th>Estado</th>
<th>Descripción</th>
<th>Acciones principales</th>
</tr>

<tr>
<td><strong>Docente</strong></td>
<td>Implementado</td>
<td>Usuario que reporta fallas tecnológicas dentro del aula.</td>
<td>
Crear reportes, agregar aula, descripción, evidencia fotográfica, consultar estado y cancelar reportes.
</td>
</tr>

<tr>
<td><strong>Colaborador</strong></td>
<td>Implementado</td>
<td>Personal técnico encargado de atender las incidencias reportadas.</td>
<td>
Ver reportes pendientes, tomar reportes, cambiar estado a en proceso y marcar reportes como solucionados.
</td>
</tr>

<tr>
<td><strong>Administrador</strong></td>
<td>Planificado</td>
<td>Rol pensado para una futura versión administrativa del sistema.</td>
<td>
Gestión de usuarios, revisión general de reportes, estadísticas, asignación de colaboradores y control administrativo.
</td>
</tr>
</table>

---

## Funcionalidades implementadas

| Funcionalidad | Estado |
|---|---|
| Selección de rol | Implementado |
| Pantalla de credenciales | Implementado |
| Login simulado con correo y contraseña | Implementado |
| Panel principal del docente | Implementado |
| Creación de reportes | Implementado |
| Selección del tipo de problema | Implementado |
| Registro del salón o aula | Implementado |
| Descripción del problema | Implementado |
| Adjuntar imagen desde galería | Implementado |
| Tomar foto desde la cámara | Implementado |
| Vista previa antes de enviar el reporte | Implementado |
| Confirmación del reporte enviado | Implementado |
| Historial de reportes del docente | Implementado |
| Detalle del estado del reporte | Implementado |
| Cancelación de reportes | Implementado |
| Panel del colaborador | Implementado |
| Tomar reporte pendiente | Implementado |
| Marcar reporte como solucionado | Implementado |
| Cambio de estado visible para el docente | Implementado |
| Cerrar sesión | Implementado |
| Modo claro y modo oscuro | Implementado |
| Fondo animado | Implementado |
| Transiciones entre pantallas | Implementado |
| Panel del administrador | Implementado |
| Gestión real de usuarios | Pendiente |
| Estadísticas administrativas | Pendiente |

---

## Diseño visual

La aplicación utiliza un diseño moderno, limpio y consistente.

### Paleta principal

| Elemento | Color |
|---|---|
| Color principal | `#32A0A6` |
| Fondo claro | `#F4FAFB` |
| Texto principal | `#102A43` |
| Texto secundario | `#7A8A99` |
| Estado pendiente | Rojo |
| Estado en proceso | Naranja |
| Estado resuelto | Verde / turquesa |

---

## Características de interfaz

- Diseño basado en tarjetas redondeadas.
- Botones principales con el color `#32A0A6`.
- Fondo animado en pantallas de ingreso.
- Modo claro y modo oscuro.
- Transiciones suaves entre pantallas.
- Interfaz coherente para docente y colaborador.
- Formularios simples y fáciles de usar.
- Vista previa del reporte antes de enviarlo.

---

## Flujo general de la aplicación

```mermaid
flowchart TD
    A[Inicio de la aplicación] --> B[Selección de rol]

    B --> C[Docente]
    B --> D[Colaborador]
    B --> E[Administrador]

    C --> F[Ingreso de credenciales]
    D --> G[Ingreso de credenciales]
    E --> H[Ingreso de credenciales]

    F --> I[Panel Docente]
    G --> J[Panel Colaborador]
    H --> K[Panel Administrador pendiente]

    I --> L[Crear y consultar reportes]
    J --> M[Tomar y solucionar reportes]
    K --> N[Funcionalidades planificadas]
