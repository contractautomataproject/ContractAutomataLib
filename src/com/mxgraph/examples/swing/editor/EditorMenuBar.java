package com.mxgraph.examples.swing.editor;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.mxgraph.examples.swing.editor.EditorActions.ExitAction;
import com.mxgraph.examples.swing.editor.EditorActions.HistoryAction;
import com.mxgraph.examples.swing.editor.EditorActions.NewAction;
import com.mxgraph.examples.swing.editor.EditorActions.OpenAction;
import com.mxgraph.examples.swing.editor.EditorActions.PageSetupAction;
import com.mxgraph.examples.swing.editor.EditorActions.PrintAction;
import com.mxgraph.examples.swing.editor.EditorActions.SaveAction;
import com.mxgraph.examples.swing.editor.EditorActions.ScaleAction;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;

import FMCA.FMCA;
import FMCA.FMCATGUI;
import FMCA.Family;
import FMCA.Product;
import MSCA.MSCA;
import MSCA.MSCAIO;


public class EditorMenuBar extends JMenuBar 
{
	String lastDir;

	
	final String errorMsg = "States or labels contain errors.\n "
			+ 		"Please, check that each state has following format:\n"
			+		"[INTEGER, ..., INTEGER]\n" 
			+		"and  each label has the following format:\n"
			+		"[(TYPE)STRING, ...,(TYPE)STRING]\n where (TYPE) is either ! or ?";

	Predicate<FMCATGUI> loseChanges = x->((x != null)&&(!x.isModified()
			|| JOptionPane.showConfirmDialog(x,	mxResources.get("loseChanges")) == JOptionPane.YES_OPTION));

	private static final long serialVersionUID = 4060203894740766714L;

	/**
	 * @param editor
	 */
	public EditorMenuBar(final FMCATGUI editor)
	{
		final mxGraph graphfinal = editor.getGraphComponent().getGraph();

		graphfinal.setDisconnectOnMove(false);
		graphfinal.setEdgeLabelsMovable(false);
		//graph.setAllowDanglingEdges(false);
		graphfinal.setAllowLoops(true);
		graphfinal.setCellsResizable(false);
		graphfinal.setCellStyles("width","50.0");
		graphfinal.setCellStyles("heigth","50.0");


		// Creates the file menu
		JMenu menu = add(new JMenu(mxResources.get("file")));

		menu.add(editor.bind(mxResources.get("new"), new NewAction(), "/com/mxgraph/examples/swing/images/new.gif"));
		menu.add(editor.bind(mxResources.get("openFile"), new OpenAction(), "/com/mxgraph/examples/swing/images/open.gif"));
		//menu.add(editor.bind(mxResources.get("importStencil"), new ImportAction(), "/com/mxgraph/examples/swing/images/open.gif"));

		menu.addSeparator();


		//menu.add(editor.bind("Import Automaton", new ImportAction(), "/com/mxgraph/examples/swing/images/save.gif"));
		menu.add(editor.bind(mxResources.get("save"), new SaveAction(false), "/com/mxgraph/examples/swing/images/save.gif"));
		menu.add(editor.bind(mxResources.get("saveAs"), new SaveAction(true), "/com/mxgraph/examples/swing/images/saveas.gif"));

		menu.addSeparator();

		JMenuItem item = menu.add(new JMenuItem("Import .data"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			if (!loseChanges.test(editor)) return;

			mxGraph graph = editor.getGraphComponent().getGraph();
			if (graph == null) return;

			JFileChooser fc = new JFileChooser(
					(editor.getCurrentFile()!=null)?editor.getCurrentFile().getParent(): System.getProperty("user.dir"));

			// Adds file filter for supported file format
			setDefaultFilter(fc,".data","FMCA description",null);

			int rc = fc.showDialog(null,
					mxResources.get("openFile"));
			if (rc == JFileChooser.APPROVE_OPTION)
			{
				lastDir = fc.getSelectedFile().getParent();	
				MSCA aut=MSCAIO.load(fc.getSelectedFile().toString());
				File file=MSCAIO.convertMSCAintoXML(fc.getSelectedFile().toString(),aut);
				editor.lastaut=aut;
				loadMorphStore(file.getName(), editor, file);
			}
		});

		item = menu.add(new JMenuItem("Export .data"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			String filename =editor.getCurrentFile().getAbsolutePath();
			MSCA aut=editor.lastaut;
//			try {
//				aut = MSCAIO.parseXMLintoMSCA(filename);
//				editor.lastaut=aut;
//			} catch (ParserConfigurationException|SAXException|IOException e1) {
//				JOptionPane.showMessageDialog(editor.getGraphComponent(),e1.getMessage()+"\n"+errorMsg,mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
//				return;
//			}

			try {
				MSCAIO.printToFile(filename,aut);
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"The automaton has been stored with filename "+filename+".data","Success!",JOptionPane.PLAIN_MESSAGE);
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"File not found"+e1.toString(),mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			}	


		});

		menu.addSeparator();
		menu.add(editor.bind(mxResources.get("pageSetup"), new PageSetupAction(), "/com/mxgraph/examples/swing/images/pagesetup.gif"));
		menu.add(editor.bind(mxResources.get("print"), new PrintAction(), "/com/mxgraph/examples/swing/images/print.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("exit"), new ExitAction()));

		// Creates the edit menu
		menu = add(new JMenu(mxResources.get("edit")));

		menu.add(editor.bind(mxResources.get("undo"), new HistoryAction(true), "/com/mxgraph/examples/swing/images/undo.gif"));
		menu.add(editor.bind(mxResources.get("redo"), new HistoryAction(false), "/com/mxgraph/examples/swing/images/redo.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("cut"), TransferHandler.getCutAction(), "/com/mxgraph/examples/swing/images/cut.gif"));
		menu.add(editor.bind(mxResources.get("copy"), TransferHandler.getCopyAction(), "/com/mxgraph/examples/swing/images/copy.gif"));
		menu.add(editor.bind(mxResources.get("paste"), TransferHandler.getPasteAction(), "/com/mxgraph/examples/swing/images/paste.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("delete"), mxGraphActions.getDeleteAction(), "/com/mxgraph/examples/swing/images/delete.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("selectAll"), mxGraphActions.getSelectAllAction()));
		menu.add(editor.bind(mxResources.get("selectNone"), mxGraphActions.getSelectNoneAction()));

		menu.addSeparator();

		item = menu.add(new JMenuItem("Add handles to edges"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			if (checkAut(editor)) return;

			lastDir=editor.getCurrentFile().getParent();
			String absfilename =editor.getCurrentFile().getAbsolutePath();
			MSCA aut=editor.lastaut;
			File file = MSCAIO.convertMSCAintoXML(absfilename,aut);
			parseAndSet(absfilename, editor,file);
		});



		//menu.add(editor.bind(mxResources.get("warning"), new WarningAction()));
		//menu.add(editor.bind(mxResources.get("edit"), mxGraphActions.getEditAction()));

		// Creates the view menu
		menu = add(new JMenu(mxResources.get("view")));


		JMenu submenu = (JMenu) menu.add(new JMenu(mxResources.get("zoom")));

		submenu.add(editor.bind("400%", new ScaleAction(4)));
		submenu.add(editor.bind("200%", new ScaleAction(2)));
		submenu.add(editor.bind("150%", new ScaleAction(1.5)));
		submenu.add(editor.bind("100%", new ScaleAction(1)));
		submenu.add(editor.bind("75%", new ScaleAction(0.75)));
		submenu.add(editor.bind("50%", new ScaleAction(0.5)));

		submenu.addSeparator();

		submenu.add(editor.bind(mxResources.get("custom"), new ScaleAction(0)));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("zoomIn"), mxGraphActions.getZoomInAction()));
		menu.add(editor.bind(mxResources.get("zoomOut"), mxGraphActions.getZoomOutAction()));


		menu = add(new JMenu("FMCA"));



		item = menu.add(new JMenuItem("Composition"));
		item.addActionListener(e->
		{
			if (!loseChanges.test(editor)) return;

			mxGraph graph = editor.getGraphComponent().getGraph();
			if (graph == null) return;
	
			JFileChooser fc = new JFileChooser(
					(editor.getCurrentFile()!=null)?editor.getCurrentFile().getParent(): System.getProperty("user.dir"));

			setDefaultFilter(fc,".mxe","FMCA Description",null);

			fc.setDialogTitle("Select an FMCA to be composed");

			List<MSCA> aut = new ArrayList<>(3);
			List<String> names= new ArrayList<>(3);

			boolean lastIteration=false;
			int rc = fc.showDialog(editor.getGraphComponent(),mxResources.get("openFile"));
			while (true)
			{
				lastDir = fc.getSelectedFile().getParent();
				try
				{
					String fileName =fc.getSelectedFile().toString();
					names.add(fileName.substring(fileName.lastIndexOf("\\")+1, fileName.indexOf(".")));
					aut.add(MSCAIO.parseXMLintoMSCA(fileName));
				}
				catch (ParserConfigurationException|SAXException|IOException e1) {
					JOptionPane.showMessageDialog(editor.getGraphComponent(),e1.getMessage()+"\n"+errorMsg,mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (lastIteration)
					break;

				rc = fc.showDialog(editor.getGraphComponent(),mxResources.get("openFile"));
				if (rc == JFileChooser.APPROVE_OPTION)
				{
					int reply=JOptionPane.showOptionDialog(editor.getGraphComponent(), 
							"", 
							"Composition", 
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							new String[]{"Compute Composition", "Load other automata"}, 
							"default");
					lastIteration=(reply != JOptionPane.NO_OPTION);
				}
				else
					lastIteration=true;
			}

			
			long start = System.currentTimeMillis();
			MSCA composition = (MSCA) MSCA.composition(aut,t->t.isRequest(),100);	
			long elapsedTime = System.currentTimeMillis() - start;

			if (composition==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"Error",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			editor.lastaut=composition;
			String compositionname="("+	names.stream().reduce((x,y)->x+"x"+y).orElse("")+")";

			File file = MSCAIO.convertMSCAintoXML(lastDir+"\\"+compositionname,composition);
			String message = "The composition has been stored with filename "+lastDir+"\\"+compositionname
					+"\n Elapsed time : "+elapsedTime + " milliseconds"
					+"\n Number of states : "+composition.getNumStates();
			;
			JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.PLAIN_MESSAGE);
			
			this.loadMorphStore(compositionname, editor, file);

		});


		menu.addSeparator();

		item = menu.add(new JMenuItem("Clear Family"));
		item.addActionListener(e->
		{
			if (!loseChanges.test(editor)) return; 

			mxGraph graph = editor.getGraphComponent().getGraph();
			if (graph==null) return;

			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
				return;

			editor.setProductFrame(null);
			pf.dispose();
		});

		item = menu.add(new JMenuItem("Reset Colors Family"));
		item.addActionListener(e->
		{
			if (!loseChanges.test(editor)) return;
			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
				return;
			pf.resetColorButtonProducts();
		});

		item = menu.add(new JMenuItem("Load Family"));
		item.addActionListener(e->
		{
			if (!loseChanges.test(editor)) return;

			mxGraph graph = editor.getGraphComponent().getGraph();
			if (graph == null) return;

			ProductFrame pf=editor.getProductFrame();
			if (pf!=null)
			{
				editor.setProductFrame(null);
				pf.dispose();
			}
			
			JFileChooser fc = new JFileChooser(
					(editor.getCurrentFile()!=null)?editor.getCurrentFile().getParent(): System.getProperty("user.dir"));


			setDefaultFilter(fc,".prod","Products list",null);

			int rc = fc.showDialog(null,mxResources.get("openFile"));

			if (rc == JFileChooser.APPROVE_OPTION)
			{
				lastDir = fc.getSelectedFile().getParent();
				String fileName =fc.getSelectedFile().toString();
				Family fam=new Family(fileName);
				pf= new ProductFrame(fam, (JPanel)editor);
				editor.setProductFrame(pf);
			}

		});


		item = menu.add(new JMenuItem("Save Family"));
		item.addActionListener(e->
		{
			if (editor != null)
			{
				mxGraphComponent graphComponent = editor.getGraphComponent();
				ProductFrame pf=editor.getProductFrame();
				if (pf==null)
				{
					JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
					return;
				}
				FileFilter selectedFilter = null;
				DefaultFileFilter prodFilter = new DefaultFileFilter(".prod","Family");
			
				JFileChooser fc = new JFileChooser(
						(lastDir != null)?lastDir:(editor.getCurrentFile()!=null)?editor.getCurrentFile().getParent():
							System.getProperty("user.dir"));

				// Adds the default file format
				FileFilter defaultFilter = prodFilter;
				fc.addChoosableFileFilter(defaultFilter);

				int rc = fc.showDialog(null, mxResources.get("save"));
				if (rc != JFileChooser.APPROVE_OPTION)
					return;

				lastDir = fc.getSelectedFile().getParent();
				String filename = fc.getSelectedFile().getAbsolutePath();
				selectedFilter = fc.getFileFilter();

				if (selectedFilter instanceof DefaultFileFilter)
				{
					String ext = ((DefaultFileFilter) selectedFilter)
							.getExtension();

					if (!filename.toLowerCase().endsWith(ext))
						filename += ext;
				}

				if (new File(filename).exists()
						&& JOptionPane.showConfirmDialog(graphComponent,
								mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION)
					return;
				

				try
				{
					PrintWriter pr;
					pr = new PrintWriter(filename);
					Product[] p=pf.getFamily().getProducts();
					for (int i=0;i<p.length;i++)
						pr.println(p[i].toStringFile(i));
					pr.close();			
				}
				catch (Throwable ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(graphComponent,
							ex.toString(), mxResources.get("error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		item = menu.add(new JMenuItem("Import FeatureIDE Model"));
		item.addActionListener(e->
		{
			if (!loseChanges.test(editor)) return;

			mxGraph graph = editor.getGraphComponent().getGraph();
			if (graph == null) return;

			ProductFrame pf=editor.getProductFrame();
			if (pf!=null)
			{
				editor.setProductFrame(null);
				pf.dispose();
			}

			JFileChooser fc = new JFileChooser(
					(editor.getCurrentFile()!=null)?editor.getCurrentFile().getParent(): System.getProperty("user.dir"));

			// Adds file filter for supported file format
			setDefaultFilter(fc,".xml"," Feature Model ", ".cfr.data");

			int rc = fc.showDialog(null,
					mxResources.get("openFile"));

			if (rc == JFileChooser.APPROVE_OPTION)
			{
				lastDir = fc.getSelectedFile().getParent();
				try
				{
					String fileName =fc.getSelectedFile().toString();
					Product[] pr=Family.importFamily(fc.getSelectedFile().getPath(),fileName);
					Family fam=new Family(pr);
					pf= new ProductFrame(fam, (JPanel)editor);
					editor.setProductFrame(pf);
					pf.setExtendedState(JFrame.MAXIMIZED_BOTH); 
					//pf.setAlwaysOnTop(true);
					pf.setLocation(editor.getX() + editor.getWidth(), editor.getY());
					pf.setVisible(true);

				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(
							editor.getGraphComponent(),
							ex.toString(),
							mxResources.get("error"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		menu.addSeparator();

		item = menu.add(new JMenuItem("Maximal Products"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			Family fam= pf.getFamily();

			long start = System.currentTimeMillis();
			int[] pid = fam.getMaximalProducts();
			long elapsedTime = System.currentTimeMillis() - start;
			Product[] cp=fam.subsetOfProductsFromIndex(pid);
			if (cp==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Maximal Products",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			pf.setColorButtonProducts(pid, Color.GREEN);
			String message=cp.length + " Maximal Products Found:\n";
			for (int i=0;i<cp.length;i++)
				message+= pid[i]+" : \n"+cp[i].toString()+"\n";

			message += "Elapsed time : "+elapsedTime+ "milliseconds";

			JTextArea textArea = new JTextArea(200,200);
			textArea.setText(message);
			textArea.setEditable(true);

			JScrollPane scrollPane = new JScrollPane(textArea);
			JDialog jd = new JDialog(pf);
			jd.add(scrollPane);
			jd.setTitle("Maximal Products");
			jd.setResizable(true);
			jd.setVisible(true);
			jd.setSize(500,500);
			jd.setLocationRelativeTo(null);

		});

		item = menu.add(new JMenuItem("Valid Products"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{

			if (checkAut(editor)) return;

			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			lastDir=editor.getCurrentFile().getParent();

			MSCA aut=editor.lastaut;

			long start = System.currentTimeMillis();
			int[] vp= pf.getFamily().validProducts(aut);
			long elapsedTime= System.currentTimeMillis() - start;

			Product[] vpp=pf.getFamily().subsetOfProductsFromIndex(vp);

			if (vp==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Valid Products",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			pf.setColorButtonProducts(vp, Color.BLUE);
			String message=vp.length + " Valid Products Found:\n";

			for (int i=0;i<vp.length;i++)
				message+= vp[i]+" : \n"+vpp[i].toString()+"\n";

			message += "Elapsed Time " + elapsedTime + "milliseconds";
			JTextArea textArea = new JTextArea(200,200);
			textArea.setText(message);
			textArea.setEditable(true);

			JScrollPane scrollPane = new JScrollPane(textArea);
			JDialog jd = new JDialog(pf);
			jd.add(scrollPane);
			jd.setTitle("Valid Products");
			jd.setResizable(true);
			jd.setVisible(true);
			jd.setSize(500,500);
			jd.setLocationRelativeTo(null);

		});

		item = menu.add(new JMenuItem("Valid Products (Only)"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{

			if (checkAut(editor)) return;

			ProductFrame pf=editor.getProductFrame();

			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			lastDir=editor.getCurrentFile().getParent();

			MSCA aut=editor.lastaut;

			int[] vp= pf.getFamily().validProducts(aut);
			if (vp==null || vp.length==0)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No valid products!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}
			Product[] vpp=pf.getFamily().subsetOfProductsFromIndex(vp);
			pf=editor.getProductFrame();
			if (pf!=null)
				pf.dispose();
		
			pf= new ProductFrame(new Family(vpp), (JPanel)editor);
			editor.setProductFrame(pf);  
		});

		item = menu.add(new JMenuItem("Canonical Products"));
		item.addActionListener(e->
		{
			if (checkAut(editor)) return;

			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			lastDir=editor.getCurrentFile().getParent();

			MSCA aut=editor.lastaut;

			Family fam= editor.getProductFrame().getFamily();

			int[][] ind=new int[1][];

			long start = System.currentTimeMillis();
			Product[] cp=fam.getCanonicalProducts(aut,null,false,ind);
			long elapsedTime= System.currentTimeMillis() - start;

			if (cp==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Canonical Products"+"\n Elapsed time : "+elapsedTime+ "milliseconds",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			pf.setColorButtonProducts(ind[0], Color.ORANGE);
			String message="Canonical Products:\n";
			for (int i=0;i<cp.length;i++)
				message+= ind[0][i]+" : \n"+cp[i].toString()+"\n";

			message += "Elapsed time : "+elapsedTime+ "milliseconds";
			JTextArea textArea = new JTextArea(200,200);
			textArea.setText(message);
			textArea.setEditable(true);

			JScrollPane scrollPane = new JScrollPane(textArea);
			JDialog jd = new JDialog(pf);
			jd.add(scrollPane);
			jd.setTitle("Canonical Products");
			jd.setResizable(true);
			jd.setVisible(true);
			jd.setSize(500,500);
			jd.setLocationRelativeTo(null);

		});

		item = menu.add(new JMenuItem("Products with non-empty MPC"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{

			if (checkAut(editor)) return;

			lastDir=editor.getCurrentFile().getParent();
			MSCA aut=editor.lastaut;

			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			long start = System.currentTimeMillis();
			int[] vp= pf.getFamily().productsWithNonEmptyMPC(aut);
			long elapsedTime = System.currentTimeMillis() - start;

			Product[] vpp=pf.getFamily().subsetOfProductsFromIndex(vp);
			if (vp==null)
			{			
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Products With Non-empty MPC"+ "\nElapsed time : "+elapsedTime+ "milliseconds","",JOptionPane.WARNING_MESSAGE);
				return;
			}

			pf.setColorButtonProducts(vp, Color.BLUE);
			String message=vp.length + " Products With Non-empty MPC Found:\n";
			for (int i=0;i<vp.length;i++)
				message+= vp[i]+" : \n"+vpp[i].toString()+"\n";

			message += "Elapsed time : " + elapsedTime+ "milliseconds";
			JTextArea textArea = new JTextArea(200,200);
			textArea.setText(message);
			textArea.setEditable(true);

			JScrollPane scrollPane = new JScrollPane(textArea);
			JDialog jd = new JDialog(pf);
			jd.add(scrollPane);
			jd.setTitle("Products With Non-empty MPC");
			jd.setResizable(true);
			jd.setVisible(true);

			jd.setSize(500,500);
			jd.setLocationRelativeTo(null);
			// JOptionPane.showMessageDialog(editor.getGraphComponent(), jd);
			//JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Valid Products",JOptionPane.PLAIN_MESSAGE);

		});

		item = menu.add(new JMenuItem("Products with Non-empty MPC (Only)"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{		

			if (checkAut(editor)) return;

			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			lastDir=editor.getCurrentFile().getParent();

			MSCA aut=editor.lastaut;

			int[] vp= pf.getFamily().productsWithNonEmptyMPC(aut);
			Product[] vpp=pf.getFamily().subsetOfProductsFromIndex(vp);
			pf=editor.getProductFrame();
			if (pf!=null)
			{
				editor.setProductFrame(null);
				pf.dispose();
			}

			pf= new ProductFrame(new Family(vpp), (JPanel)editor);
			editor.setProductFrame(pf);	
		});


		item = menu.add(new JMenuItem("Sub-Products of Product"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}
			//aut.printToFile(filename);
			Family f=pf.getFamily();

			String S= (String) JOptionPane.showInputDialog(null, 
					"Insert Product id",
					JOptionPane.PLAIN_MESSAGE);
			if (S==null)
				return;

			int pindex=Integer.parseInt(S);
			Product p=f.getProducts()[pindex];

			int[] subind = f.getSubProductsofProduct(pindex);
			Product[] subprod = f.subsetOfProductsFromIndex(subind);
			pf.setColorButtonProducts(subind, Color.RED);

			String message=subind.length + " Sub-Products of Product "+pindex+"\n"+p.toString()+"\n";
			for (int i=0;i<subind.length;i++)
				message+= subind[i]+" : \n"+subprod[i].toString()+"\n";
			JTextArea textArea = new JTextArea(200,200);
			textArea.setText(message);
			textArea.setEditable(true);

			JScrollPane scrollPane = new JScrollPane(textArea);
			JDialog jd = new JDialog(pf);
			jd.add(scrollPane);
			jd.setTitle("Sub-Products");
			jd.setResizable(true);
			jd.setVisible(true);

			jd.setSize(500,500);
			jd.setLocationRelativeTo(null);


		});


		item = menu.add(new JMenuItem("Super-Products of Product"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			Family f=pf.getFamily();

			String S= (String) JOptionPane.showInputDialog(null, 
					"Insert Product id",
					JOptionPane.PLAIN_MESSAGE);
			if (S==null)
				return;

			int pindex=Integer.parseInt(S);
			Product p=f.getProducts()[pindex];

			int[] supind = f.getSuperProductsofProduct(pindex);
			Product[] subprod = f.subsetOfProductsFromIndex(supind);
			pf.setColorButtonProducts(supind, Color.RED);

			String message=supind.length + " Super-Products of Product "+pindex+"\n"+p.toString()+"\n";
			for (int i=0;i<supind.length;i++)
				message+= supind[i]+" : \n"+subprod[i].toString()+"\n";
			JTextArea textArea = new JTextArea(200,200);
			textArea.setText(message);
			textArea.setEditable(true);

			JScrollPane scrollPane = new JScrollPane(textArea);
			JDialog jd = new JDialog(pf);
			jd.add(scrollPane);
			jd.setTitle("Super-Products");
			jd.setResizable(true);
			jd.setSize(500,500);
			jd.setLocationRelativeTo(null);
			jd.setVisible(true);
		});


		menu.addSeparator();


		item = menu.add(new JMenuItem("Orchestration of Family"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{	
			if (checkAut(editor)) return;
			String filename=editor.getCurrentFile().getName();

			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			lastDir=editor.getCurrentFile().getParent();

			MSCA aut=editor.lastaut;
			
			Family f=pf.getFamily();

			long start = System.currentTimeMillis();
			MSCA controller = f.getMPCofFamily(aut);
			long elapsedTime = System.currentTimeMillis() - start;


			if (controller==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"The mpc is empty"+"\n Elapsed time : "+elapsedTime + " milliseconds","",JOptionPane.WARNING_MESSAGE);
				return;
			}

			String K="K_family_"+filename;
			File file=MSCAIO.convertMSCAintoXML(lastDir+"\\"+K,controller);

			String message = "The mpc has been stored with filename "+lastDir+"\\"
					+ K
					+ "\n Elapsed time : "+elapsedTime + " milliseconds"
					+ "\n Number of states : "+controller.getNumStates();
			;

			JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.WARNING_MESSAGE);

			editor.lastaut=controller;
			loadMorphStore(K,editor,file);
		});


		item = menu.add(new JMenuItem("Orchestration of a Product (insert manually)"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			if (checkAut(editor)) return;
			String filename=editor.getCurrentFile().getName();

			lastDir=editor.getCurrentFile().getParent();
			MSCA aut=editor.lastaut;
			
			String S= (String) JOptionPane.showInputDialog(null, 
					"Insert Required features separated by colon",
					JOptionPane.PLAIN_MESSAGE);
			if (S==null)
				return;
			String[] R=S.split(",");

			if (R[0].equals(""))
				R=new String[0];

			S= (String) JOptionPane.showInputDialog(null, 
					"Insert Forbidden actions separated by semicolon",
					JOptionPane.PLAIN_MESSAGE);
			if (S==null)
				return;
			String[] F=S.split(",");
			if (F[0].equals(""))
				F=new String[0];

			Product p=(R.length+F.length>0)?new Product(R,F):null;

			MSCA controller=null;
			FMCA faut= new FMCA(aut);
			long elapsedTime;
			long start = System.currentTimeMillis();
			controller= (p!=null)?faut.orchestration(p):aut.orchestration();
			elapsedTime = System.currentTimeMillis() - start;

			if (controller==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"The mpc is empty"+"\n Elapsed time : "+elapsedTime + " milliseconds","",JOptionPane.WARNING_MESSAGE);
				return;
			}

			String K="K_"+"(R"+Arrays.toString(R)+"_F"+Arrays.toString(F)+")_"+filename;
			File file=MSCAIO.convertMSCAintoXML(lastDir+"//"+K,controller);
			String message = "The mpc has been stored with filename "+lastDir+"//"+K
					+"\n Elapsed time : "+elapsedTime + " milliseconds"
					+"\n Number of states : "+controller.getNumStates();

			JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.WARNING_MESSAGE);
			editor.lastaut=controller;
			parseAndSet(lastDir+"//"+K, editor, file);

		});

		item = menu.add(new JMenuItem("Orchestration of a Product (product id)"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			if (checkAut(editor)) return;
			String filename=editor.getCurrentFile().getName();

			lastDir=editor.getCurrentFile().getParent();

			MSCA aut=editor.lastaut;

			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			Family f=pf.getFamily();
			FMCA faut= new FMCA(aut);

			String S= (String) JOptionPane.showInputDialog(editor.getGraphComponent(), 
					"Insert Product id",
					JOptionPane.PLAIN_MESSAGE);
			if (S==null)
				return;

			Product p=f.getProducts()[Integer.parseInt(S)];
			long start = System.currentTimeMillis();
			MSCA controller = faut.orchestration(p);
			long elapsedTime = System.currentTimeMillis() - start;

			if (controller==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"The mpc is empty"+"\n Elapsed time : "+elapsedTime + " milliseconds","Empty",JOptionPane.WARNING_MESSAGE);
				return;
			}
			String K="K_"+"(R"+Arrays.toString(p.getRequired())+"_F"+Arrays.toString(p.getForbidden())+")_"+filename;
			File file=MSCAIO.convertMSCAintoXML(lastDir+"\\"+K,controller);
			String message = "The mpc has been stored with filename "+lastDir+"//"+K
					+"\n Elapsed time : "+elapsedTime + " milliseconds"
					+"\n Number of states : "+controller.getNumStates();

			JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.PLAIN_MESSAGE);

			editor.lastaut=controller;
			parseAndSet(lastDir+"//"+K, editor, file);
					
		});

		menu.addSeparator();


		item = menu.add(new JMenuItem("Orchestration of Family (without PO)"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			if (checkAut(editor)) return;
			String filename=editor.getCurrentFile().getName();

			ProductFrame pf=editor.getProductFrame();
			if (pf==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"No Family loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
				return;
			}

			lastDir=editor.getCurrentFile().getParent();
			MSCA aut=editor.lastaut;
//			
//			String absfilename =editor.getCurrentFile().getAbsolutePath();
//			MSCA aut;
//			try {
//				aut = MSCAIO.parseXMLintoMSCA(absfilename);
//			} catch (ParserConfigurationException|SAXException|IOException e1) {
//				JOptionPane.showMessageDialog(editor.getGraphComponent(),e1.getMessage()+"\n"+errorMsg,mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
//				return;
//			}
			Family f=pf.getFamily();

			JOptionPane.showMessageDialog(editor.getGraphComponent(),"Warning : the computation without PO may require several minutes!","Warning",JOptionPane.WARNING_MESSAGE);

			long start = System.currentTimeMillis();
			int[][] vpdummy = new int[1][];
			MSCA controller = f.getMPCofFamilyWithoutPO(aut, pf, vpdummy);
			int[] vp = vpdummy[0];
			long elapsedTime = System.currentTimeMillis() - start;

//			File file=null;
			Product[] vpp=pf.getFamily().subsetOfProductsFromIndex(vp);

			if (controller==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"The mpc is empty"+"\n Elapsed time : "+elapsedTime + " milliseconds","Empty",JOptionPane.WARNING_MESSAGE);
				return;
			}

			String K="K_familyWithoutPO_"+filename;
			File file=MSCAIO.convertMSCAintoXML(lastDir+"\\"+K,controller);

			String message = "The mpc has been stored with filename "+lastDir+"\\"+K;

			message+= "\n" + vp.length + " Total Products With Non-empty MPC Found:\n";
			for (int i=0;i<vp.length;i++)
				message+= vp[i]+" : \n"+vpp[i].toString()+"\n";

			message+="\n Elapsed time : "+elapsedTime + " milliseconds"
					+"\n Number of states : "+controller.getNumStates();


			JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.WARNING_MESSAGE);
			editor.lastaut=controller;
			loadMorphStore(K,editor,file);

		});


		item = menu.add(new JMenuItem("Info about converting in MSCA without Lazy Transitions"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			if (checkAut(editor)) return;

			lastDir=editor.getCurrentFile().getParent();
			MSCA aut=editor.lastaut;

			JOptionPane.showMessageDialog(editor.getGraphComponent(),aut.infoExpressivenessLazyTransitions(),"Result",JOptionPane.WARNING_MESSAGE);

		});

		menu.addSeparator();

		item = menu.add(new JMenuItem("Choreography"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			if (checkAut(editor)) return;
			String filename=editor.getCurrentFile().getName();

			lastDir=editor.getCurrentFile().getParent();
			MSCA aut=editor.lastaut;

			long start = System.currentTimeMillis();

			MSCA controller = aut.choreographyLarger();

			long elapsedTime = System.currentTimeMillis() - start;

			if (controller==null)
			{
				JOptionPane.showMessageDialog(editor.getGraphComponent(),"The choreography is empty"+"\n Elapsed time : "+elapsedTime + " milliseconds","Empty",JOptionPane.WARNING_MESSAGE);
				return;
			}
			String K="Chor_"+//"(R"+Arrays.toString(R)+"_F"+Arrays.toString(F)+")_"+
					filename;
			File file= MSCAIO.convertMSCAintoXML(lastDir+"//"+K,controller);
			String message = "The choreography has been stored with filename "+lastDir+"//"+K
					+"\n Elapsed time : "+elapsedTime + " milliseconds"
					+"\n Number of states : "+controller.getNumStates();

			JOptionPane.showMessageDialog(editor.getGraphComponent(),message,"Success!",JOptionPane.PLAIN_MESSAGE);

			editor.lastaut=controller;
			parseAndSet(lastDir+"//"+K, editor, file);
		});

		// Creates the help menu
		menu = add(new JMenu(mxResources.get("help")));

		item = menu.add(new JMenuItem("about FMCA Tool"));//mxResources.get("aboutGraphEditor")));
		item.addActionListener(e->
		{
			editor.about();
		});
	}

	private void loadMorphStore(String name, FMCATGUI editor, File file)
	{
		if (!name.endsWith(".mxe")&&!name.endsWith(".data"))
			name=name+".mxe";
		if (!name.startsWith(lastDir))
			name=lastDir+"\\"+name;
		try
		{	
			mxGraph graph = editor.getGraphComponent().getGraph();

			//TODO I store, load, morph and store the file again, there should be a better method
			// I do this way because I use Document to update the window
			Document document = mxXmlUtils
					.parseXml(mxUtils.readFile(name));									

			mxCodec codec = new mxCodec(document);
			mxGraphModel mgm = (mxGraphModel) codec.decode(
					document.getDocumentElement(),
					graph.getModel());

			mxGraph mg=new mxGraph(mgm);
			mxGraphComponent mgc = new mxGraphComponent(mg);

			FMCATGUI.morphGraph(mgc.getGraph(), mgc);

			codec = new mxCodec();
			String xml = mxXmlUtils.getXml(codec.encode(mgc.getGraph().getModel()));

			mxUtils.writeFile(xml, name);

			parseAndSet(name, editor, file);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(
					editor.getGraphComponent(),
					ex.toString(),
					mxResources.get("error"),
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private void parseAndSet(String absfilename, FMCATGUI editor, File file)
	{
		//TODO there should be no need in parsing the xml and then converting to xml anymore

		try
		{								
			mxGraph graphfinal = editor.getGraphComponent().getGraph();
			Document document = mxXmlUtils
					.parseXml(mxUtils.readFile(absfilename));//lastDir+"//"+name));
			mxCodec codec = new mxCodec(document);
			codec.decode(
					document.getDocumentElement(),
					graphfinal.getModel());
			editor.setCurrentFile(file);
			editor.setModified(false);
			editor.getUndoManager().clear();
			editor.getGraphComponent().zoomAndCenter();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(
					editor.getGraphComponent(),
					ex.toString(),
					mxResources.get("error"),
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private boolean checkAut(FMCATGUI editor)
	{
		try
		{
			editor.getCurrentFile().getName();
			return false;

		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(editor.getGraphComponent(),"No automaton loaded!",mxResources.get("error"),JOptionPane.ERROR_MESSAGE);
			return true;
		}
	}

	private DefaultFileFilter setDefaultFilter(JFileChooser fc,String type, String title, String type2) {
		DefaultFileFilter defaultFilter = new DefaultFileFilter(type, "")//mxResources.get("allSupportedFormats")
				//+ " (.mxe, .png, .vdx)")
				{
			public boolean accept(File file)
			{
				String lcase = file.getName().toLowerCase();
				if (type2==null)
					return super.accept(file)
							|| lcase.endsWith(type);
				else
					return super.accept(file)
							|| lcase.endsWith(type2);
			}
				};
				fc.addChoosableFileFilter(defaultFilter);
				fc.addChoosableFileFilter(new DefaultFileFilter(type,
						title+" "+  mxResources.get("file")
						+ " ("+type+")"));

				fc.setFileFilter(defaultFilter);
				return defaultFilter;
	}

}

//END OF CLASS


//
///**
// *
// */
//public static class InsertGraph extends AbstractAction
//{
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 4010463992665008365L;
//
//	/**
//	 * 
//	 */
//	protected GraphType graphType;
//
//	protected mxAnalysisGraph aGraph;
//
//	/**
//	 * @param aGraph 
//	 * 
//	 */
//	public InsertGraph(GraphType tree, mxAnalysisGraph aGraph)
//	{
//		this.graphType = tree;
//		this.aGraph = aGraph;
//	}
//
//	/**
//	 * 
//	 */
//	public void actionPerformed(ActionEvent e)
//	{
//		if (e.getSource() instanceof mxGraphComponent)
//		{
//			mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
//			mxGraph graph = graphComponent.getGraph();
//
//			// dialog = new FactoryConfigDialog();
//			String dialogText = "";
//			if (graphType == GraphType.NULL)
//				dialogText = "Configure null graph";
//			else if (graphType == GraphType.COMPLETE)
//				dialogText = "Configure complete graph";
//			else if (graphType == GraphType.NREGULAR)
//				dialogText = "Configure n-regular graph";
//			else if (graphType == GraphType.GRID)
//				dialogText = "Configure grid graph";
//			else if (graphType == GraphType.BIPARTITE)
//				dialogText = "Configure bipartite graph";
//			else if (graphType == GraphType.COMPLETE_BIPARTITE)
//				dialogText = "Configure complete bipartite graph";
//			else if (graphType == GraphType.BFS_DIR)
//				dialogText = "Configure BFS algorithm";
//			else if (graphType == GraphType.BFS_UNDIR)
//				dialogText = "Configure BFS algorithm";
//			else if (graphType == GraphType.DFS_DIR)
//				dialogText = "Configure DFS algorithm";
//			else if (graphType == GraphType.DFS_UNDIR)
//				dialogText = "Configure DFS algorithm";
//			else if (graphType == GraphType.DIJKSTRA)
//				dialogText = "Configure Dijkstra's algorithm";
//			else if (graphType == GraphType.BELLMAN_FORD)
//				dialogText = "Configure Bellman-Ford algorithm";
//			else if (graphType == GraphType.MAKE_TREE_DIRECTED)
//				dialogText = "Configure make tree directed algorithm";
//			else if (graphType == GraphType.KNIGHT_TOUR)
//				dialogText = "Configure knight's tour";
//			else if (graphType == GraphType.GET_ADJ_MATRIX)
//				dialogText = "Configure adjacency matrix";
//			else if (graphType == GraphType.FROM_ADJ_MATRIX)
//				dialogText = "Input adjacency matrix";
//			else if (graphType == GraphType.PETERSEN)
//				dialogText = "Configure Petersen graph";
//			else if (graphType == GraphType.WHEEL)
//				dialogText = "Configure Wheel graph";
//			else if (graphType == GraphType.STAR)
//				dialogText = "Configure Star graph";
//			else if (graphType == GraphType.PATH)
//				dialogText = "Configure Path graph";
//			else if (graphType == GraphType.FRIENDSHIP_WINDMILL)
//				dialogText = "Configure Friendship Windmill graph";
//			else if (graphType == GraphType.INDEGREE)
//				dialogText = "Configure indegree analysis";
//			else if (graphType == GraphType.OUTDEGREE)
//				dialogText = "Configure outdegree analysis";
//			GraphConfigDialog dialog = new GraphConfigDialog(graphType, dialogText);
//			dialog.configureLayout(graph, graphType, aGraph);
//			dialog.setModal(true);
//			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//			Dimension frameSize = dialog.getSize();
//			dialog.setLocation(screenSize.width / 2 - (frameSize.width / 2), screenSize.height / 2 - (frameSize.height / 2));
//			dialog.setVisible(true);
//		}
//	}
//}



// Creates a developer menu
/*menu = add(new JMenu("Generate"));
menu.add(editor.bind("Null Graph", new InsertGraph(GraphType.NULL, aGraph)));
menu.add(editor.bind("Complete Graph", new InsertGraph(GraphType.COMPLETE, aGraph)));
menu.add(editor.bind("Grid", new InsertGraph(GraphType.GRID, aGraph)));
menu.add(editor.bind("Bipartite", new InsertGraph(GraphType.BIPARTITE, aGraph)));
menu.add(editor.bind("Complete Bipartite", new InsertGraph(GraphType.COMPLETE_BIPARTITE, aGraph)));
menu.add(editor.bind("Knight's Graph", new InsertGraph(GraphType.KNIGHT, aGraph)));
menu.add(editor.bind("King's Graph", new InsertGraph(GraphType.KING, aGraph)));
menu.add(editor.bind("Petersen", new InsertGraph(GraphType.PETERSEN, aGraph)));
menu.add(editor.bind("Path", new InsertGraph(GraphType.PATH, aGraph)));
menu.add(editor.bind("Star", new InsertGraph(GraphType.STAR, aGraph)));
menu.add(editor.bind("Wheel", new InsertGraph(GraphType.WHEEL, aGraph)));
menu.add(editor.bind("Friendship Windmill", new InsertGraph(GraphType.FRIENDSHIP_WINDMILL, aGraph)));
menu.add(editor.bind("Full Windmill", new InsertGraph(GraphType.FULL_WINDMILL, aGraph)));
menu.add(editor.bind("Knight's Tour", new InsertGraph(GraphType.KNIGHT_TOUR, aGraph)));
menu.addSeparator();
menu.add(editor.bind("Simple Random", new InsertGraph(GraphType.SIMPLE_RANDOM, aGraph)));
menu.add(editor.bind("Simple Random Tree", new InsertGraph(GraphType.SIMPLE_RANDOM_TREE, aGraph)));
menu.addSeparator();
menu.add(editor.bind("Reset Style", new InsertGraph(GraphType.RESET_STYLE, aGraph)));

menu = add(new JMenu("Analyze"));
menu.add(editor.bind("Is Connected", new AnalyzeGraph(AnalyzeType.IS_CONNECTED, aGraph)));
menu.add(editor.bind("Is Simple", new AnalyzeGraph(AnalyzeType.IS_SIMPLE, aGraph)));
menu.add(editor.bind("Is Directed Cyclic", new AnalyzeGraph(AnalyzeType.IS_CYCLIC_DIRECTED, aGraph)));
menu.add(editor.bind("Is Undirected Cyclic", new AnalyzeGraph(AnalyzeType.IS_CYCLIC_UNDIRECTED, aGraph)));
menu.add(editor.bind("BFS Directed", new InsertGraph(GraphType.BFS_DIR, aGraph)));
menu.add(editor.bind("BFS Undirected", new InsertGraph(GraphType.BFS_UNDIR, aGraph)));
menu.add(editor.bind("DFS Directed", new InsertGraph(GraphType.DFS_DIR, aGraph)));
menu.add(editor.bind("DFS Undirected", new InsertGraph(GraphType.DFS_UNDIR, aGraph)));
menu.add(editor.bind("Complementary", new AnalyzeGraph(AnalyzeType.COMPLEMENTARY, aGraph)));
menu.add(editor.bind("Regularity", new AnalyzeGraph(AnalyzeType.REGULARITY, aGraph)));
menu.add(editor.bind("Dijkstra", new InsertGraph(GraphType.DIJKSTRA, aGraph)));
menu.add(editor.bind("Bellman-Ford", new InsertGraph(GraphType.BELLMAN_FORD, aGraph)));
menu.add(editor.bind("Floyd-Roy-Warshall", new AnalyzeGraph(AnalyzeType.FLOYD_ROY_WARSHALL, aGraph)));
menu.add(editor.bind("Get Components", new AnalyzeGraph(AnalyzeType.COMPONENTS, aGraph)));
menu.add(editor.bind("Make Connected", new AnalyzeGraph(AnalyzeType.MAKE_CONNECTED, aGraph)));
menu.add(editor.bind("Make Simple", new AnalyzeGraph(AnalyzeType.MAKE_SIMPLE, aGraph)));
menu.add(editor.bind("Is Tree", new AnalyzeGraph(AnalyzeType.IS_TREE, aGraph)));
menu.add(editor.bind("One Spanning Tree", new AnalyzeGraph(AnalyzeType.ONE_SPANNING_TREE, aGraph)));
menu.add(editor.bind("Make tree directed", new InsertGraph(GraphType.MAKE_TREE_DIRECTED, aGraph)));
menu.add(editor.bind("Is directed", new AnalyzeGraph(AnalyzeType.IS_DIRECTED, aGraph)));
menu.add(editor.bind("Indegree", new InsertGraph(GraphType.INDEGREE, aGraph)));
menu.add(editor.bind("Outdegree", new InsertGraph(GraphType.OUTDEGREE, aGraph)));
menu.add(editor.bind("Is cut vertex", new InsertGraph(GraphType.IS_CUT_VERTEX, aGraph)));
menu.add(editor.bind("Get cut vertices", new AnalyzeGraph(AnalyzeType.GET_CUT_VERTEXES, aGraph)));
menu.add(editor.bind("Get cut edges", new AnalyzeGraph(AnalyzeType.GET_CUT_EDGES, aGraph)));
menu.add(editor.bind("Get sources", new AnalyzeGraph(AnalyzeType.GET_SOURCES, aGraph)));
menu.add(editor.bind("Get sinks", new AnalyzeGraph(AnalyzeType.GET_SINKS, aGraph)));
menu.add(editor.bind("Is biconnected", new AnalyzeGraph(AnalyzeType.IS_BICONNECTED, aGraph)));
 */

/**
 *
 */
//	public static class AnalyzeGraph extends AbstractAction
//	{
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 6926170745240507985L;
//
//		mxAnalysisGraph aGraph;
//
//		/**
//		 * 
//		 */
//		protected AnalyzeType analyzeType;
//
//		/**
//		 * Examples for calling analysis methods from mxGraphStructure 
//		 */
//		public AnalyzeGraph(AnalyzeType analyzeType, mxAnalysisGraph aGraph)
//		{
//			this.analyzeType = analyzeType;
//			this.aGraph = aGraph;
//		}
//
//		public void actionPerformed(ActionEvent e)
//		{
//			if (e.getSource() instanceof mxGraphComponent)
//			{
//				mxGraphComponent graphComponent = (mxGraphComponent) e.getSource();
//				mxGraph graph = graphComponent.getGraph();
//				aGraph.setGraph(graph);
//
//				if (analyzeType == AnalyzeType.IS_CONNECTED)
//				{
//					boolean isConnected = mxGraphStructure.isConnected(aGraph);
//
//					if (isConnected)
//					{
//						System.out.println("The graph is connected");
//					}
//					else
//					{
//						System.out.println("The graph is not connected");
//					}
//				}
//				else if (analyzeType == AnalyzeType.IS_SIMPLE)
//				{
//					boolean isSimple = mxGraphStructure.isSimple(aGraph);
//
//					if (isSimple)
//					{
//						System.out.println("The graph is simple");
//					}
//					else
//					{
//						System.out.println("The graph is not simple");
//					}
//				}
//				else if (analyzeType == AnalyzeType.IS_CYCLIC_DIRECTED)
//				{
//					boolean isCyclicDirected = mxGraphStructure.isCyclicDirected(aGraph);
//
//					if (isCyclicDirected)
//					{
//						System.out.println("The graph is cyclic directed");
//					}
//					else
//					{
//						System.out.println("The graph is acyclic directed");
//					}
//				}
//				else if (analyzeType == AnalyzeType.IS_CYCLIC_UNDIRECTED)
//				{
//					boolean isCyclicUndirected = mxGraphStructure.isCyclicUndirected(aGraph);
//
//					if (isCyclicUndirected)
//					{
//						System.out.println("The graph is cyclic undirected");
//					}
//					else
//					{
//						System.out.println("The graph is acyclic undirected");
//					}
//				}
//				else if (analyzeType == AnalyzeType.COMPLEMENTARY)
//				{
//					graph.getModel().beginUpdate();
//
//					mxGraphStructure.complementaryGraph(aGraph);
//
//					mxGraphStructure.setDefaultGraphStyle(aGraph, true);
//					graph.getModel().endUpdate();
//				}
//				else if (analyzeType == AnalyzeType.REGULARITY)
//				{
//					try
//					{
//						int regularity = mxGraphStructure.regularity(aGraph);
//						System.out.println("Graph regularity is: " + regularity);
//					}
//					catch (StructuralException e1)
//					{
//						System.out.println("The graph is irregular");
//					}
//				}
//				else if (analyzeType == AnalyzeType.COMPONENTS)
//				{
//					Object[][] components = mxGraphStructure.getGraphComponents(aGraph);
//					mxIGraphModel model = aGraph.getGraph().getModel();
//
//					for (int i = 0; i < components.length; i++)
//					{
//						System.out.print("Component " + i + " :");
//
//						for (int j = 0; j < components[i].length; j++)
//						{
//							System.out.print(" " + model.getValue(components[i][j]));
//						}
//
//						System.out.println(".");
//					}
//
//					System.out.println("Number of components: " + components.length);
//
//				}
//				else if (analyzeType == AnalyzeType.MAKE_CONNECTED)
//				{
//					graph.getModel().beginUpdate();
//
//					if (!mxGraphStructure.isConnected(aGraph))
//					{
//						mxGraphStructure.makeConnected(aGraph);
//						mxGraphStructure.setDefaultGraphStyle(aGraph, false);
//					}
//
//					graph.getModel().endUpdate();
//				}
//				else if (analyzeType == AnalyzeType.MAKE_SIMPLE)
//				{
//					mxGraphStructure.makeSimple(aGraph);
//				}
//				else if (analyzeType == AnalyzeType.IS_TREE)
//				{
//					boolean isTree = mxGraphStructure.isTree(aGraph);
//
//					if (isTree)
//					{
//						System.out.println("The graph is a tree");
//					}
//					else
//					{
//						System.out.println("The graph is not a tree");
//					}
//				}
//				else if (analyzeType == AnalyzeType.ONE_SPANNING_TREE)
//				{
//					try
//					{
//						graph.getModel().beginUpdate();
//						aGraph.getGenerator().oneSpanningTree(aGraph, true, true);
//						mxGraphStructure.setDefaultGraphStyle(aGraph, false);
//						graph.getModel().endUpdate();
//					}
//					catch (StructuralException e1)
//					{
//						System.out.println("The graph must be simple and connected");
//					}
//				}
//				else if (analyzeType == AnalyzeType.IS_DIRECTED)
//				{
//					boolean isDirected = mxGraphProperties.isDirected(aGraph.getProperties(), mxGraphProperties.DEFAULT_DIRECTED);
//
//					if (isDirected)
//					{
//						System.out.println("The graph is directed.");
//					}
//					else
//					{
//						System.out.println("The graph is undirected.");
//					}
//				}
//				else if (analyzeType == AnalyzeType.GET_CUT_VERTEXES)
//				{
//					Object[] cutVertices = mxGraphStructure.getCutVertices(aGraph);
//
//					System.out.print("Cut vertices of the graph are: [");
//					mxIGraphModel model = aGraph.getGraph().getModel();
//
//					for (int i = 0; i < cutVertices.length; i++)
//					{
//						System.out.print(" " + model.getValue(cutVertices[i]));
//					}
//
//					System.out.println(" ]");
//				}
//				else if (analyzeType == AnalyzeType.GET_CUT_EDGES)
//				{
//					Object[] cutEdges = mxGraphStructure.getCutEdges(aGraph);
//
//					System.out.print("Cut edges of the graph are: [");
//					mxIGraphModel model = aGraph.getGraph().getModel();
//
//					for (int i = 0; i < cutEdges.length; i++)
//					{
//						System.out.print(" " + Integer.parseInt((String) model.getValue(aGraph.getTerminal(cutEdges[i], true))) + "-"
//								+ Integer.parseInt((String) model.getValue(aGraph.getTerminal(cutEdges[i], false))));
//					}
//
//					System.out.println(" ]");
//				}
//				else if (analyzeType == AnalyzeType.GET_SOURCES)
//				{
//					try
//					{
//						Object[] sourceVertices = mxGraphStructure.getSourceVertices(aGraph);
//						System.out.print("Source vertices of the graph are: [");
//						mxIGraphModel model = aGraph.getGraph().getModel();
//
//						for (int i = 0; i < sourceVertices.length; i++)
//						{
//							System.out.print(" " + model.getValue(sourceVertices[i]));
//						}
//
//						System.out.println(" ]");
//					}
//					catch (StructuralException e1)
//					{
//						System.out.println(e1);
//					}
//				}
//				else if (analyzeType == AnalyzeType.GET_SINKS)
//				{
//					try
//					{
//						Object[] sinkVertices = mxGraphStructure.getSinkVertices(aGraph);
//						System.out.print("Sink vertices of the graph are: [");
//						mxIGraphModel model = aGraph.getGraph().getModel();
//
//						for (int i = 0; i < sinkVertices.length; i++)
//						{
//							System.out.print(" " + model.getValue(sinkVertices[i]));
//						}
//
//						System.out.println(" ]");
//					}
//					catch (StructuralException e1)
//					{
//						System.out.println(e1);
//					}
//				}
//				else if (analyzeType == AnalyzeType.PLANARITY)
//				{
//					// implement
//				}
//				else if (analyzeType == AnalyzeType.IS_BICONNECTED)
//				{
//					boolean isBiconnected = mxGraphStructure.isBiconnected(aGraph);
//
//					if (isBiconnected)
//					{
//						System.out.println("The graph is biconnected.");
//					}
//					else
//					{
//						System.out.println("The graph is not biconnected.");
//					}
//				}
//				else if (analyzeType == AnalyzeType.GET_BICONNECTED)
//				{
//					// implement
//				}
//				else if (analyzeType == AnalyzeType.SPANNING_TREE)
//				{
//					// implement
//				}
//				else if (analyzeType == AnalyzeType.FLOYD_ROY_WARSHALL)
//				{
//
//					ArrayList<Object[][]> FWIresult = new ArrayList<Object[][]>();
//					try
//					{
//						//only this line is needed to get the result from Floyd-Roy-Warshall, the rest is code for displaying the result
//						FWIresult = mxTraversal.floydRoyWarshall(aGraph);
//
//						Object[][] dist = FWIresult.get(0);
//						Object[][] paths = FWIresult.get(1);
//						Object[] vertices = aGraph.getChildVertices(aGraph.getGraph().getDefaultParent());
//						int vertexNum = vertices.length;
//						System.out.println("Distances are:");
//
//						for (int i = 0; i < vertexNum; i++)
//						{
//							System.out.print("[");
//
//							for (int j = 0; j < vertexNum; j++)
//							{
//								System.out.print(" " + Math.round((Double) dist[i][j] * 100.0) / 100.0);
//							}
//
//							System.out.println("] ");
//						}
//
//						System.out.println("Path info:");
//
//						mxCostFunction costFunction = aGraph.getGenerator().getCostFunction();
//						mxGraphView view = aGraph.getGraph().getView();
//
//						for (int i = 0; i < vertexNum; i++)
//						{
//							System.out.print("[");
//
//							for (int j = 0; j < vertexNum; j++)
//							{
//								if (paths[i][j] != null)
//								{
//									System.out.print(" " + costFunction.getCost(view.getState(paths[i][j])));
//								}
//								else
//								{
//									System.out.print(" -");
//								}
//							}
//
//							System.out.println(" ]");
//						}
//
//						try
//						{
//							Object[] path = mxTraversal.getWFIPath(aGraph, FWIresult, vertices[0], vertices[vertexNum - 1]);
//							System.out.print("The path from " + costFunction.getCost(view.getState(vertices[0])) + " to "
//									+ costFunction.getCost((view.getState(vertices[vertexNum - 1]))) + " is:");
//
//							for (int i = 0; i < path.length; i++)
//							{
//								System.out.print(" " + costFunction.getCost(view.getState(path[i])));
//							}
//
//							System.out.println();
//						}
//						catch (StructuralException e1)
//						{
//							System.out.println(e1);
//						}
//					}
//					catch (StructuralException e2)
//					{
//						System.out.println(e2);
//					}
//				}
//			}
//		}
//	}
//

