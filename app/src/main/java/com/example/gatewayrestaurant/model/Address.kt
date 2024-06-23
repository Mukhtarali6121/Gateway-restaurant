package com.example.gatewayrestaurant.model

class Address(
    var flatNumber: String? = null,
    var location: String? = null,
    var landmark: String? = null,
    var label: String? = null,
) {
    override fun toString(): String {
        return "Address(flatNumber=$flatNumber, location=$location, landmark=$landmark, label=$label)"
    }
}
