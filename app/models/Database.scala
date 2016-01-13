package models

case class Req(id: Long, criteria: String)

object Req{
  import anorm.SQL
  import play.api.db.DB
  import play.api.Play.current
  import anorm.SqlQuery

  val sql: SqlQuery = SQL("select * from req")

  def insert(req: Req): Long = {
    DB.withConnection { implicit connection =>
      val id: Option[Long] = SQL("insert into Req(criteria) values ({criteria})")
                .on('criteria -> req.criteria).executeInsert()
      id.get
    }
  }

  def getAll: List[Req] = DB.withConnection() {
    implicit connection =>
      sql().map ( row =>
        Req(row[Long]("id"), row[String]("criteria"))
      ).toList
  }

}