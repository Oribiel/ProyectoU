/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utilerias;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class JButtonCellEditor extends AbstractCellEditor implements TableCellEditor {

    private final JButton button;
    private final ActionListener actionListener;

    public JButtonCellEditor(String text, ActionListener actionListener) {
        this.button = new JButton(text);
        this.actionListener = actionListener;
        this.button.addActionListener(this.actionListener);
    }

    @Override
    public Object getCellEditorValue() {
        return button.getText();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return button;
    }

    @Override
    public boolean stopCellEditing() {
        super.stopCellEditing();
        button.doClick();
        return true;
    }
}
