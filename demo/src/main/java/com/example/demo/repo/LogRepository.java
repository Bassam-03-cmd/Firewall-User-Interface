package com.example.demo.repo;

import com.example.demo.models.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepository extends JpaRepository<Logs, Long> {

    
    List<Logs> findAll();
    
    @Query("""
    SELECT l FROM Logs l
    WHERE LOWER(l.protocol) LIKE LOWER(CONCAT('%',:q,'%'))
        OR LOWER(l.srcIp) LIKE LOWER(CONCAT('%',:q,'%'))
        OR LOWER(l.dstIp) LIKE LOWER(CONCAT('%',:q,'%'))
        OR str(l.srcPort) LIKE CONCAT('%', :q, '%')
        OR str(l.dstPort) LIKE CONCAT('%', :q, '%')
        OR LOWER(l.rule.ruleAction) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<Logs> search(@Param("q") String query);

}
