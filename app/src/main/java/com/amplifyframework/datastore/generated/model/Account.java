package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.annotations.HasMany;

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

/** This is an auto generated class representing the Account type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Accounts")
public final class Account implements Model {
  public static final QueryField ID = field("Account", "id");
  public static final QueryField PLAID_ID = field("Account", "plaidID");
  public static final QueryField NAME = field("Account", "name");
  public static final QueryField OVERRIDE_NAME = field("Account", "overrideName");
  public static final QueryField USER_DATA = field("Account", "accountUserDataId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String plaidID;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String") String overrideName;
  private final @ModelField(targetType="UserData", isRequired = true) @BelongsTo(targetName = "accountUserDataId", type = UserData.class) UserData userData;
  private final @ModelField(targetType="Balance", isRequired = true) @HasMany(associatedWith = "account", type = Balance.class) List<Balance> balances = null;
  public String getId() {
      return id;
  }
  
  public String getPlaidId() {
      return plaidID;
  }
  
  public String getName() {
      return name;
  }
  
  public String getOverrideName() {
      return overrideName;
  }
  
  public UserData getUserData() {
      return userData;
  }
  
  public List<Balance> getBalances() {
      return balances;
  }
  
  private Account(String id, String plaidID, String name, String overrideName, UserData userData) {
    this.id = id;
    this.plaidID = plaidID;
    this.name = name;
    this.overrideName = overrideName;
    this.userData = userData;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Account account = (Account) obj;
      return ObjectsCompat.equals(getId(), account.getId()) &&
              ObjectsCompat.equals(getPlaidId(), account.getPlaidId()) &&
              ObjectsCompat.equals(getName(), account.getName()) &&
              ObjectsCompat.equals(getOverrideName(), account.getOverrideName()) &&
              ObjectsCompat.equals(getUserData(), account.getUserData());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getPlaidId())
      .append(getName())
      .append(getOverrideName())
      .append(getUserData())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Account {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("plaidID=" + String.valueOf(getPlaidId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("overrideName=" + String.valueOf(getOverrideName()) + ", ")
      .append("userData=" + String.valueOf(getUserData()))
      .append("}")
      .toString();
  }
  
  public static PlaidIdStep builder() {
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
  public static Account justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Account(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      plaidID,
      name,
      overrideName,
      userData);
  }
  public interface PlaidIdStep {
    NameStep plaidId(String plaidId);
  }
  

  public interface NameStep {
    UserDataStep name(String name);
  }
  

  public interface UserDataStep {
    BuildStep userData(UserData userData);
  }
  

  public interface BuildStep {
    Account build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep overrideName(String overrideName);
  }
  

  public static class Builder implements PlaidIdStep, NameStep, UserDataStep, BuildStep {
    private String id;
    private String plaidID;
    private String name;
    private UserData userData;
    private String overrideName;
    @Override
     public Account build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Account(
          id,
          plaidID,
          name,
          overrideName,
          userData);
    }
    
    @Override
     public NameStep plaidId(String plaidId) {
        Objects.requireNonNull(plaidId);
        this.plaidID = plaidId;
        return this;
    }
    
    @Override
     public UserDataStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep userData(UserData userData) {
        Objects.requireNonNull(userData);
        this.userData = userData;
        return this;
    }
    
    @Override
     public BuildStep overrideName(String overrideName) {
        this.overrideName = overrideName;
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
    private CopyOfBuilder(String id, String plaidId, String name, String overrideName, UserData userData) {
      super.id(id);
      super.plaidId(plaidId)
        .name(name)
        .userData(userData)
        .overrideName(overrideName);
    }
    
    @Override
     public CopyOfBuilder plaidId(String plaidId) {
      return (CopyOfBuilder) super.plaidId(plaidId);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder userData(UserData userData) {
      return (CopyOfBuilder) super.userData(userData);
    }
    
    @Override
     public CopyOfBuilder overrideName(String overrideName) {
      return (CopyOfBuilder) super.overrideName(overrideName);
    }
  }
  
}
