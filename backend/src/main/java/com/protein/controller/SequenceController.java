package com.protein.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.protein.data.Sequence;
import com.protein.data.SequenceRepository;
import com.protein.parser.SequenceParser;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/sequences")
public class SequenceController {

    @Autowired
    private SequenceRepository sequenceRepository;

    @GetMapping
    public List<Sequence> getAllSequences() {
        return sequenceRepository.findAll();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        for (MultipartFile file : files) {
            try {
                List<Sequence> sequences = SequenceParser.parse(file.getInputStream(), file.getOriginalFilename());
                sequenceRepository.saveAll(sequences);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing file: " + file.getOriginalFilename());
            }
        }
        return ResponseEntity.ok("Files uploaded successfully");
    }

    @GetMapping("/search")
    public List<Sequence> searchSequences(@RequestParam String query) {
        return sequenceRepository.findByProteinsContainingOrSequenceContaining(query, query);
    }

    // 现有的导出端点（TSV格式）
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportSequences(@RequestBody List<Long> ids) {
        List<Sequence> sequences = sequenceRepository.findByIdIn(ids);
        StringBuilder sb = new StringBuilder();

        if (sequences.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        // 生成表头
        sb.append("ID\tIndex\tProteins\tAccessions\tSequences\tAnnotations\tInterpros\tOrgs\n");
        for (Sequence sequence : sequences) {
            sb.append(sequence.getId()).append("\t")
              .append(sequence.getIndexNumber() != null ? sequence.getIndexNumber() : "").append("\t")
              .append(sequence.getProteins() != null ? sequence.getProteins() : "").append("\t")
              .append(sequence.getAccessions() != null ? sequence.getAccessions() : "").append("\t")
              .append(sequence.getSequence() != null ? sequence.getSequence() : "").append("\t")
              .append(sequence.getAnnotations() != null ? sequence.getAnnotations() : "").append("\t")
              .append(sequence.getInterpros() != null ? sequence.getInterpros() : "").append("\t")
              .append(sequence.getOrgs() != null ? sequence.getOrgs() : "").append("\n");
        }
        byte[] data = sb.toString().getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "sequences.tsv");
        headers.setContentLength(data.length);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    // 新增的导出端点（TXT格式）
    @PostMapping("/export-txt")
    public ResponseEntity<byte[]> exportSequencesTxt(@RequestBody List<Long> ids) {
        List<Sequence> sequences = sequenceRepository.findByIdIn(ids);
        StringBuilder sb = new StringBuilder();

        if (sequences.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        for (Sequence sequence : sequences) {
            sb.append("ID: ").append(sequence.getId()).append("\n")
              .append("Index: ").append(sequence.getIndexNumber() != null ? sequence.getIndexNumber() : "N/A").append("\n")
              .append("Proteins: ").append(sequence.getProteins() != null ? sequence.getProteins() : "N/A").append("\n")
              .append("Accessions: ").append(sequence.getAccessions() != null ? sequence.getAccessions() : "N/A").append("\n")
              .append("Sequences: ").append(sequence.getSequence() != null ? sequence.getSequence() : "N/A").append("\n")
              .append("Annotations: ").append(sequence.getAnnotations() != null ? sequence.getAnnotations() : "N/A").append("\n")
              .append("Interpros: ").append(sequence.getInterpros() != null ? sequence.getInterpros() : "N/A").append("\n")
              .append("Orgs: ").append(sequence.getOrgs() != null ? sequence.getOrgs() : "N/A").append("\n")
              .append("----\n"); // 分隔符
        }
        byte[] data = sb.toString().getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "sequences.txt");
        headers.setContentLength(data.length);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}