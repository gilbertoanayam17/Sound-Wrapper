package project;

public class KeyboardDemo {

	public static void main(String[] args) {
		SoundEngine engine = new SoundEngine();

//		engine.loadAndPlayKeysFromFile("Air_PL.txt");

//		engine.loadAndPlayKeysFromFile("MofL.txt");
		
		engine.loadAndPlayKeysFromFile("Teardrop.txt");
		

//		engine.setBpm(110);
//		engine.setKeyboard(Keyboard.SOFT_ACCORDION);
////
//		engine.playKey(Key.F_SHARP, Octaves.NATURAL, KeyDuration.EIGHTH);
//		engine.playKey(Key.C_SHARP, Octaves.NATURAL, KeyDuration.QUARTER);
//		engine.playKey(Key.F_SHARP, Octaves.MINOR, KeyDuration.SIXTEENTH);
//		engine.playKey(Key.G_SHARP, Octaves.MINOR, KeyDuration.EIGHTH);
//		engine.playKey(Key.A_SHARP, Octaves.MINOR, KeyDuration.QUARTER);
//		engine.playKey(Key.G_SHARP, Octaves.MINOR, KeyDuration.EIGHTH);
//		engine.playKey(Key.F_SHARP, Octaves.MINOR, KeyDuration.EIGHTH);
//		engine.playKey(Key.G_SHARP, Octaves.MINOR, KeyDuration.EIGHTH);
//		engine.playKey(Key.A_SHARP, Octaves.MINOR, KeyDuration.EIGHTH);
//		engine.playKey(Key.G_SHARP, Octaves.MINOR, KeyDuration.EIGHTH);
//		engine.playKey(Key.G_SHARP, Octaves.MINOR, KeyDuration.SIXTEENTH);
//		engine.playKey(Key.F_SHARP, Octaves.MINOR, KeyDuration.EIGHTH);
//		engine.playKey(Key.D_SHARP, Octaves.MINOR, KeyDuration.HALF);
//		// engine.playKey(Key.SILENCE, Octaves.MINOR, KeyDuration.SIXTEENTH);

//		System.out.println("Keyboard tests finished.");
		engine.shutdown();
	}
}
