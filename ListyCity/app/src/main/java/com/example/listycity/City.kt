package com.example.listycity

// Default values let Firestore convert documents back into City objects.
data class City(
    val name: String = "",
    val province: String = ""
)