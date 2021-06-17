package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.temporal.Temporal;

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

/** This is an auto generated class representing the Balance type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Balances")
public final class Balance implements Model {
  public static final QueryField ID = field("Balance", "id");
  public static final QueryField ACCOUNT = field("Balance", "balanceAccountId");
  public static final QueryField AVAILABLE_BALANCE = field("Balance", "availableBalance");
  public static final QueryField CURRENT_BALANCE = field("Balance", "currentBalance");
  public static final QueryField TIME = field("Balance", "time");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Account", isRequired = true) @BelongsTo(targetName = "balanceAccountId", type = Account.class) Account account;
  private final @ModelField(targetType="Float", isRequired = true) Double availableBalance;
  private final @ModelField(targetType="Float", isRequired = true) Double currentBalance;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime time;
  public String getId() {
      return id;
  }
  
  public Account getAccount() {
      return account;
  }
  
  public Double getAvailableBalance() {
      return availableBalance;
  }
  
  public Double getCurrentBalance() {
      return currentBalance;
  }
  
  public Temporal.DateTime getTime() {
      return time;
  }
  
  private Balance(String id, Account account, Double availableBalance, Double currentBalance, Temporal.DateTime time) {
    this.id = id;
    this.account = account;
    this.availableBalance = availableBalance;
    this.currentBalance = currentBalance;
    this.time = time;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Balance balance = (Balance) obj;
      return ObjectsCompat.equals(getId(), balance.getId()) &&
              ObjectsCompat.equals(getAccount(), balance.getAccount()) &&
              ObjectsCompat.equals(getAvailableBalance(), balance.getAvailableBalance()) &&
              ObjectsCompat.equals(getCurrentBalance(), balance.getCurrentBalance()) &&
              ObjectsCompat.equals(getTime(), balance.getTime());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getAccount())
      .append(getAvailableBalance())
      .append(getCurrentBalance())
      .append(getTime())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Balance {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("account=" + String.valueOf(getAccount()) + ", ")
      .append("availableBalance=" + String.valueOf(getAvailableBalance()) + ", ")
      .append("currentBalance=" + String.valueOf(getCurrentBalance()) + ", ")
      .append("time=" + String.valueOf(getTime()))
      .append("}")
      .toString();
  }
  
  public static AccountStep builder() {
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
  public static Balance justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Balance(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      account,
      availableBalance,
      currentBalance,
      time);
  }
  public interface AccountStep {
    AvailableBalanceStep account(Account account);
  }
  

  public interface AvailableBalanceStep {
    CurrentBalanceStep availableBalance(Double availableBalance);
  }
  

  public interface CurrentBalanceStep {
    TimeStep currentBalance(Double currentBalance);
  }
  

  public interface TimeStep {
    BuildStep time(Temporal.DateTime time);
  }
  

  public interface BuildStep {
    Balance build();
    BuildStep id(String id) throws IllegalArgumentException;
  }
  

  public static class Builder implements AccountStep, AvailableBalanceStep, CurrentBalanceStep, TimeStep, BuildStep {
    private String id;
    private Account account;
    private Double availableBalance;
    private Double currentBalance;
    private Temporal.DateTime time;
    @Override
     public Balance build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Balance(
          id,
          account,
          availableBalance,
          currentBalance,
          time);
    }
    
    @Override
     public AvailableBalanceStep account(Account account) {
        Objects.requireNonNull(account);
        this.account = account;
        return this;
    }
    
    @Override
     public CurrentBalanceStep availableBalance(Double availableBalance) {
        Objects.requireNonNull(availableBalance);
        this.availableBalance = availableBalance;
        return this;
    }
    
    @Override
     public TimeStep currentBalance(Double currentBalance) {
        Objects.requireNonNull(currentBalance);
        this.currentBalance = currentBalance;
        return this;
    }
    
    @Override
     public BuildStep time(Temporal.DateTime time) {
        Objects.requireNonNull(time);
        this.time = time;
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
    private CopyOfBuilder(String id, Account account, Double availableBalance, Double currentBalance, Temporal.DateTime time) {
      super.id(id);
      super.account(account)
        .availableBalance(availableBalance)
        .currentBalance(currentBalance)
        .time(time);
    }
    
    @Override
     public CopyOfBuilder account(Account account) {
      return (CopyOfBuilder) super.account(account);
    }
    
    @Override
     public CopyOfBuilder availableBalance(Double availableBalance) {
      return (CopyOfBuilder) super.availableBalance(availableBalance);
    }
    
    @Override
     public CopyOfBuilder currentBalance(Double currentBalance) {
      return (CopyOfBuilder) super.currentBalance(currentBalance);
    }
    
    @Override
     public CopyOfBuilder time(Temporal.DateTime time) {
      return (CopyOfBuilder) super.time(time);
    }
  }
  
}
