package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import agentes.Agente;

import javax.swing.JButton;
import javax.swing.JDialog;

import sistema.Ciudad;
import sistema.Punto;

public class VistaCiudad extends JFrame implements Observer{

	private JPanel contentPane;
	private GridLayout orden;
	private JButton [][]img;
	private JDialog ventana;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VistaCiudad frame = new VistaCiudad();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VistaCiudad() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		this.setSize(800, 600);

		try{
			int[] datos = {10,25,250,15};
			LinkedList<String> direcciones = new LinkedList<>();
			direcciones.add("192.168.2.10");
			direcciones.add("192.168.2.9");
			Ciudad ct = Ciudad.getInstance(datos, direcciones);
			//Shalalalalalalalala
			int dim=ct.obtenerDimension();
			double index, red, green; 
			orden = new GridLayout(dim,dim);
			img = new JButton [dim][dim];
			contentPane.setLayout(orden);
			for(int i=0;i<dim;++i){
				for(int j=0;j<dim;++j){
					img[i][j] = new JButton();
					img[i][j].addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								int x;
								x=Ciudad.getInstance(null,null).obtenerDimension();
								String[] columnas = {"Tipo", "Codigo"};
								for(int i=0;i<x;++i){
									for (int j=0; j<x;++j){
										if(e.getSource()==img[i][j]){
											int aux=0;
											
											ArrayList <Agente> personas =Ciudad.getInstance(null, null).obtenerAHabitantes(i, j);
											ventana = new JDialog();
											ventana.setSize(new Dimension(400,400));
											ventana.setVisible(true);
											ventana.setLocationRelativeTo(null);
											ventana.setTitle("Información habitantes");
											//BigInteger[] lista = new BigInteger[personas.size()];
											Object[][] datos = new Object[personas.size()][2];
											
											for (Agente key : personas){													
												//lista[aux]=key.obtenerIdentidad();
												datos[aux][0]=key.obtenerTipo();
												datos[aux][1]=key.obtenerIdentidad();
												++aux;
													
											}
											//JList<BigInteger> miLista = new JList<BigInteger>(lista);
											JTable tabla = new JTable (datos, columnas);
											ventana.setLayout(new BorderLayout());
											ventana.add(tabla.getTableHeader(), BorderLayout.PAGE_START);
											ventana.add(tabla, BorderLayout.CENTER);						
											ventana.add(tabla);
											}
									}
								}
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								//e1.printStackTrace();
							}
							

							
						}
					});
					contentPane.add(img[i][j]);
					index = ct.obtenerIndice(i, j);
					red=(1-index)*255;
					green=index*255;
					if (ct.obtenerIndice(i, j) > 0.5){
						img[i][j].setBackground(new Color((int)red*2,255,0));
						}else {
							img[i][j].setBackground(new Color(255,(int) green*2,0));
						}
					
				}
			}
			//Shalalalalalalalala
			Thread th_ct = new Thread(ct);
			th_ct.start();
			ct.addObserver(this);
		}catch(Exception ex){
			System.out.println("NO SE HA PODIDO INCIAR EL SISTEMA");
			ex.printStackTrace();
		}
		this.setLocationRelativeTo(null);
		this.setTitle("Información - Ciudad");
		contentPane.updateUI();
	}



	@Override
	public void update(Observable o, Object arg)	 {

		//System.out.println("Cambio en el modelo");

		Punto aux = (Punto)arg;
		int x=aux.obtenerX();
		int y=aux.obtenerY();
		double index, red, green;

		try {
			index = Ciudad.getInstance(null,null).obtenerIndice(x, y);
			red=(1-index)*255;
			green=index*255;
			if (index > 0.5){
				img[x][y].setBackground(new Color((int)red*2,255,0));
			}else {
				img[x][y].setBackground(new Color(255,(int) green*2,0));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch b lock
			e.printStackTrace();
		}

	}
}