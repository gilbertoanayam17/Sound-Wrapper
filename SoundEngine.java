package proyecto;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Encargado de dar uso javax.sound.sampled y reproducir las teclas.
public class SoundEngine extends AbstractSoundEngine {

	private static final double SAMPLE_RATE = 44100.0; // 44100 hz (muestras) por segundo, el estándar de audio
	private static final int BITS_PER_SAMPLE = 8; // cada muestra se guarda en 8 bits
	private static final int CHANNELS = 1; // 1 = mono

	// Única línea de audio compartida para todas las teclas.
	private SourceDataLine line;

	public SoundEngine() {
		try {
			AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, CHANNELS, true, true);
			line = AudioSystem.getSourceDataLine(format);
			line.open(format);
			line.start();
		} catch (LineUnavailableException e) {
			System.out.println("No se pudo inicializar la línea de audio: " + e.getMessage());
		}
	}

	// Llamar al final para cerrar bien la línea.
	@Override
	public void shutdown() {
		if (line != null) {
			line.drain();
			line.stop();
			line.close();
		}
	}

	// --------- Keyboard/preset del teclado ---------

	public void setKeyboard(Keyboard keyboard) {
		if (keyboard == null) {
			System.out.println("Keyboard nulo, se mantiene " + this.keyboardActual);
			return;
		}
		this.keyboardActual = keyboard;
	}

	public Keyboard getKeyboard() {
		return keyboardActual;
	}

	// --------- Reproducción de teclas ---------

	// Versión con duración explícita en figura rítmica.
	@Override
	public void playKey(Key key, Octaves octave, KeyDuration keyDuration) {
		int durationMs = keyDuration.toMilliseconds(bpm);
		playKeyInternal(key, octave, durationMs);
	}

	// Versión comodín: negra por defecto.
	public void playKey(Key key, Octaves octave) {
		playKey(key, octave, KeyDuration.NEGRA);
	}

	// Lógica interna: generar samples con envolvente con preset de teclado.
	private void playKeyInternal(Key key, Octaves octave, int durationMs) {

		if (line == null) {
			System.out.println("Línea de audio no inicializada.");
			return;
		}

		int totalSamples = (int) (durationMs * SAMPLE_RATE / 1000.0);
		if (totalSamples <= 0) {
			return;
		}

		byte[] buffer = new byte[totalSamples];

		double frequency = key.getFrequency(octave); // Pide a la Key su frecuencia ajustada a esa Octaves, octaves hace
														// el desplazamiento de semitonos

		// Si es silencio (frecuencia <= 0), solo escribimos ceros.
		if (frequency <= 0) {
			line.write(buffer, 0, buffer.length);
			return;
		}

		// ataque y liberación de aprox 20 ms para disimular corte entre notas

		int attackSamples = (int) (SAMPLE_RATE * 0.020); // 20 ms
		int releaseSamples = (int) (SAMPLE_RATE * 0.020); // 20 ms

		if (attackSamples + releaseSamples > totalSamples) {
			int half = totalSamples / 2; // por si la nota es tan cortita que no caben 20ms inicio y fin, mitad
			attackSamples = half;
			releaseSamples = totalSamples - half;
		}

		for (int i = 0; i < totalSamples; i++) { // recorrer cada muestra.
			double time = i / SAMPLE_RATE;

			// Forma de onda según el preset del teclado
			double waveValue = KeyboardVariant.generateValue(time, frequency, keyboardActual);

			// Envolvente (fade in / fade out)
			double envelope = 1.0;

			if (i < attackSamples) {
				envelope = i / (double) attackSamples; // 0 -> 1
			} else if (i > totalSamples - releaseSamples) {
				envelope = (totalSamples - i) / (double) releaseSamples; // 1 -> 0
			}

			waveValue *= envelope;

			int sample = (int) (waveValue * 127.0 * keyboardActual.getVolume());
			buffer[i] = (byte) sample;
		}

		line.write(buffer, 0, buffer.length);
	}

	/*
	 Lee un archivo de texto y reproduce las teclas descritas línea por línea.
	 
	 Tipos de línea soportados:
	 
	 1) Comentarios o vacías -> se ignoran # Esto es un comentario
	 
	 2) Configuración de BPM: BPM=120
	 
	 3) Configuración de preset de teclado (usa nombres del enum Keyboard):
	 KEYBOARD=ORG_16_2 KEYBOARD=SOFT_ACCORDEON
	 
	 4) Notas: KEY;OCTAVE;DURATION Ejemplos: C;NATURAL;NEGRA D_SHARP;MAJOR;CORCHEA F;MINOR;BLANCA
	 
	 Octava por defecto: NATURAL Duración por defecto: NEGRA
	 */
	public void loadAndPlayKeysFromFile(String filePath) {
	    BufferedReader reader = null;

	    try {
	        reader = new BufferedReader(new FileReader(filePath));
	        String currentLine;
	        int lineNumber = 0;

	        while ((currentLine = reader.readLine()) != null) {
	            lineNumber++;
	            currentLine = currentLine.trim();

	            // Ignorar vacías o comentarios
	            if (currentLine.isEmpty() || currentLine.startsWith("#")) {
	                continue;
	            }

	            // ---------- Líneas de control: BPM ----------
	            String upper = currentLine.toUpperCase();

	            if (upper.startsWith("BPM=")) {
	                String bpmText = currentLine.substring(4).trim();
	                try {
	                    int newBpm = Integer.parseInt(bpmText);
	                    setBpm(newBpm);
	                    System.out.println("Línea " + lineNumber + ": BPM establecido a " + newBpm);
	                } catch (NumberFormatException e) {
	                    System.out.println("Línea " + lineNumber + ": BPM inválido '" + bpmText
	                            + "'. Se mantiene " + getBpm());
	                }
	                continue; // pasamos a la siguiente línea
	            }

	            // ---------- Líneas de control: KEYBOARD ----------
	            if (upper.startsWith("KEYBOARD=")) {
	                String kbText = currentLine.substring("KEYBOARD=".length()).trim();
	                try {
	                    Keyboard kb = Keyboard.valueOf(kbText.toUpperCase());
	                    setKeyboard(kb);
	                    System.out.println("Línea " + lineNumber + ": teclado establecido a " + kb);
	                } catch (IllegalArgumentException e) {
	                    System.out.println("Línea " + lineNumber + ": teclado inválido '" + kbText
	                            + "'. Se mantiene " + getKeyboard());
	                }
	                continue; // pasamos a la siguiente línea
	            }

	            // ---------- Líneas de notas: KEY;OCTAVE;DURATION ----------
	            String[] parts = currentLine.split(";");
	            if (parts.length == 0) {
	                continue;
	            }

	            // Valores por defecto
	            Key key;
	            Octaves octave = Octaves.NATURAL;
	            KeyDuration duration = KeyDuration.NEGRA;

	            // ---- Parseo de Key ----
	            try {
	                String keyText = parts[0].trim().toUpperCase();
	                key = Key.valueOf(keyText);
	            } catch (IllegalArgumentException e) {
	                System.out.println("Línea " + lineNumber + ": tecla inválida -> " + currentLine);
	                continue; // pasamos a la siguiente línea
	            }

	            // ---- Parseo de Octaves (si viene) ----
	            if (parts.length >= 2) {
	                String octaveText = parts[1].trim();
	                if (!octaveText.isEmpty()) {
	                    try {
	                        octave = Octaves.valueOf(octaveText.toUpperCase());
	                    } catch (IllegalArgumentException e) {
	                        System.out.println("Línea " + lineNumber + ": octava inválida '" + octaveText
	                                + "', se usa NATURAL.");
	                    }
	                }
	            }

	            // ---- Parseo de KeyDuration (si viene) ----
	            if (parts.length >= 3) {
	                String durationText = parts[2].trim();
	                if (!durationText.isEmpty()) {
	                    try {
	                        duration = KeyDuration.valueOf(durationText.toUpperCase());
	                    } catch (IllegalArgumentException e) {
	                        System.out.println("Línea " + lineNumber + ": duración inválida '" + durationText
	                                + "', se usa NEGRA.");
	                    }
	                }
	            }

	            // ---------- Log bonito de lo que se va a tocar ----------
	            System.out.println(
	                "Línea " + lineNumber + ": nota "
	                + key + ";" + octave + ";" + duration
	                + " (BPM=" + getBpm() + ", teclado=" + getKeyboard() + ")"
	            );

	            // Finalmente, reproducimos la tecla leída
	            playKey(key, octave, duration);
	        }

	    } catch (IOException e) {
	        System.out.println("Error al leer el archivo de teclas '" + filePath + "': " + e.getMessage());
	    }
	}


}
