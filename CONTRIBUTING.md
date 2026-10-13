# Cómo trabajar en Sadu Pedidos

## Ramas
| Rama | Uso |
| --- | --- |
| `main` | Lo entregado al docente. Solo el líder hace merge desde `develop` al cerrar cada guía. |
| `develop` | Integración semanal. |
| `guiaNN-tema` | Rama de cada guía (ej. `guia04-mvc`). La crea el líder desde `develop`. |
| `feature/devN-tema` | Tu trabajo (ej. `feature/dev3-pedido-nuevo`). |

## Flujo de cada tarea
```bash
git checkout guia04-mvc
git pull
git checkout -b feature/dev2-menu-servlet
# ... trabajar, probar en Tomcat ...
git status            # revisar que no vayan target/, db.properties, etc.
git add .
git commit -m "feat(menu): listado de productos con JSTL"
git push -u origin feature/dev2-menu-servlet
```
Luego abrir un **Pull Request** hacia la rama de la guía y pedir revisión. Antes de abrir el PR:
`git pull origin guia04-mvc` para traer los cambios de los demás y resolver conflictos en tu rama.

## Mensajes de commit
`tipo(modulo): descripción en minúsculas`
- Tipos: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `style`
- Módulos: `acceso`, `menu`, `clientes`, `pedidos`, `cocina`, `bd`, `docs`

## Reglas de arquitectura
- JSP/XHTML sin SQL ni reglas de negocio; sin scriptlets.
- Servlet/Bean sin HTML ni `PreparedStatement`.
- Servicio sin `HttpServletRequest` ni `HttpSession`.
- DAO sin HTTP; solo `PreparedStatement` y `try-with-resources`.
- Solo `javax.*` (Tomcat 9). Nada de `jakarta.*` en esta rama.
- Credenciales solo en `~/.sadu/db.properties`, nunca en el repo.

## Definición de terminado
- [ ] `mvn clean package` sin errores
- [ ] Probado desplegado en Tomcat 9
- [ ] Caso agregado a `docs/matriz_pruebas.md` y captura en `docs/evidencias/guiaNN/`
- [ ] PR aprobado por al menos 1 compañero
- [ ] Puedo explicar mi código en la verificación individual
