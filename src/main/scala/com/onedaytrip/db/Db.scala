package com.onedaytrip.db

/**
 * Created by tomek on 6/15/15.
 */

import java.sql.Date

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.jdbc.meta.MTable
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Db {
  def getDB = DatabaseConfig.forConfig[JdbcProfile]("h2_dc")
  val dc = getDB
  val driver = dc.driver
  import driver.api._

  private def createTableIfNotExists(tables: TableQuery[_ <: Table[_]]*): Future[Seq[Unit]] = {
    Future.sequence(
      tables map { table =>
        dc.db.run(MTable.getTables(table.baseTableRow.tableName)).flatMap { result =>
          if (result.isEmpty) dc.db.run(table.schema.create)
          else Future.successful(())
        }
      }
    )
  }

  def create = createTableIfNotExists(profiles, users, chatMessages, attractions, trips)

  def createForce = dc.db.run(
    DBIO.seq(
      (profiles.schema ++ users.schema ++ chatMessages.schema ++ attractions.schema ++ trips.schema).create
    ))
  def close = dc.db.close()

  val users = TableQuery[User]
  val attractions = TableQuery[Attraction]
  val trips = TableQuery[Trip]
  val profiles = TableQuery[Profile]
  val chatMessages = TableQuery[ChatMessage]

  class User(tag:Tag) extends Table[(Int,String,String)](tag, "USER") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def password = column[String]("PASSWORD")
    def profileId = column[Int]("PROFILE_ID")
    def profileFk = foreignKey("PROFILE_FK", profileId, profiles)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
    def * : ProvenShape[(Int, String, String)] = (id, name, password)
  }

  class Profile(tag:Tag) extends Table[(Int,Char,Date)](tag, "PROFILE") {
    def id = column[Int]("ID")
    def gender = column[Char]("GENDER")
    def birthDate = column[Date]("BIRTH_DATE")
    override def * : ProvenShape[(Int, Char, Date)] = (id, gender, birthDate)
  }

  class ChatMessage(tag:Tag) extends Table[(Int,Int,String)](tag,"CHAT_MESSAGE") {
    def id = column[Int]("ID")
    def userId = column[Int]("USER_ID")
    def text = column[String]("TEXT")
    def userFk = foreignKey("USER_FK", userId, chatMessages)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
    override def * : ProvenShape[(Int, Int, String)] = (id, userId, text)
  }

  class Attraction(tag:Tag) extends Table[(Int,String,Long,Int,Int,String,String,String)](tag, "ATTRACTION") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def rating = column[Long]("RATING")
    def duration = column[Int]("DURATION")
    def price = column[Int]("PRICE")
    def atrType = column[String]("TYPE")
    def lat = column[String]("LAT")
    def lon = column[String]("LON")
    override def * : ProvenShape[(Int, String, Long, Int, Int, String, String, String)] = (id,name,rating,duration,price,atrType,lat,lon)
  }

  class Trip(tag:Tag) extends Table[(Int,Int)](tag,"TRIP") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def attractionId = column[Int]("ATTRACTION_ID")
    def attraction = foreignKey("ATTRACTION_FK", attractionId, attractions)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
    override def * : ProvenShape[(Int, Int)] = (id,attractionId)
  }
}