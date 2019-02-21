package org.wickedsource.coderadar.commit.rest;

import java.util.Date;

public class CommitResource {

  private String name;

  private String author;

  private Date timestamp;

  private boolean analyzed;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isAnalyzed() {
    return analyzed;
  }

  public void setAnalyzed(boolean analyzed) {
    this.analyzed = analyzed;
  }
}
