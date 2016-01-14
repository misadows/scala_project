package models


import anorm.{Row, SimpleSql, SQL, SqlQuery}
import play.api.db.DB
import play.api.Play.current

case class Req(id: Long, criteria: String)

object Req{
  val sql: SqlQuery = SQL("select * from req")

  def insert(req: Req): Long = {
    DB.withConnection { implicit connection =>
      val id: Option[Long] = SQL("insert into Req(criteria) values ({criteria})")
                              .on('criteria -> req.criteria).executeInsert()
      id.get
    }
  }

  def getCriteria(id: Long): String = DB.withConnection() {
    implicit connection =>
      val sql: SimpleSql[Row] = SQL("select * from Req where id = {id}").on("id" -> id)
      val data = sql().map ( row => row[String]("criteria")).toList
      data.head
  }

  def getAll: List[Req] = DB.withConnection() {
    implicit connection =>
      sql().map ( row =>
        Req(row[Long]("id"), row[String]("criteria"))
      ).toList
  }

  def exists(id: Long): Boolean = DB.withConnection() {
    implicit connection =>
      val sql: SimpleSql[Row] = SQL("select * from Req where id = {id}").on("id" -> id)
      val data = sql().map ( row => row[Long]("id")).toList
      data.nonEmpty
  }
}

case class ReqData(id: Long, data: String, req: Long)

object ReqData{
  def insert(reqData: ReqData): Long = {
    DB.withConnection { implicit connection =>
      val id: Option[Long] = SQL("insert into ReqData(data, req) values ({criteria}, {req})")
        .on('criteria -> reqData.data, 'req -> reqData.req).executeInsert()
      id.get
    }
  }


  def getAll(req: Long): List[ReqData] = DB.withConnection() {
    implicit connection =>{
      val sql: SimpleSql[Row] = SQL("select * from ReqData where req = {reqd}").on("reqd" -> req)
      sql().map ( row =>
        ReqData(row[Long]("ReqData.id"), row[String]("data"), row[Long]("req"))
      ).toList
    }
  }
}