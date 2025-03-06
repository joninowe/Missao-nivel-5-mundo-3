/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroclient;
import javax.swing.*;

public class SaidaFrame extends JDialog {
    public JTextArea texto;

    public SaidaFrame() {
        // Define as dimensões da janela
        setBounds(100, 100, 400, 300);

        // Define o status modal como false
        setModal(false);

        // Acrescenta o componente JTextArea na janela
        texto = new JTextArea(25, 40);
        texto.setEditable(false); // Bloqueia edição do campo de texto
        
        // Adiciona componente para rolagem
        JScrollPane scroll = new JScrollPane(texto);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Bloqueia rolagem horizontal
        add(scroll);
    }
}
