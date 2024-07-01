package com.example.segundoproyecto.presentation.Models;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Collection;

@Entity
public class Cliente {
    @Id
    @Column(name = "identificacion")
    private String identificacion;

    @Basic
    @Column(name = "nombre")
    private String nombre;

    @Basic
    @Column(name = "proveedor")
    private String proveedor;

    @OneToMany(mappedBy = "clienteByCliente")
    @JsonManagedReference("cliente-facturas")
    private Collection<Factura> facturasByIdentificacion;

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cliente cliente = (Cliente) o;

        if (identificacion != null ? !identificacion.equals(cliente.identificacion) : cliente.identificacion != null)
            return false;
        if (nombre != null ? !nombre.equals(cliente.nombre) : cliente.nombre != null) return false;
        if (proveedor != null ? !proveedor.equals(cliente.proveedor) : cliente.proveedor != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = identificacion != null ? identificacion.hashCode() : 0;
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (proveedor != null ? proveedor.hashCode() : 0);
        return result;
    }

    public Collection<Factura> getFacturasByIdentificacion() {
        return facturasByIdentificacion;
    }

    public void setFacturasByIdentificacion(Collection<Factura> facturasByIdentificacion) {
        this.facturasByIdentificacion = facturasByIdentificacion;
    }
}
