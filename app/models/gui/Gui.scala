package models.gui ;

object Gui
{
  def checkbox_status(field : Option[String]) : String = {
    field.map {
      case "true"  => "checked"
      case "on"    => "checked"
      case "t"     => "checked"
      case "false" => "unchecked"
      case "off"   => "unchecked"
      case "f"     => "unchecked"
      case _       => "unchecked"
    }.getOrElse("unchecked")
  }
}
