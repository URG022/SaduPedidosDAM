/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.itca.sadu.model;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author serva
 */
public class ReporteDTO implements Serializable {
    private double ventasTotales;
    private double ticketPromedio;
    private int pedidosActivos;
    private int totalPedidosEntregados;
    private String horaPico;
    private Map<String, Integer> topPlatillos;
    private Map<String, Integer> pedidosPorEstado;

    public ReporteDTO() {}

    public double getVentasTotales() { return ventasTotales; }
    public void setVentasTotales(double ventasTotales) { this.ventasTotales = ventasTotales; }

    public double getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(double ticketPromedio) { this.ticketPromedio = ticketPromedio; }

    public int getPedidosActivos() { return pedidosActivos; }
    public void setPedidosActivos(int pedidosActivos) { this.pedidosActivos = pedidosActivos; }

    public int getTotalPedidosEntregados() { return totalPedidosEntregados; }
    public void setTotalPedidosEntregados(int totalPedidosEntregados) { this.totalPedidosEntregados = totalPedidosEntregados; }

    public String getHoraPico() { return horaPico; }
    public void setHoraPico(String horaPico) { this.horaPico = horaPico; }

    public Map<String, Integer> getTopPlatillos() { return topPlatillos; }
    public void setTopPlatillos(Map<String, Integer> topPlatillos) { this.topPlatillos = topPlatillos; }

    public Map<String, Integer> getPedidosPorEstado() { return pedidosPorEstado; }
    public void setPedidosPorEstado(Map<String, Integer> pedidosPorEstado) { this.pedidosPorEstado = pedidosPorEstado; }
}
