package com.example.segundoproyecto.data;
import com.example.segundoproyecto.presentation.Models.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface facturaRepository extends JpaRepository<Factura,String> {
}
