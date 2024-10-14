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
        return sequenceRepository.findByNameContainingOrSequenceContaining(query, query);
    }

    @PostMapping("/export")
    public ResponseEntity<String> exportSequences(@RequestBody List<Long> ids) {
        List<Sequence> sequences = sequenceRepository.findByIdIn(ids);
        StringBuilder sb = new StringBuilder();
        for (Sequence sequence : sequences) {
            sb.append("ID: ").append(sequence.getId()).append("\n");
            sb.append("Name: ").append(sequence.getName()).append("\n");
            sb.append("Sequence: ").append(sequence.getSequence()).append("\n\n");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "sequences.txt");
        return new ResponseEntity<>(sb.toString(), headers, HttpStatus.OK);
    }
}