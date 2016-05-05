package specctra.varie;

import freert.planar.PlaArea;

/** 
 * Deescribes a named keepout belonging to a package, 
 */
public final class DsnPackageKeepout implements java.io.Serializable
   {
   private static final long serialVersionUID = 1L;

   public final String name;
   public final PlaArea area;
   public final int layer;

   public DsnPackageKeepout(String p_name, PlaArea p_area, int p_layer)
      {
      name = p_name;
      area = p_area;
      layer = p_layer;
      }
   }