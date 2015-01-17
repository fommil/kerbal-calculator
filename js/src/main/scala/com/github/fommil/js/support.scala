package com.github.fommil.js

import scalatags.Text.all._
import org.scalajs.jquery.jQuery
import org.scalajs.dom

/**
 * Chrome doesn't allow reading or writing of cookies when served
 * locally: https://code.google.com/p/chromium/issues/detail?id=535
 *
 * Development must therefore be performed from a hosted HTML page.
 * In Emacs, use `M-x elnode-start-webserver`
 */
trait CookieSupport {
  protected def cookies = dom.document.cookie.split("; ").map { entry =>
    val i = entry.indexOf("=")
    (entry.substring(0, i).trim, entry.substring(i + 1, entry.length).trim)
  }.toMap

  protected def addCookie(key: String, value: String): Unit = {
    require((key + value).forall { c => c != '=' && c != ';' })
    dom.document.cookie = s"${key.trim}=${value.trim}" +
      s"; path=${dom.window.location.pathname}" +
      s"; expires=Fri, 01 Jan 2100 00:00:00 GMT"
  }
}

/**
 * For working with input forms.
 */
trait InputFormSupport {
  private def getElement(id: String) = {
    val el = jQuery("#" + id)
    val tag = el.prop("tagName").toString.toLowerCase
    val `type` = el.attr("type")
    (el, tag, `type`)
  }

  protected def populate(id: String, values: List[String]): Unit = {
    val select = jQuery("#" + id)
    values.map { entry =>
      select.append(option(entry).render)
    }
  }

  protected def getParam(id: String): String = getElement(id) match {
    case (el, "input", "checkbox") => el.is(":checked").toString
    case (el, "input", _) => el.value().toString
    case (el, "select", _) => el.value().toString
    case other => throw new UnsupportedOperationException(s"$other")
  }

  protected def getParams(ids: List[String]): Map[String, String] = {
    for {
      key <- ids
      value = getParam(key)
    } yield (key, value)
  }.toMap

  protected def setParam(id: String, value: String) = getElement(id) match {
    case (el, "input", "checkbox") if value.toBoolean => el.attr("checked", true)
    case (el, "input", "checkbox") => el.attr("checked", false)
    case (el, "input", _) => el.attr("value", value)
    case (el, "select", _) =>
      val all = jQuery(s"""#$id option""").removeAttr("selected")
      val hit = jQuery(s"""#$id option:contains("$value")""").first
      hit.attr("selected", "").change()
    case other => throw new UnsupportedOperationException(s"$other")
  }
}

trait PersistentInputFormSupport {
  this: InputFormSupport with CookieSupport =>

  def loadParams(ids: List[String]): Unit = for {
    id <- ids
    saved <- cookies.get(id)
  } {
    setParam(id, saved)
  }

  def persistParams(params: Map[String, String]): Unit =
    params.foreach {
      case (id, value) => addCookie(id, value)
    }
}
