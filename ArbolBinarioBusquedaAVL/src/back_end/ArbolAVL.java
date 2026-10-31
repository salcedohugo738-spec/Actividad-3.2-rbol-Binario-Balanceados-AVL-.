/*
Salcedo Alvarez Hugo Emmanuel
Morales Escobar Juan Adrian
 */
package back_end;

import Nodos.Empleado;
import Nodos.NodoAVL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Árbol binario de búsqueda AUTOBALANCEADO (AVL) de empleados ordenado por id.
 *
 * Convenciones:
 * - insertar lanza IllegalArgumentException si el empleado es inválido o si el id ya existe.
 * - buscar devuelve el Empleado o null si no existe (o si el árbol está vacío).
 * - eliminar devuelve el Empleado eliminado o null si no existía.
 * - Los recorridos devuelven una lista (vacía si el árbol está vacío).
 * - La altura de un nodo y la profundidad se miden en NIVELES/NODOS:
 *   árbol vacío = 0, un solo nodo = 1.
 * - Factor de equilibrio: FE = altura(derecho) - altura(izquierdo). En un AVL
 *   válido todos los nodos cumplen FE en {-1, 0, 1}.
 * - El rebalanceo es automático: se ejecuta dentro de insertar y eliminar,
 *   por lo que no existe un método público "balancear".
 *
 * Nomenclatura de rotaciones (según el lado hacia donde gira el nodo desbalanceado):
 * - Simple Izquierda: caso Derecha-Derecha (FE = +2, hijo derecho con FE >= 0).
 * - Simple Derecha: caso Izquierda-Izquierda (FE = -2, hijo izquierdo con FE <= 0).
 * - Doble Derecha-Izquierda: caso Derecha-Izquierda (FE = +2, hijo derecho con FE < 0).
 * - Doble Izquierda-Derecha: caso Izquierda-Derecha (FE = -2, hijo izquierdo con FE > 0).
 *
 * @author ASUS
 */
public class ArbolAVL {

    private NodoAVL raiz;
    private int tamanio;

    /** Descripción de las rotaciones hechas en la última inserción/eliminación. */
    private final List<String> ultimasRotaciones = new ArrayList<>();
    /** Ids de los nodos que fueron el pivote (nodo desbalanceado) de esas rotaciones. */
    private final List<Integer> ultimosIdsRotados = new ArrayList<>();

    public ArbolAVL() {
        this.raiz = null;
        this.tamanio = 0;
    }

    public NodoAVL getRaiz() {
        return raiz;
    }

    /** @return cantidad total de empleados en el árbol. */
    public int getTamanio() {
        return tamanio;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    /** @return rotaciones aplicadas por la última inserción o eliminación (vacía si no hubo). */
    public List<String> getUltimasRotaciones() {
        return new ArrayList<>(ultimasRotaciones);
    }

    /** @return ids de los nodos pivote de la última inserción o eliminación. */
    public List<Integer> getUltimosIdsRotados() {
        return new ArrayList<>(ultimosIdsRotados);
    }

    // ------------------------------------------------------------------
    // Altura y factor de equilibrio
    // ------------------------------------------------------------------
    /** Altura de un nodo en niveles: null = 0, hoja = 1. */
    public int obtenerAltura(NodoAVL nodo) {
        return nodo == null ? 0 : nodo.getAltura();
    }

    /** FE = altura(derecho) - altura(izquierdo). Un nodo nulo tiene FE 0. */
    public int factorEquilibrio(NodoAVL nodo) {
        if (nodo == null) {
            return 0;
        }
        return obtenerAltura(nodo.getDerecho()) - obtenerAltura(nodo.getIzquierdo());
    }

    private void actualizarAltura(NodoAVL nodo) {
        nodo.setAltura(1 + Math.max(obtenerAltura(nodo.getIzquierdo()), obtenerAltura(nodo.getDerecho())));
    }

    // ------------------------------------------------------------------
    // Rotaciones
    // ------------------------------------------------------------------
    /**
     * Rotación simple a la izquierda sobre x (caso Derecha-Derecha).
     * El hijo derecho y sube y x pasa a ser su hijo izquierdo.
     */
    private NodoAVL rotacionIzquierda(NodoAVL x) {
        NodoAVL y = x.getDerecho();
        NodoAVL t2 = y.getIzquierdo();
        y.setIzquierdo(x);
        x.setDerecho(t2);
        actualizarAltura(x);   // primero x (ahora es el de abajo)
        actualizarAltura(y);
        return y;
    }

    /**
     * Rotación simple a la derecha sobre x (caso Izquierda-Izquierda).
     * El hijo izquierdo y sube y x pasa a ser su hijo derecho.
     */
    private NodoAVL rotacionDerecha(NodoAVL x) {
        NodoAVL y = x.getIzquierdo();
        NodoAVL t2 = y.getDerecho();
        y.setDerecho(x);
        x.setIzquierdo(t2);
        actualizarAltura(x);
        actualizarAltura(y);
        return y;
    }

    /** Rotación doble Izquierda-Derecha: izquierda sobre el hijo izquierdo y derecha sobre x. */
    private NodoAVL rotacionDobleIzquierdaDerecha(NodoAVL x) {
        x.setIzquierdo(rotacionIzquierda(x.getIzquierdo()));
        return rotacionDerecha(x);
    }

    /** Rotación doble Derecha-Izquierda: derecha sobre el hijo derecho e izquierda sobre x. */
    private NodoAVL rotacionDobleDerechaIzquierda(NodoAVL x) {
        x.setDerecho(rotacionDerecha(x.getDerecho()));
        return rotacionIzquierda(x);
    }

    /**
     * Actualiza la altura del nodo y, si su FE salió de [-1, 1], aplica la
     * rotación que corresponda. Devuelve la nueva raíz del subárbol.
     * Sirve igual para inserción y eliminación (en la eliminación el hijo
     * pesado puede tener FE 0, que se resuelve con rotación simple).
     */
    private NodoAVL rebalancear(NodoAVL nodo) {
        actualizarAltura(nodo);
        int fe = factorEquilibrio(nodo);
        Integer idPivote = nodo.getDato().getId();

        if (fe > 1) { // pesado a la derecha
            if (factorEquilibrio(nodo.getDerecho()) >= 0) {
                registrar("Rotación simple izquierda (caso Derecha-Derecha) sobre el ID " + idPivote, idPivote);
                return rotacionIzquierda(nodo);
            }
            registrar("Rotación doble derecha-izquierda (caso Derecha-Izquierda) sobre el ID " + idPivote, idPivote);
            return rotacionDobleDerechaIzquierda(nodo);
        }
        if (fe < -1) { // pesado a la izquierda
            if (factorEquilibrio(nodo.getIzquierdo()) <= 0) {
                registrar("Rotación simple derecha (caso Izquierda-Izquierda) sobre el ID " + idPivote, idPivote);
                return rotacionDerecha(nodo);
            }
            registrar("Rotación doble izquierda-derecha (caso Izquierda-Derecha) sobre el ID " + idPivote, idPivote);
            return rotacionDobleIzquierdaDerecha(nodo);
        }
        return nodo;
    }

    private void registrar(String descripcion, Integer idPivote) {
        ultimasRotaciones.add(descripcion);
        ultimosIdsRotados.add(idPivote);
    }

    // ------------------------------------------------------------------
    // Insertar
    // ------------------------------------------------------------------
    public void insertar(Empleado empleado) {
        validarEmpleado(empleado);
        ultimasRotaciones.clear();
        ultimosIdsRotados.clear();
        // Si el id está repetido se lanza la excepción y el árbol no se modifica.
        raiz = insertar(raiz, empleado);
        tamanio++;
    }

    private NodoAVL insertar(NodoAVL nodo, Empleado empleado) {
        if (nodo == null) {
            return new NodoAVL(empleado);
        }
        int cmp = empleado.getId().compareTo(nodo.getDato().getId());
        if (cmp == 0) {
            throw new IllegalArgumentException("Ya existe un empleado con el ID " + empleado.getId() + ".");
        }
        if (cmp < 0) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), empleado));
        } else {
            nodo.setDerecho(insertar(nodo.getDerecho(), empleado));
        }
        return rebalancear(nodo);
    }

    /** Valida el empleado antes de insertarlo (la lógica no depende de la GUI). */
    private void validarEmpleado(Empleado e) {
        if (e == null) {
            throw new IllegalArgumentException("El empleado no puede ser nulo.");
        }
        if (e.getId() == null) {
            throw new IllegalArgumentException("El ID del empleado es obligatorio.");
        }
        Validaciones.textoRequerido("Nombre", e.getNombre());
        Validaciones.textoRequerido("Puesto", e.getPuesto());
        Validaciones.textoRequerido("Departamento", e.getDepartamento());
        Validaciones.validarSalario(e.getSalario());
    }

    // ------------------------------------------------------------------
    // Buscar
    // ------------------------------------------------------------------
    public Empleado buscar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID a buscar no puede ser nulo.");
        }
        NodoAVL actual = raiz;
        while (actual != null) {
            int cmp = id.compareTo(actual.getDato().getId());
            if (cmp == 0) {
                return actual.getDato();
            }
            actual = (cmp < 0) ? actual.getIzquierdo() : actual.getDerecho();
        }
        return null;
    }

    /** Igual que buscar pero devuelve el nodo (con su altura y FE), o null si no existe. */
    public NodoAVL buscarNodo(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID a buscar no puede ser nulo.");
        }
        NodoAVL actual = raiz;
        while (actual != null) {
            int cmp = id.compareTo(actual.getDato().getId());
            if (cmp == 0) {
                return actual;
            }
            actual = (cmp < 0) ? actual.getIzquierdo() : actual.getDerecho();
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Eliminar
    // ------------------------------------------------------------------
    public Empleado eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID a eliminar no puede ser nulo.");
        }
        ultimasRotaciones.clear();
        ultimosIdsRotados.clear();
        Empleado[] eliminado = new Empleado[1];
        raiz = eliminar(raiz, id, eliminado);
        if (eliminado[0] != null) {
            tamanio--;
        }
        return eliminado[0];
    }

    private NodoAVL eliminar(NodoAVL nodo, Integer id, Empleado[] eliminado) {
        if (nodo == null) {
            return null; // no existe
        }
        int cmp = id.compareTo(nodo.getDato().getId());
        if (cmp < 0) {
            nodo.setIzquierdo(eliminar(nodo.getIzquierdo(), id, eliminado));
        } else if (cmp > 0) {
            nodo.setDerecho(eliminar(nodo.getDerecho(), id, eliminado));
        } else {
            // Nodo encontrado
            eliminado[0] = nodo.getDato();
            if (nodo.getIzquierdo() == null) {      // hoja o solo hijo derecho
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {        // solo hijo izquierdo
                return nodo.getIzquierdo();
            }
            // Dos hijos: se reemplaza por el sucesor inorden (mínimo del subárbol derecho)
            NodoAVL sucesor = nodo.getDerecho();
            while (sucesor.getIzquierdo() != null) {
                sucesor = sucesor.getIzquierdo();
            }
            nodo.setDato(sucesor.getDato());
            Empleado[] descarte = new Empleado[1];
            nodo.setDerecho(eliminar(nodo.getDerecho(), sucesor.getDato().getId(), descarte));
        }
        // Al regresar de la recursión se actualiza la altura y se rebalancea cada ancestro
        return rebalancear(nodo);
    }

    // ------------------------------------------------------------------
    // Recorridos
    // ------------------------------------------------------------------
    /** Izquierdo - raíz - derecho (ids ascendentes). */
    public List<Empleado> mostrarInOrden() {
        List<Empleado> lista = new ArrayList<>();
        inOrden(raiz, lista);
        return lista;
    }

    private void inOrden(NodoAVL n, List<Empleado> lista) {
        if (n == null) {
            return;
        }
        inOrden(n.getIzquierdo(), lista);
        lista.add(n.getDato());
        inOrden(n.getDerecho(), lista);
    }

    /** Raíz - izquierdo - derecho. */
    public List<Empleado> mostrarPreOrden() {
        List<Empleado> lista = new ArrayList<>();
        preOrden(raiz, lista);
        return lista;
    }

    private void preOrden(NodoAVL n, List<Empleado> lista) {
        if (n == null) {
            return;
        }
        lista.add(n.getDato());
        preOrden(n.getIzquierdo(), lista);
        preOrden(n.getDerecho(), lista);
    }

    /** Izquierdo - derecho - raíz. */
    public List<Empleado> mostrarPostOrden() {
        List<Empleado> lista = new ArrayList<>();
        postOrden(raiz, lista);
        return lista;
    }

    private void postOrden(NodoAVL n, List<Empleado> lista) {
        if (n == null) {
            return;
        }
        postOrden(n.getIzquierdo(), lista);
        postOrden(n.getDerecho(), lista);
        lista.add(n.getDato());
    }

    // ------------------------------------------------------------------
    // Amplitud y profundidad
    // ------------------------------------------------------------------
    /**
     * Máxima cantidad de nodos que hay en un mismo nivel (no el total de nodos).
     * Árbol vacío = 0.
     */
    public int amplitud() {
        if (raiz == null) {
            return 0;
        }
        int maximo = 0;
        Queue<NodoAVL> cola = new ArrayDeque<>();
        cola.add(raiz);
        while (!cola.isEmpty()) {
            int enNivel = cola.size();
            maximo = Math.max(maximo, enNivel);
            for (int i = 0; i < enNivel; i++) {
                NodoAVL n = cola.poll();
                if (n.getIzquierdo() != null) {
                    cola.add(n.getIzquierdo());
                }
                if (n.getDerecho() != null) {
                    cola.add(n.getDerecho());
                }
            }
        }
        return maximo;
    }

    /** Profundidad máxima medida en NIVELES: vacío = 0, un nodo = 1. */
    public int profundidad() {
        return obtenerAltura(raiz);
    }

    // ------------------------------------------------------------------
    // Verificación y destrucción
    // ------------------------------------------------------------------
    /**
     * Comprueba (recalculando todo desde cero) que el árbol cumpla el orden
     * del ABB, que las alturas guardadas sean correctas y que todo FE esté en
     * [-1, 1]. Se usa en las pruebas y para mostrar el estado en la interfaz.
     */
    public boolean esAVLValido() {
        return verificar(raiz, null, null) >= 0;
    }

    /** Devuelve la altura real del subárbol, o -1 si algo no cumple. */
    private int verificar(NodoAVL n, Integer min, Integer max) {
        if (n == null) {
            return 0;
        }
        Integer id = n.getDato().getId();
        if ((min != null && id <= min) || (max != null && id >= max)) {
            return -1;
        }
        int hi = verificar(n.getIzquierdo(), min, id);
        int hd = verificar(n.getDerecho(), id, max);
        if (hi < 0 || hd < 0 || Math.abs(hd - hi) > 1) {
            return -1;
        }
        int h = 1 + Math.max(hi, hd);
        return (n.getAltura() == h) ? h : -1;
    }

    /** Elimina todos los nodos y deja el árbol vacío y listo para reutilizarse. */
    public void destruirArbol() {
        raiz = null;
        tamanio = 0;
        ultimasRotaciones.clear();
        ultimosIdsRotados.clear();
    }
}
