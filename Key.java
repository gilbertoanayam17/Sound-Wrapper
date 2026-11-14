package project;

// White and black keys of one piano octave (standard tuning).
public enum Key {
	SILENCE(0),
    C(261.63),// C4 (middle C)
    C_SHARP(277.18),// C# / Db
    D(293.66),// D4
    D_SHARP(311.13),// D# / Eb
    E(329.63),// E4
    F(349.23),// F4
    F_SHARP(369.99),// F# / Gb
    G(392.00),// G4
    G_SHARP(415.30),// G# / Ab
    A(440.00),// A4 (concert pitch)
    A_SHARP(466.16),// A# / Bb
    B(493.88);// B4

    private final double baseFrequency;

    Key(double baseFrequency) {
        this.baseFrequency = baseFrequency;
    }

    public double getBaseFrequency() {
        return baseFrequency;
    }

    public double getFrequency(Octaves octave) {
        return octave.adjustFrequency(baseFrequency);
    }
}
