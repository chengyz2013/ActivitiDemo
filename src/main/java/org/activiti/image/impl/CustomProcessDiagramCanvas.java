package org.activiti.image.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.activiti.image.util.ReflectUtil;

public class CustomProcessDiagramCanvas extends DefaultProcessDiagramCanvas {

	public CustomProcessDiagramCanvas(int width, int height, int minX, int minY, String imageType) {
		super(width, height, minX, minY, imageType);
		// TODO Auto-generated constructor stub
	}
	
	public CustomProcessDiagramCanvas(int width, int height, int minX, int minY, String imageType, 
      String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader) {
		super(width, height, minX, minY, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader);
	}

	public void initialize(String imageType) {
	    if ("png".equalsIgnoreCase(imageType)) {
	      this.processDiagram = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
	    } else {
	      this.processDiagram = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
	    }
	    
	    this.g = processDiagram.createGraphics();
	    if ("png".equalsIgnoreCase(imageType) == false) {
	      this.g.setBackground(new Color(255, 255, 255, 0));
	      this.g.clearRect(0, 0, canvasWidth, canvasHeight);
	    }

	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g.setPaint(Color.black);
	    
	    Font font = new Font(activityFontName, Font.BOLD, FONT_SIZE);
	    g.setFont(font);
	    this.fontMetrics = g.getFontMetrics();

	    LABEL_FONT = new Font(labelFontName, Font.BOLD, 14);//add by me
	    ANNOTATION_FONT = new Font(annotationFontName, Font.PLAIN, FONT_SIZE);
	    
	    try {
	      USERTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/userTask.png", customClassLoader));
	      SCRIPTTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/scriptTask.png", customClassLoader));
	      SERVICETASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/serviceTask.png", customClassLoader));
	      RECEIVETASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/receiveTask.png", customClassLoader));
	      SENDTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/sendTask.png", customClassLoader));
	      MANUALTASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/manualTask.png", customClassLoader));
	      BUSINESS_RULE_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/businessRuleTask.png", customClassLoader));
	      SHELL_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/shellTask.png", customClassLoader));
	      CAMEL_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/camelTask.png", customClassLoader));
	      MULE_TASK_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/muleTask.png", customClassLoader));
	      
	      TIMER_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/timer.png", customClassLoader));
	      COMPENSATE_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/compensate-throw.png", customClassLoader));
	      COMPENSATE_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/compensate.png", customClassLoader));
	      ERROR_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/error-throw.png", customClassLoader));
	      ERROR_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/error.png", customClassLoader));
	      MESSAGE_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/message-throw.png", customClassLoader));
	      MESSAGE_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/message.png", customClassLoader));
	      SIGNAL_THROW_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/signal-throw.png", customClassLoader));
	      SIGNAL_CATCH_IMAGE = ImageIO.read(ReflectUtil.getResource("org/activiti/icons/signal.png", customClassLoader));
	    } catch (IOException e) {
	      LOGGER.warn("Could not load image for process diagram creation: {}", e.getMessage());
	    }
	  }

}
