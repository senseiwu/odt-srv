import java.math.BigDecimal

import com.onedaytrip.domain.Coordinate

//
//import java.sql.Date
//
//import com.onedaytrip.dao.DbDao
//import org.joda.time.DateTime
//import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
//
//import scala.concurrent.Future
//import scala.util.{Failure, Success}
//import scala.concurrent.ExecutionContext.Implicits.global
//case class User(n:String, p:String) {}
//val u:List[User] = List(User("A","@A"), User("B", "@B"))
//u.map(_.p)
//u.flatMap(_.p)
////def get(n:String):DBIO[Option[String]] = user.filter(_.name === n).map(_.password)
//
//def getPassword(name:String) = u.filter(_.n == name).map(_.p).head
//def getUser(name:String) = u.filter(_.n == name).head
////def exist(name:String) = u.exists(Nil)
//getPassword("A")
//getUser("A")
//
//val xmas = (new DateTime).withYear(2013)
//  .withMonthOfYear(12)
//  .withDayOfMonth(25)
//
//val fr:DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
//
//println(xmas.toString(fr))
