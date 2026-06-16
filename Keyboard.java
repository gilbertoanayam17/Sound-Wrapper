package proyecto;

/*
 Presets del teclado (variante de sonido).
 Internamente se mapearán a distintas formas de onda.
 */
public enum Keyboard {
    ORG_16_2("16_2_ORG", 0.9,
			(t, f) -> Math.sin(2.0 * Math.PI * f * t)),

	SOFT_ACCORDEON("SOFT_ACCORDEON", 0.8, (t, f) -> {
		double period = 1.0 / f;
		double cycle = t % period;
		return 2.0 * Math.abs(2.0 * cycle / period - 1.0) - 1.0;
	});

	private final String displayName;
	private final double volume;
	private final Waveform waveform;

	Keyboard(String displayName, double volume, Waveform waveform) {
		this.displayName = displayName;
		this.volume = volume;
		this.waveform = waveform;
    }

    public String getDisplayName() { return displayName; }
    public double getVolume() { return volume; }
    public Waveform getWaveform() { return waveform; }
}
