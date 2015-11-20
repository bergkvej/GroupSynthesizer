import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class NoteFrequencies 
{
	public NoteFreq noteFreq;
	public NoteFrequencies()
	{
		
	}
	public enum NoteFreq //Hz
	{
		//s = sharp
		//f = flat
		none(0),
		//1st Octave
		C0(16.35),
		Cs0(17.32),
		D0(18.35),
		Ef0(19.45),
		E0(20.60),
		F0(21.83),
		Fs0(23.12),
		G0(24.50),
		Gs0(25.96),
		A0(27.50),
		Bf0(29.14),
		B0(30.87),
		
		//2nd Octave
		C1(32.70),
		Cs1(34.65),
		D1(36.71),
		Ef1(38.89),
		E1(41.20),
		F1(43.65),
		Fs1(46.25),
		G1(49),
		Gs1(51.91),
		A1(55.00),
		Bf1(58.27),
		B1(61.74),
		
		//3rd Octave
		C2(65.41),
		Cs2(69.30),
		D2(73.42),
		Ef2(77.78),
		E2(82.41),
		F2(87.31),
		Fs2(92.50),
		G2(98.00),
		Gs2(103.8),
		A2(110.0),
		Bf2(116.5),
		B2(123.5),
		
		//4th Octave
		C3(130.8),
		Cs3(138.6),
		D3(146.8),
		Ef3(155.6),
		E3(164.8),
		F3(164.8),
		Fs3(185.0),
		G3(196),
		Gs3(207.7),
		A3(220.0),
		Bf3(233.1),
		B3(246.9),
		
		//5th Octave
		C4(261.6),
		Cs4(277.2),
		D4(293.7),
		Ef4(311.1),
		E4(329.6),
		F4(349.2),
		Fs4(370.0),
		G4(392.0),
		Gs4(415.3),
		A4(440.0),
		Bf4(466.2),
		B4(493.9),
		
		//6th Octave
		C5(523.3),
		Cs5(554.4),
		D5(587.3),
		Ef5(622.3),
		E5(659.3),
		F5(698.5),
		Fs5(740.0),
		G5(784.0),
		Gs5(830.6),
		A5(880.0),
		Bf5(932.3),
		B5(987.8),
		
		//7th Octave
		C6(1047),
		Cs6(1109),
		D6(1175),
		Ef6(1245),
		E6(1319),
		F6(1397),
		Fs6(1480),
		G6(1568),
		Gs6(1661),
		A6(1760),
		Bf6(1865),
		B6(1976),
		
		//8th Octave
		C7(2093),
		Cs7(2217),
		D7(2349),
		Ef7(2489),
		E7(2637),
		F7(2794),
		Fs7(2960),
		G7(3136),
		Gs7(3322),
		A7(3520),
		Bf7(3729),
		B7(3951),
		
		//9th Octave
		C8(4186),
		Cs8(4435),
		D8(4699),
		Ef8(4978),
		E8(5274),
		F8(5588),
		Fs8(5920),
		G8(6272),
		Gs8(6645),
		A8(7040),
		Bf8(7459),
		B8(7902);
		
		private double value;
		private NoteFreq(double value) 
        {
                this.value = value;
        }
		public double getValue()
		{
			return value;
		}
	};
	public static NoteFrequencies.NoteFreq [] getRangeOfWhiteKeyNotes(int lowBoundOctave)
	{
		NoteFrequencies.NoteFreq [] allNotes = NoteFrequencies.NoteFreq.values();
		NoteFrequencies.NoteFreq [] desiredNotes = new NoteFrequencies.NoteFreq[28];
		
		for(int i = 0; i < allNotes.length; i++)
		{
			NoteFrequencies.NoteFreq note = allNotes[i];
			if(note.name().contains(Integer.toString(lowBoundOctave)) || note.name().contains(Integer.toString(lowBoundOctave + 1)) || note.name().contains(Integer.toString(lowBoundOctave + 2)) || note.name().contains(Integer.toString(lowBoundOctave + 3)))
			{
				if(!(note.name().contains("s")) && !(note.name().contains("f")))
				{
					for(int index = 0; index < desiredNotes.length; index++)
					{
						if(desiredNotes[index] == null)
						{
							desiredNotes[index] = note;
							break;
						}
					}
				}
			}
		}
		return desiredNotes;
	}
	
	public static NoteFrequencies.NoteFreq [] getRangeOfBlackKeyNotes(int lowBoundOctave)
	{
		NoteFrequencies.NoteFreq [] allNotes = NoteFrequencies.NoteFreq.values();
		NoteFrequencies.NoteFreq [] desiredNotes = new NoteFrequencies.NoteFreq[28];
		
		for(int i = 0; i < allNotes.length; i++)
		{
			NoteFrequencies.NoteFreq note = allNotes[i];
			if(note.name().contains(Integer.toString(lowBoundOctave)) || note.name().contains(Integer.toString(lowBoundOctave + 1)) || note.name().contains(Integer.toString(lowBoundOctave + 2)) || note.name().contains(Integer.toString(lowBoundOctave + 3)))
			{
				if(note.name().contains("s") || note.name().contains("f"))
				{
					for(int index = 0; index < desiredNotes.length; index++)
					{
						if(desiredNotes[index] == null)
						{
							desiredNotes[index] = note;
							break;
						}
					}
				}
			}
		}
		List<NoteFrequencies.NoteFreq> tempList = Arrays.asList(desiredNotes);
		ArrayList<NoteFrequencies.NoteFreq> desiredNotesList = new ArrayList<>(tempList);
		int keyIndex = 0;
		for(int i = 0; i < desiredNotesList.size(); i++)
		{
			if(keyIndex == 2 || keyIndex == 6)
			{
				try
				{
				desiredNotesList.add(i, NoteFrequencies.NoteFreq.none);
				}
				catch(Exception e)
				{
					System.out.println(e.toString());
				}
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
		NoteFrequencies.NoteFreq [] finalArray = desiredNotesList.toArray(new NoteFrequencies.NoteFreq [desiredNotesList.size()]);
		return finalArray;
	}
	
}