package com.mxgraph.examples.swing.editor;
import java.awt.Color;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

import FMCA.Family;
import FMCA.Product;

/**
 * 
 * @author Davide Basile
 *
 */
public class ProductFrame extends JFrame{
	private Family fam;
	JButton[] nodes;
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
        panel.setAutoscrolls(true);
        Product[] prod = f.getProducts();
        int[][] depth = f.getDepth();
        JScrollPane scrollpanel= new JScrollPane(panel,
      		   ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,  
      		   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
      
         scrollpanel.setPreferredSize(new Dimension(600,600));
         scrollpanel.setAutoscrolls(true);
         
        int rows = depth.length;
        int[] deleng=new int[depth.length];
        for (int i=0;i<rows;i++)
        	deleng[i]=depth[i].length;
        
        int columns = Collections.max(Arrays.stream(deleng).boxed().collect(Collectors.toList()));  // FMCAUtil.max(deleng);
        GridLayout gl = new GridLayout(rows,columns);
        panel.setLayout(gl);
        gl.setHgap(10);
        gl.setVgap(10);
        nodes = new JButton[prod.length];
        
        for (int i=depth.length-1;i>=0;i--)
        {
        	if (depth[i]==null)
        	{
        		for (int j=0;j<columns;j++)
        		{
        			panel.add(new JLabel());
        		}
        	}
        	else for (int j=0;j<columns;j++)
        	{
        		if(j<depth[i].length)
        		{
	        		nodes[depth[i][j]] = new JButton();
	        		nodes[depth[i][j]].setBorder(BorderFactory.createLineBorder(Color.black));
	        		nodes[depth[i][j]].setBorderPainted(false);
	        		nodes[depth[i][j]].setText(prod[depth[i][j]].toHTMLString("P"+depth[i][j]+" "));
	        		nodes[depth[i][j]].putClientProperty("i", i+"");
	        		nodes[depth[i][j]].putClientProperty("j", j+"");
	        		nodes[depth[i][j]].setSize(new Dimension(200,300));
	        		nodes[depth[i][j]].addActionListener(new ActionListener(){
	        	           	@Override
							public void actionPerformed(ActionEvent ae) 
	        	           	{
	        	           		JButton source = (JButton)ae.getSource();
	        	           		String si=(String)source.getClientProperty("i");
        	           		    int i = Integer.parseInt(si);
	        	           		String sj=(String)source.getClientProperty("j");
	        	                int j = Integer.parseInt(sj);
//	        	                int[] ptl=fam.getPointerToLevel();
//	        	                int[][] rpo=fam.getReversePO();
	        	                int[][] depth=fam.getDepth();
	        	                Product[] prod = fam.getProducts();
	        	                int poindex=depth[i][j];
	        	                String message="Subproducts of P"+poindex+" "+prod[poindex].toString()+" \n";
	        	                int[] subind = fam.getSubProductsofProduct(poindex);
	        	                Product[] subprod = fam.subsetOfProductsFromIndex(subind);
	        	                for (int ind=0;ind<subprod.length;ind++)
	        	                {
	        	                	message+="P"+subind[ind]+" "+subprod[ind].toString()+"\n";
        	                		if (!source.isBorderPainted())
    	        	           		{
        	                			try{
        	                			nodes[subind[ind]].setBorder(BorderFactory.createLineBorder(Color.red));
        	                			nodes[subind[ind]].setBorderPainted(true);
        	                			} catch (Exception e){
        	                				//debug
        	                		//		int d=prod[ind].getForbiddenAndRequiredNumber();
        	                			   //int dd=ptl[ind];
        	                			   //int ddd=depth[prod[ind].getForbiddenAndRequiredNumber()][ptl[ind]]; 
        	                		//	   int x=0;
        	                			   //System.out.println(d+dd+ddd+x);
        	                			   e.printStackTrace();
        	                			}
        	                		}
        	                		else
        	                		{
        	                			nodes[subind[ind]].setBorder(BorderFactory.createLineBorder(Color.black));
        	                			nodes[subind[ind]].setBorderPainted(false);
        	                		}
	        	                }
	        	                if (!source.isBorderPainted())
	        	                {
	        	                	source.setBorderPainted(true);
	        	                	source.setBorder(BorderFactory.createLineBorder(Color.red));
	        	                	JTextArea textArea = new JTextArea(200,200);
	        						textArea.setText(message);
	        					    textArea.setEditable(true);
	        					      
	        					    JScrollPane scrollPane = new JScrollPane(textArea);
	        					    JDialog jd = new JDialog();
	        						jd.add(scrollPane);
	        						jd.setTitle("Sub-Products");
	        						jd.setResizable(true);
	        						jd.setVisible(true);

	        						jd.setSize(500,500);
	        						jd.setLocationRelativeTo(null);

		        	                //JOptionPane.showMessageDialog(null, message);
	        	                }
	        	                else{
	        	                	source.setBorderPainted(false);
	        	                	source.setBorder(BorderFactory.createLineBorder(Color.black));	        	                
	        	                }
							}
	        	                
	        	        });
	        		//int width=prod[depth[i][j]].getForbiddenAndRequiredNumber()*60;
	        		//nodes[depth[i][j]].setBounds(10+j*width, 10+i*50, width, 50);
	        		
	        		//nodes[depth[i][j]].setHorizontalAlignment(10+j*20);
	        		//nodes[depth[i][j]].setVerticalAlignment(10+i*60);
	        		//nodes[depth[i][j]].setFont(new Font("Verdana",1,8));
	                panel.add(nodes[depth[i][j]]);
	        	}
        		else
        			panel.add(new JLabel());
        	}
        }
             
        panel.setBorder(new LineBorder(Color.BLACK));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        getContentPane().add(scrollpanel);
        this.pack();
        setVisible(true);
	}
	
	public void setColorButtonProducts(int[] prod, Color c)
	{
		for (int i=0;i<prod.length;i++)
		{
			nodes[prod[i]].setBorder(BorderFactory.createLineBorder(c));
			nodes[prod[i]].setBorderPainted(true);
		}
	}
	
	public void resetColorButtonProducts()
	{
		for (int i=0;i<nodes.length;i++)
		{
			nodes[i].setBorder(BorderFactory.createLineBorder(Color.black));
			nodes[i].setBorderPainted(false);
		}
	}
	
	public Family getFamily()
	{
		return fam;
	}
    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        /*Graphics2D g2 = (Graphics2D) g;
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
        }*/
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

  

