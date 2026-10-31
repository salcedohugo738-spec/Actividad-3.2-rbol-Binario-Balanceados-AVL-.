/*
Salcedo Alvarez Hugo Emmanuel
Morales Escobar Juan Adrian
 */
package front_end;

import Nodos.Empleado;
import Nodos.NodoAVL;
import back_end.ArbolAVL;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

/**
 * Panel que dibuja el árbol AVL con nodos y conexiones. Cada nodo muestra su
 * id y debajo su factor de equilibrio (FE) y su altura (h); los nodos que
 * sirvieron de pivote en la última rotación se marcan en color salmón. Al
 * pasar el mouse se ve el detalle del empleado. Se usa como "Arbol Visual"
 * dentro de GUI_ArbolAVL.
 *
 * @author ASUS
 */
public class PanelArbol extends JPanel {

    private static final int DIAMETRO = 46;
    private static final int SEP_X = 64;
    private static final int SEP_Y = 88;
    private static final int MARGEN = 36;

    /** Posición calculada de un nodo para dibujarlo y mostrar su tooltip. */
    private static class Caja {
        NodoAVL nodo;
        int x;
        int y;
        Caja izq;
        Caja der;
    }

    private ArbolAVL arbol;
    private Integer idResaltado;
    private final Set<Integer> idsRotados = new HashSet<>();
    private Caja raizCaja;
    private final List<Caja> cajas = new ArrayList<>();
    private int contadorX;
    private int niveles;

    public PanelArbol() {
        setBackground(Color.WHITE);
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    /** Asigna el árbol a dibujar (puede ser null si aún no se ha creado). */
    public void setArbol(ArbolAVL arbol) {
        this.arbol = arbol;
        this.idResaltado = null;
        this.idsRotados.clear();
        recalcular();
    }

    /** Marca los nodos pivote de la última rotación (null o vacío quita la marca). */
    public void setIdsRotados(Collection<Integer> ids) {
        idsRotados.clear();
        if (ids != null) {
            idsRotados.addAll(ids);
        }
        repaint();
    }

    /** Resalta (en color) el nodo con ese id; null quita el resaltado. */
    public void setIdResaltado(Integer id) {
        this.idResaltado = id;
        repaint();
    }

    /** Vuelve a calcular posiciones y repinta; llamar después de modificar el árbol. */
    public void actualizar() {
        recalcular();
    }

    private void recalcular() {
        cajas.clear();
        raizCaja = null;
        contadorX = 0;
        niveles = 0;
        if (arbol != null && arbol.getRaiz() != null) {
            raizCaja = construir(arbol.getRaiz(), 0);
        }
        revalidate();
        repaint();
    }

    /** La coordenada x sale del orden inorden, así los nodos nunca se encimen. */
    private Caja construir(NodoAVL nodo, int nivel) {
        Caja c = new Caja();
        c.nodo = nodo;
        niveles = Math.max(niveles, nivel + 1);
        if (nodo.getIzquierdo() != null) {
            c.izq = construir(nodo.getIzquierdo(), nivel + 1);
        }
        c.x = MARGEN + contadorX * SEP_X + DIAMETRO / 2;
        contadorX++;
        c.y = MARGEN + nivel * SEP_Y + DIAMETRO / 2;
        if (nodo.getDerecho() != null) {
            c.der = construir(nodo.getDerecho(), nivel + 1);
        }
        cajas.add(c);
        return c;
    }

    @Override
    public Dimension getPreferredSize() {
        if (raizCaja == null) {
            return new Dimension(400, 200);
        }
        int ancho = MARGEN * 2 + contadorX * SEP_X;
        int alto = MARGEN * 2 + niveles * SEP_Y;
        return new Dimension(ancho, alto);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (arbol == null) {
            mensajeCentrado(g2, "Presione \"Crear Arbol\" para comenzar.");
        } else if (raizCaja == null) {
            mensajeCentrado(g2, "El árbol está vacío.");
        } else {
            g2.setStroke(new BasicStroke(1.8f));
            g2.setColor(new Color(120, 120, 120));
            for (Caja c : cajas) {          // primero las líneas
                if (c.izq != null) {
                    g2.drawLine(c.x, c.y, c.izq.x, c.izq.y);
                }
                if (c.der != null) {
                    g2.drawLine(c.x, c.y, c.der.x, c.der.y);
                }
            }
            g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
            FontMetrics fm = g2.getFontMetrics();
            for (Caja c : cajas) {          // luego los nodos encima
                Integer id = c.nodo.getDato().getId();
                boolean resaltado = id.equals(idResaltado);
                int x = c.x - DIAMETRO / 2;
                int y = c.y - DIAMETRO / 2;
                boolean rotado = idsRotados.contains(id);
                g2.setColor(resaltado ? new Color(255, 214, 102)
                        : rotado ? new Color(250, 160, 140)
                        : (c == raizCaja ? new Color(163, 214, 163) : new Color(170, 205, 240)));
                g2.fillOval(x, y, DIAMETRO, DIAMETRO);
                g2.setColor(new Color(40, 60, 90));
                g2.drawOval(x, y, DIAMETRO, DIAMETRO);
                String texto = String.valueOf(id);
                if (fm.stringWidth(texto) > DIAMETRO - 6) {
                    texto = texto.substring(0, 3) + "…";
                }
                g2.setColor(Color.BLACK);
                g2.drawString(texto, c.x - fm.stringWidth(texto) / 2, c.y + fm.getAscent() / 2 - 1);

                // FE y altura debajo del nodo
                int fe = arbol.factorEquilibrio(c.nodo);
                String info = "FE " + (fe > 0 ? "+" : "") + fe + " h" + c.nodo.getAltura();
                g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
                FontMetrics fmi = g2.getFontMetrics();
                g2.setColor(new Color(60, 60, 60));
                g2.drawString(info, c.x - fmi.stringWidth(info) / 2, c.y + DIAMETRO / 2 + fmi.getAscent() + 1);
                g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
            }
        }
        g2.dispose();
    }

    private void mensajeCentrado(Graphics2D g2, String texto) {
        g2.setColor(Color.GRAY);
        g2.setFont(getFont().deriveFont(Font.ITALIC, 15f));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2, getHeight() / 2);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        for (Caja c : cajas) {
            int dx = e.getX() - c.x;
            int dy = e.getY() - c.y;
            if (dx * dx + dy * dy <= (DIAMETRO / 2) * (DIAMETRO / 2)) {
                Empleado emp = c.nodo.getDato();
                return "<html><b>ID " + emp.getId() + "</b><br>" + escapar(emp.getNombre())
                        + "<br>" + escapar(emp.getPuesto()) + " - " + escapar(emp.getDepartamento())
                        + "<br>Salario: " + String.format(java.util.Locale.US, "$%,.2f", emp.getSalario())
                        + "<br>Altura: " + c.nodo.getAltura() + " | FE: " + arbol.factorEquilibrio(c.nodo) + "</html>";
            }
        }
        return null;
    }

    private static String escapar(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
