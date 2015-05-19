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
  }

}
