
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class NativeGraphic extends JFrame{

    private static final long serialVersionUID = 1L;
    final static Color bg = Color.white;
    final static Color fg = Color.black;
    String fileLocation, fileName;
    private int[][] arrayTraces; // Array con los datos de cada traza
    private double scale; // Escala aplicada a cada dato
    private int horizontalScale = 2; // Separacion de las trazas
    private String processName; // Nombre del proceso realizado
    int height = 20000; // Alto maximo permitido por java
    int width = 60000; // Ancho maximo permitido por java

    public NativeGraphic(int[][] arrayTraces, String processName, String fileLocation, String fileName, int bigger, int smaller) {
    	this.arrayTraces = arrayTraces;
        this.processName = processName;
        this.fileLocation = fileLocation;
        this.fileName = fileName;
        setBackground(bg);
        setForeground(fg);
        
        // Crea la escala para los datos de la grafica
        scale = (int) ((bigger - smaller) / 2.0);
        
        // Fija el alto de la grafica
    	if((arrayTraces[0].length)<20000){
    		height = (arrayTraces[0].length);
    	}
    	
    	// Fija el ancho de la grafica
    	if(arrayTraces.length*horizontalScale<=60000){
    		width = arrayTraces.length*horizontalScale;
    	}
        this.graphic();
    }

	public void graphic() {
        BufferedImage graphics = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D drawable = graphics.createGraphics();
        drawable.setBackground(Color.white);
        drawable.fillRect(0, 0, width, height);
        drawable.setColor(Color.black);
        int counter = 1;
        int traceCounter;
        for (int[] trace : arrayTraces) {
            traceCounter = 1;
            for (double element : trace) {
            	
            	// Grafica valores positivos
            	if(element/scale<10 && element/scale>0){
            		drawable.drawLine(counter*horizontalScale, (int)(traceCounter), (int) (counter*horizontalScale+element/scale),(int)(traceCounter));
            	}
            	
            	// Grafica valores negativos
            	if(element/scale>-10 && element/scale<0){
            		drawable.drawLine(counter*horizontalScale, (int)(traceCounter), (int) (counter*horizontalScale+element/scale),(int)(traceCounter));
            	}
                traceCounter++;
            }
            counter ++;
        }
        try {
        	
    		// Escribe el archivo JPG segun el nombre
        	if(processName.equals("kirchhoffMigration")){
        		ImageIO.write(graphics, "jpg", new File(fileLocation + "\\" + fileName + "-Migrado.jpg"));
        	}else{
        		ImageIO.write(graphics, "jpg", new File(fileLocation + "\\" + fileName + "-SinMigrar.jpg"));
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
