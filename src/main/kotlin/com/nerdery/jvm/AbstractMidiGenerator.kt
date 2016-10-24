package com.nerdery.jvm

import java.io.IOException
import java.util.*
import javax.sound.midi.*

/**
 * AbstractMidiGenerator provides utility methods for generating a MIDI file that can be extended by subclasses.
 * This class is based on code found here: http://www.automatic-pilot.com/midifile.java

 * @author Josh Klun (jklun@nerdery.com)
 */
abstract class AbstractMidiGenerator {

    private val currentTicks = HashMap<String, Long>()
    private val channels = HashMap<String, Int>()

    @Throws(IOException::class, InvalidMidiDataException::class)
    fun generateSong(): Sequence {
        val sequence = buildSequence()
        addNotes(sequence)
        return sequence
    }

    protected fun getTrackTicks(trackId: String): Long {
        var ticks: Long? = currentTicks[trackId]
        if (ticks == null) {
            val initialTicks = 1L
            currentTicks.put(trackId, initialTicks)
            ticks = initialTicks
        }
        return ticks
    }

    protected fun addTrackTicks(trackId: String, addedTicks: Long): Long {
        val newTicks = getTrackTicks(trackId) + addedTicks
        currentTicks.replace(trackId, newTicks)
        return newTicks
    }

    protected fun getChannel(trackId: String): Int? {
        return channels[trackId]
    }

    @Throws(InvalidMidiDataException::class)
    @JvmOverloads protected fun buildTrack(sequence: Sequence, trackName: String, channel: Int = 0): Track {
        val track = sequence.createTrack()
        channels.put(track.toString(), channel)
        enableGeneralMidi(track)
        configureTempo(track)
        nameTrack(track, trackName)
        configureTrack(track)
        return track
    }

    @Throws(InvalidMidiDataException::class)
    protected abstract fun addNotes(sequence: Sequence)

    @Throws(InvalidMidiDataException::class)
    protected fun addNote(track: Track, note: Int, duration: Long) {
        addNote(track, note, 0L, duration)
    }

    @Throws(InvalidMidiDataException::class)
    protected fun addNote(track: Track, note: Int, pause: Long, duration: Long) {
        addNote(track, getChannel(track.toString()), note, pause, duration)
    }

    @Throws(InvalidMidiDataException::class)
    protected fun addNote(track: Track, channel: Int?, note: Int, pause: Long, duration: Long) {
        //****  note on ****
        var shortMessage = ShortMessage()
        val safeChannel = channel ?: 0
        shortMessage.setMessage(ShortMessage.NOTE_ON, safeChannel, note, 0x60)
        var midiEvent = MidiEvent(shortMessage, addTrackTicks(track.toString(), pause))
        track.add(midiEvent)

        //****  note off  ****
        shortMessage = ShortMessage()
        shortMessage.setMessage(ShortMessage.NOTE_OFF, safeChannel, note, 0x40)
        midiEvent = MidiEvent(shortMessage, addTrackTicks(track.toString(), duration))
        track.add(midiEvent)
    }

    @Throws(InvalidMidiDataException::class)
    protected fun nameTrack(track: Track, trackName: String) {
        val metaMessage = MetaMessage()
        metaMessage.setMessage(0x03, trackName.toByteArray(), trackName.length)
        track.add(MidiEvent(metaMessage, 0.toLong()))
    }

    @Throws(InvalidMidiDataException::class)
    protected fun configureTrack(track: Track) {
        var midiEvent: MidiEvent//****  set omni on  ****
        var shortMessage = ShortMessage()
        shortMessage.setMessage(0xB0, 0x7D, 0x00)
        midiEvent = MidiEvent(shortMessage, 0.toLong())
        track.add(midiEvent)

        //****  set poly on  ****
        shortMessage = ShortMessage()
        shortMessage.setMessage(0xB0, 0x7F, 0x00)
        midiEvent = MidiEvent(shortMessage, 0.toLong())
        track.add(midiEvent)
    }

    @Throws(InvalidMidiDataException::class)
    protected fun setVoice(track: Track, voiceNumber: Int) {
        setVoice(track, getChannel(track.toString()), voiceNumber)
    }

    @Throws(InvalidMidiDataException::class)
    protected fun setVoice(track: Track, channel: Int?, voiceNumber: Int) {
        val shortMessage = ShortMessage()
        val safeChannel = channel ?: 0
        shortMessage.setMessage(0xC0, safeChannel, voiceNumber, 0x0)
        track.add(MidiEvent(shortMessage, 0.toLong()))
    }

    @Throws(InvalidMidiDataException::class)
    @JvmOverloads protected fun endTrack(track: Track, delay: Long = 0L) {
        val metaMessage = MetaMessage()
        val bet = byteArrayOf() // empty array
        metaMessage.setMessage(0x2F, bet, 0)
        track.add(MidiEvent(metaMessage, addTrackTicks(track.toString(), delay)))
    }

    @Throws(InvalidMidiDataException::class)
    private fun configureTempo(track: Track) {
        val midiEvent: MidiEvent//****  set tempo (meta event)  ****
        val metaMessage = MetaMessage()
        val tempoBytes = byteArrayOf(0x02, 0x00.toByte(), 0x00)
        metaMessage.setMessage(0x51, tempoBytes, 3)
        midiEvent = MidiEvent(metaMessage, 0.toLong())
        track.add(midiEvent)
    }

    @Throws(InvalidMidiDataException::class)
    private fun enableGeneralMidi(track: Track) {
        //****  General MIDI sysex -- turn on General MIDI sound set  ****
        val generalMidiBytes = byteArrayOf(0xF0.toByte(), 0x7E, 0x7F, 0x09, 0x01, 0xF7.toByte())
        val sysexMessage = SysexMessage()
        sysexMessage.setMessage(generalMidiBytes, generalMidiBytes.size)
        val midiEvent = MidiEvent(sysexMessage, 0.toLong())
        track.add(midiEvent)
    }

    @Throws(InvalidMidiDataException::class)
    private fun buildSequence(): Sequence {
        return Sequence(Sequence.PPQ, 24)
    }

    companion object {
        val MIDDLE_C: Int = 0x3C
        val PIANO_VOICE: Int = 0x0
        val QUARTER_TICKS: Long = 60
    }

}
