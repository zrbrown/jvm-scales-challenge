package com.nerdery.jvm

import javax.sound.midi.Sequence

/**
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
class NotesMidiGenerator(val notes: List<RelativeNote>) : AbstractMidiGenerator() {

    override fun addNotes(sequence: Sequence): Unit {
        val track = buildTrack(sequence, "Scale Track")
        setVoice(track, PIANO_VOICE)
        notes.map {
            noteToMidi(it)
        }.forEach {
            addNote(track, it, QUARTER_TICKS)
        }
        endTrack(track)
    }

    private fun noteToMidi(note: RelativeNote): Int = MIDDLE_C + note.note.cDistance + note.octave * 12
}