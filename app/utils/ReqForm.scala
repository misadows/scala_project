package utils

import play.api.data._
import play.api.data.Forms._

case class ReqData(criteria: String)

class ReqForm {
  def reqForm: Form[ReqData] = Form(
    mapping(
      "criteria" -> text
    )(ReqData.apply)(ReqData.unapply)
  )
}
