
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.event.MouseWheelEvent;

public class MainScreen extends JComponent {
	private static final long serialVersionUID = 1L;
	static JPanel gui;
    static String imageName = "-SinMigrar.jpg";
    static String imageNameMigrate = "-Migrado.jpg";
    static String imageNameDefault = "images/blanco.jpg";
    static String titleNameDefault = "SegY Seismic Processor - Proyecto nuevo";
    static String titleName = "SegY Seismic Processor - Proyecto ";
    static File imageFile = new File(imageNameDefault);
    static JLabel imageCanvas;
    static Dimension size;
    static double scale = 0.5;
    static int PotitionScale = 50;
    private static BufferedImage image;
    private static int[][] data, migratedData;
    private static String fileLocation, fileName;

    public MainScreen() {
        checkImage();
    }

    // Se verifica que la imagen existe
    public static void checkImage(){
    	size = new Dimension(100, 100);       	
        try {
    		image = ImageIO.read(imageFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void setImage(Image image) {
    	imageCanvas.setIcon(new ImageIcon(image));
    }

    public static void initComponents() {
        if (gui == null) {
            gui = new JPanel(new BorderLayout());
            gui.setBorder(new EmptyBorder(5, 5, 5, 5));
            if(imageFile.exists() && image!=null){
	            imageCanvas = new JLabel();
	            JPanel imageCenter = new JPanel(new GridBagLayout());
	            
	            // Carga el listener para el movimiento del scroll del mouse
	            imageCenter.addMouseWheelListener(new MouseWheelListener() {
		        	public void mouseWheelMoved(MouseWheelEvent arg0) {
		        		if(arg0.getWheelRotation()==-1 && PotitionScale>0){
		        			
		        			// Aumenta la escala de la imagen
		        			PotitionScale = PotitionScale+5;
		        		}else{ 
		        			if(PotitionScale>10){
		        				
		        				//Reduce la escala de la imagen
			        			PotitionScale = PotitionScale-5;
		        			}
		        		}
		        		
		        		// Grafica la imagen segun la nueva escala 
	        			scale = PotitionScale / 100.0;
		                paintImage();
		        	}
		        });
	            imageCenter.add(imageCanvas);
	            JScrollPane imageScroll = new JScrollPane(imageCenter);
	            imageScroll.setPreferredSize(new Dimension(300, 100));
	            gui.add(imageScroll, BorderLayout.CENTER);
            }
        }
    }
    
    public Container getGui() {
        initComponents();
        return gui;
    }

    protected static void paintImage() {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        BufferedImage b = new BufferedImage(
                (int)(imageWidth*scale), 
                (int)(imageHeight*scale), 
                image.getType());
        Graphics2D g2 = b.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
        at.scale(scale, scale);
        g2.drawRenderedImage(image, at);
        setImage(b);
    }

    public Dimension getPreferredSize() {
        int w = (int) (scale * size.width);
        int h = (int) (scale * size.height);
        return new Dimension(w, h);
    }
    
    public static void main(String[] args) {
    	
    	// Inicia la pantalla principal
    	EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainScreen app = new MainScreen();
			        JFrame frame = new JFrame();
			        
			        frame.setTitle(titleNameDefault);
					frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				    Image icon = Toolkit.getDefaultToolkit().getImage("icon.png");
					frame.setIconImage(icon);
					
					JMenuBar menuBar = new JMenuBar();
					frame.setJMenuBar(menuBar);
					
					JMenu fileMenu = new JMenu("Archivo");
					menuBar.add(fileMenu);
					
					// Limpia la imagen y el titulo para un proyecto nuevo
					JMenuItem mntmNewProject = new JMenuItem("Nuevo Proyecto");
					fileMenu.add(mntmNewProject);
					mntmNewProject.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							try {
								image = ImageIO.read(new File(imageNameDefault));
							} catch (IOException e) {
								e.printStackTrace();
							}
							frame.setTitle(titleNameDefault);
							scale = 0.5;
							PotitionScale = 50;
							paintImage();
						}
					});
					
					JMenuItem mntmImport = new JMenuItem("Importar");
					fileMenu.add(mntmImport);
					mntmImport.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							
							//Importar archivo SegY
							JFileChooser fc = new JFileChooser();
							int returnVal = fc.showOpenDialog(null);

					        if (returnVal == JFileChooser.APPROVE_OPTION) {
					            File file = fc.getSelectedFile();
					            try {
					            	fileLocation = file.getParent();
					            	fileName = file.getName();
									data = new ReadIBM().readSegY(fileLocation, fileName);
									try {
										
										// Carga la nueva imagen
							    		image = ImageIO.read(new File(fileLocation+"\\"+fileName+imageName));
							    		frame.setTitle(titleName+fileLocation+"\\"+fileName);
							        } catch (Exception ex) {
							            ex.printStackTrace();
							        }
									scale = 0.5;
									paintImage();
								} catch (IOException e) {
									e.printStackTrace();
								};
					        } else {
					        	// cancelado por el usuario
					        }
						}
					});
					
					JMenuItem mntmExport = new JMenuItem("Exportar");
					fileMenu.add(mntmExport);
					mntmExport.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							try {
								if(Export.exportFile(migratedData, fileLocation, fileName)){
									// Exportación correcta
								};
							} catch (IOException e) {
								e.printStackTrace();
								//Se debe importar un archivo
							}
							
						}
					});
					
					fileMenu.add(new JSeparator());
					
					JMenuItem mntmExit = new JMenuItem("Salir");
					mntmExit.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							System.exit( 0 );
						}
					});
					fileMenu.add(mntmExit);
					
					JMenu process = new JMenu("Procesar");
					menuBar.add(process);
					
					JMenuItem mntmMigrate = new JMenuItem("Realizar migraci\u00F3n");
					process.add(mntmMigrate);
					mntmMigrate.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							try {
								// Llama al proceso de migracion
								migratedData = new KirchhoffMigration().readProperties(data, fileLocation, fileName);
								try {
									// Carga la nueva imagen
						    		image = ImageIO.read(new File(fileLocation+"\\"+fileName+imageNameMigrate));
						        } catch (Exception ex) {
						            ex.printStackTrace();
						        }
								paintImage();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
					
					JMenuItem mntmProperties = new JMenuItem("Propiedades");
					process.add(mntmProperties);
					mntmProperties.addActionListener(new ActionListener() {
						
						// Abre la pantalla de propiedades
						public void actionPerformed(ActionEvent arg0) {
							Properties.openProperties();
						}
					});
					
			        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			        frame.setContentPane(app.getGui());
			        MainScreen.setImage(MainScreen.image);

			        JLabel lblTitle = new JLabel(" ");
					frame.getContentPane().add(lblTitle, BorderLayout.SOUTH);
			        
			        frame.setSize(700, 500);
			        frame.setLocation(200, 200);
			        frame.setVisible(true);
			        
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
        
    }
}