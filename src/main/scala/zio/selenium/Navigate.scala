package zio.selenium

import org.openqa.selenium.WebDriverException

import zio._

import java.net.URL

trait Navigate {
  def to(url: URL)(implicit trace: Trace): IO[WebDriverException, Unit]
  def back(implicit trace: Trace): IO[WebDriverException, Unit]
  def forward(implicit trace: Trace): IO[WebDriverException, Unit]
  def refresh(implicit trace: Trace): IO[WebDriverException, Unit]
}
