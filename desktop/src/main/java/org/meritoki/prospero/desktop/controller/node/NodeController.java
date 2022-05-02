/*
 * Copyright 2022 Joaquin Osvaldo Rodriguez
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
package org.meritoki.prospero.desktop.controller.node;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.meritoki.prospero.library.model.document.Document;

/**
 *
 * @author osvaldo.rodriguez
 */
public class NodeController extends com.meritoki.library.controller.node.NodeController {

	static org.apache.logging.log4j.Logger logger = LogManager.getLogger(NodeController.class.getName());

	public static String getSystemHome() {
		return getUserHome() + getSeperator() + ".prospero";
	}

	public static String getDocumentCache() {
		return getSystemHome() + getSeperator() + "document";
	}

	public static String getDocumentCache(String uuid) {
		return getDocumentCache() + getSeperator() + uuid;
	}
	
	public static String getResourceCache() {
		return getSystemHome() + getSeperator() + "resource";
	}
	
	public static void saveDocument(String filePath, String fileName, Document document) {
		logger.info("saveDocument(" + filePath + ", " + fileName + ", " + document + ")");
		NodeController.saveJson(filePath, fileName, document);
	}

	public static void saveDocument(File file, Document document) {
		logger.info("saveDocument(" + file + ", " + document + ")");
		NodeController.saveJson(file, document);
	}

	public static Document openDocument(String filePath, String fileName) {
		Document document = (Document) NodeController.openJson(new java.io.File(filePath + "/" + fileName),
				Document.class);
		logger.info("openDocument(" + filePath + ", " + fileName + ") document=" + document);
		return document;
	}

	public static Document openDocument(File file) {
		Document document = (Document) NodeController.openJson(file, Document.class);
		logger.info("openDocument(" + file + ") document=" + document);
		return document;
	}
	
	public static double DPI = 300;
	public static double INCH_2_CM = 2.54;
	
	public static void savePanel(JPanel panel, String path, String name) {
		savePanel(panel, path, name, "png");
	}
	
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

	private static void savePanel(JPanel panel, String path, String name, String extension) {
		File directory = new File(path);
		if(!directory.exists()) {
			directory.mkdirs();
		}
		File file = new File(path+File.separatorChar+name+"."+extension);
		try {
			BufferedImage bufferedImage = new Robot().createScreenCapture(panel.getBounds());
			Graphics2D graphics2D = bufferedImage.createGraphics();
			panel.paint(graphics2D);
			for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(extension); iw.hasNext();) {
				ImageWriter writer = iw.next();
				ImageWriteParam writeParam = writer.getDefaultWriteParam();
				ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier
						.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
				IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
				if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
					continue;
				}
				setDPI(metadata);
				final ImageOutputStream stream = ImageIO.createImageOutputStream(file);
				if(stream != null) {
				try {
					writer.setOutput(stream);
					writer.write(metadata, new IIOImage(bufferedImage, null, metadata), writeParam);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					stream.close();
				}
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {

		// for PMG, it's dots per millimeter
		double dotsPerMilli = 1.0 * DPI / 10 / INCH_2_CM;

		IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
		horiz.setAttribute("value", Double.toString(dotsPerMilli));

		IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
		vert.setAttribute("value", Double.toString(dotsPerMilli));

		IIOMetadataNode dim = new IIOMetadataNode("Dimension");
		dim.appendChild(horiz);
		dim.appendChild(vert);

		IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
		root.appendChild(dim);

		metadata.mergeTree("javax_imageio_1.0", root);
	}
}
//public static void savePanel(JPanel panel, String filePath, String fileName) {
//File path = new File(filePath);
//if (!path.exists()) {
//	path.mkdirs();
//}
//BufferedImage bufferedImage = null;
//try {
//	bufferedImage = new Robot().createScreenCapture(panel.getBounds());
//	Graphics2D graphics2D = bufferedImage.createGraphics();
//	panel.paint(graphics2D);
//	try {
//		ImageIO.write(bufferedImage, "jpeg", new File(filePath + getSeperator() + fileName + ".jpeg"));
//	} catch (Exception e) {
//		logger.error(e.getMessage());
//	}
//} catch (AWTException e) {
//	logger.error(e.getMessage());
//}
//}
//public static void savePanel(JPanel panel, String path, String name) {
//File directory = new File(path);
//if(!directory.exists()) {
//	directory.mkdirs();
//}
//File file = new File(path+File.separatorChar+name);
//savePanel(panel,file);
//}
//
//public static void savePanel(JPanel panel, File file) {
//BufferedImage bufferedImage=null;
//try {
//    bufferedImage = new Robot().createScreenCapture(panel.getBounds());
//    Graphics2D graphics2D = bufferedImage.createGraphics();
//    panel.paint(graphics2D);
//    try {
//       ImageIO.write(bufferedImage,"jpeg", new File(filePath+getSeperator()+fileName+".jpeg"));
//   } catch (Exception e) {
//       logger.error(e.getMessage());
//   }
//} catch (AWTException e) {
//	logger.error(e.getMessage());
//}  
//}
//