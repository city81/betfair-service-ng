Betfair Service NG
====================

[![Master Build](https://travis-ci.org/city81/betfair-service-ng.svg?branch=master)](https://travis-ci.org/city81/betfair-service-ng)

This is a project which uses [Scala][scala], [Spray][spray] and the [Play][play] JSON lib to make calls to [Betfair's][betfair] New Generation API ie JSON-RPC

To use the service, you'll need a Betfair username and password, and an AppKey. These need to be placed in a file called betfair-service-ng.conf which is referenced by the application.conf file. Example configuration is below:

    betfairService.appKey = "appkey"
    betfairService.username = "username"
    betfairService.password = "password"

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


LICENCE
-------

BSD Licence - see LICENCE.txt


TODO
----

1. Add the remaining betting api calls
2. Add more convenience methods to the service API eg get all runners between two prices
3. Add a Spray REST API to the whole service
4. Repackage the domain classes
5. Push the local tests to master
6. Make this README more complete!

[scala]: http://www.scala-lang.org/ "Scala Language"
[spray]: http://spray.io/ "Spray"
[play]: https://www.playframework.com/documentation/2.0/ScalaJson
[betfair]: https://developer.betfair.com/