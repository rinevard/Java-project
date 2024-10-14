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

// RESTful API控制器，处理与蛋白质序列相关的请求
@RestController
@RequestMapping("/api/sequences")
public class SequenceController {

    @Autowired
    private SequenceRepository sequenceRepository;

    // 获取所有序列
    @GetMapping
    public List<Sequence> getAllSequences() {
        return sequenceRepository.findAll();
    }

    // 处理多个文件上传，解析并保存序列数据
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

    // 根据查询条件搜索序列
    @GetMapping("/search")
    public List<Sequence> searchSequences(@RequestParam String query) {
        // 在蛋白质名称或序列中搜索匹配项
        return sequenceRepository.findByProteinsContainingOrSequenceContaining(query, query);
    }

    // 导出选定序列为TSV格式
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportSequences(@RequestBody List<Long> ids) {
        List<Sequence> sequences = sequenceRepository.findByIdIn(ids);
        StringBuilder sb = new StringBuilder();

        if (sequences.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        // 生成TSV格式的表头和数据
        sb.append("ID\tIndex\tProteins\tAccessions\tSequences\tAnnotations\tInterpros\tOrgs\n");
        for (Sequence sequence : sequences) {
            // 拼接每个序列的数据，使用制表符分隔
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

        // 设置响应头，指定文件类型和下载文件名
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "sequences_current.tsv");
        headers.setContentLength(data.length);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    // 导出选定序列为TXT格式
    @PostMapping("/export-txt")
    public ResponseEntity<byte[]> exportSequencesTxt(@RequestBody List<Long> ids) {
        List<Sequence> sequences = sequenceRepository.findByIdIn(ids);
        StringBuilder sb = new StringBuilder();

        if (sequences.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        // 生成TXT格式的数据，每个字段单独一行
        for (Sequence sequence : sequences) {
            sb.append("ID: ").append(sequence.getId()).append("\n")
              .append("Index: ").append(sequence.getIndexNumber() != null ? sequence.getIndexNumber() : "N/A").append("\n")
              .append("Proteins: ").append(sequence.getProteins() != null ? sequence.getProteins() : "N/A").append("\n")
              .append("Accessions: ").append(sequence.getAccessions() != null ? sequence.getAccessions() : "N/A").append("\n")
              .append("Sequences: ").append(sequence.getSequence() != null ? sequence.getSequence() : "N/A").append("\n")
              .append("Annotations: ").append(sequence.getAnnotations() != null ? sequence.getAnnotations() : "N/A").append("\n")
              .append("Interpros: ").append(sequence.getInterpros() != null ? sequence.getInterpros() : "N/A").append("\n")
              .append("Orgs: ").append(sequence.getOrgs() != null ? sequence.getOrgs() : "N/A").append("\n")
              .append("----\n"); // 使用分隔符区分不同序列
        }
        byte[] data = sb.toString().getBytes();

        // 设置响应头，指定文件类型和下载文件名
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "sequences_current.txt");
        headers.setContentLength(data.length);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    // 导出所有序列（可选过滤）为TSV格式
    @PostMapping("/export-all")
    public ResponseEntity<byte[]> exportAllSequences(@RequestBody FilterRequest filterRequest) {
        List<Sequence> sequences;
        // 根据过滤条件查询序列
        if (filterRequest.getQuery() != null && !filterRequest.getQuery().isEmpty()) {
            sequences = sequenceRepository.findByProteinsContainingOrSequenceContaining(filterRequest.getQuery(), filterRequest.getQuery());
        } else {
            sequences = sequenceRepository.findAll();
        }

        StringBuilder sb = new StringBuilder();

        if (sequences.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        // 生成TSV格式的表头和数据
        sb.append("ID\tIndex\tProteins\tAccessions\tSequences\tAnnotations\tInterpros\tOrgs\n");
        for (Sequence sequence : sequences) {
            // 拼接每个序列的数据，使用制表符分隔
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

        // 设置响应头，指定文件类型和下载文件名
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "sequences_all.tsv");
        headers.setContentLength(data.length);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    // 导出所有序列（可选过滤）为TXT格式
    @PostMapping("/export-all-txt")
    public ResponseEntity<byte[]> exportAllSequencesTxt(@RequestBody FilterRequest filterRequest) {
        List<Sequence> sequences;
        // 根据过滤条件查询序列
        if (filterRequest.getQuery() != null && !filterRequest.getQuery().isEmpty()) {
            sequences = sequenceRepository.findByProteinsContainingOrSequenceContaining(filterRequest.getQuery(), filterRequest.getQuery());
        } else {
            sequences = sequenceRepository.findAll();
        }

        StringBuilder sb = new StringBuilder();

        if (sequences.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        // 生成TXT格式的数据，每个字段单独一行
        for (Sequence sequence : sequences) {
            sb.append("ID: ").append(sequence.getId()).append("\n")
              .append("Index: ").append(sequence.getIndexNumber() != null ? sequence.getIndexNumber() : "N/A").append("\n")
              .append("Proteins: ").append(sequence.getProteins() != null ? sequence.getProteins() : "N/A").append("\n")
              .append("Accessions: ").append(sequence.getAccessions() != null ? sequence.getAccessions() : "N/A").append("\n")
              .append("Sequences: ").append(sequence.getSequence() != null ? sequence.getSequence() : "N/A").append("\n")
              .append("Annotations: ").append(sequence.getAnnotations() != null ? sequence.getAnnotations() : "N/A").append("\n")
              .append("Interpros: ").append(sequence.getInterpros() != null ? sequence.getInterpros() : "N/A").append("\n")
              .append("Orgs: ").append(sequence.getOrgs() != null ? sequence.getOrgs() : "N/A").append("\n")
              .append("----\n"); // 使用分隔符区分不同序列
        }
        byte[] data = sb.toString().getBytes();

        // 设置响应头，指定文件类型和下载文件名
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "sequences_all.txt");
        headers.setContentLength(data.length);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    // 接收过滤请求的内部类
    public static class FilterRequest {
        private String query;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }
}