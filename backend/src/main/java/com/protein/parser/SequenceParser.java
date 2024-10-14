package com.protein.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

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
            String headerLine = reader.readLine(); // 读取表头
            if (headerLine == null) {
                return sequences; // 空文件
            }

            String[] headers = headerLine.split("\t");
            List<String> headerList = Arrays.asList(headers);
            boolean isType2 = headerList.contains("index") && headerList.contains("accessions") && headerList.contains("interpros") && headerList.contains("orgs");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t", -1); // -1 保留空字段
                if (parts.length != headers.length) {
                    // 行的列数与表头不匹配，跳过或记录错误
                    continue;
                }

                Sequence sequence = new Sequence();

                if (isType2) { // 第二种 TSV 类型
                    // 假设列顺序为：index, proteins, accessions, sequences, annotations, interpros, orgs
                    for (int i = 0; i < headers.length; i++) {
                        String header = headers[i].trim().toLowerCase();
                        String value = parts[i].trim();
                        switch (header) {
                            case "index":
                                try {
                                    sequence.setIndexNumber(Integer.parseInt(value));
                                } catch (NumberFormatException e) {
                                    sequence.setIndexNumber(null); // 或设置默认值
                                }
                                break;
                            case "proteins":
                                sequence.setProteins(value);
                                break;
                            case "accessions":
                                // 移除末尾的分号（如果有）并设置
                                if (value.endsWith(";")) {
                                    value = value.substring(0, value.length() - 1);
                                }
                                sequence.setAccessions(value);
                                break;
                            case "sequences":
                                sequence.setSequence(value);
                                break;
                            case "annotations":
                                sequence.setAnnotations(value);
                                break;
                            case "interpros":
                                sequence.setInterpros(value);
                                break;
                            case "orgs":
                                try {
                                    sequence.setOrgs(Integer.parseInt(value));
                                } catch (NumberFormatException e) {
                                    sequence.setOrgs(null); // 或设置默认值
                                }
                                break;
                            default:
                                // 其他字段可以忽略或记录
                                break;
                        }
                    }
                } else { // 第一种 TSV 类型
                    // 假设列顺序为：proteins, sequences, annotations
                    for (int i = 0; i < headers.length; i++) {
                        String header = headers[i].trim().toLowerCase();
                        String value = parts[i].trim();
                        switch (header) {
                            case "proteins":
                                sequence.setProteins(value);
                                break;
                            case "sequences":
                                sequence.setSequence(value);
                                break;
                            case "annotations":
                                sequence.setAnnotations(value);
                                break;
                            default:
                                // 其他字段可以忽略或记录
                                break;
                        }
                    }
                }

                sequences.add(sequence);
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
                line = line.trim();
                if (line.startsWith(">")) {
                    if (currentName != null) {
                        Sequence sequence = new Sequence();
                        sequence.setProteins(currentName);
                        sequence.setSequence(sequenceBuilder.toString());
                        sequences.add(sequence);
                        sequenceBuilder = new StringBuilder();
                    }
                    currentName = line.substring(1).trim();
                } else if (!line.isEmpty()) {
                    sequenceBuilder.append(line);
                }
            }

            if (currentName != null && sequenceBuilder.length() > 0) {
                Sequence sequence = new Sequence();
                sequence.setProteins(currentName);
                sequence.setSequence(sequenceBuilder.toString());
                sequences.add(sequence);
            }
        }
        return sequences;
    }

    private static List<Sequence> parseTxt(InputStream inputStream) throws IOException {
        List<Sequence> sequences = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String header = reader.readLine(); // 读取表头
            String[] columns = header.split("\t");
            int columnCount = columns.length;

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 2) {
                    Sequence sequence = new Sequence();
                    if (columnCount == 8) { // TSV 类型1 (8列)
                        // 假设列顺序为：ID、index、proteins、accessions、sequences、annotations、interpros、orgs
                        sequence.setId(Long.parseLong(parts[0]));
                        sequence.setIndexNumber(Integer.parseInt(parts[1]));
                        sequence.setProteins(parts[2]);
                        sequence.setAccessions(parts[3]);
                        sequence.setSequence(parts[4]);
                        sequence.setAnnotations(parts[5]);
                        sequence.setInterpros(parts[6]);
                        sequence.setOrgs(Integer.parseInt(parts[7]));
                    } else if (columnCount == 3) { // TSV 类型2 (3列)
                        // 假设列顺序为：proteins、sequences、annotations
                        sequence.setProteins(parts[0]);
                        sequence.setSequence(parts[1]);
                        sequence.setAnnotations(parts[2]);
                    }
                    sequences.add(sequence);
                }
            }
        }
        return sequences;
    }
}