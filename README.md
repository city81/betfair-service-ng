Betfair Service NG
====================

[![Master Build](https://travis-ci.org/city81/betfair-service-ng.svg?branch=master)](https://travis-ci.org/city81/betfair-service-ng)

This is a project which uses [Scala][scala], [Akka HTTP][akka-http] and the [Play][play] JSON lib to make calls to [Betfair's][betfair] New Generation API ie JSON-RPC

To use the service, you'll need a Betfair username and password, and an AppKey. The config file also contains the betfair urls. These need to be placed in a file called betfair-service-ng.conf which is referenced by the application.conf file. Example configuration is below:

    betfairService.appKey = "appkey"
    betfairService.username = "username"
    betfairService.password = "password"
    betfairService.apiUrl = "https://api.betfair.com/exchange/betting/json-rpc/v1"
    betfairService.isoUrl = "https://identitysso.betfair.com/api"

Before any of the Betting API calls can be made a session token needs to be obtained and this can be done via the loginRequest method. This will cache the session token for future calls.

The ExampleBetfairServiceNG file contains the below sample calls:

    login
    list event types
    list competitions
    list football competitions
    list all horse racing markets in the next 24 hour period
    get the next UK Win horse racing market
    list a market book with exchange best offers
    get a market book exchange favourite
    place a bet
    cancel a bet


Project also includes a FootballProcessor file which parses Betfair historical files. 
This file can be changed to extract different data. The example extracts Premier League Match Odds records
and outputs the 'at the off' odds and the longest odds matched in running.


Spray replaced by Akka-HTTP
---------------------------

The latest version of this project has been updated to use Akka HTTP instead of Spray. Whereas Spray had an actor based interface, Akka HTTP has an API based on Akka Streams making the development of HTTP applications simpler. Also with the bounded memory guarantee of Akka Streams you can serve HTTP results without increased resource usage.

For the previous version, checkout the Spray tag


LICENCE
-------

BSD Licence - see LICENCE.txt


TODO
----

1. Add the remaining betting api calls
2. Add more convenience methods to the service API

[scala]: http://www.scala-lang.org/ "Scala Language"
[play]: https://www.playframework.com/documentation/2.0/ScalaJson
[betfair]: https://developer.betfair.com/
[akka-http]: http://doc.akka.io/docs/akka-http/current/scala.html
