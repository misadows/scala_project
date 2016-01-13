package controllers

import javax.inject.Inject

import models.Req
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{MessagesApi, I18nSupport}
import play.api.mvc.{Action, Controller}
import anorm._
import play.api.db.DB
import play.api.Play.current
import utils._


class Application @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {
  val f = Form(
    mapping(
      "criteria" -> text
    )(ReqData.apply)(ReqData.unapply)
  )

  def index = Action {
    Ok(views.html.index(f))
  }

  def getData = Action {
    implicit request => {
      var r:String = ""

      f.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.index(f))
        },
        ReqData => {
          r = ReqData.criteria
          Req.insert(Req(0, r))
        }
      )
      val x = new Downloader("password".split(" "))

      Redirect(routes.Application.showRequests())
    }
  }

  def showRequests = Action {
    Ok(Req.getAll.toString)
  }

}
