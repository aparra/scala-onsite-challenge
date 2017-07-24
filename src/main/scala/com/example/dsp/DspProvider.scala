package com.example.dsp

import java.time.Instant

import com.example.{Bid, BidResponse, NoBid}
import com.example.user.UserMetadata

import scala.collection.mutable
import scala.util.Random

case class DspProvider(
    dspPool: mutable.Set[Campaign],
    waitingForConfirmation: mutable.Map[String, Transaction] = mutable.Map()) {

  def adsFor(user: UserMetadata): BidResponse = synchronized {
    val candidate = findCampaingFor(user)

    val bidAmount = randomBidAmount

    val hasNoBudget = candidate.exists(_.budget < bidAmount)

    if (candidate.isEmpty || hasNoBudget) {
      NoBid(user.auctionId)
    } else {
      candidate.get.budget -= bidAmount

      registerTransaction(
        Transaction(at = Instant.now(),
                    auctionId = user.auctionId,
                    dspId = candidate.get.id,
                    bid = bidAmount))

      println(s"${user.auctionId} -> $bidAmount")

      Bid(
        auctionId = user.auctionId,
        bid = bidAmount.toDouble,
        currency = "USD",
        creative = "http://videos-bucket.com/video123.mov",
        winningNotificationUrl = s"impression/${user.auctionId}"
      )
    }
  }

  def removeTransaction(auctionId: String) {
    println(s"confirming transaction $auctionId")
    waitingForConfirmation.remove(auctionId)
  }

  def expireTransactions(time: Instant): Unit = synchronized {
    val expiredTransactions =
      waitingForConfirmation.values.filter(_.isExpired(time))

    expiredTransactions.foreach { transaction =>
      val dsp = dspPool.find(_.id == transaction.dspId)
      dsp.foreach(_.budget += transaction.bid)
      removeTransaction(transaction.auctionId)
    }
  }

  private def registerTransaction(transaction: Transaction) {
    waitingForConfirmation += transaction.auctionId -> transaction
  }

  private def randomBidAmount: BigDecimal =
    Random.nextInt(20) / BigDecimal("1000") + BigDecimal("0.035")

  private def findCampaingFor(user: UserMetadata): Option[Campaign] =
    dspPool.find { dsp =>
      dsp.target.connectionType == user.connectionType && dsp.target.app == user.bundleName
    }
}
