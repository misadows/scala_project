package utils

import play.api.data._
import play.api.data.Forms._

case class ReqDataForm(criteria: String)

class ReqForm {
  def reqForm: Form[ReqDataForm] = Form(
    mapping(
      "criteria" -> text
    )(ReqDataForm.apply)(ReqDataForm.unapply)
  )
}
