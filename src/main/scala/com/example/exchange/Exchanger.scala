package com.example.exchange

import java.time.Instant
import java.util.Timer

import com.example.BidResponse
import com.example.dsp.{Campaign, DspProvider}
import com.example.user.UserMetadata

import scala.collection.mutable

case class Exchanger(dspProvider: DspProvider, timer: Timer = new Timer()) {

  def findAdsBy(user: UserMetadata): BidResponse =
    dspProvider.adsFor(user)

  def notifyWinner(auctionId: String) = {
    dspProvider.removeTransaction(auctionId)
  }

  import java.util.TimerTask

  timer.scheduleAtFixedRate(new TimerTask() {
    override def run(): Unit = {
      dspProvider.expireTransactions(Instant.now)
    }
  }, 200, 200)
}

object Exchanger {

  def apply(): Exchanger =
    Exchanger(DspProvider(mutable.Set(Campaign.available.toSeq: _*)))
}
