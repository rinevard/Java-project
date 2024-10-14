package com.protein.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.protein.data.Sequence;

public class SequenceParser {
    public static List<Sequence> parse(InputStream inputStream, String fileName) throws IOException {
        List<Sequence> sequences = new ArrayList<>();
        
        if (fileName.endsWith(".tsv")) {
            sequences = parseTsv(inputStream);
        } else if (fileName.endsWith(".fa") || fileName.endsWith(".fasta")) {
            sequences = parseFasta(inputStream);
        } else if (fileName.endsWith(".txt")) {
            sequences = parseTxt(inputStream);
        }
        
        return sequences;
    }

    private static List<Sequence> parseTsv(InputStream inputStream) throws IOException {
        List<Sequence> sequences = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 2) {
                    Sequence sequence = new Sequence();
                    sequence.setName(parts[0]);
                    sequence.setSequence(parts[1]);
                    sequences.add(sequence);
                }
            }
        }
        return sequences;
    }

    private static List<Sequence> parseFasta(InputStream inputStream) throws IOException {
        List<Sequence> sequences = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            StringBuilder sequenceBuilder = new StringBuilder();
            String currentName = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(">")) {
                    if (currentName != null) {
                        Sequence sequence = new Sequence();
                        sequence.setName(currentName);
                        sequence.setSequence(sequenceBuilder.toString());
                        sequences.add(sequence);
                        sequenceBuilder = new StringBuilder();
                    }
                    currentName = line.substring(1).trim();
                } else {
                    sequenceBuilder.append(line.trim());
                }
            }
            
            if (currentName != null) {
                Sequence sequence = new Sequence();
                sequence.setName(currentName);
                sequence.setSequence(sequenceBuilder.toString());
                sequences.add(sequence);
            }
        }
        return sequences;
    }

    private static List<Sequence> parseTxt(InputStream inputStream) throws IOException {
        // Implement custom parsing logic for .txt files
        // This is a placeholder implementation
        return parseTsv(inputStream);
    }
}