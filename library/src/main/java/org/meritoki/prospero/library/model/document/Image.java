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

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.controller.node.NodeController;
import org.meritoki.prospero.library.model.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Class is used to manage reference to file in filesystem
 * 
 * @author jorodriguez
 *
 */
public class Image {
	@JsonIgnore
	static Logger logger = LogManager.getLogger(Image.class.getName());
	@JsonProperty
	public String uuid;
	@JsonProperty
	public String filePath;
	@JsonProperty
	public String fileName;
	@JsonIgnore
	public String fileCache;
	@JsonProperty
	public Position position = new Position();
	@JsonProperty
	public List<Shape> shapeList = new ArrayList<>();
	@JsonIgnore
	public BufferedImage bufferedImage;
	@JsonIgnore
	public int index = 0;
	@JsonIgnore
	public File file;

	/**
	 * Default constructor
	 */
	public Image() {
	}

	public static String getSeperator() {
		return FileSystems.getDefault().getSeparator();
	}

	/**
	 * Copy constructor
	 * 
	 * @param image
	 */
	public Image(Image image) {
		this.uuid = image.uuid;
		this.file = image.file;
		this.filePath = image.filePath;
		this.fileName = image.fileName;
		this.position = new Position(image.position);
		this.index = image.index;
		for (Shape shape : image.shapeList) {
			if (shape instanceof Grid) {
				this.shapeList.add(new Grid((Grid) shape, true));
			} else {
				this.shapeList.add(new Shape(shape, true));
			}
		}
	}

	public Image(File file) {
		this.uuid = UUID.randomUUID().toString();
		this.file = file;
		this.filePath = this.file.getParent();
		this.fileName = this.file.getName();
//		this.fileCache = this.uuid + "." + this.getExtension(this.fileName);
	}

	@JsonIgnore
	public String getUUID() {
		return this.uuid;
	}

	@JsonIgnore
	public String getExtension(String fileName) {
		fileName = fileName.toLowerCase();
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	/**
	 * Function returns true if files have the same uuid
	 * 
	 * @param image
	 * @return flag
	 */
	@JsonIgnore
	public boolean equals(Image image) {
		boolean flag = false;
		if (this.uuid.equals(image.uuid)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * Function gets the current index selected by user.
	 * 
	 * @return
	 */
	@JsonIgnore
	public int getIndex() {
		//logger.debug("getIndex() this.index=" + this.index);
		return this.index;
	}

	/**
	 * Functions returns shape for index.
	 * 
	 * @return
	 */
	@JsonIgnore
	public Shape getShape() {
		Shape shape = null;
		if (this.index >= 0 && this.index < this.shapeList.size()) {
			shape = this.shapeList.get(this.index);
		}
		return shape;
	}

	/**
	 * Functions returns shape for Point
	 * 
	 * @param point
	 * @return
	 */
	@JsonIgnore
	public Shape getShape(Point point) {
		List<Guide> guideList = new ArrayList<>();
		Shape s = null;
		for (Shape shape : this.shapeList) {
			if (shape.contains(point)) {
				if(shape instanceof Guide) {
					guideList.add((Guide)shape);
				} else if(shape instanceof Grid) {
					Grid grid = (Grid)shape;
					grid.setShape(point);
					s = shape;
					break;
				} else {
					s = shape;
					break;
				}
			}
		}
		if(s == null && guideList.size() > 0) {
			s = guideList.get(0);
		}
		return s;
	}
	
	@JsonIgnore
	public Grid getGrid(String uuid) {
		List<Shape> shapeList = this.getShapeList();
		for(Shape shape: shapeList) {
			if(shape instanceof Grid) {
				Grid g = (Grid)shape;
				if(g.uuid.equals(uuid)) {
					return g;
				} else if(g.setShape(uuid)) {
					return g;
				}
			}
		}
		return null;
	}

	/**
	 * Function returns list of Shape objects
	 * 
	 * @return
	 */
	@JsonIgnore
	public List<Shape> getShapeList() {
		List<Shape> shapeList = new ArrayList<>();
		for (Shape s : this.shapeList) {
			shapeList.add(s);
		}
		return shapeList;
	}
	
	@JsonIgnore
	public List<Shape> getGuideList() {
		List<Shape> shapeList = new ArrayList<>();
		for (Shape s : this.shapeList) {
			if (s instanceof Guide) {
				shapeList.add(s);
			}
		}
		return shapeList;
	}

	@JsonIgnore
	public List<Shape> getGridShapeList() {
		List<Shape> shapeList = new ArrayList<>();
		for (Shape s : this.shapeList) {
			if (s instanceof Grid) {
				shapeList.addAll(((Grid) s).getShapeList());
			} else {
				shapeList.add(s);
			}
		}
		return shapeList;
	}

//Original Revert to this state
	@JsonIgnore
	public BufferedImage getBufferedImage(Model model) {
//		MemoryController.log();
		if (this.bufferedImage == null) {
//			logger.info("getBufferedImage(model) this.bufferedImage == null");
			// make document cache
			File directory = new File(NodeController.getDocumentCache(model.document.uuid));
			if (!directory.exists()) {
				directory.mkdirs();
			}
			BufferedImage bufferedImage = null;
			this.fileCache = this.uuid + "." + this.getExtension(this.fileName);
			File cache = new File(NodeController.getDocumentCache(model.document.uuid) + NodeController.getSeperator()
					+ this.fileCache);
			if (cache.exists()) {
//				logger.info("getBufferedImage(model) this.fileCache Exists");
				bufferedImage = NodeController.openBufferedImage(cache);
			} else {
				//logger.info("getBufferedImage(model) this.fileCache Exists Not");
				this.file = new File(this.filePath + NodeController.getSeperator() + this.fileName);// load local file
				if(!this.file.exists()) {
					this.file = new File(NodeController.getDocumentCache(model.document.uuid) + NodeController.getSeperator()
					+ this.fileName);
				}
				if (this.file.exists()) {
					//logger.info("getBufferedImage(model) this.file Exists");
					bufferedImage = NodeController.openBufferedImage(this.file);
					if (bufferedImage != null) {
						logger.info("getBufferedImage(model) bufferedImage Not Null");
						try {
							if (this.fileCache.toLowerCase().contains("jpg") || this.fileCache.toLowerCase().contains("jpeg")) {
								logger.info("getBufferedImage(model) JPG");
								NodeController.saveJpg(NodeController.getDocumentCache(model.document.uuid),
										this.fileCache, bufferedImage);

							} else if (this.fileCache.toLowerCase().contains("png")) {
								logger.info("getBufferedImage(model) PNG");
								NodeController.savePng(NodeController.getDocumentCache(model.document.uuid),
										this.fileCache, bufferedImage);

							} else {
								logger.info("getBufferedImage(model) ERROR");
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Could Not Create File Cache");
							e.printStackTrace();
							
						}
					}
				}
			}
			if (bufferedImage != null) {
				this.setBufferedImage(bufferedImage);
				BufferedImage beforeBufferedImage = bufferedImage;
				int w = beforeBufferedImage.getWidth();
				int h = beforeBufferedImage.getHeight();
				BufferedImage afterBufferedImage = new BufferedImage((int) (w * this.position.relativeScale),
						(int) (h * this.position.relativeScale), BufferedImage.TYPE_INT_ARGB);
				AffineTransform at = new AffineTransform();
				at.scale(this.position.relativeScale, this.position.relativeScale);
				AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
				afterBufferedImage = scaleOp.filter(beforeBufferedImage, afterBufferedImage);
				this.bufferedImage = afterBufferedImage;
				this.position.setAbsoluteDimension(
						new Dimension(this.bufferedImage.getWidth(), this.bufferedImage.getHeight()));
			}
				
		}
//			if (bufferedImage == null) {
//				logger.info("getBufferedImage(model) this.bufferedImage == null B");
//				if (file == null) {
//					file = new File(this.filePath + NodeController.getSeperator() + this.fileName);// load local file
//					if (!file.exists()) {
//						this.file = null;
//
//					}
//				}
//				if (file == null) {
//					// load fil
//					file = new File(NodeController.getDocumentCache(model.document.uuid) + NodeController.getSeperator()
//							+ this.fileName);
//					if (!file.exists()) {
////						file.createNewFile();
//						this.file = null;
//					}
//				}
//				logger.info("getBufferedImage(model) this.file=" + this.file);
//				if (this.file != null) {
//					bufferedImage = NodeController.openBufferedImage(this.file);
//					if (bufferedImage != null) {
//						this.setBufferedImage(bufferedImage);
//						if (this.getExtension().equals("jpg") || this.getExtension().equals("jpeg")) {
//							try {
//								NodeController.saveJpg(NodeController.getDocumentCache(model.document.uuid),
//										this.fileCache, bufferedImage);
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						} else if (this.getExtension().equals("jpeg")) {
//							try {
//								NodeController.saveJpg(NodeController.getDocumentCache(model.document.uuid),
//										this.fileCache, bufferedImage);
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						} else if (this.getExtension().equals("png")) {
//							try {
//								NodeController.savePng(NodeController.getDocumentCache(model.document.uuid),
//										this.fileCache, bufferedImage);
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					} else if (model.system.multiUser) {
//						ClientController clientController = new ClientController(model);
//						if (clientController.fileClient.checkHealth()) {
//							if (!clientController.fileClient.checkFile(this.uuid)) {
//								clientController.fileClient.markFile(this.uuid);
//							} else {
//								clientController.fileClient.downloadFile(
//										NodeController.getDocumentCache(model.document.uuid),
//										this.uuid + "." + this.getExtension());
//								clientController.fileClient.unmarkFile(this.uuid);
//							}
//						}
//					}
//				}
//			} else if (model.system.multiUser) {
//				ClientController clientController = new ClientController(model);
//				if (clientController.fileClient.checkHealth()) {
//					if (clientController.fileClient.checkFile(this.uuid)) {
//						clientController.fileClient.uploadFile(NodeController.getDocumentCache(model.document.uuid),
//								this.uuid + "." + this.getExtension());
//					}
//				}
//			}
//			if (bufferedImage != null) {
//				BufferedImage beforeBufferedImage = bufferedImage;
//				int w = beforeBufferedImage.getWidth();
//				int h = beforeBufferedImage.getHeight();
//				BufferedImage afterBufferedImage = new BufferedImage((int) (w * this.position.relativeScale),
//						(int) (h * this.position.relativeScale), BufferedImage.TYPE_INT_ARGB);
//				AffineTransform at = new AffineTransform();
//				at.scale(this.position.relativeScale, this.position.relativeScale);
//				AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//				afterBufferedImage = scaleOp.filter(beforeBufferedImage, afterBufferedImage);
//				this.bufferedImage = afterBufferedImage;
//				this.position.setAbsoluteDimension(
//						new Dimension(this.bufferedImage.getWidth(), this.bufferedImage.getHeight()));
//			}
//		}
//		MemoryController.log();
		return this.bufferedImage;
	}

	/**
	 * Function sets the current index selected by user.
	 * 
	 * @param index
	 */
	@JsonIgnore
	public void setIndex(int index) {
		if (index >= 0 && index < this.shapeList.size()) {
			this.index = index;
		}
	}

	@JsonIgnore
	public void setScale(double scale) {
//		logger.info("setScale(" + scale + ")");
		this.position.setScale(scale);
		for (Shape shape : this.shapeList) {
			shape.setScale(scale);
		}
	}

	@JsonIgnore
	public void setRelativeScale(double scale) {
		logger.info("setRelativeScale(" + scale + ")");
		this.position.setRelativeScale(scale);
		for (Shape shape : this.shapeList) {
			shape.setRelativeScale(scale);
		}
	}

//New 
	@JsonIgnore
	public void setBufferedImage(BufferedImage bufferedImage) {
		logger.debug("setBufferedImage(" + bufferedImage + ")");
		this.bufferedImage = bufferedImage;
		if (this.bufferedImage != null) {
			this.position.absoluteDimension.width = this.bufferedImage.getWidth();
			this.position.absoluteDimension.height = this.bufferedImage.getHeight();
		}
	}

	@JsonIgnore
	public void setOffset(double offset) {
		logger.debug("setOffset(" + offset + ")");
		this.position.setOffset(offset);
		for (Shape shape : this.shapeList) {
			shape.setOffset(offset);
		}
	}

	/**
	 * Function necessary for a shape to move at all with the margin shift
	 * 
	 * @param margin
	 */
	@JsonIgnore
	public void setMargin(double margin) {
		logger.debug("setMargin(" + margin + ")");
		this.position.setMargin(margin);
		for (Shape shape : this.shapeList) {
			shape.setMargin(margin);
		}
	}

	/**
	 * Functions sets current index by uuid.
	 * 
	 * @param uuid
	 */
	@JsonIgnore
	public boolean setShape(String uuid) {
		logger.debug("setShape(" + uuid + ")");
		boolean flag = false;
		Shape shape = null;
		for (int i = 0; i < this.shapeList.size(); i++) {
			shape = this.shapeList.get(i);
			if (shape.uuid.equals(uuid)) {
				this.index = i;
				flag = true;
				break;
			}
		}
		return flag;
	}

	@JsonIgnore
	public boolean setGridShape(String uuid) {
		boolean flag = false;
		Shape shape = this.getShape();
		if (shape instanceof Grid) {
			Grid grid = (Grid) shape;
			flag = grid.setShape(uuid);
		}
		return flag;
	}

	/**
	 * Function adds Shape to Shape List
	 * 
	 * @param shape
	 */
	@JsonIgnore
	public void addShape(Shape shape) {
		this.shapeList.add(shape);
	}

	/**
	 * Need To Refactor
	 * 
	 * @param point
	 * @return
	 */
	@JsonIgnore
	public Selection intersectShape(Point point) {
		Selection selection = null;
		Shape shape = this.getShape();
		if (shape != null) {
			selection = shape.position.selection(point);
		}
		return selection;
	}

	@JsonIgnore
	public boolean containsShape(Shape shape) {
		return this.containsPoint(shape.position.getStartPoint());
	}

	/**
	 * Function returns true if input Shape is removed
	 * 
	 * @param shape
	 * @return
	 */
	@JsonIgnore
	public Shape removeShape(Shape shape) {
		return this.removeShape(shape.uuid);
	}

	/**
	 * Function returns true if input Shape is removed by UUID
	 * 
	 * @param uuid
	 * @return
	 */
	@JsonIgnore
	public Shape removeShape(String uuid) {
		ListIterator<Shape> shapeListIterator = this.shapeList.listIterator();
		while (shapeListIterator.hasNext()) {
			Shape shape = shapeListIterator.next();
			if (shape.uuid.equals(uuid)) {
				shapeListIterator.remove();
				return shape;
			}
		}
		return null;
	}

	@JsonIgnore
	public List<Shape> removeAllShapes() {
		List<Shape> shapeList = this.shapeList;
		this.shapeList = new ArrayList<>();
		return shapeList;
	}

	/**
	 * Function returns true if Point is contained within Image
	 * 
	 * @param point
	 * @return
	 */
	@JsonIgnore
	public boolean containsPoint(Point point) {
		boolean flag = this.position.contains(point);
		logger.debug("containsPoint(" + point + ") flag=" + flag);
		return flag;
	}

	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer();
		if (logger.isDebugEnabled()) {
			try {
				string = ow.writeValueAsString(this);
			} catch (IOException e) {
				logger.error("IOException " + e.getMessage());
			}
		} else if (logger.isInfoEnabled()) {
			string = "{\"uuid\":" + this.uuid + ", \"position\":" + position + "}";
		}
		return string;
	}
}

//New Approach
//@JsonIgnore
//public BufferedImage getBufferedImage() {
////	logger.info("getBufferedImage() this.bufferedImage="+this.bufferedImage);
////	if(this.bufferedImage != null) {
////		BufferedImage before = this.bufferedImage;
////		int w = before.getWidth();
////		int h = before.getHeight();
////		BufferedImage after = new BufferedImage((int) (w * this.position.relativeScale),
////				(int) (h * this.position.relativeScale), BufferedImage.TYPE_INT_ARGB);
////		AffineTransform at = new AffineTransform();
////		at.scale(this.position.relativeScale, this.position.relativeScale);
////		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
////		after = scaleOp.filter(before, after);
////		this.bufferedImage = after;
////		this.position.setAbsoluteDimension(new Dimension(this.bufferedImage.getWidth(), this.bufferedImage.getHeight()));
////	}
//	return this.bufferedImage;
//}

//Original revert to this state
//@JsonIgnore
//public void setBufferedImage(BufferedImage bufferedImage) {
//	logger.debug("setBufferedImage(" + bufferedImage + ")");
//	this.bufferedImage = bufferedImage;
//	if (this.bufferedImage != null) {
//		this.position.absoluteDimension.width = this.bufferedImage.getWidth();
//		this.position.absoluteDimension.height = this.bufferedImage.getHeight();
//	}
//}