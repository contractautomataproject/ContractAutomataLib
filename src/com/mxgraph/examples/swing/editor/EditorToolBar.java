package com.mxgraph.examples.swing.editor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

import com.mxgraph.examples.swing.editor.EditorActions.HistoryAction;
import com.mxgraph.examples.swing.editor.EditorActions.NewAction;
import com.mxgraph.examples.swing.editor.EditorActions.OpenAction;
import com.mxgraph.examples.swing.editor.EditorActions.PrintAction;
import com.mxgraph.examples.swing.editor.EditorActions.SaveAction;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;

public class EditorToolBar extends JToolBar
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8015443128436394471L;
	
	private static int incrementalStateLabel=0;

	/**
	 * 
	 * @param frame
	 * @param orientation
	 */
	//private boolean ignoreZoomChange = false;

	//////////////////////////////////////////////
	///START COPIED FROM EDITORPALETTE.JAVA
	///////////////////////////////////////////////
	/**
	 * 
	 */
	protected JLabel selectedEntry = null;
	
	/**
	 * 
	 */
	protected mxEventSource eventSource = new mxEventSource(this);

	
	/**
	 * 
	 */
	public void setSelectionEntry(JLabel entry, mxGraphTransferable t)
	{
		JLabel previous = selectedEntry;
		selectedEntry = entry;

		if (previous != null)
		{
			previous.setBorder(null);
			previous.setOpaque(false);
		}

		if (selectedEntry != null)
		{
			selectedEntry.setBorder(ShadowBorder.getSharedInstance());
			selectedEntry.setOpaque(true);
		}

		eventSource.fireEvent(new mxEventObject(mxEvent.SELECT, "entry",
				selectedEntry, "transferable", t, "previous", previous));
	}

	/**
	 * 
	 * @param name
	 * @param icon
	 * @param cell
	 */
	public void addTemplate(final String name, ImageIcon icon, mxCell cell)
	{
		mxRectangle bounds = (mxGeometry) cell.getGeometry().clone();
		final mxGraphTransferable t = new mxGraphTransferable(
				new Object[] { cell }, bounds);

		// Scales the image if it's too large for the library
		if (icon != null)
		{
			if (icon.getIconWidth() > 32 || icon.getIconHeight() > 32)
			{
				icon = new ImageIcon(icon.getImage().getScaledInstance(32, 32,
						0));
			}
		}

		final JLabel entry = new JLabel(icon);
		entry.setPreferredSize(new Dimension(50, 50));
		entry.setBackground(entry.getBackground().brighter());
		entry.setFont(new Font(entry.getFont().getFamily(), 0, 10));

		entry.setVerticalTextPosition(JLabel.BOTTOM);
		entry.setHorizontalTextPosition(JLabel.CENTER);
		entry.setIconTextGap(0);

		entry.setToolTipText(name);
		entry.setText(name);

		entry.addMouseListener(new MouseListener()
		{

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent e)
			{
				setSelectionEntry(entry, t);
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
			 */
			public void mouseEntered(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
			 */
			public void mouseExited(MouseEvent e)
			{
			}

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent e)
			{
			}

		});

		// Install the handler for dragging nodes into a graph
		DragGestureListener dragGestureListener = new DragGestureListener()
		{
			/**
			 * 
			 */
			public void dragGestureRecognized(DragGestureEvent e)
			{
				if (cell.getValue()!="[]")
				{
					cell.setValue("["+incrementalStateLabel+"]");
					incrementalStateLabel++;
				}
				e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(),
								t, null);
			}

		};

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(entry,
				DnDConstants.ACTION_COPY, dragGestureListener);

		add(entry);
	}

	
	public void addEdgeTemplate(final String name, ImageIcon icon,
			String style, int width, int height, Object value)
	{
		mxGeometry geometry = new mxGeometry(0, 0, width, height);
		geometry.setTerminalPoint(new mxPoint(0, height), true);
		geometry.setTerminalPoint(new mxPoint(width, 0), false);
		geometry.setRelative(true);

		mxCell cell = new mxCell(value, geometry, style);
		cell.setEdge(true);

		addTemplate(name, icon, cell);
	}

	////////////////////////////////////////////////////////
	//////END COPIED
	////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	public EditorToolBar(final BasicGraphEditor editor, int orientation)
	{
		super(orientation);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEmptyBorder(3, 3, 3, 3), getBorder()));
		setFloatable(false);

		add(editor.bind("New", new NewAction(),
				"/com/mxgraph/examples/swing/images/new.gif"));
		add(editor.bind("Open", new OpenAction(),
				"/com/mxgraph/examples/swing/images/open.gif"));

		/*add(editor.bind("Import", new ImportAction(),
				"/com/mxgraph/examples/swing/images/open.gif"));
		*/add(editor.bind("Save", new SaveAction(false),
				"/com/mxgraph/examples/swing/images/save.gif"));
		

	//	addSeparator();

		add(editor.bind("Print", new PrintAction(),
				"/com/mxgraph/examples/swing/images/print.gif"));

	//	addSeparator();

		add(editor.bind("Cut", TransferHandler.getCutAction(),
				"/com/mxgraph/examples/swing/images/cut.gif"));
		add(editor.bind("Copy", TransferHandler.getCopyAction(),
				"/com/mxgraph/examples/swing/images/copy.gif"));
		add(editor.bind("Paste", TransferHandler.getPasteAction(),
				"/com/mxgraph/examples/swing/images/paste.gif"));

	//	addSeparator();

		add(editor.bind("Delete", mxGraphActions.getDeleteAction(),
				"/com/mxgraph/examples/swing/images/delete.gif"));

	//	addSeparator();

		add(editor.bind("Undo", new HistoryAction(true),
				"/com/mxgraph/examples/swing/images/undo.gif"));
		add(editor.bind("Redo", new HistoryAction(false),
				"/com/mxgraph/examples/swing/images/redo.gif"));
		
		
		addSeparator();

		mxCell cellState = new mxCell("", new mxGeometry(0, 0, 40, 40),
		"roundImage;image=/com/mxgraph/examples/swing/images/event.png");
		cellState.setVertex(true);
		addTemplate("State", new ImageIcon(getClass().getResource("/com/mxgraph/examples/swing/images/ellipse.png")), 
				cellState);
		
		mxCell cellFinalState = new mxCell("", new mxGeometry(0, 0, 40, 40),
				"roundImage;image=/com/mxgraph/examples/swing/images/terminate.png");
				cellState.setVertex(true);
		cellFinalState.setVertex(true);
		
		addTemplate(
				" FinalState ",//"Terminate",
				new ImageIcon(
						getClass().getResource("/com/mxgraph/examples/swing/images/doubleellipse.png")),
				cellFinalState);
		
		
		addEdgeTemplate(
				" Edge ",//"Horizontal Edge",
				new ImageIcon(
						App.class
								.getResource("/com/mxgraph/examples/swing/images/connect.png")),
				"", 100, 100, "[]");
		addEdgeTemplate(
				" Vertical Edge ",
				new ImageIcon(
						App.class
								.getResource("/com/mxgraph/examples/swing/images/vertical.png")),
				"vertical", 100, 100, "[]");
		addEdgeTemplate(
				" Rounded Edge ",
				new ImageIcon(
						App.class
								.getResource("/com/mxgraph/examples/swing/images/entity.png")),
				"entity", 100, 100, "[]");

		
//		
//		JLabel entry = new JLabel(new ImageIcon(getClass().getResource("/com/mxgraph/examples/swing/images/ellipse.png")));
//
//		add(entry);
//		
//		entry.setToolTipText("state");
//		//entry.setText("state");
//				
//		mxCell cell = new mxCell("State", new mxGeometry(0, 0, 40, 40),
//				"roundImage;image=/com/mxgraph/examples/swing/images/event.png");
//		cell.setVertex(true);
//
//		mxRectangle bounds = (mxGeometry) cell.getGeometry().clone();
//		final mxGraphTransferable t = new mxGraphTransferable(
//				new Object[] { cell }, bounds);
//
//		
//		entry.addMouseListener(new MouseListener()
//		{
//			/*
//			 * (non-Javadoc)
//			 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
//			 */
//			public void mousePressed(MouseEvent e)
//			{
//				setSelectionEntry(entry, t);
//			}
//
//			/*
//			 * (non-Javadoc)
//			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
//			 */
//			public void mouseClicked(MouseEvent e)
//			{
//			}
//
//			/*
//			 * (non-Javadoc)
//			 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
//			 */
//			public void mouseEntered(MouseEvent e)
//			{
//			}
//
//			/*
//			 * (non-Javadoc)
//			 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
//			 */
//			public void mouseExited(MouseEvent e)
//			{
//			}
//
//			/*
//			 * (non-Javadoc)
//			 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
//			 */
//			public void mouseReleased(MouseEvent e)
//			{
//			}
//
//		});
//
//		// Install the handler for dragging nodes into a graph
//		DragGestureListener dragGestureListener = new DragGestureListener()
//		{
//			/**
//			 * 
//			 */
//			public void dragGestureRecognized(DragGestureEvent e)
//			{
//				e.startDrag(null, mxSwingConstants.EMPTY_IMAGE, new Point(),
//								t, null);
//			}
//
//		};
//
//		DragSource dragSource = new DragSource();
//		dragSource.createDefaultDragGestureRecognizer(entry,
//				DnDConstants.ACTION_COPY, dragGestureListener);

		
		//addSeparator();

/*		// Gets the list of available fonts from the local graphics environment
		// and adds some frequently used fonts at the beginning of the list
		GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		List<String> fonts = new ArrayList<String>();
		fonts.addAll(Arrays.asList(new String[] { "Helvetica", "Verdana",
				"Times New Roman", "Garamond", "Courier New", "-" }));
		fonts.addAll(Arrays.asList(env.getAvailableFontFamilyNames()));

		final JComboBox fontCombo = new JComboBox(fonts.toArray());
		fontCombo.setEditable(true);
		fontCombo.setMinimumSize(new Dimension(120, 0));
		fontCombo.setPreferredSize(new Dimension(120, 0));
		fontCombo.setMaximumSize(new Dimension(120, 100));
		add(fontCombo);

		fontCombo.addActionListener(new ActionListener()
		{
			*//**
			 * 
			 *//*
			public void actionPerformed(ActionEvent e)
			{
				String font = fontCombo.getSelectedItem().toString();

				if (font != null && !font.equals("-"))
				{
					mxGraph graph = editor.getGraphComponent().getGraph();
					graph.setCellStyles(mxConstants.STYLE_FONTFAMILY, font);
				}
			}
		});

		final JComboBox sizeCombo = new JComboBox(new Object[] { "6pt", "8pt",
				"9pt", "10pt", "12pt", "14pt", "18pt", "24pt", "30pt", "36pt",
				"48pt", "60pt" });
		sizeCombo.setEditable(true);
		sizeCombo.setMinimumSize(new Dimension(65, 0));
		sizeCombo.setPreferredSize(new Dimension(65, 0));
		sizeCombo.setMaximumSize(new Dimension(65, 100));
		add(sizeCombo);

		sizeCombo.addActionListener(new ActionListener()
		{
			*//**
			 * 
			 *//*
			public void actionPerformed(ActionEvent e)
			{
				mxGraph graph = editor.getGraphComponent().getGraph();
				graph.setCellStyles(mxConstants.STYLE_FONTSIZE, sizeCombo
						.getSelectedItem().toString().replace("pt", ""));
			}
		});

		addSeparator();

		add(editor.bind("Bold", new FontStyleAction(true),
				"/com/mxgraph/examples/swing/images/bold.gif"));
		add(editor.bind("Italic", new FontStyleAction(false),
				"/com/mxgraph/examples/swing/images/italic.gif"));

		addSeparator();

		add(editor.bind("Left", new KeyValueAction(mxConstants.STYLE_ALIGN,
				mxConstants.ALIGN_LEFT),
				"/com/mxgraph/examples/swing/images/left.gif"));
		add(editor.bind("Center", new KeyValueAction(mxConstants.STYLE_ALIGN,
				mxConstants.ALIGN_CENTER),
				"/com/mxgraph/examples/swing/images/center.gif"));
		add(editor.bind("Right", new KeyValueAction(mxConstants.STYLE_ALIGN,
				mxConstants.ALIGN_RIGHT),
				"/com/mxgraph/examples/swing/images/right.gif"));

		addSeparator();

		add(editor.bind("Font", new ColorAction("Font",
				mxConstants.STYLE_FONTCOLOR),
				"/com/mxgraph/examples/swing/images/fontcolor.gif"));
		add(editor.bind("Stroke", new ColorAction("Stroke",
				mxConstants.STYLE_STROKECOLOR),
				"/com/mxgraph/examples/swing/images/linecolor.gif"));
		add(editor.bind("Fill", new ColorAction("Fill",
				mxConstants.STYLE_FILLCOLOR),
				"/com/mxgraph/examples/swing/images/fillcolor.gif"));

		addSeparator();

		final mxGraphView view = editor.getGraphComponent().getGraph()
				.getView();
		final JComboBox zoomCombo = new JComboBox(new Object[] { "400%",
				"200%", "150%", "100%", "75%", "50%", mxResources.get("page"),
				mxResources.get("width"), mxResources.get("actualSize") });
		zoomCombo.setEditable(true);
		zoomCombo.setMinimumSize(new Dimension(75, 0));
		zoomCombo.setPreferredSize(new Dimension(75, 0));
		zoomCombo.setMaximumSize(new Dimension(75, 100));
		zoomCombo.setMaximumRowCount(9);
		add(zoomCombo);

		// Sets the zoom in the zoom combo the current value
		mxIEventListener scaleTracker = new mxIEventListener()
		{
			*//**
			 * 
			 *//*
			public void invoke(Object sender, mxEventObject evt)
			{
				ignoreZoomChange = true;

				try
				{
					zoomCombo.setSelectedItem((int) Math.round(100 * view
							.getScale())
							+ "%");
				}
				finally
				{
					ignoreZoomChange = false;
				}
			}
		};

		// Installs the scale tracker to update the value in the combo box
		// if the zoom is changed from outside the combo box
		view.getGraph().getView().addListener(mxEvent.SCALE, scaleTracker);
		view.getGraph().getView().addListener(mxEvent.SCALE_AND_TRANSLATE,
				scaleTracker);

		// Invokes once to sync with the actual zoom value
		scaleTracker.invoke(null, null);

		zoomCombo.addActionListener(new ActionListener()
		{
			*//**
			 * 
			 *//*
			public void actionPerformed(ActionEvent e)
			{
				mxGraphComponent graphComponent = editor.getGraphComponent();

				// Zoomcombo is changed when the scale is changed in the diagram
				// but the change is ignored here
				if (!ignoreZoomChange)
				{
					String zoom = zoomCombo.getSelectedItem().toString();

					if (zoom.equals(mxResources.get("page")))
					{
						graphComponent.setPageVisible(true);
						graphComponent
								.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_PAGE);
					}
					else if (zoom.equals(mxResources.get("width")))
					{
						graphComponent.setPageVisible(true);
						graphComponent
								.setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);
					}
					else if (zoom.equals(mxResources.get("actualSize")))
					{
						graphComponent.zoomActual();
					}
					else
					{
						try
						{
							zoom = zoom.replace("%", "");
							double scale = Math.min(16, Math.max(0.01,
									Double.parseDouble(zoom) / 100));
							graphComponent.zoomTo(scale, graphComponent
									.isCenterZoom());
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog(editor, ex
									.getMessage());
						}
					}
				}
			}
		});*/
	}
}
