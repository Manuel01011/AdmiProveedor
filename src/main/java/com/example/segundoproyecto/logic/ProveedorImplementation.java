package com.example.segundoproyecto.logic;
import com.example.segundoproyecto.data.ClienteRepository;
import com.example.segundoproyecto.data.ProveedorRepository;
import com.example.segundoproyecto.data.facturaRepository;
import com.example.segundoproyecto.data.productoRepository;
import com.example.segundoproyecto.presentation.Models.Cliente;
import com.example.segundoproyecto.presentation.Models.Factura;
import com.example.segundoproyecto.presentation.Models.Producto;
import com.example.segundoproyecto.presentation.Models.Proveedor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProveedorImplementation{
    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private com.example.segundoproyecto.data.productoRepository productoRepository;
    @Autowired
    private com.example.segundoproyecto.data.facturaRepository facturaRepository;

    private final List<Proveedor> listaDesabilitados = new ArrayList<>();

    public ProveedorImplementation() {
    }

    public List<Proveedor> listaProveedores() {
        return proveedorRepository.findAll();
    }

    public List<Proveedor> getListaDesabilitados() {
        return listaDesabilitados;
    }


    public List<Cliente> listaClientes(String id) {
        List<Cliente> lisClientes = clienteRepository.findAll();
        return lisClientes.stream()
                .filter(cliente -> cliente.getProveedor().equals(id))
                .collect(Collectors.toList());
    }

    public List<Producto> listaProductos(String id) {
        List<Producto> lisProductos = productoRepository.findAll();
        return lisProductos.stream()
                .filter(producto -> producto.getProveedorByProveedor().getCedula().equals(id))
                .collect(Collectors.toList());
    }

    public List<Factura> listaFactura(String id) {
        List<Factura> lisFactura = facturaRepository.findAll();
        return lisFactura.stream()
                .filter(producto -> producto.getProveedorByProveedor().getCedula().equals(id))
                .collect(Collectors.toList());
    }

    public Proveedor guardarProveedor(Proveedor proveedor) throws Exception {
        listaDesabilitados.add(proveedor);
        return proveedor;
    }

    public Proveedor guardarProveedorAceptado(Proveedor proveedor) throws Exception {
        proveedorRepository.save(proveedor);
        return proveedor;
    }

    public boolean proveedorById(String cedula) {
        Optional<Proveedor> optionalProveedor = proveedorRepository.findById(cedula);
        return optionalProveedor.isEmpty();
    }


    public Proveedor userById(String cedula) {
        List<Proveedor> proveedores = proveedorRepository.findAll();
        return proveedores.stream()
                .filter(h->h.getCedula().equals(cedula)).findFirst().orElseThrow(null);
    }

    public Proveedor actualizarProveedor(Proveedor proveedor) {
        Proveedor existingProveedor = userById(proveedor.getCedula());
        if (existingProveedor != null) {
            existingProveedor.setNombre(proveedor.getNombre());
            existingProveedor.setCorreo(proveedor.getCorreo());
            existingProveedor.setContrasena(proveedor.getContrasena());
            // Actualiza otros campos según sea necesario
            return proveedorRepository.save(existingProveedor); // Método que guarda el proveedor actualizado en la base de datos
        }
        return null;
    }

    public void saveCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    public void saveProducto(Producto producto) {
        productoRepository.save(producto);
    }

    public void saveFacturas(Factura factura) {
        facturaRepository.save(factura);
    }

    public Cliente buscarCliente(String id) {
        Optional<Cliente> optionalCliente = clienteRepository.findById(id);
        Cliente cliente = optionalCliente.orElseThrow(() -> new RuntimeException("Not Found"));
        return cliente;
    }

    public Producto buscarProducto(String id) {
        Optional<Producto> optionalProducto = productoRepository.findById(id);
        Producto producto = optionalProducto.orElseThrow(() -> new RuntimeException("Not Found"));
        return producto;
    }

    public Factura buscarFactura(String id) {
        Optional<Factura> optionalFactura = facturaRepository.findById(id);
        Factura factura = optionalFactura.orElseThrow(() -> new RuntimeException("Not Found"));
        return factura;
    }

    public void aceptar(String cedula) {
        Optional<Proveedor> proveedor = listaDesabilitados.stream().filter(h->h.getCedula().equals(cedula)).findFirst();
        Proveedor proveedor1 = proveedor.orElseThrow(() -> new RuntimeException("Not Found"));
        proveedorRepository.save(proveedor1);
        listaDesabilitados.removeIf(h -> h.getCedula().equals(cedula));
    }
    public List<Proveedor> listaNohabilitados() {
        return listaDesabilitados;
    }


}
