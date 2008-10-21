package rega.genotype;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
 * Created on Apr 7, 2003
 */

/**
 * Represents multiple sequences that are aligned
 */
public class SequenceAlignment
{
    private List<AbstractSequence> sequences;
    int sequenceType;

    public final static int FILETYPE_FASTA = 0;
    public final static int FILETYPE_CLUSTAL = 1;
    public final static int FILETYPE_NEXUS = 2;
    public final static int FILETYPE_PHYLIP = 3;

	public final static int SEQUENCE_DNA = 0;
	public final static int SEQUENCE_AA = 1;
    
    public final static int MAX_NEXUS_TAXUS_LENGTH = 20;
    public final static int MAX_PHYLIP_TAXUS_LENGTH = 8;

    public SequenceAlignment(InputStream inputFile,
                             int fileType)
        throws ParameterProblemException, IOException, FileFormatException
    {
        this.sequences = new ArrayList<AbstractSequence>();

        switch (fileType) {
            case FILETYPE_FASTA:
                readFastaFile(inputFile);
                break;
            case FILETYPE_CLUSTAL:
                //readClustalFile(inputFile);
                throw new ParameterProblemException("Reading clustal not yet supported");
            case FILETYPE_NEXUS:
                //readClustalFile(inputFile);
                throw new ParameterProblemException("Reading nexus not yet supported");
            default:
                throw new ParameterProblemException("Illegal value for fileType");
        }
        
        inputFile.close();
    }

	/*
	 * The most unefficient java code imaginable ... ! Rewrite !
	 */
	public void degap() {
		for (int i = 0; i < getLength();) {
			boolean hasGap = false;

			for (int j = 0; j < sequences.size(); ++j) {
				AbstractSequence s = sequences.get(j);
				
				if (s.getSequence().charAt(i) == '-') {
					hasGap = true;
					break;
				}
			}

			if (hasGap) {
				for (int j = 0; j < sequences.size(); ++j) {
					AbstractSequence s = sequences.get(j);

					s.removeChar(i);
				}
			} else
				++i;
		}
	}

    public boolean areAllEqualLength(boolean force) {
        Iterator<AbstractSequence> i = sequences.iterator();
        int length = -1;

        while (i.hasNext()) {
            AbstractSequence s = i.next();

            if (length == -1)
                length = s.getLength();
            else
                if (s.getLength() != length) {
                    if (force)
                        i.remove();
                    else
                        return false;
                }
        }

        return true;
    }

    SequenceAlignment(List<AbstractSequence> sequences, int sequenceType) {
        this.sequences = sequences;
        this.sequenceType = sequenceType;
    }

    private void readFastaFile(InputStream inputFile)
        throws IOException, FileFormatException
    {
        /*
         * The fasta format (for multiple sequences) as described in
         * http://www.molbiol.ox.ac.uk/help/formatexamples.htm#fasta
         * and
         * http://www.ncbi.nlm.nih.gov/BLAST/fasta.html
         */

        LineNumberReader reader
            = new LineNumberReader(new InputStreamReader(inputFile));

        for (;;) {
            Sequence s = readFastaFileSequence(reader);

            if (s != null) {
                sequences.add(s);
                //System.err.println(s.getName() + " " + s.getLength());
            } else
                return;
        }
    }

    public static Sequence readFastaFileSequence(LineNumberReader reader)
        throws IOException, FileFormatException
    {
        /*
         * first read the header
         */
        String header;
        do {
            header = reader.readLine();
            if (header == null)
                return null; // no new sequence to be found
        } while (header.length() == 0);

        if (header.charAt(0) != '>')
            throw new FileFormatException("Expecting a '>'",
                                          reader.getLineNumber());

        // eat '>'
        header = header.substring(1);
        while (header.charAt(0) == ' ')
			header = header.substring(1);
        // seperate name from description
        int spacePos = header.indexOf(' ');
        String name;
        String description;
        if (spacePos != -1) {   // only a name
           name = makeLegalName(header.substring(0, spacePos));
           description = header.substring(spacePos);
        } else {
           name = makeLegalName(header);
           description = "";
        }

        /*
         * next read the sequence
         */
        StringBuffer sequence = new StringBuffer();
        
        String s;
        do {
            reader.mark(1000);
            s = reader.readLine();
            if (s != null) {
                 if (s.length() > 0 && s.charAt(0) == '>') {
                    // this is the start of a next sequence
                    reader.reset(); 
                    s = null;
                 } else
                    sequence.append(s);
            }
        } while (s != null);

        return new Sequence(name, description, sequence.toString());
    }

    private static String makeLegalName(String name) {
    	String sane = name.replaceAll("/|\\+|\\(|\\)", "");
		try {
			sane = new String(sane.getBytes("ASCII"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	if (sane.length() > 30)
    		return sane.substring(0, 30);
    	else
    		return sane;
	}

	public List<AbstractSequence> getSequences() {
        return sequences;
    }

    public int getLength() {
        return sequences.get(0).getLength();
    }
    
    public SequenceAlignment getSubSequence(int startIndex, int endIndex)
    {
        List<AbstractSequence> subSequences = new ArrayList<AbstractSequence>();
        
        Iterator<AbstractSequence> i = sequences.iterator();
        while (i.hasNext()) {
            AbstractSequence sequence = i.next();
            
            String name = sequence.getName();
            String description = sequence.getDescription() + " [" + startIndex + ", " + endIndex + "]";
            
            subSequences.add(new SubSequence(name, description, sequence, startIndex, endIndex));
        }

        return new SequenceAlignment(subSequences, sequenceType);
    }

    void writeOutput(OutputStream outputFile, int fileType)
        throws IOException, ParameterProblemException
    {
        switch (fileType) {
            case FILETYPE_FASTA:
                writeFastaOutput(outputFile);
                break;
            case FILETYPE_CLUSTAL:
                //writeClustalOutput(outputFile);
                throw new ParameterProblemException("Writing clustal not yet supported");
            case FILETYPE_NEXUS:
                writeNexusOutput(outputFile);
                break;
            case FILETYPE_PHYLIP:
                writePhylipOutput(outputFile);
                break;
             default:
                throw new ParameterProblemException("Illegal value for fileType");
        }
    }
    
    void writeFastaOutput(OutputStream outputFile) throws IOException
    {
        /*
         * The fasta format (for multiple sequences) as described in
         * http://www.molbiol.ox.ac.uk/help/formatexamples.htm#fasta
         * and
         * http://www.ncbi.nlm.nih.gov/BLAST/fasta.html
         */

        Writer writer = new OutputStreamWriter(outputFile);
        final char endl = '\n';
         
        Iterator<AbstractSequence> i = sequences.iterator();
        
        while (i.hasNext()) {
            AbstractSequence seq = i.next();
            
            writer.write('>' + seq.getName() + " " + seq.getDescription() + endl);
            
            final int lineLength = 50;
            
            final int seqLength = seq.getLength();
            for (int j = 0; j < seqLength; j += lineLength) {
                int end = Math.min(j + lineLength, seqLength);
                writer.write(seq.getSequence().substring(j, end));
                writer.write(endl);
            }
        }
        
        writer.flush();
    }

    void writePhylipOutput(OutputStream outputFile) throws IOException
    {
        /*
         * The phylip format (for multiple sequences)
         */

        Writer writer = new OutputStreamWriter(outputFile);
        final char endl = '\n';

        writer.write(sequences.size() + " " + getLength() + endl);
        Iterator<AbstractSequence> i = sequences.iterator();
        
        Set<String> nameSet = new HashSet<String>();
        while (i.hasNext()) {
            AbstractSequence seq = (AbstractSequence) i.next();

            String name = nexusName(seq, nameSet, MAX_PHYLIP_TAXUS_LENGTH);
            nameSet.add(name);

            writer.write(padBack(new StringBuffer(name), MAX_PHYLIP_TAXUS_LENGTH + 2)
            		     + seq.getSequence() + endl);
        }
        
        writer.flush();
    }

    void writeNexusOutput(OutputStream outputFile) throws IOException
    {
        /*
         * The Nexus file format, as taken from an example file
         */
        Writer writer = new OutputStreamWriter(outputFile);
        final char endl = '\n';
        
        writer.write("#NEXUS" + endl);
        writer.write(endl);
        
        Set<String> nameSet = new HashSet<String>();
        List<String> nameList = new ArrayList<String>();

        for (Iterator i = sequences.iterator(); i.hasNext();) {
            AbstractSequence seq = (AbstractSequence) i.next();

            String name = nexusName(seq, nameSet, MAX_NEXUS_TAXUS_LENGTH);
            nameList.add(name);
            nameSet.add(name);
            writer.write("[Name: " + padBack(new StringBuffer(name), MAX_NEXUS_TAXUS_LENGTH + 2)
                        + "Len: " +  padBack(new StringBuffer().append(seq.getLength()), 10)
                        + "Check: 0]" + endl);
            
        }
        
        writer.write(endl);
        
        String dataType[] = { "DNA", "protein" };
        
        writer.write("begin data;" + endl);
        writer.write(" dimensions ntax=" + sequences.size() + " nchar=" + getLength() + ";" + endl);
        writer.write(" format datatype=" + dataType[sequenceType] + " interleave missing=? gap=-;" + endl);
        writer.write("  matrix" + endl);

        final int blockUnit = 20;
        final int blockUnitsPerBlock = 5;
        for (int j = 0; j < getLength(); j += (blockUnit * blockUnitsPerBlock)) {
            for (int i = 0; i < sequences.size(); ++i) {
                AbstractSequence seq = sequences.get(i);
                
                writer.write(new String(padFront(new StringBuffer((String) nameList.get(i)), MAX_NEXUS_TAXUS_LENGTH + 2)));
                for (int k = 0; k < blockUnitsPerBlock; ++k) {
                    int start = j + (k * blockUnit);
                    int end = Math.min(start + blockUnit, seq.getSequence().length());
                    String s = seq.getSequence().substring(start, end);
                    
                    writer.write(" " + s);
                    if (s.length() < blockUnit)
                        break;
                }
                writer.write(endl);
            }
            
            writer.write(endl);
        }
        
        writer.write("  ;" + endl);
        writer.write("end;" + endl);
        writer.flush();
    }

    String nexusName(AbstractSequence seq, Set names, int maxlength) {
        String name = seq.getName();
        name = name.substring(0, Math.min(maxlength, name.length()));
        name = name.replace('-', '_');
        name = name.replace(',', '_');
        name = name.replace('/', '_');
        if (names.contains(name)) {
  
            String base = name.substring(0, name.length() - 3) + '_';

            int c = 0;
            do {
                if (c < 10)    
                    name = base + '0' + c;
                else
                    name = base + c;
                ++c;
            } while (names.contains(name));
        }

        return name;
    }

    private static StringBuffer padBack(StringBuffer s, int total) {
        StringBuffer result = s;
        int numAdded = total - s.length();
        for (int i = 0; i < numAdded; ++i)
          result.append(' ');
        
        return result;
    }

    private static StringBuffer padFront(StringBuffer s, int total) {
        StringBuffer result = new StringBuffer();
        int numAdded = total - s.length();
        for (int i = 0; i < numAdded; ++i)
          result.append(' ');

        result.append(s);

        return result;
    }

	public void reverseTaxa() {
		Collections.reverse(sequences);
	}

	public int getSequenceType() {
		return sequenceType;
	}

	public void setSequenceType(int i) {
		sequenceType = i;
	}
    
    public void removeAllGapSequences() {
        for (int i = 0; i < sequences.size(); ++i) {
            Sequence s = (Sequence) sequences.get(i);
            if (s.firstNonGapPosition() == s.getLength()) {
                sequences.remove(i);
                --i;
            }
        }
    }

    public AbstractSequence findSequence(String name) {
        for (int i = 0; i < sequences.size(); ++i)
            if (sequences.get(i).getName().equals(name))
                return sequences.get(i);

        return null;
    }

    public int getIndex(AbstractSequence sequence) {
        return sequences.indexOf(sequence);
    }

    public SequenceAlignment selectSequences(List<String> selection) {
        List<AbstractSequence> selected = new ArrayList<AbstractSequence>();
        for (int i = 0; i < selection.size(); ++i) {
            AbstractSequence seq = findSequence(selection.get(i));
            if (seq == null) {
                System.err.println("Could not find sequence: \""
                        + selection.get(i) + "\"");
            }
            selected.add(seq);
        }
        
        return new SequenceAlignment(selected, sequenceType);
    }
}
