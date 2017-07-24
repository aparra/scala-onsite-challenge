package com.example.dsp

case class Campaign(
    id: Long,
    title: String,
    var budget: BigDecimal,
    target: Target
)

case class Target(
    countryCode: String,
    app: String,
    connectionType: String
)

object Campaign {

  def available: Set[Campaign] = Set(
    Campaign(id = 1,
             title = "CocaCola Life",
             budget = 5000,
             target = Target(countryCode = "DE",
                             app = "com.rovio.angry_birds",
                             connectionType = "WiFi")),
    Campaign(id = 2,
             title = "CocaCola Life",
             budget = 5000,
             target = Target(countryCode = "DE",
                             app = "com.spotify",
                             connectionType = "WiFi")),
    Campaign(id = 3,
             title = "CocaCola Life",
             budget = 5000,
             target = Target(countryCode = "DE",
                             app = "com.facebook",
                             connectionType = "WiFi"))
  )
}
