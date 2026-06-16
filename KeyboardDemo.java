package proyecto;

public class KeyboardDemo {

	public static void main(String[] args) {
		SoundEngine engine = new SoundEngine();

//		engine.loadAndPlayKeysFromFile("src/proyecto/Air_PL.txt");

//		engine.loadAndPlayKeysFromFile("src/proyecto/alegria.txt");
		
		engine.loadAndPlayKeysFromFile("src/proyecto/Teardrop.txt");
		

//		engine.setBpm(110);
//		engine.setKeyboard(Keyboard.SOFT_ACCORDEON);
////
//		engine.playKey(Key.F_SHARP, Octaves.NATURAL, KeyDuration.CORCHEA);
//		engine.playKey(Key.C_SHARP, Octaves.NATURAL, KeyDuration.NEGRA);
//		engine.playKey(Key.F_SHARP, Octaves.MINOR, KeyDuration.SEMICORCHEA);
//		engine.playKey(Key.G_SHARP, Octaves.MINOR, KeyDuration.CORCHEA);
//		engine.playKey(Key.A_SHARP, Octaves.MINOR, KeyDuration.NEGRA);
//		engine.playKey(Key.G_SHARP, Octaves.MINOR, KeyDuration.CORCHEA);
//		engine.playKey(Key.F_SHARP, Octaves.MINOR, KeyDuration.CORCHEA);
//		engine.playKey(Key.G_SHARP, Octaves.MINOR, KeyDuration.CORCHEA);
//		engine.playKey(Key.A_SHARP, Octaves.MINOR, KeyDuration.CORCHEA);
//		engine.playKey(Key.G_SHARP, Octaves.MINOR, KeyDuration.CORCHEA);
//		engine.playKey(Key.G_SHARP, Octaves.MINOR, KeyDuration.SEMICORCHEA);
//		engine.playKey(Key.F_SHARP, Octaves.MINOR, KeyDuration.CORCHEA);
//		engine.playKey(Key.D_SHARP, Octaves.MINOR, KeyDuration.BLANCA);
//		// engine.playKey(Key.SILENCE, Octaves.MINOR, KeyDuration.SEMICORCHEA);

//		System.out.println("Fin de pruebas de Keyboard.");
		engine.shutdown();
	}
}
