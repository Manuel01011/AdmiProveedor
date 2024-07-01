document.addEventListener('DOMContentLoaded', function() {
    // URL correcta para obtener proveedores
    const url = 'http://localhost:8080/api/SegundoProyecto/proveedores';
    const contenedor = document.querySelector('tbody');
    let resultados = '';




    const formProveedor = document.querySelector('form');
    const cedula = document.getElementById('cedula');
    const nombre = document.getElementById('nombre');
    const correo = document.getElementById('correo');
    const contrasena = document.getElementById('contrasena');
    let opcion = '';

    document.getElementById('btnCrear').addEventListener('click', () => {
        cedula.value = '';
        nombre.value = '';
        correo.value = '';
        contrasena.value = '';
        document.getElementById('modalProveedor').style.display = 'block';
        opcion = 'crear';
    });

    document.querySelector('#modalProveedor button[type="button"]').addEventListener('click', () => {
        document.getElementById('modalProveedor').style.display = 'none';
    });

    // Función para mostrar los proveedores en la tabla
    const mostrar = (proveedores) => {
        resultados = ''; // Reiniciar la variable resultados
        proveedores.forEach(proveedor => {
            if (proveedor.cedula && proveedor.nombre && proveedor.correo && proveedor.contrasena) {
                resultados += `
                    <tr>
                        <td>${proveedor.cedula}</td>
                        <td>${proveedor.nombre}</td>
                        <td>${proveedor.correo}</td>
                        <td>${proveedor.contrasena}</td>
                    </tr>`;
            }
        });
        contenedor.innerHTML = resultados;
    };

    // Obtener y mostrar los proveedores al cargar la página
    fetch(url)
        .then(response => response.json())
        .then(data => {
            console.log(data); // Verifica la estructura de los datos recibidos
            mostrar(data);
        })
        .catch(error => console.log(error));

    // Función para manejar eventos delegados
    const on = (element, event, selector, handler) => {
        element.addEventListener(event, e => {
            if (e.target.closest(selector)) {
                handler(e);
            }
        });
    };

    // Manejar el envío del formulario
    formProveedor.addEventListener('submit', (e) => {
        e.preventDefault();
        const data = {
            cedula: cedula.value,
            nombre: nombre.value,
            correo: correo.value,
            contrasena: contrasena.value
        };
        if (opcion === 'crear') {
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(nuevoProveedor => {
                    mostrar([nuevoProveedor]); // Mostrar el nuevo proveedor
                    location.reload(); // Recargar la página para actualizar la lista
                })
                .catch(error => console.log(error));
        } else if (opcion === 'editar') {
            fetch(url + '/' + idForm, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(() => location.reload())
                .catch(error => console.log(error));
        }
        document.getElementById('modalProveedor').style.display = 'none';
    });
});

//admi
document.addEventListener('DOMContentLoaded', function() {
    const url = 'http://localhost:8080/api/SegundoProyecto/administracion';
    const contenedor = document.querySelector('tbody');
    let resultados = '';

    const mostrar = (proveedores) => {
        resultados = '';
        proveedores.forEach(proveedor => {
            if (proveedor.cedula && proveedor.nombre && proveedor.correo && proveedor.contrasena) {
                resultados += `
                    <tr>
                        <td>${proveedor.cedula}</td>
                        <td>${proveedor.nombre}</td>
                        <td>${proveedor.correo}</td>
                        <td>${proveedor.contrasena}</td>
                        <td class="text-center">
                            <button class="btnAceptar" data-cedula="${proveedor.cedula}">Aceptar</button>
                        </td>
                    </tr>`;
            }
        });
        contenedor.innerHTML = resultados;
    };

    fetch(url)
        .then(response => response.json())
        .then(data => {
            console.log('Datos recibidos:', data);
            mostrar(data);
        })
        .catch(error => console.error('Error al obtener los datos:', error));

    contenedor.addEventListener('click', (e) => {
        if (e.target.classList.contains('btnAceptar')) {
            const cedula = e.target.getAttribute('data-cedula');
            fetch(url + '/aceptar/' + cedula, {
                method: 'POST'
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(errorData => { throw new Error(errorData.error); });
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Proveedor aceptado:', data);
                    // Eliminar la fila correspondiente al proveedor aceptado de la tabla
                    const filaAceptada = e.target.closest('tr');
                    filaAceptada.remove();
                })
                .catch(error => console.error('Error al aceptar el proveedor:', error.message));
        }
    });
});


document.addEventListener('DOMContentLoaded', function() {
    fetchPerfilProveedor();
    function fetchPerfilProveedor() {
        const perfilProveedorUrl = 'http://localhost:8080/api/SegundoProyecto/perfilProveedor';

        fetch(perfilProveedorUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error en la solicitud');
                }
                return response.json();
            })
            .then(data => {
                console.log('Datos del perfil del proveedor:', data);
                if (data) {
                    document.getElementById('nombreProveedor').innerText = data.nombreProveedor;
                    mostrarClientes(data.clientes || []);
                    mostrarProductos(data.productos || []);
                    mostrarFacturas(data.facturas || []);
                }
            })
            .catch(error => {
                console.error('Error:', error.message);
            });
    }

    const mostrarClientes = (clientes) => {
        const tbodyClientes = document.getElementById('tablaClientes').querySelector('tbody');
        tbodyClientes.innerHTML = '';
        clientes.forEach(cliente => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${cliente.identificacion}</td>
                <td>${cliente.nombre}</td>
            `;
            tbodyClientes.appendChild(row);
        });
    };

    const mostrarProductos = (productos) => {
        const tbodyProductos = document.getElementById('tablaProductos').querySelector('tbody');
        tbodyProductos.innerHTML = '';
        productos.forEach(producto => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${producto.codigo}</td>
                <td>${producto.nombre}</td>
                <td>${producto.precio}</td>
            `;
            tbodyProductos.appendChild(row);
        });
    };

    const mostrarFacturas = (facturas) => {
        const tbodyFacturas = document.getElementById('tablaFacturas').querySelector('tbody');
        tbodyFacturas.innerHTML = '';
        facturas.forEach(factura => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${factura.codigo}</td>
                <td>${factura.fecha}</td>
                <td>${factura.precio}</td>
                <td><button onclick="descargarPDF('${factura.codigo}')">Descargar PDF</button></td>
                <td><button onclick="descargarXML('${factura.codigo}')">Descargar XML</button></td>
            `;
            tbodyFacturas.appendChild(row);
        });
    };

    window.descargarPDF = function(facturaCodigo) {
        const url = `http://localhost:8080/api/SegundoProyecto/generar_pdf?facturaCodigo=${facturaCodigo}`;
        fetch(url, { method: 'POST' })
            .then(response => response.blob())
            .then(blob => {
                const link = document.createElement('a');
                link.href = URL.createObjectURL(blob);
                link.download = 'factura.pdf';
                link.click();
            })
            .catch(error => console.error('Error al descargar PDF:', error));
    };

    window.descargarXML = function(facturaCodigo) {
        const url = `http://localhost:8080/api/SegundoProyecto/generar_xml?facturaCodigo=${facturaCodigo}`;
        fetch(url, { method: 'POST' })
            .then(response => response.blob())
            .then(blob => {
                const link = document.createElement('a');
                link.href = URL.createObjectURL(blob);
                link.download = 'factura.xml';
                link.click();
            })
            .catch(error => console.error('Error al descargar XML:', error));
    };

    document.getElementById("loginForm").addEventListener("submit", function(event) {
        const url = 'http://localhost:8080/api/SegundoProyecto/login';
        event.preventDefault();

        const cedula = document.getElementById("cedula").value;
        const contrasena = document.getElementById("contrasena").value;

        const proveedor = { cedula: cedula, contrasena: contrasena };

        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(proveedor),
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Error en la solicitud');
                }
            })
            .then(data => {
                if (data.success) {
                    if (cedula === "admin" && contrasena === "admin") {
                        window.location.href = '/administracion';
                    } else {
                        window.location.href = '/perfilProveedor';
                    }
                } else {
                    document.getElementById("errorMessage").innerText = data.message;
                }
            })
            .catch(error => {
                console.error('Error:', error.message);
            });
    });
});

const nombre = document.getElementById('nombre');
const correo = document.getElementById('correo');
const contrasena = document.getElementById('contrasena');

// Establecer los valores iniciales de los campos de entrada como cadena vacía
nombre.value = '';
correo.value = '';
contrasena.value = '';
//actualizar proveedor
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('editarPerfilForm');

    // Fetch the current profile data and populate the form
    fetch('/api/SegundoProyecto/perfilProveedor', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        credentials: 'include'
    })
        .then(response => response.json())
        .then(data => {
            if (data) {
                document.getElementById('nombre');//.value = data.nombreProveedor;
                document.getElementById('correo');//.value = data.correo;
                document.getElementById('contrasena');//.value = data.contrasena;
            }
        })
        .catch(error => console.error('Error:', error));

    // Handle form submission
    form.addEventListener('submit', function(event) {
        event.preventDefault();

        const updatedProveedor = {
            nombre: form.nombre.value,
            correo: form.correo.value,
            contrasena: form.contrasena.value
        };

        fetch('/api/SegundoProyecto/proveedor', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedProveedor),
            credentials: 'include'
        })
            .then(response => response.json())
            .then(data => {
                alert('Perfil actualizado exitosamente');
                window.location.href = '/perfilProveedor';
            })
            .catch(error => console.error('Error:', error));
    });
});


//agregar a cliente
document.addEventListener('DOMContentLoaded', function() {
    const nombre = document.getElementById('nombre');
    const correo = document.getElementById('correo');
    const contrasena = document.getElementById('contrasena');

    // Establecer los valores iniciales de los campos de entrada como cadena vacía
    nombre.value = '';
    correo.value = '';
    contrasena.value = '';

    const form = document.getElementById('registrarClienteForm');
    form.addEventListener('submit', function(event) {
        event.preventDefault();

        const data = {
            identificacion: form.identificacion.value,
            nombre: form.nombre.value
        };

        fetch('/api/SegundoProyecto/registrarCliente', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/perfilProveedor';
                } else {
                    console.error('Error al registrar el cliente');
                }
            })
            .catch(error => console.error('Error:', error));
    });
});



//regsitrar producto
document.addEventListener('DOMContentLoaded', function() {
    const registrarProductoForm = document.getElementById('registrarProductoForm');

    registrarProductoForm.addEventListener('submit', function(event) {
        event.preventDefault();
        console.log('Formulario de registro de productos enviado'); // Agregamos este console.log

        const data = {
            codigo: registrarProductoForm.codigo.value,
            nombre: registrarProductoForm.nombre.value,
            precio: registrarProductoForm.precio.value
        };

        fetch('/api/SegundoProyecto/registrarProductos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data),
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/perfilProveedor';
                } else {
                    console.error('Error al registrar el producto');
                }
            })
            .catch(error => console.error('Error:', error));
    });
});


//realizar factura
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registrarFacturaForm');

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        const clienteIdentificacion = form.cliente.value;
        const productoCodigo = form.producto.value;
        const fecha = form.fecha.value;
        const precio = form.precio.value; // Obtener el valor del precio del formulario
        const codigo = form.codigo.value; // Obtener el valor del código del formulario

        fetch('/api/SegundoProyecto/registrarFactura', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                clienteIdentificacion: clienteIdentificacion,
                productoCodigo: productoCodigo,
                fecha: fecha,
                precio: precio,
                codigo: codigo // Incluir el valor del código en el objeto JSON
            }),
            credentials: 'include'
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Error al registrar la factura');
                }
            })
            .then(data => {
                alert(data); // Muestra un mensaje de alerta con la respuesta del servidor
                window.location.href = '/perfilProveedor'; // Redirige a la página de perfil del proveedor
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });
});

//aqui
document.addEventListener('DOMContentLoaded', function() {
    administracion();
    function administracion() {
        const administracionUrl = '/api/SegundoProyecto/administracion';

        fetch(administracionUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error en la solicitud');
                }
                return response.json();
            })
            .then(data => {
                console.log('Datos de los proveedores:', data);
                if (data) {
                    mostrarHabilitados(data.habilitados || []);
                    mostrarDeshabilitados(data.deshabilitados || []);
                }
            })
            .catch(error => {
                console.error('Error:', error.message);
            });
    }
    const mostrarHabilitados = (habilitados) => {
        const tbodyHabilitados = document.getElementById('habilitados').querySelector('tbody');
        tbodyHabilitados.innerHTML = '';
        tbodyHabilitados.forEach(proveedor => {
            if (proveedor.cedula && proveedor.nombre && proveedor.correo && proveedor.contrasena) {
                const row = document.createElement('tr');
                row.innerHTML = `
                        <td>${proveedor.cedula}</td>
                        <td>${proveedor.nombre}</td>
                        <td>${proveedor.correo}</td>
                        <td>${proveedor.contrasena}</td>
                        <td class="text-center">
                            <button class="btnEditar">Habilitar</button>
                        </td>`;
                tbodyHabilitados.appendChild(row);
            }
        });
    };

    const mostrarDeshabilitados = (deshabilitados) => {
        const tbodyDeshabilitados = document.getElementById('deshabilitados').querySelector('tbody');
        tbodyDeshabilitados.innerHTML = '';
        deshabilitados.forEach(proveedor => {
            if (proveedor.cedula && proveedor.nombre && proveedor.correo && proveedor.contrasena) {
                const row = document.createElement('tr');
                row.innerHTML = `
                        <td>${proveedor.cedula}</td>
                        <td>${proveedor.nombre}</td>
                        <td>${proveedor.correo}</td>
                        <td>${proveedor.contrasena}</td>
                        <td class="text-center">
                            <button class="btnEditar">Habilitar</button>
                        </td>`;
                tbodyDeshabilitados.appendChild(row);
            }
        });
    };
    //login

    document.getElementById("loginForm").addEventListener("submit", function(event) {
        const url = 'http://localhost:8080/api/SegundoProyecto/login';
        event.preventDefault(); // Evitar que se envíe el formulario de forma predeterminada

        const cedula = document.getElementById("cedula").value;
        const contrasena = document.getElementById("contrasena").value;

        const proveedor = {
            cedula: cedula,
            contrasena: contrasena
        };

        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(proveedor),
            credentials: 'include' // Asegura que las cookies se envíen
        })
            .then(response => {
                if (response.ok) {
                    return response.json(); // Devuelve la respuesta en formato JSON
                } else {
                    throw new Error('Error en la solicitud');
                }
            })
            .then(data => {
                if (data.success) {
                    if (cedula === "admin" && contrasena === "admin") {
                        window.location.href = '/administracion';
                    } else {
                        // Redirigir al perfil del proveedor normal
                        window.location.href = '/perfilProveedor';
                    }
                } else {
                    document.getElementById("errorMessage").innerText = data.message;
                }
            })
            .catch(error => {
                console.error('Error:', error.message);
            });
    });
});