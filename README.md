<div align="center">
  <img src="https://raw.githubusercontent.com/fabioo66/donde-estas-volve-a-casa/main/angular/public/assets/images/mascota-default.svg" alt="Dónde Estás, Volvé a Casa" width="200"/>
  
  # 🐾 Dónde Estás, Volvé a Casa
  
  ### Plataforma web para la búsqueda y reporte de mascotas perdidas
  
  ![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=for-the-badge&logo=springboot)
  ![Angular](https://img.shields.io/badge/Angular-21.0-red?style=for-the-badge&logo=angular)
  ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
  ![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)
  ![Estado](https://img.shields.io/badge/Estado-En%20Desarrollo-yellow?style=for-the-badge)
  
</div>

---

## 📑 Índice

- [Descripción del Proyecto](#-descripción-del-proyecto)
- [Estado del Proyecto](#-estado-del-proyecto)
- [Demostración de Funciones](#-demostración-de-funciones)
- [Acceso al Proyecto](#-acceso-al-proyecto)
- [Tecnologías Utilizadas](#️-tecnologías-utilizadas)
- [Características Principales](#-características-principales)
- [Instalación y Configuración](#️-instalación-y-configuración)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Personas Contribuyentes](#-personas-contribuyentes)
- [Personas Desarrolladoras](#-personas-desarrolladoras)
- [Licencia](#-licencia)

---

## 📖 Descripción del Proyecto

**Dónde Estás, Volvé a Casa** es una plataforma web desarrollada como proyecto final de la materia **Taller de Tecnologías de Producción de Software (TTPS)** de la Universidad Nacional de La Plata. 

El sistema permite a los usuarios:
- 📢 **Publicar mascotas perdidas** con información detallada (fotos, descripción, ubicación)
- 👁️ **Reportar avistamientos** de mascotas perdidas con geolocalización
- 🗺️ **Visualizar en un mapa interactivo** las mascotas perdidas y sus avistamientos
- 🔐 **Gestionar perfiles de usuario** con autenticación JWT
- 📊 **Panel de administración** para moderar contenido

La plataforma busca facilitar el reencuentro de mascotas con sus familias mediante una comunidad colaborativa y herramientas tecnológicas modernas.

---

## 📊 Estado del Proyecto

<div align="center">
  
  ![Estado](https://img.shields.io/badge/Estado-🚧%20En%20Desarrollo%20Activo-yellow?style=for-the-badge)
  
</div>

### ✅ Funcionalidades Implementadas
- ✔️ Sistema de autenticación y autorización con JWT
- ✔️ CRUD completo de mascotas perdidas
- ✔️ Sistema de avistamientos con geolocalización
- ✔️ Visualización en mapa interactivo con Leaflet
- ✔️ Carga y gestión de imágenes
- ✔️ Panel de usuario con mis publicaciones
- ✔️ API RESTful documentada con Swagger

### 🔄 En Desarrollo
- 🔨 Sistema de notificaciones en tiempo real
- 🔨 Filtros avanzados de búsqueda
- 🔨 Sistema de mensajería entre usuarios
- 🔨 Estadísticas y reportes

---

## 🎬 Demostración de Funciones

### Funcionalidades Principales

#### 1️⃣ Registro y Autenticación de Usuarios
- Registro de nuevos usuarios con validación de datos
- Login con JWT para sesiones seguras
- Recuperación de contraseña (en desarrollo)

#### 2️⃣ Gestión de Mascotas Perdidas
- **Publicar mascota perdida**: Formulario completo con:
  - Nombre, descripción, especie, raza, color
  - Múltiples fotos
  - Ubicación de pérdida con mapa interactivo
  - Fecha y hora de pérdida
  - Información de contacto
  
- **Ver mascotas perdidas**: Lista y mapa de todas las mascotas perdidas activas
- **Editar/Eliminar publicaciones**: Gestión de publicaciones propias
- **Cambiar estado**: Marcar mascota como encontrada

#### 3️⃣ Sistema de Avistamientos
- **Reportar avistamiento**: Cualquier usuario puede reportar haber visto una mascota
- **Geolocalización**: Marcador en mapa con ubicación del avistamiento
- **Fotos del avistamiento**: Carga de imágenes para confirmar identidad
- **Notificación al dueño**: El propietario recibe información del avistamiento

#### 4️⃣ Mapa Interactivo
- Visualización de todas las mascotas perdidas
- Marcadores diferenciados por estado
- Clusters de avistamientos por mascota
- Navegación intuitiva con zoom y filtros

#### 5️⃣ Panel de Usuario
- Gestión de perfil personal
- Visualización de mis publicaciones
- Historial de avistamientos reportados
- Estadísticas personales

---

## 🚀 Acceso al Proyecto

### 🔗 Repositorio
```bash
https://github.com/fabioo66/donde-estas-volve-a-casa.git
```

### 📦 Clonar el Repositorio
```bash
git clone https://github.com/fabioo66/donde-estas-volve-a-casa.git
cd donde-estas-volve-a-casa
```

### 🌐 URL de Producción
```
🚧 En desarrollo - Próximamente disponible
```

### 📚 Documentación API
Una vez levantado el servidor backend, la documentación Swagger estará disponible en:
```
http://localhost:8080/swagger-ui.html
```

---

## 🛠️ Tecnologías Utilizadas

### Backend
| Tecnología | Versión | Uso |
|------------|---------|-----|
| ![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk) | 21 | Lenguaje principal |
| ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?logo=springboot) | 3.2.0 | Framework backend |
| ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-3.2.0-green?logo=spring) | 3.2.0 | Persistencia de datos |
| ![Spring Security](https://img.shields.io/badge/Spring%20Security-3.2.0-green?logo=springsecurity) | 3.2.0 | Autenticación y autorización |
| ![Hibernate](https://img.shields.io/badge/Hibernate-6.3-59666C?logo=hibernate) | 6.3 | ORM |
| ![JWT](https://img.shields.io/badge/JWT-0.12.6-000000?logo=jsonwebtokens) | 0.12.6 | Tokens de autenticación |
| ![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql) | 8.0 | Base de datos |
| ![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?logo=apachemaven) | 3.9+ | Gestión de dependencias |
| ![Swagger](https://img.shields.io/badge/Swagger-2.2.0-85EA2D?logo=swagger) | 2.2.0 | Documentación API |

### Frontend
| Tecnología | Versión | Uso |
|------------|---------|-----|
| ![Angular](https://img.shields.io/badge/Angular-21.0-DD0031?logo=angular) | 21.0 | Framework frontend |
| ![TypeScript](https://img.shields.io/badge/TypeScript-5.4+-3178C6?logo=typescript) | 5.4+ | Lenguaje tipado |
| ![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-4.1-06B6D4?logo=tailwindcss) | 4.1 | Estilos y diseño |
| ![Leaflet](https://img.shields.io/badge/Leaflet-1.9.4-199900?logo=leaflet) | 1.9.4 | Mapas interactivos |
| ![RxJS](https://img.shields.io/badge/RxJS-7.8-B7178C?logo=reactivex) | 7.8 | Programación reactiva |
| ![Angular SSR](https://img.shields.io/badge/Angular%20SSR-21.0-DD0031?logo=angular) | 21.0 | Server-Side Rendering |

### DevOps y Herramientas
| Herramienta | Uso |
|-------------|-----|
| ![Docker](https://img.shields.io/badge/Docker-Latest-2496ED?logo=docker) | Contenedorización de MySQL |
| ![Git](https://img.shields.io/badge/Git-2.40+-F05032?logo=git) | Control de versiones |
| ![Postman](https://img.shields.io/badge/Postman-Latest-FF6C37?logo=postman) | Testing de API |

---

## ✨ Características Principales

### 🔐 Seguridad
- Autenticación JWT con refresh tokens
- Encriptación de contraseñas con BCrypt
- Protección de rutas con Guards en Angular
- Interceptores HTTP para manejo automático de tokens
- Validación de datos en backend y frontend

### 🗺️ Geolocalización
- Integración con Leaflet Maps
- Marcadores personalizados
- Cálculo de distancias
- Geolocalización del navegador
- Clusters para mejor visualización

### 📸 Gestión de Imágenes
- Carga múltiple de archivos
- Validación de formatos (JPG, PNG, JPEG)
- Almacenamiento en servidor
- Previsualización antes de subir
- Optimización de tamaño

### 📱 Diseño Responsivo
- Mobile-first con Tailwind CSS
- Adaptación a diferentes tamaños de pantalla
- Interfaz intuitiva y moderna
- Accesibilidad mejorada

---

## 🛠️ Instalación y Configuración

### Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- ☕ **Java Development Kit (JDK) 21** - [Descargar](https://www.oracle.com/java/technologies/downloads/#java21)
- 📦 **Maven 3.9+** - [Descargar](https://maven.apache.org/download.cgi)
- 🐳 **Docker** - [Descargar](https://www.docker.com/get-started)
- 🟢 **Node.js 18+** y **npm** - [Descargar](https://nodejs.org/)
- 💾 **MySQL 8.0** (opcional si usas Docker)
- 🔧 **Git** - [Descargar](https://git-scm.com/downloads)

---

### 📥 Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/fabioo66/donde-estas-volve-a-casa.git
cd donde-estas-volve-a-casa
```

---

### 🐳 Paso 2: Configurar Base de Datos con Docker

#### En Windows (PowerShell):
```powershell
docker run -d --name proyectoTTPS -e MYSQL_ROOT_PASSWORD=valen -e MYSQL_DATABASE=proyectoTTPS -e MYSQL_USER=valen -e MYSQL_PASSWORD=valen -p 3307:3306 mysql:8.0
```

#### En Linux/Mac:
```bash
docker run -d \
  --name proyectoTTPS \
  -e MYSQL_ROOT_PASSWORD=valen \
  -e MYSQL_DATABASE=proyectoTTPS \
  -e MYSQL_USER=valen \
  -e MYSQL_PASSWORD=valen \
  -p 3307:3306 \
  mysql:8.0
```

#### Verificar que el contenedor está corriendo:
```bash
docker ps
```

---

### ⚙️ Paso 3: Configurar el Backend

1. **Navegar al directorio raíz del proyecto** (si no estás ahí):
   ```bash
   cd donde-estas-volve-a-casa
   ```

2. **Revisar configuración** en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3307/proyectoTTPS
   spring.datasource.username=valen
   spring.datasource.password=valen
   ```

3. **Compilar el proyecto**:
   ```bash
   mvn clean install
   ```

4. **Inicializar la base de datos**:
   ```bash
   mvn exec:java -Dexec.mainClass="ttps.utils.DatabaseInitializer"
   ```
   
   O ejecutar manualmente desde tu IDE:
   - Abrir `src/main/java/ttps/utils/DatabaseInitializer.java`
   - Ejecutar el método `main()`

5. **Ejecutar el backend**:
   ```bash
   mvn spring-boot:run
   ```
   
   El servidor estará disponible en: `http://localhost:8080`

6. **Verificar la API**:
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - API Health: `http://localhost:8080/actuator/health`

---

### 🎨 Paso 4: Configurar el Frontend

1. **Navegar al directorio de Angular**:
   ```bash
   cd angular
   ```

2. **Instalar dependencias**:
   ```bash
   npm install
   ```

3. **Configurar la URL del backend** (si es necesario):
   
   Editar el archivo de entorno si tu backend no está en `http://localhost:8080`:
   ```typescript
   // src/environments/environment.ts
   export const environment = {
     production: false,
     apiUrl: 'http://localhost:8080'
   };
   ```

4. **Ejecutar el servidor de desarrollo**:
   ```bash
   npm start
   ```
   
   O alternativamente:
   ```bash
   ng serve
   ```
   
   La aplicación estará disponible en: `http://localhost:4200`

5. **Build para producción** (opcional):
   ```bash
   npm run build
   ```

---

### ✅ Paso 5: Verificar la Instalación

1. **Backend**:
   - ✔️ El servidor Spring Boot debe estar corriendo en el puerto 8080
   - ✔️ Swagger UI debe ser accesible
   - ✔️ La base de datos debe tener las tablas creadas

2. **Frontend**:
   - ✔️ La aplicación Angular debe estar corriendo en el puerto 4200
   - ✔️ La página de inicio debe cargar correctamente
   - ✔️ Puedes registrarte y hacer login

3. **Base de Datos**:
   - ✔️ El contenedor Docker debe estar corriendo
   - ✔️ Puedes conectarte con un cliente MySQL:
     ```bash
     mysql -h 127.0.0.1 -P 3307 -u valen -pvalen proyectoTTPS
     ```

---

### 🧪 Paso 6: Ejecutar Tests (Opcional)

#### Tests del Backend:
```bash
mvn test
```

#### Tests del Frontend:
```bash
cd angular
npm test
```

---

### 🔧 Solución de Problemas Comunes

#### ❌ Error: Puerto 3307 ya en uso
```bash
# Ver qué proceso usa el puerto
netstat -ano | findstr :3307

# Detener el contenedor existente
docker stop proyectoTTPS
docker rm proyectoTTPS
```

#### ❌ Error: No se puede conectar a MySQL
- Verificar que el contenedor está corriendo: `docker ps`
- Verificar los logs: `docker logs proyectoTTPS`
- Esperar unos segundos después de levantar el contenedor

#### ❌ Error: Puerto 8080 ya en uso
- Cambiar el puerto en `application.properties`:
  ```properties
  server.port=8081
  ```

#### ❌ Error: npm install falla
- Limpiar caché: `npm cache clean --force`
- Eliminar `node_modules`: `rm -rf node_modules`
- Reinstalar: `npm install`

---

### 🚀 Scripts Útiles

#### Backend:
```bash
# Limpiar y compilar
mvn clean install

# Ejecutar sin tests
mvn spring-boot:run -DskipTests

# Generar JAR
mvn package

# Ver dependencias
mvn dependency:tree
```

#### Frontend:
```bash
# Desarrollo
npm start

# Build producción
npm run build

# Watch mode
npm run watch

# Linting
ng lint

# Tests
npm test
```

#### Docker:
```bash
# Ver logs
docker logs proyectoTTPS

# Acceder al contenedor
docker exec -it proyectoTTPS bash

# Detener y eliminar
docker stop proyectoTTPS
docker rm proyectoTTPS

# Ver todos los contenedores
docker ps -a
```

---

### 📝 Notas Adicionales

- **Cambiar credenciales**: Si deseas usar otras credenciales de MySQL, actualiza tanto el comando Docker como `application.properties`
- **Puerto del frontend**: Por defecto Angular usa el 4200, pero podes cambiarlo con `ng serve --port 4201`
- **Hot reload**: Ambos servidores (Spring Boot con DevTools y Angular) soportan hot reload
- **CORS**: Ya está configurado en el backend para aceptar peticiones desde `http://localhost:4200`

---

## 📁 Estructura del Proyecto

```
donde-estas-volve-a-casa/
│
├── 📂 src/main/java/ttps/spring/          # Backend - Spring Boot
│   ├── controllers/                        # Controladores REST
│   │   ├── MascotaController.java
│   │   ├── AvistamientoController.java
│   │   ├── UsuarioController.java
│   │   └── AuthController.java
│   ├── models/                             # Entidades JPA
│   │   ├── Mascota.java
│   │   ├── Avistamiento.java
│   │   ├── Usuario.java
│   │   └── UsuarioRegistrado.java
│   ├── repositories/                       # Repositorios JPA
│   ├── services/                           # Lógica de negocio
│   ├── dto/                                # Data Transfer Objects
│   ├── config/                             # Configuraciones
│   │   ├── SecurityConfig.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── CorsConfig.java
│   └── utils/                              # Utilidades
│       └── DatabaseInitializer.java
│
├── 📂 src/main/resources/
│   ├── application.properties              # Configuración Spring
│   └── META-INF/persistence.xml
│
├── 📂 angular/                             # Frontend - Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── home/                       # Componente principal
│   │   │   ├── login/                      # Autenticación
│   │   │   ├── registro/                   # Registro de usuarios
│   │   │   ├── mascota/                    # Gestión de mascotas
│   │   │   │   ├── mascota-list/
│   │   │   │   ├── mascota-form/
│   │   │   │   └── mascota-edit/
│   │   │   ├── avistamientos/              # Avistamientos
│   │   │   ├── mis-publicaciones/          # Panel de usuario
│   │   │   ├── perfil/                     # Perfil de usuario
│   │   │   ├── services/                   # Servicios Angular
│   │   │   │   ├── mascota.service.ts
│   │   │   │   ├── auth.service.ts
│   │   │   │   └── avistamiento.service.ts
│   │   │   ├── models/                     # Modelos TypeScript
│   │   │   ├── guards/                     # Route Guards
│   │   │   ├── interceptors/               # HTTP Interceptors
│   │   │   └── utils/                      # Utilidades
│   │   ├── assets/                         # Recursos estáticos
│   │   └── styles.css                      # Estilos globales
│   ├── package.json
│   └── angular.json
│
├── 📂 uploads/                             # Almacenamiento de imágenes
├── 📂 target/                              # Compilados Maven
├── pom.xml                                 # Dependencias Maven
├── README.md                               # Este archivo
└── .gitignore
```

---

## 🤝 Personas Contribuyentes

Agradecemos a todas las personas que han contribuido a este proyecto:

<div align="center">
  
| Contribución | Descripción |
|--------------|-------------|
| 💻 Code reviews | Revisión de código y mejoras |
| 🐛 Bug reports | Reporte de errores y problemas |
| 📖 Documentación | Mejoras en la documentación |
| 💡 Ideas | Sugerencias de funcionalidades |
| 🎨 Diseño | Aportes al diseño UI/UX |

</div>

**¿Quieres contribuir?** 

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 👨‍💻 Personas Desarrolladoras

<div align="center">

### Equipo de Desarrollo

Este proyecto fue desarrollado como trabajo final de la materia **Taller de Tecnologías de Producción de Software (TTPS)** de la **Facultad de Informática - UNLP**.

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/fabioo66">
        <img src="https://github.com/fabioo66.png" width="100px;" alt="Fabio"/>
        <br />
        <sub><b>Fabio Torrejon</b></sub>
      </a>
      <br />
      <sub>Backend & DevOps</sub>
    </td>
    <td align="center">
      <a href="https://github.com/valenaruanno">
        <img src="https://github.com/valenaruanno.png" width="100px;" alt="Fabio"/>
        <br />
        <sub><b>Valentin Aruanno</b></sub>
      </a>
      <br />
      <sub>Backend & DevOps</sub>
    </td><td align="center">
      <a href="https://github.com/Diego-JPH">
        <img src="https://github.com/Diego-JPH.png" width="100px;" alt="Fabio"/>
        <br />
        <sub><b>Diego Pingo Hisbes</b></sub>
      </a>
      <br />
      <sub>Backend & DevOps</sub>
    </td>
  </tr>
</table>

### 🎓 Institución

**Facultad de Informática - Universidad Nacional de La Plata**  
Materia: Taller de Tecnologías de Producción de Software (TTPS)  
Año: 2025

</div>

---

## 📞 Contacto

¿Preguntas, sugerencias o reportar un problema?

- 📧 **Email**: [tu-email@ejemplo.com](mailto:tu-email@ejemplo.com)
- 🐛 **Issues**: [Reportar un problema](https://github.com/fabioo66/donde-estas-volve-a-casa/issues)
- 💬 **Discussions**: [Foro del proyecto](https://github.com/fabioo66/donde-estas-volve-a-casa/discussions)

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

```
MIT License

Copyright (c) 2026 Dónde Estás, Volvé a Casa - TTPS UNLP

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<div align="center">
  
### ⭐ Si este proyecto te fue útil, no olvides darle una estrella ⭐

### 🐾 Ayudemos juntos a que las mascotas vuelvan a casa 🐾

---

**Desarrollado con ❤️ por estudiantes de la Facultad de Informática - UNLP**

[⬆ Volver arriba](#-dónde-estás-volvé-a-casa)

</div>
