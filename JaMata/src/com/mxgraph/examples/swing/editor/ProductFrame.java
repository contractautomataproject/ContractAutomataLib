package com.mxgraph.examples.swing.editor;
import javax.swing.*;
import javax.swing.border.LineBorder;

import FMCA.Family;
import FMCA.Product;

import java.awt.*;
import java.awt.geom.*;

public class ProductFrame extends JDialog{
	private Family fam;
	JLabel[] nodes;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProductFrame(Family f, JPanel frame){
		this.fam=f;
        JPanel panel=new JPanel();
        this.setLocationRelativeTo(frame);
        this.setSize(800,800);
        setTitle("Family Products");
        //panel.setLayout(null);
        panel.setAutoscrolls(true);
        Product[] prod = f.getProducts();
        int[][] depth = f.getDepth();
        JScrollPane scrollpanel= new JScrollPane(panel,
      		   ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,  
      		   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
      
         scrollpanel.setPreferredSize(new Dimension( 600,600));
         scrollpanel.setAutoscrolls(true);
         
        nodes = new JLabel[prod.length];
        
        for (int i=0;i<depth.length;i++)
        {
        	for (int j=0;j<depth[i].length;j++)
        	{
        		nodes[depth[i][j]] = new JLabel();
        		nodes[depth[i][j]].setText(prod[depth[i][j]].toString());
        		int width=prod[depth[i][j]].getForbiddenAndRequiredNumber()*60;
        		nodes[depth[i][j]].setBounds(10+j*width, 10+i*50, width, 50);
        		//nodes[depth[i][j]].setHorizontalAlignment(10+j*20);
        		//nodes[depth[i][j]].setVerticalAlignment(10+i*60);
        		//nodes[depth[i][j]].setFont(new Font("Verdana",1,8));
                panel.add(nodes[depth[i][j]]);
        	}
        }
             
        panel.setBorder(new LineBorder(Color.BLACK));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
     
        getContentPane().add(scrollpanel);
        
        setVisible(true);
	  }

	public Family getFamily()
	{
		return fam;
	}
    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        Graphics2D g2 = (Graphics2D) g;
        Product[] prod = fam.getProducts();
        int[][] depth = fam.getDepth();
        int[] ptl=fam.getPointerToLevel();
        int[][] po=fam.getPartialOrder();
        
        for (int i=0;i<po.length;i++)
        {
        	for (int j=0;j<po[i].length;j++)
        	{
        		if (po[i][j]==1)
        		{
        			Line2D lin = new Line2D.Float(
	                	nodes[depth[prod[i].getForbiddenAndRequiredNumber()][ptl[i]]].getX()+25,
		        		nodes[depth[prod[i].getForbiddenAndRequiredNumber()][ptl[i]]].getY()+50,
		        		nodes[depth[prod[j].getForbiddenAndRequiredNumber()][ptl[j]]].getX()+25,
		        		nodes[depth[prod[j].getForbiddenAndRequiredNumber()][ptl[j]]].getY()+60
	        		);
	        		g2.draw(lin);
        		}
        	}
        }
    }   
}      
        
/*        JButton button =new JButton("press");
        panel.add(button);*/
        
//        final JFrame parent = new JFrame();
//        JButton button = new JButton();
//
//        button.setText("The products loaded are:\n"+fam.toString());
//        parent.add(button);
//        parent.pack();
//        parent.setVisible(true);
//
//        button.addActionListener(new java.awt.event.ActionListener() {
//            @Override
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                String name = JOptionPane.showInputDialog(parent,
//                        "What is your name?", null);
//            }
//        });
//        
//		JOptionPane.showMessageDialog(
//				editor.getGraphComponent(),
//				"The products loaded are:\n"+fam.toString(),
//				mxResources.get("earth"),
//				JOptionPane.INFORMATION_MESSAGE);

  

