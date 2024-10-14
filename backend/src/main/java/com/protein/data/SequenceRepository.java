package com.protein.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SequenceRepository extends JpaRepository<Sequence, Long> {
    List<Sequence> findByNameContainingOrSequenceContaining(String name, String sequence);
    List<Sequence> findByIdIn(List<Long> ids);
}