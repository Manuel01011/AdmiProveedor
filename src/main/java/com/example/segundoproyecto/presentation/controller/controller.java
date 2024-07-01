package com.example.segundoproyecto.presentation.controller;
import com.example.segundoproyecto.logic.ProveedorImplementation;
import com.example.segundoproyecto.presentation.Models.Cliente;
import com.example.segundoproyecto.presentation.Models.Factura;
import com.example.segundoproyecto.presentation.Models.Producto;
import com.example.segundoproyecto.presentation.Models.Proveedor;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/SegundoProyecto")
public class controller {

    @Autowired
    private final ProveedorImplementation proveedorImplementation;

    @Autowired
    public controller(ProveedorImplementation proveedorImplementation) {
        this.proveedorImplementation = proveedorImplementation;
    }

    @GetMapping("/proveedores")
    public List<Proveedor> getProveedores() {
        return proveedorImplementation.listaProveedores();
    }

    @PostMapping("/proveedores")
    public Proveedor createProveedor(@RequestBody Proveedor proveedor) throws Exception {
        return proveedorImplementation.guardarProveedor(proveedor);
    }

    @GetMapping("/administracion")
    public List<Proveedor> getProveedoresNO() {
        return proveedorImplementation.listaNohabilitados();
    }

    @PostMapping("/administracion/aceptar/{cedula}")
    public ResponseEntity<Map<String, String>> aceptarProveedor(@PathVariable String cedula) {
        Map<String, String> response = new HashMap<>();
        try {
            proveedorImplementation.aceptar(cedula);
            response.put("message", "Proveedor aceptado con éxito");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Proveedor proveedor, HttpSession session) {
        if (proveedor.getCedula().equals("admin") && proveedor.getContrasena().equals("admin")) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("nombreProveedor", proveedor.getCedula());
            return ResponseEntity.ok(response);
        }
        Proveedor checkLogin = proveedorImplementation.userById(proveedor.getCedula());
        if (checkLogin != null && checkLogin.getContrasena().equals(proveedor.getContrasena())) {
            session.setAttribute("loggedInProveedor", checkLogin);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("nombreProveedor", checkLogin.getNombre());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Credenciales inválidas. Inténtelo de nuevo.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/perfilProveedor")
    public ResponseEntity<Map<String, Object>> obtenerDatosPerfilProveedor(HttpSession session) {
        Proveedor loggedInProveedor = (Proveedor) session.getAttribute("loggedInProveedor");
        if (loggedInProveedor == null) {
            System.out.println("RestController: loggedInProveedor es null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        System.out.println("RestController: " + loggedInProveedor.getNombre() + " " + loggedInProveedor.getCedula());

        List<Cliente> clientes = proveedorImplementation.listaClientes(loggedInProveedor.getCedula());
        List<Producto> productos = proveedorImplementation.listaProductos(loggedInProveedor.getCedula());
        List<Factura> facturas = proveedorImplementation.listaFactura(loggedInProveedor.getCedula());

        Map<String, Object> response = new HashMap<>();
        response.put("nombreProveedor", loggedInProveedor.getNombre());
        response.put("clientes", clientes);
        response.put("productos", productos);
        response.put("facturas", facturas);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/proveedor")
    public ResponseEntity<Proveedor> actualizarProveedor(@RequestBody Proveedor proveedor, HttpSession session) {
        Proveedor loggedInProveedor = (Proveedor) session.getAttribute("loggedInProveedor");
        if (loggedInProveedor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        proveedor.setCedula(loggedInProveedor.getCedula());
        Proveedor updatedProveedor = proveedorImplementation.actualizarProveedor(proveedor);
        session.setAttribute("loggedInProveedor", updatedProveedor); // Actualiza la sesión
        return ResponseEntity.ok(updatedProveedor);
    }

    @PostMapping("/registrarCliente")
    public ResponseEntity<String> saveCliente(@RequestBody Cliente cliente, HttpSession session) {
        Proveedor proveedor = (Proveedor) session.getAttribute("loggedInProveedor");
        if (proveedor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Proveedor no autenticado");
        }

        cliente.setProveedor(proveedor.getCedula());
        try {
            proveedorImplementation.saveCliente(cliente);
            return ResponseEntity.ok("Cliente registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar el cliente: " + e.getMessage());
        }
    }

    @PostMapping("/registrarProductos")
    public ResponseEntity<String> saveProducto(@RequestBody Producto producto, HttpSession session) {
        Proveedor proveedor = (Proveedor) session.getAttribute("loggedInProveedor");
        if (proveedor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Proveedor no autenticado");
        }

        producto.setProveedorByProveedor(proveedor);
        try {
            proveedorImplementation.saveProducto(producto);
            return ResponseEntity.ok("Producto registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar el producto: " + e.getMessage());
        }
    }

    @PostMapping("/registrarFactura")
    public ResponseEntity<String> saveFactura(@ModelAttribute("factura") Factura factura,
                                              @RequestParam("cliente") String clienteIdentificacion,
                                              @RequestParam("producto") String productoCodigo,
                                              @RequestParam("fecha") String fecha,
                                              @RequestParam("precio") String precio,
                                              @RequestParam("codigo") String codigo,
                                              HttpSession session) {
        Proveedor proveedor = (Proveedor) session.getAttribute("loggedInProveedor");
        Cliente cliente = proveedorImplementation.buscarCliente(clienteIdentificacion);
        Producto producto = proveedorImplementation.buscarProducto(productoCodigo);

        if (proveedor == null || cliente == null || producto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Proveedor, cliente o producto no encontrados");
        }

        factura.setProveedorByProveedor(proveedor);
        factura.setClienteByCliente(cliente);
        factura.setProductoByProducto(producto);
        factura.setFecha(fecha); // Asignar el valor de fecha a la factura
        factura.setPrecio(precio); // Asignar el valor de precio a la factura
        factura.setCodigo(codigo);

        try {
            proveedorImplementation.saveFacturas(factura);
            return ResponseEntity.ok("Factura registrada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar la factura: " + e.getMessage());
        }
    }


    @PostMapping("/generar_pdf")
    public ResponseEntity<byte[]> generarPDF(@RequestParam("facturaCodigo") String facturaCodigo) {
        Factura factura = proveedorImplementation.buscarFactura(facturaCodigo);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDocument);

            document.add(new com.itextpdf.layout.element.Paragraph("Código: " + factura.getCodigo()));
            document.add(new com.itextpdf.layout.element.Paragraph("Fecha: " + factura.getFecha()));
            document.add(new com.itextpdf.layout.element.Paragraph("Precio: " + factura.getPrecio()));
            document.add(new com.itextpdf.layout.element.Paragraph("Cliente: " + factura.getClienteByCliente().getNombre()));
            document.add(new com.itextpdf.layout.element.Paragraph("Producto: " + factura.getProductoByProducto().getNombre()));
            document.add(new com.itextpdf.layout.element.Paragraph("Proveedor: " + factura.getProveedorByProveedor().getNombre()));
            document.add(new com.itextpdf.layout.element.Paragraph("Cedula de Proveedor: " + factura.getProveedorByProveedor().getCedula()));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "factura.pdf");

        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    @PostMapping("/generar_xml")
    public ResponseEntity<byte[]> generarXML(@RequestParam("facturaCodigo") String facturaCodigo) {
        Factura factura = proveedorImplementation.buscarFactura(facturaCodigo);

        String xmlContent = "<factura>\n" +
                "    <codigo>" + factura.getCodigo() + "</codigo>\n" +
                "    <fecha>" + factura.getFecha() + "</fecha>\n" +
                "    <precio>" + factura.getPrecio() + "</precio>\n" +
                "    <cliente>" + factura.getClienteByCliente().getNombre() + "</cliente>\n" +
                "    <producto>" + factura.getProductoByProducto().getNombre() + "</producto>\n" +
                "</factura>";

        byte[] xmlBytes = xmlContent.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setContentDispositionFormData("filename", "factura.xml");

        return new ResponseEntity<>(xmlBytes, headers, HttpStatus.OK);
    }
}
