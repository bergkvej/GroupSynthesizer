import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SynthesizerApplication extends Application
{
	Pane keyboard = new Pane();
	PianoKey whiteKeys[] = new PianoKey[100];
	PianoKey blackKeys[] = new PianoKey[100];
	Thread playNoteThread;
	float gain = -12;
	int octave = 4;
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		primaryStage.setTitle("Synthesizer");
		octave = 4; //default octave
		FixedFreqSine sin = new FixedFreqSine();
		Button playButton = new Button("Play");
		Slider gainControl = new Slider(-100, 24, 0);
		Text gainControlTitle = new Text("Gain Control");
		gainControl.setShowTickMarks(true);
		gainControl.setShowTickLabels(true);
		gainControl.setMajorTickUnit(3);
		gainControl.setBlockIncrement(0.25f);
		gainControl.setOrientation(Orientation.VERTICAL);
		gainControl.valueProperty().addListener(listener -> {
			gain = (float)gainControl.getValue();
		});
		
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
									pianoKey.play(gain);
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
							pianoKey.stop();
						}
						else if(pianoKey.getHeight() == 100.0)
						{
							pianoKey.setFill(Color.WHITE);
							pianoKey.stop();
						}
						playNoteThread.interrupt();
					}
				});
		
		playButton.setOnAction(new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent event)
					{
						try
						{
						sin.play();
						}
						catch(Exception e)
						{
							
						}
					}
				});
		Pane root = new Pane();
		root.getChildren().add(playButton);
		root.getChildren().add(keyboard);
		root.getChildren().add(gainControl);
		root.getChildren().add(gainControlTitle);
		
		gainControlTitle.setLayoutX(280);
		gainControlTitle.setLayoutY(297);
		
		gainControl.setLayoutX(300);
		gainControl.setLayoutY(300);
		
		primaryStage.setScene(new Scene(root, 560, 600));
		primaryStage.show();
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
}
