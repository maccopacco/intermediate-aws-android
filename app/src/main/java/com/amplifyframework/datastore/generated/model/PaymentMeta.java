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

/** This is an auto generated class representing the PaymentMeta type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "PaymentMetas")
public final class PaymentMeta implements Model {
  public static final QueryField ID = field("PaymentMeta", "id");
  public static final QueryField BY_ORDER_OF = field("PaymentMeta", "byOrderOf");
  public static final QueryField PAYEE = field("PaymentMeta", "payee");
  public static final QueryField PAYER = field("PaymentMeta", "payer");
  public static final QueryField PAYMENT_METHOD = field("PaymentMeta", "paymentMethod");
  public static final QueryField PAYMENT_PROCESSOR = field("PaymentMeta", "paymentProcessor");
  public static final QueryField PPD_ID = field("PaymentMeta", "ppdId");
  public static final QueryField REASON = field("PaymentMeta", "reason");
  public static final QueryField REFERENCE_NUMBER = field("PaymentMeta", "referenceNumber");
  public static final QueryField TRANSACTION = field("PaymentMeta", "paymentMetaTransactionId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String byOrderOf;
  private final @ModelField(targetType="String") String payee;
  private final @ModelField(targetType="String") String payer;
  private final @ModelField(targetType="String") String paymentMethod;
  private final @ModelField(targetType="String") String paymentProcessor;
  private final @ModelField(targetType="String") String ppdId;
  private final @ModelField(targetType="String") String reason;
  private final @ModelField(targetType="String") String referenceNumber;
  private final @ModelField(targetType="Transaction", isRequired = true) @BelongsTo(targetName = "paymentMetaTransactionId", type = Transaction.class) Transaction transaction;
  public String getId() {
      return id;
  }
  
  public String getByOrderOf() {
      return byOrderOf;
  }
  
  public String getPayee() {
      return payee;
  }
  
  public String getPayer() {
      return payer;
  }
  
  public String getPaymentMethod() {
      return paymentMethod;
  }
  
  public String getPaymentProcessor() {
      return paymentProcessor;
  }
  
  public String getPpdId() {
      return ppdId;
  }
  
  public String getReason() {
      return reason;
  }
  
  public String getReferenceNumber() {
      return referenceNumber;
  }
  
  public Transaction getTransaction() {
      return transaction;
  }
  
  private PaymentMeta(String id, String byOrderOf, String payee, String payer, String paymentMethod, String paymentProcessor, String ppdId, String reason, String referenceNumber, Transaction transaction) {
    this.id = id;
    this.byOrderOf = byOrderOf;
    this.payee = payee;
    this.payer = payer;
    this.paymentMethod = paymentMethod;
    this.paymentProcessor = paymentProcessor;
    this.ppdId = ppdId;
    this.reason = reason;
    this.referenceNumber = referenceNumber;
    this.transaction = transaction;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      PaymentMeta paymentMeta = (PaymentMeta) obj;
      return ObjectsCompat.equals(getId(), paymentMeta.getId()) &&
              ObjectsCompat.equals(getByOrderOf(), paymentMeta.getByOrderOf()) &&
              ObjectsCompat.equals(getPayee(), paymentMeta.getPayee()) &&
              ObjectsCompat.equals(getPayer(), paymentMeta.getPayer()) &&
              ObjectsCompat.equals(getPaymentMethod(), paymentMeta.getPaymentMethod()) &&
              ObjectsCompat.equals(getPaymentProcessor(), paymentMeta.getPaymentProcessor()) &&
              ObjectsCompat.equals(getPpdId(), paymentMeta.getPpdId()) &&
              ObjectsCompat.equals(getReason(), paymentMeta.getReason()) &&
              ObjectsCompat.equals(getReferenceNumber(), paymentMeta.getReferenceNumber()) &&
              ObjectsCompat.equals(getTransaction(), paymentMeta.getTransaction());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getByOrderOf())
      .append(getPayee())
      .append(getPayer())
      .append(getPaymentMethod())
      .append(getPaymentProcessor())
      .append(getPpdId())
      .append(getReason())
      .append(getReferenceNumber())
      .append(getTransaction())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("PaymentMeta {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("byOrderOf=" + String.valueOf(getByOrderOf()) + ", ")
      .append("payee=" + String.valueOf(getPayee()) + ", ")
      .append("payer=" + String.valueOf(getPayer()) + ", ")
      .append("paymentMethod=" + String.valueOf(getPaymentMethod()) + ", ")
      .append("paymentProcessor=" + String.valueOf(getPaymentProcessor()) + ", ")
      .append("ppdId=" + String.valueOf(getPpdId()) + ", ")
      .append("reason=" + String.valueOf(getReason()) + ", ")
      .append("referenceNumber=" + String.valueOf(getReferenceNumber()) + ", ")
      .append("transaction=" + String.valueOf(getTransaction()))
      .append("}")
      .toString();
  }
  
  public static TransactionStep builder() {
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
  public static PaymentMeta justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new PaymentMeta(
      id,
      null,
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
      byOrderOf,
      payee,
      payer,
      paymentMethod,
      paymentProcessor,
      ppdId,
      reason,
      referenceNumber,
      transaction);
  }
  public interface TransactionStep {
    BuildStep transaction(Transaction transaction);
  }
  

  public interface BuildStep {
    PaymentMeta build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep byOrderOf(String byOrderOf);
    BuildStep payee(String payee);
    BuildStep payer(String payer);
    BuildStep paymentMethod(String paymentMethod);
    BuildStep paymentProcessor(String paymentProcessor);
    BuildStep ppdId(String ppdId);
    BuildStep reason(String reason);
    BuildStep referenceNumber(String referenceNumber);
  }
  

  public static class Builder implements TransactionStep, BuildStep {
    private String id;
    private Transaction transaction;
    private String byOrderOf;
    private String payee;
    private String payer;
    private String paymentMethod;
    private String paymentProcessor;
    private String ppdId;
    private String reason;
    private String referenceNumber;
    @Override
     public PaymentMeta build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new PaymentMeta(
          id,
          byOrderOf,
          payee,
          payer,
          paymentMethod,
          paymentProcessor,
          ppdId,
          reason,
          referenceNumber,
          transaction);
    }
    
    @Override
     public BuildStep transaction(Transaction transaction) {
        Objects.requireNonNull(transaction);
        this.transaction = transaction;
        return this;
    }
    
    @Override
     public BuildStep byOrderOf(String byOrderOf) {
        this.byOrderOf = byOrderOf;
        return this;
    }
    
    @Override
     public BuildStep payee(String payee) {
        this.payee = payee;
        return this;
    }
    
    @Override
     public BuildStep payer(String payer) {
        this.payer = payer;
        return this;
    }
    
    @Override
     public BuildStep paymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }
    
    @Override
     public BuildStep paymentProcessor(String paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
        return this;
    }
    
    @Override
     public BuildStep ppdId(String ppdId) {
        this.ppdId = ppdId;
        return this;
    }
    
    @Override
     public BuildStep reason(String reason) {
        this.reason = reason;
        return this;
    }
    
    @Override
     public BuildStep referenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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
    private CopyOfBuilder(String id, String byOrderOf, String payee, String payer, String paymentMethod, String paymentProcessor, String ppdId, String reason, String referenceNumber, Transaction transaction) {
      super.id(id);
      super.transaction(transaction)
        .byOrderOf(byOrderOf)
        .payee(payee)
        .payer(payer)
        .paymentMethod(paymentMethod)
        .paymentProcessor(paymentProcessor)
        .ppdId(ppdId)
        .reason(reason)
        .referenceNumber(referenceNumber);
    }
    
    @Override
     public CopyOfBuilder transaction(Transaction transaction) {
      return (CopyOfBuilder) super.transaction(transaction);
    }
    
    @Override
     public CopyOfBuilder byOrderOf(String byOrderOf) {
      return (CopyOfBuilder) super.byOrderOf(byOrderOf);
    }
    
    @Override
     public CopyOfBuilder payee(String payee) {
      return (CopyOfBuilder) super.payee(payee);
    }
    
    @Override
     public CopyOfBuilder payer(String payer) {
      return (CopyOfBuilder) super.payer(payer);
    }
    
    @Override
     public CopyOfBuilder paymentMethod(String paymentMethod) {
      return (CopyOfBuilder) super.paymentMethod(paymentMethod);
    }
    
    @Override
     public CopyOfBuilder paymentProcessor(String paymentProcessor) {
      return (CopyOfBuilder) super.paymentProcessor(paymentProcessor);
    }
    
    @Override
     public CopyOfBuilder ppdId(String ppdId) {
      return (CopyOfBuilder) super.ppdId(ppdId);
    }
    
    @Override
     public CopyOfBuilder reason(String reason) {
      return (CopyOfBuilder) super.reason(reason);
    }
    
    @Override
     public CopyOfBuilder referenceNumber(String referenceNumber) {
      return (CopyOfBuilder) super.referenceNumber(referenceNumber);
    }
  }
  
}
