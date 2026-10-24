CREATE DATABASE IF NOT EXISTS dailydungeon
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE dailydungeon;

-- ---------------------------------------------------------------------
-- CATÁLOGOS (datos fijos del juego)
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS clase (
                                     id_clase    INT          NOT NULL AUTO_INCREMENT,
                                     nombre      VARCHAR(30)  NOT NULL,
    descripcion VARCHAR(200),
    PRIMARY KEY (id_clase),
    UNIQUE KEY uq_clase_nombre (nombre)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS atributo (
                                        id_atributo INT          NOT NULL AUTO_INCREMENT,
                                        nombre      VARCHAR(30)  NOT NULL,
    descripcion VARCHAR(200),
    PRIMARY KEY (id_atributo),
    UNIQUE KEY uq_atributo_nombre (nombre)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS dificultad (
                                          id_dificultad INT         NOT NULL AUTO_INCREMENT,
                                          nombre        VARCHAR(30) NOT NULL,
    xp_base       INT         NOT NULL,
    PRIMARY KEY (id_dificultad),
    UNIQUE KEY uq_dificultad_nombre (nombre),
    CONSTRAINT ck_dificultad_xp CHECK (xp_base > 0)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS prioridad (
                                         id_prioridad INT         NOT NULL AUTO_INCREMENT,
                                         nombre       VARCHAR(30) NOT NULL,
    orden        INT         NOT NULL,
    PRIMARY KEY (id_prioridad),
    UNIQUE KEY uq_prioridad_nombre (nombre),
    UNIQUE KEY uq_prioridad_orden (orden)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS estado_mision (
                                             id_estado INT         NOT NULL AUTO_INCREMENT,
                                             nombre    VARCHAR(40) NOT NULL,
    orden     INT         NOT NULL,
    PRIMARY KEY (id_estado),
    UNIQUE KEY uq_estado_nombre (nombre),
    UNIQUE KEY uq_estado_orden (orden)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS logro (
                                     id_logro       INT          NOT NULL AUTO_INCREMENT,
                                     nombre         VARCHAR(50)  NOT NULL,
    descripcion    VARCHAR(200),
    valor_objetivo INT          NOT NULL,
    PRIMARY KEY (id_logro),
    UNIQUE KEY uq_logro_nombre (nombre),
    CONSTRAINT ck_logro_objetivo CHECK (valor_objetivo > 0)
    ) ENGINE=InnoDB;

-- Valor inicial y crecimiento por nivel de cada atributo en cada clase
CREATE TABLE IF NOT EXISTS clase_atributo (
                                              id_clase              INT NOT NULL,
                                              id_atributo           INT NOT NULL,
                                              valor_inicial         INT NOT NULL,
                                              crecimiento_por_nivel INT NOT NULL,
                                              PRIMARY KEY (id_clase, id_atributo),
    CONSTRAINT fk_clase_atributo_clase
    FOREIGN KEY (id_clase)    REFERENCES clase (id_clase),
    CONSTRAINT fk_clase_atributo_atributo
    FOREIGN KEY (id_atributo) REFERENCES atributo (id_atributo),
    CONSTRAINT ck_clase_atributo_valores
    CHECK (valor_inicial >= 0 AND crecimiento_por_nivel >= 0)
    ) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- USUARIOS Y HÉROES
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS usuario (
                                       id_usuario      INT          NOT NULL AUTO_INCREMENT,
                                       nombre_usuario  VARCHAR(30)  NOT NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    contrasena_sal  VARCHAR(255) NOT NULL,
    fecha_creacion  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    eliminado       BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_usuario),
    UNIQUE KEY uq_usuario_nombre (nombre_usuario)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS heroe (
                                     id_heroe       INT         NOT NULL AUTO_INCREMENT,
                                     id_usuario     INT         NOT NULL,
                                     id_clase       INT         NOT NULL,
                                     nombre         VARCHAR(30) NOT NULL,
    nivel          INT         NOT NULL DEFAULT 1,
    xp             INT         NOT NULL DEFAULT 0,   -- XP dentro del nivel actual
    hp_actual      INT         NOT NULL,
    fecha_creacion DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    eliminado      BOOLEAN     NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_heroe),
    UNIQUE KEY uq_heroe_usuario (id_usuario),        -- un héroe por usuario
    CONSTRAINT fk_heroe_usuario
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_heroe_clase
    FOREIGN KEY (id_clase)   REFERENCES clase (id_clase),
    CONSTRAINT ck_heroe_valores
    CHECK (nivel >= 1 AND xp >= 0 AND hp_actual >= 0)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS heroe_atributo (
                                              id_heroe    INT NOT NULL,
                                              id_atributo INT NOT NULL,
                                              valor       INT NOT NULL,
                                              PRIMARY KEY (id_heroe, id_atributo),
    CONSTRAINT fk_heroe_atributo_heroe
    FOREIGN KEY (id_heroe)    REFERENCES heroe (id_heroe),
    CONSTRAINT fk_heroe_atributo_atributo
    FOREIGN KEY (id_atributo) REFERENCES atributo (id_atributo),
    CONSTRAINT ck_heroe_atributo_valor CHECK (valor >= 0)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS heroe_logro (
                                           id_heroe         INT      NOT NULL,
                                           id_logro         INT      NOT NULL,
                                           fecha_desbloqueo DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           PRIMARY KEY (id_heroe, id_logro),
    CONSTRAINT fk_heroe_logro_heroe
    FOREIGN KEY (id_heroe) REFERENCES heroe (id_heroe),
    CONSTRAINT fk_heroe_logro_logro
    FOREIGN KEY (id_logro) REFERENCES logro (id_logro)
    ) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- TABLEROS Y MISIONES
-- ---------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS tablero (
                                       id_tablero     INT         NOT NULL AUTO_INCREMENT,
                                       id_heroe       INT         NOT NULL,
                                       nombre         VARCHAR(50) NOT NULL,
    fecha_creacion DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    eliminado      BOOLEAN     NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_tablero),
    CONSTRAINT fk_tablero_heroe
    FOREIGN KEY (id_heroe) REFERENCES heroe (id_heroe)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS mision (
                                      id_mision      INT          NOT NULL AUTO_INCREMENT,
                                      id_tablero     INT          NOT NULL,
                                      id_dificultad  INT          NOT NULL,
                                      id_prioridad   INT          NOT NULL,
                                      id_estado      INT          NOT NULL,
                                      titulo         VARCHAR(100) NOT NULL,
    descripcion    TEXT,
    fecha_limite   DATE         NULL,
    penalizada     BOOLEAN      NOT NULL DEFAULT FALSE,  -- evita penalizar dos veces
    fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    eliminado      BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_mision),
    INDEX idx_mision_fecha_limite (fecha_limite),
    CONSTRAINT fk_mision_tablero
    FOREIGN KEY (id_tablero)    REFERENCES tablero (id_tablero),
    CONSTRAINT fk_mision_dificultad
    FOREIGN KEY (id_dificultad) REFERENCES dificultad (id_dificultad),
    CONSTRAINT fk_mision_prioridad
    FOREIGN KEY (id_prioridad)  REFERENCES prioridad (id_prioridad),
    CONSTRAINT fk_mision_estado
    FOREIGN KEY (id_estado)     REFERENCES estado_mision (id_estado)
    ) ENGINE=InnoDB;

-- Una misión se completa una sola vez (UNIQUE en id_mision)
CREATE TABLE IF NOT EXISTS historial_mision (
                                                id_historial     INT      NOT NULL AUTO_INCREMENT,
                                                id_mision        INT      NOT NULL,
                                                xp_obtenida      INT      NOT NULL,
                                                fue_critico      BOOLEAN  NOT NULL DEFAULT FALSE,
                                                fecha_completada DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                PRIMARY KEY (id_historial),
    UNIQUE KEY uq_historial_mision (id_mision),
    CONSTRAINT fk_historial_mision
    FOREIGN KEY (id_mision) REFERENCES mision (id_mision),
    CONSTRAINT ck_historial_xp CHECK (xp_obtenida >= 0)
    ) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- DATOS INICIALES
-- ---------------------------------------------------------------------

INSERT IGNORE INTO clase (id_clase, nombre, descripcion) VALUES
    (1, 'Guerrero', 'Aguanta los descuidos gracias a su vida y resistencia.'),
    (2, 'Mago',     'Aprende rápido y tiene golpes de suerte.'),
    (3, 'Pícaro',   'Esquiva castigos y busca golpes críticos.');

INSERT IGNORE INTO atributo (id_atributo, nombre, descripcion) VALUES
    (1, 'Vitalidad',   'Aumenta la vida máxima.'),
    (2, 'Sabiduría',   'Da un bonus de XP en cada misión.'),
    (3, 'Resistencia', 'Reduce el daño de las misiones vencidas.'),
    (4, 'Suerte',      'Da probabilidad de golpe crítico con XP extra.');

-- Todos empiezan con 5. Principal +3 por nivel, secundario +2, resto +1.
INSERT IGNORE INTO clase_atributo
    (id_clase, id_atributo, valor_inicial, crecimiento_por_nivel) VALUES
    -- Guerrero: Vitalidad y Resistencia
    (1, 1, 5, 3), (1, 2, 5, 1), (1, 3, 5, 2), (1, 4, 5, 1),
    -- Mago: Sabiduría y Suerte
    (2, 1, 5, 1), (2, 2, 5, 3), (2, 3, 5, 1), (2, 4, 5, 2),
    -- Pícaro: Suerte y Resistencia
    (3, 1, 5, 1), (3, 2, 5, 1), (3, 3, 5, 2), (3, 4, 5, 3);

INSERT IGNORE INTO dificultad (id_dificultad, nombre, xp_base) VALUES
    (1, 'Fácil',   10),
    (2, 'Media',   25),
    (3, 'Difícil', 50),
    (4, 'Épica',  100);

INSERT IGNORE INTO prioridad (id_prioridad, nombre, orden) VALUES
    (1, 'Baja',  1),
    (2, 'Media', 2),
    (3, 'Alta',  3);

INSERT IGNORE INTO estado_mision (id_estado, nombre, orden) VALUES
    (1, 'Tablón de misiones', 1),
    (2, 'En aventura',        2),
    (3, 'Gloria',             3);

-- Logros de ejemplo (opcionales)
INSERT IGNORE INTO logro (id_logro, nombre, descripcion, valor_objetivo) VALUES
    (1, 'Primer paso',   'Completá tu primera misión.',  1),
    (2, 'Aventurero',    'Completá 10 misiones.',       10),
    (3, 'Leyenda',       'Completá 50 misiones.',       50);