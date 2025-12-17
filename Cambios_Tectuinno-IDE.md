# Changelog Completo - Tectuinno IDE v0.1.1.1

## 🎯 Resumen General

Mejoras integrales en la experiencia visual y funcional del IDE con enfoque en tema Andromeda, accesibilidad del editor, rendimiento y desactivación de GPU.

---

## 🔧 Configuración de Maven

### Cambio de directorio de compilación a `out`

**Fecha:** 13/12/2025

- **Modificado:** `pom.xml` - Configuración de `<directory>out</directory>`
- **Motivo:** Cambio de la carpeta de salida de Maven de `target/` a `out/` para mayor claridad
- **Impacto:**
  - Todas las compilaciones ahora se generan en la carpeta `out/`
  - La carpeta `target/` ya no es necesaria y fue eliminada
  - Actualizado `.gitignore` para ignorar `out/` en lugar de `target/`

---

## 📑 Índice de cambios

- Notificaciones modernas flotantes (toast) con animaciones suaves
- Atajos de teclado globales y de menú
- Barra de estado con contador de problemas
- Errores en línea (visualización no intrusiva)
- Gestión de puertos COM (excluir Bluetooth y auto-selección)
- **Corrección: Bug de Ctrl+Z que borraba contenido al abrir archivos**
- Rendimiento y desactivación de GPU
- Números de línea y zoom del editor
- Empaquetado y scripts de build multiplataforma

---

## 🎨 Notificaciones Modernas (Toast Notifications)

**Archivo:** `src/main/java/org/tectuinno/view/component/ModernNotification.java` (nuevo)

### Descripción

Reemplazo de los diálogos clásicos de Swing por notificaciones flotantes modernas tipo "toast" que se posicionan en la esquina superior derecha del editor y desaparecen automáticamente.

### Características

- **Diseño compacto:** 320x60 píxeles (más pequeñas y discretas)
- **Esquinas redondeadas** con radio de 12px para diseño futurista
- **Animaciones suaves** (fade in/out) para entrada y salida elegante
- **Cierre automático** después de 4 segundos de inactividad
- **Botón cerrar manual** (X) para descartar antes de tiempo
- **Borde izquierdo coloreado** (3px) que indica el tipo de notificación
- **Transparencia con antialiasing** para integración visual suave
- **Posicionamiento optimizado:** Esquina superior derecha del área de edición (no en el borde del IDE)
- **Iconos ASCII simples** para compatibilidad universal:
  - `[OK]` **Éxito** (Verde `#10b981`)
  - `[X]` **Error** (Rojo `#ff6961`)
  - `[!]` **Advertencia** (Amarillo `#ffe66d`)
  - `[i]` **Información** (Cyan `#00e8c6`)

### Ventajas

- No interrumpen el flujo de trabajo (no requieren hacer clic para cerrar)
- Ocupan menos espacio que diálogos modales
- Diseño acorde a aplicaciones modernas (VS Code, Discord, Slack)
- Permiten seguir trabajando mientras se muestran

### Implementación técnica

- Clase `ModernNotification` extiende `JWindow` y usa doble animación `fadeIn()/fadeOut()` con `Timer` y `alpha` incremental (intervalo 10 ms).
- Dimensiones compactas: `WIDTH = 320`, `HEIGHT = 60`, padding 12px, radio 12px; borde izquierdo 3px coloreado según `NotificationType`.
- Posicionamiento: esquina superior derecha del editor con offset `(x - 23, y + 28)` para no pisar la barra del IDE.
- Iconos ASCII (`[OK]`, `[X]`, `[!]`, `[i]`) con fuente `Consolas 14` para evitar recuadros vacíos en sistemas sin emojis.
- Botón cerrar (`X`, fuente 11 bold) y antialiasing en pintura personalizada del panel.

### Uso en el Código

```java
// Reemplaza los antiguos showThemedDialog()
ModernNotification.showSuccess(this, "Archivo guardado");
ModernNotification.showError(this, "Error al procesar");
ModernNotification.showWarning(this, "Advertencia importante");
ModernNotification.showInfo(this, "Operación completada");
```

---

## 🐛 Corrección: Bug de Ctrl+Z (Undo)

**Archivo:** `src/main/java/org/tectuinno/view/assembler/AsmEditorInternalFrame.java`

### Problema

Cuando se abría un archivo, el `UndoManager` registraba la operación de carga de contenido. Al presionar Ctrl+Z sin haber realizado ningún cambio, se deshacía la carga inicial, borrando todo el contenido del archivo.

### Solución

Se agregó `undoManager.discardAllEdits()` en el método `asmSetEditorText()` para limpiar el historial de deshacer después de cargar contenido. Esto establece el archivo cargado como el "estado inicial" sin historial previo.

### Comportamiento corregido

- **Abrir archivo** → El contenido se carga sin historial de undo
- **Presionar Ctrl+Z sin cambios** → No ocurre nada (el gestor está vacío)
- **Editar y presionar Ctrl+Z** → Solo deshace los cambios realizados, nunca la carga inicial

```java
public void asmSetEditorText(String text) {
    asmEditorPane.setText(text);
    // Limpiar el historial de deshacer para que el contenido cargado sea el estado inicial
    // Evita que Ctrl+Z sin cambios previos borre el contenido del archivo
    undoManager.discardAllEdits();
}
```

---

## ⌨️ Atajos de Teclado

**Archivos:** `src/main/java/org/tectuinno/view/StartingWindow.java`, `src/main/java/org/tectuinno/view/assembler/AsmEditorInternalFrame.java`

### Menú Archivo

| Atajo          | Acción          | Descripción                                                               |
| -------------- | --------------- | ------------------------------------------------------------------------- |
| `Ctrl+N`       | Nuevo archivo   | Abre un nuevo editor ASM (global, funciona desde cualquier parte del IDE) |
| `Ctrl+O`       | Abrir           | Abre un archivo ASM existente desde el explorador                         |
| `Ctrl+S`       | Guardar         | Guarda el archivo actual (sin diálogo si ya tiene ruta)                   |
| `Ctrl+Shift+S` | Guardar Como... | Siempre muestra el explorador para elegir ubicación                       |

### Menú Editar

| Atajo    | Acción           | Descripción                                         |
| -------- | ---------------- | --------------------------------------------------- |
| `Ctrl+Z` | Deshacer         | Deshace la última acción en el editor               |
| `Ctrl+Y` | Rehacer          | Rehace la última acción deshecha                    |
| `Ctrl+C` | Copiar           | Copia el texto seleccionado al portapapeles         |
| `Ctrl+X` | Cortar           | Corta el texto seleccionado al portapapeles         |
| `Ctrl+V` | Pegar            | Pega el contenido del portapapeles                  |
| `Ctrl+A` | Seleccionar todo | Selecciona todo el texto del editor                 |
| `Ctrl+G` | Ir a línea...    | Muestra diálogo para navegar a una línea específica |

### Zoom del Editor

| Atajo               | Acción          | Descripción                               |
| ------------------- | --------------- | ----------------------------------------- |
| `Ctrl+Rueda arriba` | Aumentar fuente | Incrementa el tamaño de fuente del editor |
| `Ctrl+Rueda abajo`  | Reducir fuente  | Reduce el tamaño de fuente del editor     |

### Implementación Técnica

- Atajos registrados con `JMenuItem.setAccelerator()` para que aparezcan visibles en los menús
- Atajo global `Ctrl+N` implementado en `JRootPane` con `WHEN_IN_FOCUSED_WINDOW` para funcionar desde cualquier componente
- Atajos `Ctrl+S` y `Ctrl+Shift+S` registrados en `StartingWindow` y `AsmEditorInternalFrame` para responder desde menú y editor.
- `Ctrl+G` abre diálogo de “Ir a línea”, valida rango y posiciona caret con `modelToView2D()`.

---

## 📊 Barra de Estado (Status Bar)

**Archivo:** `src/main/java/org/tectuinno/view/StartingWindow.java`

### Descripción

Panel en la parte inferior del IDE que muestra el estado del análisis de código en tiempo real.

### Características

- **Sin problemas:** Muestra `✓ Sin problemas` en color cyan cuando no hay errores
- **Con errores:** Muestra `✕ N error(es)` en color rojo cuando hay errores de sintaxis o semánticos
- **Con advertencias:** Muestra `⚠ N advertencia(s)` en color amarillo
- Actualización automática después de cada análisis (botón "Verificar")

### Colores

- Fondo: `#0a0c12` (Andromeda background)
- Sin errores: `#00e8c6` (Cyan)
- Con errores: `#ff6961` (Rojo suave)
- Con advertencias: `#ffe66d` (Amarillo)

### Implementación

```java
private void updateStatusBar(int errorCount, int warningCount) {
    if (errorCount == 0 && warningCount == 0) {
        statusBarLabel.setText("✓ Sin problemas");
        statusBarLabel.setForeground(new Color(0x00, 0xe8, 0xc6));
    } else {
        // Mostrar contador de errores y/o advertencias
    }
}
```

- Creación en `StartingWindow`: `statusBarPanel` (BorderLayout.SOUTH) + `statusBarLabel` con colores Andromeda.
- Se invoca tras el análisis de código (hilo de verificación) para mostrar contadores en tiempo real.
- Colores definidos en el método para estados de éxito, advertencia y error.

---

## 🔌 Puertos COM (Serial)

**Archivo:** `src/main/java/org/tectuinno/view/StartingWindow.java`

### Comportamiento

- Exclusión de puertos Bluetooth de la lista mostrada (evita puertos virtuales no útiles para programación serial).
- Auto-selección inteligente:
  - Si solo hay un puerto disponible, se selecciona automáticamente.
  - Si el puerto previamente seleccionado deja de existir, se selecciona el primero disponible.
- Refresco automático periódico (timer) y botón “Escanear” manual.

### Implementación

- Filtrado en `StartingWindow` sobre `SerialPort.getCommPorts()` excluyendo nombres que contienen "Bluetooth".
- Persistencia de selección previa: si el puerto desaparece, cae al primer disponible; si hay uno solo, se autoselecciona.
- Timer de refresco + acción de botón “Escanear” reusan la misma rutina de descubrimiento para mantener la lista coherente.

### Impacto

- Flujo más directo al conectar dispositivos reales.
- Menos ruido visual en la lista de puertos.

---

## 🛠️ Errores en línea (AsmEditorPane)

**Archivos:** `src/main/java/org/tectuinno/view/assembler/AsmEditorPane.java`

- Mensajes ya no se superponen sobre el código; se usa solo resaltado visual y tooltip.
- Resaltado de línea con fondo rojo/naranja semitransparente según severidad.
- Barra lateral izquierda de 3px como indicador rápido, estilo VS Code.
- Subrayado ondulado bajo el texto con error para señal clara sin tapar el código.
- Tooltip al pasar el puntero muestra todos los errores de la línea con prefijos ✖/⚠.
- Render con antialiasing para líneas y ondulado.
- Implementación: pintura custom en `AsmEditorPane` usando `StyledDocument` offsets; tooltips agregan múltiples mensajes por línea; cálculo de rectángulos vía `modelToView2D`.

---

## 1️⃣ GPU & RENDIMIENTO

**Archivo:** `src/main/java/org/tectuinno/App.java` (líneas 47-50)

### Desactivación de Aceleración GPU

```java
System.setProperty("sun.java2d.d3d", "false");      // Deshabilita Direct3D (Windows)
System.setProperty("sun.java2d.opengl", "false");   // Deshabilita OpenGL
```

**Impacto:** Evita sobrecalentamiento de GPU en equipos con configuraciones débiles o drivers problemáticos. Renderizado por software es más estable en ambientes virtualizados.

- Props aplicadas al inicio de `main` antes de levantar UI para asegurar que Swing use pipeline por software.

---

## 2️⃣ NÚMEROS DE LÍNEA - LineNumberPanel

**Archivo:** `src/main/java/org/tectuinno/view/component/LineNumberPanel.java` (115 líneas nuevas)

### Componente Nuevo

- Panel de números de línea sincronizado con el editor
- Soporte para scroll vertical dinámico
- Actualización automática cuando cambia la fuente del editor
- Colores Andromeda:
  - Fondo: `#0a0c12` (editor.background)
  - Texto: `#746f77` (números en gris)

### Integración en Editor

```java
scrollPane.setRowHeaderView(new LineNumberPanel(textComponent));
```

### Características Técnicas

- Renderizado eficiente usando `viewToModel2D()` (solo líneas visibles)
- Padding configurable: `PADDING_LEFT = 6`, `PADDING_RIGHT = 6`
- Auto-ajuste de ancho según cantidad de dígitos
- Sincronización con `DocumentListener` y `CaretListener`
- Detección de viewport para renderizar solo líneas en pantalla
- Usa `viewToModel2D`/`modelToView2D` para traducir coordenadas y pintar solo el rango visible; recalcula ancho cuando crece el número de dígitos.

---

## 3️⃣ AUMENTO DE FUENTE CON SCROLL/ZOOM

**Archivo:** `src/main/java/org/tectuinno/view/assembler/AsmEditorPane.java` (líneas 95-150)

### Métodos Implementados

#### `updateFontSize(int newSize)`

```java
private void updateFontSize(int newSize) {
    int clamped = Math.max(8, Math.min(48, newSize));  // Rango: 8px a 48px

    // Actualizar tamaño en todos los estilos
    StyleConstants.setFontSize(keyWordStyle, clamped);
    StyleConstants.setFontSize(this.registerStyle, clamped);
    StyleConstants.setFontSize(this.defaultStyle, clamped);
    StyleConstants.setFontSize(this.immediateStyle, clamped);
    StyleConstants.setFontSize(this.tagStyle, clamped);
    StyleConstants.setFontSize(this.commentStyle, clamped);

    // Actualizar fuente del componente
    this.setFont(this.getFont().deriveFont((float) clamped));

    // Forzar re-render
    doc.setCharacterAttributes(0, doc.getLength(), this.defaultStyle, true);
    this.highLight();
    this.revalidate();
    this.repaint();
}
```

#### `increaseFontSize()` / `decreaseFontSize()`

```java
public void increaseFontSize() {
    int newSize = this.getFont().getSize() + 2;
    this.updateFontSize(newSize);
}

public void decreaseFontSize() {
    int newSize = this.getFont().getSize() - 2;
    this.updateFontSize(newSize);
}
```

#### `zoomByWheelRotation(int wheelRotation)`

```java
public void zoomByWheelRotation(int wheelRotation) {
    int direction = -wheelRotation;  // rueda arriba → incrementa
    int base = this.getFont().getSize();
    int target = base + (direction * 2);  // paso de 2px por notch
    this.updateFontSize(target);
    this.getParent().revalidate();
}
```

### Listener de Rueda de Ratón

```java
MouseWheelListener zoomListener = e -> {
    if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
        zoomByWheelRotation(e.getWheelRotation());
    }
};
this.addMouseWheelListener(zoomListener);
```

**Control:** `Ctrl + Scroll` incrementa/decrementa fuente
**Rango:** 8px (mínimo) a 48px (máximo)
**Paso:** 2px por notch de rueda

---

## 4️⃣ TEMA ANDROMEDA - UIManager Global

**Archivo:** `src/main/java/org/tectuinno/App.java` (líneas 52-227)

### Paleta de Colores

| Elemento         | Color           | Hex     |
| ---------------- | --------------- | ------- |
| Fondo Principal  | andromedaBg     | #0c0e14 |
| Fondo Secundario | andromedaBg2    | #0a0c12 |
| Hover/Selección  | andromedaHover  | #373941 |
| Acento Cyan      | andromedaAccent | #00e8c6 |
| Título Amarillo  | yellowTitle     | #ffe66d |

### 4.1 Barra de Título

```
TitlePane.background → andromedaBg (#0c0e14)
TitlePane.foreground → yellowTitle (#ffe66d)
TitlePane.inactiveForeground → yellowTitle
TitlePane.iconColor → yellowTitle
TitlePane.unifiedBackground → true
TitlePane.closeHoverBackground → #c42b1c (rojo)
TitlePane.buttonHoverBackground → andromedaHover
TitlePane.buttonPressedBackground → #50525a
```

### 4.2 Barra de Menús

```
MenuBar.background → andromedaBg
Menu.background → andromedaBg2
Menu.foreground → andromedaAccent (cyan)
Menu.selectionBackground → andromedaHover
Menu.selectionForeground → yellowTitle

MenuItem.background → andromedaBg2
MenuItem.foreground → andromedaAccent
MenuItem.selectionBackground → andromedaHover
MenuItem.selectionForeground → yellowTitle
```

### 4.3 ComboBox (Dropdown con Flecha Cian)

```
ComboBox.background → andromedaBg2
ComboBox.foreground → yellowTitle
ComboBox.buttonArrowColor → andromedaAccent ← Flecha cian
ComboBox.buttonBackground → andromedaBg2
ComboBox.buttonHoverBackground → andromedaHover
ComboBox.selectionBackground → andromedaHover
ComboBox.selectionForeground → yellowTitle
```

### 4.4 Spinner (Con Flechas Cian)

```
Spinner.background → andromedaBg2
Spinner.foreground → yellowTitle
Spinner.buttonArrowColor → andromedaAccent ← Flechas cian
Spinner.buttonBackground → andromedaBg2
Spinner.buttonHoverBackground → andromedaHover
```

### 4.5 Pestañas (Tabs)

```
TabbedPane.background → andromedaBg
TabbedPane.contentAreaColor → andromedaBg2
TabbedPane.tabsBackground → andromedaBg
TabbedPane.selectedBackground → andromedaBg2
TabbedPane.selectedForeground → yellowTitle (pestaña activa)
TabbedPane.foreground → #746f77 (pestaña inactiva)
TabbedPane.hoverColor → andromedaHover
TabbedPane.focusColor → andromedaAccent
TabbedPane.underlineHeight → 1px
TabbedPane.underlineColor → andromedaAccent (cian)
TabbedPane.selectedUnderlineColor → andromedaAccent
TabbedPane.inactiveUnderlineColor → andromedaAccent
TabbedPane.showTabSeparators → true
```

### 4.6 JFileChooser - Selecciones sin Relleno Gris

#### List View

```
FileChooser.listViewBackground → andromedaBg2
FileChooser.listViewForeground → andromedaAccent
FileChooser.listViewSelectionBackground → andromedaBg2 (NO fill)
FileChooser.listViewSelectionForeground → andromedaAccent
FileChooser.listViewBorder → LineBorder(cian, 1px)
FileChooser.listBackground → andromedaBg2
FileChooser.listSelectionBackground → andromedaBg2
FileChooser.listSelectionForeground → andromedaAccent
```

#### Detail View (Tabla)

```
FileChooser.detailViewBackground → andromedaBg2
FileChooser.detailViewSelectionBackground → andromedaBg2 (NO fill)
FileChooser.detailViewSelectionForeground → andromedaAccent
FileChooser.detailViewBorder → LineBorder(cian, 1px)
```

#### Icon View

```
FileChooser.iconViewBackground → andromedaBg2
FileChooser.iconViewSelectionBackground → andromedaBg2 (NO fill)
FileChooser.iconViewSelectionForeground → andromedaAccent
FileChooser.iconViewBorder → LineBorder(cian, 1px)
```

#### Sidebar (Elementos Rápidos)

```
FileChooser.sidebarBackground → andromedaBg
FileChooser.sidebarSelectionBackground → andromedaBg
FileChooser.sidebarSelectionForeground → andromedaAccent (cyan)
FileChooser.sidebarFocusCellHighlightBorder → LineBorder(cian, 1px)
```

#### Headers y Toolbar

```
FileChooser.lookInLabelForeground → yellowTitle
FileChooser.filesOfTypeLabelForeground → yellowTitle
FileChooser.toolbarButtonForeground → andromedaAccent
FileChooser.toolbarButtonHoverBackground → andromedaHover
FileChooser.toolbarButtonPressedBackground → andromedaHover
```

### 4.7 Botones Toolbar - Sin Relleno Gris

```
Button.toolbar.hoverBackground → transparente (rgba(0,0,0,0))
Button.toolbar.pressedBackground → transparente
Button.toolbar.selectedBackground → transparente
ToggleButton.toolbar.hoverBackground → transparente
ToggleButton.toolbar.pressedBackground → transparente
ToggleButton.toolbar.selectedBackground → transparente
```

### 4.8 Otros Controles

```
Label.foreground → andromedaAccent (cyan)
Button.foreground → andromedaAccent
Button.background → andromedaBg2
Button.hoverBackground → andromedaHover
CheckBox.foreground → andromedaAccent
RadioButton.foreground → andromedaAccent
ToolBar.background → andromedaBg
Panel.background → andromedaBg
Component.accentColor → andromedaAccent
List.focusCellHighlightBorder → LineBorder(cian, 1px)
Table.focusCellHighlightBorder → LineBorder(cian, 1px)
```

### 4.9 Diálogos (JOptionPane)

```
OptionPane.background → andromedaBg2
OptionPane.messageForeground → andromedaAccent
OptionPane.buttonBackground → andromedaBg2
```

### 4.10 Propiedades Generales

```
Component.arc → 12px (bordes redondeados)
Button.arc → 12px
TextComponent.arc → 8px
ScrollBar.thumbArc → 999 (totalmente redondeado)
ScrollBar.trackInsets → 2
```

---

## 5️⃣ MÉTODOS AUXILIARES DE THEMING

**Archivo:** `src/main/java/org/tectuinno/App.java` (líneas 228-310)

### `configureFileChooserTheme()`

Método estático que re-configura UIManager específicamente para JFileChooser:

- Colores de List, Table, TextField
- Bordes con `LineBorder` cian
- Focus borders para List y Table

**Uso:**

```java
App.configureFileChooserTheme();  // Llamar antes de crear JFileChooser
```

### `colorizeFileChooserTitleBar(Window window)`

Aplica propiedades de barra de título a diálogos JFileChooser:

```java
rootPane.putClientProperty("JRootPane.titleBarBackground", andromedaBg2);
rootPane.putClientProperty("JRootPane.titleBarForeground", yellowTitle);
rootPane.putClientProperty("JRootPane.titleBarInactiveBackground", andromedaBg);
rootPane.putClientProperty("JRootPane.titleBarButtonsForeground", yellowTitle);
rootPane.putClientProperty("JRootPane.titleBarButtonsHoverBackground", andromedaHover);
rootPane.putClientProperty("JRootPane.titleBarIconColor", yellowTitle);
```

**Uso:**

```java
App.colorizeFileChooserTitleBar(fileChooserDialog);
```

---

## 6️⃣ TOOLBAR BUTTONS - Contorno Cian Sin Fill

**Archivo:** `src/main/java/org/tectuinno/view/StartingWindow.java` (líneas 1048-1080)

### `styleToolbarButton(AbstractButton btn, Color accent, Color bg)`

Aplica estilo inicial a un botón de toolbar:

```java
private void styleToolbarButton(AbstractButton btn, Color accent, Color bg) {
    btn.setForeground(accent);
    btn.setBackground(new Color(0, 0, 0, 0));  // Transparente
    btn.setOpaque(false);
    btn.setContentAreaFilled(false);
    btn.putClientProperty("JButton.buttonType", "toolBarButton");

    // Propiedades FlatLaf para desactivar fondos en estados
    btn.putClientProperty("JButton.selectedBackground", new Color(0, 0, 0, 0));
    btn.putClientProperty("JButton.pressedBackground", new Color(0, 0, 0, 0));
    btn.putClientProperty("JButton.hoverBackground", new Color(0, 0, 0, 0));
    btn.putClientProperty("JToggleButton.selectedBackground", new Color(0, 0, 0, 0));

    btn.setBorderPainted(true);
    btn.setBorder(createEmptyBorder(2, 2, 2, 2));
    btn.setRolloverEnabled(true);

    // Listener para actualizaciones dinámicas de borde
    btn.getModel().addChangeListener(ev -> updateToolbarButtonBorder(btn, accent, bg));

    // Estado inicial
    updateToolbarButtonBorder(btn, accent, bg);
}
```

### `updateToolbarButtonBorder(AbstractButton btn, Color accent, Color bg)`

Actualiza dinámicamente el borde del botón:

```java
private void updateToolbarButtonBorder(AbstractButton btn, Color accent, Color bg) {
    ButtonModel model = btn.getModel();
    boolean active = model.isRollover() || model.isPressed() || model.isSelected();

    // Siempre mantener transparente
    btn.setBackground(new Color(0, 0, 0, 0));
    btn.setOpaque(false);
    btn.setContentAreaFilled(false);

    if (active) {
        // Borde cian al activo/hover/seleccionado
        btn.setBorder(new LineBorder(accent, 2, true));  // 2px, redondeado
    } else {
        // Sin borde cuando inactivo
        btn.setBorder(createEmptyBorder(2, 2, 2, 2));
    }

    btn.repaint();
}
```

**Comportamiento:**

- Estado inactivo: fondo transparente, sin borde
- Estado activo/hover: fondo transparente, **borde cian de 2px**
- Actualización en tiempo real mediante `ChangeListener`

---

## 7️⃣ MÉTODOS AUXILIARES DE FILECHOOSER

**Archivo:** `src/main/java/org/tectuinno/view/StartingWindow.java`

### `colorizeFileChooserWindowAsync(JFileChooser chooser)`

Aplica theming asincronamente cuando el diálogo está visible:

- Agrega `HierarchyListener` para detectar cuando el diálogo se muestra
- Llama a `colorizeFileChooserTitleBar()` para colores de título
- Llama a `applyFileChooserToolbarAccent()` para stilizar botones

### `applyFileChooserToolbarAccent(Container container)`

Encuentra la JToolBar dentro del FileChooser y aplica `applyToolbarAccentRecursive()`:

```java
private void applyFileChooserToolbarAccent(Container container) {
    if (container instanceof JToolBar) {
        applyToolbarAccentRecursive(container, accent, bg);
    }
    for (Component c : container.getComponents()) {
        if (c instanceof Container) {
            applyFileChooserToolbarAccent((Container) c);
        }
    }
}
```

### `applyToolbarAccentRecursive(Container container, Color accent, Color bg)`

Recursión para encontrar todos los `AbstractButton` y aplicar `styleToolbarButton()`:

```java
private void applyToolbarAccentRecursive(Container container, Color accent, Color bg) {
    for (Component c : container.getComponents()) {
        if (c instanceof AbstractButton) {
            styleToolbarButton((AbstractButton) c, accent, bg);
        } else if (c instanceof Container) {
            applyToolbarAccentRecursive((Container) c, accent, bg);
        }
    }
}
```

---

## 8️⃣ AUTO-SCROLL & BOTÓN LIMPIAR TERMINALES

**Archivos:**

- `src/main/java/org/tectuinno/view/component/ResultConsolePanel.java`
- `src/main/java/org/tectuinno/view/component/TerminalPanel.java`

### Botón "Limpiar Terminales"

**Ubicación:** Panel superior derecho en `ResultConsolePanel`

#### Características

- **Icono:** Bote de basura desde assets (`trash.png`)
- **Tooltip:** "Limpiar todas las terminales"
- **Acción:** Limpia los 4 paneles de terminal al mismo tiempo
- **Styling:**
  - Sin borde por defecto (`borderPainted=false`)
  - Sin relleno (`contentAreaFilled=false`)
  - Cursor en forma de mano
  - Borde visible on hover para feedback visual

#### Implementación

```java
javax.swing.ImageIcon trashIcon = new javax.swing.ImageIcon(
    getClass().getResource("/org/tectuinno/assets/trash.png"));

btnClearTerminals = new JButton(trashIcon);
btnClearTerminals.setToolTipText("Limpiar todas las terminales");
btnClearTerminals.setFocusable(false);
btnClearTerminals.setBorderPainted(false);      // Sin borde inicial
btnClearTerminals.setContentAreaFilled(false);  // Sin relleno
btnClearTerminals.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

// Hover effect
btnClearTerminals.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseEntered(MouseEvent e) {
        btnClearTerminals.setBorderPainted(true);  // Mostrar borde on hover
    }

    @Override
    public void mouseExited(MouseEvent e) {
        btnClearTerminals.setBorderPainted(false); // Ocultar borde
    }
});

btnClearTerminals.addActionListener(e -> clearAllTerminals());
```

### Auto-Scroll Automático

**Mecanismo:** `DefaultCaret` con política `ALWAYS_UPDATE`

#### Implementación en TerminalPanel

```java
((DefaultCaret) txaConsoleTextResult.getCaret())
    .setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
```

**Comportamiento:**

- Cada vez que se añade texto con `writteIn(String text)`, la terminal automáticamente scrollea al final
- El caret (cursor) se posiciona siempre en el último carácter
- No interrumpe lectura anterior del usuario (smooth scrolling)

#### Métodos que Activan Auto-Scroll

**Escribir en Terminal:**

```java
public void writteIn(String text) {
    this.txaConsoleTextResult.append("\n\r" + text);
    this.txaConsoleTextResult.setCaretPosition(
        this.txaConsoleTextResult.getDocument().getLength()
    );  // ← Fuerza scroll al final
}
```

**Limpiar Terminal (mantiene scroll bottom):**

```java
public void reset() {
    txaConsoleTextResult.setText(firstTest);
    txaConsoleTextResult.append("\n  » Listo para compilar y enviar tramas.\n");
    txaConsoleTextResult.setCaretPosition(
        txaConsoleTextResult.getDocument().getLength()
    );  // ← Posiciona al final después de limpiar
}
```

### Métodos de Limpieza en ResultConsolePanel

```java
public void clearTerminal() {
    if (terminalPanel != null) {
        terminalPanel.reset();  // Llama reset en Resultado
    }
}

public void clearTokenTerminal() {
    if (tokenTerminalPanel != null) {
        tokenTerminalPanel.reset();  // Limpia Tokens
    }
}

public void clearDisassemblyTerminal() {
    if (disassemblyTerminalPanel != null) {
        disassemblyTerminalPanel.reset();  // Limpia Disassembly
    }
}

public void clearOrderedHexTerminal() {
    if (orderedHexResultTerminalPanel != null) {
        orderedHexResultTerminalPanel.reset();  // Limpia Trama
    }
}

public void clearAllTerminals() {
    clearTerminal();
    clearTokenTerminal();
    clearDisassemblyTerminal();
    clearOrderedHexTerminal();
}
```

### Terminales Soportadas

1. **Resultado** - Salida general del compilador
2. **Tokens** - Tokens del análisis léxico
3. **Disassembly** - Desensamblado del código
4. **Trama** - Trama hexadecimal ordenada

Todas ellas se limpian simultáneamente con el botón trash

---

## 📊 TABLA RESUMEN DE CAMBIOS

| #   | Componente               | Cambio                                    | Archivo                 |
| --- | ------------------------ | ----------------------------------------- | ----------------------- |
| 1   | GPU                      | Desactivación D3D + OpenGL                | App.java                |
| 2   | Números de Línea         | Nuevo componente LineNumberPanel          | LineNumberPanel.java    |
| 3   | Zoom de Fuente           | Ctrl+Scroll para aumentar/disminuir       | AsmEditorPane.java      |
| 4   | Barra de Título          | Fondo oscuro, texto amarillo              | App.java                |
| 5   | Menús                    | Texto cian, hover amarillo                | App.java                |
| 6   | ComboBox                 | Flecha cian                               | App.java                |
| 7   | Spinner                  | Flechas cian                              | App.java                |
| 8   | Pestañas                 | Subrayado cian, texto amarillo activo     | App.java                |
| 9   | FileChooser              | Selección cian sin fill gris              | App.java                |
| 10  | Sidebar                  | Contorno cian en quick-access             | App.java                |
| 11  | Toolbar (Lista/Detalles) | Borde cian al activo, fondo transparente  | StartingWindow.java     |
| 12  | Botón Limpiar            | Icono trash, limpia todas las terminales  | ResultConsolePanel.java |
| 13  | Auto-Scroll              | Scroll automático en todas las terminales | TerminalPanel.java      |

---

## 🎨 PALETA VISUAL FINAL

### Tema Base: FlatDarkLaf + Andromeda

- **Primario Oscuro:** #0c0e14 (paneles, títulos)
- **Secundario Oscuro:** #0a0c12 (editor, diálogos)
- **Hover:** #373941 (selecciones, buttons)
- **Acento:** #00e8c6 (cyan - acentos, bordes, flechas)
- **Título:** #ffe66d (amarillo - menús activos, tabs)

### Aplicación Consistente

- ✅ Bordes: **LineBorder cian de 1-2px con rounding**
- ✅ Selecciones: **Fondo oscuro + texto cian (sin fill gris)**
- ✅ Botones toolbar: **Transparente + borde cian al activo**
- ✅ Texto general: **#d5ced9 (gris claro)**

---

## ⚙️ Empaquetado y scripts de build (Windows/Bash)

**Archivos:** [pom.xml](pom.xml), [build.bat](build.bat), [clean-build.bat](clean-build.bat), [build-and-run.bat](build-and-run.bat), [build.sh](build.sh), [clean-build.sh](clean-build.sh), [build-and-run.sh](build-and-run.sh)

### Cambios en Maven

- Salida unificada en `out/` para todos los artefactos.
- Nombres consistentes basados en la versión del POM: `Tectuinno-IDE_<version>.jar` (ejecutable con dependencias) y `Tectuinno-IDE_<version>-lib.jar` (solo clases, no ejecutable).
- `maven-assembly-plugin`: `jar-with-dependencies` sin `appendAssemblyId` para un nombre limpio.
- `maven-jar-plugin`: genera el JAR de librería con sufijo `-lib` para diferenciarlo.

### Scripts Windows (.bat)

- `build.bat`: compilación incremental (`mvn package -DskipTests`), muestra JARs generados en `out/`.
- `clean-build.bat`: compilación limpia completa (`mvn clean package -DskipTests`), lista JARs en `out/`.
- `build-and-run.bat`: compila y ejecuta el ejecutable más reciente `Tectuinno-IDE_*.jar` ignorando `-lib`; incluye `cd /d "%~dp0"` y `setlocal` para funcionar desde cualquier terminal/botón Run.

### Scripts Bash (.sh)

- `build.sh` / `clean-build.sh`: equivalentes para Git Bash/WSL, listan JARs al terminar.
- `build-and-run.sh`: selecciona el ejecutable más reciente `Tectuinno-IDE_*.jar` (excluye `-lib`) y lo ejecuta con `java -jar`.

### Uso recomendado

- Desarrollo rápido: `build.bat` o `./build.sh`.
- Build limpio para release: `clean-build.bat` o `./clean-build.sh`.
- Probar tras compilar: `build-and-run.bat` o `./build-and-run.sh`.

---

## 🔧 NOTAS TÉCNICAS IMPORTANTES

### FlatLaf Specifics

1. Las claves `Button.toolbar.*` son necesarias para botones en toolbars
2. `putClientProperty` en botones individuales puede ser ignorado; usar UIManager global
3. `LineBorder(color, width, true)` → tercer parámetro activa rounding

### Performance

1. GPU desactivada → renderizado por software más estable
2. LineNumberPanel renderiza solo líneas visibles en viewport
3. MouseWheel listener usa `InputEvent.CTRL_DOWN_MASK` para detectar Ctrl

### Synchronization

1. LineNumberPanel se sincroniza con editor via `DocumentListener` + `CaretListener`
2. Font changes en editor se propagan automáticamente a LineNumberPanel
3. Toolbar buttons se actualizan en tiempo real via `ButtonModel.ChangeListener`

---

## 📝 CHANGELOG POR SESIÓN

**Sesión 1: Foundation (Theming Base)**

- Setup FlatLaf con Andromeda
- UIManager global

**Sesión 2: FileChooser Deep Customization**

- Eliminación de gray fills
- Sidebar outline cyan
- Toolbar buttons transparentes

**Sesión 3: Editor Features**

- LineNumberPanel (números de línea)
- Zoom con Ctrl+Scroll
- GPU desactivación

**Sesión 4: Polish Final**

- Ajustes de FlatLaf properties
- Todos los botones con contorno cian
- Sincronización de estilos

---

**Versión:** v0.1.1.1  
**Fecha:** 13 de diciembre de 2025  
**Estado:** ✅ Completo
