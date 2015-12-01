import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
import javafx.stage.Stage;

public class SynthesizerApplication extends Application
{
	Canvas waveformDiagram = new Canvas(200, 150);
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
	float pan = 0.0f;
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
		
		GraphicsContext gc = waveformDiagram.getGraphicsContext2D();
		gc.fillRect(0, 0, waveformDiagram.getWidth(), waveformDiagram.getHeight());
		
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
		
		//pan (Pan) Control Slider
		Slider panControl = new Slider(-1.0f, 1.0f, 0.0f);
		Text panControlTitle = new Text("Pan Control");
		panControl.setShowTickMarks(true);
		panControl.setShowTickLabels(true);
		panControl.setMajorTickUnit(25);
		panControl.setBlockIncrement(1.0f);
		panControl.setOrientation(Orientation.HORIZONTAL);
		panControl.valueProperty().addListener(listener -> {
			pan = (float) panControl.getValue();
		});
		
		//default waveform
		
		//Waveform Selector
		ObservableList<String> waveforms = FXCollections.observableArrayList("Sine", "Square", "Triangle", "Sawtooth");
		final ComboBox<String> waveformSelector = new ComboBox<String>(waveforms);
		waveformSelector.setValue("Sine");
		waveformSelector.valueProperty().addListener(listener -> {
			waveform = waveformSelector.getValue().toString();
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
								try
								{
									pianoKey.play(waveform);
								}
								catch(Exception e)
								{
														
								}
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
							pianoKey.sound.stop();
						}
						else if(pianoKey.getHeight() == 100.0)
						{
							pianoKey.setFill(Color.WHITE);
							pianoKey.sound.stop();
						}
						playNoteThread.interrupt();
					}
				});
		
		Pane root = new Pane();
		root.getChildren().add(keyboard);
		root.getChildren().add(gainControl);
		root.getChildren().add(gainControlTitle);
		root.getChildren().add(panControl);
		root.getChildren().add(panControlTitle);
		root.getChildren().add(waveformDiagram);
		root.getChildren().add(waveformSelector);
		root.getChildren().add(attackSlider);
		root.getChildren().add(decaySlider);
		root.getChildren().add(sustainSlider);
		root.getChildren().add(releaseSlider);
		root.getChildren().add(a);
		root.getChildren().add(d);
		root.getChildren().add(s);
		root.getChildren().add(r);

		
		attackSlider.setLayoutX(50);
		attackSlider.setLayoutY(10);
		decaySlider.setLayoutX(80);
		decaySlider.setLayoutY(10);
		sustainSlider.setLayoutX(110);
		sustainSlider.setLayoutY(10);
		releaseSlider.setLayoutX(140);
		releaseSlider.setLayoutY(10);

		a.setLayoutX(53);
		a.setLayoutY(10);
		d.setLayoutX(83);
		d.setLayoutY(10);
		s.setLayoutX(113);
		s.setLayoutY(10);
		r.setLayoutX(143);
		r.setLayoutY(10);
		
		
		panControlTitle.setLayoutX(10);
		panControlTitle.setLayoutY(300);
		
		panControl.setLayoutX(40);
		panControl.setLayoutY(300);
		
		gainControlTitle.setLayoutX(280);
		gainControlTitle.setLayoutY(297);
		
		gainControl.setLayoutX(300);
		gainControl.setLayoutY(300);
		
		primaryStage.setScene(new Scene(root, 560, 600));
		primaryStage.show();
		primaryStage.setMaxWidth(primaryStage.getWidth()); primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMaxHeight(primaryStage.getHeight()); primaryStage.setMinHeight(primaryStage.getHeight());
		
		waveformDiagram.setTranslateX(primaryStage.getWidth()-200);
		
		waveformSelector.setLayoutX(400);
		waveformSelector.setLayoutY(155);
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

	private void drawVisualizer(String waveform, GraphicsContext gc)
	{
		if(waveform.equals("sine"))
		{
			
			
		}
		else if(waveform.equals("square"))
		{
			
		}
		else if(waveform.equals("saw"))
		{
			
		}
		else
		{
			
		}
	}
}