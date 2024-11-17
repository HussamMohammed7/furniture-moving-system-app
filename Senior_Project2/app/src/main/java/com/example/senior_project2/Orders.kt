package com.example.senior_project2

import java.util.*

data class Orders(
    var idClient : String ? = null,
    var idOrder : String ? = null,
    var clientName : String ? = null,
    var date : String ? = null,
    var PickUpLocationLati: String ? = null,
    var PickUpLocationLong: String ? = null,
    var destinationLocationLati : String ? = null,
    var destinationLocationLong: String ? = null,
    var switchPickUpElevator : Boolean ? = null,
    var switchDestinationElevator : Boolean ? = null,
    var description: String ? = null,
    var condetion : String ? = null

)
