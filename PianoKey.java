import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PianoKey extends Rectangle
{
	private NoteFrequencies.NoteFreq frequency;
	private Color color;
	public SourceDataLine line;
	private boolean stage_attack = false;
	private boolean stage_decay = false;
	private boolean stage_sustain = false;
	private boolean stage_release = false;

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
	
	public void play(float gain, float pan, String waveform) throws InterruptedException, LineUnavailableException 
	   {
		
	      final int SAMPLING_RATE = 44100;            		// Audio sampling rate
	      final int SAMPLE_SIZE = 2;                  		// Audio sample size in bytes
	  	  NoteFrequencies note = new NoteFrequencies();		// Creates an instance of a note frequency
	      note.noteFreq = this.frequency;					// Sets the pianoKey's frequency to the note frequency
	      double fNoteFreq = note.noteFreq.getValue();      // Sets the note to play

	      //Position through the sine wave as a percentage (i.e. 0 to 1 is 0 to 2*PI)
	      double fCyclePosition = 0;        

	      //Open up audio output, using 44100hz sampling rate, 16 bit samples, stereo, and big 
	      // endian byte ordering
	      AudioFormat format = new AudioFormat(SAMPLING_RATE, 16, 2, true, true);
	      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
	      if (!AudioSystem.isLineSupported(info))
	      {
	         System.out.println("Line matching " + info + " is not supported.");
	         throw new LineUnavailableException();
	      }

	      line = (SourceDataLine)AudioSystem.getLine(info);
	      line.open(format);
	      
	      
	      //Sets up controls to edit the sound
	      FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
	      FloatControl panControl = (FloatControl) line.getControl(FloatControl.Type.PAN);
	      panControl.setValue(pan);
	      line.start();
	      gainControl.setValue(gain);

	      // Make our buffer size match audio system's buffer
	      ByteBuffer cBuf = ByteBuffer.allocate(line.getBufferSize());   

	      int ctSamplesTotal = SAMPLING_RATE*10;         // Output for roughly 5 seconds
	      float inaudibleFreq = -70.0f;
	      
	      

	      //On each pass main loop fills the available free space in the audio buffer
	      //Main loop creates audio samples for sine wave, runs until we tell the thread to exit
	      //Each sample is spaced 1/SAMPLING_RATE apart in time
	      while (ctSamplesTotal>0) 
	      {
	    	  //Attack
	    	  if(gainControl.getValue()<= gain)
	    	  {
	    		  gainControl.setValue(inaudibleFreq+=.00001f);
	    		  stage_attack = true;
	    	  }
	    	  else
	    	  {
	    		  stage_attack = false;
	    	  }
	    	  if(stage_attack == false)
	    		  stage_decay = true;
	    	  if(stage_decay == true)
	    	  {
	    		  float sustain = -30.0f;
	    		//Decay
	    		if(gainControl.getValue() >= sustain)
		    	  gainControl.setValue(gain-=.00001);
	    	  }
	    	  
	    	  //Release
	    	  //gainControl.setValue(gain-=.00001)
	         double fCycleIncrement = fNoteFreq/SAMPLING_RATE;  // Fraction of cycle between samples

	         cBuf.clear();                            // Discard samples from previous pass

	      	  // Figure out how many samples we can add
	         int ctSamplesThisPass = line.available()/SAMPLE_SIZE; 
	         
	         for (int i=0; i < ctSamplesThisPass; i++) 
	         {
	        	 if(waveform.equals("Sine"))
	        	 {
	        		 cBuf.putShort((short)((32768) * Math.sin(2*Math.PI * fCyclePosition)));
	        	 }
	        	 else if(waveform.equals("Square"))
	        	 {
	        		 if(fCyclePosition % 2 == 0)
	        		 {
	        			 cBuf.putShort((short) 1);
	        		 }
	        		 else
	        		 {
	        			 cBuf.putShort((short)(-1));
	        		 }
	        	 }
	        	 else if(waveform.equals("Saw"))
	        	 {
	        		 cBuf.putShort((short)((32768) * Math.floor(fCyclePosition + 0.05)));
	        	 }
	        	 else if(waveform.equals("Triangle"))
	        	 {
	        		 
	        	 }

	            fCyclePosition += fCycleIncrement;
	            if (fCyclePosition > 1)
	               fCyclePosition -= 1;
	         }
	         

	         //Write samples to the line buffer.  If the audio buffer is full, this will 
	         // block until there is room (we never write more samples than buffer will hold)
	         line.write(cBuf.array(), 0, cBuf.position());            
	         ctSamplesTotal -= ctSamplesThisPass;     // Update total number of samples written 

	         //Wait until the buffer is at least half empty  before we add more
	         while (line.getBufferSize()/2 < line.available())   
	            Thread.sleep(1);                                             
	      }


	      //Done playing the whole waveform, now wait until the queued samples finish 
	      //playing, then clean up and exit
	      line.drain();                                         
	      line.close();
	   }
	public void stop()
	{
		//Stops the note from playing
	}
}
