import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import java.awt.Window.Type;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.awt.event.ActionEvent;

public class Properties {

	private JFrame frmProperties;
	private JTextField textVelhalf, textTo, textDx, textDt, textNt, textNx;
	
	public static void openProperties(){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Properties window = new Properties();
					window.frmProperties.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Properties() throws IOException {
		
		initialize();

		try(BufferedReader br = new BufferedReader(new FileReader("properties.conf"))) {
			
			// Lee el contenido del archivo de propiedades
		    StringBuilder sb = new StringBuilder();
		    String velhalf, to, dx, dt, nt, nx;
	        sb.append(System.lineSeparator());
	        velhalf = br.readLine();
	        to = br.readLine();
	        dx = br.readLine();
	        dt = br.readLine();
	        nt = br.readLine();
	        nx = br.readLine();
	        textVelhalf.setText(velhalf);
			textTo.setText(to);
			textDx.setText(dx);
			textDt.setText(dt);
			textNt.setText(nt);
			textNx.setText(nx);
	    
		}catch (FileNotFoundException  e) {
			
			// Crea propiedades predeterminadas en caso de no existir el archivo
			textVelhalf.setText("0.1");
			textTo.setText("1.0");
			textDx.setText(".04");
			textDt.setText(".04");
			textNt.setText("0");
			textNx.setText("0");
		}
	}

	// Inicia la carga de la pantalla de propiedades
	private void initialize() {
		frmProperties = new JFrame();
		frmProperties.setResizable(false);
		frmProperties.setAlwaysOnTop(true);
		frmProperties.setTitle("Propiedades");
		frmProperties.setType(Type.POPUP);
		frmProperties.setSize(150, 380);
		frmProperties.setLocationRelativeTo ( null );
		
		JLabel lblProperties = new JLabel("PROPIEDADES\r\n");
		lblProperties.setToolTipText("\r\n");
		lblProperties.setHorizontalAlignment(SwingConstants.CENTER);
		lblProperties.setFont(new Font("Tahoma", Font.BOLD, 12));
		frmProperties.getContentPane().add(lblProperties, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		frmProperties.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 10));
		
		JLabel lblVelhalf = new JLabel("Velocidad media:");
		panel.add(lblVelhalf);
		
		textVelhalf = new JTextField();
		textVelhalf.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(textVelhalf);
		textVelhalf.setColumns(5);
		
		JLabel lblInicialTime = new JLabel("Tiempo inicial:");
		panel.add(lblInicialTime);
		
		textTo = new JTextField();
		textTo.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(textTo);
		textTo.setColumns(5);
		
		JLabel lblDx = new JLabel("dx:");
		panel.add(lblDx);
		
		textDx = new JTextField();
		textDx.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(textDx);
		textDx.setColumns(5);
		
		JLabel lblDt = new JLabel("dt:");
		panel.add(lblDt);
		
		textDt = new JTextField();
		textDt.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(textDt);
		textDt.setColumns(5);
		
		JLabel lblNt = new JLabel("Trazas:");
		panel.add(lblNt);
		
		textNt = new JTextField();
		textNt.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(textNt);
		textNt.setColumns(5);
		
		textNx = new JTextField();
		textNx.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(textNx);
		textNx.setColumns(5);
		textNx.setVisible(false);
		
		JButton btnSave = new JButton("Guardar");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PrintWriter writer;
				try {
					writer = new PrintWriter("properties.conf", "UTF-8");
				    writer.println(textVelhalf.getText());
				    writer.println(textTo.getText());
				    writer.println(textDx.getText());
				    writer.println(textDt.getText());
				    writer.println(textNt.getText());
				    writer.println(textNx.getText());
				    writer.close();
				    frmProperties.dispose();
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
		panel.add(btnSave);

		 
	}

}
