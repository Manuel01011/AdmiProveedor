package com.example.segundoproyecto.data;
import com.example.segundoproyecto.presentation.Models.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor,String> {
}
