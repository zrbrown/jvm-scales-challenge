package com.nerdery.jvm

/**
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
enum class Note(val cDistance: Int) {
    A_FLAT(8), A(-3), A_SHARP(-2),
    B_FLAT(-2), B(-1), B_SHARP(0),
    C_FLAT(-1), C(0), C_SHARP(1),
    D_FLAT(1), D(2), D_SHARP(3),
    E_FLAT(3), E(4), E_SHARP(5),
    F_FLAT(4), F(5), F_SHARP(6),
    G_FLAT(6), G(7), G_SHARP(8)
}