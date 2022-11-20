package zio.selenium

import org.openqa.selenium.WebDriverException

import zio.IO

import java.net.URL

trait Navigate {
  def to(url: URL): IO[WebDriverException, Unit]
  def back: IO[WebDriverException, Unit]
  def forward: IO[WebDriverException, Unit]
  def refresh: IO[WebDriverException, Unit]
}
