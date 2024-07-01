package com.example.segundoproyecto.presentation.Models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Collection;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Proveedor {
    @Id
    @Column(name = "cedula")
    private String cedula;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "correo")
    private String correo;

    @Column(name = "contrasena")
    private String contrasena;

    @OneToMany(mappedBy = "proveedorByProveedor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore // Ignorar esta relación en la serialización
    @JsonManagedReference("proveedor-facturas")
    private Collection<Factura> facturasByCedula;

    @JsonIgnore // Ignorar esta relación en la serialización
    @OneToMany(mappedBy = "proveedorByProveedor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("proveedor-productos")
    private Collection<Producto> productosByCedula;

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Proveedor proveedor = (Proveedor) o;

        if (cedula != null ? !cedula.equals(proveedor.cedula) : proveedor.cedula != null) return false;
        if (nombre != null ? !nombre.equals(proveedor.nombre) : proveedor.nombre != null) return false;
        if (correo != null ? !correo.equals(proveedor.correo) : proveedor.correo != null) return false;
        if (contrasena != null ? !contrasena.equals(proveedor.contrasena) : proveedor.contrasena != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cedula != null ? cedula.hashCode() : 0;
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (correo != null ? correo.hashCode() : 0);
        result = 31 * result + (contrasena != null ? contrasena.hashCode() : 0);
        return result;
    }

    public Collection<Factura> getFacturasByCedula() {
        return facturasByCedula;
    }

    public void setFacturasByCedula(Collection<Factura> facturasByCedula) {
        this.facturasByCedula = facturasByCedula;
    }

    public Collection<Producto> getProductosByCedula() {
        return productosByCedula;
    }

    public void setProductosByCedula(Collection<Producto> productosByCedula) {
        this.productosByCedula = productosByCedula;
    }
}