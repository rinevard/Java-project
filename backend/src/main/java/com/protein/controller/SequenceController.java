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
import java.util.Optional;

// RESTful API 控制器，处理与蛋白质序列相关的请求
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
    public List<Sequence> searchSequences(@RequestParam String query, @RequestParam(required = false) String category) {
        // 标准化类别名称（忽略大小写）
        String normalizedCategory = (category != null) ? category.trim().toLowerCase() : "";

        // 当选择“全部类别”或未指定类别时
        if (normalizedCategory.equals("") || normalizedCategory.equals("全部类别") || normalizedCategory.equals("all")) {
            if (query == null || query.trim().isEmpty()) {
                // 空输入，查找所有至少有一个非空字段的记录
                return sequenceRepository.findAllNonEmpty();
            } else {
                // 非空输入，在所有类别中搜索包含查询的记录
                return sequenceRepository.searchInAllCategories(query.trim());
            }
        }

        // 当选择特定类别时
        switch (normalizedCategory) {
            case "id":
                if (query == null || query.trim().isEmpty()) {
                    // 空输入，查找ID非空的记录
                    return sequenceRepository.findByIdIsNotEmpty();
                } else {
                    // 非空输入，按ID字符串搜索
                    return sequenceRepository.findByIdLike(query.trim());
                }
            case "index":
                if (query == null || query.trim().isEmpty()) {
                    // 空输入，查找IndexNumber非空的记录
                    return sequenceRepository.findByIndexNumberIsNotEmpty();
                } else {
                    // 非空输入，按IndexNumber搜索
                    return sequenceRepository.findByIndexNumberLike(query.trim());
                }
            case "proteins":
                if (query == null || query.trim().isEmpty()) {
                    // 空输入，查找Proteins非空的记录
                    return sequenceRepository.findByProteinsIsNotEmpty();
                } else {
                    // 非空输入，按Proteins搜索
                    return sequenceRepository.findByProteinsLike(query.trim());
                }
            case "accessions":
                if (query == null || query.trim().isEmpty()) {
                    // 空输入，查找Accessions非空的记录
                    return sequenceRepository.findByAccessionsIsNotEmpty();
                } else {
                    // 非空输入，按Accessions搜索
                    return sequenceRepository.findByAccessionsLike(query.trim());
                }
            case "sequences":
                if (query == null || query.trim().isEmpty()) {
                    // 空输入，查找Sequences非空的记录
                    return sequenceRepository.findBySequenceIsNotEmpty();
                } else {
                    // 非空输入，按Sequences搜索
                    return sequenceRepository.findBySequenceLike(query.trim());
                }
            case "annotations":
                if (query == null || query.trim().isEmpty()) {
                    // 空输入，查找Annotations非空的记录
                    return sequenceRepository.findByAnnotationsIsNotEmpty();
                } else {
                    // 非空输入，按Annotations搜索
                    return sequenceRepository.findByAnnotationsLike(query.trim());
                }
            case "interpros":
                if (query == null || query.trim().isEmpty()) {
                    // 空输入，查找Interpros非空的记录
                    return sequenceRepository.findByInterprosIsNotEmpty();
                } else {
                    // 非空输入，按Interpros搜索
                    return sequenceRepository.findByInterprosLike(query.trim());
                }
            case "orgs":
                if (query == null || query.trim().isEmpty()) {
                    // 空输入，查找Orgs非空的记录
                    return sequenceRepository.findByOrgsIsNotEmpty();
                } else {
                    // 非空输入，按Orgs搜索
                    return sequenceRepository.findByOrgsLike(query.trim());
                }
            default:
                // 如果类别不匹配，返回空列表
                return List.of();
        }
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
            // 根据当前搜索类别和查询进行搜索
            sequences = searchWithCategory(filterRequest.getQuery(), filterRequest.getCategory());
        } else {
            // 查询所有非空字段的记录
            sequences = sequenceRepository.findAllNonEmpty();
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
            // 根据当前搜索类别和查询进行搜索
            sequences = searchWithCategory(filterRequest.getQuery(), filterRequest.getCategory());
        } else {
            // 查询所有非空字段的记录
            sequences = sequenceRepository.findAllNonEmpty();
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

    // 辅助方法，根据类别和查询内容进行搜索
    private List<Sequence> searchWithCategory(String query, String category) {
        String normalizedCategory = (category != null) ? category.trim().toLowerCase() : "";

        switch (normalizedCategory) {
            case "id":
                return sequenceRepository.findByIdLike(query.trim());
            case "index":
                return sequenceRepository.findByIndexNumberLike(query.trim());
            case "proteins":
                return sequenceRepository.findByProteinsLike(query.trim());
            case "accessions":
                return sequenceRepository.findByAccessionsLike(query.trim());
            case "sequences":
                return sequenceRepository.findBySequenceLike(query.trim());
            case "annotations":
                return sequenceRepository.findByAnnotationsLike(query.trim());
            case "interpros":
                return sequenceRepository.findByInterprosLike(query.trim());
            case "orgs":
                return sequenceRepository.findByOrgsLike(query.trim());
            default:
                // 如果类别不匹配，返回空列表
                return List.of();
        }
    }

    // 导出选定序列为TSV或TXT格式的共用方法
    @PostMapping("/export-data")
    public ResponseEntity<byte[]> exportData(@RequestBody ExportRequest exportRequest) {
        List<Sequence> sequences;

        if (exportRequest.getType().equals("current")) {
            // 导出当前页数据
            sequences = sequenceRepository.findAllById(exportRequest.getIds());
        } else if (exportRequest.getType().equals("all")) {
            // 导出所有匹配的数据
            if (exportRequest.getQuery() != null && !exportRequest.getQuery().isEmpty()) {
                sequences = searchWithCategory(exportRequest.getQuery(), exportRequest.getCategory());
            } else {
                sequences = sequenceRepository.findAllNonEmpty();
            }
        } else {
            // 未定义的导出类型，返回错误
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if (sequences.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        StringBuilder sb = new StringBuilder();
        MediaType mediaType;
        String filename;

        if (exportRequest.getFormat().equals("txt")) {
            // TXT 格式
            sb.append("Exported Sequences:\n");
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
            mediaType = MediaType.TEXT_PLAIN;
            filename = exportRequest.getType().equals("current") ? "sequences_current.txt" : "sequences_all.txt";
        } else {
            // TSV 格式
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
            mediaType = MediaType.TEXT_PLAIN;
            filename = exportRequest.getType().equals("current") ? "sequences_current.tsv" : "sequences_all.tsv";
        }

        byte[] data = sb.toString().getBytes();

        // 设置响应头，指定文件类型和下载文件名
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(data.length);
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    // 接收过滤请求的内部类
    public static class FilterRequest {
        private String query;
        private String category;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

    // 导出请求的内部类
    public static class ExportRequest {
        private String type; // "current" 或 "all"
        private List<Long> ids;
        private String format; // "tsv" 或 "txt"
        private String query;
        private String category;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Long> getIds() {
            return ids;
        }

        public void setIds(List<Long> ids) {
            this.ids = ids;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}