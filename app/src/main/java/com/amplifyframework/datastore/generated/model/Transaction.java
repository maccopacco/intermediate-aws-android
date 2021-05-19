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

/** This is an auto generated class representing the Transaction type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Transactions")
public final class Transaction implements Model {
  public static final QueryField ID = field("Transaction", "id");
  public static final QueryField OVERRIDE_NAME = field("Transaction", "overrideName");
  public static final QueryField MEMO = field("Transaction", "memo");
  public static final QueryField IMPORT_BATCH = field("Transaction", "importBatch");
  public static final QueryField IMPORT_DATE = field("Transaction", "importDate");
  public static final QueryField IMPORT_SOURCE = field("Transaction", "importSource");
  public static final QueryField PLAID_ID = field("Transaction", "plaidID");
  public static final QueryField USER_DATA = field("Transaction", "transactionUserDataId");
  public static final QueryField ACCOUNT = field("Transaction", "account");
  public static final QueryField AMOUNT = field("Transaction", "amount");
  public static final QueryField ISO_CURRENCY_CODE = field("Transaction", "isoCurrencyCode");
  public static final QueryField UNOFFICIAL_CURRENCY_CODE = field("Transaction", "unofficialCurrencyCode");
  public static final QueryField CATEGORY_FOLDER = field("Transaction", "categoryFolder");
  public static final QueryField CATEGORY = field("Transaction", "category");
  public static final QueryField CATEGORY_ID = field("Transaction", "categoryId");
  public static final QueryField DATE = field("Transaction", "date");
  public static final QueryField EXACT_TIME = field("Transaction", "exactTime");
  public static final QueryField MERCHANT_NAME = field("Transaction", "merchantName");
  public static final QueryField NAME = field("Transaction", "name");
  public static final QueryField ORIGINAL_DESCRIPTION = field("Transaction", "originalDescription");
  public static final QueryField PENDING = field("Transaction", "pending");
  public static final QueryField PENDING_TRANSACTION_ID = field("Transaction", "pendingTransactionId");
  public static final QueryField TRANSACTION_ID = field("Transaction", "transactionId");
  public static final QueryField TRANSACTION_TYPE = field("Transaction", "transactionType");
  public static final QueryField ACCOUNT_OWNER = field("Transaction", "accountOwner");
  public static final QueryField AUTHORIZED_DATE = field("Transaction", "authorizedDate");
  public static final QueryField TRANSACTION_CODE = field("Transaction", "transactionCode");
  public static final QueryField PAYMENT_CHANNEL = field("Transaction", "paymentChannel");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String overrideName;
  private final @ModelField(targetType="String") String memo;
  private final @ModelField(targetType="Int") Integer importBatch;
  private final @ModelField(targetType="String") String importDate;
  private final @ModelField(targetType="String") String importSource;
  private final @ModelField(targetType="String") String plaidID;
  private final @ModelField(targetType="UserData", isRequired = true) @BelongsTo(targetName = "transactionUserDataId", type = UserData.class) UserData userData;
  private final @ModelField(targetType="String", isRequired = true) String account;
  private final @ModelField(targetType="Float", isRequired = true) Double amount;
  private final @ModelField(targetType="String") String isoCurrencyCode;
  private final @ModelField(targetType="String") String unofficialCurrencyCode;
  private final @ModelField(targetType="String") String categoryFolder;
  private final @ModelField(targetType="String") List<String> category;
  private final @ModelField(targetType="String") String categoryId;
  private final @ModelField(targetType="String") String date;
  private final @ModelField(targetType="String") String exactTime;
  private final @ModelField(targetType="Location") @HasOne(associatedWith = "transaction", type = Location.class) Location location = null;
  private final @ModelField(targetType="String") String merchantName;
  private final @ModelField(targetType="String") String name;
  private final @ModelField(targetType="String") String originalDescription;
  private final @ModelField(targetType="PaymentMeta") @HasOne(associatedWith = "transaction", type = PaymentMeta.class) PaymentMeta paymentMeta = null;
  private final @ModelField(targetType="Boolean") Boolean pending;
  private final @ModelField(targetType="String") String pendingTransactionId;
  private final @ModelField(targetType="String") String transactionId;
  private final @ModelField(targetType="String") String transactionType;
  private final @ModelField(targetType="String") String accountOwner;
  private final @ModelField(targetType="String") String authorizedDate;
  private final @ModelField(targetType="String") String transactionCode;
  private final @ModelField(targetType="String") String paymentChannel;
  public String getId() {
      return id;
  }
  
  public String getOverrideName() {
      return overrideName;
  }
  
  public String getMemo() {
      return memo;
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
  
  public String getAccount() {
      return account;
  }
  
  public Double getAmount() {
      return amount;
  }
  
  public String getIsoCurrencyCode() {
      return isoCurrencyCode;
  }
  
  public String getUnofficialCurrencyCode() {
      return unofficialCurrencyCode;
  }
  
  public String getCategoryFolder() {
      return categoryFolder;
  }
  
  public List<String> getCategory() {
      return category;
  }
  
  public String getCategoryId() {
      return categoryId;
  }
  
  public String getDate() {
      return date;
  }
  
  public String getExactTime() {
      return exactTime;
  }
  
  public Location getLocation() {
      return location;
  }
  
  public String getMerchantName() {
      return merchantName;
  }
  
  public String getName() {
      return name;
  }
  
  public String getOriginalDescription() {
      return originalDescription;
  }
  
  public PaymentMeta getPaymentMeta() {
      return paymentMeta;
  }
  
  public Boolean getPending() {
      return pending;
  }
  
  public String getPendingTransactionId() {
      return pendingTransactionId;
  }
  
  public String getTransactionId() {
      return transactionId;
  }
  
  public String getTransactionType() {
      return transactionType;
  }
  
  public String getAccountOwner() {
      return accountOwner;
  }
  
  public String getAuthorizedDate() {
      return authorizedDate;
  }
  
  public String getTransactionCode() {
      return transactionCode;
  }
  
  public String getPaymentChannel() {
      return paymentChannel;
  }
  
  private Transaction(String id, String overrideName, String memo, Integer importBatch, String importDate, String importSource, String plaidID, UserData userData, String account, Double amount, String isoCurrencyCode, String unofficialCurrencyCode, String categoryFolder, List<String> category, String categoryId, String date, String exactTime, String merchantName, String name, String originalDescription, Boolean pending, String pendingTransactionId, String transactionId, String transactionType, String accountOwner, String authorizedDate, String transactionCode, String paymentChannel) {
    this.id = id;
    this.overrideName = overrideName;
    this.memo = memo;
    this.importBatch = importBatch;
    this.importDate = importDate;
    this.importSource = importSource;
    this.plaidID = plaidID;
    this.userData = userData;
    this.account = account;
    this.amount = amount;
    this.isoCurrencyCode = isoCurrencyCode;
    this.unofficialCurrencyCode = unofficialCurrencyCode;
    this.categoryFolder = categoryFolder;
    this.category = category;
    this.categoryId = categoryId;
    this.date = date;
    this.exactTime = exactTime;
    this.merchantName = merchantName;
    this.name = name;
    this.originalDescription = originalDescription;
    this.pending = pending;
    this.pendingTransactionId = pendingTransactionId;
    this.transactionId = transactionId;
    this.transactionType = transactionType;
    this.accountOwner = accountOwner;
    this.authorizedDate = authorizedDate;
    this.transactionCode = transactionCode;
    this.paymentChannel = paymentChannel;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Transaction transaction = (Transaction) obj;
      return ObjectsCompat.equals(getId(), transaction.getId()) &&
              ObjectsCompat.equals(getOverrideName(), transaction.getOverrideName()) &&
              ObjectsCompat.equals(getMemo(), transaction.getMemo()) &&
              ObjectsCompat.equals(getImportBatch(), transaction.getImportBatch()) &&
              ObjectsCompat.equals(getImportDate(), transaction.getImportDate()) &&
              ObjectsCompat.equals(getImportSource(), transaction.getImportSource()) &&
              ObjectsCompat.equals(getPlaidId(), transaction.getPlaidId()) &&
              ObjectsCompat.equals(getUserData(), transaction.getUserData()) &&
              ObjectsCompat.equals(getAccount(), transaction.getAccount()) &&
              ObjectsCompat.equals(getAmount(), transaction.getAmount()) &&
              ObjectsCompat.equals(getIsoCurrencyCode(), transaction.getIsoCurrencyCode()) &&
              ObjectsCompat.equals(getUnofficialCurrencyCode(), transaction.getUnofficialCurrencyCode()) &&
              ObjectsCompat.equals(getCategoryFolder(), transaction.getCategoryFolder()) &&
              ObjectsCompat.equals(getCategory(), transaction.getCategory()) &&
              ObjectsCompat.equals(getCategoryId(), transaction.getCategoryId()) &&
              ObjectsCompat.equals(getDate(), transaction.getDate()) &&
              ObjectsCompat.equals(getExactTime(), transaction.getExactTime()) &&
              ObjectsCompat.equals(getMerchantName(), transaction.getMerchantName()) &&
              ObjectsCompat.equals(getName(), transaction.getName()) &&
              ObjectsCompat.equals(getOriginalDescription(), transaction.getOriginalDescription()) &&
              ObjectsCompat.equals(getPending(), transaction.getPending()) &&
              ObjectsCompat.equals(getPendingTransactionId(), transaction.getPendingTransactionId()) &&
              ObjectsCompat.equals(getTransactionId(), transaction.getTransactionId()) &&
              ObjectsCompat.equals(getTransactionType(), transaction.getTransactionType()) &&
              ObjectsCompat.equals(getAccountOwner(), transaction.getAccountOwner()) &&
              ObjectsCompat.equals(getAuthorizedDate(), transaction.getAuthorizedDate()) &&
              ObjectsCompat.equals(getTransactionCode(), transaction.getTransactionCode()) &&
              ObjectsCompat.equals(getPaymentChannel(), transaction.getPaymentChannel());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getOverrideName())
      .append(getMemo())
      .append(getImportBatch())
      .append(getImportDate())
      .append(getImportSource())
      .append(getPlaidId())
      .append(getUserData())
      .append(getAccount())
      .append(getAmount())
      .append(getIsoCurrencyCode())
      .append(getUnofficialCurrencyCode())
      .append(getCategoryFolder())
      .append(getCategory())
      .append(getCategoryId())
      .append(getDate())
      .append(getExactTime())
      .append(getMerchantName())
      .append(getName())
      .append(getOriginalDescription())
      .append(getPending())
      .append(getPendingTransactionId())
      .append(getTransactionId())
      .append(getTransactionType())
      .append(getAccountOwner())
      .append(getAuthorizedDate())
      .append(getTransactionCode())
      .append(getPaymentChannel())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Transaction {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("overrideName=" + String.valueOf(getOverrideName()) + ", ")
      .append("memo=" + String.valueOf(getMemo()) + ", ")
      .append("importBatch=" + String.valueOf(getImportBatch()) + ", ")
      .append("importDate=" + String.valueOf(getImportDate()) + ", ")
      .append("importSource=" + String.valueOf(getImportSource()) + ", ")
      .append("plaidID=" + String.valueOf(getPlaidId()) + ", ")
      .append("userData=" + String.valueOf(getUserData()) + ", ")
      .append("account=" + String.valueOf(getAccount()) + ", ")
      .append("amount=" + String.valueOf(getAmount()) + ", ")
      .append("isoCurrencyCode=" + String.valueOf(getIsoCurrencyCode()) + ", ")
      .append("unofficialCurrencyCode=" + String.valueOf(getUnofficialCurrencyCode()) + ", ")
      .append("categoryFolder=" + String.valueOf(getCategoryFolder()) + ", ")
      .append("category=" + String.valueOf(getCategory()) + ", ")
      .append("categoryId=" + String.valueOf(getCategoryId()) + ", ")
      .append("date=" + String.valueOf(getDate()) + ", ")
      .append("exactTime=" + String.valueOf(getExactTime()) + ", ")
      .append("merchantName=" + String.valueOf(getMerchantName()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("originalDescription=" + String.valueOf(getOriginalDescription()) + ", ")
      .append("pending=" + String.valueOf(getPending()) + ", ")
      .append("pendingTransactionId=" + String.valueOf(getPendingTransactionId()) + ", ")
      .append("transactionId=" + String.valueOf(getTransactionId()) + ", ")
      .append("transactionType=" + String.valueOf(getTransactionType()) + ", ")
      .append("accountOwner=" + String.valueOf(getAccountOwner()) + ", ")
      .append("authorizedDate=" + String.valueOf(getAuthorizedDate()) + ", ")
      .append("transactionCode=" + String.valueOf(getTransactionCode()) + ", ")
      .append("paymentChannel=" + String.valueOf(getPaymentChannel()))
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
  public static Transaction justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new Transaction(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
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
      overrideName,
      memo,
      importBatch,
      importDate,
      importSource,
      plaidID,
      userData,
      account,
      amount,
      isoCurrencyCode,
      unofficialCurrencyCode,
      categoryFolder,
      category,
      categoryId,
      date,
      exactTime,
      merchantName,
      name,
      originalDescription,
      pending,
      pendingTransactionId,
      transactionId,
      transactionType,
      accountOwner,
      authorizedDate,
      transactionCode,
      paymentChannel);
  }
  public interface UserDataStep {
    AccountStep userData(UserData userData);
  }
  

  public interface AccountStep {
    AmountStep account(String account);
  }
  

  public interface AmountStep {
    BuildStep amount(Double amount);
  }
  

  public interface BuildStep {
    Transaction build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep overrideName(String overrideName);
    BuildStep memo(String memo);
    BuildStep importBatch(Integer importBatch);
    BuildStep importDate(String importDate);
    BuildStep importSource(String importSource);
    BuildStep plaidId(String plaidId);
    BuildStep isoCurrencyCode(String isoCurrencyCode);
    BuildStep unofficialCurrencyCode(String unofficialCurrencyCode);
    BuildStep categoryFolder(String categoryFolder);
    BuildStep category(List<String> category);
    BuildStep categoryId(String categoryId);
    BuildStep date(String date);
    BuildStep exactTime(String exactTime);
    BuildStep merchantName(String merchantName);
    BuildStep name(String name);
    BuildStep originalDescription(String originalDescription);
    BuildStep pending(Boolean pending);
    BuildStep pendingTransactionId(String pendingTransactionId);
    BuildStep transactionId(String transactionId);
    BuildStep transactionType(String transactionType);
    BuildStep accountOwner(String accountOwner);
    BuildStep authorizedDate(String authorizedDate);
    BuildStep transactionCode(String transactionCode);
    BuildStep paymentChannel(String paymentChannel);
  }
  

  public static class Builder implements UserDataStep, AccountStep, AmountStep, BuildStep {
    private String id;
    private UserData userData;
    private String account;
    private Double amount;
    private String overrideName;
    private String memo;
    private Integer importBatch;
    private String importDate;
    private String importSource;
    private String plaidID;
    private String isoCurrencyCode;
    private String unofficialCurrencyCode;
    private String categoryFolder;
    private List<String> category;
    private String categoryId;
    private String date;
    private String exactTime;
    private String merchantName;
    private String name;
    private String originalDescription;
    private Boolean pending;
    private String pendingTransactionId;
    private String transactionId;
    private String transactionType;
    private String accountOwner;
    private String authorizedDate;
    private String transactionCode;
    private String paymentChannel;
    @Override
     public Transaction build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Transaction(
          id,
          overrideName,
          memo,
          importBatch,
          importDate,
          importSource,
          plaidID,
          userData,
          account,
          amount,
          isoCurrencyCode,
          unofficialCurrencyCode,
          categoryFolder,
          category,
          categoryId,
          date,
          exactTime,
          merchantName,
          name,
          originalDescription,
          pending,
          pendingTransactionId,
          transactionId,
          transactionType,
          accountOwner,
          authorizedDate,
          transactionCode,
          paymentChannel);
    }
    
    @Override
     public AccountStep userData(UserData userData) {
        Objects.requireNonNull(userData);
        this.userData = userData;
        return this;
    }
    
    @Override
     public AmountStep account(String account) {
        Objects.requireNonNull(account);
        this.account = account;
        return this;
    }
    
    @Override
     public BuildStep amount(Double amount) {
        Objects.requireNonNull(amount);
        this.amount = amount;
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
     public BuildStep isoCurrencyCode(String isoCurrencyCode) {
        this.isoCurrencyCode = isoCurrencyCode;
        return this;
    }
    
    @Override
     public BuildStep unofficialCurrencyCode(String unofficialCurrencyCode) {
        this.unofficialCurrencyCode = unofficialCurrencyCode;
        return this;
    }
    
    @Override
     public BuildStep categoryFolder(String categoryFolder) {
        this.categoryFolder = categoryFolder;
        return this;
    }
    
    @Override
     public BuildStep category(List<String> category) {
        this.category = category;
        return this;
    }
    
    @Override
     public BuildStep categoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }
    
    @Override
     public BuildStep date(String date) {
        this.date = date;
        return this;
    }
    
    @Override
     public BuildStep exactTime(String exactTime) {
        this.exactTime = exactTime;
        return this;
    }
    
    @Override
     public BuildStep merchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }
    
    @Override
     public BuildStep name(String name) {
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep originalDescription(String originalDescription) {
        this.originalDescription = originalDescription;
        return this;
    }
    
    @Override
     public BuildStep pending(Boolean pending) {
        this.pending = pending;
        return this;
    }
    
    @Override
     public BuildStep pendingTransactionId(String pendingTransactionId) {
        this.pendingTransactionId = pendingTransactionId;
        return this;
    }
    
    @Override
     public BuildStep transactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }
    
    @Override
     public BuildStep transactionType(String transactionType) {
        this.transactionType = transactionType;
        return this;
    }
    
    @Override
     public BuildStep accountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
        return this;
    }
    
    @Override
     public BuildStep authorizedDate(String authorizedDate) {
        this.authorizedDate = authorizedDate;
        return this;
    }
    
    @Override
     public BuildStep transactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
        return this;
    }
    
    @Override
     public BuildStep paymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
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
    private CopyOfBuilder(String id, String overrideName, String memo, Integer importBatch, String importDate, String importSource, String plaidId, UserData userData, String account, Double amount, String isoCurrencyCode, String unofficialCurrencyCode, String categoryFolder, List<String> category, String categoryId, String date, String exactTime, String merchantName, String name, String originalDescription, Boolean pending, String pendingTransactionId, String transactionId, String transactionType, String accountOwner, String authorizedDate, String transactionCode, String paymentChannel) {
      super.id(id);
      super.userData(userData)
        .account(account)
        .amount(amount)
        .overrideName(overrideName)
        .memo(memo)
        .importBatch(importBatch)
        .importDate(importDate)
        .importSource(importSource)
        .plaidId(plaidId)
        .isoCurrencyCode(isoCurrencyCode)
        .unofficialCurrencyCode(unofficialCurrencyCode)
        .categoryFolder(categoryFolder)
        .category(category)
        .categoryId(categoryId)
        .date(date)
        .exactTime(exactTime)
        .merchantName(merchantName)
        .name(name)
        .originalDescription(originalDescription)
        .pending(pending)
        .pendingTransactionId(pendingTransactionId)
        .transactionId(transactionId)
        .transactionType(transactionType)
        .accountOwner(accountOwner)
        .authorizedDate(authorizedDate)
        .transactionCode(transactionCode)
        .paymentChannel(paymentChannel);
    }
    
    @Override
     public CopyOfBuilder userData(UserData userData) {
      return (CopyOfBuilder) super.userData(userData);
    }
    
    @Override
     public CopyOfBuilder account(String account) {
      return (CopyOfBuilder) super.account(account);
    }
    
    @Override
     public CopyOfBuilder amount(Double amount) {
      return (CopyOfBuilder) super.amount(amount);
    }
    
    @Override
     public CopyOfBuilder overrideName(String overrideName) {
      return (CopyOfBuilder) super.overrideName(overrideName);
    }
    
    @Override
     public CopyOfBuilder memo(String memo) {
      return (CopyOfBuilder) super.memo(memo);
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
     public CopyOfBuilder isoCurrencyCode(String isoCurrencyCode) {
      return (CopyOfBuilder) super.isoCurrencyCode(isoCurrencyCode);
    }
    
    @Override
     public CopyOfBuilder unofficialCurrencyCode(String unofficialCurrencyCode) {
      return (CopyOfBuilder) super.unofficialCurrencyCode(unofficialCurrencyCode);
    }
    
    @Override
     public CopyOfBuilder categoryFolder(String categoryFolder) {
      return (CopyOfBuilder) super.categoryFolder(categoryFolder);
    }
    
    @Override
     public CopyOfBuilder category(List<String> category) {
      return (CopyOfBuilder) super.category(category);
    }
    
    @Override
     public CopyOfBuilder categoryId(String categoryId) {
      return (CopyOfBuilder) super.categoryId(categoryId);
    }
    
    @Override
     public CopyOfBuilder date(String date) {
      return (CopyOfBuilder) super.date(date);
    }
    
    @Override
     public CopyOfBuilder exactTime(String exactTime) {
      return (CopyOfBuilder) super.exactTime(exactTime);
    }
    
    @Override
     public CopyOfBuilder merchantName(String merchantName) {
      return (CopyOfBuilder) super.merchantName(merchantName);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder originalDescription(String originalDescription) {
      return (CopyOfBuilder) super.originalDescription(originalDescription);
    }
    
    @Override
     public CopyOfBuilder pending(Boolean pending) {
      return (CopyOfBuilder) super.pending(pending);
    }
    
    @Override
     public CopyOfBuilder pendingTransactionId(String pendingTransactionId) {
      return (CopyOfBuilder) super.pendingTransactionId(pendingTransactionId);
    }
    
    @Override
     public CopyOfBuilder transactionId(String transactionId) {
      return (CopyOfBuilder) super.transactionId(transactionId);
    }
    
    @Override
     public CopyOfBuilder transactionType(String transactionType) {
      return (CopyOfBuilder) super.transactionType(transactionType);
    }
    
    @Override
     public CopyOfBuilder accountOwner(String accountOwner) {
      return (CopyOfBuilder) super.accountOwner(accountOwner);
    }
    
    @Override
     public CopyOfBuilder authorizedDate(String authorizedDate) {
      return (CopyOfBuilder) super.authorizedDate(authorizedDate);
    }
    
    @Override
     public CopyOfBuilder transactionCode(String transactionCode) {
      return (CopyOfBuilder) super.transactionCode(transactionCode);
    }
    
    @Override
     public CopyOfBuilder paymentChannel(String paymentChannel) {
      return (CopyOfBuilder) super.paymentChannel(paymentChannel);
    }
  }
  
}
