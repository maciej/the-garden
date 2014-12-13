package com.softwaremill.thegarden.lawn

import scala.concurrent.duration._

/**
 * @author Maciej Bilas
 * @since 13/12/14 17:43
 */
package object control {

  def retry[T](backOffOpt: Option[BackOff] = Some(BackOff.linear(100.millis, 3)))
              (op: () => Option[T]): Option[T] =
    op().orElse(backOffOpt.flatMap(backOff => retry(backOff.sleep())(op)))

}
