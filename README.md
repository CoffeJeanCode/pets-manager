# EcoGallery

### *PANTALLA 1: Catálogo (Principal)*

*Diseño:*
- Título: "Catálogo EcoGarden"
- 3 pestañas horizontales: 🌱 Plantas | 🪴 Macetas | 📱 Accesorios

*Pestaña Plantas:*
- *Consume:* GET /plantas (al abrir la pestaña)
- *Muestra:* Grid de tarjetas con:
  - Nombre de la planta
  - Nivel de luz requerido (1-5 estrellas)
  - Tipo de riego (bajo/medio/alto)
  - Precio
  - Botón "Agregar al diseño"

*Pestaña Macetas:*
- *Consume:* GET /macetas (al abrir la pestaña)
- *Muestra:* Grid de tarjetas con:
  - Nombre de la maceta
  - Material (cerámica, plástico, barro)
  - Tamaño (pequeña/mediana/grande)
  - Precio
  - Botón "Agregar al diseño"

*Pestaña Accesorios:*
- *Consume:* GET /accesorios (al abrir la pestaña)
- *Muestra:* Grid de tarjetas con:
  - Nombre del accesorio
  - Función (sensor, timer, lámpara)
  - Conectividad (WiFi/Bluetooth)
  - Precio
  - Botón "Agregar al diseño"

---

### *PANTALLA 2: Recomendaciones*

*Diseño:*
- Título: "¿Cuánta luz recibe tu espacio?"
- Slider visual de 1 a 5 estrellas (☆☆☆☆☆)
- Label mostrando nivel seleccionado: "Nivel: 3/5"
- Botón grande: "Recomendar Plantas"
- Área de resultados (vacía al inicio)

*Flujo:*
1. Usuario mueve el slider (1-5)
2. Usuario hace clic en "Recomendar Plantas"
3. *Consume:* GET /recomendaciones?nivel_luz=3
4. *Muestra:* 
   - Mensaje: "Encontramos X plantas perfectas para tu espacio"
   - Grid de tarjetas solo con plantas compatibles
   - Cada tarjeta igual que en Catálogo

---

### *PANTALLA 3: Diseñador Visual* (Opcional - versión simple)

*Diseño:*
- Panel central grande (lienzo/canvas) color verde claro
- Panel inferior con:
  - Campo de texto: "Nombre del jardín"
  - Botón: "Guardar Jardín"

*Flujo:*
1. Usuario arrastra elementos desde Catálogo (o hace clic)
2. Elementos aparecen en el canvas con posición X,Y
3. Usuario escribe nombre del jardín
4. Usuario hace clic en "Guardar Jardín"
5. *Consume:* POST /jardin (envía nombre + elementos con posiciones)
6. *Muestra:* Mensaje de confirmación "Jardín guardado correctamente"

*Nota:* Si no da tiempo, esta pantalla puede ser solo un botón que guarde datos de ejemplo.

---

### *Clases POO Frontend (Java):*
- ElementoJardin (abstracta) - herencia, abstracción
- Planta - hereda de ElementoJardin
- Maceta - hereda de ElementoJardin
- AccesorioSmart - hereda de ElementoJardin
- ApiService - clase que consume los endpoints
- Todos implementan: encapsulamiento, polimorfismo, sobrecarga

---

## 📦 *Modelos & DTOs EcoGallery*

Los modelos Java del escritorio ahora reflejan por completo el dominio de EcoGallery:

- `GardenItem`: clase abstracta base con `id`, `name`, `price` e `imageUrl`.
- `Plant`: añade `lightLevel`, `wateringType`, `description` y `season`.
- `Planter`: añade `material`, `size`, `color` y si cuenta con `drainage`.
- `SmartAccessory`: añade `feature`, `connectivity`, `compatibility` y `powerUsage`.
- `GardenElement`: representación liviana de un elemento colocado en el canvas (posición X/Y + escala).
- `GardenDesign`: diseño guardado con `nombre`, lista de `GardenElement` y marcas de tiempo.

Para la comunicación con la API existen DTOs agrupados por entidad:

- `PlantDto`: incluye `PlantResponse`, `PlantCreateRequest` y `PlantUpdateRequest`.
- `PotDto`: respuestas y peticiones de creación/actualización para macetas.
- `AccessoryDto`: estructura equivalente para accesorios inteligentes.
- `RecommendationDto`: request `nivelLuz` + response con plantas compatibles.
- `GardenDesignDto`: define `GardenElementDto`, respuesta de diseños y payload para guardar jardines.

Todos los DTOs están agrupados en un solo archivo por entidad para facilitar el mantenimiento y cumplen con los campos descritos en los flujos del catálogo, recomendaciones y diseñador visual.

### 💻 Vista de escritorio

- **Catálogo** interactivo con tarjetas minimalistas y texto en español.
- **Recomendaciones** con slider de luz que alimenta sugerencias mock.
- **Diseñador visual** con lienzo estilo canvas para arrastrar elementos y planear jardines.

---

## 🔄 *FLUJOS DE CONSUMO API*

### *Flujo 1: Ver Catálogo de Plantas*

Usuario abre app 
→ Pantalla Catálogo se abre automáticamente
→ Java: api.obtenerPlantas() 
→ Python: GET /plantas
→ BD: SELECT * FROM plantas
→ Python: Devuelve JSON
→ Java: Muestra tarjetas


### *Flujo 2: Ver Recomendaciones*

Usuario va a Recomendaciones
→ Usuario mueve slider a nivel 3
→ Usuario hace clic en "Recomendar"
→ Java: api.obtenerRecomendaciones(3)
→ Python: GET /recomendaciones?nivel_luz=3
→ BD: SELECT * FROM plantas WHERE nivel_luz <= 3
→ Python: Devuelve plantas filtradas
→ Java: Muestra solo plantas compatibles


### *Flujo 3: Guardar Jardín* (Opcional)
```
Usuario diseña jardín
→ Usuario escribe nombre
→ Usuario hace clic en "Guardar"
→ Java: api.guardarJardin(nombre, elementos)
→ Python: POST /jardin
→ BD: INSERT INTO jardines, INSERT INTO jardin_elementos
→ Python: Devuelve confirmación
→ Java: Muestra mensaje de éxito