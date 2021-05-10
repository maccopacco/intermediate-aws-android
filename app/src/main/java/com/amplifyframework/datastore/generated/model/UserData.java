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

/** This is an auto generated class representing the UserData type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "UserData")
public final class UserData implements Model {
  public static final QueryField ID = field("UserData", "id");
  public static final QueryField BANK = field("UserData", "userDataBankId");
  public static final QueryField MAX_IMPORT_BATCH = field("UserData", "max_import_batch");
  public static final QueryField OLDEST_PENDING_TIME = field("UserData", "oldest_pending_time");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Bank", isRequired = true) @BelongsTo(targetName = "userDataBankId", type = Bank.class) Bank bank;
  private final @ModelField(targetType="TransactionWrapper") @HasMany(associatedWith = "userData", type = TransactionWrapper.class) List<TransactionWrapper> transactions = null;
  private final @ModelField(targetType="Int", isRequired = true) Integer max_import_batch;
  private final @ModelField(targetType="String") String oldest_pending_time;
  public String getId() {
      return id;
  }
  
  public Bank getBank() {
      return bank;
  }
  
  public List<TransactionWrapper> getTransactions() {
      return transactions;
  }
  
  public Integer getMaxImportBatch() {
      return max_import_batch;
  }
  
  public String getOldestPendingTime() {
      return oldest_pending_time;
  }
  
  private UserData(String id, Bank bank, Integer max_import_batch, String oldest_pending_time) {
    this.id = id;
    this.bank = bank;
    this.max_import_batch = max_import_batch;
    this.oldest_pending_time = oldest_pending_time;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      UserData userData = (UserData) obj;
      return ObjectsCompat.equals(getId(), userData.getId()) &&
              ObjectsCompat.equals(getBank(), userData.getBank()) &&
              ObjectsCompat.equals(getMaxImportBatch(), userData.getMaxImportBatch()) &&
              ObjectsCompat.equals(getOldestPendingTime(), userData.getOldestPendingTime());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getBank())
      .append(getMaxImportBatch())
      .append(getOldestPendingTime())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("UserData {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("bank=" + String.valueOf(getBank()) + ", ")
      .append("max_import_batch=" + String.valueOf(getMaxImportBatch()) + ", ")
      .append("oldest_pending_time=" + String.valueOf(getOldestPendingTime()))
      .append("}")
      .toString();
  }
  
  public static BankStep builder() {
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
  public static UserData justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new UserData(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      bank,
      max_import_batch,
      oldest_pending_time);
  }
  public interface BankStep {
    MaxImportBatchStep bank(Bank bank);
  }
  

  public interface MaxImportBatchStep {
    BuildStep maxImportBatch(Integer maxImportBatch);
  }
  

  public interface BuildStep {
    UserData build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep oldestPendingTime(String oldestPendingTime);
  }
  

  public static class Builder implements BankStep, MaxImportBatchStep, BuildStep {
    private String id;
    private Bank bank;
    private Integer max_import_batch;
    private String oldest_pending_time;
    @Override
     public UserData build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new UserData(
          id,
          bank,
          max_import_batch,
          oldest_pending_time);
    }
    
    @Override
     public MaxImportBatchStep bank(Bank bank) {
        Objects.requireNonNull(bank);
        this.bank = bank;
        return this;
    }
    
    @Override
     public BuildStep maxImportBatch(Integer maxImportBatch) {
        Objects.requireNonNull(maxImportBatch);
        this.max_import_batch = maxImportBatch;
        return this;
    }
    
    @Override
     public BuildStep oldestPendingTime(String oldestPendingTime) {
        this.oldest_pending_time = oldestPendingTime;
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
    private CopyOfBuilder(String id, Bank bank, Integer maxImportBatch, String oldestPendingTime) {
      super.id(id);
      super.bank(bank)
        .maxImportBatch(maxImportBatch)
        .oldestPendingTime(oldestPendingTime);
    }
    
    @Override
     public CopyOfBuilder bank(Bank bank) {
      return (CopyOfBuilder) super.bank(bank);
    }
    
    @Override
     public CopyOfBuilder maxImportBatch(Integer maxImportBatch) {
      return (CopyOfBuilder) super.maxImportBatch(maxImportBatch);
    }
    
    @Override
     public CopyOfBuilder oldestPendingTime(String oldestPendingTime) {
      return (CopyOfBuilder) super.oldestPendingTime(oldestPendingTime);
    }
  }
  
}
