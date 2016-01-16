package controllers

import javax.inject.Inject

import models.{ReqData, Req}
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
    )(ReqDataForm.apply)(ReqDataForm.unapply)
  )

  def index = Action {
    Ok(views.html.index(f))
  }

  def getData = Action {
    implicit request => {
      var id:Long = 0
      f.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.index(f))
        },
        ReqDataForm => {
          id = Req.insert(Req(0, ReqDataForm.criteria))
          val x = new Downloader(id)
          x.runActors(ReqDataForm.criteria.split(" "))
        }
      )
      Redirect(routes.Application.showReqData(id))
    }
  }

  def showRequests = Action {
    Ok(Req.getAll.toString)
  }

  def showReqData(id: Long) = Action {
    Req.exists(id) match {
      case true => Ok(views.html.reqdata(Req.getCriteria(id), ReqData.getAll(id)))
      case false => NotFound(views.html.notfound("ReqData"))
    }
  }
}
