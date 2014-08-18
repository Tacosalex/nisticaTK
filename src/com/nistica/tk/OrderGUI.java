package com.nistica.tk;

import javax.swing.*;
import javax.swing.plaf.metal.MetalComboBoxButton;

import com.nistica.panels.HelpDialog;
import com.nistica.panels.OrderDialog;
import com.nistica.tk.MenuItem.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class OrderGUI implements ScrollPaneConstants {

	public static JFrame frame;
	public static JScrollPane menuPane, cartPane;
	public static JSplitPane menuCartPane, bigPane;
	public static JComboBox foodTypeChooser;
	public static JPanel controlsPanel, newMenuItem;
	public static MenuPanel menuItemHolder;
	public static CartPanel cartItemHolder;
	//public static OrderPanel orderItemHolder;
	
	public static int numberOfMenuItems;
	
	public static final Color MENUCOLOR = new Color(230,242,242);
	public static final Color CONTROLCOLOR = new Color(57, 183, 250);//Color(47,211,214);
	
	public static List<MenuItem> theMenu;
	static MenuParser menuParser;
	public static HSSFTester hssftest;
	
	//DINNER HAS 3 SPACES, LUNCH HAS 2 FOR EACH CATEGORY
	static HashMap<String, String> foodCategories;
	static String[] foodTypes = {"Lunch", "  Soups", "  Entrees", 
		"Dinner", "   Appetizers and Soups", "   Salad", "   Chef's Special", 
		"   Entrees-Main", "   Thai Curry", "   Duck Entrees", "   Noodles and Fried Rice"};
	static String[] lunchTypes = {"Lunch", "  Soups", "  Entrees"};
	static String[] dinnerTypes = {"   Appetizers and Soups", "   Salad", "   Chef's Special", 
		"   Entrees-Main", "   Thai Curry", "   Duck Entrees", "   Noodles and Fried Rice"};

	public OrderGUI() {
	}

	private static void createAndShowGUI() {
		
		//Used by the drop down to map the chosen element with the name used in MenuParser
		foodCategories = new HashMap<String, String>();
		foodCategories.put(foodTypes[0], MenuParser.LUNCH);
		foodCategories.put(foodTypes[1], MenuParser.SOUP);
		foodCategories.put(foodTypes[2], MenuParser.ENTREE);
		foodCategories.put(foodTypes[3], MenuParser.DINNER);
		foodCategories.put(foodTypes[4], MenuParser.APPETIZERS);
		foodCategories.put(foodTypes[5], MenuParser.SALAD);
		foodCategories.put(foodTypes[6], MenuParser.SPECIAL);
		foodCategories.put(foodTypes[7], MenuParser.ENTREE_MAIN);
		foodCategories.put(foodTypes[8], MenuParser.THAI_CURRY);
		foodCategories.put(foodTypes[9], MenuParser.DUCK);
		foodCategories.put(foodTypes[10], MenuParser.NOODLES_FRIED);
		
		
		menuParser = new MenuParser();
		theMenu = menuParser.readMenu("/other/menuFull.xml", MenuParser.LUNCH, MenuParser.ENTREE);
		hssftest = new HSSFTester();
		numberOfMenuItems = theMenu.size();
		
		
		// Create the new JFrame object
		frame = new JFrame("Nistica Thai Kitchen Ordering System");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension splitSize = new Dimension(519, 600);
		
		foodTypeChooser = new JComboBox(foodTypes);
		foodTypeChooser.setPreferredSize(new Dimension(100, 20));
		foodTypeChooser.setMaximumSize(new Dimension(Integer.MAX_VALUE, foodTypeChooser.getMinimumSize().height));
		foodTypeChooser.setBackground(new Color(40, 40, 40));
		foodTypeChooser.setForeground(Color.white);
		foodTypeChooser.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent a) {
				String course, category;
				JComboBox box = (JComboBox)a.getSource();
				String choice = (String)box.getSelectedItem();
				
				if(Arrays.asList(lunchTypes).contains(choice))
				{
					course = MenuParser.LUNCH;
					//There are two kinds of entrees, so need to check if it is the lunch or dinner entree
					//The lunch entree is the second item in the list, so it has an index of 2,
					//and the dinner one is greater than that
					if(choice.equals("Entrees") && box.getSelectedIndex()>2)
						course = MenuParser.DINNER;
				}
				else
					course = MenuParser.DINNER;
				System.out.println(course + "|||" + foodCategories.get(choice));
				//figure out what category the selected item was in and then call
				theMenu = menuParser.readMenu("/other/menuFull.xml", course, foodCategories.get(choice));
				numberOfMenuItems = theMenu.size();
				
				reInitMenuItems();
			}
			
		});
		// Create all the menu item objects
		menuItemHolder = new MenuPanel(numberOfMenuItems+1);
		menuItemHolder.add(foodTypeChooser);
		menuItemHolder.setLayout(new BoxLayout(menuItemHolder, BoxLayout.PAGE_AXIS));
		for (MenuItem menuItem : theMenu) {
			//newMenuItem = new MenuItem();
			menuItemHolder.add(menuItem);
		}
		menuItemHolder.setBackground(MENUCOLOR);
		//Initialize the Cart Panel
		cartItemHolder = new CartPanel(numberOfMenuItems);
		cartItemHolder.setLayout(new BoxLayout(cartItemHolder, BoxLayout.PAGE_AXIS));
		cartItemHolder.setBackground(MENUCOLOR);
		// Create a new JSplitPane with the two halves as the menu and cart
		menuPane = new JScrollPane(menuItemHolder, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
		menuPane.setPreferredSize(splitSize);
		menuPane.getVerticalScrollBar().setUnitIncrement(14);
		cartPane = new JScrollPane(cartItemHolder, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
		cartPane.setPreferredSize(splitSize);
		cartPane.getVerticalScrollBar().setUnitIncrement(14);
		
		menuCartPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuPane, cartPane);
		menuCartPane.setDividerLocation(525);
		menuCartPane.setEnabled(false);
		//Create the controls panel at the top of the GUI
		controlsPanel = new JPanel();
		SpringLayout springControls = new SpringLayout();
		controlsPanel.setLayout(springControls);
		controlsPanel.setBackground(CONTROLCOLOR);
		//add all of the control buttons here, such as order, clear cart, help, change order (maybe), etc
		//the help button will create a new popup dialog box telling the user all the controls for using the program
		//the order button will prompt the user (possibly with a dialog box, but maybe with JOptionPane) if they want to submit
		//the clear cart button will do the same as above but with a clear message
		//the change order button will probably be tricky.  if possible: this button would go to the database and access the order
		//that corresponds to the user that prompted the access.  If there is an order found, then the session is "reopened" so the
		//user can change the order and resubmit it.  If there is no saved order, then a dialog box will open up informing the user
		//of this.
		//any other buttons that I can think of will be added as necessary in the best possible location
		JButton helpButton = new JButton("Help");
		helpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// display/center the jdialog when the button is pressed				
				HelpDialog help = new HelpDialog();
			 	help.setLocation(new Point(265,200));
			 	help.pack();
			 	help.setVisible(true);
			}
		});
		springControls.putConstraint(SpringLayout.NORTH, helpButton, 5, SpringLayout.NORTH, controlsPanel);
		springControls.putConstraint(SpringLayout.WEST, helpButton, 5, SpringLayout.WEST, controlsPanel);
		controlsPanel.add(helpButton);
		JButton orderButton = new JButton("Order");
		orderButton.setPreferredSize(new Dimension(150, 50));
		orderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				OrderDialog order = new OrderDialog(cartItemHolder);				
			 	order.setLocation(new Point(145,30));
			 	order.setVisible(true);
			}
		});
		springControls.putConstraint(SpringLayout.NORTH, orderButton, 5, SpringLayout.NORTH, controlsPanel);
		springControls.putConstraint(SpringLayout.EAST, orderButton, -10, SpringLayout.EAST, controlsPanel);
		controlsPanel.add(orderButton);
		JButton clearCart = new JButton("Clear");
		clearCart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				int answer = JOptionPane.showConfirmDialog(clearCart, "Are you sure you want to clear your cart?","Clear Cart?",JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION){
					int i;
					int numItems = cartItemHolder.getComponents().length;
					for (i=0;i<numItems;i++) {
						cartItemHolder.remove(cartItemHolder.getComponents()[0]);
						cartItemHolder.repaint();
						cartItemHolder.revalidate();
					}
				}
			}
		});
		springControls.putConstraint(SpringLayout.NORTH, clearCart, 5, SpringLayout.NORTH, controlsPanel);
		springControls.putConstraint(SpringLayout.WEST, clearCart, 15, SpringLayout.EAST, helpButton);
		controlsPanel.add(clearCart);
		JLabel menuLabel = new JLabel("Menu");
		
		springControls.putConstraint(SpringLayout.SOUTH, menuLabel, 5, SpringLayout.SOUTH, controlsPanel);
		springControls.putConstraint(SpringLayout.WEST, menuLabel,20, SpringLayout.WEST, controlsPanel);
		menuLabel.setFont(new Font("Freestyle Script", Font.PLAIN, 58));
		menuLabel.setForeground(Color.WHITE);
		controlsPanel.add(menuLabel);
		
		JLabel cartLabel = new JLabel("Cart");
		springControls.putConstraint(SpringLayout.SOUTH, cartLabel, 5, SpringLayout.SOUTH, controlsPanel);
		springControls.putConstraint(SpringLayout.EAST, cartLabel, -430, SpringLayout.EAST, controlsPanel);
		cartLabel.setFont(new Font("Freestyle Script", Font.PLAIN, 58));
		cartLabel.setForeground(Color.WHITE);
		controlsPanel.add(cartLabel);
		//Create the Large JSplitPane tha contains the menu, the cart, and the control panel
		bigPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlsPanel, menuCartPane);
		bigPane.setDividerLocation(100);
		bigPane.setEnabled(false);
		frame.getContentPane().add(bigPane, BorderLayout.CENTER);
		// Pack frame and set it to visible
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		
		menuItemHolder.getPartner(cartItemHolder);
		cartItemHolder.getPartner(menuItemHolder);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
	private static void reInitMenuItems(){
		menuItemHolder.updateSize(numberOfMenuItems);
		menuItemHolder.removeAll();//removes all components
		
		menuItemHolder.add(foodTypeChooser);
		for (MenuItem menuItem : theMenu) {
			menuItemHolder.add(menuItem);
		}
	}
	
}