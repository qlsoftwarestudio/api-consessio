package com.concessio.crm.excel.dto;

public class ExcelLeadRow {
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String dni;
    private String modeloInteres;
    private String tipoVehiculo;
    private String origen;
    private String notas;
    private String vendedoraAsignada;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getModeloInteres() { return modeloInteres; }
    public void setModeloInteres(String modeloInteres) { this.modeloInteres = modeloInteres; }

    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public String getVendedoraAsignada() { return vendedoraAsignada; }
    public void setVendedoraAsignada(String vendedoraAsignada) { this.vendedoraAsignada = vendedoraAsignada; }

    public boolean isValid() {
        return nombre != null && !nombre.trim().isEmpty() &&
               apellido != null && !apellido.trim().isEmpty() &&
               telefono != null && !telefono.trim().isEmpty();
    }
}
