package com.nerdery.jvm

import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequence

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
class ScalesChallenge {
    fun buildScale(note: Note): List<RelativeNote> = listOf(
            RelativeNote(Note.C),
            RelativeNote(Note.D),
            RelativeNote(Note.E),
            RelativeNote(Note.F),
            RelativeNote(Note.G),
            RelativeNote(Note.A, 1),
            RelativeNote(Note.B, 1),
            RelativeNote(Note.C, 1))

    fun convertToMidi(notes: List<RelativeNote>): Sequence = NotesMidiGenerator(notes).generateSong()

    fun playMidi(sequence: Sequence): Unit {
        val sequencer = MidiSystem.getSequencer()
        sequencer.sequence = sequence
        sequencer.open()
        Thread.sleep(300L)
        sequencer.start()
        Thread.sleep(5000L)
        sequencer.stop()
        sequencer.close()
    }
}

val usageMessage = "Enter a key signature. For example: C_FLAT, C, C_SHARP"

fun main(args: Array<String>) = println(when (args.size) {
    0 -> usageMessage
    else -> try {
        val challenge = ScalesChallenge()
        val scale = challenge.buildScale(Note.valueOf(args.first()))
        val sequence = challenge.convertToMidi(scale)
        challenge.playMidi(sequence)
        scale.joinToString()
    } catch (e: IllegalArgumentException) {
        usageMessage
    }
})
