# Matriz de pruebas

| ID | Guía | Módulo | Caso | Datos | Resultado esperado | Resultado obtenido | Responsable |
| --- | --- | --- | --- | --- | --- | --- | --- |
| P01 | 01 | base | Abrir `/SaduPedidosDam/` | — | Inicio con logo de Sadu | | Dev 1 |
| P02 | 01 | base | Abrir `/diagnostico` | — | Contexto, método, servidor y Java visibles | | Dev 1 |
| P03 | 02 | acceso | Registro con correo repetido | correo existente | Mensaje de error, no se crea cuenta | OK | Dev 1 |
| P04 | 04 | acceso | Abrir `/cliente/pedidos` sin sesión | — | Redirige a login y luego regresa | OK | Dev 1 |
| P05 | 06 | menu | Crear producto con precio 0 | precio = 0 | Rechazado con mensaje | | Dev 2 |
| P06 | 07 | pedidos | Pedido con producto no disponible | producto inactivo | Rollback, no queda pedido parcial | | Dev 3 |
| P07 | 07 | cocina | Dos usuarios cambian el mismo estado | estado esperado distinto | Segundo recibe aviso de conflicto | | Dev 3 |
| P08 | 05 | acceso | Registro de cliente nuevo | datos válidos | Crea usuario + cliente en una transacción y deja la sesión iniciada | OK | Dev 1 |
| P09 | 02 | acceso | Login con "recordar mi correo" | ana.martinez@demo.local | Cookie `saduCorreo` 30 días; el login muestra el correo | OK | Dev 1 |
| P10 | 04 | acceso | Cliente entra a `/panel` | sesión de cliente | Error 403 de Sadu | OK | Dev 1 |
| P11 | 04 | acceso | Cocina entra a `/admin/dashboard` | sesión de cocina | Error 403 de Sadu | OK | Dev 1 |
| P12 | 04 | acceso | `volver` apuntando a otro sitio | volver=https://evil.com | Ignora el parámetro y va al inicio | OK | Dev 1 |
