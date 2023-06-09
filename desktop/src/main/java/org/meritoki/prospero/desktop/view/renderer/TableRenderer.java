package org.meritoki.prospero.desktop.view.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.meritoki.prospero.desktop.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableRenderer extends DefaultTableCellRenderer {

	static Logger logger = LoggerFactory.getLogger(TableRenderer.class.getName());
	Color backgroundColor = Color.white;
	Model model = null;

	public TableRenderer(Model model) {
		this.model = model;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (column == 0 && value instanceof String) {
			String v = (String) value;
			if (v.length() > 0 && v.contains("#") && v.indexOf('#') == 0) {
				Color color = Color.decode(v);
				cell.setBackground(color);
			}
		} else {
			cell.setBackground(Color.WHITE);
		}

//		Shape shape = model.system.matrix.getShape(row, column);
//		if(shape != null) {
//			String text = shape.getData().getText().value;
//			if(text != null) {
//				c.setBackground(Color.green);
//			} else {
//				c.setBackground(Color.blue);
//			}
//			Shape s = model.document.getShape();
//			if(s != null) {
//				if(s instanceof Grid) {
////				logger.info("getTableCellRendererComponent(...) s="+s);
//					Grid g = (Grid)s;
//					List<Shape> shapeList = g.getShapeList();
//					if(shapeList.contains(shape)) {
//						if(g.getShape().uuid.equals(shape.uuid)) {
//							c.setBackground(Color.YELLOW);
//						} else {
//							c.setBackground(Color.RED);
//						}
//					}
//				} else {
//					if(shape.uuid.equals(s.uuid)) {
//						c.setBackground(Color.RED);
//					}
//				}
//			}
//		} else {
//			c.setBackground(backgroundColor);
//		}
		return cell;
	}
}