/*
 *  Copyright (C) 2014  Alfons Wirtz  
 *   website www.freerouting.net
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License at <http://www.gnu.org/licenses/> 
 *   for more details.
 *
 * ObjectListWindow.java
 *
 * Created on 7. Maerz 2005, 09:26
 */

package gui.win;

import gui.BoardFrame;
import gui.GuiSubWindowSavable;
import gui.varie.GuiResources;
import java.util.Collection;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import board.infos.PrintableInfo;

/**
 * Abstract class for windows displaying a list of objects
 *
 * @author Alfons Wirtz
 */
public abstract class WindowObjectList extends GuiSubWindowSavable
   {
   private static final long serialVersionUID = 1L;

   //  Fills the list with the objects to display.
   abstract protected void fill_list();
   abstract protected void select_instances();

   private final javax.swing.JPanel main_panel;

   private javax.swing.JScrollPane list_scroll_pane = null;
   protected javax.swing.JLabel list_empty_message;

   private DefaultListModel<PrintableInfo> list_model = null;
   protected JList<PrintableInfo> gui_list;

   protected final javax.swing.JPanel south_panel;

   // The sub windows with information about selected object 
   protected final Collection<WindowObjectInfo> subwindows = new LinkedList<WindowObjectInfo>();

   private final GuiResources resources;

   protected static final int DEFAULT_TABLE_SIZE = 20;
   
   
   public WindowObjectList(BoardFrame p_board_frame)
      {
      super(p_board_frame);
      
      resources = board_frame.newGuiResources("gui.resources.WindowObjectList");

      // create main panel
      this.main_panel = new javax.swing.JPanel();
      main_panel.setLayout(new java.awt.BorderLayout());
      this.add(main_panel);

      // create a panel for adding buttons
      this.south_panel = new javax.swing.JPanel();
      south_panel.setLayout(new java.awt.BorderLayout());
      main_panel.add(south_panel, java.awt.BorderLayout.SOUTH);

      javax.swing.JPanel button_panel = new javax.swing.JPanel();
      button_panel.setLayout(new java.awt.BorderLayout());
      this.south_panel.add(button_panel, java.awt.BorderLayout.CENTER);

      javax.swing.JPanel north_button_panel = new javax.swing.JPanel();
      button_panel.add(north_button_panel, java.awt.BorderLayout.NORTH);

      javax.swing.JButton show_button = new javax.swing.JButton(resources.getString("info"));
      show_button.setToolTipText(resources.getString("info_tooltip"));
      ShowListener show_listener = new ShowListener();
      show_button.addActionListener(show_listener);
      north_button_panel.add(show_button);

      javax.swing.JButton instance_button = new javax.swing.JButton(resources.getString("select"));
      instance_button.setToolTipText(resources.getString("select_tooltip"));
      SelectListener instance_listener = new SelectListener();
      instance_button.addActionListener(instance_listener);
      north_button_panel.add(instance_button);

      javax.swing.JPanel south_button_panel = new javax.swing.JPanel();
      button_panel.add(south_button_panel, java.awt.BorderLayout.SOUTH);

      javax.swing.JButton invert_button = new javax.swing.JButton(resources.getString("invert"));
      invert_button.setToolTipText(resources.getString("invert_tooltip"));
      invert_button.addActionListener(new InvertListener());
      south_button_panel.add(invert_button);

      javax.swing.JButton recalculate_button = new javax.swing.JButton(resources.getString("recalculate"));
      recalculate_button.setToolTipText(resources.getString("recalculate_tooltip"));
      RecalculateListener recalculate_listener = new RecalculateListener();
      recalculate_button.addActionListener(recalculate_listener);
      south_button_panel.add(recalculate_button);

      this.list_empty_message = new javax.swing.JLabel(resources.getString("list_empty"));
      this.list_empty_message.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

      /** Dispose this window and all subwindows when closing the window. */
      this.addWindowListener(new java.awt.event.WindowAdapter()
         {
            public void windowClosing(java.awt.event.WindowEvent evt)
               {
               dispose();
               }
         });
      }

   public void setVisible(boolean p_value)
      {
      if (p_value == true)
         {
         recalculate();
         }
      super.setVisible(p_value);
      }

   protected void recalculate()
      {
      if (this.list_scroll_pane != null)
         {
         main_panel.remove(this.list_scroll_pane);
         }
      main_panel.remove(this.list_empty_message);
      
      // Create display list
      this.list_model = new DefaultListModel<PrintableInfo>();
      this.gui_list = new JList<PrintableInfo>(list_model);
      
      this.gui_list.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      
      fill_list();
      
      if (this.gui_list.getVisibleRowCount() > 0)
         {
         list_scroll_pane = new javax.swing.JScrollPane(this.gui_list);
         main_panel.add(list_scroll_pane, java.awt.BorderLayout.CENTER);
         }
      else
         {
         main_panel.add(list_empty_message, java.awt.BorderLayout.CENTER);
         }
      this.pack();

      this.gui_list.addMouseListener(new java.awt.event.MouseAdapter()
         {
            public void mouseClicked(java.awt.event.MouseEvent evt)
               {
               if (evt.getClickCount() > 1)
                  {
                  select_instances();
                  }
               }
         });
      }

   public void dispose()
      {
      for (WindowObjectInfo curr_subwindow : this.subwindows)
         {
         if (curr_subwindow != null)
            {
            curr_subwindow.dispose();
            }
         }
      super.dispose();
      }

   protected void add_to_list(PrintableInfo p_object)
      {
      list_model.addElement(p_object);
      }


   /** Listens to the button for showing the selected padstacks */
   private class ShowListener implements java.awt.event.ActionListener
      {
      public void actionPerformed(java.awt.event.ActionEvent p_evt)
         {
         @SuppressWarnings("deprecation")
         Object[] selected_objects = gui_list.getSelectedValues();
         if (selected_objects.length <= 0)
            {
            return;
            }
         java.util.Collection<PrintableInfo> object_list = new java.util.LinkedList<PrintableInfo>();
         for (int i = 0; i < selected_objects.length; ++i)
            {
            object_list.add((PrintableInfo) (selected_objects[i]));
            }
         freert.planar.PlaCoordTransform coordinate_transform = board_frame.board_panel.itera_board.coordinate_transform;
         WindowObjectInfo new_window = WindowObjectInfo.display(resources.getString("window_title"), object_list, board_frame, coordinate_transform);
         java.awt.Point loc = getLocation();
         java.awt.Point new_window_location = new java.awt.Point((int) (loc.getX() + WINDOW_OFFSET), (int) (loc.getY() + WINDOW_OFFSET));
         new_window.setLocation(new_window_location);
         subwindows.add(new_window);
         }

      private static final int WINDOW_OFFSET = 30;
      }

   /** Listens to the button for showing the selected incompletes */
   private class SelectListener implements java.awt.event.ActionListener
      {
      public void actionPerformed(java.awt.event.ActionEvent p_evt)
         {
         select_instances();
         }
      }

   /** Listens to the button for inverting the selection */
   private class InvertListener implements java.awt.event.ActionListener
      {
      public void actionPerformed(java.awt.event.ActionEvent p_evt)
         {
         if (list_model == null)
            {
            return;
            }
         int[] new_selected_indices = new int[list_model.getSize() - gui_list.getSelectedIndices().length];
         int curr_index = 0;
         for (int i = 0; i < list_model.getSize(); ++i)
            {
            if (!gui_list.isSelectedIndex(i))
               {
               new_selected_indices[curr_index] = i;
               ++curr_index;
               }
            }
         gui_list.setSelectedIndices(new_selected_indices);
         }
      }

   /**
    * Saves also the filter string to disk.
    */
   @Override
   public void save(java.io.ObjectOutputStream p_object_stream)
      {
      int[] selected_indices;
      if (this.gui_list != null)
         {
         selected_indices = this.gui_list.getSelectedIndices();
         }
      else
         {
         selected_indices = new int[0];
         }
      try
         {
         p_object_stream.writeObject(selected_indices);
         }
      catch (java.io.IOException e)
         {
         System.out.println("WindowObjectList.save: save failed");
         }
      super.save(p_object_stream);
      }

   @Override
   public boolean read(java.io.ObjectInputStream p_object_stream)
      {
      int[] saved_selected_indices = null;
      try
         {
         saved_selected_indices = (int[]) p_object_stream.readObject();
         }
      catch (Exception e)
         {
         System.out.println("WindowObjectListWithFilter.read: read failed");
         return false;
         }
      boolean result = super.read(p_object_stream);
      if (this.gui_list != null && saved_selected_indices.length > 0)
         {
         this.gui_list.setSelectedIndices(saved_selected_indices);
         }
      return result;
      }

   /** Listens to the button for recalculating the content of the window */
   private class RecalculateListener implements java.awt.event.ActionListener
      {
      public void actionPerformed(java.awt.event.ActionEvent p_evt)
         {
         recalculate();
         }
      }
   }
