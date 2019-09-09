package com.github.exact7.xtra.model.chat

abstract class Emote {
    abstract val name: String
    abstract val url: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Emote || name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}