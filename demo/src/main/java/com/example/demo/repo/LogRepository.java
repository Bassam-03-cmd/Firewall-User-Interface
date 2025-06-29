package com.example.demo.repo;

import com.example.demo.models.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepository extends JpaRepository<Logs, Long> {

    /** List all logs, most recent first */
    List<Logs> findAll();
    
    // /** Simple free-text filter over IP/port/protocol */
    // @Query("""
    //   SELECT l FROM LogEntry l
    //    WHERE LOWER(l.protocol) LIKE LOWER(CONCAT('%',:q,'%'))
    //       OR LOWER(l.srcIp)    LIKE LOWER(CONCAT('%',:q,'%'))
    //       OR LOWER(l.dstIp)    LIKE LOWER(CONCAT('%',:q,'%'))
    //       OR LOWER(l.srcPort)  LIKE LOWER(CONCAT('%',:q,'%'))
    //       OR LOWER(l.dstPort)  LIKE LOWER(CONCAT('%',:q,'%'))
    //   """)
    // List<Logs> search(@Param("q") String query);
}
