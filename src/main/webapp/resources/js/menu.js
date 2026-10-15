/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener("DOMContentLoaded", function() {
    const buscador = document.getElementById("buscar");
    const botonesFiltro = document.querySelectorAll("#filtros-categoria button");
    const platillos = document.querySelectorAll(".platillo");
    
    let categoriaActual = "Todos";

    function filtrarPlatillos() {
        const textoBusqueda = buscador.value.toLowerCase().trim();

        platillos.forEach(platillo => {
            const categoriaPlatillo = platillo.getAttribute("data-categoria");
            const nombrePlatillo = platillo.querySelector("h3").textContent.toLowerCase();
            const descripcionPlatillo = platillo.querySelector("p").textContent.toLowerCase();
            
            const coincideCategoria = (categoriaActual === "Todos" || categoriaPlatillo === categoriaActual);
            const coincideTexto = nombrePlatillo.includes(textoBusqueda) || descripcionPlatillo.includes(textoBusqueda);

            if (coincideCategoria && coincideTexto) {
                platillo.style.display = "flex";
            } else {
                platillo.style.display = "none";
            }
        });
    }

    if(buscador) {
        buscador.addEventListener("input", filtrarPlatillos);
    }

    if(botonesFiltro) {
        botonesFiltro.forEach(boton => {
            boton.addEventListener("click", function() {
                botonesFiltro.forEach(b => {
                    b.classList.remove("btn-azul");
                    b.classList.add("btn-borde");
                });
                this.classList.remove("btn-borde");
                this.classList.add("btn-azul");

                categoriaActual = this.getAttribute("data-cat");
                filtrarPlatillos();
            });
        });
    }
});


