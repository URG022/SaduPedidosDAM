#!/usr/bin/env bash
# Configura etiquetas, milestones y la rama develop usando GitHub CLI (gh).
# Uso: gh auth login   (una vez)   y luego:   bash scripts/configurar-github.sh
set -e
REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
echo "Configurando $REPO"

crear_label () { gh label create "$1" --color "$2" --description "$3" --force; }
crear_label acceso   5e6cb3 "Login, registro, sesión, filtros"
crear_label menu     f5c74d "Categorías y productos"
crear_label clientes 8fa0d8 "Mantenimiento de clientes"
crear_label pedidos  46528f "Pedido, detalle, transacción"
crear_label cocina   e0ad2a "Tablero y estados del pedido"
crear_label bd       3e9b6b "Esquema SQL, DAO, conexión"
crear_label docs     bfb6a8 "Documentación y evidencias"

for g in "01 Entorno" "02 Formularios y sesiones" "03 POO y SOLID" "04 Servlets y MVC" \
         "05 JDBC y MySQL" "06 DAO y CRUD" "07 CRUD relacionado" "08 JSF y Managed Beans"; do
  gh api "repos/$REPO/milestones" -f title="Guía $g" >/dev/null 2>&1 && echo "Milestone Guía $g" || echo "Milestone Guía $g ya existe"
done

git fetch origin
if ! git ls-remote --exit-code --heads origin develop >/dev/null; then
  git checkout -b develop && git push -u origin develop && git checkout main
fi
echo "Listo. Falta: proteger main y develop en Settings > Branches e invitar colaboradores."
