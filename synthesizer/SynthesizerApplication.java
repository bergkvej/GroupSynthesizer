package synthesizer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sound.Sound;

public class SynthesizerApplication extends Application
{
	Pane visualizer = new Pane();
	int numVisualizerBars = 125;
	Rectangle[] visualizerBars = new Rectangle[numVisualizerBars];
	Sound playingSound = null;
	double visualizerPhaseShift = 0;
	
	static File fileWave = null;
	
	Pane keyboard = new Pane();
	PianoKey whiteKeys[] = new PianoKey[100];
	PianoKey blackKeys[] = new PianoKey[100];
	Thread playNoteThread;
	float attack;
	float decay;
	float sustain;
	float release;
	String waveform = "Sine";
	float gain = -12.0f;
	int octave = 4;
		
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		//Application Title
		primaryStage.setTitle("Synthesizer");
		
		//default octave
		octave = 4;
		
		visualizer.setPrefSize(300, 200);
		visualizer.setStyle("-fx-background-color: black;");
		visualizer.setScaleY(-1);
		
		initVisualizer();
		
		//Visualizer bar slider
		Slider visualizerSlider = new Slider(20,200,125);
		visualizerSlider.setShowTickMarks(true);
		visualizerSlider.setShowTickLabels(true);
		visualizerSlider.setMajorTickUnit(30);
		visualizerSlider.setBlockIncrement(1);
		visualizerSlider.setOrientation(Orientation.VERTICAL);
		visualizerSlider.valueProperty().addListener(listener -> {
			numVisualizerBars = (int)visualizerSlider.getValue();
			initVisualizer();
		});
		
		//Gain (Volume) Control Slider
		Slider gainControl = new Slider(-100, 6, 0);
		Text gainControlTitle = new Text("Master Volume");
		gainControl.setShowTickMarks(true);
		gainControl.setShowTickLabels(true);
		gainControl.setMajorTickUnit(3);
		gainControl.setBlockIncrement(0.25f);
		gainControl.setOrientation(Orientation.VERTICAL);
		gainControl.valueProperty().addListener(listener -> {
			gain = (float)gainControl.getValue();
		});
		
		//default waveform
		
		//Waveform Selector
		String[] waveformTypes = {"Sine", "Square", "Triangle", "Sawtooth", "File"};

		ObservableList<String> waveforms = FXCollections.observableArrayList(waveformTypes);
		final ComboBox<String> waveformSelector = new ComboBox<String>(waveforms);
		waveformSelector.setValue("Sine");
		waveformSelector.valueProperty().addListener(listener -> {
			waveform = waveformSelector.getValue().toString();
			if(waveform.equals("File")) {
				FileChooser openFileChooser = new FileChooser();
				openFileChooser.setTitle("Open File");
				fileWave = openFileChooser.showOpenDialog(primaryStage);
			}
		});
		
		//ADSR Sliders
		Slider attackSlider = new Slider(0, 1000, 500);
		Slider decaySlider = new Slider(0, 1000, 500);
		Slider sustainSlider = new Slider(0, 1000, 500);
		Slider releaseSlider = new Slider(0, 1000, 500);
		attackSlider.setShowTickMarks(true);
		decaySlider.setShowTickMarks(true);
		sustainSlider.setShowTickMarks(true);
		releaseSlider.setShowTickMarks(true);
		attackSlider.setBlockIncrement(100);
		decaySlider.setBlockIncrement(100);
		sustainSlider.setBlockIncrement(100);
		releaseSlider.setBlockIncrement(100);
		attackSlider.setOrientation(Orientation.VERTICAL);
		decaySlider.setOrientation(Orientation.VERTICAL);
		sustainSlider.setOrientation(Orientation.VERTICAL);
		releaseSlider.setOrientation(Orientation.VERTICAL);
		attackSlider.valueProperty().addListener(listener -> {
			attack = (float) attackSlider.getValue();
		});
		decaySlider.valueProperty().addListener(listener -> {
			decay = (float) decaySlider.getValue();
		});
		sustainSlider.valueProperty().addListener(listener -> {
			sustain = (float) sustainSlider.getValue();
		});
		releaseSlider.valueProperty().addListener(listener -> {
			release = (float) releaseSlider.getValue();
		});
		//ADSR Labels
		Text a = new Text("A");
		Text d = new Text("D");
		Text s = new Text("S");
		Text r = new Text("R");
		//Setup keyboard view
		keyboard.setPrefSize(560, 100);
		keyboard.setLayoutX(0);
		keyboard.setLayoutY(500);
		setupKeyboard();
		
		keyboard.setOnMousePressed(new EventHandler<MouseEvent>()
				{
					@Override
					public void handle(MouseEvent event)
					{
						PianoKey pianoKey = (PianoKey) event.getTarget();
						pianoKey.setFill(Color.DEEPSKYBLUE);
						
						Runnable r = new Runnable()
						{
							public void run()
							{
								playingSound = pianoKey.play(waveform, attack);
								setVisualizer(playingSound);
							}
						};
						playNoteThread = new Thread(r);
						playNoteThread.start();	
					}
				});
		keyboard.setOnMouseReleased(new EventHandler<MouseEvent>()
				{
					@Override
					public void handle(MouseEvent event)
					{
						PianoKey pianoKey = (PianoKey) event.getTarget();
						if(pianoKey.getHeight() == 66.0)
						{
							pianoKey.setFill(Color.BLACK);
							playingSound = null;
							pianoKey.sound.stop();
						}
						else if(pianoKey.getHeight() == 100.0)
						{
							pianoKey.setFill(Color.WHITE);
							pianoKey.sound.stop();
							playingSound = null;
						}
						playNoteThread.interrupt();
					}
				});
		
		Pane root = new Pane();
		root.getChildren().add(keyboard);
		root.getChildren().add(gainControl);
		root.getChildren().add(gainControlTitle);
		root.getChildren().add(visualizer);
		root.getChildren().add(visualizerSlider);
		root.getChildren().add(waveformSelector);
		root.getChildren().add(attackSlider);
		root.getChildren().add(decaySlider);
		root.getChildren().add(sustainSlider);
		root.getChildren().add(releaseSlider);
		root.getChildren().add(a);
		root.getChildren().add(d);
		root.getChildren().add(s);
		root.getChildren().add(r);
		
		primaryStage.setScene(new Scene(root, 560, 600));
		primaryStage.show();
		primaryStage.setMaxWidth(primaryStage.getWidth()); primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMaxHeight(primaryStage.getHeight()); primaryStage.setMinHeight(primaryStage.getHeight());
		
		visualizer.setLayoutX(primaryStage.getWidth() - visualizer.getWidth());
		visualizer.setLayoutY(0);
		
		visualizerSlider.setLayoutX(visualizer.getLayoutX() - visualizerSlider.getWidth());
		visualizerSlider.setLayoutY((visualizer.getPrefHeight() - visualizerSlider.getHeight()) / 2.0);

		double optimalHeight = visualizerSlider.getLayoutY();
		
		attackSlider.setLayoutX(80);
		attackSlider.setLayoutY(optimalHeight);
		decaySlider.setLayoutX(110);
		decaySlider.setLayoutY(optimalHeight);
		sustainSlider.setLayoutX(140);
		sustainSlider.setLayoutY(optimalHeight);
		releaseSlider.setLayoutX(170);
		releaseSlider.setLayoutY(optimalHeight);

		gainControlTitle.setRotate(-90);
		gainControlTitle.setLayoutX(-30);
		gainControlTitle.setLayoutY(110);
		
		gainControl.setLayoutX(20);
		gainControl.setLayoutY(optimalHeight);
		
		a.setLayoutX(83);
		a.setLayoutY(optimalHeight);
		d.setLayoutX(113);
		d.setLayoutY(optimalHeight);
		s.setLayoutX(143);
		s.setLayoutY(optimalHeight);
		r.setLayoutX(173);
		r.setLayoutY(optimalHeight);
		
		waveformSelector.setLayoutX(400);
		waveformSelector.setLayoutY(250);

		int[] textXPos = {70, 165, 260, 355, 470};
		for(int i = 0 ; i < waveformTypes.length; i++) {
			Text text = new Text(waveformTypes[i]);
			text.setTextAlignment(TextAlignment.CENTER);
			text.setLayoutX(textXPos[i]);
			text.setLayoutY(250);
			int x = 75 + 100 * i;
			Slider amplitudeSlider = new Slider(0,1,0);
			amplitudeSlider.setOrientation(Orientation.VERTICAL);
			amplitudeSlider.setLayoutX(x - 20);
			amplitudeSlider.setLayoutY(text.getLayoutY() + 20);
			root.getChildren().add(amplitudeSlider);
			Slider frequencySlider = new Slider(0,1,0);
			frequencySlider.setOrientation(Orientation.VERTICAL);
			frequencySlider.setLayoutX(x + 20);
			frequencySlider.setLayoutY(text.getLayoutY() + 20);
			root.getChildren().add(frequencySlider);
			Rectangle setFrequency = new Rectangle();
			root.getChildren().add(text);
		}	
		
		boolean running = true;
		new Thread() {
			public void run() {
				long lastTime = System.currentTimeMillis();
				while(running) {
					if(System.currentTimeMillis() - lastTime > 20) {
						lastTime = System.currentTimeMillis();
						if(playingSound != null) {
							double frequency = 1;
							String[] splitString = playingSound.toString().split(" ");
							if(!splitString[splitString.length - 1].equals("null")) {
								frequency = Double.valueOf(splitString[splitString.length - 1]);
							}
							visualizerPhaseShift += .03 * frequency / 1000.0;
							if(visualizerPhaseShift >=1) { 
								visualizerPhaseShift = 0;
							}
							setVisualizer(playingSound);
						}
					}
				}
			}
		}.start();
	}
	
	private void setupKeyboard()
	{
		int totalNumWhiteKeysDisplayed = 28;
		int totalNumBlackKeysDisplayed = 20;
		int totalNumKeysDisplayed = 48;
		int keyIndex = 0;
		NoteFrequencies.NoteFreq [] whiteKeyFrequencies = NoteFrequencies.getRangeOfWhiteKeyNotes(octave - 2);
		NoteFrequencies.NoteFreq [] blackKeyFrequencies = NoteFrequencies.getRangeOfBlackKeyNotes(octave - 2);

		//Draw White Piano Keys
		for(int whiteKeyNum = 0; whiteKeyNum < totalNumWhiteKeysDisplayed; whiteKeyNum++)
		{
			PianoKey whiteKey = new PianoKey((whiteKeyNum*20.0), 0.0, 20.0, 100.0, whiteKeyFrequencies[whiteKeyNum], Color.WHITE);
			whiteKeys[whiteKeyNum] = whiteKey;
			keyboard.getChildren().add(whiteKey);

		}
		for(int blackKeyNum = 1; blackKeyNum < totalNumWhiteKeysDisplayed; blackKeyNum++)
		{

			if(keyIndex != 2 && keyIndex != 6)
			{
				PianoKey blackKey = new PianoKey((20.0*blackKeyNum - 5.0), 0.0, 10.0, 66.0, blackKeyFrequencies[blackKeyNum - 1], Color.BLACK);
				blackKeys[blackKeyNum] = blackKey;
				keyboard.getChildren().add(blackKey);
			}
			
			if(keyIndex == 6)
			{
				keyIndex = 0;
			}
			else
			{
				keyIndex++;
			}
		}
		for(int keyNum = 0; keyNum < totalNumKeysDisplayed; keyNum++)
		{
			//Draws Space in between keys
			Rectangle space = new Rectangle(20*keyNum, 0, 1, keyboard.getHeight());
			keyboard.getChildren().add(space);
		}
		
		Rectangle pianoSeperator = new Rectangle(0, 0, keyboard.getWidth(), 1);	
		keyboard.getChildren().add(pianoSeperator);
	}
	
	public void initVisualizer() {
		visualizer.getChildren().clear();
		visualizerBars = new Rectangle[numVisualizerBars];
		for(int i = 0; i < numVisualizerBars; i++) {
			Rectangle rectangle = new Rectangle();
			rectangle.setWidth(visualizer.getPrefWidth() / (double)(numVisualizerBars));
			rectangle.setHeight(visualizer.getPrefHeight() / 2.0);
			rectangle.setFill(i % 2 == 0 ? Color.BLUE : Color.BLUEVIOLET);
			rectangle.setX(i*(visualizer.getPrefWidth() / numVisualizerBars));
			rectangle.setY(0);
			visualizer.getChildren().add(rectangle);
			visualizerBars[i] = rectangle;
		}
	}
	
	public void setVisualizer(Sound sound) {
		if(sound != null) {
			for(int i = 0; i < numVisualizerBars; i++) {
				Rectangle rectangle = visualizerBars[i];
				int index = (int)(((double)(i) / (double)(numVisualizerBars) + visualizerPhaseShift) * sound.getData().length) % sound.getData().length;
				int beginIndex = (index % 2 == 0) ? index : index - 1;
				int endIndex = beginIndex + 1;
				if(endIndex < sound.getData().length) {
					byte value0 = sound.getData()[beginIndex];
					byte value1 = sound.getData()[endIndex];
					ByteBuffer bb = ByteBuffer.allocate(2);
					bb.order(ByteOrder.BIG_ENDIAN);
					bb.put(value0);
					bb.put(value1);
					short value = bb.getShort(0);
					double length = (0.5 - (double)(value) / (double)(Short.MAX_VALUE) * 0.5) * 0.95;
					rectangle.setHeight(visualizer.getPrefHeight() * length);
				}
			}	
		}
	}
}