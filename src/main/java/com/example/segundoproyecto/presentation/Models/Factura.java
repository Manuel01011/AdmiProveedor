package com.example.segundoproyecto.presentation.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Factura {
    @Id
    @Column(name = "codigo")
    private String codigo;

    @Column(name = "fecha")
    private String fecha;

    @Column(name = "precio")
    private String precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor", referencedColumnName = "cedula")
    @JsonBackReference("proveedor-facturas")
    private Proveedor proveedorByProveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente", referencedColumnName = "identificacion")
    @JsonBackReference("cliente-facturas")
    private Cliente clienteByCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto", referencedColumnName = "codigo")
    @JsonBackReference("producto-facturas")
    private Producto productoByProducto;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Factura factura = (Factura) o;

        if (codigo != null ? !codigo.equals(factura.codigo) : factura.codigo != null) return false;
        if (fecha != null ? !fecha.equals(factura.fecha) : factura.fecha != null) return false;
        if (precio != null ? !precio.equals(factura.precio) : factura.precio != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = codigo != null ? codigo.hashCode() : 0;
        result = 31 * result + (fecha != null ? fecha.hashCode() : 0);
        result = 31 * result + (precio != null ? precio.hashCode() : 0);
        return result;
    }

    public Cliente getClienteByCliente() {
        return clienteByCliente;
    }

    public void setClienteByCliente(Cliente clienteByCliente) {
        this.clienteByCliente = clienteByCliente;
    }

    public Producto getProductoByProducto() {
        return productoByProducto;
    }

    public void setProductoByProducto(Producto productoByProducto) {
        this.productoByProducto = productoByProducto;
    }

    public Proveedor getProveedorByProveedor() {
        return proveedorByProveedor;
    }

    public void setProveedorByProveedor(Proveedor proveedorByProveedor) {
        this.proveedorByProveedor = proveedorByProveedor;
    }
}
