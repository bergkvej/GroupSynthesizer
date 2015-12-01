import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sound.Sound;
import wave.SawtoothWave;
import wave.SineWave;
import wave.SquareWave;
import wave.TriangleWave;

public class PianoKey extends Rectangle
{
	private NoteFrequencies.NoteFreq frequency;
	private Color color;
	public SourceDataLine line;
	private boolean stage_attack = false;
	private boolean stage_decay = false;
	private boolean stage_sustain = false;
	private boolean stage_release = false;
	Sound sound = null;

	public PianoKey()
	{
		
	}
	public PianoKey(double x, double y, double width, double height, NoteFrequencies.NoteFreq frequency, Color color)
	{
		this.setLayoutX(x);
		this.setLayoutY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.frequency = frequency;
		this.setFill(color);
		this.setStroke(Color.BLACK);
		this.setStrokeWidth(1);
	}
	
	
	public NoteFrequencies.NoteFreq getFrequency()
	{
		return this.frequency;
	}
	
	public void setFrequency(NoteFrequencies.NoteFreq frequency)
	{
		this.frequency = frequency;
	}
	public Color getColor()
	{
		return this.color;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public Sound play(String waveform, float gain) 
	{  
		switch(waveform)
		{
			case "Sine":
			{
				sound = new SineWave().getPeriod(frequency.getValue());
				break;
			}
			case "Sawtooth":
			{
				sound = new SawtoothWave().getPeriod(frequency.getValue());
				break;
			}
			case "Square":
			{
				sound = new SquareWave().getPeriod(frequency.getValue());
				break;
			}
			case "Triangle":
			{
				sound = new TriangleWave().getPeriod(frequency.getValue());
				break;
			}
			default:
			{
				break;
			}
		}
		sound.start(gain);
//		sound.shiftGain(1.0);

		return sound;
	}
	public void stop()
	{
		sound.stop();
	}
}
