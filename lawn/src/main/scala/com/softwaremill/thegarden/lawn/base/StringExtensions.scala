package com.softwaremill.thegarden.lawn.base


object StringExtensions {
  implicit class ExtString(str: String) {

    /**
     * Truncates the string, omitting more than '''toLength - omission.length''' characters.
     *
     * Based on ActiveSupport's counterpart.
     *
     * @see http://guides.rubyonrails.org/active_support_core_extensions.html#truncate
     * @throws IllegalArgumentException if ''prefix length'' (as defined above) is less or equal to 0
     */
    def truncate(toLength: Int, omission: String = "...") = {
      val prefixLength = toLength - omission.length
      require(prefixLength > 0)
      if (str.length > toLength) {
        s"${str.take(prefixLength)}$omission"
      } else {
        str
      }
    }

    def underscore: String = {
      val spacesPattern = "[-\\s]".r
      val firstPattern = "([A-Z]+)([A-Z][a-z])".r
      val secondPattern = "([a-z\\d])([A-Z])".r
      val replacementPattern = "$1_$2"
      spacesPattern.replaceAllIn(
        secondPattern.replaceAllIn(
          firstPattern.replaceAllIn(
            str, replacementPattern), replacementPattern), "_").toLowerCase
    }

    def camelCase: String = "_([a-z\\d])".r.replaceAllIn(str, { m =>
      m.group(1).toUpperCase
    })
  }

}
