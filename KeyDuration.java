package proyecto;

/*
 Tipo de figura rítmica.
 Se expresa en "número de negras" que dura cada figura.
 */
public enum KeyDuration {

	REDONDA(4.0), // 4 negras
	BLANCA(2.0), // 2 negras
	NEGRA(1.0), // 1 negra (pulso base del BPM)
	CORCHEA(0.5), // media negra
	SEMICORCHEA(0.25); // cuarto de negra

	// Cuántas negras dura esta figura.
	private final double beats;

	KeyDuration(double beats) {
		this.beats = beats;
	}

	
	 //Convierte la figura a milisegundos, dada la velocidad en BPM. BPM se entiende como "negras por minuto".

	public int toMilliseconds(int bpm) {
		// Duración de una negra en milisegundos:
		// 60,000 ms (1 min) / BPM.
		double negraMs = 60000.0 / bpm;

		double durationMs = beats * negraMs;

		// Redondeamos al entero más cercano.
		return (int) Math.round(durationMs);
	}
}
