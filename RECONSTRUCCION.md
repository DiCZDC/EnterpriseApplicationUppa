# Úppa: reconstrucción en Java EE 8 (Payara 5)

Este documento explica cómo se reconstruyó **Úppa**, el sistema de reconocimiento de hongos, sobre el proyecto NetBeans `EnterpriseApplication2` (Java EE 8, Java 17, Payara 5). También indica cómo configurarlo y ejecutarlo en otro equipo.

Proyectos de origen:

| Proyecto | Tecnología | Función |
|---|---|---|
| `uppa-back` | Python 3, FastAPI, Ultralytics YOLO | API `POST /analizar` que identifica la especie y el estado de salud a partir de una foto |
| `Uppa-front` | Laravel 13, Livewire 4, Flux UI, Fortify, SQLite | Interfaz web: login y registro, subida o captura de la foto, resultado, gráficas |

---

## 1. Arquitectura resultante

```
Navegador (JSF + PrimeFaces 13)
      │  http(s)://<host>:8080/EnterpriseApplication2-war/
      ▼
┌────────────────────────── Payara 5 (EAR EnterpriseApplication2) ──────────────────────────┐
│ WAR  EnterpriseApplication2-war                                                           │
│   vistas/*.xhtml  ──►  CDIs.ASesion · CDIs.AReconocimiento · CDIs.AAnalisis               │
│                        CDIs.AEnfermedad · com.uppa.beans.GraficasBean                     │
│   filtros.FiltroAutenticacion (protege /vistas/* y /imagenes/*)                           │
│   servlets.ImagenServlet (/imagenes/*, entrega las fotos guardadas)                       │
│                                   │ @EJB                                                  │
│ EJB  EnterpriseApplication2-ejb   ▼                                                       │
│   logica.MReconocimiento ─► logica.MUppaApi ──HTTP multipart──► uppa-api (Python :8000)   │
│   logica.MUsuario · logica.MEnfermedad · logica.MGraficas · logica.InicializadorDatos     │
│   dao.*Facade  ──JPA (EclipseLink)──► jdbc/uppa ──► PostgreSQL "uppa"                     │
└───────────────────────────────────────────────────────────────────────────────────────────┘
```

**Decisión clave:** la inferencia con YOLO (PyTorch) **se queda en Python**, como microservicio, y se copió al repositorio en `uppa-api/`. Reescribirla en Java significaría convertir los modelos a otro formato (ONNX o DJL) y meter PyTorch dentro de Payara. Java hace con esa API lo mismo que hacía Laravel: la consume por HTTP. A cambio, ahora **guarda cada análisis en PostgreSQL**. En Laravel el resultado solo se mostraba y no se guardaba.

**Prioridad a las clases existentes del proyecto NetBeans:** no se crearon entidades paralelas. Las entidades de `modelo.*` (generadas desde la BD `uppa`), los facades de `dao.*`, `logica.MEnfermedad`, `CDIs.AEnfermedad`, `GraficasBean`, las plantillas JSF y el CSS se conservaron y se ampliaron. Las entidades JPA (`modelo/*.java`) **no se modificaron**.

---

## 2. Correspondencia entre el origen y Java EE

| Origen (Laravel / FastAPI) | Java EE (este proyecto) |
|---|---|
| Modelo `Especie` + tabla `especies` + `EspecieSeeder` (CSV) | Entidad existente `modelo.Hongo` (tabla `hongo`), cargada desde `datos/especies.csv` por `logica.InicializadorDatos`. `id_hongo` es el **id de clase del modelo YOLO** (0–209) |
| `User` + Fortify (registro, login, rate limit de 5 por minuto) | `modelo.Usuario` + `modelo.Credencial` (PBKDF2-SHA256 con salt) + `modelo.Rol`/`UsuarioRol`, lógica en `logica.MUsuario`, sesión en `CDIs.ASesion` |
| Middleware `auth` | `filtros.FiltroAutenticacion` |
| `App\Services\UppaApiService::catalogarHongo()` | `logica.MUppaApi.catalogarHongo()` (usa `java.net.http.HttpClient` y JSON-P) |
| `UPPA_API_URL` en `.env` | Propiedad de sistema `uppa.api.url` o variable de entorno `UPPA_API_URL` (`logica.Configuracion`) |
| Componente Livewire `file/⚡upload` (Alpine: cámara, vista previa, progreso) | `vistas/reconocimiento/index.xhtml` + `CDIs.AReconocimiento` + `resources/js/reconocimiento.js` |
| `ReconocimientoController@show` (marcador de posición) | `vistas/reconocimiento/show.xhtml?id=N` + `CDIs.AAnalisis` (detalle real desde la tabla `analisis`) |
| "Identificaciones recientes" con datos fijos | `dashboard.xhtml` + `AAnalisis.recientes` (últimos 4 análisis del usuario) |
| (no existía) | `vistas/reconocimiento/historial.xhtml` (todos los análisis del usuario) |
| Gráficas con datos fijos | `GraficasBean` + `logica.MGraficas` + consultas en `dao.AnalisisFacade` (datos reales del usuario) |
| Recomendaciones fijas de "moho verde" en Blade | Tabla `tratamiento` (3 filas iniciales) asociada a `enfermedad` "Trichoderma spp. / Moho verde" |
| Modelos `my_model.pt` y `uppa_v1_yolo11n.pt` | Tabla `version_modelo` (2 filas). Las épocas y la precisión se leyeron de los checkpoints |
| Caddy + certificados para la IP de la LAN | Opcional (ver §6.7): la cámara del navegador exige HTTPS fuera de `localhost` |

---

## 3. Instrucciones que se llevaron a cabo (registro de cambios)

### 3.1 Base de datos y configuración
1. Se volcó el esquema existente de la BD `uppa` a **`db/01_esquema.sql`**, sin las líneas exclusivas de PostgreSQL 18 para que funcione con versiones anteriores.
2. `persistence.xml`: `jta-data-source` pasó de `jdbc:postgresql://localhost:5432/uppa [postgres on public]` (nombre de conexión de NetBeans, que no existe en Payara) a **`jdbc/uppa`**. También se desactivó la generación de esquema por JPA.
3. Se añadió **`EnterpriseApplication2-ejb/setup/glassfish-resources.xml`** con el pool `uppaPool` y el recurso `jdbc/uppa`. NetBeans los registra solo al desplegar.
4. PrimeFaces ya no se referencia en `~/Descargas`: se copió a **`EnterpriseApplication2-war/lib/primefaces-13.0.10.jar`** con ruta relativa en `project.properties`, y se exceptuó en `.gitignore`.

### 3.2 Módulo EJB (`EnterpriseApplication2-ejb`)
| Archivo | Cambio |
|---|---|
| `dao/AbstractFacade.java` | `+ flush()`, necesario para obtener el id IDENTITY de `usuario` antes de crear su `credencial` |
| `dao/AnalisisFacade.java` | `siguienteId()` (la PK compuesta no tiene secuencia), `findByUsuario`, `findByIdYUsuario`, `existeImagenDeUsuario` y consultas para gráficas |
| `dao/UsuarioFacade`, `RolFacade`, `EnfermedadFacade`, `VersionModeloFacade`, `TratamientoFacade` | Búsquedas por nombre o correo con las `@NamedQuery` ya generadas |
| `logica/MEnfermedad.java` | Se implementó: etiqueta del modelo (`moho_verde`) → `Enfermedad`, tratamientos y vínculo `hongo_enfermedad` |
| `logica/MUppaApi.java` *(nuevo)* | Cliente HTTP de la API Python |
| `logica/MReconocimiento.java` *(nuevo)* | Flujo completo: validar imagen (JPG, PNG o WEBP, ≤ 10 MB) → API → enriquecer con `Hongo`/`Enfermedad`/`Tratamiento` → guardar foto y `Analisis` |
| `logica/MUsuario.java` *(nuevo)* | Registro y login, bloqueo de 1 minuto tras 5 intentos fallidos |
| `logica/MGraficas.java` *(nuevo)* | Estadísticas: sanos frente a enfermedades, top de especies, tendencia de 6 meses |
| `logica/InicializadorDatos.java` *(nuevo)* | `@Singleton @Startup`: al desplegar carga roles, versiones de modelo, enfermedad, tratamientos y 210 hongos, **solo si las tablas están vacías** |
| `logica/Configuracion.java`, `Contrasenas.java`, `ResultadoAnalisis.java`, `UppaException.java` *(nuevos)* | Configuración externa, hash de contraseñas, DTO del resultado y errores de negocio |
| `datos/especies.csv` *(nuevo)* | Copiado de `Uppa-front/database/data/especies.csv` |

### 3.3 Módulo WAR (`EnterpriseApplication2-war`)
| Archivo | Cambio |
|---|---|
| `CDIs/ASesion.java` *(nuevo)* | Login, registro y logout (`@SessionScoped`), con cambio de id de sesión al autenticarse |
| `CDIs/AReconocimiento.java` *(nuevo)* | Subida con `h:inputFile` (Part) y resultado (`@ViewScoped`) |
| `CDIs/AAnalisis.java` *(nuevo)* | Recientes, historial y detalle |
| `CDIs/AEnfermedad.java` | Pasa de `@Dependent` vacío a `@RequestScoped` y expone `tratamientos(Enfermedad)` |
| `com/uppa/beans/GraficasBean.java` | Misma estructura y paleta, pero alimentado desde `MGraficas`, con estado vacío |
| `filtros/FiltroAutenticacion.java`, `servlets/ImagenServlet.java` *(nuevos)* | Seguridad de vistas y entrega de las fotos (solo las del propio usuario) |
| `WEB-INF/web.xml` | Servlet 4.0, `request-character-encoding` UTF-8 (sin esto los acentos se guardan mal), `multipart-config` (10 MB), mapeo `*.xhtml` para que no se sirva el código fuente, cookie `http-only`, página para `ViewExpiredException` |
| `plantillas/layout.xhtml` | Se quitó una "V" suelta que se imprimía en pantalla. Se añadieron los huecos `metadata` y `estilos` |
| `plantillas/navbar.xhtml` | Entrada "Historial", avatar con iniciales y botón para cerrar sesión |
| `plantillas/tarjeta-analisis.xhtml`, `plantillas/tratamiento.xhtml` *(nuevos)* | Componentes reutilizables |
| `index.xhtml` | Botones "Crear cuenta" e "Iniciar sesión" (o "Ir al inicio" si hay sesión) |
| `auth/login.xhtml`, `auth/registro.xhtml` *(nuevos)* | Pantallas de acceso |
| `vistas/dashboard.xhtml` | Saludo con el nombre real y recientes desde la BD |
| `vistas/reconocimiento/index.xhtml`, `show.xhtml`, `historial.xhtml` | Flujo completo de reconocimiento |
| `vistas/graficas/index.xhtml` | Estado vacío. Icono `pi-line-chart`, que no existe en PrimeIcons 6, cambiado a `pi-chart-line` |
| `resources/css/auth.css`, `reconocimiento.css`, `resources/js/reconocimiento.js` *(nuevos)* | Estilos y cámara |

### 3.4 Servicio Python (`uppa-api/`)
Copia de `uppa-back`, sin `.venv` ni `__pycache__`: `main.py`, `especie_detector/`, `enfermedad_detector/`, `my_model.pt` y `uppa_v1_yolo11n.pt` (≈ 24 MB). Además, `requirements-cpu.txt` para equipos sin GPU.

### 3.5 Verificación realizada (en este equipo)
Se compiló con el Ant de NetBeans, se desplegó en Payara 5.2022.2 y se ejecutó una prueba de extremo a extremo con HTTP real. **Todas las comprobaciones pasaron**:
- Registro, login correcto e incorrecto, cierre de sesión y redirección a login sin sesión.
- Análisis de una foto real de *Boletus edulis* con la API Python: especie correcta, seguridad "Excelente" (93.9 %) y registro guardado en `analisis`.
- Detalle, historial, gráficas con datos reales y foto protegida (devuelve 401/404 sin sesión).
- Ruta de plaga con una API simulada (`moho_verde`): enfermedad, 3 tratamientos, consejos de foto y vínculo `hongo_enfermedad`.
- Después se borraron los datos de prueba. La BD quedó solo con el catálogo: 210 hongos, 1 enfermedad, 3 tratamientos, 2 versiones y 2 roles.

---

## 4. Estructura del repositorio

```
EnterpriseApplication2/
├── RECONSTRUCCION.md                ← este documento
├── db/01_esquema.sql                ← esquema PostgreSQL (14 tablas)
├── uppa-api/                        ← microservicio Python (FastAPI + YOLO)
├── EnterpriseApplication2-ejb/
│   ├── setup/glassfish-resources.xml
│   └── src/
│       ├── conf/persistence.xml
│       └── java/{modelo,dao,logica,datos}/
├── EnterpriseApplication2-war/
│   ├── lib/primefaces-13.0.10.jar
│   ├── src/java/{CDIs,com/uppa/beans,filtros,servlets}/
│   └── web/{index.xhtml,auth/,vistas/,plantillas/,resources/,WEB-INF/}
└── nbproject/ · build.xml           ← proyecto EAR de NetBeans (Ant)
```

---

## 5. Requisitos para otro equipo

| Componente | Versión probada | Notas |
|---|---|---|
| JDK | **17** (Zulu 17.0.20 / OpenJDK 17) | Payara 5 **no** arranca con JDK 21 o superior |
| Payara Server | **5.2022.2** (Full, perfil Java EE 8) | Cualquier 5.2022.x con soporte para JDK 17 |
| Apache NetBeans | 20 o superior (con el plugin *Java EE / Payara*) | Incluye Ant; no hace falta instalarlo aparte |
| PostgreSQL | 14 o superior (probado con 18.6) | BD `uppa` |
| Driver JDBC PostgreSQL | `postgresql-42.x.jar` | En `<payara>/glassfish/domains/domain1/lib/` |
| Python | 3.11–3.13 recomendado (aquí funcionó 3.14) | Para `uppa-api` |
| RAM | ≥ 4 GB libres | Payara (~1 GB) + PyTorch y YOLO (~1–2 GB) |
| Navegador | Chrome, Edge o Firefox recientes | La cámara exige **HTTPS** salvo en `localhost` |

Puertos usados: **8080** (HTTP de Payara), **4848** (consola de administración), **8181** (HTTPS de Payara), **8000** (API Python) y **5432** (PostgreSQL).

---

## 6. Configuración paso a paso en otro equipo

> Los comandos son para Linux o macOS. En Windows usa `asadmin.bat`, `\` en las rutas y `.venv\Scripts\` en lugar de `.venv/bin/`.

### 6.1 Obtener el proyecto
```bash
git clone <url-del-repo> EnterpriseApplication2
cd EnterpriseApplication2
```

### 6.2 PostgreSQL
```bash
createdb -U postgres uppa
psql -U postgres -d uppa -f db/01_esquema.sql
```
Las tablas quedan vacías. **Los datos de catálogo se cargan solos** la primera vez que se despliega la aplicación (`InicializadorDatos`).

### 6.3 Payara 5
1. Instala el JDK 17 y descomprime Payara 5. Si Payara no detecta tu JDK, fíjalo en `<payara>/glassfish/config/asenv.conf` con `AS_JAVA="/ruta/al/jdk-17"`.
2. Copia el driver JDBC:
   ```bash
   cp postgresql-42.*.jar <payara>/glassfish/domains/domain1/lib/
   ```
3. Arranca el dominio:
   ```bash
   <payara>/bin/asadmin start-domain domain1
   ```
4. **Crea la fuente de datos** con una de estas dos opciones:
   - **A (NetBeans):** edita `EnterpriseApplication2-ejb/setup/glassfish-resources.xml` y pon tu `user` y `password` de PostgreSQL. NetBeans la crea al desplegar.
   - **B (asadmin, recomendada fuera de NetBeans):**
     ```bash
     <payara>/bin/asadmin create-jdbc-connection-pool \
       --datasourceclassname org.postgresql.ds.PGSimpleDataSource \
       --restype javax.sql.DataSource \
       --property "serverName=localhost:portNumber=5432:databaseName=uppa:user=postgres:password=TU_PASSWORD" \
       uppaPool
     <payara>/bin/asadmin create-jdbc-resource --connectionpoolid uppaPool jdbc/uppa
     <payara>/bin/asadmin ping-connection-pool uppaPool
     ```
     Si la contraseña lleva `:`, escápala como `\:`.
5. **Configura Úppa** (opcional, estos son los valores por defecto):
   ```bash
   <payara>/bin/asadmin create-system-properties "uppa.api.url=http\://localhost\:8000"
   <payara>/bin/asadmin create-system-properties "uppa.imagenes.dir=/ruta/a/uppa-imagenes"
   ```

| Propiedad de sistema | Variable de entorno equivalente | Por defecto | Uso |
|---|---|---|---|
| `uppa.api.url` | `UPPA_API_URL` | `http://localhost:8000` | URL de la API Python |
| `uppa.api.timeout` | `UPPA_API_TIMEOUT` | `60` | Segundos de espera de la API |
| `uppa.imagenes.dir` | `UPPA_IMAGENES_DIR` | `~/uppa-imagenes` (home del usuario que ejecuta Payara) | Carpeta de las fotos analizadas |

### 6.4 Servicio Python (`uppa-api`)
```bash
cd uppa-api
python3 -m venv .venv
# Con GPU NVIDIA (versiones exactas originales, ~3 GB):
.venv/bin/pip install -r requirements.txt
# Sin GPU (recomendado):
.venv/bin/pip install --index-url https://download.pytorch.org/whl/cpu torch torchvision
.venv/bin/pip install -r requirements-cpu.txt

# Ejecutar SIEMPRE desde uppa-api/: los modelos .pt se cargan con rutas relativas
.venv/bin/uvicorn main:app --host 0.0.0.0 --port 8000
```
Para comprobarlo: `curl http://localhost:8000/` debe devolver `{"mensaje":"Úppa API funcionando"}`.

### 6.5 NetBeans
1. **Tools → Java Platforms:** registra el JDK 17. El proyecto espera una plataforma llamada `Zulu_17.0.20.1_1` (ver `platform.active` en `*/nbproject/project.properties`). Si la tuya se llama distinto, NetBeans marca el proyecto con *"Resolve Project Problems"*: elige ahí tu JDK 17.
2. **Tools → Servers → Add Server → Payara Server:** apunta a tu carpeta de Payara y a `domain1`.
3. Abre `EnterpriseApplication2` (el EAR; NetBeans abre solo los módulos `-ejb` y `-war`). Si aparece *"Missing server"*, haz clic derecho → *Resolve Missing Server Problem* y elige el Payara del paso anterior. Esto queda en `nbproject/private/`, que no está versionado.
4. Clic derecho en `EnterpriseApplication2` → **Clean and Build**, y luego **Run** (despliega en Payara).

**Sin NetBeans** (por línea de comandos):
```bash
# Usa el Ant que trae NetBeans y sus propiedades (rutas de Payara, JDK y EclipseLink).
# Requiere haber abierto el proyecto en NetBeans una vez (§6.5) para que existan nbproject/private/.
JAVA_HOME=/ruta/al/jdk-17 <netbeans>/extide/ant/bin/ant \
    -Duser.properties.file=$HOME/.netbeans/<versión>/build.properties clean dist
<payara>/bin/asadmin deploy --force=true --name EnterpriseApplication2 dist/EnterpriseApplication2.ear
```
> Este es exactamente el procedimiento con el que se verificó la build (NetBeans 30 → `~/.netbeans/30/build.properties`).

### 6.6 Comprobación
1. En el log de Payara (`domains/domain1/logs/server.log`) deben aparecer las líneas `Úppa: … 210 especies de hongo cargadas.` (solo en el primer despliegue).
2. Abre `http://localhost:8080/EnterpriseApplication2-war/`, ve a **Crear cuenta**, entra en **Identificar**, sube una foto y pulsa **Analizar foto**.
3. El resultado aparece en **Historial** y en **Gráficas**.

### 6.7 Acceso desde el celular o la LAN (cámara)
Los navegadores solo permiten `getUserMedia` (la cámara) en **HTTPS** o en `localhost`. Tienes dos opciones:
- **HTTPS de Payara:** `https://<IP-del-equipo>:8181/EnterpriseApplication2-war/`. El certificado es autofirmado y hay que aceptarlo en el teléfono.
- **Caddy**, como en el front original:
  ```
  https://<IP-del-equipo> {
      tls /ruta/<IP>.pem /ruta/<IP>-key.pem     # certificados generados con mkcert
      reverse_proxy http://127.0.0.1:8080
  }
  ```
  Entra en `https://<IP>/EnterpriseApplication2-war/`. Abre en el firewall solo los puertos que necesites (443 u 8181). La API Python **no** tiene que ser accesible desde la LAN, porque solo la llama Payara.

---

## 7. Solución de problemas

| Síntoma | Causa y solución |
|---|---|
| `Invalid resource : jdbc/uppa__pm` al desplegar | Falta el recurso JDBC. Revisa §6.3 paso 4 |
| `ping-connection-pool` falla | Usuario o contraseña, puerto o BD incorrectos, o falta el driver en `domain1/lib` (reinicia el dominio después de copiarlo) |
| "No se pudo conectar con la API de reconocimiento" | La API Python no está corriendo o `uppa.api.url` es incorrecto. Comprueba con `curl <url>/` |
| La API falla con `FileNotFoundError: my_model.pt` | `uvicorn` no se lanzó desde la carpeta `uppa-api/` |
| Acentos corruptos (`Ã©`) | El `web.xml` debe declarar `version="4.0"` y `request-character-encoding` UTF-8 |
| El botón "Usar cámara" muestra un error | La página no se abrió por HTTPS ni por localhost (§6.7) |
| La tabla `hongo` queda vacía | El despliegue no encontró la BD o el esquema. Revisa el `SEVERE` del log, aplica `db/01_esquema.sql` y vuelve a desplegar |
| Payara no arranca | Estás usando JDK 21 o superior. Configura `AS_JAVA` con el JDK 17 |
| *Resolve Project Problems* en NetBeans | La plataforma `Zulu_17.0.20.1_1` o el servidor Payara no existen en ese equipo (§6.5) |

---

## 8. Diferencias conocidas y pendientes

- **No se portaron** las pantallas de ajustes de Laravel (perfil, apariencia, 2FA), la recuperación de contraseña ni la verificación de correo. Las columnas ya existen en `credencial` (`mfa_*`, `token_recuperacion`) y `usuario.email_verificado`.
- Las tablas `plan`, `suscripcion`, `pago` y `metodo_pago` y sus facades existen, pero no tienen interfaz, igual que en el proyecto original.
- `version_modelo.tamano_dataset` se cargó como `0` porque los checkpoints no lo contienen. Actualízalo cuando conozcas el valor.
- **Backend Python (sin cambios):** `ESPECIES_CULTIVADAS`, en `especie_detector/main.py`, contiene las 210 clases del modelo, así que la API responde siempre `cultivada: true` y la interfaz muestra siempre "Cultivable". Se corrige en el Python, no en Java.
- `analisis.id_analisis` no tiene secuencia en la BD (forma parte de la PK compuesta) y se calcula con `MAX + 1`. Con mucha concurrencia conviene crear una secuencia.
- `analisis.ubicacion` se deja en `NULL`, porque la geolocalización no se captura.
- Los análisis en los que la API no detecta un hongo se muestran, pero **no se guardan**, porque `id_hongo` es obligatorio en la PK.
- Para producción: `javax.faces.PROJECT_STAGE=Production` en `web.xml` y reglas de contraseña más estrictas en `MUsuario.validarContrasena`.

### Estado en el equipo original (`crstvbl`)
- Se creó en Payara el recurso **`jdbc/uppa`** apuntando al pool existente **`uppa`** (el mismo de `UPPARES`). Por eso, en este equipo, **no hace falta** editar la contraseña de `glassfish-resources.xml`. Si NetBeans avisa al desplegar de que `jdbc/uppa` ya existe, se puede ignorar.
- La aplicación quedó desplegada como `EnterpriseApplication2`, y la BD `uppa`, con los datos de catálogo.
