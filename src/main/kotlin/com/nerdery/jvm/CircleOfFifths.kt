package com.nerdery.jvm

import java.util.*

/**
 * Pretty bad, it doesn't work for all keys.
 *
 * @author Zack Brown (zbrown@nerdery.com)
 */
class CircleOfFifths(val tonicNote: Note, val mode: Int = 0) {
    private val major = listOf(3, 0, 4, 1, 5, 2, 6)

    fun getScale(): List<RelativeNote> {
        val tonicPosition = major.indexOf(0)
        val tonicDegree = getDegree(tonicNote)
        val scale = ArrayList<RelativeNote>(8)

        for (position in 0..6) {
            val normalizedNoteDegree = major.indexOf(position) - tonicPosition + tonicDegree + 1
            val accidentalIndicator = normalizedNoteDegree / 7

            if (normalizedNoteDegree >= 0) {
                if (accidentalIndicator % 2 > 0) {
                    scale.add(getNote(tonicNote, major.indexOf(position) - tonicPosition, "SHARP"))
                } else {
                    scale.add(getNote(tonicNote, major.indexOf(position) - tonicPosition))
                }
            } else {
                if (accidentalIndicator % 2 < 0) {
                    scale.add(getNote(tonicNote, major.indexOf(position) - tonicPosition))
                } else {
                    scale.add(getNote(tonicNote, major.indexOf(position) - tonicPosition, "FLAT"))
                }
            }
        }

        scale.add(RelativeNote(tonicNote, 1))

        return scale
    }

    fun getDegree(note: Note, semitones: Int = 0): Int {
        if (note.cDistance == 0) {
            return 0
        } else if (semitones != 0 && semitones % note.cDistance == 0) {
            return semitones / 7
        } else {
            if (note == Note.F || note.name.endsWith("FLAT")) {
                return getDegree(note, semitones - 7)
            }

            return getDegree(note, semitones + 7)
        }
    }

    fun getNote(baseNote: Note, offsetDegrees: Int, accidental: String = ""): RelativeNote {
        if (offsetDegrees == 0) {
            return RelativeNote(baseNote, 0)
        }

        val note = getNoteFromCDistance(baseNote.cDistance + ((offsetDegrees * 7) % 12), accidental)

        val octaveOffset: Int

        if (note.cDistance < baseNote.cDistance) {
            octaveOffset = 1
        } else {
            octaveOffset = 0
        }

        return RelativeNote(note, octaveOffset)
    }

    fun getNoteFromCDistance(cDistance: Int, accidental: String): Note {
        var normalizedDistance: Int = cDistance
        if (cDistance > 8) {
            normalizedDistance = cDistance - 12
        } else if (cDistance < -3) {
            normalizedDistance = cDistance + 12
        }

        for (value in Note.values()) {
            if (value.cDistance == normalizedDistance) {
                if (accidental.isEmpty()) {
                    if (value.name.length == 1) {
                        return value
                    }
                } else {
                    if (value.name.endsWith(accidental)) {
                        return value
                    }
                }
            }
        }

        // should never reach this
        return Note.C
    }
}