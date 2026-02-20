# 🎓 Sistema de Gestión Académica

Aplicación de escritorio desarrollada en **Java con Swing** que implementa los patrones de diseño creacionales **Builder** y **Prototype** aplicados a la gestión de planes de estudio académicos.

---

## 📋 Descripción

El sistema permite a una institución educativa administrar sus **planes de estudio**, **asignaturas** y **docentes**. La característica principal es la capacidad de:

- **Construir** nuevos planes de estudio paso a paso con el patrón Builder.
- **Clonar** planes de semestres anteriores como base para nuevos períodos con el patrón Prototype.

---

## 🎯 Patrones de Diseño Implementados

### 🔨 Builder
Permite construir un `StudyPlan` (plan de estudio) de forma incremental, configurando cada atributo por separado antes de ensamblar el objeto final. El builder valida que todos los campos obligatorios estén presentes antes de permitir la construcción.

```
IStudyPlanBuilder  ←  interfaz con los pasos de construcción
       ↑
StudyPlanBuilder   ←  implementación concreta (fluent API)
       ↓
   StudyPlan       ←  objeto construido
```

**Flujo en la aplicación:**
```
Usuario llena formulario → NewStudyPlanDialog
    → AcademyController.createStudyPlan()
        → StudyPlanBuilder.setName().setPeriod()...build()
            → StudyPlan (objeto completo y validado)
```

---

### 🧬 Prototype
Permite clonar un `StudyPlan` existente realizando una **copia profunda (deep copy)** de todos sus objetos anidados (`Group`, `Teacher`, `Subject`, `Schedule`). El clon es completamente independiente del original.

```
StudyPlan (original)
    └── clone()  →  StudyPlan (copia profunda)
                        ├── Group.clone()
                        │     ├── Subject.clone()
                        │     ├── Teacher.clone()
                        │     └── Schedule.clone()
                        └── ...
```

**Flujo en la aplicación:**
```
Usuario selecciona plan origen → CloneStudyPlanDialog
    → AcademyController.cloneStudyPlan()
        → studyPlan.clone()  (deep copy)
            → nuevo StudyPlan con ID, nombre y período nuevos
```

---

## 🏗️ Arquitectura del Proyecto

El proyecto sigue el patrón **MVC (Model - View - Controller)**:

```
SistemaGestionAcademica/
└── src/
    └── com/
        └── academia/
            │
            ├── Main.java                          # Punto de entrada
            │
            ├── model/                             # Capa de datos (Model)
            │   ├── Subject.java                   # Asignatura
            │   ├── Teacher.java                   # Docente
            │   ├── Schedule.java                  # Horario
            │   ├── Group.java                     # Grupo académico
            │   └── StudyPlan.java                 # Plan de estudio (Prototype)
            │
            ├── patterns/                          # Patrones de diseño
            │   └── builder/
            │       ├── IStudyPlanBuilder.java     # Interfaz Builder
            │       └── StudyPlanBuilder.java      # Implementación Builder
            │
            ├── controller/                        # Capa de control (Controller)
            │   └── AcademyController.java         # Singleton + lógica de negocio
            │
            └── view/                              # Capa de presentación (View)
                ├── MainFrame.java                 # Ventana principal
                ├── utils/
                │   └── UIStyle.java               # Estilos y componentes visuales
                ├── panels/
                │   ├── StudyPlansPanel.java       # Panel de planes de estudio
                │   ├── SubjectsPanel.java         # Panel de asignaturas
                │   └── TeachersPanel.java         # Panel de docentes
                └── dialogs/
                    ├── NewStudyPlanDialog.java    # Diálogo Builder
                    └── CloneStudyPlanDialog.java  # Diálogo Prototype
```

---

## 🧩 Diagrama de Clases (simplificado)

```
         «interface»
      IStudyPlanBuilder
             ▲
             │ implements
      StudyPlanBuilder ──────builds──────► StudyPlan ◄──clone()── StudyPlan
                                               │
                                          List<Group>
                                               │
                              ┌────────────────┼───────────────┐
                              ▼                ▼               ▼
                           Subject          Teacher         Schedule
                        (Cloneable)       (Cloneable)     (Cloneable)
```

---

## ⚙️ Requisitos

| Herramienta | Versión mínima |
|-------------|---------------|
| Java JDK    | 17 o superior |
| Java Swing  | Incluido en JDK |
| IDE         | VS Code, IntelliJ, Eclipse |

---

## 🚀 Instalación y Ejecución

### 1. Clonar o descargar el proyecto

```bash
git clone https://github.com/tu-usuario/SistemaGestionAcademica.git
cd SistemaGestionAcademica
```

### 2. Compilar

**Linux / macOS:**
```bash
mkdir -p bin
javac -d bin $(find src -name "*.java")
```

**Windows (CMD):**
```cmd
mkdir bin
for /r src %f in (*.java) do javac -d bin "%f"
```

**Windows (PowerShell):**
```powershell
New-Item -ItemType Directory -Force -Path bin
Get-ChildItem -Recurse -Filter "*.java" src | ForEach-Object { javac -d bin $_.FullName }
```

### 3. Ejecutar

```bash
java -cp bin com.academia.Main
```

### Desde VS Code

1. Instalar la extensión **Extension Pack for Java** (Microsoft).
2. Abrir la carpeta raíz del proyecto (`Archivo → Abrir Carpeta`).
3. Abrir `Main.java` y hacer clic en **▶ Run** sobre el método `main`.

---

## 🖥️ Funcionalidades

### Planes de Estudio
| Acción | Patrón usado | Descripción |
|--------|-------------|-------------|
| Nuevo Plan | 🔨 Builder | Construye un plan configurando nombre, período, programa, modalidad, fechas y grupos. |
| Clonar Plan | 🧬 Prototype | Duplica un plan existente con deep copy para un nuevo período académico. |
| Eliminar Plan | — | Elimina el plan seleccionado de la lista. |
| Ver Grupos | — | Al seleccionar un plan, muestra sus grupos en el panel inferior. |

### Asignaturas
- Agregar nuevas asignaturas con nombre, créditos, modalidad y descripción.
- Eliminar asignaturas existentes.

### Docentes
- Registrar docentes con nombre, apellido, especialidad, email y teléfono.
- Eliminar docentes del sistema.

---

## 📦 Datos de Ejemplo

Al iniciar la aplicación se cargan automáticamente datos de ejemplo:

**Docentes:**
- Carlos Ramírez — Algoritmos
- Laura Torres — Base de Datos
- Andrés Molina — Redes

**Asignaturas:**
- Algoritmos y Estructuras de Datos (4 créditos)
- Bases de Datos (3 créditos)
- Redes de Computadores (3 créditos)
- Ingeniería de Software (4 créditos)

**Plan de estudio inicial:**
- Plan 2024-I — Ingeniería de Sistemas (4 grupos, 14 créditos)

---

## 👥 Autores

| Nombre | Rol |
|--------|-----|
| _(Tu nombre aquí)_ | Desarrollador principal |

---

## 📄 Licencia

Este proyecto fue desarrollado con fines académicos para demostrar la implementación de los patrones de diseño **Builder** y **Prototype** en Java.
