/*
Salcedo Alvarez Hugo Emmanuel
Morales Escobar Juan Adrian
 */
package Nodos;

/**
 * Nodo del árbol AVL. Guarda un Empleado, las referencias a sus hijos
 * izquierdo (ids menores) y derecho (ids mayores) y su altura.
 *
 * La altura se mide en NODOS: un nodo sin hijos (hoja) tiene altura 1 y la
 * referencia nula tiene altura 0. Con ella se calcula el factor de
 * equilibrio FE = altura(derecho) - altura(izquierdo).
 *
 * @author ASUS
 */
public class NodoAVL {

    private Empleado dato;
    private NodoAVL izquierdo;
    private NodoAVL derecho;
    private Integer altura;

    public NodoAVL(Empleado dato) {
        this.dato = dato;
        this.izquierdo = null;
        this.derecho = null;
        this.altura = 1; // un nodo recién creado es hoja
    }

    public Empleado getDato() {
        return dato;
    }

    public void setDato(Empleado dato) {
        this.dato = dato;
    }

    public NodoAVL getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoAVL izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoAVL getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoAVL derecho) {
        this.derecho = derecho;
    }

    public Integer getAltura() {
        return altura;
    }

    public void setAltura(Integer altura) {
        this.altura = altura;
    }

    /** @return true si el nodo no tiene hijos. */
    public boolean esHoja() {
        return izquierdo == null && derecho == null;
    }
}
