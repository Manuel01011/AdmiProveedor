package com.example.segundoproyecto.data;

import com.example.segundoproyecto.presentation.Models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente,String> {

}
