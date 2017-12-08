package cn.wlwl.cube.analyse.bean.alart3G;

public class PointDouble
{
  public double x;//经度
  public double y;//维度

 public PointDouble(double x, double y)
  {
    this.x = x;
    this.y = y; 
  }

  public String toString() {
    return "x=" + this.x + ", y=" + this.y;
  }
}