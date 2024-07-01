CREATE DATABASE facturaElectronica;
USE facturaElectronica;

CREATE TABLE Proveedor(
                          cedula VARCHAR(20) PRIMARY KEY,
                          nombre VARCHAR(30) NOT NULL,
                          correo VARCHAR(50) NOT NULL,
                          contrasena VARCHAR(20) NOT NULL
);
CREATE TABLE Cliente (
                         identificacion VARCHAR(20) PRIMARY KEY,
                         nombre VARCHAR(30) NOT NULL,
                         proveedor VARCHAR(20) NOT NULL,
                         FOREIGN KEY (proveedor) REFERENCES Proveedor(cedula)
);
CREATE TABLE Producto(
                         codigo VARCHAR(20) PRIMARY KEY,
                         nombre VARCHAR(30) NOT NULL,
                         precio DECIMAL(10, 2) NOT NULL,
                         proveedor VARCHAR(20) NOT NULL,
                         FOREIGN KEY (proveedor) REFERENCES Proveedor(cedula)
);

CREATE TABLE Factura(
                        codigo VARCHAR(20) PRIMARY KEY,
                        fecha varchar(20) NOT NULL,
                        precio DECIMAL(10, 2) NOT NULL,
                        cliente VARCHAR(20) NOT NULL,
                        producto VARCHAR(20) NOT NULL,
                        proveedor VARCHAR(20) NOT NULL,
                        FOREIGN KEY (cliente) REFERENCES Cliente(identificacion),
                        FOREIGN KEY (producto) REFERENCES Producto(codigo),
                        FOREIGN KEY (proveedor) REFERENCES Proveedor(cedula)
);

-- Insertar proveedores
INSERT INTO Proveedor (cedula, nombre, correo, contrasena) VALUES ('2001', 'Acme Corp', 'info@acmecorp.com', 'contraseña1');
INSERT INTO Proveedor (cedula, nombre, correo, contrasena) VALUES ('2002', 'XYZ Corp', 'contact@xyzcorp.com', 'contraseña2');
INSERT INTO Proveedor (cedula, nombre, correo, contrasena) VALUES ('2003', 'ABC Inc', 'sales@abcinc.com', 'contraseña3');

-- Insertar clientes
INSERT INTO Cliente (identificacion, nombre, proveedor) VALUES ('1001', 'Juan Pérez', '2001');
INSERT INTO Cliente (identificacion, nombre, proveedor) VALUES ('1002', 'María Rodríguez', '2002');
INSERT INTO Cliente (identificacion, nombre, proveedor) VALUES ('1003', 'Roberto Gómez', '2003');