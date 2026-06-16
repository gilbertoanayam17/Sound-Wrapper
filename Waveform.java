package proyecto;

@FunctionalInterface
public interface Waveform {
	double sampleAt(double t, double frequency);
}
