-- Guía 7: datos mínimos para probar el registro de tickets.
-- El script es idempotente: no duplica técnicos ni equipos.
--
-- Nota sobre prioridades: la tabla tickets de la Guía 5 tiene un CHECK que
-- acepta únicamente BAJA, MEDIA y ALTA. Por eso la aplicación valida esas
-- tres prioridades. Si más adelante desea habilitar CRÍTICA, un usuario con
-- privilegio ALTER puede ejecutar:
--
-- ALTER TABLE tickets DROP CHECK chk_ticket_prioridad;
-- ALTER TABLE tickets ADD CONSTRAINT chk_ticket_prioridad
--     CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'CRÍTICA'));
--
-- y agregar CRÍTICA a las listas de TicketService, TicketServlet y las vistas.

USE servicedesk360_db;

-- Ajuste los ids 1 y 2 si sus clientes usan otros identificadores.
INSERT INTO tecnicos (nombre, correo, especialidad, activo)
SELECT 'Tecnico de Laboratorio', 'tecnico.lab@demo.local',
       'Soporte tecnico', 1
WHERE NOT EXISTS (
    SELECT 1 FROM tecnicos WHERE correo = 'tecnico.lab@demo.local'
);

INSERT INTO equipos (id_cliente, codigo_inventario, tipo, marca, modelo, activo)
SELECT c.id_cliente, 'LAB-EQ-C1', 'Laptop', 'Dell', 'Latitude 5420', 1
FROM clientes c
WHERE c.id_cliente = 1
  AND NOT EXISTS (
      SELECT 1 FROM equipos e WHERE e.codigo_inventario = 'LAB-EQ-C1'
  );

INSERT INTO equipos (id_cliente, codigo_inventario, tipo, marca, modelo, activo)
SELECT c.id_cliente, 'LAB-EQ-C2', 'Desktop', 'HP', 'EliteDesk 800', 1
FROM clientes c
WHERE c.id_cliente = 2
  AND NOT EXISTS (
      SELECT 1 FROM equipos e WHERE e.codigo_inventario = 'LAB-EQ-C2'
  );

SELECT id_cliente, nombre, correo, activo FROM clientes ORDER BY id_cliente;
SELECT id_equipo, id_cliente, codigo_inventario, marca, modelo, activo
FROM equipos ORDER BY id_equipo;
SELECT id_tecnico, nombre, correo, activo FROM tecnicos ORDER BY id_tecnico;
SELECT id_categoria, nombre FROM categorias ORDER BY id_categoria;
