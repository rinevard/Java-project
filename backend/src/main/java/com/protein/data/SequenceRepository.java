package com.protein.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface SequenceRepository extends JpaRepository<Sequence, Long> {
    // 已有的包含搜索方法
    List<Sequence> findByProteinsContaining(String proteins);
    List<Sequence> findByAccessionsContaining(String accessions);
    List<Sequence> findBySequenceContaining(String sequence);
    List<Sequence> findByAnnotationsContaining(String annotations);
    List<Sequence> findByInterprosContaining(String interpros);
    List<Sequence> findByOrgsContaining(String orgs);
    List<Sequence> findByIndexNumberContaining(String indexNumber); // 假设IndexNumber为字符串类型

    // 复合搜索方法，用于同时搜索多个字段（非空输入）
    @Query("SELECT s FROM Sequence s WHERE " +
           "s.id LIKE CONCAT('%', :query, '%') OR " +
           "s.indexNumber LIKE CONCAT('%', :query, '%') OR " +
           "s.proteins LIKE CONCAT('%', :query, '%') OR " +
           "s.accessions LIKE CONCAT('%', :query, '%') OR " +
           "s.sequence LIKE CONCAT('%', :query, '%') OR " +
           "s.annotations LIKE CONCAT('%', :query, '%') OR " +
           "s.interpros LIKE CONCAT('%', :query, '%') OR " +
           "s.orgs LIKE CONCAT('%', :query, '%')")
    List<Sequence> searchInAllCategories(@Param("query") String query);

    // 方法用于搜索所有非空字段（空输入）
    @Query("SELECT s FROM Sequence s WHERE " +
           "s.id IS NOT NULL OR " +
           "s.indexNumber IS NOT NULL OR " +
           "s.proteins IS NOT NULL OR " +
           "s.accessions IS NOT NULL OR " +
           "s.sequence IS NOT NULL OR " +
           "s.annotations IS NOT NULL OR " +
           "s.interpros IS NOT NULL OR " +
           "s.orgs IS NOT NULL")
    List<Sequence> findAllNonEmpty();

    // 特定类别的搜索方法（非空输入）
    @Query("SELECT s FROM Sequence s WHERE s.id LIKE CONCAT('%', :query, '%')")
    List<Sequence> findByIdLike(@Param("query") String query);

    @Query("SELECT s FROM Sequence s WHERE s.indexNumber LIKE CONCAT('%', :query, '%')")
    List<Sequence> findByIndexNumberLike(@Param("query") String query);

    @Query("SELECT s FROM Sequence s WHERE s.proteins LIKE CONCAT('%', :query, '%')")
    List<Sequence> findByProteinsLike(@Param("query") String query);

    @Query("SELECT s FROM Sequence s WHERE s.accessions LIKE CONCAT('%', :query, '%')")
    List<Sequence> findByAccessionsLike(@Param("query") String query);

    @Query("SELECT s FROM Sequence s WHERE s.sequence LIKE CONCAT('%', :query, '%')")
    List<Sequence> findBySequenceLike(@Param("query") String query);

    @Query("SELECT s FROM Sequence s WHERE s.annotations LIKE CONCAT('%', :query, '%')")
    List<Sequence> findByAnnotationsLike(@Param("query") String query);

    @Query("SELECT s FROM Sequence s WHERE s.interpros LIKE CONCAT('%', :query, '%')")
    List<Sequence> findByInterprosLike(@Param("query") String query);

    @Query("SELECT s FROM Sequence s WHERE s.orgs LIKE CONCAT('%', :query, '%')")
    List<Sequence> findByOrgsLike(@Param("query") String query);

    // 特定类别的非空匹配方法（空输入）
    @Query("SELECT s FROM Sequence s WHERE s.id IS NOT NULL")
    List<Sequence> findByIdIsNotEmpty();

    @Query("SELECT s FROM Sequence s WHERE s.indexNumber IS NOT NULL AND s.indexNumber <> ''")
    List<Sequence> findByIndexNumberIsNotEmpty();

    @Query("SELECT s FROM Sequence s WHERE s.proteins IS NOT NULL AND s.proteins <> ''")
    List<Sequence> findByProteinsIsNotEmpty();

    @Query("SELECT s FROM Sequence s WHERE s.accessions IS NOT NULL AND s.accessions <> ''")
    List<Sequence> findByAccessionsIsNotEmpty();

    @Query("SELECT s FROM Sequence s WHERE s.sequence IS NOT NULL AND s.sequence <> ''")
    List<Sequence> findBySequenceIsNotEmpty();

    @Query("SELECT s FROM Sequence s WHERE s.annotations IS NOT NULL AND s.annotations <> ''")
    List<Sequence> findByAnnotationsIsNotEmpty();

    @Query("SELECT s FROM Sequence s WHERE s.interpros IS NOT NULL AND s.interpros <> ''")
    List<Sequence> findByInterprosIsNotEmpty();

    @Query("SELECT s FROM Sequence s WHERE s.orgs IS NOT NULL AND s.orgs <> ''")
    List<Sequence> findByOrgsIsNotEmpty();

    // 批量导出方法
    List<Sequence> findByIdIn(List<Long> ids);
}