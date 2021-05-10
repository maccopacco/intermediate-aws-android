package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
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

/** This is an auto generated class representing the Bank type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Banks")
public final class Bank implements Model {
  public static final QueryField ID = field("Bank", "id");
  public static final QueryField USER = field("Bank", "bankUserId");
  public static final QueryField PLAID_ACCESS_TOKEN = field("Bank", "plaid_access_token");
  public static final QueryField INSTITUTION_ID = field("Bank", "institution_id");
  public static final QueryField INSTITUTION_NAME = field("Bank", "institution_name");
  public static final QueryField INSTITUTION_LOGO = field("Bank", "institution_logo");
  public static final QueryField LAST_TOUCHED_TIME = field("Bank", "last_touched_time");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User", isRequired = true) @BelongsTo(targetName = "bankUserId", type = User.class) User user;
  private final @ModelField(targetType="String", isRequired = true) String plaid_access_token;
  private final @ModelField(targetType="String", isRequired = true) String institution_id;
  private final @ModelField(targetType="String") String institution_name;
  private final @ModelField(targetType="String") String institution_logo;
  private final @ModelField(targetType="String", isRequired = true) String last_touched_time;
  private final @ModelField(targetType="UserData") @HasOne(associatedWith = "bank", type = UserData.class) UserData userData = null;
  public String getId() {
      return id;
  }
  
  public User getUser() {
      return user;
  }
  
  public String getPlaidAccessToken() {
      return plaid_access_token;
  }
  
  public String getInstitutionId() {
      return institution_id;
  }
  
  public String getInstitutionName() {
      return institution_name;
  }
  
  public String getInstitutionLogo() {
      return institution_logo;
  }
  
  public String getLastTouchedTime() {
      return last_touched_time;
  }
  
  public UserData getUserData() {
      return userData;
  }
  
  private Bank(String id, User user, String plaid_access_token, String institution_id, String institution_name, String institution_logo, String last_touched_time) {
    this.id = id;
    this.user = user;
    this.plaid_access_token = plaid_access_token;
    this.institution_id = institution_id;
    this.institution_name = institution_name;
    this.institution_logo = institution_logo;
    this.last_touched_time = last_touched_time;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Bank bank = (Bank) obj;
      return ObjectsCompat.equals(getId(), bank.getId()) &&
              ObjectsCompat.equals(getUser(), bank.getUser()) &&
              ObjectsCompat.equals(getPlaidAccessToken(), bank.getPlaidAccessToken()) &&
              ObjectsCompat.equals(getInstitutionId(), bank.getInstitutionId()) &&
              ObjectsCompat.equals(getInstitutionName(), bank.getInstitutionName()) &&
              ObjectsCompat.equals(getInstitutionLogo(), bank.getInstitutionLogo()) &&
              ObjectsCompat.equals(getLastTouchedTime(), bank.getLastTouchedTime());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUser())
      .append(getPlaidAccessToken())
      .append(getInstitutionId())
      .append(getInstitutionName())
      .append(getInstitutionLogo())
      .append(getLastTouchedTime())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Bank {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("user=" + String.valueOf(getUser()) + ", ")
      .append("plaid_access_token=" + String.valueOf(getPlaidAccessToken()) + ", ")
      .append("institution_id=" + String.valueOf(getInstitutionId()) + ", ")
      .append("institution_name=" + String.valueOf(getInstitutionName()) + ", ")
      .append("institution_logo=" + String.valueOf(getInstitutionLogo()) + ", ")
      .append("last_touched_time=" + String.valueOf(getLastTouchedTime()))
      .append("}")
      .toString();
  }
  
  public static UserStep builder() {
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
  public static Bank justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Bank(
      id,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      user,
      plaid_access_token,
      institution_id,
      institution_name,
      institution_logo,
      last_touched_time);
  }
  public interface UserStep {
    PlaidAccessTokenStep user(User user);
  }
  

  public interface PlaidAccessTokenStep {
    InstitutionIdStep plaidAccessToken(String plaidAccessToken);
  }
  

  public interface InstitutionIdStep {
    LastTouchedTimeStep institutionId(String institutionId);
  }
  

  public interface LastTouchedTimeStep {
    BuildStep lastTouchedTime(String lastTouchedTime);
  }
  

  public interface BuildStep {
    Bank build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep institutionName(String institutionName);
    BuildStep institutionLogo(String institutionLogo);
  }
  

  public static class Builder implements UserStep, PlaidAccessTokenStep, InstitutionIdStep, LastTouchedTimeStep, BuildStep {
    private String id;
    private User user;
    private String plaid_access_token;
    private String institution_id;
    private String last_touched_time;
    private String institution_name;
    private String institution_logo;
    @Override
     public Bank build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Bank(
          id,
          user,
          plaid_access_token,
          institution_id,
          institution_name,
          institution_logo,
          last_touched_time);
    }
    
    @Override
     public PlaidAccessTokenStep user(User user) {
        Objects.requireNonNull(user);
        this.user = user;
        return this;
    }
    
    @Override
     public InstitutionIdStep plaidAccessToken(String plaidAccessToken) {
        Objects.requireNonNull(plaidAccessToken);
        this.plaid_access_token = plaidAccessToken;
        return this;
    }
    
    @Override
     public LastTouchedTimeStep institutionId(String institutionId) {
        Objects.requireNonNull(institutionId);
        this.institution_id = institutionId;
        return this;
    }
    
    @Override
     public BuildStep lastTouchedTime(String lastTouchedTime) {
        Objects.requireNonNull(lastTouchedTime);
        this.last_touched_time = lastTouchedTime;
        return this;
    }
    
    @Override
     public BuildStep institutionName(String institutionName) {
        this.institution_name = institutionName;
        return this;
    }
    
    @Override
     public BuildStep institutionLogo(String institutionLogo) {
        this.institution_logo = institutionLogo;
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
    private CopyOfBuilder(String id, User user, String plaidAccessToken, String institutionId, String institutionName, String institutionLogo, String lastTouchedTime) {
      super.id(id);
      super.user(user)
        .plaidAccessToken(plaidAccessToken)
        .institutionId(institutionId)
        .lastTouchedTime(lastTouchedTime)
        .institutionName(institutionName)
        .institutionLogo(institutionLogo);
    }
    
    @Override
     public CopyOfBuilder user(User user) {
      return (CopyOfBuilder) super.user(user);
    }
    
    @Override
     public CopyOfBuilder plaidAccessToken(String plaidAccessToken) {
      return (CopyOfBuilder) super.plaidAccessToken(plaidAccessToken);
    }
    
    @Override
     public CopyOfBuilder institutionId(String institutionId) {
      return (CopyOfBuilder) super.institutionId(institutionId);
    }
    
    @Override
     public CopyOfBuilder lastTouchedTime(String lastTouchedTime) {
      return (CopyOfBuilder) super.lastTouchedTime(lastTouchedTime);
    }
    
    @Override
     public CopyOfBuilder institutionName(String institutionName) {
      return (CopyOfBuilder) super.institutionName(institutionName);
    }
    
    @Override
     public CopyOfBuilder institutionLogo(String institutionLogo) {
      return (CopyOfBuilder) super.institutionLogo(institutionLogo);
    }
  }
  
}
