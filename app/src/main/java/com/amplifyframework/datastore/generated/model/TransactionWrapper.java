package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;

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

/** This is an auto generated class representing the TransactionWrapper type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "TransactionWrappers")
public final class TransactionWrapper implements Model {
  public static final QueryField ID = field("TransactionWrapper", "id");
  public static final QueryField IMPORT_BATCH = field("TransactionWrapper", "importBatch");
  public static final QueryField IMPORT_DATE = field("TransactionWrapper", "importDate");
  public static final QueryField IMPORT_SOURCE = field("TransactionWrapper", "importSource");
  public static final QueryField PLAID_ID = field("TransactionWrapper", "plaidID");
  public static final QueryField USER_DATA = field("TransactionWrapper", "transactionWrapperUserDataId");
  public static final QueryField TRANSACTION = field("TransactionWrapper", "transactionWrapperTransactionId");
  public static final QueryField OVERRIDE_NAME = field("TransactionWrapper", "overrideName");
  public static final QueryField MEMO = field("TransactionWrapper", "memo");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Int") Integer importBatch;
  private final @ModelField(targetType="String") String importDate;
  private final @ModelField(targetType="String") String importSource;
  private final @ModelField(targetType="String") String plaidID;
  private final @ModelField(targetType="UserData", isRequired = true) @BelongsTo(targetName = "transactionWrapperUserDataId", type = UserData.class) UserData userData;
  private final @ModelField(targetType="Transaction", isRequired = true) @BelongsTo(targetName = "transactionWrapperTransactionId", type = Transaction.class) Transaction transaction;
  private final @ModelField(targetType="String") String overrideName;
  private final @ModelField(targetType="String") String memo;
  public String getId() {
      return id;
  }
  
  public Integer getImportBatch() {
      return importBatch;
  }
  
  public String getImportDate() {
      return importDate;
  }
  
  public String getImportSource() {
      return importSource;
  }
  
  public String getPlaidId() {
      return plaidID;
  }
  
  public UserData getUserData() {
      return userData;
  }
  
  public Transaction getTransaction() {
      return transaction;
  }
  
  public String getOverrideName() {
      return overrideName;
  }
  
  public String getMemo() {
      return memo;
  }
  
  private TransactionWrapper(String id, Integer importBatch, String importDate, String importSource, String plaidID, UserData userData, Transaction transaction, String overrideName, String memo) {
    this.id = id;
    this.importBatch = importBatch;
    this.importDate = importDate;
    this.importSource = importSource;
    this.plaidID = plaidID;
    this.userData = userData;
    this.transaction = transaction;
    this.overrideName = overrideName;
    this.memo = memo;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      TransactionWrapper transactionWrapper = (TransactionWrapper) obj;
      return ObjectsCompat.equals(getId(), transactionWrapper.getId()) &&
              ObjectsCompat.equals(getImportBatch(), transactionWrapper.getImportBatch()) &&
              ObjectsCompat.equals(getImportDate(), transactionWrapper.getImportDate()) &&
              ObjectsCompat.equals(getImportSource(), transactionWrapper.getImportSource()) &&
              ObjectsCompat.equals(getPlaidId(), transactionWrapper.getPlaidId()) &&
              ObjectsCompat.equals(getUserData(), transactionWrapper.getUserData()) &&
              ObjectsCompat.equals(getTransaction(), transactionWrapper.getTransaction()) &&
              ObjectsCompat.equals(getOverrideName(), transactionWrapper.getOverrideName()) &&
              ObjectsCompat.equals(getMemo(), transactionWrapper.getMemo());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getImportBatch())
      .append(getImportDate())
      .append(getImportSource())
      .append(getPlaidId())
      .append(getUserData())
      .append(getTransaction())
      .append(getOverrideName())
      .append(getMemo())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("TransactionWrapper {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("importBatch=" + String.valueOf(getImportBatch()) + ", ")
      .append("importDate=" + String.valueOf(getImportDate()) + ", ")
      .append("importSource=" + String.valueOf(getImportSource()) + ", ")
      .append("plaidID=" + String.valueOf(getPlaidId()) + ", ")
      .append("userData=" + String.valueOf(getUserData()) + ", ")
      .append("transaction=" + String.valueOf(getTransaction()) + ", ")
      .append("overrideName=" + String.valueOf(getOverrideName()) + ", ")
      .append("memo=" + String.valueOf(getMemo()))
      .append("}")
      .toString();
  }
  
  public static UserDataStep builder() {
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
  public static TransactionWrapper justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new TransactionWrapper(
      id,
      null,
      null,
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
      importBatch,
      importDate,
      importSource,
      plaidID,
      userData,
      transaction,
      overrideName,
      memo);
  }
  public interface UserDataStep {
    TransactionStep userData(UserData userData);
  }
  

  public interface TransactionStep {
    BuildStep transaction(Transaction transaction);
  }
  

  public interface BuildStep {
    TransactionWrapper build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep importBatch(Integer importBatch);
    BuildStep importDate(String importDate);
    BuildStep importSource(String importSource);
    BuildStep plaidId(String plaidId);
    BuildStep overrideName(String overrideName);
    BuildStep memo(String memo);
  }
  

  public static class Builder implements UserDataStep, TransactionStep, BuildStep {
    private String id;
    private UserData userData;
    private Transaction transaction;
    private Integer importBatch;
    private String importDate;
    private String importSource;
    private String plaidID;
    private String overrideName;
    private String memo;
    @Override
     public TransactionWrapper build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new TransactionWrapper(
          id,
          importBatch,
          importDate,
          importSource,
          plaidID,
          userData,
          transaction,
          overrideName,
          memo);
    }
    
    @Override
     public TransactionStep userData(UserData userData) {
        Objects.requireNonNull(userData);
        this.userData = userData;
        return this;
    }
    
    @Override
     public BuildStep transaction(Transaction transaction) {
        Objects.requireNonNull(transaction);
        this.transaction = transaction;
        return this;
    }
    
    @Override
     public BuildStep importBatch(Integer importBatch) {
        this.importBatch = importBatch;
        return this;
    }
    
    @Override
     public BuildStep importDate(String importDate) {
        this.importDate = importDate;
        return this;
    }
    
    @Override
     public BuildStep importSource(String importSource) {
        this.importSource = importSource;
        return this;
    }
    
    @Override
     public BuildStep plaidId(String plaidId) {
        this.plaidID = plaidId;
        return this;
    }
    
    @Override
     public BuildStep overrideName(String overrideName) {
        this.overrideName = overrideName;
        return this;
    }
    
    @Override
     public BuildStep memo(String memo) {
        this.memo = memo;
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
    private CopyOfBuilder(String id, Integer importBatch, String importDate, String importSource, String plaidId, UserData userData, Transaction transaction, String overrideName, String memo) {
      super.id(id);
      super.userData(userData)
        .transaction(transaction)
        .importBatch(importBatch)
        .importDate(importDate)
        .importSource(importSource)
        .plaidId(plaidId)
        .overrideName(overrideName)
        .memo(memo);
    }
    
    @Override
     public CopyOfBuilder userData(UserData userData) {
      return (CopyOfBuilder) super.userData(userData);
    }
    
    @Override
     public CopyOfBuilder transaction(Transaction transaction) {
      return (CopyOfBuilder) super.transaction(transaction);
    }
    
    @Override
     public CopyOfBuilder importBatch(Integer importBatch) {
      return (CopyOfBuilder) super.importBatch(importBatch);
    }
    
    @Override
     public CopyOfBuilder importDate(String importDate) {
      return (CopyOfBuilder) super.importDate(importDate);
    }
    
    @Override
     public CopyOfBuilder importSource(String importSource) {
      return (CopyOfBuilder) super.importSource(importSource);
    }
    
    @Override
     public CopyOfBuilder plaidId(String plaidId) {
      return (CopyOfBuilder) super.plaidId(plaidId);
    }
    
    @Override
     public CopyOfBuilder overrideName(String overrideName) {
      return (CopyOfBuilder) super.overrideName(overrideName);
    }
    
    @Override
     public CopyOfBuilder memo(String memo) {
      return (CopyOfBuilder) super.memo(memo);
    }
  }
  
}
