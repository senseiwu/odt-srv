val STEPINT:Int = 1

def fun(xmin:Int,xmax:Int,ymin:Int,ymax:Int) = {
  def loop(mxmin:Int,mxmax:Int,mymin:Int,mymax:Int):Unit = {
    if(mxmax>xmax&&mymax>ymax) return
    println("-- osm data get ", mxmin, mxmax, mymin, mymax)
    if(mymax <= ymax) loop(mxmin,mxmax,mymin+STEPINT,mymax+STEPINT)
    else loop(mxmin+STEPINT,mxmax+STEPINT,ymin,ymin+STEPINT)
  }
  loop(xmin,xmin+STEPINT,ymin,ymin+STEPINT)
}
//       xmin       xmax       ymin       ymax
def fun2(minlat:Int,maxlat:Int,minlon:Int,maxlon:Int) = {
  def loop(mminlat:Int,mmaxlat:Int,mminlon:Int,mmaxlon:Int):Unit = {
    if(mmaxlat>maxlat && mmaxlon>maxlon) return
    println("-- osm data get ", mminlat, mmaxlat, mminlon, mmaxlon)
    if(mmaxlon <= maxlon) loop(mminlat,mmaxlat,mminlon+STEPINT,mmaxlon+STEPINT)
    else loop(mminlat+STEPINT,mmaxlat+STEPINT,minlat,minlat+STEPINT)
  }
  loop(minlat, minlat+STEPINT, minlon, minlon+STEPINT)
}

fun2(0,4,0,4)