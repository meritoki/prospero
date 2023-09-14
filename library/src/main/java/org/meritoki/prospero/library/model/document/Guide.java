package org.meritoki.prospero.library.model.document;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Guide extends Shape {
	
	@JsonProperty
	public boolean flag = true; 
	
	public Guide() {}
	
	public Guide(Guide guide,boolean flag) {
		super(guide,flag);
	}
	
	@JsonIgnore
	public void snapShape(Shape shape, Tool tool) {
		List<Selection> selectionList = this.getSelectionList(shape);
		for(Selection selection:selectionList) {
			switch(selection) {
			case TOP: {
				if(tool == Tool.RESIZE || tool == Tool.DRAW)
					shape.position.resize(this.position.getStartPoint(), selection);
				else if(tool == Tool.MOVE) {
					shape.position.move(Position.getMovedPoint(new Point(shape.position.getStartPoint().x,this.position.getStartPoint().y),new Point(shape.position.getStartPoint().x,shape.position.getStartPoint().y)));
				}
				break;
			}
			case BOTTOM:
				if(tool == Tool.RESIZE || tool == Tool.DRAW)
					shape.position.resize(this.position.getStopPoint(), selection);
				else if(tool == Tool.MOVE) {
					shape.position.move(Position.getMovedPoint(new Point(shape.position.getStopPoint().x,this.position.getStopPoint().y),new Point(shape.position.getStopPoint().x,shape.position.getStopPoint().y)));
				}
				break;
			case BOTTOM_LEFT:
				break;
			case BOTTOM_RIGHT:
				break;
			case LEFT:
				if(tool == Tool.RESIZE || tool == Tool.DRAW)
					shape.position.resize(this.position.getStartPoint(), selection);
				else if(tool == Tool.MOVE) {
					shape.position.move(Position.getMovedPoint(new Point(this.position.getStartPoint().x,shape.position.getStartPoint().y),new Point(shape.position.getStartPoint().x,shape.position.getStartPoint().y)));
				}
				break;
			case RIGHT:
				if(tool == Tool.RESIZE || tool == Tool.DRAW)
					shape.position.resize(this.position.getStopPoint(), selection);
				else if(tool == Tool.MOVE) {
					shape.position.move(Position.getMovedPoint(new Point(this.position.getStopPoint().x,shape.position.getStopPoint().y),new Point(shape.position.getStopPoint().x,shape.position.getStopPoint().y)));
				}
				break;
			case TOP_LEFT:
				break;
			case TOP_RIGHT:
				break;
			default:
				break;
			}
		}
	}
	
	public List<Selection> getSelectionList(Shape shape) {
		List<Selection> selectionList = new ArrayList<>();
		List<Point> pointList = shape.position.getPointList();
		for(Point point:pointList) {
			Selection selection = this.position.selection(point);
			if(selection != null) {
				selectionList.add(selection);
			}
		}
		return selectionList;
	}
}
//if(this.position.containsPosition(shape.position)) {
//
//}
