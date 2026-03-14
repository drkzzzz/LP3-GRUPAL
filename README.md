# 🍹 DrinkGo Point of Sale & E-commerce

Bienvenido al repositorio oficial del proyecto **DrinkGo**. Este documento sirve como guía central para desarrolladores, colaboradores y evaluadores sobre la arquitectura y puesta en marcha del sistema.

## 📜 Descripción del Proyecto
**DrinkGo** es un sistema dual que funciona como un Punto de Venta (POS) multi-sede para la gestión administrativa de inventarios y ventas, y al mismo tiempo como un E-commerce (Storefront) para clientes finales. Su objetivo es implementar una arquitectura moderna y desacoplada, utilizando tecnologías líderes en la industria para el frontend, backend y la gestión de la base de datos.

## 🛠️ Tech Stack

| Área | Tecnología | Propósito |
| :--- | :--- | :--- |
| **Frontend** | React (con Vite) + JavaScript + Tailwind | Creación de una interfaz de usuario moderna, reactiva y modular. |
| **Backend** | Java + Spring Boot | Desarrollo de una API REST robusta, segura y escalable. |
| **Base de Datos** | MySQL | Almacenamiento de datos relacionales. |
| **Versionado de BD** | Scripts SQL nativos / Migraciones | Gestión de estructura, historial de base de datos (`/bd`) y datos base (`/seeds`). |
| **Versionado de Código** | Git & GitHub | Control de versiones y flujo de trabajo colaborativo. |

## 📁 Estructura del Repositorio
Este es un monorepo, lo que significa que tanto el código del frontend como el del backend viven en el mismo repositorio para facilitar su gestión conjunta.

```text
/
├── DrinkGo-backend/     # Proyecto de Spring Boot (API REST)
│   └── src/
│       └── main/
│           ├── java/      # Código fuente de la aplicación (Controladores, Servicios, Entidades)
│           └── resources/ # Configuraciones (application.properties), DB Scripts de migración y Seeds
├── DrinkGo-frontend/    # Proyecto de React JS (Interfaz de Usuario)
│   └── src/             
│       ├── admin/       # Panel de administración (Inventario, Ventas, Reportes, etc.)
│       ├── storefront/  # Interfaz e-commerce para clientes finales
│       └── superadmin/  # Panel de gestión global para todas las sedes
└── README.md            # Documentación central del proyecto
```

## 🚀 Guía de Inicio Rápido (Quickstart)
Sigue estos pasos para tener el proyecto corriendo en tu máquina local.

### 1. Prerrequisitos
Asegúrate de tener instalado el siguiente software:
* Git
* JDK 17 o superior
* Node.js (LTS) 
* Gestor de paquetes: **pnpm** (recomendado para el frontend) o npm.
* MySQL Server

### 2. Clonar el Repositorio
```bash
git clone https://github.com/tu-organizacion/drinkgo.git
# Asegúrate de cambiar la URL por la tuya.
cd drinkgo
```

### 3. Configuración del Backend
1. **Crea la Base de Datos:** Abre tu gestor de MySQL (ej. MySQL Workbench o DBeaver) y crea una base de datos vacía llamada `drinkgo_database`.
2. **Setup de Datos:** Si es tu primera vez, puedes ejecutar los scripts que se encuentran en `DrinkGo-backend/src/main/resources/bd/` (y luego en `/seeds/`) para armar las tablas e insertar data de prueba.
3. **Configura las Variables de Entorno:** Revisa el archivo `application.properties` en `DrinkGo-backend/src/main/resources/` para asegurarte de que tus credenciales de MySQL locales coincidan:
   ```properties
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   ```

### 4. Configuración del Frontend
Navega a la carpeta del frontend y ejecuta la instalación de las dependencias.

```bash
cd DrinkGo-frontend
pnpm install
# (Si usas npm, utiliza: npm install)
```

## ▶️ Cómo Ejecutar el Proyecto
Debes tener dos terminales abiertas, una para el backend y otra para el frontend.

**Ejecutar el Backend:**
1. Abre la carpeta `DrinkGo-backend` en tu IDE (como VS Code, IntelliJ o Eclipse).
2. Ejecuta la aplicación de Spring Boot. Puedes hacerlo por terminal:
   ```bash
   .\mvnw spring-boot:run
   ```
   El servidor se iniciará. La API estará disponible en `http://localhost:8080` (o el puerto configurado).

**Ejecutar el Frontend:**
1. En una nueva terminal, asegúrate de estar en la carpeta `DrinkGo-frontend`.
2. Ejecuta el entorno de desarrollo con Vite:
   ```bash
   pnpm run dev
   ```
3. Abre tu navegador y ve a la URL que te indique la terminal (usualmente `http://localhost:5173`).

## 🤝 Cómo Contribuir
La colaboración es la clave de este proyecto. Todo el trabajo se realiza siguiendo un flujo de trabajo estricto para mantener la calidad y el orden.

* **Flujo de Ramas:** Usamos un modelo Git Flow simplificado (`master`, `feature`).
* **Pull Requests:** Todo cambio debe ser integrado a `master` a través de un Pull Request revisado y aprobado por al menos un miembro del equipo.

## 👥 Autor y el unico

| Nombre | Rol |

| Carlos Sisniegas | Desarrollador |
