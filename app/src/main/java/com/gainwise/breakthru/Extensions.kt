package com.gainwise.breakthru

fun String.removeAllButNumbers(): String{
    return this.replace("[^0-9]".toRegex(), "")
}
fun String?.last7(): String?{
    return this?.takeLast(7)
}
data class Contact(val name: String, val number: String, var on: Boolean = false) {
}