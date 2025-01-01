package com.inception.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompareDbController {

  private final ObjectProvider<JdbcTemplate> primaryJdbc;
  private final ObjectProvider<JdbcTemplate> secondaryJdbc;

  public CompareDbController(
      @Qualifier("primaryJdbcTemplate") ObjectProvider<JdbcTemplate> primaryJdbc,
      @Qualifier("secondaryJdbcTemplate") ObjectProvider<JdbcTemplate> secondaryJdbc
  ) {
    this.primaryJdbc = primaryJdbc;
    this.secondaryJdbc = secondaryJdbc;
  }

  record RunResult(Long count, String sampleMessage, long elapsedMs, String error) {}

  @GetMapping("/compare-db")
  public Map<String, Object> compareDb() {
    JdbcTemplate p = primaryJdbc.getIfAvailable();
    JdbcTemplate s = secondaryJdbc.getIfAvailable();

    if (p == null || s == null) {
      return Map.of(
          "status", "error",
          "message", "compare mode disabled. set SPRING_DATASOURCE_URL_PRIMARY/SECONDARY"
      );
    }

    String query = "SELECT COUNT(*) FROM health_check";
    RunResult pr = run(p);
    RunResult sr = run(s);

    List<String> diff = new ArrayList<>();

    if (pr.error != null || sr.error != null) {
      if (pr.error != null) diff.add("primary error: " + pr.error);
      if (sr.error != null) diff.add("secondary error: " + sr.error);

      return Map.of(
          "status", "error",
          "query", query,
          "primary", pr,
          "secondary", sr,
          "diff", diff
      );
    }

    if (!safeEq(pr.count, sr.count)) diff.add("count mismatch: " + pr.count + " vs " + sr.count);
    if (!safeEq(pr.sampleMessage, sr.sampleMessage)) diff.add("sample mismatch: " + pr.sampleMessage + " vs " + sr.sampleMessage);

    String status = diff.isEmpty() ? "ok" : "mismatch";

    return Map.of(
        "status", status,
        "query", query,
        "primary", pr,
        "secondary", sr,
        "diff", diff
    );
  }

  private RunResult run(JdbcTemplate jdbc) {
    long start = System.nanoTime();
    try {
      Long count = jdbc.queryForObject("SELECT COUNT(*) FROM health_check", Long.class);
      String sample = jdbc.queryForObject("SELECT message FROM health_check ORDER BY id LIMIT 1", String.class);
      long ms = (System.nanoTime() - start) / 1_000_000;
      return new RunResult(count, sample, ms, null);
    } catch (Exception e) {
      long ms = (System.nanoTime() - start) / 1_000_000;
      return new RunResult(null, null, ms, e.getClass().getSimpleName() + ": " + e.getMessage());
    }
  }

  private boolean safeEq(Object a, Object b) {
    return (a == b) || (a != null && a.equals(b));
  }
}
