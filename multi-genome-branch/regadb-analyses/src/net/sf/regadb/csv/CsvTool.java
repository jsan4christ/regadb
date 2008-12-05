package net.sf.regadb.csv;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created on Dec 28, 2003
 */

/**
 * @author kdf
 */
public class CsvTool {

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length == 0)
			printUsageAndExit();

		CmdLineParser cmdLineParser = new CmdLineParser();
		Option inFileOption = cmdLineParser.addStringOption('i', "in");
		Option outFileOption = cmdLineParser.addStringOption('o', "out");
		Option rangeOption
			= cmdLineParser.addStringOption('r', "range");
		Option patternOption
			= cmdLineParser.addStringOption('p', "pattern");
		Option columnsOption
			= cmdLineParser.addStringOption('c', "columns");
		Option headerOption
			= cmdLineParser.addBooleanOption('h', "headervalues");
		Option selectValueOption
			= cmdLineParser.addStringOption('s', "selectvalue");
		Option valueOption
			= cmdLineParser.addStringOption('v', "value");
		Option editOption
			= cmdLineParser.addStringOption('e', "edit");
		Option whereColumnOption
			= cmdLineParser.addStringOption('w', "wherecolumn");
		Option thresholdOption
			= cmdLineParser.addDoubleOption('t', "threshold");
		Option lumpValuesOption
			= cmdLineParser.addBooleanOption('l', "lump");
		Option sampleSizeOption
			= cmdLineParser.addDoubleOption('a', "samplesize");
		Option stratificationColumnOption
			= cmdLineParser.addStringOption('z', "stratify");
		Option targetColumnOption
			= cmdLineParser.addStringOption('y', "target");
		Option arffAttributesOption
			= cmdLineParser.addStringOption('f', "arffattributes");
		Option numLevelsOption
			= cmdLineParser.addDoubleOption('n', "numlevels");
		Option alternativeOption
			= cmdLineParser.addStringOption('b', "alternative");

		try {
			String[] rargs = new String[args.length - 1];
			for (int i = 0; i < rargs.length; ++i)
				rargs[i] = args[i + 1];
			cmdLineParser.parse(rargs);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsageAndExit();
		}

		InputStream input = null;
		OutputStream output = null;
		String inputStreamName = null;
		String outputStreamName = null;
		try {
			inputStreamName
				= (String) cmdLineParser.getOptionValue(inFileOption);
			input =
				inputStreamName != null
					? new BufferedInputStream(new FileInputStream(inputStreamName))
					: System.in;
			
			outputStreamName
				= (String) cmdLineParser.getOptionValue(outFileOption);
			output =
				outputStreamName != null
					? (OutputStream) new BufferedOutputStream(new FileOutputStream(outputStreamName))
					: (OutputStream) System.out;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		String range
			= (String) cmdLineParser.getOptionValue(rangeOption);
		String pattern
			= (String) cmdLineParser.getOptionValue(patternOption);
		String columns
			= (String) cmdLineParser.getOptionValue(columnsOption);
		String selectValue
			= (String) cmdLineParser.getOptionValue(selectValueOption);
		String value
			= (String) cmdLineParser.getOptionValue(valueOption);
		String edit
			= (String) cmdLineParser.getOptionValue(editOption);
		Boolean headerValue
			= (Boolean) cmdLineParser.getOptionValue(headerOption);
		String whereColumn
			= (String) cmdLineParser.getOptionValue(whereColumnOption);
		Double threshold
			= (Double) cmdLineParser.getOptionValue(thresholdOption);
		Boolean lumpValues
			= (Boolean) cmdLineParser.getOptionValue(lumpValuesOption);
		Double sampleSize
			= (Double) cmdLineParser.getOptionValue(sampleSizeOption);
		String stratificationColumn
			= (String) cmdLineParser.getOptionValue(stratificationColumnOption);
		String targetColumn
			= (String) cmdLineParser.getOptionValue(targetColumnOption);
		String arffAttributes
			= (String) cmdLineParser.getOptionValue(arffAttributesOption);
		Double numLevels
			= (Double) cmdLineParser.getOptionValue(numLevelsOption);
		String alternative
			= (String) cmdLineParser.getOptionValue(alternativeOption);

		if (args.length == 0)
			printUsageAndExit();

		String command = args[0];

		try {
			if (command.equals("transverse")) {
				Table table = new Table(input, false);
				Table result = new Table();
				for (int i = 0; i < table.numColumns(); ++i) {
					result.rows.add(table.getColumn(i));
				}
				result.exportAsCsv(output);
			} else if (command.equals("select-columns")) {
				Table table = new Table(input, true);
				ArrayList selected = selectColumns(table, range, pattern, columns, headerValue);

				table.readSelectedColumns(input, selected, output);
		
//				for (int i = 0; i < selected.size(); ++i) {
//					result.addColumn(table.getColumn(((Integer) selected.get(i)).intValue()),
//									 result.numColumns());
//				}
		
				//table.exportAsCsv(output);
			} else if (command.equals("not-columns") || command.equals("prune-columns")) {
				Table table = new Table(input, true);

				ArrayList selected = null;
				if (command.equals("prune-columns")) {
					selected = selectPruneColumns(table);
				} else {
					selected = selectColumns(table, range, pattern, columns, headerValue);
				}

				ArrayList inverse = new ArrayList();		
				for (int ii = 0; ii < table.numColumns(); ++ii) {
					boolean found = false;
					for (int i = 0; i < selected.size(); ++i) {
						int c = ((Integer) selected.get(i)).intValue();
						if (c == ii) {
							found = true;
							break;
						}
					}
					if (!found)
						inverse.add(new Integer(ii));
						//result.addColumn(table.getColumn(ii), result.numColumns());
				}
		
				table.readSelectedColumns(input, inverse, output);
			} else if (command.equals("select-rows") || command.equals("not-rows")) {
				boolean invertSelection = command.equals("not-rows");
				Table table = new Table(input, false);

				ArrayList selected = selectColumns(table, null, null, whereColumn, headerValue);

				if (selected.size() != 1) {
					System.err.println("cvstool select-rows -w: Only one column may be selected.");
					System.exit(1);
				}
				
				int c = ((Integer) selected.get(0)).intValue();
				
				Table result = new Table();
				result.rows.add(table.rows.get(0));

				Pattern p = Pattern.compile(selectValue);
				
				for (int r = 1; r < table.numRows(); ++r) {
					String entry = table.valueAt(c,r);
					Matcher m = p.matcher(entry);
					boolean matches = m.matches();
					if ((!invertSelection && matches) || (invertSelection && !matches)) {
						result.rows.add(table.rows.get(r));
					}
				}
				
				result.exportAsCsv(output);
			} else if (command.equals("edit")) {
				if (value == null || edit == null)
					printUsageAndExit();

				Table table = new Table(input, false);
				ArrayList selected = selectColumns(table, range, pattern, columns, headerValue);
				Pattern p = Pattern.compile(value);
				Pattern sp = null;

				int whereColumnId = -1;

				if (whereColumn != null && selectValue != null) {
					ArrayList whereColumns = selectColumns(table, null, null, whereColumn, headerValue);
					if (whereColumns.size() != 1) {
						System.err.println("cvstool edit -w: only column may be selected.");
						System.exit(1);
					}
					whereColumnId = ((Integer) whereColumns.get(0)).intValue();
					sp = Pattern.compile(selectValue);
				}

				for (int j = 1; j < table.numRows(); ++j) {
					boolean matches = true;
					if (whereColumnId != -1) {
						Matcher spm = sp.matcher(table.valueAt(whereColumnId, j));
						matches = spm.matches();
					}
					
					if (matches) {
						for (int i = 0; i < selected.size(); ++i) {
							int c = ((Integer) selected.get(i)).intValue();
							Matcher m = p.matcher(table.valueAt(c, j));
						
							if (m.matches()) {
								table.setValue(c, j, m.replaceFirst(edit));
							}
						}
					}
				}

				table.exportAsCsv(output);
			} else if (command.equals("sample")) {
				if (sampleSize == null)
					printUsageAndExit();

				Table table = new Table(input, false);

				Table result = new Table();
				result.rows.add(table.rows.get(0));

				Table result2 = null;				
				if (alternative != null) {
					result2 = new Table();
					result2.rows.add(table.rows.get(0));
				}

				int sampleCount;
				if (sampleSize.doubleValue() < 1) {
					sampleCount = (int) ((table.numRows() - 1) * sampleSize.doubleValue());
				} else
					sampleCount = (int) sampleSize.doubleValue();
				System.err.println("SampleCount: " + sampleCount);

				Iterator i = null;
				for (;;) {
					ArrayList resultRows = new ArrayList();
					ArrayList alternativeRows = new ArrayList();
					int count;

					if (stratificationColumn != null) {
						/*
						 * sample within each stratum: for each value of stratification column.
						 */
						int stratificationC = getColumn(table, headerValue, stratificationColumn);
						Map stratHistogam = table.histogram(stratificationC);

						if (i == null)
							i = stratHistogam.keySet().iterator();

						if (!i.hasNext())
							break;

						String value2 = (String) i.next();
						for (int j = 1; j < table.numRows(); ++j) {
							if (table.valueAt(stratificationC, j).equals(value2)) {
								resultRows.add(new Integer(j));
							}
						}

						count = (int)(((double)resultRows.size() / (table.numRows() - 1))*sampleCount);
					} else {
						for (int j = 1; j < table.numRows(); ++j) {
							resultRows.add(new Integer(j));
						}
						count = sampleCount;
					}

					while (resultRows.size() > count) {
						int k = (int) (Math.random() * resultRows.size());
						alternativeRows.add(resultRows.get(k));
						resultRows.remove(k);
					}
				
					for (int j = 0; j < resultRows.size(); ++j) {
						int k = ((Integer) resultRows.get(j)).intValue();

						//System.err.println(k);
					
						result.rows.add(table.rows.get(k));
					}

					if (result2 != null) {
						for (int j = 0; j < alternativeRows.size(); ++j) {
							int k = ((Integer) alternativeRows.get(j)).intValue();

							//System.err.println(k);
					
							result2.rows.add(table.rows.get(k));
						}
					}
					
					if (stratificationColumn == null)
						break;
				}

				result.exportAsCsv(output);
				if (result2 != null)
					result2.exportAsCsv(new BufferedOutputStream(new FileOutputStream(alternative)));
			} else if (command.equals("bootstrap")) {
				Table table = new Table(input, false);

				Table result = new Table();
				result.rows.add(table.rows.get(0));

				if (stratificationColumn != null) {
					/*
					 * resample within each stratum: for each value of stratification column.
					 */
					int stratificationC = getColumn(table, headerValue, stratificationColumn);
					Map stratHistogam = table.histogram(stratificationC);

					for (Iterator i = stratHistogam.keySet().iterator(); i.hasNext();) {
						String value2 = (String) i.next();
						int count = ((Integer) stratHistogam.get(value2)).intValue();
						int rows[] = new int[count];
						
						int r = 0;
						for (int j = 1; j < table.numRows(); ++j) {
							if (table.valueAt(stratificationC, j).equals(value2)) {
								rows[r++] = j;
							}
						}
						
						for (int j = 0; j < count; ++j) {
							int k = (int)(Math.random() * count);
							result.rows.add(table.rows.get(rows[k]));
						}
					}
				} else {
					for (int j = 0; j < table.numRows() - 1; ++j) {
						int k = 1 + (int)(Math.random() * (table.numRows() - 1));
					
						result.rows.add(table.rows.get(k));
					}
				}

				result.exportAsCsv(output);
			} else if (command.equals("arff")) {
				Table table = new Table(input, false);
				Table attrTable = null;
				
				if (arffAttributes != null) {
					try {
						attrTable = new Table(new BufferedInputStream(new FileInputStream(arffAttributes)), false);
					} catch (FileNotFoundException e3) {
						e3.printStackTrace();
					}
				}

				table.exportAsArff(output, "data", attrTable);
			} else if (command.equals("spss")) {
				Table table = new Table(input, false);
				table.exportAsSpss(output);
			} else if (command.equals("histogram")) {
				Table table = new Table(input, false);
				ArrayList selected = selectColumns(table, range, pattern, columns, headerValue);
				histogram(selected, table, output, threshold, lumpValues);
			} else if (command.equals("stratify")) {
				Table table = new Table(input, false);
				stratify(table, output, stratificationColumn, targetColumn, headerValue, threshold, value);
			} else if (command.equals("vd")) {
				Table table = new Table(input, false);
				
				if (outputStreamName == null) {
					System.err.println("Output filename must be specified");
					System.exit(-1);
				} else {
					try {
						OutputStream outputVd =
							new BufferedOutputStream(new FileOutputStream(outputStreamName + ".vd"));
						OutputStream outputIdt =
							new BufferedOutputStream(new FileOutputStream(outputStreamName + ".idt"));				
						table.exportAsVdFiles(outputVd, outputIdt);
						try {
							outputVd.flush();
							outputIdt.flush();
						} catch (IOException e3) {
							e3.printStackTrace();
						}
					} catch (FileNotFoundException e2) {
						e2.printStackTrace();
						System.exit(-1);
					}
				}
			} else if (command.equals("concat")) {
				Table table = new Table(input, false);
				ArrayList selected = selectColumns(table, range, pattern, columns, headerValue);

				ArrayList concatenated = new ArrayList();
				concatenated.add(targetColumn);
				for (int i = 1; i < table.numRows(); ++i) {
					String v = "";
					for (int j = 0; j < selected.size(); ++j) {
						int c = ((Integer) selected.get(j)).intValue();
						if (j != 0)
							v += "_";
						v += table.valueAt(c, i);		
					}
					concatenated.add(v);
				}
				
				table.addColumn(concatenated, table.numColumns());
				
				table.exportAsCsv(output);
			} else if (command.equals("contingency")) {
				Table table = new Table(input, false);
				ArrayList selected = selectColumns(table, range, pattern, columns, headerValue);

				ArrayList histogram = table.histogram();

				int i1 = ((Integer) selected.get(0)).intValue();
				int i2 = ((Integer) selected.get(1)).intValue();
				int i3 = -1;
				if (selected.size() > 2)
					i3 = ((Integer) selected.get(2)).intValue();
				ArrayList entries1 = new ArrayList(((Map) histogram.get(i1)).keySet());
				ArrayList entries2 = new ArrayList(((Map) histogram.get(i2)).keySet());
				ArrayList entries3 = (i3 != -1 ? new ArrayList(((Map) histogram.get(i3)).keySet()) : null);

				for (int j = 0; j < entries2.size(); ++j) {
					System.out.print("\t" + entries2.get(j));
				}
				System.out.println();

				if (i3 == -1) {
					printContingencyTable(table, i1, i2, entries1, entries2, -1, null);				
				} else {
					for (int j = 0; j < entries3.size(); ++j) {
						String v3 = (String) entries3.get(j);
						System.out.println(table.valueAt(i3, 0) + "=" + v3 + ":");

						printContingencyTable(table, i1, i2, entries1, entries2, i3, v3);
					}
				}
			} else if (command.equals("mutation")) {
				Table table = new Table(input, false);
				ArrayList selected = selectColumns(table, range, pattern, columns, headerValue);
				
				ArrayList histogram = table.histogram();

				for (int i = 0; i < selected.size(); ++i) {
					int c = ((Integer) selected.get(i)).intValue();
					
					Map m = (Map) histogram.get(c);
					Map corrected = new HashMap();

					/**
					 * Make a corrected map with decomposed counts.
					 */
					for (Iterator it = m.keySet().iterator(); it.hasNext();) {
						String v = (String) it.next();
						int count = ((Integer) m.get(v)).intValue();
						
						if (v.length() == 1) {
							if (corrected.containsKey(v))
								count += ((Integer) corrected.get(v)).intValue();
							
							corrected.put(v, new Integer(count));
						} else {
							for (int mut = 0; mut < v.length(); ++mut) {
								String d = v.substring(mut, mut+1);
								int dcount = count;

								if (corrected.containsKey(d))
									dcount += ((Integer) corrected.get(d)).intValue();
								
								corrected.put(d, new Integer(dcount));
							}
						}
					}

					/**
					 * Sort the decomposed counts.
					 */
					SortedSet values = new TreeSet();
					int l = 0;
					for (Iterator j = corrected.keySet().iterator(); j.hasNext(); ++l) {					
						String k = (String) j.next();
						int count = ((Integer) corrected.get(k)).intValue();
						values.add(new AttributeValue(k, count, l));
					}

					/*
					 * Replace each value with the least frequent decomposed one, which has a
					 * frequency larger than the threshold. If none could be found, use the one
					 * with the largest frequency.
					 */
					int count_threshold = 1;
					if (threshold != null) {
						count_threshold = (int)(threshold.doubleValue() * (table.numRows() - 1));
					}

					for (int j = 1; j < table.numRows(); ++j) {
						String v = table.valueAt(c, j);
						
						if (v.length() > 1) {
							String bestD = null;
							int bestFreq = 0;

							for (int mut = 0; mut < v.length(); ++mut) {
								String d = v.substring(mut, mut+1);

								/*
								 * lookup the count of 'd', and compare it with the threshold
								 */
								for (Iterator it = values.iterator(); it.hasNext();) {
									AttributeValue attvalue = (AttributeValue) it.next();
									if (attvalue.name.equals(d)) {
										if (bestFreq >= count_threshold) {
											if ((attvalue.count >= count_threshold)
												&& (attvalue.count < bestFreq)) {
												bestD = d;
												bestFreq = attvalue.count;
											}
										} else if (attvalue.count > bestFreq) {
											bestD = d;
											bestFreq = attvalue.count;
										}
									}
								}
							}

							//System.err.println(v + " -> " + bestD);		
							table.setValue(c, j, bestD);
						}
					}
				}

				table.exportAsCsv(output);
			} else if (command.equals("discretize")) {
				if (numLevels == null) {
					System.err.println("discretize: need to specify the desired number of levels");
					System.exit(1);
				}
				
				Table table = new Table(input, false);

				ArrayList selected = selectColumns(table, null, null, columns, headerValue);

				if (selected.size() != 1) {
					System.err.println("discretize: only one column can be discretized");
					System.exit(1);
				}
				
				int column = ((Integer) selected.get(0)).intValue();
				
				double minValue = Double.MAX_VALUE;
				double maxValue = Double.MIN_VALUE;
				for (int j = 1; j < table.numRows(); ++j) {
					if (!table.valueAt(column, j).equals("")) {
						double d = Double.parseDouble(table.valueAt(column, j));
						
						if (d < minValue)
							minValue = d;
						if (d > maxValue)
							maxValue = d;
					}
				}
				
				System.err.println("range: " + minValue + " - " + maxValue);
				
				ArrayList levels = new ArrayList();
				double levelSize = (maxValue - minValue) / (numLevels.doubleValue());
				
				NumberFormat format = NumberFormat.getInstance();
				format.setMaximumFractionDigits(2);
				format.setGroupingUsed(false);

				for (int i = 0; i < numLevels.intValue(); ++i) {
					String level = "[" + format.format(minValue + (i * levelSize)) + "-"
						+ format.format(minValue + ((i + 1)*levelSize)) + "]";
					System.err.println(level);
					levels.add(level);
				}
				System.err.println();
				
				ArrayList discretized = new ArrayList();
				discretized.add(targetColumn);
				for (int j = 1; j < table.numRows(); ++j) {
					if (!table.valueAt(column, j).equals("")) {
						double d = Double.parseDouble(table.valueAt(column, j));
						
						int level = Math.min(numLevels.intValue() - 1, (int)((d - minValue)/levelSize));
						discretized.add(levels.get(level));
					} else
						discretized.add("");
				}
				
				table.addColumn(discretized, table.numColumns());
				
				table.exportAsCsv(output);
			} else {
				printUsageAndExit();
			}
			
			try {
				output.flush();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private static void printContingencyTable(Table table, int i1, int i2, ArrayList entries1,
											  ArrayList entries2, int i3, String v3) {
		int contingency[][] = new int[entries1.size()][entries2.size()];

		for (int j = 1; j < table.numRows(); ++j) {
			if (i3 == -1 || table.valueAt(i3, j).equals(v3)) {
				int s1 = entries1.indexOf(table.valueAt(i1, j));
				int s2 = entries2.indexOf(table.valueAt(i2, j));
				if ((s1 != -1) && (s2 != -1))
				++contingency[s1][s2];
			}
		}

		for (int i = 0; i < entries1.size(); ++i) {
			for (int j = 0; j < entries2.size(); ++j) {
				System.out.print("\t" + contingency[i][j]);
			}
			System.out.println("\t" + table.valueAt(i1, 0) + "=" + entries1.get(i));
		}
	}

	private static void stratify(Table table, OutputStream output, String stratificationColumn, String targetColumn,
								 Boolean headerValue, Double thresholdValue, String value) {
		if (stratificationColumn == null || targetColumn == null || value == null) {
			printUsageAndExit();
		}

		double threshold = (thresholdValue == null ? 0. : thresholdValue.doubleValue());
		int stratificationC = getColumn(table, headerValue, stratificationColumn);
		int targetC = getColumn(table, headerValue, targetColumn);

		/*
		 * Assert targetC has only 2 possible values (for now).
		 * 
		 * Get counts (and row numbers) of targetC for every value of stratificationC.
		 * Chose a target ratio of target values, as the one with lowest 'value'.
		 * 
		 * Randomly delete rows within each stratificationC.
		 */
		HashMap stratValues = new HashMap(); // in it a pair of ArrayLists, with row ids of 'value' and 'non-value' rows.
		
		for (int j = 1; j < table.numRows(); ++j) {
			String stratV = table.valueAt(stratificationC, j);
			String targetV = table.valueAt(targetC, j);
			
			ArrayList al[] = (ArrayList[]) stratValues.get(stratV);
			if (al == null) {
				al = new ArrayList[] { new ArrayList(), new ArrayList() };
			}

			al[targetV.equals(value) ? 0 : 1].add(new Integer(j));

			stratValues.put(stratV, al);
		}
		
		double minValueFrac = 1.;

		for (Iterator i = stratValues.keySet().iterator(); i.hasNext();) {
			String stratV = (String) i.next();
			
			ArrayList al[] = (ArrayList[]) stratValues.get(stratV);
			
			double valueFrac = (double) al[0].size() / (al[0].size() + al[1].size());
			
			if ((valueFrac < minValueFrac) && (valueFrac > threshold))
				minValueFrac = valueFrac;
		}


		Table result = new Table();
		result.rows.add(table.rows.get(0));

		for (Iterator i = stratValues.keySet().iterator(); i.hasNext();) {
			String stratV = (String) i.next();
			
			ArrayList al[] = (ArrayList[]) stratValues.get(stratV);

			double valueFrac = (double) al[0].size() / (al[0].size() + al[1].size());
			
			int value0Keep, value1Keep;

			if (valueFrac < minValueFrac) {
				value0Keep = al[0].size();
				value1Keep = (int) ((1 - minValueFrac) / (minValueFrac) * al[0].size() + 0.5);
			} else {
				value0Keep = (int) (minValueFrac / (1 - minValueFrac) * al[1].size() + 0.5);
				value1Keep = al[1].size();
			}

			System.err.println(stratificationColumn + " = " + stratV + ": "
							   + targetColumn + "=" + value + ": "
							   + valueFrac
							   + " (" + al[0].size() + "/" + (al[0].size() + al[1].size()) + ")"
							   + " keeping " + value0Keep + "/" + (value0Keep + value1Keep));

			for (int j = 0; j < value0Keep; ++j) {
				int k = (int) (Math.random() * al[0].size());
				//System.err.println(k);
				int r = ((Integer) al[0].get(k)).intValue();

				result.rows.add(table.rows.get(r));
				al[0].remove(k);
			}

			for (int j = 0; j < value1Keep; ++j) {
				int k = (int) (Math.random() * al[1].size());
				int r = ((Integer) al[1].get(k)).intValue();

				result.rows.add(table.rows.get(r));
				al[1].remove(k);
			}
		}

		result.exportAsCsv(output);									
	}

	private static int getColumn(Table table, Boolean headerValue, String s) {
		if (headerValue == Boolean.TRUE) {			
			int j = table.findInRow(0, s);
			if (j == -1 && s.startsWith("\""))
				j = table.findInRow(0, s.substring(1, s.length() - 2));
			if (j != -1)
				return j;
			else {
				System.err.println("No column matching " + s);
				System.exit(2);
				return 0;
			}
		} else {
			int j = Integer.parseInt(s);
			return j;
		}
	}

	private static ArrayList selectPruneColumns(Table table) {
		ArrayList result = new ArrayList();

		ArrayList histogram = table.histogram();
		Map posMap = new HashMap(); // of ArrayList with indexes
		
		for (int i = 0; i < table.numColumns(); ++i) {
			String cName = table.valueAt(i, 0);
			String posName = cName.substring(0, cName.length() - 1);
			
			if (!posMap.containsKey(posName))
				posMap.put(posName, new ArrayList());

			((ArrayList) posMap.get(posName)).add(new Integer(i));
		}

		for (Iterator j = posMap.keySet().iterator(); j.hasNext();) {
			String posName = (String) j.next();
			ArrayList ii = (ArrayList) posMap.get(posName);
			
			if (ii.size() != 2)
				continue;
			
			float f1 = computeEntropy((Map) histogram.get(((Integer) ii.get(0)).intValue()), table.numRows() - 1);
			float f2 = computeEntropy((Map) histogram.get(((Integer) ii.get(1)).intValue()), table.numRows() - 1);
			
			if (f1 > f2)
				result.add(ii.get(1));
			else
				result.add(ii.get(0));
		}

		return result;
	}

	private static float computeEntropy(Map map, int size) {
		float result = 0;

		for (Iterator i = map.keySet().iterator(); i.hasNext();) {
			String k = (String) i.next();
			float p = ((float) ((Integer) map.get(k)).intValue()) / size;
			result += -p * Math.log(p)/Math.log(2);
		}
		
		return result;
	}

	private static ArrayList selectColumns(Table table,
									  	   String range, String pattern, String columns,
									  	   Boolean headerValue) {
		ArrayList result = new ArrayList();
		if (range != null) {
			if (pattern != null || columns != null) {
				System.err.println("-r cannot be combined with -c or -p");
				printUsageAndExit();
			}
		
			String[] borders = range.split("-");

			if (borders.length != 2) {
				System.err.println("invalid range specification");
				printUsageAndExit();
			}
			int from = Integer.parseInt(borders[0]) - 1;
			int to = Integer.parseInt(borders[1]) - 1;
		
			for (int i = 0; i < table.numColumns(); ++i) {
				if (i >= from && i <= to) {
					
					result.add(new Integer(i));
				}
			}
		} else if (pattern != null) {
			if (range != null || columns != null)
				printUsageAndExit();
		
			Pattern p = Pattern.compile(pattern);

			for (int i = 0; i < table.numColumns(); ++i) {
				Matcher m = p.matcher(table.valueAt(i, 0));
		
				if (m.matches()) {
					result.add(new Integer(i));
				}
			}
		} else if (columns != null) {
			if (range != null || pattern != null)
				printUsageAndExit();

			String[] cs = columns.split(",");
		
			for (int i = 0; i < cs.length; ++i) {
				if (headerValue == Boolean.TRUE) {
					int j = table.findInRow(0, cs[i]);
					if (j == -1 && cs[i].startsWith("\""))
						j = table.findInRow(0, cs[i].substring(1, cs[i].length() - 2));
					if (j != -1)
						result.add(new Integer(j));
					else {
						System.err.println("No column matching " + cs[i]);
						System.exit(2);					 
					}
				} else {
					int c = Integer.parseInt(cs[i]);

					result.add(new Integer(c));
				}
			}
		} else {
			for (int i = 0; i < table.numColumns(); ++i) {
				result.add(new Integer(i));
			}
		}
		
		//return new ArrayList(new TreeSet(result));
		return result;
	}

	private static class AttributeValue implements Comparable {
		String name;
		int count;
		int tie;
		
		AttributeValue(String name, int count, int tie) {
			this.name = name;
			this.count = count;
			this.tie = tie;
		}

		public int compareTo(Object arg0) {
			AttributeValue other = (AttributeValue) arg0;
			
			return (count - other.count) * 1000	+ (tie - other.tie);
			
		}
	}

	private static void histogram(ArrayList columns, Table table, OutputStream output,
								  Double threshold, Boolean lumpValues) {
		ArrayList histogram = table.histogram();

		if (threshold != null) {
			int numColumnsRemoved = 0;
			
			if (lumpValues == null) {
				
				/*
				 * 05 December 2008
				 * because of disappearing wild types under certain circumstances involving mixtures
				 * I (gbehey0) tried to put a threshold without lumpValues. However this part of the code
				 * seemed broken (the index went out of bounds) and I wasn't able to quickly fix the old code.
				 * I re-wrote this part (the old code is still commented a few lines down). 
				 * 
				 * problem before (threshold 2%):
				 * RT65K (WT) n : 1.95 % => deleted
				 * RT65R y : 2.4 % => not deleted
				 * RT65E y : 0.02 % => deleted
				 * 
				 * problem : wild type got deleted but mutation remained due to mixtures.
				 * solution: only delete column when occurrence of 'y' < threshold.
				 * 
				 * new possible problem: (threshold 2% - no mixtures): 
				 * RT65K (WT) 	n : 5 % => ok
				 * RT65R 		y : 4 % => ok
				 * RT65E 		y : 1 % => deleted
				 * 
				 * what to do with the 1% rows containing RT65E = y ? delete rows?
				 */
				ArrayList<Integer> colIndicesToBeRemoved = new ArrayList<Integer>();
				for(int i = 0; i < columns.size();i++){
					int colindex = ((Integer)columns.get(i)).intValue();
					Map m = (Map) histogram.get(colindex);
					for (Iterator j = m.keySet().iterator(); j.hasNext();) {
						String k = (String) j.next();
						int c = ((Integer) m.get(k)).intValue();

						if ((double) c / (table.numRows() - 1)
							< threshold.doubleValue() && k.equals("y")) {
							colIndicesToBeRemoved.add(colindex);
						}
					}					
				}
				
				Collections.sort(colIndicesToBeRemoved);
				for(int i = colIndicesToBeRemoved.size() - 1; i >= 0 ; i--){
					table.deleteColumn(colIndicesToBeRemoved.get(i));
				}
				
				//OLD CODE
				
//				boolean hasDeleted = false;
//				int numRowsRemoved = 0;
//
//				int numIterations = 1;
//				do {
//					hasDeleted = false;
//					System.err.print("(" + numIterations + ") ");
//
//					for (int ii = 0; ii < columns.size(); ++ii) {
//						int i = ((Integer)columns.get(ii)).intValue();
//
//						Map m = (Map) histogram.get(i);
//						ArrayList smaller = new ArrayList();
//
//						for (Iterator j = m.keySet().iterator(); j.hasNext();) {
//							String k = (String) j.next();
//							int c = ((Integer) m.get(k)).intValue();
//
//							if ((double) c / (table.numRows() - 1)
//								< threshold.doubleValue()) {
//								smaller.add(k);
//							}
//						}
//
//						if (smaller.size() == m.size() - 1) {
//							table.deleteColumn(i);
//							columns.remove(ii);
//							--ii;
//							++numColumnsRemoved;
//							System.err.print("c");
//						} else {
//							for (Iterator j = m.keySet().iterator(); j.hasNext();) {
//								String k = (String) j.next();
//								int c = ((Integer) m.get(k)).intValue();
//
//								if ((double) c / (table.numRows() - 1)
//									< threshold.doubleValue()) {
//									numRowsRemoved
//										+= table.deleteRowsWithValue(i, k);
//									hasDeleted = true;
//									System.err.print("r");
//								}
//							}
//						}
//					}
//
//					if (hasDeleted)
//						histogram = table.histogram();
//					System.err.println();
//					++numIterations;
//				} while (hasDeleted);
//
//				System.err.println(
//					"Deleted "
//						+ numRowsRemoved
//						+ " rows, "
//						+ numColumnsRemoved
//						+ " columns");
					
			} else {
				/*
				 * For every attribute, we sort the values in ascending frequency order,
				 * and lump the last value together with the preceding one until the last
				 * one is above the threshold
				 */
				ArrayList removeColumns = new ArrayList();

				for (int ii = 0; ii < columns.size(); ++ii) {
					int i = ((Integer)columns.get(ii)).intValue();

					Map m = (Map) histogram.get(i);

					SortedSet values = new TreeSet();
					int l = 0;
					for (Iterator j = m.keySet().iterator(); j.hasNext(); ++l) {					
						String k = (String) j.next();
						int c = ((Integer) m.get(k)).intValue();
						values.add(new AttributeValue(k, c, l));
					}
					
					boolean again = false;
					do {
						again = false;
						Iterator it = values.iterator();
						AttributeValue v = (AttributeValue) it.next();

						if ((double) v.count / (table.numRows() - 1)
							< threshold.doubleValue()) {
                            if (it.hasNext()) {                            
                                /*
							     * lump together with next value.
							     */
							  AttributeValue v2 = (AttributeValue) it.next();

							  values.remove(v);
							  values.remove(v2);

							  v2.count += v.count;
							  String oldv2name = v2.name;
							  v2.name = v2.name + "/" + v.name;

							  values.add(v2);

							  for (int j = 1; j < table.numRows(); ++j) {
								  if (table.valueAt(i, j).equals(v.name)
									  || table.valueAt(i, j).equals(oldv2name)) {
									  table.setValue(i, j, v2.name);
								  }
							  }

							  System.err.print(".");

							  again = true;
                            }
						}
					} while (again);
					
					if (values.size() == 1) {
						removeColumns.add(new Integer(i));
						System.err.print("c");					
					}
				}
				
				/* FIXME: assumes columns in increasing order. */
				Collections.sort(removeColumns);
				for (int i = removeColumns.size() - 1; i >= 0; --i) {
					table.deleteColumn(((Integer)removeColumns.get(i)).intValue());
				}

				System.err.println();
				System.err.println("Removed " + removeColumns.size() + " columns.");
				histogram = table.histogram();
			}
			table.exportAsCsv(output);
		} else
			printHistogram(table, histogram);
	}

	private static void printHistogram(Table table, ArrayList histogram) {
		for (int i = 0; i < histogram.size(); ++i) {
			System.out.print(i + ": " + table.valueAt(i, 0));
			Map m = (Map) histogram.get(i);
			
			//calculate sum of values to be able to calculate %
			Integer sum = 0; 
			for (Iterator sumIterator = m.keySet().iterator(); sumIterator.hasNext();) {
				String k = (String) sumIterator.next();
				sum += (Integer) m.get(k);
			}
			
			for (Iterator j = m.keySet().iterator(); j.hasNext();) {
				String k = (String) j.next();
				Integer number = (Integer) m.get(k);
				System.out.print(" " + k + "(" + number + " - "+ ((((float) number)*100)/sum) +"%)");
			}
			System.out.println();
		}
	}

	private static void printUsageAndExit() {
		System.err.println("usage:");
		System.err.println();
		System.err.println("csvtool (select-columns|not-columns|prune-columns|edit|arff|spss|histogram");
		System.err.println("         |mutation|vd|sample|bootstrap|stratify|discretize) options");
		System.err.println();
		System.err.println("  general options:");
		System.err.println("      -i,--in infile.csv");
		System.err.println("      -o,--out outfile.csv");
		System.err.println();
		System.err.println("  select-columns|not-columns|prune-columns options:");
		System.err.println("      -r,--range first-last");
		System.err.println("      -p,--pattern regexp");
		System.err.println("      [-h,--headervalues] -c,--columns c1,c2,c3,c4,...");
		System.err.println();
		System.err.println("  select-rows|not-rows options:");
		System.err.println("      [-h,--headervalues] -w column -s,--selectvalue regexp");
		System.err.println();
		System.err.println("  edit options:");
		System.err.println("      -r,--range first-last");
		System.err.println("      -p,--pattern regexp");
		System.err.println("      [-h,--headervalues] -c,--columns c1,c2,c3,c4,...");
		System.err.println("      [-w,--wherecolumn c -s regexp]");
		System.err.println("      -v,--value regexp");
		System.err.println("      -e,--edit substitution");
		System.err.println();
		System.err.println("  arff options:");
		System.err.println("      -b,--bifvariables filename");
		System.err.println();
		System.err.println("  spss options:");
		System.err.println("      (none)");
		System.err.println();
		System.err.println("  histogram options:");
		System.err.println("      -r,--range first-last");
		System.err.println("      -p,--pattern regexp");
		System.err.println("      [-h,--headervalues] -c,--columns c1,c2,c3,c4,...");
		System.err.println("      -t,--threshold threshold");
		System.err.println("      [-l,--lump]");
		System.err.println();
		System.err.println("  mutation options:");
		System.err.println("      -r,--range first-last");
		System.err.println("      -p,--pattern regexp");
		System.err.println("      [-h,--headervalues] -c,--columns c1,c2,c3,c4,...");
		System.err.println("      [-t,--threshold t]");
		System.err.println();
		System.err.println("  sample options:");
		System.err.println("      -a,--samplesize count");
		System.err.println("      [-b,--alternative complementdata.csv]");
		System.err.println("      [[-h --headervalues] -z,--stratificationcolumn c]");
		System.err.println();
		System.err.println("  stratify options:");
		System.err.println("      [-h,--headervalues]");
		System.err.println("      -y,--targetcolumn c");
		System.err.println("      -v,--value v");
		System.err.println("      [-t,--treshold t]");
		System.err.println("      -z,--stratificationcolumn c");
		System.err.println();
		System.err.println("  discretize options:");
		System.err.println("      [-h,--headervalues] -c,--columns c");
		System.err.println("      -y,--targetcolumn c");
		System.err.println("      -n,--numlevels n");
		System.err.println("      -x,--seperatezero");
		System.err.println();
		System.err.println("  concat options:");
		System.err.println("      -r,--range first-last");
		System.err.println("      -p,--pattern regexp");
		System.err.println("      [-h,--headervalues] -c,--columns c");
		System.err.println("      -y,--targetcolumn c");
		System.exit(2);
	}
}
