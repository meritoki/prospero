/*
 * Copyright 2021 Joaquin Osvaldo Rodriguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meritoki.prospero.library.model.document;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * The Page class is used to hold a list of Images.
 *
 * @author jorodriguez
 *
 */
public class Page {

	/**
	 * Logger for class.
	 */
	@JsonIgnore
	static Logger logger = LogManager.getLogger(Page.class.getName());

	/**
	 * Unique identifier instance of Page class
	 */
	@JsonProperty
	public String uuid;
	/**
	 * List of Images
	 */
	@JsonProperty
	public List<Image> imageList = new ArrayList<>();
	/**
	 * Current Image index selected. Default is zero.
	 */
	@JsonProperty
	public int index = 0;
	/**
	 * Cached copy of joined bufferedImage from one or more Images.
	 */
	@JsonIgnore
	public BufferedImage bufferedImage = null;
	/**
	 * Position of class.
	 */
	@JsonProperty
	public Position position = new Position();
	/**
	 * Script applied to Matrix
	 */
	@JsonProperty
	public Script script = new Script();
	/**
	 * 
	 */
	@JsonProperty
	public double threshold = 16;

	@JsonIgnore
	public Query query = null;

	/**
	 * Class constructor
	 */
	public Page() {
		this.uuid = UUID.randomUUID().toString();
	}

	/**
	 * Class constructor that accepts image as parameter
	 */
	public Page(Image image) {
		this.uuid = UUID.randomUUID().toString();
		this.addImage(image);
		this.index = 0;
	}

	/**
	 * Class copy constructor
	 *
	 * @param page
	 */
	public Page(Page page) {
		this.uuid = page.uuid;
		for (Image image : page.imageList) {
			this.imageList.add(new Image(image));
		}
		this.index = page.index;
		this.position = new Position(page.position);
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public Shape moveShape(Shape pressedShape, Point pressedPoint, Point releasedPoint) throws Exception {
		Point movedPoint = this.getMovedPoint(new Point(releasedPoint), new Point(pressedPoint));
		Shape newShape = null;
		if (this.contains(pressedPoint) && this.contains(releasedPoint)) {
			Image pressedImage = this.getImage(pressedPoint);
			Image releasedImage = this.getImage(releasedPoint);
			if (pressedImage != null && releasedImage != null) {
				if (!pressedImage.equals(releasedImage)) {
					newShape = pressedShape;
					newShape.position.move(movedPoint);
					// Testing new logic
					newShape.position = new Position(new Point(newShape.position.getStartPoint()),
							new Point(newShape.position.getStopPoint()), releasedImage.position.relativeScale,
							releasedImage.position.scale, releasedImage.position.offset, releasedImage.position.margin);
					newShape = new Shape(newShape, true);
					pressedImage.removeShape(newShape.uuid);
					releasedImage.addShape(newShape);
					if (newShape instanceof Grid) {
						((Grid) newShape).updateMatrix();
					}
				} else {
					pressedShape.position.move(movedPoint);
					newShape = pressedShape;
					if (newShape instanceof Grid) {
						((Grid) newShape).updateMatrix();
					}
				}
			} else {
				throw new Exception("Image Null");
			}
		} else {
			throw new Exception("Point Not In Bounds");
		}
		return newShape;
	}

	public Point getMovedPoint(Point a, Point b) {
		return new Point(a.x - b.x, a.y - b.y);
	}

	/**
	 * Function gets the current index.
	 *
	 * @return this.index
	 */
	@JsonIgnore
	public int getIndex() {
		return this.index;
	}

	@JsonIgnore
	public int getIndex(String uuid) {
		int index = 0;
		Image image;
		for (int i = 0; i < this.imageList.size(); i++) {
			image = this.imageList.get(i);
			if (image.uuid.equals(uuid)) {
				index = i;
				break;
			}
		}
		return index;
	}

	@JsonIgnore
	public double getThreshold() {
		return threshold;
	}

	/**
	 * Function returns Image using index
	 *
	 * @return file
	 */
	@JsonIgnore
	public Image getImage() {
		int size = this.imageList.size();
		Image image = (this.index < size && size > 0) ? this.imageList.get(this.index) : null;
		//logger.debug("getImage() image=" + image);
		return image;
	}

	@JsonIgnore
	public Image getImage(int index) {
		int size = this.imageList.size();
		Image image = (index < size && size > 0) ? this.imageList.get(index) : null;
		//logger.debug("getImage() image=" + image);
		return image;
	}

	/**
	 * Function returns Image that contains the Point
	 *
	 * @param Point
	 * @return File
	 */
	@JsonIgnore
	public Image getImage(Point point) {
		for (Image image : this.imageList) {
			if (image.containsPoint(point)) {
				return image;
			}
		}
		return null;
	}

	/**
	 * Function returns list of Image objects
	 * 
	 * @return List<Image>
	 */
	@JsonIgnore
	public List<Image> getImageList() {
		return this.imageList;
	}

	/**
	 * Function returns shape that contains the point parameter
	 *
	 * @param point
	 * @return
	 */
	@JsonIgnore
	public Shape getShape(Point point) {
		Shape s = null;
		for (Image image : this.imageList) {
			s = image.getShape(point);
			if (s != null) {
				break;
			}
		}
		return s;
	}

	/**
	 * Function returns the Shape at the current index in Image
	 *
	 * @return shape
	 */
	@JsonIgnore
	public Shape getShape() {
		Image image = this.getImage();
		Shape shape = (image != null) ? image.getShape() : null;
		return shape;
	}

	@JsonIgnore
	public Grid getGrid(String uuid) {
		for (Image image : this.imageList) {
			Grid g = image.getGrid(uuid);
			if(g != null) {
				return g;
			}
		}
		return null;
	}
	
	@JsonIgnore
	public Shape getGridShape() {
		Shape shape = this.getShape();
		if (shape instanceof Grid) {
			Grid grid = (Grid) shape;
			shape = grid.getShape();
		}
		return shape;
	}

	@JsonIgnore
	public List<Shape> getShapeList() {
		List<Shape> shapeList = new ArrayList<>();
		for (Image image : this.imageList) {
			for (Shape shape : image.getShapeList()) {
				shapeList.add(shape);
			}
		}
		return shapeList;
	}
	
	@JsonIgnore
	public List<Guide> getGuideList() {
		List<Shape> shapeList = this.getShapeList();
		List<Guide> guideList = new ArrayList<>();
		for(Shape shape:shapeList) {
			if(shape instanceof Guide) {
				guideList.add((Guide)shape);
			}
		}
		return guideList;
	}

	@JsonIgnore
	public List<Shape> getGridShapeList() {
		List<Shape> shapeList = new ArrayList<>();
		for (Image image : this.imageList) {
			for (Shape shape : image.getGridShapeList()) {
				shapeList.add(shape);
			}
		}
//		logger.info("getGridShapeList() shapeList.size()=" + shapeList.size());
		if (this.query != null) {
			//Fix 20211104 Later
//			boolean timeFlag = query.getTime();
//			boolean spaceFlag = query.getSpace();
//			boolean energyFlag = query.getEnergy();
//			boolean languageFlag = query.getLanguage();
//			if (timeFlag || spaceFlag || energyFlag || languageFlag) {
//				ListIterator<Shape> shapeListIterator = shapeList.listIterator();
//				while (shapeListIterator.hasNext()) {
//					Shape shape = shapeListIterator.next();
//					switch (shape.data.unit.type) {
//					case TIME: {
//						if (!query.getTime()) {
//							shapeListIterator.remove();
//						}
//						break;
//					}
//					case SPACE: {
//						if (!query.getSpace()) {
//							shapeListIterator.remove();
//						}
//						break;
//					}
//					case ENERGY: {
//						if (!query.getEnergy()) {
//							shapeListIterator.remove();
//						}
//						break;
//					}
//					case LANGUAGE: {
//						if (!query.getLanguage()) {
//							shapeListIterator.remove();
//						}
//						break;
//					}
//					default:
//						break;
//					}
//				}
//			}
			if (query.getRandom()) {
				Collections.shuffle(shapeList);
			}
		}
//		logger.info("getGridShapeList() shapeList.size()=" + shapeList.size());
		return shapeList;
	}

	@JsonIgnore
	public List<Shape> getSortedShapeList() {
		return new Matrix(this.getShapeList(), null, this.threshold).getShapeList();
	}

	/**
	 * Function returns bufferedImage with one or more File bufferedImages from the
	 * fileList
	 *
	 * @return this.bufferedImage;
	 */
	@JsonIgnore
	public BufferedImage getBufferedImage(Model model) {
		if (this.bufferedImage == null) {
			this.bufferedImage = this.joinImages(model);
			if (this.bufferedImage != null) {
				this.position.setAbsoluteDimension(
						new Dimension(this.bufferedImage.getWidth(), this.bufferedImage.getHeight()));
			} else {
				logger.info("this.bufferedImage == null");
				this.position.setAbsoluteDimension(new Dimension(0, 0));
			}
		}
		return this.bufferedImage;
	}

	@JsonIgnore
	public BufferedImage getScaledBufferedImage(Model model) {
		BufferedImage before = this.getBufferedImage(model);
		BufferedImage after = new BufferedImage((int) this.position.dimension.width,
				(int) this.position.dimension.height, BufferedImage.TYPE_INT_RGB);// new
																					// BufferedImage(before.getWidth(),
																					// before.getHeight(),
																					// BufferedImage.TYPE_INT_RGB);
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.scale(this.position.scale, this.position.scale);// this handles scaling the bufferedImage
		AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);
		after = affineTransformOp.filter(before, after);
		return after;
	}

	@JsonIgnore
	public Matrix getMatrix() {
		return new Matrix(this.getGridShapeList(), this.script, this.threshold);
	}

	@JsonIgnore
	public Table getTable() {
		return new Table(this.getMatrix());
	}

	@JsonIgnore
	public Archive getArchive() {
		return new Archive(this.getMatrix());
	}

	@JsonIgnore
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	@JsonIgnore
	public void setBufferedImage(BufferedImage bufferedImage) {
		//logger.debug("setBufferedImage(" + bufferedImage + ")");
		this.bufferedImage = bufferedImage;
	}

	/**
	 * Very important function called from Document that ensures only one Page is
	 * loaded in Memory at once
	 */
	@JsonIgnore
	public void setBufferedImageNull() {
		this.setBufferedImage(null);
		for (Image image : this.imageList) {
			image.setBufferedImage(null);
		}
	}

	/**
	 * Function sets the current index selected by user.
	 *
	 * @param index
	 */
	@JsonIgnore
	public boolean setIndex(int index) {
		boolean flag = false;
		if (index >= 0 && index < this.imageList.size()) {
			this.index = index;
			flag = true;
		}
		return flag;
	}

	@JsonIgnore
	public boolean setImage(String uuid) {
		//logger.debug("setImage(" + uuid + ")");
		boolean flag = false;
		Image image = null;
		List<Image> imageList = this.getImageList();
		for (int i = 0; i < imageList.size(); i++) {
			image = imageList.get(i);
			if (image.uuid.equals(uuid)) {
				flag = true;
				this.setIndex(i);
				break;
			}
		}
		return flag;
	}

	/**
	 * Function sets scale for page and all files in fileList
	 *
	 * @param scale
	 */
	@JsonIgnore
	public void setScale(double scale) {
//		logger.info("setScale(" + scale + ")");
		this.position.setScale(scale);
		for (Image image : this.imageList) {
			image.setScale(scale);
		}
	}

	/**
	 * Function sets current shape and file using uuid
	 *
	 * @param uuid
	 */
	@JsonIgnore
	public void setShape(String uuid) {
		for (Image image : this.getImageList()) {
			if (image.setShape(uuid)) {
				this.setImage(image.uuid);
				break;
			}
		}
	}

	@JsonIgnore
	public void setGridShape(String uuid) {
		for (Image image : this.getImageList()) {
			if (image.setGridShape(uuid)) {
				this.setImage(image.uuid);
				break;
			}
		}
	}

	@JsonIgnore
	public void addImage(Image image) {
		//logger.info("addImage(" + image + ")");
		this.imageList.add(image);
		image.setScale(this.position.scale);
		image.setOffset(this.position.absoluteDimension.width);
		this.position.addAbsoluteDimension(
				new Dimension(image.position.absoluteDimension.width, image.position.absoluteDimension.height));// added
																												// height
																												// to
																												// fix
																												// contain
																												// point
																												// method
																												// and
																												// allow
																												// MoveShapeTest
																												// to
																												// work
		for (Shape shape : image.shapeList) {
			shape.position.setOffset(image.position.offset);
		}
	}

	/**
	 * Shape has global coordinates
	 *
	 * @param shape
	 */
	@JsonIgnore
	public void addShape(Shape shape) {
		for (Image image : this.getImageList()) {
			if (image.containsShape(shape)) {
				image.addShape(shape);
				this.setShape(shape.uuid);
				break;
			}
		}
	}

	@JsonIgnore
	public Image removeImage(Image image) {
		return this.removeImage(image.uuid);
	}

	@JsonIgnore
	public Image removeImage(String uuid) {
		ListIterator<Image> imageListIterator = this.imageList.listIterator();
		while (imageListIterator.hasNext()) {
			Image image = imageListIterator.next();
			if (image.uuid.equals(uuid)) {
				imageListIterator.remove();
				this.position.absoluteDimension.width -= image.position.absoluteDimension.width;
				image.setOffset(0);
				image.setMargin(0);
				return image;
			}
		}
		return null;
	}

	/**
	 * Need to refactor
	 * 
	 * @param shape
	 * @return
	 */
	@JsonIgnore
	public Shape removeShape(Shape shape) {
		//logger.info("removeShape(" + shape + ")");
		Shape s = null;
		if (shape != null) {
			for (Image image : this.getImageList()) {
				s = image.removeShape(shape.uuid);
				if (s != null) {
					break;
				}
			}
		}
		return s;
	}

	@JsonIgnore
	public Selection intersectShape(Point point) {
		Selection selection = null;
		Image image = this.getImage();
		if (image != null) {
			selection = image.intersectShape(point);
		}
		return selection;
	}

	@JsonIgnore
	public boolean contains(Point point) {
		boolean flag = this.position.contains(point);
		//logger.debug("contains(" + point + ") flag=" + flag);
		return flag;
	}

	@JsonIgnore
	public BufferedImage joinImages(Model model) {
//		logger.debug("joinImages(" + imageList + ")");
		BufferedImage bufferedImage = null;
		double offset = 0;
		for (int i = 0; i < imageList.size(); i++) {
			Image a = imageList.get(i);
			if (bufferedImage == null) {
				a.setOffset(offset);
				BufferedImage aBufferedImage = a.getBufferedImage(model);
				if (aBufferedImage != null) {
					int width = aBufferedImage.getWidth();
					int height = (int) (aBufferedImage.getHeight() + a.position.margin);
					bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
					Graphics2D graphics2D = bufferedImage.createGraphics();
					graphics2D.setPaint(Color.BLACK);
					graphics2D.fillRect(0, 0, width, height);
					graphics2D.setColor(Color.BLACK);
					graphics2D.drawImage(a.getBufferedImage(model), null, 0, (int) (a.position.margin));
					graphics2D.dispose();
					offset = a.position.absoluteDimension.width;
				}
			}
			if (i + 1 < imageList.size()) {
				Image b = imageList.get(i + 1);
				b.setOffset(offset);
				int w = bufferedImage.getWidth();
				int h = bufferedImage.getHeight();
				int width = w + b.getBufferedImage(model).getWidth();
				int height = Math.max(h, b.getBufferedImage(model).getHeight() + (int) b.position.margin);
				BufferedImage bI = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics2D = bI.createGraphics();
				Color oldColor = graphics2D.getColor();
				graphics2D.setPaint(Color.BLACK);
				graphics2D.fillRect(0, 0, width, height);
				graphics2D.setColor(oldColor);
				graphics2D.drawImage(bufferedImage, null, 0, 0);
				graphics2D.drawImage(b.getBufferedImage(model), null, w, (int) (b.position.margin));
				graphics2D.dispose();
				bufferedImage = bI;
				offset += b.position.absoluteDimension.width;
			}
		}
		return bufferedImage;
	}

	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer();// .withDefaultPrettyPrinter();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			logger.error("IOException " + ex.getMessage());
		}
		return string;
	}
}

//@JsonIgnore
//public BufferedImage joinImages(List<Image> imageList) {
//	logger.debug("joinImages(" + imageList + ")");
//	BufferedImage bufferedImage = null;
//	double offset = 0;
//	for (int i = 0; i < imageList.size(); i++) {
//		Image a = imageList.get(i);
//		if (bufferedImage == null) {
//			a.setOffset(offset);
//			int width = a.getBufferedImage().getWidth();
//			int height = (int) (a.getBufferedImage().getHeight() + a.position.margin);
//			bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//			Graphics2D graphics2D = bufferedImage.createGraphics();
//			graphics2D.setPaint(Color.BLACK);
//			graphics2D.fillRect(0, 0, width, height);
//			graphics2D.setColor(Color.BLACK);
//			graphics2D.drawImage(a.getBufferedImage(), null, 0, (int)(a.position.margin));
//			graphics2D.dispose();
//			offset = a.position.absoluteDimension.width;
//		}
//		if (i + 1 < imageList.size()) {
//			Image b = imageList.get(i + 1);
//			b.setOffset(offset);
//			int w = bufferedImage.getWidth();
//			int h = bufferedImage.getHeight();
//			int width = w + b.getBufferedImage().getWidth();
//			int height = Math.max(h, b.getBufferedImage().getHeight() + (int) b.position.margin);
//			BufferedImage bI = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//			Graphics2D graphics2D = bI.createGraphics();
//			Color oldColor = graphics2D.getColor();
//			graphics2D.setPaint(Color.BLACK);
//			graphics2D.fillRect(0, 0, width, height);
//			graphics2D.setColor(oldColor);
//			graphics2D.drawImage(bufferedImage, null, 0, 0);
//			graphics2D.drawImage(b.getBufferedImage(), null, w, (int) (b.position.margin));
//			graphics2D.dispose();
//			bufferedImage = bI;
//			offset+= b.position.absoluteDimension.width;
//		}
//	}
//	return bufferedImage;
//}

//@JsonIgnore
//public void paint(Graphics2D graphics2D) {
//	AffineTransform affineTransform = new AffineTransform();
//	affineTransform.scale(this.position.scale, this.position.scale);//this handles scaling the bufferedImage
//	BufferedImage bufferedImage = this.getBufferedImage();
//	if (bufferedImage != null) {
//		graphics2D.drawImage(bufferedImage, affineTransform, null);
//	}
//	List<Image> imageList = this.getImageList();
//	Image image = this.getImage();
//	if (imageList != null) {
//		for (Image i : imageList) {
//			Position p = i.position;
//			if (image != null && i.uuid.equals(image.uuid)) {
//				graphics2D.setColor(Color.RED);
//			} else {
//				graphics2D.setColor(Color.YELLOW);
//			}
//			Rectangle2D.Double rectangle = new Rectangle2D.Double(p.point.x, p.point.y, p.dimension.width, p.dimension.height);
//			graphics2D.draw(rectangle);
//		}
//
//	}
//	List<Shape> shapeList = this.getSortedShapeList();//this.getMatrix().getShapeList();
//	Shape shape = this.getShape();
//	Shape gridShape = this.getGridShape();
//	Shape previousShape = null;
//	if (shapeList != null) {
//		for (Shape s : shapeList) {
//			Position position = s.position;
//			if (shape != null && s.uuid.equals(shape.uuid)) {
//				graphics2D.setColor(Color.RED);
//			} else {
//				graphics2D.setColor(Color.BLUE);
//			}
//			switch (s.type) {
//			case ELLIPSE: {
//				Ellipse2D.Double ellipse = new Ellipse2D.Double(position.point.x, position.point.y, position.dimension.width, position.dimension.height);
//				graphics2D.draw(ellipse);
//				break;
//			}
//			case RECTANGLE: {
//				Rectangle2D.Double rectangle = new Rectangle2D.Double(position.point.x, position.point.y, position.dimension.width, position.dimension.height);
//				graphics2D.draw(rectangle);
//				if(s instanceof Grid) {
//					Shape[][] matrix = ((Grid) s).matrix;
//					for(int i=0;i<matrix.length;i++) {
//						for(int j=0;j<matrix[i].length;j++) {
//							Shape gs = matrix[i][j];
//							if (gridShape != null && gs.uuid.equals(gridShape.uuid)) {
//								graphics2D.setColor(Color.YELLOW);
//							} else {
//								if (shape != null && s.uuid.equals(shape.uuid)) {
//									graphics2D.setColor(Color.RED);
//								} else {
//									graphics2D.setColor(Color.BLUE);
//								}
//							}
//							rectangle = new Rectangle2D.Double(gs.position.point.x, gs.position.point.y, gs.position.dimension.width, gs.position.dimension.height);
//							graphics2D.draw(rectangle);
//						}
//					}
//				}
//				break;
//			}
//			}
//			if (previousShape != null) {
//				Position p = previousShape.position;
//				graphics2D.drawLine((int)(p.center.x),(int)(p.center.y),(int)(position.center.x),(int)(position.center.y));
//			}
//			previousShape = s;
//		}
//	}
//	graphics2D.setColor(Color.BLUE);
//	Rectangle2D.Double frame = new Rectangle2D.Double(0, 0, this.position.dimension.width, this.position.dimension.height);
//	graphics2D.draw(frame);
//}

//	@JsonIgnore
//	public List<Shape> getShapeList(String uuid, boolean flag) {
//		BufferedImage bufferedImage = null;
//		if(flag)
//			bufferedImage = this.getScaledBufferedImage(uuid);
//		List<Shape> shapeList = new ArrayList<>();
//		for (Image image : this.imageList) {
//			for (Shape shape : image.getShapeList()) {
//				if(flag)
//					shape.bufferedImage = this.getShapeBufferedImage(bufferedImage, shape);
//				shapeList.add(shape);
//			}
//		}
//		return shapeList;
//	}
//	
//	@JsonIgnore
//	public List<Shape> getGridShapeList(String uuid, boolean flag) {
//		BufferedImage bufferedImage = null;
//		if(flag)
//			bufferedImage = this.getScaledBufferedImage(uuid);
//		List<Shape> shapeList = new ArrayList<>();
//		for (Image image : this.imageList) {
//			for (Shape shape : image.getGridShapeList()) {
//				if(flag)
//					shape.bufferedImage = this.getShapeBufferedImage(bufferedImage,shape);
//				shapeList.add(shape);
//			}
//		}
//		return shapeList;
//	}

//@JsonIgnore
//public List<Shape> getShapeList(String uuid, boolean flag) {
//	BufferedImage bufferedImage = null;
//	if(flag)
//		bufferedImage = this.getScaledBufferedImage(uuid);
//	List<Shape> shapeList = new ArrayList<>();
//	for (Image image : this.imageList) {
//		for (Shape shape : image.getShapeList()) {
//			if(flag)
//				shape.bufferedImage = this.getShapeBufferedImage(bufferedImage, shape);
//			shapeList.add(shape);
//		}
//	}
//	return shapeList;
//}
//
//@JsonIgnore
//public List<Shape> getGridShapeList(String uuid, boolean flag) {
//	BufferedImage bufferedImage = null;
//	if(flag)
//		bufferedImage = this.getScaledBufferedImage(uuid);
//	List<Shape> shapeList = new ArrayList<>();
//	for (Image image : this.imageList) {
//		for (Shape shape : image.getGridShapeList()) {
//			if(flag)
//				shape.bufferedImage = this.getShapeBufferedImage(bufferedImage,shape);
//			shapeList.add(shape);
//		}
//	}
//	return shapeList;
//}