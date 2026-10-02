# ⚔️ DailyDungeon

> Tus tareas de la vida real son las misiones de tu héroe.

**DailyDungeon** es un juego de rol de escritorio que convierte tus tareas en una aventura. Cargás tus pendientes como misiones, y cumplirlas hace crecer a tu personaje. Dejarlas vencer, en cambio, lo debilita.

Está inspirado en Habitica, pero sin hábitos: acá cada tarea es única y el foco está en el héroe y su aventura.

---

## Cómo se juega

1. **Te registrás** con usuario y contraseña (guardada cifrada, nunca en texto plano), **elegís tu clase** y le ponés nombre a tu héroe. Cada usuario tiene su propio héroe y sus propios tableros.
2. **Creás un tablero** para cada ámbito de tu vida: "Facultad", "Casa", "Proyecto final"...
3. **Cargás misiones** con título, descripción, fecha límite, dificultad y prioridad.
4. **Avanzás cada misión por tres etapas**, que son pantallas completas por las que te movés con botones:

   | Etapa | Significado |
   |-------|-------------|
   | 📜 Tablón de misiones | Lo que todavía no empezaste |
   | 🗡️ En aventura | Lo que estás haciendo ahora |
   | 🏆 Gloria | Lo que ya cumpliste |

5. **Al completar una misión** aparece la pantalla de recompensa con la experiencia ganada.
6. **Si una misión vence**, tu héroe pierde vida.

---

## El héroe

Cada héroe tiene **nivel, experiencia (XP), vida (HP) y cuatro atributos** que cambian cómo se juega:

| Atributo | Qué hace |
|----------|----------|
| ❤️ **Vitalidad** | Aumenta la vida máxima |
| 📖 **Sabiduría** | Da un bonus de XP en cada misión |
| 🛡️ **Resistencia** | Reduce el daño de las misiones vencidas |
| 🍀 **Suerte** | Da probabilidad de un golpe crítico: XP extra |

### Las clases

Cada clase se especializa en uno o dos atributos, que crecen más rápido cada vez que sube de nivel:

| Clase | Se especializa en | Estilo |
|-------|------------------|--------|
| 🛡️ **Guerrero** | Vitalidad y Resistencia | Aguanta los descuidos |
| 🔮 **Mago** | Sabiduría y Suerte | Sube de nivel más rápido |
| 🗝️ **Pícaro** | Suerte y Resistencia | Esquiva castigos y busca críticos |

### Progresión

- La **XP** que da cada misión depende de su dificultad: Fácil, Media, Difícil o Épica.
- Al **subir de nivel**, los atributos crecen automáticamente según la clase.
- Si la **vida llega a 0**, el héroe pierde un nivel (y los atributos que había ganado en él), pero recupera la mitad de su vida. Nunca pierde la partida.

---

## Estadísticas

Una pantalla muestra el historial del héroe: misiones completadas, XP total y progreso por tablero.

---

## Arquitectura

El proyecto sigue el patrón **MVC** separado en capas, para que las reglas del juego no dependan de la pantalla ni de la base de datos:

- **Dominio:** el héroe, las misiones y las reglas del juego (XP, niveles, penalizaciones).
- **Servicios:** las acciones que puede hacer el usuario (completar una misión, crear un tablero).
- **Persistencia:** guarda y recupera los datos en MySQL.
- **Vista y controladores:** las pantallas y la conexión con los servicios, sin lógica de juego.

Hecho con **Java 21**, **JavaFX**, **MySQL** y **Maven**.

---

## Cómo ejecutarlo

1. Iniciar MySQL desde XAMPP e importar `sql/schema.sql` en phpMyAdmin.
2. Copiar `db.example.properties` como `db.properties` (por defecto en XAMPP: usuario `root`, sin contraseña).
3. Ejecutar `./mvnw clean javafx:run` (en Windows: `mvnw.cmd clean javafx:run`).

---

## Autor

Desarrollado por **Joaquin Plaza**.
