package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import sistema.Ciudad;

public class VistaCiudad extends JFrame implements Observer{

	private JPanel contentPane;

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
		try{
			int[] datos = {5,5,5};
			LinkedList<String> direcciones = new LinkedList<>();
			direcciones.add("192.168.2.10");
			Ciudad ct = Ciudad.getInstance(datos, direcciones);
			Thread th_ct = new Thread(ct);
			th_ct.start();
			ct.addObserver(this);
		}catch(Exception ex){
			System.out.println("NO SE HA PODIDO INCIAR EL SISTEMA");
			ex.printStackTrace();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		System.out.println("Cambio en el modelo");
	}
	
	

}
