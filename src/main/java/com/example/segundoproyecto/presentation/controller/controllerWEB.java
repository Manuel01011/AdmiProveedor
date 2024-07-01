package com.example.segundoproyecto.presentation.controller;
import com.example.segundoproyecto.logic.ProveedorImplementation;
import com.example.segundoproyecto.presentation.Models.Cliente;
import com.example.segundoproyecto.presentation.Models.Factura;
import com.example.segundoproyecto.presentation.Models.Producto;
import com.example.segundoproyecto.presentation.Models.Proveedor;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
@Controller
public class controllerWEB {

    private final ProveedorImplementation proveedorImplementation;

    @Autowired
    public controllerWEB(ProveedorImplementation proveedorImplementation) {
        this.proveedorImplementation = proveedorImplementation;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/proveedores")
    public String getForm(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedores";
    }

    @GetMapping("/administracion")
    public String getFormNo(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "administracion";
    }

    @PostMapping("/administracion/aceptar/{cedula}")
    public String aceptar(@PathVariable String cedula) throws Exception {
        proveedorImplementation.aceptar(cedula);
        return "redirect:/proveedores";
    }

    @PostMapping("/proveedores")
    public String saveProveedor(Proveedor proveedor) throws Exception {
        proveedorImplementation.guardarProveedor(proveedor);
        return "redirect:/proveedores";
    }

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        Proveedor proveedor = new Proveedor();
        model.addAttribute("proveedor", proveedor);
        return "login";
    }

    @PostMapping("/login")
    public String loginValidation(@ModelAttribute("proveedor") Proveedor proveedor, Model model, HttpSession session) throws Exception {
        if (proveedor.getCedula().equals("admin") && proveedor.getContrasena().equals("admin")) {
            return "redirect:/administracion";
        }
        Proveedor existingProveedor = proveedorImplementation.userById(proveedor.getCedula());

        if (existingProveedor == null || !existingProveedor.getContrasena().equals(proveedor.getContrasena())) {
            model.addAttribute("error", "Credenciales incorrectas, por favor intente de nuevo.");
            return "login";
        }
        session.setAttribute("loggedInProveedor", existingProveedor);
        return "redirect:/perfilProveedor";
    }

    @GetMapping("/perfilProveedor")
    public String perfilDelProveedor(Model model, HttpSession session) {
        Proveedor loggedInProveedor = (Proveedor) session.getAttribute("loggedInProveedor");
        if (loggedInProveedor == null) {
            System.out.println("Controller: loggedInProveedor es null");
            return "redirect:/login";
        }
        System.out.println("Controller: " + loggedInProveedor.getNombre() + " " + loggedInProveedor.getCedula());

        List<Cliente> clientes = proveedorImplementation.listaClientes(loggedInProveedor.getCedula());
        List<Producto> productos = proveedorImplementation.listaProductos(loggedInProveedor.getCedula());
        List<Factura> facturas = proveedorImplementation.listaFactura(loggedInProveedor.getCedula());

        model.addAttribute("nombreProveedor", loggedInProveedor.getNombre());
        model.addAttribute("clientes", clientes);
        model.addAttribute("productos", productos);
        model.addAttribute("facturas", facturas);

        return "perfilProveedor";
    }


    @GetMapping("/editarPerfil")
    public String mostrarFormularioEditar(Model model, HttpSession session) {
        Proveedor loggedInProveedor = (Proveedor) session.getAttribute("loggedInProveedor");
        model.addAttribute("proveedor", loggedInProveedor);
        return "editarPerfil";
    }

    @PostMapping("/editarPerfil")
    public String editProveedorSubmit(@ModelAttribute("proveedor") Proveedor editedProveedor, Model model) throws Exception {
        proveedorImplementation.actualizarProveedor(editedProveedor);
        return "redirect:/perfilProveedor";
    }

    @GetMapping("/registrarCliente")
    public String formCliente(Model model) {
        Cliente cliente = new Cliente();
        model.addAttribute("cliente", cliente);
        return "registrarCliente";
    }

    @PostMapping("/registrarCliente")
    public String saveCliente(@ModelAttribute("cliente") Cliente cliente, HttpSession session, Model model) {
        Proveedor proveedor = (Proveedor) session.getAttribute("loggedInProveedor");
        cliente.setProveedor(proveedor.getCedula());
        proveedorImplementation.saveCliente(cliente);
        return "redirect:perfilProveedor";
    }

    @GetMapping("/registrarProductos")
    public String formProductos(Model model) {
        Producto producto = new Producto();
        model.addAttribute("producto", producto);
        return "registrarProductos";
    }

    @PostMapping("/registrarProductos")
    public String saveProducto(@ModelAttribute("producto") Producto producto, HttpSession session, Model model) {
        Proveedor proveedor = (Proveedor) session.getAttribute("loggedInProveedor");
        producto.setProveedorByProveedor(proveedor);
        proveedorImplementation.saveProducto(producto);
        return "redirect:perfilProveedor";
    }

    @GetMapping("/registrarFactura")
    public String formFacturas(Model model, HttpSession session) {
        Factura factura = new Factura();
        Proveedor loggedInProveedor = (Proveedor) session.getAttribute("loggedInProveedor");
        model.addAttribute("proveedor", loggedInProveedor);
        model.addAttribute("clientes", proveedorImplementation.listaClientes(loggedInProveedor.getCedula()));
        model.addAttribute("productos", proveedorImplementation.listaProductos(loggedInProveedor.getCedula()));
        model.addAttribute("factura", factura);
        return "registrarFactura";
    }

    @PostMapping("/registrarFactura")
    public String saveFactura(@ModelAttribute("factura") Factura factura,
                              @RequestParam("cliente") String clienteIdentificacion,
                              @RequestParam("producto") String productoCodigo,
                              HttpSession session) {
        Proveedor proveedor = (Proveedor) session.getAttribute("loggedInProveedor");
        Cliente cliente = proveedorImplementation.buscarCliente(clienteIdentificacion);
        Producto producto = proveedorImplementation.buscarProducto(productoCodigo);

        factura.setProveedorByProveedor(proveedor);
        factura.setClienteByCliente(cliente);
        factura.setProductoByProducto(producto);

        proveedorImplementation.saveFacturas(factura);

        return "redirect:perfilProveedor";
    }


    @PostMapping("/generar_pdf")
    public ResponseEntity<byte[]> generarPDF(@RequestParam("facturaCodigo") String facturaCodigo) {
        // Buscar la factura en función de su código
        Factura factura = proveedorImplementation.buscarFactura(facturaCodigo);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDocument);

            // Agrega el contenido de la factura al PDF
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

        // Construir el ResponseEntity con el arreglo de bytes y los encabezados adecuados
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "factura.pdf");

        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    @PostMapping("/generar_xml")
    public ResponseEntity<byte[]> generarXML(@RequestParam("facturaCodigo") String facturaCodigo) {
        // Buscar la factura en función de su código
        Factura factura = proveedorImplementation.buscarFactura(facturaCodigo);

        // Crear el contenido XML de la factura
        String xmlContent = "<factura>\n" +
                "    <codigo>" + factura.getCodigo() + "</codigo>\n" +
                "    <fecha>" + factura.getFecha() + "</fecha>\n" +
                "    <precio>" + factura.getPrecio() + "</precio>\n" +
                "    <cliente>" + factura.getClienteByCliente().getNombre() + "</cliente>\n" +
                "    <producto>" + factura.getProductoByProducto().getNombre() + "</producto>\n" +
                "</factura>";

        // Convertir el contenido XML en un arreglo de bytes
        byte[] xmlBytes = xmlContent.getBytes(StandardCharsets.UTF_8);

        // Construir el ResponseEntity con el arreglo de bytes y los encabezados adecuados
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setContentDispositionFormData("filename", "factura.xml");

        return new ResponseEntity<>(xmlBytes, headers, HttpStatus.OK);
    }

}