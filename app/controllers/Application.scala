package controllers

import play.api.mvc.{Action, Controller}
import anorm._
import play.api.db.DB
import play.api.Play.current

class Application extends Controller {

  def index = Action {
    var r:String = ""
    DB.withConnection { implicit c =>
      val result: Boolean = SQL("Select 1").execute()
      r=result.toString;
    }
    Ok("Got request [" + r + "]")
  }

}
