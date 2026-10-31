/*
Salcedo Alvarez Hugo Emmanuel
Morales Escobar Juan Adrian
 */
package Nodos;

/**
 * Representa a un empleado de la empresa.
 *
 * El atributo {@code id} es la clave de ordenamiento del árbol AVL
 * (ArbolAVL) y debe ser único. IMPORTANTE: no se debe modificar el id
 * de un empleado que ya esté insertado en el árbol, porque se rompería el
 * orden del árbol; para cambiarlo hay que eliminar el empleado y volver a
 * insertarlo.
 *
 * Las validaciones de los datos (campos vacíos, salario negativo, etc.) las
 * realiza ArbolAVL al insertar y la clase Validaciones para la interfaz.
 *
 * @author ASUS
 */
public class Empleado {

    private Integer id;
    private String nombre;
    private String puesto;
    private String departamento;
    private Double salario;

    public Empleado() {
    }

    public Empleado(Integer id, String nombre, String puesto, String departamento, Double salario) {
        this.id = id;
        this.nombre = nombre;
        this.puesto = puesto;
        this.departamento = departamento;
        this.salario = salario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    @Override
    public String toString() {
        return "Empleado{id=" + id
                + ", nombre=" + nombre
                + ", puesto=" + puesto
                + ", departamento=" + departamento
                + ", salario=" + salario + "}";
    }
}
