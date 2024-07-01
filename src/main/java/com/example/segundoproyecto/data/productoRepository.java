package com.example.segundoproyecto.data;
import com.example.segundoproyecto.presentation.Models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface productoRepository extends JpaRepository<Producto,String> {

}
