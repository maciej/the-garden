package com.softwaremill.thegarden.shrubs

/**
 * @author Maciej Bilas
 * @since 14/1/15 17:09
 */
package object ci {

  def inCi(block: => Unit) = if (isRunningTeamCity) block

  private def isRunningTeamCity: Boolean = sys.env.get("TEAMCITY_VERSION").isDefined

}
