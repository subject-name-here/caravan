package com.unicorns.invisible.caravan

enum class AnimationSpeed(val delay: Long) {
    NORMAL(380L),
    LAGGY(190L),
    NONE(0L);

    fun prev(): AnimationSpeed {
        return when (this) {
            NORMAL -> LAGGY
            LAGGY -> NONE
            NONE -> NONE
        }
    }
    fun next(): AnimationSpeed {
        return when (this) {
            NORMAL -> NORMAL
            LAGGY -> NORMAL
            NONE -> LAGGY
        }
    }
}