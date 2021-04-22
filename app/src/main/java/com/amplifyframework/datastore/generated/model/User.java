package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasOne;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the User type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Users")
public final class User implements Model {
  public static final QueryField ID = field("User", "id");
  public static final QueryField GOOGLE_ID = field("User", "googleID");
  public static final QueryField ORIGINAL_EMAIL = field("User", "originalEmail");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String googleID;
  private final @ModelField(targetType="String", isRequired = true) String originalEmail;
  private final @ModelField(targetType="UserData") @HasOne(associatedWith = "user", type = UserData.class) UserData userData = null;
  public String getId() {
      return id;
  }
  
  public String getGoogleId() {
      return googleID;
  }
  
  public String getOriginalEmail() {
      return originalEmail;
  }
  
  public UserData getUserData() {
      return userData;
  }
  
  private User(String id, String googleID, String originalEmail) {
    this.id = id;
    this.googleID = googleID;
    this.originalEmail = originalEmail;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      User user = (User) obj;
      return ObjectsCompat.equals(getId(), user.getId()) &&
              ObjectsCompat.equals(getGoogleId(), user.getGoogleId()) &&
              ObjectsCompat.equals(getOriginalEmail(), user.getOriginalEmail());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getGoogleId())
      .append(getOriginalEmail())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("User {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("googleID=" + String.valueOf(getGoogleId()) + ", ")
      .append("originalEmail=" + String.valueOf(getOriginalEmail()))
      .append("}")
      .toString();
  }
  
  public static GoogleIdStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static User justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new User(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      googleID,
      originalEmail);
  }
  public interface GoogleIdStep {
    OriginalEmailStep googleId(String googleId);
  }
  

  public interface OriginalEmailStep {
    BuildStep originalEmail(String originalEmail);
  }
  

  public interface BuildStep {
    User build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements GoogleIdStep, OriginalEmailStep, BuildStep {
    private String id;
    private String googleID;
    private String originalEmail;
    @Override
     public User build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new User(
          id,
          googleID,
          originalEmail);
    }
    
    @Override
     public OriginalEmailStep googleId(String googleId) {
        Objects.requireNonNull(googleId);
        this.googleID = googleId;
        return this;
    }
    
    @Override
     public BuildStep originalEmail(String originalEmail) {
        Objects.requireNonNull(originalEmail);
        this.originalEmail = originalEmail;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String googleId, String originalEmail) {
      super.id(id);
      super.googleId(googleId)
        .originalEmail(originalEmail);
    }
    
    @Override
     public CopyOfBuilder googleId(String googleId) {
      return (CopyOfBuilder) super.googleId(googleId);
    }
    
    @Override
     public CopyOfBuilder originalEmail(String originalEmail) {
      return (CopyOfBuilder) super.originalEmail(originalEmail);
    }
  }
  
}
