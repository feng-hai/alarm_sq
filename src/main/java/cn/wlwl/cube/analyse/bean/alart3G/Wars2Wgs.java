package cn.wlwl.cube.analyse.bean.alart3G;

import java.io.InputStream;
import java.io.ObjectInputStream;

public class Wars2Wgs
{
  static double[] X = new double[297000];
  static double[] Y = new double[297000];
  static {
	  try {
		init(Wars2Wgs.class.getResourceAsStream("axisoffset.dat"));
	} catch (Exception e) {
		e.printStackTrace();
	}
  }
  
  /*
  public static void main(String[] args) throws Exception {
    
    System.out.println(c2s(new PointDouble(118.776695D, 32.076898D)));
    System.out.println(s2c(new PointDouble(112.56966199999999D, 38.841886000000002D)));
  }
*/
  public static void init(InputStream inputStream) throws Exception
  {
    ObjectInputStream in = new ObjectInputStream(inputStream);
    try {
      int i = 0;
      while (in.available() > 0) {
        if ((i & 0x1) == 1)
          Y[(i - 1 >> 1)] = (in.readInt() / 100000.0D);
        else {
          X[(i >> 1)] = (in.readInt() / 100000.0D);
        }
        ++i;
      }
    } finally {
      if (in != null)
        in.close();
    }
  }
/**
 * 纠偏
 * @param pt
 * @return
 */
  public static PointDouble s2c(PointDouble pt)
  {
    int cnt = 10;
    double x = pt.x; double y = pt.y;
    while (cnt-- > 0) {
      if ((x < 71.998900000000006D) || (x > 137.8998D) || (y < 9.999700000000001D) || (y > 54.8996D))
        return pt;
      int ix = (int)(10.0D * (x - 72.0D));
      int iy = (int)(10.0D * (y - 10.0D));
      double dx = (x - 72.0D - (0.1D * ix)) * 10.0D;
      double dy = (y - 10.0D - (0.1D * iy)) * 10.0D;
      x = (x + pt.x + (1.0D - dx) * (1.0D - dy) * X[(ix + 660 * iy)] + dx * (1.0D - dy) * X[(ix + 660 * iy + 1)] + dx * dy * X[(ix + 660 * iy + 661)] + (1.0D - dx) * dy * X[(ix + 660 * iy + 660)] - x) / 2.0D;
      y = (y + pt.y + (1.0D - dx) * (1.0D - dy) * Y[(ix + 660 * iy)] + dx * (1.0D - dy) * Y[(ix + 660 * iy + 1)] + dx * dy * Y[(ix + 660 * iy + 661)] + (1.0D - dx) * dy * Y[(ix + 660 * iy + 660)] - y) / 2.0D;
    }
    return new PointDouble(x, y);
  }

  public static PointDouble c2s(PointDouble pt)
  {
    int cnt = 10;
    double x = pt.x; double y = pt.y;
    while (cnt-- > 0) {
      if ((x < 71.998900000000006D) || (x > 137.8998D) || (y < 9.999700000000001D) || (y > 54.8996D))
        return pt;
      int ix = (int)(10.0D * (x - 72.0D));
      int iy = (int)(10.0D * (y - 10.0D));
      double dx = (x - 72.0D - (0.1D * ix)) * 10.0D;
      double dy = (y - 10.0D - (0.1D * iy)) * 10.0D;
      x = (x + pt.x - ((1.0D - dx) * (1.0D - dy) * X[(ix + 660 * iy)]) - (dx * (1.0D - dy) * X[(ix + 660 * iy + 1)]) - (dx * dy * X[(ix + 660 * iy + 661)]) - ((1.0D - dx) * dy * X[(ix + 660 * iy + 660)]) + x) / 2.0D;
      y = (y + pt.y - ((1.0D - dx) * (1.0D - dy) * Y[(ix + 660 * iy)]) - (dx * (1.0D - dy) * Y[(ix + 660 * iy + 1)]) - (dx * dy * Y[(ix + 660 * iy + 661)]) - ((1.0D - dx) * dy * Y[(ix + 660 * iy + 660)]) + y) / 2.0D;
    }
    return new PointDouble(x, y);
  }
}