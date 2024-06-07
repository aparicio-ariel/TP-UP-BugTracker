package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Asegurarse de que solo se aplique color a la columna "Status"
        if (column == 3) {  // Suponiendo que la columna de estado es la cuarta columna (índice 3)
            if (value != null) {
                String status = value.toString();
                if (status.equals("Retrasado")) {
                    cellComponent.setForeground(Color.RED);
                } else {
                    cellComponent.setForeground(Color.GREEN);
                }
            }
        } else {
            cellComponent.setForeground(table.getForeground()); // Color original para otras columnas
        }

        return cellComponent;
    }
}
