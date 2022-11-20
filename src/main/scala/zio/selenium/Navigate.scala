package zio.selenium

import zio.IO

import org.openqa.selenium.WebDriverException

import java.net.URL

trait Navigate {
  def to(url: URL): IO[WebDriverException, Unit]
  def back: IO[WebDriverException, Unit]
  def forward: IO[WebDriverException, Unit]
  def refresh: IO[WebDriverException, Unit]
}
