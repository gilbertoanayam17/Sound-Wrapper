package project;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Uses javax.sound.sampled to play the keys.
public class SoundEngine extends AbstractSoundEngine {

	private static final double SAMPLE_RATE = 44100.0; // 44100 Hz (samples per second), the standard audio rate
	private static final int BITS_PER_SAMPLE = 8; // each sample is stored in 8 bits
	private static final int CHANNELS = 1; // 1 = mono

	// Single audio line shared by all keys.
	private SourceDataLine line;

	public SoundEngine() {
		try {
			AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, CHANNELS, true, true);
			line = AudioSystem.getSourceDataLine(format);
			line.open(format);
			line.start();
		} catch (LineUnavailableException e) {
			System.out.println("Could not initialize the audio line: " + e.getMessage());
		}
	}

	// Call at the end to close the line properly.
	@Override
	public void shutdown() {
		if (line != null) {
			line.drain();
			line.stop();
			line.close();
		}
	}

	// --------- Keyboard preset ---------

	public void setKeyboard(Keyboard keyboard) {
		if (keyboard == null) {
			System.out.println("Null keyboard, keeping " + this.currentKeyboard);
			return;
		}
		this.currentKeyboard = keyboard;
	}

	public Keyboard getKeyboard() {
		return currentKeyboard;
	}

	// --------- Key playback ---------

	// Overload with an explicit rhythmic duration.
	@Override
	public void playKey(Key key, Octaves octave, KeyDuration keyDuration) {
		int durationMs = keyDuration.toMilliseconds(bpm);
		playKeyInternal(key, octave, durationMs);
	}

	// Convenience overload: quarter note by default.
	public void playKey(Key key, Octaves octave) {
		playKey(key, octave, KeyDuration.QUARTER);
	}

	// Internal logic: generate samples with an envelope using the keyboard preset.
	private void playKeyInternal(Key key, Octaves octave, int durationMs) {

		if (line == null) {
			System.out.println("Audio line not initialized.");
			return;
		}

		int totalSamples = (int) (durationMs * SAMPLE_RATE / 1000.0);
		if (totalSamples <= 0) {
			return;
		}

		byte[] buffer = new byte[totalSamples];

		double frequency = key.getFrequency(octave); // Ask the Key for its frequency in that octave; Octaves applies
														// the semitone shift

		// For silence (frequency <= 0), just write zeros.
		if (frequency <= 0) {
			line.write(buffer, 0, buffer.length);
			return;
		}

		// ~20 ms attack and release to smooth the cut between notes

		int attackSamples = (int) (SAMPLE_RATE * 0.020); // 20 ms
		int releaseSamples = (int) (SAMPLE_RATE * 0.020); // 20 ms

		if (attackSamples + releaseSamples > totalSamples) {
			int half = totalSamples / 2; // if the note is too short to fit 20 ms at each end, split it in half
			attackSamples = half;
			releaseSamples = totalSamples - half;
		}

		for (int i = 0; i < totalSamples; i++) { // iterate over every sample.
			double time = i / SAMPLE_RATE;

			// Waveform for the keyboard preset
			double waveValue = KeyboardVariant.generateValue(time, frequency, currentKeyboard);

			// Envelope (fade in / fade out)
			double envelope = 1.0;

			if (i < attackSamples) {
				envelope = i / (double) attackSamples; // 0 -> 1
			} else if (i > totalSamples - releaseSamples) {
				envelope = (totalSamples - i) / (double) releaseSamples; // 1 -> 0
			}

			waveValue *= envelope;

			int sample = (int) (waveValue * 127.0 * currentKeyboard.getVolume());
			buffer[i] = (byte) sample;
		}

		line.write(buffer, 0, buffer.length);
	}

	/*
	 Reads a text file and plays the keys it describes, line by line.
	 
	 Supported line types:
	 
	 1) Comments or blank lines -> ignored # This is a comment
	 
	 2) BPM setting: BPM=120
	 
	 3) Keyboard preset setting (uses Keyboard enum names):
	 KEYBOARD=ORG_16_2 KEYBOARD=SOFT_ACCORDION
	 
	 4) Notes: KEY;OCTAVE;DURATION Examples: C;NATURAL;QUARTER D_SHARP;MAJOR;EIGHTH F;MINOR;HALF
	 
	 Default octave: NATURAL Default duration: QUARTER
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

	            // Skip blank lines and comments
	            if (currentLine.isEmpty() || currentLine.startsWith("#")) {
	                continue;
	            }

	            // ---------- Control lines: BPM ----------
	            String upper = currentLine.toUpperCase();

	            if (upper.startsWith("BPM=")) {
	                String bpmText = currentLine.substring(4).trim();
	                try {
	                    int newBpm = Integer.parseInt(bpmText);
	                    setBpm(newBpm);
	                    System.out.println("Line " + lineNumber + ": BPM set to " + newBpm);
	                } catch (NumberFormatException e) {
	                    System.out.println("Line " + lineNumber + ": invalid BPM '" + bpmText
	                            + "'. Keeping " + getBpm());
	                }
	                continue; // move on to the next line
	            }

	            // ---------- Control lines: KEYBOARD ----------
	            if (upper.startsWith("KEYBOARD=")) {
	                String kbText = currentLine.substring("KEYBOARD=".length()).trim();
	                try {
	                    Keyboard kb = Keyboard.valueOf(kbText.toUpperCase());
	                    setKeyboard(kb);
	                    System.out.println("Line " + lineNumber + ": keyboard set to " + kb);
	                } catch (IllegalArgumentException e) {
	                    System.out.println("Line " + lineNumber + ": invalid keyboard '" + kbText
	                            + "'. Keeping " + getKeyboard());
	                }
	                continue; // move on to the next line
	            }

	            // ---------- Note lines: KEY;OCTAVE;DURATION ----------
	            String[] parts = currentLine.split(";");
	            if (parts.length == 0) {
	                continue;
	            }

	            // Default values
	            Key key;
	            Octaves octave = Octaves.NATURAL;
	            KeyDuration duration = KeyDuration.QUARTER;

	            // ---- Parse Key ----
	            try {
	                String keyText = parts[0].trim().toUpperCase();
	                key = Key.valueOf(keyText);
	            } catch (IllegalArgumentException e) {
	                System.out.println("Line " + lineNumber + ": invalid key -> " + currentLine);
	                continue; // move on to the next line
	            }

	            // ---- Parse Octaves (if present) ----
	            if (parts.length >= 2) {
	                String octaveText = parts[1].trim();
	                if (!octaveText.isEmpty()) {
	                    try {
	                        octave = Octaves.valueOf(octaveText.toUpperCase());
	                    } catch (IllegalArgumentException e) {
	                        System.out.println("Line " + lineNumber + ": invalid octave '" + octaveText
	                                + "', using NATURAL.");
	                    }
	                }
	            }

	            // ---- Parse KeyDuration (if present) ----
	            if (parts.length >= 3) {
	                String durationText = parts[2].trim();
	                if (!durationText.isEmpty()) {
	                    try {
	                        duration = KeyDuration.valueOf(durationText.toUpperCase());
	                    } catch (IllegalArgumentException e) {
	                        System.out.println("Line " + lineNumber + ": invalid duration '" + durationText
	                                + "', using QUARTER.");
	                    }
	                }
	            }

	            // ---------- Log what is about to play ----------
	            System.out.println(
	                "Line " + lineNumber + ": note "
	                + key + ";" + octave + ";" + duration
	                + " (BPM=" + getBpm() + ", keyboard=" + getKeyboard() + ")"
	            );

	            // Finally, play the key that was read
	            playKey(key, octave, duration);
	        }

	    } catch (IOException e) {
	        System.out.println("Error reading key file '" + filePath + "': " + e.getMessage());
	    }
	}


}
