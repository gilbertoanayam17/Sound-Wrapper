package proyecto;

// Representa las teclas blancas y negras de una octava del piano (afinación estándar).
public enum Key {
	SILENCE(0),
    C(261.63),//DO
    C_SHARP(277.18),// C# / Db
    D(293.66),//RE
    D_SHARP(311.13),// D# / Eb
    E(329.63),//MI
    F(349.23),//FA
    F_SHARP(369.99),// F# / Gb
    G(392.00),//SOL
    G_SHARP(415.30),// G# / Ab
    A(440.00),//LA
    A_SHARP(466.16),// A# / Bb
    B(493.88);//SI

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
