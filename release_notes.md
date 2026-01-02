# Release v5 — Tectuinno IDE

## Resumen
Lanzamiento mayor con mejoras integrales en la interfaz, tema Andromeda moderno, notificaciones flotantes elegantes, números de línea, búsqueda integrada, gestión mejorada de puertos COM y correcciones de bugs críticos.

---

## ✨ Novedades Principales

### 🎨 Interfaz & Experiencia Visual

#### Tema Andromeda Completo
- Diseño visual moderno y consistente en toda la aplicación
- Paleta de colores oscuros: fondos azul oscuro, acentos cyan, títulos amarillos
- **Menú de Ayuda** integrado con opción "Acerca de..."
- **Visor de Licencia** desde la aplicación
- Colores personalizados en: barras de título, menús, dropdowns, pestañas, selectores de archivo

#### Notificaciones Modernas (Toast Notifications)
- Reemplazo de diálogos clásicos por notificaciones flotantes tipo "toast"
- Aparecen en esquina superior derecha sin interrumpir el trabajo
- Desaparición automática en 4 segundos
- 4 tipos con iconos ASCII: ✓ Éxito (verde), ✕ Error (rojo), ⚠ Advertencia (amarillo), ℹ Información (cyan)
- Botón cerrar manual (X) si prefieres descartar antes

#### Panel de Números de Línea
- Números de línea sincronizados automáticamente al scroll
- Actualización dinámica al cambiar tamaño de fuente
- Estilo Andromeda: fondos oscuros con números en gris

#### Zoom con Rueda de Ratón
- Control de tamaño de fuente: **Ctrl + Scroll** (arriba para aumentar, abajo para reducir)
- Rango: 8px (mínimo) a 48px (máximo)
- Paso: 2px por notch de rueda
- Actualización en tiempo real sin reiniciar el editor

### 🔍 Editor Mejorado

#### Ventana de Búsqueda
- Diálogo dedicado para buscar texto en el editor
- Navegación rápida entre resultados
- Atajo de teclado optimizado: **Ctrl+B**

#### Errores en Línea (No-Intrusivos)
- Visualización de errores sin tapar el código
- Resaltado de línea con fondo semitransparente (rojo/naranja según severidad)
- Indicador visual en barra lateral izquierda (3px)
- Subrayado ondulado bajo texto con error
- Tooltip al pasar el puntero muestra detalles del error
- Impacto: menos distracciones, mejor visibilidad del código

#### Atajo "Ir a Línea"
- **Ctrl+G**: Abre diálogo para navegar a una línea específica
- Validación automática del rango
- Posicionamiento instantáneo en la línea objetivo

### ⌨️ Atajos de Teclado Completos

#### Menú Archivo
| Atajo | Acción | |
|---|---|---|
| `Ctrl+N` | Nuevo archivo | Abre editor ASM (funciona desde cualquier parte) |
| `Ctrl+O` | Abrir | Carga archivo ASM existente |
| `Ctrl+S` | Guardar | Guarda sin diálogo si ya tiene ruta |
| `Ctrl+Shift+S` | Guardar Como... | Siempre abre el explorador |

#### Menú Editar
| Atajo | Acción | |
|---|---|---|
| `Ctrl+Z` | Deshacer | Deshace cambios (no borra contenido cargado) |
| `Ctrl+Y` | Rehacer | Rehace cambios deshechados |
| `Ctrl+C` | Copiar | Copia selección |
| `Ctrl+X` | Cortar | Corta selección |
| `Ctrl+V` | Pegar | Pega desde portapapeles |
| `Ctrl+A` | Seleccionar todo | Selecciona todo el contenido |
| `Ctrl+G` | Ir a línea... | Navega a línea específica |

#### Zoom
| Atajo | Acción | |
|---|---|---|
| `Ctrl+Scroll↑` | Aumentar fuente | Incrementa tamaño de texto |
| `Ctrl+Scroll↓` | Reducir fuente | Reduce tamaño de texto |

### 🔌 Conexión Serial Mejorada

#### Gestión Inteligente de Puertos COM
- **Exclusión automática** de puertos Bluetooth (no aparecen en la lista)
- **Auto-selección**: Si hay un puerto, se selecciona automáticamente
- **Recuperación**: Si el puerto actual desaparece, se selecciona el primero disponible
- Botón **"Escanear"** para refrescar manualmente la lista
- Impacto: Flujo más directo y menos ruido visual

#### Comunicación Serial Mejorada
- Cadena de identificación **TECTUINNO** en comunicación
- Mejor confiabilidad en transmisiones por puerto COM
- Corrección en protocolo de envío de trama hexadecimal por WiFi

### 🐛 Correcciones Críticas

#### Bug de Ctrl+Z (Undo) - Corregido
- **Problema**: Al abrir archivo, Ctrl+Z sin cambios borraba todo el contenido
- **Solución**: Historial de undo se limpia después de cargar contenido
- **Comportamiento nuevo**: Ctrl+Z solo deshace cambios propios, nunca el contenido cargado

### 💻 Rendimiento

#### Optimizaciones de Renderizado
- **Desactivación de GPU**: Direct3D (Windows) y OpenGL deshabilitados para estabilidad
- **Impacto**: Renderizado por software más estable en máquinas débiles y virtualizadas
- Previene sobrecalentamiento en configuraciones limitadas

---

## 🛠️ Detalles Técnicos

### Requisitos
- **Java**: Versión 21 o superior
- **Sistema Operativo**: Windows, macOS, Linux
- **RAM**: 512 MB mínimo (recomendado 1-2 GB)

### Tecnologías
- **Build**: Maven 3
- **Dependencias Clave**:
  - jSerialComm 2.11.2 (Comunicación serial)
  - FlatLaf 3.5.2 (Tema UI moderno)
  - JUnit 5.11.0 (Testing)

### Estructura de Directorios
```
out/
├── artifacts/
│   └── tectuinno_ide_jar/
│       └── Tectuinno-IDE.jar ← Ejecutable
src/
├── main/java/org/tectuinno/
│   ├── view/
│   │   ├── component/       ← UI Components (Notificaciones, LineNumbers)
│   │   ├── assembler/       ← Editor principal
│   │   └── StartingWindow.java ← Ventana principal
│   └── App.java             ← Entrada + configuración de tema
```

---

## 📥 Instalación

### Método 1: JAR Directo (Recomendado)
```bash
java -jar Tectuinno-IDE.jar
```

### Método 2: Desde Línea de Comandos
```bash
cd ruta/al/proyecto
java -jar out/artifacts/tectuinno_ide_jar/Tectuinno-IDE.jar
```

### Método 3: Construcción desde Fuentes
```bash
mvn clean package
java -jar out/artifacts/tectuinno_ide_jar/Tectuinno-IDE.jar
```

---

## 📦 Archivos Incluidos
- `Tectuinno-IDE.jar` (985 KB) - JAR ejecutable con todas las dependencias incluidas
- `release_notes.md` - Este archivo (notas de release)
- Código fuente completo en carpeta `src/`
- Scripts de construcción: `build.bat`, `clean-build.bat`, `build-and-run.bat`

---

## 🚀 Guía Rápida de Uso

### Primer Inicio
1. Ejecuta: `java -jar Tectuinno-IDE.jar`
2. Se abrirá la ventana principal con menús: Archivo, Editar, Herramientas, Ayuda
3. Verifica que se detectó correctamente tu puerto COM en el dropdown (esquina superior)

### Crear Nuevo Archivo
1. Atajo: **Ctrl+N** O Menú → Archivo → Nuevo
2. Se abre un editor ASM vacío en nueva pestaña

### Abrir Archivo Existente
1. Atajo: **Ctrl+O** O Menú → Archivo → Abrir
2. Selecciona archivo `.asm`
3. Se carga en el editor con números de línea automáticos

### Guardar Archivo
1. Tras editar: **Ctrl+S** (si ya tiene ruta) O **Ctrl+Shift+S** (para elegir ubicación)
2. Notificación flotante confirma el guardado

### Compilar/Verificar
1. Botón "Verificar" en la barra de herramientas
2. Los errores aparecen resaltados en la barra de estado sin tapar el código
3. Busca detalles en tooltip (pasa el puntero sobre errores)

### Buscar Texto
1. Atajo: **Ctrl+B** O Menú → Editar → Buscar
2. Ingresa texto y navega entre resultados

### Ajustar Tamaño de Fuente
1. Método 1: **Ctrl + Scroll** del ratón
2. Método 2: Menú Editar → Zoom In / Zoom Out
3. Rango permitido: 8px a 48px

---

## 👥 Contribuidores
- **darimm-dot** (Darinel Gordillo Palacios) - Desarrollo principal
- **Pablo-Gomez-Perez** - Mejoras de UI/UX
- **GHz-0** - Búsqueda e integraciones

---

## 📋 Notas de Compatibilidad

- ✅ Windows 10/11 con Java 21+
- ✅ macOS 10.15+ (Intel/ARM) con Java 21+
- ✅ Linux (Ubuntu 18.04+, Fedora 30+) con Java 21+
- ⚠️ Para usar WiFi Programmer: hardware compatible requerido

---

## 🆘 Resolución de Problemas

### "No se detecta puerto COM"
1. Verifica que el dispositivo está conectado
2. Abre "Administrador de Dispositivos" (Windows) y busca en "Puertos (COM y LPT)"
3. Haz click en botón "Escanear" en el IDE
4. Si sigue sin aparecer, revisa drivers del dispositivo

### "La aplicación se ve lenta o no responde"
1. Intenta reducir el tamaño de fuente: **Ctrl+Scroll↓**
2. Cierra otras aplicaciones pesadas
3. Si persiste, desactiva aceleración de GPU en el código (ya está hecho por defecto)

### "Ctrl+Z borra todo el contenido"
- Este bug fue corregido en esta versión. Si persiste, guarda, cierra y reabre el archivo.

---

# Release v5 — Tectuinno IDE

## Summary
Major release with comprehensive interface improvements, modern Andromeda theme, elegant floating notifications, line numbers, integrated search, improved COM port management, and critical bug fixes.

---

## ✨ Main Features

### 🎨 Interface & Visual Experience

#### Complete Andromeda Theme
- Modern and consistent visual design throughout the application
- Dark color palette: dark blue backgrounds, cyan accents, yellow titles
- **Integrated Help Menu** with "About..." option
- **Built-in License Viewer** accessible from the application
- Custom colors in: title bars, menus, dropdowns, tabs, file choosers

#### Modern Toast Notifications
- Replacement of classic Swing dialogs with floating "toast" notifications
- Appear in the top-right corner without interrupting work
- Automatic disappearance in 4 seconds
- 4 types with ASCII icons: ✓ Success (green), ✕ Error (red), ⚠ Warning (yellow), ℹ Info (cyan)
- Manual close button (X) if you prefer to dismiss earlier

#### Line Number Panel
- Line numbers synchronized automatically with scrolling
- Dynamic update when changing font size
- Andromeda style: dark backgrounds with gray numbers

#### Mouse Wheel Zoom
- Font size control: **Ctrl + Scroll** (up to increase, down to decrease)
- Range: 8px (minimum) to 48px (maximum)
- Step: 2px per mouse wheel notch
- Real-time update without editor restart

### 🔍 Improved Editor

#### Search Window
- Dedicated dialog for searching text in the editor
- Quick navigation between results
- Optimized keyboard shortcut: **Ctrl+B**

#### Non-Intrusive Line Errors
- Error visualization without obscuring code
- Line highlighting with semi-transparent background (red/orange depending on severity)
- Visual indicator on the left sidebar (3px)
- Wavy underline under text with errors
- Tooltip on hover shows error details
- Impact: fewer distractions, better code visibility

#### Go to Line Shortcut
- **Ctrl+G**: Opens dialog to navigate to a specific line
- Automatic range validation
- Instant positioning at target line

### ⌨️ Complete Keyboard Shortcuts

#### File Menu
| Shortcut | Action | |
|----------|--------|---|
| `Ctrl+N` | New file | Opens ASM editor (works from anywhere) |
| `Ctrl+O` | Open | Loads existing ASM file |
| `Ctrl+S` | Save | Saves without dialog if path exists |
| `Ctrl+Shift+S` | Save As... | Always opens explorer |

#### Edit Menu
| Shortcut | Action | |
|----------|--------|---|
| `Ctrl+Z` | Undo | Undoes changes (doesn't erase loaded content) |
| `Ctrl+Y` | Redo | Redoes undone changes |
| `Ctrl+C` | Copy | Copies selection |
| `Ctrl+X` | Cut | Cuts selection |
| `Ctrl+V` | Paste | Pastes from clipboard |
| `Ctrl+A` | Select All | Selects entire content |
| `Ctrl+G` | Go to Line... | Navigate to specific line |

#### Zoom
| Shortcut | Action | |
|----------|--------|---|
| `Ctrl+Scroll↑` | Increase Font | Increases text size |
| `Ctrl+Scroll↓` | Decrease Font | Decreases text size |

### 🔌 Improved Serial Connection

#### Smart COM Port Management
- **Automatic exclusion** of Bluetooth ports (don't appear in list)
- **Auto-selection**: If one port available, it's automatically selected
- **Recovery**: If current port disappears, first available port is selected
- **"Scan"** button to manually refresh the list
- Impact: Smoother workflow and less visual clutter

#### Enhanced Serial Communication
- **TECTUINNO** identification string in communication
- Better reliability in COM port transmissions
- Correction in hexadecimal frame sending protocol over WiFi

### 🐛 Critical Bug Fixes

#### Ctrl+Z (Undo) Bug - Fixed
- **Problem**: Opening a file and pressing Ctrl+Z without changes would delete all content
- **Solution**: Undo history is cleared after loading content
- **New Behavior**: Ctrl+Z only undoes user changes, never loaded content

### 💻 Performance

#### Rendering Optimizations
- **GPU Acceleration Disabled**: Direct3D (Windows) and OpenGL disabled for stability
- **Impact**: Software rendering more stable on weak machines and virtualized environments
- Prevents overheating on limited configurations

---

## 🛠️ Technical Details

### Requirements
- **Java**: Version 21 or higher
- **Operating System**: Windows, macOS, Linux
- **RAM**: 512 MB minimum (1-2 GB recommended)

### Technologies
- **Build**: Maven 3
- **Key Dependencies**:
  - jSerialComm 2.11.2 (Serial communication)
  - FlatLaf 3.5.2 (Modern UI theme)
  - JUnit 5.11.0 (Testing)

### Directory Structure
```
out/
├── artifacts/
│   └── tectuinno_ide_jar/
│       └── Tectuinno-IDE.jar ← Executable
src/
├── main/java/org/tectuinno/
│   ├── view/
│   │   ├── component/       ← UI Components (Notifications, LineNumbers)
│   │   ├── assembler/       ← Main editor
│   │   └── StartingWindow.java ← Main window
│   └── App.java             ← Entry point + theme configuration
```

---

## 📥 Installation

### Method 1: Direct JAR (Recommended)
```bash
java -jar Tectuinno-IDE.jar
```

### Method 2: From Command Line
```bash
cd path/to/project
java -jar out/artifacts/tectuinno_ide_jar/Tectuinno-IDE.jar
```

### Method 3: Build from Sources
```bash
mvn clean package
java -jar out/artifacts/tectuinno_ide_jar/Tectuinno-IDE.jar
```

---

## 📦 Included Files
- `Tectuinno-IDE.jar` (985 KB) - Executable JAR with all dependencies included
- `release_notes.md` - This file (release notes)
- Complete source code in `src/` folder
- Build scripts: `build.bat`, `clean-build.bat`, `build-and-run.bat`

---

## 🚀 Quick Start Guide

### First Launch
1. Run: `java -jar Tectuinno-IDE.jar`
2. Main window opens with menus: File, Edit, Tools, Help
3. Verify your COM port was correctly detected in the dropdown (top right corner)

### Create New File
1. Shortcut: **Ctrl+N** OR Menu → File → New
2. Opens empty ASM editor in new tab

### Open Existing File
1. Shortcut: **Ctrl+O** OR Menu → File → Open
2. Select `.asm` file
3. Loads in editor with automatic line numbers

### Save File
1. After editing: **Ctrl+S** (if path exists) OR **Ctrl+Shift+S** (to choose location)
2. Floating notification confirms save

### Compile/Verify
1. Click "Verify" button in toolbar
2. Errors appear highlighted in status bar without obscuring code
3. Check details in tooltip (hover over errors)

### Search Text
1. Shortcut: **Ctrl+B** OR Menu → Edit → Search
2. Enter text and navigate between results

### Adjust Font Size
1. Method 1: **Ctrl + Mouse Scroll**
2. Method 2: Edit Menu → Zoom In / Zoom Out
3. Allowed range: 8px to 48px

---

## 👥 Contributors
- **darimm-dot** (Darinel Gordillo Palacios) - Main development
- **Pablo-Gomez-Perez** - UI/UX improvements
- **GHz-0** - Search and integrations

---

## 📋 Compatibility Notes

- ✅ Windows 10/11 with Java 21+
- ✅ macOS 10.15+ (Intel/ARM) with Java 21+
- ✅ Linux (Ubuntu 18.04+, Fedora 30+) with Java 21+
- ⚠️ To use WiFi Programmer: compatible hardware required

---

## 🆘 Troubleshooting

### "COM port not detected"
1. Verify device is connected
2. Open "Device Manager" (Windows) and check "Ports (COM & LPT)"
3. Click "Scan" button in the IDE
4. If still not appearing, check device drivers

### "Application appears slow or unresponsive"
1. Try reducing font size: **Ctrl+Scroll↓**
2. Close other heavy applications
3. If persists, GPU acceleration is already disabled by default

### "Ctrl+Z deletes all content"
- This bug was fixed in this version. If it persists, save, close, and reopen the file.

---

**Release Date:** January 2, 2026
**Version:** v5
**Status:** Stable
