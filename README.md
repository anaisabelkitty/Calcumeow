# Calcumeow

Calcumeow es una app de Android que hace cuatro cálculos: Propina y cuenta dividida, Distancia entre dos puntos, Sistema de ecuaciones 2×2 y Gasolina del viaje.

## Índice

1. [Qué hace la app](#qué-hace-la-app)
2. [Datos del proyecto](#datos-del-proyecto)
3. [Cómo abrir y ejecutar el proyecto](#cómo-abrir-y-ejecutar-el-proyecto)
4. [Cómo se usa](#cómo-se-usa)
5. [Pantallas y flujo](#pantallas-y-flujo)
6. [Los cálculos](#los-cálculos)
7. [Formulario dinámico](#formulario-dinámico)
8. [Validación de datos](#validación-de-datos)
9. [Resultado](#resultado)
10. [Diseño](#diseño)
11. [Detalles de uso](#detalles-de-uso)
12. [Mensajes y textos de la app](#mensajes-y-textos-de-la-app)
13. [Idiomas e ícono](#idiomas-e-ícono)
14. [Cumplimiento de los requisitos](#cumplimiento-de-los-requisitos)
15. [Pruebas](#pruebas)
16. [Estructura del proyecto](#estructura-del-proyecto)
17. [Créditos y licencias](#créditos-y-licencias)

---

## Qué hace la app

Cada uno de los cuatro cálculos tiene su propio formulario, con solo los campos que necesita. Cada campo indica su unidad y tiene una ayuda debajo. Arriba del formulario se ve la imagen de la fórmula que se usa.

Cuando presionas **Calcular**, la app primero revisa los datos. Si alguno es incorrecto, lo marca en su campo y no hace el cálculo. Si todos son correctos, muestra el resultado con su unidad.

La app está en español y en inglés, y puedes cambiar de idioma en cualquier momento.

En la pantalla de bienvenida y en el menú aparece un gatito con una **nube de texto** al lado, como un globo de cómic. Cuando este README dice "la nube", se refiere a esa nube de texto del gatito.

---

## Datos del proyecto

| Dato | Valor |
|---|---|
| Materia | Cómputo móvil, Facultad de Ingeniería, UNAM |
| Trabajo | Ejercicio 1, "Calculadora interactiva con formularios dinámicos" |
| Autora | Ana Isabel Díaz Bautista |
| Lenguaje y herramientas | Kotlin, en Android Studio |
| Versión de la app | 1.0 |
| Versión mínima de Android | Android 9 (API 28) |
| Conexión a internet | No la necesita. La app tampoco pide permisos |

---

## Cómo abrir y ejecutar el proyecto

Para abrir el proyecto necesitas Android Studio.

1. Abre Android Studio.
2. Ve al menú **File > Open**.
3. Elige la carpeta del proyecto, `Calcumeow` (la que contiene la carpeta `app`).
4. Espera a que Android Studio termine de preparar el proyecto. Es automático, pero la primera vez necesita internet para descargar las dependencias.
5. Elige dónde correr la app: un emulador con Android 9 (API 28) o más nuevo, o un teléfono conectado por USB con la depuración USB activada.
6. Presiona **Run** (el triángulo verde de la barra superior).

---

## Cómo se usa

1. Abre la app. En la pantalla de bienvenida, presiona **Empezar**.
2. En el menú, toca la tarjeta del cálculo que quieres usar. La tarjeta que tocaste se pone rosa y el texto de la nube del gatito cambia para explicar qué hace ese cálculo. Después, presiona **Abrir cálculo**.
3. Llena los campos del formulario. Cada campo muestra su unidad dentro del campo y una ayuda debajo.
4. Presiona **Calcular**. También puedes presionar **Listo** en el teclado cuando estés en el último campo.
5. Si algún dato es incorrecto, su campo se marca con borde rojo y debajo aparece lo que debes corregir. Corrígelo y vuelve a presionar **Calcular**.
6. Si todos los datos son correctos, el resultado aparece debajo del formulario y la pantalla se desplaza hasta él.
7. Con el resultado a la vista, **Limpiar** borra los datos y **Compartir** envía el resultado como texto a otra app, por ejemplo a un chat o a una nota.
8. Para cambiar el idioma, presiona el botón de idioma de la esquina superior derecha (dice ES o EN). Para regresar al menú, presiona la flecha de la esquina superior izquierda.

---

## Pantallas y flujo

| Pantalla | Qué hace |
|---|---|
| Bienvenida | Muestra el nombre de la app, el gatito con un saludo en su nube y el botón **Empezar**. |
| Menú | Muestra el gatito con su nube y cuatro tarjetas, una por cada cálculo. Al tocar una tarjeta, la nube cambia de texto. El botón **Abrir cálculo** abre el cálculo que está seleccionado. |
| Cálculo | Muestra el formulario del cálculo elegido y los botones **Calcular**, **Limpiar** y **Compartir**. |

Se pasa de una a otra en este orden: Bienvenida, Menú y Cálculo. Las tres pantallas tienen el botón de idioma.

---

## Los cálculos

Cada cálculo define sus datos, sus reglas, su validación y su fórmula. Su código está en las carpetas `calculos/` y `modelo/` (ver [Estructura del proyecto](#estructura-del-proyecto)).

Estos dos errores aplican a todos los campos:

| Situación | Mensaje |
|---|---|
| El campo está vacío | Este campo es obligatorio |
| Lo escrito no es un número | Escribe un número válido |

Los demás errores dependen de cada campo y se indican en la tabla de cada cálculo.

### 1. Propina y cuenta dividida

Calcula el total con propina y cuánto paga cada persona, a partir de 3 datos.

```
total       = cuenta × (1 + propina / 100)
por persona = total / personas
```

| Dato | Unidad | Regla | Error |
|---|---|---|---|
| Cuenta | $ | Mayor que 0 y hasta 1,000,000 | 0 o menos: "Debe ser mayor que 0". Más del máximo: "Debe ser menor o igual que 1,000,000" |
| Propina | % | De 0 a 100 | Negativa: "No puede ser negativo". Más de 100: "Debe ser menor o igual que 100" |
| Personas | pers. | Entero de 1 a 100 | 0, negativo o con decimales: "Debe ser un número entero mayor que 0". Más de 100: "Debe ser menor o igual que 100" |

Resultados: **Total con propina** ($) y **Cada persona paga** ($).

Ejemplo: con una cuenta de 850, una propina de 20 y 4 personas, el total es 1,020.00 y cada persona paga 255.00.

### 2. Distancia entre dos puntos

Calcula la distancia en línea recta entre dos puntos del plano, a partir de 4 datos: las coordenadas `x` y `y` de cada punto.

```
d = √((x₂ − x₁)² + (y₂ − y₁)²)
```

| Dato | Unidad | Regla | Error |
|---|---|---|---|
| x₁, y₁, x₂, y₂ | u (unidades del plano) | De −1,000,000 a 1,000,000. Pueden ser negativos | Más del máximo: "Debe ser menor o igual que 1,000,000". Menos del mínimo: "Debe ser mayor o igual que -1,000,000" |

Resultado: **Distancia** (u).

Ejemplo: entre los puntos (1, 2) y (4, 6) la distancia es 5.00 u.

### 3. Sistema de ecuaciones 2×2

Resuelve dos ecuaciones lineales con dos incógnitas, `x` y `y`, a partir de 6 datos: los números `a`, `b` y `c` de cada ecuación.

```
a₁x + b₁y = c₁
a₂x + b₂y = c₂

D = a₁b₂ − a₂b₁
x = (c₁b₂ − c₂b₁) / D
y = (a₁c₂ − a₂c₁) / D
```

| Dato | Unidad | Regla | Error |
|---|---|---|---|
| a₁, b₁, c₁, a₂, b₂, c₂ | sin unidad | De −1,000,000 a 1,000,000. Pueden ser negativos | Más del máximo: "Debe ser menor o igual que 1,000,000". Menos del mínimo: "Debe ser mayor o igual que -1,000,000" |

Los coeficientes son los números que acompañan a `x` y a `y`, y no llevan unidad.

> **Qué pasa cuando D vale 0.** `D` es el resultado de `a₁b₂ − a₂b₁`. Las fórmulas de `x` y de `y` dividen entre `D`, y no se puede dividir entre 0. Además, si `D` vale 0 el sistema no tiene una sola solución: o no tiene ninguna (las dos rectas son paralelas), o tiene infinitas (las dos ecuaciones son la misma recta).
>
> En ese caso la app no calcula y muestra debajo del formulario este mensaje: *"El sistema no tiene una única solución (D = 0). Cambia los coeficientes."*
>
> La app revisa `D` solo cuando los seis campos son válidos, porque `D` se calcula con esos seis números.

Resultados: **x** e **y**, sin unidad.

Ejemplo: con `2x + y = 5` y `x − y = 1`, el resultado es x = 2.00 y y = 1.00.

### 4. Gasolina del viaje

Calcula los litros que se gastan, el costo del viaje y cuánto paga cada persona, a partir de 4 datos.

```
litros      = distancia / rendimiento
costo       = litros × precio por litro
por persona = costo / personas
```

| Dato | Unidad | Regla | Error |
|---|---|---|---|
| Distancia | km | Mayor que 0 y hasta 100,000 | 0 o menos: "Debe ser mayor que 0". Más del máximo: "Debe ser menor o igual que 100,000" |
| Rendimiento | km/L | Mayor que 0 y hasta 100 | 0 o menos: "Debe ser mayor que 0". Más del máximo: "Debe ser menor o igual que 100" |
| Precio por litro | $/L | Mayor que 0 y hasta 1,000 | 0 o menos: "Debe ser mayor que 0". Más del máximo: "Debe ser menor o igual que 1,000" |
| Personas | pers. | Entero de 1 a 100 | 0, negativo o con decimales: "Debe ser un número entero mayor que 0". Más de 100: "Debe ser menor o igual que 100" |

Resultados: **Litros** (L), **Costo del viaje** ($) y **Cada persona paga** ($).

Ejemplo: en un viaje de 320 km, con un rendimiento de 14 km/L, gasolina a 24.5 $/L y 4 personas, se gastan 22.86 L, el viaje cuesta 560.00 y cada persona paga 140.00.

---

## Formulario dinámico

La pantalla de cálculo es la misma para los cuatro. Al abrirla, muestra solo los campos del cálculo elegido y oculta los demás. También cambian según el cálculo:

- Las etiquetas de los campos.
- La unidad de cada campo, que aparece dentro del campo.
- La ayuda que está debajo de cada campo.
- El título, la explicación corta y la imagen de la fórmula.

La imagen de la fórmula tiene una versión en español y otra en inglés, y se muestra la que corresponde al idioma de la app.

El teclado también cambia según el campo. Siempre es numérico. El signo menos aparece solo en los campos que aceptan negativos, y el campo Personas no tiene punto decimal porque solo acepta enteros.

---

## Validación de datos

Estas son las reglas:

- **Obligatorio:** un campo vacío da error.
- **Número válido:** se acepta punto o coma como separador decimal. Textos como `abc`, un `-` solo, `NaN` o `Infinity` no son números válidos.
- **Límites:** cada campo tiene un máximo, y también un mínimo si acepta negativos. Cuenta, Distancia, Rendimiento y Precio por litro deben ser mayores que 0. Propina puede ser 0, pero no negativa. Personas debe ser un entero de 1 a 100. Las coordenadas y los coeficientes sí pueden ser negativos.
- **Relación entre campos:** solo se usa en Sistema de ecuaciones 2×2, donde `D` debe ser distinto de 0.

Al presionar **Calcular** pasa lo siguiente:

1. La app revisa todos los datos escritos.
2. Si hay algún dato incorrecto, **no calcula**. En su lugar, marca cada campo con error con borde rojo y pone el mensaje de error debajo del campo, en el lugar donde estaba la ayuda. También aparece abajo el aviso "Corrige los campos marcados" y el cursor se va al primer campo con error.
3. Cuando escribes de nuevo en un campo con error, su error desaparece y la ayuda vuelve a mostrarse. También se oculta el resultado anterior, porque ya no corresponde a los datos escritos.
4. Si todo es correcto, la app calcula y muestra el resultado.

---

## Resultado

El resultado aparece en una tarjeta color arena debajo del formulario, con un renglón por dato: la etiqueta a la izquierda y el valor con su unidad a la derecha. Los números se muestran con dos decimales, punto decimal y coma en los miles, por ejemplo `1,020.00`. El formato es el mismo en español y en inglés, sin importar el idioma del teléfono. Solo `x` e `y` salen sin unidad.

- **Limpiar:** borra los campos, quita los errores, oculta el resultado y pone el cursor en el primer campo.
- **Compartir:** abre el selector de apps de Android para enviar el resultado como texto. El texto empieza con el nombre de la app y del cálculo (por ejemplo `Calcumeow · Gasolina del viaje`) y después sigue con una línea por resultado: etiqueta, valor y unidad. El botón solo aparece cuando ya hay un resultado.

---

## Diseño

La app usa colores aqua y petróleo, con detalles en rosa que vienen del gatito.

### Colores

| Color | Código | Dónde se usa |
|---|---|---|
| Aqua claro | `#BDE8E4` | Fondo de las tres pantallas |
| Petróleo | `#2F7280` | Barra superior, tarjetas del menú y texto de los botones claros |
| Oscuro | `#2E2429` | Texto principal y botón grande de cada pantalla (**Empezar**, **Abrir cálculo**) |
| Blanco | `#FFFFFF` | Texto sobre la barra y las tarjetas, y fondo de las tarjetas de información |
| Gris azulado claro | `#EAF1F2` | Relleno de los campos |
| Gris azulado | `#4F7782` | Unidad dentro de cada campo |
| Gris oscuro | `#4A4247` | Ayuda debajo de cada campo |
| Aqua muy claro | `#DDF1EF` | Fondo de la tarjeta de la fórmula |
| Arena | `#EADFCB` | Fondo de la tarjeta del resultado |
| Café | `#5E5240` | Texto de la tarjeta del resultado |
| Rojo | `#B71C1C` | Solo para errores: borde del campo y su mensaje |
| Rosa | `#FAB4E0` | Tarjeta seleccionada en el menú |

### Tipografía

| Fuente | Dónde se usa |
|---|---|
| Pixelify Sans (negrita) | Nombre de la app, títulos, la pregunta del menú y el texto de la nube |
| Nunito (negrita) | Números y unidades de la tarjeta del resultado |
| Fuente predeterminada de Android | Los demás textos: etiquetas, ayudas, botones y explicaciones |

### Forma y estilo

- Las tarjetas, los campos y la barra superior tienen las esquinas redondeadas.
- Todos los botones tienen forma de pastilla, es decir, con los lados completamente redondeados. Los botones grandes (**Empezar** y **Abrir cálculo**) son oscuros, y los demás son blancos con el texto en color petróleo.
- La tarjeta de la fórmula tiene un borde color petróleo.
- Los íconos de los botones y de las tarjetas son de Tabler Icons.
- La app siempre se ve con la paleta clara. No cambia a modo oscuro aunque el teléfono lo tenga activado, porque la paleta es fija.
- Los botones y textos no quedan tapados por la barra de estado, la barra de navegación ni el teclado.

### Pantallas de distintos tamaños

El diseño funciona en teléfonos de varios anchos. Si el contenido no cabe en la pantalla, por ejemplo al girar el teléfono, se puede hacer scroll hacia abajo. En el menú, el gatito y su nube se hacen un poco más pequeños antes de que aparezca el scroll.

---

## Detalles de uso

Otros comportamientos de la app:

- **Teclado:** en todos los campos menos el último, la tecla de acción pasa al siguiente campo. En el último campo dice **Listo** y calcula.
- **Después de calcular:** el teclado se esconde y la pantalla se desplaza hasta la tarjeta del resultado.
- **Al girar el teléfono o cambiar el idioma:** los datos escritos se conservan. Si había un resultado, se calcula de nuevo con los mismos datos y se muestra en el idioma nuevo.
- **Menú:** al girar el teléfono, la tarjeta seleccionada se mantiene.
- **Idioma:** la app recuerda el idioma elegido aunque la cierres.
- **Accesibilidad:** las imágenes de las fórmulas tienen una descripción que un lector de pantalla puede leer en voz alta (por ejemplo, "el total es la cuenta por uno más p entre cien").
---

## Mensajes y textos de la app

Todos los textos de la app están en los archivos `app/src/main/res/values/strings.xml` (español) y `app/src/main/res/values-en/strings.xml` (inglés).

### Mensajes de error

Aparecen en rojo, debajo del campo que tiene el problema. El último es distinto: aparece debajo del formulario.

| Cuándo aparece | Español | Inglés |
|---|---|---|
| El campo está vacío | Este campo es obligatorio | This field is required |
| Lo escrito no es un número | Escribe un número válido | Enter a valid number |
| El dato debe ser mayor que 0 y es 0 o menos | Debe ser mayor que 0 | Must be greater than 0 |
| La propina es negativa | No puede ser negativo | Cannot be negative |
| Personas no es un entero de 1 o más | Debe ser un número entero mayor que 0 | Must be a whole number greater than 0 |
| El dato pasa su máximo | Debe ser menor o igual que *máximo* | Must be at most *max* |
| El dato pasa su mínimo (coordenadas y coeficientes) | Debe ser mayor o igual que *mínimo* | Must be at least *min* |
| En el sistema, `D` vale 0 | El sistema no tiene una única solución (D = 0). Cambia los coeficientes. | The system does not have a single solution (D = 0). Change the coefficients. |

Además, al presionar **Calcular** con datos incorrectos aparece un aviso corto en la parte de abajo de la pantalla: "Corrige los campos marcados" (en inglés, "Fix the highlighted fields").

---

## Idiomas e ícono

**Idiomas.** La app está en español (idioma base) y en inglés. El idioma se cambia con el botón ES / EN de cada pantalla. Todos los textos están en los archivos de recursos mencionados arriba, no dentro del código ni de los diseños.

**Ícono.** La app tiene un ícono propio con el gatito.

---

## Cumplimiento de los requisitos

| Requisito | Dónde se cumple | Cómo |
|---|---|---|
| Varios cálculos, uno con 3 o más datos y los demás con 2 o más; que no sean conversiones directas de unidades; cada uno con su proceso o validación | `app/src/main/java/com/anaisabel/calcumeow/calculos/` | La app tiene cuatro cálculos, con 3, 4, 6 y 4 datos. Ninguno es una conversión de unidades y cada uno tiene sus reglas. Además, Sistema de ecuaciones 2×2 revisa que `D` sea distinto de 0. |
| Formulario dinámico: campos, etiquetas, unidades, ayudas e imagen de la fórmula; los campos sin uso se ocultan | `app/src/main/java/com/anaisabel/calcumeow/CalculoActivity.kt`, `app/src/main/java/com/anaisabel/calcumeow/CatalogoVista.kt` | Solo se muestran los campos del cálculo elegido, con sus textos, su unidad y su imagen de fórmula. Los demás se ocultan. |
| Validación antes de calcular: obligatorio, número válido, signo o distinto de cero, rangos y relación entre campos; no se calcula con datos incorrectos; errores en el campo | `app/src/main/java/com/anaisabel/calcumeow/modelo/Validador.kt`, cada archivo de `app/src/main/java/com/anaisabel/calcumeow/calculos/`, `app/src/main/java/com/anaisabel/calcumeow/CalculoActivity.kt` | Cada campo se valida con su regla. Si algo falla, no se calcula y el error se muestra en el campo. |
| Al presionar Calcular: recuperar los datos, validarlos, calcular y mostrar el resultado con su unidad | `app/src/main/java/com/anaisabel/calcumeow/CalculoActivity.kt` | Se leen los campos, se validan, se calcula y se muestra cada resultado con su unidad cuando la tiene. |
| Versión mínima de Android: 28 | `app/build.gradle.kts` | `minSdk = 28`. |
| Ícono personalizado | `app/src/main/AndroidManifest.xml`, `app/src/main/res/mipmap-anydpi/` | El manifiesto usa el ícono del gatito. |
| Sin textos escritos directamente en el código ni en los diseños | `app/src/main/res/values/strings.xml` y `app/src/main/res/values-en/strings.xml` | Todos los textos visibles salen de los archivos de recursos. Se revisaron el código y los diseños, y no hay textos fijos. |
| Al menos dos idiomas desde los archivos de recursos | `app/src/main/res/values/` y `app/src/main/res/values-en/` | Español e inglés, con botón para cambiar el idioma dentro de la app. |

---

## Pruebas

Hay 13 pruebas en `app/src/test/java/com/anaisabel/calcumeow/CalculosTest.kt`. Revisan los cálculos y la validación, y corren sin emulador ni teléfono. No revisan las pantallas.

### Qué revisa cada prueba

| Grupo | Prueba | Qué comprueba |
|---|---|---|
| Lectura de números | `leerNumero_aceptaPuntoComaYEspacios` | `1,5` se lee como 1.5, ` 7 ` (con espacios) como 7 y `-2.25` como −2.25 |
| | `leerNumero_rechazaTextoNoNumerico` | Un texto vacío, `abc`, `-`, `NaN` e `Infinity` no se aceptan como número |
| Propina y cuenta dividida | `propina_calculaTotalYPagoPorPersona` | Con cuenta 850, propina 20 y 4 personas, el total es 1,020 y cada persona paga 255 |
| | `propina_validaRangosYTipos` | Cuenta vacía da "obligatorio", propina 120 da "máximo 100" y personas 2.5 da "debe ser entero" |
| | `propina_aceptaCeroPeroNoNegativos` | Propina 0 es válida, propina −5 da "No puede ser negativo" y cuenta 0 da "Debe ser mayor que 0" |
| Distancia entre dos puntos | `distancia_calculaConPuntosPositivos` | Entre (1, 2) y (4, 6) la distancia es 5 |
| | `distancia_aceptaCoordenadasNegativas` | Entre (−1, −1) y (2, 3) la distancia también es 5 |
| | `distancia_rechazaTextoIncompleto` | `y₁` vacío da "obligatorio", `x₂` con solo `-` da "número inválido" y `x₁`, que está bien, no da error |
| Sistema de ecuaciones 2×2 | `sistema_resuelveSolucionUnica` | Con `2x + y = 5` y `x − y = 1`, el resultado es x = 2 y y = 1 |
| | `sistema_rechazaDeterminanteCero` | Con `x + 2y = 3` y `2x + 4y = 6`, `D` vale 0, así que no se calcula y se marca el error del sistema |
| | `sistema_noRevisaElDeterminanteSiHayCamposInvalidos` | Si `a₁` está vacío, solo sale el error de ese campo y todavía no se revisa `D` |
| Gasolina del viaje | `gasolina_calculaLitrosCostoYPagoPorPersona` | Con 320 km, 14 km/L, 24.5 $/L y 4 personas: 22.86 L, costo 560 y 140 por persona |
| | `gasolina_validaQueTodoSeaPositivoYConLimites` | Distancia 0, rendimiento 500, precio −3 y personas 0 dan un error cada uno |

### Cómo ejecutarlas

En Android Studio:

1. Abre el archivo `CalculosTest.kt`.
2. Haz clic derecho sobre el nombre de la clase `CalculosTest` y elige **Run 'CalculosTest'**. Otra opción es usar el menú **Run > Run Tests in 'CalculosTest'**.
3. Los resultados aparecen en la ventana de abajo.

También puedes correrlas desde la terminal, en la carpeta del proyecto:

```
gradlew.bat testDebugUnitTest      (Windows)
./gradlew testDebugUnitTest        (macOS o Linux)
```

---

## Estructura del proyecto

```
app/src/main/java/com/anaisabel/calcumeow/
  calculos/    los cuatro cálculos
  modelo/      reglas de validación y errores
  WelcomeActivity.kt, MenuActivity.kt, CalculoActivity.kt   las tres pantallas
  CatalogoVista.kt   textos, íconos e imágenes de cada cálculo
  Utilidades.kt      cambio de idioma, formato de números y ajuste a las barras del sistema
app/src/main/res/
  values/ (español)    values-en/ (inglés)
  drawable-nodpi/ (fórmulas en español)    drawable-en-nodpi/ (fórmulas en inglés)
  font/      tipos de letra (Pixelify Sans y Nunito)
  layout/    diseño de las pantallas
app/src/test/          pruebas
docs/                  licencias de fuentes e íconos
```

---

## Créditos y licencias

| Recurso | Para qué se usa | Licencia | Archivo |
|---|---|---|---|
| Fuente Pixelify Sans | Nombre de la app, títulos y nube del menú | SIL Open Font License 1.1 | `docs/PixelifySans-OFL.txt` |
| Fuente Nunito | Números del resultado | SIL Open Font License 1.1 | `docs/Nunito-OFL.txt` |
| Tabler Icons | Íconos de botones y tarjetas | MIT | `docs/TablerIcons-LICENSE.txt` |
