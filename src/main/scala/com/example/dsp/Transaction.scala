package com.example.dsp

import java.time.{Duration, Instant}

case class Transaction(at: Instant,
                       auctionId: String,
                       dspId: Long,
                       bid: BigDecimal) {

  def isExpired(time: Instant): Boolean =
    Duration.between(at, time).toMillis > 200
}
