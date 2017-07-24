package com.example

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Route
import com.example.exchange.Exchanger
import com.example.user.UserMetadata

/*
 * Add your logic here. Feel free to rearrange the code as you see fit,
 * this is just a starting point.
 */
object DspFrontend extends Directives with BidResponseJsonFormats {

  val exchanger = Exchanger()

  def apply(): Route =
    path("bid_request") {
      get {
        parameters('auction_id, 'ip, 'bundle_name, 'connection_type) {
          (auctionId, ip, bundleName, connectionType) =>
            complete {
              exchanger.findAdsBy(
                UserMetadata(auctionId, ip, bundleName, connectionType))
            }
        }
      }
    } ~ path("impression" / Segment) { auctionId =>
      get {
        complete {
          exchanger.notifyWinner(auctionId)
          "OK"
        }
      }
    }
}
