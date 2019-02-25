package org.wickedsource.coderadar.security.domain;

public class ChangePasswordResponseResource {

  // Controller returns Error 500 if the class is just empty.
  // I don't know why, but adding an attribute fixes it. I guess it allows
  // valid JSON to be created from the class.
  private boolean successful = true;

  public ChangePasswordResponseResource() {}

  // Yes it also needs getter and setter for some reason
  public boolean isSuccessful() {
    return successful;
  }

  public void setSuccessful(boolean successful) {
    this.successful = successful;
  }
}
